/**      
 * MyFavoriteTopicActivity.java Create on 2013-12-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseFragmentActivity;
import com.iwgame.msgs.common.NoDataListener;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.youban.msgs.R;

/**
 * @ClassName: MyFavoriteTopicActivity
 * @Description: TODO(我的收藏帖子)
 * @author chuanglong
 * @date 2013-12-27 下午5:05:24
 * @Version 1.0
 * 
 */
public class MyFavoriteTopicActivity extends BaseFragmentActivity implements OnClickListener,NoDataListener {
    private final static String TAG = "MyFavoriteTopicActivity";

    private TextView topRightTextView;
    TopicListFragment topicListFragment;

    /**
     * 是否处于编辑状态
     */
    private boolean isEditStatus = false;
    private int textColor;
    
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle arg0) {
	// TODO Auto-generated method stub
	super.onCreate(arg0);
	init();
    }

    private void init() {

	// 设置显示top左边
	setLeftVisible(true);
	// 设置包显示top右边
	setRightVisible(true);

	topRightTextView = new TextView(this);
	topRightTextView.setText(R.string.postbar_myfavorite_edit_info);
	textColor = AdaptiveAppContext.getInstance().getAppConfig().getDisplay_title_textcolor();
	topRightTextView.setTextColor(textColor);
	LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
	rightView.addView(topRightTextView);
	rightView.setOnClickListener(this);

	// 设置TOP的中间布局容器
	LinearLayout topcenter = (LinearLayout) findViewById(R.id.center);
	titleTxt = (TextView) topcenter.findViewById(R.id.titleTxt);
	titleTxt.setText(R.string.postbar_myfavorite_title);

	// 设置中间内容
	topicListFragment = new TopicListFragment();
	Bundle bundle = new Bundle();
	bundle.putInt(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETTYPE, SystemConfig.GETTOPICLIST_TARGETTYPE_FAVORITE);
	bundle.putLong(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETID, SystemContext.getInstance().getExtUserVo().getUserid());
	topicListFragment.setArguments(bundle);
	FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
	fragmentTransaction.replace(R.id.contentView, topicListFragment);
	fragmentTransaction.commit();
	
	
	topicListFragment.setNoDataListener(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	if (v.getId() == R.id.rightView) {
	    if (isEditStatus) {
		isEditStatus = false;
		topicListFragment.setItemEditStatus(false);
		topRightTextView.setText(R.string.postbar_myfavorite_edit_info);
	    } else {
		isEditStatus = true;
		topicListFragment.setItemEditStatus(true);
		topRightTextView.setText(R.string.postbar_myfavorite_edit_ok);
	    }

	}

    }

    /* (non-Javadoc)
     * @see com.iwgame.msgs.common.NoDataListener#noDataNotify()
     */
    @Override
    public void noDataNotify() {
	// TODO Auto-generated method stub
	if(topRightTextView != null)
	{
	    topRightTextView.setVisibility(View.GONE);
	}
    }

}
