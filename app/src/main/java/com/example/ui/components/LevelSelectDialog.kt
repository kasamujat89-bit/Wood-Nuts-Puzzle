package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Level selection modal with strict level progression locking.
 * Fully supports Light Mode and Dark Mode.
 */
@Composable
fun LevelSelectDialog(
    currentLevel: Int,
    maxLevelUnlocked: Int,
    isDarkMode: Boolean = false,
    language: String,
    onLevelSelected: (Int) -> Unit,
    onLockedLevelClick: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var jumpInput by remember { mutableStateOf("") }

    val cardBg = if (isDarkMode) Color(0xFF161C2E) else Color(0xFFFFFFFF)
    val cardBorder = if (isDarkMode) Color(0xFF384360) else Color(0xFFCBD5E1)
    val titleColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val fieldText = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val fieldBorder = if (isDarkMode) Color(0xFF37474F) else Color(0xFFCBD5E1)

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
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = LanguageStrings.levelSelect(language),
                            color = titleColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (language == "gu") "અનલોક થયેલ: લેવલ $maxLevelUnlocked સુધી" else "Unlocked up to Level $maxLevelUnlocked",
                            color = Color(0xFF2E7D32),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_level_select")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (isDarkMode) Color(0xFF90A4AE) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Direct jump to unlocked levels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = jumpInput,
                        onValueChange = { if (it.all { char -> char.isDigit() }) jumpInput = it.take(6) },
                        placeholder = { Text("Level (1-$maxLevelUnlocked)...", color = Color(0xFF94A3B8), fontSize = 13.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                val target = jumpInput.toIntOrNull()
                                if (target != null && target >= 1) {
                                    if (target <= maxLevelUnlocked) {
                                        onLevelSelected(target)
                                    } else {
                                        onLockedLevelClick(target)
                                    }
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = fieldText,
                            unfocusedTextColor = fieldText,
                            focusedBorderColor = Color(0xFF1E88E5),
                            unfocusedBorderColor = fieldBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("jump_level_input")
                    )

                    Button(
                        onClick = {
                            val target = jumpInput.toIntOrNull()
                            if (target != null && target >= 1) {
                                if (target <= maxLevelUnlocked) {
                                    onLevelSelected(target)
                                } else {
                                    onLockedLevelClick(target)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("jump_level_button")
                    ) {
                        Text("PLAY", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Grid of levels (Levels > maxLevelUnlocked are strictly LOCKED)
                val displayCount = maxOf(maxLevelUnlocked + 12, 32)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    items(displayCount) { index ->
                        val levelNum = index + 1
                        val isUnlocked = levelNum <= maxLevelUnlocked
                        val isCurrent = levelNum == currentLevel
                        val isCompleted = levelNum < maxLevelUnlocked

                        val bgColor = when {
                            isCurrent -> Color(0xFF43A047)
                            isCompleted -> if (isDarkMode) Color(0xFF1E3A5F) else Color(0xFFE0F2FE)
                            isUnlocked -> if (isDarkMode) Color(0xFF26324A) else Color(0xFFF1F5F9)
                            else -> if (isDarkMode) Color(0xFF141926) else Color(0xFFF8FAFC)
                        }

                        val borderColor = when {
                            isCurrent -> Color(0xFF81C784)
                            isCompleted -> if (isDarkMode) Color(0xFF64B5F6) else Color(0xFF7DD3FC)
                            isUnlocked -> if (isDarkMode) Color(0xFF455A7D) else Color(0xFFCBD5E1)
                            else -> if (isDarkMode) Color(0xFF212838) else Color(0xFFE2E8F0)
                        }

                        val levelTextColor = when {
                            isCurrent -> Color.White
                            isCompleted -> if (isDarkMode) Color.White else Color(0xFF0369A1)
                            isUnlocked -> if (isDarkMode) Color.White else Color(0xFF0F172A)
                            else -> Color(0xFF94A3B8)
                        }

                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(bgColor)
                                .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                                .clickable {
                                    if (isUnlocked) {
                                        onLevelSelected(levelNum)
                                    } else {
                                        onLockedLevelClick(levelNum)
                                    }
                                }
                                .testTag("level_item_$levelNum"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUnlocked) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "$levelNum",
                                        color = levelTextColor,
                                        fontSize = 16.sp,
                                        fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Cleared",
                                            tint = Color(0xFF43A047),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            } else {
                                // Locked Level Representation
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = if (isDarkMode) Color(0xFF546E7A) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "$levelNum",
                                        color = if (isDarkMode) Color(0xFF546E7A) else Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
