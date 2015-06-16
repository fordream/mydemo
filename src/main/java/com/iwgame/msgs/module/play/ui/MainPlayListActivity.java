/**      
 * MainPlayListActivity.java Create on 2015-5-8     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseSuperFragmentActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.chat.adapter.MyTabsAdapter;
import com.iwgame.msgs.module.chat.ui.ParentViewPager;
import com.youban.msgs.R;

/**
 * @ClassName: MainPlayListActivity
 * @Description: 我的陪玩列表
 * @author 王卫
 * @date 2015-5-8 下午3:32:16
 * @Version 1.0
 * 
 */
public class MainPlayListActivity extends BaseSuperFragmentActivity {

	// 定义数组来存放Fragment界面
	private Class mFragmentArray[] = null;
	// Tab选项卡的文字
	private String mTabTextArray[] = null;
	// 定义TabHost对象
	private TabHost mTabHost;
	private ParentViewPager mViewPager;
	private MyTabsAdapter mTabsAdapter;

	private LayoutInflater layoutInflater;
	private int index;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
			index = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_INDEX);
		// 实例化布局对象
		layoutInflater = LayoutInflater.from(this);
		init();
	}

	/**
	 * 
	 */
	private void init() {
		setContentView(R.layout.common_content);
		// 设置Top中间标题
		((TextView) findViewById(R.id.titleTxt)).setText(getResources().getString(R.string.play_main_title));
		// 定义数组来存放Fragment界面
		mFragmentArray = new Class[] { CreatListFragment.class, ApplyListFragment.class };
		// Tab选项卡的文字
		mTabTextArray = new String[] { getString(R.string.play_main_list_tab_created), getString(R.string.play_main_list_tab_apply) };
		// 设置Top左边返回功能
		Button backBtn = (Button) findViewById(R.id.leftBtn);
		if (backBtn != null) {
			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MainPlayListActivity.this.finish();
				}
			});
		}
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		contentView.addView(View.inflate(this, R.layout.play_list_main, null));
		// 实例化TabHost对象，得到TabHost
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ParentViewPager) findViewById(R.id.pager);
		mTabsAdapter = new MyTabsAdapter(this, mTabHost, mViewPager, null);
		// 得到fragment的个数
		int count = mFragmentArray.length;
		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTabTextArray[i]).setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			Bundle bundle = new Bundle();
			mTabsAdapter.addTab(tabSpec, mFragmentArray[i], bundle);
		}
		mTabHost.setCurrentTabByTag(mTabTextArray[0]);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int arg0) {
				mTabsAdapter.onPageSelected(arg0);
			}

		});
		if (index <= 0)
			return;
		mViewPager.setCurrentItem(index);
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
		return view;
	}

}
