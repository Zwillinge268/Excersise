package com.example.test

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.test.mainFragment.CartFragment
import com.example.test.mainFragment.HomeFragment
import com.example.test.mainFragment.RecommendFragment
import com.example.test.mainFragment.UserFragment

const val NUMBER_OF_FRAGMENT = 4

class MainVPAdapter(view: FragmentActivity) : FragmentStateAdapter(view) {

    //位置命名
    companion object {
        const val HOME = 0
        const val RECOMMEND = 1
        const val CART = 2
        const val USER = 3
    }

    override fun getItemCount(): Int {
        return NUMBER_OF_FRAGMENT
    }

    //根據位置創建頁面（頁面可回收）（此 Function 會在創建及滑動時調用）
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            HOME -> HomeFragment.newInstance()
            RECOMMEND -> RecommendFragment.newInstance()
            CART -> CartFragment.newInstance()
            USER -> UserFragment.newInstance()
            else -> Fragment()
        }
    }
}