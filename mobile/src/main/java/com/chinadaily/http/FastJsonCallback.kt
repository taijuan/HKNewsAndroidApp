package com.chinadaily.http

import android.app.Dialog
import android.util.Log
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.AbsCallback
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.request.base.Request
import com.lzy.okgo.utils.HttpUtils.runOnUiThread
import okhttp3.Response
import java.net.SocketException
import java.net.SocketTimeoutException

abstract class FastJsonCallback<T>(val lifecycle: Lifecycle) : AbsCallback<T>(), LifecycleObserver {
    var clazz: TypeReference<T>? = null
    var url: String? = null
    var header: HttpHeaders? = null
    var dialog: Dialog? = null
    var params: HttpParams? = null
    var pauseBaseRes = true

    init {
        lifecycle.addObserver(object : LifecycleObserver {
            @Keep
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                OkGo.cancelTag(OkGo.getInstance().okHttpClient, url)
                Log.e("zuiweng", "${javaClass.name}  ->  onDestroy")
            }
        })
    }

    @Throws(Throwable::class)
    override fun convertResponse(response: Response): T {
        if (response.body == null) throw ServerIllegalStateException("response body is empty")
        val body = response.body?.string()
        return if (pauseBaseRes) {
            val baseRes = JSON.parseObject(body, BaseRes::class.java)
            when (baseRes.resCode) {
                200 -> JSON.parseObject(baseRes.resObject, clazz)
                else -> throw ServerIllegalStateException(baseRes.resMsg
                        ?: "resCode is not 200 and it is ${baseRes.resCode}")
            }
        } else {
            JSON.parseObject(body, clazz)
        }
    }

    abstract fun onSuccess(body: T)
    final override fun onSuccess(response: com.lzy.okgo.model.Response<T>?) {
        val body = response?.body()
        if (body == null) {
            runOnUiThread { onError("body is null") }
        } else {
            runOnUiThread { onSuccess(body) }
        }

    }

    abstract fun onError(exception: String)
    final override fun onError(response: com.lzy.okgo.model.Response<T>?) {
        super.onError(response)
        runOnUiThread {
            when (response?.exception) {
                is ServerIllegalStateException -> onError(response.exception?.message ?: "")
                is SocketTimeoutException -> onError("connect time out")
                is SocketException -> onError("server exception")
                else -> onError("system exception")
            }
        }
    }

    override fun onStart(request: Request<T, out Request<Any, Request<*, *>>>?) {
        if (dialog?.isShowing == false) {
            dialog?.show()
        }
    }

    override fun onFinish() {
        dialog?.dismiss()
        dialog = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        OkGo.cancelTag(OkGo.getInstance().okHttpClient, url)
        Log.e("zuiweng", "${javaClass.name}  ->  onDestroy")
    }
}

class ServerIllegalStateException(exception: String) : Throwable(exception)
