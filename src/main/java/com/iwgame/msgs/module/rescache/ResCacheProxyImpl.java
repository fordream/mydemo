/**      
 * ResCacheProxyImpl.java Create on 2013-9-14     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.rescache;

import java.io.File;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: ResCacheProxyImpl
 * @Description: 资源缓存代理类
 * @author 王卫
 * @date 2013-9-14 下午12:24:23
 * @Version 1.0
 * 
 */
public class ResCacheProxyImpl implements ResCacheProxy {

	protected static final String TAG = "ResCacheProxyImpl";

	private static byte[] lock = new byte[0];

	private static ResCacheProxy instance = null;
	// 普通原始文件
	private File commonCacheFile;
	private File smallCacheFile;
	private File midiumCacheFile;
	private File bigCacheFile;

	private ResCacheProxyImpl() {
		// 创建缓存目录，系统一运行就得创建缓存目录的，
		// cache = new File(Environment.getExternalStorageDirectory(),
		// "rescache");
		commonCacheFile = new File(Environment.getExternalStorageDirectory(), "commonRescache");
		smallCacheFile = new File(Environment.getExternalStorageDirectory(), "smallRescache");
		midiumCacheFile = new File(Environment.getExternalStorageDirectory(), "midiumRescache");
		bigCacheFile = new File(Environment.getExternalStorageDirectory(), "bigRescache");

		if (!commonCacheFile.exists()) {
			commonCacheFile.mkdirs();
		}
		if (!smallCacheFile.exists()) {
			smallCacheFile.mkdirs();
		}
		if (!midiumCacheFile.exists()) {
			midiumCacheFile.mkdirs();
		}
		if (!bigCacheFile.exists()) {
			bigCacheFile.mkdirs();
		}
	}

	public static ResCacheProxy getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new ResCacheProxyImpl();
				return instance;
			}
		} else {
			return instance;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgsdemo.rescache.ResCacheProxy#getRes(com.iwgame.msgsdemo
	 * .common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void getRes(final ProxyCallBack<Uri> callback, final Context context, final String resid, final int type) {
		loadRes(callback, context, resid, type, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.rescache.ResCacheProxy#getRes(com.iwgame.msgs.
	 * common.ProxyCallBack, android.content.Context, java.lang.String, int,
	 * boolean)
	 */
	@Override
	public void getRes(ProxyCallBack<Uri> callback, Context context, String resid, int type, boolean isnet) {
		loadRes(callback, context, resid, type, isnet);
	}

	private void loadRes(final ProxyCallBack<Uri> callback, final Context context, final String resid, final int type, final boolean isnet) {
		if (resid != null && !resid.isEmpty()) {

			new MyAsyncTask(null).execute(new AsyncCallBack<Uri>() {

				@Override
				public Uri execute() {
					File cacheFile = commonCacheFile;
					String mpath = "";
					if (type == ResCacheProxy.TYPE_MIDIUM) {
						cacheFile = midiumCacheFile;
						mpath = "/midium";
					} else if (type == ResCacheProxy.TYPE_SMALL) {
						cacheFile = smallCacheFile;
						mpath = "/small";
					} else if (type == ResCacheProxy.TYPE_BIG) {
						cacheFile = bigCacheFile;
						mpath = "/big";
					}
					ResCacheHelp help = new ResCacheHelp(context, SystemContext.getInstance().getResIP() + mpath, isnet);
					try {
						return help.getImageURI(resid, cacheFile);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}

				@Override
				public void onHandle(Uri result) {
					if (result == null) {
						LogUtil.d(TAG, "没有相关资源");
					} else {
						callback.onSuccess(result);
					}
				}

			});
		} else {
			callback.onFailure(ErrorCode.EC_REQUEST_ERROR,null);
		}
	}

}
