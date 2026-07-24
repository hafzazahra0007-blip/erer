package com.example.engine

import com.example.data.model.Bottle
import com.example.data.model.LiquidColor

object WaterSortEngine {

    /**
     * Checks if liquid can be poured from [source] bottle into [target] bottle.
     */
    fun canPour(source: Bottle, target: Bottle): Boolean {
        if (source.id == target.id) return false
        if (source.isEmpty) return false
        if (target.isFull) return false

        // If target is empty, we can pour
        if (target.isEmpty) return true

        // Otherwise target top color must match source top color
        return source.topColor == target.topColor
    }

    /**
     * Calculates maximum liquid units that can be transferred from [source] to [target].
     */
    fun getPourableUnits(source: Bottle, target: Bottle): Int {
        if (!canPour(source, target)) return 0
        val colorToPour = source.topColor ?: return 0
        val sourceContiguous = source.topColorCount
        val targetSpace = target.availableSpace
        return minOf(sourceContiguous, targetSpace)
    }

    /**
     * Performs pour operation and returns updated Pair(newSource, newTarget).
     */
    fun pour(source: Bottle, target: Bottle): Pair<Bottle, Bottle> {
        val units = getPourableUnits(source, target)
        if (units <= 0) return Pair(source, target)

        val color = source.topColor ?: return Pair(source, target)

        // Remove [units] from source top
        val newSourceLayers = source.layers.dropLast(units)
        val newSource = source.copy(layers = newSourceLayers, isSelected = false)

        // Add [units] of [color] to target top
        val newTargetLayers = target.layers + List(units) { color }
        val newTarget = target.copy(layers = newTargetLayers, isSelected = false)

        return Pair(newSource, newTarget)
    }

    /**
     * Checks if the puzzle is completely solved.
     * All non-empty bottles must be full with 4 units of identical color.
     */
    fun isLevelSolved(bottles: List<Bottle>): Boolean {
        return bottles.all { bottle ->
            bottle.isEmpty || bottle.isCompleted
        }
    }
}
