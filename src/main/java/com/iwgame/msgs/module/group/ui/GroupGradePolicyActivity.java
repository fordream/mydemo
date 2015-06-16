package com.iwgame.msgs.module.group.ui;

import java.util.List;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.group.adapter.GroupUserAdapter;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GroupGradeVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.youban.msgs.R;


/**
 * 公会等级界面
 * @ClassName: GroupGradePolicyActivity 
 * @Description: TODO(...) 
 * @author zhangjianchuan
 * @date 2014-10-9 下午4:09:43 
 * @Version 1.0
 *
 */
public class GroupGradePolicyActivity extends BaseActivity {

	private TextView user_or_group_grade; // 公会的等级
	private TextView user_or_group_need_point; // 公会的升级所需要的积分
	private TextView user_or_group_point;// 公会当前的积分
	private LinearLayout user_or_group_upgrade_detail;
	private TextView upgrade_btn;
	private Dialog dialog;// 升级成功后所弹出的对话框
	private Button btn; // 我知道了 这个按钮
	private List<GroupGradeVo> list = null;
	private long groupid;
	private TextView cueWords;
	private GroupVo vo = null;
	private Button commitBtn;
	private Drawable enable;
	private Drawable unenable;
	private TextView gradeIcon;
	private ImageView line;
	private ProgressBar gradeProgress;
	private int progress;
    private boolean flag = false;//弹出对话框
    private Dialog upgradeDialog;
    private CustomProgressDialog customProgressDialog;
    private GroupDao groupdao;

	/**
	 * 当界面启动的时候
	 * 执行下面的这个分支
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取用户的等级，与积分
		groupid = getIntent().getLongExtra(
				SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, 0);
		customProgressDialog = CustomProgressDialog.createDialog(this, false);
		initialize();
	}


	/**
	 * 做一些初始化操作,
	 * 在界面刚创建的时候
	 */
	private void initialize() {
		// 设置标题
		groupdao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
		setTitleTxt(getResources().getString(R.string.group_grade));
		View view = View.inflate(this, R.layout.group_integral_grade, null);
		user_or_group_grade = (TextView) view
				.findViewById(R.id.grade_txt);
		user_or_group_point = (TextView) view
				.findViewById(R.id.user_point);
		user_or_group_need_point = (TextView) view
				.findViewById(R.id.show_need_number);
		upgrade_btn = (TextView) view.findViewById(R.id.user_or_group_grade_btn);
		user_or_group_upgrade_detail = (LinearLayout) view
				.findViewById(R.id.user_or_group_upgrade_detail);
		gradeIcon = (TextView)view.findViewById(R.id.grade_icon);
		line = (ImageView)view.findViewById(R.id.line);
		gradeProgress = (ProgressBar)view.findViewById(R.id.grade_progressbar);
		user_or_group_upgrade_detail.setOrientation(LinearLayout.VERTICAL);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		getContentView().addView(view, params);
		setView();
		setListener();
		getGroupGradeInfo();// 获取公会等级的划分
	}


	/**
	 * 获取公会的积分
	 *  显示在手机的界面
	 */
	private void getgroupPoint() {

		ProxyFactory.getInstance().getGroupProxy()
		.getGroupPoint(new ProxyCallBack<List<GroupVo>>() {

			@Override
			public void onSuccess(List<GroupVo> result) {
				if (result != null && result.size() > 0) {
					GroupVo vo = result.get(0);
					updateLayout(vo);
				}else{
					updateLayout(null);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				handleErrorCode(result, resultMsg);
				updateLayout(null);
			}
		}, GroupGradePolicyActivity.this, groupid + "");
	}


	/**
	 * 获取公会 等级的划分信息
	 * 显示到手机界面上
	 */
	private void getGroupGradeInfo() {
		customProgressDialog.show();
		ProxyFactory.getInstance().getGroupProxy()
		.getGroupGradePolicy(new ProxyCallBack<List<GroupGradeVo>>() {
			@Override
			public void onSuccess(List<GroupGradeVo> result) {
				addDataToLinearLayout(result);
				getgroupPoint();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				handleErrorCode(result, resultMsg);
				customProgressDialog.dismiss();
			}
		}, GroupGradePolicyActivity.this);
	}


	/**
	 * 首先获取公会的资料 
	 * 将公会名称
	 *  公会等级
	 *   显示出来
	 */
	private void getGroupInfo(final long grid) {
		ServiceFactory.getInstance().getSyncEntityService()
		.syncEntity(grid, SyncEntityService.TYPE_GROUP,
				new SyncCallBack() {
			@Override
			public void onSuccess(Object res) {
                GroupVo result = groupdao.findGroupByGrid(grid);
				if (result != null) {
					if (SystemContext.getInstance().getExtUserVo().getUserid() != result.getPresidentId()) {
						upgrade_btn.setVisibility(View.INVISIBLE);
						flag = false;
					}else{
						upgrade_btn.setVisibility(View.VISIBLE);
						upgrade_btn.setText("升到LV"+(result.getGrade()+1));
						upgrade_btn.setBackgroundDrawable(unenable);
						flag = false;
						upgrade_btn.setTextColor(getResources().getColor(R.color.cur_point_desc));
					}
					vo = result;
					user_or_group_grade.setText("LV"+ result.getGrade());
					gradeIcon.setText(""+result.getGrade());
					getgroupPoint();
				} else {
					GroupDao groupdao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
					GroupVo gvo = groupdao.findGroupByGrid(grid);
					ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
					ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
					cdp.setId(grid);
					cdp.setUtime(gvo == null ? 0 : gvo.getUtime());
					p.addParam(cdp.build());
					ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(
							new ProxyCallBack<List<GroupVo>>() {
								@Override
								public void onSuccess(List<GroupVo> groupvoresult) {
									if(groupvoresult == null || groupvoresult.size() <= 0) return;
									GroupVo result = groupvoresult.get(0);
									if (result != null) {
										if (SystemContext.getInstance().getExtUserVo().getUserid() != result.getPresidentId()) {
											upgrade_btn.setVisibility(View.INVISIBLE);
											flag = false;
										}else{
											upgrade_btn.setVisibility(View.VISIBLE);
											upgrade_btn.setText("升到LV"+(result.getGrade()+1));
											upgrade_btn.setBackgroundDrawable(unenable);
											flag = false;
											upgrade_btn.setTextColor(getResources().getColor(R.color.cur_point_desc));
										}
										vo = result;
										user_or_group_grade.setText("LV"+ result.getGrade());
										gradeIcon.setText(result.getGrade()+"");
										getgroupPoint();
									}
								}

								@Override
								public void onFailure(Integer result,String resultMsg) {
									handleErrorCode(result,resultMsg);
									getgroupPoint();
								}
							},
							GroupGradePolicyActivity.this,p.build(),MsgsConstants.OT_GROUP,null);
				}
			}

			@Override
			public void onFailure(Integer result) {
				getgroupPoint();
			}
		});
	}


	/**
	 * 第一次起来的时候，
	 *  把布局中的某些控件的值
	 *  设置成为默认值
	 */
	private void setView() {
		//首先得到要显示的两图片
		enable = this.getResources().getDrawable(R.drawable.group_level_nor);
		unenable = this.getResources().getDrawable(R.drawable.group_level_dis);
		// 界面刚启动的时候显示一些默认的参数
		user_or_group_grade.setText("LV1");
		gradeIcon.setText("1");
		line.setVisibility(View.INVISIBLE);
		user_or_group_need_point.setText("0");
		user_or_group_point.setText("0积分/0积分");
		upgrade_btn.setVisibility(View.INVISIBLE);
		flag = false;
		// 初始化dialog
		dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_upgrade_group);
		btn = (Button) dialog.findViewById(R.id.cannelBtn);
		cueWords = (TextView) dialog.findViewById(R.id.upgrade_group_success);
		commitBtn = (Button)dialog.findViewById(R.id.commitBtn);
		//当点击确定的时候，邀请成员界面
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GroupGradePolicyActivity.this, GroupManageUserListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOOD, GroupUserAdapter.MODE_INVITE);
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, groupid);
				intent.putExtras(bundle);
				GroupGradePolicyActivity.this.startActivity(intent);
				dialog.dismiss();
			}
		});
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		upgradeDialog = new Dialog(this,R.style.SampleTheme_Light);
		upgradeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = View.inflate(this, R.layout.dialog_grade_success, null);
		TextView tv = (TextView)view.findViewById(R.id.cue_words);
		Button cancelBtn = (Button)view.findViewById(R.id.i_know_it);
		tv.setText("积分还未达到升级条件哦！");
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				upgradeDialog.dismiss();
			}
		});
		upgradeDialog.setContentView(view);
	}
	
	
	/**
	 * 在这方法里面重新去请求
	 * 数据刷新界面
	 */
	@Override
	protected void onResume() {
		super.onResume();
		getGroupInfo(groupid);
	}

	
	/**
	 * 给布局中的按钮设置监听器 当点击升级按钮的时候级公会升级
	 */
	private void setListener() {
		upgrade_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(flag == true){
					// 首先获取到公会的等级
					String groupGrade = user_or_group_grade.getText().toString()
							.trim();
					// 升级公会
					upgradeGroup(groupGrade);
				}else{
					upgradeDialog.show();
				}
			}
		});
	}


	/**
	 * 点击升级按钮 升级公会的等级
	 * 
	 * @param groupGrade
	 */
	protected void upgradeGroup(final String groupGrade) {
		// 升级公会的等级
		ProxyFactory.getInstance().getUserProxy()
		.userAction(
				new ProxyCallBack<Integer>() {
					@Override
					public void onSuccess(Integer result) {
						// 升级成功之后 更新界面的数据
						getGroupInfo(groupid);
						// 升级成功的时候 弹框
						if ("LV1".equals(groupGrade)) {
							cueWords.setText("恭喜你公会升级到LV2级,可容纳"+list.get(1).getMembers()+"人，马上去邀请成员！");
						} else if ("LV2".equals(groupGrade)) {
							cueWords.setText("恭喜你公会升级到LV3级,可容纳"+list.get(2).getMembers()+"人，马上去邀请成员！");
						} else if ("LV3".equals(groupGrade)) {
							cueWords.setText("恭喜你公会升级到LV4级,可容纳"+list.get(3).getMembers()+"人，马上去邀请成员！");
						} else if ("LV4".equals(groupGrade)) {
							cueWords.setText("恭喜你公会升级到LV5级,可容纳"+list.get(4).getMembers()+"人，马上去邀请成员！");
						} else if ("LV5".equals(groupGrade)) {
							cueWords.setText("恭喜你公会升级到LV6级,可容纳"+list.get(5).getMembers()+"人，马上去邀请成员！");
						}else if("LV6".equals(groupGrade)){
							cueWords.setText("恭喜你公会升级到LV7级,可容纳"+list.get(6).getMembers()+"人，马上去邀请成员！");
						}else if("LV7".equals(groupGrade)){
							cueWords.setText("恭喜你公会升级到LV8级,可容纳"+list.get(7).getMembers()+"人，马上去邀请成员！");
						}else if("LV8".equals(groupGrade)){
							cueWords.setText("恭喜你公会升级到LV9级,可容纳"+list.get(8).getMembers()+"人，马上去邀请成员！");
						}else if("LV9".equals(groupGrade)){
							cueWords.setText("恭喜你公会升级到LV10级,可容纳"+list.get(9).getMembers()+"人，马上去邀请成员！");
						}else if("LV10".equals(groupGrade)){
							//暂时10级不可能在往上面升级了
						}
						dialog.show();
					}

					@Override
					public void onFailure(Integer result,
							String resultMsg) {
						handleErrorCode(result, resultMsg);
					}
				}, GroupGradePolicyActivity.this, groupid,
				MsgsConstants.OT_GROUP, MsgsConstants.OP_GRADE_UP,
				null, null,null);
	}


	/**
	 * 更新一下界面的布局 
	 * 如果升级成功 或者第一次加载数据成功后
	 *  都需要更新布局
	 */
	protected void updateLayout(GroupVo groupVo) {
		try {
			int grade = Integer.parseInt(gradeIcon.getText().toString().trim());
			int point_number;
			if(groupVo == null){
				point_number = 0;
			}else{
				point_number = groupVo.getPoint();
			}
			if (list != null) {
				if(grade == 10 || grade >= list.size()){
					user_or_group_need_point.setText("0");
					flag = false;
					gradeProgress.setProgress(100);
					upgrade_btn.setTextColor(this.getResources().getColor(R.color.upgrade_btn_text_color_dis));
					upgrade_btn.setBackgroundDrawable(unenable);
					user_or_group_point.setText("满级");
					upgrade_btn.setText("满级");
					if(isPresident(vo)){
						upgrade_btn.setVisibility(View.VISIBLE);
						flag = false;
					}else{
						upgrade_btn.setVisibility(View.INVISIBLE);
					}
					return;
				}
				upgrade_btn.setText("升到LV"+(grade+1));
				if(point_number >= list.get(grade).getPoint()){
					user_or_group_need_point.setText(""+0);
					gradeProgress.setProgress(100);
					user_or_group_point.setText(list.get(grade).getPoint()+"积分/"+list.get(grade).getPoint()+"积分");
					if(isPresident(vo)){
						upgrade_btn.setVisibility(View.VISIBLE);
						flag = true;
						upgrade_btn.setTextColor(this.getResources().getColor(R.color.upgrade_btn_text_color_nor));
						upgrade_btn.setBackgroundDrawable(enable);
					}else{
						upgrade_btn.setVisibility(View.INVISIBLE);
					}
				}else{
					user_or_group_need_point.setText(""+(list.get(grade).getPoint()-point_number));
					progress = (point_number*100)/list.get(grade).getPoint();
					gradeProgress.setProgress(progress);
					user_or_group_point.setText(point_number+"积分/"+list.get(grade).getPoint()+"积分");
					if(isPresident(vo)){
						upgrade_btn.setVisibility(View.VISIBLE);
						flag = false;
						upgrade_btn.setTextColor(this.getResources().getColor(R.color.upgrade_btn_text_color_dis));
						upgrade_btn.setBackgroundDrawable(unenable);
					}else{
						upgrade_btn.setVisibility(View.INVISIBLE);
						flag = false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 判断当前自己是不是会长
	 *  如果是会长 
	 *  当公会积分足够的时候 要显示升级按钮
	 *   否则，一直不显示升级按钮
	 * 
	 * @param vo
	 * @return
	 */
	private boolean isPresident(GroupVo vo) {
		if (vo == null)
			return false;
		if (SystemContext.getInstance().getExtUserVo().getUserid() == vo
				.getPresidentId()) {
			return true;
		}
		return false;
	}


	/**
	 * 将返回来的VIP等级划分数据 添加到LinearLayout里面显示
	 */
	protected void addDataToLinearLayout(List<GroupGradeVo> result) {
		this.list = result;
		if(result == null || result.size() <= 0){
			return;
		}
		// 首先把最左边的一栏标题给加进去
		View view_title = View.inflate(this,
				R.layout.group_integral_detail_upgrade_title, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		user_or_group_upgrade_detail.addView(view_title, params);
		// 然后把获取到的数据一列一列的加进来
		int lenght = result.size();
		for (int i = 0; i < lenght; i++) {
			GroupGradeVo obj = result.get(i);
			View view = View.inflate(this,
					R.layout.group_integral_detail_upgrade_item_view, null);
			TextView group_vip_grade = (TextView) view
					.findViewById(R.id.user_vip_grade);
			TextView cosume_point = (TextView) view
					.findViewById(R.id.cosume_point);
			TextView accomdate_member = (TextView) view
					.findViewById(R.id.accomondate_member);
			group_vip_grade.setText("LV" + obj.getGrade());
			cosume_point.setText(((obj.getPoint() / 10000) >= 1) ? (obj
					.getPoint() / 10000 + "万") : (obj.getPoint() + ""));
			accomdate_member.setText(obj.getMembers() + "");
			LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, 1);
			user_or_group_upgrade_detail.addView(view, param);
		}
		line.setVisibility(View.VISIBLE);
		customProgressDialog.dismiss();
	}


	/**
	 * 当请求数据失败了以后 返回来是执行onfailure方法时 在这个方法里面处理这个错误码
	 * 
	 * @param i
	 * @param msg
	 */
	private void handleErrorCode(Integer i, String msg) {
		ErrorCodeUtil.handleErrorCode(GroupGradePolicyActivity.this, i, msg);
	}
	
	
	/**
	 * 在onstop方法里面把
	 * 弹框取消
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if(dialog != null)dialog.dismiss();
		if(upgradeDialog != null)upgradeDialog.dismiss();
	}
}
