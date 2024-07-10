package com.example.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.test.databinding.ActivityErrorPageBinding
import com.example.test.payment.CheckoutPageActivity

class ErrorPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityErrorPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErrorPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val errorCode = intent.getIntExtra("Code", CheckoutPageActivity.UNKNOWN_ERROR)
        binding.errorText.text = errorCode.toString()
    }
}