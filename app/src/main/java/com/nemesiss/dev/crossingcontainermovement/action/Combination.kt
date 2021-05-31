package com.nemesiss.dev.crossingcontainermovement.action

import com.nemesiss.dev.crossingcontainermovement.GameBoard
import com.nemesiss.dev.crossingcontainermovement.GameBoardMap
import com.nemesiss.dev.crossingcontainermovement.model.Coord

data class Combination(val from: Coord, val to: Coord): ElementAction {
    override fun apply(view: GameBoardMap) {
        view[from.row][from.col] = GameBoard.Element.EMPTY
        view[to.row][to.col].combineFrom = from
    }
}