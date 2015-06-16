/**      
 * AppUtil.java Create on 2014-12-5     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import java.lang.reflect.Field;
import java.util.HashMap;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: AppUtil
 * @Description: 应用工具类（处理游伴和其他攻略的业务）
 * @author 王卫
 * @date 2014-12-5 上午11:05:37
 * @Version 1.0
 * 
 */
public class AppUtil {

	/**
	 * 此app中有推荐或分享其他游戏消息,判断游伴是否有安装，有打开并跳转到响应的贴吧页面，没有则下载
	 * 
	 * @param context
	 * @param tid
	 * @param clazzName
	 * @param content
	 */
	public static void openGame(Context context, Long tid, String clazzName, String content) {
		AppConfig config = AdaptiveAppContext.getInstance().getAppConfig();
		if (config != null) {
			openOtherApp(context, tid, config.getRecbarmsgUrl(), config.getRecbarmsgPackage(), clazzName, content);
		}
	}

	/**
	 * 
	 * @param context
	 * @param tid
	 * @param url
	 * @param pkg
	 * @param clazzName
	 * @param content
	 */
	private static void openOtherApp(Context context, Long tid, String url, String pkg, String clazzName, String content) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(pkg, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageInfo != null) {// 安装了配置的包
			/**
			 * ComponentName componentName = new ComponentName(pkg, cls); Intent
			 * intent = new Intent(); Bundle bundle = new Bundle();
			 * bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID,
			 * tid); intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			 * intent.setComponent(componentName);
			 * context.startActivity(intent);
			 **/
			ToastUtil.showToast(context, context.getResources().getString(R.string.postbar_show_gameortopic_tip_for_youban_install));
		} else {// 下载对应包的文件, 启动浏览器
			createDialog(context, content, url);
		}
	}

	/**
	 * 是否消息提醒
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isRemind(Context context) {
		AppConfig config = AdaptiveAppContext.getInstance().getAppConfig();
		if (config != null && config.isMsgremindCheck()) {
			PackageInfo packageInfo = null;
			try {
				packageInfo = context.getPackageManager().getPackageInfo(config.getMsgremindpkg(), 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			if (packageInfo != null) {// 安装了配置的包
				return false;
			}
		}
		return true;
	}

	/**
	 * 如果没有绑定手机 则弹出绑定手机的对话框
	 */
	private static void createDialog(final Context context, String content, final String url) {
		final Dialog dialog = new Dialog(context, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = View.inflate(context, R.layout.dialog_upgrade_group, null);
		TextView cueword = (TextView) view.findViewById(R.id.upgrade_group_success);
		Button cancelBtn = (Button) view.findViewById(R.id.cannelBtn);
		cancelBtn.setText("取消");
		Button commitBtn = (Button) view.findViewById(R.id.commitBtn);
		commitBtn.setText("下载");
		cueword.setText(content);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				context.startActivity(intent);
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}
	
	public static Activity getActivity() {
		 
        Application application = MainFragmentActivity.getInstance().getApplication();
 
        if (application == null) {
            throw new IllegalStateException("Application is null");
        }
        Object obj = null;
        Field f;
        try {
            f = Application.class.getDeclaredField("mLoadedApk");
            f.setAccessible(true);
            obj = f.get(application); // obj => LoadedApk
            f = obj.getClass().getDeclaredField("mActivityThread");
            f.setAccessible(true);
            obj = f.get(obj); // obj => ActivityThread
            f = obj.getClass().getDeclaredField("mActivities");
            f.setAccessible(true);
            obj = f.get(obj);
 
            //ver <= 4.4
//            if (obj instanceof ArrayMap) {
//                ArrayMap map = (ArrayMap) obj; //  obj => HashMap=<IBinder, ActivityClientRecord>
//                if (map.values().size() == 0) {
//                    return null;
//                }
// 
//                for (Object o : map.values().toArray()) {
//                    obj = o;
//                    f = obj.getClass().getDeclaredField("activity");
//                    f.setAccessible(true);
//                    obj = f.get(obj); // obj => Activity
//                }
//            }
            //ver < 4.4
            if (obj instanceof HashMap) {
                HashMap map = (HashMap) obj; //  obj => HashMap=<IBinder, ActivityClientRecord>
                if (map.values().size() == 0) {
                    return null;
                }
 
                for (Object o : map.values().toArray()) {
                    obj = o;
                    f = obj.getClass().getDeclaredField("activity");
                    f.setAccessible(true);
                    obj = f.get(obj); // obj => Activity
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!(obj instanceof Activity)) {
                return null;
            }
        }
        return (Activity) obj;
    }

}
