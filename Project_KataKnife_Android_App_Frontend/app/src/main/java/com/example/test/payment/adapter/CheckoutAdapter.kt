package com.example.test.payment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.test.R
import com.example.test.module.Cart

class CheckoutAdapter(
    private var mCart: MutableList<Cart>
) : RecyclerView.Adapter<CheckoutAdapter.ViewHolder>() {

    //Create View Holder
    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val mImage: ImageView = item.findViewById(R.id.checkout_image)
        val mName: TextView = item.findViewById(R.id.checkout_product_name)
        val mPrice: TextView = item.findViewById(R.id.checkout_product_price)
        val mQty: TextView = item.findViewById(R.id.checkout_product_qty)
    }

    //Return View Holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.checkout_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mCart.size
    }

    //Init element in View Holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = mCart[position]

        holder.mImage.load(product.productImage)
        holder.mName.text = product.productName
        holder.mPrice.text = product.productPrice
        holder.mQty.text = product.productQty
    }
}