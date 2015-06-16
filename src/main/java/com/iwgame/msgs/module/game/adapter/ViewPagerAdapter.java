/**      
 * ViewPagerAdapter.java Create on 2013-11-19     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @ClassName: ViewPagerAdapter
 * @Description: 用于主界面的划屏适配器
 * @author 王卫
 * @date 2013-11-19 下午4:53:13
 * @Version 1.0
 * 
 */
public class ViewPagerAdapter extends PagerAdapter {

	// 页面集合
	private List<View> pageViews = null;

	/**
	 * 
	 */
	public ViewPagerAdapter(List<View> views) {
		this.pageViews = views;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		if (pageViews != null)
			return pageViews.size();
		else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.view.PagerAdapter#isViewFromObject(android.view.View,
	 * java.lang.Object)
	 */
	@Override
	public boolean isViewFromObject(View paramView, Object paramObject) {
		return paramView == paramObject;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(pageViews.get(arg1));
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(pageViews.get(arg1));
		return pageViews.get(arg1);
	}

}
