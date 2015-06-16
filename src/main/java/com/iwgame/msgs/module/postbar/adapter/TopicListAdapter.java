/**      
 * PostListAdapter.java Create on 2013-12-23     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PostElementType;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.utils.SafeUtils;
import com.youban.msgs.R;

/**
 * @ClassName: PostListAdapter
 * @Description: TODO(帖子列表适配器)
 * @author chuanglong
 * @date 2013-12-23 下午12:35:09
 * @Version 1.0
 * 
 */
public class TopicListAdapter extends BaseAdapter {

	protected static final String TAG = "PostListAdapter";
	private Context context;
	private List<Msgs.PostbarTopicDetail> data;
	private LayoutInflater inflater;
	private int targettype = 0;

	public static int ACTION_DEL = 1 ;
	public interface ItemClickListener{
		public void onClickAction(int position,int action);
	}
	private ItemClickListener itemClickListener = null;

	private int ordertype = MsgsConstants.POSTBAR_ORDER_REPLY_TIME;

	private boolean editStatus;

	public TopicListAdapter(Context context, List<Msgs.PostbarTopicDetail> data, int targettype,ItemClickListener listener) {
		this.context = context;
		this.data = data;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.targettype = targettype;
		itemClickListener = listener;

	}

	/**
	 * 设置编辑状态， true 处于编辑中（删除按钮显示），false 处于一般状态 （删除按钮不显示）
	 * 
	 * @param status
	 */
	public void setEditStatus(boolean status) {
		editStatus = status;
	}

	/**
	 * 设置排序方式
	 * @param order
	 */
	public void setOrderType(int order)
	{
		ordertype = order ;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder tmpViewHolder = new ViewHolder();
		if (convertView == null) {

			convertView = this.inflater.inflate(R.layout.postbar_topic_list_item, null);
			tmpViewHolder.postbar_topic_list_item_isnotice = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isnotice);
			tmpViewHolder.postbar_topic_list_item_isessence = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isessence);
			tmpViewHolder.postbar_topic_list_item_istop = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_istop);
			tmpViewHolder.postbar_topic_list_item_ishot = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_ishot);
			tmpViewHolder.postbar_topic_list_item_isnews = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isnews);
			tmpViewHolder.postbar_topic_list_item_isji = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isji);
			tmpViewHolder.postbar_topic_list_item_islock = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_islock);
			tmpViewHolder.postbar_topic_list_item_title = (TextView) convertView.findViewById(R.id.postbar_topic_list_item_title);
			tmpViewHolder.postbar_topic_list_item_nickname = (TextView) convertView.findViewById(R.id.postbar_topic_list_item_nickname);
			tmpViewHolder.postbar_topic_list_item_date = (TextView) convertView.findViewById(R.id.postbar_topic_list_item_date);
			tmpViewHolder.postbar_topic_list_item_ishavepic = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_ishavepic);
			tmpViewHolder.postbar_topic_list_item_comments = (TextView) convertView.findViewById(R.id.postbar_topic_list_item_comments);
			tmpViewHolder.postbar_topic_list_item_visits = (TextView) convertView.findViewById(R.id.postbar_topic_list_item_visits);
			tmpViewHolder.postbar_topic_list_item_del = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_del);
			tmpViewHolder.postbar_topic_list_item_loc =  (TextView) convertView.findViewById(R.id.postbar_topic_list_item_loc);
			tmpViewHolder.postbar_topic_list_item_title_gap =  (View) convertView.findViewById(R.id.postbar_topic_list_item_title_gap);

			tmpViewHolder.tmp_fg_1 =  (View) convertView.findViewById(R.id.tmp_fg_1);
			tmpViewHolder.tmp_fg_2 =  (View) convertView.findViewById(R.id.tmp_fg_2);
			tmpViewHolder.postbar_topic_list_item_showorhide_ll =  (LinearLayout) convertView.findViewById(R.id.postbar_topic_list_item_showorhide_ll);
			tmpViewHolder.postbar_topic_list_item_master =  (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_master);
			convertView.setTag(tmpViewHolder);
		} else {
			tmpViewHolder = (ViewHolder) convertView.getTag();
		}
		final ViewHolder viewHolder = tmpViewHolder;


		final Msgs.PostbarTopicDetail detail = data.get(position);
		final Msgs.PostbarTopicDetail previousDetail = position ==0?null:data.get(position-1);
		//标签显示规则， 有置顶的，显示2个,后面的显示顺序   礼包，公告，精华，集合，热，新,图
		//其他，全部显示

		boolean isShowfullTag = false ;
		//是否显示了tag的图片
		boolean isShowtagImage =false ;
		//是否显示了图片的image
		boolean isShowImageImage =false ;


		if(previousDetail ==null ||detail.getIsTop()){
			viewHolder.tmp_fg_1.setVisibility(View.GONE);
			viewHolder.tmp_fg_2.setVisibility(View.GONE);
		}
		else{
			viewHolder.tmp_fg_1.setVisibility(View.VISIBLE);
			viewHolder.tmp_fg_2.setVisibility(View.VISIBLE);
		}
		//下面需要换算，显示那些tag
		// 是否锁帖
		if (detail.getIsLock()) {
			viewHolder.postbar_topic_list_item_islock.setVisibility(View.VISIBLE);
			isShowtagImage = true ;
		} else {
			viewHolder.postbar_topic_list_item_islock.setVisibility(View.GONE);
		}
		// 是否置顶
		if (detail.getIsTop()) {
			viewHolder.postbar_topic_list_item_showorhide_ll.setVisibility(View.GONE);
			viewHolder.postbar_topic_list_item_istop.setVisibility(View.VISIBLE);
			isShowtagImage = true ;
		} else {
			viewHolder.postbar_topic_list_item_showorhide_ll.setVisibility(View.VISIBLE);
			viewHolder.postbar_topic_list_item_istop.setVisibility(View.GONE);
		}
		// 是否公告
		if (detail.getIsNotice() && !isShowfullTag) {
			viewHolder.postbar_topic_list_item_isnotice.setVisibility(View.VISIBLE);
			isShowtagImage = true ;
			if(detail.getIsTop())
				isShowfullTag = true ;
		} else {
			viewHolder.postbar_topic_list_item_isnotice.setVisibility(View.GONE);
		}

		//是否礼包（暂时未加判断）

		// 是否热帖
		if (detail.getIsHot()&& !isShowfullTag) {
			isShowtagImage = true ;
			viewHolder.postbar_topic_list_item_ishot.setVisibility(View.VISIBLE);
			if(detail.getIsTop())
				isShowfullTag = true ;
		} else {
			viewHolder.postbar_topic_list_item_ishot.setVisibility(View.GONE);
		}
		
		// 是否精华
		if (detail.getIsEssence()&& !isShowfullTag) {
			isShowtagImage = true ;
			viewHolder.postbar_topic_list_item_isessence.setVisibility(View.VISIBLE);
			if(detail.getIsTop())
				isShowfullTag = true ;
		} else {
			viewHolder.postbar_topic_list_item_isessence.setVisibility(View.GONE);
		}

		// 是否集合贴
		if (detail.getIsTopicSet() && !isShowfullTag) {
			isShowtagImage = true ;
			viewHolder.postbar_topic_list_item_isji.setVisibility(View.VISIBLE);
			if(detail.getIsTop())
				isShowfullTag = true ;
		} else {
			viewHolder.postbar_topic_list_item_isji.setVisibility(View.GONE);
		}
		// 是否新帖
		if ((detail.getCreateTime() + SystemContext.getInstance().getPTID() > SystemContext.getInstance().getCurrentTimeInMillis())&& !isShowfullTag) {
			isShowtagImage = true ;
			viewHolder.postbar_topic_list_item_isnews.setVisibility(View.VISIBLE);
			if(detail.getIsTop())
				isShowfullTag = true ;
		} else {
			viewHolder.postbar_topic_list_item_isnews.setVisibility(View.GONE);
		}

		// 是否有图片
		if (detail.hasPostContent() && detail.getPostContent().getElementsCount() > 0) {
			List<Msgs.PostElement> data = detail.getPostContent().getElementsList();
			for (int i = 0; i < data.size(); i++) {
				final Msgs.PostElement element = data.get(i);
				if (element.getType() == PostElementType.PE_IMAGE_ID_REF) {
					isShowImageImage = true ;
					break;
				}
			}
		}

		if (isShowImageImage) {
			viewHolder.postbar_topic_list_item_ishavepic.setVisibility(View.VISIBLE);
		} else {
			viewHolder.postbar_topic_list_item_ishavepic.setVisibility(View.GONE);
		}
		// 标题
		if(!isShowtagImage && !isShowImageImage )
			viewHolder.postbar_topic_list_item_title_gap.setVisibility(View.GONE);
		else
			viewHolder.postbar_topic_list_item_title_gap.setVisibility(View.VISIBLE);
		viewHolder.postbar_topic_list_item_title.setText(detail.getTitle());
		String remotePosition = null;
		//if (detail.getLastReplyUid() == 0 || ordertype == MsgsConstants.POSTBAR_ORDER_CREATE_TIME) {
		// 发布者
		viewHolder.postbar_topic_list_item_nickname.setText(detail.getPosterNickname());
		// 发布时间
		viewHolder.postbar_topic_list_item_date.setText(SafeUtils.getDate2MyStr2(detail.getCreateTime()));
		//发布者是否是吧主
		if (detail.getPosterIsMaster()) {
			viewHolder.postbar_topic_list_item_master.setImageResource(R.drawable.postbar_bazhu);
		} else {
			viewHolder.postbar_topic_list_item_master.setImageResource(R.drawable.game_icon_people);
		}
		remotePosition = detail.getPosition();
		//	} else {
		//	    // 最后回复者
		//	    viewHolder.postbar_topic_list_item_nickname.setText(detail.getLastReplyNickname());
		//	    // 最后回复时间
		//	    viewHolder.postbar_topic_list_item_date.setText(SafeUtils.getDate2MyStr2(detail.getLastReplyTime()));
		//	    //最后回复者是否是吧主
		//	    if (detail.getLastReplyIsMaster()) {
		//		viewHolder.postbar_topic_list_item_master.setImageResource(R.drawable.postbar_bazhu);
		//	    } else {
		//		viewHolder.postbar_topic_list_item_master.setImageResource(R.drawable.game_icon_people);
		//	    }
		//	    remotePosition = detail.getLastReplyPosition();
		//	}
		//位置距离

		String tmploc = DistanceUtil.covertDistance(remotePosition);
		if(!tmploc.isEmpty())
		{
			tmpViewHolder.postbar_topic_list_item_loc.setText(" | "+tmploc);
		}
		else
		{
			tmpViewHolder.postbar_topic_list_item_loc.setText("");
		}



		// 评论数
		viewHolder.postbar_topic_list_item_comments.setText("" + detail.getComments());

		String visits = "/0";

		if(detail.getTopicVisits() > 99999){
			visits = "10万以上";
		}else{
			visits = "/" + detail.getTopicVisits();
		}
		viewHolder.postbar_topic_list_item_visits.setText(visits);

		if(targettype !=SystemConfig.GETTOPICLIST_TARGETTYPE_FAVORITE)
		{
			viewHolder.postbar_topic_list_item_del.setVisibility(View.GONE);
		}
		else {
			// 是否显示删除按钮
			if (editStatus) {
				viewHolder.postbar_topic_list_item_del.setVisibility(View.VISIBLE);
			} else {
				viewHolder.postbar_topic_list_item_del.setVisibility(View.GONE);
			}

		}

		viewHolder.postbar_topic_list_item_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				delFavorite(position);
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView postbar_topic_list_item_isnotice;
		ImageView postbar_topic_list_item_isessence;
		ImageView postbar_topic_list_item_istop;
		ImageView postbar_topic_list_item_ishot;
		ImageView postbar_topic_list_item_isnews;
		ImageView postbar_topic_list_item_isji;
		ImageView postbar_topic_list_item_islock;
		TextView postbar_topic_list_item_title;
		TextView postbar_topic_list_item_nickname;
		TextView postbar_topic_list_item_date;
		ImageView postbar_topic_list_item_ishavepic;
		TextView postbar_topic_list_item_comments;
		TextView postbar_topic_list_item_visits;
		ImageView postbar_topic_list_item_del;
		TextView postbar_topic_list_item_loc;
		View tmp_fg_1;//根据情况需要隐藏
		View tmp_fg_2;//根据情况需要隐藏
		LinearLayout postbar_topic_list_item_showorhide_ll ;

		View postbar_topic_list_item_title_gap;//标题前的gap

		ImageView postbar_topic_list_item_master;
	}

	private void delFavorite( int position) {
		if(itemClickListener != null)
		{
			itemClickListener.onClickAction(position, ACTION_DEL);
		}
	}
}
