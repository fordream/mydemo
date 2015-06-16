/**      
 * RegiestRecommendGame.java Create on 2014-4-30     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.game.adapter.RegiestRecommendGameAdapter;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: GameRegiestRecommend
 * @Description: 注册推荐贴吧
 * @author 王卫
 * @date 2014-4-30 下午3:13:59
 * @Version 1.0
 * 
 */
public class GameRegiestRecommendActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

	protected static final String TAG = "GameRegiestRecommend";

	private GridView gameGridView;


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SystemContext.getInstance().setRecommendGameTag(false);
		initialize();
	}

	private void initialize() {
		// 显示左边
		setLeftVisible(false);
		// 隐藏右边
		setRightVisible(false);
		// 设置TITLE
		setTitleTxt("推荐关注");
		// 设置内容UI
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(View.inflate(this, R.layout.game_regiest_recommend, null), params);
		// 贴吧列表页面
		gameGridView = (GridView) findViewById(R.id.gameGridView);
		gameGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		requestRecommendData();
		//获取游戏服务器列表
		ProxyFactory.getInstance().getGameProxy().getGameService(new ProxyCallBack<Msgs.UserGameServerResult>() {

			@Override
			public void onSuccess(Msgs.UserGameServerResult result) {
				SystemContext.getInstance().setGameServices(result);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub

			}
		}, this);
	}

	/**
	 * 获取推荐贴吧数据
	 */
	private void requestRecommendData() {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getGameProxy().searchRecommendGames(new ProxyCallBack<PagerVo<ExtGameVo>>() {

			@Override
			public void onSuccess(PagerVo<ExtGameVo> result) {
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					// 设置LIST数据
					gameGridView.setAdapter(new RegiestRecommendGameAdapter(GameRegiestRecommendActivity.this, result.getItems()));
					gameGridView.setOnItemClickListener(GameRegiestRecommendActivity.this);
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "获取推荐贴吧失败");
				dialog.dismiss();
			}
		}, this, Long.MAX_VALUE, -Integer.MAX_VALUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ExtGameVo vo = ((RegiestRecommendGameAdapter) gameGridView.getAdapter()).getData().get(position);
		followRecommendGame(vo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
	}

	/**
	 * 关注推荐贴吧
	 * 
	 * @param tmplist
	 */
	private void followRecommendGame(final ExtGameVo vo) {
		if (NetworkUtil.isNetworkAvailable(this)) {
			if (vo != null) {
				ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {
					@Override
					public void onSuccess(Integer result) {
						onFollowCompleted(vo);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						LogUtil.e(TAG, "关注失败");
						onFollowCompleted(vo);
					}
				}, this, (Long) vo.getGameid(), MsgsConstants.OT_GAME, MsgsConstants.OP_FOLLOW, "recommend", null,null);
			} else {
				Intent intent = new Intent(this, MainFragmentActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
		} else {
			ToastUtil.showToast(this, getString(R.string.network_error));
		}
	}
	
	/**
	 * 关注成功跳转至帖子列表页
	 * @param vo
	 */
	private void onFollowCompleted(ExtGameVo vo) {
		Intent intent = new Intent(this, GameTopicListActivity.class);
		Bundle bundle = new Bundle();
	    bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, vo.getGameid());
	    bundle.putString("From", "GameRegiestRecommendActivity");
	    intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		startActivity(intent);
		finish();
	}

}
