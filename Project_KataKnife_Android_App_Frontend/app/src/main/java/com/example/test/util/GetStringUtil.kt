package com.example.test.util

import android.app.Application
import androidx.annotation.StringRes

class GetStringUtil : Application() {
    companion object {
        lateinit var instance: GetStringUtil private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}

object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return GetStringUtil.instance.getString(stringRes, *formatArgs)
    }
}