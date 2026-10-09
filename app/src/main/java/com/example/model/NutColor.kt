package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Vibrant 3D colors for hexagonal nuts, matched with highlight, shadow, and face gradients.
 */
enum class NutColor(
    val displayNameEn: String,
    val displayNameGu: String,
    val primaryColor: Color,
    val lightHighlight: Color,
    val darkShadow: Color,
    val borderColor: Color,
    val glowColor: Color
) {
    RED(
        displayNameEn = "Red",
        displayNameGu = "લાલ",
        primaryColor = Color(0xFFFF334B),
        lightHighlight = Color(0xFFFF8292),
        darkShadow = Color(0xFFBA0B22),
        borderColor = Color(0xFF7E0213),
        glowColor = Color(0xFFFF5268)
    ),
    BLUE(
        displayNameEn = "Blue",
        displayNameGu = "વાદળી",
        primaryColor = Color(0xFF1E88E5),
        lightHighlight = Color(0xFF6AB7FF),
        darkShadow = Color(0xFF0D5BA6),
        borderColor = Color(0xFF063B6F),
        glowColor = Color(0xFF42A5F5)
    ),
    YELLOW(
        displayNameEn = "Yellow",
        displayNameGu = "પીળો",
        primaryColor = Color(0xFFFFCA28),
        lightHighlight = Color(0xFFFFF070),
        darkShadow = Color(0xFFD49000),
        borderColor = Color(0xFF8C5E00),
        glowColor = Color(0xFFFFD54F)
    ),
    GREEN(
        displayNameEn = "Green",
        displayNameGu = "લીલો",
        primaryColor = Color(0xFF43A047),
        lightHighlight = Color(0xFF81C784),
        darkShadow = Color(0xFF236B26),
        borderColor = Color(0xFF124315),
        glowColor = Color(0xFF66BB6A)
    ),
    CYAN(
        displayNameEn = "Cyan",
        displayNameGu = "આકાશી",
        primaryColor = Color(0xFF00BCD4),
        lightHighlight = Color(0xFF62EFFF),
        darkShadow = Color(0xFF007E8F),
        borderColor = Color(0xFF004D58),
        glowColor = Color(0xFF26C6DA)
    ),
    ORANGE(
        displayNameEn = "Orange",
        displayNameGu = "નારંગી",
        primaryColor = Color(0xFFFF9800),
        lightHighlight = Color(0xFFFFC066),
        darkShadow = Color(0xFFB86600),
        borderColor = Color(0xFF7A4200),
        glowColor = Color(0xFFFFA726)
    ),
    PURPLE(
        displayNameEn = "Purple",
        displayNameGu = "જાંબલી",
        primaryColor = Color(0xFFAB47BC),
        lightHighlight = Color(0xFFD47AE4),
        darkShadow = Color(0xFF741E85),
        borderColor = Color(0xFF4B0C57),
        glowColor = Color(0xFFBA68C8)
    ),
    BROWN(
        displayNameEn = "Brown",
        displayNameGu = "કથ્થઈ",
        primaryColor = Color(0xFFA17248),
        lightHighlight = Color(0xFFC79E78),
        darkShadow = Color(0xFF6A4729),
        borderColor = Color(0xFF432A15),
        glowColor = Color(0xFFB5885C)
    ),
    LIME(
        displayNameEn = "Lime",
        displayNameGu = "પોપટી",
        primaryColor = Color(0xFF9CCC65),
        lightHighlight = Color(0xFFC5E1A5),
        darkShadow = Color(0xFF689F38),
        borderColor = Color(0xFF33691E),
        glowColor = Color(0xFFAED581)
    ),
    PINK(
        displayNameEn = "Pink",
        displayNameGu = "ગુલાબી",
        primaryColor = Color(0xFFEC407A),
        lightHighlight = Color(0xFFFF80AB),
        darkShadow = Color(0xFFAD1457),
        borderColor = Color(0xFF6E0433),
        glowColor = Color(0xFFF06292)
    );

    companion object {
        fun fromIndex(index: Int): NutColor {
            val entries = entries
            return entries[index % entries.size]
        }
    }
}
