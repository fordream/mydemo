/**      
 * EditDetailActivity.java Create on 2013-11-13     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.setting.ui;

import java.util.Calendar;
import java.util.List;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.user.object.UserPointTaskObj;
import com.iwgame.msgs.module.user.ui.PointTaskDetailPageActivity;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.PhotoUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.AreaVo;
import com.iwgame.msgs.vo.local.ResourceVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.msgs.widget.picker.NumberPicker;
import com.iwgame.msgs.widget.picker.NumberPicker.OnScrollListener;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.StringUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;


/**
 * @ClassName: EditDetailActivity
 * @Description: 编辑详情页面
 * @author 王卫
 * @date 2013-11-13 下午2:36:24
 * @Version 1.0
 * 
 */
public class EditDetailActivity extends BaseActivity implements OnClickListener {
	
    private String TAG = "EditDetailActivity";
	// 属性内容
	private TextView nickname;
	private TextView mood;
	private TextView sex;
	private TextView age;
	private TextView city;
	private TextView job;
	private TextView gameTime;
	private TextView likeGame;
	//用户头像
	private ImageView userAvter;
	// 选择的年份
	private int selectedValue;
	// 地区名称
	private String areaname;
	// 城市名称
	private String cityname;
	// 玩贴吧时间值
	private String gameTiemValue;
	// 钟情贴吧类型值
	private String likeGameTypeValue;
	private Uri imageUri;
    private ImageView iv;
	// 头像数据
	private byte[] avatar = null;
	private ImageView iconPoint;
	private ImageView nickPoint;
	private ImageView genderPoint;
	private ImageView datePoint;
	private ImageView cityPoint;
	private LinearLayout detailView;
	private int status;

	
	/*
	 * 对编辑用户资料界面的一些
	 * 控件进行初始化
	 *
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
		addContent();
	}

	/*
     *
     *在onresume的生命周期方法中
     * 每次调用的时候
     * 都去从本地获取用户的基本信息 展示到界面上	
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// 获取我的详细信息(从内存里面查用户)
		ExtUserVo uvo = SystemContext.getInstance().getExtUserVo();
		if (uvo != null) {
			renderContent(uvo);
		}
	}

	/**
	 * 
	 * 初始化用户界面
	 *  的控件
	 */
	private void initialize() {
		// 显示左边
		setLeftVisible(true);
		// 影藏右边
		setRightVisible(false);
		// 设置TOP TITLE
		TextView titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText("编辑资料");
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		 detailView = (LinearLayout) View.inflate(this, R.layout.setting_my_detail_info_edit, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(detailView, params);
		// 设置可编辑项
		detailView.findViewById(R.id.nicknameItem).setOnClickListener(this);
		detailView.findViewById(R.id.sexItem).setOnClickListener(this);
		detailView.findViewById(R.id.ageItem).setOnClickListener(this);
		detailView.findViewById(R.id.cityItem).setOnClickListener(this);
		detailView.findViewById(R.id.jobItem).setOnClickListener(this);
		detailView.findViewById(R.id.moodItem).setOnClickListener(this);
		detailView.findViewById(R.id.gameTimeItem).setOnClickListener(this);
		detailView.findViewById(R.id.likeGameItem).setOnClickListener(this);
		// 设置可选内容
		userAvter = (ImageView)detailView.findViewById(R.id.icon);
		userAvter.setOnClickListener(this);
		nickname = (TextView) detailView.findViewById(R.id.nickname);
		mood = (TextView) detailView.findViewById(R.id.mood);
		sex = (TextView) detailView.findViewById(R.id.sex);
		age = (TextView) detailView.findViewById(R.id.age);
		city = (TextView) detailView.findViewById(R.id.city);
		job = (TextView) detailView.findViewById(R.id.job);
		gameTime = (TextView) detailView.findViewById(R.id.gameTime);
		likeGame = (TextView) detailView.findViewById(R.id.likeGame);
		//积分图标
		iconPoint =(ImageView)detailView.findViewById(R.id.icon_image);
		nickPoint =(ImageView)detailView.findViewById(R.id.nick_name_image);
		genderPoint =(ImageView)detailView.findViewById(R.id.gener_image);
		datePoint =(ImageView)detailView.findViewById(R.id.grith_image);
		cityPoint =(ImageView)detailView.findViewById(R.id.city_image);
		detailView.setVisibility(View.GONE);
	}

	
	/**
	 * 渲染用户基本信息
	 */
	private void renderContent(ExtUserVo vo) {
		// 获取上下文用户信息
		if (vo != null) {
			if(vo.getAvatar() != null){
				ImageViewUtil.showImage(userAvter, vo.getAvatar(), R.drawable.common_user_icon_default);
			}
			nickname.setText(vo.getUsername() == null ? "" : vo.getUsername());
			mood.setText(vo.getMood() == null ? "" : vo.getMood());
			if(vo.getSex() == 0){
				sex.setText("男");
			}else if(vo.getSex() == 1){
				sex.setText("女");
			}else{
				sex.setText("");
			}
			age.setText(vo.getAge() > 0 ? String.valueOf(vo.getAge()) : "");
			city.setText(vo.getCity() == null ? "" : vo.getCity());
			job.setText(vo.getJob() == null ? "" : vo.getJob());
			gameTime.setText(vo.getGameTime() == null ? "" : vo.getGameTime());
			likeGame.setText(vo.getLikeGameType() == null ? "" : vo.getLikeGameType());
		} else {
			nickname.setText("");
			mood.setText("");
			sex.setText("");
			age.setText("");
			city.setText("");
			job.setText("");
			gameTime.setText("");
			likeGame.setText("");
			ToastUtil.showToast(this, getString(R.string.setting_userinfo_isnull));
		}
	}

	/**
	 * 对于界面上的按钮 
	 * 设置点击事件
	 * 弹出对话框，用于用户编辑自己的个人信息
	 */
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.icon){
			PhotoUtil.showSelectDialog(EditDetailActivity.this);
		}else if (v.getId() == R.id.nicknameItem) {
			v.setEnabled(false);
			showModifyNameView(v);
		} else if (v.getId() == R.id.moodItem) {
			v.setEnabled(false);
			showModifyMoodView(v);
		} else if (v.getId() == R.id.sexItem) {
			v.setEnabled(false);
			showModifySexView(v);
		} else if (v.getId() == R.id.ageItem) {
			v.setEnabled(false);
			showModifyAgeView(v);
		} else if (v.getId() == R.id.cityItem) {
			v.setEnabled(false);
			showModifyAreaView(v);
		} else if (v.getId() == R.id.jobItem) {
			v.setEnabled(false);
			showModifyJobView(v);
		} else if (v.getId() == R.id.gameTimeItem) {
			v.setEnabled(false);
			modifyGametimeView(v,new String[] { "三个月以内", "一年", "两年", "三年", "四年", "五年", "六年", "七年", "八年", "九年", "十年", "十年以上" });
		} else if (v.getId() == R.id.likeGameItem) {
			v.setEnabled(false);
			modifyLikeGameTypeView(v,new String[] { "益智贴吧", "射击贴吧", "策略贴吧", "动作冒险", "赛车竞速", "模拟经营", "角色扮演", "体育运动", "棋牌桌游", "虚拟养成", "音乐贴吧", "对战格斗",
					"手机网游" });
		}
	}

	/**
	 * 修改用户的
	 * 昵称方法 
	 */
	private void showModifyNameView(final View v) {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		final EditText txt = new EditText(this);
		txt.setTextColor(getResources().getColor(R.color.dialog_content_text_color));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText(nickname.getText());
		txt.setSelection(nickname.getText().toString().length());
		txt.setBackgroundResource(R.drawable.common_edit_text_bg);
		content.setPadding(DisplayUtil.dip2px(this, 10), 0, DisplayUtil.dip2px(this, 10), 0);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(this, 50)));
		InputFilterUtil.lengthFilter(this, txt, 16, getString(R.string.nickname_verify_fail, 16));
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("昵称设置");
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String nickname = txt.getText().toString();
				if(EditDetailActivity.this.nickname.getText().toString().equals(nickname)){
					dialog.dismiss();
					return;
				}
				if(!nickname.trim().isEmpty()){
					if (!nickname.matches("[0-9]+") && StringUtil.getCharacterNum(nickname) >= 4) {
						if (ServiceFactory.getInstance().getWordsManager().matchName(nickname)) {
							ToastUtil.showToast(EditDetailActivity.this, EditDetailActivity.this.getResources().getString(R.string.global_words_error));
						}else if(nickname.trim().length() <= 0){
							ToastUtil.showToast(EditDetailActivity.this, getString(R.string.name_not_total_space), 1000);
						} else {
							modifyAccountInfo(null, null, null, null, null, null, null, nickname, null, null, null);
							dialog.dismiss();
						}
					} else {
						ToastUtil.showToast(EditDetailActivity.this, "输入的昵称不能少于2个汉字或4个字符哦！");
					}
				}else{
					ToastUtil.showToast(EditDetailActivity.this, "昵称不能全为纯空格哦！");
				}
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				v.setEnabled(true);
			}
		});
		dialog.show();
	}

	/**
	 * 显示修改职业
	 */
	private void showModifyJobView(final View v) {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		final EditText txt = new EditText(this);
		txt.setTextColor(getResources().getColor(R.color.dialog_content_text_color));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText(job.getText().toString());
		txt.setSelection(job.getText().toString().length());
		txt.setSingleLine();
		txt.setBackgroundResource(R.drawable.common_edit_text_bg);
		content.setPadding(DisplayUtil.dip2px(this, 10), 0, DisplayUtil.dip2px(this, 10), 0);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(this, 50)));
		InputFilterUtil.lengthFilter(this, txt, 20, getString(R.string.job_verify_fail, 10));
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("职业设置");
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String tjob = txt.getText().toString();
				if (!tjob.isEmpty() && !tjob.matches("[0-9]+")) {
					if (ServiceFactory.getInstance().getWordsManager().match(tjob)) {
						ToastUtil.showToast(EditDetailActivity.this, EditDetailActivity.this.getResources().getString(R.string.global_words_error));
					} else {
						if(tjob.trim().isEmpty()){
							ToastUtil.showToast(EditDetailActivity.this, getString(R.string.job_not_total_space), 1000);
						}else{
							modifyAccountInfo(null, null, null, txt.getText().toString(), null, null, null, null, null, null, null);
							dialog.dismiss();
						}
					}
				} else {
					ToastUtil.showToast(EditDetailActivity.this, getString(R.string.account_modify_job_verify_fail), 1000);
				}
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
 
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				v.setEnabled(true);
			}
		});
		dialog.show();
	}

	
	/**
	 * 显示修改签名
	 */
	private void showModifyMoodView(final View v) {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		final EditText txt = new EditText(this);
		txt.setTextColor(getResources().getColor(R.color.dialog_content_text_color));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText(mood.getText());
		txt.setSelection(mood.getText().toString().length());
		txt.setBackgroundResource(R.drawable.common_edit_text_bg);
		content.setPadding(DisplayUtil.dip2px(this, 10), 0, DisplayUtil.dip2px(this, 10), 0);
		content.removeAllViews();
		InputFilterUtil.lengthFilter(this, txt, 40, getString(R.string.mood_verify_fail, 20));
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(this, 50)));
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("签名设置");
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String moode = txt.getText().toString();
				if (!txt.getText().toString().isEmpty()) {
					if (ServiceFactory.getInstance().getWordsManager().match(moode)) {
						ToastUtil.showToast(EditDetailActivity.this, EditDetailActivity.this.getResources().getString(R.string.global_words_error));
					} else {
						if(!txt.getText().toString().trim().isEmpty()){
							modifyAccountInfo(null, moode, null, null, null, null, null, null, null, null, null);
							dialog.dismiss();
						}else{
							ToastUtil.showToast(EditDetailActivity.this, getString(R.string.mood_not_space));
						}
					}
				} else {
					ToastUtil.showToast(EditDetailActivity.this, getString(R.string.txt_verify_fail));
				}
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				v.setEnabled(true);
			}
		});
		dialog.show();
	}

	
	/**
	 * 显示修改出身年份 对话框
	 */
	private void showModifyAgeView(final View v) {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		NumberPicker np = new NumberPicker(dialog.getContext());
		content.removeAllViews();
		content.addView(np, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("年份设置");

		Calendar c = Calendar.getInstance();
		final int year = c.get(Calendar.YEAR);
		np.setMaxValue(year-1);
		np.setMinValue(year - 91);
		np.setFocusable(false);
		np.setFocusableInTouchMode(true);
		np.setWrapSelectorWheel(false);
		if(age != null && !"".equals(age.getText().toString())){
			np.setValue(Integer.parseInt(age.getText().toString()));
		}else{
			np.setValue(1988);
		}
		selectedValue = np.getValue();
		np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				selectedValue = newVal;
			}
		});
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				modifyAccountInfo(null, null, selectedValue, null, null, null, null, null, null, null, null);
				dialog.dismiss();
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
         
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				v.setEnabled(true);
			}
		});
		dialog.show();
	}

	/**
	 * 显示修改性别对话框
	 */
	private void showModifySexView(final View v) {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("性别设置");
		NumberPicker np = new NumberPicker(dialog.getContext());
		content.removeAllViews();
		content.addView(np, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		np.setMaxValue(1);
		np.setMinValue(0);
		np.setFocusable(false);
		np.setFocusableInTouchMode(true);
		np.setWrapSelectorWheel(false);
		np.setDisplayedValues(new String[] { "男", "女" });
		selectedValue = 0;
		np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				selectedValue = newVal;
			}
		});
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				modifyAccountInfo(selectedValue, null, null, null, null, null, null, null, null, null, null);
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				v.setEnabled(true);
			}
		});
		dialog.show();
	}

	/**
	 * 修改用户信息
	 * 
	 * @param sex
	 * @param mood
	 * @param age
	 * @param job
	 * @param avatarData
	 * @param avatar
	 * @param city
	 * @param nickname
	 * @param desc
	 * @param gameTime
	 * @param likeGameType
	 */
	private void modifyAccountInfo(final Integer sex, final String mood, final Integer age, final String job, final byte[] avatarData,
			final String avatar, final String city, final String nickname, final String desc, final String gameTime, final String likeGameType) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getSettingProxy().modifyAccountInfo(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					if (sex != null) {
						SystemContext.getInstance().setSex(sex);
						EditDetailActivity.this.sex.setText(selectedValue == 0 ? "男" : "女");
					}
					if (mood != null && !mood.isEmpty()) {
						SystemContext.getInstance().setMood(mood);
						EditDetailActivity.this.mood.setText(mood);
					}
					if (age != null) {
						SystemContext.getInstance().setAge(age);
						EditDetailActivity.this.age.setText(age + "");
					}
					if (job != null && !job.isEmpty()) {
						SystemContext.getInstance().setJob(job);
						EditDetailActivity.this.job.setText(job);
					}
					if (avatar != null && !avatar.isEmpty()) {
						SystemContext.getInstance().setAvatar(avatar);
					}
					if (city != null && !city.isEmpty()) {
						SystemContext.getInstance().setCity(city);
						EditDetailActivity.this.city.setText(city);
					}
					if (nickname != null && !nickname.isEmpty()) {
						if(SystemContext.getInstance().getIsGuest() == 1){//游客
							SystemContext.getInstance().setIsUpdateUserName(1);//游客已更改资料
						}
						SystemContext.getInstance().setUsername(nickname);
						EditDetailActivity.this.nickname.setText(nickname);
					}
					if (gameTime != null && !gameTime.isEmpty()) {
						SystemContext.getInstance().setGameTime(gameTime);
						EditDetailActivity.this.gameTime.setText(gameTime);
					}
					if (likeGameType != null && !likeGameType.isEmpty()) {
						SystemContext.getInstance().setLikeGameType(likeGameType);
						EditDetailActivity.this.likeGame.setText(likeGameType);
					}
					break;
				default:
					ToastUtil.showToast(EditDetailActivity.this, getString(R.string.account_modify_fail));
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ToastUtil.showToast(EditDetailActivity.this, getString(R.string.account_modify_fail));
				dialog.dismiss();
			}
		}, this, sex, mood, age, job, avatarData, avatar, city, nickname, desc, gameTime, likeGameType, null, null);
	}

	
	/**
	 * 修改所在地
	 */
	private void showModifyAreaView(final View v) {
		// 获取省列表
		ProxyFactory.getInstance().getSettingProxy().findAreaByType(new ProxyCallBack<List<AreaVo>>() {

			@Override
			public void onSuccess(List<AreaVo> result) {
				showAreaDialog(v,result);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				v.setEnabled(true);
			}
		}, null);
	}

	/**
	 * 显示地区对话框
	 * 
	 * @param result
	 */
	private void showAreaDialog(final View v,final List<AreaVo> result) {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		LinearLayout dialogContent = (LinearLayout) View.inflate(this, R.layout.fragment_setting_city_dialog_content, null);
		content.addView(dialogContent);
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("城市设置");
		LinearLayout leftPicker = (LinearLayout) dialogContent.findViewById(R.id.leftPicker);
		NumberPicker np = new NumberPicker(dialog.getContext());
		leftPicker.removeAllViews();
		leftPicker.addView(np, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		np.setMaxValue(result.size() - 1);
		np.setMinValue(0);
		np.setFocusable(false);
		np.setFocusableInTouchMode(true);
		np.setWrapSelectorWheel(false);
		// 设置显示地区名称
		String[] values = new String[result.size()];
		for (int i = 0; i < result.size(); i++) {
			values[i] = result.get(i).getAreaname();
		}
		np.setDisplayedValues(values);
		getCity(dialog, result.get(0).getId());
		areaname = result.get(np.getValue()).getAreaname();
		np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				areaname = result.get(newVal).getAreaname();
				getCity(dialog, result.get(newVal).getId());
			}
		});
		// 添加确定按钮功能
		final Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (areaname != null && !areaname.isEmpty()) {
					String cname = null;
					if (SystemConfig.OTHER_AREA.equals(areaname)) {
						cname = cityname == null ? "" : cityname;
					} else {
						cname = areaname + (cityname == null ? "" : cityname);
					}
					modifyAccountInfo(null, null, null, null, null, null, cname, null, null, null, null);
				} else {
					ToastUtil.showToast(EditDetailActivity.this, getString(R.string.city_verify_fail2));
				}
				dialog.dismiss();

			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cityname = null;
				areaname = null;
				dialog.dismiss();
			}
		});

		np.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChange(NumberPicker view, int scrollState) {
				switch (scrollState) {
				case android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_FLING:
					commitBtn.setEnabled(false);
					break;

				case android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE:
					commitBtn.setEnabled(true);
					break;
				case android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					commitBtn.setEnabled(false);
					break;
				}
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				v.setEnabled(true);
			}
		});
		dialog.show();
	}

	/**
	 * 获取城市
	 * 
	 * @param parentid
	 */
	private void getCity(final Dialog dialog, int parentid) {
		ProxyFactory.getInstance().getSettingProxy().findAreaByParentid(new ProxyCallBack<List<AreaVo>>() {

			@Override
			public void onSuccess(final List<AreaVo> result) {
				if (result != null && result.size() > 0) {
					LinearLayout cityPicker = (LinearLayout) dialog.findViewById(R.id.rightPicker);
					NumberPicker np = new NumberPicker(dialog.getContext());
					cityPicker.removeAllViews();
					cityPicker.addView(np, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					np.setMaxValue(result.size() - 1);
					np.setMinValue(0);
					np.setFocusable(false);
					np.setFocusableInTouchMode(true);
					np.setWrapSelectorWheel(false);
					// 设置显示地区名称
					String[] values = new String[result.size()];
					for (int i = 0; i < result.size(); i++) {
						values[i] = result.get(i).getAreaname();
					}
					np.setDisplayedValues(values);
					cityname = result.get(np.getValue()).getAreaname();
					np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

						@Override
						public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
							cityname = result.get(newVal).getAreaname();
						}
					});
				} else {
					LinearLayout cityPicker = (LinearLayout) dialog.findViewById(R.id.rightPicker);
					cityPicker.removeAllViews();
					cityname = "";
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
			}
		}, parentid);
	}

	/**
	 * 修改玩贴吧时长
	 * 
	 * @param valueList
	 */
	private void modifyGametimeView(final View v,final String[] valueList) {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		NumberPicker np = new NumberPicker(dialog.getContext());
		content.removeAllViews();
		content.addView(np, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("玩贴吧时长设置");

		np.setMaxValue(valueList.length - 1);
		np.setMinValue(0);
		np.setFocusable(false);
		np.setFocusableInTouchMode(true);
		np.setWrapSelectorWheel(false);
		// 设置显示地区名称
		String[] values = new String[valueList.length];
		for (int i = 0; i < valueList.length; i++) {
			values[i] = valueList[i];
		}
		np.setDisplayedValues(values);
		gameTiemValue = valueList[np.getValue()];
		np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				gameTiemValue = valueList[newVal];
			}
		});
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (gameTiemValue != null && !gameTiemValue.isEmpty()) {
					modifyAccountInfo(null, null, null, null, null, null, null, null, null, gameTiemValue, null);
					dialog.dismiss();
				} else {
					ToastUtil.showToast(EditDetailActivity.this, getString(R.string.account_game_time_verify_fail));
				}
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				v.setEnabled(true);
			}
		});
		dialog.show();
	}

	/**
	 * 钟情贴吧类型设置
	 * 
	 * @param valueList
	 */
	private void modifyLikeGameTypeView(final View v,final String[] valueList) {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		NumberPicker np = new NumberPicker(dialog.getContext());
		content.removeAllViews();
		content.addView(np, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("钟情贴吧类型设置");

		np.setMaxValue(valueList.length - 1);
		np.setMinValue(0);
		np.setFocusable(false);
		np.setFocusableInTouchMode(true);
		np.setWrapSelectorWheel(false);
		// 设置显示地区名称
		String[] values = new String[valueList.length];
		for (int i = 0; i < valueList.length; i++) {
			values[i] = valueList[i];
		}
		np.setDisplayedValues(values);
		likeGameTypeValue = valueList[np.getValue()];
		np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				likeGameTypeValue = valueList[newVal];
			}
		});
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (likeGameTypeValue != null && !likeGameTypeValue.isEmpty()) {
					modifyAccountInfo(null, null, null, null, null, null, null, null, null, null, likeGameTypeValue);
					dialog.dismiss();
				} else {
					ToastUtil.showToast(EditDetailActivity.this, getString(R.string.account_game_type_verify_fail));
				}
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				v.setEnabled(true);
			}
		});
		dialog.show();
	}

	/**
	 * 当调用了startactivityforresult后
	 * 会调用此方法 ，一个是修改头像成功后，
	 * 要重新设置当前界面的用户头像
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
				Display mDisplay = this.getWindowManager().getDefaultDisplay();
				int w = mDisplay.getWidth();
				int h = mDisplay.getHeight();
				int imagewidth = w > h ? h : w;
				imageUri = Uri.parse("file://" + PhotoUtil.sdcardTempFilePath);
				PhotoUtil.doCropBigPhoto(this, imageUri, imageUri, 1, 1, imagewidth, imagewidth);
				return;
			case PhotoUtil.PHOTO_PICKED_WITH_DATA:
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				Display mDisplay2 = this.getWindowManager().getDefaultDisplay();
				int w2 = mDisplay2.getWidth();
				int h2 = mDisplay2.getHeight();
				int imagewidth2 = w2 > h2 ? h2 : w2;
				imageUri = Uri.parse("file://" + PhotoUtil.sdcardTempFilePath);
				PhotoUtil.doCropBigPhoto(this, originalUri, imageUri, 1, 1, imagewidth2, imagewidth2);
				return;
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
						photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, 30);
					}
				} else {
					if (imageUri != null) {
						try {
							photo = ImageUtil.decodeUri2Bitmap(this.getContentResolver(), imageUri);
						} catch (Exception e) {
							e.printStackTrace();
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
						}
						if(photo != null){
							photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, 30);
						}
					}
				}
				break;
			}
			LogUtil.d(TAG, "photo =" + photo);
			if (photo != null) {
				avatar = photoByte;
				showAvatarImage(photo, avatar);
				if(!photo.isRecycled()){
					photo.recycle();
					photo = null;
					//System.gc();
				}
			} else {
				ToastUtil.showToast(this, getResources().getString(R.string.common_add_photo_error));
				LogUtil.e(TAG, "获得需要发送的图片异常");
				return;
			}
		} else {
			LogUtil.e(TAG, "选择发送的图片异常");
		}
	}
	
	
	
	

	/**
	 * 显示头像图片
	 */
	private void showAvatarImage(final Bitmap bmp, byte[] bymp) {
		// 添加照片显示
		if (bmp != null) {
			final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
			dialog.show();
			// 发送请求提交服务端
			ProxyFactory.getInstance().getSettingProxy().modifyAccountAvatar(new ProxyCallBack<ResourceVo>() {

				@Override
				public void onSuccess(ResourceVo result) {
					if (result != null) {
						ImageViewUtil.showImage(userAvter, result.getResourceId(), 0);
						SystemContext.getInstance().setAvatar(result.getResourceId());
						String imageUrl = ResUtil.getSmallRelUrl(result.getResourceId());
						if(imageUrl != null && !imageUrl.isEmpty()){
							ProxyFactory.getInstance().getUserProxy().getImageUrlToData(new ProxyCallBack<Integer>() {
								
								@Override
								public void onSuccess(Integer result) {
									LogUtil.d(TAG, "图片保存成功");
								}
								
								@Override
								public void onFailure(Integer result, String resultMsg) {
									LogUtil.d(TAG, "图片保存失败");
								}
							}, EditDetailActivity.this, imageUrl, SystemConfig.LOGIN_USER_ICON_NAME);
						}
					} else {
						ToastUtil.showToast(EditDetailActivity.this, "设置头像失败");
					}
					dialog.dismiss();
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					ToastUtil.showToast(EditDetailActivity.this, "设置头像失败");
					dialog.dismiss();
				}
			}, EditDetailActivity.this, bymp);
		} else {
			return;
		}
	}
	/**
	 * 首先去服务器
	 * 获取新手任务
	 */
	protected void addContent() {
		
		/**
		 * 下面的这个方法是去获取新手任务
		 */
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this, false);
		dialog.show();
		ProxyFactory.getInstance().getUserProxy().getPointTask(new ProxyCallBack<List<UserPointTaskObj>>() {

			@Override
			public void onSuccess(List<UserPointTaskObj> result) {
				//三、获取每日任务
				dialog.dismiss();
				getDayTask(result);
				
			}
			@Override
			public void onFailure(Integer result, String resultMsg) {
				dialog.dismiss();
				detailView.setVisibility(View.VISIBLE);
			}
		}, EditDetailActivity.this, 1,1);
	}
	private void getDayTask(List<UserPointTaskObj> result) {
		// TODO Auto-generated method stub
		if(result==null&&result.size()<=0) return;
		for(int i=0;i<result.size();i++){
			UserPointTaskObj obj = result.get(i);
			int toids = obj.getPointTask().getToid();
			if(toids==12){
				if(obj.getStatus()==3){
					iconPoint.setVisibility(View.GONE);
					nickPoint.setVisibility(View.GONE);
					genderPoint.setVisibility(View.GONE);
					datePoint.setVisibility(View.GONE);
					cityPoint.setVisibility(View.GONE);
				}else{
					iconPoint.setVisibility(View.VISIBLE);
					nickPoint.setVisibility(View.VISIBLE);
					datePoint.setVisibility(View.VISIBLE);
					cityPoint.setVisibility(View.VISIBLE);
					genderPoint.setVisibility(View.VISIBLE);
				}
			}
		}
		detailView.setVisibility(View.VISIBLE);
	}
}