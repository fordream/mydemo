/**      
 * LatestContactsAdapter.java Create on 2014-4-1     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.chat.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.youban.msgs.R;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.chat.vo.SubjectVo;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.PinyinUtil;

/**
 * @ClassName: LatestContactsAdapter
 * @Description: TODO(最近联系人适配器)
 * @author chuanglong
 * @date 2014-4-1 下午4:55:10
 * @Version 1.0
 * 
 */
public class SubjectAdapter extends BaseAdapter implements SectionIndexer {
	final static String TAG = "SubjectAdapter";
	Context context;
	List<SubjectVo> data;
	LayoutInflater inflater;
	// key 由type_id组成
	private Map<String, Object> subjectObject_cache = new HashMap<String, Object>();

	public SubjectAdapter(Context context, List<SubjectVo> data) {
		this.context = context;
		this.data = data;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

		ViewHolder tmpViewHolder = null;
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.subject_list_item, null);
			tmpViewHolder = new ViewHolder();
			tmpViewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			tmpViewHolder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(tmpViewHolder);
		} else {
			tmpViewHolder = (ViewHolder) convertView.getTag();
		}
		final ViewHolder viewHolder = tmpViewHolder;

		final SubjectVo vo = data.get(position);
		final String cachekey = String.valueOf(vo.getSubjectType()) + "_" + String.valueOf(vo.getSubjectId());
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();

		if(vo.getSubjectType() == MsgsConstants.OT_USER){
			if (subjectObject_cache.containsKey(cachekey)||vo.getSubject() != null) {
				// 有cache
				if(subjectObject_cache.containsKey(cachekey))
					vo.setSubject(subjectObject_cache.get(cachekey));
				else
					subjectObject_cache.put(cachekey, vo.getSubject());

				if (vo.getSubjectType() == MsgsConstants.OT_USER) {
					UserVo result = (UserVo) vo.getSubject();
					new ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), 0, viewHolder.icon, R.drawable.common_user_icon_default);
					viewHolder.title.setText(result.getUsername());
				} else if (vo.getSubjectType() == MsgsConstants.OT_GROUP) {
					GroupVo result = (GroupVo) vo.getSubject();
					new ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), 0, viewHolder.icon, R.drawable.common_user_icon_default);
					viewHolder.title.setText(String.format(context.getString(R.string.chat_forwarding_content_title), result.getName(), result.getTotal()));

				}
			}else{

				// 获得用户信息
				ProxyCallBack<List<UserVo>> callback = new ProxyCallBack<List<UserVo>>() {

					@Override
					public void onSuccess(List<UserVo> uservoresult) {
						if(uservoresult != null && uservoresult.size() > 0){
							UserVo result = uservoresult.get(0);
							if (result != null) {
								new ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), 0, viewHolder.icon, R.drawable.common_user_icon_default);
								if(result != null && result.getRemarksName() != null && !"".equals(result.getRemarksName())){
									viewHolder.title.setText(result.getRemarksName());
								}else{
									viewHolder.title.setText(result.getUsername());
								}
								subjectObject_cache.put(cachekey, result);
								vo.setSubject(result);
							}
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub
						LogUtil.e(TAG, "获得用户信息异常：" + result);
						// ErrorCodeUtil.handleErrorCode(context, result);
						new ImageLoader().loadRes("drawable://" + R.drawable.common_user_icon_default, 0, viewHolder.icon, R.drawable.common_user_icon_default);
						viewHolder.title.setText("");
					}
				};
				UserDao userdao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
				UserVo userVo = userdao.getUserById(vo.getSubjectId());
				p.clear();
				cdp.clear();
				cdp.setId(vo.getSubjectId());
				cdp.setUtime(userVo == null ? 0 : userVo.getUpdatetime());
				p.addParam(cdp.build());
				ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback, context,p.build(), 0, null);

			}
		}else if(vo.getSubjectType() == MsgsConstants.OT_GROUP){
			if (subjectObject_cache.containsKey(cachekey)||(vo.getSubject() != null && ((GroupVo)vo.getSubject()).getName() != null && !"".equals(((GroupVo)vo.getSubject()).getName()) && ((GroupVo)vo.getSubject()).getMaxcount() > 0)) {
				// 有cache
				if(subjectObject_cache.containsKey(cachekey))
					vo.setSubject(subjectObject_cache.get(cachekey));
				else
					subjectObject_cache.put(cachekey, vo.getSubject());

				if (vo.getSubjectType() == MsgsConstants.OT_USER) {
					UserVo result = (UserVo) vo.getSubject();
					new ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), 0, viewHolder.icon, R.drawable.common_user_icon_default);
					viewHolder.title.setText(result.getUsername());
				} else if (vo.getSubjectType() == MsgsConstants.OT_GROUP) {
					GroupVo result = (GroupVo) vo.getSubject();
					new ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), 0, viewHolder.icon, R.drawable.common_user_icon_default);
					viewHolder.title.setText(String.format(context.getString(R.string.chat_forwarding_content_title), result.getName(), result.getTotal()));

				}
			}else{

				// 获得公会
				ProxyCallBack<List<GroupVo>> callback = new ProxyCallBack<List<GroupVo>>() {

					@Override
					public void onSuccess(List<GroupVo> groupVoresult) {
						if(groupVoresult != null && groupVoresult.size() > 0){
							GroupVo result = groupVoresult.get(0);
							if (result != null) {
								new ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), 0, viewHolder.icon, R.drawable.common_user_icon_default);
								viewHolder.title.setText(String.format(context.getString(R.string.chat_forwarding_content_title), result.getName(), result.getTotal()));
								subjectObject_cache.put(cachekey, result);
								vo.setSubject(result);
							}
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub
						LogUtil.e(TAG, "获得公会信息异常：" + result);
						// ErrorCodeUtil.handleErrorCode(context, result);
						new ImageLoader().loadRes("drawable://" + R.drawable.common_user_icon_default, 0, viewHolder.icon, R.drawable.common_user_icon_default);
						viewHolder.title.setText("");
					}

				};
				GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
				GroupVo groupVo = groupDao.findGroupByGrid(vo.getSubjectId());
				p.clear();
				cdp.clear();
				cdp.setId(vo.getSubjectId());
				cdp.setUtime(groupVo == null ? 0 : groupVo.getUtime());
				p.addParam(cdp.build());
				ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(callback, context, p.build(), MsgsConstants.OT_GROUP, null);

			}
		}
		return convertView;
	}

	class ViewHolder {
		ImageView icon;
		TextView title;
	}

	/* (non-Javadoc)
	 * @see android.widget.SectionIndexer#getPositionForSection(int)
	 */
	@Override
	public int getPositionForSection(int sectionIndex) {
		// TODO Auto-generated method stub
		if (sectionIndex == '!') {
			return 0;
		} else {
			for (int i = 0; i < getCount(); i++) {

				final SubjectVo vo = data.get(i);
				if(vo.getSubject()!= null && vo.getSubject() instanceof UserVo)
				{
					String tmp = ((UserVo)vo.getSubject()).getUsername();
					if( tmp != null )
					{
						String headChar = PinyinUtil.getPinYinHeadChar(tmp);
						char firstChar = headChar.toUpperCase().charAt(0);
						if (firstChar == sectionIndex) {
							return i;
						}
					}

				}
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see android.widget.SectionIndexer#getSectionForPosition(int)
	 */
	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.SectionIndexer#getSections()
	 */
	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

}
