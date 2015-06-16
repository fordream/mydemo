/**      
* MorePopWindowAdapter.java Create on 2013-10-15     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.chat.adapter;

import java.util.ArrayList;

import java.util.HashMap;

import com.youban.msgs.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
* @ClassName: MorePopWindowAdapter 
* @Description: TODO(聊天more弹出窗口的adapter) 
* @author 吴禹青
* @date 2013-10-15 上午11:47:10 
* @Version 1.0
*
 */
public class MorePopWindowAdapter extends BaseAdapter {

	public static final String KEY_TITLE = "title";
	 private Context context;
	 private LayoutInflater inflater;
	 private ArrayList<HashMap<String,String>> data ;
	public MorePopWindowAdapter(Context context, ArrayList<HashMap<String,String>> data) {
		this.context = context;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView ;
		if(convertView ==null)
		{
			view = inflater.inflate(R.layout.common_popwindow_menu_item, null);	
		}
		TextView title = (TextView)view.findViewById(R.id.tv_popwindow_item_title);
		
	    HashMap<String,String> item = data.get(position);
	    title.setText(item.get(KEY_TITLE));
		return view ;
	}

}
