package com.example.test.detailPage.adapter

import coil.load
import com.example.test.R
import com.otaliastudios.zoom.ZoomImageView
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

//Create Base Banner Adapter
class ProductDetailImageAdapter : BaseBannerAdapter<Any>() {
    //設定控件
    override fun bindData(holder: BaseViewHolder<Any>?, data: Any?, position: Int, pageSize: Int) {
        val zoomImageView: ZoomImageView = holder!!.findViewById(R.id.zoom_image)
        zoomImageView.load(data)
    }

    //設定Layout
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.product_detail_image_item
    }
}