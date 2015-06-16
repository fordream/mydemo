/**      
 * feedbackActivity.java Create on 2013-11-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.setting.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: feedbackActivity
 * @Description: 用户反馈
 * @author 王卫
 * @date 2013-11-27 下午3:08:39
 * @Version 1.0
 * 
 */
public class feedbackActivity extends BaseSuperActivity implements OnClickListener {

	protected static final String TAG = "feedbackActivity";

	private ImageView sendMenu;

	private EditText content;

	private TextView titleTxt;
	private Button backBtn;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_user_feedback);
		initialize();
	}

	/**
	 * 初始化
	 */
	private void initialize() {
		backBtn = (Button)findViewById(R.id.leftBtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				feedbackActivity.this.finish();
			}
		});
		// 设置TITLE
		titleTxt = (TextView)findViewById(R.id.titleTxt);
		titleTxt.setText("用户反馈");
		sendMenu = new ImageView(this);
		sendMenu.setBackgroundResource(R.drawable.common_tab_btn_go);
		LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rightView.addView(sendMenu,params);
		sendMenu.setOnClickListener(this);
		content = (EditText) findViewById(R.id.content);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == sendMenu.getId()) {// 发送反馈信息
			if (content.getText() != null && !content.getText().toString().isEmpty()) {
				final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
				dialog.show();
				ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						ToastUtil.showToast(feedbackActivity.this, "反馈成功");
						dialog.dismiss();
						feedbackActivity.this.finish();
					}

					@Override
					public void onFailure(Integer result,String resultMsg) {
						ToastUtil.showToast(feedbackActivity.this, "反馈失败");
						LogUtil.e(TAG, "反馈请求失败");
						dialog.dismiss();
					}
				}, this, -1, -1, MsgsConstants.OP_ADVICE, content.getText().toString(), null,null);
			} else {
				ToastUtil.showToast(this, "请填写反馈内容");
			}
		}
	}

}
