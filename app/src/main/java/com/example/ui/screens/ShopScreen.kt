package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.data.model.BackgroundTheme
import com.example.data.model.BottleTheme
import com.example.ui.viewmodel.GameViewModel

@Composable
fun ShopScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0 = Bottles, 1 = Backgrounds

    val isDark = state.isDarkMode
    val cardBg = if (isDark) Color(0xFF1E293B) else Color.White
    val cardBorder = if (isDark) Color(0x33FFFFFF) else Color(0xFFE2E8F0)
    val textColor = if (isDark) Color.White else Color(0xFF0F172A)
    val subTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    // Gradual animated background transition
    val targetColors = state.equippedBackgroundTheme.gradientColors
    val c0 by animateColorAsState(targetColors.getOrElse(0) { Color(0xFFE0F2FE) }, tween(800), label = "bg_c0")
    val c1 by animateColorAsState(targetColors.getOrElse(1) { Color(0xFFBAE6FD) }, tween(800), label = "bg_c1")
    val c2 by animateColorAsState(targetColors.getOrElse(2) { Color(0xFF7DD3FC) }, tween(800), label = "bg_c2")
    val bgBrush = remember(c0, c1, c2) { Brush.verticalGradient(listOf(c0, c1, c2)) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .testTag("shop_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(18.dp)
        ) {
            // Header
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
                        .testTag("shop_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor
                    )
                }

                Text(
                    text = "BOTTLES & BACKGROUNDS",
                    color = textColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )

                // Coins Pill Balance
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

            Spacer(modifier = Modifier.height(14.dp))

            // Tabs Row (BOTTLES, BACKGROUNDS)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBg)
                    .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ShopTabButton(
                    text = "BOTTLES",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                ShopTabButton(
                    text = "BACKGROUNDS",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab 0: BOTTLES
            if (selectedTab == 0) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(BottleTheme.entries) { bottle ->
                        val isUnlocked = state.unlockedThemeIds.contains(bottle.id) || bottle.cost == 0
                        val isEquipped = state.equippedBottleTheme == bottle

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
                                Text(text = bottle.iconEmoji, fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = bottle.title,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = bottle.description,
                                    color = subTextColor,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 14.sp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                when {
                                    isEquipped -> {
                                        Text(
                                            text = "✓ EQUIPPED",
                                            color = Color(0xFF0284C7),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp,
                                            letterSpacing = 1.sp,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                    }
                                    isUnlocked -> {
                                        Button(
                                            onClick = { viewModel.equipBottleTheme(bottle) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("EQUIP", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    else -> {
                                        Button(
                                            onClick = { viewModel.buyTheme(bottle.id, "BOTTLE", bottle.cost) },
                                            enabled = state.coins >= bottle.cost,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("🪙 ${bottle.cost}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Tab 1: BACKGROUNDS
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(BackgroundTheme.entries) { bg ->
                        val isUnlocked = state.unlockedThemeIds.contains(bg.id) || bg.cost == 0
                        val isEquipped = state.equippedBackgroundTheme == bg

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
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(bg.brush)
                                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = bg.title,
                                    color = textColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                when {
                                    isEquipped -> {
                                        Text(
                                            text = "✓ EQUIPPED",
                                            color = Color(0xFF0284C7),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp,
                                            letterSpacing = 1.sp,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                    }
                                    isUnlocked -> {
                                        Button(
                                            onClick = { viewModel.equipBackgroundTheme(bg) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("EQUIP", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    else -> {
                                        Button(
                                            onClick = { viewModel.buyTheme(bg.id, "BACKGROUND", bg.cost) },
                                            enabled = state.coins >= bg.cost,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("🪙 ${bg.cost}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
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
}

@Composable
private fun ShopTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF0284C7) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF64748B),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        )
    }
}
