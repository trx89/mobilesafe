package com.trx.mobilesafe.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.base.BaseActivity;
import com.trx.mobilesafe.utils.LogUtils;
import com.trx.mobilesafe.utils.SmsUtils;
import com.trx.mobilesafe.utils.SmsUtils.SmsCallback;

public class AToolsActivity extends BaseActivity {
	
	@Override
	public void initView() {
		setContentView(R.layout.activity_atools);
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

	/**
	 * 电话归属地
	 */
	public void addressQuery(View view) {
		startActivity(new Intent(this, AddressQueryActivity.class));
	}
	
	/**
	 *  短信备份
	 */
	public void smsBackup(View view) {
		final ProgressDialog dialog = new ProgressDialog(this);
		
		dialog.setTitle("正在备份短信...");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		
		new Thread(){
			public void run(){
				FileOutputStream outputStream = null;
				File file = new File(getFilesDir(), "sms.xml");
				try {
					outputStream = new FileOutputStream(file);
					
					SmsUtils.backup(getContentResolver(), outputStream, new SmsCallback() {
						
						@Override
						public void smsPreBackup(int max) {
							// TODO Auto-generated method stub
							dialog.setMax(max);
						
							LogUtils.d(this, "smsPreBackup "+ max);
						}
						
						@Override
						public void smsOnBackup(int count, int max) {
							// TODO Auto-generated method stub
							dialog.setProgress(count);
							
							LogUtils.d(this, "smsOnBackup "+ count);
						}
					});	
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					try {
						outputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				dialog.dismiss();
			}
		}.start();
		
		
		
		dialog.show();
		
	}
	
	public void commonNumberQuery(View v){
		
		startActivity(new Intent(this, CommonNumberActivity.class));
	}
	
	public void appLock(View v){
		
		startActivity(new Intent(this, AppLockActivity.class));
	}
	

}
