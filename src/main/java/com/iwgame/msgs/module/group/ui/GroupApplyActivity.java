/**      
 * GroupVerifyActivity.java Create on 2013-10-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.baidu.location.BDLocation;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.LocationCallBack;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.UserActionDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: GroupVerifyActivity
 * @Description: 公会验证请求
 * @author 王卫
 * @date 2013-10-24 下午08:20:25
 * @Version 1.0
 * 
 */
public class GroupApplyActivity extends BaseActivity implements OnClickListener {
	// 发送按钮
	private Button sendBtn;
	// 编辑内容
	private EditText contentTxt;

	private long grid;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
	}

	/**
	 * 初始化
	 */
	private void initialize() {
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			grid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);
		}
		// 显示左边
		setLeftVisible(true);
		titleTxt.setText("申请加入公会");
		// 添加右边功能按钮
		sendBtn = new Button(this);
		sendBtn.setBackgroundResource(R.drawable.group_send_apply_menu);
		LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rightView.setVisibility(View.VISIBLE);
		rightView.addView(sendBtn, params);
		sendBtn.setOnClickListener(this);
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View detailView = (LinearLayout) View.inflate(this, R.layout.group_verify_content, null);
		contentView.addView(detailView, params);
		// 编辑内容
		contentTxt = (EditText) findViewById(R.id.contentTxt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == sendBtn.getId()) {// 发送申请请求
			if (contentTxt.getText() != null && !contentTxt.getText().toString().isEmpty()) {
				if (ServiceFactory.getInstance().getWordsManager().match(contentTxt.getText().toString())) {
					ToastUtil.showToast(GroupApplyActivity.this, getResources().getString(R.string.global_words_error));
					return;
				}

				sendBtn.setEnabled(false);
				final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
				dialog.show();
				
				ServiceFactory.getInstance().getBaiduLocationService().requestLocation(new LocationCallBack() {
					@Override
					public void onBack(BDLocation bdLocation) {
						joinGroup(dialog);
					}
				});
		
				
			} else {
				ToastUtil.showToast(this, getString(R.string.group_apply_content_isnull));
			}
		}
	}

	
	
	/**
	 * 申请加入公会
	 */
	private void joinGroup(final CustomProgressDialog dialog){
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				sendBtn.setEnabled(true);
				dialog.dismiss();
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					ToastUtil.showToast(GroupApplyActivity.this, getString(R.string.group_apply_success));
					DaoFactory.getDaoFactory().getUserActionDao(GroupApplyActivity.this)
							.insertUserAction(grid, UserActionDao.ENTITY_TYPE_GROUP, UserActionDao.OP_TYPE_GROUP_APPLEY);
					GroupApplyActivity.this.finish();
					break;
				
				default:
					ToastUtil.showToast(GroupApplyActivity.this, getString(R.string.group_apply_fail));
					break;
				}
			}

			@Override
			public void onFailure(Integer result,String resultMsg) {
			    switch (result) {
				
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_MSGS_GROUP_ALREADY_IN_VALUE:
					ToastUtil.showToast(GroupApplyActivity.this, getString(R.string.group_apply_already_in));
					break;
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_MSGS_OVER_COUNT_VALUE:
					ToastUtil.showToast(GroupApplyActivity.this, getString(R.string.group_apply_over_join_group));
					break;
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_MSGS_GROUP_ALREDY_APPLY_VALUE:
					ToastUtil.showToast(GroupApplyActivity.this, getString(R.string.group_apply_over_max_count));
					break;
				default:
					ToastUtil.showToast(GroupApplyActivity.this, getString(R.string.group_apply_fail));
					break;
				}
				sendBtn.setEnabled(true);
				dialog.dismiss();
			}
		}, this, grid, MsgsConstants.OT_GROUP, MsgsConstants.OP_JOIN_GROUP, contentTxt.getText().toString(), null,null);
	}
	
	
}
