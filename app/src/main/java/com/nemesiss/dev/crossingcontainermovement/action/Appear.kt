package com.nemesiss.dev.crossingcontainermovement.action

import com.nemesiss.dev.crossingcontainermovement.GameBoard
import com.nemesiss.dev.crossingcontainermovement.GameBoardMap
import com.nemesiss.dev.crossingcontainermovement.model.Coord

data class Appear(val coord: Coord, val element: GameBoard.Element): ElementAction {
    override fun apply(view: GameBoardMap) {
        val (r, c) = coord
        view[r][c] = element.copy()
    }
}