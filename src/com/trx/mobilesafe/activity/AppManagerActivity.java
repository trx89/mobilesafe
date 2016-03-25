package com.trx.mobilesafe.activity;

import java.util.ArrayList;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.activity.BlackNumberActivity.ViewHolder;
import com.trx.mobilesafe.dao.BlackNumberDao;
import com.trx.mobilesafe.domain.AppInfo;
import com.trx.mobilesafe.domain.BlackNumberInfo;
import com.trx.mobilesafe.engine.AppInfoProvide;
import com.trx.mobilesafe.utils.ToastUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AppManagerActivity extends Activity {

	private TextView tvRomAvail;
	private TextView tvSdAvail;
	private ListView lvApp;
	private ProgressBar pbLoad;
	private MyAdapter mAdapter;
	private ArrayList<AppInfo> mUserLists = null;
	private ArrayList<AppInfo> mSysLists = null;
	private PopupWindow mPopupWindow = null;
	private View mPopView;
	private ScaleAnimation mAnimation;
	private AppInfo mAppInfo;
	private TextView tvHeader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_app_manager);

		tvRomAvail = (TextView) findViewById(R.id.tv_rom_avail);
		tvSdAvail = (TextView) findViewById(R.id.tv_sdcard_avail);
		
		lvApp = (ListView) findViewById(R.id.lv_app);
		pbLoad = (ProgressBar) findViewById(R.id.pb_load);
		tvHeader = (TextView)findViewById(R.id.tv_header);
		

		// data目录的大小
		String romAvail = getAvailSize(Environment.getDataDirectory()
				.getAbsolutePath());
		tvRomAvail.setText("内部存储可用：" + romAvail);
		
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String sdAvail = getAvailSize(Environment
					.getExternalStorageDirectory().getAbsolutePath());
			tvSdAvail.setText("sd卡可用：" + sdAvail);
		}else{
			tvSdAvail.setText("sd卡不可用");
		}

		initData();

		lvApp.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				mAppInfo = (AppInfo)mAdapter.getItem(position);
				if (null != mAppInfo) {
					showPopupWindow(view);
				}
			}
		});
		
		lvApp.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (null != mUserLists && null != mSysLists){
					if (firstVisibleItem <= mUserLists.size()){
						tvHeader.setText(String.format("用户应用(%d)", mUserLists.size()));
					}else {
						tvHeader.setText(String.format("系统应用(%d)", mSysLists.size()));
					}
				}
			}
		});

		
	}

	public void showPopupWindow(View view) {
		if (null == mPopupWindow) {
			mPopView = View.inflate(view.getContext(),
					R.layout.popup_item_appinfo, null);

			mPopupWindow = new PopupWindow(mPopView,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
			mPopupWindow.setBackgroundDrawable(new ColorDrawable());
			
			mAnimation = new ScaleAnimation(0.1f, 1f, 0.1f, 1f,
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
			mAnimation.setDuration(500);
			
			TextView tvUninstall = (TextView)mPopView.findViewById(R.id.tv_uninstall);
			TextView tvLaunch = (TextView)mPopView.findViewById(R.id.tv_launch);
			TextView tvShare = (TextView)mPopView.findViewById(R.id.tv_share);
			
			tvUninstall.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					uninstallApp();
				}
			});
			
			tvLaunch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					launchApp();
				}
			});

			tvShare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					share();
				}
			});
			
		}
		
		mPopView.startAnimation(mAnimation);
		mPopupWindow.showAsDropDown(view, 50, -view.getHeight());
	}

	public void uninstallApp(){
		mPopupWindow.dismiss();
		if (mAppInfo.isUser){
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_DELETE);
			intent.setData(Uri.parse("package:"+mAppInfo.packageName));
			startActivityForResult(intent, 0);
		}
	}
	
	public void launchApp(){
		mPopupWindow.dismiss();
		
		PackageManager pm = getPackageManager();
		
		Intent intent = pm.getLaunchIntentForPackage(mAppInfo.packageName);
		
		if (null != intent){
			startActivity(intent);
		}else{
			ToastUtils.showToast(this, "无法启动");
		}
	}
	
	public void share(){
		mPopupWindow.dismiss();
		
		Intent intent = new Intent(Intent.ACTION_SEND);
	    intent.setType("text/plain");
	    intent.putExtra(Intent.EXTRA_TEXT, "hello "+mAppInfo.packageName);
	    
	    startActivity(intent);
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		initData();
	}
	
	public String getAvailSize(String path) {
		StatFs sf = new StatFs(path);
		long blocks = sf.getAvailableBlocks();
		long blockSize = sf.getBlockSize();
		long avail = blocks * blockSize;

		return Formatter.formatFileSize(this, avail);

	}

	public void initData() {
		pbLoad.setVisibility(View.VISIBLE);

		new Thread() {
			public void run() {
				SystemClock.sleep(100);

				ArrayList<AppInfo> mAppLists = AppInfoProvide
						.getInstallApps(AppManagerActivity.this);
				mUserLists = new ArrayList<AppInfo>();
				mSysLists = new ArrayList<AppInfo>();
				
				for (AppInfo info : mAppLists) {
					if (info.isUser) {
						mUserLists.add(info);
					} else {
						mSysLists.add(info);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						
						mAdapter = new MyAdapter();
						lvApp.setAdapter(mAdapter);

						pbLoad.setVisibility(View.INVISIBLE);
					}
				});

			}
		}.start();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			if (0 == position || (mUserLists.size() + 1) == position) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mUserLists.size() + mSysLists.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (0 == position || (mUserLists.size() + 1) == position) {
				return null;
			}

			if (position < (mUserLists.size() + 1)) {
				return mUserLists.get(position - 1);
			} else if (position > mUserLists.size() + 1) {
				return mSysLists.get(position - mUserLists.size() - 2);
			}

			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			HeadHolder headHolder;
			View view = null;

			int type = getItemViewType(position);
			switch (type) {
			case 0:
				// 标题类型
				if (null == convertView) {
					view = View.inflate(AppManagerActivity.this,
							R.layout.list_item_header, null);
					headHolder = new HeadHolder();
					headHolder.tvTitle = (TextView) view
							.findViewById(R.id.tv_header);
					view.setTag(headHolder);

				} else {
					view = convertView;
					headHolder = (HeadHolder) view.getTag();
				}

				if (0 == position) {
					headHolder.tvTitle.setText(String.format("用户应用(%d)",
							mUserLists.size()));
				} else {
					headHolder.tvTitle.setText(String.format("系统应用(%d)",
							mSysLists.size()));
				}

				break;

			case 1:
				// 应用类型
				if (null == convertView) {
					view = View.inflate(AppManagerActivity.this,
							R.layout.list_item_appinfo, null);
					holder = new ViewHolder();
					holder.tvName = (TextView) view.findViewById(R.id.tv_name);
					holder.tvLocation = (TextView) view
							.findViewById(R.id.tv_location);
					holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
					view.setTag(holder);
				} else {
					view = convertView;
					holder = (ViewHolder) view.getTag();
				}

				AppInfo info = (AppInfo) getItem(position);
				holder.ivIcon.setImageDrawable(info.icon);
				holder.tvName.setText(info.name);
				if (info.isRom) {
					holder.tvLocation.setText("手机内存");
				} else {
					holder.tvLocation.setText("sd卡");
				}

				break;
			}

			return view;
		}

	}

	class ViewHolder {
		public ImageView ivIcon;
		public TextView tvName;
		public TextView tvLocation;

	}

	class HeadHolder {
		public TextView tvTitle;
	}

}
