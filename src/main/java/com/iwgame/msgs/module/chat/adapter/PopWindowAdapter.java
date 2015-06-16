/**      
* GameAdapter.java Create on 2013-9-3     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.chat.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youban.msgs.R;


public class PopWindowAdapter extends BaseAdapter {

	public static final String KEY_TITLE = "title";
	public static final String KEY_RESID = "resid";
	 private Context context;
	 private LayoutInflater inflater;
	 private ArrayList<HashMap<String,String>> data ;
	public PopWindowAdapter(Context context, ArrayList<HashMap<String,String>> data) {
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
			view = inflater.inflate(R.layout.fragment_news_menu_popwindow_item, null);	
		}
		TextView title = (TextView)view.findViewById(R.id.iv_news_popwindow_item_title);
		ImageView icon =(ImageView)view.findViewById(R.id.iv_news_popwindow_item_icon);
	    HashMap<String,String> item = data.get(position);
	    title.setText(item.get(KEY_TITLE));
	    icon.setBackgroundResource(Integer.parseInt(item.get(KEY_RESID)));
		return view ;
	}

}
