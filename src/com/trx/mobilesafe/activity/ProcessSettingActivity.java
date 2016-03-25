package com.trx.mobilesafe.activity;

import com.trx.mobilesafe.service.AutoKillService;
import com.trx.mobilesafe.utils.ServiceStatusUtils;
import com.trx.mobilesafe.R;
import com.trx.mobilesafe.utils.PrefUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSettingActivity extends Activity{
	
	private CheckBox cbShowSys;
	private CheckBox cbAutoKill;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_process_setting);
		
		cbShowSys = (CheckBox)findViewById(R.id.cb_show_system);
		cbAutoKill = (CheckBox)findViewById(R.id.cb_auto_kill);
		
		boolean isChecked = PrefUtils.getBoolean("show_sys_checked", true, this);
		cbShowSys.setChecked(isChecked);
		if (isChecked){
			cbShowSys.setText("显示系统进程");
		}else {
			cbShowSys.setText("不显示系统进程");
		}

		cbShowSys.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				cbShowSys.setChecked(isChecked);
				if (isChecked){
					cbShowSys.setText("显示系统进程");
				}else {
					cbShowSys.setText("不显示系统进程");
				}
				
				PrefUtils.putBoolean("show_sys_checked", isChecked, ProcessSettingActivity.this);
			}
			
			
		});

		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"com.trx.mobilesafe.service.AutoKillService");
		if (serviceRunning) {
			cbAutoKill.setChecked(true);
			cbAutoKill.setText("锁屏清理已开启");
		} else {
			cbAutoKill.setChecked(false);
			cbAutoKill.setText("锁屏清理已关闭");
		}

		cbAutoKill.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Intent service = new Intent(getApplicationContext(),
						AutoKillService.class);
				if (isChecked) {
					cbAutoKill.setText("锁屏清理已开启");
					startService(service);
				} else {
					cbAutoKill.setText("锁屏清理已关闭");
					stopService(service);
				}
			}
		});
	}

}
