package com.nemesiss.dev.crossingcontainermovement.view.animator

import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.nemesiss.dev.crossingcontainermovement.GameConfig
import com.nemesiss.dev.crossingcontainermovement.R

class BumpAppearAnimator(
    val view: View,
    val container: ViewGroup,
    val duration: Long = GameConfig.AnimationDuration,
    var onEnd: () -> Unit = {}
) : CallbackAnimator {

    override fun start() {
        val anim = AnimationUtils.loadAnimation(view.context, R.anim.bump_appear)
        anim.duration = duration
        container.addView(view)
        view.startAnimation(anim)
        view.postDelayed({ onEnd() }, duration)
    }

    override fun getOnEndCallback(): () -> Unit {
        return onEnd
    }

    override fun setOnEndCallback(onEnd: () -> Unit) {
        this.onEnd = onEnd
    }
}