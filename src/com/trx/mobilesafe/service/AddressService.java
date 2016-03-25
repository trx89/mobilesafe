package com.trx.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.dao.AddressDao;
import com.trx.mobilesafe.utils.LogUtils;
import com.trx.mobilesafe.utils.PrefUtils;

/**
 * Created by trx08 on 2016/3/16.
 */
public class AddressService extends Service {

    private TelephonyManager mTm;
    private MyListener mListener;

    private int startX;
    private int startY;

    private int mScreenWidth;
    private int mScreenHeight;

    private WindowManager mWm;
    private View mView;
    private InnerReceiver mReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*监听来电*/
        mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mListener = new MyListener();
        mTm.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);

        mWm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenWidth = mWm.getDefaultDisplay().getWidth();
        mScreenHeight = mWm.getDefaultDisplay().getHeight();

        /*监听去电*/
        mReceiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mReceiver, filter);

    }

    class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();
            String address = AddressDao.getAddressLocation(number);
            showLocationNotify(address);
        }
    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    /*电话铃响*/
                    String address = AddressDao.getAddressLocation(incomingNumber);
                    showLocationNotify(address);
                    LogUtils.d(this, "电话铃响");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    /*通话中*/
                    LogUtils.d(this, "通话中");

                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    /*电话挂断*/
                    LogUtils.d(this, "电话挂断");
                    if (null != mView){
                        mWm.removeView(mView);
                    }
                    break;

            }
        }
    }

    /**
     * 为了保证可触摸:
     *
     * 1. params.flags, 删掉FLAG_NOT_TOUCHABLE 2. params.type更改为TYPE_PHONE 3. 加权限
     *
     * 需要权限 <uses-permission
     * android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
     *
     * @param text
     */
    private void showLocationNotify(String address){

        // 初始化布局参数
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                //| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        params.gravity = Gravity.LEFT + Gravity.TOP;

// 初始化布局
        // mView = new TextView(this);
        // mView.setText(text);
        // mView.setTextColor(Color.RED);
        // mView.setTextSize(22);
        mView = View.inflate(this, R.layout.custom_toast, null);
        TextView tvAddress = (TextView) mView.findViewById(R.id.tv_address);

        // 取出保存的样式
        int style = PrefUtils.getInt("address_style", 0, this);
        // 背景图片id数组
        int[] bgIds = new int[] { R.drawable.call_locate_white,
                R.drawable.call_locate_orange, R.drawable.call_locate_blue,
                R.drawable.call_locate_gray, R.drawable.call_locate_green };
        // 更新背景图片
        tvAddress.setBackgroundResource(bgIds[style]);
        tvAddress.setText(address);

        // int x = params.x;//相对于默认重心的x偏移
        // 修改布局位置
        int lastX = PrefUtils.getInt("lastX", 0, this);
        int lastY = PrefUtils.getInt("lastY", 0, this);
        params.x = lastX;
        params.y = lastY;

        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        int dx = endX - startX;
                        int dy = endY - startY;

                        params.x += dx;
                        params.y += dy;

                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.x  > mScreenWidth - mView.getWidth()){
                            params.x = mScreenWidth - mView.getWidth();
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.y  > mScreenHeight - mView.getHeight()-25){
                            params.y = mScreenHeight - mView.getHeight() - 25;
                        }

                        // 更新窗口布局
                        mWm.updateViewLayout(mView, params);

                        startX = endX;
                        startY = endY;

                        break;

                    case MotionEvent.ACTION_UP:
                        PrefUtils.putInt("lastX", params.x, getApplicationContext());
                        PrefUtils.putInt("lastY", params.y, getApplicationContext());
                        break;
            }

            return true;
        }
    });

        mWm.addView(mView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mTm.listen(mListener, PhoneStateListener.LISTEN_NONE);
        mListener = null;

        unregisterReceiver(mReceiver);
        mReceiver = null;
    }
}
