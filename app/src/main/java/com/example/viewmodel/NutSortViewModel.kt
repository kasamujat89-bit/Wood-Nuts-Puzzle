package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.game.GamePreferences
import com.example.game.LevelGenerator
import com.example.game.SoundManager
import com.example.model.Bolt
import com.example.model.MoveAction
import com.example.model.Nut
import com.example.ui.components.LanguageStrings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val level: Int = 1,
    val bolts: List<Bolt> = emptyList(),
    val selectedBoltId: Int? = null,
    val completedBoltIds: Set<Int> = emptySet(),
    val totalColorCount: Int = 2,
    val moveCount: Int = 0,
    val parMoves: Int = 3,
    val coins: Int = 100,
    val maxLevelUnlocked: Int = 1,
    val isVictory: Boolean = false,
    val earnedStars: Int = 3,
    val earnedCoins: Int = 25,
    val shakeBoltId: Int? = null,
    val hintPair: Pair<Int, Int>? = null,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val language: String = "en", // Default English as requested by user!
    val isDarkMode: Boolean = false, // Default Light Mode fixed as requested!
    val canUndo: Boolean = false,
    val showVictoryDialog: Boolean = false,
    val showLevelSelectDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val extraBoltsAdded: Int = 0,
    val toastMessage: String? = null
)

class NutSortViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application)
    private val soundManager = SoundManager(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val moveHistory = mutableListOf<MoveAction>()
    private var currentLevelData: LevelGenerator.LevelData? = null

    init {
        soundManager.soundEnabled = preferences.soundEnabled
        soundManager.hapticEnabled = preferences.hapticEnabled

        _uiState.update {
            it.copy(
                level = preferences.currentLevel,
                coins = preferences.coins,
                maxLevelUnlocked = preferences.maxLevelUnlocked,
                soundEnabled = preferences.soundEnabled,
                hapticEnabled = preferences.hapticEnabled,
                language = preferences.language,
                isDarkMode = preferences.isDarkMode
            )
        }

        loadLevel(preferences.currentLevel)
    }

    fun loadLevel(levelNum: Int) {
        val safeLevel = levelNum.coerceAtLeast(1)
        val data = LevelGenerator.generateLevel(safeLevel)
        currentLevelData = data
        moveHistory.clear()

        preferences.currentLevel = safeLevel

        val completedInitially = data.bolts.filter { it.isCompleted }.map { it.id }.toSet()

        _uiState.update {
            it.copy(
                level = safeLevel,
                bolts = data.bolts,
                selectedBoltId = null,
                completedBoltIds = completedInitially,
                totalColorCount = data.colorCount,
                moveCount = 0,
                parMoves = data.parMoves,
                earnedCoins = data.coinReward,
                isVictory = false,
                shakeBoltId = null,
                hintPair = null,
                canUndo = false,
                showVictoryDialog = false,
                extraBoltsAdded = 0,
                maxLevelUnlocked = preferences.maxLevelUnlocked
            )
        }
    }

    fun onBoltClicked(boltId: Int) {
        val state = _uiState.value
        if (state.isVictory) return

        val selectedId = state.selectedBoltId

        // Case 1: No bolt is currently selected
        if (selectedId == null) {
            val clickedBolt = state.bolts.find { it.id == boltId } ?: return
            if (clickedBolt.isEmpty) return // Can't select empty bolt
            if (clickedBolt.isCompleted) return // Bolt already completed

            _uiState.update { it.copy(selectedBoltId = boltId, hintPair = null) }
            soundManager.playPickup()
            return
        }

        // Case 2: Clicked the SAME bolt that is already selected -> Deselect
        if (selectedId == boltId) {
            _uiState.update { it.copy(selectedBoltId = null) }
            soundManager.playDrop()
            return
        }

        // Case 3: Clicked a DIFFERENT bolt -> Attempt to transfer nut(s)
        val sourceBolt = state.bolts.find { it.id == selectedId } ?: return
        val destBolt = state.bolts.find { it.id == boltId } ?: return

        val topNut = sourceBolt.topNut
        if (topNut == null) {
            _uiState.update { it.copy(selectedBoltId = null) }
            return
        }

        // Check if destination can accept this color
        if (destBolt.canAccept(topNut)) {
            // MULTI-NUT TRANSFER: Move all consecutive matching nuts that fit!
            val matchingNuts = mutableListOf<Nut>()
            for (nut in sourceBolt.nuts.asReversed()) {
                if (nut.color == topNut.color && nut.isVisible) {
                    matchingNuts.add(nut)
                } else {
                    break
                }
            }

            val spaceAvailable = destBolt.availableSpace
            val countToMove = minOf(matchingNuts.size, spaceAvailable)
            if (countToMove <= 0) return

            val nutsToMove = sourceBolt.nuts.takeLast(countToMove)
            executeMultiMove(sourceBolt, destBolt, nutsToMove)
        } else {
            // If destination has nuts and is not completed, switch selection to destination!
            if (!destBolt.isEmpty && !destBolt.isCompleted) {
                _uiState.update { it.copy(selectedBoltId = destBolt.id, hintPair = null) }
                soundManager.playPickup()
            } else {
                triggerShake(destBolt.id)
                soundManager.playError()
            }
        }
    }

    private fun executeMultiMove(sourceBolt: Bolt, destBolt: Bolt, movedNuts: List<Nut>) {
        val currentBolts = _uiState.value.bolts.toMutableList()

        val srcIndex = currentBolts.indexOfFirst { it.id == sourceBolt.id }
        val destIndex = currentBolts.indexOfFirst { it.id == destBolt.id }
        if (srcIndex == -1 || destIndex == -1) return

        // 1. Remove nuts from source
        val updatedSrcNuts = sourceBolt.nuts.dropLast(movedNuts.size).toMutableList()
        var uncoveredNutId: Long? = null

        // Check if removing these nuts uncovered a mystery nut on source
        if (updatedSrcNuts.isNotEmpty()) {
            val newTop = updatedSrcNuts.last()
            if (newTop.isMystery && !newTop.isRevealed) {
                uncoveredNutId = newTop.id
                updatedSrcNuts[updatedSrcNuts.size - 1] = newTop.copy(isRevealed = true)
            }
        }

        // 2. Add nuts to destination
        val updatedDestNuts = destBolt.nuts + movedNuts

        currentBolts[srcIndex] = sourceBolt.copy(nuts = updatedSrcNuts)
        currentBolts[destIndex] = destBolt.copy(nuts = updatedDestNuts)

        // Record for undo
        val action = MoveAction(
            fromBoltId = sourceBolt.id,
            toBoltId = destBolt.id,
            movedNuts = movedNuts,
            uncoveredNutId = uncoveredNutId
        )
        moveHistory.add(action)

        val newMoveCount = _uiState.value.moveCount + 1

        // Play snappy drop sound and vibration
        soundManager.playDrop()

        // Check newly completed bolts
        val newlyCompleted = currentBolts[destIndex].isCompleted
        val currentCompleted = _uiState.value.completedBoltIds.toMutableSet()
        if (newlyCompleted) {
            currentCompleted.add(destBolt.id)
            soundManager.playBoltComplete()
        }

        // Check overall victory condition:
        // All non-empty bolts must be completed!
        val nonEmptyBolts = currentBolts.filter { !it.isEmpty }
        val allSolved = nonEmptyBolts.isNotEmpty() && nonEmptyBolts.all { it.isCompleted }

        if (allSolved) {
            handleVictory(newMoveCount, currentBolts, currentCompleted)
        } else {
            _uiState.update {
                it.copy(
                    bolts = currentBolts,
                    selectedBoltId = null,
                    completedBoltIds = currentCompleted,
                    moveCount = newMoveCount,
                    canUndo = true,
                    hintPair = null
                )
            }
            if (!hasAvailableMoves(currentBolts)) {
                showToast(LanguageStrings.hintNotFound(_uiState.value.language))
            }
        }
    }

    private fun hasAvailableMoves(bolts: List<Bolt>): Boolean {
        val hasEmpty = bolts.any { it.isEmpty }
        for (src in bolts) {
            if (src.isEmpty || src.isCompleted) continue
            val topNut = src.topNut ?: continue

            if (hasEmpty) {
                val isUniform = src.nuts.all { it.color == topNut.color && it.isVisible }
                if (!isUniform || src.nuts.size < src.capacity) {
                    return true
                }
            }

            for (dest in bolts) {
                if (dest.id == src.id || dest.isFull || dest.isCompleted) continue
                if (!dest.isEmpty && dest.topColor == topNut.color) {
                    return true
                }
            }
        }
        return false
    }

    private fun handleVictory(moves: Int, updatedBolts: List<Bolt>, completedIds: Set<Int>) {
        soundManager.playVictory()

        val par = _uiState.value.parMoves
        val stars = when {
            moves <= par -> 3
            moves <= (par * 1.5).toInt() -> 2
            else -> 1
        }

        val reward = currentLevelData?.coinReward ?: 25
        val updatedCoins = _uiState.value.coins + reward
        preferences.coins = updatedCoins

        val nextLevel = _uiState.value.level + 1
        if (nextLevel > preferences.maxLevelUnlocked) {
            preferences.maxLevelUnlocked = nextLevel
        }

        _uiState.update {
            it.copy(
                bolts = updatedBolts,
                selectedBoltId = null,
                completedBoltIds = completedIds,
                moveCount = moves,
                isVictory = true,
                earnedStars = stars,
                earnedCoins = reward,
                coins = updatedCoins,
                showVictoryDialog = true,
                canUndo = false,
                maxLevelUnlocked = preferences.maxLevelUnlocked
            )
        }
    }

    fun undoMove() {
        if (moveHistory.isEmpty()) {
            showToast(LanguageStrings.noMovesToUndo(_uiState.value.language))
            return
        }

        val lastAction = moveHistory.removeAt(moveHistory.size - 1)
        val currentBolts = _uiState.value.bolts.toMutableList()

        val fromIndex = currentBolts.indexOfFirst { it.id == lastAction.fromBoltId }
        val toIndex = currentBolts.indexOfFirst { it.id == lastAction.toBoltId }
        if (fromIndex == -1 || toIndex == -1) return

        val fromBolt = currentBolts[fromIndex]
        val toBolt = currentBolts[toIndex]

        val countToReturn = lastAction.movedNuts.size
        val restoredToNuts = toBolt.nuts.dropLast(countToReturn)

        val restoredFromNuts = fromBolt.nuts.map { nut ->
            if (nut.id == lastAction.uncoveredNutId) {
                nut.copy(isRevealed = false)
            } else nut
        } + lastAction.movedNuts

        currentBolts[fromIndex] = fromBolt.copy(nuts = restoredFromNuts)
        currentBolts[toIndex] = toBolt.copy(nuts = restoredToNuts)

        val completed = currentBolts.filter { it.isCompleted }.map { it.id }.toSet()

        soundManager.playPickup()

        _uiState.update {
            it.copy(
                bolts = currentBolts,
                selectedBoltId = null,
                completedBoltIds = completed,
                canUndo = moveHistory.isNotEmpty(),
                hintPair = null
            )
        }
    }

    fun addExtraBolt() {
        val state = _uiState.value
        if (state.extraBoltsAdded >= 2) {
            showToast(LanguageStrings.extraBoltLimit(state.language))
            return
        }

        val cost = 20
        if (state.coins < cost) {
            showToast(LanguageStrings.outOfCoins(state.language))
            return
        }

        val updatedCoins = state.coins - cost
        preferences.coins = updatedCoins

        val maxId = (state.bolts.maxOfOrNull { it.id } ?: 0) + 1
        val extraBolt = Bolt(id = maxId, capacity = 4, nuts = emptyList(), isExtra = true)
        val updatedBolts = state.bolts + extraBolt

        soundManager.playPickup()
        showToast(LanguageStrings.extraBoltAdded(state.language))

        _uiState.update {
            it.copy(
                bolts = updatedBolts,
                coins = updatedCoins,
                extraBoltsAdded = state.extraBoltsAdded + 1
            )
        }
    }

    fun getHint() {
        val state = _uiState.value
        val bolts = state.bolts

        // 1. Move to a matching bolt
        for (src in bolts) {
            if (src.isEmpty || src.isCompleted) continue
            val topNut = src.topNut ?: continue

            for (dest in bolts) {
                if (dest.id == src.id || dest.isFull) continue
                if (!dest.isEmpty && dest.topColor == topNut.color) {
                    _uiState.update { it.copy(hintPair = src.id to dest.id) }
                    soundManager.playPickup()
                    return
                }
            }
        }

        // 2. Move to an empty bolt
        for (src in bolts) {
            if (src.isEmpty || src.isCompleted || src.nuts.size < 2) continue
            val topNut = src.topNut ?: continue
            val nutBelow = src.nuts[src.nuts.size - 2]
            if (nutBelow.color != topNut.color || !nutBelow.isVisible) {
                val emptyDest = bolts.find { it.isEmpty }
                if (emptyDest != null) {
                    _uiState.update { it.copy(hintPair = src.id to emptyDest.id) }
                    soundManager.playPickup()
                    return
                }
            }
        }

        showToast(LanguageStrings.hintNotFound(state.language))
    }

    fun restartLevel() {
        loadLevel(_uiState.value.level)
    }

    fun nextLevel() {
        val target = _uiState.value.level + 1
        if (target <= _uiState.value.maxLevelUnlocked) {
            loadLevel(target)
        } else {
            onLockedLevelClick(target)
        }
    }

    fun onLockedLevelClick(levelNum: Int) {
        soundManager.playError()
        showToast(LanguageStrings.levelLocked(_uiState.value.language, levelNum))
    }

    fun prevLevel() {
        if (_uiState.value.level > 1) {
            loadLevel(_uiState.value.level - 1)
        }
    }

    fun selectLevel(levelNum: Int) {
        if (levelNum <= _uiState.value.maxLevelUnlocked) {
            _uiState.update { it.copy(showLevelSelectDialog = false) }
            loadLevel(levelNum)
        } else {
            onLockedLevelClick(levelNum)
        }
    }

    fun resetProgress() {
        preferences.maxLevelUnlocked = 1
        preferences.currentLevel = 1
        _uiState.update {
            it.copy(
                maxLevelUnlocked = 1,
                showSettingsDialog = false
            )
        }
        loadLevel(1)
        soundManager.playPickup()
        showToast(if (_uiState.value.language == "gu") "પ્રગતિ રીસેટ થઈ! લેવલ 1 થી શરૂ." else "Progress reset! Starting from Level 1.")
    }

    fun toggleSound(enabled: Boolean) {
        soundManager.soundEnabled = enabled
        preferences.soundEnabled = enabled
        _uiState.update { it.copy(soundEnabled = enabled) }
    }

    fun toggleHaptic(enabled: Boolean) {
        soundManager.hapticEnabled = enabled
        preferences.hapticEnabled = enabled
        _uiState.update { it.copy(hapticEnabled = enabled) }
    }

    fun toggleLanguage() {
        val newLang = if (_uiState.value.language == "en") "gu" else "en"
        preferences.language = newLang
        _uiState.update { it.copy(language = newLang) }
    }

    fun toggleDarkMode() {
        val newMode = !_uiState.value.isDarkMode
        preferences.isDarkMode = newMode
        _uiState.update { it.copy(isDarkMode = newMode) }
        soundManager.playPickup()
    }

    fun setDarkMode(enabled: Boolean) {
        preferences.isDarkMode = enabled
        _uiState.update { it.copy(isDarkMode = enabled) }
    }

    fun openLevelSelect() {
        _uiState.update { it.copy(showLevelSelectDialog = true) }
    }

    fun closeLevelSelect() {
        _uiState.update { it.copy(showLevelSelectDialog = false) }
    }

    fun openSettings() {
        _uiState.update { it.copy(showSettingsDialog = true) }
    }

    fun closeSettings() {
        _uiState.update { it.copy(showSettingsDialog = false) }
    }

    fun dismissVictoryDialog() {
        _uiState.update { it.copy(showVictoryDialog = false) }
    }

    private fun triggerShake(boltId: Int) {
        _uiState.update { it.copy(shakeBoltId = boltId) }
        viewModelScope.launch {
            delay(300)
            if (_uiState.value.shakeBoltId == boltId) {
                _uiState.update { it.copy(shakeBoltId = null) }
            }
        }
    }

    private fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
        viewModelScope.launch {
            delay(2000)
            if (_uiState.value.toastMessage == msg) {
                _uiState.update { it.copy(toastMessage = null) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
