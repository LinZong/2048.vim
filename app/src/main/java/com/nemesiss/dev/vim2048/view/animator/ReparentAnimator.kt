package com.nemesiss.dev.vim2048.view.animator

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import com.nemesiss.dev.vim2048.GameConfig

class ReparentAnimator(
    decorView: ViewGroup,
    val movingView: View,
    val fromContainer: ViewGroup,
    val toContainer: ViewGroup,
    val duration: Long = GameConfig.AnimationDuration,
    var onEnd: () -> Unit = {}
) : OverlayAnimator(decorView), CallbackAnimator {
    override fun start() {
        moveToContainer(movingView, fromContainer, toContainer)
    }

    override fun getOnEndCallback(): () -> Unit {
        return onEnd
    }

    override fun setOnEndCallback(onEnd: () -> Unit) {
        this.onEnd = onEnd
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
            val (x, y) = oldTranslations
            view.translationX = x
            view.translationY = y
            onEnd()
        }
    }

    private fun animateTranslationDelta(mv: View, deltaX: Float, deltaY: Float): AnimatorSet {
        val moveX = ObjectAnimator.ofFloat(mv, "translationX", 0f, deltaX)
        val moveY = ObjectAnimator.ofFloat(mv, "translationY", 0f, deltaY)
        val moveAnimations = AnimatorSet()
        moveAnimations.duration = 300
        moveAnimations.playTogether(moveX, moveY)
        moveAnimations.start()
        return moveAnimations
    }
}