package com.chinadaily.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.chinadaily.R
import com.chinadaily.adapter.EPaperAdapter
import com.chinadaily.base.LazyLoadBaseFragment
import com.chinadaily.data.CardItem
import com.chinadaily.http.FastJsonCallback
import com.chinadaily.http.HttpConstants
import com.chinadaily.http.httpGet
import com.chinadaily.utils.SPUtils
import com.tmall.ultraviewpager.UltraViewPager
import kotlinx.android.synthetic.main.epaper_fragment.*


class EPaperFragment : LazyLoadBaseFragment() {
    private val cardPagerAdapter by lazy { EPaperAdapter(lifecycle) }
    private val fastJsonCallback by lazy {
        object : FastJsonCallback<JSONObject>(lifecycle) {
            override fun onSuccess(body: JSONObject) {
                SPUtils.put("ePaper_config", body.toJSONString())
                initData()
            }

            override fun onError(exception: String) {
                initData()
            }
        }.apply {
            url = HttpConstants.E_PAPER_URL
            clazz = object : TypeReference<JSONObject>() {}
            pauseBaseRes = false
        }
    }

    private fun initData() {
        val ePaperConfig = SPUtils.getString("ePaper_config", "")
        if (!TextUtils.isEmpty(ePaperConfig)) {
            var data = JSON.parseArray(JSON.parseObject(ePaperConfig).getString("newestPubDate"), CardItem::class.java)
                    ?: mutableListOf()
            data = data.filter { it.publicationConfig.isHide == 0 }
            ultra_viewpager.visibility = View.VISIBLE
            loadingProgressBar.visibility = View.GONE
            loadingRetry.visibility = View.GONE
            cardPagerAdapter.refreshData(data)
            ultra_viewpager.setOffscreenPageLimit(cardPagerAdapter.count)
            ultra_viewpager.adapter = cardPagerAdapter
        } else {
            ultra_viewpager.visibility = View.GONE
            loadingProgressBar.visibility = View.GONE
            loadingRetry.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.epaper_fragment, container, false)
    }

    override fun onFragmentFirstVisible() {
        ultra_viewpager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        ultra_viewpager.setMultiScreen(0.7f)
        httpGet(fastJsonCallback)
        loadingRetry.setOnClickListener {
            httpGet(fastJsonCallback)
            ultra_viewpager.visibility = View.GONE
            loadingProgressBar.visibility = View.VISIBLE
            loadingRetry.visibility = View.GONE
        }
    }
}
