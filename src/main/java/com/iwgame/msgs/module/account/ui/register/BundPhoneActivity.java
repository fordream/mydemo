/**      
 * RegisterActivity.java Create on 2013-7-29     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.ui.register;

import android.content.Intent;
import android.os.Bundle;

import com.iwgame.msgs.common.BaseSuperActivity;

/**
 * @ClassName: BundPhoneActivity
 * @Description: 空白跳转页面
 * @author 徐亚辉
 * @date 2014-10-16 上午10:29:24
 * @Version 1.0
 * 
 */
public class BundPhoneActivity extends BaseSuperActivity  {
	private int startTag = -1;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(BundPhoneActivity.this, SetAccountActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("BundPhoneType", "BundPhone");
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(startTag == 1){
			this.finish(); 
		}
		startTag = 1;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0) {  
			this.finish();  
		}  
	}

}
