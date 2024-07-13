package com.example.test.mainFragment.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.mainFragment.RecommendDetailActivity
import com.example.test.module.Post
import com.google.android.material.imageview.ShapeableImageView

class RecommendAdapter(
    private val mContext: Context,
    private val mPost: MutableList<Post> = MainActivity.POST_DATA
) : RecyclerView.Adapter<RecommendAdapter.ViewHolder>() {

    //Init View
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ShapeableImageView = itemView.findViewById(R.id.recommend_image)
        val postTitle: TextView = itemView.findViewById(R.id.recommend_title)
        val postDate: TextView = itemView.findViewById(R.id.recommend_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recommend_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    //Setting element
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.load(mPost[position].postImg)
        holder.postTitle.text = mPost[position].postTitle
        holder.postDate.text = mPost[position].postData

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, RecommendDetailActivity::class.java)
            intent.putExtra("position", position)
            mContext.startActivity(intent)
        }
    }
}