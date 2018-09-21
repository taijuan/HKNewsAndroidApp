package com.share

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.umeng.commonsdk.UMConfigure
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors


fun Context.initShare() {
    UMConfigure.setLogEnabled(BuildConfig.DEBUG)
    UMConfigure.init(this, BuildConfig.UMENG_APPKEY, BuildConfig.UMENG_CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, "")
    WXAPIFactory.createWXAPI(this, BuildConfig.WECHAT_APPKEY)
}

fun AppCompatActivity.share(share_media: SHARE_MEDIA, title: String, description: String, imageFile: File, webUrl: String) {
    when (share_media) {
        SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN -> {
            val api = WXAPIFactory.createWXAPI(this, BuildConfig.WECHAT_APPKEY)
            if (api.isWXAppInstalled) {
                val web = WXWebpageObject().apply { this.webpageUrl = webUrl }
                val msg = WXMediaMessage(web).apply {
                    this.title = title
                    this.description = description
                    try {
                        val bm = BitmapFactory.decodeFile(imageFile.absolutePath)
                        val t = Bitmap.createScaledBitmap(bm, dp2px(30f), dp2px(30f), true)
                        if (t != null) {
                            this.setThumbImage(t)
                        }
                    } catch (e: Exception) {
                        Log.e("zuiweng", "", e)
                    }

                }
                val req = SendMessageToWX.Req().apply {
                    message = msg
                    this.scene = if (share_media == SHARE_MEDIA.WEIXIN_CIRCLE) SendMessageToWX.Req.WXSceneTimeline else SendMessageToWX.Req.WXSceneSession
                    this.transaction = "webPage"
                }
                api.sendReq(req)
            } else {
                Toast.makeText(this, R.string.share_wx_toast, Toast.LENGTH_SHORT).show()
            }
        }
        SHARE_MEDIA.FACEBOOK -> {
            try {
                this.startActivity(Intent("android.intent.action.SEND").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, webUrl)
                    `package` = "com.facebook.katana"
                })
            } catch (e: Exception) {
                shareWeb("https://www.facebook.com/sharer.php?u=${Uri.encode(webUrl)}")
            }
        }
        SHARE_MEDIA.LINKEDIN -> {
            try {
                this.startActivity(Intent("android.intent.action.SEND").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, webUrl)
                    `package` = "com.linkedin.android"
                })
            } catch (e: Exception) {
                shareWeb("https://www.linkedin.com/shareArticle?url=${Uri.encode(webUrl)}")
            }
        }
        SHARE_MEDIA.INSTAGRAM -> {
            runBackground(this.lifecycle) {
                try {
                    val file = File(Environment.getExternalStorageDirectory(), "instagram.jpg")
                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    val out = FileOutputStream(file)
                    BitmapFactory.decodeStream(imageFile.inputStream()).compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out.flush()
                    out.close()
                    this.startActivity(Intent("android.intent.action.SEND").apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        type = "image/*"
                        if (Build.VERSION.SDK_INT >= 24) {
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@share, "${this@share.packageName}.fileprovider", file))
                        } else {
                            putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
                        }
                        `package` = "com.instagram.android"
                    })
                } catch (e: Exception) {
                    Log.e("zuiweng", "", e)
                    runOnUiThread {
                        Toast.makeText(this@share, R.string.share_instagram_toast, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
        SHARE_MEDIA.TWITTER -> {
            try {
                this.startActivity(Intent("android.intent.action.SEND").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "$title \n $webUrl")
                    `package` = "com.twitter.android"
//                    component = android.content.ComponentName("com.twitter.android", "com.twitter.composer.ComposerActivity")
                })
            } catch (e: Exception) {
                shareWeb("https://twitter.com/intent/tweet?url=${Uri.encode(webUrl)}")
            }
        }
        SHARE_MEDIA.GOOGLEPLUS -> {
            try {
                this.startActivity(Intent("android.intent.action.SEND").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, webUrl)
                    `package` = "com.google.android.apps.plus"
                })
            } catch (e: Exception) {
                shareWeb("https://plus.google.com/share?url=${Uri.encode(webUrl)}")
            }
        }
    }
}

private val background by lazy { Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()) }

fun runBackground(lifecycle: Lifecycle, body: () -> Unit) {
    val future = background.submit {
        body.invoke()
    }
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            if (future.isDone) {
                future.cancel(true)
            }
        }
    })
}

fun Activity.shareWeb(url: String) {
    try {
        startActivity(Intent().apply {
            Log.e("zuiweng", url)
            action = Intent.ACTION_VIEW
            addCategory(Intent.CATEGORY_DEFAULT)
            addCategory(Intent.CATEGORY_BROWSABLE)
            type = "text/plain"
            data = Uri.parse(url)
        })
    } catch (e: Exception) {
    }
}