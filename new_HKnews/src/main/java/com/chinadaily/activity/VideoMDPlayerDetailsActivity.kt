package com.chinadaily.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import cn.jzvd.JZVideoPlayer
import cn.jzvd.JZVideoPlayer.*
import cn.jzvd.JZVideoPlayerStandard
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
import com.chinadaily.http.BaseRes
import com.chinadaily.http.FastJsonCallback
import com.chinadaily.http.HttpConstants
import com.chinadaily.http.httpGet
import com.chinadaily.utils.DeviceUtils
import com.chinadaily.utils.GlideApp
import com.chinadaily.utils.JZExoPlayer
import com.chinadaily.utils.YouTubeParser
import com.lzy.okgo.utils.OkLogger
import com.share.ShareDialog
import com.share.runBackground
import kotlinx.android.synthetic.main.activity_video_play_detail_layout.*
import kotlinx.android.synthetic.main.video_detail_header.*
import org.litepal.LitePal
import java.io.File
import java.net.URL

class VideoMDPlayerDetailsActivity : BaseAppActivity(), View.OnClickListener {

    private lateinit var url: String
    private lateinit var adapter: VideoAdapter
    private lateinit var id: String
    private lateinit var data: NewsBean
    private val saveGoodFastJsonCallback by lazy {
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
    private val latestFastJsonCallback by lazy {
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

    private val fastJsonCallback by lazy {
        object : FastJsonCallback<NewsBean>(lifecycle) {
            override fun onSuccess(body: NewsBean) {
                this@VideoMDPlayerDetailsActivity.data = body
                videoContent.visibility = View.VISIBLE
                GlideApp.with(this@VideoMDPlayerDetailsActivity).load(HttpConstants.SERVICE_URL + data.bigTitleImage).fitCenter().into(view_super_player.thumbImageView)
                Log.e("zuiweng", HttpConstants.SERVICE_URL + data.bigTitleImage)
                if (LitePal.isExist(NewsBean::class.java, "dataId = ${data.dataId}")) {
                    iv_like.setImageResource(R.drawable.favorite_selected)
                    iv_like.isClickable = false
                } else {
                    iv_like.setImageResource(R.drawable.favorite)
                }
                setYoutubeUrl("https://www.youtube.com/watch?v=" + body.ytbUrl, body.txyUrl)
                loadingRetry.visibility = View.GONE
                loadingProgressBar.visibility = View.GONE
                headerTitle.text = body.title
                headerDes.text = body.description
                headerSubject.text = body.subjectName
                headerTime.text = body.publishTime
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
        view_super_player.layoutParams = LinearLayoutCompat.LayoutParams(DeviceUtils.width(), DeviceUtils.width() * 9 / 16)
        JZVideoPlayer.setMediaInterface(JZExoPlayer())
        view_super_player.mRetryLayout.visibility = View.GONE
        view_super_player.startButton.visibility = View.GONE
        view_super_player.loadingProgressBar.visibility = View.VISIBLE
        view_super_player.batteryTimeLayout.visibility = View.GONE
        view_super_player.titleTextView.setSingleLine()
        view_super_player.isEnabled = false
        view_super_player.thumbImageView.setOnClickListener(null)
        FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        iv_like.setOnClickListener(this)
        iv_good.setOnClickListener(this)
        iv_share.setOnClickListener(this)
        loadingRetry.setOnClickListener(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = VideoAdapter(lifecycle)
        recyclerView.adapter = adapter
        httpGet(fastJsonCallback)
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
                    clearSavedProgress(BaseApp.getInstance(), url)
                    view_super_player.setUp(url, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, data.title)
                    view_super_player.startVideo()
                    view_super_player.isEnabled = true
                    view_super_player.backButton.visibility = View.VISIBLE
                    view_super_player.backButton.contentDescription = "back"
                    view_super_player.backButton.setOnClickListener {
                        onBackPressed()
                    }
                }
            }
        }
    }

    private fun likeData() {
        if (!LitePal.isExist(NewsBean::class.java, "dataId=${data.dataId}")) {//存在
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
                        .into<SimpleTarget<File>>(object : SimpleTarget<File>() {
                            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                                ShareDialog(this@VideoMDPlayerDetailsActivity, data.title, data.description, resource, "https://www.chinadailyhk.com${data.htmlUrl}?newsId=${data.dataId}&play_url=${data.jsonUrl}").show()
                            }
                        })
            }
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            goOnPlayOnPause()
        } catch (e: Exception) {
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            view_super_player?.release()
        } catch (e: Exception) {
        }
    }

    override fun onBackPressed() {
        if (backPress()) {
            return
        }
        super.onBackPressed()
    }

}