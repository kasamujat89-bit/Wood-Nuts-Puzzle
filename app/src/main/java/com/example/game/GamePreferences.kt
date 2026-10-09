package com.example.game

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages local persistence of game progression, coins, and settings.
 */
class GamePreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("nut_sort_prefs", Context.MODE_PRIVATE)

    var currentLevel: Int
        get() = prefs.getInt(KEY_CURRENT_LEVEL, 1)
        set(value) = prefs.edit().putInt(KEY_CURRENT_LEVEL, value).apply()

    var maxLevelUnlocked: Int
        get() = prefs.getInt(KEY_MAX_LEVEL, 1)
        set(value) = prefs.edit().putInt(KEY_MAX_LEVEL, value).apply()

    var coins: Int
        get() = prefs.getInt(KEY_COINS, 100) // 100 starter coins
        set(value) = prefs.edit().putInt(KEY_COINS, value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    var hapticEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTIC, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTIC, value).apply()

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var nutTheme: String
        get() = prefs.getString(KEY_THEME, "CLASSIC") ?: "CLASSIC"
        set(value) = prefs.edit().putString(KEY_THEME, value).apply()

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false) // Default: Light Mode fixed!
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    companion object {
        private const val KEY_CURRENT_LEVEL = "current_level"
        private const val KEY_MAX_LEVEL = "max_level_unlocked"
        private const val KEY_COINS = "coins"
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_HAPTIC = "haptic_enabled"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME = "nut_theme"
        private const val KEY_DARK_MODE = "dark_mode"
    }
}
