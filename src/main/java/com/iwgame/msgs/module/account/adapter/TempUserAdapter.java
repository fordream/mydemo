package com.iwgame.msgs.module.account.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youban.msgs.R;
import com.iwgame.msgs.vo.local.UserVo;

public class TempUserAdapter extends BaseAdapter {

	private List<UserVo> list;
	private Activity activity;
	public TempUserAdapter(List<UserVo> list,Activity activity)
	{
		this.list = list ;
		this.activity = activity;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view;
		if(convertView==null){
			view=activity.getLayoutInflater().inflate(R.layout.tempuser_item, null);
		}else{
			view=convertView;
		}
		final UserVo user=list.get(position);
		TextView tv_username = (TextView)view.findViewById(R.id.tv_username);
		tv_username.setText(user.getUsername());
		return view;
	}

}
