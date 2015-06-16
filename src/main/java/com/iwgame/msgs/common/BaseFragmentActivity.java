/**      
 * BaseActicity.java Create on 2013-8-26     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.youban.msgs.R;

public class BaseFragmentActivity extends BaseSuperFragmentActivity {

	public TextView titleTxt;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_content);

		titleTxt = (TextView) findViewById(R.id.titleTxt);

		// 设置返回功能
		Button backBtn = (Button) findViewById(R.id.leftBtn);
		if (backBtn != null) {
			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					BaseFragmentActivity.this.finish();
				}
			});
		}

	}

	public void setLeftVisible(Boolean visible) {
		View view = findViewById(R.id.left);
		if (view != null) {
			if (visible) {
				// 显示左边菜单
				view.setVisibility(View.VISIBLE);
			} else {
				// 隐藏左边菜单
				view.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void setRightVisible(Boolean visible) {
		View view = findViewById(R.id.right);
		if (view != null) {
			if (visible) {
				// 显示左边菜单
				view.setVisibility(View.VISIBLE);
			} else {
				// 隐藏左边菜单
				view.setVisibility(View.INVISIBLE);
			}
		}
	}

	// /**
	// * 显示LOADING
	// */
	// protected void showLoading() {
	// DialogManager.getInstance().show(this);
	// }
	//
	// /**
	// * 不显示LOADING
	// */
	// protected void dismissLoading() {
	// DialogManager.getInstance().dismiss();
	// }
}
