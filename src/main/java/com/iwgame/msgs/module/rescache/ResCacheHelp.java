/**      
 * ResCacheHelp.java Create on 2013-9-14     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.rescache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.Uri;

import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;

/**
 * @ClassName: ResCacheHelp
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-9-14 下午12:23:51
 * @Version 1.0
 * 
 */
public class ResCacheHelp {

	private static final String TAG = "ResCacheHelp";
	private String mBaseNetpath;
	private Context mContext;
	private boolean mIsnet = true;

	/**
	 * 
	 * @param basepath
	 */
	public ResCacheHelp(Context context, String baseNetpath) {
		mContext = context;
		mBaseNetpath = baseNetpath;
	}

	/**
	 * 
	 * @param basepath
	 */
	public ResCacheHelp(Context context, String baseNetpath, boolean isnet) {
		mContext = context;
		mBaseNetpath = baseNetpath;
		mIsnet = isnet;
	}

	/*
	 * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片 这里的path是图片的地址
	 */
	public Uri getImageURI(String fileName, File cache) throws Exception {
		LogUtil.d(TAG, "------>fileName=" + fileName + ", cache=" + cache.toString());
		File file = new File(cache, fileName);
		// 如果图片存在本地缓存目录，则不去服务器下载
		if (file.exists()) {
			LogUtil.d(TAG, "------>本地存在图片文件");
			return Uri.fromFile(file);// Uri.fromFile(path)这个方法能得到文件的URI
		} else {
			if (mIsnet) {
				LogUtil.d(TAG, "------>本地不存在图片文件");
				// 判定网络
				if (!NetworkUtil.isConnect(mContext)) {
					return null;
				}

				// 从网络上获取图片
				URL url = new URL(mBaseNetpath + "/" + fileName);
				LogUtil.d(TAG, "------>url=" + url);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				conn.connect();
				if (conn.getResponseCode() == 200) {

					InputStream is = conn.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
					// 返回一个URI对象
					LogUtil.d(TAG, "------>file=" + file.toString());
					return Uri.fromFile(file);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

}
