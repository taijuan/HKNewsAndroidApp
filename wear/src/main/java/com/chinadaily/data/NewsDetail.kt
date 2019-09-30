package com.chinadaily.data

import com.alibaba.fastjson.annotation.JSONField

data class NewsDetail(val title: String, @JSONField(name = "text") val content: String)