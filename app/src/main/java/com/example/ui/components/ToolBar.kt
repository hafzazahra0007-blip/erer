package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ToolBar(
    onUndoClick: () -> Unit,
    onRestartClick: () -> Unit,
    onHintClick: () -> Unit,
    onAddBottleClick: () -> Unit,
    onSkipClick: () -> Unit,
    undoCount: Int,
    canUndo: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 10.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. UNDO Tool
            ToolItem(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = "UNDO",
                badgeText = if (canUndo) "$undoCount" else null,
                enabled = canUndo,
                testTag = "undo_button",
                onClick = onUndoClick
            )

            // 2. RESTART Tool
            ToolItem(
                icon = Icons.Default.Refresh,
                label = "RESTART",
                testTag = "restart_button",
                onClick = onRestartClick
            )

            // 3. HINT Tool
            ToolItem(
                icon = Icons.Default.Lightbulb,
                label = "HINT",
                badgeText = "🪙 50",
                testTag = "hint_button",
                onClick = onHintClick
            )

            // 4. +BOTTLE Tool
            ToolItem(
                icon = Icons.Default.Add,
                label = "+BOTTLE",
                badgeText = "🪙 50",
                testTag = "extra_bottle_button",
                onClick = onAddBottleClick
            )

            // 5. SKIP Tool (Vibrant Green Primary Button)
            ToolItem(
                icon = Icons.Default.SkipNext,
                label = "SKIP",
                badgeText = "🪙 100",
                testTag = "skip_button",
                isPrimary = true,
                onClick = onSkipClick
            )
        }
    }
}

@Composable
private fun ToolItem(
    icon: ImageVector,
    label: String,
    badgeText: String? = null,
    enabled: Boolean = true,
    isPrimary: Boolean = false,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.testTag(testTag)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isPrimary) {
                            Brush.verticalGradient(listOf(Color(0xFF4ADE80), Color(0xFF16A34A)))
                        } else if (enabled) {
                            Brush.verticalGradient(listOf(Color(0xFFF8FAFC), Color(0xFFEFF6FF)))
                        } else {
                            Brush.verticalGradient(listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0)))
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isPrimary) Color(0xFF22C55E) else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable(enabled = enabled, onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isPrimary) Color.White else if (enabled) Color(0xFF0284C7) else Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Badge pill on top right
            if (badgeText != null) {
                Surface(
                    shape = CircleShape,
                    color = if (isPrimary) Color(0xFFFEF08A) else Color(0xFFFEF3C7),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color(0xFF78350F),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Text(
            text = label,
            color = if (isPrimary) Color(0xFF15803D) else if (enabled) Color(0xFF0F172A) else Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
    }
}
