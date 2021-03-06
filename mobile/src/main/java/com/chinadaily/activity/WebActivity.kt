package com.chinadaily.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.chinadaily.BuildConfig
import com.chinadaily.R
import com.chinadaily.base.BaseApp
import com.chinadaily.base.BaseAppActivity
import com.chinadaily.base.setTabBarTop
import com.chinadaily.vassonic.SonicRuntimeImpl
import com.chinadaily.vassonic.SonicSessionClientImpl
import com.tencent.sonic.sdk.SonicConfig
import com.tencent.sonic.sdk.SonicEngine
import com.tencent.sonic.sdk.SonicSession
import com.tencent.sonic.sdk.SonicSessionConfig
import kotlinx.android.synthetic.main.base_title_layout.*
import kotlinx.android.synthetic.main.fragment_web.*


@Suppress("DEPRECATION", "OverridingDeprecatedMember")
class WebActivity : BaseAppActivity() {

    private var sonicSession: SonicSession? = null
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
        val url = intent.getStringExtra("data")
        Log.e("zuiweng", url)
        val sessionConfig = SonicSessionConfig.Builder().setSupportLocalServer(true).build()
        sonicSession = SonicEngine
                .createInstance(SonicRuntimeImpl(BaseApp.getInstance()), SonicConfig.Builder().build())
                .createSession(url, sessionConfig)
        setContentView(R.layout.activity_web)
        tabBar.setTabBarTop()
        sonicSession?.bindClient(SonicSessionClientImpl(webView))
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
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
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
                Log.e("zuiweng", url)
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

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                if (failingUrl.toString() == url) {
                    isError = true
                }
            }
        }
        if (sonicSession != null) {
            sonicSession?.sessionClient?.clientReady()
        } else {
            webView.loadUrl(url)
        }
        loadingRetry.setOnClickListener {
            isError = false
            webView.reload()
            webView.visibility = View.VISIBLE
            loadingProgressBar.visibility = View.VISIBLE
            loadingRetry.visibility = View.GONE
        }
        baseTitle.text = intent.getStringExtra("title")
        baseBack.setOnClickListener {
            finish()
        }

    }

    override fun onDestroy() {
        sonicSession?.destroy()
        webView.destroy()
        super.onDestroy()
    }
}
