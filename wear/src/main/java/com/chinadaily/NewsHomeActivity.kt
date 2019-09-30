package com.chinadaily

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.chinadaily.adapter.NewsAdapter
import com.chinadaily.data.News
import com.chinadaily.http.*
import com.chinadaily.utils.initApp
import com.chinadaily.utils.showToast
import kotlinx.android.synthetic.main.activity_recycler.*

class NewsHomeActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)
        setAmbientEnabled()
        application.initHttpUtils()
        application.initApp()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
            setDrawable(getDrawable(R.drawable.divider)!!)
        })
        PagerSnapHelper().attachToRecyclerView(recyclerView)
        swipeRefreshLayout.isRefreshing = true
        swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }
        loadData()
    }

    private fun loadData() {
        httpGet(object : FastJsonCallback<JSONObject>() {
            override fun onSuccess(body: JSONObject) {
                recyclerView.adapter = NewsAdapter(JSON.parseArray(body.getString("allLists"), News::class.java))
                recyclerView.adapter?.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onError(exception: String) {
                showToast(exception)
                swipeRefreshLayout.isRefreshing = false
            }

        }.apply {
            url = "${API_DNS}homeDataNewsList"
            clazz = object : TypeReference<JSONObject>() {}
        })
    }

    override fun onDestroy() {
        cancel("${API_DNS}homeDataNewsList")
        super.onDestroy()
    }
}
