package com.example.test.login

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import com.example.test.MainActivity
import com.example.test.db.HomeDbHelper
import com.example.test.util.OnlineDbUtil
import java.io.ByteArrayOutputStream

class LoginUtil(context: Context) {
    private val mDbHelper = HomeDbHelper.getInstance(context)!!

    companion object {
        //登錄結果
        const val LOGIN_CONNECTION_FAIL = 0
        const val LOGIN_DONE = 1
        const val INVALID_LOGIN_INFO = 2

        //注冊結果
        const val REGISTER_USERNAME_EXIST = 119
        const val REGISTER_EMAIL_EXIST = 120
        const val REGISTER_SUCCESS = 121
        const val REGISTER_CONNECTION_ERROR = 122

        //更改結果
        const val SUCCESS = 230
        const val FAIL = 231


        // convert from bitmap to byte array
        fun getBytes(bitmap: Bitmap): ByteArray {
//            if (bitmap == null) return null
            val stream = ByteArrayOutputStream()
            bitmap.compress(CompressFormat.PNG, 0, stream)
            return stream.toByteArray()
        }

        // convert from byte array to bitmap
        fun getImage(image: ByteArray): Bitmap? {
            if (image.isEmpty()) return null
            return BitmapFactory.decodeByteArray(image, 0, image.size)
        }
    }

    //登錄方法
    fun userLogin(userId: String, userPassword: String): Int {
        val result = OnlineDbUtil.checkLogin(userId, userPassword);
        if (result.resultCode == LOGIN_DONE && result.user != null) {
            MainActivity.USER_DATA = result.user
            MainActivity.USER_DATA.isLogin = true
            MainActivity.USER_DATA.icon = getImage( MainActivity.USER_DATA.iconByteArray )
            return LOGIN_DONE
        } else if (result.resultCode == LOGIN_DONE && result.user == null) {
            return INVALID_LOGIN_INFO
        } else {
            return LOGIN_CONNECTION_FAIL
        }
    }
}