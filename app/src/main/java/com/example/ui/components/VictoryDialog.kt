package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Nut
import com.example.model.NutColor

/**
 * Victory celebration dialog shown after completing a level.
 * Features animated scale-in, star ratings, coin reward badge, and Next Level button.
 */
@Composable
fun VictoryDialog(
    level: Int,
    stars: Int,
    moves: Int,
    coinsEarned: Int,
    isDarkMode: Boolean = false,
    language: String,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onDismiss: () -> Unit
) {
    var animateIn by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "victoryScale"
    )

    LaunchedEffect(Unit) {
        animateIn = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) Color(0xFF171D33) else Color(0xFFFFFFFF)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .border(
                        width = 3.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFD54F), Color(0xFFFF9800), Color(0xFFE91E63))
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Victory Top Banner Plate (Red / Gold 3D Badge)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFFE53935), Color(0xFFB71C1C))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(2.dp, Color(0xFFFFE082), RoundedCornerShape(16.dp))
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = LanguageStrings.victory(language),
                                color = Color(0xFFFFEB3B),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "NUT SORT • ${LanguageStrings.level(language, level)}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 2. Stars Rating (1 to 3 stars)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..3) {
                            val isFilled = i <= stars
                            val starSize = if (i == 2) 48.dp else 38.dp
                            Icon(
                                imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Star $i",
                                tint = if (isFilled) Color(0xFFFFD700) else Color(0xFF4A5568),
                                modifier = Modifier.size(starSize)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3. Central Nut Reward Token & Coins
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(12.dp, CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFFECB3), Color(0xFFFFC107), Color(0xFFFF8F00))
                                ),
                                shape = CircleShape
                            )
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            NutView(
                                nut = Nut(id = 999, color = NutColor.YELLOW),
                                width = 54.dp,
                                height = 22.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+$coinsEarned",
                                color = Color(0xFF3E2723),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = LanguageStrings.moves(language, moves),
                        color = if (isDarkMode) Color(0xFFB0BEC5) else Color(0xFF475569),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. Large Green "NEXT LEVEL" / "PLAY NOW" Action Button
                    Button(
                        onClick = onNextLevel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF43A047)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(58.dp)
                            .testTag("next_level_button")
                            .shadow(10.dp, RoundedCornerShape(20.dp))
                            .border(2.dp, Color(0xFFC8E6C9), RoundedCornerShape(20.dp))
                    ) {
                        Text(
                            text = LanguageStrings.playNext(language),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Replay button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = onReplay,
                            modifier = Modifier.testTag("replay_level_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = LanguageStrings.restart(language),
                                tint = if (isDarkMode) Color(0xFF90CAF9) else Color(0xFF1E88E5),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = LanguageStrings.restart(language),
                            color = if (isDarkMode) Color(0xFF90CAF9) else Color(0xFF1E88E5),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
