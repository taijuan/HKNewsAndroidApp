package com.chinadaily.update

import com.alibaba.fastjson.annotation.JSONField

/**
 * Created by Staff on 2017/5/19.
 */

class AppVersion {
    init {

    }

    var id: String? = null
    var type: String? = null           // 1强制更新，0非强制更新
    @JSONField(name = "version_name")
    var versionName: String? = null
    @JSONField(name = "version_code")
    var versionCode: Int = 0   //版本
    @JSONField(name = "version_descEn")
    var versionDescEn: String? = null  //英文描述
    @JSONField(name = "version_path")
    var versionPath: String? = null    //香港下载地址
}
