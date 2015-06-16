/**      
 * SysUtil.java Create on 2013-9-11     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.utils;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

/**
 * @ClassName: SysUtil
 * @Description: 系统工具类
 * @author 王卫
 * @date 2013-9-11 下午05:27:52
 * @Version 1.0
 * 
 */
public class SysUtil {

	public static void launchApp(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(packageName);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void openApp(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();

		PackageInfo pi = null;
		try {
			pi = packageManager.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(pi.packageName);

		List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);

		ResolveInfo ri = apps.iterator().next();
		if (ri != null) {
			String className = ri.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			context.startActivity(intent);
		}
	}

	/**
	 * 获取系统是否安装了某个应用
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean appInstalled(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> applist = pm.getInstalledApplications(0);
		if (applist != null && packageName != null) {
			for (ApplicationInfo info : applist) {
				if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
					if (packageName.equals(info.packageName)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
