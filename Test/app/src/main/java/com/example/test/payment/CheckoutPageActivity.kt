package com.example.test.payment

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.test.ErrorPageActivity
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.databinding.ActivityCheckoutPageBinding
import com.example.test.module.Cart
import com.example.test.payment.adapter.CheckoutAdapter
import java.lang.Exception

class CheckoutPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutPageBinding
    private lateinit var mCheckout: MutableList<Cart>
    private lateinit var mAdapter: CheckoutAdapter

    companion object {
        const val ARRAY_ERROR = 99
        const val UNKNOWN_ERROR = 100

        const val CASH = 0
        const val CREDIT = 1
        var payMethod = MutableLiveData(CASH)  //被觀察資料

        const val DEFAULT = 2
        const val OTHER = 3
        var addressChoose = MutableLiveData(DEFAULT) //被觀察資料
        var address1 = ""
        var address2 = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initEvent()
    }

    private fun initData() {
        val bundle = intent.extras

        //取得序列化信息
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle?.getSerializable("product", ArrayList::class.java)
        } else {
            bundle?.getSerializable("product") as ArrayList<*>
        }

        try {
            //强轉類型警告（類型擦除導致無法判斷）
            mCheckout = data as ArrayList<Cart>
            loadAddress()
            loadProduct()
            loadPayment()
        } catch (_: Exception) {
        } finally {
            //If cant any data
            if (mCheckout.isEmpty()) {
                val intent = Intent(this, ErrorPageActivity::class.java)
                intent.putExtra("Code", ARRAY_ERROR)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun loadProduct() {
        //Adapter setting
        mAdapter = CheckoutAdapter(mCheckout)
        binding.checkoutItemLayout.apply {
            layoutManager = LinearLayoutManager(this@CheckoutPageActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@CheckoutPageActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = mAdapter
        }

        //Price calculate
        var totalPrice = 0
        for (item in mCheckout) totalPrice += (item.productPrice.toInt() * item.productQty.toInt())
        binding.checkoutSummarizePrice.text = totalPrice.toString()
    }

    private fun loadAddress() {
        //Read user data(name and email)
        val fullName = MainActivity.USER_DATA.lastName + MainActivity.USER_DATA.firstName
        binding.checkoutAddressName.text = fullName
        binding.checkoutAddressTel.text = MainActivity.USER_DATA.email

        //Check which address we need to use
        if (addressChoose.value == DEFAULT)
            binding.checkoutAddressAddress.text = MainActivity.USER_DATA.address
        else binding.checkoutAddressAddress.text =
            getString(R.string.double_string_resource, address1, address2)
    }

    private fun loadPayment() {
        //Payment data
        val payList = listOf(
            Triple(CASH, "Cash", R.drawable.baseline_money_24),
            Triple(CREDIT, "Credit Card", R.drawable.baseline_credit_card_24)
        )

        //Show method UI
        binding.checkoutPaymentShow.text =
            getString(R.string.checkout_pay_method, payList[payMethod.value!!].second)
        binding.checkoutPaymentLogo.load(payList[payMethod.value!!].third)
    }

    private fun initEvent() {
        //To other fragment
        binding.checkoutAddressLayout.setOnClickListener {
            toFragment(AddressFragment.newInstance())
        }
        binding.checkoutPaymentLayout.setOnClickListener {
            toFragment(PaymentFragment.newInstance())
        }

        //建立觀察者(與發生變動的行動)，及將被觀察資料與觀察者綁定
        val addressObserver = Observer<Int> { loadAddress() }
        addressChoose.observe(this, addressObserver)
        val methodObserver = Observer<Int> { loadPayment() }
        payMethod.observe(this, methodObserver)

        binding.checkoutSummarizeCheckout.setOnClickListener {
            Toast.makeText(this, getString(R.string.end_of_simulation), Toast.LENGTH_LONG).show()
        }
    }

    private fun toFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .setReorderingAllowed(true)
            .add(R.id.checkout_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}