package com.chinadaily.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.chinadaily.base.BaseApp;

public class SPUtils {
    private static final String FILE_NAME = SPUtils.class.getName();

    public static void put(String key, Object object) {
        SharedPreferences sp = BaseApp.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.apply();
    }

    public static void remove(String key) {
        SharedPreferences sp = BaseApp.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(key).apply();
    }

    public static String getString(String key, String defaultObject) {
        SharedPreferences sp = BaseApp.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultObject);
    }

    public static boolean getBoolean(String key, boolean defaultObject) {
        SharedPreferences sp = BaseApp.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultObject);
    }

    public static int getInt(String key, int defaultObject) {
        SharedPreferences sp = BaseApp.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultObject);
    }
}
