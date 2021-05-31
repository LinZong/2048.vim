package com.nemesiss.dev.crossingcontainermovement.model

import android.content.Context
import com.nemesiss.dev.crossingcontainermovement.R.color.*
import java.util.concurrent.ConcurrentHashMap

class ElementColorTable private constructor() {
    companion object {

        @Volatile
        private var INSTANCE: ElementColorTable? = null

        fun getInstance(context: Context): ElementColorTable {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ElementColorTable().apply {
                    prepare(context)
                    INSTANCE = this
                }
            }
        }
    }

    private val colorDefine = ConcurrentHashMap<Int, Int>()

    private fun prepare(context: Context) {
        val refs = arrayOf(
            2 to color_2,
            4 to color_4,
            8 to color_8,
            16 to color_16,
            32 to color_32,
            64 to color_64,
            128 to color_128,
            256 to color_256,
            512 to color_512,
            1024 to color_1024,
            2048 to color_2048
        )

        for ((value, ref) in refs) {
            colorDefine[value] = context.getColor(ref)
        }
    }

    fun getColor(number: Int): Int {
        var n = number
        while (n > 2048) {
            n /= 2048
        }
        return colorDefine[n] ?: color_2
    }

    operator fun get(number: Int) = getColor(number)
}