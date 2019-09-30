package com.chinadaily.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.alibaba.fastjson.JSON
import com.chinadaily.R
import com.chinadaily.activity.NewsDetailActivity
import com.chinadaily.base.BaseApp
import com.chinadaily.data.NewsBean
import com.chinadaily.fragment.HomeNewsFragment
import com.chinadaily.http.HttpConstants
import com.chinadaily.utils.GlideApp
import com.chinadaily.utils.dp2px
import com.tmall.ultraviewpager.UltraViewPager
import kotlinx.android.synthetic.main.adapter_news.view.*
import kotlinx.android.synthetic.main.adapter_pager.view.*

val TF1: Typeface by lazy { Typeface.createFromAsset(BaseApp.getInstance().assets, "fonts/Raleway_Bold.ttf") }
val TF2: Typeface by lazy { Typeface.createFromAsset(BaseApp.getInstance().assets, "fonts/OpenSans_Regular.ttf") }
val TF3: Typeface by lazy { Typeface.createFromAsset(BaseApp.getInstance().assets, "fonts/OpenSans_Bold.ttf") }

class NewsAdapter(var context: androidx.fragment.app.Fragment) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private var data = mutableListOf<NewsBean>()
    private var pagerData = mutableListOf<NewsBean>()
    fun refreshPager(data: List<NewsBean>) {
        pagerData.clear()
        pagerData.addAll(data)
    }

    fun refreshData(data: List<NewsBean>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun addData(data: List<NewsBean>) {
        val start = itemCount
        this.data.addAll(data)
        val count = data.size
        notifyItemRangeInserted(start + 1, count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return if (viewType == 0) {
            HeaderHolder(parent, context.lifecycle)
        } else {
            NewsHolder(parent, context.lifecycle, context is HomeNewsFragment)
        }
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderHolder -> {
                holder.refreshData(pagerData)
            }
            is NewsHolder -> {
                holder.refreshData(data[position - pageDataItemCount()])
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size + if (isPageDataEmpty()) 0 else 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && !isPageDataEmpty()) {
            0
        } else {
            1
        }
    }

    private fun pageDataItemCount(): Int {
        return if (isPageDataEmpty()) 0 else 1
    }

    private fun isPageDataEmpty(): Boolean {
        return pagerData.isNullOrEmpty()
    }
}

class HeaderHolder(parent: ViewGroup, var lifecycle: Lifecycle) : androidx.recyclerview.widget.RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_pager, parent, false)) {
    fun refreshData(data: MutableList<NewsBean>) {
        adapter.refreshData(data)
        itemView.ultra_viewpager.adapter = adapter
    }

    private val adapter by lazy { HeaderPagerAdapter() }

    init {
        itemView.ultra_viewpager.setRatio(16 / 9f)
        itemView.ultra_viewpager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        itemView.ultra_viewpager.initIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setMargin(0, 0, dp2px(16f), dp2px(8f))
                .setFocusColor(Color.WHITE)
                .setNormalColor(Color.LTGRAY)
                .setRadius(dp2px(6f))
                .setGravity(Gravity.END or Gravity.BOTTOM)
                .build()
        itemView.ultra_viewpager.setInfiniteLoop(true)
        itemView.ultra_viewpager.setAutoScroll(5000)
    }
}

class NewsHolder(parent: ViewGroup, val lifecycle: Lifecycle, private val show: Boolean = false) : androidx.recyclerview.widget.RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_news, parent, false)) {
    private lateinit var data: NewsBean
    fun refreshData(data: NewsBean) {
        this.data = data
        itemView.title.typeface = TF1
        if (!TextUtils.isEmpty(data.title)) {
            itemView.title.visibility = View.VISIBLE
            itemView.title.text = data.title
        } else {
            itemView.title.visibility = View.GONE
        }
        itemView.desc.typeface = TF2
        if (!TextUtils.isEmpty(data.description)) {
            itemView.desc.visibility = View.VISIBLE
            itemView.desc.text = data.description
        } else {
            itemView.desc.visibility = View.GONE
        }
        itemView.time.typeface = TF3
        if (!TextUtils.isEmpty(data.publishTime)) {
            itemView.time.visibility = View.VISIBLE
            itemView.time.text = data.publishTime.toUpperCase()
        } else {
            itemView.time.visibility = View.GONE
        }
        itemView.tagName.visibility = if (show) View.VISIBLE else View.GONE
        itemView.tagName.text = data.subjectName
        val imgUrl = HttpConstants.SERVICE_URL + data.bigTitleImage
        GlideApp.with(itemView.image)
                .load(imgUrl)
                .placeholder(R.drawable.news_big_default)
                .error(R.drawable.news_big_default)
                .centerCrop()
                .into(itemView.image)
    }

    init {
        itemView.setOnClickListener {
            it.context.startActivity(Intent(it.context, NewsDetailActivity::class.java).apply {
                putExtra("bean", JSON.toJSONString(this@NewsHolder.data))
            })
        }
    }
}
