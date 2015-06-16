/**      
 * ForwardingSubjectListFragment.java Create on 2014-4-1     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.chat.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.cache.Cache;
import com.iwgame.msgs.module.cache.CacheCallBack;
import com.iwgame.msgs.module.chat.adapter.SubjectAdapter;
import com.iwgame.msgs.module.chat.vo.SubjectVo;
import com.iwgame.msgs.utils.EditTextUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.SideBar;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.PinyinUtil;
import com.youban.msgs.R;

/**
 * @ClassName: ForwardingSubjectListFragment
 * @Description: TODO(转发消息选择界面，最近联系人列表，用户，公会)
 * @author chuanglong
 * @date 2014-4-1 下午4:14:27
 * @Version 1.0
 * 
 */
public class ForwardingShareSubjectListFragment extends BaseFragment implements OnItemClickListener {

	private static final String TAG = "LatestContactsFragment";
	LayoutInflater inflater = null;

	ListView latestcontacts_listview = null;
	SubjectAdapter latestContactsAdapter = null;
	List<SubjectVo> latestContactsData = new ArrayList<SubjectVo>();

	int forwardingSubjectListType = 0;
	EditText searchTxt;
	String searchKeyword = null;

	/**
	 * 没有数据时的界面
	 */
	LinearLayout nullContent;
	/**
	 * 没有数据时显示的图片
	 */
	ImageView nullContentBgIcon;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		PTAG = TAG;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// MobclickAgent.onPageEnd(TAG);
		List<Long> s = new ArrayList<Long>();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// MobclickAgent.onPageStart(TAG);
		LogUtil.d(TAG, "----->onResume");
		loadData();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.inflater = inflater;
		// 获得传入的参数
		Bundle tmpbundle = this.getArguments();
		if (tmpbundle != null) {
			forwardingSubjectListType = tmpbundle.getInt(SystemConfig.BUNDLE_NAME_FORWARDING_SUBJECT_LIST_TYPE);
		}
		View v = inflater.inflate(R.layout.subject_list, container, false);

		init(v);

		return v;
	}

	private void init(View v) {
		latestcontacts_listview = (ListView) v.findViewById(R.id.lv_latestcontacts);
		latestContactsAdapter = new SubjectAdapter(this.getActivity(), latestContactsData);
		latestcontacts_listview.setAdapter(latestContactsAdapter);
		latestcontacts_listview.setOnItemClickListener(this);
		LinearLayout subject_search = (LinearLayout) v.findViewById(R.id.subject_search);
		SideBar sideBar = (SideBar) v.findViewById(R.id.sideBar);
		if (forwardingSubjectListType == SystemConfig.FORWARDING_SUBJECT_LIST_TYPE_USERS) {
			subject_search.setVisibility(View.VISIBLE);
			sideBar.setVisibility(View.VISIBLE);
			// 设置按字母定位
			sideBar.setListView(latestcontacts_listview, latestContactsAdapter);
		} else {
			subject_search.setVisibility(View.GONE);
			sideBar.setVisibility(View.GONE);

		}
		searchTxt = (EditText) v.findViewById(R.id.searchTxt);
		// 设置文本框输入监听功能
		searchTxt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					return true;
				}
				return false;
			}
		});
		searchTxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (searchTxt.getText().length() == 0) {
					searchKeyword = null;
				} else {
					searchKeyword = searchTxt.getText().toString();
				}
				loadData();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		// 设置清除按钮
		Button cleanBtn = (Button) v.findViewById(R.id.cleanBtn);
		// 设置文本输入框文本变化监听
		EditTextUtil.ChangeCleanTextButtonVisible(searchTxt, cleanBtn);
		cleanBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchTxt.setText("");
			}
		});

		nullContent = (LinearLayout) v.findViewById(R.id.nullContent);
		nullContentBgIcon = (ImageView) v.findViewById(R.id.bgIcon);

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
		// TODO Auto-generated method stub
		if (parent.getId() == R.id.lv_latestcontacts) {
			int realposition = (int) parent.getAdapter().getItemId(position);
			SubjectVo vo = latestContactsData.get(realposition);
			if (this.getActivity() != null && this.getActivity() instanceof ForwardingShareContentFragmentActivity) {
				ForwardingShareContentFragmentActivity tmpActivity = (ForwardingShareContentFragmentActivity) getActivity();
				if (vo.getSubjectType() == MsgsConstants.OT_USER) {
					tmpActivity.forwardingShareContent(vo.getSubjectId(), MsgsConstants.DOMAIN_USER, vo.getSubject());
				} else if (vo.getSubjectType() == MsgsConstants.OT_GROUP) {
					tmpActivity.forwardingShareContent(vo.getSubjectId(), MsgsConstants.DOMAIN_GROUP, vo.getSubject());
				}
			}

		}

	}

	private void loadData() {
		if (forwardingSubjectListType == SystemConfig.FORWARDING_SUBJECT_LIST_TYPE_LATESTCONTACTS) {
			loadDatalatestContacts();
		} else if (forwardingSubjectListType == SystemConfig.FORWARDING_SUBJECT_LIST_TYPE_USERS) {
			loadDataUsers();
		} else if (forwardingSubjectListType == SystemConfig.FORWARDING_SUBJECT_LIST_TYPE_GROUP) {
			loadDataGroup();
		}
	}

	private void loadDatalatestContacts() {

		new MyAsyncTask(null).execute(new AsyncCallBack<List<MessageVo>>() {

			@Override
			public List<MessageVo> execute() {
				// TODO Auto-generated method stub
				List<MessageVo> list = ProxyFactory.getInstance().getMessageProxy().getSubjectLastMessage();
				return list;
			}

			@Override
			public void onHandle(List<MessageVo> result) {
				// TODO Auto-generated method stub
				latestContactsData.clear();
				for (int i = 0; i < result.size(); i++) {
					MessageVo vo = result.get(i);
					if (vo.getChannelType().equals(MsgsConstants.MC_CHAT) && vo.getCategory().equals(MsgsConstants.MCC_CHAT)
							&& vo.getSubjectDomain().equals(MsgsConstants.DOMAIN_USER)) {
						// 对聊
						SubjectVo subVo = new SubjectVo();
						subVo.setSubjectId(vo.getSubjectId());
						subVo.setSubjectType(MsgsConstants.OT_USER);
						latestContactsData.add(subVo);
					} else if (vo.getChannelType().equals(MsgsConstants.MC_MCHAT) && vo.getCategory().equals(MsgsConstants.MCC_CHAT)
							&& vo.getSubjectDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
						// 公会群聊
						SubjectVo subVo = new SubjectVo();
						subVo.setSubjectId(vo.getSubjectId());
						subVo.setSubjectType(MsgsConstants.OT_GROUP);
						latestContactsData.add(subVo);
					}
				}
				latestContactsAdapter.notifyDataSetChanged();
				loadDataAfter();
			}
		});
	}

	private void loadDataUsers() {

		ProxyFactory.getInstance().getUserProxy().getContactUsers(new ProxyCallBack<List<UserVo>>() {

			@Override
			public void onSuccess(List<UserVo> result) {
				if (result != null) {
					List<UserVo> list = (List<UserVo>) result;
					result = sortUserByNickname(list);

					latestContactsData.clear();
					for (int i = 0; i < list.size(); i++) {
						UserVo vo = list.get(i);
						SubjectVo subVo = new SubjectVo();
						subVo.setSubjectId(vo.getUserid());
						subVo.setSubjectType(MsgsConstants.OT_USER);
						// subVo.setSubject(vo);
						latestContactsData.add(subVo);
					}
					latestContactsAdapter.notifyDataSetChanged();
				}
				loadDataAfter();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {

			}
		}, ForwardingShareSubjectListFragment.this.getActivity(), 1, 1, 0, 0, 0, searchKeyword);
	}

	/**
	 * 安装查找的用户昵称进行排序
	 * 
	 * @param list
	 * @return
	 */
	private List<UserVo> sortUserByNickname(List<UserVo> list) {
		try {
			Collections.sort(list, new Comparator<UserVo>() {

				@Override
				public int compare(UserVo o1, UserVo o2) {
					try {
						if (o1.getUsername() != null && o2.getUsername() != null) {
							String headChar = PinyinUtil.getPinYinHeadChar(o1.getUsername());
							char firstChar = headChar.toUpperCase().charAt(0);
							String headChar1 = PinyinUtil.getPinYinHeadChar(o2.getUsername());
							char firstChar1 = headChar1.toUpperCase().charAt(0);
							return String.valueOf(firstChar).compareTo(String.valueOf(firstChar1));
						} else {
							return 0;
						}
					} catch (Exception e) {
						LogUtil.e(TAG, e.getMessage());
						return 0;
					}
				}

			});
		} catch (Exception e) {
			LogUtil.e(TAG, e.getMessage());
		}
		return list;
	}

	private void loadDataGroup() {
		CacheCallBack cacheCallBack = new CacheCallBack() {

			@Override
			public void onBack(Object result) {
				// TODO Auto-generated method stub
				if (result != null) {
					List<GroupVo> list = (List<GroupVo>) result;

					latestContactsData.clear();
					for (int i = 0; i < list.size(); i++) {
						GroupVo vo = list.get(i);
						SubjectVo subVo = new SubjectVo();
						subVo.setSubjectId(vo.getGrid());
						subVo.setSubjectType(MsgsConstants.OT_GROUP);
						subVo.setSubject(vo);
						latestContactsData.add(subVo);
					}
					latestContactsAdapter.notifyDataSetChanged();
				}
				loadDataAfter();
			}
		};
		ProxyFactory.getInstance().getCache().getData(Cache.DATA_TYPE_CONTRANCT_GROUP, cacheCallBack);

	}

	/**
	 * 加载数据之后的操作
	 */
	private void loadDataAfter() {

		if (latestContactsData.size() == 0) {

			// 设置无数据时的界面
			nullContent.setVisibility(View.VISIBLE);
			if (forwardingSubjectListType == SystemConfig.FORWARDING_SUBJECT_LIST_TYPE_LATESTCONTACTS) {
				nullContentBgIcon.setBackgroundResource(R.drawable.common_no_seach_uers);
			} else if (forwardingSubjectListType == SystemConfig.FORWARDING_SUBJECT_LIST_TYPE_USERS) {
				nullContentBgIcon.setBackgroundResource(R.drawable.common_no_seach_uers);
			} else if (forwardingSubjectListType == SystemConfig.FORWARDING_SUBJECT_LIST_TYPE_GROUP) {
				nullContentBgIcon.setBackgroundResource(R.drawable.common_no_seach_group);
			}

		} else {
			nullContent.setVisibility(View.GONE);
		}

	}

}
