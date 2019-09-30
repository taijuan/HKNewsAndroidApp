package com.chinadaily.googleCast

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import androidx.mediarouter.app.MediaRouteDialogFactory
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import com.chinadaily.R
import com.chinadaily.base.BaseApp
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadOptions
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.images.WebImage
import com.google.android.material.dialog.MaterialAlertDialogBuilder


object GoogleServiceUtils {

    fun isSupport(): Boolean {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(BaseApp.getInstance()) == ConnectionResult.SUCCESS
    }

    private fun isWifiEnable(): Boolean {
        val wifiManager = BaseApp.getInstance().applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        return wifiManager?.isWifiEnabled ?: false
    }

    private val instance: CastContext by lazy {
        CastContext.getSharedInstance(BaseApp.getInstance())
    }

    private fun getCastSession(): CastSession? {
        return instance.sessionManager.currentCastSession
    }

    private fun isConnected(): Boolean {
        return if (isSupport()) {
            getCastSession()?.isConnected ?: false
        } else {
            false
        }
    }

    private fun isSame(title: String): Boolean {
        return if (isConnected()) {
            getCastSession()?.remoteMediaClient?.currentItem?.media?.metadata?.getString(MediaMetadata.KEY_TITLE) == title
        } else {
            false
        }
    }

    private fun AppCompatActivity.showChooserDialogFragment() {
        MediaRouteDialogFactory.getDefault().onCreateChooserDialogFragment().apply {
            routeSelector = MediaRouteSelector.Builder()
                    .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
                    .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                    .build()
        }.show(supportFragmentManager, "showChooserDialogFragment")
    }

    private fun AppCompatActivity.showControllerDialogFragment() {
        MediaRouteDialogFactory.getDefault().onCreateControllerDialogFragment().apply {
            show(supportFragmentManager, "showControllerDialogFragment")
        }
    }

    private fun AppCompatActivity.showNotSupportGoogleServiceDialog() {
        MaterialAlertDialogBuilder(this, R.style.Theme_MaterialComponents_DayNight_Dialog)
                .setMessage("not support google cast!!!")
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    private fun AppCompatActivity.showWifiDialog() {
        MaterialAlertDialogBuilder(this, R.style.Theme_MaterialComponents_DayNight_Dialog)
                .setMessage("Please turn on wifi and connect")
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    @SuppressLint("RestrictedApi")
    fun AppCompatActivity.loadAndPlay(title: String = "", subtitle: String = "", imageUrls: List<String>, videoUrl: String) {
        when {
            !isSupport() -> showNotSupportGoogleServiceDialog()
            !isWifiEnable() -> showWifiDialog()
            getCastSession() == null -> showChooserDialogFragment()
            isSame(title) -> showControllerDialogFragment()
            else -> {
                getCastSession()?.remoteMediaClient?.apply {
                    val mediaMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE).apply {
                        putString(MediaMetadata.KEY_TITLE, title)
                        putString(MediaMetadata.KEY_SUBTITLE, subtitle)
                        for (imageUrl in imageUrls) {
                            addImage(WebImage(Uri.parse(imageUrl)))
                        }
                    }
                    val info = MediaInfo.Builder(videoUrl)
                            .setContentType("video/${videoUrl.split(".").last()}")
                            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                            .setMetadata(mediaMetadata)
                            .build()
                    this.load(info, MediaLoadOptions
                            .Builder()
                            .setAutoplay(true)
                            .setPlayPosition(0)
                            .build())
                }
                showControllerDialogFragment()
            }
        }
    }
}
