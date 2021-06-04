package com.nemesiss.dev.vim2048.action

import com.nemesiss.dev.vim2048.GameBoard
import com.nemesiss.dev.vim2048.model.GameBoardMap
import com.nemesiss.dev.vim2048.view.GameBoardView

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