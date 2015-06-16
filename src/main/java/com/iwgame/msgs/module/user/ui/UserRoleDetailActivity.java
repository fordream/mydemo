/**      
* UserRoleDetailActivity.java Create on 2015-6-3     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.user.ui;

import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.pay.adapter.PayListAdapter;
import com.iwgame.msgs.module.pay.ui.PayDetailInfoActivity;
import com.iwgame.msgs.module.pay.ui.PayDetailPageInfoActivity;
import com.iwgame.msgs.module.user.adapter.UserRoleAdapter2;
import com.iwgame.msgs.proto.Msgs.UserRoleData;
import com.iwgame.msgs.proto.Msgs.UserRoleDetail;
import com.iwgame.msgs.proto.Msgs.UserYoubiDetail;
import com.iwgame.msgs.vo.local.UserRoleVo;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

/** 
 * @ClassName: UserRoleDetailActivity 
 * @Description: 用户角色列表
 * @author xingjianlong
 * @date 2015-6-3 上午9:29:29 
 * @Version 1.0
 * 
 */
public class UserRoleDetailActivity extends BaseActivity implements OnClickListener{
		
	 private boolean PLAY;
	private boolean EDIT;
	private boolean ENROLL;
	private TextView rightText;
	private View view;
	private TextView noRoleText;
	private RelativeLayout addRole;
	private ListView roleListView;
	private boolean Other;
	private LinearLayout roleContent;
	private CommonListView listContent;
	private UserRoleAdapter2 adapter;
	private boolean flag;
	private long gid;
	private long sid;
	private long roleid;
	private String TAG="UserRoleDetailActivity";
	private String sname;
	private long uid;
	public static long userid;
	private String gname;
	private CustomProgressDialog downloaddialog;
	public static UserRoleData vo;
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();
		init();
	}
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (listContent != null && listContent.listData.size() <= 0) {
			listContent.onHeaderRefresh();
		} else if (listContent != null) {
			listContent.onHeaderRefresh();
			listContent.setRefreshMode(Mode.BOTH);
			listContent.adapter.notifyDataSetChanged();
			listContent.adapter.notifyDataSetInvalidated();
		}
		if(flag){
		UserRoleAdapter2.flag = false;
		flag =false;
		rightText.setText("编辑");
		listContent.adapter.notifyDataSetChanged();
		}
		
	}
	public void getData(){
		Intent intent = getIntent();
		Other =intent.getBooleanExtra("other", false);
		PLAY = intent.getBooleanExtra("play", false);
		EDIT=intent.getBooleanExtra("edit", false);
		ENROLL =intent.getBooleanExtra("enroll", false);
		sname = intent.getStringExtra("sname");
		uid = intent.getLongExtra("uid", 0);
		gid = intent.getLongExtra("gid", 0);
		sid= intent.getLongExtra("sid", 0);
		gname=intent.getStringExtra("gname");
	}
	/**
	 * 初始化界面
	 * @param roleContent 
	 */
	private void init() {
		if(PLAY||EDIT||ENROLL){
			setTitleTxt("选择游戏角色");
		}else{
		setTitleTxt(getString(R.string.my_game_role));
		}
		rightText =(TextView)findViewById(R.id.rightText);
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view = View.inflate(this, R.layout.user_role_manage, null);
		contentView.addView(view,params);
		noRoleText = (TextView)view.findViewById(R.id.no_game_role);
		roleContent=(LinearLayout)view.findViewById(R.id.roleContent);
		addRole = (RelativeLayout)view.findViewById(R.id.add_game_role_parent);
		roleListView= (ListView)view.findViewById(R.id.role_info_listview);
		if(Other){
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 0, 0, 0);
			roleListView.setLayoutParams(lp);
		}
		addRole.setOnClickListener(this);
		rightText.setOnClickListener(this);
		
		 if(Other){
			 addRole.setVisibility(View.GONE);
		 }else{
			 addRole.setVisibility(View.VISIBLE);
		 }
		 addView();
		 if(PLAY||EDIT||ENROLL){
			 
			 setListener();
		 }
		 view.setVisibility(View.GONE);
		 downloaddialog = CustomProgressDialog.createDialog(this, false);
			view.setVisibility(View.GONE);
			downloaddialog.show();
	}
	
	private void setListener(){
		listContent.list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				vo = (UserRoleData) listContent.listData.get(position-1);
					if(PLAY){
					finish();
					}else if(EDIT){
						if(checkGame(vo.getGid())){
						finish();
						}
					}else if(ENROLL){
						finish();
					}
			}	
		});
	}
		public boolean checkGame(Long id){
			if(gid!=id){
				differentGame();
				return false;
			}
			return true;
		}
		public void differentGame(){
			final Dialog dialog = new Dialog(UserRoleDetailActivity.this, R.style.SampleTheme_Light);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.user_role_dialog);
			dialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog消失
			TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
			content.setText("你已经创建了"+gname+"游戏陪玩,无法修改其他游戏,若需要添加其他游戏,请重新创建陪玩信息!");
			LinearLayout btnBttom = (LinearLayout)dialog.findViewById(R.id.btn_bottom);
			LinearLayout textBttom =(LinearLayout)dialog.findViewById(R.id.text_bottom);
			btnBttom.setVisibility(View.GONE);
			textBttom.setVisibility(View.VISIBLE);
			dialog.show();
			textBttom.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
		}
	private void addView(){
		roleContent.removeAllViews();
		listContent =null;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		listContent = new CommonListView(this, View.VISIBLE, true){
			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				if(sid==0){
					getUserRoleData(0, 0, 0);
				}else{
				getUserRoleData(gid,sid,roleid);
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.iwgame.msgs.widget.listview.CommonListView#showNullBgView()
			 */
			@Override
			public void showNullBgView() {
				listContent.setVisibility(View.GONE);
				nullContent.setVisibility(View.VISIBLE);
			}
		};
		roleContent.addView(listContent, params);
		if(ENROLL&&sid!=0){
			setNUllContent("你还没有该游戏服务器的角色哦,赶快添加吧！");
		}else{
		setNUllContent(getResources().getString(R.string.no_role_tip));
		}
		adapter = new UserRoleAdapter2(UserRoleDetailActivity.this,listContent.listData, flag);
		listContent.setAdapter(adapter);
		
	}
	/**
	 * 
	 * @param gid
	 * @param sid
	 * @param rid
	 */
	private void getUserRoleData(long gid,long sid,long rid){
		ProxyFactory.getInstance().getUserProxy().getFilterUserRoleData(new ProxyCallBack<UserRoleDetail>() {
			
			@Override
			public void onSuccess(UserRoleDetail result) {
				if(downloaddialog.isShowing()){
					downloaddialog.dismiss();
					view.setVisibility(View.VISIBLE);
				}
				if (listContent.isRefresh) {
					listContent.clean();
					listContent.onHeaderRefreshComplete();
				} else {
					listContent.onFooterRefreshComplete();
				}
				if (result != null && result.getRoleList() != null && result.getRoleList().size() > 0) {
					if (result.getRoleList().size() < listContent.limit)
						listContent.hasNext = false;
					if(PLAY||EDIT||ENROLL||Other){
						rightText.setVisibility(View.GONE);
					}else{
						rightText.setVisibility(View.VISIBLE);
					}
						userid =result.getUid();
					listContent.listData.addAll(result.getRoleList());
					listContent.showListView();
				} else {
					rightText.setVisibility(View.GONE);
					LogUtil.d(TAG, "数据为空");
					if (listContent.listData.size() <= 0)
						listContent.showNullBgView();
				}
				if (listContent.listData.size() <= 0) {
					listContent.setRefreshMode(Mode.PULL_DOWN_TO_REFRESH);
					listContent.listData.clear();
				} else {
					listContent.setRefreshMode(Mode.BOTH);
				}
				listContent.adapter.notifyDataSetChanged();
				listContent.offset = listContent.listData.size();
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				downloaddialog.dismiss();
				view.setVisibility(View.VISIBLE);
			}
		}, UserRoleDetailActivity.this, gid, sid, rid, listContent.offset, listContent.limit);
	}
	@Override
	public void onClick(View v) {
		if(v==rightText){
			if(flag){
				flag = false;
				rightText.setText("编辑");
				}else{
				flag = true;
				rightText.setText("完成");
				}
			adapter = new UserRoleAdapter2(UserRoleDetailActivity.this,listContent.listData, flag);
			listContent.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			adapter.notifyDataSetInvalidated();
		}else if(v==addRole){
			Intent intent;
			if(sid!=0){
				intent = new Intent(UserRoleDetailActivity.this, UserAddRoleInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong("gid", gid);
				bundle.putLong("sid", sid);
				intent.putExtras(bundle);
			}else{
				 intent = new Intent(UserRoleDetailActivity.this,UserChooseGameActivity.class);
			}
			startActivity(intent);
		}
	}
	private void setNUllContent(String text){
		listContent.nullContent.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LinearLayout v = (LinearLayout) View.inflate(UserRoleDetailActivity.this, R.layout.pay_no_any_info, null);
		TextView tv = (TextView)v.findViewById(R.id.no_pay_info_tip);
		tv.setVisibility(View.GONE);
		TextView tip = (TextView)v.findViewById(R.id.no_game_role);
		tip.setVisibility(View.VISIBLE);
		tip.setText(text);
		listContent.nullContent.addView(v, params);
	}
}
