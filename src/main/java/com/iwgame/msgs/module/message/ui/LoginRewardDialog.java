/**      
 * LoginRewardDialog.java Create on 2015-3-26     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.message.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.user.ui.PointTaskDetailActivity;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.MessageVo;
import com.youban.msgs.R;

/**
 * @ClassName: LoginRewardDialog
 * @Description: 消息弹出登陆奖励提示对话框
 * @author 王卫
 * @date 2015-3-26 下午5:29:44
 * @Version 1.0
 * 
 */
public class LoginRewardDialog extends MessageDialog {

	private LinearLayout mondayRewardContent;
	private TextView mondayReward;
	private LinearLayout tuesdayRewardContent;
	private TextView tuesdayReward;
	private LinearLayout wednesdayRewardContent;
	private TextView wednesdayReward;
	private LinearLayout thursdayRewardContent;
	private TextView thursdayReward;
	private LinearLayout fridayRewardContent;
	private TextView fridayReward;
	private LinearLayout saturdayRewardContent;
	private TextView saturdayReward;
	private LinearLayout sundayRewardContent;
	private TextView sundayReward;

	/**
	 * @param context
	 */
	public LoginRewardDialog(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param theme
	 */
	public LoginRewardDialog(Context context, int theme) {
		super(context, theme);
	}
	
	/**
	 * 
	 * @param context
	 * @param theme
	 * @param messageVo
	 */
	public LoginRewardDialog(Context context, int theme, MessageVo messageVo) {
		super(context, theme, messageVo);
	}

	/**
	 * @param context
	 * @param cancelable
	 * @param cancelListener
	 */
	public LoginRewardDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.message.ui.MessageDialog#init()
	 */
	@Override
	protected void init() {
		super.init();
		setContentView(R.layout.dialog_message_login_reward);
		mondayRewardContent = (LinearLayout) findViewById(R.id.mondayRewardContent);
		mondayReward = (TextView) findViewById(R.id.mondayReward);
		tuesdayRewardContent = (LinearLayout) findViewById(R.id.tuesdayRewardContent);
		tuesdayReward = (TextView) findViewById(R.id.tuesdayReward);
		wednesdayRewardContent = (LinearLayout) findViewById(R.id.wednesdayRewardContent);
		wednesdayReward = (TextView) findViewById(R.id.wednesdayReward);
		thursdayRewardContent = (LinearLayout) findViewById(R.id.thursdayRewardContent);
		thursdayReward = (TextView) findViewById(R.id.thursdayReward);
		fridayRewardContent = (LinearLayout) findViewById(R.id.fridayRewardContent);
		fridayReward = (TextView) findViewById(R.id.fridayReward);
		saturdayRewardContent = (LinearLayout) findViewById(R.id.saturdayRewardContent);
		saturdayReward = (TextView) findViewById(R.id.saturdayReward);
		sundayRewardContent = (LinearLayout) findViewById(R.id.sundayRewardContent);
		sundayReward = (TextView) findViewById(R.id.sundayReward);
		// 数据设置
		if (messageVo != null && messageVo.getContent() != null && messageVo.getExt() != null
				&& messageVo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_REWARD_LOGIN))) {
			String ext = messageVo.getExt();
			try {
				// 解析json数据
				JSONObject obj = new JSONObject(ext);
				int times = obj.getInt("times");
				JSONArray array = obj.getJSONArray("reward");
				if (array != null) {
					int length = array.length();
					for (int i = 0; i < length; i++) {
						JSONObject rjson = new JSONObject(array.get(i).toString());
						int day = rjson.getInt("day");
						int point = rjson.getInt("point");
						//int exp = rjson.getInt("exp");
						if (day == 1) {
							mondayReward.setText(String.valueOf(point));
						} else if (day == 2) {
							tuesdayReward.setText(String.valueOf(point));
						} else if (day == 3) {
							wednesdayReward.setText(String.valueOf(point));
						} else if (day == 4) {
							thursdayReward.setText(String.valueOf(point));
						} else if (day == 5) {
							fridayReward.setText(String.valueOf(point));
						} else if (day == 6) {
							saturdayReward.setText(String.valueOf(point));
						} else if (day == 7) {
							sundayReward.setText(String.valueOf(point));
						}
					}
				}
				// 设置数据
				if (times == 1) {
					setLoginRewardView(R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_pre, R.drawable.dailylogin_integral_pre,
							R.drawable.dailylogin_integral_pre, R.drawable.dailylogin_integral_pre, R.drawable.dailylogin_integral_pre,
							R.drawable.dailylogin_integral_pre);
				} else if (times == 2) {
					setLoginRewardView(R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_pre,
							R.drawable.dailylogin_integral_pre, R.drawable.dailylogin_integral_pre, R.drawable.dailylogin_integral_pre,
							R.drawable.dailylogin_integral_pre);
				} else if (times == 3) {
					setLoginRewardView(R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set,
							R.drawable.dailylogin_integral_pre, R.drawable.dailylogin_integral_pre, R.drawable.dailylogin_integral_pre,
							R.drawable.dailylogin_integral_pre);
				} else if (times == 4) {
					setLoginRewardView(R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set,
							R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_pre, R.drawable.dailylogin_integral_pre,
							R.drawable.dailylogin_integral_pre);
				} else if (times == 5) {
					setLoginRewardView(R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set,
							R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_pre,
							R.drawable.dailylogin_integral_pre);
				} else if (times == 6) {
					setLoginRewardView(R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set,
							R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set,
							R.drawable.dailylogin_integral_pre);
				} else if (times == 7) {
					setLoginRewardView(R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set,
							R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set, R.drawable.dailylogin_integral_set,
							R.drawable.dailylogin_integral_set);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// 点击链接设置
		TextView loginRewardLink = (TextView) findViewById(R.id.loginRewardLink);
		loginRewardLink.setText(Html.fromHtml("<u>看看还能挣取哪些积分?></u>"));
		loginRewardLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SystemContext.getInstance().getContext(), PointTaskDetailActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				SystemContext.getInstance().getContext().startActivity(intent);
			}
		});
	}

	/**
	 * 
	 * @param one
	 * @param two
	 * @param three
	 * @param four
	 * @param five
	 * @param six
	 * @param seven
	 */
	private void setLoginRewardView(int one, int two, int three, int four, int five, int six, int seven) {
		mondayRewardContent.setBackgroundResource(one);
		tuesdayRewardContent.setBackgroundResource(two);
		wednesdayRewardContent.setBackgroundResource(three);
		thursdayRewardContent.setBackgroundResource(four);
		fridayRewardContent.setBackgroundResource(five);
		saturdayRewardContent.setBackgroundResource(six);
		sundayRewardContent.setBackgroundResource(seven);
	}

}
