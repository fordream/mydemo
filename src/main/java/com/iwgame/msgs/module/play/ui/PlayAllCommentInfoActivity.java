/**      
* PlayAllCommentInfoActivity.java Create on 2015-5-29     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.play.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.SendMsgCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.pay.adapter.PayListAdapter;
import com.iwgame.msgs.module.pay.ui.PayDetailInfoActivity;
import com.iwgame.msgs.module.pay.ui.PayDetailPageInfoActivity;
import com.iwgame.msgs.module.play.adapter.PlayCommentAdapter;
import com.iwgame.msgs.module.play.adapter.PlayCommentAdapter.ReplyButtonOnClickListener;
import com.iwgame.msgs.module.postbar.ui.TopicDetailFragment;
import com.iwgame.msgs.proto.Msgs.MessageContentType;
import com.iwgame.msgs.proto.Msgs.PlayEvalInfo.PlayEvalReply;
import com.iwgame.msgs.proto.Msgs.PlayEvalInfo;
import com.iwgame.msgs.proto.Msgs.PlayEvalList;
import com.iwgame.msgs.proto.Msgs.QuotesReplyDetail;
import com.iwgame.msgs.proto.Msgs.UserInfoDetail;
import com.iwgame.msgs.proto.Msgs.UserYoubiDetail;
import com.iwgame.msgs.utils.Utils;
import com.iwgame.msgs.widget.SendMsgView;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/** 
 * @ClassName: PlayAllCommentInfoActivity 
 * @Description: 陪玩全部评价内容
 * @author xingjianlong
 * @date 2015-5-29 下午5:06:44 
 * @Version 1.0
 * 
 */
public class PlayAllCommentInfoActivity extends BaseActivity {
	
	private long pid;
	private long uid;
	private View view;
	private LinearLayout parentView;
	private LinearLayout bottomView;
	private CommonListView listContent;
	private PlayCommentAdapter adapter;
	private String TAG ="playAllCommentInfoActivity";
	private SendMsgView sendMsgView;
	private long  cid;
	private CustomProgressDialog downloaddialog;
	private int position;
	private String username;
	private UserInfoDetail userinfo;
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();
		init();
	}
	private void setloadingView(){
		downloaddialog = CustomProgressDialog.createDialog(this, false);
		downloaddialog.show();
	}
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (listContent != null && listContent.listData.size() <= 0) {
			listContent.onHeaderRefresh();
		} else if (listContent != null) {
			listContent.setRefreshMode(Mode.BOTH);
			listContent.adapter.notifyDataSetChanged();
			listContent.adapter.notifyDataSetInvalidated();
		}
	}
	/**
	 * 初始化界面
	 */
	private void init() {
			setloadingView()	;
			setTitleTxt("陪玩评价");
			parentView=(LinearLayout) findViewById(R.id.contentView);
			parentView.setVisibility(View.GONE);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			view = View.inflate(this, R.layout.play_all_comment_content, null);
			parentView.addView(view, params);
			contentView=(LinearLayout)view.findViewById(R.id.remark_contentView);
			bottomView =(LinearLayout)view.findViewById(R.id.remark_bottomView);
			bottomView.setVisibility(View.GONE);
			addView();
	}
	private void addView(){
		contentView.removeAllViews();
		listContent =null;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		listContent = new CommonListView(this, View.VISIBLE, true){
			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				getCommentData(PlayAllCommentInfoActivity.this, pid);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
 			 * com.iwgame.msgs.widget.listview.CommonListView#showNullBgView()
			 */
			@Override
			public void showNullBgView() {
				listContent.setVisibility(View.GONE);
				nullContent.setVisibility(View.VISIBLE);
			}
		};
		contentView.addView(listContent, params);
		ReplyButtonOnClickListener replyButtonOnClickListener = new ReplyButtonOnClickListener(){


			

			@Override
			public void onClickReply(QuotesReplyDetail detail, int position) {
				
			}

			@Override
			public void onClick(int pos,long id,UserInfoDetail info,String name) {
				username = name;
				position = pos;
				userinfo = info;
				cid = id;
				sendMsgView.setEditTextHint(getString(R.string.postbar_reply_sendedit_hint, info.getNickname()));
				sendMsgView.setEditTextFocus2();
				bottomView.setVisibility(View.VISIBLE);
			}};
		sendMsgView =  (SendMsgView) View.inflate(PlayAllCommentInfoActivity.this, R.layout.public_send_msg_view, null);
		bottomView.addView(sendMsgView);
		sendMsgView.sendmsg_addattachments.setEnabled(false);
		sendMsgView.sendmsg_addattachments.setBackgroundResource(R.drawable.chat_msg_addattachments_pre2);
		SendMsgCallBack sendMsgCallBack = new SendMsgCallBack() {
			
			@Override
			public void setListViewLastIndexSelection(int delaytime) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setAudioRecorderStatus(int status) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void send(int msgtype, String content, byte[] contentBytes,
					int action) {
				if (msgtype == MessageContentType.TEXT_VALUE) {
					
							if (!content.trim().isEmpty() ) {
								replyComment(content);
							} else {
								ToastUtil.showToast(PlayAllCommentInfoActivity.this, "回复内容必须有文字或表情");
						
					}
				}
			}
			
			@Override
			public void createDialog() {
				// TODO Auto-generated method stub
				
			}
		};
		sendMsgView.setSendMsgCallBack(sendMsgCallBack);
		adapter = new PlayCommentAdapter(PlayAllCommentInfoActivity.this, listContent.listData,uid);
		adapter.setReplyButtonOnClickListener(replyButtonOnClickListener);
		listContent.setAdapter(adapter);
		listContent.getmPullRefreshListView().setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸后滚动
					bottomView.setVisibility(View.GONE);
					Utils.hideSoftInput(PlayAllCommentInfoActivity.this, sendMsgView.getSendMsgEditText());
					break;
				}
			}
			
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	/**
	 * 获取评价数据
	 */
	private void getData() {
		pid = getIntent().getLongExtra("pid", 0);
		uid = getIntent().getLongExtra("uid", 0);
	}
	
	private void getCommentData(Context context,Long pid){
		ProxyFactory.getInstance().getPlayProxy().getPlayComments(new ProxyCallBack<PlayEvalList>() {
			
			@Override
			public void onSuccess(PlayEvalList result) {
				if(downloaddialog.isShowing()){
					downloaddialog.dismiss();
				}
				if (listContent.isRefresh) {
					listContent.clean();
					listContent.onHeaderRefreshComplete();
				} else {
					listContent.onFooterRefreshComplete();
				}
				if (result != null && result.getPlayEvalList() != null && result.getPlayEvalList().size() > 0) {
					if (result.getPlayEvalList().size() < listContent.limit)
						listContent.hasNext = false;
					listContent.listData.addAll(result.getPlayEvalList());
					listContent.showListView();
				} else {
					LogUtil.d(TAG, "数据为空");
					if (listContent.listData.size() <= 0)
						listContent.showNullBgView();
				}
				if (listContent.listData.size() <= 0) {
					listContent.setRefreshMode(Mode.PULL_DOWN_TO_REFRESH);
					listContent.listData.clear();
				} else {
					listContent.setRefreshMode(Mode.BOTH);
				}
				listContent.adapter.notifyDataSetChanged();
				listContent.offset = listContent.listData.size();
				parentView.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				parentView.setVisibility(View.VISIBLE);
				if(downloaddialog.isShowing()){
					downloaddialog.dismiss();
				}
				ToastUtil.showToast(PlayAllCommentInfoActivity.this, result+"");
			}
		}, context, pid,  listContent.offset, listContent.limit);
	}
	/**
	 * 发送回复内容
	 * @param content
	 */
	private void replyComment(final String content){
		listContent.setRefreshMode(Mode.DISABLED);
		downloaddialog.show();
		sendMsgView.getSendButton().setClickable(false);
		ProxyFactory.getInstance().getPlayProxy().sendPlayReply(new ProxyCallBack<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				if(downloaddialog.isShowing()){
					downloaddialog.dismiss();
				}
				if(result==0){
					ToastUtil.showToast(PlayAllCommentInfoActivity.this, "回复成功");
					listContent.setRefreshMode(Mode.BOTH);
					PlayEvalReply.Builder re = PlayEvalReply.newBuilder();
					re.setId(55);
					re.setContent(content);
					re.setNickname(username);
					re.setTonickname(userinfo.getNickname());
					re.setCreatetime(System.currentTimeMillis());
					
					Map<Long, List<PlayEvalReply>> evalReply = ((PlayCommentAdapter)listContent.adapter).getEvalReply();
					 List<PlayEvalReply> newReplys = new ArrayList<PlayEvalReply>();
					if(evalReply != null){
						List<PlayEvalReply> replys = evalReply.get(cid);
						if(replys!=null&&replys.size()>0){
						for (int i = 0; i < replys.size(); i++) {
							PlayEvalReply reply = replys.get(i);
							newReplys.add(reply);
						}
					}
						}
					 newReplys.add(re.build());
					 evalReply.put(cid, newReplys);
					listContent.adapter.notifyDataSetChanged();
					bottomView.setVisibility(View.GONE);
					sendMsgView.setEditTextHint("请输入内容");
					sendMsgView.setHideSendMsgSmileypanel();
					sendMsgView.getSendMsgEditText().setText("");
					sendMsgView.getSendButton().setClickable(true);
					// 隐藏输入法
					Utils.hideSoftInput(PlayAllCommentInfoActivity.this, sendMsgView.getSendMsgEditText());
				}
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				downloaddialog.dismiss();
				ToastUtil.showToast(PlayAllCommentInfoActivity.this, "回复失败");
				sendMsgView.getSendButton().setClickable(true);
			}
		}, PlayAllCommentInfoActivity.this, cid, content);
	}
}
