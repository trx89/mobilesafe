package com.trx.mobilesafe.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;

import java.util.List;

/**
 * Created by trx08 on 2016/3/16.
 */
public class ServiceStatusUtils {
    public static boolean isServiceRunning(Context context, String serviceName){

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(100);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices){
            String name = runningServiceInfo.service.getClassName();

            if (serviceName.equals(name)){
                return true;
        }
        }
        return false;

    }
}
