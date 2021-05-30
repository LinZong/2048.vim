package com.nemesiss.dev.crossingcontainermovement

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.nemesiss.dev.crossingcontainermovement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.movingView.setOnClickListener { move() }
    }

    private fun move() {
        val mv = binding.movingView
        val container = mv.parent as? ViewGroup ?: return
        val nextContainer = if (container == binding.leftContainer) binding.rightContainer else binding.leftContainer
        moveToContainer(mv, container, nextContainer)
    }

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

    private companion object {
        const val LEFT = false
        const val RIGHT = true
        const val TAG = "MainActivity"
    }
}