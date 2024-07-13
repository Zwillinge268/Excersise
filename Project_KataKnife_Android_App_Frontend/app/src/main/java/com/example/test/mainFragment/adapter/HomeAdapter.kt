package com.example.test.mainFragment.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.test.MainActivity
import com.example.test.detailPage.ProductDetailActivity
import com.example.test.R
import com.example.test.detailPage.adapter.ProductDetailBannerAdapter
import com.example.test.module.Product
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.indicator.enums.IndicatorSlideMode

//item type
private const val TYPE_HEADER = 0
private const val TYPE_ITEM = 1

class HomeAdapter(
    private val mContext: Context,
    private var mProduct: MutableList<Product>,
    private val owner: Lifecycle
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //渲染畫面
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //鑒別使用哪個 View Holder
        return if (viewType == TYPE_HEADER) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.home_banner, parent, false)
            VHHeader(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
            VHItem(view)
        }
    }

    //使用父類 View Holder (RecyclerView.ViewHolder)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //鑒別 holder 類型
        if (holder is VHItem) {
            val product = getItem(position)
            holder.mImage.load(product.productImage)
            holder.mName.text = product.productName
            holder.mType.text = product.productType
            holder.mPrice.text = product.productPrice

            holder.itemView.setOnClickListener {
                val intent = Intent(mContext, ProductDetailActivity::class.java)
                intent.putExtra("List", product)
                mContext.startActivity(intent)
            }
        } else if (holder is VHHeader) {
            val list = mutableListOf<String>()
            for (post in MainActivity.POST_DATA) list.add(post.postImg)

            holder.mBanner.apply {
                this.adapter = ProductDetailBannerAdapter(true)
                registerLifecycleObserver(owner)
                setIndicatorSlideMode(IndicatorSlideMode.SCALE)
                setIndicatorSliderColor(
                    mContext.getColor(R.color.dark_gray),
                    mContext.getColor(R.color.beige_300)
                )
            }.create(list as List<String>)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == TYPE_HEADER) TYPE_HEADER else TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return mProduct.size + 1
    }

    private fun getItem(position: Int): Product {
        return mProduct[position - 1]
    }

    //列表更新
    fun refresh(newList: MutableList<Product>) {
        mProduct = newList
        notifyItemRangeChanged(1, itemCount - 1)
    }

    //構建兩種 View Holder
    class VHHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBanner: BannerViewPager<Any> = itemView.findViewById(R.id.home_banner)
    }

    class VHItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImage: ImageView = itemView.findViewById(R.id.home_product_image)
        val mName: TextView = itemView.findViewById(R.id.home_product_name)
        val mType: TextView = itemView.findViewById(R.id.home_product_type)
        val mPrice: TextView = itemView.findViewById(R.id.home_product_price)
    }
}