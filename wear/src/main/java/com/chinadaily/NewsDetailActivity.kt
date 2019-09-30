package com.chinadaily

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.TypeReference
import com.chinadaily.adapter.NewsDetailAdapter
import com.chinadaily.data.NewsDetail
import com.chinadaily.http.FastJsonCallback
import com.chinadaily.http.IMAGE_DNS
import com.chinadaily.http.cancel
import com.chinadaily.http.httpGet
import com.chinadaily.utils.showToast
import kotlinx.android.synthetic.main.activity_recycler.*

class NewsDetailActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)
        setAmbientEnabled()
        swipeRefreshLayout.isRefreshing = true
        recyclerView.layoutManager = LinearLayoutManager(this)
        swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }
        loadData()
    }

    private fun loadData() {
        httpGet(object : FastJsonCallback<NewsDetail>() {
            override fun onSuccess(body: NewsDetail) {
                swipeRefreshLayout.isRefreshing = false
                recyclerView.adapter = NewsDetailAdapter(body)
                recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onError(exception: String) {
                swipeRefreshLayout.isRefreshing = false
                showToast(exception)
            }

        }.apply {
            url = "$IMAGE_DNS${intent.getStringExtra("json")}"
            clazz = object : TypeReference<NewsDetail>() {}
            pauseBaseRes = false
        })
    }

    override fun onDestroy() {
        pictureMap.clear()
        cancel("$IMAGE_DNS${intent.getStringExtra("json")}")
        super.onDestroy()
    }

}

val pictureMap: MutableMap<String, Drawable> = mutableMapOf()