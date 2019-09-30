package com.chinadaily.vassonic

import android.os.Bundle
import android.webkit.WebView
import com.tencent.sonic.sdk.SonicSessionClient
import java.util.*

/**
 * a implement of SonicSessionClient which need to connect webview and content data.
 */

class SonicSessionClientImpl(var webView: WebView) : SonicSessionClient() {

    override fun loadUrl(url: String, extraData: Bundle?) {
        webView.loadUrl(url)
    }

    override fun loadDataWithBaseUrl(baseUrl: String, data: String, mimeType: String, encoding: String, historyUrl: String) {
        webView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }


    override fun loadDataWithBaseUrlAndHeader(baseUrl: String, data: String, mimeType: String, encoding: String, historyUrl: String, headers: HashMap<String, String>) {
        loadDataWithBaseUrl(baseUrl, data, mimeType, encoding, historyUrl)
    }
}