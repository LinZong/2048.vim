package com.nemesiss.dev.vim2048.action

import com.nemesiss.dev.vim2048.model.GameBoardMap
import com.nemesiss.dev.vim2048.view.GameBoardView

object Died : ElementAction {
    override fun apply(map: GameBoardMap) {
        // we are died. no op.
    }

    override fun apply(view: GameBoardView) {
        view.doDied()
    }

    override fun hasVisibilityChanges(): Boolean {
        return true
    }
}