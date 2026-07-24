package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val currentLevel: Int = 1,
    val coins: Int = 300,
    val equippedBottleTheme: String = "classic_glass",
    val equippedBackgroundTheme: String = "deep_space",
    val equippedSoundTheme: String = "classic_water",
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val darkMode: Boolean = true,
    val difficulty: String = "MEDIUM"
)

@Entity(tableName = "unlocked_themes", primaryKeys = ["themeId", "themeType"])
data class UnlockedThemeEntity(
    val themeId: String,
    val themeType: String // "BOTTLE", "BACKGROUND", or "SOUND"
)

@Entity(tableName = "saved_game_state")
data class SavedGameStateEntity(
    @PrimaryKey val levelNumber: Int,
    val bottlesJson: String,
    val historyJson: String,
    val movesCount: Int,
    val isDailyChallenge: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey val dateKey: String, // e.g., "2026-07-24"
    val isCompleted: Boolean = false,
    val coinsRewarded: Int = 150,
    val puzzleSeed: Long = 0
)

@Entity(tableName = "game_stats")
data class GameStatsEntity(
    @PrimaryKey val id: Int = 1,
    val totalLevelsCompleted: Int = 0,
    val totalPoursMade: Int = 0,
    val totalUndosUsed: Int = 0,
    val totalHintsUsed: Int = 0,
    val dailyChallengeStreak: Int = 0,
    val totalCoinsEarned: Int = 0,
    val easyLevelsCleared: Int = 0,
    val mediumLevelsCleared: Int = 0,
    val hardLevelsCleared: Int = 0
)
