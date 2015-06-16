/**      
* SmileyPanel.java Create on 2013-12-6     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.widget.smiley;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.youban.msgs.R;
import com.iwgame.msgs.widget.DotView;

/** 
 * @ClassName: SmileyPanel 
 * @Description: TODO(表情的view) 
 * @author chuanglong
 * @date 2013-12-6 下午3:25:57 
 * @Version 1.0
 * 
 */
public class SmileyPanel extends LinearLayout {
    LayoutInflater inflater;
    Context context;
    ViewPager viewPager;
    DotView dotView ;
    String[] smileys ;
    int smiley_count ;
    int smiley_row ;
    int smiley_col ;
    ActionSmileyListener actionSmileyListener ;
    /**
     * @return the actionSmileyListener
     */
    public ActionSmileyListener getActionSmileyListener() {
        return actionSmileyListener;
    }

    /**
     * @param actionSmileyListener the actionSmileyListener to set
     */
    public void setActionSmileyListener(ActionSmileyListener actionSmileyListener) {
        this.actionSmileyListener = actionSmileyListener;
    }

    /**
     * @param context
     * @param attrs
     */
    public SmileyPanel(Context context, AttributeSet attrs) {
	super(context, attrs);
	// TODO Auto-generated constructor stub
	this.context = context;
	inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View mainview = (LinearLayout) inflater.inflate(R.layout.smiley_panel, null);
	addView(mainview);
	viewPager = (ViewPager) mainview.findViewById(R.id.smiley_panel_pager);
	dotView = (DotView) mainview.findViewById(R.id.smiley_panel_dot);
	
	smileys =(String[]) context.getResources().getStringArray(R.array.smiley_file);
	smiley_count = smileys.length;
	smiley_row =Integer.parseInt(context.getResources().getString(R.string.smiley_page_numRows));
	smiley_col = Integer.parseInt(context.getResources().getString(R.string.smiley_page_numColumns));
	setSmiley();
    }
    
    /**
     * 设置表情
     */
    private void setSmiley() {
	
	int smiley_pagercount = smiley_row * smiley_col;
	int smiley_pagertotalcount = smiley_count % (smiley_row * smiley_col - 1) == 0 ? smiley_count / (smiley_row * smiley_col -1) : smiley_count / (smiley_row * smiley_col -1) + 1;
	dotView.setDotCount(smiley_pagertotalcount);
	dotView.setSelectedDot(0);
	
	List<View> pageViews = new ArrayList<View>();
	for (int i = 0; i < smiley_pagertotalcount; i++) {
	    View pageView = inflater.inflate(R.layout.smiley_pager_item, null);
	    GridView gridView = (GridView) pageView.findViewById(R.id.smiley_pager_gridview);
	    List<Object> list = new ArrayList<Object>();
	    for (int j = i * (smiley_pagercount - 1); (j < (i + 1) * (smiley_pagercount - 1) && j < smiley_count); j++) {
		
		SmileyVo vo = new SmileyVo(SmileyVo.NAME_PREFIX + smileys[j]);
		list.add(vo);
	    }
	    list.add(new DelSmileyVo());
	    gridView.setAdapter(new SmileyPagerGridAdapter(context, list, i));

	    gridView.setOnItemClickListener(new GridView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		    View item = arg0.getChildAt(arg2);
		    Object obj = item.getTag();
		    if(obj instanceof SmileyVo)
		    {
			actionSmileyListener.onAddSmiley((SmileyVo)obj);
		    }
		    else if(obj instanceof DelSmileyVo)
		    {
			actionSmileyListener.onDelSmiley();
		    }
		}

	    });

	    pageViews.add(pageView);
	}

	viewPager.setAdapter(new SmileyPagerAdapter(context, pageViews));
	viewPager.setOnPageChangeListener(new ViewPageChangeListener());

    }
    
    public interface ActionSmileyListener {
	public void onAddSmiley(SmileyVo vo);
	public void onDelSmiley();
    }
    

    
    
    // 指引页面更改事件监听器
    class ViewPageChangeListener implements OnPageChangeListener {

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
	    dotView.setSelectedDot(arg0);
	}
    }


}
