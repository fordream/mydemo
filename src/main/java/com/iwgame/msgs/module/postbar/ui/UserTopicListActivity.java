/**      
 * UserPostListActivity.java Create on 2013-12-23     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youban.msgs.R;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.BaseFragmentActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;

/**
 * @ClassName: UserPostListActivity
 * @Description: TODO(用户的帖子列表页)
 * @author chuanglong
 * @date 2013-12-23 下午2:15:47
 * @Version 1.0
 * 
 */
public class UserTopicListActivity extends BaseFragmentActivity {

    private final static String TAG = "UserPostListActivity";

    private long uid = 0;
    String title = "";

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle arg0) {
	// TODO Auto-generated method stub
	super.onCreate(arg0);
	// 获得传入的参数
	Intent tmpintent = this.getIntent();
	if (tmpintent != null) {
	    Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
	    if (tmpbundle != null) {
		 uid = tmpbundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID ,0) ;
		 title = tmpbundle.getString(BaseActivity.TITLE);
	    }
	}
	init();
    }

    private void init() {

	// 设置显示top左边
	setLeftVisible(true);
	// 设置包显示top右边
	setRightVisible(false);

	// 设置TOP的中间布局容器
	LinearLayout topcenter = (LinearLayout) findViewById(R.id.center);
	titleTxt = (TextView) topcenter.findViewById(R.id.titleTxt);

	if(uid == SystemContext.getInstance().getExtUserVo().getUserid())
	{
	    titleTxt.setText(R.string.postbar_userpostlist_my_title);  
	}
	else
	{
	    titleTxt.setText(title);
	}
	

	// 设置中间内容
	TopicListFragment topicListFragment = new TopicListFragment();
	Bundle bundle = new Bundle();
	bundle.putInt(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETTYPE, SystemConfig.GETTOPICLIST_TARGETTYPE_USER);
	bundle.putLong(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETID, uid);
        topicListFragment.setArguments(bundle);
	FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
	fragmentTransaction.replace(R.id.contentView, topicListFragment);
	fragmentTransaction.commit();

    }

}
