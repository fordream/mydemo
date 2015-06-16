/**      
 * ShareTransitActivity.java Create on 2015-3-24     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.module.postbar.ui.TopicDetailActivity;
import com.iwgame.msgs.vo.local.UserVo;

/**
 * @ClassName: ShareTransitActivity
 * @Description: 分享到微信后跳转到相应的分享界面的中转界面
 * @author 王卫
 * @date 2015-3-24 上午10:38:57
 * @Version 1.0
 * 
 */
public class ShareTransitActivity extends BaseSuperActivity {

	public static final String TID = "tid";
	public static final String GID = "gid";
	public static final String TYPE = "type";
	
	public static String tid;
	public static String gid;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UserVo loginUser = SystemContext.getInstance().getExtUserVo();
		if (loginUser != null) {
			Intent tIntent = this.getIntent();
			if (tIntent != null) {
				String tSchema = tIntent.getScheme();
				if (tSchema != null) {
					Uri myURI = tIntent.getData();
					if (myURI != null) {
						tid = myURI.getQueryParameter(TID);
						gid = myURI.getQueryParameter(GID);
						// String type = myURI.getQueryParameter(TYPE);
						Intent intent = new Intent(ShareTransitActivity.this, MainFragmentActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						Bundle bundle = new Bundle();
						bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			}
		} else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		finish();
	}

	/**
	 * 
	 * @param tid
	 */
	private void jumpTopicDetail(long tid, long gid) {
		Intent intent = new Intent(this, TopicDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, tid);
		bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gid);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		startActivity(intent);
		finish();
	}

}
