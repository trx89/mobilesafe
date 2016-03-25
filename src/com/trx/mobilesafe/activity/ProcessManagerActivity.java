package com.trx.mobilesafe.activity;

import java.util.ArrayList;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.activity.AppManagerActivity.HeadHolder;
import com.trx.mobilesafe.activity.AppManagerActivity.MyAdapter;
import com.trx.mobilesafe.activity.AppManagerActivity.ViewHolder;
import com.trx.mobilesafe.domain.AppInfo;
import com.trx.mobilesafe.domain.ProcessInfo;
import com.trx.mobilesafe.engine.AppInfoProvide;
import com.trx.mobilesafe.engine.ProcessInfoProvide;
import com.trx.mobilesafe.utils.PrefUtils;
import com.trx.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ProcessManagerActivity extends Activity {

	private TextView tvProcessCnt;
	private TextView tvMemoInfo;
	private ListView lvList;
	private TextView tvHeader;
	private LinearLayout llLoad;
	private ArrayList<ProcessInfo> mUserLists;
	private ArrayList<ProcessInfo> mSysLists;
	private MyAdapter mAdapter;
	private int mProcessCnt;
	private long mAvailMem;
	private long mTotalMem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_process_manager);

		tvProcessCnt = (TextView) findViewById(R.id.tv_running_num);
		tvMemoInfo = (TextView) findViewById(R.id.tv_memo_info);
		lvList = (ListView) findViewById(R.id.lv_list);
		tvHeader = (TextView) findViewById(R.id.tv_header);
		llLoad = (LinearLayout) findViewById(R.id.ll_loading);

		mProcessCnt = ProcessInfoProvide.getRunningProcessNum(this);
		tvProcessCnt.setText("运行中的进程：" + mProcessCnt + "个");

		mAvailMem = ProcessInfoProvide.getAvailMemory(this);
		mTotalMem = ProcessInfoProvide.getTotalMemory();
		tvMemoInfo.setText(String.format("剩余内存/总内存：%s/%s",
				Formatter.formatFileSize(this, mAvailMem),
				Formatter.formatFileSize(this, mTotalMem)));
		
		lvList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (null != mUserLists && null != mSysLists) {
					if (firstVisibleItem <= mUserLists.size()) {
						tvHeader.setText(String.format("用户进程(%d)",
								mUserLists.size()));
					} else {
						tvHeader.setText(String.format("系统进程(%d)",
								mSysLists.size()));
					}
				}
			}
		});

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				ProcessInfo info = (ProcessInfo) mAdapter.getItem(position);
				if (null != info && !info.packageName.equals(getPackageName())) {
					info.isChecked = !info.isChecked;
					CheckBox cbCheck = (CheckBox) view
							.findViewById(R.id.cb_check);
					cbCheck.setChecked(info.isChecked);
				}
			}
		});

		initData();
	}

	public void initData() {
		

		llLoad.setVisibility(View.VISIBLE);

		new Thread() {
			public void run() {
				SystemClock.sleep(100);

				ArrayList<ProcessInfo> mProcessLists = ProcessInfoProvide
						.getRunningProcess(ProcessManagerActivity.this);

				mUserLists = new ArrayList<ProcessInfo>();
				mSysLists = new ArrayList<ProcessInfo>();

				for (ProcessInfo info : mProcessLists) {
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
						lvList.setAdapter(mAdapter);

						llLoad.setVisibility(View.INVISIBLE);
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
			boolean isChecked = PrefUtils.getBoolean("show_sys_checked", true,
					ProcessManagerActivity.this);
			if (!isChecked) {
				return mUserLists.size() + 1;
			} else {
				return mUserLists.size() + mSysLists.size() + 2;
			}
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
					view = View.inflate(ProcessManagerActivity.this,
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
					headHolder.tvTitle.setText(String.format("用户进程(%d)",
							mUserLists.size()));
				} else {
					headHolder.tvTitle.setText(String.format("系统进程(%d)",
							mSysLists.size()));
				}

				break;

			case 1:
				// 应用类型
				if (null == convertView) {
					view = View.inflate(ProcessManagerActivity.this,
							R.layout.list_item_processinfo, null);
					holder = new ViewHolder();
					holder.tvName = (TextView) view.findViewById(R.id.tv_name);
					holder.tvMem = (TextView) view.findViewById(R.id.tv_memory);
					holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
					holder.cbCheck = (CheckBox) view
							.findViewById(R.id.cb_check);
					view.setTag(holder);
				} else {
					view = convertView;
					holder = (ViewHolder) view.getTag();
				}

				ProcessInfo info = (ProcessInfo) getItem(position);
				holder.ivIcon.setImageDrawable(info.icon);
				holder.tvName.setText(info.name);
				holder.tvMem.setText(Formatter.formatFileSize(
						getApplicationContext(), info.memory));
				
				if (info.packageName.equals(getPackageName())){
					holder.cbCheck.setVisibility(View.INVISIBLE);					
				}else {
					holder.cbCheck.setVisibility(View.VISIBLE);
				}
				holder.cbCheck.setChecked(info.isChecked);
				
				break;
			}

			return view;
		}
	}

	class ViewHolder {
		public ImageView ivIcon;
		public TextView tvName;
		public TextView tvMem;
		public CheckBox cbCheck;

	}

	class HeadHolder {
		public TextView tvTitle;
	}

	public void selectAll(View view) {

		for (ProcessInfo info : mUserLists) {
			//不能清除自己
			if (!info.packageName.equals(getPackageName())){
				info.isChecked = true;
			}
		}

		boolean isChecked = PrefUtils.getBoolean("show_sys_checked", true,
				ProcessManagerActivity.this);
		if (isChecked) {
			for (ProcessInfo info : mSysLists) {
				info.isChecked = true;
			}
		}

		mAdapter.notifyDataSetChanged();
	}

	public void reverseSelect(View view) {
		for (ProcessInfo info : mUserLists) {
			if (!info.packageName.equals(getPackageName())){
				info.isChecked = !info.isChecked;
			}
		}

		boolean isChecked = PrefUtils.getBoolean("show_sys_checked", true,
				ProcessManagerActivity.this);
		if (isChecked) {
			for (ProcessInfo info : mSysLists) {
				info.isChecked = !info.isChecked;
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	public void killAll(View view) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// java.util.ConcurrentModificationException,并发修改异常,遍历集合过程中,修改集合元素个数
		// foreach会出现此问题
		ArrayList<ProcessInfo> killedList = new ArrayList<ProcessInfo>();// 被清理的进程集合
		for (ProcessInfo info : mUserLists) {
			if (info.isChecked) {
				am.killBackgroundProcesses(info.packageName);
				// mUserList.remove(info);
				killedList.add(info);
			}
		}

		boolean showSystem = PrefUtils.getBoolean("show_system", true, this);
		if (showSystem) {// 如果不展示系统应用,就不用清除系统进程
			for (ProcessInfo info : mSysLists) {
				if (info.isChecked) {
					am.killBackgroundProcesses(info.packageName);
					// mSystemList.remove(info);
					killedList.add(info);
				}
			}
		}

		long savedMemory = 0;
		for (ProcessInfo processInfo : killedList) {
			if (processInfo.isUser) {
				mUserLists.remove(processInfo);
			} else {
				mSysLists.remove(processInfo);
			}

			savedMemory += processInfo.memory;
		}

		mAdapter.notifyDataSetChanged();

		ToastUtils
				.showToast(this, String.format("帮您杀死了%d个进程,共节省%s空间!",
						killedList.size(),
						Formatter.formatFileSize(this, savedMemory)));

		// 更新文本信息
		mProcessCnt -= killedList.size();
		mAvailMem += savedMemory;
		tvProcessCnt.setText(String.format("运行中的进程:%d个", mProcessCnt));
		tvMemoInfo.setText(String.format("剩余/总内存:%s/%s",
				Formatter.formatFileSize(this, mAvailMem),
				Formatter.formatFileSize(this, mTotalMem)));

	}

	public void setting(View view) {
		startActivityForResult(new Intent(this, ProcessSettingActivity.class),
				0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		mAdapter.notifyDataSetChanged();// 重新会走一次getCount方法,根据设置信息返回相应item数量
	}
}
