/**      
 * ReplyMyActivity.java Create on 2013-12-26     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.StrictModeWrapper;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.postbar.adapter.ReplyMyListAdapter;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PostbarTopicDetail;
import com.iwgame.msgs.proto.Msgs.PostbarTopicReplyDetail;
import com.iwgame.msgs.proto.Msgs.PostbarTopicReplyListResult;
import com.iwgame.msgs.utils.AppUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DateUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: ReplyMyActivity
 * @Description: 回复我的
 * @author 王卫
 * @date 2015-3-25 下午1:55:35
 * @Version 1.0
 * 
 */
public class ReplyMyFragment extends BaseFragment implements OnItemClickListener, OnRefreshListener2<ListView> {

	private final static String TAG = "ReplyMyFragment";
	private LayoutInflater inflater;

	ListView listView_content;
	PullToRefreshListView pullToRefreshView;

	List<Msgs.PostbarTopicReplyDetail> listdata;
	ReplyMyListAdapter listAdapter;

	int pageSize = 20;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictModeWrapper.init(this.getActivity());
		super.onCreate(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		View v = inflater.inflate(R.layout.postbar_replymy_list, container, false);
		init(v);
		return v;
	}

	private void init(View view) {
		pullToRefreshView = (PullToRefreshListView) view.findViewById(R.id.refreshList2);
		pullToRefreshView.setMode(Mode.BOTH);
		final ILoadingLayout headerLabels = pullToRefreshView.getLoadingLayoutProxy(true, false);
		final ILoadingLayout footerLabels = pullToRefreshView.getLoadingLayoutProxy(false, true);
		headerLabels.setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
		footerLabels.setPullLabel("加载更多");// 刚下拉时，显示的提示
		footerLabels.setRefreshingLabel("加载中...");// 刷新时
		footerLabels.setReleaseLabel("松开后加载");// 下来达到一定距离时，显示的提示

		pullToRefreshView.setOnRefreshListener(this);
		pullToRefreshView.setOnItemClickListener(this);

		listdata = new ArrayList<Msgs.PostbarTopicReplyDetail>();
		listAdapter = new ReplyMyListAdapter(getActivity(), listdata);
		pullToRefreshView.setAdapter(listAdapter);
		// 处理未读数
		long lastUnReadMaxIndex = SystemContext.getInstance().getSubjectLastUnReadMaxIndex(MsgsConstants.MC_NOTIFY,
				SystemContext.getInstance().getExtUserVo().getUserid(), MsgsConstants.DOMAIN_USER, MsgsConstants.MCC_COMMENT);
		long unReadMaxIndex = SystemContext.getInstance().getSubjectUnReadMaxIndex(MsgsConstants.MC_NOTIFY,
				SystemContext.getInstance().getExtUserVo().getUserid(), MsgsConstants.DOMAIN_USER, MsgsConstants.MCC_COMMENT);
		if (unReadMaxIndex > lastUnReadMaxIndex) {
			// 更新已经读到的数值
			SystemContext.getInstance().setSubjectLastUnReadMaxIndex(MsgsConstants.MC_NOTIFY, SystemContext.getInstance().getExtUserVo().getUserid(),
					MsgsConstants.DOMAIN_USER, MsgsConstants.MCC_COMMENT, unReadMaxIndex);
			// 取消状态栏上的通知
			NotificationManager nm = (NotificationManager) getActivity().getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			nm.cancel(SystemConfig.NOTIFICATION_ID_BASE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (SystemContext.getInstance().getExtUserVo() != null) {
			if (listdata.size() == 0) // 没有数据开始加载，加载最新的10条
			{
				loadData(Integer.MAX_VALUE, -pageSize);
			} else {
				loadData(listdata.get(0).getRid(), Integer.MAX_VALUE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int realposition = (int) parent.getAdapter().getItemId(position);
		// TODO Auto-generated method stub
		// 增加防止快速点击
		if (FastClickLimitUtil.isFastClick())
			return;
		final PostbarTopicReplyDetail detail = listdata.get(realposition);
		// 通过帖子id获得帖子详情，然后判断帖子是否被删除
		// 获得帖子详情
		ProxyCallBack<Msgs.PostbarTopicDetail> callback = new ProxyCallBack<Msgs.PostbarTopicDetail>() {

			@Override
			public void onSuccess(final PostbarTopicDetail result) {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (result != null) {
							if (result.getIsDel()) {
								ToastUtil.showToast(getActivity(), getString(R.string.postbaor_replymy_topic_del));
							} else {
								long gid = result.getGameid();
								if (gid != 0) {
									// 根据当前的应用配置判断是否要启动游伴
									AppConfig appconfig = AdaptiveAppContext.getInstance().getAppConfig();
									if (appconfig != null && appconfig.isRecbarmsg() && gid != appconfig.getGameId()) {
										AppUtil.openGame(getActivity(), detail.getTid(), TopicDetailActivity.class.getName(), getResources()
												.getString(R.string.postbar_show_topic_tip_for_youban_uninstall));
									} else {
										Intent intent = new Intent(getActivity(), TopicDetailActivity.class);
										Bundle bundle = new Bundle();
										bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, detail.getTid());
										bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, detail.getGameid());
										bundle.putSerializable(SystemConfig.BUNDLE_NAME_TOPICDETAIL_REPLYMYREPLY, detail);
										intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
										startActivity(intent);
									}
								}
							}

						} else {
							ErrorCodeUtil.handleErrorCode(getActivity(), ErrorCode.EC_CLIENT_POSTBAR_GETTOPICDETAILISNULL, null);
						}
					}
				});
			}

			@Override
			public void onFailure(final Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);
			}

		};
		ProxyFactory.getInstance().getPostbarProxy().getTopicDetail(callback, getActivity(), detail.getTid());

	}

	private void loadData(long offset, final int limit) {

		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(getActivity());
		dialog.show();
		ProxyCallBack<PostbarTopicReplyListResult> callback = new ProxyCallBack<PostbarTopicReplyListResult>() {

			@Override
			public void onSuccess(PostbarTopicReplyListResult result) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (result != null) {

					List<PostbarTopicReplyDetail> list = result.getEntryList();
					if (list != null && list.size() > 0) {
						if (limit < 0) {

							// 增加在后面
							for (int i = 0; i < list.size(); i++) {

								listdata.add(list.get(i));
							}

						} else {
							// 增加在前面
							for (int i = 0; i < list.size(); i++) {
								listdata.add(0, list.get(list.size() - 1 - i));
							}
						}
						listAdapter.notifyDataSetChanged();

					}

				}

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);
			}

		};
		ProxyFactory
				.getInstance()
				.getPostbarProxy()
				.getTopicReplyList(callback, getActivity(), SystemContext.getInstance().getExtUserVo().getUserid(), MsgsConstants.OT_USER, 0,
						MsgsConstants.POSTBAR_REPLAY_BY_OFFSET, offset, limit);

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (listdata.size() > 0) {
			PostbarTopicReplyDetail tmp = listdata.get(0);
			loadData(tmp.getRid(), pageSize);

			// 处理未读数
			long lastUnReadMaxIndex = SystemContext.getInstance().getSubjectLastUnReadMaxIndex(MsgsConstants.MC_NOTIFY,
					SystemContext.getInstance().getExtUserVo().getUserid(), MsgsConstants.DOMAIN_USER, MsgsConstants.MCC_COMMENT);
			long unReadMaxIndex = SystemContext.getInstance().getSubjectUnReadMaxIndex(MsgsConstants.MC_NOTIFY,
					SystemContext.getInstance().getExtUserVo().getUserid(), MsgsConstants.DOMAIN_USER, MsgsConstants.MCC_COMMENT);
			if (unReadMaxIndex > lastUnReadMaxIndex) {
				// 更新已经读到的数值
				SystemContext.getInstance().setSubjectLastUnReadMaxIndex(MsgsConstants.MC_NOTIFY,
						SystemContext.getInstance().getExtUserVo().getUserid(), MsgsConstants.DOMAIN_USER, MsgsConstants.MCC_COMMENT, unReadMaxIndex);
				// 取消状态栏上的通知
				NotificationManager nm = (NotificationManager) getActivity().getSystemService(android.content.Context.NOTIFICATION_SERVICE);
				nm.cancel(SystemConfig.NOTIFICATION_ID_BASE);
			}
		}
		pullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				pullToRefreshView.onRefreshComplete();
			}
		}, 1000);

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		if (listdata.size() > 0) {
			PostbarTopicReplyDetail tmp = listdata.get(listdata.size() - 1);
			loadData(tmp.getRid(), -pageSize);
		}
		pullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				pullToRefreshView.onRefreshComplete();
			}
		}, 1000);
	}

}
