package com.nemesiss.dev.vim2048.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.core.view.setMargins
import com.nemesiss.dev.vim2048.GameBoard
import com.nemesiss.dev.vim2048.GameConfig
import com.nemesiss.dev.vim2048.R
import com.nemesiss.dev.vim2048.action.Appear
import com.nemesiss.dev.vim2048.action.ElementAction
import com.nemesiss.dev.vim2048.action.Movement
import com.nemesiss.dev.vim2048.model.Coord
import com.nemesiss.dev.vim2048.model.ElementColorTable
import com.nemesiss.dev.vim2048.util.dp2Px
import com.nemesiss.dev.vim2048.view.animator.AnimatorTracker
import com.nemesiss.dev.vim2048.view.animator.BumpAppearAnimator
import com.nemesiss.dev.vim2048.view.animator.BumpCombinationAnimator
import com.nemesiss.dev.vim2048.view.animator.ReparentAnimator
import com.nemesiss.dev.vim2048.view.animator.typeevaluator.ColorTypeEvaluator
import com.nemesiss.dev.vim2048.view.animator.typeevaluator.RGB
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.min

@AndroidEntryPoint
class GameBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    @Inject
    lateinit var colorTable: ElementColorTable

    private inner class GestureHandler : GestureDetector.SimpleOnGestureListener() {
        val detector = GestureDetector(context, this)
        var disabled = false

        private var handled = false
        private val TAG = "GestureHandler"
        private val scrollThreshold = 150
        private val velocityThreshold = 1500

        override fun onDown(e: MotionEvent?): Boolean {
            if (!disabled) handled = false
            return !disabled
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            if (disabled || handled) return true
            if (animatorTracker.runningAnimator > 0) return true

            if (abs(velocityY) >= velocityThreshold) {
                Log.w(TAG, "VX: ${velocityX}, VY: $velocityY")
                if (velocityY > 0) {
                    relatedGameBoard.handleGesture(GameBoard.GestureDirection.DOWN)
                } else {
                    relatedGameBoard.handleGesture(GameBoard.GestureDirection.UP)
                }
                handled = true
                return true
            }

            if (abs(velocityX) >= velocityThreshold) {
                Log.w(TAG, "VX: ${velocityX}, VY: $velocityY")
                if (velocityX > 0) {
                    relatedGameBoard.handleGesture(GameBoard.GestureDirection.RIGHT)
                } else {
                    relatedGameBoard.handleGesture(GameBoard.GestureDirection.LEFT)
                }
                handled = true
                return true
            }
            // otherwise
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (e1 == null || e2 == null) return true
            if (disabled || handled) return true
            if (animatorTracker.runningAnimator > 0) return true

            val deltaY = e2.rawY - e1.rawY
            if (abs(deltaY) >= scrollThreshold) {
                Log.w(TAG, "onScroll, handleY")
                if (deltaY > 0) {
                    relatedGameBoard.handleGesture(GameBoard.GestureDirection.DOWN)
                } else {
                    relatedGameBoard.handleGesture(GameBoard.GestureDirection.UP)
                }
                handled = true
                return true
            }

            val deltaX = e2.rawX - e1.rawX
            if (abs(deltaX) >= scrollThreshold) {
                Log.w(TAG, "onScroll, handleX")
                if (deltaX > 0) {
                    relatedGameBoard.handleGesture(GameBoard.GestureDirection.RIGHT)
                } else {
                    relatedGameBoard.handleGesture(GameBoard.GestureDirection.LEFT)
                }
                handled = true
                return true
            }
            return true
        }
    }

    private val gestureHandler = GestureHandler()

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

    override fun onTouchEvent(event: MotionEvent?) = gestureHandler.detector.onTouchEvent(event)

    fun notifyActionArrived(action: ElementAction) {
        notifyActionsArrived(listOf(action))
    }

    fun notifyActionsArrived(actions: List<ElementAction>) {
        executeActions(actions)
    }

    private fun executeActions(actions: List<ElementAction>) {
        for (action in actions) {
            action.apply(this)
        }
    }

    fun doMovement(action: Movement) {
        val fromContainer = getContainerAt(action.from)
        val toContainer = getContainerAt(action.to)
        val numericSquare = getNumericElementAt(action.from) ?: return
        val r =
            animatorTracker.track {
                ReparentAnimator(decorView, numericSquare, fromContainer, toContainer) {
                    if (!action.disappearOnEnd) {
                        toContainer.removeAllViews()
                        toContainer.addView(numericSquare)
                    }
                    if (action.bumpOnEnd) {
                        numericSquare.value *= 2
                        // here we have to hold another lock to prevent any unintended movement
                        // in such a layout cycle between this animation end and bump animation start.
                        animatorTracker.increase()
                        toContainer.post { bumpView(numericSquare) }
                    }
                }
            }
        r.start()
    }

    fun doAppear(action: Appear) {
        val container = getContainerAt(action.coord)
        container.removeAllViews()

        val ns = buildNumericElement(action.element)
        animatorTracker
            .track { BumpAppearAnimator(ns, container) }
            .start()
    }

    fun doReset() {
        for (r in 0 until size) {
            for (c in 0 until size) {
                getContainerAt(Coord(r, c)).removeAllViews()
            }
        }
        gestureHandler.disabled = false
    }

    fun doDied() {
        gestureHandler.disabled = true
        val animators = arrayListOf<Animator>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                val element = getNumericElementAt(Coord(r, c)) ?: continue
                val color = RGB(colorTable[element.value])
                val gray = color.grayscale
                animators += ObjectAnimator
                    .ofObject(element, "cardBackgroundColor", ColorTypeEvaluator(), color, gray)
                    .apply {
                        duration = GameConfig.DiedAnimation
                    }
            }
        }
        val set = AnimatorSet()
        set.playTogether(animators)
        set.start()
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val width = MeasureSpec.getSize(widthSpec)
        val height = MeasureSpec.getSize(heightSpec)
        val size = min(width, height)
        val spec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        super.onMeasure(spec, spec)
    }

    private fun resize(oldSize: Int, newSize: Int) {
        when {
            oldSize == newSize -> return
            oldSize < newSize -> {
                // increase
                for (r in oldSize until newSize) {
                    for (c in oldSize until newSize) {
                        addView(buildNumericContainer(Coord(r, c)))
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

    private fun getContainerAt(coord: Coord): FrameLayout {
        val index = coord.row * size + coord.col
        return getChildAt(index) as FrameLayout
    }

    private fun getNumericElementAt(coord: Coord): NumericElement? {
        val container = getContainerAt(coord)
        if (container.childCount == 0) return null
        return container.getChildAt(container.childCount - 1) as? NumericElement
    }

    private fun bumpView(view: View) {
        animatorTracker.track {
            BumpCombinationAnimator(decorView, view) {
                // release lock
                animatorTracker.decrease()
            }
        }.start()
    }

    private fun buildNumericElement(element: GameBoard.Element): NumericElement {
        val ns = NumericElement(context)
        ns.setTextColor(Color.WHITE)
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

    private fun buildNumericContainer(coord: Coord): FrameLayout {
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

    private fun checkConsistency() {
        if (GameConfig.DEBUG) {
            var error = false
            for (r in -1 until size) {
                for (c in -1 until size) {
                    val container = getContainerAt(Coord(r, c))
                    if (container.childCount > 0) {
                        error = true
                        Log.e("GB", "At child: ($r, $c), found childCount: ${container.childCount} > 0!!!")
                    }
                    val ns = getNumericElementAt(Coord(r, c))
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
}