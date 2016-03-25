package com.trx.mobilesafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VirusDao {

	private static final String PATH = "/data/data/com.trx.mobilesafe/files/antivirus.db";

	public static boolean isVirus(String md5) {

		boolean result = false;
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);// 打开数据库, 只支持从data/data目录打开,
												// 不能从assets打开
		Cursor cursor = database.query("datable",
				new String[] { "name", "md5" }, "md5=?", new String[] { md5 },
				null, null, null);
		if (cursor.moveToFirst()){
			result = true;
		}
		
		cursor.close();
		database.close();
		
		return result;
	}
}
