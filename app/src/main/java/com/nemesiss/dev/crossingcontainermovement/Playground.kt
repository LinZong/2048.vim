package com.nemesiss.dev.crossingcontainermovement

import com.nemesiss.dev.crossingcontainermovement.model.Coord

fun main() {
    val gameBoard = GameBoard(4)
    gameBoard.set(GameBoard.Element(2), Coord(1, 1))
    gameBoard.set(GameBoard.Element(2), Coord(1, 2))
    println(" --------- ")
    println(gameBoard.stringifyGameBoardView(gameBoard.view))
    println(" --------- ")
    val gesture = gameBoard.handleGesture(GameBoard.GestureDirection.RIGHT)
    println(" --------- ")
    println(gesture)
}