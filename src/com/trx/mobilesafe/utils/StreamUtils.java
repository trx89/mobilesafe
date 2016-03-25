package com.trx.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtils {

	public static String stream2String(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder builder = new StringBuilder();
		String str;
		while (null != (str = reader.readLine())){
			builder.append(str);
		}

		return builder.toString();
	}
}
