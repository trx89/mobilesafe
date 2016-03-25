package com.trx.mobilesafe.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.trx.mobilesafe.activity.ContactActivity;
import com.trx.mobilesafe.activity.Setup4Activity;
import com.trx.mobilesafe.utils.ToastUtils;
import com.trx.mobilesafe.utils.PrefUtils;
import com.trx.mobilesafe.R;
import com.trx.mobilesafe.base.BaseActivity;
import com.trx.mobilesafe.base.BaseSetupActivity;

/**
 * Created by trx08 on 2016/3/15.
 */
public class Setup3Activity extends BaseSetupActivity {
	
	private EditText etPhone;
    @Override
    public void initView() {
        setContentView(R.layout.activity_setup3);
        
        etPhone = (EditText) findViewById(R.id.et_phone);
		String phone = PrefUtils.getString("safe_phone", "", this);
		etPhone.setText(phone);
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
    	String phone = etPhone.getText().toString().trim();
		if (!TextUtils.isEmpty(phone)) {
			PrefUtils.putString("safe_phone", phone, this);

			startActivity(new Intent(this, Setup4Activity.class));
			finish();
			// 两个activity之间切换的动画, 应该放在finish之后运行
			overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
		} else {
			ToastUtils.showToast(this, "安全号码不能为空!");
		}
    }

    @Override
    public void showPrevious() {
        startActivity(new Intent(this, Setup2Activity.class));

        finish();
        overridePendingTransition(R.anim.anim_previous_in, R.anim.anim_previous_out);
    }
    
    public void selectContact(View view) {
		startActivityForResult(new Intent(this, ContactActivity.class), 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {// 避免用户直接返回,导致空指针异常
			String phone = data.getStringExtra("phone");
			phone = phone.replaceAll("-", "").replaceAll(" ", "");// 去掉所有-和空格
			etPhone.setText(phone);
		}
	}
}
