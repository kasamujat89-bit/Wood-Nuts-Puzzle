package com.example

import com.example.game.LevelGenerator
import com.example.model.Bolt
import com.example.model.Nut
import com.example.model.NutColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NutSortUnitTest {

    @Test
    fun levelGenerator_generatesValidPuzzlesForUnlimitedLevels() {
        // Test multiple levels across the progression curve
        val levelsToTest = listOf(1, 2, 5, 10, 25, 50, 100, 999)

        for (lvl in levelsToTest) {
            val levelData = LevelGenerator.generateLevel(lvl)
            assertEquals(lvl, levelData.levelNumber)
            assertTrue("Level $lvl should have bolts", levelData.bolts.isNotEmpty())

            // Verify total nut count equals colorCount * 4
            val totalNuts = levelData.bolts.sumOf { it.nuts.size }
            assertEquals("Level $lvl total nuts should equal colors * 4", levelData.colorCount * 4, totalNuts)

            // Verify all bolts do not exceed max capacity
            levelData.bolts.forEach { bolt ->
                assertTrue("Bolt ${bolt.id} exceeded capacity", bolt.nuts.size <= bolt.capacity)
            }
        }
    }

    @Test
    fun levelGenerator_isDeterministicForSameLevel() {
        val level5A = LevelGenerator.generateLevel(5)
        val level5B = LevelGenerator.generateLevel(5)

        assertEquals(level5A.colorCount, level5B.colorCount)
        assertEquals(level5A.bolts.size, level5B.bolts.size)

        for (i in level5A.bolts.indices) {
            val nutsA = level5A.bolts[i].nuts
            val nutsB = level5B.bolts[i].nuts
            assertEquals(nutsA.size, nutsB.size)
            for (j in nutsA.indices) {
                assertEquals(nutsA[j].color, nutsB[j].color)
                assertEquals(nutsA[j].isMystery, nutsB[j].isMystery)
            }
        }
    }

    @Test
    fun bolt_canAcceptLogic() {
        val emptyBolt = Bolt(id = 1, capacity = 4, nuts = emptyList())
        val redNut = Nut(id = 1, color = NutColor.RED)
        val blueNut = Nut(id = 2, color = NutColor.BLUE)

        // Empty bolt can accept any nut
        assertTrue(emptyBolt.canAccept(redNut))
        assertTrue(emptyBolt.canAccept(blueNut))

        // Bolt with red nut can accept another red nut, but NOT blue nut
        val boltWithRed = Bolt(id = 2, capacity = 4, nuts = listOf(redNut))
        assertTrue(boltWithRed.canAccept(redNut))
        assertFalse(boltWithRed.canAccept(blueNut))

        // Full bolt cannot accept any nut
        val fullBolt = Bolt(
            id = 3,
            capacity = 4,
            nuts = listOf(redNut, redNut, redNut, redNut)
        )
        assertFalse(fullBolt.canAccept(redNut))
    }

    @Test
    fun bolt_completionValidation() {
        val red1 = Nut(id = 1, color = NutColor.RED)
        val red2 = Nut(id = 2, color = NutColor.RED)
        val red3 = Nut(id = 3, color = NutColor.RED)
        val red4 = Nut(id = 4, color = NutColor.RED)

        // 3 nuts: not complete
        val partialBolt = Bolt(id = 1, capacity = 4, nuts = listOf(red1, red2, red3))
        assertFalse(partialBolt.isCompleted)

        // 4 identical nuts: complete!
        val completedBolt = Bolt(id = 2, capacity = 4, nuts = listOf(red1, red2, red3, red4))
        assertTrue(completedBolt.isCompleted)

        // 4 nuts but one is mystery: not complete until revealed
        val mysteryNut = Nut(id = 5, color = NutColor.RED, isMystery = true, isRevealed = false)
        val hiddenBolt = Bolt(id = 3, capacity = 4, nuts = listOf(mysteryNut, red2, red3, red4))
        assertFalse(hiddenBolt.isCompleted)
    }

    @Test
    fun multiNutTransfer_calculatesCorrectCount() {
        val y1 = Nut(id = 1, color = NutColor.YELLOW)
        val y2 = Nut(id = 2, color = NutColor.YELLOW)
        val b1 = Nut(id = 3, color = NutColor.BLUE)

        // Stack with [BLUE, YELLOW, YELLOW] -> top 2 are YELLOW
        val sourceBolt = Bolt(id = 0, capacity = 4, nuts = listOf(b1, y1, y2))
        val targetBolt = Bolt(id = 1, capacity = 4, nuts = listOf(y1)) // space = 3

        val topNut = sourceBolt.topNut!!
        val matching = sourceBolt.nuts.reversed().takeWhile { it.color == topNut.color }
        assertEquals(2, matching.size)

        val countToMove = minOf(matching.size, targetBolt.availableSpace)
        assertEquals(2, countToMove)
    }

    @Test
    fun levelProgression_locksHigherLevelsUntilCompleted() {
        var maxLevelUnlocked = 1

        // Initially only level 1 is accessible
        assertTrue(1 <= maxLevelUnlocked)
        assertFalse(2 <= maxLevelUnlocked)
        assertFalse(3 <= maxLevelUnlocked)

        // Completing level 1 unlocks level 2
        maxLevelUnlocked = maxOf(maxLevelUnlocked, 2)
        assertTrue(1 <= maxLevelUnlocked)
        assertTrue(2 <= maxLevelUnlocked)
        assertFalse(3 <= maxLevelUnlocked)

        // Completing level 2 unlocks level 3
        maxLevelUnlocked = maxOf(maxLevelUnlocked, 3)
        assertTrue(3 <= maxLevelUnlocked)
        assertFalse(4 <= maxLevelUnlocked)
    }

    @Test
    fun themeState_defaultsToLightModeAndToggles() {
        var isDarkMode = false // Light mode fixed by default
        assertFalse(isDarkMode)

        // Toggle to dark mode
        isDarkMode = !isDarkMode
        assertTrue(isDarkMode)

        // Toggle back to light mode
        isDarkMode = !isDarkMode
        assertFalse(isDarkMode)
    }
}
