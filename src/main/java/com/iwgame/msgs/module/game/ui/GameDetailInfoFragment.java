/**      
 * GameInfoFragment.java Create on 2013-9-3     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.youban.msgs.R;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.game.adapter.GamePackageAdapter;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GamePackageVo;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.msgs.widget.roundedimageview.RoundedImageView;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;

/**
 * @ClassName: GameInfoFragment
 * @Description: 贴吧-详情
 * @author 王卫
 * @date 2013-9-3 下午05:00:03
 * @Version 1.0
 * 
 */
public class GameDetailInfoFragment extends BaseFragment implements OnClickListener {

	protected static final String TAG = "GameDetailInfoFragment";

	private long gid;

	private GameVo gvo;

	private boolean isfollow;

	private ImageView rightMenu;

	private ListView list;

	// 贴吧ICON
	private RoundedImageView gameIcon;
	// 贴吧名称
	private TextView gameName;
	// 贴吧开发商
	private TextView publisher;
	// 贴吧类型
	private TextView gameType;

	// 贴吧平台类型
	private TextView gamePlatformType;

	// 描述布局容器
	private FrameLayout descContent;
	// 短描述
	private TextView gameDesc;
	// 长描述
	private TextView gameDesc2;
	// 更多
	private Button moreBtn;

	private boolean isInit = false;
	private boolean isShowShortText = true;

	private DownloadManager downloadManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getActivity().unregisterReceiver(receiver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		downloadManager = (DownloadManager) getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
		Bundle tmpbundle = this.getArguments();
		if (tmpbundle != null) {
			gid = tmpbundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID);
		}
		View v = inflater.inflate(R.layout.fragment_game_detail_info, container, false);
		initialize(v, inflater);
		PTAG = TAG;
		return v;
	}

	/**
	 * 初始化
	 */
	private void initialize(View v, LayoutInflater inflater) {
		// 列表
		list = (ListView) v.findViewById(R.id.listView);

		// 添加头信息
		View headView = inflater.inflate(R.layout.fragment_game_detail_list_head, null);
		list.addHeaderView(headView);

		// 赋值界面对象
		gameIcon = (RoundedImageView) headView.findViewById(R.id.icon);
		gameName = (TextView) headView.findViewById(R.id.gameName);
		publisher = (TextView) headView.findViewById(R.id.publisher);
		gameType = (TextView) headView.findViewById(R.id.gameType);
		gamePlatformType = (TextView) headView.findViewById(R.id.gamePlatformType);

		descContent = (FrameLayout) headView.findViewById(R.id.descContent);
		gameDesc = (TextView) headView.findViewById(R.id.gameDesc);
		gameDesc2 = (TextView) headView.findViewById(R.id.gameDesc2);
		moreBtn = (Button) headView.findViewById(R.id.moreBtn);
		moreBtn.setOnClickListener(this);
		ViewTreeObserver vtob = descContent.getViewTreeObserver();
		vtob.addOnPreDrawListener(new OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				if (isInit)
					return true;
				if (mesureDescription(gameDesc, gameDesc2)) {
					moreBtn.setVisibility(View.VISIBLE);
				} else {
					moreBtn.setVisibility(View.GONE);
				}
				isInit = true;
				return true;
			}
		});
		// 获取贴吧信息
		getGameInfo();
		// 获取贴吧包信息
		searchGamePackage(list);
	}

	/**
	 * 计算描述信息是否过长
	 */
	private boolean mesureDescription(TextView shortView, TextView longView) {
		final int shortHeight = shortView.getHeight();
		final int longHeight = longView.getHeight();
		if (longHeight > shortHeight) {
			shortView.setVisibility(View.VISIBLE);
			longView.setVisibility(View.GONE);
			return true;
		}
		shortView.setVisibility(View.GONE);
		longView.setVisibility(View.VISIBLE);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.moreBtn) {
			if (isShowShortText) {
				gameDesc.setVisibility(View.GONE);
				gameDesc2.setVisibility(View.VISIBLE);
				moreBtn.setBackgroundResource(R.drawable.game_info_up_btn);
			} else {
				gameDesc.setVisibility(View.VISIBLE);
				gameDesc2.setVisibility(View.GONE);
				moreBtn.setBackgroundResource(R.drawable.game_info_more_btn);
			}
			isShowShortText = !isShowShortText;
		}
	}

	/**
	 * 获取贴吧信息
	 */
	private void getGameInfo() {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(getActivity());
		dialog.show();
		ProxyFactory.getInstance().getGameProxy().getGameInfo(new ProxyCallBack<GameVo>() {

			@Override
			public void onSuccess(GameVo result) {
				if (result != null) {
					gvo = result;
					gameName.setText(result.getGamename());
					String pub = result.getPublisher();
					if (pub == null || "".equals(pub)) {
						publisher.setText("开发商：未知");
					} else {
						publisher.setText("开发商：" + pub);
					}
					String type = result.getType();
					if (type == null || "".equals(type)) {
						gameType.setText("类型：未知");
					} else {
						gameType.setText("类型：" + type);
					}
					int tmpGamePlatformType = result.getGtype();
					switch (tmpGamePlatformType) {
					case 1:
						break;
					case 2:
						break;
					default:
						gamePlatformType.setText("平台：未知");
						break;
					}

					setIcon(gameIcon, result.getGamelogo());
				} else {
					LogUtil.e(TAG, "获取贴吧信息失败");
				}

				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {

				dialog.dismiss();
			}
		}, getActivity(), gid);
	}

	/**
	 * 搜索贴吧包
	 */
	private void searchGamePackage(final ListView list) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(getActivity());
		dialog.show();
		ProxyFactory.getInstance().getGameProxy().searchGamePackage(new ProxyCallBack<List<GamePackageVo>>() {

			@Override
			public void onSuccess(List<GamePackageVo> result) {
				// 设置LIST数据
				GamePackageAdapter adapter = new GamePackageAdapter(list.getContext(), new ArrayList<Map<String, Object>>(), R.layout.game_list_item,
						new String[] {}, new int[] {}, 0);
				list.setAdapter(adapter);
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				dialog.dismiss();
			}
		}, list.getContext(), gid);
	}

	/**
	 * 设置头像
	 * 
	 * @param iconView
	 * @param resid
	 */
	private void setIcon(final ImageView view, String resid) {
		if (resid != null && !resid.isEmpty()) {
			new ImageLoader().loadRes(ResUtil.getSmallRelUrl(resid), ImageLoader.RADIUS_DEFAULT_PX10, view, R.drawable.common_default_icon);
		} else {
			new ImageLoader().loadRes("drawable://" + R.drawable.common_default_icon, ImageLoader.RADIUS_DEFAULT_PX10, view);
		}
	}

	/**
	 * 解析用户数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> prasePackageList(List<GamePackageVo> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			GamePackageVo vo = list.get(i);
			map.put("gpid", vo.getPackageid());
			if (gvo != null && gvo.getGamelogo() != null)
				map.put("gavatar", gvo.getGamelogo());
			tmplist.add(map);
		}
		return tmplist;
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听
			Log.v("intent", "" + intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
			queryDownloadStatus(intent);
		}
	};

	private void queryDownloadStatus(Intent intent) {
		DownloadManager.Query query = new DownloadManager.Query();
		Cursor c = downloadManager.query(query);
		if (c.moveToFirst()) {
			int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
			switch (status) {
			case DownloadManager.STATUS_PAUSED:
				Log.v("down", "STATUS_PAUSED");
			case DownloadManager.STATUS_PENDING:
				Log.v("down", "STATUS_PENDING");
			case DownloadManager.STATUS_RUNNING:
				// 正在下载，不做任何事情
				Log.v("down", "STATUS_RUNNING");
				break;
			case DownloadManager.STATUS_SUCCESSFUL:
				// 完成
				Log.v("down", "下载完成");
				ToastUtil.showToast(getActivity(), "下载完成");
				break;
			case DownloadManager.STATUS_FAILED:
				// 清除已下载的内容，重新下载
				Log.v("down", "STATUS_FAILED");
				break;
			}
		}
	}

}
