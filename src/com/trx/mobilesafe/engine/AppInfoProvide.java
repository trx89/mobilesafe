package com.trx.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.provider.Settings.System;

import com.trx.mobilesafe.domain.AppInfo;

public class AppInfoProvide {

	public static ArrayList<AppInfo> getInstallApps(Context context){
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		
		ArrayList<AppInfo> appLists = new ArrayList<AppInfo>();
			
		for (PackageInfo info : installedPackages){
			AppInfo appInfo = new AppInfo();
			
			String packageName = info.packageName;
			ApplicationInfo applicationInfo = info.applicationInfo;
			String name = applicationInfo.loadLabel(pm).toString();
			Drawable drawable = applicationInfo.loadIcon(pm);
			int uid = applicationInfo.uid;
			
			appInfo.icon = drawable;
			appInfo.packageName = packageName;
			appInfo.name = name + uid;
			
			int flags = applicationInfo.flags;
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) 
					== ApplicationInfo.FLAG_EXTERNAL_STORAGE){
				//在sd卡中
				appInfo.isRom = false;
			}else {
				appInfo.isRom = true;
			}
			
			if ((flags & ApplicationInfo.FLAG_SYSTEM) 
					== ApplicationInfo.FLAG_SYSTEM){
				//系统应用
				appInfo.isUser = false;
			}else {
				appInfo.isUser = true;
			}
			
			appLists.add(appInfo);
		}
		
		return appLists;
	}
}
