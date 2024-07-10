package com.example.test.mainFragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.load
import com.example.test.MainActivity
import com.example.test.databinding.ActivityRecommendDetailBinding

class RecommendDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
    }

    //Set data from temp data (POST_DATA)
    private fun initData() {
        val position = intent.getIntExtra("position", 0)
        binding.recommendDetailImg.load(MainActivity.POST_DATA[position].postImg)
        binding.recommendDetailTitle.text = MainActivity.POST_DATA[position].postTitle
        binding.recommendDetailContent.text = MainActivity.POST_DATA[position].postContent
        binding.recommendDetailDate.text = MainActivity.POST_DATA[position].postData
    }
}