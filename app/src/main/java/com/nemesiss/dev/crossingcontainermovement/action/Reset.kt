package com.nemesiss.dev.crossingcontainermovement.action

import com.nemesiss.dev.crossingcontainermovement.GameBoard
import com.nemesiss.dev.crossingcontainermovement.GameBoardMap

object Reset : ElementAction {

    override fun apply(view: GameBoardMap) {
        for(r in view.indices) {
            for(c in view[r].indices) {
                view[r][c] = GameBoard.Element.EMPTY
            }
        }
    }
}