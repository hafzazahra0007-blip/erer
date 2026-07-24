package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.UserProgressEntity
import com.example.data.model.*
import com.example.data.repository.GameRepository
import com.example.engine.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ActivePourAnimation(
    val sourceIndex: Int,
    val targetIndex: Int,
    val color: LiquidColor,
    val units: Int
)

data class GameUiState(
    val levelNumber: Int = 1,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val bottles: List<Bottle> = emptyList(),
    val historyStack: List<List<Bottle>> = emptyList(),
    val movesCount: Int = 0,
    val poursCount: Int = 0,
    val coins: Int = 300,
    val equippedBottleTheme: BottleTheme = BottleTheme.CLASSIC_GLASS,
    val equippedBackgroundTheme: BackgroundTheme = BackgroundTheme.DEEP_SPACE,
    val equippedSoundTheme: SoundTheme = SoundTheme.GENTLE_WATER,
    val unlockedThemeIds: Set<String> = setOf(
        "classic_glass", "crystal_tube",
        "deep_space", "midnight_cyber",
        "classic_water", "nature_haven"
    ),
    val lastRewardCoins: Int = 10,
    val isSoundEnabled: Boolean = true,
    val isMusicEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val isDarkMode: Boolean = true,
    val isVictoryDialogVisible: Boolean = false,
    val hintMove: WaterSortSolver.Move? = null,
    val isDailyChallenge: Boolean = false,
    val activePour: ActivePourAnimation? = null,
    val gameStats: GameStats = GameStats(),
    val isBonusAvailable: Boolean = false,
    val bonusRemainingSeconds: Long = 0L,
    val previewingThemeId: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    val audioManager = AudioEffectManager()
    val vibrationManager = VibrationManager(application)

    private val prefs = application.getSharedPreferences("water_sort_prefs", android.content.Context.MODE_PRIVATE)
    private var lastBonusClaimTime: Long
        get() = prefs.getLong("last_bonus_claim_time", 0L)
        set(value) = prefs.edit().putLong("last_bonus_claim_time", value).apply()

    private val appOpenTime = System.currentTimeMillis()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = GameRepository(database.gameDao())

        // Hourly / Half-Hour Bonus ticker
        viewModelScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val timeSinceLastClaim = now - lastBonusClaimTime
                
                // If game is OPEN:
                // 1) If user returned after app was closed for 2.5-3 hours: bonus is available!
                // 2) If app is open and user plays, bonus appears every 30 mins (1,800,000 ms)
                val isClaimReady = if (lastBonusClaimTime == 0L) {
                    true
                } else {
                    timeSinceLastClaim >= (30 * 60 * 1000L) || (timeSinceLastClaim >= (2.5 * 3600 * 1000L))
                }

                val remainingMillis = if (isClaimReady) 0L else maxOf(0L, (30 * 60 * 1000L) - timeSinceLastClaim)

                _uiState.update {
                    it.copy(
                        isBonusAvailable = isClaimReady,
                        bonusRemainingSeconds = remainingMillis / 1000L
                    )
                }

                kotlinx.coroutines.delay(1000L)
            }
        }

        // Observe progress from DB
        viewModelScope.launch {
            repository.userProgress.collect { progress ->
                val soundTheme = SoundTheme.fromId(progress.equippedSoundTheme)
                val autoDifficulty = Difficulty.forLevel(progress.currentLevel)
                _uiState.update { state ->
                    state.copy(
                        levelNumber = progress.currentLevel,
                        coins = progress.coins,
                        equippedBottleTheme = BottleTheme.fromId(progress.equippedBottleTheme),
                        equippedBackgroundTheme = BackgroundTheme.fromId(progress.equippedBackgroundTheme),
                        equippedSoundTheme = soundTheme,
                        isSoundEnabled = progress.soundEnabled,
                        isMusicEnabled = progress.musicEnabled,
                        isVibrationEnabled = progress.vibrationEnabled,
                        isDarkMode = progress.darkMode,
                        difficulty = autoDifficulty
                    )
                }
                audioManager.equippedSoundTheme = soundTheme
                audioManager.isSoundEnabled = progress.soundEnabled
                audioManager.isMusicEnabled = progress.musicEnabled
                vibrationManager.isVibrationEnabled = progress.vibrationEnabled

                if (progress.musicEnabled) {
                    audioManager.startAmbientMusic()
                } else {
                    audioManager.stopAmbientMusic()
                }
            }
        }

        val defaultUnlockedThemes = setOf(
            "classic_glass", "crystal_tube",
            "deep_space", "midnight_cyber",
            "classic_water", "nature_haven"
        )

        viewModelScope.launch {
            // Guarantee second themes are unlocked in database
            repository.unlockTheme("crystal_tube", "BOTTLE")
            repository.unlockTheme("midnight_cyber", "BACKGROUND")
            repository.unlockTheme("nature_haven", "SOUND")

            repository.unlockedThemes.collect { themes ->
                val set = themes.map { it.themeId }.toSet() + defaultUnlockedThemes
                _uiState.update { it.copy(unlockedThemeIds = set) }
            }
        }

        viewModelScope.launch {
            repository.gameStats.collect { statsEntity ->
                _uiState.update { state ->
                    state.copy(
                        gameStats = GameStats(
                            totalLevelsCompleted = statsEntity.totalLevelsCompleted,
                            totalPoursMade = statsEntity.totalPoursMade,
                            totalUndosUsed = statsEntity.totalUndosUsed,
                            totalHintsUsed = statsEntity.totalHintsUsed,
                            dailyChallengeStreak = statsEntity.dailyChallengeStreak,
                            totalCoinsEarned = statsEntity.totalCoinsEarned,
                            easyLevelsCleared = statsEntity.easyLevelsCleared,
                            mediumLevelsCleared = statsEntity.mediumLevelsCleared,
                            hardLevelsCleared = statsEntity.hardLevelsCleared
                        )
                    )
                }
            }
        }

        loadCurrentLevel()
    }

    fun loadCurrentLevel(levelNumber: Int = _uiState.value.levelNumber, isDaily: Boolean = false) {
        val difficulty = Difficulty.forLevel(levelNumber)
        viewModelScope.launch {
            val savedState = repository.loadBoardState(levelNumber)
            val bottles = if (savedState != null) {
                savedState.first
            } else {
                LevelGenerator.generateLevel(levelNumber, difficulty, isDaily)
            }

            _uiState.update { state ->
                state.copy(
                    levelNumber = levelNumber,
                    difficulty = difficulty,
                    bottles = bottles,
                    historyStack = savedState?.second ?: emptyList(),
                    movesCount = savedState?.second?.size ?: 0,
                    poursCount = 0,
                    isVictoryDialogVisible = false,
                    hintMove = null,
                    isDailyChallenge = isDaily
                )
            }
        }
    }

    fun onBottleClick(index: Int) {
        if (_uiState.value.activePour != null) return // Ignore input during pour animation

        val currentBottles = _uiState.value.bottles
        if (index !in currentBottles.indices) return

        val selectedIndex = currentBottles.indexOfFirst { it.isSelected }

        if (selectedIndex == -1) {
            // No bottle selected -> select if not empty & not complete
            val target = currentBottles[index]
            if (!target.isEmpty && !target.isCompleted) {
                val updated = currentBottles.mapIndexed { i, b ->
                    if (i == index) b.copy(isSelected = true) else b.copy(isSelected = false)
                }
                _uiState.update { it.copy(bottles = updated) }
                audioManager.playTapSound()
                vibrationManager.vibrateTap()
            }
        } else if (selectedIndex == index) {
            // Deselect same bottle
            val updated = currentBottles.mapIndexed { i, b -> b.copy(isSelected = false) }
            _uiState.update { it.copy(bottles = updated) }
            audioManager.playTapSound()
        } else {
            // Attempt pour from selectedIndex to index
            val source = currentBottles[selectedIndex]
            val target = currentBottles[index]

            if (WaterSortEngine.canPour(source, target)) {
                val units = WaterSortEngine.getPourableUnits(source, target)
                val color = source.topColor ?: return
                val (newSource, newTarget) = WaterSortEngine.pour(source, target)

                // Set active pour animation state
                _uiState.update {
                    it.copy(
                        activePour = ActivePourAnimation(
                            sourceIndex = selectedIndex,
                            targetIndex = index,
                            color = color,
                            units = units
                        )
                    )
                }

                audioManager.playPourSound()
                vibrationManager.vibratePour()

                viewModelScope.launch {
                    kotlinx.coroutines.delay(850) // Smooth realistic pour trajectory and stream animation
                    executeCompletePour(selectedIndex, index, newSource, newTarget)
                }
            } else {
                // Invalid pour -> switch selection if clicked non-empty
                if (!target.isEmpty && !target.isCompleted) {
                    val updated = currentBottles.mapIndexed { i, b ->
                        if (i == index) b.copy(isSelected = true) else b.copy(isSelected = false)
                    }
                    _uiState.update { it.copy(bottles = updated) }
                    audioManager.playTapSound()
                } else {
                    val updated = currentBottles.mapIndexed { i, b -> b.copy(isSelected = false) }
                    _uiState.update { it.copy(bottles = updated) }
                }
            }
        }
    }

    private fun executeCompletePour(
        sourceIndex: Int,
        targetIndex: Int,
        newSource: Bottle,
        newTarget: Bottle
    ) {
        val currentBottles = _uiState.value.bottles
        val newHistory = _uiState.value.historyStack + listOf(currentBottles.map { it.copy(isSelected = false) })
        val updatedBottles = currentBottles.toMutableList().apply {
            set(sourceIndex, newSource)
            set(targetIndex, newTarget)
        }

        val isSolved = WaterSortEngine.isLevelSolved(updatedBottles)

        if (newTarget.isCompleted) {
            audioManager.playBottleCompleteSound()
        }

        _uiState.update { state ->
            state.copy(
                bottles = updatedBottles,
                historyStack = newHistory,
                movesCount = state.movesCount + 1,
                poursCount = state.poursCount + 1,
                hintMove = null,
                activePour = null,
                isVictoryDialogVisible = isSolved
            )
        }

        viewModelScope.launch {
            if (isSolved) {
                audioManager.playVictorySound()
                vibrationManager.vibrateSuccess()
                val coinsEarned = if (_uiState.value.isDailyChallenge) 100 else 10
                _uiState.update { it.copy(lastRewardCoins = coinsEarned) }
                repository.addCoins(coinsEarned)
                repository.recordLevelCompleted(
                    _uiState.value.difficulty.name,
                    _uiState.value.poursCount,
                    0,
                    0
                )
                repository.clearBoardState(_uiState.value.levelNumber)
            } else {
                repository.saveBoardState(
                    _uiState.value.levelNumber,
                    updatedBottles,
                    newHistory,
                    _uiState.value.movesCount + 1,
                    _uiState.value.isDailyChallenge
                )
            }
        }
    }

    fun undoMove() {
        val history = _uiState.value.historyStack
        if (history.isEmpty()) return

        val previousBoard = history.last()
        val newHistory = history.dropLast(1)

        _uiState.update { state ->
            state.copy(
                bottles = previousBoard,
                historyStack = newHistory,
                hintMove = null
            )
        }
        audioManager.playTapSound()

        viewModelScope.launch {
            repository.saveBoardState(
                _uiState.value.levelNumber,
                previousBoard,
                newHistory,
                _uiState.value.movesCount,
                _uiState.value.isDailyChallenge
            )
        }
    }

    fun restartLevel() {
        viewModelScope.launch {
            repository.clearBoardState(_uiState.value.levelNumber)
            loadCurrentLevel(_uiState.value.levelNumber, _uiState.value.isDailyChallenge)
        }
    }

    fun useHint() {
        if (_uiState.value.coins < 20) return
        val hint = WaterSortSolver.getHint(_uiState.value.bottles)
        if (hint != null) {
            viewModelScope.launch {
                repository.addCoins(-20)
            }
            _uiState.update { it.copy(hintMove = hint) }
            audioManager.playTapSound()
        }
    }

    fun addExtraBottle() {
        if (_uiState.value.coins < 50) return
        val currentBottles = _uiState.value.bottles
        val newBottle = Bottle(id = currentBottles.size, layers = emptyList(), isExtraBottle = true)
        val updated = currentBottles + newBottle

        viewModelScope.launch {
            repository.addCoins(-50)
            repository.saveBoardState(
                _uiState.value.levelNumber,
                updated,
                _uiState.value.historyStack,
                _uiState.value.movesCount,
                _uiState.value.isDailyChallenge
            )
        }

        _uiState.update { it.copy(bottles = updated) }
        audioManager.playTapSound()
    }

    fun skipLevel() {
        if (_uiState.value.coins < 100) return
        viewModelScope.launch {
            repository.addCoins(-100)
            nextLevel()
        }
    }

    fun nextLevel() {
        val nextLvl = _uiState.value.levelNumber + 1
        viewModelScope.launch {
            val progress = repository.userProgress.first()
            repository.saveProgress(progress.copy(currentLevel = maxOf(progress.currentLevel, nextLvl)))
        }
        loadCurrentLevel(nextLvl, false)
    }

    fun selectDifficulty(diff: Difficulty) {
        viewModelScope.launch {
            val progress = repository.userProgress.first()
            repository.saveProgress(progress.copy(difficulty = diff.name))
        }
        loadCurrentLevel(_uiState.value.levelNumber, _uiState.value.isDailyChallenge)
    }

    fun equipBottleTheme(theme: BottleTheme) {
        _uiState.update { it.copy(equippedBottleTheme = theme) }
        audioManager.playTapSound()
        vibrationManager.vibrateTap()
        viewModelScope.launch {
            val progress = repository.userProgress.first()
            repository.saveProgress(progress.copy(equippedBottleTheme = theme.id))
        }
    }

    fun equipBackgroundTheme(theme: BackgroundTheme) {
        _uiState.update { it.copy(equippedBackgroundTheme = theme) }
        audioManager.playTapSound()
        vibrationManager.vibrateTap()
        viewModelScope.launch {
            val progress = repository.userProgress.first()
            repository.saveProgress(progress.copy(equippedBackgroundTheme = theme.id))
        }
    }

    fun equipSoundTheme(theme: SoundTheme) {
        _uiState.update { it.copy(equippedSoundTheme = theme, previewingThemeId = null) }
        audioManager.equippedSoundTheme = theme
        audioManager.restartAmbientMusicForEquippedTheme()
        audioManager.playBottleCompleteSound(theme)
        vibrationManager.vibrateTap()
        viewModelScope.launch {
            val progress = repository.userProgress.first()
            repository.saveProgress(progress.copy(equippedSoundTheme = theme.id))
        }
    }

    fun previewSoundTheme(theme: SoundTheme) {
        if (_uiState.value.previewingThemeId == theme.id) {
            audioManager.stopPreview()
            _uiState.update { it.copy(previewingThemeId = null) }
            if (_uiState.value.isMusicEnabled) {
                audioManager.startAmbientMusic()
            }
        } else {
            _uiState.update { it.copy(previewingThemeId = theme.id) }
            audioManager.previewAmbientTheme(theme) {
                _uiState.update { state ->
                    if (state.previewingThemeId == theme.id) {
                        state.copy(previewingThemeId = null)
                    } else state
                }
            }
        }
    }

    fun claimHourlyBonus() {
        val bonusCoins = 50
        lastBonusClaimTime = System.currentTimeMillis()
        audioManager.playVictorySound()
        vibrationManager.vibrateSuccess()
        _uiState.update {
            it.copy(
                isBonusAvailable = false,
                bonusRemainingSeconds = 30 * 60L
            )
        }
        viewModelScope.launch {
            repository.addCoins(bonusCoins)
        }
    }

    fun buyTheme(themeId: String, themeType: String, cost: Int) {
        if (_uiState.value.coins < cost) return
        viewModelScope.launch {
            repository.addCoins(-cost)
            repository.unlockTheme(themeId, themeType)
            when (themeType) {
                "BOTTLE" -> equipBottleTheme(BottleTheme.fromId(themeId))
                "BACKGROUND" -> equipBackgroundTheme(BackgroundTheme.fromId(themeId))
                "SOUND" -> equipSoundTheme(SoundTheme.fromId(themeId))
            }
        }
    }

    fun claimDailyBonus() {
        val bonusCoins = (20..30).random()
        viewModelScope.launch {
            repository.addCoins(bonusCoins)
            // Occasionally unlock a bonus theme if available
            val bonusThemes = listOf("bubbling_soda", "sunset_gradient", "crystal_vial")
            val unlocked = _uiState.value.unlockedThemeIds
            val themeToUnlock = bonusThemes.firstOrNull { it !in unlocked }
            if (themeToUnlock != null) {
                val type = when (themeToUnlock) {
                    "bubbling_soda" -> "SOUND"
                    "crystal_vial" -> "BOTTLE"
                    else -> "BACKGROUND"
                }
                repository.unlockTheme(themeToUnlock, type)
            }
        }
    }

    fun toggleSound(enabled: Boolean) {
        _uiState.update { it.copy(isSoundEnabled = enabled) }
        audioManager.isSoundEnabled = enabled
        if (enabled) {
            audioManager.playTapSound()
        }
        viewModelScope.launch {
            val progress = repository.userProgress.first()
            repository.saveProgress(progress.copy(soundEnabled = enabled))
        }
    }

    fun toggleMusic(enabled: Boolean) {
        _uiState.update { it.copy(isMusicEnabled = enabled) }
        audioManager.isMusicEnabled = enabled
        if (enabled) {
            audioManager.startAmbientMusic()
        } else {
            audioManager.stopAmbientMusic()
        }
        viewModelScope.launch {
            val progress = repository.userProgress.first()
            repository.saveProgress(progress.copy(musicEnabled = enabled))
        }
    }

    fun toggleVibration(enabled: Boolean) {
        _uiState.update { it.copy(isVibrationEnabled = enabled) }
        vibrationManager.isVibrationEnabled = enabled
        if (enabled) {
            vibrationManager.vibrateTap()
        }
        viewModelScope.launch {
            val progress = repository.userProgress.first()
            repository.saveProgress(progress.copy(vibrationEnabled = enabled))
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
        viewModelScope.launch {
            val progress = repository.userProgress.first()
            repository.saveProgress(progress.copy(darkMode = enabled))
        }
    }
}
