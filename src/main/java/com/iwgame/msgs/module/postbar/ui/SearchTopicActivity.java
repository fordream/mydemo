/**      
* SearchPostActivity.java Create on 2013-12-23     
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
import com.iwgame.msgs.common.BaseFragmentActivity;
import com.iwgame.msgs.config.SystemConfig;

/** 
 * @ClassName: SearchPostActivity 
 * @Description: TODO(搜索帖子页) 
 * @author chuanglong
 * @date 2013-12-23 下午3:03:05 
 * @Version 1.0
 * 
 */
public class SearchTopicActivity extends BaseFragmentActivity {
    private final static String TAG = "UserPostListActivity";
    private long gid = 0 ;

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
		 gid = tmpbundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID ,0) ;
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
	titleTxt.setText(R.string.postbar_searchpost_title);

	// 设置中间内容
	TopicListFragment postListFragment = new TopicListFragment();
	Bundle bundle = new Bundle();
	bundle.putInt(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETTYPE, SystemConfig.GETTOPICLIST_TARGETTYPE_GAMESEARCHTOPIC);
	bundle.putLong(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETID,gid);
	postListFragment.setArguments(bundle);
	FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
	fragmentTransaction.replace(R.id.contentView, postListFragment);
	// fragmentTransaction.addToBackStack(null);
	fragmentTransaction.commit();

    }
}
