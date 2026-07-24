package com.example.data.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Background themes for the game canvas with 10 distinct options.
 */
enum class BackgroundTheme(
    val id: String,
    val title: String,
    val cost: Int,
    val gradientColors: List<Color>,
    val cardColor: Color,
    val textColor: Color
) {
    DEEP_SPACE(
        id = "deep_space",
        title = "Sunny Sky Light",
        cost = 0,
        gradientColors = listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD), Color(0xFF7DD3FC)),
        cardColor = Color.White,
        textColor = Color(0xFF0F172A)
    ),
    MIDNIGHT_CYBER(
        id = "midnight_cyber",
        title = "Cyberpunk Night",
        cost = 0,
        gradientColors = listOf(Color(0xFF0D0221), Color(0xFF0F0826), Color(0xFF190028)),
        cardColor = Color(0x33240046),
        textColor = Color(0xFFF0F6FC)
    ),
    CHERRY_BLOSSOM(
        id = "cherry_blossom",
        title = "Sakura Blossom",
        cost = 350,
        gradientColors = listOf(Color(0xFF500724), Color(0xFF831843), Color(0xFF9F1239)),
        cardColor = Color(0x33831843),
        textColor = Color(0xFFFDF2F8)
    ),
    ZEN_BAMBOO(
        id = "zen_bamboo",
        title = "Zen Sanctuary",
        cost = 450,
        gradientColors = listOf(Color(0xFF064E3B), Color(0xFF022C22), Color(0xFF065F46)),
        cardColor = Color(0x33047857),
        textColor = Color(0xFFECFDF5)
    ),
    OCEAN_ABYSS(
        id = "ocean_abyss",
        title = "Deep Sea Abyss",
        cost = 600,
        gradientColors = listOf(Color(0xFF0C4A6E), Color(0xFF0369A1), Color(0xFF0284C7)),
        cardColor = Color(0x33075985),
        textColor = Color(0xFFF0F9FF)
    ),
    ENCHANTED_FOREST(
        id = "enchanted_forest",
        title = "Forest Emerald",
        cost = 750,
        gradientColors = listOf(Color(0xFF14532D), Color(0xFF15803D), Color(0xFF166534)),
        cardColor = Color(0x33166534),
        textColor = Color(0xFFF0FDF4)
    ),
    SUNSET_GLOW(
        id = "sunset_glow",
        title = "Sunset Serenade",
        cost = 900,
        gradientColors = listOf(Color(0xFF4C0519), Color(0xFF831843), Color(0xFF581C87)),
        cardColor = Color(0x339F1239),
        textColor = Color(0xFFFFF1F2)
    ),
    ROYAL_GOLD(
        id = "royal_gold",
        title = "Imperial Marble",
        cost = 1100,
        gradientColors = listOf(Color(0xFF1C1917), Color(0xFF292524), Color(0xFF0C0A09)),
        cardColor = Color(0x3344403C),
        textColor = Color(0xFFFEF3C7)
    ),
    VOLCANIC_MAGMA(
        id = "volcanic_magma",
        title = "Molten Lava",
        cost = 1350,
        gradientColors = listOf(Color(0xFF450A0A), Color(0xFF7F1D1D), Color(0xFF991B1B)),
        cardColor = Color(0x33991B1B),
        textColor = Color(0xFFFEF2F2)
    ),
    NEBULA_COSMOS(
        id = "nebula_cosmos",
        title = "Galactic Nebula",
        cost = 1600,
        gradientColors = listOf(Color(0xFF311042), Color(0xFF581C87), Color(0xFF7E22CE)),
        cardColor = Color(0x336B21A8),
        textColor = Color(0xFFFAF5FF)
    );

    val brush: Brush get() = Brush.verticalGradient(gradientColors)

    companion object {
        fun fromId(id: String): BackgroundTheme = entries.find { it.id == id } ?: DEEP_SPACE
    }
}

