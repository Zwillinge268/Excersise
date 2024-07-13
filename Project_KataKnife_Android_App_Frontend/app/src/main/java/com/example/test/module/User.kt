package com.example.test.module

import android.graphics.Bitmap

data class User(
    var isLogin: Boolean = false,
    var userId: String = "",
    var userName: String = "",
    var email: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var address: String = "",
    var point: String = "",
    var icon: Bitmap? = null,
    var iconByteArray: ByteArray = byteArrayOf(),
    var _id: String = ""
) {}