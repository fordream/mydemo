/**      
* GameLableAdapter.java Create on 2013-10-23     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.group.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.iwgame.msgs.module.game.adapter.GameAdapter;

/** 
 * @ClassName: GameLableAdapter 
 * @Description: 公会选择贴吧标签适配器
 * @author 王卫
 * @date 2013-10-23 下午02:44:22 
 * @Version 1.0
 * 
 */
public class GameLableAdapter extends GameAdapter {

	private int selectItem = -1;
	
	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public GameLableAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see android.widget.SimpleAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView =  super.getView(position, convertView, parent);
		if(position == selectItem){
			convertView.setBackgroundColor(Color.BLUE);
		}else{
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		return convertView;
	}
	
	public void setSelectItem(int selectIndex){
		this.selectItem = selectIndex;
	}

}
