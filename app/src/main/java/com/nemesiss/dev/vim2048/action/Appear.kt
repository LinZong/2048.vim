package com.nemesiss.dev.vim2048.action

import com.nemesiss.dev.vim2048.GameBoard
import com.nemesiss.dev.vim2048.model.Coord
import com.nemesiss.dev.vim2048.model.GameBoardMap
import com.nemesiss.dev.vim2048.view.GameBoardView

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