package com.trx.mobilesafe.activity;

import java.util.ArrayList;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.dao.CommonNumberDao;
import com.trx.mobilesafe.dao.CommonNumberDao.ChildInfo;
import com.trx.mobilesafe.dao.CommonNumberDao.GroupInfo;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberActivity extends Activity{

	private ArrayList<GroupInfo> mLists;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_common_number);
		
		ExpandableListView elvList = (ExpandableListView)findViewById(R.id.elv_list);
		
		final MyAdapter mAdapter = new MyAdapter();
		mLists = CommonNumberDao.getCommonNumberGroups();
		elvList.setAdapter(mAdapter);
		
		elvList.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				ChildInfo info = mAdapter.getChild(groupPosition, childPosition);
				
				/*跳转到打电话页面*/
				//需要CALL_PHONE权限
				Intent  intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:"+info.number));
				startActivity(intent);
				
				return false;
			}
		});
	}
	
	class MyAdapter implements ExpandableListAdapter{

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return mLists.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return mLists.get(groupPosition).children.size();
		}

		@Override
		public GroupInfo getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return mLists.get(groupPosition);
		}

		@Override
		public ChildInfo getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return mLists.get(groupPosition).children.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView view = new TextView(getApplicationContext());
			view.setTextColor(Color.RED);
			view.setTextSize(20);
			// view.setText("       第" + groupPosition + "组");
			GroupInfo group = getGroup(groupPosition);
			view.setText("      " + group.name);
			return view;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView view = new TextView(getApplicationContext());
			view.setTextColor(Color.BLACK);
			view.setTextSize(18);
			// view.setText("第" + groupPosition + "组-第" + childPosition + "项");
			ChildInfo child = getChild(groupPosition, childPosition);
			view.setText(child.name + "\n" + child.number);
			return view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean areAllItemsEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onGroupExpanded(int groupPosition) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGroupCollapsed(int groupPosition) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public long getCombinedChildId(long groupId, long childId) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getCombinedGroupId(long groupId) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
}
