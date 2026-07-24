package com.example.data.model

import androidx.compose.ui.graphics.Color

/**
 * Visual glass bottle theme styles with 10 distinct options.
 */
enum class BottleTheme(
    val id: String,
    val title: String,
    val description: String,
    val cost: Int,
    val iconEmoji: String,
    val glassColor: Color,
    val outlineColor: Color,
    val highlightColor: Color,
    val capColor: Color,
    val shapeType: ShapeType
) {
    CLASSIC_GLASS(
        id = "classic_glass",
        title = "Classic Crystal",
        description = "Sleek standard glass cylinder with glossy highlights",
        cost = 0,
        iconEmoji = "🧪",
        glassColor = Color(0x0DFFFFFF),
        outlineColor = Color(0x33FFFFFF),
        highlightColor = Color(0xFFA5C9FF),
        capColor = Color(0xFFA5C9FF),
        shapeType = ShapeType.CYLINDER
    ),
    CRYSTAL_TUBE(
        id = "crystal_tube",
        title = "Lab Test Tube",
        description = "Slender scientific tube with rounded bottom base",
        cost = 0,
        iconEmoji = "🧫",
        glassColor = Color(0x2200F2FE),
        outlineColor = Color(0xAA00F2FE),
        highlightColor = Color(0xDDFFFFFF),
        capColor = Color(0xFF0284C7),
        shapeType = ShapeType.ROUND_BOTTOM
    ),
    EMERALD_POTION(
        id = "emerald_potion",
        title = "Emerald Potion",
        description = "Mystic enchanted potion vial with glowing green crystal tint",
        cost = 350,
        iconEmoji = "❇️",
        glassColor = Color(0x3310B981),
        outlineColor = Color(0xFF34D399),
        highlightColor = Color(0xFFA7F3D0),
        capColor = Color(0xFF047857),
        shapeType = ShapeType.POTION_VIAL
    ),
    NEON_CYBER(
        id = "neon_cyber",
        title = "Cyber Neon",
        description = "Futuristic glowing neon glass container",
        cost = 500,
        iconEmoji = "💡",
        glassColor = Color(0x33EC4899),
        outlineColor = Color(0xFFF43F5E),
        highlightColor = Color(0xFF38BDF8),
        capColor = Color(0xFFA855F7),
        shapeType = ShapeType.HEXAGONAL
    ),
    RUBY_CHALICE(
        id = "ruby_chalice",
        title = "Ruby Decanter",
        description = "Elegant crimson glass decanter with radiant ruby highlights",
        cost = 650,
        iconEmoji = "🍷",
        glassColor = Color(0x33EF4444),
        outlineColor = Color(0xFFF87171),
        highlightColor = Color(0xFFFECACA),
        capColor = Color(0xFFB91C1C),
        shapeType = ShapeType.GOBLET
    ),
    GOLDEN_FLASK(
        id = "golden_flask",
        title = "Royal Flask",
        description = "Ornate golden rim decanter for luxury sorting",
        cost = 800,
        iconEmoji = "🏺",
        glassColor = Color(0x33F59E0B),
        outlineColor = Color(0xFFFBBF24),
        highlightColor = Color(0xFFFEF3C7),
        capColor = Color(0xFFB45309),
        shapeType = ShapeType.FLASK
    ),
    CELESTIAL_AURORA(
        id = "celestial_aurora",
        title = "Cosmic Aurora",
        description = "Shimmering cyan and violet starry night glass prism",
        cost = 1000,
        iconEmoji = "🌌",
        glassColor = Color(0x3306B6D4),
        outlineColor = Color(0xFF22D3EE),
        highlightColor = Color(0xFFE0F2FE),
        capColor = Color(0xFF0284C7),
        shapeType = ShapeType.OCTAGONAL
    ),
    DIAMOND_GLASS(
        id = "diamond_glass",
        title = "Diamond Prism",
        description = "Faceted diamond glass with shimmering edges",
        cost = 1200,
        iconEmoji = "💎",
        glassColor = Color(0x33A855F7),
        outlineColor = Color(0xFFC084FC),
        highlightColor = Color(0xFFF3E8FF),
        capColor = Color(0xFF7E22CE),
        shapeType = ShapeType.DIAMOND
    ),
    OBSIDIAN_PRISM(
        id = "obsidian_prism",
        title = "Obsidian Onyx",
        description = "Sleek dark smoke obsidian glass with glowing silver accents",
        cost = 1500,
        iconEmoji = "🔮",
        glassColor = Color(0x44334155),
        outlineColor = Color(0xFF94A3B8),
        highlightColor = Color(0xFFF1F5F9),
        capColor = Color(0xFF475569),
        shapeType = ShapeType.BEAKER
    ),
    SUNSET_AMBER(
        id = "sunset_amber",
        title = "Sunset Amber",
        description = "Warm glowing golden-orange blown glass bottle",
        cost = 1800,
        iconEmoji = "🌅",
        glassColor = Color(0x33F97316),
        outlineColor = Color(0xFFFB923C),
        highlightColor = Color(0xFFFFEDD5),
        capColor = Color(0xFFC2410C),
        shapeType = ShapeType.HOURGLASS
    );

    enum class ShapeType {
        CYLINDER, ROUND_BOTTOM, HEXAGONAL, FLASK, DIAMOND, GOBLET, POTION_VIAL, OCTAGONAL, BEAKER, HOURGLASS
    }

    companion object {
        fun fromId(id: String): BottleTheme = entries.find { it.id == id } ?: CLASSIC_GLASS
    }
}

