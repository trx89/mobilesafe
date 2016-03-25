package com.trx.mobilesafe.utils;

import android.util.Log;

/**
 * Created by trx08 on 2016/3/5.
 */
public class LogUtils {

    private static int VERBOSE = 1;
    private static int DEBUG = 2;
    private static int INFORM = 3;
    private static int WARN = 4;
    private static int ERROR = 5;
    private static int NOTHING = 6;
    private static int LEVEL = VERBOSE;


    public static void v(Object tag, String text){
        if (LEVEL <= VERBOSE){
            Log.v(tag.getClass().getSimpleName(), text);
        }
    }

    public static void d(Object tag, String text){
        if (LEVEL <= DEBUG){
            Log.d(tag.getClass().getSimpleName(), text);
        }
    }

    public static void i(Object tag, String text){
        if (LEVEL <= INFORM){
            Log.i(tag.getClass().getSimpleName(), text);
        }
    }

    public static void w(Object tag, String text){
        if (LEVEL <= WARN){
            Log.w(tag.getClass().getSimpleName(), text);
        }
    }

    public static void e(Object tag, String text){
        if (LEVEL <= ERROR){
            Log.e(tag.getClass().getSimpleName(), text);
        }
    }
}
