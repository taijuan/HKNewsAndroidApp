package com.chinadaily.activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.chinadaily.R
import com.chinadaily.adapter.VideoAdapter
import com.chinadaily.base.BaseApp
import com.chinadaily.base.BaseAppActivity
import com.chinadaily.data.NewsBean
import com.chinadaily.googleCast.GoogleServiceUtils.loadAndPlay
import com.chinadaily.http.BaseRes
import com.chinadaily.http.FastJsonCallback
import com.chinadaily.http.HttpConstants
import com.chinadaily.http.httpGet
import com.chinadaily.utils.*
import com.google.android.gms.cast.framework.CastButtonFactory
import com.lzy.okgo.utils.OkLogger
import com.share.ShareDialog
import com.share.runBackground
import kotlinx.android.synthetic.main.activity_video_play_detail_layout.*
import org.litepal.Operator
import java.io.File
import java.net.URL

class VideoMDPlayerDetailsActivity : BaseAppActivity(), View.OnClickListener {

    private lateinit var url: String
    private lateinit var adapter: VideoAdapter
    private lateinit var id: String
    private lateinit var data: NewsBean
    private val saveGoodFastJsonCallback: FastJsonCallback<BaseRes> by lazy {
        object : FastJsonCallback<BaseRes>(lifecycle) {
            override fun onSuccess(body: BaseRes) {
                if (body.resCode == 200) {
                    iv_good.setImageResource(R.drawable.like_selected)
                    iv_good.isEnabled = false
                }
            }

            override fun onError(exception: String) {
            }
        }.apply {
            url = "${HttpConstants.STATIC_URL}like?newsId=${data.dataId}"
            clazz = object : TypeReference<BaseRes>() {}
            pauseBaseRes = false
        }
    }
    private val latestFastJsonCallback: FastJsonCallback<JSONObject> by lazy {
        object : FastJsonCallback<JSONObject>(lifecycle) {
            override fun onSuccess(body: JSONObject) {
                val data = JSON.parseArray(body.getString("dateList"), NewsBean::class.java)
                adapter.refreshData(data)
                adapter.notifyDataSetChanged()
            }

            override fun onError(exception: String) {
            }
        }.apply {
            url = "${HttpConstants.STATIC_URL}selectNewsList?subjectCode=${data.subjectCode}&currentPage=1&dataType=3"
            clazz = object : TypeReference<JSONObject>() {}
        }
    }

    private val fastJsonCallback: FastJsonCallback<NewsBean> by lazy {
        object : FastJsonCallback<NewsBean>(lifecycle) {
            override fun onSuccess(body: NewsBean) {
                this@VideoMDPlayerDetailsActivity.data = body
                videoContent.visibility = View.VISIBLE
                GlideApp.with(this@VideoMDPlayerDetailsActivity).load(HttpConstants.SERVICE_URL + data.bigTitleImage).fitCenter().into(view_super_player.thumbImageView)
                Log.e("zuiweng", HttpConstants.SERVICE_URL + data.bigTitleImage)
                if (Operator.isExist(NewsBean::class.java, "dataId = ${data.dataId}")) {
                    iv_like.setImageResource(R.drawable.favorite_selected)
                    iv_like.isClickable = false
                } else {
                    iv_like.setImageResource(R.drawable.favorite)
                }
                setYoutubeUrl("https://www.youtube.com/watch?v=" + body.ytbUrl, body.txyUrl)
                loadingRetry.visibility = View.GONE
                loadingProgressBar.visibility = View.GONE
                adapter.refreshData(body)
                httpGet(latestFastJsonCallback)
            }

            override fun onError(exception: String) {
                videoContent.visibility = View.GONE
                loadingProgressBar.visibility = View.GONE
                loadingRetry.visibility = View.VISIBLE
            }
        }.apply {
            url = "${HttpConstants.STATIC_URL}selecNewsDetail?dataId=$id"
            clazz = object : TypeReference<NewsBean>() {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play_detail_layout)
        id = intent.getStringExtra("id")
        initPlayerView()
        iv_like.setOnClickListener(this)
        iv_good.setOnClickListener(this)
        iv_share.setOnClickListener(this)
        loadingRetry.setOnClickListener(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = VideoAdapter(lifecycle)
        recyclerView.adapter = adapter
        httpGet(fastJsonCallback)
        CastButtonFactory.setUpMediaRouteButton(applicationContext, mediaRouteButton)
        mediaRouteButtonClick.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Jzvd.goOnPlayOnPause()
                    loadAndPlay(data.title, "", listOf(HttpConstants.SERVICE_URL + data.bigTitleImage), url)
                }
            }
            true
        }
    }


    private fun setYoutubeUrl(pathUrl: String, txyUrl: String) {
        runBackground(lifecycle) {
            try {
                val urlPath = URL(pathUrl)
                val map = YouTubeParser.h264videosWithYoutubeURL(urlPath)
                url = map?.getValue("medium") ?: ""
                if (TextUtils.isEmpty(url)) {
                    url = txyUrl
                }
            } catch (e: Exception) {
                url = txyUrl
            } finally {
                runOnUiThread {
                    OkLogger.e(pathUrl)
                    OkLogger.e(if (TextUtils.isEmpty(txyUrl)) "txyUrl  is  null" else txyUrl)
                    OkLogger.e(url)
                    Jzvd.clearSavedProgress(BaseApp.getInstance(), url)
                    view_super_player.setUp(url, data.title, JzvdStd.SCREEN_NORMAL, JZExoPlayer::class.java)
                    view_super_player.startVideo()
                    view_super_player.isEnabled = true
                    view_super_player.backButton.visibility = View.VISIBLE
                    view_super_player.backButton.contentDescription = "back"
                    view_super_player.backButton.setOnClickListener {
                        onBackPressed()
                    }
                    mediaRouteButtonRoot.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun likeData() {
        if (!Operator.isExist(NewsBean::class.java, "dataId=${data.dataId}")) {//存在
            data.also { it.dataType = "3" }.saveOrUpdateAsync("dataId = " + data.dataId).listen {
                iv_like.setImageResource(R.drawable.favorite_selected)
                iv_like.isClickable = false
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.iv_like -> likeData()
            R.id.iv_good -> httpGet(saveGoodFastJsonCallback)
            R.id.loadingRetry -> {
                videoContent.visibility = View.GONE
                loadingProgressBar.visibility = View.VISIBLE
                loadingRetry.visibility = View.GONE
                httpGet(fastJsonCallback)
            }
            R.id.iv_share -> {
                GlideApp.with(this@VideoMDPlayerDetailsActivity)
                        .asFile()
                        .load(HttpConstants.SERVICE_URL + data.bigTitleImage)
                        .error(R.drawable.logo)
                        .override(160, 90)
                        .into(object : SimpleTarget<File>() {
                            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                                ShareDialog(this@VideoMDPlayerDetailsActivity, data.title, data.description, resource, "https://www.chinadailyhk.com${data.htmlUrl}?newsId=${data.dataId}&play_url=${data.jsonUrl}").show()
                            }
                        })
            }
        }
    }

    override fun onPause() {
        Jzvd.resetAllVideos()
        super.onPause()
    }


    override fun onDestroy() {
        super.onDestroy()
        try {

        } catch (e: Exception) {
        }
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    private fun initPlayerView() {
        view_super_player_root.layoutParams = LinearLayoutCompat.LayoutParams(screenWidth(), screenWidth() * 9 / 16)
        view_super_player.layoutParams = LinearLayoutCompat.LayoutParams(screenWidth(), screenWidth() * 9 / 16)
        view_super_player.mRetryLayout.visibility = View.GONE
        view_super_player.startButton.visibility = View.GONE
        view_super_player.loadingProgressBar.visibility = View.VISIBLE
        view_super_player.batteryTimeLayout.visibility = View.GONE
        view_super_player.titleTextView.setSingleLine()
        view_super_player.isEnabled = false
        view_super_player.thumbImageView.setOnClickListener(null)
        view_super_player.backButton.visibility = View.VISIBLE
        view_super_player.backButton.setOnClickListener {
            onBackPressed()
        }
        setBackAndTitle()
    }

    private fun setBackAndTitle() {
        view_super_player.findViewById<RelativeLayout>(R.id.layout_top).apply {
            layoutParams.height = dp2px(24f) + topHeight()
            layoutParams = layoutParams
        }
        view_super_player.backButton.setVerticalCenter()
        view_super_player.titleTextView.setVerticalCenter()
        view_super_player.batteryTimeLayout.setVerticalCenter()
    }

    private fun View.setVerticalCenter() {
        val layoutParams = this.layoutParams
        layoutParams.height = dp2px(24f) + topHeight()
        this.layoutParams = layoutParams
        if (this is TextView) {
            gravity = Gravity.CENTER_VERTICAL
            setSingleLine(true)
        }
        if (this is LinearLayout) {
            gravity = Gravity.CENTER_VERTICAL
        }
        (layoutParams as RelativeLayout.LayoutParams).setMargins(0, 0, 0, 0)
        setPadding(0, topHeight() - if (this is TextView) dp2px(2f) else 0, 0, 0)
        this.layoutParams = layoutParams
    }
}
