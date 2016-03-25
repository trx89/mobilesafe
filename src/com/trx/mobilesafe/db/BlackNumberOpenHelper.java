package com.trx.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by trx08 on 2016/3/20.
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper{
    private static final String CREATE_SQL = "create table blacknumber ("
            +"_id integer auto increament, number varchar(20), mode integer)";

    public BlackNumberOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
