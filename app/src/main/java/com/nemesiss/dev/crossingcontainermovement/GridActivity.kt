package com.nemesiss.dev.crossingcontainermovement

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.get
import androidx.core.view.setMargins
import com.nemesiss.dev.crossingcontainermovement.databinding.ActivityGridBinding
import com.nemesiss.dev.crossingcontainermovement.view.NumericElement

class GridActivity : AppCompatActivity(), View.OnClickListener {

    private val binding by lazy { ActivityGridBinding.inflate(layoutInflater) }
    private lateinit var numericElement: NumericElement
    private var moving = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        numericElement = buildNumericSquare()
//        binding.containerGrid.children.forEach { cv -> cv.setOnClickListener(this) }
//        val child = binding.containerGrid.getChild(1, 1)
//        if (child is ViewGroup) {
//            child.addView(numericElement)
//        }
//        binding.containerGrid.post {
//            val child = binding.containerGrid.getChild(1,3)
//            println(child.layoutParams)
//        }
    }

    override fun onClick(v: View?) {
        if (v !is ViewGroup) return
        val curr = numericElement.parent as? ViewGroup ?: return
        if (moving) return
        moving = true
        moveToContainer(numericElement, curr, v)
    }

    private fun GridLayout.getChild(row: Int, col: Int): View {
        if (row !in 0 until rowCount) throw IllegalArgumentException("row index is invalid.")
        if (col !in 0 until columnCount) throw IllegalArgumentException("column index is invalid.")
        val index = row * columnCount + col
        return get(index)
    }

    private fun buildNumericSquare(): NumericElement {
        val ns = NumericElement(this)
        ns.setTextColor(Color.WHITE)
        ns.setCardBackground(getDrawable(R.drawable.round_ripple_purple)!!)
        ns.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER)
            .apply {
                setMargins(4.toPx())
            }
        ns.isClickable = true
        ns.isFocusable = true
        ns.elevation = 4.toPx().toFloat()
        return ns
    }

    private fun Context.dp2Px(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
    }

    private fun Int.toPx() = dp2Px(this)

    private fun moveToContainer(view: View, currentContainer: ViewGroup, nextContainer: ViewGroup) {
        val cr = currentContainer.getGlobalVisibleRect()
        val nr = nextContainer.getGlobalVisibleRect()

        val oldTranslations = view.translationX to view.translationY
        // 1. Add view to the ViewOverlay of DecorView.
        overlayOnDecorView(view)
        // 2. Remove view from current container.
        currentContainer.removeView(view)
        // 3. Calculate distance between two container.
        val deltaX = nr.left - cr.left
        val deltaY = nr.top - cr.top
        // 4. Move view with translation property.
        val animator = animateTranslationDelta(view, deltaX.toFloat(), deltaY.toFloat())
        // 5. Add view to next container and clean any translation property.
        animator.doOnEnd {
            removeDecorViewOverlay(view)
            nextContainer.addView(view)
            val (x, y) = oldTranslations
            view.translationX = x
            view.translationY = y
            moving = false
        }
    }

    private fun animateTranslationDelta(mv: View, deltaX: Float, deltaY: Float): AnimatorSet {
        val moveX = ObjectAnimator.ofFloat(mv, "translationX", 0f, deltaX)
        val moveY = ObjectAnimator.ofFloat(mv, "translationY", 0f, deltaY)
        val moveAnimations = AnimatorSet()
        moveAnimations.duration = 500
        moveAnimations.playTogether(moveX, moveY)
        moveAnimations.start()
        return moveAnimations
    }

    private fun overlayOnDecorView(view: View) {
        val r = view.getGlobalVisibleRect()
        val vg = window.decorView as? ViewGroup ?: return
        vg.overlay.add(view)
        // since ViewOverlayGroup doesn't implement [onLayout], we should do this manually.
        view.layout(r.left, r.top, r.right, r.bottom)
    }

    private fun removeDecorViewOverlay(view: View) {
        val vg = window.decorView as? ViewGroup ?: return
        vg.overlay.remove(view)
    }

    private fun View.getGlobalVisibleRect(): Rect {
        val r = Rect()
        getGlobalVisibleRect(r)
        return r
    }
}