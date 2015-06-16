/**      
 * GroupManageUserListActivity.java Create on 2013-10-28     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseListActivity;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.group.adapter.GroupUserAdapter;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.StringUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: GroupManageUserListActivity
 * @Description: 通用公会管理用户列表
 * @author 王卫
 * @date 2013-10-28 上午10:23:31
 * @Version 1.0
 * 
 */
public class GroupManageUserListActivity extends BaseListActivity implements OnClickListener {

	protected static final String TAG = "GroupManageUserListActivity";

	// 界面类型(1批准成员列表2删除成员列表3管理员设置列表4邀请成员列表5转让公会列表)
	private int mode;
	// 公会ID
	private long grid;
	// 右边菜单
	private Button mRightMenu;
	// 当邀请成员和删除成员的时候弹出来的对话框
	private Dialog dialog;
	private TextView title;
	private EditText editWhat;
	private Button cleanBtn;
	private TextView wordsNum;
	private Button cancelBtn;
	private Button commitBtn;
	private LinearLayout editItem;
	private ImageView fengexian;
	private Button cleanbtn;
	private EditText editText;
	private String keyword = null;
	private InputMethodManager manager;
	private GroupUserAdapter groupUserAdapter;
	private LinearLayout nullContent;
	private PullToRefreshListView pullToRefreshListView;
	private List<UserItemObj> inviteObjList;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseListActivity#initialize()
	 */
	@Override
	protected void initialize() {
		setViews();
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mode = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOOD);
			grid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);
		}
		// 显示左边
		setLeftVisible(true);
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		// 设置内容UI
		contentView.removeAllViews();
		View view = (LinearLayout) View.inflate(this, R.layout.group_user_list, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(view, params);
		editItem = (LinearLayout) view.findViewById(R.id.show_edittext);
		fengexian = (ImageView) view.findViewById(R.id.fengexian);
		cleanbtn = (Button) view.findViewById(R.id.cleanBtn);
		cleanbtn.setVisibility(View.GONE);
		nullContent = (LinearLayout) view.findViewById(R.id.null_content);
		editText = (EditText) view.findViewById(R.id.searchTxt);
		manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.refreshList);
		setPullRefreshListView(pullToRefreshListView);
		groupUserAdapter = new GroupUserAdapter(this, listData, mode, grid,list);
		setListAndAdapter(list, groupUserAdapter);
		// 添加列表点击功能
		setListItemClikEvent(list);
		setListener();
		if (mode == GroupUserAdapter.MODE_TRANSFER || mode == GroupUserAdapter.MODE_USERLIST) {
			// 影藏右边
			setRightVisible(false);
		} else {
			// 显示右边
			setRightVisible(true);
			// 添加右边功能按钮
			mRightMenu = new Button(this);
			LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
			rightView.addView(mRightMenu);
			mRightMenu.setOnClickListener(this);
			StateListDrawable stateListDrawable = null;
			if (mode == GroupUserAdapter.MODE_APPLY)
				mRightMenu.setBackgroundResource(R.drawable.group_userlist_clean_menu);
			else if (mode == GroupUserAdapter.MODE_DELED)
				mRightMenu.setBackgroundResource(R.drawable.group_userlist_delete_menu);
			else if (mode == GroupUserAdapter.MODE_SETTING)
				mRightMenu.setBackgroundResource(R.drawable.common_add_menu);
			else if (mode == GroupUserAdapter.MODE_INVITE) {
				mRightMenu.setBackgroundResource(R.drawable.group_userlist_invite_menu);
				pullToRefreshListView.setMode(Mode.DISABLED);
			}
		}
		// 设置TITLE
		if (mode == GroupUserAdapter.MODE_APPLY) {
			mRightMenu.setVisibility(View.INVISIBLE);
			titleTxt.setText("批准成员");
			editItem.setVisibility(View.GONE);
			fengexian.setVisibility(View.GONE);
		} else if (mode == GroupUserAdapter.MODE_DELED)
			titleTxt.setText("删除成员");
		else if (mode == GroupUserAdapter.MODE_SETTING)
			titleTxt.setText("添加管理员");
		else if (mode == GroupUserAdapter.MODE_INVITE) {
			titleTxt.setText("邀请成员");
			editItem.setVisibility(View.VISIBLE);
			fengexian.setVisibility(View.VISIBLE);
		} else if (mode == GroupUserAdapter.MODE_TRANSFER)
			titleTxt.setText("转让公会");
		else if (mode == GroupUserAdapter.MODE_USERLIST)
			titleTxt.setText("公会成员");
	}

	/**
	 * 给界面上的按钮 设置监听器
	 */
	private void setListener() {
		/**
		 * 如果文字变化的话 则重新请求数据加载
		 */
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if (editText.getText().length() == 0) {
					cleanbtn.setVisibility(View.GONE);
					keyword = null;
				} else {
					cleanbtn.setVisibility(View.VISIBLE);
					keyword = editText.getText().toString();
				}
				refreshList();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

		cleanbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				editText.setText("");
			}
		});
	}

	protected void refreshList() {
		if (listData != null && adapter != null) {
			if (offsetMode == 1)
				mOffset = Long.MAX_VALUE;
			else if (offsetMode == 0)
				mOffset = 0;
			hasNext = true;
			listData.clear();
			getListData(mOffset, mLimit);
		} else {
			return;
		}

	}

	/**
	 * 显示加载的loading
	 */
	public void setLoadingUI() {
		nullContent.setVisibility(View.VISIBLE);
		pullToRefreshListView.setVisibility(View.GONE);
		nullContent.removeAllViews();
		LinearLayout view = (LinearLayout) View.inflate(this, R.layout.frame_donghua, null);
		view.setBackgroundColor(getResources().getColor(R.color.set_first_bg));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ImageView ivLoading = (ImageView) view.findViewById(R.id.loading_iv);
		AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		animationDrawable.start();
		nullContent.addView(view, params);
	}

	/**
	 * 初始化邀请成员 和删除成员 所弹出来的对话框
	 */
	private void setViews() {
		dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		View view = View.inflate(this, R.layout.dialog_card, null);
		title = (TextView) view.findViewById(R.id.title);
		editWhat = (EditText) view.findViewById(R.id.edit_group_card);
		cleanBtn = (Button) view.findViewById(R.id.act_login_cleanAccountBtn);
		cleanBtn.setVisibility(View.GONE);
		wordsNum = (TextView) view.findViewById(R.id.edit_word_num);
		wordsNum.setText("0/20");
		cancelBtn = (Button) view.findViewById(R.id.cannelBtn);
		cancelBtn.setText("取消");
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mRightMenu.setEnabled(true);
				editWhat.setText("");
				dialog.dismiss();
			}
		});
		commitBtn = (Button) view.findViewById(R.id.commitBtn);
		editWhat.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String source = editWhat.getText().toString();
				int sourceLen = StringUtil.getCharacterNum(source.toString());
				if (sourceLen > 0) {
					double length = Math.ceil(sourceLen / 2.0);
					wordsNum.setText((int) length + "/20");
				} else {
					wordsNum.setText("0/20");
				}
			}
		});
		dialog.setContentView(view);
	}

	@Override
	protected void onResume() {
		super.onResume();
		onresumeNeedRefresh = false;
	}

	/**
	 * 添加列表点击功能
	 * 
	 * @param list
	 */
	protected void setListItemClikEvent(ListView list) {
		// 添加列表点击功能
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
				Object uid = map.get("uid");
				if (uid != null && !uid.equals(SystemContext.getInstance().getExtUserVo().getUserid())) {
					if(mode == GroupUserAdapter.MODE_INVITE){
						Intent intent = new Intent(parent.getContext(), UserDetailInfoActivity.class);
						Bundle bundle = new Bundle();
						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, Long.valueOf(uid.toString()));
						bundle.putBoolean(SystemConfig.IS_FROM_FOLLOWS_LISTVIEW, true);
						bundle.putString(SystemConfig.USER_FOLLOW_REMARK_NAME,map.get("remarkname"));
						intent.putExtras(bundle);
						startActivity(intent);
					
					}else{
						Intent intent = new Intent(parent.getContext(), UserDetailInfoActivity.class);
						Bundle bundle = new Bundle();
						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, Long.valueOf(uid.toString()));
						intent.putExtras(bundle);
						startActivity(intent);
					}
				} else {
					ToastUtil.showToast(GroupManageUserListActivity.this, getString(R.string.check_information));
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseListActivity2#getListData(long, int)
	 */
	@Override
	protected void getListData(long offset, int limit) {
		super.getListData(offset, limit);
		groupUserAdapter.setFlag(true);
		hasNext = false;
		if (listData.size() <= 0)
			setLoadingUI();
		ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_GROUP_USER, grid, new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				groupUserAdapter.clean();
				if (mode == GroupUserAdapter.MODE_APPLY)
					getApplyUsers(grid);
				else if (mode == GroupUserAdapter.MODE_DELED) {
					getGroupUsers(grid);
				} else if (mode == GroupUserAdapter.MODE_SETTING) {
					getGroupUsers(grid);
				} else if (mode == GroupUserAdapter.MODE_INVITE) {
					if (keyword == null || "".equals(keyword)) {
						getInviteUsers(grid);
					} else {
						getUserData(grid,1, 1, keyword);
					}
				} else if (mode == GroupUserAdapter.MODE_TRANSFER) {
					getGroupUsers(grid);
				} else if (mode == GroupUserAdapter.MODE_USERLIST) {
					getGroupUsers(grid);
				}
			}

			@Override
			public void onFailure(Integer result) {
				onFooterRefreshComplete();
			}
		});
	}

	/**
	 * 去查询数据
	 * 
	 * @param i
	 * @param j
	 * @param keyword2
	 */
	protected void getUserData(long grid,int i, int j, String keyword2) {
		getUserContactData(grid,i, j, keyword);
	}

	/**
	 * 真正的执行下面的这 个方法去查询数据
	 * 
	 * @param i
	 * @param j
	 * @param keyword2
	 * @param dialog2
	 */
	private void getUserContactData(long grid,int i, int j, String keyword2) {
		ProxyFactory.getInstance().getUserProxy().getFollowUsers(new ProxyCallBack<List<UserItemObj>>() {

			@Override
			public void onSuccess(List<UserItemObj> result) {
				showListview();
				if (result != null && result.size() > 0) {
					inviteObjList = result;
					pullToRefreshListView.setVisibility(View.VISIBLE);
					nullContent.setVisibility(View.GONE);
					// 添加设置LIST数据
					listData.clear();
					addListData(result);
				} else {
					hasNext = false;
					showNullView();
					LogUtil.d(TAG, "数据为空");
				}
				dialog.dismiss();
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				showNullView();
				dialog.dismiss();
				onFooterRefreshComplete();
			}
		}, this, 1, 1, 1, 0, Integer.MAX_VALUE, keyword,grid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// 点击右边菜单
		if (v.getId() == mRightMenu.getId()) {
			mRightMenu.setEnabled(false);
			// 清空批准成员
			if (mode == GroupUserAdapter.MODE_APPLY) {
				if (listData.size() > 0) {
					cleanApproveUsers();
				} else {
					mRightMenu.setEnabled(true);
				}
			}
			// 删除公会成员
			else if (mode == GroupUserAdapter.MODE_DELED) {
				String content = getCheckedUsers(listData);
				if (content != null) {
					popDialog(0, content);
				} else {
					ToastUtil.showToast(this, getString(R.string.group_unselectde_user));
					mRightMenu.setEnabled(true);
				}
			}
			// 添加设置管理员
			else if (mode == GroupUserAdapter.MODE_SETTING) {
				String content = getCheckedUsers(listData);
				if (content != null)
					groupUsersAction(MsgsConstants.OP_ADD_ADMIN, content, null);
				else {
					ToastUtil.showToast(this, getString(R.string.group_unselectde_user));
					mRightMenu.setEnabled(true);
				}
			}
			// 邀请会员
			else if (mode == GroupUserAdapter.MODE_INVITE) {
				String content = getCheckedUsers(listData);
				if (content != null) {
					popDialog(1, content);
				} else {
					ToastUtil.showToast(this, getString(R.string.group_unselectde_user));
					mRightMenu.setEnabled(true);
				}
			}
		}
	}

	/**
	 * 当邀请公会成员和删除公会成员的时候 都调用下面的这个方法弹框
	 * 
	 * @param key
	 */
	private void popDialog(final int key, final String content) {
		// key 如果为1 表示邀请 key 如果为0 表示删除
		if (key == 1) {
			InputFilterUtil.lengthFilter(this, editWhat, 40, "邀请理由上限为20个字哦！");
			title.setText("请输入邀请理由");
			commitBtn.setText("提交邀请");
		} else if (key == 0) {
			title.setText("请输入删除理由");
			InputFilterUtil.lengthFilter(this, editWhat, 40, "删除理由上限为20个字哦！");
			commitBtn.setText("提交删除");
		}
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mRightMenu.setEnabled(true);
				String remark = editWhat.getText().toString();
				editWhat.setText("");
				dialog.dismiss();
				inviteOrDelete(key, content, remark);
			}
		});
		dialog.show();
	}

	/**
	 * 邀请成员 或者删除成员
	 * 
	 * @param key
	 */
	protected void inviteOrDelete(int key, String content, String remark) {
		if (key == 1) {
			groupUsersAction(MsgsConstants.OP_INVITE_MEMBER, content, remark);
			UMUtil.sendEvent(contentView.getContext(), UMConfig.MSGS_EVENT_GROUP_INVITE, null, null, null, null, null);
		} else if (key == 0) {
			groupUsersAction(MsgsConstants.OP_DELETE_MEMBER, content, remark);
			UMUtil.sendEvent(contentView.getContext(), UMConfig.MSGS_EVENT_GROUP_DEL, null, null, null, null, null);
		}
	}

	/**
	 * 获取申请的用户列表数据
	 * 
	 * @param grid
	 */
	private void getApplyUsers(long grid) {
		ProxyFactory.getInstance().getGroupProxy().getApplyUsers(new ProxyCallBack<List<UserItemObj>>() {

			@Override
			public void onSuccess(List<UserItemObj> result) {
				showListview();
				if (result != null && result.size() > 0) {
					hasNext = false;
					mRightMenu.setVisibility(View.VISIBLE);
					// 添加设置LIST数据
					addListData(result);
				} else {
					hasNext = false;
					if (listData.size() == 0) {
						setNoDataUI();
						mRightMenu.setVisibility(View.INVISIBLE);
					}
					LogUtil.d(TAG, "数据为空");
				}
				dialog.dismiss();
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (listData.size() == 0) {
					setNoDataUI();
					mRightMenu.setVisibility(View.INVISIBLE);
				}
				dialog.dismiss();
				onFooterRefreshComplete();
			}
		}, this, grid);
	}

	/**
	 * 获取公会成员 列表数据
	 * 
	 * @param grid
	 */
	private void getGroupUsers(long grid) {
		ProxyFactory.getInstance().getGroupProxy().getGroupUsers(new ProxyCallBack<List<UserItemObj>>() {

			@Override
			public void onSuccess(List<UserItemObj> result) {
				showListview();
				hasNext = false;
				listData.clear();
				if (result != null && result.size() > 0) {
					// 添加设置LIST数据
					addListData(result);
				} else {
					hasNext = false;
					showNullBgView(null);
					LogUtil.d(TAG, "数据为空");
				}
				dialog.dismiss();
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				showNullBgView(null);
				dialog.dismiss();
				onFooterRefreshComplete();
			}
		}, this, grid);
	}

	/**
	 * 当搜索不到用户数据的时候，则 显示默认的背景图片
	 */
	private void showNullView() {
		pullToRefreshListView.setVisibility(View.GONE);
		nullContent.removeAllViews();
		View v = View.inflate(this, R.layout.user_null_data_bg, null);
		((TextView) v.findViewById(R.id.desc)).setText("很抱歉，没有找到相关用户");
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		nullContent.addView(v, params);
		nullContent.setVisibility(View.VISIBLE);

	}

	/**
	 * 获取可邀请的用户列表数据
	 * 
	 * @param grid
	 */
	private void getInviteUsers(long grid) {
		ProxyFactory.getInstance().getGroupProxy().getInviteUsers(new ProxyCallBack<List<UserItemObj>>() {

			@Override
			public void onSuccess(List<UserItemObj> result) {
				showListview();
				if (result != null && result.size() > 0) {
					inviteObjList = result;
					pullToRefreshListView.setVisibility(View.VISIBLE);
					nullContent.setVisibility(View.GONE);
					// 添加设置LIST数据
					addListData(result);
				} else {
					hasNext = false;
					showNullView();
					LogUtil.d(TAG, "数据为空");
				}
				dialog.dismiss();
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				showNullView();
				dialog.dismiss();
				onFooterRefreshComplete();
			}
		}, this, grid);
	}

	/**
	 * 添加列表数据
	 * 
	 * @param result
	 */
	private void addListData(List<UserItemObj> list) {
		// 添加设置LIST数据
		listData.addAll(praseList(list));
		adapter.notifyDataSetChanged();
	}

	/**
	 * 解析用户数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseList(List<UserItemObj> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		if (list != null) {
			boolean isAddPreTag = false;
			boolean isAddManagerTag = false;
			boolean isAddUserTag = false;
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				UserItemObj vo = list.get(i);
				if (mode == GroupUserAdapter.MODE_DELED || mode == GroupUserAdapter.MODE_SETTING || mode == GroupUserAdapter.MODE_TRANSFER
						|| mode == GroupUserAdapter.MODE_USERLIST) {
					if (vo.getRel() == 3) {// 会长
						if (!isAddPreTag) {
							Map<String, Object> tmap = new HashMap<String, Object>();
							tmap.put("tag", "会长");
							isAddPreTag = true;
							tmplist.add(tmap);
						}
					} else if (vo.getRel() == 2) {// 管理员
						if (!isAddManagerTag) {
							Map<String, Object> tmap = new HashMap<String, Object>();
							tmap.put("tag", "管理员");
							isAddManagerTag = true;
							tmplist.add(tmap);
						}
					} else if (vo.getRel() == 1) {// 会员
						if (!isAddUserTag) {
							Map<String, Object> tmap = new HashMap<String, Object>();
							tmap.put("tag", "成员");
							isAddUserTag = true;
							tmplist.add(tmap);
						}
					}
				}
				map.put("uid", vo.getUid());
				map.put("nickname", vo.getNickname());
				if (vo.getAvatar() != null)
					map.put("avatar", vo.getAvatar());
				if (vo.getSex() != -1) {
					map.put("sex", vo.getSex());
				}
				if (vo.getAge() > 0) {
					map.put("age", vo.getAge());
				}
				if (vo.getMood() != null)
					map.put("mood", vo.getMood());
				map.put("status", vo.getStatus());
				map.put("isChecked", vo.isChecked());
				map.put("rel", vo.getRel());
				map.put("grade", vo.getGrade());
				if (mode == GroupUserAdapter.MODE_INVITE) {
					map.put("remark", vo.getRemark());
				}
				tmplist.add(map);
			}
		}
		return tmplist;
	}

	/**
	 * 清空批准成员
	 */
	private void cleanApproveUsers() {
		// 清空列表
		if (listData != null && adapter != null) {
			listData.clear();
			adapter.notifyDataSetChanged();
			adapter.notifyDataSetInvalidated();
		}
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		// 操作数据库,保存清空标识
		ProxyFactory.getInstance().getGroupProxy().cleanApproveUsers(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					ToastUtil.showToast(GroupManageUserListActivity.this, getString(R.string.group_op_success));
					break;
				default:
					break;
				}
				dialog.dismiss();
				mRightMenu.setEnabled(true);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				dialog.dismiss();
				mRightMenu.setEnabled(true);
			}
		}, this, grid);
	}

	/**
	 * 删除公会会员,设置公会管理员,邀请公会成员
	 */
	private void groupUsersAction(int op, String content, String remark) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					if (mode == GroupUserAdapter.MODE_DELED) {
						ToastUtil.showToast(GroupManageUserListActivity.this, getString(R.string.group_delete_user_success));
						refreshList();
					} else if (mode == GroupUserAdapter.MODE_SETTING) {
						ToastUtil.showToast(GroupManageUserListActivity.this, getString(R.string.group_set_manager_success));
						refreshList();
					} else if (mode == GroupUserAdapter.MODE_INVITE) {
						ToastUtil.showToast(GroupManageUserListActivity.this, getString(R.string.group_invite_success));
						listData.clear();
						listData.addAll(praseList(inviteObjList));
						adapter.notifyDataSetChanged();
						adapter.notifyDataSetInvalidated();
					}
					break;
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_MSGS_GROUP_PART_NOT_IN_VALUE:
					ToastUtil.showToast(GroupManageUserListActivity.this, getString(R.string.group_set_manager_not_join_fail));
					break;
				default:
					if (mode == GroupUserAdapter.MODE_DELED)
						ToastUtil.showToast(GroupManageUserListActivity.this, getString(R.string.group_delete_user_fail));
					else if (mode == GroupUserAdapter.MODE_SETTING) {
						ToastUtil.showToast(GroupManageUserListActivity.this, getString(R.string.group_set_manager_fail));
					} else if (mode == GroupUserAdapter.MODE_INVITE)
						ToastUtil.showToast(GroupManageUserListActivity.this, getString(R.string.group_invite_fail));
					break;
				}
				dialog.dismiss();
				mRightMenu.setEnabled(true);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (mode == GroupUserAdapter.MODE_DELED)
					ToastUtil.showToast(GroupManageUserListActivity.this, getString(R.string.group_delete_user_fail));
				else if (mode == GroupUserAdapter.MODE_SETTING) {
					if (result == com.iwgame.msgs.proto.Msgs.ErrorCode.EC_MSGS_GROUP_PART_NOT_IN_VALUE) {
						ToastUtil.showToast(GroupManageUserListActivity.this, getString(R.string.group_set_manager_not_join_fail));
					} else {
						ToastUtil.showToast(GroupManageUserListActivity.this, getString(R.string.group_set_manager_fail));
					}
				} else if (mode == GroupUserAdapter.MODE_INVITE) {
					if (result == ErrorCode.EC_CLIENT_INVITE_ERROR_CODE) {
						ToastUtil.showToast(GroupManageUserListActivity.this, "对方已加入公会！");
					} else {
						ToastUtil.showToast(GroupManageUserListActivity.this, getString(R.string.group_invite_fail));
					}
				}
				dialog.dismiss();
				mRightMenu.setEnabled(true);
			}
		}, this, grid, MsgsConstants.OT_GROUP, op, content, null, remark);
	}

	/**
	 * 设置添加公会
	 */
	public void setNoDataUI() {
		nullContent.setVisibility(View.VISIBLE);
		nullContent.removeAllViews();
		LinearLayout view = (LinearLayout) View.inflate(this, R.layout.user_null_data_bg, null);
		TextView desc = (TextView) view.findViewById(R.id.desc);
		desc.setText("还没有人申请加入公会");
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		nullContent.addView(view, params);
	}

	/**
	 * 显示列表的内容
	 */
	private void showListview() {
		nullContent.setVisibility(View.GONE);
		pullToRefreshListView.setVisibility(View.VISIBLE);
	}

	/**
	 * 
	 * @return
	 */
	private String getCheckedUsers(List<Map<String, Object>> tmplist) {
		if (tmplist != null) {
			StringBuilder strBuf = new StringBuilder();
			int size = tmplist.size();
			for (int i = 0; i < size; i++) {
				Set<Map.Entry<String, Object>> set = ((Map<String, Object>) tmplist.get(i)).entrySet();
				for (Iterator<Map.Entry<String, Object>> it = set.iterator(); it.hasNext();) {
					Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
					if (entry.getKey().equals("isChecked") && (Boolean) entry.getValue()) {
						strBuf.append(tmplist.get(i).get("uid").toString());
						strBuf.append(",");
						break;
					}
				}
			}
			if (strBuf.length() > 0)
				return strBuf.substring(0, strBuf.length() - 1);
			return null;
		} else {
			return null;
		}
	}

}
