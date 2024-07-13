package com.example.test.detailPage.adapter

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import coil.load
import com.example.test.R
import com.example.test.mainFragment.RecommendDetailActivity
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

//Create Base Banner Adapter for both home page and product detail page
class ProductDetailBannerAdapter(private val isHome: Boolean = false, private val mContext: Context? = null) : BaseBannerAdapter<Any>() {
    //設定控件
    override fun bindData(holder: BaseViewHolder<Any>?, data: Any?, position: Int, pageSize: Int) {
        val imageView: ImageView = holder!!.findViewById(R.id.banner_image)
        imageView.load(data)
        if (isHome) {
            holder.itemView.setOnClickListener {
                val intent = Intent(mContext, RecommendDetailActivity::class.java)
                intent.putExtra("position", position)
                mContext?.startActivity(intent)
            }
        }
    }

    //設定Layout
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.product_detail_banner_item
    }
}