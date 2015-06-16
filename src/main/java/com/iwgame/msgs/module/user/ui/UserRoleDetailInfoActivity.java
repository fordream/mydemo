package com.iwgame.msgs.module.user.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.user.adapter.UserRoleAdapter;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.vo.local.UserRoleVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

public class UserRoleDetailInfoActivity extends BaseActivity{
	
	
	private String TAG ="userRoleDetailInfoActivity";
	private TextView rightText;
	private View view;
	private RelativeLayout addRole;
	private TextView noRoleText;
	private ListView roleListView;
	private List<UserRoleVo> list = new ArrayList<UserRoleVo>();
	private ExtUserVo userVo;
	private UserRoleAdapter adapter;
	public boolean Other = false;
	public Long uid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getData();
		initial();
	}
	
	public void getData(){
		Intent intent = getIntent();
		Other =intent.getBooleanExtra("other", false);
		uid = intent.getLongExtra("uid", 0);
	}
	
	public void onActivity(){
		onResume();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}
	
	private void initial() {
		setTitleTxt(getString(R.string.my_game_role));
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view = View.inflate(this, R.layout.user_role_manage, null);
		contentView.addView(view,params);
		noRoleText = (TextView)view.findViewById(R.id.no_game_role);
		addRole = (RelativeLayout)view.findViewById(R.id.add_game_role_parent);
		roleListView= (ListView)view.findViewById(R.id.role_info_listview);
		if(Other){
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 0, 0, 0);
			roleListView.setLayoutParams(lp);
		}
		adapter = new UserRoleAdapter(UserRoleDetailInfoActivity.this,list);
		 roleListView.setAdapter( adapter);
			 addRole.setVisibility(View.GONE);
		 view.setVisibility(View.GONE);
		 getUserRoleData();
	}

	
	
	/**
	 * 获取用户角色信息
	 * @param context
	 */
	
	public void  getUserRoleData(){
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
	
			UserDao userdao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
			UserVo uvo = userdao.getUserById(uid);
			ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
			cdp.setId(uid);
			cdp.setUtime(uvo == null ? 0 : uvo.getUpdatetime());
			p.addParam(cdp.build());
			ProxyFactory.getInstance().getUserProxy().getUserRoleInfo(new ProxyCallBack<List<UserRoleVo>>() {
				
				@Override
				public void onSuccess(List<UserRoleVo> result) {
					if((result == null || result.size()<=0)){
						noRoleText.setVisibility(View.VISIBLE);
						view.setVisibility(View.VISIBLE);
					}else{
						noRoleText.setVisibility(View.GONE);
						view.setVisibility(View.VISIBLE);
						list.clear();
						list.addAll(result);
						adapter = new UserRoleAdapter(UserRoleDetailInfoActivity.this,list);
						 roleListView.setAdapter( adapter);
						adapter.notifyDataSetChanged();
						adapter.notifyDataSetInvalidated();
					}
				}
				@Override
				public void onFailure(Integer result, String resultMsg) {
					handleErrorCode(result, resultMsg);
					view.setVisibility(View.VISIBLE);
				}
			}, UserRoleDetailInfoActivity.this, p.build(), 23, null,(long)0,(long)0);
	}
	
	public void getNetWork(){
		if(NetworkUtil.isNetworkAvailable(getApplicationContext())){
		}else{
			ToastUtil.showToast(UserRoleDetailInfoActivity.this, "网络不可用，请检查网络!");
		}
	}
	/**
	 * 
	 * 处理错误码
	 * @param i
	 * @param msg
	 */
	private void handleErrorCode(Integer i,String msg){
		ErrorCodeUtil.handleErrorCode(UserRoleDetailInfoActivity.this, i, msg);
	}
}