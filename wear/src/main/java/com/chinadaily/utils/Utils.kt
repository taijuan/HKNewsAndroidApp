package com.chinadaily.utils

import android.app.Application
import android.content.res.Resources
import android.widget.Toast

/**
 * 获取设备的一些信息
 */

private lateinit var application: Application

fun getApp() = application
fun Application.initApp() {
    application = this
}

/**
 * dp 转化为 px
 *
 * @param dpValue dpValue
 * @return int
 */
fun dp2px(dpValue: Float): Int {
    val scale = application.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}


/**
 * px 转化为 dp
 *
 * @param pxValue pxValue
 */
fun px2dp(pxValue: Float): Int {
    val scale = application.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}


/**
 * 获取设备宽度（px）
 *
 * @return int
 */
fun screenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun screenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

fun isScreenRound() = getApp().resources.configuration.isScreenRound

fun headerOrFooterHeight() = (screenHeight() * 0.146447f).toInt()

fun showToast(msg: String) {
    Toast.makeText(getApp(), msg, Toast.LENGTH_SHORT).show()
}

