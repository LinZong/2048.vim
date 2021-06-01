package com.nemesiss.dev.crossingcontainermovement.action

import com.nemesiss.dev.crossingcontainermovement.GameBoard
import com.nemesiss.dev.crossingcontainermovement.GameBoardMap
import com.nemesiss.dev.crossingcontainermovement.model.Coord
import com.nemesiss.dev.crossingcontainermovement.view.GameBoardView

data class Appear(val coord: Coord, val element: GameBoard.Element) : ElementAction {
    override fun apply(map: GameBoardMap) {
        val (r, c) = coord
        map[r][c] = element.copy()
    }

    override fun apply(view: GameBoardView) {
        view.doAppear(this)
    }

    override fun hasVisibilityChanges(): Boolean {
        return true
    }
}