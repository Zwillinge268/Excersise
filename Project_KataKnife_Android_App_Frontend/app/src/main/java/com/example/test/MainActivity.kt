package com.example.test

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.test.login.LoginUtil
import com.example.test.module.Post
import com.example.test.module.User
import com.google.android.material.bottomnavigation.BottomNavigationView

//Project KataKnife
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var mainViewPager: ViewPager2
    private lateinit var mVPAdapter: MainVPAdapter

    //全局參數，暫存 user資料 及 推薦文章
    companion object {
        var USER_DATA = User()
        var POST_DATA = mutableListOf<Post>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkLogin()
        initView()
        initEvent()
    }

    private fun checkLogin() {
        //自動登入
        val sharedPreferences =
            getSharedPreferences("com.example.test.REMEMBER", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("remember", false)) {
            val id = sharedPreferences.getString("userId", "")!!
            val pw = sharedPreferences.getString("userPassword", "")!!

            //TODO

            if (LoginUtil(this).userLogin(id, pw) == LoginUtil.LOGIN_DONE) {
                Toast.makeText(
                    this,
                    getString(R.string.login_welcome, USER_DATA.userName),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initView() {
        bottomNav = findViewById(R.id.bottom_nav)
        mainViewPager = findViewById(R.id.main_viewpager)
        mVPAdapter = MainVPAdapter(this)
    }

    private fun initEvent() {
        //滑動View Pager改變底部導航狀態
        mainViewPager.adapter = mVPAdapter
        mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    MainVPAdapter.HOME -> bottomNav.selectedItemId = R.id.bottom_nav_main
                    MainVPAdapter.RECOMMEND -> bottomNav.selectedItemId = R.id.bottom_nav_recommend
                    MainVPAdapter.CART -> bottomNav.selectedItemId = R.id.bottom_nav_cart
                    MainVPAdapter.USER -> bottomNav.selectedItemId = R.id.bottom_nav_user
                }
            }
        })

        //點擊底部導航使View Pager改變頁面
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_nav_main -> mainViewPager.currentItem = MainVPAdapter.HOME
                R.id.bottom_nav_recommend -> mainViewPager.currentItem = MainVPAdapter.RECOMMEND
                R.id.bottom_nav_cart -> mainViewPager.currentItem = MainVPAdapter.CART
                R.id.bottom_nav_user -> mainViewPager.currentItem = MainVPAdapter.USER
            }
            true
        }
    }
}