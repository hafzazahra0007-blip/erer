package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    // User Progress
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getUserProgress(): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE id = 1")
    suspend fun getUserProgressDirect(): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProgress(progress: UserProgressEntity)

    // Unlocked Themes
    @Query("SELECT * FROM unlocked_themes")
    fun getUnlockedThemes(): Flow<List<UnlockedThemeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun unlockTheme(theme: UnlockedThemeEntity)

    // Saved Board State
    @Query("SELECT * FROM saved_game_state WHERE levelNumber = :levelNumber")
    suspend fun getSavedGameState(levelNumber: Int): SavedGameStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGameState(state: SavedGameStateEntity)

    @Query("DELETE FROM saved_game_state WHERE levelNumber = :levelNumber")
    suspend fun clearSavedGameState(levelNumber: Int)

    // Daily Challenges
    @Query("SELECT * FROM daily_challenges WHERE dateKey = :dateKey")
    suspend fun getDailyChallenge(dateKey: String): DailyChallengeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDailyChallenge(challenge: DailyChallengeEntity)

    @Query("SELECT * FROM daily_challenges ORDER BY dateKey DESC LIMIT 30")
    fun getRecentDailyChallenges(): Flow<List<DailyChallengeEntity>>

    // Game Stats
    @Query("SELECT * FROM game_stats WHERE id = 1")
    fun getGameStats(): Flow<GameStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGameStats(stats: GameStatsEntity)
}
