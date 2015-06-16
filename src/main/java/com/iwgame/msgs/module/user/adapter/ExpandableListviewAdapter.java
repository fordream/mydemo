/**      
 * MyExpandableListviewAdapter.java Create on 2014-9-15     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */ 

package com.iwgame.msgs.module.user.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.PointTaskDao;
import com.iwgame.msgs.localdb.dao.UserGradeDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.cache.Cache;
import com.iwgame.msgs.module.cache.CacheCallBack;
import com.iwgame.msgs.module.cache.CacheImpl;
import com.iwgame.msgs.module.user.object.UserPointTaskObj;
import com.iwgame.msgs.module.user.ui.widget.QQListView;
import com.iwgame.msgs.module.user.ui.widget.QQListView.QQHeaderAdapter;
import com.iwgame.msgs.proto.Msgs.PostContent;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.PointTaskVo;
import com.iwgame.msgs.vo.local.UserGradeVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

/** 
 * @ClassName: MyExpandableListviewAdapter 
 * @Description: TODO(...) 
 * @author zhangjianchuan
 * @date 2014-9-15 上午9:25:02 
 * @Version 1.0
 * 
 */
public class ExpandableListviewAdapter extends BaseExpandableListAdapter implements QQHeaderAdapter{

	private List<List<Map<String, UserPointTaskObj>>> children;
	private List<Map<String, String>> group;
	private LayoutInflater inflater;
	private QQListView listView; 
	private HashMap<Integer,Integer> groupStatusMap = new HashMap<Integer, Integer>();
	private Context context;
	private UserGradeDao userGradeDao; 
	private UserGradeVo vo;
//	private TextView cur_your_integral;
	private CustomProgressDialog downloaddialog;
	private Dialog dialog;
	private UserVo userVo;
	private View view;
	private double gradePoint;
	private UserGradeVo userGradeVo;
	private PostContent postContent;
	private int size;
	PointTaskDao pointTaskDao = DaoFactory.getDaoFactory().getPointTaskDao(SystemContext.getInstance().getContext());
	/**
	 * 构造方法
	 * @param context
	 * @param listview
	 * @param group
	 * @param glayout
	 * @param groupFrom
	 * @param groupTo
	 * @param children
	 * @param clayout
	 * @param childTo
	 */
	public ExpandableListviewAdapter(Context context,QQListView listview, List<Map<String, String>> group,List<List<Map<String, UserPointTaskObj>>> children,TextView cur,CustomProgressDialog customProgressDialog,Dialog dialog,int grade) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.group = group;
		this.children = children;
		this.listView = listview;
		userGradeDao = DaoFactory.getDaoFactory().getUserGradeDao(SystemContext.getInstance().getContext());
		vo = userGradeDao.queryByGrade(grade);
//		this.cur_your_integral = cur;
		this.downloaddialog = customProgressDialog;
		this.dialog = dialog;
		userVo = SystemContext.getInstance().getExtUserVo();
		userGradeDao = DaoFactory.getDaoFactory().getUserGradeDao(SystemContext.getInstance().getContext());
		userGradeVo = userGradeDao.queryByGrade(userVo.getGrade());
		gradePoint = Double.parseDouble(userGradeVo.getMultiple());
	
	
	}
	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return children.get(groupPosition).get(childPosition);
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		size=children.size();
		ChildViewHolder holder = null;
		if(convertView == null){
			holder = new ChildViewHolder();
			convertView = inflater.inflate(R.layout.point_task_item_view, null);
			holder.title = (TextView)convertView.findViewById(R.id.new_hand_task_title);
			holder.point = (TextView)convertView.findViewById(R.id.new_hand_task_point);
			holder.exp = (TextView)convertView.findViewById(R.id.new_hand_task_exp);
			holder.fenge = (ImageView)convertView.findViewById(R.id.line_fenge);
			holder.pointimg1 = (ImageView)convertView.findViewById(R.id.new_hand_task_point_img1);
			holder.expimg1 = (ImageView)convertView.findViewById(R.id.new_hand_task_exp_img1);
			holder.expimg2 = (ImageView)convertView.findViewById(R.id.new_hand_task_exp_img2);
			holder.expimg3 = (ImageView)convertView.findViewById(R.id.new_hand_task_exp_img3);
			holder.expimg4 = (ImageView)convertView.findViewById(R.id.new_hand_task_exp_img4);
			holder.expimg5 = (ImageView)convertView.findViewById(R.id.new_hand_task_exp_img5);
			convertView.setTag(holder);
		}else{
			holder = (ChildViewHolder)convertView.getTag();
		}
		if((children.size() == 2) && (groupPosition == 0) && (childPosition == children.get(0).size() - 1)){
			holder.fenge.setVisibility(View.GONE);
		}else{
			holder.fenge.setVisibility(View.VISIBLE);
		}
			
		final UserPointTaskObj obj = children.get(groupPosition).get(childPosition).get("taskobj");
		if(size<=1){
			groupPosition=1;
		}
		double point = 0;
		if(obj != null && vo != null && obj.getPointTask() != null)
			point = (obj.getPointTask().getPoint())*(Double.parseDouble(vo.getMultiple()));
		String s = String.valueOf(point);
		final int poin; 
		if((children.size() == 2 && groupPosition == 1)||(children.size() == 1)){
			poin = Integer.parseInt(s.substring(0, s.indexOf(".")));
		}else{
			poin = obj.getPointTask().getPoint();
		}
		String title = obj.getPointTask().getTaskname();
		holder.title.setText(title);
		int userPoint;
		if(groupPosition==1){
			int tid = obj.getPointTask().getTaskid();
			if(tid == 2007){
				userPoint=(int) (obj.getPointTask().getPoint());
			}else{
				userPoint=(int) (obj.getPointTask().getPoint()*gradePoint);
			}
		}else{
			userPoint=obj.getPointTask().getPoint();
		}
		if(obj.getPointTask().getPoint()==0){
			holder.point.setText("无积分");
			holder.pointimg1.setVisibility(View.GONE);
		}else{
		holder.point.setText(userPoint + "积分/次");
		holder.pointimg1.setVisibility(View.VISIBLE);
		}
		holder.exp.setText(obj.getPointTask().getExp() + "经验值/次");
		Drawable expPre = context.getResources().getDrawable(R.drawable.tasklist_exp_pre);
		Drawable pointPre = context.getResources().getDrawable(R.drawable.tasklist_jifen_pre);
		Drawable  expNor = context.getResources().getDrawable(R.drawable.tasklist_exp_nor);
		Drawable pointNor =context.getResources().getDrawable(R.drawable.tasklist_jifen_nor);
		//显示图片
		setTaskVisible(groupPosition, holder);
		if(groupPosition==0&&obj.getStatus()==3){
			holder.pointimg1.setBackgroundDrawable(pointPre);
			holder.expimg1.setBackgroundDrawable(expPre);
		}else if(groupPosition==1){
			if(obj.getStatus()==3){
			holder.pointimg1.setBackgroundDrawable(pointPre);
			}else{
			holder.pointimg1.setBackgroundDrawable(pointNor);
			}
			if(obj.getPointTask().getExptimes()==1){
				holder.expimg1.setVisibility(View.VISIBLE);
				holder.expimg2.setVisibility(View.GONE);
				holder.expimg3.setVisibility(View.GONE);
				holder.expimg4.setVisibility(View.GONE);
				holder.expimg5.setVisibility(View.GONE);
			}else if(obj.getPointTask().getExptimes()==2){
				holder.expimg1.setVisibility(View.VISIBLE);
				holder.expimg2.setVisibility(View.VISIBLE);
				holder.expimg3.setVisibility(View.GONE);
				holder.expimg4.setVisibility(View.GONE);
				holder.expimg5.setVisibility(View.GONE);
			}else if(obj.getPointTask().getExptimes()==3){
				holder.expimg1.setVisibility(View.VISIBLE);
				holder.expimg2.setVisibility(View.VISIBLE);
				holder.expimg3.setVisibility(View.VISIBLE);
				holder.expimg4.setVisibility(View.GONE);
				holder.expimg5.setVisibility(View.GONE);
			}else if(obj.getPointTask().getExptimes()==4){
				holder.expimg1.setVisibility(View.VISIBLE);
				holder.expimg2.setVisibility(View.VISIBLE);
				holder.expimg3.setVisibility(View.VISIBLE);
				holder.expimg4.setVisibility(View.VISIBLE);
				holder.expimg5.setVisibility(View.GONE);
			}else if(obj.getPointTask().getExptimes()>=5){
				holder.expimg1.setVisibility(View.VISIBLE);
				holder.expimg2.setVisibility(View.VISIBLE);
				holder.expimg3.setVisibility(View.VISIBLE);
				holder.expimg4.setVisibility(View.VISIBLE);
				holder.expimg5.setVisibility(View.VISIBLE);
			}else{
				holder.expimg1.setVisibility(View.GONE);
				holder.expimg2.setVisibility(View.GONE);
				holder.expimg3.setVisibility(View.GONE);
				holder.expimg4.setVisibility(View.GONE);
				holder.expimg5.setVisibility(View.GONE);
			}
			if(obj.getTimes()==1){
				holder.expimg1.setBackgroundDrawable(expPre);
				holder.expimg2.setBackgroundDrawable(expNor);		
				holder.expimg3.setBackgroundDrawable(expNor);		
				holder.expimg4.setBackgroundDrawable(expNor);		
				holder.expimg5.setBackgroundDrawable(expNor);		
				}else if(obj.getTimes()==2){
					holder.expimg1.setBackgroundDrawable(expPre);
					holder.expimg2.setBackgroundDrawable(expPre);
					holder.expimg3.setBackgroundDrawable(expNor);		
					holder.expimg4.setBackgroundDrawable(expNor);		
					holder.expimg5.setBackgroundDrawable(expNor);
				}else if(obj.getTimes()==3){
					holder.expimg1.setBackgroundDrawable(expPre);
					holder.expimg2.setBackgroundDrawable(expPre);
					holder.expimg3.setBackgroundDrawable(expPre);
					holder.expimg4.setBackgroundDrawable(expNor);		
					holder.expimg5.setBackgroundDrawable(expNor);
				}else if(obj.getTimes()==4){
					holder.expimg1.setBackgroundDrawable(expPre);
					holder.expimg2.setBackgroundDrawable(expPre);
					holder.expimg3.setBackgroundDrawable(expPre);
					holder.expimg4.setBackgroundDrawable(expPre);
					holder.expimg5.setBackgroundDrawable(expNor);
				}else if(obj.getTimes()>=5){
					holder.expimg1.setBackgroundDrawable(expPre);
					holder.expimg2.setBackgroundDrawable(expPre);
					holder.expimg3.setBackgroundDrawable(expPre);
					holder.expimg4.setBackgroundDrawable(expPre);
					holder.expimg5.setBackgroundDrawable(expPre);
				}else{
					holder.expimg1.setBackgroundDrawable(expNor);	
					holder.expimg2.setBackgroundDrawable(expNor);		
					holder.expimg3.setBackgroundDrawable(expNor);		
					holder.expimg4.setBackgroundDrawable(expNor);		
					holder.expimg5.setBackgroundDrawable(expNor);	
				}
		}else{
			holder.pointimg1.setBackgroundDrawable(pointNor);
			holder.expimg1.setBackgroundDrawable(expNor);
		}
		
		

	
		
		//分支判断当前任务是否已经完成
//		if(obj.getStatus() == 1){
//			holder.state.setEnabled(false);
//			int progress = (100 * obj.getTimes())/obj.getPointTask().getTimes();
//			if(progress == 0){
//				Drawable drawable = context.getResources().getDrawable(R.drawable.progressbar_style4);
//				holder.pb.setVisibility(View.INVISIBLE);
//				drawable.setBounds(holder.pb.getProgressDrawable().getBounds());
//				holder.state.setBackgroundDrawable(drawable);
//			}else{
//				Drawable drawable = context.getResources().getDrawable(R.drawable.progressbar_style1);
//				holder.pb.setVisibility(View.VISIBLE);
//				drawable.setBounds(holder.pb.getProgressDrawable().getBounds());
//				holder.pb.setProgressDrawable(drawable);
//				holder.pb.setProgress(progress);
//				holder.state.setBackgroundColor(android.R.color.transparent);
//			}
//			holder.state.setTextColor(context.getResources().getColor(R.color.global_color1));
//			holder.state.setText("未完成");
//			holder.state.setVisibility(View.VISIBLE);
//		}else if(obj.getStatus() == 2){
//			holder.state.setEnabled(true);
//			holder.state.setBackgroundResource(R.drawable.progressbar_style3);
//			holder.state.setTextColor(context.getResources().getColor(R.color.global_color1));
//			holder.state.setText("领"+poin+"积分");
//			holder.state.setVisibility(View.VISIBLE);
//			holder.pb.setVisibility(View.INVISIBLE);
//			final ChildViewHolder hold = holder;
//			holder.state.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					if(SystemContext.getInstance().isNeedBundPhone(SystemConfig.USERACTION_GET_TASK_INTEGRAL)){
//						createBundPhoneDialog();
//					}else{
//						fetchTaskPoint(obj,poin,hold);
//					}
//				}
//			});
//		}else if(obj.getStatus() == 3){
//			holder.state.setEnabled(false);
//			Drawable drawable = context.getResources().getDrawable(R.drawable.progressbar_style2);
//			drawable.setBounds(holder.pb.getProgressDrawable().getBounds());
//			holder.pb.setVisibility(View.INVISIBLE);
//			holder.state.setTextColor(context.getResources().getColor(R.color.global_color5));
//			holder.state.setBackgroundDrawable(drawable);
//			holder.state.setText("已完成");
//			holder.state.setVisibility(View.VISIBLE);
//			holder.state.setEnabled(false);
//		}
		return convertView;
	}



	/**
	 * 执行领取积分操作
	 * 
	 */
	protected void fetchTaskPoint(final UserPointTaskObj obj, final int score,final ChildViewHolder holder) {
		downloaddialog.show();
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {
			@Override
			public void onSuccess(Integer result) {
				obj.setStatus(3);
				downloaddialog.dismiss();
				//领取成功后更改右边按钮的色值
//				holder.state.setEnabled(false);
//				Drawable drawable = context.getResources().getDrawable(R.drawable.progressbar_style2);
//				drawable.setBounds(holder.pb.getProgressDrawable().getBounds());
//				holder.pb.setVisibility(View.INVISIBLE);
//				holder.state.setTextColor(context.getResources().getColor(R.color.global_color5));
//				holder.state.setBackgroundDrawable(drawable);
//				holder.state.setText("已完成");
//				holder.state.setVisibility(View.VISIBLE);
//				holder.state.setEnabled(false);
			
				SystemContext.getInstance().setPoint(SystemContext.getInstance().getPoint()+score);
//				cur_your_integral.setText(userVo.getPoint()+"");
				View view = View.inflate(context, R.layout.dialog_fetch_integral, null);
				TextView i_know_it = (TextView)view.findViewById(R.id.i_know_it);
				i_know_it.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				TextView cue_words = (TextView)view.findViewById(R.id.cue_words);
				cue_words.setText("成功领取"+score+"积分");
				TextView show_fetch_number = (TextView)view.findViewById(R.id.show_fetch_score);
				show_fetch_number.setText("+"+score);
				dialog.setContentView(view);
				dialog.show();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
				downloaddialog.dismiss();
			}
		}, context, obj.getPointTask().getTaskid(), MsgsConstants.OT_USER, MsgsConstants.OP_FETCH_POINT, null, null,null);
	
	}

	/**
	 * 弹出是否需要
	 * 绑定手机号的对话框
	 * @param actionName
	 */
	private void createBundPhoneDialog(){
		Intent intent = new Intent(context, BundPhoneActivity.class);
		context.startActivity(intent);
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if(groupPosition >=0 && groupPosition < children.size())
			return children.get(groupPosition).size();
		else
			return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		if(groupPosition < 0) 
			return null;
		return group.get(groupPosition);
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		return group.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView(int groupPosition,final boolean isExpanded, View convertView, ViewGroup parent) {
		GroupViewHolder holder = null;
		if(convertView == null){
			holder = new GroupViewHolder();
			convertView = inflater.inflate(R.layout.header, null);
			holder.timg = (LinearLayout)convertView.findViewById(R.id.new_hand_task_img);
			holder.tv = (TextView)convertView.findViewById(R.id.new_hand_task_title_description);
//			holder.fengexian = (ImageView)convertView.findViewById(R.id.head_fengexian);
			convertView.setTag(holder);
		}else{
			holder = (GroupViewHolder)convertView.getTag();
		}
		convertView.setBackgroundColor(context.getResources().getColor(R.color.point_detail_bg));
		holder.tv.setText(group.get(groupPosition).get("groupname"));
		if(groupPosition != 0)holder.timg.setVisibility(View.GONE);
//		if(isExpanded){
//			holder.iv.setImageResource(R.drawable.user_score_arrows_pre);
//		}else{
//			holder.iv.setImageResource(R.drawable.user_score_arrows_nor);
//		}
		return convertView;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see com.example.expandablelistview.QQListView.QQHeaderAdapter#getQQHeaderState(int, int)
	 */
	@Override
	public int getQQHeaderState(int groupPosition, int childPosition) {
		int childrenCount = getChildrenCount(groupPosition);
		if(childPosition == childrenCount - 1){
			return PINNED_HEADER_PUSHED_UP; 
		}else if(childPosition == -1 && !listView.isGroupExpanded(groupPosition)){
			return PINNED_HEADER_GONE; 
		}else{
			return PINNED_HEADER_VISIBLE;
		}
	}

	/* (non-Javadoc)
	 * @see com.example.expandablelistview.QQListView.QQHeaderAdapter#configureQQHeader(android.view.View, int, int, int)
	 */
	@Override
	public void configureQQHeader(View header, int groupPosition,
			int childPosition, int alpha) {
		// TODO Auto-generated method stub
		Map<String,String> groupData = (Map<String,String>)this.getGroup(groupPosition);
		if(groupData == null)
			((TextView)header.findViewById(R.id.new_hand_task_title_description)).setText("");
		((TextView)header.findViewById(R.id.new_hand_task_title_description)).setText(groupData.get("groupname"));
	}

	/* (non-Javadoc)
	 * @see com.example.expandablelistview.QQListView.QQHeaderAdapter#setGroupClickStatus(int, int)
	 */
	@Override
	public void setGroupClickStatus(int groupPosition, int status) {
		groupStatusMap.put(groupPosition, status);
	}

	/* (non-Javadoc)
	 * @see com.example.expandablelistview.QQListView.QQHeaderAdapter#getGroupClickStatus(int)
	 */
	@Override
	public int getGroupClickStatus(int groupPosition) {
		// TODO Auto-generated method stub
		if(groupStatusMap.containsKey(groupPosition)){
			return groupStatusMap.get(groupPosition);
		}
		else{
			return 0;
		}
	}

	/**
	 * 组的句柄
	 * @ClassName: GroupViewHolder 
	 * @Description: TODO(...) 
	 * @author zhangjianchuan
	 * @date 2014-9-15 上午10:28:49 
	 * @Version 1.0
	 *
	 */
	class GroupViewHolder{
		LinearLayout timg;
		TextView tv;
//		ImageView fengexian;
	}


	/**
	 * 子视图 的句柄 
	 * @ClassName: ChildViewHolder 
	 * @Description: TODO(...) 
	 * @author zhangjianchuan
	 * @date 2014-9-15 上午10:29:40 
	 * @Version 1.0
	 *
	 */
	class ChildViewHolder{
		TextView title;
		TextView point;
		TextView exp;
		ImageView fenge;
		ImageView pointimg1;
		
		ImageView expimg1;
		ImageView expimg2;
		ImageView expimg3;
		ImageView expimg4;
		ImageView expimg5;
	}
	public void setTaskVisible(int groupPosition,ChildViewHolder holder){
		if(groupPosition == 1){
			holder.expimg2.setVisibility(View.VISIBLE);
			holder.expimg3.setVisibility(View.VISIBLE);
			holder.expimg4.setVisibility(View.VISIBLE);
			holder.expimg5.setVisibility(View.VISIBLE);
		}else{
			holder.expimg2.setVisibility(View.GONE);
			holder.expimg3.setVisibility(View.GONE);
			holder.expimg4.setVisibility(View.GONE);
			holder.expimg5.setVisibility(View.GONE);
		}
	}
}
	