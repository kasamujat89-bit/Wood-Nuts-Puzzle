package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.game.GamePreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Nut Sort", appName)
    }

    @Test
    fun `preferences default values comply with user requirements`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = GamePreferences(context)

        // Default language MUST be English
        assertEquals("en", prefs.language)

        // Default theme MUST be Light Mode (isDarkMode = false)
        assertFalse(prefs.isDarkMode)

        // Default start level MUST be 1, and maxLevelUnlocked MUST be 1
        assertEquals(1, prefs.currentLevel)
        assertEquals(1, prefs.maxLevelUnlocked)
    }

    @Test
    fun `level progression and unlock persistence`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = GamePreferences(context)

        // Complete level 1 and unlock level 2
        prefs.maxLevelUnlocked = 2
        assertEquals(2, prefs.maxLevelUnlocked)

        // Toggle theme to dark mode
        prefs.isDarkMode = true
        assertTrue(prefs.isDarkMode)

        // Toggle back to light mode
        prefs.isDarkMode = false
        assertFalse(prefs.isDarkMode)
    }
}
