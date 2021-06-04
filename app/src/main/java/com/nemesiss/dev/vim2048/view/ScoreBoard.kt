package com.nemesiss.dev.vim2048.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.TypeConverter
import android.animation.TypeEvaluator
import android.content.Context
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import com.nemesiss.dev.vim2048.GameConfig
import com.nemesiss.dev.vim2048.R
import com.nemesiss.dev.vim2048.databinding.ScoreBoardBinding
import java.util.*

class ScoreBoard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    inner class AnimatedStepNumber {
        var value = 0
            private set

        private val converter = NumberStringifyConverter()
        private val evaluator = LinearValueEvaluator()

        private var activeAnimator: Animator? = null
        private val increaseDeltaQueue = LinkedList<Int>()

        fun setValue(value: Int) {
            increaseDeltaQueue.clear()
            activeAnimator?.cancel()
            this.value = value
            number.text = value.toString()
        }

        operator fun plusAssign(delta: Int) {
            val oldValue = value
            value += delta
            if (activeAnimator == null) {
                activeAnimator = obtainAnimator(oldValue, value).apply { start() }
            } else {
                increaseDeltaQueue.add(delta)
            }
        }

        private fun next() {
            val first = increaseDeltaQueue.poll()
            if (first == null) {
                activeAnimator = null
                return
            }
            val oldValue = value
            value += first
            activeAnimator = obtainAnimator(oldValue, value).apply { start() }
        }

        private fun obtainAnimator(oldValue: Int, newValue: Int): Animator {
            val animator = ObjectAnimator.ofObject(
                number,
                Property.of(TextView::class.java, CharSequence::class.java, "text"),
                converter,
                evaluator,
                oldValue,
                newValue
            )
            animator.duration = GameConfig.AnimationDuration
            animator.doOnEnd { next() }
            return animator
        }

        private inner class NumberStringifyConverter :
            TypeConverter<Int, CharSequence>(Int::class.java, CharSequence::class.java) {
            override fun convert(value: Int): String {
                return value.toString()
            }
        }

        private inner class LinearValueEvaluator : TypeEvaluator<Int> {
            override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
                return startValue + ((endValue - startValue) * fraction).toInt()
            }
        }
    }

    private val binding = ScoreBoardBinding.bind(View.inflate(context, R.layout.score_board, this))

    private val title = binding.scoreBoardTitle
    private val number = binding.scoreBoardNumber

    val score = AnimatedStepNumber()

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ScoreBoard)
            title.text = ta.getString(R.styleable.ScoreBoard_title)
            ta.recycle()
        }
    }
}