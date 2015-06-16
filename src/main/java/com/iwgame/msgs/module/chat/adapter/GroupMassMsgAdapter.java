/**      
 * ChatMessageAdapter.java Create on 2013-10-28     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.chat.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.chat.adapter.ChatMessageAdapter.SystemViewHolder;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.smiley.SmileyUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.TextUtil;
import com.youban.msgs.R;

/**
 * @ClassName: ChatMessageAdapter
 * @Description: TODO(...)
 * @author Administrator
 * @date 2013-10-28 下午1:59:48
 * @Version 1.0
 * 
 */
public class GroupMassMsgAdapter extends BaseAdapter {

	private final String TAG = "GroupMassMsgAdapter";
	private Context context;
	private LayoutInflater inflater;
	private List<MessageVo> data;
	private long groupId;
	private UserVo loginUserVo = null;

	public GroupMassMsgAdapter(Context context, List<MessageVo> data, long groupId) {
		this.context = context;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
		this.groupId = groupId;
		loginUserVo = SystemContext.getInstance().getExtUserVo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return data.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int type = getItemViewType(position);
		UserViewHolder tmp_userViewHolder = null;
		SystemViewHolder tmp_systemViewHolder = null;
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.chatmsg_in_user_item, null);
			tmp_userViewHolder = new UserViewHolder();
			tmp_userViewHolder.item_chat_headimg = (ImageView) convertView.findViewById(R.id.icon);
			tmp_userViewHolder.tv_username = (TextView) convertView.findViewById(R.id.item_chat_username);
			tmp_userViewHolder.tv_date = (TextView) convertView.findViewById(R.id.item_chat_date);
			tmp_userViewHolder.tv_msg_text = (TextView) convertView.findViewById(R.id.item_chat_msg_text);
			tmp_userViewHolder.tv_msg_img = (ImageView) convertView.findViewById(R.id.item_chat_msg_img);
			convertView.setTag(tmp_userViewHolder);

		} else {

			tmp_userViewHolder = (UserViewHolder) convertView.getTag();
		}
		final MessageVo vo = data.get(position);
		final UserViewHolder userViewHolder = tmp_userViewHolder;

		// 设置时间
		userViewHolder.tv_date.setText(SafeUtils.getFormatDate(vo.getCreateTime(), "MM-dd HH:mm:ss"));

		userViewHolder.tv_username.setText("");
		// 用户头像

		new ImageLoader().loadRes(ResUtil.getSmallRelUrl( loginUserVo.getAvatar()),ImageLoader.RADIUS_DEFAULT_PX6, userViewHolder.item_chat_headimg,R.drawable.common_user_icon_default);

		// 设置内容
		userViewHolder.tv_msg_text.setVisibility(View.VISIBLE);
		userViewHolder.tv_msg_img.setVisibility(View.GONE);
		Paint paint = new Paint();
		paint.setTextSize(userViewHolder.tv_msg_text.getTextSize());
		paint.setTextScaleX(userViewHolder.tv_msg_text.getTextScaleX());
		userViewHolder.tv_msg_text.setText(SmileyUtil.ReplaceSmiley(context, vo.getSummary(), (int) TextUtil.getTextHeight(paint), (int) TextUtil.getTextHeight(paint)));

		return convertView;
	}

	class UserViewHolder {
		ImageView item_chat_headimg;
		TextView tv_username;
		TextView tv_date;
		TextView tv_msg_text;
		ImageView tv_msg_img;
	}

}
