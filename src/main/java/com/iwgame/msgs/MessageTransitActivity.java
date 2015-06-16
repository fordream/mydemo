/**      
 * MessageTransitActivity.java Create on 2014-2-26     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: MessageTransitActivity
 * @Description: TODO(消息中转页，用于任务栏中打开消息的跳转)
 * @author chuanglong
 * @date 2014-2-26 下午12:05:48
 * @Version 1.0
 * 
 */
public class MessageTransitActivity extends BaseActivity {

    private String TAG = "MessageTransitActivity";
    private boolean isFirstOpen = false;
    
    private boolean isAgain = false ;
    Class<?> openClass = null;
    Bundle bundle = null;
    boolean islocalInTaskTop = false;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	isFirstOpen = true;
	islocalInTaskTop = islocalInTaskTop();
	// 打开对应页面
	Intent tmpintent = this.getIntent();
	if (tmpintent != null) {
	    openClass = (Class<?>) tmpintent.getSerializableExtra(SystemConfig.BUNDLEEXTRA_NAME_CLASS);
	    bundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
	}
	
	LogUtil.d(TAG, "-----onCreate");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	LogUtil.d(TAG, "-----onResume");
	if (!isFirstOpen) {
	    if(isAgain)
	    {
		//再次打开
		isAgain = false ;
		 if (openClass != null) {
			Intent intent = new Intent(this, openClass);
			intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			startActivity(intent);

		 }
	    }
	    else
	    {
		
	    // 返回 ，销毁，打开主界面
	    LogUtil.d(TAG, "返回时销毁中转页，打开主界面");
	    this.finish();
	    Intent intent = new Intent(this, MainFragmentActivity.class);
	    Bundle bundle = new Bundle();
	    bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISFROMNOTIFICATIONOPEN, true);
	    intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
	    startActivity(intent);
	    }
	} else {
	    // 打开对应的窗口
	    if (islocalInTaskTop) {
		this.finish();
		LogUtil.d(TAG, "打开时销毁中转页");
	    } else {
		LogUtil.d(TAG, "打开时没有销毁中转页");
	    }
	    isFirstOpen = false;

	    if (openClass != null) {
		Intent intent = new Intent(this, openClass);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		startActivity(intent);

	    }

	}

    }
    
    
    

    /* (non-Javadoc)
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {
	// TODO Auto-generated method stub
	LogUtil.d(TAG, "-----onNewIntent");
	super.onNewIntent(intent);
	isAgain = true ;
	if (intent != null) {
	    openClass = (Class<?>) intent.getSerializableExtra(SystemConfig.BUNDLEEXTRA_NAME_CLASS);
	    bundle = intent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
	}
    }

    /**
     * 是否本应用在最上面
     */
    private boolean islocalInTaskTop() {
	// 获得最上面运行的应用的包名
	ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	List<RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
	// int i = 0 ;
	// for (RunningTaskInfo info : runningTasks) {
	// LogUtil.d(TAG, i+
	// ":PackageName         :"+info.topActivity.getPackageName());
	// LogUtil.d(TAG, i+
	// ":topactivityClassName:"+info.topActivity.getClassName());
	// LogUtil.d(TAG, i+
	// ":luancherClassName   :"+info.topActivity.getClassName());
	// LogUtil.d(TAG, i+ ":numActivities       :"+info.numActivities);
	// i ++ ;
	//
	// }
	RunningTaskInfo runningTaskInfo = runningTasks.get(0);
	ComponentName topActivity = runningTaskInfo.topActivity;
	String packageName = topActivity.getPackageName();
	LogUtil.d(TAG, "最上面应用的包名：" + packageName);
	// // 获得本应用的包名
	// String localpackageName = "";
	// try {
	// PackageInfo info =
	// getPackageManager().getPackageInfo(getPackageName(), 0);
	// localpackageName = info.packageName;
	// } catch (NameNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// LogUtil.d(TAG, "本应用的包名："+localpackageName);
	// if (packageName.equals(localpackageName)) {
	// return true;
	// } else {
	// return false;
	// }
	if (runningTaskInfo.numActivities > 1) {
	    return true;
	} else {
	    return false;
	}

    }
}
