package com.chinadaily.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.chinadaily.NewsDetailActivity
import com.chinadaily.R
import com.chinadaily.data.News
import com.chinadaily.http.IMAGE_DNS
import com.chinadaily.utils.*
import kotlinx.android.synthetic.main.item_news.view.*


class NewsAdapter(private val data: MutableList<News>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        0 -> HeaderHolder(parent)
        1 -> NewsHolder(parent)
        else -> FooterHolder(parent)
    }

    override fun getItemCount() = data.size + 2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewsHolder) {
            holder.initData(data[position - 1])
        }
    }

    override fun getItemViewType(position: Int) = when (position) {
        0 -> 0
        data.size + 1 -> 2
        else -> 1
    }
}

class NewsHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false).apply {
    layoutParams = ViewGroup.LayoutParams(-1, screenHeight() - 2 * headerOrFooterHeight() - 2 * dp2px(4f))
    if (isScreenRound()) {
        setPadding(headerOrFooterHeight(), 0, headerOrFooterHeight(), 0)
    } else {
        setPadding(dp2px(4f), 0, dp2px(4f), 0)
    }
    image.layoutParams = FrameLayout.LayoutParams(-1, -1)
}) {
    private lateinit var data: News
    fun initData(data: News) {
        this.data = data
        itemView.title.text = data.title
        GlideApp.with(itemView.image)
                .load("$IMAGE_DNS${data.bigTitleImage}")
                .placeholder(R.drawable.placeholder)
                .transform(CenterCropAndRound())
                .into(itemView.image)
    }

    init {
        itemView.setOnClickListener {
            itemView.context.startActivity(Intent(itemView.context, NewsDetailActivity::class.java).apply {
                this.putExtra("json", this@NewsHolder.data.jsonUrl)
            })
        }
    }
}

