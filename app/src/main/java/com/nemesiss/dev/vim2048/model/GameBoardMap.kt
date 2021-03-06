package com.nemesiss.dev.vim2048.model

import android.os.Parcel
import android.os.Parcelable
import com.nemesiss.dev.vim2048.GameBoard

class GameBoardMap() : Parcelable {

    var size: Int = 0
        private set

    lateinit var map: Array<Array<GameBoard.Element>>
        private set

    constructor(size: Int) : this() {
        this.size = size
        map = Array(size) { Array(size) { GameBoard.Element.EMPTY } }
    }

    constructor(parcel: Parcel) : this() {
        this.size = parcel.readInt()
        // here cannot use [Element.EMPTY], it will cause all elements are referenced to one object.
        map = Array(size) { Array(size) { GameBoard.Element(0) } }
        for (row in map) {
            for (col in row) {
                col.value = parcel.readInt()
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(size)
        for (row in map) {
            for (col in row) {
                parcel.writeInt(col.value)
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GameBoardMap> {
        override fun createFromParcel(parcel: Parcel): GameBoardMap {
            return GameBoardMap(parcel)
        }

        override fun newArray(size: Int): Array<GameBoardMap?> {
            return arrayOfNulls(size)
        }
    }

    operator fun get(index: Int): Array<GameBoard.Element> = map[index]

    operator fun set(index: Int, value: Array<GameBoard.Element>) {
        map[index] = value
    }

    val indices get() = map.indices
}