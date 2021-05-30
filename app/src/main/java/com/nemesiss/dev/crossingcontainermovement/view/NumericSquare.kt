package com.nemesiss.dev.crossingcontainermovement.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.nemesiss.dev.crossingcontainermovement.R
import com.nemesiss.dev.crossingcontainermovement.databinding.NumericSquareBinding
import kotlin.math.min

class NumericSquare @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val binding = NumericSquareBinding.bind(View.inflate(context, R.layout.numeric_square, this))

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.NumericSquare)
            binding.numberTextview.setTextColor(ta.getColor(R.styleable.NumericSquare_textColor, Color.WHITE))
            binding.root.background =
                ta.getDrawable(R.styleable.NumericSquare_cardBackground) ?: context.getDrawable(R.color.purple_200)
            ta.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(width, height)
        val squareSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        super.onMeasure(squareSpec, squareSpec)
    }
}