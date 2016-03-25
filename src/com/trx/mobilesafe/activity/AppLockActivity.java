package com.trx.mobilesafe.activity;

import java.util.ArrayList;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.activity.AppManagerActivity.HeadHolder;
import com.trx.mobilesafe.activity.AppManagerActivity.MyAdapter;
import com.trx.mobilesafe.activity.AppManagerActivity.ViewHolder;
import com.trx.mobilesafe.dao.AppLockDao;
import com.trx.mobilesafe.domain.AppInfo;
import com.trx.mobilesafe.domain.AppLockInfo;
import com.trx.mobilesafe.engine.AppInfoProvide;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AppLockActivity extends Activity implements OnClickListener {

	private Button btnUnlock;
	private Button btnLock;

	private LinearLayout llUnlock;
	private LinearLayout llLock;

	private ListView lvUnlock;
	private ListView lvLock;

	private TextView tvUnlock;
	private TextView tvLock;

	private ArrayList<AppLockInfo> mLockLists;
	private ArrayList<AppLockInfo> mUnLockLists;

	private MyAdapter mLockAdapter;
	private MyAdapter mUnLockAdapter;

	private boolean isLock = false;
	private TranslateAnimation mRightAnimation;
	private TranslateAnimation mLeftAnimation;
	private AppLockDao mDao;
	private LinearLayout llLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_app_lock);

		btnUnlock = (Button) findViewById(R.id.btn_unlock);
		btnLock = (Button) findViewById(R.id.btn_lock);
		btnUnlock.setOnClickListener(this);
		btnLock.setOnClickListener(this);

		llUnlock = (LinearLayout) findViewById(R.id.ll_unlock);
		llLock = (LinearLayout) findViewById(R.id.ll_lock);

		lvUnlock = (ListView) findViewById(R.id.lv_unlock);
		lvLock = (ListView) findViewById(R.id.lv_lock);

		tvUnlock = (TextView) findViewById(R.id.tv_unlock);
		tvLock = (TextView) findViewById(R.id.tv_lock);
		
		llLoading = (LinearLayout)findViewById(R.id.ll_loading);

		mDao = AppLockDao.getInstance(this);
		
		initAnimation();
		initData();
	}

	public void initData() {
		
		llLoading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {

				ArrayList<AppInfo> mAppLists = AppInfoProvide
						.getInstallApps(AppLockActivity.this);
				mLockLists = new ArrayList<AppLockInfo>();
				mUnLockLists = new ArrayList<AppLockInfo>();

				for (AppInfo info : mAppLists) {
					if (info.packageName.equals(getPackageName())) {
						continue;
					}

					AppLockInfo lockInfo = new AppLockInfo();
					lockInfo.icon = info.icon;
					lockInfo.name = info.name;
					lockInfo.packageName = info.packageName;
					if (mDao.isLock(info.packageName)) {
						mLockLists.add(lockInfo);
					} else {
						mUnLockLists.add(lockInfo);
					}

				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mLockAdapter = new MyAdapter();
						lvLock.setAdapter(mLockAdapter);

						mUnLockAdapter = new MyAdapter();
						lvUnlock.setAdapter(mUnLockAdapter);

						llLoading.setVisibility(View.INVISIBLE);
					}
				});

			}
		}.start();
	}

	public void updateNum(){
		tvUnlock.setText("未加锁软件:" + mUnLockLists.size() + "个");
		tvLock.setText("已加锁软件:" + mLockLists.size() + "个");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_lock:
			if (!isLock) {
				isLock = true;

				llUnlock.setVisibility(View.GONE);
				llLock.setVisibility(View.VISIBLE);

				btnUnlock.setBackgroundResource(R.drawable.tab_left_default);
				btnLock.setBackgroundResource(R.drawable.tab_right_pressed);
			}
			break;

		case R.id.btn_unlock:
			if (isLock) {
				isLock = false;

				llLock.setVisibility(View.GONE);
				llUnlock.setVisibility(View.VISIBLE);

				btnUnlock.setBackgroundResource(R.drawable.tab_left_pressed);
				btnLock.setBackgroundResource(R.drawable.tab_right_default);
			}
			break;
		default:
			break;
		}
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			updateNum();
			if (isLock) {
				return mLockLists.size();
			} else {
				return mUnLockLists.size();
			}
		}

		@Override
		public AppLockInfo getItem(int position) {
			// TODO Auto-generated method stub
			if (isLock) {
				return mLockLists.get(position);
			} else {
				return mUnLockLists.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			View view = null;

			if (null == convertView) {
				view = View.inflate(AppLockActivity.this,
						R.layout.list_item_applock, null);
				holder = new ViewHolder();
				holder.tvName = (TextView) view.findViewById(R.id.tv_name);
				holder.ivLock = (ImageView) view.findViewById(R.id.iv_lock);
				holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			final AppLockInfo info = (AppLockInfo) getItem(position);
			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);
			if (isLock) {
				holder.ivLock.setImageResource(R.drawable.unlock);
			} else {
				holder.ivLock.setImageResource(R.drawable.lock);
			}
			
			final View tmpView = view;
			holder.ivLock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!isLock) {
						mRightAnimation.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								// 动画开始
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// 动画重复
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								// 动画结束
								// 1. 数据库增加app
								// 2. 已加锁集合增加对象
								// 3. 未加锁集合减少对象
								// 4. 刷新listview
								mDao.add(info.packageName);
								mLockLists.add(info);
								mUnLockLists.remove(info);

								mUnLockAdapter.notifyDataSetChanged();
								mLockAdapter.notifyDataSetChanged();
							}
						});

						// 动画异步执行,不会阻塞
						tmpView.startAnimation(mRightAnimation);
					} else {
						mLeftAnimation.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								// 动画开始
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// 动画重复
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								// 动画结束
								// 1. 从数据库删除
								// 2. 已加锁集合删除对象
								// 3. 未加锁集合增加对象
								// 4. 刷新listview
								mDao.delete(info.packageName);
								mLockLists.remove(info);
								mUnLockLists.add(info);

								mUnLockAdapter.notifyDataSetChanged();
								mLockAdapter.notifyDataSetChanged();
							}
						});

						// 动画异步执行,不会阻塞
						tmpView.startAnimation(mLeftAnimation);
					}
				}
			});

			return view;
		}

	}

	class ViewHolder {
		public ImageView ivIcon;
		public TextView tvName;
		public ImageView ivLock;

	}

	public void initAnimation() {
		mRightAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		mRightAnimation.setDuration(500);
		
		mLeftAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		mLeftAnimation.setDuration(500);
	}

}
