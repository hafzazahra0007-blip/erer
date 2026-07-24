package com.example.data.model

/**
 * Represents a glass bottle in the Water Sort puzzle.
 * @param id Unique bottle index
 * @param layers Stack of liquid colors from bottom (index 0) to top (last element)
 * @param capacity Maximum liquid units (default 4)
 * @param isSelected True if currently tapped/highlighted for pouring
 * @param isExtraBottle True if added via Extra Bottle power-up
 */
data class Bottle(
    val id: Int,
    val layers: List<LiquidColor> = emptyList(),
    val capacity: Int = BOTTLE_CAPACITY,
    val isSelected: Boolean = false,
    val isExtraBottle: Boolean = false
) {
    val isFull: Boolean get() = layers.size >= capacity
    val isEmpty: Boolean get() = layers.isEmpty()
    val availableSpace: Int get() = capacity - layers.size

    val topColor: LiquidColor? get() = layers.lastOrNull()

    /**
     * Number of continuous matching color units at the top of the bottle.
     */
    val topColorCount: Int
        get() {
            if (layers.isEmpty()) return 0
            val color = layers.last()
            var count = 0
            for (i in layers.indices.reversed()) {
                if (layers[i] == color) count++ else break
            }
            return count
        }

    /**
     * Checks if the bottle is completely filled with a single color.
     */
    val isCompleted: Boolean
        get() = isFull && layers.all { it == layers.first() }

    companion object {
        const val BOTTLE_CAPACITY = 4
    }
}
