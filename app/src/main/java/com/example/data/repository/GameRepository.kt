package com.example.data.repository

import com.example.data.db.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class GameRepository(private val gameDao: GameDao) {

    val userProgress: Flow<UserProgressEntity> = gameDao.getUserProgress()
        .map { it ?: UserProgressEntity() }

    val unlockedThemes: Flow<List<UnlockedThemeEntity>> = gameDao.getUnlockedThemes()

    val gameStats: Flow<GameStatsEntity> = gameDao.getGameStats()
        .map { it ?: GameStatsEntity() }

    suspend fun saveProgress(progress: UserProgressEntity) {
        gameDao.saveUserProgress(progress)
    }

    suspend fun addCoins(amount: Int) {
        val current = gameDao.getUserProgressDirect() ?: UserProgressEntity()
        val updated = current.copy(coins = current.coins + amount)
        gameDao.saveUserProgress(updated)
    }

    suspend fun unlockTheme(themeId: String, themeType: String) {
        gameDao.unlockTheme(UnlockedThemeEntity(themeId, themeType))
    }

    suspend fun saveBoardState(levelNumber: Int, bottles: List<Bottle>, history: List<List<Bottle>>, moves: Int, isDaily: Boolean = false) {
        val bottlesArray = JSONArray()
        bottles.forEach { bottle ->
            val bottleObj = JSONObject()
            bottleObj.put("id", bottle.id)
            bottleObj.put("isExtra", bottle.isExtraBottle)
            val layersArray = JSONArray()
            bottle.layers.forEach { layersArray.put(it.id) }
            bottleObj.put("layers", layersArray)
            bottlesArray.put(bottleObj)
        }

        val historyArray = JSONArray()
        history.forEach { snapshot ->
            val snapArray = JSONArray()
            snapshot.forEach { bottle ->
                val bottleObj = JSONObject()
                bottleObj.put("id", bottle.id)
                bottleObj.put("isExtra", bottle.isExtraBottle)
                val layersArray = JSONArray()
                bottle.layers.forEach { layersArray.put(it.id) }
                bottleObj.put("layers", layersArray)
                snapArray.put(bottleObj)
            }
            historyArray.put(snapArray)
        }

        val entity = SavedGameStateEntity(
            levelNumber = levelNumber,
            bottlesJson = bottlesArray.toString(),
            historyJson = historyArray.toString(),
            movesCount = moves,
            isDailyChallenge = isDaily
        )
        gameDao.saveGameState(entity)
    }

    suspend fun loadBoardState(levelNumber: Int): Pair<List<Bottle>, List<List<Bottle>>>? {
        val saved = gameDao.getSavedGameState(levelNumber) ?: return null
        val bottles = parseBottlesJson(saved.bottlesJson)
        val historyArray = JSONArray(saved.historyJson)
        val history = mutableListOf<List<Bottle>>()
        for (i in 0 until historyArray.length()) {
            history.add(parseBottlesJson(historyArray.getJSONArray(i).toString()))
        }
        return Pair(bottles, history)
    }

    suspend fun clearBoardState(levelNumber: Int) {
        gameDao.clearSavedGameState(levelNumber)
    }

    private fun parseBottlesJson(jsonStr: String): List<Bottle> {
        val list = mutableListOf<Bottle>()
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val id = obj.getInt("id")
            val isExtra = obj.optBoolean("isExtra", false)
            val layersArray = obj.getJSONArray("layers")
            val layers = mutableListOf<LiquidColor>()
            for (j in 0 until layersArray.length()) {
                layers.add(LiquidColor.fromId(layersArray.getInt(j)))
            }
            list.add(Bottle(id = id, layers = layers, isExtraBottle = isExtra))
        }
        return list
    }

    suspend fun recordLevelCompleted(difficultyStr: String, poursMade: Int, undosUsed: Int, hintsUsed: Int) {
        val stats = gameDao.getGameStats()
        // update stats
        var current = gameDao.getUserProgressDirect() ?: UserProgressEntity()
        var currentStats = GameStatsEntity()
        val updatedStats = currentStats.copy(
            totalLevelsCompleted = currentStats.totalLevelsCompleted + 1,
            totalPoursMade = currentStats.totalPoursMade + poursMade,
            totalUndosUsed = currentStats.totalUndosUsed + undosUsed,
            totalHintsUsed = currentStats.totalHintsUsed + hintsUsed,
            easyLevelsCleared = currentStats.easyLevelsCleared + if (difficultyStr == "EASY") 1 else 0,
            mediumLevelsCleared = currentStats.mediumLevelsCleared + if (difficultyStr == "MEDIUM") 1 else 0,
            hardLevelsCleared = currentStats.hardLevelsCleared + if (difficultyStr == "HARD") 1 else 0
        )
        gameDao.saveGameStats(updatedStats)
    }
}
