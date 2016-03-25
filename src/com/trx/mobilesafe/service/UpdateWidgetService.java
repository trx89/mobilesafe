package com.trx.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.trx.mobilesafe.utils.LogUtils;
import com.trx.mobilesafe.R;
import com.trx.mobilesafe.engine.ProcessInfoProvide;
import com.trx.mobilesafe.receiver.MyWidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.RemoteViews.RemoteView;

public class UpdateWidgetService extends Service {

	private AppWidgetManager mAwm;
	private Timer mTimer;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		mAwm = AppWidgetManager.getInstance(this);

		startTimer();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		mTimer.cancel();
		mTimer = null;
	}

	public void startTimer() {
		mTimer = new Timer();

		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				UpdateWidget();
			}
		}, 0, 5000);
	}

	public void UpdateWidget() {
		LogUtils.d(this, "update widget");

		ComponentName provider = new ComponentName(this, MyWidget.class);
		
		RemoteViews views = new RemoteViews(getPackageName(),
				R.layout.process_widget);

		
		views.setTextViewText(R.id.tv_running_num, "正在运行进程："
				+ ProcessInfoProvide.getRunningProcessNum(this));
		views.setTextViewText(
				R.id.tv_avail_memory,
				"可用内存："
						+ Formatter.formatFileSize(this,
								ProcessInfoProvide.getAvailMemory(this)));
		
		//跳到主页面
		Intent intent = new Intent();
		intent.setAction("com.trx.mobilesafe.HOME");
		PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.ll_root, pendIntent);
		
		//一键清理
		intent = new Intent();
		intent.setAction("com.trx.mobilesafe.KILLALL");
		pendIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.btn_clear, pendIntent);
		
		
		mAwm.updateAppWidget(provider, views);
		
	}
}
