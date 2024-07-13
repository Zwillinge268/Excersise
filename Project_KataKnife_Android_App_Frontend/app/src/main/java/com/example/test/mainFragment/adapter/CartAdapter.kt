package com.example.test.mainFragment.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.test.detailPage.ProductDetailActivity
import com.example.test.R
import com.example.test.db.HomeDbHelper
import com.example.test.mainFragment.CartFragment.Companion.mCart
import com.example.test.module.Cart
import com.example.test.util.OnlineDbUtil

class CartAdapter(
    private val mContext: Context,
    private val cartInterface: CartInterface
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private val mDbHelper: HomeDbHelper = HomeDbHelper.getInstance(mContext)!!

    //尋找 Item 控件
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImage: ImageView = itemView.findViewById(R.id.cart_image)
        val mName: TextView = itemView.findViewById(R.id.cart_product_name)
        val mPrice: TextView = itemView.findViewById(R.id.cart_product_price)
        val mQty: TextView = itemView.findViewById(R.id.cart_product_qty)
        val mItemCheck: CheckBox = itemView.findViewById(R.id.cart_check)
        val mPlusImage: ImageView = itemView.findViewById(R.id.cart_product_plus)
        val mMinusImage: ImageView = itemView.findViewById(R.id.cart_product_minus)
    }

    //渲染畫面
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mCart.size
    }

    //項目綁定
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = mCart[position]

        holder.mImage.load(product.productImage)
        holder.mName.text = product.productName
        holder.mPrice.text = product.productPrice
        holder.mQty.text = product.productQty
        holder.mItemCheck.isChecked = product.checked

        //控制check box
        holder.mItemCheck.setOnClickListener {
            product.checked = holder.mItemCheck.isChecked
            //檢測是否all checked並調用Fragment function
            if (mCart.all { it.checked }) cartInterface.isAllCheck(true)
            else cartInterface.isAllCheck(false)
        }

        //加減購物車商品數量
        holder.mPlusImage.setOnClickListener {
            cartQtyChange(product, true, holder, position)
        }
        holder.mMinusImage.setOnClickListener {
            cartQtyChange(product, false, holder, position)
        }

        //點擊跳轉
        holder.itemView.setOnClickListener {
            val dataList = OnlineDbUtil.queryProduct(null, null, null, null, product.productId)
            if (!dataList.isNullOrEmpty()) {
                val intent = Intent(mContext, ProductDetailActivity::class.java)
                intent.putExtra("List", dataList[0])
                mContext.startActivity(intent)
            } else {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.cart_missing_product),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    //加減數量
    private fun cartQtyChange(product: Cart, state: Boolean, holder: ViewHolder, position: Int) {
        var qty = mCart[position].productQty.toInt()

        if (state) qty++ else qty--
        //超過數量就不做更改
        if (qty > 99 || qty < 1) {
            Toast.makeText(mContext, mContext.getString(R.string.cart_qty_limit), Toast.LENGTH_LONG)
                .show()
        } else {
            if (mDbHelper.cartQtyChange(product.productId, qty.toString()) == 1) {
                mCart[position].productQty = qty.toString()
                notifyItemChanged(position)
            }
        }
    }

    //如果Cart頁面點擊“選擇全部”，則全部 mCart item set checked=true，反之亦然
    fun allCheck(isChecked: Boolean) {
        for (item in mCart) item.checked = isChecked
        notifyItemRangeChanged(0, mCart.size)
    }

    //監聽界面，用作調用 Fragment 的 function
    interface CartInterface {
        fun isAllCheck(checked: Boolean)
        fun showNullMessage()
    }

    fun cartDelete() {
        //將所有要刪除的數據加到一個 List
        val delList: MutableList<Cart> = mutableListOf()
        for (item in mCart) if (item.checked) delList.add(item)

        if (delList.size == 0) Toast.makeText(
            mContext,
            mContext.getString(R.string.cart_empty_delete), Toast.LENGTH_SHORT
        )
            .show()
        else {
            //執行資料庫刪除
            var count = 0
            for (delItem in delList) count += mDbHelper.cartDelItem(delItem)

            //執行更新數據及畫面
            mCart = mDbHelper.cartShowAll()
            refresh()
            if (count == delList.size) {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.cart_delete_message, count), Toast.LENGTH_SHORT
                ).show()
            } else Toast.makeText(
                mContext,
                mContext.getString(R.string.cart_delete_error), Toast.LENGTH_SHORT
            ).show()
        }
    }

    //列表更新
    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        notifyDataSetChanged()
        cartInterface.showNullMessage()
    }
}