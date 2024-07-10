package com.example.test.login

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import com.example.test.db.HomeDbHelper
import java.io.ByteArrayOutputStream

class LoginUtil(context: Context) {
    private val mDbHelper = HomeDbHelper.getInstance(context)!!

    companion object {
        //登錄結果
        const val INVALID_ID = 0
        const val INVALID_PASSWORD = 1
        const val AC_DISABLE = 2
        const val SUCCESS = 3

        //注冊結果
        const val REGISTER_USERNAME_EXIST = 119
        const val REGISTER_EMAIL_EXIST = 120
        const val REGISTER_SUCCESS = 121
        const val REGISTER_CONNECTION_ERROR = 122


        // convert from bitmap to byte array
        fun getBytes(bitmap: Bitmap?): ByteArray? {
            if (bitmap == null) return null
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

    //登錄方法 (Move to online db)
    fun userLogin(userId: String, userPassword: String): Int {
        return mDbHelper.checkLogin(userId, userPassword)
    }
}