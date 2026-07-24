package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.example.ui.viewmodel.GameViewModel

@Composable
fun StatsScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val stats = state.gameStats

    val targetColors = state.equippedBackgroundTheme.gradientColors
    val c0 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(0) { Color(0xFFE0F2FE) }, androidx.compose.animation.core.tween(800), label = "bg_c0")
    val c1 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(1) { Color(0xFFBAE6FD) }, androidx.compose.animation.core.tween(800), label = "bg_c1")
    val c2 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(2) { Color(0xFF7DD3FC) }, androidx.compose.animation.core.tween(800), label = "bg_c2")
    val bgBrush = remember(c0, c1, c2) { androidx.compose.ui.graphics.Brush.verticalGradient(listOf(c0, c1, c2)) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .testTag("stats_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Text(
                    text = "PLAYER STATISTICS",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { StatMetricCard(label = "Levels Cleared", value = "${stats.totalLevelsCompleted}", icon = "🏆") }
                item { StatMetricCard(label = "Total Pours", value = "${stats.totalPoursMade}", icon = "🧪") }
                item { StatMetricCard(label = "Undos Used", value = "${stats.totalUndosUsed}", icon = "↩️") }
                item { StatMetricCard(label = "Hints Used", value = "${stats.totalHintsUsed}", icon = "💡") }
                item { StatMetricCard(label = "Easy Cleared", value = "${stats.easyLevelsCleared}", icon = "🟢") }
                item { StatMetricCard(label = "Medium Cleared", value = "${stats.mediumLevelsCleared}", icon = "🟡") }
                item { StatMetricCard(label = "Hard Cleared", value = "${stats.hardLevelsCleared}", icon = "🔴") }
                item { StatMetricCard(label = "Streak Days", value = "${stats.dailyChallengeStreak}", icon = "🔥") }
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    label: String,
    value: String,
    icon: String
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1D2023)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = label, color = Color(0xFFA5C9FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
