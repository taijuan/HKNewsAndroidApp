package com.chinadaily.utils;

import android.content.SharedPreferences;

import com.chinadaily.base.BaseApp;
import com.tencent.mmkv.MMKV;

import static android.content.Context.MODE_PRIVATE;

public class SPUtils {
    private static final String FILE_NAME = SPUtils.class.getName();
    private static MMKV mmkv = MMKV.mmkvWithID(FILE_NAME);

    static {
        SharedPreferences old_man = BaseApp.getInstance().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        mmkv.importFromSharedPreferences(old_man);
        old_man.edit().clear().apply();
    }

    public static void put(String key, Object object) {
        if (object instanceof String) {
            mmkv.encode(key, (String) object);
        } else if (object instanceof Integer) {
            mmkv.encode(key, (Integer) object);
        } else if (object instanceof Boolean) {
            mmkv.encode(key, (Boolean) object);
        } else if (object instanceof Float) {
            mmkv.encode(key, (Float) object);
        } else if (object instanceof Long) {
            mmkv.encode(key, (Long) object);
        } else {
            mmkv.encode(key, object.toString());
        }
    }

    public static void remove(String key) {
        mmkv.removeValueForKey(key);
    }

    public static String getString(String key, String defaultObject) {
        return mmkv.getString(key, defaultObject);
    }

    public static boolean getBoolean(String key, boolean defaultObject) {
        return mmkv.getBoolean(key, defaultObject);
    }

    public static int getInt(String key, int defaultObject) {
        return mmkv.getInt(key, defaultObject);
    }
}
