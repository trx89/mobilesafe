package com.trx.mobilesafe.activity;

import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.base.BaseActivity;
import com.trx.mobilesafe.base.BaseSetupActivity;
import com.trx.mobilesafe.utils.PrefUtils;
import com.trx.mobilesafe.utils.ToastUtils;
import com.trx.mobilesafe.view.SettingItemView;

/**
 * Created by trx08 on 2016/3/15.
 */
public class Setup2Activity extends BaseSetupActivity {
    private SettingItemView sivBind;

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup2);

        sivBind = (SettingItemView) findViewById(R.id.siv_bind);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        String bindSim = PrefUtils.getString("bind_sim", null, this);
        if (TextUtils.isEmpty(bindSim)) {
            sivBind.setChecked(false);
        } else {
            sivBind.setChecked(true);
        }

        sivBind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sivBind.isChecked()) {
                    sivBind.setChecked(false);
                    PrefUtils.remove("bind_sim", getApplicationContext());
                } else {
                    sivBind.setChecked(true);
                    // 初始化电话管理器
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();// 获取sim卡序列号,需要权限:android.permission.READ_PHONE_STATE
                    
                    //真机中获取simSerialNumber失败
                    if (TextUtils.isEmpty(simSerialNumber)){
                    	simSerialNumber = "1234";
                    }
                    
                    PrefUtils.putString("bind_sim", simSerialNumber,
                            getApplicationContext());// 保存sim卡序列号
                }
            }
        });
    }

    @Override
    public void processClick(View v) {

    }

    @Override
    public void showNext() {
        // 判断是否已经绑定sim卡,只有绑定,才能进行下一步操作
        String bindSim = PrefUtils.getString("bind_sim", null, this);
        if (TextUtils.isEmpty(bindSim)) {
            ToastUtils.showToast(this, "必须绑定sim卡!");
            return;
        }

        startActivity(new Intent(this, Setup3Activity.class));

        finish();
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    @Override
    public void showPrevious() {
        startActivity(new Intent(this, Setup1Activity.class));

        finish();
        overridePendingTransition(R.anim.anim_previous_in, R.anim.anim_previous_out);
    }
}
