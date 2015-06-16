/**      
* UserRoleAdapter2.java Create on 2015-6-3     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.user.adapter;

import java.util.List;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.user.ui.UserRoleDetailActivity;
import com.iwgame.msgs.proto.Msgs.UserRoleData;
import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.UserRoleVo;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * @ClassName: UserRoleAdapter2 
 * @Description: TODO(...) 
 * @author xingjianlong
 * @date 2015-6-3 上午9:45:17 
 * @Version 1.0
 * 
 */
public class UserRoleAdapter2 extends BaseAdapter implements OnClickListener{
	private  UserRoleDetailActivity context;
	private List<Object> list;
	private LayoutInflater inflater;
	public static  boolean flag;
	private Dialog dialog;
	public boolean NetFlag;
	private static final int EC_MSGS_USER_ROLE_PLAY_OCCUPY = 500018; 
	public UserRoleAdapter2(UserRoleDetailActivity context,List<Object>list,boolean flag){
		this.context = context;
		this.list = list;
		this.flag = flag;
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView=inflater.inflate(R.layout.user_role_info_item, null);
			holder.gameImage =(ImageView)convertView.findViewById(R.id.user_game_role_image);
			holder.userName =(TextView)convertView.findViewById(R.id.user_role_name);
			holder.sName = (TextView)convertView.findViewById(R.id.user_role_sname);
			holder.userGrade =(TextView)convertView.findViewById(R.id.user_role_grade);
			holder.userRZ =(TextView)convertView.findViewById(R.id.user_role_rz);
			holder.deleteBtn =(Button)convertView.findViewById(R.id.delete_user_role);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		UserRoleData vo = (UserRoleData) list.get(position);
		setUserRoleInfo(vo,holder);
		if(flag){
			holder.deleteBtn.setVisibility(View.VISIBLE);
			holder.userRZ.setVisibility(View.GONE);
			holder.deleteBtn.setTag(vo);
			holder.deleteBtn.setOnClickListener(this);
		}else{
			holder.deleteBtn.setVisibility(View.GONE);
			holder.userRZ.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

/**
 * 设置用户信息
 * @param vo
 * @param holder
 */
	private void setUserRoleInfo(UserRoleData vo,ViewHolder holder) {
		holder.gameImage.setImageResource(R.drawable.postbar_thumbimg_default);
		ImageViewUtil.showImage(holder.gameImage, vo.getAvatar(), R.drawable.postbar_thumbimg_default);
		List<RoleAttr> attrlist = vo.getAttrList();
		StringBuilder builder = new StringBuilder();
		for(RoleAttr attr: attrlist){
			if(attr.getAttrtype()==MsgsConstants.GAME_ROLE_KEY_SERVER){
				holder.sName.setText(attr.getContent());
			}else if(attr.getAttrtype()==MsgsConstants.GAME_ROLE_KEY_USER){
				holder.userName.setText(attr.getContent());
			}else{
				builder.append(attr.getContent()+" ");
			}
		}
		holder.userGrade.setText(builder.toString());
		if(vo.getStatus()==1){
			holder.userRZ.setText("已认证");
		}else if(vo.getStatus()==0){
			holder.userRZ.setText("未认证");
		}else if(vo.getStatus()==2){
			holder.userRZ.setText("未通过");
		}else if(vo.getStatus()==3){
			holder.userRZ.setText("未验证");
		}
		
	}
	class ViewHolder{
	ImageView gameImage;
	TextView userName;
	TextView sName;
	TextView userGrade;
	TextView userRZ;
	Button     deleteBtn;
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view.getId()==R.id.delete_user_role){
			UserRoleData vo = (UserRoleData) view.getTag();
			getNetWork();
			deleteRole(vo);
		
		}
	}
	/**
	 * 删除游戏角色提示框
	 * @param vo
	 */
	
	public void deleteRole(final UserRoleData vo ){
		dialog = new Dialog(context, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("确定删除该角色吗？");
		LinearLayout btnBttom = (LinearLayout)dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom =(LinearLayout)dialog.findViewById(R.id.text_bottom);
		btnBttom.setVisibility(View.VISIBLE);
		textBttom.setVisibility(View.GONE);
		dialog.show();
		dialog.findViewById(R.id.role_cannelBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.findViewById(R.id.role_commitBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				deleteUserRole(context,vo.getRoleid(), vo);
				
			}
		});
	}
	/**
	 * 删除用户角色
	 * @param context
	 * @param roleid
	 */
	public void deleteUserRole(final UserRoleDetailActivity context,Long roleid,final UserRoleData vo){
		ProxyFactory.getInstance().getUserProxy().deleteGameRole(new ProxyCallBack<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				// TODO Auto-generated method stub
				if(result == 0){
					
					list.remove(vo);
					if(list.size()<=0){
						context.onResume();
					}
					UserRoleAdapter2.this.notifyDataSetChanged();
					UserRoleAdapter2.this.notifyDataSetInvalidated();
					dialog.dismiss();
					ToastUtil.showToast(context, "角色删除成功!");
					
				}else{
					ToastUtil.showToast(context, "角色删除失败!");
				}
			
			}
			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				//handleErrorCode(result, resultMsg);
				if(dialog.isShowing()){
					dialog.dismiss();
				}
				if(result==EC_MSGS_USER_ROLE_PLAY_OCCUPY){
				ToastUtil.showToast(context, "角色信息存在陪玩信息，不可删除");
				}
			}
		}, context, roleid);
	}
	
	public void getNetWork(){
		if(NetworkUtil.isNetworkAvailable(context)){
			NetFlag  =true;
		}else{
			ToastUtil.showToast(context, "网络不可用，请检查网络!");
		}
	}
	
	/**
	 * 
	 * 处理错误码
	 * @param i
	 * @param msg
	 */
	private void handleErrorCode(Integer i,String msg){
		ErrorCodeUtil.handleErrorCode(context, i, msg);
	}
}
