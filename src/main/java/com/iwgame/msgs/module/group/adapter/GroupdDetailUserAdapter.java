/**      
 * RegiestRecommendGameAdapter.java Create on 2014-4-30     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.vo.local.UserVo;
import com.youban.msgs.R;

/**
 * @ClassName: GroupdDetailUserAdapter
 * @Description: 公会成员适配器
 * @author 王卫
 * @date 2014-4-30 下午4:06:44
 * @Version 1.0
 * 
 */
public class GroupdDetailUserAdapter extends BaseAdapter {

	private List<UserItemObj> mData;
	private final String TAG = "GroupdDetailUserAdapter";
	private Context mContext;
	private LayoutInflater mInflater;
	
	/**
	 * 
	 */
	public GroupdDetailUserAdapter(Context context, List<UserItemObj> data) {
		mData = data;
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if (mData != null)
			return mData.size();
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		if (mData != null)
			return mData.get(position);
		return null;
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.group_detail_user_gridview_item, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position != 0 && position%8 == 0){
			holder.icon.setImageResource(R.drawable.common_user_icon_default);
		}
		UserItemObj obj = (UserItemObj) getItem(position);
		renderUI(holder, obj);
		return convertView;
	}

	/**
	 * 
	 * @param holder
	 * @param uvo
	 */
	private void renderUI(final ViewHolder holder,final UserItemObj gvo) {
		UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
		UserVo uservo = dao.getUserById(gvo.getUid());
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(gvo.getUid());
		cdp.setUtime(uservo == null ? 0 : uservo.getUpdatetime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(new ProxyCallBack<List<UserVo>>() {

			@Override
			public void onSuccess(List<UserVo> uservoresult) {
				if(uservoresult == null || uservoresult.size() <= 0) return;
				UserVo result = uservoresult.get(0);
				if (result != null && result.getAvatar() != null) {
					ImageViewUtil.showImage(holder.icon, result.getAvatar(), R.drawable.common_user_icon_default);
				} else {
					ImageViewUtil.showImage(holder.icon, null, R.drawable.common_user_icon_default);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ImageViewUtil.showImage(holder.icon, null, R.drawable.common_user_icon_default);
			}
		}, mContext, p.build(), 0, null);
	}

	public List<UserItemObj> getData() {
		return mData;
	}

	static public class ViewHolder {
		public ImageView icon;
	}

}
