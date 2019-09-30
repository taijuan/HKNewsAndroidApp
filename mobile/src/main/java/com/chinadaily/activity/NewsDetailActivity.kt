package com.chinadaily.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.chinadaily.BuildConfig
import com.chinadaily.R
import com.chinadaily.base.BaseApp
import com.chinadaily.base.BaseAppActivity
import com.chinadaily.data.NewsBean
import com.chinadaily.http.BaseRes
import com.chinadaily.http.FastJsonCallback
import com.chinadaily.http.HttpConstants
import com.chinadaily.http.httpGet
import com.chinadaily.utils.GlideApp
import com.chinadaily.utils.topHeight
import com.chinadaily.vassonic.SonicRuntimeImpl
import com.chinadaily.vassonic.SonicSessionClientImpl
import com.share.ShareDialog
import com.tencent.sonic.sdk.SonicConfig
import com.tencent.sonic.sdk.SonicEngine
import com.tencent.sonic.sdk.SonicSession
import com.tencent.sonic.sdk.SonicSessionConfig
import kotlinx.android.synthetic.main.fragment_web.*
import kotlinx.android.synthetic.main.layout_news_detail_foot_bar.*
import org.litepal.Operator
import java.io.File


@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class NewsDetailActivity : BaseAppActivity(), OnClickListener {
    private val news by lazy {
        arrayOf(
                arrayOf("HOME", "home", getString(R.string.page1)),
                arrayOf("HONG KONG", "hong_kong", getString(R.string.page1)),
                arrayOf("NATION", "nation", getString(R.string.page1)),
                arrayOf("ASIA", "asia", getString(R.string.page1)),
                arrayOf("WORLD", "world", getString(R.string.page1)),
                arrayOf("BUSINESS", "business", getString(R.string.page1)),
                arrayOf("DATA", "data", getString(R.string.page1)),
                arrayOf("SPORTS", "sports", getString(R.string.page1)),
                arrayOf("LIFE & ART", "life_art", getString(R.string.page2)),
                arrayOf("LEADERS", "leaders", getString(R.string.page2)),
                arrayOf("OFFBEAT HK", "offbeat_hk", getString(R.string.page2)),
                arrayOf("IN-DEPTH CHINA", "in_depth_china", getString(R.string.page2)),
                arrayOf("EYE ON ASIA", "eye_on_asia", getString(R.string.page2)),
                arrayOf("QUIRKY", "quirky", getString(R.string.page2)),
                arrayOf("PHOTO", "photo", getString(R.string.page2))
        )
    }
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
    private var sonicSession: SonicSession? = null
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
        data = JSON.parseObject(intent.getStringExtra("bean"), NewsBean::class.java)
        val url = HttpConstants.SERVICE_HTTPS + data.murl + "?newsId=${data.dataId}"
        val sessionConfig = SonicSessionConfig.Builder().setSupportLocalServer(true).build()
        sonicSession = SonicEngine
                .createInstance(SonicRuntimeImpl(BaseApp.getInstance()), SonicConfig.Builder().build())
                .createSession(url, sessionConfig)
        setContentView(R.layout.activity_news_detail)
        sonicSession?.bindClient(SonicSessionClientImpl(webView))
        (webView.parent as ViewGroup).setPadding(0, topHeight(), 0, 0)
        webView.visibility = View.VISIBLE
        webView.settings.apply {
            javaScriptEnabled = true
            databaseEnabled = true
            domStorageEnabled = true
            setAppCacheEnabled(true)
            savePassword = false
            saveFormData = false
            useWideViewPort = true
            loadWithOverviewMode = true
            javaScriptCanOpenWindowsAutomatically = true
            allowFileAccess = true
            setSupportZoom(false)
            userAgentString = "$userAgentString;${BuildConfig.APPLICATION_ID}"
        }
        var isError = false
        webView.removeJavascriptInterface("searchBoxJavaBridge_")

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, reqUrl: String) {
                super.onPageFinished(view, reqUrl)
                sonicSession?.sessionClient?.pageFinish(reqUrl)
                if (reqUrl == url) {
                    if (isError) {
                        webView?.visibility = View.GONE
                        loadingRetry?.visibility = View.VISIBLE
                    } else {
                        loadingRetry?.visibility = View.GONE
                    }
                }
            }

            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                return sonicSession?.sessionClient?.requestResource(url) as? WebResourceResponse
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return shouldInterceptRequest(view, request?.url.toString())
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return shouldOverrideUrlLoading(view, request?.url.toString())
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                val uri = Uri.parse(url)
                if (uri != null && uri.scheme == "js" && uri.authority == "jstojava") {
                    goToPDF(uri.getQueryParameter("pdfUrl") ?: "")
                    return true
                }
                if (uri.scheme == "mailto") {
                    try {
                        val data = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(data)
                    } finally {
                        return true
                    }
                }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageCommitVisible(view: WebView?, reqUrl: String?) {
                super.onPageCommitVisible(view, reqUrl)
                loadingProgressBar?.visibility = View.GONE
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                if (request?.url.toString() == url) {
                    isError = true
                }
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                result?.confirm()
                System.out.print(url)
                System.out.print(message)
                return true
            }
        }
        loadingRetry.setOnClickListener {
            isError = false
            webView.reload()
            webView.visibility = View.VISIBLE
            loadingProgressBar.visibility = View.VISIBLE
            loadingRetry.visibility = View.GONE
        }
        if (sonicSession != null) {
            sonicSession?.sessionClient?.clientReady()
        } else {
            webView.loadUrl(url)
        }
        iv_back.setOnClickListener(this)
        iv_like.setOnClickListener(this)
        iv_share.setOnClickListener(this)
        iv_good.setOnClickListener(this)
        if (Operator.isExist(NewsBean::class.java, "dataId = " + data.dataId)) {
            iv_like.setImageResource(R.drawable.favorite_selected)
            iv_like.isClickable = false
        } else {
            iv_like.setImageResource(R.drawable.favorite)
        }

        news.filter { it[1] == data.subjectCode }.map {
            iv_back.contentDescription = "back to ${it[0]} on ${it[2]}"
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.iv_back -> finish()
            R.id.iv_like -> likeData()
            R.id.iv_good -> httpGet(saveGoodFastJsonCallback)
            R.id.iv_share -> GlideApp.with(this@NewsDetailActivity)
                    .asFile()
                    .load(HttpConstants.SERVICE_URL + data.bigTitleImage)
                    .error(R.drawable.logo)
                    .override(160, 90)
                    .fitCenter()
                    .into<SimpleTarget<File>>(object : SimpleTarget<File>() {
                        override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                            ShareDialog(this@NewsDetailActivity, data.title, data.description, resource, HttpConstants.SERVICE_HTTPS + data.murl + "?newsId=${data.dataId}").show()
                        }
                    })
        }

    }

    private fun likeData() {
        if (!Operator.isExist(NewsBean::class.java, "dataId = " + data.dataId)) {//存在
            data.also { it.dataType = "1" }.saveOrUpdateAsync("dataId = " + data.dataId).listen {
                iv_like.setImageResource(R.drawable.favorite_selected)
                iv_like.isClickable = false
            }
        }
    }

    override fun onDestroy() {
        sonicSession?.destroy()
        webView.destroy()
        super.onDestroy()
    }
}
