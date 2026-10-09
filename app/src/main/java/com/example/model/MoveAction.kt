package com.example.model

/**
 * Record of a move between bolts for Undo support.
 * Supports multiple consecutive matching nuts moved in a single swift action.
 */
data class MoveAction(
    val fromBoltId: Int,
    val toBoltId: Int,
    val movedNuts: List<Nut>,
    val uncoveredNutId: Long? = null
)
