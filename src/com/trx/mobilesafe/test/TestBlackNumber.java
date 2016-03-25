package com.trx.mobilesafe.test;

import java.util.Random;

import com.trx.mobilesafe.dao.BlackNumberDao;

import android.R.integer;
import android.test.AndroidTestCase;

public class TestBlackNumber extends AndroidTestCase {

	public void testAdd(){
		BlackNumberDao mDao = BlackNumberDao.getInstance(getContext());
		
		Random random = new Random();
		String number;
		int mode;
		for (int i = 0; i < 50; i++){
			
			if (i < 10){
				number = "1351234567"+i;
			}else {
				number = "130123456"+i;
			}
			
			mode = random.nextInt(3)+1; 
			mDao.add(number, mode);
		}
	}
}
