package com.nemesiss.dev.vim2048.view.animator

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
            onEnd()
            decrease()
        }
    }

    @Volatile
    var runningAnimator = 0
        private set

    fun track(animator: CallbackAnimator): CallbackAnimator {
        return TrackingWrapper(animator)
    }

    fun track(block: () -> CallbackAnimator): CallbackAnimator {
        return TrackingWrapper(block())
    }

    @Synchronized
    fun increase() {
        runningAnimator++
    }


    @Synchronized
    fun decrease() {
        runningAnimator--
    }
}