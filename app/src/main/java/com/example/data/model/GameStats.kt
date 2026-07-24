package com.example.data.model

data class GameStats(
    val totalLevelsCompleted: Int = 0,
    val totalPoursMade: Int = 0,
    val totalUndosUsed: Int = 0,
    val totalHintsUsed: Int = 0,
    val dailyChallengeStreak: Int = 0,
    val totalCoinsEarned: Int = 0,
    val easyLevelsCleared: Int = 0,
    val mediumLevelsCleared: Int = 0,
    val hardLevelsCleared: Int = 0
) {
    val winRate: Float
        get() = if (totalLevelsCompleted == 0) 100f else 100f
}
