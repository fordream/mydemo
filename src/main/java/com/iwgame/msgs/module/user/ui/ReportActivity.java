/**      
 * ReportActivity.java Create on 2013-11-22     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: ReportActivity
 * @Description: 举报页面
 * @author 王卫
 * @date 2013-11-22 下午10:00:59
 * @Version 1.0
 * 
 */
public class ReportActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = "ReportActivity";
	private long tid;
	private int ttype;
	private String extContent;
	private String chatinfo;
	private ImageView commitBtn;
	private EditText content;
	private Button tag1;
	private Button tag2;
	private Button tag3;
	private Button tag4;
	private boolean btag1 = false;
	private boolean btag2 = false;
	private boolean btag3 = false;
	private boolean btag4 = false;

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
			// 举报目标ID
			tid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID);
			// 举报类型
			ttype = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE);
			// 举报内容（如图片资源ID等）
			extContent = bundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CONTENT);
			chatinfo = bundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CHATINFO);
		}
		// 显示左边
		setLeftVisible(true);
		// 添加右边功能按钮
		setRightVisible(true);
		commitBtn = new ImageView(this);
		commitBtn.setBackgroundResource(R.drawable.common_tab_btn_go);
		LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rightView.addView(commitBtn, params);
		commitBtn.setOnClickListener(this);
		// 设置TITLE
		setTitleTxt("举报");
		// 设置内容UI
		getContentView().addView(View.inflate(this, R.layout.user_report, null), params);

		content = (EditText) findViewById(R.id.content);
		InputFilterUtil.lengthFilter(this, content, 100, getString(R.string.report_post_word, 50));
		tag1 = (Button) findViewById(R.id.tag1);
		tag2 = (Button) findViewById(R.id.tag2);
		tag3 = (Button) findViewById(R.id.tag3);
		tag4 = (Button) findViewById(R.id.tag4);

		tag1.setOnClickListener(this);
		tag2.setOnClickListener(this);
		tag3.setOnClickListener(this);
		tag4.setOnClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tag1) {
			btag1 = !btag1;
			if (btag1) {
				tag1.setBackgroundResource(R.drawable.common_report_tag_check_bg);
			} else {
				tag1.setBackgroundResource(R.drawable.common_report_tag_uncheck_bg);
			}
		} else if (v.getId() == R.id.tag2) {
			btag2 = !btag2;
			if (btag2) {
				tag2.setBackgroundResource(R.drawable.common_report_tag_check_bg);
			} else {
				tag2.setBackgroundResource(R.drawable.common_report_tag_uncheck_bg);
			}
		} else if (v.getId() == R.id.tag3) {
			btag3 = !btag3;
			if (btag3) {
				tag3.setBackgroundResource(R.drawable.common_report_tag_check_bg);
			} else {
				tag3.setBackgroundResource(R.drawable.common_report_tag_uncheck_bg);
			}
		} else if (v.getId() == R.id.tag4) {
			btag4 = !btag4;
			if (btag4) {
				tag4.setBackgroundResource(R.drawable.common_report_tag_check_bg);
			} else {
				tag4.setBackgroundResource(R.drawable.common_report_tag_uncheck_bg);
			}
		} else if (v.getId() == commitBtn.getId()) {
			if (FastClickLimitUtil.isFastClick())
			    return;
			report();
		}
	}

	/**
	 * 举报
	 */
	private void report() {
		if (content.getText() != null && !content.getText().toString().isEmpty()) {
			if(ServiceFactory.getInstance().getWordsManager().matchName(content.getText().toString())){
				ToastUtil.showToast(this, "输入非法字符");
			}else{
				commitBtn.setEnabled(false);
				StringBuffer ct = new StringBuffer();
				// 添加标签TAG
				if (btag1) {
					ct.append("tg1,");
				}
				if (btag2) {
					ct.append("tg2,");
				}
				if (btag3) {
					ct.append("tg3,");
				}
				if (btag4) {
					ct.append("tg4,");
				}
				if (ct.length() > 0) {
					ct.deleteCharAt(ct.length()-1);
				}
				ct.append("|");
				// 添加资源内容
				if (extContent != null) {
					ct.append(extContent);
				}
				ct.append("|");
				ct.append(content.getText().toString().replace("|", ""));
				ct.append("|");
				// 添加资源内容
				if (chatinfo != null) {
					ct.append(chatinfo);
				}
				final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
				dialog.show();
				ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						ToastUtil.showToast(ReportActivity.this, "举报成功");
						dialog.dismiss();
						commitBtn.setEnabled(true);
						ReportActivity.this.finish();
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						commitBtn.setEnabled(true);
						LogUtil.e(TAG, "举报请求失败");
						dialog.dismiss();
					}
				}, this, tid, ttype, MsgsConstants.OP_REPORT, ct.toString(), null,null);	
			}
		} else {
			ToastUtil.showToast(this, "请填写举报内容");
		}
	}
}
