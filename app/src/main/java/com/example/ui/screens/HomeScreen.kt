package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Difficulty
import com.example.ui.viewmodel.GameViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onStartGame: () -> Unit,
    onNavigateDaily: () -> Unit,
    onNavigateShop: () -> Unit,
    onNavigateSounds: () -> Unit = {},
    onNavigateStats: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateComingSoon: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showDailyBonusDialog by remember { mutableStateOf(false) }
    var bonusClaimedAmount by remember { mutableStateOf(50) }

    // Date for Daily Challenge Calendar Tile
    val calendar = remember { Calendar.getInstance() }
    val monthStr = remember { SimpleDateFormat("MMM", Locale.US).format(calendar.time).uppercase() }
    val dayStr = remember { calendar.get(Calendar.DAY_OF_MONTH).toString() }

    val targetColors = state.equippedBackgroundTheme.gradientColors
    val c0 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(0) { Color(0xFFE0F2FE) }, androidx.compose.animation.core.tween(800), label = "bg_c0")
    val c1 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(1) { Color(0xFFBAE6FD) }, androidx.compose.animation.core.tween(800), label = "bg_c1")
    val c2 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(2) { Color(0xFF7DD3FC) }, androidx.compose.animation.core.tween(800), label = "bg_c2")
    val bgBrush = remember(c0, c1, c2) { Brush.verticalGradient(listOf(c0, c1, c2)) }

    val textColor = if (state.isDarkMode) Color.White else Color(0xFF0F172A)
    val subTextColor = if (state.isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
    val cardColor = if (state.isDarkMode) Color(0xFF1E293B) else Color.White
    val cardBorder = if (state.isDarkMode) Color(0x33FFFFFF) else Color(0xFFE2E8F0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Top Header Bar: Coins Pill, Daily Bonus, Themes Pill & Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Coins Pill
                        Surface(
                            shape = CircleShape,
                            color = cardColor,
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onNavigateShop() }
                                .testTag("home_coins_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "🪙", fontSize = 14.sp)
                                Text(
                                    text = "${state.coins}",
                                    color = textColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Hourly / 30-Min Bonus Pill
                        if (state.isBonusAvailable) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFEF08A),
                                shadowElevation = 6.dp,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        bonusClaimedAmount = 50
                                        viewModel.claimHourlyBonus()
                                        showDailyBonusDialog = true
                                    }
                                    .testTag("home_daily_bonus_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = "🎁", fontSize = 14.sp)
                                    Text(
                                        text = "+50",
                                        color = Color(0xFF854D0E),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        } else {
                            // Bonus Timer Pill when waiting for next bonus
                            val mins = state.bonusRemainingSeconds / 60
                            val secs = state.bonusRemainingSeconds % 60
                            Surface(
                                shape = CircleShape,
                                color = cardColor.copy(alpha = 0.85f),
                                shadowElevation = 2.dp,
                                modifier = Modifier.clip(CircleShape)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = "⏳", fontSize = 12.sp)
                                    Text(
                                        text = String.format(Locale.US, "%02d:%02d", mins, secs),
                                        color = subTextColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Themes Button
                        Surface(
                            shape = CircleShape,
                            color = cardColor,
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onNavigateShop() }
                                .testTag("home_shop_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "🎨", fontSize = 14.sp)
                                Text(
                                    text = "Themes",
                                    color = textColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Settings Gear Button
                        Surface(
                            shape = CircleShape,
                            color = cardColor,
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("settings_button")
                                .clickable { onNavigateSettings() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = textColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // 2. Hero Header Area with Realistic Glass Bottles & Water Splash Title
                HeroHeaderSection()

                // 3. Central Main White Card Container (Difficulty & Play Button)
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Level Difficulty Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .background(Color(0xFFCBD5E1))
                            )
                            Text(
                                text = "CURRENT PROGRESSION",
                                color = Color(0xFF1E293B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .background(Color(0xFFCBD5E1))
                            )
                        }

                        // Automatic Difficulty Indicator Pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = when (state.difficulty) {
                                Difficulty.EASY -> Color(0xFFDCFCE7)
                                Difficulty.MEDIUM -> Color(0xFFDBEAFE)
                                Difficulty.HARD -> Color(0xFFFEE2E2)
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when (state.difficulty) {
                                    Difficulty.EASY -> Color(0xFF86EFAC)
                                    Difficulty.MEDIUM -> Color(0xFF93C5FD)
                                    Difficulty.HARD -> Color(0xFFFCA5A5)
                                }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "LEVEL ${state.levelNumber}",
                                        color = Color(0xFF0F172A),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = state.difficulty.description,
                                        color = Color(0xFF475569),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = when (state.difficulty) {
                                        Difficulty.EASY -> Color(0xFF22C55E)
                                        Difficulty.MEDIUM -> Color(0xFF2563EB)
                                        Difficulty.HARD -> Color(0xFFDC2626)
                                    }
                                ) {
                                    Text(
                                        text = state.difficulty.title.uppercase(),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }

                        // Huge Emerald Green "PLAY" Button
                        Button(
                            onClick = onStartGame,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .shadow(8.dp, CircleShape)
                                .testTag("play_button"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF22C55E)
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF4ADE80), Color(0xFF16A34A))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play",
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.Start) {
                                        Text(
                                            text = "PLAY",
                                            color = Color.White,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = "LEVEL ${state.levelNumber}",
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Daily Challenge Card
                Surface(
                    onClick = onNavigateDaily,
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("daily_challenge_card")
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Calendar Tile Icon Widget
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(18.dp)
                                            .background(Color(0xFFEF4444)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = monthStr,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dayStr,
                                            color = Color(0xFF0F172A),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "DAILY CHALLENGE",
                                    color = Color(0xFF0F172A),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Complete today's challenge\nand earn rewards!",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = "🪙 +150",
                                        color = Color(0xFF16A34A),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "|",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "🎁 Daily Bonus",
                                        color = Color(0xFF334155),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open Daily",
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // 5. Quick Navigation Cards Row (THEME SHOP & STATISTICS)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // THEME SHOP Card
                    Surface(
                        onClick = onNavigateShop,
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shop_button")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDBEAFE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🏆", fontSize = 22.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE0F2FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Shop",
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "THEME SHOP",
                                    color = Color(0xFF0F172A),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Unlock beautiful\nthemes",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    // STATISTICS Card
                    Surface(
                        onClick = onNavigateStats,
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stats_button")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF3E8FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "📊", fontSize = 22.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF3E8FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Stats",
                                        tint = Color(0xFF9333EA),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "STATISTICS",
                                    color = Color(0xFF0F172A),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "View your game\nprogress",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // 6. Bottom Navigation Bar
            Surface(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color(0xFF0284C7),
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 1: HOME (Active Pill)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🧪", fontSize = 16.sp)
                            Text(
                                text = "HOME",
                                color = Color(0xFF0284C7),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Tab 2: BOTTLES
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigateShop() }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(text = "🍾", fontSize = 18.sp)
                        Text(
                            text = "BOTTLES",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Tab 3: SOUNDS
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigateSounds() }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Sounds",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "SOUNDS",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Tab 4: MORE GAMES
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigateComingSoon() }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = "More Games",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "MORE GAMES",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Daily Bonus Claimed Modal Dialog
        if (showDailyBonusDialog) {
            AlertDialog(
                onDismissRequest = { showDailyBonusDialog = false },
                containerColor = cardColor,
                title = {
                    Text(
                        text = "🎁 DAILY BONUS CLAIMED!",
                        fontWeight = FontWeight.Black,
                        color = textColor,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🪙 +$bonusClaimedAmount COINS",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp
                        )
                        Text(
                            text = "Come back every day to claim bonus coins, unlock free bottle themes & sound effects!",
                            color = subTextColor,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showDailyBonusDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Text("GREAT!", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            )
        }
    }
}

@Composable
private fun HeroHeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Shelf with Realistic Glass Bottles & Center Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background Water Splash Graphic Ring
            Canvas(modifier = Modifier.size(220.dp, 120.dp)) {
                val splashPath = Path().apply {
                    moveTo(size.width * 0.1f, size.height * 0.5f)
                    cubicTo(
                        size.width * 0.2f, size.height * 0.1f,
                        size.width * 0.8f, size.height * 0.1f,
                        size.width * 0.9f, size.height * 0.5f
                    )
                    cubicTo(
                        size.width * 0.8f, size.height * 0.9f,
                        size.width * 0.2f, size.height * 0.9f,
                        size.width * 0.1f, size.height * 0.5f
                    )
                }
                drawPath(
                    path = splashPath,
                    brush = Brush.radialGradient(
                        listOf(Color(0x330284C7), Color(0x0038BDF8)),
                        center = Offset(size.width / 2, size.height / 2)
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Left 3 Glass Bottles
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-6).dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    MiniHeaderBottle(
                        colors = listOf(Color(0xFF0284C7), Color(0xFF38BDF8), Color(0xFF1D4ED8)),
                        heightDp = 100.dp,
                        widthDp = 26.dp
                    )
                    MiniHeaderBottle(
                        colors = listOf(Color(0xFF22C55E), Color(0xFFFACC15), Color(0xFFA855F7)),
                        heightDp = 110.dp,
                        widthDp = 28.dp
                    )
                    MiniHeaderBottle(
                        colors = listOf(Color(0xFFF97316), Color(0xFFFBBF24)),
                        heightDp = 85.dp,
                        widthDp = 22.dp
                    )
                }

                // Center Title Column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "WATER",
                        color = Color(0xFF0284C7),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color(0x40000000),
                                offset = Offset(0f, 4f),
                                blurRadius = 6f
                            )
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "SORT",
                            color = Color(0xFF16A34A),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color(0x40000000),
                                    offset = Offset(0f, 4f),
                                    blurRadius = 6f
                                )
                            )
                        )
                        Text(text = "🍃", fontSize = 18.sp, modifier = Modifier.offset(y = (-10).dp))
                    }

                    Text(
                        text = "Sort Colors, Relax Your Mind",
                        color = Color(0xFF334155),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // Right 3 Glass Bottles
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-6).dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    MiniHeaderBottle(
                        colors = listOf(Color(0xFFEC4899), Color(0xFFFBBF24)),
                        heightDp = 85.dp,
                        widthDp = 22.dp
                    )
                    MiniHeaderBottle(
                        colors = listOf(Color(0xFFF97316), Color(0xFFFACC15), Color(0xFF22C55E), Color(0xFFA855F7)),
                        heightDp = 110.dp,
                        widthDp = 28.dp
                    )
                    MiniHeaderBottle(
                        colors = listOf(Color(0xFF0284C7), Color(0xFF38BDF8), Color(0xFF22C55E)),
                        heightDp = 100.dp,
                        widthDp = 26.dp
                    )
                }
            }

            // Bottom Marble Shelf
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x88FFFFFF), Color(0x33000000))
                        )
                    )
            )
        }
    }
}

@Composable
private fun MiniHeaderBottle(
    colors: List<Color>,
    heightDp: Dp,
    widthDp: Dp
) {
    Canvas(
        modifier = Modifier
            .size(widthDp, heightDp)
    ) {
        val w = size.width
        val h = size.height

        // Outer Glass Bottle Path
        val bottlePath = Path().apply {
            moveTo(w * 0.35f, 0f)
            lineTo(w * 0.65f, 0f)
            lineTo(w * 0.65f, h * 0.15f)
            cubicTo(w * 0.65f, h * 0.22f, w, h * 0.25f, w, h * 0.35f)
            lineTo(w, h * 0.92f)
            cubicTo(w, h, 0f, h, 0f, h * 0.92f)
            lineTo(0f, h * 0.35f)
            cubicTo(0f, h * 0.25f, w * 0.35f, h * 0.22f, w * 0.35f, h * 0.15f)
            close()
        }

        // Draw Cap
        drawRoundRect(
            color = Color(0xFF38BDF8),
            topLeft = Offset(w * 0.3f, 0f),
            size = Size(w * 0.4f, h * 0.12f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Draw Glass Outline & Reflection
        drawPath(
            path = bottlePath,
            color = Color(0x22FFFFFF)
        )

        // Clip & Draw Liquids inside
        clipPath(bottlePath) {
            val liquidTop = h * 0.28f
            val liquidHeight = h * 0.68f
            val layerH = liquidHeight / colors.size

            colors.forEachIndexed { i, c ->
                drawRect(
                    color = c,
                    topLeft = Offset(0f, liquidTop + i * layerH),
                    size = Size(w, layerH + 1f)
                )
            }

            // Glass Shine Gloss
            drawRect(
                color = Color(0x44FFFFFF),
                topLeft = Offset(w * 0.15f, h * 0.25f),
                size = Size(w * 0.2f, h * 0.65f)
            )
        }

        // Glass Outline border
        drawPath(
            path = bottlePath,
            color = Color(0x66FFFFFF),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )
    }
}
