package com.chinadaily.base;

import android.annotation.SuppressLint;

import com.chinadaily.http.HttpUtilsKt;
import com.chinadaily.vassonic.SonicRuntimeImpl;
import com.lzy.okgo.model.HttpHeaders;
import com.share.ShareConfigKt;
import com.tencent.mmkv.MMKV;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;

import org.litepal.LitePalApplication;

import cn.jpush.android.api.JPushInterface;

/**
 * BaseApp
 */

public class BaseApp extends LitePalApplication {
    @SuppressLint("StaticFieldLeak")
    private static BaseApp sInstance;
    private static String USER_AGENT;

    public void onCreate() {
        super.onCreate();
        sInstance = this;
        MMKV.initialize(this);
        USER_AGENT = HttpHeaders.getUserAgent();
        ShareConfigKt.initShare(this);
        HttpUtilsKt.initHttpUtils(this);
        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);
        JPushInterface.initCrashHandler(this);
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(this), new SonicConfig.Builder().build());
        }
    }

    public static BaseApp getInstance() {
        return sInstance;
    }

    public static String getUserAgent() {
        return USER_AGENT;
    }
}
