/**      
 * GameAdapter.java Create on 2013-9-3     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.youban.msgs.R;
import com.iwgame.msgs.common.ClearCacheListener;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: GameAdapter
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-9-3 上午11:58:26
 * @Version 1.0
 * 
 */
public class GameAdapter extends SimpleAdapter implements ClearCacheListener{
	private Map<Long, GameVo> gameVo_cache = new HashMap<Long, GameVo>();

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public GameAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
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
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.gamename = (TextView) convertView.findViewById(R.id.gamename);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) tag;
		}
		boolean hasGameName = false;
		boolean hasGameLogo = false;
		Map<String, Object> map = (HashMap<String, Object>) getItem(position);
		if (map.containsKey("gamename")) {
			String gname = (String) map.get("gamename");
			if (gname != null && !gname.isEmpty()) {
				holder.gamename.setText((String) map.get("gamename"));
				hasGameName = true;
			}
		}
		ImageViewUtil.showImage(holder.icon, null, R.drawable.common_default_icon);
		if (map.containsKey("logo")) {
			LogUtil.i("GameAdapter::getView", "----->>游戏名称：" + map.get("gamename")+", 游戏头像地址："+map.get("logo"));
			ImageViewUtil.showImage(holder.icon, (String) map.get("logo"), R.drawable.common_default_icon);
			hasGameLogo = true;
		}
		getGameInfo(map, holder, hasGameName, false);
		return convertView;
	}

	/**
	 * 
	 * @param gid
	 */
	protected void getGameInfo(Map<String, Object> map, final ViewHolder holder, final boolean hasGameName, final boolean hasGameLogo) {
		if (map != null && map.get("gid") != null) {
			final long gid = Long.valueOf(map.get("gid").toString());
			if(gameVo_cache.containsKey(gid)){
				GameVo gameVo = gameVo_cache.get(gid);
				getAndShowGameInfo(holder,gameVo, hasGameName, hasGameLogo, false);
			}else{
				ProxyFactory.getInstance().getGameProxy().getGameInfo(new ProxyCallBack<GameVo>() {

					@Override
					public void onSuccess(GameVo result) {
						getAndShowGameInfo(holder, result, hasGameName, hasGameLogo, true);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
					}
				}, SystemContext.getInstance().getContext(), gid);
			}
		}
	}

	private void getAndShowGameInfo(ViewHolder holder, GameVo gameVo, boolean hasGameName,boolean hasGameLogo, boolean isNeedSaveGameVo){
		if(gameVo != null){
			if(isNeedSaveGameVo){
				gameVo_cache.put(gameVo.getGameid(), gameVo);
			}
			if (!hasGameName) {
				String gname = gameVo.getGamename();
				if (gname != null)
					holder.gamename.setText(gname);
				else
					holder.gamename.setText("");
			}
			if (!hasGameLogo) {
				LogUtil.i("GameAdapter::getGameInfo", "----->>游戏名称：" + gameVo.getGamename()+", 游戏头像地址："+gameVo.getGamelogo());
				if (gameVo.getGamelogo() != null) {
					ImageViewUtil.showImage(holder.icon, gameVo.getGamelogo(), R.drawable.common_default_icon);
				} else {
					ImageViewUtil.showImage(holder.icon, null, R.drawable.common_default_icon);
				}
			}
		} else {
			if (!hasGameLogo)
				ImageViewUtil.showImage(holder.icon, null, R.drawable.common_default_icon);
			if (!hasGameName)
				holder.gamename.setText("");
		}
		
		onHandlerGameInfo(gameVo, holder);
	}
	
	/**
	 * 
	 * @param result
	 */
	protected void onHandlerGameInfo(GameVo result, ViewHolder holder) {

	}

	/**
	 * 获取贴吧ICON
	 * 
	 * @param view
	 * @param gid
	 */
	protected void showGameIcon(final ImageView view, final long gid) {
		final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
		GameVo result = gameDao.getGameByGameId(gid);
		if (result != null && result.getGamelogo() != null) {
			LogUtil.i("GameAdapter::showGameIcon", "----->>游戏名称：" + result.getGamename()+", 游戏头像地址："+result.getGamelogo());
			ImageViewUtil.showImage(view, result.getGamelogo(), R.drawable.common_default_icon);
		} else {
			view.setImageResource(R.drawable.common_default_icon);
		}
	}

	/**
	 * 显示图片
	 * 
	 * @param view
	 * @param resid
	 */
	protected void showImage(final ImageView view, String resid) {
		new ImageLoader().loadRes(ResUtil.getSmallRelUrl(resid), ImageLoader.RADIUS_DEFAULT_PX10, view, R.drawable.common_default_icon);
	}

	public static class ViewHolder {
		public ImageView icon;
		public TextView gamename;
		public ImageView followBtn;
		public ImageView hotArea;
		public TextView functionTxt;
		public FrameLayout distanceView;
		public TextView rdesc;
		public ImageView topView;
		public LinearLayout rightView;
		public ImageView tag;
		public TextView desc;
		public TextView desc3;
	}
	
	/**
	 * 清除缓存
	 */
	@Override
	public void clearCache() {
		gameVo_cache.clear();
	}

}
