package com.trx.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.base.BaseActivity;
import com.trx.mobilesafe.service.AddressService;
import com.trx.mobilesafe.service.BlackNumberService;
import com.trx.mobilesafe.service.WatchDogService;
import com.trx.mobilesafe.utils.PrefUtils;
import com.trx.mobilesafe.utils.ServiceStatusUtils;
import com.trx.mobilesafe.view.SettingItemClickView;
import com.trx.mobilesafe.view.SettingItemView;

public class SettingActivity extends BaseActivity {

    private SettingItemView sivAutoUpdate;
    private SettingItemView sivAddress;
    private SettingItemClickView sicStyle;
    private SettingItemClickView sicLocation;
    private SettingItemView sivBlackNumber;
    private SettingItemView sivApplock;
    
    private static final String ADDRESS_SERVICE = "com.trx.mobilesafe.service.AddressService";
    private static final String BLACK_NUMBER_SERVICE = "com.trx.mobilesafe.service.BlackNumberService";
    private static final String WATCH_DOG_SERVICE = "com.trx.mobilesafe.service.WatchDogService";
    
    private String[] mItems = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };


    @Override
    public void initView() {
        setContentView(R.layout.activity_setting);

        sivAutoUpdate = (SettingItemView) findViewById(R.id.siv_auto_update);
        sivAddress = (SettingItemView) findViewById(R.id.siv_address);
        sicStyle = (SettingItemClickView) findViewById(R.id.sic_style);
        sicLocation = (SettingItemClickView) findViewById(R.id.sic_location);
        sivBlackNumber = (SettingItemView)findViewById(R.id.siv_black_number);
        sivApplock = (SettingItemView)findViewById(R.id.siv_app_lock);

    }

    @Override
    public void initListener() {
        sivAutoUpdate.setOnClickListener(this);
        sivAddress.setOnClickListener(this);
        sicStyle.setOnClickListener(this);
        sicLocation.setOnClickListener(this);
        sivBlackNumber.setOnClickListener(this);
        sivApplock.setOnClickListener(this);
    }

    @Override
    public void initData() {
        initAutoUpdate();
        initAddress();
        initAddressStyle();
        initAddressLocation();
        initBlackNumber();
        initApplock();
    }

    @Override
    public void processClick(View v) {
        Boolean isChecked;
        Intent intent;
        switch (v.getId()) {
            case R.id.siv_auto_update:
                isChecked = sivAutoUpdate.isChecked();
                sivAutoUpdate.setChecked(!isChecked);

                PrefUtils.putBoolean("auto_update", !isChecked, SettingActivity.this);
                break;
            case R.id.siv_address:
                isChecked = sivAddress.isChecked();
                sivAddress.setChecked(!isChecked);

                intent = new Intent(this, AddressService.class);
                if (isChecked){
                    stopService(intent);
                }else {
                    startService(intent);
                }
                break;
            case R.id.sic_style:
                showStyleSelectDialog();
                break;
            case R.id.sic_location:
                startActivity(new Intent(this, DragViewActivity.class));
                break;
            case R.id.siv_black_number:
                isChecked = sivBlackNumber.isChecked();
                sivBlackNumber.setChecked(!isChecked);

                intent = new Intent(this, BlackNumberService.class);
                if (isChecked){
                    stopService(intent);
                }else {
                    startService(intent);
                }
                break;
            case R.id.siv_app_lock:
            	isChecked = sivApplock.isChecked();
                sivApplock.setChecked(!isChecked);
                
                intent = new Intent(this, WatchDogService.class);
                if (isChecked){
                    stopService(intent);
                }else {
                    startService(intent);
                }
                break;

        }
    }

    /*初始化自动更新*/
    private void initAutoUpdate() {
        Boolean bAutoUpdate = PrefUtils.getBoolean("auto_update", false, SettingActivity.this);
        sivAutoUpdate.setChecked(bAutoUpdate);
    }

    private void initAddress() {
        boolean isRunning = ServiceStatusUtils
                .isServiceRunning(this, ADDRESS_SERVICE);

        sivAddress.setChecked(isRunning);
    }

    private void initAddressStyle(){
        sicStyle.setTitle("归属地提示框风格");

        int style = PrefUtils.getInt("address_style", 0, this);
        sicStyle.setDesc(mItems[style]);
    }

    private void initAddressLocation(){
        sicLocation.setTitle("归属地提示框位置");
        sicLocation.setDesc("设置归属地提示框的显示位置");
    }

    private void initBlackNumber(){
        boolean isRunning = ServiceStatusUtils
                .isServiceRunning(this, BLACK_NUMBER_SERVICE);

        sivBlackNumber.setChecked(isRunning);
    }
    
    private void initApplock(){
    	boolean isRunning = ServiceStatusUtils
                .isServiceRunning(this, WATCH_DOG_SERVICE);

        sivApplock.setChecked(isRunning);
    }

    private void showStyleSelectDialog(){
        // 获取已保存的样式
        int style = PrefUtils.getInt("address_style", 0, this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("归属地提示框风格");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setSingleChoiceItems(mItems, style,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 保存当前风格
                        PrefUtils.putInt("address_style", which,
                                getApplicationContext());
                        dialog.dismiss();

                        sicStyle.setDesc(mItems[which]);// 更新描述
                    }

                });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

}
