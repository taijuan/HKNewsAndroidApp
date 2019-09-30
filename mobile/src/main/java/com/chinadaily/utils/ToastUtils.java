package com.chinadaily.utils;

import android.text.TextUtils;
import android.widget.Toast;

import com.chinadaily.base.BaseApp;
import com.lzy.okgo.utils.HttpUtils;

public class ToastUtils {

    private ToastUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void showShort(CharSequence msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        HttpUtils.runOnUiThread(() -> Toast.makeText(BaseApp.getInstance(), msg, Toast.LENGTH_SHORT).show());
    }
}
