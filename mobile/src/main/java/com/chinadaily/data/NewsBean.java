package com.chinadaily.data;

import android.text.TextUtils;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Staff on 2017/9/19.
 */

public class NewsBean extends LitePalSupport {

    public String title;
    public String headImage;

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
        this.bigTitleImage = headImage;
    }

    public String bigTitleImage;
    public String publishTime;
    public String jsonUrl;
    public String htmlUrl;

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
        if (TextUtils.isEmpty(this.murl)) {
            this.murl = htmlUrl;
        }
    }

    public String murl;
    public String description;
    public String dataId;
    public String dataType;
    public String subjectName;
    public String subjectCode;
    public String txyUrl;
    public String ytbUrl;
}
