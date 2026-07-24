package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SoundTheme
import com.example.ui.viewmodel.GameViewModel

@Composable
fun SoundsScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    val isDark = state.isDarkMode
    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val cardBorder = if (isDark) Color(0x33FFFFFF) else Color(0xFFE2E8F0)
    val textColor = if (isDark) Color.White else Color(0xFF0F172A)
    val subTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    // Animated background gradient from current equipped background
    val targetColors = state.equippedBackgroundTheme.gradientColors
    val c0 by animateColorAsState(targetColors.getOrElse(0) { Color(0xFFE0F2FE) }, tween(800), label = "bg_c0")
    val c1 by animateColorAsState(targetColors.getOrElse(1) { Color(0xFFBAE6FD) }, tween(800), label = "bg_c1")
    val c2 by animateColorAsState(targetColors.getOrElse(2) { Color(0xFF7DD3FC) }, tween(800), label = "bg_c2")
    val bgBrush = remember(c0, c1, c2) { Brush.verticalGradient(listOf(c0, c1, c2)) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .testTag("sounds_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(cardBg)
                        .testTag("sounds_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor
                    )
                }

                Text(
                    text = "RELAXING SOUNDS",
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )

                // Coins Balance Pill
                Surface(
                    shape = CircleShape,
                    color = cardBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🪙", fontSize = 16.sp)
                        Text(
                            text = "${state.coins}",
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Master Audio Toggles Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🎧 AUDIO PREFERENCES",
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    // Sound Effects Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(text = "Sound Effects", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Soft liquid pours & gentle taps", color = subTextColor, fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = state.isSoundEnabled,
                            onCheckedChange = { viewModel.toggleSound(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0284C7))
                        )
                    }

                    HorizontalDivider(color = subTextColor.copy(alpha = 0.2f))

                    // Ambient Music Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color(0xFFA855F7),
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(text = "Ambient Music", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Calming procedural background melody", color = subTextColor, fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = state.isMusicEnabled,
                            onCheckedChange = { viewModel.toggleMusic(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFFA855F7))
                        )
                    }

                    HorizontalDivider(color = subTextColor.copy(alpha = 0.2f))

                    // Haptic Vibration Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = null,
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(text = "Haptic Vibration", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Tactile feedback during fluid transfer", color = subTextColor, fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = state.isVibrationEnabled,
                            onCheckedChange = { viewModel.toggleVibration(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF22C55E))
                        )
                    }
                }
            }

            // Section Header
            Text(
                text = "SOFT & RELAXING SOUND THEMES",
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            // 5 Soft Procedural Sound Themes Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(SoundTheme.entries) { theme ->
                    val isUnlocked = state.unlockedThemeIds.contains(theme.id) || theme.cost == 0
                    val isEquipped = state.equippedSoundTheme == theme

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isEquipped) 2.5.dp else 1.dp,
                            color = if (isEquipped) Color(0xFF0284C7) else cardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Top Row with Emoji and Preview Play Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = theme.iconEmoji, fontSize = 32.sp)

                                val isPreviewing = state.previewingThemeId == theme.id

                                // Preview Sound Button
                                IconButton(
                                    onClick = { viewModel.previewSoundTheme(theme) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isPreviewing) Color(0xFFEF4444) else Color(0xFF0284C7).copy(alpha = 0.15f))
                                ) {
                                    Icon(
                                        imageVector = if (isPreviewing) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = if (isPreviewing) "Stop Preview" else "Preview Ambient Sound",
                                        tint = if (isPreviewing) Color.White else Color(0xFF0284C7),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = theme.title,
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = theme.description,
                                color = subTextColor,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            when {
                                isEquipped -> {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF0284C7).copy(alpha = 0.15f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "✓ EQUIPPED",
                                            color = Color(0xFF0284C7),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp,
                                            letterSpacing = 1.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                                isUnlocked -> {
                                    Button(
                                        onClick = { viewModel.equipSoundTheme(theme) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = CircleShape
                                    ) {
                                        Text("EQUIP", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                                else -> {
                                    Button(
                                        onClick = { viewModel.buyTheme(theme.id, "SOUND", theme.cost) },
                                        enabled = state.coins >= theme.cost,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = CircleShape
                                    ) {
                                        Text("🪙 ${theme.cost}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
