package com.example.to10apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_items.view.*


class FeedAdapter (private val items:ArrayList<FeedEntry>): RecyclerView.Adapter<FeedAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val tv : TextView = itemView.tvItem


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_items,
            parent,
            false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holderMy: MyViewHolder, position: Int) {
        val title =items[position].title
        holderMy.tv.text = title

    }

    override fun getItemCount(): Int {
        return items.size
    }


}