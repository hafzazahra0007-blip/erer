package com.example.data.model

import androidx.compose.ui.graphics.Color

/**
 * Vibrant liquid color palette for Water Sort puzzle.
 * Each color includes gradient tones, glow colors, and display name.
 */
enum class LiquidColor(
    val id: Int,
    val displayName: String,
    val primaryColor: Color,
    val topColor: Color,
    val bottomColor: Color,
    val glowColor: Color
) {
    RED(
        id = 1,
        displayName = "Ruby Red",
        primaryColor = Color(0xFFFF3B30),
        topColor = Color(0xFFFF6961),
        bottomColor = Color(0xFFC01C28),
        glowColor = Color(0x66FF3B30)
    ),
    BLUE(
        id = 2,
        displayName = "Ocean Blue",
        primaryColor = Color(0xFF007AFF),
        topColor = Color(0xFF54A0FF),
        bottomColor = Color(0xFF0040DD),
        glowColor = Color(0x66007AFF)
    ),
    YELLOW(
        id = 3,
        displayName = "Solar Yellow",
        primaryColor = Color(0xFFFFCC00),
        topColor = Color(0xFFFFE066),
        bottomColor = Color(0xFFD4A000),
        glowColor = Color(0x66FFCC00)
    ),
    GREEN(
        id = 4,
        displayName = "Emerald Green",
        primaryColor = Color(0xFF34C759),
        topColor = Color(0xFF63E685),
        bottomColor = Color(0xFF1E8238),
        glowColor = Color(0x6634C759)
    ),
    PURPLE(
        id = 5,
        displayName = "Royal Purple",
        primaryColor = Color(0xFFAF52DE),
        topColor = Color(0xFFD48BFF),
        bottomColor = Color(0xFF7B2CBF),
        glowColor = Color(0x66AF52DE)
    ),
    ORANGE(
        id = 6,
        displayName = "Sunset Orange",
        primaryColor = Color(0xFFFF9500),
        topColor = Color(0xFFFFB74D),
        bottomColor = Color(0xFFCC6D00),
        glowColor = Color(0x66FF9500)
    ),
    CYAN(
        id = 7,
        displayName = "Neon Cyan",
        primaryColor = Color(0xFF00F2FE),
        topColor = Color(0xFF70F8FF),
        bottomColor = Color(0xFF0096C7),
        glowColor = Color(0x6600F2FE)
    ),
    PINK(
        id = 8,
        displayName = "Magenta Pink",
        primaryColor = Color(0xFFFF2D55),
        topColor = Color(0xFFFF7597),
        bottomColor = Color(0xFFC2185B),
        glowColor = Color(0x66FF2D55)
    ),
    TEAL(
        id = 9,
        displayName = "Mystic Teal",
        primaryColor = Color(0xFF00B4D8),
        topColor = Color(0xFF48CAE4),
        bottomColor = Color(0xFF0077B6),
        glowColor = Color(0x6600B4D8)
    ),
    LIME(
        id = 10,
        displayName = "Electric Lime",
        primaryColor = Color(0xFFAACC00),
        topColor = Color(0xFFD4E157),
        bottomColor = Color(0xFF708800),
        glowColor = Color(0x66AACC00)
    ),
    BROWN(
        id = 11,
        displayName = "Amber Brown",
        primaryColor = Color(0xFFA52A2A),
        topColor = Color(0xFFC65151),
        bottomColor = Color(0xFF6A1B1B),
        glowColor = Color(0x66A52A2A)
    ),
    WHITE(
        id = 12,
        displayName = "Pearl White",
        primaryColor = Color(0xFFE2E8F0),
        topColor = Color(0xFFFFFFFF),
        bottomColor = Color(0xFFCBD5E1),
        glowColor = Color(0x66E2E8F0)
    );

    companion object {
        fun fromId(id: Int): LiquidColor = entries.find { it.id == id } ?: RED
    }
}
