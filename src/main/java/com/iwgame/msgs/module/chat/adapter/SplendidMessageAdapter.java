/**      
 * SplendidMessageAdapter.java Create on 2014-10-28     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.chat.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ImageCacheLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.chat.ui.GroupChatFragmentActivity;
import com.iwgame.msgs.module.group.adapter.SplendidGroupAdapter;
import com.iwgame.msgs.module.group.ui.GroupDetailActivity;
import com.iwgame.msgs.module.postbar.adapter.SplendidTopicAdapter;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.module.postbar.ui.TopicDetailActivity;
import com.iwgame.msgs.module.user.adapter.SplendidUserAdapter;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PostbarTopicDetail;
import com.iwgame.msgs.utils.AppUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.listview.SplendidListView;
import com.iwgame.msgs.widget.roundedimageview.RoundedImageView;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: SplendidMessageAdapter
 * @Description: 精彩推荐适配器
 * @author Administrator
 * @date 2014-10-28 下午1:59:48
 * @Version 1.0
 * 
 */
public class SplendidMessageAdapter extends BaseAdapter {

	private final String TAG = "SplendidMessageAdapter";
	private Context context;
	private LayoutInflater inflater;
	private List<Map<String, Object>> data;
	private Map<Long, Integer> pushCountMap = new HashMap<Long, Integer>();
	private UserVo loginUserVo = null;
	private GroupVo groupVo;
	/**
	 * 精彩推荐
	 */
	public static int PAGE_TYPE_SPLENDID_PUSH = 10;

	// 布局类型有2中，TYPE_GAME为推荐贴吧，TYPE_NOT_GAME为推荐用户，推荐公会和推荐帖子,缘分好友
	final int VIEW_TYPE = 2;
	final int TYPE_GAME = 0;
	final int TYPE_NOT_GAME = 1;
	private GroupDao groupDao;

	
	public SplendidMessageAdapter(Context context, List<Map<String, Object>> data) {
		this.context = context;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
		loginUserVo = SystemContext.getInstance().getExtUserVo();
		groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return data.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		Map<String, Object> map = data.get(position);
		String cardsType = null;
		if(map.containsKey("cardsType")){
			cardsType = (String) map.get("cardsType");
		}
		if(cardsType != null && cardsType.equals("POSTBAR")){//贴吧
			return TYPE_GAME;
		}else{//非贴吧
			return TYPE_NOT_GAME;
		}
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final int type = getItemViewType(position);
		GameViewHolder tmp_gameViewHolder = null;
		NotGameViewHolder tmp_notGameViewHolder = null;
		if (convertView == null) {
			switch (type) {
			case TYPE_GAME://贴吧
				convertView = this.inflater.inflate(R.layout.splendid_recommend_grid_item, null);
				tmp_gameViewHolder = new GameViewHolder();
				tmp_gameViewHolder.splendid_recommend_table = (TableLayout) convertView.findViewById(R.id.splendid_recommend_table);
				tmp_gameViewHolder.item_chat_date = (TextView) convertView.findViewById(R.id.item_chat_date);
				convertView.setTag(tmp_gameViewHolder);
				break;
			case TYPE_NOT_GAME://非贴吧
				convertView = this.inflater.inflate(R.layout.splendid_recommend_list_item, null);
				tmp_notGameViewHolder = new NotGameViewHolder();
				tmp_notGameViewHolder.splendid_recommond_type = (TextView) convertView.findViewById(R.id.splendid_recommond_type);
				tmp_notGameViewHolder.splendid_recommond_imageview = (ImageView) convertView.findViewById(R.id.splendid_recommond_imageview);
				tmp_notGameViewHolder.item_chat_date = (TextView) convertView.findViewById(R.id.item_chat_date);
				tmp_notGameViewHolder.splendid_recommend_list = (SplendidListView) convertView.findViewById(R.id.splendid_recommend_list);
				convertView.setTag(tmp_notGameViewHolder);
				break;
			}
		} else {
			switch (type) {
			case TYPE_GAME:
				tmp_gameViewHolder = (GameViewHolder) convertView.getTag();
				break;
			case TYPE_NOT_GAME:
				tmp_notGameViewHolder = (NotGameViewHolder) convertView.getTag();
				break;
			}
		}
		final Map<String, Object> voMap = data.get(position);
		String cardsType = (String) voMap.get("cardsType");//推荐类型
		if(null == cardsType){//空类型添加保护
			return convertView;
		}
		
		if(voMap.containsKey("pushId") && Long.valueOf(voMap.get("pushId").toString()) >= 0){
			long pushid = Long.valueOf(voMap.get("pushId").toString());
			sendPushIdString(pushid);
		}
		// 判断时间是否需要显示,默认true，需要显示，false不需要显示
		boolean isShowTime = true;
		if (position > 0
				&& SafeUtils.getFormatDate(Long.valueOf(data.get(position - 1).get("createTime").toString()), "MM-dd HH:mm").equals(
						SafeUtils.getFormatDate(Long.valueOf(voMap.get("createTime").toString()), "MM-dd HH:mm"))) {
			isShowTime = false;
		}

		GameViewHolder gameViewHolder = tmp_gameViewHolder;
		NotGameViewHolder notGameViewHolder = tmp_notGameViewHolder;

		if (type == TYPE_GAME) {// 贴吧
			// 增加判断时间是否需要显示
			if (isShowTime) {
				gameViewHolder.item_chat_date.setVisibility(View.VISIBLE);
				gameViewHolder.item_chat_date.setText(SafeUtils.getFormatDate(Long.valueOf(voMap.get("createTime").toString()), "MM-dd HH:mm"));
			} else {
				gameViewHolder.item_chat_date.setVisibility(View.GONE);
			}
			//填充数据
			setSplendidGamesTableLayout(gameViewHolder.splendid_recommend_table,(ArrayList<Map<String, Object>>)voMap.get("arrayList"));
		} else {
			// 增加判断时间是否需要显示
			if (isShowTime) {
				notGameViewHolder.item_chat_date.setVisibility(View.VISIBLE);
				notGameViewHolder.item_chat_date.setText(SafeUtils.getFormatDate(Long.valueOf(voMap.get("createTime").toString()), "MM-dd HH:mm"));
			} else {
				notGameViewHolder.item_chat_date.setVisibility(View.GONE);
			}

			//填充数据
			if("GROUP".equals(cardsType)){// 公会
				tmp_notGameViewHolder.splendid_recommond_imageview.setBackgroundResource(R.drawable.news_icon_recommend3);
				tmp_notGameViewHolder.splendid_recommond_type.setText(context.getResources().getString(R.string.message_notify_splendid_group));
				SplendidGroupAdapter adapter = new SplendidGroupAdapter(context, (ArrayList<Map<String, Object>>)voMap.get("arrayList"));
				notGameViewHolder.splendid_recommend_list.setAdapter(adapter);
				notGameViewHolder.splendid_recommend_list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						ArrayList<Map<String, Object>> tempList = (ArrayList<Map<String, Object>>)voMap.get("arrayList");
						Map<String, Object> map = tempList.get(arg2);
						Long grid = Long.valueOf(map.get("grid").toString());
						int rel = 0;
						groupVo = groupDao.findGroupByGrid(grid);
						if(groupVo == null)
							rel = 0;
						else
							rel = groupVo.getRelWithGroup();
						if (rel == 0) {
							Intent intent = new Intent(context, GroupDetailActivity.class);
							Bundle bundle = new Bundle();
							bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
							intent.putExtras(bundle);
							context.startActivity(intent);
							
							String groupName = map.containsKey("title")?(String) map.get("title"):"";
							addUmEvent(UMConfig.MSGS_EVENT_SPLENDID_GROUP_CLICK, map.get("grid").toString(), groupName);
						} else {
							// 群聊
							Intent intent = new Intent(context, GroupChatFragmentActivity.class);
							Bundle bundle = new Bundle();
							bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, grid);
							intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
							context.startActivity(intent);
						}

					}
				});
			}else if("USER".equals(cardsType)){// 用户
				tmp_notGameViewHolder.splendid_recommond_imageview.setBackgroundResource(R.drawable.news_icon_recommend3);
				tmp_notGameViewHolder.splendid_recommond_type.setText(context.getResources().getString(R.string.message_notify_splendid_user));
				SplendidUserAdapter adapter = new SplendidUserAdapter(context, (ArrayList<Map<String, Object>>)voMap.get("arrayList"), R.layout.splendid_user_list_item, new String[] { "title" },
						new int[] { R.id.nickname }, false, cardsType);
				notGameViewHolder.splendid_recommend_list.setAdapter(adapter);
				notGameViewHolder.splendid_recommend_list.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						ArrayList<Map<String, Object>> tempList = (ArrayList<Map<String, Object>>)voMap.get("arrayList");
						Map<String, Object> map = tempList.get(arg2);
						Object uid = map.get("uid");
						if (uid != null && !uid.equals(loginUserVo.getUserid())) {
							Intent intent = new Intent(context, UserDetailInfoActivity.class);
							Bundle bundle = new Bundle();
							bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, Long.valueOf(uid.toString()));
							intent.putExtras(bundle);
							context.startActivity(intent);
							
							String userName = map.containsKey("title")?(String) map.get("title"):"";
							addUmEvent(UMConfig.MSGS_EVENT_SPLENDID_USER_CLICK, uid.toString(), userName);
						}
					}
				});
			}else if("TOPIC".equals(cardsType)){// 帖子
				tmp_notGameViewHolder.splendid_recommond_imageview.setBackgroundResource(R.drawable.news_icon_recommend3);
				tmp_notGameViewHolder.splendid_recommond_type.setText(context.getResources().getString(R.string.message_notify_splendid_topic));
				SplendidTopicAdapter adapter = new SplendidTopicAdapter(context, (ArrayList<Map<String, Object>>)voMap.get("arrayList"), R.layout.splendid_topic_item, new String[] { "title" },
						new int[] { R.id.splendid_topic_title});
				notGameViewHolder.splendid_recommend_list.setAdapter(adapter);
				notGameViewHolder.splendid_recommend_list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						ArrayList<Map<String, Object>> tempList = (ArrayList<Map<String, Object>>)voMap.get("arrayList");
						final Map<String, Object> map = tempList.get(arg2);
						final Object tid = map.get("tid");
						// 通过帖子id获得帖子详情，然后判断帖子是否被删除
						// 获得帖子详情
						ProxyCallBack<Msgs.PostbarTopicDetail> callback = new ProxyCallBack<Msgs.PostbarTopicDetail>() {

							@Override
							public void onSuccess(final PostbarTopicDetail result) {
								if (result != null) {
									if (result.getIsDel()) {
										ToastUtil.showToast(context, context.getResources().getString(R.string.postbaor_set_topic_del));
									} else {
										long gid = result.getGameid();
										if(gid != 0){
											// 根据当前的应用配置判断是否要启动游伴
											AppConfig appconfig = AdaptiveAppContext.getInstance().getAppConfig();
											if (appconfig != null && appconfig.isRecbarmsg() && gid != appconfig.getGameId()) {
												AppUtil.openGame(context, Long.valueOf(tid.toString()), TopicDetailActivity.class.getName(), context.getResources().getString(R.string.postbar_show_topic_tip_for_youban_uninstall));
											}else {
												Intent intent = new Intent(context, TopicDetailActivity.class);
												Bundle bundle = new Bundle();
												bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, Long.valueOf(tid.toString()));
												intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
												context.startActivity(intent);
												
												String topicName = map.containsKey("title")?(String) map.get("title"):"";
												addUmEvent(UMConfig.MSGS_EVENT_SPLENDID_TOPIC_CLICK, tid.toString(), topicName);
											}
										}
									}
								} else {
									ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENT_POSTBAR_GETTOPICDETAILISNULL,null);
								}
							}

							@Override
							public void onFailure(final Integer result,String resultMsg) {
								ErrorCodeUtil.handleErrorCode(context, result,resultMsg);
							}

						};
						ProxyFactory.getInstance().getPostbarProxy().getTopicDetail(callback, context, Long.valueOf(tid.toString()));
					}
				});
			}else if("FATE".equals(cardsType)){// 缘分好友
				tmp_notGameViewHolder.splendid_recommond_type.setText(context.getResources().getString(R.string.message_notify_fate_user));
				tmp_notGameViewHolder.splendid_recommond_imageview.setBackgroundResource(R.drawable.news_icon_recommend4);
				SplendidUserAdapter adapter = new SplendidUserAdapter(context, (ArrayList<Map<String, Object>>)voMap.get("arrayList"), R.layout.splendid_user_list_item, new String[] { "title" },
						new int[] { R.id.nickname }, false, cardsType);
				notGameViewHolder.splendid_recommend_list.setAdapter(adapter);
				notGameViewHolder.splendid_recommend_list.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						ArrayList<Map<String, Object>> tempList = (ArrayList<Map<String, Object>>)voMap.get("arrayList");
						Map<String, Object> map = tempList.get(arg2);
						Object uid = map.get("uid");
						if (uid != null && !uid.equals(loginUserVo.getUserid())) {
							Intent intent = new Intent(context, UserDetailInfoActivity.class);
							Bundle bundle = new Bundle();
							bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, Long.valueOf(uid.toString()));
							intent.putExtras(bundle);
							context.startActivity(intent);
							
							String userName = map.containsKey("title")?(String) map.get("title"):"";
							addUmEvent(UMConfig.MSGS_EVENT_FATE_USER_CLICK, uid.toString(), userName);
						}
					}
				});
			}
		}

		return convertView;
	}

	/**
	 * 添加友盟统计事件
	 * @param eventType 事件类型
	 * @param tiTargetId 统计对象的ID
	 * @param toTargetName 统计对象的名称
	 */
	private void addUmEvent(String eventType, String tiTargetId, String toTargetName){
		HashMap<String, String> ummap = new HashMap<String, String>();
		ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(loginUserVo.getUserid()));
		ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, loginUserVo.getUsername());
		ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, tiTargetId);
		if (toTargetName != null)
			ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, toTargetName);
		
		MobclickAgent.onEvent(context, eventType, ummap);
	}
	
	
	/**
	 * 绘制推荐贴吧布局
	 * @param table
	 * @param arrayList
	 */
	private void setSplendidGamesTableLayout(TableLayout table, ArrayList<Map<String, Object>> arrayList) {
		if (arrayList == null)
			return;
		int dataSize = arrayList.size();
		int numColumns = 3;
		int numRows = dataSize % numColumns == 0 ? dataSize / numColumns : (dataSize / numColumns + 1);
		int i = 0;
		table.removeAllViews();
		for (i = 0; i < numRows; i++) {
			View rowView = this.inflater.inflate(R.layout.splendid_game_row, null);
			View item1 = rowView.findViewById(R.id.splendid_game_row_item_1);
			View item2 = rowView.findViewById(R.id.splendid_game_row_item_2);
			View item3 = rowView.findViewById(R.id.splendid_game_row_item_3);
			for (int j = i * numColumns; j < (i + 1) * numColumns && j < dataSize; j++) {
				Map<String, Object> vo = arrayList.get(j);
				if(j % numColumns ==0)
				{
					setSplendidGameItemData (item1, vo) ;
				}
				else if (j % numColumns == 1){
					setSplendidGameItemData (item2, vo) ;
				}
				else if (j % numColumns == 2){
					setSplendidGameItemData (item3, vo) ;
				}
			}
			table.addView(rowView);
		}

	}

	/**
	 * 单个推荐贴吧绘制
	 * @param view
	 * @param gameVo
	 */
	private void setSplendidGameItemData(final View view ,final Map<String, Object> gameVo) {
		if(view == null)
			return ;
		view.setVisibility(View.VISIBLE);
		final ViewHolder  viewHolder= new ViewHolder();
		viewHolder.icon = (RoundedImageView) view.findViewById(R.id.icon);
		viewHolder.name = (TextView) view.findViewById(R.id.desc);

		if(gameVo.containsKey("pic")){
			ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl((String)gameVo.get("pic")), viewHolder.icon, R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
		}
		if(gameVo.containsKey("title")){
			viewHolder.name.setText((String)gameVo.get("title"));
		}

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				long gameId = 0;
				// 贴吧详情
				Map<String, Object> tmp = gameVo;
				if (tmp != null && tmp.containsKey("gid")) {
					gameId = Long.valueOf(tmp.get("gid").toString());
				}
				AppConfig appconfig = AdaptiveAppContext.getInstance().getAppConfig();
				if (appconfig != null && appconfig.isRecbarmsg() && gameId != appconfig.getGameId()) {
					AppUtil.openGame(context, gameId, GameTopicListActivity.class.getName(), context.getResources().getString(R.string.postbar_show_game_tip_for_youban_uninstall));
				} else {
					Intent intent = new Intent(context, GameTopicListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gameId);
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					context.startActivity(intent);
					
					String groupName = tmp.containsKey("title")?(String) tmp.get("title"):"";
					addUmEvent(UMConfig.MSGS_EVENT_SPLENDID_GAME_CLICK, gameId + "", groupName);
				}
			}
		});
	}

	/**
	 * 统计PUSH的点击数，如果已经统计过，则不再统计
	 * @param pushid
	 */
	private void sendPushIdString(final long pushid){
		if(!pushCountMap.containsKey(pushid) //未统计
			|| (pushCountMap.containsKey(pushid) && pushCountMap.get(pushid) == 1)){//或统计失败
			ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {
				@Override
				public void onSuccess(Integer result) {
					if (result == Msgs.ErrorCode.EC_OK_VALUE) {
						LogUtil.i(TAG, "PUSH统计成功" + result);
					}
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					pushCountMap.put(pushid, 1);
					LogUtil.e(TAG, "PUSH统计异常" + result);
				}
			};
			ProxyFactory.getInstance().getUserProxy().SendPushCount(callback, context, pushid, 1);
			pushCountMap.put(pushid, 0);
		}
	}

	class GameViewHolder {
		TableLayout splendid_recommend_table;
		TextView item_chat_date;
	}

	// 非贴吧ITEM布局
	class NotGameViewHolder {
		TextView splendid_recommond_type;
		ImageView splendid_recommond_imageview;
		TextView item_chat_date;
		SplendidListView splendid_recommend_list;
	}

	// 单个贴吧布局
	private class ViewHolder {
		RoundedImageView icon;
		TextView name;
	}
}
