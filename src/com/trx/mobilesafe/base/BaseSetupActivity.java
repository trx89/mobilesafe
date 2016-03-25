package com.trx.mobilesafe.base;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.trx.mobilesafe.R;
import com.trx.mobilesafe.utils.LogUtils;

/**
 * Created by trx08 on 2016/3/15.
 */
public abstract class BaseSetupActivity extends BaseActivity {

	private GestureDetector mDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mDetector = new GestureDetector(this, new OnGestureListener() {

		
			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
				
			}

			/**
			 * 快速滑动,抛 e1: 起点坐标 e2: 终点坐标 velocityX: 水平滑动速度 velocityY:
			 * 竖直滑动速度
			 */
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {

				if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {// 竖直方向滑动范围太大
					return true;
				}

				if (Math.abs(velocityX) < 100) {
					return true;
				}

				// 判断向左划还是想右划
				// e1.getX();//相对父控件的x坐标
				// e1.getRawX();//屏幕的绝对坐标
				if (e2.getRawX() - e1.getRawX() > 200) {// 向右划,上一页
					System.out.println("上一页");
					showPrevious();
					return true;
				}

				if (e1.getRawX() - e2.getRawX() > 200) {// 向左划, 下一页
					System.out.println("下一页");
					showNext();
					return true;
				}

				return false;
			}

			
		});
	}

	public void next(View v) {
		showNext();
	}

	public void previous(View v) {
		showPrevious();
	}

	public abstract void showNext();

	public abstract void showPrevious();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// 触摸事件交给GestureDetector处理
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
