package com.chinadaily.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import cn.jpush.android.api.JPushInterface
import com.alibaba.fastjson.JSON
import com.chinadaily.utils.SPUtils
import me.leolin.shortcutbadger.ShortcutBadger

/**
 * 自定义接收器
 *
 *
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
class PushReceiver : BroadcastReceiver() {
    private val tag = "zuiweng"
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        Log.e(tag, "[PushReceiver] onReceive - " + intent.action + ", extras: " + JSON.toJSONString(intent.extras))
        when (intent.action) {
            JPushInterface.ACTION_REGISTRATION_ID -> {
                val regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID)
                Log.e(tag, regId)
            }
            JPushInterface.ACTION_MESSAGE_RECEIVED, JPushInterface.ACTION_NOTIFICATION_RECEIVED -> {
                var badgeCount = SPUtils.getInt("badgeCount", 0)
                badgeCount++
                SPUtils.put("badgeCount", badgeCount)
                Log.e(tag, badgeCount.toString() + "")
                ShortcutBadger.applyCount(context, badgeCount)
            }
            JPushInterface.ACTION_NOTIFICATION_OPENED -> Log.e(tag, "[PushReceiver] 用户点击打开了通知")
            JPushInterface.ACTION_CONNECTION_CHANGE -> {
                val connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false)
                Log.e(tag, "[PushReceiver]" + intent.action + " connected state change to " + connected)
            }
            else -> Log.e(tag, "[PushReceiver] Unhandled intent - " + intent.action!!)
        }
    }
}
