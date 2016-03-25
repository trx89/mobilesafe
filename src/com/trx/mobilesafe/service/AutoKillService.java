package com.trx.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.trx.mobilesafe.engine.ProcessInfoProvide;

/**
 * 锁屏清理
 * 
 * @author Kevin
 * 
 */
public class AutoKillService extends Service {

	private InnerScreenOffReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 注册广播监听屏幕关闭
		mReceiver = new InnerScreenOffReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 注销广播
		unregisterReceiver(mReceiver);
		mReceiver = null;

	}

	class InnerScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ProcessInfoProvide.killAll(context);
		}

	}
}
