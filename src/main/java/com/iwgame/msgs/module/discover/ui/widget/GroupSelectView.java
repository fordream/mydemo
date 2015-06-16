/**      
 * UserSelectView.java Create on 2013-12-26     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.discover.ui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwgame.msgs.common.ImageCacheLoader;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.widget.gridview.SelectGridView;
import com.iwgame.utils.DisplayUtil;
import com.youban.msgs.R;

/**
 * @ClassName: UserSelectView
 * @Description: 用户选择界面
 * @author 王卫
 * @date 2013-12-26 下午4:14:44
 * @Version 1.0
 * 
 */
public class GroupSelectView extends LinearLayout {

	private LayoutInflater mInflater;

	private LinearLayout verifySelect;

	private TextView gameSelect;
	// 贴吧第一行
	private LinearLayout gameSelect1;
	// 贴吧第二行
	private LinearLayout gameSelect2;

	private SelectGridView verifyGridView;

	private SelectGridView countGridView;

	private HorizontalScrollView gameSelectItemView;

	private FrameLayout gameSelectFL;

	private TextView gameDefaultSelectItem;

	private List<GameVo> gameList;

	// 贴吧选择视图集合
	private List<GameSelectView> selectViewList;

	public static List<Map<String, Object>> countData = getCountData();
	public static List<Map<String, Object>> verifyData = getVerifyData();
    private TextView gameBelong;
	private RelativeLayout gameContent;
	private LinearLayout loadingContent;
	private Context context;
	
	
	/**
	 * 
	 * @param context
	 * @param sexSelectedIndex
	 * @param timeSelectedIndex
	 */
	public GroupSelectView(Context context, List<GameVo> result, int mode) {
		super(context);
		this.context = context;
		mInflater = LayoutInflater.from(getContext());
		View contentView = mInflater.inflate(R.layout.discover_group_dialog_content, this, false);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(contentView, params);
		verifySelect = (LinearLayout) contentView.findViewById(R.id.verifySelect);
		gameBelong = (TextView)contentView.findViewById(R.id.belong);
		gameContent = (RelativeLayout)contentView.findViewById(R.id.gamecontent);
		gameSelect = (TextView) contentView.findViewById(R.id.gameSelect);
		loadingContent = (LinearLayout)contentView.findViewById(R.id.loading_content);
		gameSelect.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectViewList != null) {
					for (int j = 0; j < selectViewList.size(); j++) {
						selectViewList.get(j).disSelect();
					}
				}
				SystemContext.getInstance().setDiscoverGame(null);
			}
		});
		gameSelect1 = (LinearLayout) contentView.findViewById(R.id.gameSelect1);
		gameSelect2 = (LinearLayout) contentView.findViewById(R.id.gameSelect2);

		gameSelectFL = (FrameLayout) contentView.findViewById(R.id.gameSelectFL);

		gameSelectItemView = (HorizontalScrollView) contentView.findViewById(R.id.gameSelectItemView);
		gameDefaultSelectItem = (TextView) contentView.findViewById(R.id.gameDefaultSelectItem);
		gameDefaultSelectItem.setVisibility(View.INVISIBLE);
		initVerifyContent(verifySelect);
		gameList = result;
		if(mode == 1){
			//1表示游伴
			if(result != null && result.size() > 0){
				setLoadingUI();
				setGameSelectIcon();
			}else{
				hitGameSelect();
			}
		}else if(mode == 2){
			//2表示单攻略
			gameContent.setVisibility(View.GONE);
			gameSelectFL.setVisibility(View.GONE);
			gameBelong.setVisibility(View.GONE);
			loadingContent.setVisibility(View.GONE);
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
		loadingContent.setVisibility(View.GONE);
	}
	
	/**
	 * 影藏贴吧选项
	 */
	private void hitGameSelect() {
		gameSelectFL.setVisibility(View.GONE);
		gameSelectItemView.setVisibility(View.GONE);
		gameDefaultSelectItem.setVisibility(View.VISIBLE);
		gameDefaultSelectItem.setText("您还未关注任何游戏");
	}

	public static List<Map<String, Object>> getCountData() {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "不限");
		map.put("id", 0);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "<100");
		map.put("id", 1);
		map.put("min", 0);
		map.put("max", 100);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "100-200");
		map.put("id", 2);
		map.put("min", 101);
		map.put("max", 200);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, ">200");
		map.put("id", 3);
		map.put("min", 200);
		map.put("max", Integer.MAX_VALUE);
		items.add(map);
		return items;
	}

	public static List<Map<String, Object>> getVerifyData() {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "全部");
		map.put("id", 0);
		map.put("verify", null);
		map.put(SelectGridView.ITEM_ALL, true);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "无验证");
		map.put("id", 1);
		map.put("verify", false);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "有验证");
		map.put("id", 2);
		map.put("verify", true);
		items.add(map);
		return items;
	}

	public static int[] getSelectUserCount(int index) {
		if (index == 0) {
			return null;
		} else {
			for (int i = 0; i < countData.size(); i++) {
				if (index == (Integer) countData.get(i).get("id")) {
					int[] ret = new int[2];
					ret[0] = (Integer) countData.get(i).get("min");
					ret[1] = (Integer) countData.get(i).get("max");
					return ret;
				}
			}
			return null;
		}
	}

	public static Boolean getSelectVerify(int index) {
		if (index == 0) {
			return null;
		} else {
			for (int i = 0; i < verifyData.size(); i++) {
				if (index == (Integer) verifyData.get(i).get("id")) {
					return (Boolean) verifyData.get(i).get("verify");
				}
			}
			return null;
		}
	}

	public static String getSelectGamesData() {
		String games = SystemContext.getInstance().getDiscoverGame();
		if (games != null && !"".equals(games) && !"-1".equals(games)) {
			return games;
		} else {
			return null;
		}
	}

	/**
	 * 
	 */
	private void initVerifyContent(LinearLayout verifySelect) {
		verifyGridView = new SelectGridView(this.getContext(), verifyData, 3, true, null, null);
		verifyGridView.setSelection(SystemContext.getInstance().getDiscoverVerify());
		verifySelect.addView(verifyGridView);
	}

	/**
	 * 
	 * @param items
	 * @param index
	 */
	private void checkItem(List<Map<String, Object>> items, String selects) {
		if (items != null && selects != null && !"".equals(selects)) {
			String[] arrs = selects.split(",");
			for (int i = 0; i < items.size(); i++) {
				Map<String, Object> item = items.get(i);
				for (int j = 0; j < arrs.length; j++) {
					if (item.containsKey("gid") && item.get("gid").equals(Long.valueOf(arrs[j]))) {
						item.put(SelectGridView.ITEM_CHECK, true);
						break;
					}
				}
			}
		} else {
			return;
		}
	}

	/**
	 * 保存选显卡信息
	 */
	public void saveSelectItems() {
		// 保存贴吧信息
		String ids = getGameIds(selectViewList);
		if (ids != null)
			SystemContext.getInstance().setDiscoverGame(ids);
		// 保存是否要验证信息
		List<Map<String, Object>> verifySelectItems = verifyGridView.getSelectedItems();
		if (verifySelectItems != null && verifySelectItems.size() > 0)
			SystemContext.getInstance().setDiscoverVerify((Integer) verifySelectItems.get(0).get("id"));
		else
			SystemContext.getInstance().setDiscoverVerify(0);
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
	 * 
	 * @return
	 */
	public Boolean getSelectVerify() {
		return (Boolean) verifyGridView.getSelectedItems().get(0).get("verify");
	}

	/**
	 * 
	 * @return
	 */
	public int[] getSelectUserCount() {
		int id = (Integer) countGridView.getSelectedItems().get(0).get("id");
		if (id == 0) {
			return null;
		} else {
			int[] ret = new int[2];
			ret[0] = (Integer) countGridView.getSelectedItems().get(0).get("min");
			ret[1] = (Integer) countGridView.getSelectedItems().get(0).get("max");
			return ret;
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getSelectGames() {
		return getGameIds(selectViewList);
	}

	/**
	 * 设置贴吧选择ICON
	 * 
	 * @param result
	 */
	private void setGameSelectIcon() {
		new AsyncTask<Object, Void, Void>() {

			@Override
			protected Void doInBackground(Object... arg0) {
				selectViewList = new ArrayList<GroupSelectView.GameSelectView>();
				int size = gameList.size();
				for (int i = 0; i < size; i++) {
					GameSelectView gv = new GameSelectView(getContext(), gameList.get(i));
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
					selectViewList.add(gv);
				}
				return null;
			}
			protected void onPostExecute(Void result) {
				showContent();
			};
		}.execute();
	}

	
	class GameSelectView extends LinearLayout {

		public GameVo mGameVo;

		public Boolean mIsSelected = false;

		private ImageView tag;

		/**
		 * @param context
		 */
		public GameSelectView(Context context, GameVo gameVo) {
			super(context);
			mGameVo = gameVo;
			View fv = View.inflate(getContext(), R.layout.discover_group_game_select, null);
			ImageView iv = (ImageView) fv.findViewById(R.id.icon);
			tag = (ImageView) fv.findViewById(R.id.tag);
			fv.findViewById(R.id.desc).setVisibility(View.GONE);
			fv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (tag.getVisibility() == View.VISIBLE) {
						tag.setVisibility(View.INVISIBLE);
						mIsSelected = false;
					} else {
						tag.setVisibility(View.VISIBLE);
						mIsSelected = true;
					}
				}
			});
			addView(fv);
			String sgames = getSelectGamesData();
			if (sgames != null) {
				String[] games = sgames.split(",");
				for (int i = 0; i < games.length; i++) {
					String gid = games[i];
					if (gid.equals(String.valueOf(gameVo.getGameid()))) {
						tag.setVisibility(View.VISIBLE);
						mIsSelected = true;
					}
				}
			}
			//new ImageCacheLoader().loadRes(ResUtil.getSmallRelUrl(mGameVo.getGamelogo()), 0, iv, R.drawable.common_default_icon);
			ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(mGameVo.getGamelogo()), iv, R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
		}

		public void select() {
			tag.setVisibility(View.VISIBLE);
			mIsSelected = true;
		}

		public void disSelect() {
			tag.setVisibility(View.INVISIBLE);
			mIsSelected = false;
		}

	}
}
