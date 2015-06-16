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

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.GameUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.RelationGameVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: RecommendGameFollowAdapter
 * @Description: 推荐贴吧适配器
 * @author 王卫
 * @date 2013-9-10 下午05:03:48
 * @Version 1.0
 * 
 */
public class DiscoverCommonGameAdapter extends GameAdapter {
	// 用于列表类型(没有操作按钮)
	public static int MODE_SIMPLE = 1;
	// 附近的贴吧(有关注等按钮)
	public static int MODE_COMMON = 2;
	// 注册推荐贴吧(显示复选框)
	public static int MODE_RECOMMEND = 3;
	// 显示关注标记
	public static int MODE_SHOW_TAG = 4;
	// 描述模式1：不需要修改描述信息域
	public static int DESC_MODE_0 = 0;
	// 描述模式1：需要修改描述信息域
	public static int DESC_MODE_1 = 1;
	// 背景模式[0先白色1后白色]
	private int mBgmode = 0;
	// 列表显示类型
	private int mMode;
	// 列表描述显示类型
	private int mDescMode = DESC_MODE_1;
	private UserVo loginUserVo = null;
	private int VIEW_TYPE = 2;
	final private int TYPE_DETAIL = 0;
	final private int TYPE_NODATA = 1;
	private int noDataHeight;// 用于在发现页面当没有获取到数据的时候，显示默认图片的高度

	public int getNoDataHeight() {
		return noDataHeight;
	}

	public void setNoDataHeight(int noDataHeight) {
		this.noDataHeight = noDataHeight;
	}

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 * @param bgmode
	 */
	public DiscoverCommonGameAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, int bgmode) {
		super(context, data, resource, from, to);
		mBgmode = bgmode;
		loginUserVo = SystemContext.getInstance().getExtUserVo();
	}

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 * @param bgmode
	 * @param mode
	 */
	public DiscoverCommonGameAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, int bgmode, int mode) {
		super(context, data, resource, from, to);
		mBgmode = bgmode;
		mMode = mode;
		loginUserVo = SystemContext.getInstance().getExtUserVo();
	}

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 * @param bgmode
	 * @param mode
	 */
	public DiscoverCommonGameAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, int bgmode,
			int mode, int descMode) {
		super(context, data, resource, from, to);
		mBgmode = bgmode;
		mMode = mode;
		mDescMode = descMode;
		loginUserVo = SystemContext.getInstance().getExtUserVo();
	}

	/**
	 * 获取到每一种数据的类型
	 */
	@Override
	public int getItemViewType(int position) {
		Map<String, Object> map = (HashMap<String, Object>) getItem(position);
		if (map.size() <= 0) {
			return TYPE_NODATA;
		} else {
			return TYPE_DETAIL;
		}
	}

	/**
	 * 返回了适配器里面有几种类型
	 */
	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		ViewHolder holder1 = new ViewHolder();
		NodateViewHolder nodataHolder = new NodateViewHolder();
		if (convertView == null) {
			switch (type) {
			case TYPE_DETAIL:
				convertView = View.inflate(SystemContext.getInstance().getContext(), R.layout.game_list_item, null);
				holder1.followBtn = (ImageView) convertView.findViewById(R.id.functionBtn);
				holder1.hotArea = (ImageView) convertView.findViewById(R.id.hotArea);
				holder1.functionTxt = (TextView) convertView.findViewById(R.id.functionTxt);
				holder1.distanceView = (FrameLayout) convertView.findViewById(R.id.distanceView);
				holder1.rdesc = (TextView) convertView.findViewById(R.id.rdesc);
				holder1.gamename = (TextView) convertView.findViewById(R.id.gamename);
				holder1.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder1.topView = (ImageView) convertView.findViewById(R.id.topVeiw);
				holder1.rightView = (LinearLayout) convertView.findViewById(R.id.rightView);
				holder1.tag = (ImageView) convertView.findViewById(R.id.tag);
				holder1.desc = (TextView) convertView.findViewById(R.id.desc);
				holder1.desc3 = (TextView) convertView.findViewById(R.id.desc3);
				convertView.setTag(holder1);
				break;

			case TYPE_NODATA:
				LinearLayout v = (LinearLayout) View.inflate(SystemContext.getInstance().getContext(), R.layout.no_data_view_discover, null);
				convertView = v;
				nodataHolder.bg_layout = v;
				nodataHolder.bgIcon = (ImageView) convertView.findViewById(R.id.bgIcon);
				convertView.setTag(nodataHolder);
				break;
			}
		} else {
			switch (type) {
			case TYPE_DETAIL:
				holder1 = (ViewHolder) convertView.getTag();
				break;

			case TYPE_NODATA:
				nodataHolder = (NodateViewHolder) convertView.getTag();
				break;
			}
		}
		final ViewHolder holder = holder1;
		final NodateViewHolder nodateViewHolder = nodataHolder;
		if (type == TYPE_DETAIL) {
			// 设置UI元素
			holder.hotArea.getBackground().setAlpha(0);
			final Map<String, Object> map = (HashMap<String, Object>) getItem(position);

			// 获取距离
			String distance = (String) map.get("distance");
			if (holder.distanceView != null) {
				if (distance != null && !"".equals(distance) && !"-1".equals(distance)) {
					holder.distanceView.setVisibility(View.VISIBLE);
					holder.rdesc.setText(distance);
				} else {
					holder.distanceView.setVisibility(View.INVISIBLE);
				}
			}
			// 设置排行榜
			if (map.get("top") != null) {
				holder.topView.setVisibility(View.VISIBLE);
				setTop((Integer) map.get("top"), holder.topView);
			}
			// 贴吧gid
			final long gid = (Long) (map.get("gid"));
//			if (map.containsKey("logo")) {
//				LogUtil.i("GameAdapter::getView", "----->>游戏名称：" + map.get("gamename") + ", 游戏头像地址：" + map.get("logo"));
//				ImageViewUtil.showImage(holder.icon, (String) map.get("logo"), R.drawable.common_default_icon);
//			}
			if (map.containsKey("gamename") && map.get("gamename") != null) {
				holder.gamename.setText((String) map.get("gamename"));
			}
			ProxyFactory.getInstance().getGameProxy().getGameInfo(new ProxyCallBack<GameVo>() {

				@Override
				public void onSuccess(GameVo result) {
					if (result != null) {
						holder.gamename.setText(result.getGamename());
						ImageViewUtil.showImage(holder.icon, result.getGamelogo(), R.drawable.common_default_icon);
						map.put("gamename", result.getGamename());
						map.put("logo", result.getGamelogo());
					}
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					// TODO Auto-generated method stub

				}
			}, SystemContext.getInstance().getContext(), gid);
			// 是否关注
			if (mMode == MODE_SIMPLE) {
				holder.rightView.setVisibility(View.GONE);
			} else if (mMode == MODE_COMMON) {
				holder.rightView.setVisibility(View.VISIBLE);
				boolean follow = false;
				if (map.containsKey("follow")) {
					follow = (Boolean) (map.get("follow"));
				} else {
					RelationGameVo relGameVo = DaoFactory.getDaoFactory().getRelationGameDao(SystemContext.getInstance().getContext())
							.getRelationGameByGameId(gid);
					if (relGameVo != null && relGameVo.getRelation() == 1) {
						follow = true;
					}
				}
				if (follow) {
					holder.followBtn.setEnabled(false);
					holder.hotArea.setEnabled(false);
				} else {
					holder.followBtn.setEnabled(true);
					holder.hotArea.setEnabled(true);
					holder.followBtn.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							holder.followBtn.setEnabled(true);
							holder.hotArea.setEnabled(true);
							followGame(holder.followBtn, holder.hotArea, gid, MsgsConstants.OP_FOLLOW, position, holder.functionTxt);
						}
					});
					holder.hotArea.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							holder.followBtn.setEnabled(true);
							holder.hotArea.setEnabled(true);
							followGame(holder.followBtn, holder.hotArea, gid, MsgsConstants.OP_FOLLOW, position, holder.functionTxt);
						}
					});
				}
			} else if (mMode == MODE_RECOMMEND) {// 用户注册推荐
				holder.rightView.setVisibility(View.VISIBLE);
				addCheckBoxView(convertView, holder.rightView, map);
			} else if (mMode == MODE_SHOW_TAG) {// 只显示关注标签
				RelationGameVo relGameVo = DaoFactory.getDaoFactory().getRelationGameDao(SystemContext.getInstance().getContext())
						.getRelationGameByGameId(gid);
				if (relGameVo != null && relGameVo.getRelation() == 1) {
					holder.distanceView.setVisibility(View.VISIBLE);
					holder.rightView.setVisibility(View.VISIBLE);
					holder.followBtn.setVisibility(View.INVISIBLE);
					holder.hotArea.setVisibility(View.INVISIBLE);
					holder.functionTxt.setVisibility(View.INVISIBLE);
					holder.tag.setVisibility(View.VISIBLE);
					holder.tag.setImageResource(R.drawable.discover_game_follow_tag);
				} else {
					holder.rightView.setVisibility(View.GONE);
				}
			}
			showDesc(convertView, map, holder.desc, holder.desc3);
		} else if (type == TYPE_NODATA) {
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, noDataHeight);
			nodateViewHolder.bg_layout.setLayoutParams(params);
			nodateViewHolder.bgIcon.setBackgroundResource(R.drawable.common_no_seach_postbar);
		}
		return convertView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.game.adapter.GameAdapter#onHandlerGameInfo(com
	 * .iwgame.msgs.vo.local.GameVo,
	 * com.iwgame.msgs.module.game.adapter.GameAdapter.ViewHolder)
	 */
	@Override
	protected void onHandlerGameInfo(GameVo result, ViewHolder holder) {
		super.onHandlerGameInfo(result, holder);

		if (mDescMode == DESC_MODE_1 && result != null) {
			String type = "类型：";
			if (result.getType() == null) {
				type += "未知";
			} else {
				type += result.getType();
			}
			String publisher = "开发商：";
			if (result.getPublisher() == null) {
				publisher += "未知";
			} else {
				publisher += result.getPublisher();
			}
			if (holder != null && holder.desc != null) {
				holder.desc.setText(type + " | " + publisher);
			}
		}
	}

	/**
	 * 
	 * @param convertView
	 * @param desc1
	 * @param desc2
	 * @param desc3
	 * @param desc4
	 */
	private void showDesc(View convertView, Map<String, Object> map, TextView desc, TextView desc3) {
		String ds1 = null;
		String ds3 = null;
		if (map.containsKey("desc")) {
			ds1 = (String) map.get("desc");
		}
		if (map.containsKey("desc3")) {
			ds3 = (String) map.get("desc3");
		}
		if (mDescMode != DESC_MODE_1) {
			if (ds1 != null) {
				desc.setVisibility(View.VISIBLE);
				desc.setText(ds1);
			} else {
				desc.setVisibility(View.GONE);
			}
		}

		if (ds3 != null) {
			desc3.setVisibility(View.VISIBLE);
			desc3.setText(ds3);
		} else {
			desc3.setVisibility(View.GONE);
		}
	}

	/**
	 * 添加复选框
	 * 
	 * @param convertView
	 * @param rightView
	 * @param mapData
	 */
	private void addCheckBoxView(View convertView, LinearLayout rightView, final Map<String, Object> mapData) {
		// 添加CHECKBOX
		rightView.removeAllViews();
		LinearLayout boxLayout = (LinearLayout) View.inflate(convertView.getContext(), R.layout.common_checkbox, null);
		final CheckBox cbox = (CheckBox) boxLayout.findViewById(R.id.checkBox);
		rightView.addView(boxLayout);
		cbox.setChecked((Boolean) mapData.get("isChecked"));
		cbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mapData != null) {
					mapData.put("isChecked", isChecked);
				}
			}
		});
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cbox != null) {
					if (cbox.isChecked()) {
						cbox.setChecked(false);
						mapData.put("isChecked", false);
					} else {
						cbox.setChecked(true);
						mapData.put("isChecked", true);
					}
				}
			}
		});
	}

	/**
	 * 添加或取消关注
	 * 
	 * @param v
	 * @param gid
	 * @param op
	 */
	private void followGame(final View followBtn, final View hotArea, final long gid, int op, final int position, final TextView functionTxt) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(followBtn.getContext());
		dialog.show();
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					followBtn.setEnabled(false);
					hotArea.setEnabled(false);
					Map<String, Object> map = (HashMap<String, Object>) getItem(position);
					map.put("follow", true);
					ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GAME, null);
					HashMap<String, String> ummap = new HashMap<String, String>();
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(loginUserVo.getUserid()));
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, loginUserVo.getUsername());
					ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, String.valueOf(gid));
					GameVo gvo = DaoFactory.getDaoFactory().getGameDao(followBtn.getContext()).getGameById(gid);
					if (gvo != null)
						ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, gvo.getGamename());
					MobclickAgent.onEvent(followBtn.getContext(), UMConfig.MSGS_EVENT_GAME_FOLLOW, ummap);
					break;
				default:
					followBtn.setEnabled(true);
					hotArea.setEnabled(true);
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				dialog.dismiss();
				followBtn.setEnabled(true);
				hotArea.setEnabled(true);
				if (followBtn.getContext() != null) {
					if (result == Msgs.ErrorCode.EC_MSGS_OVER_COUNT_VALUE) {
						int mfcount = SystemContext.getInstance().getFGM();
						ErrorCodeUtil.handleErrorCode(followBtn.getContext(), com.iwgame.msgs.common.ErrorCode.EC_CLIENT_FOLLOWGAME_OVER_COUNT, null,
								mfcount);
						GameUtil.redressGameRel(mfcount);
					} else {
						ErrorCodeUtil.handleErrorCode(followBtn.getContext(), result, resultMsg);
					}
				}
			}
		}, followBtn.getContext(), gid, MsgsConstants.OT_GAME, op, null, null, null);
	}

	/**
	 * 搜素贴吧信息
	 * 
	 * @param gid
	 * @param tv
	 */
	private void searchGameUserInfo(long gid, final TextView tv) {
		ProxyFactory
				.getInstance()
				.getUserProxy()
				.searchUsers(new ProxyCallBack<PagerVo<UserObject>>() {

					@Override
					public void onSuccess(PagerVo<UserObject> result) {
						StringBuffer desc = new StringBuffer();
						if (result != null && result.getItems() != null && result.getItems().size() > 0) {
							UserObject uobj = result.getItems().get(0);
							desc.append(uobj.getNickname());
							desc.append("等" + result.getItems().size() + "个好友已关注");
						} else {
							desc.append("没有好友关注");
						}
						tv.setText(desc.toString());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						tv.setText("没有好友关注");
					}
				}, tv.getContext(), gid, null, null, 1, "1", loginUserVo.getUserid() + "", Integer.MAX_VALUE, -Integer.MAX_VALUE, null, null, null,
						null, null);
	}

	/**
	 * 设置排行榜标签
	 * 
	 * @param top
	 */
	private void setTop(int top, ImageView topView) {
		switch (top) {
		case 1:
			topView.setBackgroundResource(R.drawable.game_top_1);
			break;
		case 2:
			topView.setBackgroundResource(R.drawable.game_top_2);
			break;
		case 3:
			topView.setBackgroundResource(R.drawable.game_top_3);
			break;
		case 4:
			topView.setBackgroundResource(R.drawable.game_top_4);
			break;
		case 5:
			topView.setBackgroundResource(R.drawable.game_top_5);
			break;
		case 6:
			topView.setBackgroundResource(R.drawable.game_top_6);
			break;
		case 7:
			topView.setBackgroundResource(R.drawable.game_top_7);
			break;
		case 8:
			topView.setBackgroundResource(R.drawable.game_top_8);
			break;
		case 9:
			topView.setBackgroundResource(R.drawable.game_top_9);
			break;
		case 10:
			topView.setBackgroundResource(R.drawable.game_top_10);
			break;
		default:
			break;
		}
	}

	/**
	 * 没有数据的时候的viewholder
	 * 
	 * @author jczhang
	 * 
	 */
	static class NodateViewHolder {
		LinearLayout bg_layout;
		ImageView bgIcon;
	}
}
