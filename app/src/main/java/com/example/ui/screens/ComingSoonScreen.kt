package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.GameViewModel

@Composable
fun ComingSoonScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val isDark = state.isDarkMode

    val targetColors = state.equippedBackgroundTheme.gradientColors
    val c0 by animateColorAsState(targetColors.getOrElse(0) { Color(0xFFE0F2FE) }, tween(800), label = "bg_c0")
    val c1 by animateColorAsState(targetColors.getOrElse(1) { Color(0xFFBAE6FD) }, tween(800), label = "bg_c1")
    val c2 by animateColorAsState(targetColors.getOrElse(2) { Color(0xFF7DD3FC) }, tween(800), label = "bg_c2")
    val bgBrush = remember(c0, c1, c2) { Brush.verticalGradient(listOf(c0, c1, c2)) }

    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF0F172A)
    val subTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    // Infinite Animation for pulse glow
    val infiniteTransition = rememberInfiniteTransition(label = "coming_soon_anim")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .testTag("coming_soon_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF334155) else Color.White)
                        .testTag("coming_soon_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "MORE GAMES",
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            // Central Animated Card
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = cardBg,
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(28.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Animated Glowing Game Controller & Sparkle Canvas
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(scalePulse),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val radius = size.minDimension / 2f
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF38BDF8).copy(alpha = 0.4f),
                                        Color(0xFF818CF8).copy(alpha = 0.2f),
                                        Color.Transparent
                                    ),
                                    center = center,
                                    radius = radius * 1.2f
                                ),
                                center = center,
                                radius = radius * 1.2f
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF0284C7), Color(0xFF6366F1))
                                    )
                                )
                                .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    // Status Badge
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF0284C7).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "IN DEVELOPMENT",
                                color = Color(0xFF0284C7),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Main Title
                    Text(
                        text = "COMING SOON!",
                        color = textColor,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )

                    // Description text
                    Text(
                        text = "We are currently handcrafting brand new liquid puzzle game modes, speed sorting challenges, and rewards for you!",
                        color = subTextColor,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Divider(color = subTextColor.copy(alpha = 0.2f))

                    // Feature highlights list
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ComingSoonFeatureItem(
                            emoji = "⚡",
                            title = "Timed Liquid Rush",
                            subtitle = "Race against the clock to sort fast!",
                            textColor = textColor,
                            subTextColor = subTextColor
                        )
                        ComingSoonFeatureItem(
                            emoji = "🔮",
                            title = "Magic Color Alchemy",
                            subtitle = "Combine special potion layers for bonus coins!",
                            textColor = textColor,
                            subTextColor = subTextColor
                        )
                        ComingSoonFeatureItem(
                            emoji = "⚔️",
                            title = "Multiplayer Duels",
                            subtitle = "Challenge friends in real-time sorting races!",
                            textColor = textColor,
                            subTextColor = subTextColor
                        )
                    }
                }
            }

            // Return Button
            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(4.dp, CircleShape),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = "RETURN TO GAME",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ComingSoonFeatureItem(
    emoji: String,
    title: String,
    subtitle: String,
    textColor: Color,
    subTextColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(textColor.copy(alpha = 0.05f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = emoji, fontSize = 24.sp)
        Column {
            Text(
                text = title,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = subtitle,
                color = subTextColor,
                fontSize = 12.sp
            )
        }
    }
}
