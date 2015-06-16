package com.iwgame.msgs;

import java.util.Stack;

import org.openudid.OpenUDID_manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;

import com.iwgame.msgs.common.CrashHandler;
import com.iwgame.msgs.common.ImageCacheLoader;
import com.iwgame.msgs.common.MsgsExtensionRegistry;
import com.iwgame.msgs.common.StrictModeWrapper;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.setting.ui.SysSettingActivity;
import com.iwgame.msgs.utils.UploadLogUtil;
import com.iwgame.sdk.xaction.XActionMain;
import com.iwgame.utils.LogUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class BaseApplication extends Application {

	private static final String TAG = "BaseApplication";
	
	static {
        System.loadLibrary("xaction");
        UploadLogUtil.checkDirectoryExists(UploadLogUtil.LOGPATH);
        XActionMain.initLogger(UploadLogUtil.LOGPATH);
    }
	
	private Stack<Activity> activityStack;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
	        StrictModeWrapper.init(getApplicationContext());
		super.onCreate();
		Context context = this.getApplicationContext();
		OpenUDID_manager.sync(context);
		// 初始化应用配置
		SystemContext.getInstance().init(context);
		MsgsExtensionRegistry.registryExtension();
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(getPackageName(),PackageManager.GET_META_DATA);
			SystemContext.GLOBAL_CONFIG_URL = appInfo.metaData.getString("SERVICE_CONFIG_URL");
			SystemContext.UMENG_CHANNEL = appInfo.metaData.getString("UMENG_CHANNEL");
			SystemContext.APPTYPE = appInfo.metaData.getString("APPTYPE");
			SystemContext.WXPAY_APPID = appInfo.metaData.getString("WXPAY_APPID");
			SystemContext.COREDESC = appInfo.metaData.getString("COREDESC");
			SystemContext.GLOBAL_APPCONFIG_URL = SystemContext.GLOBAL_CONFIG_URL.substring(0, SystemContext.GLOBAL_CONFIG_URL.length() - 1)
												+ SystemContext.APPTYPE + "_rule";
			LogUtil.d(TAG, "------->SERVICE_CONFIG_URL:"+SystemContext.GLOBAL_CONFIG_URL);
			LogUtil.d(TAG, "------->UMENG_CHANNEL:"+SystemContext.UMENG_CHANNEL);
			LogUtil.d(TAG, "------->APPTYPE:"+SystemContext.APPTYPE);
			LogUtil.d(TAG, "------->COREDESC:"+SystemContext.COREDESC);
			LogUtil.d(TAG, "------->GLOBAL_APPCONFIG_URL:"+SystemContext.GLOBAL_APPCONFIG_URL);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		CrashHandler crashHandler = CrashHandler.getInstance();
		// 注册crashHandler
		crashHandler.init(getApplicationContext());
		// 发送以前没发送的报告(可选)
		crashHandler.sendPreviousReportsToServer();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(64 * 1024 * 1024).denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator())
				.discCacheFileCount(1000).tasksProcessingOrder(QueueProcessingType.LIFO)

				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		
		//WeChatProxy.getInstance().registerApp(getApplicationContext());
		
		ImageCacheLoader.getInstance().init(getApplicationContext());
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
	    ImageCacheLoader.getInstance().saveDataToDb(getApplicationContext());
		super.onTerminate();
	}
	
	
	public void pushActivity(Activity activity){ 
		if(activityStack==null){ 
			activityStack = new Stack<Activity>(); 
		} 
		activityStack.add(activity); 
	} 
	
	public void finishActivity(){
		if(activityStack != null){
			for(int i = 0; i <activityStack.size(); i++){
				Activity activity = (Activity) activityStack.get(i);
				if(activity instanceof SysSettingActivity){
					activity.finish();
					activityStack.remove(activity);
					activity = null;
				}
			}
		}
	}

}
