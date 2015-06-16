package com.iwgame.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class StreamTool {

	/** 涓嬭浇鏈嶅姟鍣ㄩ厤缃枃浠� */
	public static String downloadConfig(String url) {

		try {

			byte[] bytes = null;

			HttpURLConnection conn;

			conn = (HttpURLConnection) new URL(url).openConnection();

			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bytes = StreamTool.readInputStream(is);
			conn.disconnect();

			String string = new String(bytes);
			LogUtil.error(string);
			return string;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * 浠庤緭鍏ユ祦鑾峰彇鏁版嵁
	 * 
	 * @param inputStream
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */

	public static byte[] readInputStream(InputStream inputStream)
			throws IOException {

		byte[] buffer = new byte[1024]; // 鍙互鏍规嵁瀹為檯闇�璋冩暣缂撳瓨澶у皬

		int len = -1;

		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();

		while ((len = inputStream.read(buffer)) != -1) {

			outSteam.write(buffer, 0, len);

		}

		outSteam.close();

		inputStream.close();

		return outSteam.toByteArray();

	}

}
