package com.chinadaily.http

import android.app.Dialog
import androidx.lifecycle.LifecycleObserver
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.lzy.okgo.callback.AbsCallback
import com.lzy.okgo.model.HttpHeaders
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.request.base.Request
import com.lzy.okgo.utils.HttpUtils.runOnUiThread
import okhttp3.Response
import java.net.SocketException
import java.net.SocketTimeoutException

abstract class FastJsonCallback<T> : AbsCallback<T>(), LifecycleObserver {
    var clazz: TypeReference<T>? = null
    var url: String? = null
    var header: HttpHeaders? = null
    var dialog: Dialog? = null
    var params: HttpParams? = null
    var pauseBaseRes = true

    @Throws(Throwable::class)
    override fun convertResponse(response: Response): T {
        if (response.body() == null) throw ServerIllegalStateException("response body is empty")
        val body = response.body()?.string()
        return if (pauseBaseRes) {
            val baseRes = JSON.parseObject(body, BaseRes::class.java)
            when (baseRes.resCode) {
                200 -> JSON.parseObject(baseRes.resObject, clazz)
                else -> throw ServerIllegalStateException(baseRes.resMsg)
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
}

class ServerIllegalStateException(exception: String) : Throwable(exception)
