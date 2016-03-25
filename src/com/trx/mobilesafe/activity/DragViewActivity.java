package com.trx.mobilesafe.activity;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.base.BaseActivity;
import com.trx.mobilesafe.utils.PrefUtils;

/**
 * Created by trx08 on 2016/3/16.
 */
public class DragViewActivity extends BaseActivity {
    private ImageView ivDrag;
    private TextView tvTop;
    private TextView tvBottom;

    private int startX;
    private int startY;

    private int mScreenWidth;
    private int mScreenHeight;

    @Override
    public void initView() {
        setContentView(R.layout.activity_drag_view);

        ivDrag = (ImageView) findViewById(R.id.iv_drag);
        tvTop = (TextView) findViewById(R.id.tv_top);
        tvBottom = (TextView) findViewById(R.id.tv_bottom);
    }

    @Override
    public void initListener() {
        ivDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        int dx = endX - startX;
                        int dy = endY - startY;
                        int l = ivDrag.getLeft()+dx;
                        int t = ivDrag.getTop()+dy;
                        int r = ivDrag.getRight()+dx;
                        int b = ivDrag.getBottom()+dy;

                        if (t > mScreenHeight/2){
                            tvTop.setVisibility(View.VISIBLE);
                            tvBottom.setVisibility(View.INVISIBLE);
                        }else {
                            tvTop.setVisibility(View.INVISIBLE);
                            tvBottom.setVisibility(View.VISIBLE);
                        }

                        if (l < 0 || l > mScreenWidth - ivDrag.getWidth()){
                            return true;
                        }

                        //减去状态栏高度
                        if (t < 0 || t > mScreenHeight -25 - ivDrag.getHeight()){
                            return true;
                        }

                        ivDrag.layout(l, t, r, b);

                        startX = endX;
                        startY = endY;

                        break;

                    case MotionEvent.ACTION_UP:
                        PrefUtils.putInt("lastX", ivDrag.getLeft(), DragViewActivity.this);
                        PrefUtils.putInt("lastY", ivDrag.getTop(), DragViewActivity.this);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void initData() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenWidth = windowManager.getDefaultDisplay().getWidth();
        mScreenHeight = windowManager.getDefaultDisplay().getHeight();

        int lastX = PrefUtils.getInt("lastX", 0, this);
        int lastY = PrefUtils.getInt("lastY", 0, this);

        // 根据当前位置,显示文本框提示
        if (lastY > mScreenHeight / 2) {
            // 下方
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        } else {
            // 上方
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }

        // measure(测量宽高)->layout(设定位置)->draw(绘制), 这三个步骤必须在oncreate方法结束之后才调用
        // ivDrag.layout(lastX, lastY, lastX + ivDrag.getWidth(),
        // lastY + ivDrag.getHeight());//此方法不能在oncreate中执行,因为布局还没有开始绘制

        // 通过修改布局参数,设定位置
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivDrag
                .getLayoutParams();// 布局的父控件是谁,就获取谁的布局参数
        // int topMargin = params.topMargin;
        // System.out.println("top margin->" + topMargin);
        // 临时修改了布局参数, 通过布局参数修改布局位置
        params.topMargin = lastY;
        params.leftMargin = lastX;
    }

    @Override
    public void processClick(View v) {

    }
}
