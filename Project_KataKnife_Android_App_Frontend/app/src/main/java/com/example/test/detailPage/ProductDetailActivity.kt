package com.example.test.detailPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.test.R
import com.example.test.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //填入商品資料頁
        val fm = supportFragmentManager
        fm.beginTransaction().setReorderingAllowed(true)
            .add(R.id.detail_fragment, ProductDetailFragment.newInstance(), "Detail")
            .commit()
    }
}