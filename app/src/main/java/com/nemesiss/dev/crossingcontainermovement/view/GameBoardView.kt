package com.nemesiss.dev.crossingcontainermovement.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.core.view.setMargins
import com.nemesiss.dev.crossingcontainermovement.R
import com.nemesiss.dev.crossingcontainermovement.action.ElementAction
import com.nemesiss.dev.crossingcontainermovement.model.Coord
import com.nemesiss.dev.crossingcontainermovement.util.dp2Px

class GameBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    var size = 4 // default
        private set

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.GameBoardView)
            setSize(ta.getInteger(R.styleable.GameBoardView_size, 4))
            ta.recycle()
        }
        resize(0, this.size)
    }

    fun setSize(size: Int) {
        rowCount = size
        columnCount = size
        this.size = size
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
                        removeView(getChildAt(Coord(r, c)))
                    }
                }
            }
        }
    }

    private fun getChildAt(coord: Coord): View {
        val index = coord.row * size + coord.col
        return getChildAt(index)
    }

    private fun executeActions(actions: List<ElementAction>) {
        for(action in actions) {

        }
    }

    private fun buildNumericSquare(): NumericSquare {
        val ns = NumericSquare(context)
        ns.setTextColor(Color.WHITE)
        ns.setCardBackground(context.getDrawable(R.drawable.round_ripple_purple)!!)
        ns.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
            .apply {
                setMargins(dp2Px(2))
            }
        ns.isClickable = true
        ns.isFocusable = true
        ns.elevation = dp2Px(4).toFloat()
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
        return fl
    }
}