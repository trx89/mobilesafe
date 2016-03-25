package com.trx.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.R.integer;

public class MD5Utils {

	public static String encode(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytes = digest.digest(password.getBytes());// 进行加密运算,返回加密后的字节数组

			StringBuffer sb = new StringBuffer();
			for (byte b : bytes) {
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);
				// System.out.println(hexString);
				if (hexString.length() == 1) {
					hexString = "0" + hexString;
				}

				sb.append(hexString);
			}

			String md5 = sb.toString();

			return md5;
		} catch (NoSuchAlgorithmException e) {
			// 没有此算法异常
			e.printStackTrace();
		}

		return null;
	}
	
	public static String encodeFile(String path){
		FileInputStream in = null;
		String md5 = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(new File(path));
			byte[] byteArray = new byte[1024];
			int len = 0;
			while (-1 != (len = in.read(byteArray))){
				digest.update(byteArray, 0, len);
			}
				
			byte[] bytes = digest.digest();// 进行加密运算,返回加密后的字节数组
			StringBuffer sb = new StringBuffer();
			for (byte b : bytes) {
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);
				// System.out.println(hexString);
				if (hexString.length() == 1) {
					hexString = "0" + hexString;
				}

				sb.append(hexString);
			}

			md5 = sb.toString();

		} catch (Exception e) {
			// 没有此算法异常
			e.printStackTrace();
		}finally{
			if (null != in){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return md5;
	}
}
