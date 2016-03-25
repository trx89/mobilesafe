package com.trx.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.base.BaseActivity;
import com.trx.mobilesafe.dao.BlackNumberDao;
import com.trx.mobilesafe.domain.BlackNumberInfo;
import com.trx.mobilesafe.utils.LogUtils;
import com.trx.mobilesafe.utils.ToastUtils;

import java.util.ArrayList;

/**
 * Created by trx08 on 2016/3/20.
 */
public class BlackNumberActivity extends Activity {

    private ListView lvShow;
    private ShowAdapter mAdapter;
    private int mIndex = 0;
    private final int PAGE_NUMBER = 20;
    private ArrayList<BlackNumberInfo> mLists = new ArrayList<BlackNumberInfo>();
    private ProgressBar pbLoad;
    private boolean isLoading = false; /*防止重复加载*/
    private BlackNumberDao mDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_black_number);

        lvShow = (ListView) findViewById(R.id.lv_black_number);
        pbLoad = (ProgressBar)findViewById(R.id.pb_loading);

        mDao = BlackNumberDao.getInstance(this);

        lvShow.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (SCROLL_STATE_IDLE == scrollState){

                    /*到达底部，重新获取数据*/
                    int lastVisiblePosition = lvShow.getLastVisiblePosition();
                    if (lastVisiblePosition >= mLists.size() - 1 && !isLoading){

                        int totalCnt = mDao.getTotalCnt();
                        if (lastVisiblePosition < totalCnt -1) {
                            initData();
                        }else {
                            ToastUtils.showToast(BlackNumberActivity.this, "没有更多数据");
                        }
                    }

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                LogUtils.d(this, "totalItemCount："+totalItemCount);
            }
        });

        initData();
    }


    private void initData() {
        isLoading = true;
        pbLoad.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                ArrayList<BlackNumberInfo> pageLists =
                        mDao.queryOnePage(mIndex, PAGE_NUMBER);
                mLists.addAll(pageLists);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null == mAdapter) {
                            mAdapter = new ShowAdapter();
                            lvShow.setAdapter(mAdapter);
                        }else {
                            mAdapter.notifyDataSetChanged();
                        }
                        /*更新索引*/
                        mIndex += PAGE_NUMBER;
                        isLoading = false;

                        pbLoad.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }.start();
    }

    class ShowAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLists.size();
        }

        @Override
        public BlackNumberInfo getItem(int position) {
            return mLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView){
                convertView = View.inflate(BlackNumberActivity.this, R.layout.list_item_black_number, null);
                holder = new ViewHolder();
                holder.tvNumber = (TextView)convertView.findViewById(R.id.tv_number);
                holder.tvMode = (TextView)convertView.findViewById(R.id.tv_mode);
                holder.ivDelete = (ImageView)convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            }else{
               holder = (ViewHolder)convertView.getTag();
            }

            final BlackNumberInfo info = getItem(position);
            holder.tvNumber.setText(info.number);

            String text = "";
            switch (info.mode){
                case BlackNumberDao.MODE_TELEPHONE:
                    text = "拦截电话";
                    break;
                case BlackNumberDao.MODE_SMS:
                    text = "拦截短信";
                    break;
                case BlackNumberDao.MODE_ALL:
                    text = "拦截全部";
                    break;
            }
            holder.tvMode.setText(text);

            // 给删除按钮添加点击事件
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 1.从数据库删除
                    // 2.从集合删除
                    // 3.刷机listview
                    mDao.delete(info.number);
                    mLists.remove(info);
                    mAdapter.notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }


    class ViewHolder{
        public TextView tvNumber;
        public TextView tvMode;
        public ImageView ivDelete;
    }


    public void addBlackNumber(View v){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = View.inflate(this, R.layout.dialog_add_blacknumber, null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        Button btnOk = (Button)view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button)view.findViewById(R.id.btn_cancel);
        final RadioGroup rgGroup = (RadioGroup)view.findViewById(R.id.rg_group);
        final EditText etNumber = (EditText)view.findViewById(R.id.et_black_number);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = BlackNumberDao.MODE_NONE;
                switch (rgGroup.getCheckedRadioButtonId()){
                    case R.id.rb_sms:
                        mode = BlackNumberDao.MODE_SMS;
                        break;
                    case R.id.rb_phone:
                        mode = BlackNumberDao.MODE_TELEPHONE;
                        break;
                    case R.id.rb_all:
                        mode = BlackNumberDao.MODE_ALL;
                        break;
                }

                String number = etNumber.getText().toString().trim();
                if (!TextUtils.isEmpty(number)){
                    if (mDao.add(number, mode)){
                        BlackNumberInfo info = new BlackNumberInfo();
                        info.number = number;
                        info.mode = mode;
                        mLists.add(0, info);

                        mAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }else{
                        ToastUtils.showToast(BlackNumberActivity.this, "号码已经配置过了");
                    }
                }else {
                    ToastUtils.showToast(BlackNumberActivity.this, "号码不能为空");
                }

            }
        });

        dialog.show();
    }
}
