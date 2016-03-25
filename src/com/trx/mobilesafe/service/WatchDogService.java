package com.trx.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import com.trx.mobilesafe.activity.EnterPwdActivity;
import com.trx.mobilesafe.dao.AppLockDao;
import com.trx.mobilesafe.domain.AppLockInfo;
import com.trx.mobilesafe.domain.ProcessInfo;
import com.trx.mobilesafe.engine.ProcessInfoProvide;
import com.trx.mobilesafe.utils.LogUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

public class WatchDogService extends Service {

	private String mSkipName = "";
	private MyReceiver mReceiver;
	private ArrayList<String> infos = new ArrayList<String>();
	private AppLockDao mDao;
	private MyObserver mObserver;
	private boolean isRunning = true;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		mDao = AppLockDao.getInstance(this);
		infos = mDao.findAll();
		
		new Thread() {
			public void run() {
				while (isRunning) {
					// 获得当前屏幕展示的页面
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					String packageName = runningTasks.get(0).topActivity
							.getPackageName();
					if (infos.contains(packageName)
							&& !mSkipName.equals(packageName)) {
						Intent intent = new Intent(WatchDogService.this,
								EnterPwdActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("package", packageName);
						startActivity(intent);
					}

					LogUtils.d(this, "watchdog running");
					SystemClock.sleep(500);
				}
			}
		}.start();

		// 动态注册广播接收者
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.trx.mobilesafe.SKIP_CHECK");
		mReceiver = new MyReceiver();
		registerReceiver(mReceiver, filter);

		//注册内容观察者
		mObserver = new MyObserver(new Handler());
		getContentResolver().registerContentObserver(
				Uri.parse("content://com.trx.mobilesafe/applock"), true,
				mObserver);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		unregisterReceiver(mReceiver);
		mReceiver = null;
		
		getContentResolver().unregisterContentObserver(mObserver);
		mObserver = null;
		
		isRunning = false;
	}

	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			mSkipName = intent.getStringExtra("package");
		}

	}

	class MyObserver extends ContentObserver {

		public MyObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			
			LogUtils.d(this, "applock observer change");
			infos = mDao.findAll();
		}

	}
}
