/**      
 * UpdateService.java Create on 2013-6-6     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.utils.FileUtil;
import com.iwgame.utils.HttpUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.MD5;
import com.iwgame.utils.NetworkUtil;
import com.youban.msgs.R;

/**
 * @ClassName: UpdateService
 * @Description: 更新服务
 * @author 王卫
 * @date 2013-11-28 上午11:14:34
 * @Version 1.0
 * 
 */
public class UpdateService extends Service {

	private static final String TAG = "UpdateService";
	private String url = "";

	// 文件存储
	public File updateDir = null;
	public File updateFile = null;

	// 通知栏
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;

	// 通知栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;

	// 下载状态
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	private final static int DOWNLOADING = 2;
	// 是否加载中
	private static boolean loading = false;

	private static final int BUFFER_SIZE = 1024;
	// 最后下载的进度
	private int lastRate = 0;
	// 当前下载的进度
	private int progress = 0;

	private BufferedInputStream bis = null;
	private RandomAccessFile fos = null;
	private static boolean canceled = false;

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				loading = false;
				// 点击安装PendingIntent
				Uri uri = Uri.fromFile(updateFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				Bundle bundle = new Bundle();
				installIntent.putExtras(bundle);
				installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
				updatePendingIntent = PendingIntent.getActivity(UpdateService.this, 0, installIntent, 0);
				// 铃声提醒
				updateNotification.defaults = Notification.DEFAULT_SOUND;
				// updateNotification.setLatestEventInfo(UpdateService.this,
				// SystemContext.getInstance().getContext().getResources().getString(R.string.app_name),
				// "下载完成", updatePendingIntent);
				// updateNotification.contentView = new
				// RemoteViews(getPackageName(), R.layout.common_checkbox);
				// updateNotificationManager.notify(0, updateNotification);
				updateNotification.contentIntent = updatePendingIntent;
				updateNotification.contentView.setTextViewText(R.id.progressTxt, "游伴 下载完成");
				updateNotification.contentView.setProgressBar(R.id.progressBar, 100, 100, false);
				updateNotificationManager.notify(0, updateNotification);
				// 弹出提示给app
				if(SystemContext.mainFragmentActivity != null)
					SystemContext.mainFragmentActivity.startActivity(installIntent);
				break;
			case DOWNLOAD_FAIL:
				loading = false;
				// 停止服务
				stopSelf();
				// 下载失败
				// updateNotification.setLatestEventInfo(UpdateService.this,
				// SystemContext.getInstance().getContext().getResources().getString(R.string.app_name),
				// "下载失败", updatePendingIntent);
				// updateNotification.contentView = new
				// RemoteViews(getPackageName(), R.layout.common_checkbox);
				// updateNotificationManager.notify(0, updateNotification);
				updateNotification.contentView.setTextViewText(R.id.progressTxt, "游伴 下载停止");
				updateNotification.contentView.setProgressBar(R.id.progressBar, 100, progress, false);
				updateNotificationManager.notify(0, updateNotification);
				break;
			case DOWNLOADING:
				updateNotification.contentView.setTextViewText(R.id.progressTxt, "游伴 下载进度：" + msg.obj + "% (点击停止下载)");
				updateNotification.contentView.setProgressBar(R.id.progressBar, 100, (Integer) msg.obj, false);
				updateNotificationManager.notify(0, updateNotification);
			default:
				stopSelf();
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d(TAG, "更新服务：onStartCommand");
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				int mode = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE);
				if (mode == -4) {
					loading = false;
					canceled = true;
				} else {
					if (!loading) {
						loading = true;
						url = SystemConfig.VERSION_DOWNLOAD_URL;
						// 创建文件
						if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
							updateDir = new File(Environment.getExternalStorageDirectory(), "msgs/download/");
						} else {
							updateDir = new File(getCacheDir(), "");
						}
						updateFile = new File(updateDir.getPath(), SystemConfig.C_VERSION_CODE + ".apk");

						this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						this.updateNotification = new Notification();

						// 设置通知栏显示内容
						updateIntent = new Intent(this, MainFragmentActivity.class);
						updateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						Bundle bundle2 = new Bundle();
						bundle2.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -4);
						updateIntent.putExtras(bundle2);
						updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
						updateNotification.icon = R.drawable.ic_launcher;
						updateNotification.tickerText = "开始下载";
						// updateNotification.setLatestEventInfo(this,
						// SystemContext.getInstance().getContext().getResources().getString(R.string.app_name),
						// "0%",
						// updatePendingIntent);
						updateNotification.contentView = new RemoteViews(getPackageName(), R.layout.update_notify_progress);
						updateNotification.contentIntent = updatePendingIntent;
						// 发出通知
						updateNotificationManager.notify(0, updateNotification);

						// 开启一个新的线程下载，异步下载
						new Thread(new UpdateRunnable()).start();
					}
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	class UpdateRunnable implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// 通知需要同步数据
			Message message = updateHandler.obtainMessage();
			message.what = DOWNLOAD_COMPLETE;
			try {
				if (!"".equals(url)) {
					if (!updateDir.exists()) {
						updateDir.mkdirs();
					}
					if (!updateFile.exists()) {
						updateFile.createNewFile();
					}
					boolean completed = downloadUpdateFile(url, updateFile);
					if (completed) {
						// 下载成功
						updateHandler.sendMessage(message);
					} else {
						message.what = DOWNLOAD_FAIL;
						// 下载失败
						updateHandler.sendMessage(message);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				// // 下载失败
				updateHandler.sendMessage(message);
			}
		}

		public boolean downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {
			LogUtil.i(TAG, "------------------------downloadUrl=" + downloadUrl);
			// 判断网络状态
			if (!NetworkUtil.isConnect(SystemContext.getInstance().getContext())) {
				return false;
			}
			progress = 0;
			lastRate = 0;
			canceled = false;
			URL url = new URL(downloadUrl);
			long startPosition = saveFile.length();
			long endPosition = HttpUtil.getRemoteFileSize(downloadUrl);
			long curPosition = saveFile.length();
			long downloadSize = saveFile.length();
			byte[] buf = new byte[BUFFER_SIZE];
			URLConnection con = null;
			try {
				con = url.openConnection();
				con.setAllowUserInteraction(true);
				// con.setConnectTimeout(10000);
				// 设置当前线程下载的起点，终点
				con.setRequestProperty("Range", "bytes=" + startPosition + "-" + endPosition);
				// 使用java中的RandomAccessFile 对文件进行随机读写操作
				fos = new RandomAccessFile(saveFile, "rw");
				// 设置开始写文件的位置
				fos.seek(saveFile.length());
				bis = new BufferedInputStream(con.getInputStream());
				// 开始循环以流的形式读写文件
				// updateNotification.setLatestEventInfo(UpdateService.this,
				// SystemContext.getInstance().getContext().getResources().getString(R.string.app_name),
				// downloadSize * 100 / endPosition + "%",
				// updatePendingIntent);
				// 通知栏显示所用到的布局文件
				// updateNotification.contentView.setTextViewText(R.id.progressTxt,
				// "游伴 下载进度："+downloadSize * 100 / endPosition + "%");
				// updateNotification.contentView.setProgressBar(R.id.progressBar,
				// 100, (int)(downloadSize * 100 / endPosition), false);
				// updateNotificationManager.notify(0, updateNotification);

				Message message = updateHandler.obtainMessage();
				message.what = DOWNLOADING;
				message.obj = (int) (downloadSize * 100 / endPosition);
				updateHandler.sendMessage(message);
				while (curPosition < endPosition) {
					if (canceled)
						break;
					int len = bis.read(buf, 0, BUFFER_SIZE);
					if (len == -1) {
						break;
					}
					fos.write(buf, 0, len);
					curPosition = curPosition + len;
					if (curPosition > endPosition) {
						downloadSize += len - (curPosition - endPosition) + 1;
					} else {
						downloadSize += len;
					}

					progress = (int) (downloadSize * 100 / endPosition);
					if (progress >= lastRate + 1) {
						lastRate = progress;
						// updateNotification.setLatestEventInfo(UpdateService.this,
						// SystemContext.getInstance().getContext().getResources().getString(R.string.app_name),
						// downloadSize * 100
						// / endPosition + "%", updatePendingIntent);
						// updateNotificationManager.notify(0,
						// updateNotification);

						// updateNotification.contentView.setTextViewText(R.id.progressTxt,
						// "游伴 下载进度："+downloadSize * 100 / endPosition + "%");
						// updateNotification.contentView.setProgressBar(R.id.progressBar,
						// 100, (int)(downloadSize * 100 / endPosition), false);
						// updateNotificationManager.notify(0,
						// updateNotification);

						Message message2 = updateHandler.obtainMessage();
						message2.what = DOWNLOADING;
						message2.obj = progress;
						updateHandler.sendMessage(message2);
					}
				}
			} catch (IOException e) {
				Log.e("updateservive", e.getMessage());
			} finally {
				if (bis != null) {
					bis.close();
				}
				if (fos != null) {
					fos.close();
				}
			}
			LogUtil.i(TAG, "------------endPosition=" + endPosition);
			LogUtil.i(TAG, "------------saveFile.length()=" + saveFile.length());
			LogUtil.i(TAG, "------------downloadSize=" + downloadSize);
			if (endPosition > 1024 && downloadSize > 1024 && saveFile.length() == endPosition) {
				Log.i(TAG, "---------------"+downloadUrl+", "+MD5.getFileMD5String(saveFile));
	        	 if(MD5.getFileMD5String(saveFile).equals(downloadUrl.substring(downloadUrl.lastIndexOf("-")+1, downloadUrl.lastIndexOf(".apk")))){
	        		 return true;
	        	 }else{
	        		 FileUtil.deleteFile(saveFile);
	        		 return false;
	        	 }
			} else {
				return false;
			}
		}
	}

}
