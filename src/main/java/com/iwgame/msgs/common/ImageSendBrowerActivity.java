/**      
 * ImageSendBrowerActivity.java Create on 2015-1-14     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.utils.ImageUtil;
import com.youban.msgs.R;

/**
 * @ClassName: ImageSendBrowerActivity
 * @Description: 发送图片预览界面
 * @author 王卫
 * @date 2015-1-14 下午4:21:48
 * @Version 1.0
 * 
 */
public class ImageSendBrowerActivity extends BaseSuperActivity {

	private ImageView imageView;

	private byte[] content;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_image_brower);
		Intent intent = this.getIntent();
		if (intent != null) {
			Bundle tmpbundle = intent.getExtras();
			if (tmpbundle != null) {
				content = tmpbundle.getByteArray(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CONTENT);
			}
		}
		// 设置图片
		imageView = (ImageView) findViewById(R.id.imageView);
		if (content != null)
			imageView.setImageBitmap(ImageUtil.Bytes2Bimap(content));
		// 左边返回菜单
		findViewById(R.id.leftBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 右边才到完成发送
		TextView topRightTextView = new TextView(this);
		topRightTextView.setText(R.string.imagebrower_send);
		topRightTextView.setTextColor(Color.rgb(255, 255, 255));
		LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
		rightView.addView(topRightTextView);
		rightView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 数据是使用Intent返回,设置返回数据
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putByteArray(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CONTENT, content);
				intent.putExtras(bundle);
				ImageSendBrowerActivity.this.setResult(SystemConfig.ACTIVITY_RESULTCODE_DISTROY, intent);
				finish();
			}
		});

	}

}
