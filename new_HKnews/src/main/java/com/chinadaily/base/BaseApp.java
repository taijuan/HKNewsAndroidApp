package com.chinadaily.base;

import android.util.Log;

import com.chinadaily.http.HttpUtilsKt;
import com.chinadaily.vassonic.SonicRuntimeImpl;
import com.lzy.okgo.model.HttpHeaders;
import com.share.ShareConfigKt;
import com.taobao.sophix.SophixManager;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;

import org.litepal.LitePalApplication;

import cn.jpush.android.api.JPushInterface;

/**
 * BaseApp
 */

public class BaseApp extends LitePalApplication {
    private static BaseApp sInstance;
    private static String USER_AGENT;

    public void onCreate() {
        super.onCreate();
        sInstance = this;
        USER_AGENT = HttpHeaders.getUserAgent();
        ShareConfigKt.initShare(this);
        initHotfix();
        HttpUtilsKt.initHttpUtils(this);
        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);
        JPushInterface.initCrashHandler(this);
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(this), new SonicConfig.Builder().build());
        }
    }

    private void initHotfix() {
        String appVersion;
        try {
            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (Exception e) {
            appVersion = "1.0.0";
        }

        SophixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub((mode, code, info, handlePatchVersion) -> {
                    String msg = "" + "Mode:" + mode +
                            " Code:" + code +
                            " Info:" + info +
                            " HandlePatchVersion:" + handlePatchVersion;
                    Log.e("zuiweng", msg);
                }).initialize();
    }

    public static BaseApp getInstance() {
        return sInstance;
    }

    public static String getUserAgent() {
        return USER_AGENT;
    }
}
