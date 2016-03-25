package com.trx.mobilesafe.activity;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.base.BaseActivity;
import com.trx.mobilesafe.base.BaseSetupActivity;
import com.trx.mobilesafe.utils.PrefUtils;

/**
 * Created by trx08 on 2016/3/15.
 */
public class Setup4Activity extends BaseSetupActivity {
    private CheckBox cbProtect;

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup4);

        cbProtect = (CheckBox) findViewById(R.id.cb_protect);
    }

    @Override
    public void initListener() {
        cbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    cbProtect.setText("防盗保护已经开启");
                    PrefUtils.putBoolean("protect", true,
                            getApplicationContext());
                } else {
                    cbProtect.setText("您没有开启防盗保护");
                    PrefUtils.putBoolean("protect", false,
                            getApplicationContext());
                }
            }
        });
    }

    @Override
    public void initData() {
        boolean protect = PrefUtils.getBoolean("protect", false, this);
        if (protect) {
            cbProtect.setChecked(true);
            cbProtect.setText("防盗保护已经开启");
        } else {
            cbProtect.setChecked(false);
            cbProtect.setText("您没有开启防盗保护");
        }
    }

    @Override
    public void processClick(View v) {

    }

    @Override
    public void showNext() {
        PrefUtils.putBoolean("configed", true, this);
        startActivity(new Intent(this, LostAndFindActivity.class));

        finish();
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    @Override
    public void showPrevious() {
    	startActivity(new Intent(this, Setup3Activity.class));

        finish();
        overridePendingTransition(R.anim.anim_previous_in, R.anim.anim_previous_out);
    }

}
