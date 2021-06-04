package com.nemesiss.dev.vim2048.action

import com.nemesiss.dev.vim2048.GameBoard
import com.nemesiss.dev.vim2048.model.Coord
import com.nemesiss.dev.vim2048.model.GameBoardMap
import com.nemesiss.dev.vim2048.view.GameBoardView

data class Combination(val from: Coord, val to: Coord) : ElementAction {
    override fun apply(map: GameBoardMap) {
        map[from.row][from.col] = GameBoard.Element.EMPTY
        map[to.row][to.col].combineFrom = from
    }

    override fun apply(view: GameBoardView) {
        // no op
    }

    override fun hasVisibilityChanges(): Boolean {
        return false
    }
}