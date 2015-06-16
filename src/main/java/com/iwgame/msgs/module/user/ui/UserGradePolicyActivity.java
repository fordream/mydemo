package com.iwgame.msgs.module.user.ui;

import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.UserGradeDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.UserGradeVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.youban.msgs.R;

/**
 * 
 * @ClassName: UserGradePolicyActivity
 * @Description:  用户等级界面
 * @author zhangjianchuan
 * @date 2014-10-9 上午10:30:26
 * @Version 1.0
 * 
 */
public class UserGradePolicyActivity extends BaseActivity {

	private TextView upgradeBtn;// 升级按钮，要用到
	private TextView user_or_group_grade;// 显示用户的等级
	private TextView user_or_group_need_point;// 显示升级所需要的积分
	private TextView user_or_group_point;// 显示用户的积分
	private LinearLayout user_or_group_upgrade_detail;// 用户等级列表
	private LinearLayout user_or_group_upgrade_more;
	private List<UserGradeVo> list = null;// 用户等级集合
	private Dialog dialog;// 升级成功后弹框所需
	private Button btn; // 弹框时用到的按钮
	private TextView cueWords;// 弹框里面的提示语
	private TextView gradeIcon;// 以图片的开工显示icon的等级
	private ProgressBar gradeProgress;// 显示用户升级所需要的积分进度
	private ImageView line;
	private Drawable drawable;
	private Drawable clickDrawable;
	private int progress;
	private CustomProgressDialog customProgressDialog;
	private boolean flag = false;
	private Dialog upgradeDialog;
	private TextView needPointDesc;
	private UserGradeDao userGradeDao;
	private TextView tv;

	/**
	 * 界面启动的时候 执行下面的这个方法
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		customProgressDialog = CustomProgressDialog.createDialog(this, false);
		initialize();
	}

	/**
	 * 做一些界面的 
	 * 初始化工作 
	 * 获取用户的详细信息
	 */
	private void initialize() {
		userGradeDao = DaoFactory.getDaoFactory().getUserGradeDao(SystemContext.getInstance().getContext());
		setTitleTxt(getResources().getString(R.string.personal_point));
		View view = View.inflate(this, R.layout.personal_integral_grade, null);
		upgradeBtn = (TextView) view.findViewById(R.id.user_or_group_grade_btn);
//		upgradeBtn.setText(R.string.experience_value_detailed);
//		upgradeBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		upgradeBtn.setText(Html.fromHtml("<u>经验值明细>></u>"));
		gradeIcon = (TextView) view.findViewById(R.id.grade_icon);
		user_or_group_grade = (TextView) view.findViewById(R.id.grade_txt);
		user_or_group_point = (TextView) view.findViewById(R.id.user_point);
		line = (ImageView) view.findViewById(R.id.line);
		user_or_group_need_point = (TextView) view.findViewById(R.id.show_need_number);
		gradeProgress = (ProgressBar) view.findViewById(R.id.grade_progressbar);
		user_or_group_upgrade_detail = (LinearLayout) view.findViewById(R.id.one_to_five_grade);
		user_or_group_upgrade_detail.setOrientation(LinearLayout.HORIZONTAL);
		user_or_group_upgrade_more = (LinearLayout) view.findViewById(R.id.six_to_ten_grade);
		needPointDesc = (TextView) view.findViewById(R.id.need_point_number);
		user_or_group_upgrade_more.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(view, params);
		setView();// 界面刚起来的时候，显示的值
		setListener();// 给按钮设置监听器
		setData();
	}

	/**
	 * 首先从本地 数据库里面获取数据， 如果获取到数据了显示， 获取不到数据，则去加载
	 */
	private void setData() {
//		List<UserGradeVo> userGradeList = userGradeDao.queryAll();
//		if (userGradeList != null && userGradeList.size() > 0) {
//			// 第一步是要把获取到的数据添加到LinearLayout里面去
//			addDataToLinearLayout(userGradeList);
//			// 第二步，通过获取到的数据，要更新一个ui界面上的东西，按钮是否显示，所需要的积分是多少
//			updateLayout();
//		} else {
//			fetchUserGradeData();
//		}
		fetchUserGradeData();
	}

	/**
	 * 获取个人用户 等级的划分详情信息
	 */
	private void fetchUserGradeData() {
		/**
		 * 获取用户等级的划分信息
		 */
		customProgressDialog.show();
		ProxyFactory.getInstance().getUserProxy().getUserGradePolicy(new ProxyCallBack<List<UserGradeVo>>() {

			@Override
			public void onSuccess(List<UserGradeVo> result) {
				// 第一步是要把获取到的数据添加到LinearLayout里面去
				addDataToLinearLayout(result);
				// 第二步，通过获取到的数据，要更新一个ui界面上的东西，按钮是否显示，所需要的积分是多少
				if (result != null && result.size() > 0)
					updateLayout();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				handleErrorCode(result, resultMsg);
				customProgressDialog.dismiss();
			}
		}, this);
	}

	/**
	 * 更新一下 界面的布局
	 */
	protected void updateLayout() {
		try {
			// 获取用户的等级和用户的积分
			int grade = SystemContext.getInstance().getExtUserVo().getGrade();
			changeBtn(grade);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 改变升级按钮的背景
	 * 
	 * @param grade
	 */
	private void changeBtn(int grade) {
		if (grade == 10 || grade >= list.size()) {
			user_or_group_need_point.setText("0");
			needPointDesc.setVisibility(View.VISIBLE);
			flag = false;
			gradeProgress.setProgress(100);
		//	upgradeBtn.setTextColor(this.getResources().getColor(R.color.upgrade_btn_text_color_dis));
		//	upgradeBtn.setBackgroundDrawable(drawable);
			user_or_group_point.setText("满级");
		//	upgradeBtn.setText("满级");
		} 
		else if (grade == 1) {
			
//			user_or_group_need_point.setText("绑定手机号1");
//			needPointDesc.setVisibility(View.INVISIBLE);
//			flag = false;
//			gradeProgress.setProgress(0);
//		//	upgradeBtn.setTextColor(this.getResources().getColor(R.color.upgrade_btn_text_color_dis));
//			//upgradeBtn.setBackgroundDrawable(drawable);
//			user_or_group_point.setText("绑定手机号1");
//			//upgradeBtn.setText("升到LV2");
			int point_number = SystemContext.getInstance().getExp();
			gradeProgress.setProgress(0);
			user_or_group_point.setText(list.get(grade).getOptions());
			
		} 
		else if (list != null) {
			int point_number = SystemContext.getInstance().getExp();
			if (point_number >= list.get(grade).getPoint()) {
				user_or_group_need_point.setText("" + 0);
				needPointDesc.setVisibility(View.VISIBLE);
			//	upgradeBtn.setEnabled(true);
				flag = true;
				gradeProgress.setProgress(100);
			//	upgradeBtn.setTextColor(this.getResources().getColor(R.color.upgrade_btn_text_color_nor));
			//	upgradeBtn.setBackgroundDrawable(clickDrawable);
				user_or_group_point.setText(list.get(grade).getPoint() + "经验值/" + list.get(grade).getPoint() + "经验值");
			} else {
				user_or_group_need_point.setText("" + (list.get(grade).getPoint() - point_number));
				needPointDesc.setVisibility(View.VISIBLE);
				flag = false;
			//	upgradeBtn.setTextColor(this.getResources().getColor(R.color.upgrade_btn_text_color_dis));
			//	upgradeBtn.setBackgroundDrawable(drawable);
				progress = (point_number * 100) / list.get(grade).getPoint();
				gradeProgress.setProgress(progress);
				user_or_group_point.setText(point_number + "经验值/" + list.get(grade).getPoint() + "经验值");
			}
			//upgradeBtn.setText("升到LV" + (grade + 1));
			
		}
	}

	/**
	 * 将返回来的VIP等级划分数据 添加到LinearLayout里面
	 */
	protected void addDataToLinearLayout(List<UserGradeVo> result) {
		user_or_group_upgrade_more.removeAllViews();
		user_or_group_upgrade_detail.removeAllViews();
		this.list = result;
		if (result == null || result.size() <= 0) {
			return;
		}
		// 首先把最左边的一栏标题给加进去
		View view_title = View.inflate(this, R.layout.personal_integral_detail_upgrade_title, null);
		View view_title1 = View.inflate(this, R.layout.personal_integral_detail_upgrade_title, null);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		user_or_group_upgrade_detail.addView(view_title, params);
		user_or_group_upgrade_more.addView(view_title1, params);
		// 然后把获取到的数据一列一列的加进来
		int size = result.size();
		for (int i = 0; i < size; i++) {
			UserGradeVo obj = result.get(i);
			if (obj.getStatus() != 0)
				continue;
			View view = View.inflate(this, R.layout.personal_integral_detail_upgrade_item_view, null);
			TextView user_vip_grade = (TextView) view.findViewById(R.id.user_vip_grade);
			TextView cosume_point = (TextView) view.findViewById(R.id.cosume_point);
			TextView creat_group_number = (TextView) view.findViewById(R.id.creat_group_number);
			TextView join_group_number = (TextView) view.findViewById(R.id.join_group_number);
			TextView care_game_number = (TextView) view.findViewById(R.id.care_game_number);
			TextView send_postbar_number = (TextView) view.findViewById(R.id.send_postbar_number);
			TextView point_times = (TextView) view.findViewById(R.id.point_times);
			user_vip_grade.setText("LV" + obj.getGrade());
			try {
				int point = Integer.parseInt(obj.getOptions());
				cosume_point.setText(((point / 10000) >= 1) ? (point / 10000 + "万") : (point + ""));
			} catch (Exception e) {
				cosume_point.setText(obj.getOptions() + "");
			}
			creat_group_number.setText(obj.getCreategroup() + "");
			join_group_number.setText(obj.getJoingroup() + "");
			care_game_number.setText(obj.getFollowgame() + "");
			send_postbar_number.setText(obj.getSendpost() + "");
			point_times.setText(obj.getMultiple() + "");
			LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
			if (i < 5) {
				user_or_group_upgrade_detail.addView(view, param);
			} else if (i >= 5) {
				user_or_group_upgrade_more.addView(view, param);
			}
		}
		line.setVisibility(View.VISIBLE);
		customProgressDialog.dismiss();
	}

	/**
	 * 在这个方法里面 主要是刷新数据
	 */
	@Override
	protected void onResume() {
		super.onResume();
	//	if(upgradeBtn != null)upgradeBtn.setEnabled(true);
		int grade = SystemContext.getInstance().getExtUserVo().getGrade();
		user_or_group_grade.setText("LV" + grade);
		gradeIcon.setText(grade + "");
		updateLayout();
	}

	/**
	 * 给界面上的 按钮设置监听器
	 */
	private void setListener() {
//		upgradeBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// flag变量判断是否达到 了升级条件
//				if (flag) {
//					if (SystemContext.getInstance().isNeedBundPhone(SystemConfig.USERACTION_PERSONAL_UPGRADE)) {
//						upgradeBtn.setEnabled(false);
//						createBundPhoneDialog();
//					} else {
//						// 获取到当前的等级
//						String user_or_group_grade_txt = user_or_group_grade.getText().toString();
//						// 升级
//						upgrade(user_or_group_grade_txt);
//					}
//				} else {
//					if (SystemContext.getInstance().getExtUserVo().getGrade() < 2) {
//						// 如果是游客 提示用户绑定手机号
//						upgradeBtn.setEnabled(false);
//						createBundPhoneDialog();
//					} else {
//						if (list != null && SystemContext.getInstance().getExtUserVo().getGrade() != list.size()) {
//							tv.setText("积分还未达到升级条件哦！");
//							upgradeDialog.show();
//						} else {
//							tv.setText("当前已达到最高等级了！");
//							upgradeDialog.show();
//						}
//					}
//				}
//			}
//		});
		upgradeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserGradePolicyActivity.this,UserExperienceDetailActivity.class);
				startActivity(intent);
			}
		});
	}
//	/**
//	 * 当用户等级是1级时点击
//	 * 绑定手机号
//	 */
//	public void setBundPhone(){
//		user_or_group_need_point.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				createBundPhoneDialog();
//			}
//		});
//	}
	/**
	 * 弹出绑定手机的对话框
	 * 
	 * @param actionName
	 */
	private void createBundPhoneDialog() {
		Intent intent = new Intent(UserGradePolicyActivity.this, BundPhoneActivity.class);
		UserGradePolicyActivity.this.startActivity(intent);
	}

	/**
	 * 把当前用户的等级传进去 点击这个按钮就是执行升级的操作
	 * 
	 * @param user_or_group_grade_txt
	 */
	protected void upgrade(final String user_or_group_grade_txt) {
		ProxyFactory
				.getInstance()
				.getUserProxy()
				.userAction(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						int grade = SystemContext.getInstance().getExtUserVo().getGrade();
						showCueWords(grade);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						Log.i("false", "false");
						handleErrorCode(result, resultMsg);
					}
				}, UserGradePolicyActivity.this, SystemContext.getInstance().getExtUserVo().getUserid(), MsgsConstants.OT_USER,
						MsgsConstants.OP_GRADE_UP, null, null, null);
	}

	/**
	 * 升级成功后，要对 界面做出一定的修改
	 */
	private void showCueWords(int grade) {
		cueWords.setText("恭喜你，成功升级到LV" + (grade + 1) + "！");
		SystemContext.getInstance().setGrade(grade + 1);
		SystemContext.getInstance().setPoint(SystemContext.getInstance().getPoint() - list.get(grade >= list.size() ? list.size() - 1 : grade).getPoint());
		user_or_group_grade.setText("LV" + (grade + 1));
		gradeIcon.setText("" + (grade + 1));
		dialog.show();
		updateLayout();
	}

	
	/**
	 * 给界面上的 一些控件设置内容
	 * 当界面刚创建的时候 显示一些默认的值
	 */
	private void setView() {
		clickDrawable = this.getResources().getDrawable(R.drawable.group_level_nor);
		// 首先把升级按钮设置成灰色,不可以点击时候的图片
		drawable = this.getResources().getDrawable(R.drawable.group_level_dis);
		flag = false;
		//upgradeBtn.setBackgroundDrawable(drawable);
		// 设置升级按钮里面的文字
		int grade = SystemContext.getInstance().getExtUserVo().getGrade();
		//upgradeBtn.setTextColor(this.getResources().getColor(R.color.global_color7));
		if (grade >= 10) {
		//	upgradeBtn.setText("满级");
		} else {
		//	upgradeBtn.setText("升到LV" + (grade + 1));
		}
		if (grade == 1) {
//			user_or_group_point.setText("绑定手机号");
			user_or_group_need_point.setText("绑定手机号");
			needPointDesc.setVisibility(View.INVISIBLE);
			//setBundPhone();
		} else 
			if (grade == 10) {
			user_or_group_point.setText("满级");
			user_or_group_need_point.setText("0");
			needPointDesc.setVisibility(View.VISIBLE);
		} else {
			user_or_group_point.setText("0经验值/0经验值");
			user_or_group_need_point.setText("0");
			needPointDesc.setVisibility(View.VISIBLE);
		}
		user_or_group_grade.setText("LV" + SystemContext.getInstance().getExtUserVo().getGrade());
		gradeIcon.setText(SystemContext.getInstance().getExtUserVo().getGrade() + "");
		// 下面的这些代码是初始化dialog
		dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_grade_success);
		cueWords = (TextView) dialog.findViewById(R.id.cue_words);
		btn = (Button) dialog.findViewById(R.id.i_know_it);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		upgradeDialog = new Dialog(this, R.style.SampleTheme_Light);
		upgradeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = View.inflate(this, R.layout.dialog_grade_success, null);
		tv = (TextView) view.findViewById(R.id.cue_words);
		Button cancelBtn = (Button) view.findViewById(R.id.i_know_it);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				upgradeDialog.dismiss();
			}
		});
		upgradeDialog.setContentView(view);
	}

	/**
	 * 当请求数据失败了以后 返回来是执行onfailure方法时 
	 * 在这个方法里面处理这个错误码
	 * @param i
	 * @param msg
	 */
	private void handleErrorCode(Integer i, String msg) {
		ErrorCodeUtil.handleErrorCode(UserGradePolicyActivity.this, i, msg);
	}

	/**
	 * 在onstop方法里面 取消弹出的对话框
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if (dialog != null)
			dialog.dismiss();
		if (upgradeDialog != null)
			upgradeDialog.dismiss();
	}
}
