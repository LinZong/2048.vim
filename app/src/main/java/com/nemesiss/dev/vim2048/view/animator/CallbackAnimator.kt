package com.nemesiss.dev.vim2048.view.animator

interface CallbackAnimator {
    fun start()
    fun getOnEndCallback(): () -> Unit
    fun setOnEndCallback(onEnd: () -> Unit)
}