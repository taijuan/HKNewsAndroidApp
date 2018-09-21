package com.chinadaily.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v4.util.Pools
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.fastjson.JSON
import com.bumptech.glide.Priority
import com.chinadaily.R
import com.chinadaily.activity.NewsDetailActivity
import com.chinadaily.data.NewsBean
import com.chinadaily.http.HttpConstants
import com.chinadaily.utils.GlideApp
import kotlinx.android.synthetic.main.adapter_view_pager.view.*


class HeaderPagerAdapter : PagerAdapter() {
    private val images = mutableListOf<NewsBean>()
    private val background by lazy { GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(Color.TRANSPARENT, Color.parseColor("#cc000000"))) }
    fun refreshData(data: List<NewsBean>) {
        this.images.clear()
        this.images.addAll(data)
    }

    private val pool: Pools.SynchronizedPool<View> = Pools.SynchronizedPool(5)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = pool.acquire() ?: initView(container)
        val data = images[position]
        itemView.tag = JSON.toJSONString(data)
        itemView.pagerTitle.text = data.title
        itemView.contentDescription = data.title
        val imgUrl = HttpConstants.SERVICE_URL + data.bigTitleImage
        GlideApp.with(itemView.pagerImg)
                .load(imgUrl)
                .priority(Priority.HIGH)
                .placeholder(R.drawable.news_big_default)
                .error(R.drawable.news_big_default)
                .centerCrop()
                .into(itemView.pagerImg)
        container.addView(itemView)
        return itemView
    }

    private fun initView(container: ViewGroup): View {
        return LayoutInflater.from(container.context).inflate(R.layout.adapter_view_pager, container, false).apply {
            pagerTitle.typeface = TF1
            pagerTitle.background = this@HeaderPagerAdapter.background
            isClickable = true
            setOnClickListener {
                context.startActivity(Intent(it.context, NewsDetailActivity::class.java).apply {
                    putExtra("bean", tag as String)
                })
            }
        }
    }

    override fun getCount(): Int = images.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object`

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        val itemView = any as View
        container.removeView(itemView)
        pool.release(itemView)
    }
}
