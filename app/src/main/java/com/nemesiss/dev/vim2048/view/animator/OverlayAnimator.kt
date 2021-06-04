package com.nemesiss.dev.vim2048.view.animator

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup

open class OverlayAnimator(val decorView: ViewGroup) {
    protected fun overlayOnDecorView(view: View) {
        val r = view.getGlobalVisibleRect()
        val vg = decorView
        vg.overlay.add(view)
        // since ViewOverlayGroup doesn't implement [onLayout], we should do this manually.
        view.layout(r.left, r.top, r.right, r.bottom)
    }

    protected fun removeDecorViewOverlay(view: View) {
        val vg = decorView
        vg.overlay.remove(view)
    }

    protected fun View.getGlobalVisibleRect(): Rect {
        val r = Rect()
        getGlobalVisibleRect(r)
        return r
    }
}