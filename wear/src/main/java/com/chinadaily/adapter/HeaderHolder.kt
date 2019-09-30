package com.chinadaily.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chinadaily.utils.headerOrFooterHeight

class HeaderHolder(parent: ViewGroup) : RecyclerView.ViewHolder(TextView(parent.context).apply {
    this.layoutParams = ViewGroup.LayoutParams(-1, headerOrFooterHeight())
})

