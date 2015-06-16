package com.iwgame.msgs.module.user.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.user.adapter.UserAdapter2;
import com.iwgame.msgs.vo.local.SupportUserVo;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * 赞用户列表
 * @author jczhang
 *
 */
public class SupportUserListActivity extends BaseSuperActivity {

	private Button backBtn;
	private TextView title;
	private LinearLayout content;
	private int supportNum;//赞的数量
	private long gameid;
	private CommonListView supportListView;
	private long OFFSET = 0;
	private int LIMITE = 20;
	/**
	 * 当界面启动的时候 
	 * 首先执行下面的这个生命周期方法 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.support_user_list_activity);
		init();//对界面做一些初始化操作
		setListener();//给界面上的按钮添加监听器
		getData();//获取数据显示
	}

	
	/**
	 * 向服务端请求数据显示
	 */
	private void getData() {
		supportListView.offset = OFFSET;//从零开始支获取
		supportListView.limit = LIMITE;//每次获取20条数据
		supportListView.setRefreshMode(Mode.DISABLED);
		supportListView.getListData(supportListView.offset, supportListView.limit);
	}


	/**
	 * 给界面上的按钮设置监听器
	 */
	private void setListener() {
		//当点击返回按钮的时候执行下面的这个方法
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SupportUserListActivity.this.finish();
			}
		});
		
		//点击列表的条目 跳转到用户的资料页面
		supportListView.list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
				Object uid = map.get("uid");
				if (uid != null && !uid.equals(SystemContext.getInstance().getExtUserVo().getUserid())) {
					Intent intent = new Intent(parent.getContext(), UserDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, Long.valueOf(uid.toString()));
					intent.putExtras(bundle);
					startActivity(intent);
				}else{
					ToastUtil.showToast(SupportUserListActivity.this, getString(R.string.check_information));
				}
			
			}
		});
	}

	/**
	 * 对界面做一些初始化操作
	 */
	private void init() {
		backBtn = (Button)findViewById(R.id.leftBtn);
		title = (TextView)findViewById(R.id.titleTxt);
		content = (LinearLayout)findViewById(R.id.contentView);
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			gameid = bundle.getLong(SystemConfig.SUPPORT_USER_LIST_ACTIVITY_GAMEID, 0);
			supportNum = bundle.getInt(SystemConfig.TOTAL_SUPPORT_NUMBER, 0);
		}
		title.setText(getString(R.string.suppport_user_list_title, supportNum));
		getView();
	}


	/**
	 * 初始化commonlistview
	 * 并且添加到contentview里面
	 */
	private void getView() {
	supportListView = new CommonListView(this,View.GONE){
		@Override
		public void getListData(long offset, int limit) {
			super.getListData(offset, limit);
			if(supportListView.listData.size() <= 0)supportListView.setLoadingUI();
			((UserAdapter2)(supportListView.adapter)).setFlag(true);
			//通过网络去请求数据
			ProxyFactory.getInstance().getUserProxy().getSupportUserData(new ProxyCallBack<List<SupportUserVo>>() {
				
				@Override
				public void onSuccess(List<SupportUserVo> result) {
					showListView();//显示列表数据
					supportListView.setRefreshMode(Mode.PULL_UP_TO_REFRESH);
					if(result != null && result.size() > 0){
						supportListView.offset = supportListView.offset + result.size();
						if(result.size() < LIMITE)
							supportListView.hasNext = false;
					}
					supportListView.listData.addAll(praseUserList(result));
					supportListView.adapter.notifyDataSetChanged();
					supportListView.adapter.notifyDataSetInvalidated();
				}
				
				@Override
				public void onFailure(Integer result, String resultMsg) {
					showListView();
				}
			}, SupportUserListActivity.this, offset, limit, gameid);
		}
	};
	LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	content.addView(supportListView, params);
	supportListView.adapter = new UserAdapter2(this, supportListView.listData,R.layout.user_list_item_userfragment , new String[] { "nickname", "distance" },
			new int[] { R.id.nickname, R.id.rdesc },UserAdapter2.TYPE_SUPPORT_USER, supportListView.list);
	supportListView.list.setAdapter(supportListView.adapter);
	}
	
	/**
	 * 解析用户数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseUserList(List<SupportUserVo> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			SupportUserVo vo = list.get(i);
			map.put("avatar", vo.getAvatar());
			map.put("nickname", vo.getUserName());
			map.put("gamecount", vo.getCommonCount());
			map.put("sex", vo.getSex());
			map.put("age", vo.getAge());
			map.put("uid", vo.getUid());
			map.put("news", vo.getMood());
			map.put("grade", vo.getGrade());
			map.put("new", vo.getNews());
			map.put("suptime", SafeUtils.getDate2MyStr2(vo.getTime()));
			tmplist.add(map);
		}
		return tmplist;
	}

	
	
}
	
