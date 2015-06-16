/**      
* MyTabsAdapter.java Create on 2014-3-31     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.chat.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;


/** 
 * @ClassName: MyTabsAdapter 
 * @Description: TODO(...) 
 * @author chuanglong
 * @date 2014-3-31 下午2:04:53 
 * @Version 1.0
 * 
 */


public class MyTabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
	private final Context mContext;
	private final TabHost mTabHost;
	private final ViewPager mViewPager;
	private View mMenu;
	private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
	private final Map<Integer, Fragment> mFragments = new HashMap<Integer, Fragment>();

	static final class TabInfo {
	    private final String tag;
	    private final Class<?> clss;
	    private final Bundle args;

	    TabInfo(String _tag, Class<?> _class, Bundle _args) {
		tag = _tag;
		clss = _class;
		args = _args;
	    }
	}

	static class DummyTabFactory implements TabHost.TabContentFactory {
	    private final Context mContext;

	    public DummyTabFactory(Context context) {
		mContext = context;
	    }

	    @Override
	    public View createTabContent(String tag) {
		View v = new View(mContext);
		v.setMinimumWidth(0);
		v.setMinimumHeight(0);
		return v;
	    }
	}

	public MyTabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager, View menu) {
	    super(activity.getSupportFragmentManager());
	    mContext = activity;
	    mTabHost = tabHost;
	    mViewPager = pager;
	    mMenu = menu;
	    mTabHost.setOnTabChangedListener(this);
	    mViewPager.setAdapter(this);
	    mViewPager.setOnPageChangeListener(this);
	}

	public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
	    tabSpec.setContent(new DummyTabFactory(mContext));
	    String tag = tabSpec.getTag();

	    TabInfo info = new TabInfo(tag, clss, args);
	    mTabs.add(info);
	    mTabHost.addTab(tabSpec);
	    notifyDataSetChanged();
	}

	@Override
	public int getCount() {
	    return mTabs.size();
	}

	@Override
	public Fragment getItem(int position) {
		Fragment f;
	    if(!mFragments.containsKey(position)){
	    	TabInfo info = mTabs.get(position);
		    f = Fragment.instantiate(mContext, info.clss.getName(), info.args);
	    	mFragments.put(position, f);
	    }else{
	    	f = mFragments.get(position);
	    }
	    return f;
	}
	
	public Fragment getItemByPosition(int position){
		int size = mFragments.size();
		if(mFragments != null && size > 0 && position < size)
			return mFragments.get(position);
		else
			return null;
	}

	@Override
	public void onTabChanged(String tabId) {
	    int position = mTabHost.getCurrentTab();
	    mViewPager.setCurrentItem(position);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
	    // Unfortunately when TabHost changes the current tab, it kindly
	    // also takes care of putting focus on it when not in touch mode.
	    // The jerk.
	    // This hack tries to prevent this from pulling focus out of our
	    // ViewPager.
	    TabWidget widget = mTabHost.getTabWidget();
	    int oldFocusability = widget.getDescendantFocusability();
	    widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
	    mTabHost.setCurrentTab(position);
	    widget.setDescendantFocusability(oldFocusability);

	    // change menu state
	    // if (position != 0) {
	    // mMenu.setBackgroundResource(R.drawable.game_detail_menu_recommend_btn);
	    // } else {
	    // mMenu.setBackgroundResource(R.drawable.game_detail_menu_pubnews_btn);
	    // }
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}
}
