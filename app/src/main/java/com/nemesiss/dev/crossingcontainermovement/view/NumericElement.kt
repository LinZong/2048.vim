package com.nemesiss.dev.crossingcontainermovement.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.nemesiss.dev.crossingcontainermovement.R
import com.nemesiss.dev.crossingcontainermovement.databinding.NumericSquareBinding
import com.nemesiss.dev.crossingcontainermovement.model.ElementColorTable
import com.nemesiss.dev.crossingcontainermovement.util.dp2Px
import com.nemesiss.dev.crossingcontainermovement.view.animator.typeevaluator.RGB
import dagger.hilt.android.AndroidEntryPoint
import top.defaults.drawabletoolbox.DrawableBuilder
import javax.inject.Inject
import kotlin.math.min

@AndroidEntryPoint
class NumericElement @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {


    @Inject
    lateinit var colorTable: ElementColorTable

    private val binding = NumericSquareBinding.bind(View.inflate(context, R.layout.numeric_square, this))

    var value: Int = 2
        set(value) {
            field = value
            setText(value.toString())
            setCardBackgroundColor(RGB(colorTable[value]))
        }

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.NumericElement)
            binding.numberTextview.setTextColor(
                ta.getColor(
                    R.styleable.NumericElement_textColor,
                    Color.WHITE
                )
            )
            binding.root.background =
                ta.getDrawable(R.styleable.NumericElement_cardBackground) ?: context.getDrawable(R.color.purple_200)
            value = ta.getInteger(R.styleable.NumericElement_value, 2)
            ta.recycle()
        }
        binding.numberTextview.isClickable = false
        binding.numberTextview.isFocusable = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(width, height)
        val squareSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        super.onMeasure(squareSpec, squareSpec)
    }

    fun getCardBackground(): Drawable {
        return binding.root.background
    }

    fun setCardBackground(d: Drawable) {
        binding.root.background = d
    }

    fun setCardBackgroundColor(color: RGB) {
        setCardBackground(roundRectColorDrawable(color.colorValue))
    }

    fun getTextColor() = binding.numberTextview.currentTextColor

    fun setTextColor(color: Int) {
        binding.numberTextview.setTextColor(color)
    }

    fun setTextColor(color: ColorStateList) {
        binding.numberTextview.setTextColor(color)
    }

    fun setText(text: CharSequence) {
        binding.numberTextview.text = text
    }

    private fun roundRectColorDrawable(color: Int): Drawable {
        return DrawableBuilder()
            .rectangle()
            .cornerRadius(dp2Px(4))
            .solidColor(color)
            .ripple()
            .rippleColor(Color.WHITE)
            .build()
    }
}