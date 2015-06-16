package com.iwgame.msgs.module.group.ui;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.GroupGradeDao;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.chat.ui.GroupChatFragmentActivity;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.module.guide.GuideActivity;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.GuideUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GroupGradeVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * 贡献积分页面
 * 
 * @author Administrator
 * 
 */
public class ContributePointActivity extends BaseActivity {

	private EditText donateEdit;
	private Button cofirmBtn;
	private TextView groupPointNum;
	private TextView userPointNum;
	private TextView contributeRank;
	private long groupid;
	private Dialog dialog;
	private Button cancelBtn;
	private TextView cueWordsSuccess;
	private TextView cueWordsFail;
	private LinearLayout showList;
	private LinearLayout noContriIv;
	private InputMethodManager manager;
	private int CONTRIBUTE_LIMITE_NUMBER = 500;
	private TextView contriRange;// 贡献范围描述
	private TextView contriLimiteDes;// 贡献积分上限描述
	private CustomProgressDialog contridialog;// 贡献积分的时候弹出的dialog
	private TextView groupDesc;
	private GroupGradeVo groupGradeVo;
	private GroupGradeDao groupGradeDao;
	private boolean ispres;
	private int groupGrade;

	/**
	 * 界面启动的时候执行此方法
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GuideUtil.startGuide(this, GuideActivity.GUIDE_MODE_GROUP_POINT);
		initialize();
	}

	/**
	 * 在onresume方法里面隐藏 输入法面板
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// 获取输法manager
		manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(donateEdit.getWindowToken(), 0);
		cofirmBtn.setEnabled(true);
	}

	/**
	 * 在此方法里面 做一些初始化工作 加载排行榜的数据
	 */
	private void initialize() {
		groupGradeDao = DaoFactory.getDaoFactory().getGroupGradeDao(SystemContext.getInstance().getContext());
		groupid = getIntent().getLongExtra(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, 0);
		ispres = getIntent().getBooleanExtra(SystemConfig.IF_NOT_SHOW_GROUP_CARD, false);
		// 设置标题
		setTitleTxt(getString(R.string.groupchat_menu_point_info));
		// 加载view到布局中
		View view = View.inflate(this, R.layout.contribute_integral_activity, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(view, params);
		// 初始化参数
		donateEdit = (EditText) view.findViewById(R.id.edit_donate_integral_number);
		donateEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String s = donateEdit.getText().toString();
				if (s != null && !"".equals(s)) {
					if (s.startsWith("0")) {
						donateEdit.setText("");
					}
					String userPoint = userPointNum.getText().toString();
					int num = Integer.parseInt(s);
					if (num > Integer.parseInt(userPoint)) {
						cueWordsSuccess.setText("您的积分余额不足！");
						cueWordsFail.setVisibility(View.GONE);
						int lenght = s.length();
						donateEdit.setText(s.substring(0, lenght - 1));
						donateEdit.setSelection(lenght - 1);
						dialog.show();
					}
				}
			}
		});
		cofirmBtn = (Button) view.findViewById(R.id.confirm_donate_integral);
		groupPointNum = (TextView) view.findViewById(R.id.current_group_total_integral);
		userPointNum = (TextView) view.findViewById(R.id.remain_integral);
		contributeRank = (TextView) view.findViewById(R.id.rank_of_mine);
		showList = (LinearLayout) view.findViewById(R.id.show_list);
		noContriIv = (LinearLayout) view.findViewById(R.id.no_contribute_iv);
		contriLimiteDes = (TextView) view.findViewById(R.id.contri_point_limite_descri);
		contriRange = (TextView) view.findViewById(R.id.contri_point_range);
		groupDesc = (TextView) view.findViewById(R.id.group_desc);
		setView();// 先初始化界面的一些控件 的值
		getContriLimite();// 获取每日贡献的阀值显示到手机界面上
		setListener();// 设置监听器
		contridialog.show();
		syncGroupMember();// 加载贡献排行榜的数据
	}

	/**
	 * 获取每日用户 能够贡献给公会的最大值 显示到手机界面上
	 */
	private void getContriLimite() {
		GroupDao dao = DaoFactory.getDaoFactory().getGroupDao(this);
		GroupVo vo = dao.findGroupByGrid(groupid);
		if (vo != null) {
			groupGrade = vo.getGrade();
			getGroupGradeInfo(vo.getGrade());
		} else {
			ServiceFactory.getInstance().getSyncEntityService().syncEntity(groupid, SyncEntityService.TYPE_GROUP, new SyncCallBack() {
				@Override
				public void onSuccess(Object res) {
					GroupVo result = (GroupVo) res;
					if (result != null) {
						groupGrade = result.getGrade();
						getGroupGradeInfo(result.getGrade());
					}
				}

				@Override
				public void onFailure(Integer result) {
				}
			});
		}
	}

	/**
	 * 获取公会 等级的划分信息
	 */
	private void getGroupGradeInfo(final int grade) {
		GroupGradeDao dao = DaoFactory.getDaoFactory().getGroupGradeDao(this);
		List<GroupGradeVo> groupGradeVoList = dao.queryAll();
		if (groupGradeVoList != null && groupGradeVoList.size() > 0) {
			int length = groupGradeVoList.size();
			for (int i = 0; i < length; i++) {
				GroupGradeVo vo = groupGradeVoList.get(i);
				if (grade == vo.getGrade()) {
					CONTRIBUTE_LIMITE_NUMBER = vo.getUserlimit();
					contriLimiteDes.setText("每日最多只能贡献" + vo.getUserlimit() + "积分");
					contriRange.setText("输入的积分必须在这个范围：1≤积分≤" + vo.getUserlimit());
					if (grade == groupGradeVoList.size()) {
						groupGradeVo = null;
					} else {
						groupGradeVo = groupGradeVoList.get(i + 1);
					}
					getgroupPoint();
					break;
				}
			}
		} else {
			ProxyFactory.getInstance().getGroupProxy().getGroupGradePolicy(new ProxyCallBack<List<GroupGradeVo>>() {
				@Override
				public void onSuccess(List<GroupGradeVo> result) {
					int length = result.size();
					for (int i = 0; i < length; i++) {
						GroupGradeVo vo = result.get(i);
						if (grade == vo.getGrade()) {
							CONTRIBUTE_LIMITE_NUMBER = vo.getUserlimit();
							contriLimiteDes.setText("每日最多只能贡献" + vo.getUserlimit() + "积分");
							contriRange.setText("输入的积分必须在这个范围：1≤积分≤" + vo.getUserlimit());
							if (grade == result.size()) {
								groupGradeVo = null;
							} else {
								groupGradeVo = result.get(i + 1);
							}
							getgroupPoint();
							break;
						}
					}
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					handleErrorCode(result, resultMsg);
				}
			}, ContributePointActivity.this);
		}
	}

	/**
	 * 初始化界面的控件 里面的值 以及贡献成功后 所弹框的dialog
	 */
	private void setView() {
		groupDesc.setText("公会等级：LV" + "   升级还需： " + 0 + "积分");
		getPoint();
		contributeRank.setText("您当前贡献的总积分: 0");
		showList.setVisibility(View.VISIBLE);
		noContriIv.setVisibility(View.GONE);
		dialog = new Dialog(ContributePointActivity.this, R.style.SampleTheme_Light);
		View view = View.inflate(ContributePointActivity.this, R.layout.dialog_integral, null);
		contridialog = CustomProgressDialog.createDialog(this, false);
		dialog.setContentView(view);
		cancelBtn = (Button) view.findViewById(R.id.i_know_it);
		cueWordsSuccess = (TextView) view.findViewById(R.id.cue_words);
		cueWordsFail = (TextView) view.findViewById(R.id.other_cue_words);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 获取用户的 积分信息 将积分数值显示到界面上
	 */
	private void getPoint() {
		ProxyFactory.getInstance().getUserProxy().getUserPoint(new ProxyCallBack<List<ExtUserVo>>() {
			@Override
			public void onSuccess(List<ExtUserVo> result) {
				int point = 0;
				if (result != null && result.size() == 1) {
					point = result.get(0).getPoint();
				}
				userPointNum.setText(String.valueOf(point));
				SystemContext.getInstance().setPoint(point);

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				userPointNum.setText(0 + "");
			}
		}, this, SystemContext.getInstance().getExtUserVo().getUserid() + "");
	}

	/**
	 * 获取公会的积分 显示在手机的界面
	 */
	private void getgroupPoint() {
		ProxyFactory.getInstance().getGroupProxy().getGroupPoint(new ProxyCallBack<List<GroupVo>>() {

			@Override
			public void onSuccess(List<GroupVo> result) {
				if (result != null && result.size() > 0) {
					GroupVo vo = result.get(0);
					groupPointNum.setText(vo.getPoint() + "");
					if (vo.getPoint() <= 0) {
						groupDesc.setText("公会等级：LV" + groupGrade + "   升级还需： " + vo.getPoint() + "积分");
					} else if (groupGradeVo != null && groupGradeVo.getPoint() > vo.getPoint()) {
						groupDesc.setText("公会等级：LV" + groupGrade + "   升级还需： " + (groupGradeVo.getPoint() - vo.getPoint()) + "积分");
					} else {
						groupDesc.setText("公会等级：LV" + groupGrade + "   升级还需： " + 0 + "积分");
					}
				} else {
					groupPointNum.setText("0");
					GroupGradeVo vo = groupGradeDao.queryByGrade(groupGrade + 1);
					if (vo != null) {
						groupDesc.setText("公会等级：LV" + groupGrade + "   升级还需： " + vo.getPoint() + "积分");
					} else {
						groupDesc.setText("公会等级：LV" + groupGrade + "   已满级啦");
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				handleErrorCode(result, resultMsg);
			}
		}, ContributePointActivity.this, groupid + "");
	}

	/**
	 * 获取贡献排行榜
	 */
	private void getContributeRank() {
		ProxyFactory.getInstance().getGroupProxy().getGroupContributePointTop(new ProxyCallBack<List<UserItemObj>>() {

			@Override
			public void onSuccess(List<UserItemObj> result) {
				contridialog.dismiss();
				if (result != null && result.size() > 0) {
					addDataToLayout(result);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				contridialog.dismiss();
				handleErrorCode(result, resultMsg);
			}
		}, ContributePointActivity.this, groupid, Integer.MAX_VALUE);
	}

	/**
	 * 同步公会成员 在获取公会的贡献排行榜的时候 先去同步数据 在去查询
	 */
	private void syncGroupMember() {
		GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
		GroupVo vo = groupDao.findGroupByGrid(groupid);
		long time = (vo == null ? 0 : vo.getUreltime());
		ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_GROUP_USER, groupid, time, new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				// 同步成功了后 在去获取数据
				getContributeRank();
			}

			@Override
			public void onFailure(Integer result) {
				contridialog.dismiss();
				handleErrorCode(result, null);
			}
		});
	}

	/**
	 * 添加数据到linearLayout里面 并且给添加进去的每一个item都添加事件跳转
	 * 
	 * @param result
	 */
	protected void addDataToLayout(final List<UserItemObj> result) {
		showList.removeAllViews();
		// 获取到数据显示自己的用户排名
		boolean flag = false;
		// 标记获取贡献排行榜 是否有人贡献
		boolean temp = false;
		int size = result.size();
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		for (int i = 0; i < size; i++) {
			final UserItemObj obj = result.get(i);
			// 首先是只显示前10名的排行
			if (i < 10) {
				final View view = View.inflate(ContributePointActivity.this, R.layout.contribute_integral_item_view, null);
				// 给添加进去的每一个item都添加一个事件跳转
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (SystemContext.getInstance().getExtUserVo().getUserid() == obj.getUid()) {
							ToastUtil.showToast(ContributePointActivity.this, getString(R.string.check_information));
							return;
						}
						Intent intent = new Intent(ContributePointActivity.this, UserDetailInfoActivity.class);
						Bundle bundle = new Bundle();
						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, obj.getUid());
						bundle.putBoolean(SystemConfig.IF_NOT_SHOW_GROUP_CARD, true);
						bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GROUP_CARD, obj.getRemark());
						if (ispres) {
							bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_STOP_SAY_WORDS, true);
							bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, groupid);
						}
						intent.putExtras(bundle);
						ContributePointActivity.this.startActivity(intent);
					}
				});
				final ImageView touxiang = (ImageView) view.findViewById(R.id.icon);
				final TextView age = (TextView) view.findViewById(R.id.contribute_integral_age);
				final TextView nichen = (TextView) view.findViewById(R.id.contribute_integral_people_name);
				ImageView rank = (ImageView) view.findViewById(R.id.contribute_integral_rank_pic);
				TextView top = (TextView) view.findViewById(R.id.rank_id);
				TextView number = (TextView) view.findViewById(R.id.contribute_point_number);
				if (obj.getTop() == 1) {
					rank.setBackgroundResource(R.drawable.group_no1);
					rank.setVisibility(View.VISIBLE);
					top.setVisibility(View.GONE);
				} else if (obj.getTop() == 2) {
					rank.setBackgroundResource(R.drawable.group_no2);
					rank.setVisibility(View.VISIBLE);
					top.setVisibility(View.GONE);
				} else if (obj.getTop() == 3) {
					rank.setBackgroundResource(R.drawable.group_no3);
					rank.setVisibility(View.VISIBLE);
					top.setVisibility(View.GONE);
				} else {
					rank.setVisibility(View.GONE);
					top.setVisibility(View.VISIBLE);
					top.setText("No." + obj.getTop());
				}
				number.setText(obj.getPoint() + "");
				if (obj.getPoint() > 0) {
					showList.addView(view);
					temp = true;
				}

				nichen.setText(obj.getRemark() + "");
				UserDao userdao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
				UserVo vo = userdao.getUserById(obj.getUid());
				// 获取用户的更详细的信息
				p.clear();
				cdp.clear();
				cdp.setId(obj.getUid());
				cdp.setUtime(vo == null ? 0 : vo.getUpdatetime());
				p.addParam(cdp.build());
				ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(new ProxyCallBack<List<UserVo>>() {

					@Override
					public void onSuccess(List<UserVo> uservoresult) {
						if (uservoresult == null && uservoresult.size() <= 0)
							return;
						UserVo result = uservoresult.get(0);
						if (result != null) {
							if (result.getAvatar() != null) {
								ImageViewUtil.showImage(touxiang, result.getAvatar(), R.drawable.common_default_icon);
							}
							if (nichen.getText().toString() == null || "".equals(nichen.getText().toString())) {
								nichen.setText(result.getUsername());
							}
							if (result.getAge() > 0) {
								age.setText(AgeUtil.convertAgeByBirth(result.getAge()) + "");
								age.setVisibility(View.VISIBLE);

								if (result.getSex() == 0) {
									Drawable sexdraw = ContributePointActivity.this.getResources().getDrawable(R.drawable.user_man_icon);
									sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
									age.setCompoundDrawables(sexdraw, null, null, null);
									age.setCompoundDrawablePadding(DisplayUtil.dip2px(ContributePointActivity.this, 4));
									age.setBackgroundResource(R.drawable.common_item_jh2_shap);
								} else if (result.getSex() == 1) {
									Drawable sexdraw = ContributePointActivity.this.getResources().getDrawable(R.drawable.user_woman_icon);
									sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
									age.setCompoundDrawables(sexdraw, null, null, null);
									age.setCompoundDrawablePadding(DisplayUtil.dip2px(ContributePointActivity.this, 4));
									age.setBackgroundResource(R.drawable.common_item_jh_shap);
								}
							} else {
								age.setText("");
								age.setCompoundDrawablePadding(0);
								if (result.getSex() == 0) {
									Drawable sexdraw = ContributePointActivity.this.getResources().getDrawable(R.drawable.user_man_icon);
									sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
									age.setCompoundDrawables(sexdraw, null, null, null);
									age.setCompoundDrawablePadding(0);
									age.setBackgroundResource(R.drawable.common_item_jh2_shap);
									age.setVisibility(View.VISIBLE);
								} else if (result.getSex() == 1) {
									age.setVisibility(View.VISIBLE);
									Drawable sexdraw = ContributePointActivity.this.getResources().getDrawable(R.drawable.user_woman_icon);
									sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
									age.setCompoundDrawables(sexdraw, null, null, null);
									age.setCompoundDrawablePadding(0);
									age.setBackgroundResource(R.drawable.common_item_jh_shap);
								} else {
									age.setVisibility(View.GONE);
								}
							}
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						handleErrorCode(result, resultMsg);
					}
				}, this, p.build(), 0, null);
			}
			// 判断当前自己的排名
			ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
			if (vo != null && vo.getUserid() == result.get(i).getUid()) {
				flag = true;
				if (obj.getPoint() > 0) {
					contributeRank.setText("您当前贡献的总积分: " + result.get(i).getPoint() + " 排名:" + (i + 1));
				} else {
					contributeRank.setText("您当前贡献的总积分: " + result.get(i).getPoint());
				}
				if (i >= 10) {
					break;
				}
			}
		}
		// 如果查找完了都没有找到自己 则直接显示0分 0 名
		if (!flag) {
			contributeRank.setText("您当前贡献的总积分: " + 0);
		}
		if (temp) {
			noContriIv.setVisibility(View.GONE);
			showList.setVisibility(View.VISIBLE);
		} else {
			noContriIv.setVisibility(View.VISIBLE);
			showList.setVisibility(View.GONE);
		}
	}

	/**
	 * 给贡献按钮设置监听器
	 */
	private void setListener() {
		cofirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				manager.hideSoftInputFromWindow(donateEdit.getWindowToken(), 0);
				if (SystemContext.getInstance().getIsGuest() != SystemConfig.LOGIN_TARGET_NORMAL) {
					Intent intent = new Intent(ContributePointActivity.this, BundPhoneActivity.class);
					startActivity(intent);
				} else {
					cofirmBtn.setEnabled(false);
					contriPoint();
				}
			}
		});
	}

	private void createBundPhoneDialog() {
		Intent intent = new Intent(ContributePointActivity.this, BundPhoneActivity.class);
		ContributePointActivity.this.startActivity(intent);
	}

	/**
	 * 
	 * 确认贡献积分
	 */
	protected void contriPoint() {
		try {
			String donataNum = donateEdit.getText().toString();
			String userPoint = userPointNum.getText().toString();
			if (donataNum == null || "".equals(donataNum)) {
				cueWordsSuccess.setText("贡献积分值不能为空哦");
				cueWordsFail.setVisibility(View.GONE);
				dialog.show();
				// 贡献成功后，把输入的值清空
				donateEdit.setText("");
				cofirmBtn.setEnabled(true);
				return;
			}
			int num = Integer.parseInt(donataNum);
			if (num == 0) {
				cueWordsSuccess.setText("贡献积分不能为0哦");
				dialog.show();
				// 贡献成功后，把输入的值清空
				donateEdit.setText("");
				cofirmBtn.setEnabled(true);
				return;
			} else if (num > Integer.parseInt(userPoint)) {
				cueWordsSuccess.setText("您的积分余额不足！");
				cueWordsFail.setVisibility(View.GONE);
				dialog.show();
				// 贡献成功后，把输入的值清空
				donateEdit.setText("");
				cofirmBtn.setEnabled(true);
				return;
			} else if (num > CONTRIBUTE_LIMITE_NUMBER) {
				cueWordsSuccess.setText("每日最多贡献" + CONTRIBUTE_LIMITE_NUMBER + "积分！");
				cueWordsFail.setVisibility(View.VISIBLE);
				cueWordsFail.setText("贡献失败！");
				dialog.show();
				// 贡献成功后，把输入的值清空
				donateEdit.setText("");
				cofirmBtn.setEnabled(true);
				return;
			} else {
				contridialog.show();
				showDialog(donataNum, 1);
				return;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下面的这个方法 是在做了预判断之后 真正的将积分给贡献出去
	 */
	private void realContribute(final String donateNum) {
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				Log.i("success", "success");
				SystemContext.getInstance().setPoint(SystemContext.getInstance().getPoint() - Integer.parseInt(donateNum));
				getPoint();
				getgroupPoint();
				// 更新排行榜
				syncGroupMember();
				// 显示dialog
				showDialog(donateNum, 2);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				Log.i("false", "false");
				handleErrorCode(result, resultMsg);
				cofirmBtn.setEnabled(true);
				contridialog.dismiss();
			}
		}, ContributePointActivity.this, groupid, MsgsConstants.OT_GROUP, MsgsConstants.OP_CONTRIBUTION_POINT, donateNum, null, null);
	}

	/**
	 * 当贡献成功后， 弹出对话框
	 * 
	 * @param donateNum
	 */
	private void showDialog(final String donateNum, final int key) {
		ProxyFactory.getInstance().getGroupProxy().getUserContributeGroupPoint(new ProxyCallBack<List<UserItemObj>>() {

			@Override
			public void onSuccess(List<UserItemObj> result) {
				try {
					if (result != null) {
						UserItemObj obj = result.get(0);
						if (key == 1) {
							int num = Integer.parseInt(donateNum);
							if ((num + obj.getPoint()) > CONTRIBUTE_LIMITE_NUMBER) {
								contridialog.dismiss();
								cueWordsSuccess.setText("今天你还能贡献" + (CONTRIBUTE_LIMITE_NUMBER - obj.getPoint()) + "积分");
								cueWordsFail.setVisibility(View.VISIBLE);
								cueWordsFail.setText("贡献失败");
								dialog.show();
								// 贡献成功后，把输入的值清空
								donateEdit.setText("");
								cofirmBtn.setEnabled(true);
							} else {
								realContribute(donateNum);
							}
						} else if (key == 2) {
							contridialog.dismiss();
							cueWordsSuccess.setText("成功贡献" + donateNum + "积分");
							cueWordsFail.setVisibility(View.VISIBLE);
							cueWordsFail.setText("今日还能贡献" + (CONTRIBUTE_LIMITE_NUMBER - obj.getPoint()) + "积分哦");
							dialog.show();
							// 贡献成功后，把输入的值清空
							donateEdit.setText("");
							cofirmBtn.setEnabled(true);
						}
					} else if (result == null && key == 1) {
						realContribute(donateNum);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (key == 1) {
					realContribute(donateNum);
				} else if (key == 2) {
					handleErrorCode(result, resultMsg);
					contridialog.dismiss();
					// 贡献成功后，把输入的值清空
					donateEdit.setText("");
					cofirmBtn.setEnabled(true);
				}
			}
		}, ContributePointActivity.this, groupid + "");
	}

	/**
	 * 当请求数据失败了以后 返回来是执行onfailure方法时 在这个方法里面处理这个错误码
	 * 
	 * @param i
	 * @param msg
	 */
	private void handleErrorCode(Integer i, String msg) {
		ErrorCodeUtil.handleErrorCode(ContributePointActivity.this, i, msg);
	}
}
