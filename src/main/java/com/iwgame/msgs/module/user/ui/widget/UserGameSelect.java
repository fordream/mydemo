/**      
 * UserGameSelect.java Create on 2014-4-16     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.ui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.utils.DisplayUtil;
import com.youban.msgs.R;

/**
 * @ClassName: UserGameSelect
 * @Description: 用户贴吧筛选界面
 * @author 王卫
 * @date 2014-4-16 下午8:37:12
 * @Version 1.0
 * 
 */
public class UserGameSelect extends LinearLayout {

	private LayoutInflater mInflater;

	private HorizontalScrollView gameSelectItemView;

	private LinearLayout gameSelect1;

	private LinearLayout gameSelect2;

	private TextView gameDefaultSelectItem;

	private static List<GameVo> gameList = new ArrayList<GameVo>();

	private int realIndex = -1;
	private Context context;
	private RelativeLayout gameContent;
	private LinearLayout loadingContent;
	// 贴吧选择视图集合
	private List<GameSelectView> selectViewList;
	// 选中的贴吧对象
	private GameVo selectGameVo;
	private static UserGameSelect instance;

	List<GameVo>result = new ArrayList<GameVo>();

	public List<GameSelectView> getSelectViewList() {
		return selectViewList;
	}

	public void setSelectViewList(List<GameSelectView> selectViewList) {
		this.selectViewList = selectViewList;
	}

	public int getRealIndex() {
		return realIndex;
	}

	public void setRealIndex(int realIndex) {
		this.realIndex = realIndex;
	}

	/**
	 * @param context
	 */
	public UserGameSelect(Context context, List<GameVo> result) {
		super(context);
		this.context = context;
		mInflater = LayoutInflater.from(getContext());
		View contentView = mInflater.inflate(R.layout.user_follow_selectgame_dialog_content, this, false);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(contentView, params);
		gameSelectItemView = (HorizontalScrollView) findViewById(R.id.gameSelectItemView);
		gameSelect1 = (LinearLayout) findViewById(R.id.gameSelect1);
		gameSelect2 = (LinearLayout) findViewById(R.id.gameSelect2);
		gameDefaultSelectItem = (TextView) findViewById(R.id.gameDefaultSelectItem);
		loadingContent = (LinearLayout)contentView.findViewById(R.id.loading_content);
		gameContent = (RelativeLayout)contentView.findViewById(R.id.game_content);
		initGameContent(result);
		instance = this;
	}

	/**
	 * 获取实例
	 */
	public static UserGameSelect getInstance(Context context,List<GameVo>result){
		if(gameList.size() != result.size()){
			gameList = result;
			return new UserGameSelect(context, result);
		}
		gameList = result;
		if(instance != null){
			return instance;
		}else{
			return new UserGameSelect(context, result);
		}
	}


	public String getSelectGames() {
		return getGameIds(selectViewList);
	}

	/**
	 * 获取贴吧IDS
	 * 
	 * @param lists
	 * @return
	 */
	private String getGameIds(List<GameSelectView> list) {
		if (list != null) {
			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				GameSelectView gsView = list.get(i);
				if (gsView.mIsSelected) {
					strBuf.append(gsView.mGameVo.getGameid());
					strBuf.append(",");
				}
			}
			if (strBuf.length() > 0)
				return strBuf.substring(0, strBuf.length() - 1);
			return null;
		} else {
			return null;
		}
	}

	/**
	 * 获取贴吧IDS
	 * 
	 * @param games
	 * @return
	 */
	private String getGameIds2(List<GameVo> games) {
		if (games != null) {
			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < games.size(); i++) {
				GameVo gvo = games.get(i);
				strBuf.append(gvo.getGameid());
				strBuf.append(",");
			}
			if (strBuf.length() > 0)
				return strBuf.substring(0, strBuf.length() - 1);
			return null;
		} else {
			return null;
		}
	}

	/**
	 * 
	 */
	private void initGameContent(final List<GameVo> result) {
		setLoadingUI();
		new AsyncTask<Object, Void, Void>() {

			@Override
			protected Void doInBackground(Object... arg0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(Void res) {
				if (result != null && result.size() > 0) {
					// 设置我关注的贴吧内容
					setGameSelectIcon(result);
					getGameFollowInfo(result);
				} else {
					hitGameSelect();
				}
			};
		}.execute();
	}

	/**
	 * 影藏贴吧选项
	 */
	private void hitGameSelect() {
		gameSelectItemView.setVisibility(View.GONE);
		gameDefaultSelectItem.setVisibility(View.VISIBLE);
		loadingContent.setVisibility(View.GONE);
	}

	public GameVo getSelectGameVo() {
		return selectGameVo;
	}

	/**
	 * 设置贴吧选择ICON
	 * 
	 * @param result
	 */
	private void setGameSelectIcon(List<GameVo> games) {
		if (games == null)
			return;
		selectViewList = new ArrayList<UserGameSelect.GameSelectView>();
		for (int i = 0; i < games.size(); i++) {
			GameSelectView gv = new GameSelectView(getContext(), games.get(i), new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (selectViewList != null) {
						for (int j = 0; j < selectViewList.size(); j++) {
							GameSelectView gsv = selectViewList.get(j);
							gsv.disSelect();
						}
					}
				}
			},i);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if(i == 0 || i == 1){
				params.leftMargin = 0;
			}else{
				params.leftMargin = DisplayUtil.dip2px(getContext(), 3);
			}
			if (i % 2 == 0) {
				gameSelect1.addView(gv, params);
			} else {
				gameSelect2.addView(gv, params);
			}
			gv.setGameId(games.get(i).getGameid());
			selectViewList.add(gv);
		}
	}


	/**
	 * 显示加载的动画
	 */
	private void setLoadingUI(){
		gameContent.setVisibility(View.GONE);
		loadingContent.removeAllViews();
		LinearLayout view = (LinearLayout) View.inflate(getContext(), R.layout.frame_donghua, null);
		TextView loadingDesc = (TextView)view.findViewById(R.id.loading_desc);
		loadingDesc.setVisibility(View.GONE);
		view.setBackgroundColor(context.getResources().getColor(R.color.discover_select_dialog_unselect_textcolor));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ImageView ivLoading = (ImageView) view.findViewById(R.id.loading_iv);
		AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		animationDrawable.start();
		loadingContent.addView(view, params);
	}


	/**
	 * 显示游戏图标的内容
	 */
	private void showContent(){
		gameContent.setVisibility(View.VISIBLE);
		gameDefaultSelectItem.setVisibility(View.GONE);
		loadingContent.setVisibility(View.GONE);
	}


	/**
	 * 
	 * @param games
	 */
	private void getGameFollowInfo(List<GameVo> games) {
		String ids = getGameIds2(games);
		ProxyFactory.getInstance().getGameProxy().getGameFriendFollowers(new ProxyCallBack<Map<Long, ExtGameVo>>() {

			@Override
			public void onSuccess(Map<Long, ExtGameVo> result) {
				if(result != null){
					int size = selectViewList.size();
					GameSelectView gv;
					ExtGameVo vo;
					for(int i = 0;i < size; i ++){
						gv = selectViewList.get(i);
						if(result.containsKey(gv.getGameId())){
							vo = (ExtGameVo)result.get(gv.getGameId());
							if(vo != null)
								gv.setDesc("好友数:" + vo.getFollowCount());
						}
					}
				}
				showContent();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				showContent();
			}
		}, this.getContext(), ids);
	}

	public class GameSelectView extends LinearLayout {

		private GameVo mGameVo;

		public Boolean mIsSelected = false;

		private ImageView tag;

		private TextView desc;

		private Long gameId;

		private int index;

		public Long getGameId() {
			return gameId;
		}

		public void setGameId(Long gameId) {
			this.gameId = gameId;
		}

		/**
		 * @param context
		 */
		public GameSelectView(Context context, GameVo gameVo, final OnClickListener onClickListener, int indexNum) {
			super(context);
			index = indexNum;
			mGameVo = gameVo;
			View fv = View.inflate(getContext(), R.layout.discover_group_game_select, null);
			ImageView iv = (ImageView) fv.findViewById(R.id.icon);
			tag = (ImageView) fv.findViewById(R.id.tag);
			this.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (tag.getVisibility() == View.VISIBLE) {
						realIndex = -1;
						tag.setVisibility(View.INVISIBLE);
						mIsSelected = false;
						selectGameVo = null;
					} else {
						realIndex = index;
						// 取消其他贴吧选中状态
						onClickListener.onClick(v);
						selectGameVo = mGameVo;
						tag.setVisibility(View.VISIBLE);
						mIsSelected = true;
					}
				}
			});
			addView(fv);
			desc = (TextView) fv.findViewById(R.id.desc);
			new ImageLoader().loadRes(ResUtil.getSmallRelUrl(mGameVo.getGamelogo()), 0, iv, R.drawable.common_default_icon);
		}

		public void select() {
			tag.setVisibility(View.VISIBLE);
			mIsSelected = true;
		}

		public void disSelect() {
			tag.setVisibility(View.INVISIBLE);
			mIsSelected = false;
		}

		public void setDesc(String countdesc) {
			desc.setText(countdesc);
		}

	}

}
