package com.example.engine

import com.example.data.model.Bottle
import java.util.ArrayDeque

object WaterSortSolver {

    data class Move(val fromIndex: Int, val toIndex: Int)

    private data class StateNode(
        val bottles: List<Bottle>,
        val moves: List<Move>
    )

    /**
     * Finds full solution move sequence if puzzle is solvable.
     * Returns list of moves, or null if no solution found within maxSteps.
     */
    fun findSolution(initialBottles: List<Bottle>, maxSteps: Int = 3000): List<Move>? {
        if (WaterSortEngine.isLevelSolved(initialBottles)) return emptyList()

        val queue = ArrayDeque<StateNode>()
        val visited = mutableSetOf<String>()

        val initialState = StateNode(initialBottles, emptyList())
        queue.add(initialState)
        visited.add(serializeBottles(initialBottles))

        var steps = maxSteps
        while (queue.isNotEmpty() && steps > 0) {
            steps--
            val current = queue.poll() ?: break

            if (WaterSortEngine.isLevelSolved(current.bottles)) {
                return current.moves
            }

            for (from in current.bottles.indices) {
                for (to in current.bottles.indices) {
                    if (from == to) continue
                    val source = current.bottles[from]
                    val target = current.bottles[to]

                    if (WaterSortEngine.canPour(source, target)) {
                        val (newSource, newTarget) = WaterSortEngine.pour(source, target)
                        val nextBottles = current.bottles.toMutableList().apply {
                            set(from, newSource)
                            set(to, newTarget)
                        }

                        val key = serializeBottles(nextBottles)
                        if (!visited.contains(key)) {
                            visited.add(key)
                            val nextMoves = current.moves + Move(from, to)
                            if (WaterSortEngine.isLevelSolved(nextBottles)) {
                                return nextMoves
                            }
                            queue.add(StateNode(nextBottles, nextMoves))
                        }
                    }
                }
            }
        }
        return null
    }

    /**
     * Finds the shortest move sequence to solve the puzzle from [currentBottles].
     * Returns the first move to recommend to the user, or null if no valid move found.
     */
    fun getHint(currentBottles: List<Bottle>): Move? {
        if (WaterSortEngine.isLevelSolved(currentBottles)) return null

        val queue = ArrayDeque<StateNode>()
        val visited = mutableSetOf<String>()

        val initialState = StateNode(currentBottles, emptyList())
        queue.add(initialState)
        visited.add(serializeBottles(currentBottles))

        var maxSearchSteps = 3000
        while (queue.isNotEmpty() && maxSearchSteps > 0) {
            maxSearchSteps--
            val current = queue.poll() ?: break

            if (WaterSortEngine.isLevelSolved(current.bottles)) {
                return current.moves.firstOrNull()
            }

            // Generate possible moves
            for (from in current.bottles.indices) {
                for (to in current.bottles.indices) {
                    if (from == to) continue
                    val source = current.bottles[from]
                    val target = current.bottles[to]

                    if (WaterSortEngine.canPour(source, target)) {
                        val (newSource, newTarget) = WaterSortEngine.pour(source, target)
                        val nextBottles = current.bottles.toMutableList().apply {
                            set(from, newSource)
                            set(to, newTarget)
                        }

                        val key = serializeBottles(nextBottles)
                        if (!visited.contains(key)) {
                            visited.add(key)
                            val nextMoves = current.moves + Move(from, to)
                            if (WaterSortEngine.isLevelSolved(nextBottles)) {
                                return nextMoves.firstOrNull()
                            }
                            queue.add(StateNode(nextBottles, nextMoves))
                        }
                    }
                }
            }
        }

        // Fallback: Return any immediately valid pour move
        for (from in currentBottles.indices) {
            for (to in currentBottles.indices) {
                if (from == to) continue
                if (WaterSortEngine.canPour(currentBottles[from], currentBottles[to])) {
                    return Move(from, to)
                }
            }
        }

        return null
    }

    private fun serializeBottles(bottles: List<Bottle>): String {
        return bottles.joinToString(";") { bottle ->
            bottle.layers.joinToString(",") { it.id.toString() }
        }
    }
}
