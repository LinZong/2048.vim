package com.nemesiss.dev.crossingcontainermovement.util

import android.content.Context
import android.util.TypedValue
import android.view.View

fun Context.dp2Px(dp: Int): Int {
    return TypedValue
        .applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        )
        .toInt()
}

fun View.dp2Px(dp: Int): Int {
    return context.dp2Px(dp)
}
