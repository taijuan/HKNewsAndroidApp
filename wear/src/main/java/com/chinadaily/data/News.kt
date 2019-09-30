package com.chinadaily.data

import com.alibaba.fastjson.annotation.JSONField

/**
 * Created by Staff on 2017/9/19.
 */

data class News(
        @JSONField(name = "dataId") val id: String,
        val title: String,
        @JSONField(name = "murl") val url: String,
        val bigTitleImage: String,
        val jsonUrl: String
)
