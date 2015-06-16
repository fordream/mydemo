/**      
 * LoadDataAcitvity.java Create on 2014-2-12     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.module.game.ui.GameRegiestRecommendActivity;
import com.iwgame.msgs.utils.InitDataUtil;
import com.iwgame.utils.BitmapUtil;
import com.iwgame.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: LoadDataAcitvity
 * @Description: 加载初始化数据（静态资源贴吧、关系）
 * @author 王卫
 * @date 2014-2-12 下午5:32:37
 * @Version 1.0
 * 
 */
public class LoadDataAcitvity extends BaseSuperActivity {

	private static final String TAG = "LoadDataAcitvity";
	private ImageView launchImageView;
	public Bitmap luncher_bg = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d(TAG, "--------->LoadDataAcitvity::onCreate");
		setContentView(R.layout.app_init_loading);
		launchImageView = (ImageView) findViewById(R.id.launch_image_view);
		if(SystemContext.getInstance().isLoadAppLaunchFromLocal()){//启动页是从本地取得数据
			SystemContext.getInstance().setLuncharBgLoadTime((long)0);
			launchImageView.setImageResource(R.drawable.common_luncher_bg);
		}else{//启动页是从服务端取得数据
			luncher_bg = BitmapUtil.getBitmapFromData(this, SystemConfig.APP_LUNCHER_BG, "UTF-8");
			if(luncher_bg != null){
				launchImageView.setImageBitmap(luncher_bg);
			}else{
				SystemContext.getInstance().setLuncharBgLoadTime((long)0);
				launchImageView.setImageResource(R.drawable.common_luncher_bg);
			}
		}

		TextView load_tv_first_line = (TextView) findViewById(R.id.load_tv_first_line);
		TextView load_tv_second_line = (TextView) findViewById(R.id.load_tv_second_line);
		load_tv_first_line.setTextColor(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_loading_textcolor());
		load_tv_first_line.setText(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_loading_text());
		load_tv_second_line.setTextColor(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_loading_textcolor());

		syncGame();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
	}

	/**
	 * 同步贴吧静态数据
	 */
	private void syncGame() {
		new Thread() {
			public void run() {
				long time = System.currentTimeMillis();
				// 判断是否有贴吧数据，没有执行初始化数据
				GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
				int gcount = gameDao.getGameCount();
				if (gcount == 0) {
					InitDataUtil.initData();
					InitDataUtil.initGamePackageData();
					SystemContext.getInstance().setGameContentSyncKey(gameDao.getGameMaxTime());
				}
				LogUtil.d(TAG, "---------------->初始化贴吧数据时间=" + (System.currentTimeMillis() - time));
				android.os.Message msg = android.os.Message.obtain();
				msg.what = MESSAGE_TYPE_SYNCGAME;
				handler.sendMessage(msg);
			};
		}.start();
	}

	private static final int MESSAGE_TYPE_SYNCGAME = 1;
	private static final int MESSAGE_TYPE_ACCOUNT_ERROR = 2;
	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_TYPE_SYNCGAME:
				jumpView();
				break;
			case MESSAGE_TYPE_ACCOUNT_ERROR:
				jumpLoginView();
				break;
			}
		}
	};

	/**
	 * 跳转到主界面
	 */
	private void jumpMainView() {
		LogUtil.d(TAG, "--------->LoadDataAcitvity::jumpMainView:跳转到主界面");
		Intent intent = new Intent(this, MainFragmentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 跳转页面
	 */
	private void jumpView() {
		LogUtil.d(TAG, "--------->LoadDataAcitvity::jumpGameRecommendView:跳转到贴吧推荐页面");
		// 获取是否要打开贴吧推荐页面
		boolean rgTag = SystemContext.getInstance().getRecommendGameTag();
		int startUp = AdaptiveAppContext.getInstance().getAppConfig().getStartup();
		if (rgTag && startUp == 1) {
			SystemContext.getInstance().setRecommendGameTag(false);
			// 跳转到推荐贴吧列表界面
			Intent rgIntent = new Intent(this, GameRegiestRecommendActivity.class);
			startActivity(rgIntent);
		} else {
			jumpMainView();
		}
		finish();
	}

	/**
	 * 跳转到登录界面
	 */
	private void jumpLoginView() {
		LogUtil.d(TAG, "--------->LoadDataAcitvity::jumpLoginView:跳转到登录面");
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(luncher_bg != null && !luncher_bg.isRecycled()){ 
			// 回收并且置为null
			luncher_bg.recycle(); 
			luncher_bg = null; 
		} 
		//System.gc();
	}


}
