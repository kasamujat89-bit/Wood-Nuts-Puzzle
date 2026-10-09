package com.example.model

/**
 * Represents a single hexagonal nut sitting on a bolt.
 */
data class Nut(
    val id: Long,
    val color: NutColor,
    val isMystery: Boolean = false,
    val isRevealed: Boolean = false
) {
    /**
     * Whether the true color of this nut is currently visible to the player.
     */
    val isVisible: Boolean
        get() = !isMystery || isRevealed
}
