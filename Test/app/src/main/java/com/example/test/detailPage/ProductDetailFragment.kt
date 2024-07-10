package com.example.test.detailPage

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import coil.load
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.databinding.FragmentProductDetailBinding
import com.example.test.db.HomeDbHelper
import com.example.test.detailPage.adapter.ProductDetailBannerAdapter
import com.example.test.module.Cart
import com.example.test.module.Product
import com.zhpan.indicator.enums.IndicatorSlideMode

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding: FragmentProductDetailBinding get() = _binding!!

    private lateinit var dbHelper: HomeDbHelper
    private lateinit var dataList: Product

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        binding.detailAdd.setOnClickListener { addCart() }
        binding.detailPlus.setOnClickListener { setQty(true) }
        binding.detailMinus.setOnClickListener { setQty(false) }

        //Scroll to top of the page
        binding.detailFloatButton.setOnClickListener {
            binding.detailScroll.smoothScrollTo(0, 0)
        }

        //Back button
        binding.detailImageBack.greyBackButton.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun initView() {
        dbHelper = HomeDbHelper.getInstance(requireContext())!!

        //取得商品資料
        dataList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //新式反序列化，需求SDK 33 (Android 13) 以上
            requireActivity().intent.getSerializableExtra("List", Product::class.java)!!
        } else {
            requireActivity().intent.getSerializableExtra("List") as Product
        }

        //setting data from intent
        binding.detailPrice.text = dataList.productPrice
        binding.detailTitle.text = dataList.productName
        binding.detailDetail.text = dataList.productContent

        //分割圖片URL List
        val detailImgList = dataList.productDetailImage.let {
            it.trim()
            it.split(',')
        }

        //Banner Setting
        binding.detailVp.apply {
            this.adapter = ProductDetailBannerAdapter()
            registerLifecycleObserver(lifecycle)
            setCanLoop(false)
            setAutoPlay(false)
            setIndicatorSlideMode(IndicatorSlideMode.SCALE)
            setIndicatorSliderColor(
                requireContext().getColor(R.color.dark_gray),
                requireContext().getColor(R.color.beige_300)
            )
            //open a image viewer when clicked
            setOnPageClickListener { _, position ->
                //open a zoomable image view
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    )
                    setReorderingAllowed(true)
                    add(
                        R.id.detail_fragment,
                        ImageViewFragment.newInstance(position, detailImgList)
                    )
                    hide(this@ProductDetailFragment)
                    addToBackStack(null)
                    commit()
                }
            }
        }.create(detailImgList)

        //建立詳細介紹圖片顯示
        for (imgUrl in detailImgList) {
            val img = ImageView(requireContext()).apply {
                load(imgUrl)
                adjustViewBounds = true
                maxHeight = 1000
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            binding.detailRV.addView(img)
        }
    }

    //加減數量
    private fun setQty(state: Boolean) {
        var qty = binding.detailProductQty.text.toString().toInt()
        if (state) qty++ else qty--
        //超過數量就不做更改
        if (qty > 99 || qty < 1) {
            Toast.makeText(requireContext(), getString(R.string.cart_qty_limit), Toast.LENGTH_LONG)
                .show()
        } else {
            binding.detailProductQty.text = qty.toString()
        }
    }

    //加入購物車
    private fun addCart() {
        if (MainActivity.USER_DATA.isLogin) {
            dbHelper.cartInsert(
                Cart(
                    dataList.productId,
                    dataList.productName,
                    dataList.productPrice,
                    dataList.productImage,
                    binding.detailProductQty.text.toString()
                )
            )
            Toast.makeText(requireContext(),
                getString(R.string.cart_add_success), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.cart_login_first), Toast.LENGTH_SHORT).show()
        }
    }

    //釋放記憶體
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProductDetailFragment()
    }
}