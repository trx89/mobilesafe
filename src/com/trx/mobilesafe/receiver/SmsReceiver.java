package com.trx.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.trx.mobilesafe.service.LocationService;

/**
 * Created by trx08 on 2016/3/15.
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] objects = (Object[])intent.getExtras().get("pdus");

        for (Object obj : objects) {// 超过140字节,会分多条短信发送
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);

            String originatingAddress = sms.getOriginatingAddress();
            String messageBody = sms.getMessageBody();

            System.out.println("号码:" + originatingAddress + ";内容:"
                    + messageBody);

            if ("#*alarm*#".equals(messageBody)) {
                // 播放报警音乐
                System.out.println("播放报警音乐");

                // 4.4+版本上,无法拦截短信, 调此方法没有, 比如当前应用时默认短信应用才可以
                // 操作短信数据库, 删除数据库相关短信内容, 间接达到删除短信目的
                abortBroadcast();// 中断短信传递
            } else if ("#*location*#".equals(messageBody)) {
                System.out.println("手机定位");
                // 启动位置监听的服务
                context.startService(new Intent(context, LocationService.class));

                abortBroadcast();// 中断短信传递
            } else if ("#*lockscreen*#".equals(messageBody)) {
                System.out.println("一键锁屏");
                abortBroadcast();// 中断短信传递
            } else if ("#*wipedata*#".equals(messageBody)) {
                System.out.println("清除数据");
                abortBroadcast();// 中断短信传递
            }
        }
    }
}
