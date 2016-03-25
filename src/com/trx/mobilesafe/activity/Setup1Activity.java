package com.trx.mobilesafe.activity;

import android.content.Intent;
import android.view.View;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.base.BaseActivity;
import com.trx.mobilesafe.base.BaseSetupActivity;
import com.trx.mobilesafe.utils.LogUtils;

/**
 * Created by trx08 on 2016/3/15.
 */
public class Setup1Activity extends BaseSetupActivity {
    @Override
    public void initView() {
    	LogUtils.d(this, "Setup1Activity initView ");
        setContentView(R.layout.activity_setup1);
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

    @Override
    public void showNext() {
        startActivity(new Intent(this, Setup2Activity.class));

        finish();
        // 两个activity之间切换的动画, 应该放在finish之后运行
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    @Override
    public void showPrevious() {

    }
}
