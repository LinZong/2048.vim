package com.nemesiss.dev.vim2048.view.animator.typeevaluator

import android.animation.TypeEvaluator

class RGB {
    val r: Int
    val g: Int
    val b: Int

    val colorValue get() = (0xFF shl 24) or (r shl 16) or (g shl 8) or b

    constructor(color: Int) {
        r = (color ushr 16) and 0xFF
        g = (color ushr 8) and 0xFF
        b = (color) and 0xFF
    }

    constructor(r: Int, g: Int, b: Int) {
        this.r = r
        this.g = g
        this.b = b
    }

    val grayscale: RGB
        get() {
            val gray = (r * 19595 + g * 38469 + b * 7472) shr 16
            return RGB(gray, gray, gray)
        }
}

class ColorTypeEvaluator : TypeEvaluator<RGB> {
    override fun evaluate(fraction: Float, startValue: RGB, endValue: RGB): RGB {
        val cr = (startValue.r + (endValue.r - startValue.r) * fraction).toInt()
        val cg = (startValue.g + (endValue.g - startValue.g) * fraction).toInt()
        val cb = (startValue.b + (endValue.b - startValue.b) * fraction).toInt()
        return RGB(cr, cg, cb)
    }
}