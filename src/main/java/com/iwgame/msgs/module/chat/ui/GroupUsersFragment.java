/**      
 * GroupUserListFragment.java Create on 2013-10-25     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.chat.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.GroupUserRelDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.chat.adapter.GroupChatUserAdapter2;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.syncgroupmember.vo.SyncGroupMemberVo;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.utils.DateUtil;
import com.iwgame.utils.FileUtils;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * 加载公会成员列表的
 * fragment
 * @author jczhang
 *
 */
public class GroupUsersFragment extends BaseFragment implements OnItemClickListener,OnClickListener{

	protected static final String TAG = "GroupUsersFragment";
	LayoutInflater inflater = null;
	int currentRel = 0;
	ListView group_chat_user_listview = null;
	GroupChatUserAdapter2 groupChatUserAdapter = null;
	List<GroupUserRelVo> groupChatUserData = new ArrayList<GroupUserRelVo>();
	long groupId = 0;
	private static GroupUsersFragment instance;
	private PullToRefreshListView pullToRefreshListView;
	private int PULL_UP = 1;//向上拉
	private int PULL_DOWN = 0;//下拉 
	private int mLimit = 20;//每次加载10条数据
	private long offset = 0;//分页加载数据的偏移量
	private boolean hasNext = true;//判断分页获取的时候，是否还有数据
	private int flag = 2;//默认表示的是以发言时间排序 
	private LinearLayout topView;
	private ImageView ivDialog;
	private LinearLayout speakintTime;
	private LinearLayout lastLoginTime;
	private TextView contriRank;
	private TextView speakintTimeTxt;
	private ImageView speakingTimeIcon;
	private TextView lastLoginTxt;
	private ImageView lastLoginIcon;
	//定义个二维数组，前面的表示是否被点中（true表示选中，false表示没有选中），后面那个表示是升序还是降序（true表示最新发言时间往后），
	private boolean[][] temp = {{true,false,false},{true,true}}; 
	
	
	/**
	 * 在其界面启动的时候会
	 * 执行下面的这个方法
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PTAG = TAG;
		instance = this;
	}

	/**
	 * 获取到
	 * groupusersFragment的引用
	 * @return
	 */
	public static GroupUsersFragment getInstance() {
		return instance;
	}

	/**
	 * 同步公会成员信息
	 */
	private void saveData(List<GroupUserRelVo> result){
		if(result != null && result.size() > 0){
			//第二保存到文件
			try {
				SyncGroupMemberVo vo = (SyncGroupMemberVo) FileUtils.readFile(SystemContext.getInstance().getContext(), SystemConfig.SYNC_GROUP_MEMBER_FILENAME);
				//保存的是成员到map中
				if(vo.getGroupuserList().containsKey(""+groupId)){
					vo.getGroupuserList().get(""+groupId).clear();
					vo.getGroupuserList().put(""+groupId, result);
				}else{
					vo.getGroupuserList().put(""+groupId, result);
				}
				FileUtils.delFile(SystemContext.getInstance().getContext(), SystemConfig.SYNC_GROUP_MEMBER_FILENAME);
				FileUtils.writeFile(SystemContext.getInstance().getContext(), SystemConfig.SYNC_GROUP_MEMBER_FILENAME, vo, GroupUsersFragment.this.getActivity().MODE_PRIVATE);
			} catch (IOException e) {
				e.printStackTrace();
				SyncGroupMemberVo vo = new SyncGroupMemberVo();
				Map<String, List<GroupUserRelVo>> groupuserList = new HashMap<String, List<GroupUserRelVo>>();
				groupuserList.put(""+groupId, result);
				vo.setGroupuserList(groupuserList);
				try {
					FileUtils.writeFile(SystemContext.getInstance().getContext(), SystemConfig.SYNC_GROUP_MEMBER_FILENAME, vo, GroupUsersFragment.this.getActivity().MODE_PRIVATE);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}


	/**
	 * 在fragment里面获取到要显示的的view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		// 获得传入的参数
		Bundle tmpbundle = this.getArguments();
		if (tmpbundle != null) {
			groupId = tmpbundle.getLong(SystemConfig.BUNDLE_NAME_TOID);
		}
		View v = inflater.inflate(R.layout.group_chat_user_list, container, false);
		init(v);
		return v;
	}

	/*
	 *对界面的初始化操作
	 */
	private void init(View v) {
		GroupDao dao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
		GroupVo gvo = dao.findGroupByGrid(groupId);
		if(gvo != null) 
			currentRel = gvo.getRelWithGroup();
		// 成员列表
		pullToRefreshListView = (PullToRefreshListView) v.findViewById(R.id.lv_group_chat_users);
		setPullRefreshListView(pullToRefreshListView, Mode.BOTH);
		topView = (LinearLayout)v.findViewById(R.id.top_view);
		ivDialog = (ImageView)v.findViewById(R.id.iv_dialog);
		AnimationDrawable animationDrawable = (AnimationDrawable) ivDialog.getBackground();
		animationDrawable.start();
		speakintTime = (LinearLayout)v.findViewById(R.id.time_say_words);
		lastLoginTime = (LinearLayout)v.findViewById(R.id.last_login_time);
		contriRank = (TextView)v.findViewById(R.id.contri_rank);
		speakintTimeTxt = (TextView)v.findViewById(R.id.speaking_time);
		lastLoginTxt = (TextView)v.findViewById(R.id.last_login);
		speakingTimeIcon = (ImageView)v.findViewById(R.id.speakint_time_icon);
		lastLoginIcon = (ImageView)v.findViewById(R.id.last_login_icon);
		contriRank.setOnClickListener(this);
		speakintTime.setOnClickListener(this);
		lastLoginTime.setOnClickListener(this);
		groupChatUserAdapter = new GroupChatUserAdapter2(this.getActivity(), groupChatUserData, currentRel,groupId,group_chat_user_listview);
		group_chat_user_listview.setDivider(null);
		group_chat_user_listview.setAdapter(groupChatUserAdapter);
		group_chat_user_listview.setOnItemClickListener(this);
	}


	/**
	 * 该方法是
	 * 初始化上拉刷新
	 * 下拉刷新的控件
	 */
	public void setPullRefreshListView(PullToRefreshListView pullRefreshListView, Mode mode) {
		this.pullToRefreshListView = pullRefreshListView;
		group_chat_user_listview = this.pullToRefreshListView.getRefreshableView();
		if(mode == null)
			pullRefreshListView.setMode(Mode.BOTH);
		else
			pullRefreshListView.setMode(mode);

		final ILoadingLayout headerLabels = pullRefreshListView.getLoadingLayoutProxy(true, false);
		final ILoadingLayout footerLabels = pullRefreshListView.getLoadingLayoutProxy(false, true);
		headerLabels.setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
		footerLabels.setPullLabel("加载更多");// 刚下拉时，显示的提示
		footerLabels.setRefreshingLabel("加载中...");// 刷新时
		footerLabels.setReleaseLabel("松开后加载");// 下来达到一定距离时，显示的提示
		pullRefreshListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				getListData(0, mLimit, PULL_DOWN,true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				if (hasNext)
					getListData(offset,mLimit,PULL_UP,false);
				else
					new GetDataTask().execute();
			}
		});
	}

	/**
	 * 加载更多的数据
	 * @param moffset
	 * @param limit
	 * @param type
	 * @param temp
	 */
	private void getListData(final long moffset, final int limit,final int type,final boolean temp){
		ProxyFactory.getInstance().getGroupProxy().syncGroupMemberList(new ProxyCallBack<List<GroupUserRelVo>>() {

			@Override
			public void onSuccess(List<GroupUserRelVo> result) {
				groupChatUserAdapter.setFlag(true);
				pullToRefreshListView.setMode(Mode.BOTH);
				topView.setVisibility(View.GONE);
				//第一，获取到数据，后缓存
				if(flag == 2 && offset == 0)
					saveData(result);
				//第三，刷新界面
				pullToRefreshListView.onRefreshComplete();
				if(type == PULL_UP){
					if(result == null){
						hasNext = false;
					}else{
						if(offset == 0 || temp){
							groupChatUserData.clear();
							groupChatUserData.addAll(result);
						}else{
							groupChatUserData.addAll(result);
						}
						offset = moffset + Math.abs(limit);
						groupChatUserAdapter.notifyDataSetChanged();
						groupChatUserAdapter.notifyDataSetInvalidated();
						int firstvisPos = group_chat_user_listview.getFirstVisiblePosition();
						int lastVisPos = group_chat_user_listview.getLastVisiblePosition();
						group_chat_user_listview.setSelection(groupChatUserData.size()-result.size()-(lastVisPos - firstvisPos)+1);
						if(result != null && result.size() >= mLimit){
							hasNext = true;
						}else{
							hasNext = false;
						}
					}
				}else if(type == PULL_DOWN){
					if(result != null && result.size() > 0){
						offset = result.size();
						groupChatUserData.clear();
						groupChatUserData.addAll(result);
						hasNext = true;
						groupChatUserAdapter.notifyDataSetChanged();
						groupChatUserAdapter.notifyDataSetInvalidated();
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				topView.setVisibility(View.GONE);
				ErrorCodeUtil.handleErrorCode(GroupUsersFragment.this.getActivity(), result, resultMsg);
			}
		}, this.getActivity(), groupId, moffset, limit, flag);
	}






	/**
	 * 当数据刷新完的时候
	 * 要隐藏转的dialog
	 * @author jczhang
	 *
	 */
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected String[] doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return null;
		}
		@Override
		protected void onPostExecute(String[] result) {
			pullToRefreshListView.onRefreshComplete();
			super.onPostExecute(null);
		}
	}




	/**
	 * 按照发言时间排序
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(!isVisibleToUser){
			if(topView != null)topView.setVisibility(View.GONE);
			return;
		}
		if (groupChatUserData == null || groupChatUserAdapter == null)return;
		//如果没有数据，则去重新请求，如果有数据，则不用在去请求了
		if(groupChatUserData == null || groupChatUserData.size() <= 0){
			temp[0][0] = true;
			temp[0][1] = false;
			temp[0][2] = false;
			temp[1][0] = true;
			temp[1][1] = true;
			setChecked();
			flag = 2;
			offset = 0;
			topView.setVisibility(View.VISIBLE);
			if(pullToRefreshListView != null) pullToRefreshListView.setMode(Mode.DISABLED);
			loadCacheData();
			if(temp[1][0]) mLimit = 20;
			else mLimit = -20;
			GroupUserRelDao dao = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getContext());
			List<GroupUserRelVo> list = dao.findUsersByGrid(groupId);
			if(list != null && list.size() > 0){
				getListData(offset, mLimit, PULL_UP,true);
			}else{
				// 同步公会成员
				ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_GROUP_USER, groupId, new SyncCallBack() {
					
					@Override
					public void onSuccess(Object result) {
						getListData(offset, mLimit, PULL_UP,true);
					}
					
					@Override
					public void onFailure(Integer result) {
						getListData(offset, mLimit, PULL_UP,true);
					}
				});
			}
		}else if(groupChatUserData != null && groupChatUserData.size() > 0){
			setChecked();
			// 取消息
			List<MessageVo> list_message = ProxyFactory.getInstance().getMessageProxy()
					.getFromLastMessage(MsgsConstants.MC_MCHAT, groupId, MsgsConstants.DOMAIN_GROUP, MsgsConstants.MCC_CHAT);
			int size = groupChatUserData.size();
			for(int i = 0; i < size; i++){
				GroupUserRelVo vo = groupChatUserData.get(i);
				for (int j = 0; j < list_message.size(); j++) {
					MessageVo messageVo = list_message.get(j);
					if (messageVo.getFromDomain().equals(MsgsConstants.DOMAIN_USER)) {
						if (vo.getUid() == messageVo.getFromId()) {
							vo.setMessageVo(messageVo);
						}
					}
				}
			}
			groupChatUserAdapter.notifyDataSetChanged();
			groupChatUserAdapter.notifyDataSetInvalidated();
		}
	}

	/**
	 * 判断哪些按钮
	 * 被点击中，哪个按钮不被点中
	 */
	private void setChecked(){
		if(temp[1][0]){
			speakingTimeIcon.setBackgroundResource(R.drawable.group_user_list_bg_selector1);
		}else{
			speakingTimeIcon.setBackgroundResource(R.drawable.group_user_list_bg_selector);
		}
		if(temp[1][1]){
			lastLoginIcon.setBackgroundResource(R.drawable.group_user_list_bg_selector1);
		}else{
			lastLoginIcon.setBackgroundResource(R.drawable.group_user_list_bg_selector);
		}
		speakintTimeTxt.setSelected(temp[0][0]);
		speakingTimeIcon.setSelected(temp[0][0]);
		lastLoginTxt.setSelected(temp[0][1]);
		lastLoginIcon.setSelected(temp[0][1]);
		contriRank.setSelected(temp[0][2]);
	}
	
	
	/**
	 * 刚进入这个界面的
	 * 时候，添加缓存的数据
	 */
	private void loadCacheData(){
		try {
			//从文件里面读取到对象后显示
			SyncGroupMemberVo vo = (SyncGroupMemberVo)FileUtils.readFile(SystemContext.getInstance().getContext(), SystemConfig.SYNC_GROUP_MEMBER_FILENAME);
			if(vo != null){
				if(vo.getGroupuserList().containsKey(""+groupId)){
					List<GroupUserRelVo> groupuserrelvoList = vo.getGroupuserList().get(""+groupId);
					groupChatUserData.clear();
					groupChatUserData.addAll(groupuserrelvoList);
					groupChatUserAdapter.notifyDataSetChanged();
					groupChatUserAdapter.notifyDataSetInvalidated();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 点击listview
	 * 上的item时候
	 * 执行下面的方法 
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		try {
			if (parent == group_chat_user_listview) {
				// 头像单击，如果自己，不要加单击事件
				if (groupChatUserData.get(position-1).getUid() != SystemContext.getInstance().getExtUserVo().getUserid()) {
					// 打开对方资料
					Intent intent = new Intent(this.getActivity(), UserDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, groupChatUserData.get(position-1).getUid());
						if (currentRel == GroupUserRelVo.REL_ADMIN || currentRel == GroupUserRelVo.REL_NORMALADMIN) {
							bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_STOP_SAY_WORDS, true);
					}
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, groupId);
					bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GROUP_CARD, groupChatUserData.get(position-1).getRemark());
					bundle.putBoolean(SystemConfig.IF_NOT_SHOW_GROUP_CARD, true);
					intent.putExtras(bundle);
					this.getActivity().startActivity(intent);
				} else {
					ToastUtil.showToast(GroupUsersFragment.this.getActivity(),
							GroupUsersFragment.this.getActivity().getString(R.string.check_information));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public long getGroupId() {
		return groupId;
	}


	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	/**
	 * 当点击底部的按钮的时候
	 * 弹出popwindow
	 */
	@Override
	public void onClick(View v) {
		if(v.getId() == speakintTime.getId()){
			//点击的是发言时间 
			flag = 2;
			if(temp[0][0]){
				boolean tmp = temp[1][0];
				if(tmp){
					temp[1][0] = false;
					mLimit = -20;
				}else{
					temp[1][0] = true;
					mLimit = 20;
				}
			}else{
				if(temp[1][0]){
					mLimit = 20;
				}else{
					mLimit = -20;
				}
			}
			temp[0][0] = true;
			temp[0][1] = false;
			temp[0][2] = false;
			setChecked();
			offset = 0;
		    getListData(offset, mLimit, PULL_UP, true);
		}else if(v.getId() == lastLoginTime.getId()){
			//点击 的是活跃时间 
			flag = 3;
			if(temp[0][1]){
				boolean tmp = temp[1][1];
				if(tmp){
					temp[1][1] = false;
					mLimit = -20;
				}else{
					temp[1][1] = true;
					mLimit = 20;
				}
			}else{
				if(temp[1][1]){
					mLimit = 20;
				}else{
					mLimit = -20;
				}
			}
			temp[0][0] = false;
			temp[0][1] = true;
			temp[0][2] = false;
			setChecked();
			offset = 0;
		    getListData(offset, mLimit, PULL_UP, true);
		}else if(v.getId() == contriRank.getId()){
			//点击的是贡献排名
			if(temp[0][2]) return;
			flag = 1;
			temp[0][0] = false;
			temp[0][1] = false;
			temp[0][2] = true;
			setChecked();
			mLimit = 20;
			offset = 0;
			getListData(offset, mLimit, PULL_UP, true);
			
		}
	}
}
