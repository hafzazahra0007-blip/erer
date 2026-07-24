package com.example.data.model

/**
 * Procedural Relaxing Ambient Sounds & Music Themes.
 */
enum class SoundTheme(
    val id: String,
    val title: String,
    val description: String,
    val cost: Int,
    val iconEmoji: String
) {
    GENTLE_WATER(
        id = "classic_water",
        title = "Gentle Water",
        description = "Soothing flowing stream with calm aquatic ambient harmonies",
        cost = 0,
        iconEmoji = "💧"
    ),
    NATURE_HAVEN(
        id = "nature_haven",
        title = "Nature Soundscape",
        description = "Peaceful woodland breeze, soft leaves & harmonic forest pentatonic",
        cost = 0,
        iconEmoji = "🍃"
    ),
    LIGHT_WIND(
        id = "light_wind",
        title = "Whispering Breeze",
        description = "Airy serenity with smooth wind swells & soft harmonic chimes",
        cost = 150,
        iconEmoji = "🌬️"
    ),
    PEACEFUL_PIANO(
        id = "peaceful_piano",
        title = "Peaceful Instrumental",
        description = "Warm piano-like arpeggios, gentle acoustic chords & lullaby tones",
        cost = 250,
        iconEmoji = "🎹"
    ),
    ZEN_MEDITATION(
        id = "zen_meditation",
        title = "Subtle Meditation",
        description = "432Hz singing bowl resonance & deeply grounding tranquil waves",
        cost = 350,
        iconEmoji = "🧘"
    );

    companion object {
        fun fromId(id: String): SoundTheme = entries.find { it.id == id } ?: GENTLE_WATER
    }
}

