package com.example.data.model

enum class Difficulty(
    val title: String,
    val description: String
) {
    EASY("Easy", "2–4 Colors • Relaxing Puzzles"),
    MEDIUM("Medium", "5–7 Colors • Balanced Challenge"),
    HARD("Hard", "8–10 Colors • Master Brain Teaser");

    companion object {
        fun fromName(name: String): Difficulty = entries.find { it.name == name } ?: MEDIUM

        /**
         * Automatically assigns difficulty based on the level number.
         * - Levels 1–10: Easy
         * - Levels 11–20: Medium
         * - Levels 21+: Hard at regular intervals (e.g. 23, 25, 28, 30, 33, 35...)
         */
        fun forLevel(levelNumber: Int): Difficulty {
            return when {
                levelNumber <= 10 -> EASY
                levelNumber <= 20 -> MEDIUM
                else -> {
                    if (isHardLevel(levelNumber)) HARD else MEDIUM
                }
            }
        }

        fun isHardLevel(levelNumber: Int): Boolean {
            if (levelNumber <= 20) return false
            val rem = levelNumber % 10
            return (levelNumber % 3 == 0) || rem == 3 || rem == 5 || rem == 8 || rem == 0
        }
    }
}
