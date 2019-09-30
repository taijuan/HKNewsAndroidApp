package com.chinadaily.fragment

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.chinadaily.R
import com.chinadaily.adapter.NewsAdapter
import com.chinadaily.base.LazyLoadBaseFragment
import com.chinadaily.data.NewsBean
import com.chinadaily.http.FastJsonCallback
import com.chinadaily.http.HttpConstants
import com.chinadaily.http.httpGet
import com.chinadaily.utils.SPUtils
import kotlinx.android.synthetic.main.fragment_news.*

class HomeNewsFragment : LazyLoadBaseFragment() {
    private val adapter: NewsAdapter by lazy { NewsAdapter(this) }
    override fun getContentViewRes() = R.layout.fragment_news
    override fun onFragmentFirstVisible() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        smartRefreshLayout.setOnRefreshListener {
            getData()
        }
        loadingRetry.setOnClickListener {
            getData(true)
        }
        loadingRetry.setOnClickListener {
            getData(true)
        }
        getData(true)
    }

    private val name by lazy { arguments?.getString("name", "") ?: "" }
    private val fastJsonCallback: FastJsonCallback<JSONObject>by lazy {
        object : FastJsonCallback<JSONObject>(lifecycle) {

            override fun onSuccess(body: JSONObject) {
                SPUtils.put(name, body.toJSONString())
                val data = JSON.parseArray(body.getString("allLists"), NewsBean::class.java)
                        ?: mutableListOf()
                val focusData = JSON.parseArray(body.getString("top_focus"), NewsBean::class.java)
                        ?: mutableListOf()
                adapter.refreshPager(focusData)
                adapter.refreshData(data)
                loadingProgressBar?.visibility = View.GONE
                smartRefreshLayout?.finishRefresh()
                smartRefreshLayout?.finishLoadMore(300, true, true)
            }

            override fun onError(exception: String) {
                val homeCache = SPUtils.getString(name, "")
                if (!TextUtils.isEmpty(homeCache)) {
                    val resObject = JSON.parseObject(homeCache)
                    val data = JSON.parseArray(resObject.getString("allLists"), NewsBean::class.java)
                            ?: mutableListOf()
                    val focusData = JSON.parseArray(resObject.getString("top_focus"), NewsBean::class.java)
                            ?: mutableListOf()
                    adapter.refreshPager(focusData)
                    adapter.refreshData(data)
                } else {
                    loadingRetry?.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE
                }
                loadingProgressBar?.visibility = View.GONE
                smartRefreshLayout?.finishRefresh(false)
                smartRefreshLayout?.finishLoadMore(300, false, true)
            }
        }.apply {
            url = "${HttpConstants.STATIC_URL}homeDataNewsList"
            clazz = object : TypeReference<JSONObject>() {}
        }
    }

    private fun getData(isRefresh: Boolean = false) {
        loadingProgressBar?.visibility = if (isRefresh) View.VISIBLE else View.GONE
        recyclerView?.visibility = View.VISIBLE
        loadingRetry?.visibility = View.GONE
        httpGet(fastJsonCallback)
    }
}