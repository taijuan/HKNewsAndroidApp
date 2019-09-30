package com.share

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.dialog_share.*
import java.io.File

class ShareDialog(var activity: AppCompatActivity, var title: String = "", var description: String = "", var imageFile: File, var webUrl: String = "") : Dialog(activity, R.style.AppTheme_Alert_Fullscreen) {

    private val icons = arrayListOf(R.drawable.share_facebook, R.drawable.share_twitter, R.drawable.share_linkedin, R.drawable.share_instagram, R.drawable.share_wechat, R.drawable.share_wxcircle)
    private val names = arrayListOf(R.string.share_facebook, R.string.share_twitter, R.string.share_linked_in, R.string.share_instagram, R.string.share_wechat, R.string.share_wechat_moment)
    private val data = arrayListOf(SHARE_MEDIA.FACEBOOK, SHARE_MEDIA.TWITTER, SHARE_MEDIA.LINKEDIN, SHARE_MEDIA.INSTAGRAM, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_share)
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission_group.STORAGE) != 0) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }
        Log.e("zuiweng", webUrl)
        window?.setLayout(activity.resources.displayMetrics.widthPixels, -2)
        window?.decorView?.setPadding(0, 0, 0, 0)
        window?.setGravity(Gravity.BOTTOM)
        cancel.setOnClickListener { dismiss() }
        recyclerView.layoutManager = GridLayoutManager(activity, 4)
        recyclerView.adapter = object : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder = object : androidx.recyclerview.widget.RecyclerView.ViewHolder(LinearLayoutCompat(activity).apply {
                orientation = LinearLayoutCompat.VERTICAL
                gravity = Gravity.CENTER
                setPadding(0, 16f.dip2px(),0, 0)
                addView(ImageView(context))
                addView(TextView(context).apply {
                    setPadding(0, 8f.dip2px(), 0, 0)
                    textSize = 14f
                    gravity = Gravity.CENTER
                    setSingleLine()
                })
            }) {}

            override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
                val item = holder.itemView as LinearLayoutCompat
                val iconView = item.getChildAt(0) as ImageView
                val nameView = item.getChildAt(1) as TextView
                iconView.setImageResource(icons[position])
                nameView.setText(names[position])
                item.setOnClickListener {
                    dismiss()
                    activity.share(data[position], this@ShareDialog.title, this@ShareDialog.description, this@ShareDialog.imageFile, this@ShareDialog.webUrl)
                }
            }

            override fun getItemCount(): Int = data.size
        }
    }

    fun Float.dip2px(): Int {
        val scale = activity.resources.displayMetrics.density
        return (this * scale).toInt()
    }
}