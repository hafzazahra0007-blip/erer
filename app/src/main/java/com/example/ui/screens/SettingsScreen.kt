package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Difficulty
import com.example.ui.viewmodel.GameViewModel

@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val isDark = state.isDarkMode

    val targetColors = state.equippedBackgroundTheme.gradientColors
    val c0 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(0) { Color(0xFFE0F2FE) }, androidx.compose.animation.core.tween(800), label = "bg_c0")
    val c1 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(1) { Color(0xFFBAE6FD) }, androidx.compose.animation.core.tween(800), label = "bg_c1")
    val c2 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(2) { Color(0xFF7DD3FC) }, androidx.compose.animation.core.tween(800), label = "bg_c2")
    val bgBrush = remember(c0, c1, c2) { androidx.compose.ui.graphics.Brush.verticalGradient(listOf(c0, c1, c2)) }

    val cardBg = if (isDark) Color(0xFF1D2023) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF0F172A)
    val subTextColor = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF475569)
    val dividerColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .testTag("settings_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                    }

                    Text(
                        text = "SETTINGS",
                        color = textColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(48.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0x1AFFFFFF) else Color(0x10000000)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SettingToggleRow(
                            label = "Sound Effects",
                            checked = state.isSoundEnabled,
                            isDark = isDark,
                            textColor = textColor,
                            onCheckedChange = { viewModel.toggleSound(it) }
                        )

                        HorizontalDivider(color = dividerColor)

                        SettingToggleRow(
                            label = "Ambient Music",
                            checked = state.isMusicEnabled,
                            isDark = isDark,
                            textColor = textColor,
                            onCheckedChange = { viewModel.toggleMusic(it) }
                        )

                        HorizontalDivider(color = dividerColor)

                        SettingToggleRow(
                            label = "Vibration Feedback",
                            checked = state.isVibrationEnabled,
                            isDark = isDark,
                            textColor = textColor,
                            onCheckedChange = { viewModel.toggleVibration(it) }
                        )

                        HorizontalDivider(color = dividerColor)

                        SettingToggleRow(
                            label = "Dark Mode",
                            checked = state.isDarkMode,
                            isDark = isDark,
                            textColor = textColor,
                            onCheckedChange = { viewModel.toggleDarkMode(it) }
                        )
                    }
                }
            }

            Text(
                text = "Water Sort Puzzle v1.0 • Offline Native Game",
                color = subTextColor,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    label: String,
    checked: Boolean,
    isDark: Boolean,
    textColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF22C55E),
                uncheckedThumbColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                uncheckedTrackColor = if (isDark) Color(0xFF0F172A) else Color(0xFFE2E8F0)
            )
        )
    }
}
