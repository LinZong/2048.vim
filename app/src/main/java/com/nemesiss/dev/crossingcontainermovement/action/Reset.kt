package com.nemesiss.dev.crossingcontainermovement.action

import com.nemesiss.dev.crossingcontainermovement.GameBoard
import com.nemesiss.dev.crossingcontainermovement.model.GameBoardMap
import com.nemesiss.dev.crossingcontainermovement.view.GameBoardView

object Reset : ElementAction {

    override fun apply(map: GameBoardMap) {
        for (r in map.indices) {
            // construct a new Array instead of filling each item with EMPTY.
            map[r] = Array(map[r].size) { GameBoard.Element.EMPTY }
        }
    }

    override fun apply(view: GameBoardView) {
        view.doReset()
    }

    override fun hasVisibilityChanges(): Boolean {
        return true
    }
}