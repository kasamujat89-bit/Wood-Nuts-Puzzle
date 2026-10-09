package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Settings dialog for sound, vibration, light/dark mode, and language toggle.
 */
@Composable
fun SettingsDialog(
    soundEnabled: Boolean,
    hapticEnabled: Boolean,
    isDarkMode: Boolean,
    language: String,
    onSoundToggle: (Boolean) -> Unit,
    onHapticToggle: (Boolean) -> Unit,
    onDarkModeToggle: (Boolean) -> Unit,
    onLanguageToggle: () -> Unit,
    onResetProgress: () -> Unit,
    onDismiss: () -> Unit
) {
    val cardBg = if (isDarkMode) Color(0xFF161C2E) else Color(0xFFFFFFFF)
    val cardBorder = if (isDarkMode) Color(0xFF384360) else Color(0xFFCBD5E1)
    val textColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val tipBg = if (isDarkMode) Color(0xFF1F263D) else Color(0xFFF1F5F9)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, cardBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = LanguageStrings.settings(language),
                        color = textColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (isDarkMode) Color(0xFF90A4AE) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dark / Light Mode Setting Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Theme",
                            tint = if (isDarkMode) Color(0xFF90CAF9) else Color(0xFFF59E0B)
                        )
                        Text(
                            text = "  ${LanguageStrings.themeMode(language, isDarkMode)}",
                            color = textColor,
                            fontSize = 16.sp
                        )
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onDarkModeToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF90CAF9),
                            checkedTrackColor = Color(0xFF1E3A5F),
                            uncheckedThumbColor = Color(0xFFF59E0B),
                            uncheckedTrackColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.testTag("dark_mode_switch")
                    )
                }

                // Sound Setting Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Sound",
                            tint = Color(0xFFFFD54F)
                        )
                        Text(
                            text = "  ${LanguageStrings.sound(language)}",
                            color = textColor,
                            fontSize = 16.sp
                        )
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = onSoundToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFD54F),
                            checkedTrackColor = Color(0xFF795548),
                            uncheckedTrackColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.testTag("sound_switch")
                    )
                }

                // Vibration Setting Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Vibration",
                            tint = Color(0xFF42A5F5)
                        )
                        Text(
                            text = "  ${LanguageStrings.vibration(language)}",
                            color = textColor,
                            fontSize = 16.sp
                        )
                    }
                    Switch(
                        checked = hapticEnabled,
                        onCheckedChange = onHapticToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF42A5F5),
                            checkedTrackColor = Color(0xFF1E3A5F),
                            uncheckedTrackColor = Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.testTag("vibration_switch")
                    )
                }

                // Language Toggle Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Language",
                            tint = Color(0xFF43A047)
                        )
                        Text(
                            text = "  ${LanguageStrings.language(language)}",
                            color = textColor,
                            fontSize = 16.sp
                        )
                    }
                    IconButton(
                        onClick = onLanguageToggle,
                        modifier = Modifier
                            .background(
                                color = if (isDarkMode) Color(0xFF26324A) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isDarkMode) Color(0xFF455A7D) else Color(0xFF94A3B8),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .testTag("toggle_language_btn")
                    ) {
                        Text(
                            text = if (language == "gu") "EN" else "ગુજ",
                            color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // How to play tip
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = tipBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (language == "gu") "કેવી રીતે રમવું:" else "How to Play:",
                            color = if (isDarkMode) Color(0xFFFFD54F) else Color(0xFFD97706),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == "gu")
                                "1. બોલ્ટ પર ટેપ કરી ઉપરનો નટ ઊંચકો.\n2. ખાલી અથવા સમાન રંગ ધરાવતા બોલ્ટ પર મૂકો.\n3. બધા બોલ્ટ સિંગલ કલરમાં સોર્ટ કરી જીતો!"
                            else
                                "1. Tap a bolt to lift its top nut.\n2. Tap an empty bolt or one with matching color.\n3. Sort all nuts by color to win!",
                            color = if (isDarkMode) Color(0xFFCFD8DC) else Color(0xFF475569),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Reset Progress Row
                Row(
                    modifier = Modifier
                        .clickable(onClick = onResetProgress)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "  " + if (language == "gu") "પ્રગતિ રીસેટ કરો (લેવલ 1)" else "Reset All Levels (Start at Level 1)",
                        color = Color(0xFFE53935),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
