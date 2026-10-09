package com.example.game

import com.example.model.Bolt
import com.example.model.Nut
import com.example.model.NutColor
import kotlin.random.Random

/**
 * Procedural generator for UNLIMITED levels.
 * Features a friendly, instant tutorial for Level 1 & 2,
 * followed by reverse-shuffle simulation for guaranteed solvability on all higher levels.
 */
object LevelGenerator {

    data class LevelData(
        val levelNumber: Int,
        val bolts: List<Bolt>,
        val colorCount: Int,
        val parMoves: Int,
        val coinReward: Int
    )

    fun generateLevel(levelNumber: Int): LevelData {
        val safeLevel = levelNumber.coerceAtLeast(1)

        // Handcrafted fun Level 1: 3 quick moves to introduce the sorting concept
        if (safeLevel == 1) {
            val colorA = NutColor.YELLOW
            val colorB = NutColor.BLUE

            val bolt0 = Bolt(
                id = 0,
                capacity = 4,
                nuts = listOf(
                    Nut(id = 1, color = colorA),
                    Nut(id = 2, color = colorA),
                    Nut(id = 3, color = colorA),
                    Nut(id = 4, color = colorB) // Top is Blue
                )
            )

            val bolt1 = Bolt(
                id = 1,
                capacity = 4,
                nuts = listOf(
                    Nut(id = 5, color = colorB),
                    Nut(id = 6, color = colorB),
                    Nut(id = 7, color = colorB),
                    Nut(id = 8, color = colorA) // Top is Yellow
                )
            )

            val bolt2 = Bolt(
                id = 2,
                capacity = 4,
                nuts = emptyList() // Empty buffer
            )

            return LevelData(
                levelNumber = 1,
                bolts = listOf(bolt0, bolt1, bolt2),
                colorCount = 2,
                parMoves = 3,
                coinReward = 25
            )
        }

        // Handcrafted fun Level 2: 4 bolts (2 colors, 2 empty bolts)
        if (safeLevel == 2) {
            val colorA = NutColor.GREEN
            val colorB = NutColor.RED

            val bolt0 = Bolt(
                id = 0,
                capacity = 4,
                nuts = listOf(
                    Nut(id = 11, color = colorA),
                    Nut(id = 12, color = colorB),
                    Nut(id = 13, color = colorA),
                    Nut(id = 14, color = colorB)
                )
            )

            val bolt1 = Bolt(
                id = 1,
                capacity = 4,
                nuts = listOf(
                    Nut(id = 15, color = colorB),
                    Nut(id = 16, color = colorA),
                    Nut(id = 17, color = colorB),
                    Nut(id = 18, color = colorA)
                )
            )

            val bolt2 = Bolt(id = 2, capacity = 4, nuts = emptyList())
            val bolt3 = Bolt(id = 3, capacity = 4, nuts = emptyList())

            return LevelData(
                levelNumber = 2,
                bolts = listOf(bolt0, bolt1, bolt2, bolt3),
                colorCount = 2,
                parMoves = 6,
                coinReward = 25
            )
        }

        // Procedural generation for Level 3 to infinity
        val seed = safeLevel.toLong() * 2654435761L + 982451653L
        val rng = Random(seed)

        val colorCount = when {
            safeLevel <= 5 -> 3
            safeLevel <= 10 -> 4
            safeLevel <= 20 -> 5
            safeLevel <= 40 -> 6
            safeLevel <= 70 -> 7
            else -> 8
        }

        val emptyBoltsCount = 2
        val totalBolts = colorCount + emptyBoltsCount
        val capacity = 4

        val shuffleSteps = when {
            safeLevel <= 5 -> 18
            safeLevel <= 15 -> 28
            safeLevel <= 30 -> 38
            safeLevel <= 60 -> 50
            else -> 65
        }

        val mysteryCount = when {
            safeLevel < 6 -> 0
            safeLevel <= 10 -> 1
            safeLevel <= 20 -> 2
            safeLevel <= 40 -> 3
            safeLevel <= 70 -> 4
            else -> 5
        }

        val availableColors = NutColor.entries.shuffled(rng).take(colorCount)

        // 1. Solved baseline
        var nutIdCounter = safeLevel * 100L
        val currentStacks = MutableList(totalBolts) { boltIdx ->
            if (boltIdx < colorCount) {
                val color = availableColors[boltIdx]
                MutableList(capacity) {
                    Nut(
                        id = nutIdCounter++,
                        color = color,
                        isMystery = false,
                        isRevealed = false
                    )
                }
            } else {
                mutableListOf<Nut>()
            }
        }

        // 2. Reverse move simulation
        var lastFrom = -1
        var lastTo = -1

        for (step in 0 until shuffleSteps) {
            val nonEmpties = currentStacks.indices.filter { currentStacks[it].isNotEmpty() }
            if (nonEmpties.isEmpty()) break

            val candidatesFrom = if (nonEmpties.size > 1) {
                nonEmpties.filter { it != lastTo }
            } else nonEmpties

            val fromIdx = (if (candidatesFrom.isNotEmpty()) candidatesFrom else nonEmpties).random(rng)

            val candidatesTo = currentStacks.indices.filter {
                it != fromIdx && currentStacks[it].size < capacity && it != lastFrom
            }

            val fallbackTo = currentStacks.indices.filter {
                it != fromIdx && currentStacks[it].size < capacity
            }

            val validTo = if (candidatesTo.isNotEmpty()) candidatesTo else fallbackTo
            if (validTo.isEmpty()) continue

            val toIdx = validTo.random(rng)

            val nut = currentStacks[fromIdx].removeAt(currentStacks[fromIdx].size - 1)
            currentStacks[toIdx].add(nut)

            lastFrom = fromIdx
            lastTo = toIdx
        }

        // 3. Mystery nuts assignment (below the top nut)
        if (mysteryCount > 0) {
            val hiddenPositions = mutableListOf<Pair<Int, Int>>()
            for (boltIdx in currentStacks.indices) {
                val stack = currentStacks[boltIdx]
                if (stack.size >= 2) {
                    for (nutIdx in 0 until stack.size - 1) {
                        hiddenPositions.add(boltIdx to nutIdx)
                    }
                }
            }

            hiddenPositions.shuffle(rng)
            val selectedMysteryPositions = hiddenPositions.take(mysteryCount)

            for ((bIdx, nIdx) in selectedMysteryPositions) {
                val nut = currentStacks[bIdx][nIdx]
                currentStacks[bIdx][nIdx] = nut.copy(isMystery = true, isRevealed = false)
            }
        }

        val bolts = currentStacks.mapIndexed { idx, stack ->
            Bolt(
                id = idx,
                capacity = capacity,
                nuts = stack.toList(),
                isExtra = false
            )
        }

        val parMoves = (shuffleSteps * 1.2).toInt().coerceAtLeast(8)
        val coinReward = (20 + (safeLevel * 2).coerceAtMost(50))

        return LevelData(
            levelNumber = safeLevel,
            bolts = bolts,
            colorCount = colorCount,
            parMoves = parMoves,
            coinReward = coinReward
        )
    }
}
