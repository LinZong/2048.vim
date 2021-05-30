package com.nemesiss.dev.crossingcontainermovement.action

import com.nemesiss.dev.crossingcontainermovement.GameBoardMap

interface ElementAction {
    fun apply(view: GameBoardMap)
}