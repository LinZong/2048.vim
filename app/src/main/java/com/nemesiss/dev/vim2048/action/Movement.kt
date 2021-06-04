package com.nemesiss.dev.vim2048.action

import com.nemesiss.dev.vim2048.GameBoard
import com.nemesiss.dev.vim2048.model.Coord
import com.nemesiss.dev.vim2048.model.GameBoardMap
import com.nemesiss.dev.vim2048.view.GameBoardView

data class Movement(
    val from: Coord,
    val to: Coord,
    var disappearOnEnd: Boolean = false,
    var bumpOnEnd: Boolean = false
) : ElementAction {
    override fun apply(map: GameBoardMap) {
        // normal movement actions.

        // do what animator do.

        if (from != to) {
            if (!disappearOnEnd) {
                map[to.row][to.col] = map[from.row][from.col]
            }
            map[from.row][from.col] = GameBoard.Element.EMPTY
        }

        if (bumpOnEnd) {
            map[to.row][to.col].double()
        }
    }

    override fun apply(view: GameBoardView) {
        view.doMovement(this)
    }

    override fun hasVisibilityChanges(): Boolean {
        return true
    }
}
