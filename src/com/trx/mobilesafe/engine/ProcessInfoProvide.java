package com.trx.mobilesafe.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import android.R.anim;
import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.app.ActivityManager.MemoryInfo;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.domain.ProcessInfo;

public class ProcessInfoProvide {

	public static ArrayList<ProcessInfo> getRunningProcess(Context context) {
		ArrayList<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();

		ActivityManager aManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> lists = aManager.getRunningAppProcesses();

		PackageManager pm = context.getPackageManager();
		for (RunningAppProcessInfo runningAppProcessInfo : lists) {
			ProcessInfo info = new ProcessInfo();

			info.packageName = runningAppProcessInfo.processName;// 包名

			int pid = runningAppProcessInfo.pid;

			Debug.MemoryInfo[] processMemoryInfo = aManager
					.getProcessMemoryInfo(new int[] { pid });
			long memory = processMemoryInfo[0].getTotalPrivateDirty() * 1024;// 单位是kb
			info.memory = memory;

			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						info.packageName, 0);

				String name = applicationInfo.loadLabel(pm).toString();
				Drawable drawable = applicationInfo.loadIcon(pm);

				info.icon = drawable;
				info.name = name;

				int flags = applicationInfo.flags;
				if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					// 系统应用
					info.isUser = false;
				} else {
					info.isUser = true;
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				// 某些系统进程没有名称和图标,会走此异常
				info.name = info.packageName;
				info.icon = context.getResources().getDrawable(
						com.trx.mobilesafe.R.drawable.system_default);
				info.isUser = false;
				e.printStackTrace();
			}

			processInfos.add(info);
		}

		return processInfos;
	}

	public static int getRunningProcessNum(Context context) {
		ActivityManager aManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> lists = aManager.getRunningAppProcesses();

		return lists.size();
	}

	public static long getAvailMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		android.app.ActivityManager.MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);// 获取内存信息

		return outInfo.availMem;
	}

	public static long getTotalMemory() {

		BufferedReader reader = null;
		long memory = 0;
		try {
			File file = new File("/proc/meminfo");

			reader = new BufferedReader(new FileReader(file));
			
			String line = reader.readLine();
			char[] charArray = line.toCharArray();
			StringBuilder builder = new StringBuilder();
			
			for (char c : charArray){
				if (c >= '0' && c <= '9'){
					builder.append(c);
				}
			}
			
			memory = Long.parseLong(builder.toString()) * 1024;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return memory;
	}
	
	/**
	 * 清理后台所有进程
	 * 
	 * @param ctx
	 */
	public static void killAll(Context ctx) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();

		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			String packageName = runningAppProcessInfo.processName;

			if (packageName.equals(ctx.getPackageName())) {
				continue;
			}
			am.killBackgroundProcesses(packageName);
		}
	}
}
