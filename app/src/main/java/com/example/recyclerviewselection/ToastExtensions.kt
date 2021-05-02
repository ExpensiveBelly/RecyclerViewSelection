package com.example.recyclerviewselection

import android.content.Context
import android.widget.Toast

/**
 * https://github.com/Kotlin/anko/blob/master/anko/library/static/commons/src/main/java/dialogs/Toasts.kt
 */

inline fun Context.toast(message: String): Toast = Toast
    .makeText(this, message, Toast.LENGTH_SHORT)
    .apply {
        show()
    }