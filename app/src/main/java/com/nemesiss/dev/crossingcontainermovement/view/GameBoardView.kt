package com.nemesiss.dev.crossingcontainermovement.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.core.view.setMargins
import com.nemesiss.dev.crossingcontainermovement.GameBoard
import com.nemesiss.dev.crossingcontainermovement.GameConfig
import com.nemesiss.dev.crossingcontainermovement.R
import com.nemesiss.dev.crossingcontainermovement.action.Appear
import com.nemesiss.dev.crossingcontainermovement.action.ElementAction
import com.nemesiss.dev.crossingcontainermovement.action.Movement
import com.nemesiss.dev.crossingcontainermovement.model.Coord
import com.nemesiss.dev.crossingcontainermovement.util.dp2Px
import com.nemesiss.dev.crossingcontainermovement.view.animator.AnimatorTracker
import com.nemesiss.dev.crossingcontainermovement.view.animator.BumpAppearAnimator
import com.nemesiss.dev.crossingcontainermovement.view.animator.BumpCombinationAnimator
import com.nemesiss.dev.crossingcontainermovement.view.animator.ReparentAnimator
import kotlin.math.abs

class GameBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private inner class GestureHandler : GestureDetector.SimpleOnGestureListener() {
        private val TAG = "GestureHandler"
        private val scrollThreshold = 150
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {

            return true
        }
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (e1 == null || e2 == null) return true
            if (animatorTracker.runningAnimator > 0) return true

            val deltaY = e2.rawY - e1.rawY
            if (abs(deltaY) >= scrollThreshold) {
                if (deltaY > 0) {
                    relatedGameBoard.handleGesture(GameBoard.GestureDirection.DOWN)
                } else {
                    relatedGameBoard.handleGesture(GameBoard.GestureDirection.UP)
                }
                return true
            }

            val deltaX = e2.rawX - e1.rawX
            if (abs(deltaX) >= scrollThreshold) {
                if (deltaX > 0) {
                    relatedGameBoard.handleGesture(GameBoard.GestureDirection.RIGHT)
                } else {
                    relatedGameBoard.handleGesture(GameBoard.GestureDirection.LEFT)
                }
                return true
            }
            return true
        }
    }

    private val gestureDetector = GestureDetector(context, GestureHandler())

    private val animatorTracker = AnimatorTracker()

    private lateinit var decorView: ViewGroup

    lateinit var relatedGameBoard: GameBoard

    var size = 4 // default
        private set

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.GameBoardView)
            setSize(ta.getInteger(R.styleable.GameBoardView_size, 4))
            ta.recycle()
        }
        resize(0, this.size)
        isClickable = true
        isFocusable = true
    }

    fun setDecorView(decor: ViewGroup) {
        decorView = decor
    }

    fun setSize(size: Int) {
        rowCount = size
        columnCount = size
        this.size = size
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private fun resize(oldSize: Int, newSize: Int) {
        when {
            oldSize == newSize -> return
            oldSize < newSize -> {
                // increase
                for (r in oldSize until newSize) {
                    for (c in oldSize until newSize) {
                        addView(buildCardContainer(Coord(r, c)))
                    }
                }
            }
            else -> {
                // oldSize > newSize
                // shrink
                for (r in newSize until oldSize) {
                    for (c in newSize until oldSize) {
                        removeView(getContainerAt(Coord(r, c)))
                    }
                }
            }
        }
    }

    fun notifyActionsArrived(actions: List<ElementAction>) {
        executeActions(actions)
    }

    private fun getContainerAt(coord: Coord): FrameLayout {
        val index = coord.row * size + coord.col
        return getChildAt(index) as FrameLayout
    }

    private fun getNumericSquareAt(coord: Coord): NumericSquare? {
        val container = getContainerAt(coord)
        if (container.childCount == 0) return null
        return container.getChildAt(container.childCount - 1) as? NumericSquare
    }

    private fun checkConsistency() {
        if (GameConfig.DEBUG) {
            var error = false
            for (r in 0 until size) {
                for (c in 0 until size) {
                    val container = getContainerAt(Coord(r, c))
                    if (container.childCount > 1) {
                        error = true
                        Log.e("GB", "At child: ($r, $c), found childCount: ${container.childCount} > 1!!!")
                    }
                    val ns = getNumericSquareAt(Coord(r, c))
                    val elem = relatedGameBoard.view[r][c]
                    if (ns == null && elem == GameBoard.Element.EMPTY) {
                        continue
                    }
                    if (ns == null && elem != GameBoard.Element.EMPTY) {
                        error = true
                        Log.e("GB", "($r, $c) ns is null but elem is not null!")
                    }
                    if (ns != null && elem == GameBoard.Element.EMPTY) {
                        Log.e("GB", "($r, $c) ns is not null, value is ${ns.value} but elem is null")
                        error = true
                    }
                    if (ns?.value != elem.value) {
                        Log.e("GB", "($r, $c) ns value is ${ns?.value} and elem value is ${elem.value} not equal!!")
                        error = true
                    }
                }
            }
            if (!error) {
                Log.d("GB", "Consistency check passed!")
            } else {
                Log.d("GB", "Consistency violation!")
            }
        }
    }

    private fun executeActions(actions: List<ElementAction>) {
        for (action in actions) {
            when (action) {
                is Movement -> {
                    val fromContainer = getContainerAt(action.from)
                    val toContainer = getContainerAt(action.to)
                    val numericSquare = getNumericSquareAt(action.from) ?: continue
                    val r =
                        animatorTracker.wrap(ReparentAnimator(decorView, numericSquare, fromContainer, toContainer) {
                            if (!action.disappearOnEnd) {
                                toContainer.removeAllViews()
                                toContainer.addView(numericSquare)
                            }
                            if (action.bumpOnEnd) {
                                numericSquare.value *= 2
                                toContainer.post { bumpView(numericSquare) }
                            }
                        })
                    r.start()
                }
                is Appear -> {
                    val container = getContainerAt(action.coord)
                    container.removeAllViews()

                    val ns = buildNumericSquare(action.element)
                    animatorTracker.wrap(BumpAppearAnimator(ns, container))
                        .start()
                }
            }
        }
    }

    private fun bumpView(view: View) {
        val bca = BumpCombinationAnimator(decorView, view)
        animatorTracker.wrap(bca).start()
    }

    private fun buildNumericSquare(element: GameBoard.Element): NumericSquare {
        val ns = NumericSquare(context)
        ns.setTextColor(Color.WHITE)
        ns.setCardBackground(context.getDrawable(R.drawable.round_ripple_purple)!!)
        ns.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
            .apply {
                setMargins(dp2Px(4))
            }
        ns.isClickable = false
        ns.isFocusable = false
        ns.elevation = dp2Px(4).toFloat()
        ns.value = element.value
        return ns
    }

    private fun buildCardContainer(coord: Coord): FrameLayout {
        val fl = FrameLayout(context)
        fl.elevation = dp2Px(2).toFloat()
        // set layout_width and layout_height
        val marginLp = MarginLayoutParams(0, 0) // 0 means to match_constraints.
        // set margins
        marginLp.setMargins(dp2Px(2))
        // set grid layout params.
        val lp = LayoutParams(marginLp)
        lp.rowSpec = spec(coord.row, 1f)
        lp.columnSpec = spec(coord.col, 1f)
        fl.background = context.getDrawable(R.color.container_background)
        fl.layoutParams = lp
        fl.isClickable = false
        fl.isFocusable = false
        fl.clipChildren = false
        return fl
    }
}