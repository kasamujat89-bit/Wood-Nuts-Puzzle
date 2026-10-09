package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Bolt
import com.example.model.Nut
import com.example.model.NutColor
import com.example.ui.components.BoltView
import com.example.ui.components.ConfettiCelebration
import com.example.ui.components.LanguageStrings
import com.example.ui.components.LevelSelectDialog
import com.example.ui.components.NutView
import com.example.ui.components.SettingsDialog
import com.example.ui.components.VictoryDialog
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.LightBgBottom
import com.example.ui.theme.LightBgMid
import com.example.ui.theme.LightBgTop
import com.example.viewmodel.NutSortViewModel

/**
 * Main gameplay screen for Nut Sort with fast multi-nut sorting,
 * quick level navigation, progress indicators, boosters, and Light/Dark mode support.
 */
@Composable
fun GameScreen(
    viewModel: NutSortViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Light Mode (default) vs Dark Mode background
    val bgGradient = if (uiState.isDarkMode) {
        listOf(DarkNavyBg, Color(0xFF161F38), Color(0xFF10162B))
    } else {
        listOf(LightBgTop, LightBgMid, LightBgBottom)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = bgGradient))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Header Bar (Level nav, Moves, Coins, Dark/Light Mode toggle, Settings)
            TopHeaderBar(
                level = uiState.level,
                maxLevelUnlocked = uiState.maxLevelUnlocked,
                moves = uiState.moveCount,
                coins = uiState.coins,
                language = uiState.language,
                isDarkMode = uiState.isDarkMode,
                onPrevLevel = { viewModel.prevLevel() },
                onNextLevel = { viewModel.nextLevel() },
                onLevelClick = { viewModel.openLevelSelect() },
                onToggleDarkMode = { viewModel.toggleDarkMode() },
                onSettingsClick = { viewModel.openSettings() }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 2. Banner title & Sorted Progress Bar
            GameBannerAndProgress(
                language = uiState.language,
                completedCount = uiState.completedBoltIds.size,
                totalColorCount = uiState.totalColorCount,
                isDarkMode = uiState.isDarkMode
            )

            // Tutorial guidance for Level 1 to make it fun, fast, and easy to play
            if (uiState.level == 1 && !uiState.isVictory) {
                Spacer(modifier = Modifier.height(4.dp))
                val tutorialText = when (uiState.moveCount) {
                    0 -> LanguageStrings.tutorialStep1(uiState.language)
                    1 -> LanguageStrings.tutorialStep2(uiState.language)
                    else -> LanguageStrings.tutorialStep3(uiState.language)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.94f)
                        .background(
                            color = if (uiState.isDarkMode) Color(0xFF1E293B) else Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (uiState.isDarkMode) Color(0xFF475569) else Color(0xFFF59E0B),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tutorialText,
                        color = if (uiState.isDarkMode) Color(0xFFFFD54F) else Color(0xFF92400E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Game Board with Bolts
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GameBoardBolts(
                    bolts = uiState.bolts,
                    selectedBoltId = uiState.selectedBoltId,
                    hintPair = uiState.hintPair,
                    shakeBoltId = uiState.shakeBoltId,
                    onBoltClick = { boltId -> viewModel.onBoltClicked(boltId) }
                )
            }

            // 4. Bottom Booster Action Bar (Undo, +Bolt, Hint, Restart)
            BottomActionBar(
                canUndo = uiState.canUndo,
                language = uiState.language,
                coins = uiState.coins,
                isDarkMode = uiState.isDarkMode,
                onUndo = { viewModel.undoMove() },
                onAddBolt = { viewModel.addExtraBolt() },
                onHint = { viewModel.getHint() },
                onRestart = { viewModel.restartLevel() },
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // In-game toast / notification alert
        AnimatedVisibility(
            visible = uiState.toastMessage != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        ) {
            uiState.toastMessage?.let { msg ->
                val toastBg = if (uiState.isDarkMode) Color(0xEE1E293B) else Color(0xF00F172A)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = toastBg),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.border(1.dp, Color(0xFF64748B), RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = msg,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                    )
                }
            }
        }

        // Fullscreen Confetti on victory
        if (uiState.isVictory) {
            ConfettiCelebration()
        }

        // Victory Dialog
        if (uiState.showVictoryDialog) {
            VictoryDialog(
                level = uiState.level,
                stars = uiState.earnedStars,
                moves = uiState.moveCount,
                coinsEarned = uiState.earnedCoins,
                isDarkMode = uiState.isDarkMode,
                language = uiState.language,
                onNextLevel = { viewModel.nextLevel() },
                onReplay = { viewModel.restartLevel() },
                onDismiss = { viewModel.dismissVictoryDialog() }
            )
        }

        // Level Select Dialog
        if (uiState.showLevelSelectDialog) {
            LevelSelectDialog(
                currentLevel = uiState.level,
                maxLevelUnlocked = uiState.maxLevelUnlocked,
                isDarkMode = uiState.isDarkMode,
                language = uiState.language,
                onLevelSelected = { lvl -> viewModel.selectLevel(lvl) },
                onLockedLevelClick = { lvl -> viewModel.onLockedLevelClick(lvl) },
                onDismiss = { viewModel.closeLevelSelect() }
            )
        }

        // Settings Dialog
        if (uiState.showSettingsDialog) {
            SettingsDialog(
                soundEnabled = uiState.soundEnabled,
                hapticEnabled = uiState.hapticEnabled,
                isDarkMode = uiState.isDarkMode,
                language = uiState.language,
                onSoundToggle = { viewModel.toggleSound(it) },
                onHapticToggle = { viewModel.toggleHaptic(it) },
                onDarkModeToggle = { viewModel.setDarkMode(it) },
                onLanguageToggle = { viewModel.toggleLanguage() },
                onResetProgress = { viewModel.resetProgress() },
                onDismiss = { viewModel.closeSettings() }
            )
        }
    }
}

/**
 * Top header containing interactive level navigator, moves counter, coins, direct Dark/Light mode toggle, and settings.
 */
@Composable
private fun TopHeaderBar(
    level: Int,
    maxLevelUnlocked: Int,
    moves: Int,
    coins: Int,
    language: String,
    isDarkMode: Boolean,
    onPrevLevel: () -> Unit,
    onNextLevel: () -> Unit,
    onLevelClick: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val movesColor = if (isDarkMode) Color(0xFFCFD8DC) else Color(0xFF334155)
    val capsuleBg = if (isDarkMode) Color(0xFF263238) else Color(0xFFFFFFFF)
    val capsuleBorder = if (isDarkMode) Color(0xFF455A7D) else Color(0xFFCBD5E1)
    val coinTextColor = if (isDarkMode) Color(0xFFFFD54F) else Color(0xFFD97706)
    val iconTint = if (isDarkMode) Color(0xFFB0BEC5) else Color(0xFF475569)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Level Navigation Row with Prev, Level Badge, and Next
        val nextLevelUnlocked = level < maxLevelUnlocked
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            IconButton(
                onClick = onPrevLevel,
                enabled = level > 1,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("prev_level_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Previous Level",
                    tint = if (level > 1) Color(0xFF1E88E5) else Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Level Capsule (Clickable to open level browser)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF1E88E5), Color(0xFF1565C0))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(1.5.dp, Color(0xFF90CAF9), RoundedCornerShape(20.dp))
                    .clickable(onClick = onLevelClick)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("level_badge")
            ) {
                Text(
                    text = LanguageStrings.level(language, level),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onNextLevel,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("next_level_quick_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Next Level",
                    tint = if (nextLevelUnlocked) Color(0xFF1E88E5) else (if (isDarkMode) Color(0xFF546E7A) else Color(0xFFCBD5E1)),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Moves Counter
        Text(
            text = LanguageStrings.moves(language, moves),
            color = movesColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Coins, Direct Dark/Light Toggle & Settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Coin counter capsule
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .background(capsuleBg, RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                NutView(
                    nut = Nut(id = 100, color = NutColor.YELLOW),
                    width = 18.dp,
                    height = 10.dp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$coins",
                    color = coinTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Direct Dark & Light Mode toggle button on top header (as requested by user!)
            IconButton(
                onClick = onToggleDarkMode,
                modifier = Modifier
                    .size(36.dp)
                    .shadow(2.dp, CircleShape)
                    .background(capsuleBg, CircleShape)
                    .border(1.dp, capsuleBorder, CircleShape)
                    .testTag("theme_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                    tint = if (isDarkMode) Color(0xFFFFD54F) else Color(0xFF475569),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Settings button
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(36.dp)
                    .shadow(2.dp, CircleShape)
                    .background(capsuleBg, CircleShape)
                    .border(1.dp, capsuleBorder, CircleShape)
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Banner and dynamic Level Completion progress indicator.
 */
@Composable
private fun GameBannerAndProgress(
    language: String,
    completedCount: Int,
    totalColorCount: Int,
    isDarkMode: Boolean
) {
    val progressTrack = if (isDarkMode) Color(0xFF263238) else Color(0xFFCBD5E1)
    val progressTextColor = if (isDarkMode) {
        if (completedCount == totalColorCount) Color(0xFF81C784) else Color(0xFFB0BEC5)
    } else {
        if (completedCount == totalColorCount) Color(0xFF2E7D32) else Color(0xFF475569)
    }

    Column(
        modifier = Modifier.fillMaxWidth(0.94f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner plate
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(10.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF0D47A1))
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                .border(1.dp, Color(0xFF64B5F6), RoundedCornerShape(10.dp))
                .padding(vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = LanguageStrings.sortSameColor(language),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress bar showing completion momentum
        val target = totalColorCount.coerceAtLeast(1)
        val progressFraction = (completedCount.toFloat() / target).coerceIn(0f, 1f)
        val animatedProgress by animateFloatAsState(
            targetValue = progressFraction,
            animationSpec = tween(350),
            label = "levelProgress"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = LanguageStrings.sortedProgress(language, completedCount, target),
                color = progressTextColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(progressTrack)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF42A5F5), Color(0xFF66BB6A))
                            )
                        )
                )
            }
        }
    }
}

/**
 * Arranges bolts cleanly into rows based on the number of bolts.
 */
@Composable
private fun GameBoardBolts(
    bolts: List<Bolt>,
    selectedBoltId: Int?,
    hintPair: Pair<Int, Int>?,
    shakeBoltId: Int?,
    onBoltClick: (Int) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val count = bolts.size

        val topRowCount = when {
            count <= 3 -> count
            count == 4 -> 2
            count <= 6 -> (count + 1) / 2
            count <= 8 -> (count + 1) / 2
            else -> (count + 1) / 2
        }

        val topRowBolts = bolts.take(topRowCount)
        val bottomRowBolts = bolts.drop(topRowCount)

        val maxPerRow = maxOf(topRowBolts.size, bottomRowBolts.size)

        val boltWidth = when {
            maxPerRow <= 3 -> 78.dp
            maxPerRow == 4 -> 68.dp
            maxPerRow == 5 -> 58.dp
            else -> 50.dp
        }

        val boltHeight = when {
            bottomRowBolts.isEmpty() -> 200.dp
            maxPerRow <= 4 -> 175.dp
            else -> 160.dp
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Row 1 (Top)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                topRowBolts.forEach { bolt ->
                    BoltView(
                        bolt = bolt,
                        isSelected = selectedBoltId == bolt.id,
                        isTargetHint = hintPair?.second == bolt.id || hintPair?.first == bolt.id,
                        isShakeTriggered = shakeBoltId == bolt.id,
                        onBoltClick = onBoltClick,
                        boltWidth = boltWidth,
                        boltHeight = boltHeight
                    )
                }
            }

            // Row 2 (Bottom) if applicable
            if (bottomRowBolts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    bottomRowBolts.forEach { bolt ->
                        BoltView(
                            bolt = bolt,
                            isSelected = selectedBoltId == bolt.id,
                            isTargetHint = hintPair?.second == bolt.id || hintPair?.first == bolt.id,
                            isShakeTriggered = shakeBoltId == bolt.id,
                            onBoltClick = onBoltClick,
                            boltWidth = boltWidth,
                            boltHeight = boltHeight
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bottom action dock with Undo, +Bolt, Hint, and Restart boosters.
 * Seamlessly adapts to Light and Dark themes.
 */
@Composable
private fun BottomActionBar(
    canUndo: Boolean,
    language: String,
    coins: Int,
    isDarkMode: Boolean,
    onUndo: () -> Unit,
    onAddBolt: () -> Unit,
    onHint: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDarkMode) Color(0xFF161C2E) else Color(0xFFFFFFFF)
    val cardBorder = if (isDarkMode) Color(0xFF2C3852) else Color(0xFFCBD5E1)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .fillMaxWidth(0.96f)
            .border(1.5.dp, cardBorder, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Undo Button
            BoosterButton(
                icon = Icons.Default.Undo,
                label = LanguageStrings.undo(language),
                enabled = canUndo,
                isDarkMode = isDarkMode,
                accentColor = Color(0xFFFFB74D),
                onClick = onUndo,
                testTag = "undo_button"
            )

            // 2. +Bolt Booster
            BoosterButton(
                icon = Icons.Default.AddCircleOutline,
                label = LanguageStrings.extraBolt(language),
                enabled = true,
                isDarkMode = isDarkMode,
                badgeText = "20🪙",
                accentColor = Color(0xFF43A047),
                onClick = onAddBolt,
                testTag = "add_bolt_button"
            )

            // 3. Hint Button
            BoosterButton(
                icon = Icons.Default.Lightbulb,
                label = LanguageStrings.hint(language),
                enabled = true,
                isDarkMode = isDarkMode,
                accentColor = Color(0xFFFFD54F),
                onClick = onHint,
                testTag = "hint_button"
            )

            // 4. Restart Level Button
            BoosterButton(
                icon = Icons.Default.Refresh,
                label = LanguageStrings.restart(language),
                enabled = true,
                isDarkMode = isDarkMode,
                accentColor = Color(0xFF1E88E5),
                onClick = onRestart,
                testTag = "restart_button"
            )
        }
    }
}

/**
 * Individual booster action button in the bottom dock.
 */
@Composable
private fun BoosterButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    isDarkMode: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    badgeText: String? = null,
    testTag: String
) {
    val buttonBg = if (isDarkMode) {
        if (enabled) Color(0xFF252E46) else Color(0xFF1B2233)
    } else {
        if (enabled) Color(0xFFF1F5F9) else Color(0xFFF8FAFC)
    }

    val buttonBorder = if (isDarkMode) {
        if (enabled) accentColor.copy(alpha = 0.6f) else Color(0xFF333D52)
    } else {
        if (enabled) accentColor.copy(alpha = 0.6f) else Color(0xFFE2E8F0)
    }

    val labelColor = if (isDarkMode) {
        if (enabled) Color(0xFFCFD8DC) else Color(0xFF5A667D)
    } else {
        if (enabled) Color(0xFF334155) else Color(0xFF94A3B8)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color = buttonBg, shape = CircleShape)
                    .border(width = 1.dp, color = buttonBorder, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (enabled) accentColor else Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )
            }

            badgeText?.let { badge ->
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE65100), RoundedCornerShape(6.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            color = labelColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
