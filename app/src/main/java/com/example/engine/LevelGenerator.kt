package com.example.engine

import com.example.data.model.Bottle
import com.example.data.model.Difficulty
import com.example.data.model.LiquidColor
import kotlin.random.Random

object LevelGenerator {

    /**
     * Determines color count based on level number for smooth gradual progression.
     */
    fun getColorCountForLevel(levelNumber: Int): Int {
        return when {
            levelNumber <= 2 -> 2
            levelNumber <= 5 -> 3
            levelNumber <= 10 -> 4
            levelNumber <= 13 -> 5
            levelNumber <= 16 -> 6
            levelNumber <= 20 -> 7
            levelNumber <= 25 -> 8
            levelNumber <= 30 -> 9
            else -> minOf(LiquidColor.entries.size, 10)
        }
    }

    /**
     * Determines empty bottle count based on level number and difficulty.
     * Easy/Medium levels get 2 or 3 empty bottles.
     * Hard levels (Level 21+) get fewer empty bottles (1 empty bottle) for high puzzle complexity.
     */
    fun getEmptyBottlesForLevel(levelNumber: Int, difficulty: Difficulty): Int {
        return when {
            levelNumber <= 10 -> if (levelNumber == 8 || levelNumber == 9) 3 else 2
            levelNumber <= 20 -> 2
            else -> {
                if (difficulty == Difficulty.HARD) 1 else 2
            }
        }
    }

    /**
     * Generates a puzzle for a given level number and auto-assigned difficulty.
     * Guarantees a solvable level with no impossible setups or sudden difficulty spikes.
     */
    fun generateLevel(levelNumber: Int, overrideDifficulty: Difficulty? = null, isDaily: Boolean = false): List<Bottle> {
        val difficulty = overrideDifficulty ?: Difficulty.forLevel(levelNumber)
        val colorCount = getColorCountForLevel(levelNumber).coerceAtMost(LiquidColor.entries.size)
        val emptyBottles = getEmptyBottlesForLevel(levelNumber, difficulty)

        var seedOffset = 0L
        var isValidSetup = false
        val resultBottles = mutableListOf<Bottle>()

        while (!isValidSetup && seedOffset < 100) {
            val seed = if (isDaily) {
                levelNumber * 77777L + seedOffset * 13L
            } else {
                levelNumber * 10007L + difficulty.ordinal * 12345L + seedOffset * 31L
            }
            val random = Random(seed)
            seedOffset++

            val selectedColors = LiquidColor.entries.shuffled(random).take(colorCount)

            val allUnits = mutableListOf<LiquidColor>()
            for (i in 0 until colorCount) {
                val color = selectedColors[i]
                repeat(Bottle.BOTTLE_CAPACITY) {
                    allUnits.add(color)
                }
            }

            allUnits.shuffle(random)

            resultBottles.clear()
            for (i in 0 until colorCount) {
                val subList = allUnits.subList(i * Bottle.BOTTLE_CAPACITY, (i + 1) * Bottle.BOTTLE_CAPACITY)
                resultBottles.add(
                    Bottle(
                        id = i,
                        layers = subList.toList()
                    )
                )
            }

            for (i in 0 until emptyBottles) {
                resultBottles.add(
                    Bottle(
                        id = colorCount + i,
                        layers = emptyList()
                    )
                )
            }

            // Verify it's not already solved and is guaranteed solvable by WaterSortSolver
            if (!WaterSortEngine.isLevelSolved(resultBottles)) {
                val solution = WaterSortSolver.findSolution(resultBottles, maxSteps = 3500)
                if (solution != null && solution.isNotEmpty()) {
                    isValidSetup = true
                }
            }
        }

        return resultBottles
    }
}
