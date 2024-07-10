package com.example.test.mainFragment.searchPage

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.test.R
import com.example.test.detailPage.ProductDetailActivity
import com.example.test.module.Product

class SearchResultAdapter(
    private val mContext: Context,
    private var mProduct: MutableList<Product>
) :
    RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImage: ImageView = itemView.findViewById(R.id.home_product_image)
        val mName: TextView = itemView.findViewById(R.id.home_product_name)
        val mType: TextView = itemView.findViewById(R.id.home_product_type)
        val mPrice: TextView = itemView.findViewById(R.id.home_product_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mProduct.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = mProduct[position]
        holder.mImage.load(product.productImage)
        holder.mName.text = product.productName
        holder.mType.text = product.productType
        holder.mPrice.text = product.productPrice

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, ProductDetailActivity::class.java)
            intent.putExtra("List", product)
            mContext.startActivity(intent)
        }
    }

    fun refresh(newList: MutableList<Product>) {
        mProduct = newList
        notifyItemRangeChanged(0, itemCount - 1)
    }
}