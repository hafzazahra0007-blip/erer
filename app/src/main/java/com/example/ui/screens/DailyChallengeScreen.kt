package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.GameViewModel

@Composable
fun DailyChallengeScreen(
    viewModel: GameViewModel,
    onStartDailyPuzzle: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    val targetColors = state.equippedBackgroundTheme.gradientColors
    val c0 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(0) { Color(0xFFE0F2FE) }, androidx.compose.animation.core.tween(800), label = "bg_c0")
    val c1 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(1) { Color(0xFFBAE6FD) }, androidx.compose.animation.core.tween(800), label = "bg_c1")
    val c2 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(2) { Color(0xFF7DD3FC) }, androidx.compose.animation.core.tween(800), label = "bg_c2")
    val bgBrush = remember(c0, c1, c2) { androidx.compose.ui.graphics.Brush.verticalGradient(listOf(c0, c1, c2)) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .testTag("daily_challenge_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
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
                    text = "DAILY CHALLENGE",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            // Daily Card Hero
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1D2023)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "🎁", fontSize = 56.sp)

                    Text(
                        text = "TODAY'S SPECIAL PUZZLE",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = "Solve today's unique liquid sorting layout and test your master sorting skills!",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )

                    Button(
                        onClick = {
                            viewModel.loadCurrentLevel(state.levelNumber, isDaily = true)
                            onStartDailyPuzzle()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_daily_puzzle_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA5C9FF))
                    ) {
                        Text(
                            text = "START DAILY PUZZLE",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Streak Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1D2023))
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "CURRENT STREAK", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text(text = "🔥 ${state.gameStats.dailyChallengeStreak} Days", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "REWARD", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text(text = "+150 🪙", color = Color(0xFFFEF3C7), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
