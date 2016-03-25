package com.trx.mobilesafe.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.base.BaseActivity;
import com.trx.mobilesafe.dao.AddressDao;
import com.trx.mobilesafe.utils.ToastUtils;

/**
 * Created by trx08 on 2016/3/15.
 */
public class AddressQueryActivity extends BaseActivity {

    private EditText etNumber;
    private TextView tvResult;

    @Override
    public void initView() {
        setContentView(R.layout.activity_address_query);

        etNumber = (EditText) findViewById(R.id.et_number);
        tvResult = (TextView)findViewById(R.id.tv_result);
    }

    @Override
    public void initListener() {
        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String number = s.toString().trim();
                if (!TextUtils.isEmpty(number)) {
                    String addressLocation = AddressDao.getAddressLocation(number);
                    tvResult.setText(addressLocation);
                }
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void processClick(View v) {

    }

    public void query(View v){
        String number = etNumber.getText().toString().trim();
        if (!TextUtils.isEmpty(number)) {
            String addressLocation = AddressDao.getAddressLocation(number);
            tvResult.setText(addressLocation);
        } else {
            /*实现edittext抖动效果*/
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            etNumber.startAnimation(shake);
        }
    }

    public void queryLocation(){

    }
}
