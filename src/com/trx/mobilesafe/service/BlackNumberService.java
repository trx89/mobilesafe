package com.trx.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.trx.mobilesafe.dao.AddressDao;
import com.trx.mobilesafe.dao.BlackNumberDao;
import com.trx.mobilesafe.utils.LogUtils;

import java.lang.reflect.Method;

/**
 * Created by trx08 on 2016/3/20.
 */
public class BlackNumberService extends Service {

    private TelephonyManager mTm;
    private MyListener mListener;
    private BlackNumberDao mDao;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mDao = BlackNumberDao.getInstance(this);

        /*监听来电*/
        mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mListener = new MyListener();
        mTm.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);

        //监听短信
        InnerReceiver receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(Integer.MAX_VALUE);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, filter);

    }

    class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[])intent.getExtras().get("pdus");

            for (Object obj : objects) {// 超过140字节,会分多条短信发送
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);

                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();

                int mode = mDao.getMode(originatingAddress);
                if (0 != (mode & BlackNumberDao.MODE_SMS)){
                    abortBroadcast();
                    LogUtils.d(this, originatingAddress + " message is rejected");
                }
            }
        }
    }

    private void endCall(){
//        IBinder b = ServiceManager.getService(TELEPHONY_SERVICE);
//        ITelephonyManager service = ITelephonyManager.Stub.asInterface(b);

        /*通过反射得到service*/
        try {

            Class<?> object = Class.forName("android.os.ServiceManager");
            Method getService = object.getMethod("getService", String.class);

            IBinder b = (IBinder) getService.invoke(null, TELEPHONY_SERVICE);
            ITelephony service = ITelephony.Stub.asInterface(b);
            service.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    class MyListener extends PhoneStateListener{

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:

                    int mode = mDao.getMode(incomingNumber);
                    if (0 != (mode & BlackNumberDao.MODE_TELEPHONE)){
                        /*拦截电话*/
                        endCall();
                        LogUtils.d(this, incomingNumber+" phone is rejected");

                        /*创建内容观察者，观察通话记录是否变化*/
                        getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"),
                                true, new MyObeserver(new Handler(), incomingNumber));
                    }
                    break;

            }
        }
    }

    class MyObeserver extends ContentObserver{
        private String mIncomingNumber;

        public MyObeserver(Handler handler, String incomingNumber) {
            super(handler);
            mIncomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            //删除通话记录
            getContentResolver().delete(Uri.parse("content://call_log/calls"), "number=?",
                    new String[]{mIncomingNumber});

            LogUtils.d(this, mIncomingNumber + " call_log is rejected");
        }
    }
}
