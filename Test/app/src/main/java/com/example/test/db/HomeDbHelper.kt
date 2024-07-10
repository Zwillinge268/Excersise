package com.example.test.db

//TODO: move sqlite to online db (now try post new ac data to db)                  <-- NOW

//other
//TODO: Finish About us text
//TODO: Finish detail text *1 (same detail for all)
//TODO: Finish recommend img *2

//TODO: Make zoom img perfect combined with view pager

//TODO: Finish search page
//TODO: Night mode text color

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

private const val ACCOUNT_TABLE_NAME = "account"
private const val ACCOUNT_CREATE_TABLE_SQL =
    "CREATE TABLE $ACCOUNT_TABLE_NAME (userID INTEGER PRIMARY KEY, userName TEXT, userPassword TEXT, email TEXT, firstName TEXT, lastName TEXT, address TEXT, permission INTEGER, point TEXT, icon BLOB)"

private const val POST_TABLE_NAME = "post"
private const val POST_CREATE_TABLE_SQL =
    "CREATE TABLE $POST_TABLE_NAME (postID INTEGER PRIMARY KEY, postImg TEXT, postTitle TEXT, postContent TEXT, postDate TEXT)"

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
        //db?.execSQL(HOME_CREATE_TABLE_SQL)
        db?.execSQL(CART_CREATE_TABLE_SQL)
        db?.execSQL(ACCOUNT_CREATE_TABLE_SQL)
        db?.execSQL(POST_CREATE_TABLE_SQL)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun insertData() {
        val db = writableDatabase

        val user = ContentValues().apply {
            put("userName", "admin")
            put("userPassword", "admin")
            put("email", "admin@admin.com")
            put("firstName", "admin")
            put("lastName", "kotlin")
            put("address", "Rm.8 11/F, block A, earth street, Earth")
            put("permission", 0)
            put("point", "896")
            put("icon", byteArrayOf())
        }

        db.insert(ACCOUNT_TABLE_NAME, null, user)

        val post = ContentValues().apply {
            put("postTitle", "慶祝新開張！超大優惠送給你！")
            put("postImg", "https://i.imgur.com/rlZTSgs.jpg")
            put("postContent", "Content of HTML.\n TODO: Please finish the content html.")
            put("postDate", "2024-02-31")
        }

        db.insert(POST_TABLE_NAME, null, post)

        val post2 = ContentValues().apply {
            put("postTitle", "臺北信義總店開張！來看看有甚麽好貨~")
            put("postImg", "https://i.imgur.com/rlZTSgs.jpg")
            put("postContent", "Content of HTML.\n TODO: Please finish the content html.")
            put("postDate", "2024-02-31")
        }

        db.insert(POST_TABLE_NAME, null, post2)
    }

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

    //Register 新增用戶
    fun register(userName: String, password: String, email: String): Long {
        val db = writableDatabase
        val data = ContentValues().apply {
            put("userName", userName)
            put("userPassword", password)
            put("email", email)
            put("permission", 0)
            put("firstName", "")
            put("lastName", "")
            put("address", "")
            put("point", "0")
        }
        return db.insert(ACCOUNT_TABLE_NAME, null, data)
    }

    //Waiting to move to online db
    fun checkLogin(userId: String, userPassword: String): Int {
        val db = readableDatabase
        val cursor = db.query(
            ACCOUNT_TABLE_NAME, null, "userName = '$userId'", null, null, null, null
        )

        if (cursor.count != 1 || !cursor.moveToLast()) cursor.close()
            .run { return LoginUtil.INVALID_ID }
        if (cursor.getString(cursor.getColumnIndexOrThrow("userPassword")) != userPassword) cursor.close()
            .run { return LoginUtil.INVALID_PASSWORD }
        if (cursor.getInt(cursor.getColumnIndexOrThrow("permission")) == 1) cursor.close()
            .run { return LoginUtil.AC_DISABLE }

        //Temp message of user
        MainActivity.USER_DATA.apply {
            isLogin = true
            userID = String.format("%06d", cursor.getInt(cursor.getColumnIndexOrThrow("userID")))
            userName = cursor.getString(cursor.getColumnIndexOrThrow("userName"))
            email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            firstName = cursor.getString(cursor.getColumnIndexOrThrow("firstName"))
            lastName = cursor.getString(cursor.getColumnIndexOrThrow("lastName"))
            address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
            point = cursor.getString(cursor.getColumnIndexOrThrow("point"))
            //icon = LoginUtil.getImage(cursor.getBlob(cursor.getColumnIndexOrThrow("icon")))
        }
        cursor.close()
        return LoginUtil.SUCCESS
    }

    //Change user data
    fun userDataChange(): Int {
        val db = writableDatabase
        val data = ContentValues().apply {
            put("firstName", MainActivity.USER_DATA.firstName)
            put("lastName", MainActivity.USER_DATA.lastName)
            put("address", MainActivity.USER_DATA.address)
            put("icon", LoginUtil.getBytes(MainActivity.USER_DATA.icon))
        }

        return db.update(
            ACCOUNT_TABLE_NAME,
            data,
            "userName=? AND email=?",
            arrayOf(MainActivity.USER_DATA.userName, MainActivity.USER_DATA.email)
        )
    }
}