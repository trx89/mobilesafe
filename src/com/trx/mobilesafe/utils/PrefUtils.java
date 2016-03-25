package com.trx.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by trx08 on 2016/3/14.
 */
public class PrefUtils {

    private static SharedPreferences getPref(Context context){
        return context.getSharedPreferences("config", Context.MODE_PRIVATE);
    }
    public static void putString(String key, String value, Context context){
        getPref(context).edit().putString(key, value).commit();
    }

    public static void putInt(String key, int value, Context context){
        getPref(context).edit().putInt(key, value).commit();
    }

    public static void putBoolean(String key, boolean value, Context context){
        getPref(context).edit().putBoolean(key, value).commit();
    }

    public static String getString(String key, String defVal, Context context){
        return getPref(context).getString(key, defVal);
    }
    public static int getInt(String key, int defVal, Context context){
        return getPref(context).getInt(key, defVal);
    }
    public static Boolean getBoolean(String key, boolean defVal, Context context){
        return getPref(context).getBoolean(key, defVal);
    }

    public static void remove(String key, Context context){
        getPref(context).edit().remove(key);
    }
}
