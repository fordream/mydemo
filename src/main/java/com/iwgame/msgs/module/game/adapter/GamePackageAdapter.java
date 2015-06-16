/**      
 * RecommendGameFollowAdapter.java Create on 2013-9-10     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.youban.msgs.R;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GamePackageDao;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.utils.BitUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.vo.local.GamePackageVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.roundedimageview.RoundedImageView;
import com.iwgame.utils.SysUtil;
import com.iwgame.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: RecommendGameFollowAdapter
 * @Description: 贴吧包适配器
 * @author 王卫
 * @date 2013-9-10 下午05:03:48
 * @Version 1.0
 * 
 */
public class GamePackageAdapter extends SimpleAdapter {

	private static final String TAG = "GamePackageAdapter";

	private DownloadManager downloadManager;

	private Context mContext;

	// 背景模式[0先白色1后白色]
	private int mBgmode = 0;
	
	private UserVo loginUserVo = null;

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public GamePackageAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, int bgmode) {
		super(context, data, resource, from, to);
		this.mContext = context;
		downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
		mBgmode = bgmode;
		loginUserVo = SystemContext.getInstance().getExtUserVo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
		Object tag = convertView.getTag();
		final ViewHolder holder;
		if (tag == null) {
			holder = new ViewHolder();
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			holder.rightView = (LinearLayout) convertView.findViewById(R.id.rightView);
			holder.distanceView = (FrameLayout) convertView.findViewById(R.id.distanceView);
			holder.functionBtn = (ImageView) convertView.findViewById(R.id.functionBtn);
			holder.functionTxt = (TextView) convertView.findViewById(R.id.functionTxt);
			holder.gname = (TextView) convertView.findViewById(R.id.gamename);
			holder.hotArea = (ImageView) convertView.findViewById(R.id.hotArea);
			holder.icon = (RoundedImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) tag;
		}
		holder.distanceView.setVisibility(View.INVISIBLE);
		holder.functionBtn.setVisibility(View.INVISIBLE);
		holder.hotArea.setVisibility(View.INVISIBLE);
		Map<String, Object> map = (HashMap<String, Object>) getItem(position);
		long gpid = (Long) (map.get("gpid"));
		String gavatar = (String) (map.get("gavatar"));
		getGamePackageInfo(convertView.getContext(), gpid, holder.icon, holder.desc, holder.gname, holder.functionBtn, gavatar, holder.functionTxt,
				holder.rightView);
		return convertView;
	}

	/**
	 * 
	 * @param gpid
	 * @param icon
	 * @param desc
	 * @param gname
	 */
	private void getGamePackageInfo(final Context context, final long gpid, final ImageView icon, final TextView descText, final TextView gname,
			final ImageView functionBtn, final String gavatar, final TextView functionTxt, final LinearLayout rightView) {
		ServiceFactory.getInstance().getSyncEntityService().syncEntity(gpid, SyncEntityService.TYPE_GAME_PACKAGE, new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				if (result != null) {
					GamePackageVo vo = (GamePackageVo) result;
					setGamePackageInfo(context, vo, icon, descText, gname, functionBtn, gavatar, functionTxt, rightView);
				} else {
					GamePackageDao gpackageDao = DaoFactory.getDaoFactory().getGamePackageDao(SystemContext.getInstance().getContext());
					setGamePackageInfo(context, gpackageDao.getGamePackageByPackageId(gpid), icon, descText, gname, functionBtn, gavatar,
							functionTxt, rightView);
				}
			}

			@Override
			public void onFailure(Integer result) {
				GamePackageDao gpackageDao = DaoFactory.getDaoFactory().getGamePackageDao(SystemContext.getInstance().getContext());
				setGamePackageInfo(context, gpackageDao.getGamePackageByPackageId(gpid), icon, descText, gname, functionBtn, gavatar, functionTxt,
						rightView);
			}
		});
	}

	/**
	 * 
	 * @param vo
	 * @param icon
	 * @param descText
	 * @param gname
	 */
	private void setGamePackageInfo(Context context, final GamePackageVo vo, ImageView icon, TextView descText, TextView gname,
			ImageView functionBtn, String gavatar, TextView functionTxt, LinearLayout rightView) {
		if (vo != null) {
			if (vo.getGameicon() != null && !"".equals(vo.getGameicon())) {
				ImageViewUtil.showImage(icon, vo.getGameicon(), R.drawable.common_default_icon);
			} else {
				ImageViewUtil.showImage(icon, gavatar, R.drawable.common_default_icon);
			}
			gname.setText(vo.getGamename());
			String desc = vo.getPublisher();
			if (desc == null)
				desc = "未知";
			long size = vo.getFilesize();
			descText.setText("大小：" + BitUtil.covertBit(size));

			final String packageName = vo.getPackagename();
			final String downLoadUrl = vo.getDownloadurl();
			// 打开
			final boolean isdown = SysUtil.appInstalled(context, packageName);
			if (isdown) {
				functionBtn.setVisibility(View.VISIBLE);
				functionTxt.setText("打开");
			} else {
				if (downLoadUrl != null && !"".equals(downLoadUrl)) {
					functionBtn.setVisibility(View.VISIBLE);
					functionTxt.setText("下载");
				} else {
					functionBtn.setVisibility(View.INVISIBLE);
				}
			}
			functionBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isdown) {
						openGame(v.getContext(), vo, packageName);
					} else {
						downloadGame(v.getContext(), vo, downLoadUrl);
					}
				}
			});

		} else {
			return;
		}
	}

	/**
	 * 打开贴吧
	 */
	private void openGame(Context context, GamePackageVo vo, String packageName) {
		SysUtil.launchApp(context, packageName);
		HashMap<String, String> ummap = new HashMap<String, String>();
		ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(loginUserVo.getUserid()));
		ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, loginUserVo.getUsername());
		ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, String.valueOf(vo.getPackageid()));
		ummap.put(UMConfig.MSGS_OPT_FROM_OBJ2_ID, String.valueOf(vo.getGameid()));
		ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, vo.getPackagename());
		ummap.put(UMConfig.MSGS_OPT_FROM_OBJ2_NAME, vo.getGamename());
		MobclickAgent.onEvent(context, UMConfig.MSGS_EVENT_GAME_OPEN, ummap);
	}

	/**
	 * 下载贴吧
	 */
	private void downloadGame(Context context, GamePackageVo vo, String downLoadUrl) {
		Log.e(TAG, "---------------downLoadUrl=" + downLoadUrl);
		if (vo == null || downLoadUrl == null) {
			ToastUtil.showToast(context, "下载地址错误");
			return;
		}
		// 开始下载
		Uri resource = Uri.parse(downLoadUrl);
		DownloadManager.Request request = new DownloadManager.Request(resource);
		request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
		request.setAllowedOverRoaming(false);
		// 设置文件类型
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(downLoadUrl));
		request.setMimeType(mimeString);
		// 在通知栏中显示
		request.setShowRunningNotification(true);
		request.setVisibleInDownloadsUi(true);
		// sdcard的目录下的download文件夹
		// 创建文件
		String path = null;
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
			path = "/msgs/";
		} else {
			path = "/";
		}
		request.setDestinationInExternalPublicDir(path, vo.getGamename() + ".apk");
		request.setTitle(vo.getGamename());
		request.setDescription("下载中");// 设置下载中通知栏提示的介绍
		String tip = String.format(context.getString(R.string.game_download_tip), vo.getGamename());
		ToastUtil.showToast(context, tip + ", 路径为:/msgs/" + vo.getGamename() + ".apk");
		long id = downloadManager.enqueue(request);

		HashMap<String, String> ummap = new HashMap<String, String>();
		ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(loginUserVo.getUserid()));
		ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, loginUserVo.getUsername());
		ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, String.valueOf(vo.getPackageid()));
		ummap.put(UMConfig.MSGS_OPT_FROM_OBJ2_ID, String.valueOf(vo.getGameid()));
		ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, vo.getPackagename());
		ummap.put(UMConfig.MSGS_OPT_FROM_OBJ2_NAME, vo.getGamename());
		MobclickAgent.onEvent(context, UMConfig.MSGS_EVENT_GAMEPACKAGE_DOWNLOAD, ummap);
	}

	static class ViewHolder {
		LinearLayout rightView;
		FrameLayout distanceView;
		ImageView functionBtn;
		ImageView hotArea;
		RoundedImageView icon;
		TextView desc;
		TextView gname;
		TextView functionTxt;
	}

}
