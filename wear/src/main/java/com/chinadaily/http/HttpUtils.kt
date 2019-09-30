package com.chinadaily.http

import android.app.Application
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheEntity
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import java.util.logging.Level

const val IMAGE_DNS = "https://www.chinadailyhk.com"
const val API_DNS = "https://api.cdeclips.com/hknews-api/"
private val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder().apply {
        addInterceptor(HttpLoggingInterceptor("OkGo").apply {
            setPrintLevel(HttpLoggingInterceptor.Level.BODY)
            setColorLevel(Level.INFO)
        })
        readTimeout(10000, TimeUnit.MILLISECONDS)
        writeTimeout(10000, TimeUnit.MILLISECONDS)
        connectTimeout(10000, TimeUnit.MILLISECONDS)
    }.build()
}

fun Application.initHttpUtils() {
    OkGo.getInstance().init(this)
            .setOkHttpClient(okHttpClient)
            .setCacheMode(CacheMode.NO_CACHE)
            .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE).retryCount = 1
}

fun <T> httpPost(fastJsonCallback: FastJsonCallback<T>) {
    OkGo.post<T>(fastJsonCallback.url)
            .cacheMode(CacheMode.DEFAULT)
            .tag(fastJsonCallback.url)
            .cacheKey(fastJsonCallback.url)
            .headers(fastJsonCallback.header)
            .params(fastJsonCallback.params)
            .execute(fastJsonCallback)
}

fun <T> httpGet(fastJsonCallback: FastJsonCallback<T>) {
    OkGo.get<T>(fastJsonCallback.url)
            .cacheMode(CacheMode.DEFAULT)
            .tag(fastJsonCallback.url)
            .cacheKey(fastJsonCallback.url)
            .headers(fastJsonCallback.header)
            .execute(fastJsonCallback)
}

fun cancel(vararg any: Any) {
    any.forEach {
        OkGo.cancelTag(OkGo.getInstance().okHttpClient, it)
    }
}


