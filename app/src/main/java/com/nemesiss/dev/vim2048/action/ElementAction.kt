package com.nemesiss.dev.vim2048.action

import com.nemesiss.dev.vim2048.model.GameBoardMap
import com.nemesiss.dev.vim2048.view.GameBoardView


sealed interface ElementAction {

    fun apply(map: GameBoardMap)

    fun apply(view: GameBoardView)

    fun hasVisibilityChanges(): Boolean
}