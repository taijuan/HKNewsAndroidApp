package com.chinadaily.update

import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.chinadaily.BuildConfig
import com.chinadaily.activity.SettingActivity
import com.chinadaily.http.FastJsonCallback
import com.chinadaily.http.httpPost
import com.chinadaily.utils.EncryptUtil
import com.chinadaily.utils.ToastUtils
import com.lzy.okgo.model.HttpParams

fun AppCompatActivity.checkVersion() {
    val fastJsonCallback by lazy {
        object : FastJsonCallback<JSONObject>(lifecycle) {
            override fun onSuccess(body: JSONObject) {
                body.getJSONObject("resBody")?.getObject("resultObject", AppVersion::class.java)?.let {
                    compareVersion(it)
                }
            }

            override fun onError(exception: String) {
            }
        }.apply {
            url = "https://api.cdeclips.com/vdoapi/common/version"
            clazz = object : TypeReference<JSONObject>() {}
            val jsonObject = JSONObject()
            jsonObject["version_package"] = "com.chinadaily"
            jsonObject["encryptKey"] = "hang!@#$"
            jsonObject["deviceType"] = "Android"
            jsonObject["operatingSystemType"] = "Android"
            val strJSON = jsonObject.toJSONString()
            val encodeJSON = EncryptUtil.getEncodeData(strJSON, "hang!@#$")
            params = HttpParams("data", encodeJSON)
            pauseBaseRes = false
        }
    }
    httpPost(fastJsonCallback)
}

private fun AppCompatActivity.compareVersion(versionBean: AppVersion) {
    val versionCode = versionBean.versionCode
    val downloadUrl = versionBean.versionPath
    Log.e("zuiweng",versionCode.toString())
    if (versionCode > BuildConfig.VERSION_CODE && !TextUtils.isEmpty(downloadUrl)) {
        UpdateVersionDialog(this, versionBean).show()
    } else {
        if (this is SettingActivity) {
            ToastUtils.showShort("China Daily News is up to date, versionName:${BuildConfig.VERSION_NAME}")
        }
    }
}