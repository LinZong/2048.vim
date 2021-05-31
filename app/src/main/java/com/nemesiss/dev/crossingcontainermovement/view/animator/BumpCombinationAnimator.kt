package com.nemesiss.dev.crossingcontainermovement.view.animator

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import com.nemesiss.dev.crossingcontainermovement.GameConfig

class BumpCombinationAnimator(decorView: ViewGroup, val bumpingView: View, var onEnd: () -> Unit = {}) :
    OverlayAnimator(decorView), CallbackAnimator {
    override fun start() {
        val parent = bumpingView.parent as? ViewGroup ?: return
        overlayOnDecorView(bumpingView)
        parent.removeView(bumpingView)
        // play animation
        val preScaleX = ObjectAnimator.ofFloat(bumpingView, "scaleX", 1f, 1.2f)
        preScaleX.duration = GameConfig.AnimationDuration / 2

        val preScaleY = ObjectAnimator.ofFloat(bumpingView, "scaleY", 1f, 1.2f)
        preScaleY.duration = GameConfig.AnimationDuration / 2

        val preAs = AnimatorSet()
        preAs.playTogether(preScaleX, preScaleY)

        val postScaleX = ObjectAnimator.ofFloat(bumpingView, "scaleX", 1.2f, 1f)
        val postScaleY = ObjectAnimator.ofFloat(bumpingView, "scaleY", 1.2f, 1f)
        postScaleX.duration = GameConfig.AnimationDuration / 2
        postScaleY.duration = GameConfig.AnimationDuration / 2
        val postAs = AnimatorSet()
        postAs.playTogether(postScaleX, postScaleY)
        AnimatorSet().apply {
            playSequentially(preAs, postAs)
            doOnEnd {
                removeDecorViewOverlay(bumpingView)
                bumpingView.clearParentForSafe()
                parent.addView(bumpingView)
                onEnd()
            }
            start()
        }
    }

    override fun getOnEndCallback(): () -> Unit {
        return onEnd
    }

    override fun setOnEndCallback(onEnd: () -> Unit) {
        this.onEnd = onEnd
    }

    private fun View.clearParentForSafe() {
        val p = parent as? ViewGroup ?: return
        p.removeView(this)
    }
}