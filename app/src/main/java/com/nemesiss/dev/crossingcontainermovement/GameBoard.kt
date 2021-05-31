package com.nemesiss.dev.crossingcontainermovement

import android.util.Log
import com.nemesiss.dev.crossingcontainermovement.action.*
import com.nemesiss.dev.crossingcontainermovement.model.Coord
import com.nemesiss.dev.crossingcontainermovement.view.GameBoardView
import java.util.concurrent.ThreadLocalRandom


typealias GameBoardMap = Array<Array<GameBoard.Element>>

/**
 * Control flow:
 * GameBoardView receives gesture -> notify GameBoard with action -> GameBoard calculates movement sequences -> GameBoardView applies.
 */
class GameBoard(val bindingView: GameBoardView) {

    companion object {
        fun newViewOf(size: Int) = Array(size) { Array(size) { Element.EMPTY } }
    }

    enum class GestureDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    data class Element(var value: Int, var combineFrom: Coord? = null) {
        companion object {
            @JvmStatic
            val EMPTY = Element(-1)
        }

        fun double() {
            value *= 2
        }

        fun copy() = Element(value, combineFrom)
    }

    init {
        bindingView.relatedGameBoard = this
    }

    var disabled = false

    private val size = bindingView.size

    var view: GameBoardMap = newViewOf(size)
        private set

    fun set(element: Element, coord: Coord) {
        val (row, col) = coord
        view[row][col] = element
        bindingView.notifyActionsArrived(listOf(Appear(coord, element)))
    }

    fun get(coord: Coord): Element {
        val (row, col) = coord
        return view[row][col]
    }


    fun handleGesture(gestureDirection: GestureDirection): List<ElementAction> {
        if (disabled) return emptyList()
        if (checkDied()) {
            return handleDied()
        }
        if (!GameConfig.ComputeAction) {
            val mergeActions = when (gestureDirection) {
                GestureDirection.UP -> view.mergeBottomUp()
                GestureDirection.DOWN -> view.mergeTopDown()
                GestureDirection.LEFT -> view.mergeRTL()
                GestureDirection.RIGHT -> view.mergeLTR()
            }
            val fakeView = cloneView()
            val mergedView = cloneView()
            playActions(mergedView, mergeActions)

            // 此时newView携带了合并信息
            // 再调用handleAlignment生成最终的指令序列
            val alignmentActions = handleAlignment(mergedView, gestureDirection)
            playActions(mergedView, alignmentActions)

            val generateActions = randomlyGenerateNewElement(mergedView)
            playActions(mergedView, generateActions)

            val finalActions = alignmentActions + generateActions

            if (finalActions.isEmpty()) {
                handleDied()
                return emptyList()
            }

            bindingView.notifyActionsArrived(finalActions)
            playActions(fakeView, finalActions)
            view = cloneView(mergedView, true)

            if (GameConfig.DEBUG) {
                Log.d(
                    "GB",
                    " $gestureDirection \n" + stringifyGameBoardView(view)
                )
                var notTheSame = false
                for (r in 0 until size) {
                    for (c in 0 until size) {
                        if (view[r][c] != fakeView[r][c]) {
                            Log.e("GB", "view and fakeView are not the same! at $r $c")
                            notTheSame = true
                        }
                    }
                }
                if (notTheSame) {
                    Log.e("GB", "NotTheSame \n ${stringifyGameBoardView(fakeView)}")
                } else {
                    Log.d("GB", "GameBoardMap check passed!")
                }
            }
            return finalActions
        } else {
            val actions = computeActions(gestureDirection)
            val newView = cloneView()
            playActions(newView, actions)
            view = newView
            bindingView.notifyActionsArrived(actions)
            return actions
        }
    }

    private fun computeActions(direction: GestureDirection): List<ElementAction> {
        val newView = cloneView()
        val mergeActions = when (direction) {
            GestureDirection.UP -> newView.mergeBottomUp()
            GestureDirection.DOWN -> newView.mergeTopDown()
            GestureDirection.LEFT -> newView.mergeRTL()
            GestureDirection.RIGHT -> newView.mergeLTR()
        }
        playActions(newView, mergeActions)
        val alignment = handleAlignment(newView, direction)
        playActions(newView, alignment)
        val generate = randomlyGenerateNewElement(newView)
        return alignment + generate
    }

    fun setup() {
        val actions = randomlyGenerateNewElement(view)
        playActions(actions)
        bindingView.notifyActionsArrived(actions)
    }

    private fun handleDied(): List<ElementAction> {
        disabled = true
        // send died notification.
        val action = listOf(Died())
        bindingView.notifyActionsArrived(action)
        return action
    }

    private fun randomlyGenerateNewElement(gameBoardMap: GameBoardMap): List<Appear> {
        val freeCoords = arrayListOf<Coord>()
        val size = gameBoardMap.size
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (gameBoardMap[i][j] == Element.EMPTY) {
                    freeCoords += Coord(i, j)
                }
            }
        }
        Log.d("GB", "NewElementGenerator See: \n ${stringifyGameBoardView(gameBoardMap)}")
        return Array(1) { freeCoords.randomOrNull() }
            .filterNotNull()
            .map { c -> Appear(c, Element(random2Or4())) }
    }


    private fun handleAlignment(view: GameBoardMap, direction: GestureDirection): List<ElementAction> {
        return when (direction) {
            GestureDirection.UP -> view.alignmentBottomUp()
            GestureDirection.DOWN -> view.alignmentTopDown()
            GestureDirection.LEFT -> view.alignmentRTL()
            GestureDirection.RIGHT -> view.alignmentLTR()
        }
    }


    private fun checkDied(): Boolean {
        val directions = GestureDirection.values()
        for (d in directions) {
            val actions = computeActions(d).filter { action ->
                when (action) {
                    is Appear -> true
                    is Combination -> false
                    is Died -> false
                    is Movement -> action.from != action.to
                }
            }
            if (actions.isNotEmpty()) return false
        }
        return true
    }


    private fun playActions(actions: List<ElementAction>) {
        playActions(view, actions)
    }

    private fun playActions(v: GameBoardMap, actions: List<ElementAction>) {
        for (action in actions) {
            action.apply(v)
        }
    }

    // ============= Action Generators =============

    private fun GameBoardMap.alignmentLTR(): List<ElementAction> {
        val sequences = arrayListOf<ElementAction>()
        for (row in 0 until size) {
            var alignCol = size - 1
            var col = size - 1
            while (col >= 0) {
                val e = this[row][col]
                if (e != Element.EMPTY) {
                    val combineFrom = e.combineFrom
                    if (combineFrom != null) {
                        sequences += Movement(Coord(row, col), Coord(row, alignCol), bumpOnEnd = true)
                        sequences += Movement(
                            Coord(combineFrom.row, combineFrom.col),
                            Coord(row, alignCol),
                            disappearOnEnd = true
                        )
                    } else {
                        sequences += Movement(Coord(row, col), Coord(row, alignCol))
                    }
                    alignCol--
                }
                col--
            }
        }
        return sequences
    }

    private fun GameBoardMap.alignmentRTL(): List<ElementAction> {
        val sequences = arrayListOf<ElementAction>()
        for (row in 0 until size) {
            var alignCol = 0
            var col = 0
            while (col < size) {
                val e = this[row][col]
                if (e != Element.EMPTY) {
                    val combineFrom = e.combineFrom
                    if (combineFrom != null) {
                        sequences += Movement(Coord(row, col), Coord(row, alignCol), bumpOnEnd = true)
                        sequences += Movement(
                            Coord(combineFrom.row, combineFrom.col),
                            Coord(row, alignCol),
                            disappearOnEnd = true
                        )
                    } else {
                        sequences += Movement(Coord(row, col), Coord(row, alignCol))
                    }
                    alignCol++
                }
                col++
            }
        }
        return sequences
    }

    private fun GameBoardMap.alignmentTopDown(): List<ElementAction> {
        val sequences = arrayListOf<ElementAction>()
        for (col in 0 until size) {
            var alignRow = size - 1
            var row = size - 1
            while (row >= 0) {
                val e = this[row][col]
                if (e != Element.EMPTY) {
                    val combineFrom = e.combineFrom
                    if (combineFrom != null) {
                        sequences += Movement(Coord(row, col), Coord(alignRow, col), bumpOnEnd = true)
                        sequences += Movement(
                            Coord(combineFrom.row, combineFrom.col),
                            Coord(alignRow, col),
                            disappearOnEnd = true
                        )
                    } else {
                        sequences += Movement(Coord(row, col), Coord(alignRow, col))
                    }
                    alignRow--
                }
                row--
            }
        }
        return sequences
    }

    private fun GameBoardMap.alignmentBottomUp(): List<ElementAction> {
        val sequences = arrayListOf<ElementAction>()
        for (col in 0 until size) {
            var alignRow = 0
            var row = 0
            while (row < size) {
                val e = this[row][col]
                if (e != Element.EMPTY) {
                    val combineFrom = e.combineFrom
                    if (combineFrom != null) {
                        sequences += Movement(Coord(row, col), Coord(alignRow, col), bumpOnEnd = true)
                        sequences += Movement(
                            Coord(combineFrom.row, combineFrom.col),
                            Coord(alignRow, col),
                            disappearOnEnd = true
                        )
                    } else {
                        sequences += Movement(Coord(row, col), Coord(alignRow, col))
                    }
                    alignRow++
                }
                row++
            }
        }
        return sequences
    }

    private fun GameBoardMap.mergeLTR(): List<ElementAction> {
        val sequences = arrayListOf<ElementAction>()
        /**
         * 1. 从行尾起遍历整行。对于每一个元素。
         * 1.1 向前遍历看是否存在可以与之合并的元素。如有，执行合并。
         */
        for (row in 0 until size) {
            var col = size - 1
            while (col >= 0) {
                val curr = this[row][col]
                // 跳过空格子。
                if (curr != Element.EMPTY) {
                    // 此时curr是有数字的元素。
                    // 寻找这个元素左边第一个可以合并的结果。
                    val combineCoord = curr.firstLeftCombinable(Coord(row, col))
                    if (combineCoord != Coord.NO_COORD) {
                        // 找到了可以合并的元素的坐标。
                        sequences += Combination(combineCoord, Coord(row, col))
                        col = combineCoord.col
                    }
                }
                col--
            }
        }
        return sequences
    }

    private fun GameBoardMap.mergeRTL(): List<ElementAction> {
        val sequences = arrayListOf<ElementAction>()
        /**
         * 1. 从行头起遍历整行。对于每一个元素。
         * 1.1 向后遍历看是否存在可以与之合并的元素。如有，执行合并。
         */
        for (row in 0 until size) {
            var col = 0
            while (col < size) {
                val curr = this[row][col]
                // 跳过空格子。
                if (curr != Element.EMPTY) {
                    // 此时curr是有数字的元素。
                    // 寻找这个元素左边第一个可以合并的结果。
                    val combineCoord = curr.firstRightCombinable(Coord(row, col))
                    if (combineCoord != Coord.NO_COORD) {
                        // 找到了可以合并的元素的坐标。
                        sequences += Combination(combineCoord, Coord(row, col))
                        col = combineCoord.col
                    }
                }
                col++
            }
        }
        return sequences
    }

    private fun GameBoardMap.mergeTopDown(): List<ElementAction> {
        val sequences = arrayListOf<ElementAction>()
        /**
         * 1. 从列尾起遍历整行。对于每一个元素。
         * 1.1 向前遍历看是否存在可以与之合并的元素。如有，执行合并。
         */
        for (col in 0 until size) {
            var row = size - 1
            while (row >= 0) {
                val curr = this[row][col]
                if (curr != Element.EMPTY) {
                    // 此时curr是有效数字
                    val combineCoord = curr.firstTopCombinable(Coord(row, col))
                    if (combineCoord != Coord.NO_COORD) {
                        sequences += Combination(combineCoord, Coord(row, col))
                        row = combineCoord.row
                    }
                }
                row--
            }
        }
        return sequences
    }

    private fun GameBoardMap.mergeBottomUp(): List<ElementAction> {
        val sequences = arrayListOf<ElementAction>()
        /**
         * 1. 从列头起遍历整行。对于每一个元素。
         * 1.1 向后遍历看是否存在可以与之合并的元素。如有，执行合并。
         */
        for (col in 0 until size) {
            var row = 0
            while (row < size) {
                val curr = this[row][col]
                if (curr != Element.EMPTY) {
                    val combineCoord = curr.firstBottomCombinable(Coord(row, col))
                    if (combineCoord != Coord.NO_COORD) {
                        sequences += Combination(combineCoord, Coord(row, col))
                        row = combineCoord.row
                    }
                }
                row++
            }
        }
        return sequences
    }

    private fun Element.firstLeftCombinable(coord: Coord): Coord {
        val row = coord.row
        for (col in coord.col - 1 downTo 0) {
            val currElement = view[row][col]
            if (currElement == Element.EMPTY) continue
            return if (this canCombine currElement) Coord(row, col) else Coord.NO_COORD
        }
        return Coord.NO_COORD
    }

    private fun Element.firstRightCombinable(coord: Coord): Coord {
        val row = coord.row
        for (col in coord.col + 1 until size) {
            val currElement = view[row][col]
            if (currElement == Element.EMPTY) continue
            return if (this canCombine currElement) Coord(row, col) else Coord.NO_COORD
        }
        return Coord.NO_COORD
    }

    private fun Element.firstTopCombinable(coord: Coord): Coord {
        val col = coord.col
        for (row in coord.row - 1 downTo 0) {
            val currElement = view[row][col]
            if (currElement == Element.EMPTY) continue
            return if (this canCombine currElement) Coord(row, col) else Coord.NO_COORD
        }
        return Coord.NO_COORD
    }

    private fun Element.firstBottomCombinable(coord: Coord): Coord {
        val col = coord.col
        for (row in coord.row + 1 until size) {
            val currElement = view[row][col]
            if (currElement == Element.EMPTY) continue
            return if (this canCombine currElement) Coord(row, col) else Coord.NO_COORD
        }
        return Coord.NO_COORD
    }

    private infix fun Element.canCombine(other: Element): Boolean {
        return this != Element.EMPTY && other != Element.EMPTY && value == other.value
    }

    // ============= Helpers =============

    private fun random1Or2(): Int {
        val tr = ThreadLocalRandom.current()
        return tr.nextInt(1, 3)
    }

    private fun random2Or4(): Int {
        return random1Or2() * 2
    }

    private fun cloneView(cleanCombine: Boolean = false): GameBoardMap {
        return cloneView(view, cleanCombine)
    }

    private fun cloneView(original: GameBoardMap, cleanCombine: Boolean = false): GameBoardMap {
        val newView = newViewOf(size)
        for (r in 0 until size) {
            for (c in 0 until size) {
                newView[r][c] = original[r][c].copy()
                if (cleanCombine) {
                    newView[r][c].combineFrom = null
                }
            }
        }
        return newView
    }

    private fun stringifyGameBoardView(view: GameBoardMap): String {
        val sb = StringBuilder()
        for (r in 0 until size) {
            for (c in 0 until size) {
                sb.append(view[r][c].value)
                sb.append(" ")
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}