package com.trx.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.trx.mobilesafe.utils.PrefUtils;

/**
 * Created by trx08 on 2016/3/15.
 */
public class BootCompeleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean proctect = PrefUtils.getBoolean("protect", false, context);
        if (!proctect) {// 如果没有开启防盗保护,直接返回
            return;
        }

        String saveSim = PrefUtils.getString("bind_sim", null, context);
        if (!TextUtils.isEmpty(saveSim)) {
            // 获取当前sim卡,和保存的sim卡进行比对
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String currentSim = tm.getSimSerialNumber() + "xxx";// 当前sim卡,
            // 故意修改,模拟sim卡变化逻辑,方便测试
            if (!saveSim.equals(currentSim)) {
                System.out.println("sim卡已经变化!发送报警短信!");
                String safePhone = PrefUtils.getString("safe_phone", "",
                        context);

                // 需要权限: <uses-permission
                // android:name="android.permission.SEND_SMS" />
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(safePhone, null, "sim card changed!!!",
                        null, null);
            }
        }
    }
}
