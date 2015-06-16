/**      
 * UpgradeDialog.java Create on 2015-3-26     
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
import android.view.Window;
import android.widget.TextView;

import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.setting.ui.SettingFragment;
import com.iwgame.msgs.module.user.ui.UserGradePolicyActivity;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.MessageVo;
import com.youban.msgs.R;

/**
 * @ClassName: UpgradeDialog
 * @Description: 消息弹出升级奖励提示对话框
 * @author 王卫
 * @date 2015-3-26 下午5:41:40
 * @Version 1.0
 * 
 */
public class UpgradeDialog extends MessageDialog {

	/**
	 * @param context
	 */
	public UpgradeDialog(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param theme
	 */
	public UpgradeDialog(Context context, int theme) {
		super(context, theme);
	}
	
	/**
	 * 
	 * @param context
	 * @param theme
	 * @param messageVo
	 */
	public UpgradeDialog(Context context, int theme, MessageVo messageVo) {
		super(context, theme, messageVo);
	}

	/**
	 * @param context
	 * @param cancelable
	 * @param cancelListener
	 */
	public UpgradeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
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
		setContentView(R.layout.dialog_message_upgrade);
		// 数据设置
		TextView level = (TextView) findViewById(R.id.level);
		TextView upgradeDesc = (TextView) findViewById(R.id.upgradeDesc);
		// 数据设置
		if (messageVo != null && messageVo.getContent() != null && messageVo.getExt() != null
				&& messageVo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_USER_GRADE_UP))) {
			String ext = messageVo.getExt();
			// 解析json数据
			try {
				JSONObject obj = new JSONObject(ext);
				int grade = obj.getInt("grade");
				level.setText(String.valueOf(grade));
				upgradeDesc.setText("恭喜你升级到LV" + grade + "!");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 点击链接设置
		TextView gradeLink = (TextView) findViewById(R.id.gradeLink);
		gradeLink.setText(Html.fromHtml("<u>点击查看等级权限></u>"));
		gradeLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SystemContext.getInstance().getContext(), UserGradePolicyActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				SystemContext.getInstance().getContext().startActivity(intent);
			}
		});
		findViewById(R.id.okTxt).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				UpgradeDialog.this.dismiss();
			}
		});
	}

}
