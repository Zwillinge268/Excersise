package com.example.test.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import com.example.test.MainActivity
import com.example.test.login.LoginUtil
import com.example.test.login.LoginUtil.Companion.REGISTER_EMAIL_EXIST
import com.example.test.login.LoginUtil.Companion.REGISTER_SUCCESS
import com.example.test.login.LoginUtil.Companion.REGISTER_USERNAME_EXIST
import com.example.test.module.Cart
import com.example.test.module.Post
import com.example.test.module.Product

//設置 DB 及 Table 名稱
private const val DB_NAME = "KaTaKnifeSQLite.db"

private const val CART_TABLE_NAME = "cart"
private const val CART_CREATE_TABLE_SQL =
    "CREATE TABLE $CART_TABLE_NAME (productId INTEGER PRIMARY KEY, productName TEXT, productPrice TEXT, productImage TEXT, productQty TEXT)"

//參數設置
class HomeDbHelper(
    context: Context,
    name: String = DB_NAME,
    factory: CursorFactory? = null,
    version: Int = 1
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private var mInstance: HomeDbHelper? = null

        fun getInstance(context: Context): HomeDbHelper? {
            if (mInstance == null) mInstance = HomeDbHelper(context)
            return mInstance
        }
    }

    //建立 Helper 時創建 Table
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CART_CREATE_TABLE_SQL)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    //Cart 商品顯示（不帶搜尋功能）
    fun cartShowAll(): MutableList<Cart> {
        val db = readableDatabase
        val result: MutableList<Cart> = ArrayList()

        val cursor =
            db.query(CART_TABLE_NAME, null, null, null, null, null, null)

        while (cursor?.moveToNext() == true) {
            val productId = cursor.getString(cursor.getColumnIndexOrThrow("productId"))
            val productName = cursor.getString(cursor.getColumnIndexOrThrow("productName"))
            val productPrice = cursor.getString(cursor.getColumnIndexOrThrow("productPrice"))
            val productImage = cursor.getString(cursor.getColumnIndexOrThrow("productImage"))
            val productQty = cursor.getString(cursor.getColumnIndexOrThrow("productQty"))

            val cart = Cart(productId, productName, productPrice, productImage, productQty)
            result.add(cart)
        }
        cursor.close()
        return result
    }

    //Cart 加入商品
    fun cartInsert(cart: Cart) {

        val data = ContentValues().apply {
            put("productId", cart.productId)
            put("productName", cart.productName)
            put("productPrice", cart.productPrice)
            put("productImage", cart.productImage)
        }

        val db = writableDatabase
        //如果存在就update，不存在就insert
        val cursor = db.query(
            CART_TABLE_NAME,
            null,
            "productId = '${cart.productId}'",
            null,
            null,
            null,
            null
        )

        if (cursor.count != 0) {
            //指針預設為 -1
            cursor.moveToLast()
            //取出原本數量 + 新加入的數量
            var qty = (cursor.getString(cursor.getColumnIndexOrThrow("productQty"))).toInt()
            qty += cart.productQty.toInt()
            data.put("productQty", qty)
            db.replace(CART_TABLE_NAME, null, data)
        } else {
            //無資料則直接 insert
            data.put("productQty", cart.productQty)
            db.insert(CART_TABLE_NAME, null, data)
        }

        cursor.close()
    }

    //Cart 改變數量
    fun cartQtyChange(id: String, newQty: String): Int {
        val db = writableDatabase

        val data = ContentValues()
        data.put("productQty", newQty)

        return db.update(CART_TABLE_NAME, data, "productId = ?", arrayOf(id))
    }

    //Cart 刪除商品
    fun cartDelItem(cart: Cart): Int {
        val db = writableDatabase
        //返回異動行數
        return db.delete(
            CART_TABLE_NAME, "productId=? AND productName=?",
            arrayOf(cart.productId, cart.productName)
        )
    }
}