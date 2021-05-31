package com.nemesiss.dev.crossingcontainermovement.action

import com.nemesiss.dev.crossingcontainermovement.GameBoard
import com.nemesiss.dev.crossingcontainermovement.GameBoardMap
import com.nemesiss.dev.crossingcontainermovement.model.Coord

data class Movement(
    val from: Coord,
    val to: Coord,
    var disappearOnEnd: Boolean = false,
    var bumpOnEnd: Boolean = false
) : ElementAction {
    override fun apply(view: GameBoardMap) {
        // normal movement actions.

        // do what animator do.
        val e = view[from.row][from.col]
        if (bumpOnEnd) {
            e.double()
        }
        if (from != to) {
            if (!disappearOnEnd) {
                view[to.row][to.col] = view[from.row][from.col]
            }
            view[from.row][from.col] = GameBoard.Element.EMPTY
        }
    }
}
