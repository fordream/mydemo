/**      
 * RewardDialog.java Create on 2015-3-26     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.message.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.setting.ui.SettingFragment;
import com.iwgame.msgs.module.user.ui.PointTaskDetailActivity;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.MessageVo;
import com.youban.msgs.R;

/**
 * @ClassName: RewardDialog
 * @Description: 消息弹出奖励提示对话框
 * @author 王卫
 * @date 2015-3-26 下午5:39:10
 * @Version 1.0
 * 
 */
public class RewardDialog extends MessageDialog {

	/**
	 * @param context
	 */
	public RewardDialog(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param theme
	 */
	public RewardDialog(Context context, int theme) {
		super(context, theme);
	}
	
	/**
	 * 
	 * @param context
	 * @param theme
	 * @param messageVo
	 */
	public RewardDialog(Context context, int theme, MessageVo messageVo) {
		super(context, theme, messageVo);
	}

	/**
	 * @param context
	 * @param cancelable
	 * @param cancelListener
	 */
	public RewardDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
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
		setContentView(R.layout.dialog_message_reward);
		// 数据设置
		TextView reward = (TextView) findViewById(R.id.reward);
		TextView rewardDesc = (TextView) findViewById(R.id.rewardDesc);
		// 数据设置
		if (messageVo != null && messageVo.getContent() != null && messageVo.getExt() != null
				&& messageVo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_REWARD_POINT_EXP))) {
			String ext = messageVo.getExt();
			// 解析json数据
			try {
				JSONObject obj = new JSONObject(ext);
				int point = obj.getInt("point");
				int exp = obj.getInt("exp");
				String desc = obj.getString("desc");
				String treward = "";
				if (point > 0) {
					treward += "+" + point + "积分";
				}
				if (exp > 0) {
					treward += " +" + exp + "经验";
				}
				reward.setText(treward);
				rewardDesc.setText(desc);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 点击链接设置
		TextView rewardLink = (TextView) findViewById(R.id.rewardLink);
		rewardLink.setText(Html.fromHtml("<u>看看还能挣取哪些积分?></u>"));
		rewardLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SystemContext.getInstance().getContext(), PointTaskDetailActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				SystemContext.getInstance().getContext().startActivity(intent);
			}
		});
	}

}
