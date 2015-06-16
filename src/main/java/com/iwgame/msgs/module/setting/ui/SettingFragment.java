/**      
 * SettingFragment.java Create on 2013-8-5     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.setting.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareDate;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.PointTaskDao;
import com.iwgame.msgs.localdb.dao.UserGradeDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.module.guide.GuideActivity;
import com.iwgame.msgs.module.pay.ui.PayMainActivity;
import com.iwgame.msgs.module.pay.ui.util.DoubleUtil;
import com.iwgame.msgs.module.play.ui.MainPlayListActivity;
import com.iwgame.msgs.module.postbar.ui.MyFavoriteTopicActivity;
import com.iwgame.msgs.module.postbar.ui.UserTopicListActivity;
import com.iwgame.msgs.module.rescache.ResCacheProxyImpl;
import com.iwgame.msgs.module.user.object.UserPointTaskObj;
import com.iwgame.msgs.module.user.ui.PointTaskDetailActivity;
import com.iwgame.msgs.module.user.ui.UserGradePolicyActivity;
import com.iwgame.msgs.module.user.ui.UserPointDetailActivity;
import com.iwgame.msgs.module.user.ui.UserRoleDetailActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.proto.Msgs.YoubiSchemeResult;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.utils.GuideUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.PhotoUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.ShareUtil;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.utils.UserUtil;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.PointTaskVo;
import com.iwgame.msgs.vo.local.ResourceVo;
import com.iwgame.msgs.vo.local.UserGradeVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ShareTaskUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: SettingFragment
 * @Description: 我页面 设置界面
 * @author zhangjianchuan
 * @date 2014-10-10 上午11:39:47
 * @Version 1.0
 * 
 */
public class SettingFragment extends BaseFragment implements OnClickListener, OnLongClickListener {

	private static final String TAG = "SettingFragment";
	private LayoutInflater inflater;
	private int addPhotoType;// 添加图片类型
	public static boolean isRender = true;// 是否要重新渲染
	private ExtUserVo uvo;
	private Dialog dialog;
	private Uri imageUri;
	private SharedPreferences sp;
	private ImageView avatar;// 用户的头像
	private TextView nickName;// 用户昵称
	private TextView age;// 用户年龄
	private TextView city;// 用户的城市
	private TextView signName;// 签名
	private TextView userId;// 用户的id
	private LinearLayout postItem;// 帖子的item
	private LinearLayout favorateItem;// 收藏的item
	private LinearLayout gradeItem;// 等级的item
	private LinearLayout pointItem;// 积分的item
	private LinearLayout ucoinItem;// 游币的item
	private TextView my_ucoin;// 游币数
	private LinearLayout taskItem;// 积分任务item
	private LinearLayout settingItem;// 设置的item
	private View view;// 编辑资料的view
	private ImageView addPhoteBtn;// 添加照片的button
	private TextView pointNum;
	private TextView gradeNum;
	private TextView ucoin;
	private ImageView taskTag;// 积分任务标记
	public HorizontalScrollView horizontalScrollView;
	public LinearLayout photoContent;// 显示相册的父容器
	private int photoCount; // 相册数
	public TextView tip;// 相册的提示
	private ImageView settingTag;
	private ImageView gradeTag;
	private UserGradeDao userGradeDao;
	private LinearLayout pointMarketItem;
	private Button leftBtn;
	private ImageView pointMartketTag;
	private boolean temp = false;// 标记积分任务右边的tag,true说明任务有变化，false说明任务没有变化
	private View v;
	private LinearLayout activityDivision;// 活动专区的按钮
	private ImageView fengexian;
	private TextView activityDisvisionTv;// 活动专区的那几个字 通过服务端去配的，动态的去获取
	private int grade;
	private LinearLayout roleItem;
	private LinearLayout playItem;

	public ImageView getTaskTag() {
		return taskTag;
	}

	public void setTaskTag(ImageView taskTag) {
		this.taskTag = taskTag;
	}

	public ImageView getPointMartketTag() {
		return pointMartketTag;
	}

	public void setPointMartketTag(ImageView pointMartketTag) {
		this.pointMartketTag = pointMartketTag;
	}

	public View getV() {
		return v;
	}

	public void setV(View v) {
		this.v = v;
	}

	public boolean isTemp() {
		return temp;
	}

	public void setTemp(boolean temp) {
		this.temp = temp;
	}

	private static SettingFragment instance = null;

	/**
	 * 给相册数设置了一个get方法
	 * 
	 * @return
	 */
	public int getPhotoCount() {
		return photoCount;
	}

	/**
	 * 给相册数设置了一个set方法
	 * 
	 * @param photoCount
	 */
	public void setPhotoCount(int photoCount) {
		this.photoCount = photoCount;
	}

	/**
	 * 获取到要显示到手机 界面上的view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		instance = this;
		this.inflater = inflater;
		sp = this.getActivity().getSharedPreferences("sync", this.getActivity().MODE_PRIVATE);
		if (v == null) {
			v = inflater.inflate(R.layout.user_detail_view, container, false);// 设置内容UI
			LinearLayout right = (LinearLayout) v.findViewById(R.id.rightView);// 左边菜单
			right.setVisibility(View.VISIBLE);
			// 添加右边功能按钮
			view = View.inflate(this.getActivity(), R.layout.setting_right_top_view, null);// 添加右边功能按钮
			LinearLayout rightView = (LinearLayout) v.findViewById(R.id.rightView);
			ImageView edit = (ImageView) view.findViewById(R.id.edit);
			edit.setBackgroundResource(R.drawable.edit_user_detail);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			rightView.addView(view, params);
			rightView.setVisibility(View.VISIBLE);
		}
		ViewGroup parent = (ViewGroup) v.getParent();
		if (parent != null)
			parent.removeAllViews();
		initialize(v);// 初始化界面
		PTAG = TAG;
		getActivityDivisionTitle();// 获取我页面活动专区的title
		checkShowGuide();
		synctask();
		ExtUserVo userVo = SystemContext.getInstance().getExtUserVo();
		if (userVo != null && userVo.getUserid() != 0 && userVo.getUsername() != null && !userVo.getUsername().isEmpty()
				&& SystemContext.getInstance().getIsGuest() == 0) {
			requestUserDetailInfo();
		}
		return v;
	}

	/**
	 * 获取活动专区配置的title名字
	 */
	private void getActivityDivisionTitle() {
		ProxyFactory.getInstance().getUserProxy().getActivityDivisionTitle(new ProxyCallBack<String>() {

			@Override
			public void onSuccess(String result) {
				if (activityDisvisionTv != null) {
					activityDisvisionTv.setText(result);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (activityDisvisionTv != null) {
					activityDisvisionTv.setText("转盘比拼");
				}
			}
		}, this.getActivity());
	}

	/**
	 * 判断是否显示 引导图的问题
	 */
	private void checkShowGuide() {
		if (!SystemContext.getInstance().getGuidUserDetail() && SystemContext.getInstance().isnotShowGuide()) {
			if (MainFragmentActivity.getInstance().getmTabHost().getCurrentTab() != 4)
				return;
			SystemContext.getInstance().setGuidUserDetail(true);
			// 设置内容UI
			if (SystemContext.getInstance().getAlbumCount() > 0) {
				tip.setVisibility(View.VISIBLE);
				GuideUtil.startGuide(SystemContext.getInstance().getContext(), GuideActivity.GUIDE_MODE_SETTINGFRAGMENT_YES);
			} else {
				tip.setVisibility(View.GONE);
				GuideUtil.startGuide(SystemContext.getInstance().getContext(), GuideActivity.GUIDE_MODE_SETTINGFRAGMENT_NO);
			}
		} else {
			if (SystemContext.getInstance().getAlbumCount() > 0) {
				tip.setVisibility(View.VISIBLE);
			} else {
				tip.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 获取instance实例
	 * 
	 * @return
	 */
	public static SettingFragment getInstance() {
		return instance;
	}

	/**
	 * 重新请求用户的详细信息 保存到本地
	 */
	private void requestUserDetailInfo() {
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		ExtUserVo extUserVo = SystemContext.getInstance().getExtUserVo();
		cdp.setId(extUserVo.getUserid());
		cdp.setUtime(extUserVo.getUpdatetime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getUserProxy().getUserDetailInfoNoFromLocal(new ProxyCallBack<List<UserVo>>() {
			@Override
			public void onSuccess(List<UserVo> result) {
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
			}
		}, this.getActivity(), p.build(), 0, null);
	}

	/**
	 * 初始化界面的控件 设置按钮的事件监听
	 * 
	 * @param v
	 */
	private void initialize(View v) {
		userGradeDao = DaoFactory.getDaoFactory().getUserGradeDao(SystemContext.getInstance().getContext());
		avatar = (ImageView) v.findViewById(R.id.icon);
		nickName = (TextView) v.findViewById(R.id.nickname);
		gradeTag = (ImageView) v.findViewById(R.id.grade_tag);
		gradeTag.setVisibility(View.GONE);
		age = (TextView) v.findViewById(R.id.age);
		city = (TextView) v.findViewById(R.id.city);
		signName = (TextView) v.findViewById(R.id.mood);
		userId = (TextView) v.findViewById(R.id.serial);
		taskTag = (ImageView)v.findViewById(R.id.task_tag);
		postItem = (LinearLayout) v.findViewById(R.id.my_post_item);
		favorateItem = (LinearLayout) v.findViewById(R.id.my_favorate_item);
		gradeItem = (LinearLayout) v.findViewById(R.id.grade_item);
		pointItem = (LinearLayout) v.findViewById(R.id.point_item);
		ucoinItem = (LinearLayout) v.findViewById(R.id.ucoin_item);
		my_ucoin = (TextView) v.findViewById(R.id.my_ucoin);
		taskItem = (LinearLayout) v.findViewById(R.id.task_item);
		roleItem = (LinearLayout) v.findViewById(R.id.role_item);
		playItem = (LinearLayout) v.findViewById(R.id.play_item);
		settingItem = (LinearLayout) v.findViewById(R.id.setting_item);
		addPhoteBtn = (ImageView) v.findViewById(R.id.addpotoBtn);
		pointNum = (TextView) v.findViewById(R.id.my_point);
		gradeNum = (TextView) v.findViewById(R.id.my_grade);
		ucoin = (TextView) v.findViewById(R.id.my_ucoin);
		horizontalScrollView = (HorizontalScrollView) v.findViewById(R.id.detailInfo);
		photoContent = (LinearLayout) v.findViewById(R.id.photoContentView);
		settingTag = (ImageView) v.findViewById(R.id.setting_tag);
		pointMarketItem = (LinearLayout) v.findViewById(R.id.point_market_item);
		leftBtn = (Button) v.findViewById(R.id.leftBtn);
		pointMartketTag = (ImageView) v.findViewById(R.id.point_market_tag);
		activityDivision = (LinearLayout) v.findViewById(R.id.activity_division);// 活动专区
		fengexian = (ImageView) v.findViewById(R.id.fengexian);
		activityDisvisionTv = (TextView) v.findViewById(R.id.activity_division_tv);
		activityDivision.setOnClickListener(this);
		leftBtn.setOnClickListener(this);
		pointMarketItem.setOnClickListener(this);
		tip = (TextView) v.findViewById(R.id.tip);
		tip.setVisibility(View.GONE);
		ucoinItem.setVisibility(View.VISIBLE);
		addPhoteBtn.setOnClickListener(this);
		avatar.setOnLongClickListener(this);
		view.setOnClickListener(this);
		postItem.setOnClickListener(this);
		favorateItem.setOnClickListener(this);
		gradeItem.setOnClickListener(this);
		pointItem.setOnClickListener(this);
		taskItem.setOnClickListener(this);
		roleItem.setOnClickListener(this);
		playItem.setOnClickListener(this);
		settingItem.setOnClickListener(this);
		ucoinItem.setOnClickListener(this);
		setRoleItemVisible();
	}

	/**
	 * 设置角色模块是否显示
	 */
	public void setRoleItemVisible() {
		AppConfig config = AdaptiveAppContext.getInstance().getAppConfig();
		if (config.isShowRoleList()) {
			roleItem.setVisibility(View.VISIBLE);
		} else {
			roleItem.setVisibility(View.GONE);
		}
	}

	/**
	 * 当长按头像的时候， 就弹出对话框 可以进行设置头像
	 */
	@Override
	public boolean onLongClick(View v) {
		if (v.getId() == avatar.getId()) {
			addPhotoType = 1;
			dialog = PhotoUtil.showSelectDialog(this);
			UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_USER_CLICK_AVATAR, null, null, null, null, null);
		}
		return false;
	}

	/**
	 * 当点击界面中的按钮的时候 执行下面的方法 事件处理的方法
	 */
	@Override
	public void onClick(View v) {
		// 判断是否是快速点击
		if (FastClickLimitUtil.isFastClick())
			return;
		if (v.getId() == view.getId()) {// 编辑资料
			Intent intent = new Intent(SettingFragment.this.getActivity(), EditDetailActivity.class);
			startActivity(intent);
		} else if (v.getId() == addPhoteBtn.getId()) {// 添加相册图片
			if (NetworkUtil.isNetworkAvailable(getActivity())) {// 点击添加相册照片
				if (getPhotoCount() < SystemContext.getInstance().getAM()) {
					addPhotoType = 2;
					dialog = PhotoUtil.showSelectDialog(this);
				} else {
					String etip = getResources().getString(R.string.setting_addphoto_max_error);
					String fetip = String.format(etip, SystemContext.getInstance().getAM());
					ToastUtil.showToast(SettingFragment.this.getActivity(), fetip);
				}
			} else {
				ToastUtil.showToast(getActivity(), getString(R.string.network_error));
			}
		} else if (v.getId() == postItem.getId()) {// 点击贴子
			Intent intent = new Intent(getActivity(), UserTopicListActivity.class);
			Bundle bundle = new Bundle();
			bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, SystemContext.getInstance().getExtUserVo().getUserid());
			intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			startActivity(intent);
		} else if (v.getId() == favorateItem.getId()) {// 点击我的收藏
			Intent intent = new Intent(getActivity(), MyFavoriteTopicActivity.class);
			Bundle bundle = new Bundle();
			bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, SystemContext.getInstance().getExtUserVo().getUserid());
			intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			startActivity(intent);
		} else if (v.getId() == gradeItem.getId()) {// 点击我的等级
			Intent intent = new Intent(SettingFragment.this.getActivity(), UserGradePolicyActivity.class);
			startActivity(intent);
		} else if (v.getId() == pointItem.getId()) {// 点击我的积分
			Intent intent = new Intent(SettingFragment.this.getActivity(), UserPointDetailActivity.class);
			startActivity(intent);
		} else if (v.getId() == taskItem.getId()) {// 点击我的任务
			Intent intent = new Intent(SettingFragment.this.getActivity(), PointTaskDetailActivity.class);
			startActivity(intent);
		} else if (v.getId() == roleItem.getId()) {// 点击我的角色
			Intent intent = new Intent(SettingFragment.this.getActivity(), UserRoleDetailActivity.class);
			startActivity(intent);
		} else if (v.getId() == playItem.getId()) {// 点击我的陪玩
			Intent intent = new Intent(SettingFragment.this.getActivity(), MainPlayListActivity.class);
			startActivity(intent);
		} else if (v.getId() == settingItem.getId()) {// 点击我的设置
			Intent intent = new Intent(getActivity(), SysSettingActivity.class);
			startActivity(intent);
		} else if (v.getId() == pointMarketItem.getId()) {// 点击积分商城
			if (pointMartketTag.getVisibility() == View.VISIBLE) {
				ProxyFactory.getInstance().getMessageProxy()
						.delByChannelTypeAndCategory(MsgsConstants.MC_PUB, MsgsConstants.MCC_INFO, MsgsConstants.RESOURCE_APP_TYPE_GOODS + "");
				pointMartketTag.setVisibility(View.INVISIBLE);
			}
			Intent intent = new Intent(this.getActivity(), PointMarketActivity.class);
			this.getActivity().startActivity(intent);
		} else if (v.getId() == leftBtn.getId()) {
			shareUserInfo();
		} else if (v.getId() == activityDivision.getId()) {
			if (NetworkUtil.isNetworkAvailable(SystemContext.getInstance().getContext())) {
				// 跳转到活动专区页面
				Intent intent = new Intent(SettingFragment.this.getActivity(), ActivityDivisionActivity.class);
				SettingFragment.this.startActivity(intent);
			} else {
				ToastUtil.showToast(this.getActivity(), "当前没有网络哦！");
			}
		} else if (v.getId() == ucoinItem.getId()) {
			Intent intent = new Intent(SettingFragment.this.getActivity(), PayMainActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 设置裁剪 图片资源
	 * 
	 * @param url
	 */
	public void cropRes(String url) {
		ResCacheProxyImpl.getInstance().getRes(new ProxyCallBack<Uri>() {
			@Override
			public void onSuccess(Uri result) {
				addPhotoType = 1;
				Display mDisplay = getActivity().getWindowManager().getDefaultDisplay();
				int w = mDisplay.getWidth();
				int h = mDisplay.getHeight();
				int imagewidth = w > h ? h : w;
				imageUri = Uri.parse("file://" + PhotoUtil.sdcardTempFilePath);
				PhotoUtil.doCropBigPhoto(SettingFragment.this, result, imageUri, 1, 1, imagewidth, imagewidth);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
			}
		}, this.getActivity(), url, ResCacheProxyImpl.TYPE_ORRGINAL);
	}

	/**
	 * 同步任务 把任务同步下来保存到数据库里面
	 */
	private void synctask() {
		PointTaskDao dao = DaoFactory.getDaoFactory().getPointTaskDao(SystemContext.getInstance().getContext());
		List<PointTaskVo> list = dao.queryAll();
		if (list != null && list.size() > 0) {
			isShowTaskAndMarketTag();
			sp.edit().putBoolean("iscon", false).commit();
		} else {
			// 首先去同步数据
			ProxyFactory.getInstance().getUserProxy().getPointTaskDetail(new ProxyCallBack<List<PointTaskVo>>() {
				@Override
				public void onSuccess(List<PointTaskVo> result) {
					isShowTaskAndMarketTag();
					sp.edit().putBoolean("iscon", false).commit();
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {

				}
			}, this.getActivity());
		}
	}

	/**
	 * 在onresume方法里面 重新刷新数据
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (SystemContext.getInstance().isUnAuth()) {
			// 会话失效， 主动注销，token不存在/认证失败，都到登录页
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UNAUTHENTICATED, true);
			intent.putExtras(bundle);
			startActivity(intent);
		} else {
			if (getActivity() instanceof MainFragmentActivity) {
				((MainFragmentActivity) getActivity()).showOrDismissTabHost(true, null, null);
			}
			temp = false;
			isShowSettingTag();
			// 获取头像图片
			ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
			if (vo != null && vo.getAvatar() != null) {
				UserUtil.showAvatar(getActivity(), avatar, SystemContext.getInstance().getExtUserVo().getAvatar(), true, SystemContext.getInstance()
						.getExtUserVo().getUserid(), UserUtil.TYPE_SETTING);
			}
			if (isRender)
				resetUI();
			else
				isRender = true;
		}
		//获取我的游币
		getUb();
	}

	/**
	 * 是否显示设置上面的小红点
	 */
	private void isShowSettingTag() {
		if (MainFragmentActivity.getInstance().isFlag() && SystemContext.getInstance().getIsGuest() == 0) {
			settingTag.setVisibility(View.INVISIBLE);
		} else {
			settingTag.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 重新渲染用户 界面
	 */
	private void resetUI() {
		uvo = SystemContext.getInstance().getExtUserVo();// 获取我的详细信息(从内存里面查用户)
		if (uvo != null) {// 请求用户相册图片
			horizontalScrollView.setVisibility(View.VISIBLE);
			UserUtil.showUserAlbum(this, getActivity(), SettingFragment.this, uvo.getUserid(), avatar, UserUtil.TYPE_SETTING, 2);
			renderContent(SystemContext.getInstance().getExtUserVo());
		}
	}

	/**
	 * 将用户的基本资料信息 显示到手机界面上
	 * 
	 * @param vo
	 */
	private void renderContent(ExtUserVo vo) {
		if (vo != null) {
			userId.setText(vo.getSerial() > 0 ? String.valueOf(vo.getSerial()) : "");
			nickName.setText(vo.getUsername() == null ? "" : vo.getUsername());
			if (vo.getMood() != null && !"".equals(vo.getMood())) {
				fengexian.setVisibility(View.VISIBLE);
				signName.setVisibility(View.VISIBLE);
				signName.setText(vo.getMood() == null ? "" : vo.getMood().trim());
			} else {
				fengexian.setVisibility(View.INVISIBLE);
				signName.setVisibility(View.INVISIBLE);
			}

			if (vo.getSex() == 0) {
				age.setText("男" + (vo.getAge() > 0 ? " " + AgeUtil.convertAgeByBirth(vo.getAge()) + "" : ""));
			} else if (vo.getSex() == 1) {
				age.setText("女" + (vo.getAge() > 0 ? " " + AgeUtil.convertAgeByBirth(vo.getAge()) + "" : ""));
			} else {
				age.setText(vo.getAge() > 0 ? AgeUtil.convertAgeByBirth(vo.getAge()) + "" : "");
			}
			if (vo.getCity() == null || "".equals(vo.getCity())) {
				city.setVisibility(View.GONE);
			} else {
				city.setVisibility(View.VISIBLE);
				city.setText(vo.getCity() == null ? "" : vo.getCity());
			}
			// 获取头像图片
			if (vo.getAvatar() != null) {
				UserUtil.showAvatar(getActivity(), avatar, vo.getAvatar(), true, vo.getUserid(), UserUtil.TYPE_SETTING);
			}
		} else {
			userId.setText("");
			nickName.setText("");
			signName.setText("");
			age.setText("");
			city.setText("");
			ToastUtil.showToast(SettingFragment.this.getActivity(), getString(R.string.setting_userinfo_isnull));
		}
		isShowTaskAndMarketTag();
		getPoint();
		getDetailInfo();
		getExp();
	}

	/**
	 * 判断是否显示 商城的tag和 任务的tag
	 */
	private void isShowTaskAndMarketTag() {
		if (MainFragmentActivity.getInstance().isFlag() && !MainFragmentActivity.getInstance().isTemp())
			MainFragmentActivity.getInstance().getSettingTag().setVisibility(View.GONE);
		else
			MainFragmentActivity.getInstance().getSettingTag().setVisibility(View.VISIBLE);
		List<MessageVo> list = ProxyFactory.getInstance().getMessageProxy()
				.getMessageByChannelTypeAndCategory(MsgsConstants.MC_PUB, MsgsConstants.MCC_INFO);
		if (list == null) {
			temp = false;
			 showPointTaskTag();
			return;
		}
		int size = list.size();
		for (int i = 0; i < size; i++) {
			MessageVo vo = list.get(i);
			if (vo != null) {
				if (vo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_TYPE_GOODS))) {
					// 显示商城的tag
					pointMartketTag.setVisibility(View.VISIBLE);
				} else if (vo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_TYPE_TASK))) {
					temp = true;
					 taskTag.setVisibility(View.VISIBLE);
					 taskTag.setBackgroundResource(R.drawable.common_tag_new);
				} else if (vo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_FINISHED_TASK))) {
					// TODO
					 taskTag.setVisibility(View.GONE);
				}
			}
		}
		if (!temp) {
			temp = false;
			 showPointTaskTag();
		}
	}

	/**
	 * 判断是否 显示积分任务的标记
	 */
	private void showPointTaskTag() {
		ProxyFactory.getInstance().getUserProxy().getPointTask(new ProxyCallBack<List<UserPointTaskObj>>() {

			@Override
			public void onSuccess(List<UserPointTaskObj> result) {
				int length = result.size();
				for (int i = 0; i < length; i++) {
					UserPointTaskObj obj = result.get(i);
					if (obj.getStatus() == UserPointTaskObj.STATUS_UNFETCH) {
						// taskTag.setVisibility(View.VISIBLE);
						// taskTag.setBackgroundResource(R.drawable.task_tag_circle);
						return;
					}
				}
				 checkTaskTag();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// taskTag.setVisibility(View.INVISIBLE);
				if (sp.getBoolean("ifnot", true)) {
					// checkTaskTag();
				}
			}
		}, getActivity(), 1, 1);
	}

	/**
	 * 获取用户的 积分信息 将积分数值显示到界面上
	 */
	private void getPoint() {
		ProxyFactory.getInstance().getUserProxy().getUserPoint(new ProxyCallBack<List<ExtUserVo>>() {
			@Override
			public void onSuccess(List<ExtUserVo> result) {
				if (result != null && result.size() == 1) {
					int point = result.get(0).getPoint();
					pointNum.setText(point + "");
					SystemContext.getInstance().setPoint(point);
					// showGradeTag(point);//下面的代码执行来判断是否要显示等级的标记
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				pointNum.setText("0");
			}
		}, getActivity(), SystemContext.getInstance().getExtUserVo().getUserid() + "");
	}

	/**
	 * 获取用户的 经验值信息 将积分数值显示到界面上
	 */
	private void getExp() {
		ProxyFactory.getInstance().getUserProxy().getUserExperience(new ProxyCallBack<List<ExtUserVo>>() {
			@Override
			public void onSuccess(List<ExtUserVo> result) {
				if (result != null && result.size() == 1) {
					int exp = result.get(0).getExp();
					SystemContext.getInstance().setExp(exp);
					gradeNum.setText("LV" + grade + "(" + exp + ")");
					// showGradeTag(point);//下面的代码执行来判断是否要显示等级的标记
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				SystemContext.getInstance().setExp(0);
			}
		}, getActivity(), SystemContext.getInstance().getExtUserVo().getUserid() + "");
	}

	/**
	 * 判断是否显示 用户等级的标记
	 * 
	 * @param point
	 */
	private void showGradeTag(int point) {
		List<UserGradeVo> list = userGradeDao.queryAll();
		if (list == null) {
			gradeTag.setVisibility(View.GONE);
			return;
		}
		UserVo userVo = SystemContext.getInstance().getExtUserVo();
		if (userVo == null)
			return;
		if (userVo.getGrade() >= 10) {
			gradeTag.setVisibility(View.GONE);
		} else if (userVo.getGrade() < 2) {
			gradeTag.setVisibility(View.GONE);// 如何小于2级 说明是游客，游客不显示tasktag的
		} else {
			// 判断是否要显示
			int lenght = list.size();
			for (int i = 0; i < lenght; i++) {
				UserGradeVo vo = list.get(i);
				if (vo.getGrade() == userVo.getGrade()) {
					if (point > list.get(vo.getGrade()).getPoint()) {
						gradeTag.setVisibility(View.VISIBLE);
					} else {
						gradeTag.setVisibility(View.GONE);
					}
				}
			}
		}
	}

	/**
	 * 检测tasktag是否显示出来 先通过新手任务去判断 在通过每日任务去判断
	 */
	private void checkTaskTag() {
		ProxyFactory.getInstance().getUserProxy().getPointTask(new ProxyCallBack<List<UserPointTaskObj>>() {
			@Override
			public void onSuccess(List<UserPointTaskObj> result) {
				int lenght = result.size();
				for (int i = 0; i < lenght; i++) {
					UserPointTaskObj obj = result.get(i);
					if (obj.getStatus() == UserPointTaskObj.STATUS_UNFETCH) {
						// taskTag.setVisibility(View.VISIBLE);
						// taskTag.setBackgroundResource(R.drawable.task_tag_circle);
						return;
					}
				}
				 taskTag.setVisibility(View.GONE);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				 taskTag.setVisibility(View.GONE);
			}
		}, getActivity(), 2, 1);
	}

	/**
	 * 获取用户更 详细的资料 将用户的等级显示出来
	 */
	private void getDetailInfo() {
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		ExtUserVo extUserVo = SystemContext.getInstance().getExtUserVo();
		cdp.setId(extUserVo.getUserid());
		cdp.setUtime(extUserVo.getUpdatetime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(new ProxyCallBack<List<UserVo>>() {
			@Override
			public void onSuccess(List<UserVo> uservoresult) {
				if (uservoresult == null || uservoresult.size() <= 0)
					return;
				UserVo result = uservoresult.get(0);
				if (result != null) {
					grade = result.getGrade();

					SystemContext.getInstance().setGrade(grade);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				gradeNum.setText("LV0");
			}
		}, getActivity(), p.build(), 0, null);
	}

	/**
	 * fragment的生命周期方法 中的onpause方法
	 */
	@Override
	public void onPause() {
		super.onPause();
		if (dialog != null)
			dialog.dismiss();
	}

	/**
	 * 在onstop方法里面，把对话框给取消掉
	 */
	@Override
	public void onStop() {
		super.onStop();
		if (dialog != null && SystemContext.getInstance().getExtUserVo() == null) {
			dialog.dismiss();
		}
		Bitmap bmp = UserUtil.bmp;
		if (bmp != null && bmp.isRecycled()) {
			bmp.recycle();
			bmp = null;
		}
		// System.gc();
	}

	/**
	 * 在设置头像返回 的时候需要执行下面的这个方法
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.d(TAG, "resultCode =" + resultCode + ";requestCode=" + requestCode);
		Bitmap photo = null;
		byte[] photoByte = null;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case PhotoUtil.CAMERA_WITH_DATA:
				if (addPhotoType == 1) {
					Display mDisplay = getActivity().getWindowManager().getDefaultDisplay();
					int w = mDisplay.getWidth();
					int h = mDisplay.getHeight();
					int imagewidth = w > h ? h : w;
					imageUri = Uri.parse("file://" + PhotoUtil.sdcardTempFilePath);
					PhotoUtil.doCropBigPhoto(SettingFragment.this, imageUri, imageUri, 1, 1, imagewidth, imagewidth);
					return;
				} else if (addPhotoType == 2) {
					try {
						photo = ImageUtil.createImageThumbnail(PhotoUtil.sdcardTempFilePath, -1, SystemConfig.BITMAP_MAX_RESOLUTION, true);
					} catch (Exception e) {
						e.printStackTrace();
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
					photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, SystemConfig.BITMAP_COMPRESS_QUALITY);
				}
				break;
			case PhotoUtil.PHOTO_PICKED_WITH_DATA:
				ContentResolver resolver = this.getActivity().getContentResolver();
				Uri originalUri = data.getData();// 照片的原始资源地址
				if (addPhotoType == 1) {
					Display mDisplay = getActivity().getWindowManager().getDefaultDisplay();
					int w = mDisplay.getWidth();
					int h = mDisplay.getHeight();
					int imagewidth = w > h ? h : w;
					imageUri = Uri.parse("file://" + PhotoUtil.sdcardTempFilePath);
					PhotoUtil.doCropBigPhoto(SettingFragment.this, originalUri, imageUri, 1, 1, imagewidth, imagewidth);
					return;
				} else if (addPhotoType == 2) {
					try {// 使用ContentProvider通过URI获取原始图片
						photo = ImageUtil.createImageThumbnail(resolver, originalUri, -1, SystemConfig.BITMAP_MAX_RESOLUTION, true);
						if (photo != null) {
							photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, SystemConfig.BITMAP_COMPRESS_QUALITY);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
				}
				break;
			case PhotoUtil.CROP_IMAGE_WITH_DATA:
				if (data != null && data.getParcelableExtra("data") != null) {
					try {
						photo = data.getParcelableExtra("data");
					} catch (Exception e) {
						e.printStackTrace();
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
					if (photo != null) {
						photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, 100);
					}
				} else {
					if (imageUri != null) {
						try {
							photo = ImageUtil.decodeUri2Bitmap(this.getActivity().getContentResolver(), imageUri);
						} catch (Exception e) {
							e.printStackTrace();
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
						}
						photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, 100);
					}
				}
				break;
			}
			LogUtil.d(TAG, "photo =" + photo);
			if (photo != null) {
				if (addPhotoType == 1)
					showAvatarImage(avatar, photoByte);
				if (addPhotoType == 2) {
					showPhotoImage(photoContent, avatar, photoByte);
					tip.setVisibility(View.VISIBLE);
				}
				if (!photo.isRecycled()) {
					photo.recycle();
					photo = null;
					// System.gc();
				}

			} else {
				ToastUtil.showToast(this.getActivity(), getResources().getString(R.string.common_sendphoto_isnull));
				LogUtil.w(TAG, "获得需要发送的图片异常");
				return;
			}
		} else {
			LogUtil.w(TAG, "选择发送的图片异常");
		}
	}

	/**
	 * 显示用户的头像
	 * 
	 * @param avatarView
	 * @param bmp
	 * @param bymp
	 */
	private void showAvatarImage(final ImageView avatarView, byte[] bymp) {
		// 添加照片显示
		if (bymp != null) {
			final CustomProgressDialog dialog = CustomProgressDialog.createDialog(getActivity());
			dialog.show();
			// 发送请求提交服务端
			ProxyFactory.getInstance().getSettingProxy().modifyAccountAvatar(new ProxyCallBack<ResourceVo>() {
				@Override
				public void onSuccess(ResourceVo result) {
					if (result != null) {
						UserUtil.showAvatar(getActivity(), avatar, result.getResourceId(), true, uvo.getUserid(), UserUtil.TYPE_SETTING);
						SystemContext.getInstance().setAvatar(result.getResourceId());
						String imageUrl = ResUtil.getSmallRelUrl(result.getResourceId());
						if (imageUrl != null && !imageUrl.isEmpty()) {
							ProxyFactory.getInstance().getUserProxy().getImageUrlToData(new ProxyCallBack<Integer>() {

								@Override
								public void onSuccess(Integer result) {
									LogUtil.d(TAG, "图片保存成功");
								}

								@Override
								public void onFailure(Integer result, String resultMsg) {
									LogUtil.d(TAG, "图片保存失败");
								}
							}, getActivity(), imageUrl, SystemConfig.LOGIN_USER_ICON_NAME);
							// BitmapUtil.saveUrlBitmapToData(SettingFragment.this.getActivity(),
							// imageUrl, SystemConfig.LOGIN_USER_ICON_NAME);
						}
					} else {
						ToastUtil.showToast(getActivity(), "设置头像失败");
					}
					dialog.dismiss();
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					ToastUtil.showToast(getActivity(), "设置头像失败");
					dialog.dismiss();
				}
			}, getActivity(), bymp);
		} else {
			return;
		}
	}

	/**
	 * 显示相册图片
	 * 
	 * @param photoContentView
	 * @param avatarView
	 * @param bmp
	 * @param bymp
	 */
	private void showPhotoImage(final LinearLayout photoContentView, final ImageView avatarView, byte[] bymp) {
		if (bymp != null) {// 添加照片显示
			isRender = false;
			ProxyFactory.getInstance().getUserProxy().addAlbum(new ProxyCallBack<ResourceVo>() {// 发送请求提交服务端
						@Override
						public void onSuccess(ResourceVo result) {
							isRender = true;
							resetUI();
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							isRender = true;
							switch (result) {
							case Msgs.ErrorCode.EC_MSGS_OVER_COUNT_VALUE:
								ToastUtil.showToast(getActivity(), "您相册照片数已达到最大值了");
								break;
							default:
								ToastUtil.showToast(getActivity(), "添加相册图片失败");
								break;
							}
						}
					}, getActivity(), bymp);
		} else {
			return;
		}
	}

	/**
	 * 分享用户资料
	 */
	private void shareUserInfo() {
		if (SystemContext.getInstance().getExtUserVo() == null)
			return;
		ShareDate shareDate = new ShareDate();
		shareDate.setShareType(ShareUtil.SHARE_TYPE_SHAER);// 类型为分享
		shareDate.setTargetType(ShareUtil.TYPE_USER);// 类型为类型为用户
		shareDate.setInTargetType(SystemConfig.CONTENT_TYPE_USER_DETIAL);// 内部分享类型
		shareDate.setTargetId(SystemContext.getInstance().getExtUserVo().getUserid());// 用户id(目标ID)
		shareDate.setTargerSerialId(SystemContext.getInstance().getExtUserVo().getSerial());// 用户serialid(目标serialID)
		shareDate.setTargetName(SystemContext.getInstance().getExtUserVo().getUsername());// 用户名称（目标名称）
		if (SystemContext.getInstance().getExtUserVo().getUserid() == SystemContext.getInstance().getExtUserVo().getUserid()) {
			shareDate.setDetailType(0);
		} else if (SystemContext.getInstance().getExtUserVo().getSex() == 0) {
			shareDate.setDetailType(1);
		} else if (SystemContext.getInstance().getExtUserVo().getSex() == 1) {
			shareDate.setDetailType(2);
		}
		// shareDate.setSite("游伴");//QQ空间分享时使用（暂固定写法）
		// shareDate.setSiteUrl("http://www.51uban.com");//QQ空间分享时使用（暂固定写法）

		shareDate.setImagePath(SystemContext.getInstance().getExtUserVo().getAvatar());
		// 获得图片地址
		shareDate.setImageUrl(ResUtil.getSmallRelUrl(SystemContext.getInstance().getExtUserVo().getAvatar()));
		ShareCallbackListener listener = new ShareCallbackListener() {
			@Override
			public void doSuccess(String plamType) {
				// 分享个人资料
				ShareTaskUtil.makeShareTask(getActivity(), TAG, SystemContext.getInstance().getExtUserVo().getUserid(), MsgsConstants.OT_USER,
						MsgsConstants.OP_RECORD_SHARE, plamType, null, this.shortUrl);
			}

			@Override
			public void doFail() {
				// TODO Auto-generated method stub
			}
		};
		ShareManager.getInstance().share(this.getActivity(), inflater, SystemContext.getInstance().getExtUserVo(), shareDate, listener);
	}

	/**
	 * 在退出的时候， 删除偏好设置里面 的数据 这里面的数据是用来控件tasktag 是否显示用的
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		// 从偏好设置里面移除这条数据
		if (sp != null && sp.edit() != null && sp.edit().remove("iscon") != null)
			sp.edit().remove("iscon").commit();
	}
	
	/**
	 * 获取游币
	 */
	private void getUb(){
		ProxyFactory.getInstance().getPayProxy().getUb(new ProxyCallBack<Msgs.YoubiSchemeResult>() {

			@Override
			public void onSuccess(YoubiSchemeResult result) {
				if (result != null) {
					my_ucoin.setText(DoubleUtil.getDoubleFormat(result.getMyYoubi()));
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				
			}
		}, getActivity(), 1);
	}
}
