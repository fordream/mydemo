package com.iwgame.msgs.module.user.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.game.ui.GameFragment2;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.module.postbar.ui.TopicDetailActivity;
import com.iwgame.msgs.module.setting.ui.EditDetailActivity;
import com.iwgame.msgs.module.user.object.UserRoleAttr;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.GameKeysDetail;
import com.iwgame.msgs.proto.Msgs.UserRoleData;
import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GameKeyVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

public class UserAddRoleInfoActivity extends BaseActivity implements OnClickListener{
		
	private View view;
	private LinearLayout addRoleContent;
	private Button commitBtn;
	private long gid;
	private List<GameKeyVo> list = new ArrayList<GameKeyVo>();
	private View serviceView;
	private View inputView;
	private View gradeView;
	private EditText roleName;
	public  List<View> viewList= new ArrayList<View>();
	public Map<Long,View> map = new HashMap<Long, View>();
	public static int ROLE_NAME_EXIST =500014;
	
	public static int  GAME_ROLE_NOT_EXIST =500017;
	private static int EC_MSGS_USER_GAME_NOT_ADD_ROLE = 500019;  //游戏不允许添加角色
	private static int EC_MSGS_USER_GAME_NOT_FOLLOW = 500020; //未关注游戏
	public Map<Long,UserRoleAttr> attrMap = new HashMap<Long, UserRoleAttr>();
	private long ssid;
	private boolean HasService =false;
	public Dialog dialog;
	public boolean DataTag = false;
	
	private List<UserRoleAttr> attrList = new ArrayList<UserRoleAttr>();
	private long sid;
	private CustomProgressDialog downloaddialog;
	private TextView noRoleInfo;
	private ScrollView dataContent;
	private TextView rightText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initial();
		setView();
		getRoleInfoData(this);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getNetWork();
	}
	/**
	 * 设置加载dialog
	 */
	private void setView() {
		downloaddialog = CustomProgressDialog.createDialog(this, false);
		
	}
	private void initial() {
		// TODO Auto-generated method stub
		setTitleTxt("添加角色");
		rightText =(TextView)findViewById(R.id.rightText);
		rightText.setText("完成");
		rightText.setVisibility(View.VISIBLE);
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view = View.inflate(this, R.layout.user_add_role_info, null);
		contentView.addView(view,params);
		noRoleInfo = (TextView)view.findViewById(R.id.no_role_info);
		addRoleContent =(LinearLayout)view.findViewById(R.id.add_role_content);
		dataContent =(ScrollView)view.findViewById(R.id.role_data_info);
		commitBtn =(Button)view.findViewById(R.id.compelte_role);
		commitBtn.setVisibility(View.GONE);
		commitBtn.setOnClickListener(this);
		rightText.setOnClickListener(this);
		getData();
		view.setVisibility(View.GONE);
		
	}
	
	public void getData(){
		Bundle bundle = getIntent().getExtras();
		gid = bundle.getLong("gid",0);
		sid = bundle.getLong("sid", 0);
	}
	/**
	 * 获取角色属性值
	 * @param context
	 */
	
	public void getRoleInfoData(Context context){
		downloaddialog.show();
		ProxyFactory.getInstance().getUserProxy().getGameKeyData(new ProxyCallBack<List<GameKeyVo>>() {
			
			@Override
			public void onSuccess(List<GameKeyVo> result) {
				// TODO Auto-generated method stub
				if(downloaddialog.isShowing())
					downloaddialog.dismiss();
				if(result!=null&&result.size()>0){
					//commitBtn.setVisibility(View.VISIBLE);
					noRoleInfo.setVisibility(View.GONE);
					dataContent.setVisibility(View.VISIBLE);
					for(int i =0;i<result.size();i++){
						list.add(result.get(i));
					}
				}else{
					dataContent.setVisibility(View.GONE);
					commitBtn.setVisibility(View.GONE);
					noRoleInfo.setVisibility(View.VISIBLE);
				}
				setNatureView();
				view.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				handleErrorCode(result, resultMsg);
				if(downloaddialog.isShowing())
					downloaddialog.dismiss();
			}
		}, context, gid);
		
		
	}
	/**
	 * 根据获取数据类型，定义相应的布局
	 */
	public void setNatureView(){
		if(list==null)  return;
		 long id ;
		for(int i = 0;i<list.size();i++){
			final GameKeyVo vo = list.get(i);
			if(vo.getType()==1){
				
				View view = View.inflate(UserAddRoleInfoActivity.this, R.layout.user_add_role_input_item, null);
				EditText edit =(EditText)view.findViewById(R.id.user_role_name);
				edit.setHint("请输入"+vo.getName());
				setEditText(vo,edit);
				viewList.add(view);
				map.put(vo.getId(), view);
			}else if(vo.getType()==0){
				View view = View.inflate(UserAddRoleInfoActivity.this, R.layout.user_add_role_list_item, null);
				((TextView) view.findViewById(R.id.user_item_text)).setText("请选择"+vo.getName());
				
				if(sid!=0&&vo.getAttrType()==MsgsConstants.GAME_ROLE_KEY_SERVER){
				
				List<GameKeysDetail> detaillist = vo.getList();
				for(GameKeysDetail detail : detaillist){
					if(detail.getId()==sid){
						((TextView) view.findViewById(R.id.user_item_text)).setText(detail.getContent());
						view.findViewById(R.id.user_item_image).setVisibility(View.GONE);
						}
					}
				addUserRoleAttr(vo.getId(),vo.getName(),sid+"");
				}else{
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						
						skipListView(vo);
						
						
					}
				});
			
				}
				map.put(vo.getId(), view);
				viewList.add(view);
			}
		}
		addView();
	}
	/**
	 * 添加角色属性布局
	 */
	private void addView() {
		// TODO Auto-generated method stub
	
		if(viewList!=null&&viewList.size()>0){
		for (View v : viewList) {  
				 addRoleContent.addView(v); 
			}
		}
	}
	
	private String name;
	public boolean flag;
	
	public void setEditText(final GameKeyVo vo,final EditText edit){
		edit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
				if(!edit.getText().toString().equals("")&&CheckUserRoleName(edit,vo)){
					if(vo.getAttrType()==MsgsConstants.GAME_ROLE_KEY_USER){
						name = edit.getText().toString();
					}
					addUserRoleAttr(vo.getId(),vo.getName(), edit.getText().toString());
				}
			}
		});
	}
	
	/**
	 * 跳转取数据
	 * @param vo
	 */
	public void skipListView(GameKeyVo vo){
		Intent  intent = new Intent(UserAddRoleInfoActivity.this,UserRoleNatureActiviy.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("gamekey", vo);
		intent.putExtras(bundle);
		startActivityForResult(intent, 100);
	}
	
	/**
	 * 获取返回的数据
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode==Activity.RESULT_OK){
			if(requestCode==100){
			Long id = data.getLongExtra("id", 0);
			String values =data.getExtras().getString("values");
			String name =data.getExtras().getString("name");
			Long key =data.getLongExtra("key", 0);
			if(id==0) return;
			if(values==null)return;
			if(name==null) return;
			if(map.get(id)!=null){
				TextView view = (TextView) map.get(id).findViewById(R.id.user_item_text);
				view.setText(values);
			}
			//addUserRoleAttr(id, values);
			addUserRoleAttr(id,name,key+"");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
		
	}
	
	/**
	 * 处理返回数据
	 * @param id
	 * @param name2
	 * @param values
	 */
	public  void addUserRoleAttr(Long id, String name2, String values) {
		UserRoleAttr attr = new UserRoleAttr();
		attr.setId(id);
		attr.setContent(values);
		attr.setKey(name2);
		attrMap.put(id, attr);
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view.getId()==R.id.compelte_role){
			if(checkNature()&&flag){
				commitBtn.setClickable(false);
				commitBtn.setBackgroundResource(R.color.gray);
				downloaddialog.show();
			addUserRole(this, gid, sid,name,builderAttrMap(attrMap));
			}
		}else if(view == rightText){
			if(checkNature()&&flag){
				rightText.setClickable(false);
				commitBtn.setBackgroundResource(R.color.gray);
				downloaddialog.show();
			addUserRole(this, gid, sid,name,builderAttrMap(attrMap));
			}
		}
	}
	
	/**
	 * 添加用户角色返回到服务器
	 * @param context
	 * @param sid
	 * @param role
	 */
	public void addUserRole(final Context context,Long gid,Long sid,String rolename,Msgs.UserRoleData role){
		
		ProxyFactory.getInstance().getUserProxy().addUserRoleData(new ProxyCallBack<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				// TODO Auto-generated method stub
				if(result==0){
					downloaddialog.dismiss();
					completeUserRole();
					final Intent intent = new Intent(UserAddRoleInfoActivity.this, UserRoleDetailActivity.class);
					Timer timer = new Timer();
					TimerTask task = new TimerTask() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							dialog.dismiss();
							 startActivity(intent);
							 UserAddRoleInfoActivity.this.finish();
						}
					};
					timer.schedule(task, 2000);
					
					
				}
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				downloaddialog.dismiss();
				if(result==ROLE_NAME_EXIST){
					RoleNameExistDialog(context.getResources().getString(R.string.role_already_exist));
				}else if(result==GAME_ROLE_NOT_EXIST){
					RoleNameExistDialog(context.getResources().getString(R.string.role_not_exists));
				}else if(result==EC_MSGS_USER_GAME_NOT_ADD_ROLE){
					RoleNameExistDialog("该游戏不能添加角色哦!");
				}else if(result==EC_MSGS_USER_GAME_NOT_FOLLOW){
					setFollowTip();
				}
				rightText.setClickable(true);
			}
		}, context,gid, sid,rolename, role);
		
	}
	/**
	 * 检查输入的内容是否合适
	 * @param txt
	 */
	public boolean CheckUserRoleName(EditText txt,GameKeyVo vo){
		String moode = txt.getText().toString();
		if (!txt.getText().toString().isEmpty()) {
			if (ServiceFactory.getInstance().getWordsManager().match(moode)) {
				ToastUtil.showToast(UserAddRoleInfoActivity.this,"您输入的"+vo.getName()+"内容"+UserAddRoleInfoActivity.this.getResources().getString(R.string.global_words_error2));
				return false;
			} else {
				if(!txt.getText().toString().trim().isEmpty()){
					if(txt.getText().toString().getBytes().length>40){
						ToastUtil.showToast(UserAddRoleInfoActivity.this,"您输入的"+vo.getName()+"内容"+UserAddRoleInfoActivity.this.getResources().getString(R.string.role_name_size));
						return false;
					}
				}else{
					ToastUtil.showToast(UserAddRoleInfoActivity.this,"您输入的"+vo.getName()+"内容"+UserAddRoleInfoActivity.this.getResources().getString(R.string.role_not_space));
					return false;
				}
			}
		} else {
			ToastUtil.showToast(UserAddRoleInfoActivity.this, UserAddRoleInfoActivity.this.getResources().getString(R.string.txt_verify_fail)+vo.getName()+"哦！");
			return false;
		}
		return true;
	}
	
	
	/**
	 * 检验角色数据
	 * @return
	 */
	
	public boolean checkNature(){
		for(int i =0;i<list.size();i++){
			if(list.get(i).getType()==0){
				TextView text =(TextView) map.get(list.get(i).getId()).findViewById(R.id.user_item_text);
				String data =text.getText().toString();
				if(list.get(i).getAttrType()==MsgsConstants.GAME_ROLE_KEY_SERVER){
					if(data.contains(list.get(i).getName())){
						ToastUtil.showToast(UserAddRoleInfoActivity.this, getString(R.string.role_service_name));
						return false;
					}else{
						List<GameKeysDetail> keyList =list.get(i).getList();
						for(GameKeysDetail key:keyList){
							if(key.getContent().equals(text.getText().toString())){
								sid = key.getId();
							}
						}
					}
				}
				if(data.equals("请选择"+list.get(i).getName())){
					ToastUtil.showToast(UserAddRoleInfoActivity.this, list.get(i).getName()+getString(R.string.role_blank_values));
					return false;
				}
			}else if(list.get(i).getType()==1){
				EditText edit = (EditText)map.get(list.get(i).getId()).findViewById(R.id.user_role_name);
				if(CheckUserRoleName(edit,list.get(i))){
				}else{
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * 检查用户是否输入数据
	 * @return
	 */
	public boolean checkNatureData(){
		for(int i =0;i<list.size();i++){
			if(list.get(i).getType()==0){
				TextView text =(TextView) map.get(list.get(i).getId()).findViewById(R.id.user_item_text);
				String data =text.getText().toString();
				if(!data.contains(list.get(i).getName())){
					return true;
				}
			}else if(list.get(i).getType()==1){
				EditText edit = (EditText)map.get(list.get(i).getId()).findViewById(R.id.user_role_name);
				if(!edit.getText().toString().equals("")){
				return true;
			}
		}
		}
		return false;
	}
	
	/**
	 * 构建userRoleData参数
	 * @param map
	 * @return
	 */
	public UserRoleData builderAttrMap(Map<Long,UserRoleAttr> map){
		UserRoleData.Builder urdBuilder = UserRoleData.newBuilder();
		if(map==null&&map.size()<=0) return null;
		for(UserRoleAttr att:map.values()){
			RoleAttr.Builder attr =RoleAttr.newBuilder();
			attr.setKeyid(att.getId());
			attr.setKey(att.getKey());
			attr.setContent(att.getContent());
			urdBuilder.addAttr(attr);
		}
		return urdBuilder.build();
	}
	
	/**
	 * 完成角色创建提示
	 */
	public void completeUserRole(){
		dialog = new Dialog(UserAddRoleInfoActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("角色添加成功！");
		LinearLayout btnBttom = (LinearLayout)dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom =(LinearLayout)dialog.findViewById(R.id.text_bottom);
		btnBttom.setVisibility(View.GONE);
		textBttom.setVisibility(View.GONE);
		dialog.show();
	}
	
	public void getNetWork(){
		if(NetworkUtil.isNetworkAvailable(getApplicationContext())){
			flag  =true;
		}else{
			downloaddialog.dismiss();
			ToastUtil.showToast(UserAddRoleInfoActivity.this, "网络不可用，请检查网络!");
		}
	}
	
	@Override
	protected void back() {
		// TODO Auto-generated method stub
		
		if(checkNatureData()){
		 backTip();
		}else{
			super.back();
		}
	}
	/**
	 * 返回角色提示框
	 */
	public void backTip(){
		final Dialog dialog = new Dialog(UserAddRoleInfoActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("添加角色尚未完成，确定要返回吗？");
		LinearLayout btnBttom = (LinearLayout)dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom =(LinearLayout)dialog.findViewById(R.id.text_bottom);
		Button commit = (Button)dialog.findViewById(R.id.role_commitBtn);
		Button cancle =(Button)dialog.findViewById(R.id.role_cannelBtn);
		btnBttom.setVisibility(View.VISIBLE);
		textBttom.setVisibility(View.GONE);
		dialog.show();
		commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				UserAddRoleInfoActivity.this.finish();
			}
		});
		cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	private void setFollowTip(){
		final Dialog dialog = new Dialog(UserAddRoleInfoActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("你还未关注该游戏，暂不能添加角色，先去关注它吧！");
		LinearLayout btnBttom = (LinearLayout)dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom =(LinearLayout)dialog.findViewById(R.id.text_bottom);
		Button commit = (Button)dialog.findViewById(R.id.role_commitBtn);
		Button cancle =(Button)dialog.findViewById(R.id.role_cannelBtn);
		btnBttom.setVisibility(View.VISIBLE);
		textBttom.setVisibility(View.GONE);
		commit.setText("去关注");
		cancle.setText("取消");
		dialog.show();
		commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(UserAddRoleInfoActivity.this,GameTopicListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gid);
				intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
				startActivity(intent);
				dialog.dismiss();
			}
		});
		cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	/**
	 * 角色存在提示框
	 * @param text
	 */
	public void RoleNameExistDialog(String text){
		final Dialog dialog = new Dialog(UserAddRoleInfoActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText(text);
		LinearLayout btnBttom = (LinearLayout)dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom =(LinearLayout)dialog.findViewById(R.id.text_bottom);
		Button commit = (Button)dialog.findViewById(R.id.role_commitBtn);
		Button cancle =(Button)dialog.findViewById(R.id.role_cannelBtn);
		btnBttom.setVisibility(View.GONE);
		textBttom.setVisibility(View.VISIBLE);
		dialog.show();
		textBttom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				commitBtn.setClickable(true);
				rightText.setClickable(true);
				commitBtn.setBackgroundResource(R.drawable.add_game_role_btn_selector);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		 if(keyCode == KeyEvent.KEYCODE_BACK){    
				if(checkNatureData()){
					 backTip();
					}else{
						super.back();
					}
		 }
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 
	 * 处理错误码
	 * @param i
	 * @param msg
	 */
	private void handleErrorCode(Integer i,String msg){
		ErrorCodeUtil.handleErrorCode(UserAddRoleInfoActivity.this, i, msg);
	}
}
