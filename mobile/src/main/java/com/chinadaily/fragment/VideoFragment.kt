package com.chinadaily.fragment

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.chinadaily.R
import com.chinadaily.adapter.VideoAdapter
import com.chinadaily.base.LazyLoadBaseFragment
import com.chinadaily.base.setTabBarTop
import com.chinadaily.data.NewsBean
import com.chinadaily.http.FastJsonCallback
import com.chinadaily.http.HttpConstants
import com.chinadaily.http.httpGet
import com.chinadaily.utils.SPUtils
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.android.synthetic.main.fragment_news.*

class VideoFragment : LazyLoadBaseFragment() {
    private var curPage = 0
    private val adapter: VideoAdapter by lazy { VideoAdapter(lifecycle) }
    override fun getContentViewRes() = R.layout.fragment_video_news
    override fun onFragmentFirstVisible() {
        tabBar.setTabBarTop()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        smartRefreshLayout.setOnLoadMoreListener {
            getData(curPage)
        }
        smartRefreshLayout.setOnRefreshListener {
            getData(1)
        }
        loadingRetry.setOnClickListener {
            getData(1, true)
        }
        loadingRetry.setOnClickListener {
            getData(1, true)
        }
        getData(1, true)
    }

    private fun getData(page: Int, isRefresh: Boolean = false) {
        curPage = page
        loadingProgressBar.visibility = if (isRefresh) View.VISIBLE else View.GONE
        recyclerView.visibility = View.VISIBLE
        loadingRetry.visibility = View.GONE
        httpGet(fastJsonCallback.also {
            it.url = "${HttpConstants.STATIC_URL}selectNewsList?currentPage=$curPage&dataType=3"
        })
    }

    private val fastJsonCallback: FastJsonCallback<JSONObject> by lazy {
        object : FastJsonCallback<JSONObject>(lifecycle) {
            override fun onSuccess(body: JSONObject) {
                val data = JSON.parseArray(body.getString("dateList"), NewsBean::class.java)
                        ?: mutableListOf()
                if (curPage == 1) {
                    SPUtils.put("video", body)
                    adapter.refreshData(data)
                    smartRefreshLayout?.finishRefresh()
                } else {
                    adapter.addData(data)
                    smartRefreshLayout?.finishLoadMore(300, true, data.isEmpty())
                }
                curPage++
                loadingProgressBar?.visibility = View.GONE
            }

            override fun onError(exception: String) {
                if (curPage == 1) {
                    val homeCache = SPUtils.getString("video", "")
                    if (!TextUtils.isEmpty(homeCache)) {
                        val body = JSON.parseObject(homeCache)
                        val data = JSON.parseArray(body.getString("dateList"), NewsBean::class.java)
                        adapter.refreshData(data)
                    } else {
                        loadingRetry?.visibility = View.VISIBLE
                        recyclerView?.visibility = View.GONE
                    }
                    curPage++
                    smartRefreshLayout?.finishRefresh(false)
                } else {
                    smartRefreshLayout?.finishLoadMore(300, false, false)
                }
                loadingProgressBar?.visibility = View.GONE
            }
        }.apply {
            clazz = object : TypeReference<JSONObject>() {}
        }
    }
}