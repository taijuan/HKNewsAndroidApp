package com.chinadaily.adapter

import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.chinadaily.data.NewsBean

/**
 * Created by Staff on 2017/6/29.
 */

open class CollectionAdapter(protected var lifecycle: Lifecycle) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
    private var data = mutableListOf<NewsBean>()
    fun getData(): MutableList<NewsBean> {
        return data
    }

    fun refreshData(data: List<NewsBean>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> NewsHolder(parent, lifecycle, true)
            else -> VideoHolder(parent, lifecycle)
        }
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NewsHolder -> holder.refreshData(data[position])
            is VideoHolder -> holder.refreshData(data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun getItemViewType(position: Int): Int {
        val type = data[position].dataType
        return when (type) {
            "3" -> 2
            else -> 1
        }
    }
}
