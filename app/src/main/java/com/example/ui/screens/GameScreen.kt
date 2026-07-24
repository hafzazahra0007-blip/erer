package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ui.components.GlassBottleView
import com.example.ui.components.PourStreamCanvas
import com.example.ui.components.ToolBar
import com.example.ui.components.TopBarHeader
import com.example.ui.components.VictoryDialog
import com.example.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateHome: () -> Unit,
    onNavigateShop: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var isPauseModalOpen by remember { mutableStateOf(false) }

    // Map to capture global positions of bottles for pour stream calculation
    val bottlePositions = remember { mutableStateMapOf<Int, Offset>() }

    // Pour Animation Progress (0f -> 1f)
    val animProgress = remember { Animatable(0f) }

    val activePour = state.activePour
    LaunchedEffect(activePour) {
        if (activePour != null) {
            animProgress.snapTo(0f)
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
            )
        } else {
            animProgress.snapTo(0f)
        }
    }

    val targetColors = state.equippedBackgroundTheme.gradientColors
    val c0 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(0) { Color(0xFFE0F2FE) }, androidx.compose.animation.core.tween(800), label = "bg_c0")
    val c1 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(1) { Color(0xFFBAE6FD) }, androidx.compose.animation.core.tween(800), label = "bg_c1")
    val c2 by androidx.compose.animation.animateColorAsState(targetColors.getOrElse(2) { Color(0xFF7DD3FC) }, androidx.compose.animation.core.tween(800), label = "bg_c2")
    val bgBrush = remember(c0, c1, c2) { Brush.verticalGradient(listOf(c0, c1, c2)) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .testTag("game_screen")
    ) {
        // Decorative Leaves Canvas Layer
        DecorativeLeavesOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Bar Header
            TopBarHeader(
                levelNumber = state.levelNumber,
                coins = state.coins,
                difficulty = state.difficulty,
                onBackClick = onNavigateHome,
                onPauseClick = { isPauseModalOpen = true },
                onShopClick = onNavigateShop
            )

            // 2. Hint Banner
            AnimatedVisibility(
                visible = state.hintMove != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF59E0B))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💡 HINT: Pour bottle #${(state.hintMove?.fromIndex ?: 0) + 1} into #${(state.hintMove?.toIndex ?: 0) + 1}",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // 3. Main Glass Bottles Grid Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                val gridColumns = if (state.bottles.size <= 8) 4 else 5

                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 12.dp, top = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(state.bottles) { index, bottle ->
                        val isSource = activePour?.sourceIndex == index
                        val isTarget = activePour?.targetIndex == index

                        val progress = animProgress.value

                        var pourFraction = 0f
                        if (isTarget && activePour != null) {
                            pourFraction = when {
                                progress < 0.30f -> 0f
                                progress < 0.75f -> (progress - 0.30f) / 0.45f
                                else -> 1.0f
                            }
                        }

                        Box(
                            modifier = Modifier
                                .wrapContentSize()
                                .onGloballyPositioned { coordinates ->
                                    bottlePositions[index] = coordinates.positionInRoot()
                                }
                        ) {
                            GlassBottleView(
                                bottle = bottle,
                                theme = state.equippedBottleTheme,
                                pourFraction = pourFraction,
                                isPourSource = false,
                                isPourTarget = isTarget,
                                pourColor = activePour?.color,
                                pourUnits = activePour?.units ?: 1,
                                onClick = { viewModel.onBottleClick(index) },
                                modifier = Modifier
                                    .wrapContentSize()
                                    .graphicsLayer {
                                        if (isSource && activePour != null) {
                                            alpha = 0f
                                        }
                                    }
                            )
                        }
                    }
                }
            }

            // 4. Bottom Tools Bar (Floating White Card)
            ToolBar(
                onUndoClick = { viewModel.undoMove() },
                onRestartClick = { viewModel.restartLevel() },
                onHintClick = { viewModel.useHint() },
                onAddBottleClick = { viewModel.addExtraBottle() },
                onSkipClick = { viewModel.skipLevel() },
                undoCount = state.historyStack.size,
                canUndo = state.historyStack.isNotEmpty()
            )
        }

        // Top-Level Overlay for Pouring Stream and Moving Source Bottle with Highest Z-Index
        if (activePour != null) {
            val sourcePos = bottlePositions[activePour.sourceIndex]
            val targetPos = bottlePositions[activePour.targetIndex]

            if (sourcePos != null && targetPos != null) {
                val density = LocalDensity.current
                val progress = animProgress.value

                val bottleWidthPx = with(density) { 68.dp.toPx() }

                val deltaX = targetPos.x - sourcePos.x
                val deltaY = targetPos.y - sourcePos.y
                val isLeftToRight = deltaX >= 0
                val sideSign = if (isLeftToRight) 1f else -1f

                val targetTranslationX = deltaX - (with(density) { 24.dp.toPx() } * sideSign)
                val targetTranslationY = deltaY - with(density) { 46.dp.toPx() }

                val moveFactor = when {
                    progress < 0.22f -> kotlin.math.sin(progress / 0.22f * (Math.PI / 2)).toFloat()
                    progress < 0.78f -> 1.0f
                    else -> kotlin.math.cos((progress - 0.78f) / 0.22f * (Math.PI / 2)).toFloat()
                }

                val currentSourceX = sourcePos.x + targetTranslationX * moveFactor
                val currentSourceY = sourcePos.y + targetTranslationY * moveFactor

                val maxTiltAngle = if (isLeftToRight) 76f else -76f
                val tiltFactor = when {
                    progress < 0.18f -> 0f
                    progress < 0.32f -> (progress - 0.18f) / 0.14f
                    progress < 0.75f -> 1.0f
                    progress < 0.88f -> 1.0f - (progress - 0.75f) / 0.13f
                    else -> 0f
                }
                val tiltAngle = maxTiltAngle * tiltFactor

                val pourFraction = when {
                    progress < 0.30f -> 0f
                    progress < 0.75f -> (progress - 0.30f) / 0.45f
                    else -> 1.0f
                }

                val streamProgress = when {
                    progress < 0.25f -> 0f
                    progress < 0.32f -> (progress - 0.25f) / 0.07f
                    progress < 0.72f -> 1f
                    progress < 0.80f -> 1f - (progress - 0.72f) / 0.08f
                    else -> 0f
                }.coerceIn(0f, 1f)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(2000f)
                ) {
                    // Pour Liquid Stream Canvas
                    if (streamProgress > 0f) {
                        val streamSourceX = currentSourceX + bottleWidthPx / 2f + (sideSign * with(density) { 14.dp.toPx() })
                        val streamSourceY = currentSourceY + with(density) { 20.dp.toPx() }

                        val streamTargetX = targetPos.x + bottleWidthPx / 2f
                        val streamTargetY = targetPos.y + with(density) { 16.dp.toPx() }

                        PourStreamCanvas(
                            sourcePos = Offset(streamSourceX, streamSourceY),
                            targetPos = Offset(streamTargetX, streamTargetY),
                            color = activePour.color,
                            progress = streamProgress
                        )
                    }

                    // Moving Source Bottle
                    val sourceBottle = state.bottles.getOrNull(activePour.sourceIndex)
                    if (sourceBottle != null) {
                        val currentSourceXDp = with(density) { currentSourceX.toDp() }
                        val currentSourceYDp = with(density) { currentSourceY.toDp() }

                        GlassBottleView(
                            bottle = sourceBottle,
                            theme = state.equippedBottleTheme,
                            tiltAngle = tiltAngle,
                            translationX = 0.dp,
                            translationY = 0.dp,
                            pourFraction = pourFraction,
                            isPourSource = true,
                            isPourTarget = false,
                            pourColor = activePour.color,
                            pourUnits = activePour.units,
                            onClick = {},
                            modifier = Modifier
                                .offset(x = currentSourceXDp, y = currentSourceYDp)
                                .wrapContentSize()
                        )
                    }
                }
            }
        }

        // Victory Dialog
        if (state.isVictoryDialogVisible) {
            VictoryDialog(
                levelNumber = state.levelNumber,
                coinsEarned = state.lastRewardCoins,
                poursCount = state.poursCount,
                onNextLevelClick = { viewModel.nextLevel() },
                onHomeClick = onNavigateHome
            )
        }

        // Pause / Settings Modal
        if (isPauseModalOpen) {
            AlertDialog(
                onDismissRequest = { isPauseModalOpen = false },
                containerColor = Color.White,
                title = {
                    Text(
                        text = "GAME PAUSED",
                        color = Color(0xFF0F172A),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { isPauseModalOpen = false },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                        ) {
                            Text("RESUME", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                isPauseModalOpen = false
                                viewModel.restartLevel()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9))
                        ) {
                            Text("RESTART LEVEL", color = Color(0xFF0F172A))
                        }

                        Button(
                            onClick = {
                                isPauseModalOpen = false
                                onNavigateHome()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9))
                        ) {
                            Text("EXIT TO MENU", color = Color(0xFF0F172A))
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}

@Composable
private fun DecorativeLeavesOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Top-Left Tropical Leaf Corner
        drawPath(
            path = Path().apply {
                moveTo(0f, 0f)
                cubicTo(w * 0.15f, h * 0.04f, w * 0.25f, h * 0.12f, w * 0.18f, h * 0.18f)
                cubicTo(w * 0.1f, h * 0.2f, 0f, h * 0.1f, 0f, 0f)
            },
            brush = Brush.verticalGradient(
                listOf(Color(0xFF22C55E), Color(0xFF15803D))
            )
        )

        // Top-Right Tropical Leaf Corner
        drawPath(
            path = Path().apply {
                moveTo(w, 0f)
                cubicTo(w * 0.85f, h * 0.05f, w * 0.75f, h * 0.14f, w * 0.82f, h * 0.22f)
                cubicTo(w * 0.9f, h * 0.22f, w, h * 0.12f, w, 0f)
            },
            brush = Brush.verticalGradient(
                listOf(Color(0xFF4ADE80), Color(0xFF16A34A))
            )
        )

        // Bottom Water Reflection Effect
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0x0038BDF8), Color(0x330284C7)),
                startY = h * 0.9f,
                endY = h
            ),
            topLeft = Offset(0f, h * 0.9f),
            size = Size(w, h * 0.1f)
        )
    }
}
