package com.chinadaily.adapter

import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v4.util.Pools
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.LinearLayoutCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.chinadaily.R
import com.chinadaily.activity.WebActivity
import com.chinadaily.data.CardItem
import com.chinadaily.http.FastJsonCallback
import com.chinadaily.http.HttpConstants
import com.chinadaily.http.cancel
import com.chinadaily.http.httpGet
import com.chinadaily.utils.DeviceUtils
import com.chinadaily.utils.GlideApp
import com.chinadaily.utils.SPUtils
import kotlinx.android.synthetic.main.adapter_e_paper.view.*

class EPaperAdapter(var lifecycle: Lifecycle) : PagerAdapter() {
    private val data: MutableList<CardItem> = mutableListOf()
    private val pool: Pools.SynchronizedPool<EPaperView> = Pools.SynchronizedPool(5)
    fun refreshData(data: List<CardItem>) {
        this.data.clear()
        this.data.addAll(data)
    }

    override fun isViewFromObject(view: View, any: Any) = view === any

    override fun getCount() = data.size
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = pool.acquire() ?: EPaperView(container, lifecycle)
        container.addView(itemView)
        itemView.refreshData(data[position])
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        val itemView = any as EPaperView
        container.removeView(itemView)
        pool.release(itemView)
    }
}

class EPaperView(parent: ViewGroup, lifecycle: Lifecycle) : FrameLayout(parent.context) {
    private lateinit var data: CardItem
    fun refreshData(data: CardItem) {
        this.data = data
        name.text = ""
        image.setImageResource(R.drawable.default_epaper_pic)
        param = data.publicationCode + "/" + strFormat(data.pubDate) + "/issue.json"
        tag?.let { cancel(it) }
        tag = "${HttpConstants.E_PAPER_SERVICE}pubs/$param"
        httpGet(fastJsonCallback.also {
            it.url = "${HttpConstants.E_PAPER_SERVICE}pubs/$param"
        })
    }

    private lateinit var param: String
    private val fastJsonCallback by lazy {
        object : FastJsonCallback<JSONObject>(lifecycle) {
            override fun onSuccess(body: JSONObject) {
                val jsonArray = body.getJSONArray("data")
                val jsonObject1 = jsonArray.getJSONObject(0)
                val imgUrl = HttpConstants.E_PAPER_SERVICE + "pubs" + jsonObject1.getString("snapshotBigUrl")
                SPUtils.put(param, imgUrl)
                showImage()
            }

            override fun onError(exception: String) {
                showImage()
            }
        }.apply {
            clazz = object : TypeReference<JSONObject>() {}
            pauseBaseRes = false
        }
    }


    init {
        addView(LayoutInflater.from(parent.context).inflate(R.layout.adapter_e_paper, parent, false))
        setOnClickListener {
            val url = HttpConstants.E_PAPER_SERVICE + "mobile/index.html?pubCode=" + data.publicationCode + "&pubDate=" + data.pubDate
            val intent = Intent(it.context, WebActivity::class.java)
            intent.putExtra("data", url)
            intent.putExtra("title", "ePaper")
            it.context.startActivity(intent)
        }
    }

    private fun strFormat(date: String): String? {
        var newDate: String? = null
        if (date.contains("-")) {
            newDate = date.replace("-", "/")
        }
        return newDate
    }

    private val target by lazy {
        object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                image.setImageDrawable(resource)
                image.layoutParams = LinearLayoutCompat.LayoutParams(-1, resource.intrinsicHeight)
                name.visibility = View.VISIBLE
                name.text = data.publicationName
                image.contentDescription = data.publicationName
            }
        }
    }

    private fun showImage() {
        val url = SPUtils.getString(param, "")
        if (TextUtils.isEmpty(url)) {
            return
        }
        GlideApp.with(image).clear(target)
        val parent = image.parent as ViewGroup
        GlideApp.with(image)
                .load(url)
                .fitCenter()
                .override(image.measuredWidth, parent.measuredHeight - DeviceUtils.dp2px(36f))
                .into<SimpleTarget<Drawable>>(target)
    }
}
