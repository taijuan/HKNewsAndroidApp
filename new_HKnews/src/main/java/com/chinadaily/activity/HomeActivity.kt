package com.chinadaily.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import cn.jpush.android.api.JPushInterface
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.chinadaily.R
import com.chinadaily.base.BaseApp
import com.chinadaily.base.BaseAppActivity
import com.chinadaily.data.NewsBean
import com.chinadaily.fragment.EPaperFragment
import com.chinadaily.fragment.HomeFragment
import com.chinadaily.fragment.MineFragment
import com.chinadaily.fragment.VideoFragment
import com.chinadaily.http.FastJsonCallback
import com.chinadaily.http.HttpConstants
import com.chinadaily.http.httpGet
import com.chinadaily.update.checkVersion
import com.chinadaily.utils.SPUtils
import com.lzy.okgo.db.DownloadManager
import com.lzy.okserver.OkDownload
import kotlinx.android.synthetic.main.activity_home.*
import me.leolin.shortcutbadger.ShortcutBadger
import java.io.File

class HomeActivity : BaseAppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        webView.loadUrl("")
        Log.e("zuiweng  ", JPushInterface.getRegistrationID(this))
        OkDownload.getInstance().folder = cacheDir.absolutePath + File.separator
        OkDownload.getInstance().threadPool.setCorePoolSize(3)
        OkDownload.restore(DownloadManager.getInstance().all)
        SPUtils.remove("badgeCount")
        ShortcutBadger.removeCount(BaseApp.getInstance())
        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> HomeFragment().apply { isNews = true }
                    1 -> HomeFragment().apply { isNews = false }
                    2 -> EPaperFragment()
                    3 -> VideoFragment()
                    else -> MineFragment()
                }
            }

            override fun getCount() = 5
        }
        tabLayout.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = 5
        tabLayout.removeAllTabs()
        tabItem(R.string.page1, R.drawable.news_selected, R.drawable.news)
        tabItem(R.string.page2, R.drawable.focus_selected, R.drawable.focus)
        tabItem(R.string.page5, R.drawable.epaper_selected, R.drawable.epaper)
        tabItem(R.string.page3, R.drawable.video_selected, R.drawable.video)
        tabItem(R.string.page4, R.drawable.me_selected, R.drawable.me)
        checkVersion()
        intent?.filterIntent()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.filterIntent()
    }

    private val fastJsonCallback by lazy {
        object : FastJsonCallback<NewsBean>(lifecycle) {
            override fun onSuccess(body: NewsBean) {
                startActivity(Intent(this@HomeActivity, NewsDetailActivity::class.java).apply {
                    putExtra("bean", JSON.toJSONString(body))
                })
            }

            override fun onError(exception: String) {

            }
        }.apply {
            clazz = object : TypeReference<NewsBean>() {}
        }
    }

    private fun Intent.filterIntent() {
        val newsId = this.data.getQueryParameter("newsId")
        if (TextUtils.isEmpty(newsId)) {
            return
        }
        val scheme = this.data.host
        when {
            scheme.contains("news") -> {
                httpGet(fastJsonCallback.also { it.url = "${HttpConstants.STATIC_URL}selecNewsDetail?dataId=$newsId" })
            }
            scheme.contains("video") -> {
                startActivity(Intent(this@HomeActivity, VideoMDPlayerDetailsActivity::class.java).apply {
                    putExtra("id", newsId)
                })
            }
        }
    }

    private fun tabItem(name: Int, select: Int, normal: Int) {
        tabLayout.addTab(tabLayout.newTab().setCustomView(TextView(this@HomeActivity).apply {
            text = resources.getString(name)
            gravity = Gravity.CENTER
            textSize = 10f
            val colors = intArrayOf(ContextCompat.getColor(this@HomeActivity, R.color.mainpage_color),
                    ContextCompat.getColor(this@HomeActivity, R.color.mainpage_color),
                    ContextCompat.getColor(this@HomeActivity, R.color.main_bottom_text_normal))
            val states = arrayOf(intArrayOf(android.R.attr.state_pressed),
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf())
            setTextColor(ColorStateList(states, colors))
            setCompoundDrawablesWithIntrinsicBounds(null, StateListDrawable().apply {
                addState(intArrayOf(android.R.attr.state_selected), ContextCompat.getDrawable(this@HomeActivity, select))
                addState(intArrayOf(android.R.attr.state_pressed), ContextCompat.getDrawable(this@HomeActivity, select))
                addState(intArrayOf(), ContextCompat.getDrawable(this@HomeActivity, normal))
            }, null, null)
        }))
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}