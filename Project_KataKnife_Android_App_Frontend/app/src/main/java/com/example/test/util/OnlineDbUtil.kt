package com.example.test.util

import android.content.res.Resources
import android.util.Log
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.login.LoginUtil.Companion.FAIL
import com.example.test.login.LoginUtil.Companion.LOGIN_CONNECTION_FAIL
import com.example.test.login.LoginUtil.Companion.LOGIN_DONE
import com.example.test.login.LoginUtil.Companion.REGISTER_CONNECTION_ERROR
import com.example.test.login.LoginUtil.Companion.REGISTER_EMAIL_EXIST
import com.example.test.login.LoginUtil.Companion.REGISTER_SUCCESS
import com.example.test.login.LoginUtil.Companion.REGISTER_USERNAME_EXIST
import com.example.test.login.LoginUtil.Companion.SUCCESS
import com.example.test.module.LoginResult
import com.example.test.module.Post
import com.example.test.module.Product
import com.example.test.module.User
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.streams.toList


class OnlineDbUtil private constructor() {
    private object Connection {
        const val POST = 1
        const val PATCH = 2

        /**
         * Connect db to get json data
         * @return MutableList<String>
         */
        @OptIn(DelicateCoroutinesApi::class)
        fun connect(dbName: String, query: String): MutableList<String> {
            var list: MutableList<String> = mutableListOf()
            val connectJob = GlobalScope.async(Dispatchers.IO) {
                //Connection setting
                val url = "https://kahiroshi-3a31.restdb.io/rest/$dbName"
                val restdbEndpoint = URL(url + query)
                val myConnection = restdbEndpoint.openConnection() as HttpsURLConnection
                myConnection.apply {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 10000
                    setRequestProperty("User-Agent", "my-restdb-app")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("x-apikey", Strings.get(R.string.api_key))
                }

                try {
                    if (myConnection.responseCode == 200) {
                        // Success -> read data with stream
                        val br = BufferedReader(InputStreamReader(myConnection.inputStream))
                        list = br.lines().toList().toMutableList()
                        br.close()
                    }
                } catch (_: Exception) {
                } finally {
                    myConnection.disconnect()
                }
            }
            runBlocking { connectJob.await() }
            return list
        }

        @OptIn(DelicateCoroutinesApi::class)
        fun post(dbName: String, data: String, method: Int): Boolean {
            var result = false
            val postJob = GlobalScope.async(Dispatchers.IO) {
                //Connection setting
                val url = "https://kahiroshi-3a31.restdb.io/rest/$dbName"
                val restdbEndpoint = URL(url)
                val myConnection = restdbEndpoint.openConnection() as HttpsURLConnection
                myConnection.apply {
                    requestMethod = if (method == POST) "POST"
                    else "PATCH"
                    doOutput = true
                    connectTimeout = 5000
                    readTimeout = 10000
                    setRequestProperty("User-Agent", "my-restdb-app")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("x-apikey", Strings.get(R.string.api_key))
                    setRequestProperty("Content-Type", "application/json")
                }

                val bw = BufferedWriter(OutputStreamWriter(myConnection.outputStream))
                bw.write(data)
                bw.close()

                try {
                    val responseCode = myConnection.responseCode
                    result = responseCode == 201 || responseCode == 200
                } catch (e: Exception) {
                    Log.d("qaz", e.toString())
                } finally {
                    myConnection.disconnect()
                }
            }
            runBlocking { postJob.await() }
            return result
        }
    }

    companion object {
        /**
         * Get product data(List) from db, return null if connection failed
         * @return MutableList<Product>
         */
        fun queryProduct(
            nameSearch: String? = null,
            typeSearch: String? = null,
            minSearch: String? = null,
            maxSearch: String? = null,
            id: String? = null
        ): MutableList<Product>? {
            val mProduct: MutableList<Product> = mutableListOf()

            val keyWord = if (nameSearch.isNullOrBlank()) ".*" else ".*$nameSearch.*"
            val type = if (typeSearch.isNullOrBlank()) ".*" else typeSearch
            val minPrice = if (minSearch.isNullOrBlank()) "0" else minSearch
            val maxPrice = if (maxSearch.isNullOrBlank()) Int.MAX_VALUE.toString() else maxSearch

            val query = if (!id.isNullOrBlank())
                "?q={\"\$and\":[{\"productName\":{\"\$regex\":\"$keyWord\"}},{\"productType\":{\"\$regex\":\"$type\"}},{\"productPrice\":{\"\$bt\":[$minPrice,$maxPrice]}},{\"productId\":$id}]}"
            else "?q={\"\$and\":[{\"productName\":{\"\$regex\":\"$keyWord\"}},{\"productType\":{\"\$regex\":\"$type\"}},{\"productPrice\":{\"\$bt\":[$minPrice,$maxPrice]}},{\"productId\":{\"\$regex\":\".*\"}}]}"

            //get json(string) list
            val dataList = Connection.connect("product", query)
            if (dataList.isNotEmpty()) { //empty mean connect error, will return null

                //result from db surrounding with [ ] (even no match item found)
                dataList.apply { removeFirst(); removeLast() }

                //Parse json string to <Product>
                val gson = Gson()
                for (json in dataList) {
                    val item: String = if (json.endsWith(',')) json.substring(0, json.length - 1)
                    else json
                    mProduct.add(gson.fromJson(item, Product::class.java))
                }
            } else return null
            return mProduct
        }

        /**
         * Get all product type from db, return null if connection failed
         * @return MutableList<String>
         * */
        fun getProductType(): MutableList<String>? {
            val mType: MutableList<String> = mutableListOf()
            val query = "?q={\"\$distinct\":\"productType\"}&h={\"\$fields\":{\"productType\":1}}"
            val dataList = Connection.connect("product", query)
            if (dataList.isNotEmpty()) {
                dataList.apply { removeFirst(); removeLast() }
                val gson = Gson()
                for (json in dataList) {
                    val item: String = if (json.endsWith(',')) json.substring(0, json.length - 1)
                    else json
                    mType.add(gson.fromJson(item, String::class.java))
                }
            } else return null
            return mType
        }

        /**
         * Get all post from db, return null if connection failed
         * @return MutableList<Post>
         * */
        fun getPost(): MutableList<Post>? {
            val mPost: MutableList<Post> = mutableListOf()

            //get json(string) list
            val dataList = Connection.connect("post", "")
            if (dataList.isNotEmpty()) { //empty mean connect error, will return null

                //result from db surrounding with [ ] (even no match item found)
                dataList.apply { removeFirst(); removeLast() }

                //Parse json string to <Product>
                val gson = Gson()
                for (json in dataList) {
                    val item: String = if (json.endsWith(',')) json.substring(0, json.length - 1)
                    else json
                    mPost.add(gson.fromJson(item, Post::class.java))
                }
            } else return null
            return mPost
        }

        /**
         * Check user name and email is exist and return result code
         * @return Int
         * */
        fun checkUserExist(userName: String, email: String): Int {
            //Check User Name
            val queryName = "?q={\"userName\":\"$userName\"}"
            val nameData = Connection.connect("account", queryName)
            if (nameData.isNotEmpty()) { //empty mean connect error, will return null
                nameData.apply { removeFirst(); removeLast() }
                if (nameData[0].isNotEmpty()) return REGISTER_USERNAME_EXIST
            } else return REGISTER_CONNECTION_ERROR
            //Check Email
            val queryEmail = "?q={\"email\":\"$email\"}"
            val mailData = Connection.connect("account", queryEmail)
            if (mailData.isNotEmpty()) {
                mailData.apply { removeFirst(); removeLast() }
                if (mailData[0].isNotEmpty()) return REGISTER_EMAIL_EXIST
            } else return REGISTER_CONNECTION_ERROR
            //Pass
            return REGISTER_SUCCESS
        }

        /**
         * Insert new account data to db
         * @return Int
         **/
        fun register(userName: String, password: String, email: String): Int {
            val request =
                "{\"userName\":\"${userName}\",\"userPassword\":\"${password}\",\"email\":\"${email}\",\"point\":0,\"permission\":\"0\"}"

            val result = Connection.post("account", request, Connection.POST)

            return if (result) REGISTER_SUCCESS
            else REGISTER_CONNECTION_ERROR
        }

        /**
         * Check user input correct username & password
         * @return Int
         * */
        fun checkLogin(userId: String, userPassword: String): LoginResult {
            val userJson = Connection.connect(
                "account",
                "?q={\"userName\":\"${userId}\",\"userPassword\":\"${userPassword}\"}"
            )
            if (userJson.isNotEmpty()) {  //empty mean connect error, will return null
                var userBean: User? = null
                //result from db surrounding with [ ] (even no match item found)
                val user = userJson.apply { removeFirst(); removeLast() }
                if (user.isNotEmpty()) {
                    val userString = user[0]
                    val gson = Gson()
                    userBean = gson.fromJson(userString, User::class.java)
                }
                return LoginResult(LOGIN_DONE, userBean)
            } else {
                return LoginResult(LOGIN_CONNECTION_FAIL, null)
            }
        }

        /**
         * Update user info
         * @return Int
         * */
        fun updateUserData(user: User): Int {
            val newData =
                "{\"firstName\": \"${user.firstName}\", \"lastName\": \"${user.lastName}\", \"address\": \"${user.lastName}\"}"
            val result = Connection.post("account/${MainActivity.USER_DATA._id}", newData, Connection.PATCH)

            //uploadImage(user.iconByteArray)

            return if (result) SUCCESS
            else FAIL
        }
    }
}