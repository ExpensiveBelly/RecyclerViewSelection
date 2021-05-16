package com.example.recyclerviewselection

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View

fun View.isEventWithinViewBounds(event: MotionEvent): Boolean {
    val rect = Rect()
    getGlobalVisibleRect(rect)
    return rect.contains(
        event.rawX.toInt(),
        event.rawY.toInt()
    )
}