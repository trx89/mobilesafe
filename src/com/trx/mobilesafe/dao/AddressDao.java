package com.trx.mobilesafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by trx08 on 2016/3/15.
 */
public class AddressDao {

    public static  final String PATH = "/data/data/com.trx.mobilesafe/files/address.db";


    public static String getAddressLocation(String number){

        String addressLocation = "未知号码";

        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);

        // 要先判断是否是手机号码
        // 1 + [3-8] + 9位数字
        // 正则表达式 ^1[3-8]\d{9}$
        if (number.matches("^1[3-8]\\d{9}$")){

            Cursor cursor = db.rawQuery(
                    "select location from data2 where id=(select outkey from data1 where id=?)",
                    new String[]{number.substring(0,7)});

            if (cursor.moveToFirst()){
                addressLocation = cursor.getString(0);
            } else {
                switch (number.length()) {
                    case 3:
                        addressLocation = "报警电话";
                        break;
                    case 4:
                        addressLocation = "模拟器";
                        break;
                    case 5:
                        addressLocation = "客服电话";
                        break;
                    case 7:
                    case 8:
                        // 8888 8888
                        addressLocation = "本地电话";
                        break;
                    default:
                        // 010 8888 8888
                        // 0910 8888 8888
                        if (number.startsWith("0") && number.length() >= 11
                                && number.length() <= 12) {
                            // 有可能是长途电话
                            // 先查询4位区号
                            Cursor mcursor = db.rawQuery(
                                    "select location from data2 where area=?",
                                    new String[] { number.substring(1, 4) });

                            if (mcursor.moveToFirst()) {// 查到四位区号
                                addressLocation = mcursor.getString(0);
                            }

                            mcursor.close();

                            if ("未知号码".equals(addressLocation)) {
                                // 没有查到4位区号,继续查3位区号
                                mcursor = db.rawQuery(
                                        "select location from data2 where area=?",
                                        new String[] { number.substring(1, 3) });

                                if (mcursor.moveToFirst()) {// 查到3位区号
                                    addressLocation = mcursor.getString(0);
                                }

                                mcursor.close();
                            }
                        }
                        break;
                }
            }

            cursor.close();
        }

        db.close();

        return addressLocation;

    }
}
