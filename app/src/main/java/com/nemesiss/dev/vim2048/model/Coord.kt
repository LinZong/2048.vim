package com.nemesiss.dev.vim2048.model

data class Coord(val row: Int, val col: Int) {
    companion object {
        @JvmStatic
        val NO_COORD = Coord(-1, -1)
    }
}
