package com.chinadaily.adapter

import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.chinadaily.R
import com.chinadaily.activity.VideoMDPlayerDetailsActivity
import com.chinadaily.data.NewsBean
import com.chinadaily.http.FastJsonCallback
import com.chinadaily.http.HttpConstants
import com.chinadaily.http.httpGet
import com.chinadaily.utils.GlideApp
import kotlinx.android.synthetic.main.adapter_video.view.*

class VideoAdapter(var lifecycle: Lifecycle) : RecyclerView.Adapter<VideoHolder>() {

    private var data = mutableListOf<NewsBean>()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        return VideoHolder(parent, lifecycle)
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        holder.refreshData(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class VideoHolder(var parent: ViewGroup, var lifecycle: Lifecycle) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_video, parent, false)) {
    private lateinit var data: NewsBean

    fun refreshData(data: NewsBean) {
        this.data = data
        itemView.contentDescription = data.title
        if (!TextUtils.isEmpty(data.subjectName)) {
            itemView.video_type.visibility = View.VISIBLE
            itemView.video_type.typeface = TF2
            itemView.video_type.text = data.subjectName
            itemView.video_type.isClickable = true
        } else {
            itemView.video_type.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(data.title)) {
            itemView.video_title.visibility = View.VISIBLE
            itemView.video_title.typeface = TF1
            itemView.video_title.text = data.title
            itemView.video_title.isClickable = true
        } else {
            itemView.video_title.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(data.publishTime)) {
            itemView.video_time.visibility = View.VISIBLE
            itemView.video_time.background = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(Color.TRANSPARENT, Color.parseColor("#cc000000")))
            itemView.video_time.typeface = TF2
            itemView.video_time.text = data.publishTime
            itemView.video_time.isClickable = true
            itemView.video_time.contentDescription = "video publish time ${data.publishTime}"
        } else {
            itemView.video_time.visibility = View.GONE
        }
        itemView.video_likes.text = ""
        itemView.video_likes.tag = data.dataId
        httpGet(fastJsonCallback.also {
            it.url = "${HttpConstants.STATIC_URL}searchLike?newsId=${data.dataId}"
        })
        val imgUrl = HttpConstants.SERVICE_URL + data.bigTitleImage
        GlideApp.with(itemView)
                .load(imgUrl)
                .placeholder(R.drawable.news_big_default)
                .error(R.drawable.news_big_default)
                .centerCrop()
                .into(itemView.video_image)
    }

    init {
        itemView.isClickable = true
        itemView.setOnClickListener {
            it.context.startActivity(Intent(it.context, VideoMDPlayerDetailsActivity::class.java).apply {
                putExtra("id", this@VideoHolder.data.dataId)
            })
        }
    }

    private val fastJsonCallback by lazy {
        object : FastJsonCallback<JSONObject>(lifecycle) {
            override fun onSuccess(body: JSONObject) {
                val newsId = body.getString("newsId")
                if (itemView.video_likes.tag != newsId) {
                    return
                }
                val goodCount = body.getInteger("count")
                itemView.video_likes.text = when (goodCount) {
                    0 -> ""
                    1 -> "$goodCount like"
                    else -> "$goodCount likes"
                }
            }

            override fun onError(exception: String) {
            }
        }.apply {
            clazz = object : TypeReference<JSONObject>() {}
        }
    }
}
