package com.nemesiss.dev.crossingcontainermovement.view.animator

import android.util.Log

class AnimatorTracker {
    inner class TrackingWrapper constructor(val animator: CallbackAnimator) : CallbackAnimator {
        private lateinit var originalOnEnd: () -> Unit

        init {
            setOnEndCallback(animator.getOnEndCallback())
        }

        override fun start() {
            increase()
            animator.start()
        }

        override fun getOnEndCallback(): () -> Unit {
            return originalOnEnd
        }

        override fun setOnEndCallback(onEnd: () -> Unit) {
            originalOnEnd = onEnd
            animator.setOnEndCallback(wrapOnEnd(onEnd))
        }

        private fun wrapOnEnd(onEnd: () -> Unit): () -> Unit = {
            decrease()
            onEnd()
        }
    }

    @Volatile
    var runningAnimator = 0
        private set

    fun wrap(animator: CallbackAnimator): CallbackAnimator {
        return TrackingWrapper(animator)
    }

    @Synchronized
    fun increase() {
        runningAnimator++
        Log.w("ATTTTT", "Inc, ${runningAnimator}")
    }


    @Synchronized
    fun decrease() {
        runningAnimator--
        Log.w("ATTTTT", "Dec, ${runningAnimator}")
    }
}