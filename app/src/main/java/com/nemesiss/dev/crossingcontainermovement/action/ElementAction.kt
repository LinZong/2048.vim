package com.nemesiss.dev.crossingcontainermovement.action

import com.nemesiss.dev.crossingcontainermovement.GameBoardMap
import com.nemesiss.dev.crossingcontainermovement.view.GameBoardView


sealed interface ElementAction {

    fun apply(map: GameBoardMap)

    fun apply(view: GameBoardView)

    fun hasVisibilityChanges(): Boolean
}