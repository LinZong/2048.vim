package com.nemesiss.dev.crossingcontainermovement.view.animator

interface CallbackAnimator {
    fun start()
    fun getOnEndCallback(): () -> Unit
    fun setOnEndCallback(onEnd: () -> Unit)
}