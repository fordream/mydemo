/**      
 * NearUserAdapter.java Create on 2013-9-9     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.PinyinUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: SplendidUserAdapter
 * @Description: 推荐用户列表适配器
 * @author 
 * @date 2014-10-24 上午10:25:37
 * @Version 1.0
 * 
 */
public class SplendidUserAdapter extends SimpleAdapter implements SectionIndexer {

	protected static final String TAG = "SplendidUserAdapter";
	
	private boolean isShowRdesc = false;
	private UserVo loginUserVo = null;
	private String cardsType; 

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public SplendidUserAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, boolean isShowRdesc, String cardsType) {
		super(context, data, resource, from, to);
		this.isShowRdesc = isShowRdesc;
		loginUserVo = SystemContext.getInstance().getExtUserVo();
		this.cardsType = cardsType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
		Object tag = convertView.getTag();
		final ViewHolder holder;
		if (tag == null) {
			holder = new ViewHolder();
			holder.avatarView = (ImageView) convertView.findViewById(R.id.icon);
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			holder.sex = (ImageView) convertView.findViewById(R.id.sex);
			holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
			holder.age = (TextView) convertView.findViewById(R.id.age);
			holder.newsTxt = (TextView) convertView.findViewById(R.id.newsTxt);
			holder.newtag = (ImageView) convertView.findViewById(R.id.newtag);
			holder.submitBtn = (ImageView) convertView.findViewById(R.id.submitBtn);
			holder.hotArea = (ImageView) convertView.findViewById(R.id.hotArea);
			holder.submitTxt = (TextView) convertView.findViewById(R.id.submitTxt);
			holder.rightView = (LinearLayout) convertView.findViewById(R.id.rightView);
			holder.rdesc = (TextView) convertView.findViewById(R.id.rdesc);
			holder.grade = (TextView) convertView.findViewById(R.id.grade);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) tag;
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (HashMap<String, Object>) getItem(position);
		// 获取用户
		final long uid = Long.valueOf(map.get("uid").toString());
		// 显示头像
		if (map.containsKey("pic") && null != map.get("pic")) {
			String avatar = (String) map.get("pic");
			ImageViewUtil.showImage(holder.avatarView, avatar, R.drawable.common_user_icon_default);
		}
		// 查找用户关注贴吧
		holder.hotArea.getBackground().setAlpha(0);
		holder.newtag.setVisibility(View.GONE);
		holder.newsTxt.setText("");
		holder.submitBtn.setVisibility(View.VISIBLE);
		//holder.hotArea.setVisibility(View.VISIBLE);
		holder.submitTxt.setVisibility(View.VISIBLE);
		if (map.containsKey("title") && null != map.get("title")) {
			String title = (String) map.get("title");
			holder.nickname.setText(title);
		}
		if (map.containsKey("news") && null != map.get("news")) {
			String news = (String) map.get("news");
			if (!news.isEmpty()) {
				holder.newtag.setVisibility(View.VISIBLE);
				holder.newsTxt.setText(news);
			}else{
				if (map.containsKey("mood") && null != map.get("mood")) {
					String mood = (String) map.get("mood");
					if(!mood.isEmpty()){
						holder.newtag.setVisibility(View.VISIBLE);
						holder.newtag.setBackgroundResource(R.drawable.common_qian);
						holder.newsTxt.setText(mood);
					}
				}
			}
		}
		if (map.containsKey("age") && (Integer) map.get("age") > 0) {
			int age = AgeUtil.convertAgeByBirth((Integer) map.get("age"));
			if(age > 0){
				holder.age.setText(AgeUtil.convertAgeByBirth((Integer) map.get("age")) + "");
			}else{
				holder.age.setText("");
			}
			
			holder.age.setVisibility(View.VISIBLE);

			if(map.containsKey("gender") && (Integer) map.get("gender") == 0){
				Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_man_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				holder.age.setCompoundDrawables(sexdraw, null, null, null);
				holder.age.setCompoundDrawablePadding(DisplayUtil.dip2px(convertView.getContext(), 4));
				holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
			}else if(map.containsKey("gender") && (Integer) map.get("gender") == 1){
				Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_woman_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				holder.age.setCompoundDrawables(sexdraw, null, null, null);
				holder.age.setCompoundDrawablePadding(DisplayUtil.dip2px(convertView.getContext(), 4));
				holder.age.setBackgroundResource(R.drawable.common_item_jh_shap);
			}
		} else {
			holder.age.setText("");
			holder.age.setCompoundDrawablePadding(0);
			if(map.containsKey("gender") && (Integer) map.get("gender") == 0){
				Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_man_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				holder.age.setCompoundDrawables(sexdraw, null, null, null);
				holder.age.setCompoundDrawablePadding(0);
				holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
				holder.age.setVisibility(View.VISIBLE);
			}else if(map.containsKey("gender") && (Integer) map.get("gender") == 1){
				holder.age.setVisibility(View.VISIBLE);
				Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_woman_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				holder.age.setCompoundDrawables(sexdraw, null, null, null);
				holder.age.setCompoundDrawablePadding(0);
				holder.age.setBackgroundResource(R.drawable.common_item_jh_shap);
			}else{
				holder.age.setVisibility(View.GONE);
			}
		}
		if (map.containsKey("grade") && (((Integer)map.get("grade"))>0)) {
			holder.grade.setVisibility(View.VISIBLE);
			holder.grade.setText("LV"+map.get("grade"));
		}else{
			holder.grade.setVisibility(View.GONE);
		}
		
		if(isShowRdesc){
			// 获取距离
			if (holder != null && holder.rdesc != null) {
				int distance =  0;
				if(map.containsKey("distance")){
					distance = Integer.valueOf(map.get("distance").toString());
				}
				long time = 0;
				if(map.containsKey("lastLoginTime")){
					time = Long.valueOf(map.get("lastLoginTime").toString());
				}
				
				if (distance != 0 && time != 0) {//两者都有
					String p = DistanceUtil.covertDistance(distance);
					String ltime = SafeUtils.getDate2MyStr2(time);
					holder.rdesc.setVisibility(View.VISIBLE);
					holder.rdesc.setText(p + " | " + ltime);
				} else if(distance != 0 && time == 0){//有距离没时间
					String p = DistanceUtil.covertDistance(distance);
					holder.rdesc.setVisibility(View.VISIBLE);
					holder.rdesc.setText(p);
				} else if(distance == 0 && time != 0){//没距离有时间
					String ltime = SafeUtils.getDate2MyStr2(time);
					holder.rdesc.setVisibility(View.VISIBLE);
					holder.rdesc.setText(ltime);
				} else {//两者都没
					holder.rdesc.setVisibility(View.INVISIBLE);
				}
			}
		}
		
		// 获取关注信息
		UserVo userVo = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext()).getUserByUserId(uid);
		if (userVo != null)
			map.put("rel", userVo.getRelPositive());
		else
			map.put("rel", 0);
		final int rel = (Integer) map.get("rel");
		if (rel == 1) {
			holder.submitBtn.setEnabled(false);
			holder.hotArea.setEnabled(false);
		} else {
			holder.submitBtn.setEnabled(true);
			holder.hotArea.setEnabled(true);
		}
		// 获取用户
		if (loginUserVo != null && uid == loginUserVo.getUserid()) {
			holder.submitBtn.setVisibility(View.INVISIBLE);
			holder.hotArea.setVisibility(View.INVISIBLE);
			holder.submitTxt.setVisibility(View.INVISIBLE);
		}
		holder.submitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				holder.submitBtn.setEnabled(false);
				holder.hotArea.setEnabled(false);
				addFollow(holder.submitBtn, holder.hotArea, uid, position, holder.submitTxt);
			}
		});
		holder.hotArea.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				holder.submitBtn.setEnabled(false);
				holder.hotArea.setEnabled(false);
				addFollow(holder.submitBtn, holder.hotArea, uid, position, holder.submitTxt);
			}
		});
		return convertView;
	}




	/**
	 * 关注
	 * 
	 * @param hotArea
	 * @param uid
	 */
	protected void addFollow(final View submit, final View hotArea, final long uid, final int position, final TextView submitTxt) {
			final CustomProgressDialog dialog = CustomProgressDialog.createDialog(hotArea.getContext());
			dialog.show();
			ProxyFactory.getInstance().getUserProxy().addFollowUser(new ProxyCallBack<Integer>() {

				@Override
				public void onSuccess(Integer result) {
					if (result == com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE) {
						Map<String, Object> map = (HashMap<String, Object>) getItem(position);
						map.put("rel", 1);
						hotArea.setEnabled(false);
						submit.setEnabled(false);
						if(cardsType != null && cardsType.equals("FATE")){//缘分好友推荐的关注数统计 
							HashMap<String, String> ummap = new HashMap<String, String>();
							ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(loginUserVo.getUserid()));
							ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, loginUserVo.getUsername());
							ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, String.valueOf(uid));
							UserVo uvo = DaoFactory.getDaoFactory().getUserDao(submit.getContext()).getUserById(uid);
							if (uvo != null)
								ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, uvo.getUsername());
							
							MobclickAgent.onEvent(submit.getContext(), UMConfig.MSGS_EVENT_FTAE_USER_FOLLOW, ummap);
						}
					} else {
						hotArea.setEnabled(true);
						submit.setEnabled(true);
						LogUtil.e(TAG, "关注返回错误");
					}
					dialog.dismiss();
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					dialog.dismiss();
					hotArea.setEnabled(true);
					submit.setEnabled(true);
					ToastUtil.showToast(hotArea.getContext(), "关注失败");
				}
			}, hotArea.getContext(), uid, false);
	}

	/**
	 * 创建并显示绑定手机对话框
	 * @param actionName
	 * @param view
	 */
	private void createBundPhoneDialog(final View view){
		Intent intent = new Intent(view.getContext(), BundPhoneActivity.class);
		view.getContext().startActivity(intent);
	}
	



	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SectionIndexer#getSections()
	 */
	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SectionIndexer#getPositionForSection(int)
	 */
	@Override
	public int getPositionForSection(int section) {
		if (section == '!') {
			return 0;
		} else {
			for (int i = 0; i < getCount(); i++) {
				Map<String, Object> map = (HashMap<String, Object>) getItem(i);
				String nickname = (String) map.get("nickname");
				if (nickname != null) {
					String headChar = PinyinUtil.getPinYinHeadChar(nickname);
					char firstChar = headChar.toUpperCase().charAt(0);
					if (firstChar == section) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SectionIndexer#getSectionForPosition(int)
	 */
	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	static class ViewHolder {
		ImageView avatarView;
		TextView desc;
		ImageView sex;
		TextView nickname;
		TextView age;
		TextView newsTxt;
		ImageView newtag;
		ImageView submitBtn;
		ImageView hotArea;
		TextView submitTxt;
		LinearLayout rightView;
		TextView rdesc;
		TextView grade;
	}

}
