package com.kumar.acronyms.utils

import android.util.Log


data class Logger(val tag: String, val message: String?) {
    companion object {
        fun info(tag: String, message: String) =
            Log.i(tag, message)

        fun error(tag: String, message: String) =
            Log.e(tag, message)

        fun warn(tag: String, message: String) =
            Log.w(tag, message)

        fun debug(tag: String, message: String) =
            Log.d(tag, message)
    }
}
