package com.example.test.mainFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.mainFragment.adapter.CartAdapter
import com.example.test.databinding.FragmentCartBinding
import com.example.test.db.HomeDbHelper
import com.example.test.module.Cart
import com.example.test.payment.CheckoutPageActivity

//引用Adapter的interface讓其可以調用Fragment的重寫方法
class CartFragment : Fragment(), CartAdapter.CartInterface {
    //View Binding
    private var _binding: FragmentCartBinding? = null
    private val binding: FragmentCartBinding get() = _binding!!

    private lateinit var mAdapter: CartAdapter
    private lateinit var mDbHelper: HomeDbHelper

    //onCreate -> onCreateView -> onViewCreated
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
    }

    private fun initEvent() {
        mDbHelper = HomeDbHelper.getInstance(requireContext())!!

        //Recycler View init
        mAdapter = CartAdapter(this.requireContext(), this)
        binding.cartRv.layoutManager = LinearLayoutManager(this.requireContext())
        binding.cartRv.addItemDecoration(
            DividerItemDecoration(
                this.requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        binding.cartRv.adapter = mAdapter

        //Del data listener
        binding.cartDelete.setOnClickListener { delItem() }

        //調用adapter function告知所有 item 轉爲check/uncheck
        binding.cartAll.setOnClickListener { mAdapter.allCheck(binding.cartAll.isChecked) }

        //結賬
        binding.cartCheckout.setOnClickListener {
            if (!MainActivity.USER_DATA.isLogin) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.cart_login_first),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (mCart.all { !it.checked }) {
                Toast.makeText(requireContext(), getString(R.string.cart_empty), Toast.LENGTH_SHORT)
                    .show()
            } else {
                //將有勾選的商品傳遞給 Check out page
                val product = arrayListOf<Cart>()
                for (item in mCart) {
                    if (item.checked) product.add(item)
                }
                val bundle = Bundle()
                bundle.putSerializable("product", product)
                val intent = Intent(requireContext(), CheckoutPageActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //show item and make all checked false
        showCart()
        isAllCheck(false)
    }

    //從Cart DB中取出所有資料
    private fun showCart() {
        mCart = mDbHelper.cartShowAll()
        mAdapter.refresh()
    }

    //重寫方法，用作讓adapter操作fragment方法
    override fun isAllCheck(checked: Boolean) {
        //If all item checked, make the "pick all" box checked too, and vice versa.
        binding.cartAll.isChecked = checked
    }
    override fun showNullMessage() {
        //Show message if cart is null and hide it when cart is not null
        if (mCart.isEmpty()) {
            binding.cartMessage.visibility = View.VISIBLE
        } else binding.cartMessage.visibility = View.GONE
    }

    //Delete product in cart
    private fun delItem() {
        mAdapter.cartDelete()
        isAllCheck(false)
    }

    //釋放記憶體
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        //創建函數
        fun newInstance() = CartFragment()

        var mCart: MutableList<Cart> = mutableListOf()
    }
}