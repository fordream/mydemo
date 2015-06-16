/**      
 * TopicListActivity.java Create on 2015-3-8     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseFragmentActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.youban.msgs.R;

/**
 * @ClassName: TopicListActivity
 * @Description: 题吧列表页面（新闻、攻略等）
 * @author 王卫
 * @date 2015-3-8 下午4:53:06
 * @Version 1.0
 * 
 */
public class TopicListActivity extends BaseFragmentActivity{

	public static int NEWS = 1;
	public static int RAIDES = 2;
	
	private int mode;
	private long targetId;
	private int tagid;
	private String tagname;
	
	private TopicListFragment topicListFragment;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.common.BaseFragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mode = getIntent().getExtras().getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE);
		targetId = getIntent().getExtras().getLong(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETID);
		tagid = getIntent().getExtras().getInt(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TAGID);
		tagname = getIntent().getExtras().getString(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TAGNAME);
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
		if (mode == NEWS) {
			titleTxt.setText(R.string.postbar_news_title);
		} else if (mode == RAIDES) {
			titleTxt.setText(R.string.postbar_raiders_title);
		}

		// 设置中间内容
		topicListFragment = new TopicListFragment();
		Bundle bundle = new Bundle();
		if(mode == NEWS)
			bundle.putInt(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETTYPE, SystemConfig.GETTOPICLIST_TARGETTYPE_NEWS);
		else if(mode == RAIDES)
			bundle.putInt(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETTYPE, SystemConfig.GETTOPICLIST_TARGETTYPE_RAIDERS);
		bundle.putLong(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETID, targetId);
		bundle.putInt(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TAGID, tagid);
		bundle.putString(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TAGNAME, tagname);
		topicListFragment.setArguments(bundle);
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.contentView, topicListFragment);
		fragmentTransaction.commit();
	}

}
