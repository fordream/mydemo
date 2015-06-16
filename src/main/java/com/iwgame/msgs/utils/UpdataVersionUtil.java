/**      
 * UpdataVersionUtil.java Create on 2013-12-2     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.service.UpdateService;
import com.iwgame.utils.AppUtils;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: UpdataVersionUtil
 * @Description: 更新版本工具类
 * @author 王卫
 * @date 2013-12-2 下午2:01:51
 * @Version 1.0
 * 
 */
public class UpdataVersionUtil {

	// 必须更新
	public static final int UPDATA_MODE_MUST = 1;
	// 有更新
	public static final int UPDATA_MODE_HAVE = 2;
	// 没有更新
	public static final int UPDATA_MODE_NO = 3;

	/**
	 * 获取版本更新模式
	 * 
	 * @param bversion
	 *            本地
	 * @param cversion
	 *            当前新的
	 * @param ncversion
	 *            必须更新的
	 * @return
	 */
	public static int getVersionUpdataMode(int bversion, int cversion, int ucversion) {
		if (bversion == cversion) {
			return UPDATA_MODE_NO;
		} else {
			if (bversion < ucversion) {
				return UPDATA_MODE_MUST;
			} else {
				return UPDATA_MODE_HAVE;
			}
		}
	}

	/**
	 * 检测更新
	 * 
	 * @param context
	 * @param listener
	 * @param isTip
	 */
	public static Dialog checkUpdata(final Context context, boolean isTip, final OnCallBackListener listener) {
		Dialog dialog = null;
		int updataMode = UpdataVersionUtil.getVersionUpdataMode(AppUtils.getLocalAppVersionCode(context), SystemConfig.C_VERSION_CODE,
				SystemConfig.UC_VERSION_CODE);
		switch (updataMode) {
		case UpdataVersionUtil.UPDATA_MODE_MUST:
			// 开启更新服务UpdateService
			dialog = DialogUtil.showDialog(context, "下载更新", creatDialogContent(context, netTip() + "检测到有必须更新的版本，不更新无法操作，请点击确定下载更新；更新内容：" + SystemConfig.VERSION_DESC), new DialogUtil.OKCallBackListener() {

				@Override
				public void execute() {
					ToastUtil.showToast(context, "新版本下载中");
					startUpdateService(context);
					Intent intent = new Intent(context, MainFragmentActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -3);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}

				@Override
				public void cannel() {
					Intent intent = new Intent(context, MainFragmentActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -3);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}

			});

			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					Intent intent = new Intent(context, MainFragmentActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -3);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			});
			break;
		case UpdataVersionUtil.UPDATA_MODE_HAVE:
			// 开启更新服务UpdateService
			dialog = DialogUtil.showDialog(context, "下载更新", creatDialogContent(context, netTip() + "检测到有新版本，点击确定下载更新；更新内容：" + SystemConfig.VERSION_DESC), new DialogUtil.OKCallBackListener() {

				@Override
				public void execute() {
					ToastUtil.showToast(context, "新版本下载中");
					startUpdateService(context);
					if (listener != null)
						listener.execute();
				}

				@Override
				public void cannel() {
					if (listener != null)
						listener.execute();
				}

			});

			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					if (listener != null)
						listener.execute();
				}
			});
			break;
		case UpdataVersionUtil.UPDATA_MODE_NO:
			if (isTip)
				ToastUtil.showToast(context, "当前已是最新版本");
			if (listener != null)
				listener.execute();
			break;
		default:
			if (isTip)
				ToastUtil.showToast(context, "当前已是最新版本");
			if (listener != null)
				listener.execute();
			break;
		}
		return dialog;
	}
	
	/**
	 * 创建更新提示对话框
	 * @param context
	 * @param content
	 * @return
	 */
	private static View creatDialogContent(Context context, String content){
		View view = View.inflate(context, R.layout.update_content_tip, null);
		TextView tv = (TextView)view.findViewById(R.id.content);
		tv.setText(content);
		return view;
	}

	/**
	 * 检测强制更新
	 * 
	 * @param context
	 * @param listener
	 * @param isTip
	 * @param type
	 *            类型为0
	 */
	public static void checkMustUpdata(final Context context, boolean isTip, final OnCallBackListener listener) {
		int updataMode = UpdataVersionUtil.getVersionUpdataMode(AppUtils.getLocalAppVersionCode(context), SystemConfig.C_VERSION_CODE,
				SystemConfig.UC_VERSION_CODE);
		switch (updataMode) {
		case UpdataVersionUtil.UPDATA_MODE_MUST:
			// 开启更新服务UpdateService
			TextView tv = new TextView(context);
			tv.setTextColor(0xff000000);
			tv.setTextSize(16);
			tv.setMaxLines(5);
			tv.setText(netTip() + "检测到有必须更新的版本，不更新无法操作，请点击确定下载更新；更新内容：" + SystemConfig.VERSION_DESC);
			int px = DisplayUtil.dip2px(context, 8);
			tv.setPadding(px, px, px, px);
			Dialog dialog = DialogUtil.showDialog(context, "下载更新", tv, new DialogUtil.OKCallBackListener() {

				@Override
				public void execute() {
					ToastUtil.showToast(context, "新版本下载中");
					startUpdateService(context);
					Intent intent = new Intent(context, MainFragmentActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -3);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}

				@Override
				public void cannel() {
					Intent intent = new Intent(context, MainFragmentActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -3);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}

			});

			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					Intent intent = new Intent(context, MainFragmentActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -3);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			});
			break;
		case UpdataVersionUtil.UPDATA_MODE_NO:
			if (isTip)
				ToastUtil.showToast(context, "当前已是最新版本");
			if (listener != null)
				listener.execute();
			break;
		default:
			if (isTip)
				ToastUtil.showToast(context, "当前已是最新版本");
			if (listener != null)
				listener.execute();
			break;
		}
	}

	/**
	 * 启动下载更新服务
	 */
	public static void startUpdateService(Context context) {
		Intent updateIntent = new Intent(context, UpdateService.class);
		updateIntent.putExtra("titleId", R.string.app_name);
		updateIntent.putExtra("url", SystemConfig.VERSION_DOWNLOAD_URL);
		context.startService(updateIntent);
	}

	public interface OnCallBackListener {
		public void execute();
	}

	private static String netTip() {
		int ntype = NetworkUtil.getNetworkType(SystemContext.getInstance().getContext());
		if (ntype == NetworkUtil.NETTYPE_NONET) {// 无网
			return "您当无网络，无法下载";
		} else if (ntype != NetworkUtil.NETTYPE_WIFI) {// WIFI
			return "您当前为非wifi环境，下载可能消耗你较多的流量";
		}
		return "";
	}

}
