package com.iwgame.msgs.module.user.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.module.user.ui.ExtraGameVo;
import com.iwgame.msgs.utils.BlackWhiteFilter;
import com.iwgame.msgs.utils.Image;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.widget.roundedimageview.RoundedImageView;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

public class UserGameAdapter extends BaseAdapter {
	private Context context;
	private List<ExtraGameVo> list = new ArrayList<ExtraGameVo>();
	private LayoutInflater inflater;
	public UserGameAdapter(Context context,List<ExtraGameVo> list){
		this.context = context;
		this.list = list;
		LogUtil.d("gameadapter", "adapter"+list.size());
		inflater = LayoutInflater.from(context);
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder ;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.user_game_item, null);
			holder.gameIcon = (RoundedImageView)convertView.findViewById(R.id.game_icon_image);
			holder.gameName =(TextView)convertView.findViewById(R.id.game_name);
			holder.tuijian = (ImageView)convertView.findViewById(R.id.tui_jian_icon);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
			
		}
		ExtraGameVo vo = list.get(position);
		LogUtil.d("gameadapter", vo.getIcon());
		holder.gameIcon.setImageResource(R.drawable.postbar_thumbimg_default);
		if(vo.getPriority()>=3&&vo.getPriority()<=6){
			holder.tuijian.setVisibility(View.VISIBLE);
		}else{
			holder.tuijian.setVisibility(View.GONE);
		}
		if(vo.getPriority()==2){
			new ImageLoader().loadRes(ResUtil.getOriginalRelUrl(vo.getIcon()), 0, holder.gameIcon,R.drawable.postbar_thumbimg_default, true, true, false);
			holder.gameName.setTextColor(context.getResources().getColor(R.color.game_list_name));
		}else{
		new ImageLoader().loadRes(ResUtil.getOriginalRelUrl(vo.getIcon()), 0, holder.gameIcon,
				R.drawable.postbar_thumbimg_default,  true, false, false);
		holder.gameName.setTextColor(context.getResources().getColor(R.color.black));
		}
		holder.gameName.setText(vo.getgName());
		return convertView;
	}
	
	class ViewHolder{
		RoundedImageView gameIcon;
		TextView   gameName;
		ImageView tuijian;
	}
	

		
	
	
	

}
	