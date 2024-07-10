package com.example.test.dbUtil

import android.util.Log
import com.example.test.login.LoginUtil.Companion.REGISTER_CONNECTION_ERROR
import com.example.test.login.LoginUtil.Companion.REGISTER_EMAIL_EXIST
import com.example.test.login.LoginUtil.Companion.REGISTER_SUCCESS
import com.example.test.login.LoginUtil.Companion.REGISTER_USERNAME_EXIST
import com.example.test.module.Post
import com.example.test.module.Product
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
                    setRequestProperty("x-apikey", "6632e1a7f438f1421cb48b0d")
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
        fun post(dbName: String, data: String) {
            val postJob = GlobalScope.async(Dispatchers.IO) {
                //Connection setting
                val url = "https://kahiroshi-3a31.restdb.io/rest/$dbName"
                val restdbEndpoint = URL(url)
                val myConnection = restdbEndpoint.openConnection() as HttpsURLConnection
                myConnection.apply {
                    requestMethod = "POST"
                    connectTimeout = 5000
                    readTimeout = 10000
                    setRequestProperty("User-Agent", "my-restdb-app")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("x-apikey", "6632e1a7f438f1421cb48b0d")
                }
                try {
                    if (myConnection.responseCode == 200) {
                        Log.d("qaz","Code=200")
                        // Success -> write data with stream
                        val bw = BufferedWriter(OutputStreamWriter(myConnection.outputStream))
                        bw.write(data)
                        bw.close()
                    } else {Log.d("qaz", myConnection.responseCode.toString())}
                } catch (e: Exception) {
                    Log.d("qaz", e.toString())
                } finally {
                    myConnection.disconnect()
                }
            }
            runBlocking { postJob.await() }
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
        fun register(data: String) {
            Connection.post("account", data)
        }


        ////Register 新增用戶
        //    fun register(userName: String, password: String, email: String): Long {
        //        val db = writableDatabase
        //        val data = ContentValues().apply {
        //            put("userName", userName)
        //            put("userPassword", password)
        //            put("email", email)
        //            put("permission", 0)
        //            put("firstName", "")
        //            put("lastName", "")
        //            put("address", "")
        //            put("point", "0")
        //        }
        //        return db.insert(ACCOUNT_TABLE_NAME, null, data)
        //    }
    }
}