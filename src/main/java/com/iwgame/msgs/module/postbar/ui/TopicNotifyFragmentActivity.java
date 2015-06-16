/**      
 * TopicNotifiFragmentActivity.java Create on 2015-3-15     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseSuperFragmentActivity;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.chat.adapter.MyTabsAdapter;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.UserVo;
import com.youban.msgs.R;

/**
 * @ClassName: TopicNotifiFragmentActivity
 * @Description: 帖子通知
 * @author 王卫
 * @date 2015-3-15 下午2:23:58
 * @Version 1.0
 * 
 */
public class TopicNotifyFragmentActivity extends BaseSuperFragmentActivity {

	private int selectIndex;

	// 定义TabHost对象
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private MyTabsAdapter mTabsAdapter;
	// 定义数组来存放Fragment界面
	private Class mFragmentArray[] = null;
	// Tab选项卡的文字
	private String mTabTextArray[] = null;
	private LayoutInflater layoutInflater;

	private boolean hasPraise;
	private boolean hasReply;
	private ImageView tagView;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// 获取是否有回复我的和赞的未读通知消息
		UserVo loginUserVo = SystemContext.getInstance().getExtUserVo();
		if (loginUserVo != null) {
			hasPraise = SystemContext.getInstance().getHasUnreadMessage(MsgsConstants.MC_NOTIFY, loginUserVo.getUserid(), MsgsConstants.DOMAIN_USER,
					MsgsConstants.MCC_COMMENT, SystemContext.HAS_UNREAD_MESSAGE_TYPE_PRAISE);
			hasReply = SystemContext.getInstance().getHasUnreadMessage(MsgsConstants.MC_NOTIFY, loginUserVo.getUserid(), MsgsConstants.DOMAIN_USER,
					MsgsConstants.MCC_COMMENT, SystemContext.HAS_UNREAD_MESSAGE_TYPE_REPLY);
			if (hasPraise && !hasReply) {
				selectIndex = 1;
			} else {
				selectIndex = 0;
			}
			setContentView(R.layout.common_content);
			layoutInflater = LayoutInflater.from(this);
			init();
			SystemContext.getInstance().setHasUnreadMessage(MsgsConstants.MC_NOTIFY, loginUserVo.getUserid(), MsgsConstants.DOMAIN_USER,
					MsgsConstants.MCC_COMMENT, SystemContext.HAS_UNREAD_MESSAGE_TYPE_PRAISE, false);
			SystemContext.getInstance().setHasUnreadMessage(MsgsConstants.MC_NOTIFY, loginUserVo.getUserid(), MsgsConstants.DOMAIN_USER,
					MsgsConstants.MCC_COMMENT, SystemContext.HAS_UNREAD_MESSAGE_TYPE_REPLY, false);
		} else {
			finish();
		}
	}

	private void init() {
		// 定义数组来存放Fragment界面
		mFragmentArray = new Class[] { ReplyMyFragment.class, PraiseTopicFragment.class };
		// // 定义数组来存放按钮图片
		// Tab选项卡的文字
		mTabTextArray = new String[] { getString(R.string.postbar_notify_tab_name_replyme), getString(R.string.postbar_notify_tab_name_praise) };
		// 设置Top左边返回功能
		Button backBtn = (Button) findViewById(R.id.leftBtn);
		if (backBtn != null) {
			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					TopicNotifyFragmentActivity.this.finish();
				}
			});
		}
		// 设置TOP的中间布局容器
		LinearLayout topcenter = (LinearLayout) findViewById(R.id.center);
		((TextView) topcenter.findViewById(R.id.titleTxt)).setText(R.string.postbar_replymy_activity_title);
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View view = (LinearLayout) View.inflate(this, R.layout.topic_notify_main, null);
		contentView.addView(view);
		// 实例化TabHost对象，得到TabHost
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabsAdapter = new MyTabsAdapter(this, mTabHost, mViewPager, null);
		// 得到fragment的个数
		int count = mFragmentArray.length;
		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTabTextArray[i]).setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			Bundle bundle = null;
			bundle = new Bundle();
			mTabsAdapter.addTab(tabSpec, mFragmentArray[i], bundle);
		}

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mTabsAdapter.onPageSelected(arg0);
				if (arg0 == 1 && tagView != null) {
					tagView.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		mViewPager.setCurrentItem(selectIndex);
		if (selectIndex == 0 && tagView != null && hasPraise) {
			tagView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 根据索引值获取界面
	 * 
	 * @param index
	 * @return
	 */
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.common_sub_tab_item_view, null);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTabTextArray[index]);
		if (index == 1) {// 赞
			tagView = (ImageView) view.findViewById(R.id.tag);
		}
		return view;
	}

}
