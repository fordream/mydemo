/**      
* PhotoActivity.java Create on 2014-7-9     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs;

import android.os.Bundle;

import com.iwgame.msgs.common.BaseSuperActivity;
import com.umeng.analytics.MobclickAgent;

/** 
 * @ClassName: PhotoActivity 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2014-7-9 下午4:54:58 
 * @Version 1.0
 * 
 */
public class PhotoActivity extends BaseSuperActivity {
	private static final String TAG = "PhotoActivity";

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	MobclickAgent.onPageStart(TAG);
    	MobclickAgent.onResume(this);
    	
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	MobclickAgent.onPageEnd(TAG);
    	MobclickAgent.onPause(this);
    }
	
}
