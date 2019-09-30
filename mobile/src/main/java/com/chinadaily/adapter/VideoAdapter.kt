package com.chinadaily.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.chinadaily.R
import com.chinadaily.activity.VideoMDPlayerDetailsActivity
import com.chinadaily.data.NewsBean
import com.chinadaily.http.HttpConstants
import com.chinadaily.utils.GlideApp
import kotlinx.android.synthetic.main.adapter_video.view.*
import kotlinx.android.synthetic.main.video_detail_header.view.*

class VideoAdapter(var lifecycle: Lifecycle) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = mutableListOf<NewsBean>()
    private var videoData: NewsBean? = null
    fun refreshData(data: List<NewsBean>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun refreshData(videoData: NewsBean) {
        this.videoData = videoData
        notifyDataSetChanged()
    }

    fun addData(data: List<NewsBean>) {
        val start = itemCount
        this.data.addAll(data)
        val count = data.size
        notifyItemRangeInserted(start + 1, count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            VideoHeaderHolder(parent, lifecycle)
        } else {
            VideoHolder(parent, lifecycle)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VideoHeaderHolder -> holder.refreshData(videoData!!)
            is VideoHolder -> holder.refreshData(data[position - if (videoData == null) 0 else 1])
        }
    }

    override fun getItemCount(): Int {
        return data.size + if (videoData == null) 0 else 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (videoData != null && position == 0) 0 else 1
    }
}

class VideoHolder(parent: ViewGroup, var lifecycle: Lifecycle) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_video, parent, false)) {
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

}

class VideoHeaderHolder(parent: ViewGroup, var lifecycle: Lifecycle) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.video_detail_header, parent, false)) {
    private lateinit var data: NewsBean

    fun refreshData(data: NewsBean) {
        this.data = data
        itemView.headerTitle.text = data.title
        itemView.headerDes.text = data.description
        itemView.headerSubject.text = data.subjectName
        itemView.headerTime.text = data.publishTime
    }
}
