package com.trx.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.trx.mobilesafe.db.BlackNumberOpenHelper;
import com.trx.mobilesafe.domain.BlackNumberInfo;
import com.trx.mobilesafe.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by trx08 on 2016/3/20.
 */
public class BlackNumberDao {

    private static BlackNumberDao mDao;
    private BlackNumberOpenHelper mHelper;
    private static final String TABLE_NAME = "blacknumber";
    public static final int MODE_NONE = 0;
    public  static final int MODE_SMS = 1;
    public static final int MODE_TELEPHONE = 2;
    public static final int MODE_ALL = 3;


    private BlackNumberDao(Context context){
        mHelper = new BlackNumberOpenHelper(context);
    }

    public synchronized static BlackNumberDao getInstance(Context context) {
        if (null == mDao){
            mDao = new BlackNumberDao(context);
        }

        return mDao;
    }


    public boolean add(String number, int mode){
        SQLiteDatabase db = mHelper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, "number=?", new String[]{number}, null, null, null);
        if (cursor.moveToFirst()){
            /*已经添加号码,返回错误*/
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);

        db.insert(TABLE_NAME, null, values);

        db.close();

        return true;
    }

    public boolean delete(String number){
        SQLiteDatabase db = mHelper.getReadableDatabase();

        int rows = db.delete(TABLE_NAME, "number=?", new String[]{number});

        db.close();

        return (rows>0) ? true : false;
    }

    public int getMode(String number){
        SQLiteDatabase db = mHelper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
        if (cursor.moveToFirst()){
            return cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return MODE_NONE;
    }

    public ArrayList<BlackNumberInfo> queryOnePage(int index, int number){
        ArrayList<BlackNumberInfo> lists = new ArrayList<BlackNumberInfo>();

        SQLiteDatabase db = mHelper.getReadableDatabase();
        final String sql = "select * from "+ TABLE_NAME + " order by _id desc" + " limit " + index + "," + number;
        LogUtils.d(this, sql);

        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()){
            BlackNumberInfo info = new BlackNumberInfo();
            info.number = cursor.getString(cursor.getColumnIndex("number"));
            info.mode = cursor.getInt(cursor.getColumnIndex("mode"));

            lists.add(info);
        }

        cursor.close();
        db.close();

        return lists;
    }

    public int getTotalCnt(){
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select count(*) from blacknumber",
                null);

        int count = -1;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        database.close();
        return count;
    }
}
