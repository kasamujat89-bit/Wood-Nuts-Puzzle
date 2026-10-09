package com.example.model

/**
 * Represents a threaded screw rod that holds nuts.
 * [nuts] are ordered from bottom (index 0) to top (last index).
 */
data class Bolt(
    val id: Int,
    val capacity: Int = 4,
    val nuts: List<Nut> = emptyList(),
    val isExtra: Boolean = false
) {
    val isEmpty: Boolean
        get() = nuts.isEmpty()

    val isFull: Boolean
        get() = nuts.size >= capacity

    val availableSpace: Int
        get() = (capacity - nuts.size).coerceAtLeast(0)

    val topNut: Nut?
        get() = nuts.lastOrNull()

    val topColor: NutColor?
        get() = topNut?.color

    /**
     * A bolt is complete when it is filled to capacity with nuts of identical visible color.
     */
    val isCompleted: Boolean
        get() = nuts.size == capacity && nuts.isNotEmpty() && nuts.all {
            it.color == nuts[0].color && it.isVisible
        }

    fun canAccept(nut: Nut): Boolean {
        if (isFull) return false
        if (isEmpty) return true
        return topColor == nut.color
    }
}
