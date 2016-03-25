package com.trx.mobilesafe.utils;

import java.io.FileOutputStream;
import java.net.InterfaceAddress;

import org.xmlpull.v1.XmlSerializer;


import android.R.integer;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Xml;

public class SmsUtils {
	public static void backup(ContentResolver resolver, FileOutputStream outputStream,
			final SmsCallback callback){
		
		int progress = 0;
		Uri uri = Uri.parse("content://sms");
		String[] projection = new String[]{"address", "type", "body"};
		Cursor cursor = resolver.query(uri, projection, null, null, null);
		
		int max = cursor.getCount();
		callback.smsPreBackup(max);
		
		try {
			
			XmlSerializer xml = Xml.newSerializer();
			xml.setOutput(outputStream, "UTF-8");
			xml.startDocument("UTF-8", false);
			xml.startTag(null, "smss");
			while (cursor.moveToNext()){
				/*保存xml的逻辑*/
				xml.startTag(null, "sms");
				
				xml.startTag(null, "address");
				String address = cursor.getString(cursor.getColumnIndex("address"));
				xml.text(address);
				xml.endTag(null, "address");
	
				xml.startTag(null, "type");
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				xml.text(Integer.toString(type));
				xml.endTag(null, "type");
				
				xml.startTag(null, "body");
				String body = cursor.getString(cursor.getColumnIndex("body"));
				xml.text(body);
				xml.endTag(null, "body");
				
				xml.endTag(null, "sms");
				
				progress++;
				callback.smsOnBackup(progress, max);
				SystemClock.sleep(100);
				
			}
			
			xml.startTag(null, "smss");
			xml.endDocument();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		cursor.close();
	
	}

	public interface SmsCallback{
		void smsPreBackup(int max);
		void smsOnBackup(int count, int max);
	}

}


