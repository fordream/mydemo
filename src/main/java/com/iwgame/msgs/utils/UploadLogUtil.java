/**      
 * UploadLogUtil.java Create on 2014-12-16     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.EnvManager;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.utils.FileUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: UploadLogUtil
 * @Description: 上传日志并删除目录下的文件
 * @author 王卫
 * @date 2014-12-16 下午12:55:25
 * @Version 1.0
 * 
 */
public class UploadLogUtil {

	public static String LOGPATH = "/mnt/sdcard/msgs/logs";
	
	public static String TAG = "UploadLogUtil";

	public static boolean checkDirectoryExists(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		if (dirFile.exists() && dirFile.isDirectory()) {
			return true;
		} else {
			dirFile.mkdirs();
			return false;
		}
	}

	/**
	 * 上传日志并删除目录（文件夹）以及目录下的文件
	 * 
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean UploadAndDeleteDirectory(String sPath) {
		if(sPath == null)
			return false;
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				// 删除子文件
				if (files[i].isFile()) {
					uploadLog(files[i]);
				} // 删除子目录
				else {
					flag = UploadAndDeleteDirectory(files[i].getAbsolutePath());
					if (!flag)
						break;
				}
			}
		}
		if (!flag)
			return false;
		return true;
	}

	/**
	 * 上传日志文件
	 * 
	 * @param file
	 */
	private static void uploadLog(final File file) {
		new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				try {
					byte[] data = FileUtil.readFile(file);
					String imsi = null;
					String systemVersion = null;
					String networkType = null;
					String phoneModel = null;
					String mac = null;
					String position = null;

					Map<String, String> emap = EnvManager.getInstance().getEnvMap();
					if (emap.containsKey(EnvManager.D_IMSI))
						imsi = emap.get(EnvManager.D_IMSI);
					if (emap.containsKey(EnvManager.D_SVERSION))
						systemVersion = emap.get(EnvManager.D_SVERSION);
					if (emap.containsKey(EnvManager.D_NTYPE))
						networkType = emap.get(EnvManager.D_NTYPE);
					if (emap.containsKey(EnvManager.D_MODEL))
						phoneModel = emap.get(EnvManager.D_MODEL);
					if (emap.containsKey(EnvManager.D_MAC))
						mac = emap.get(EnvManager.D_MAC);
					if (emap.containsKey(EnvManager.D_POSITION))
						position = emap.get(EnvManager.D_POSITION);
					ServiceFactory.getInstance().getContentRemoteService().saveLog(new ServiceCallBack<XAction.XActionResult>() {

						@Override
						public void onSuccess(XActionResult result) {
							LogUtil.d(TAG, "-->>result="+result.getRc());
							FileUtil.deleteFile(file.getAbsolutePath());
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							// TODO Auto-generated method stub

						}
					}, null, "", data, imsi, systemVersion, networkType, phoneModel, mac, position);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public void onHandle(Void result) {
			}
		});

	}

}
