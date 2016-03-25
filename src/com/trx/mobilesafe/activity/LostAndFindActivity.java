package com.trx.mobilesafe.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trx.mobilesafe.utils.LogUtils;
import com.trx.mobilesafe.R;
import com.trx.mobilesafe.base.BaseActivity;
import com.trx.mobilesafe.utils.PrefUtils;

/**
 * Created by trx08 on 2016/3/14.
 */
public class LostAndFindActivity extends BaseActivity {
    @Override
    public void initView() {
        boolean configed = PrefUtils.getBoolean("configed", false, this);

        LogUtils.d(this, "LostAndFindActivity initView " + configed);
        if (!configed){
            startActivity(new Intent(this, Setup1Activity.class));
            finish();
        }else{
            setContentView(R.layout.activity_lost_and_find);
            TextView tvPhone = (TextView) findViewById(R.id.tv_safe_phone);
            ImageView ivLock = (ImageView) findViewById(R.id.iv_lock);

            String phone = PrefUtils.getString("safe_phone", "", this);
            tvPhone.setText(phone);

            boolean protect = PrefUtils.getBoolean("protect", false, this);
            if (protect) {
                ivLock.setImageResource(R.drawable.lock);
            } else {
                ivLock.setImageResource(R.drawable.unlock);
            }
        }
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void processClick(View v) {

    }

    public void reSetup(View v){
        startActivity(new Intent(this, Setup1Activity.class));
        finish();
    }
}
