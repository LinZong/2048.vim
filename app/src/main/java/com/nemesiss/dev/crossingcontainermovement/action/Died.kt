package com.nemesiss.dev.crossingcontainermovement.action

import com.nemesiss.dev.crossingcontainermovement.model.GameBoardMap
import com.nemesiss.dev.crossingcontainermovement.view.GameBoardView

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