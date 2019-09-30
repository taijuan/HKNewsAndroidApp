package com.chinadaily.update

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import android.text.Html
import android.text.TextUtils
import android.view.View
import com.chinadaily.BuildConfig
import com.chinadaily.R
import com.chinadaily.base.BaseApp
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Progress
import com.lzy.okserver.OkDownload
import com.lzy.okserver.download.DownloadListener
import com.lzy.okserver.download.DownloadTask
import kotlinx.android.synthetic.main.dialog_update_version.*
import java.io.File

@Suppress("DEPRECATION")
class UpdateVersionDialog(context: Context, private var appVersion: AppVersion) : Dialog(context, R.style.AppTheme_Alert_Fullscreen) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_update_version)
        setCanceledOnTouchOutside(false)
        versionName.text = "${string(R.string.discover_new_version)}\n(${appVersion.versionName})"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            versionUpdateContent.text = Html.fromHtml(appVersion.versionDescEn, Html.FROM_HTML_MODE_LEGACY)
        } else {
            versionUpdateContent.text = Html.fromHtml(appVersion.versionDescEn)
        }
        cancel.visibility = if (appVersion.type == "1") View.GONE else View.VISIBLE
        val downloadListener = object : DownloadListener(appVersion.versionPath) {
            override fun onFinish(t: File?, progress: Progress?) {
                update.text = string(R.string.install_the_application)
                t?.installApk()
            }

            override fun onRemove(progress: Progress?) {
            }

            override fun onProgress(progress: Progress?) {
                update.text = string(R.string.loading)
                progress?.let {
                    progress_bar.progress = (it.fraction * 10000).toInt()
                }
            }

            override fun onError(progress: Progress?) {
                progress?.let {
                    OkDownload.restore(it).register(this).start()
                }

            }

            override fun onStart(progress: Progress?) {
                progress_bar.show()
            }

        }
        if (OkDownload.getInstance().getTask(appVersion.versionName)?.progress?.status == Progress.FINISH) {
            update.text = string(R.string.install_the_application)
            progress_bar.progress = 10000
        } else {
            progress_bar.visibility = View.GONE
            update.text = string(R.string.update)
        }
        cancel.setOnClickListener { dismiss() }
        update.setOnClickListener {
            if (update.text.toString() == string(R.string.loading)) {
                return@setOnClickListener
            }
            val task: DownloadTask? = OkDownload.getInstance().getTask(appVersion.versionName)
            if (task?.progress?.status == Progress.FINISH && task.progress?.filePath?.isExists() != true) {
                task.register(downloadListener).restart()
                progress_bar.visibility = View.VISIBLE
            } else {
                OkDownload.request(appVersion.versionName, OkGo.get(appVersion.versionPath))
                        .register(downloadListener)
                        .save()
                        .start()
                progress_bar.visibility = View.VISIBLE
            }
        }
    }

    override fun dismiss() {
        super.dismiss()
        OkDownload.getInstance().getTask(appVersion.versionName)?.unRegister(appVersion.versionName)
        OkDownload.getInstance().getTask(appVersion.versionName)?.pause()
    }

    override fun onBackPressed() {

    }
}

fun string(@StringRes res: Int) = BaseApp.getInstance().getString(res) ?: ""
fun File.installApk() {
    if (this.exists()) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(FileProvider.getUriForFile(BaseApp.getInstance(), "${BuildConfig.APPLICATION_ID}.fileprovider", this), "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(this), "application/vnd.android.package-archive")
        }
        BaseApp.getInstance().startActivity(intent)
    }
}

fun String.isExists(): Boolean {
    return if (TextUtils.isEmpty(this)) {
        false
    } else {
        File(this).exists()
    }
}