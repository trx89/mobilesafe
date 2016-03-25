package com.trx.mobilesafe.receiver;

import com.trx.mobilesafe.engine.ProcessInfoProvide;
import com.trx.mobilesafe.utils.LogUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 一键清理广播
 * 
 * @author Kevin
 * 
 */
public class KillReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.d(this, "receiver killall broadcast");
		ProcessInfoProvide.killAll(context);
	}

}
