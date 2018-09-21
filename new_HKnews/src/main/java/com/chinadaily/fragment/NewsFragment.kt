package com.chinadaily.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class NewsFragment : LazyLoadBaseFragment() {
    private var curPage = 0
    private val adapter by lazy { NewsAdapter(this) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onFragmentFirstVisible() {
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

    private val name by lazy { arguments?.getString("name", "") ?: "" }
    private val fastJsonCallback by lazy {
        object : FastJsonCallback<JSONObject>(lifecycle) {
            override fun onSuccess(body: JSONObject) {
                val data = JSON.parseArray(body.getString("dateList"), NewsBean::class.java)
                        ?: mutableListOf()
                if (curPage == 1) {
                    SPUtils.put(name, body.toJSONString())
                    val size = data.size
                    if (size > 4) {
                        adapter.refreshPager(data.subList(0, 4))
                        adapter.refreshData(data.subList(4, size))
                    } else {
                        adapter.refreshPager(data)
                        adapter.refreshData(mutableListOf())
                    }
                    smartRefreshLayout?.finishRefresh()
                } else {
                    adapter.addData(data)
                    smartRefreshLayout?.finishLoadMore(300, true, data.isEmpty())
                }
                curPage += 1
                loadingProgressBar?.visibility = View.GONE
            }

            override fun onError(exception: String) {
                if (curPage == 1) {
                    val homeCache = SPUtils.getString(name, "")
                    if (!TextUtils.isEmpty(homeCache)) {
                        val resObject = JSON.parseObject(homeCache)
                        val data = JSON.parseArray(resObject.getString("dateList"), NewsBean::class.java)
                        val size = data.size
                        if (size > 4) {
                            adapter.refreshPager(data.subList(0, 4))
                            adapter.refreshData(data.subList(4, size))
                        } else {
                            adapter.refreshPager(data)
                            adapter.refreshData(mutableListOf())
                        }
                        curPage++
                    } else {
                        loadingRetry?.visibility = View.VISIBLE
                        recyclerView?.visibility = View.GONE
                    }
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

    private fun getData(page: Int, isRefresh: Boolean = false) {
        curPage = page
        loadingProgressBar.visibility = if (isRefresh) View.VISIBLE else View.GONE
        recyclerView.visibility = View.VISIBLE
        loadingRetry.visibility = View.GONE
        httpGet(fastJsonCallback.also {
            it.url = "${HttpConstants.STATIC_URL}selectNewsList?subjectCode=$name&currentPage=$curPage&dataType=1"
        })
    }
}