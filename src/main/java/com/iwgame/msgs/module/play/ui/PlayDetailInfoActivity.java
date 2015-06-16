package com.iwgame.msgs.module.play.ui;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.SimpleFormatter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.play.util.PlayUtil;
import com.iwgame.msgs.module.user.ui.ReportActivity;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PlayEvalInfo;
import com.iwgame.msgs.proto.Msgs.PlayEvalLabelInfo;
import com.iwgame.msgs.proto.Msgs.PlayEvalList;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.proto.Msgs.PlayOrderInfo;
import com.iwgame.msgs.proto.Msgs.UserInfoDetail;
import com.iwgame.msgs.proto.Msgs.PlayEvalInfo.PlayEvalReply;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.MyTextView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.msgs.widget.popwindow.MsgsPopTextItem;
import com.iwgame.msgs.widget.popwindow.MsgsPopWindow;
import com.iwgame.msgs.widget.popwindow.MsgsPopWindow.OnClickPopItemListener;
import com.iwgame.msgs.widget.smiley.SmileyUtil;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

public class PlayDetailInfoActivity extends BaseActivity implements OnClickListener {

	// 陪玩id
	private long pid;
	private PlayInfo playInfo;
	private UserVo loginUserVo;
	/* 界面元素 */
	private ImageView play_user_icon;
	private TextView play_info_nickname;
	private TextView play_info_user_age;
	private ImageView play_info_gender_icon;
	private TextView play_info_position_values;
	private TextView play_info_time;
	private TextView play_info_sale;
	private TextView play_info_joinnum;
	private TextView play_info_remark;
	private LinearLayout play_info_content;
	private RelativeLayout play_manage;
	private RelativeLayout play_apply;
   
	private int status;
	private LinearLayout play_certify;
	private long orderid;
	private TextView play_pay;
	private TextView play_ss;

	private final int CREATE_ENROLL = 600;
	private View parentView;
	private Button rightBtn;
	private PlayOrderInfo playOrder;
	private LinearLayout play_cancleout;
	private TextView play_cancle;
	private RelativeLayout play_game_over;
	private TextView play_over;
	private RelativeLayout play_remark;
	private ImageView play_image;
	private TextView play_apay;
	private ImageView play_enroll_info;
	private ExtUserVo userVo;
	private int mode;
	private LinearLayout play_skip_manage;
	private TextView play_pay2;
	private TextView user_remark;
	private ImageView user_remark_star1;
	private ImageView user_remark_star2;
	private ImageView user_remark_star3;
	private ImageView user_remark_star4;
	private ImageView user_remark_star5;
	private LinearLayout user_remark_count;
	private TextView user_remark_total;
	private TextView user_remark_percent;
	private ImageView user_remark_line;
	private LinearLayout user_remark_type;
	private TextView user_remark_good;
	private TextView user_remark_often;
	private TextView user_remark_bad;
	private LinearLayout user_remark_content;

	private LinearLayout playEnroll;
	private boolean compelte;
	private boolean comp;
	private Dialog dialog;
	private RelativeLayout play_appeal;
	private CustomProgressDialog downloaddialog;
	public static final int MODE_0 = 0;
	public static final int MODE_1 = 1;
	private List<PlayEvalLabelInfo> labelList =null;
	private List<PlayEvalInfo>evallist=null;
	private PlayEvalInfo playEval;
	private TextView remark_time;
	private LinearLayout replyView;
	private RelativeLayout play_more_remark;
	private LinearLayout play_remark_star_content;
	
	private String edittime ;//编辑陪玩的时间
	private String todayTime;//当前时间
	private LinearLayout top_layout;
	private Button backButton;
	private ScrollView play_info_scrollview;
	private float y;
	private boolean isHide=true;
	private boolean isAnimation=false;
	private ImageView more_comment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			pid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PID);
			mode = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE);
		}
		loginUserVo = SystemContext.getInstance().getExtUserVo();
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		play_info_content.removeAllViews();
		play_apply.setClickable(true);
		getPlayInfo(this,pid);
	}

	/**
	 * 初始化
	 */
	private void init() {
//		setTitleTxt("陪玩详情");
		
//		rightBtn.setBackgroundResource(R.drawable.common_menu_more_nor);
		findViewById(R.id.top).setVisibility(View.GONE);
		parentView = View.inflate(PlayDetailInfoActivity.this, R.layout.user_play_more_info, null);
		addContentViewChild(parentView);
		play_info_scrollview=(ScrollView) findViewById(R.id.play_info_scrollview);
		top_layout=(LinearLayout) findViewById(R.id.play_top_layout);
		rightBtn= (Button) findViewById(R.id.play_rightbtn1);
		backButton=(Button) findViewById(R.id.play_leftbtn1);
		play_image = (ImageView) findViewById(R.id.play_image);
		play_user_icon = (ImageView) findViewById(R.id.play_user_icon);
		play_info_nickname = (TextView) findViewById(R.id.play_info_nickname);
		play_info_user_age = (TextView) findViewById(R.id.play_info_user_age);
		play_info_gender_icon = (ImageView) findViewById(R.id.play_info_gender_icon);
		play_info_position_values = (TextView) findViewById(R.id.play_info_position_values);
		play_info_time = (TextView) findViewById(R.id.play_info_time);
		play_info_sale = (TextView) findViewById(R.id.play_info_sale);
		play_info_joinnum = (TextView) findViewById(R.id.play_info_joinnum);
		play_info_remark = (TextView) findViewById(R.id.play_info_remark);
		play_info_content = (LinearLayout) findViewById(R.id.play_info_content);
		play_manage = (RelativeLayout) findViewById(R.id.play_manage);
		play_manage.setOnClickListener(this);
		play_apply = (RelativeLayout) findViewById(R.id.play_apply);
		play_certify=(LinearLayout)findViewById(R.id.play_pay);
		play_cancleout =(LinearLayout)findViewById(R.id.play_cancle);
		play_pay=(TextView)findViewById(R.id.play_enroll_paid);
		play_ss=(TextView)findViewById(R.id.play_enroll_ss);
		play_apay=(TextView)findViewById(R.id.play_enroll_pay);
		play_cancle =(TextView)findViewById(R.id.play_enroll_cancle);
		play_game_over=(RelativeLayout)findViewById(R.id.play_game_over);
		play_over =(TextView)findViewById(R.id.play_enroll_over);
		play_remark=(RelativeLayout)findViewById(R.id.play_remark_btn);
		play_enroll_info=(ImageView)findViewById(R.id.play_info_skip_enroll);
		play_skip_manage=(LinearLayout)findViewById(R.id.skip_playmanage);
		play_more_remark =(RelativeLayout)findViewById(R.id.check_more_remark);
		play_remark_star_content=(LinearLayout)findViewById(R.id.play_remark_star_content);
		more_comment =(ImageView)findViewById(R.id.skip_more_comment);
		
		
		user_remark=(TextView)findViewById(R.id.play_remark_text);
		user_remark_star1=(ImageView)findViewById(R.id.user_remark_star1);
		user_remark_star2=(ImageView)findViewById(R.id.user_remark_star2);
		user_remark_star3=(ImageView)findViewById(R.id.user_remark_star3);
		user_remark_star4=(ImageView)findViewById(R.id.user_remark_star4);
		user_remark_star5=(ImageView)findViewById(R.id.user_remark_star5);
		user_remark_count=(LinearLayout)findViewById(R.id.user_remark_count);
		user_remark_total=(TextView)findViewById(R.id.remark_user_count);
		user_remark_percent=(TextView)findViewById(R.id.remark_percent_num);
		user_remark_line =(ImageView)findViewById(R.id.remark_line);
		user_remark_type=(LinearLayout)findViewById(R.id.remark_type);
		user_remark_good=(TextView)findViewById(R.id.remark_good);
		user_remark_often=(TextView)findViewById(R.id.remark_often);
		user_remark_bad =(TextView)findViewById(R.id.remark_bad);
		user_remark_content=(LinearLayout)findViewById(R.id.play_remark_content);
		
		
		
		
		
		
		play_ss = (TextView) findViewById(R.id.play_enroll_ss);
		play_appeal =(RelativeLayout)findViewById(R.id.play_appeal_btn);
		play_pay2 = (TextView) findViewById(R.id.play_enroll_pay);
		playEnroll = (LinearLayout) findViewById(R.id.play_info_enroll);
		backButton.setOnClickListener(this);
		play_apply.setOnClickListener(this);
		play_pay.setOnClickListener(this);
		play_ss.setOnClickListener(this);
		rightBtn.setOnClickListener(this);
		play_cancle.setOnClickListener(this);
		play_image.setOnClickListener(this);
		play_remark.setOnClickListener(this);
		play_pay2.setOnClickListener(this);
		play_more_remark.setOnClickListener(this);
		playEnroll.setOnClickListener(this);
		play_appeal.setOnClickListener(this);
		top_layout.setVisibility(View.GONE);
		play_info_scrollview.setOnTouchListener(scrollviewOnOnTouchListener);
	     RelativeLayout.LayoutParams para;
	        para = (RelativeLayout.LayoutParams) play_image.getLayoutParams();
	        para.width=getWindowManager().getDefaultDisplay().getWidth();
	        para.height=getWindowManager().getDefaultDisplay().getWidth()*7/8;
	    play_image.setLayoutParams(para);   
	    Log.i("play_image", "width="+para.width+"height="+para.height);
		// 获取陪玩详情
		parentView.setVisibility(View.GONE);
		downloaddialog = CustomProgressDialog.createDialog(this, false);
		downloaddialog.show();
	}
 /**
  * 滚动监听，是否透明Top
  * 
  * */
	OnTouchListener scrollviewOnOnTouchListener=new OnTouchListener(){

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
          
            case MotionEvent.ACTION_MOVE:
            	if (Math.abs(event.getY() - y) > DisplayUtil.dip2px(PlayDetailInfoActivity.this,10)) {
            		if(v.getScrollY()==0){
            			return false;
            		}
    				float temp = y;
    				y = event.getY();
    				if (y < temp) {// 上拉
    					if(isHide)
    					showTop(true,500);
    					LogUtil.d("PlayDetailInfoActivity", "上拉");
    				} else if (y > temp) {// 下拉
    					if(!isHide)
    					showTop(false,500);
    					LogUtil.d("PlayDetailInfoActivity", "下拉");
    					
    				}
    			}
                break;
            case MotionEvent.ACTION_DOWN:
    			y = event.getY();
    			break;
            case MotionEvent.ACTION_UP:
    			y = event.getY();
    			break;
            default:
                break;
            }
			
			return false;
		}};
/**
 * Top透明与否
 * 
 * */
	private void showTop(final boolean isShow,long duration){
	  
		Animation alphaAnimation = null;
		if(isShow){
				alphaAnimation=	new AlphaAnimation(0,1);
				isHide=false;
		}else{
				alphaAnimation=	new AlphaAnimation(1,0);
				isHide=true;
		}
	
		
		if(alphaAnimation!=null){
			alphaAnimation.setAnimationListener(new AnimationListener(){

				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					if(!isShow){
						top_layout.setVisibility(View.GONE);
						rightBtn.setBackgroundResource(R.drawable.common_peiwan_more);
						backButton.setBackgroundResource(R.drawable.common_peiwan_back);
					}
					
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					if(isShow){
						top_layout.setVisibility(View.VISIBLE);
						rightBtn.setBackgroundResource(R.drawable.common_menu_more_nor);
						backButton.setBackgroundResource(R.drawable.back_button_selector);
					}
				}
				
				
			} );
			// 设置动画时间
			alphaAnimation.setDuration(duration);
			//保持动画结束时的帧
			alphaAnimation.setFillAfter(true);
			alphaAnimation.setFillBefore(false);
			top_layout.startAnimation(alphaAnimation);
		}
	
		
		
	}
	private void getPlayInfo(Context context, long pid) {
		ProxyFactory.getInstance().getPlayProxy().searchPlayDetailInfo(new ProxyCallBack<Msgs.PlayInfo>() {

			@Override
			public void onSuccess(PlayInfo result) {
				renderView(result);
				
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub

			}
		}, this, pid, "1,2,3,4,5");
	}

	/**
	 * 渲染界面
	 * 
	 * @param result
	 */
	private void renderView(PlayInfo result) {
		userVo = SystemContext.getInstance().getExtUserVo();
		if (result != null) {
			playInfo = result;
			status = playInfo.getStatus();
			playOrder = playInfo.getOrderInfo();
			orderid = playOrder.getId();
			ImageViewUtil.showBigImage(play_image, playInfo.getResourceid(), R.drawable.common_user_icon_default);
			final UserInfoDetail userInfo = result.getUserInfo();
			ImageViewUtil.showImage(play_user_icon, userInfo.getAvatar(), R.drawable.common_user_icon_default);
			play_user_icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (userInfo.getId() != loginUserVo.getUserid()) {
						// 打开对方资料
						Intent intent = new Intent(PlayDetailInfoActivity.this, UserDetailInfoActivity.class);
						Bundle bundle = new Bundle();
						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, playInfo.getUid()
								);
						intent.putExtras(bundle);
						PlayDetailInfoActivity.this.startActivity(intent);
					} else {
						ToastUtil.showToast(PlayDetailInfoActivity.this, "查看自己的信息,请进入我的资料页");
					}
				}
			});
			play_info_nickname.setText(userInfo.getNickname());
			int age = AgeUtil.convertAgeByBirth(userInfo.getAge());
			play_info_user_age.setText((age == AgeUtil.NOT_SET_AGE ? "" : age) + "");
			int sex = userInfo.getSex();
			if (sex == 0) {
				play_info_gender_icon.setBackgroundResource(R.drawable.user_man_icon);
			} else if (sex == 1) {
				play_info_gender_icon.setBackgroundResource(R.drawable.user_woman_icon);
			} else {
				play_info_gender_icon.setBackgroundResource(R.drawable.user_man_icon);
			}
			play_info_position_values.setText(DistanceUtil.covertDistance(userInfo.getDistance()));
			play_info_time.setText(SafeUtils.getDate2MyStr2(userInfo.getLastLogin()));
			play_info_sale.setText(result.getCost() + "U币/小时");
			play_info_joinnum.setText(result.getJoinnum() + "人已报名");
			play_info_remark.setText(result.getRemark());
			rightBtn.setVisibility(View.VISIBLE);
			if (userVo.getUserid() == playInfo.getUid()) {
				play_enroll_info.setVisibility(View.VISIBLE);
				play_skip_manage.setOnClickListener(this);
			} else {
				play_enroll_info.setVisibility(View.GONE);
			}
			
			if(playInfo.getJoinnum() != 0){
				playEnroll.setClickable(true);
			}else{
				playEnroll.setClickable(false);
			}
			if(playInfo.getEvalnum()==0){
				play_remark_star_content.setVisibility(View.GONE);
				user_remark_count.setVisibility(View.GONE);
				user_remark_type.setVisibility(View.GONE);
				user_remark_content.setVisibility(View.GONE);
				user_remark_line.setVisibility(View.GONE);
				play_more_remark.setClickable(false);
				more_comment.setVisibility(View.GONE);
			}else{
				play_remark_star_content.setVisibility(View.VISIBLE);
				user_remark_count.setVisibility(View.VISIBLE);
				user_remark_type.setVisibility(View.VISIBLE);
				user_remark_content.setVisibility(View.VISIBLE);
				user_remark_line.setVisibility(View.VISIBLE);
				play_more_remark.setClickable(true);
				more_comment.setVisibility(View.VISIBLE);
			}
			
			// 其他详情设置
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			PlayUtil.addBasePlayAttrView(this, inflater, play_info_content, result, true);
			playEnroll.setClickable(false);
			if (loginUserVo != null && playInfo.getUid() == loginUserVo.getUserid()) {
				play_manage.setVisibility(View.VISIBLE);
				play_apply.setVisibility(View.GONE);
				play_cancleout.setVisibility(View.GONE);
				play_certify.setVisibility(View.GONE);
				play_game_over.setVisibility(View.GONE);
				rightBtn.setVisibility(View.GONE);
				play_appeal.setVisibility(View.GONE);
				play_remark.setVisibility(View.GONE);
				playEnroll.setClickable(true);
			} else if (playOrder.getId() <= 0) {
				play_manage.setVisibility(View.GONE);
				play_apply.setVisibility(View.VISIBLE);
			} else if (playOrder != null && playOrder.getStatus() == 0) {
				play_pay.setClickable(true);
				play_manage.setVisibility(View.GONE);
				play_apply.setVisibility(View.GONE);
				play_cancleout.setVisibility(View.VISIBLE);
				play_certify.setVisibility(View.GONE);
				play_game_over.setVisibility(View.GONE);
				play_remark.setVisibility(View.GONE);
				play_appeal.setVisibility(View.GONE);
			} else if (playOrder != null && playOrder.getStatus() == 1) {
				play_manage.setVisibility(View.GONE);
				play_apply.setVisibility(View.GONE);
				play_cancleout.setVisibility(View.GONE);
				play_certify.setVisibility(View.VISIBLE);
				play_game_over.setVisibility(View.GONE);
				play_remark.setVisibility(View.GONE);
				play_appeal.setVisibility(View.GONE);
			} else if (playOrder != null && playOrder.getStatus() == 2) {
				play_manage.setVisibility(View.GONE);
				play_apply.setVisibility(View.GONE);
				play_cancleout.setVisibility(View.GONE);
				play_certify.setVisibility(View.GONE);
				play_game_over.setVisibility(View.GONE);
				play_appeal.setVisibility(View.VISIBLE);
				play_remark.setVisibility(View.GONE);
			} else if (playOrder != null && playOrder.getStatus() == 3) {
				play_remark.setVisibility(View.VISIBLE);
				play_manage.setVisibility(View.GONE);
				play_apply.setVisibility(View.GONE);
				play_cancleout.setVisibility(View.GONE);
				play_certify.setVisibility(View.GONE);
				play_game_over.setVisibility(View.GONE);
				play_appeal.setVisibility(View.GONE);
			} else if (playOrder != null && playOrder.getStatus() == 4) {
				if (mode != 1) {
					play_manage.setVisibility(View.GONE);
					play_apply.setVisibility(View.GONE);
					play_cancleout.setVisibility(View.GONE);
					play_certify.setVisibility(View.GONE);
					play_game_over.setVisibility(View.VISIBLE);
					play_over.setText("已取消");
					play_remark.setVisibility(View.GONE);
					play_appeal.setVisibility(View.GONE);
				} else {
					play_manage.setVisibility(View.GONE);
					play_apply.setVisibility(View.VISIBLE);
					play_cancleout.setVisibility(View.GONE);
					play_certify.setVisibility(View.GONE);
					play_game_over.setVisibility(View.GONE);
					play_remark.setVisibility(View.GONE);
				}
			} else if (playOrder != null && playOrder.getStatus() == 5) {
				if (mode != MODE_1) {
					play_manage.setVisibility(View.GONE);
					play_apply.setVisibility(View.GONE);
					play_cancleout.setVisibility(View.GONE);
					play_certify.setVisibility(View.GONE);
					play_game_over.setVisibility(View.VISIBLE);
					play_over.setText("游戏结束");
				} else {
					play_manage.setVisibility(View.GONE);
					play_apply.setVisibility(View.VISIBLE);
					play_cancleout.setVisibility(View.GONE);
					play_certify.setVisibility(View.GONE);
					play_game_over.setVisibility(View.GONE);
				}
			} else if (playOrder != null && playOrder.getStatus() == 6) {
				if (mode != MODE_1) {
					play_manage.setVisibility(View.GONE);
					play_apply.setVisibility(View.GONE);
					play_cancleout.setVisibility(View.GONE);
					play_certify.setVisibility(View.GONE);
					play_game_over.setVisibility(View.VISIBLE);
					play_over.setText("已过期");
				} else {
					play_manage.setVisibility(View.GONE);
					play_apply.setVisibility(View.VISIBLE);
					play_cancleout.setVisibility(View.GONE);
					play_certify.setVisibility(View.GONE);
					play_game_over.setVisibility(View.GONE);
				}
			}

		}
		getCommentInfo();//获取评价数据
		if (UserPlayEditActivity.compelte) {
			editTip("你已经编辑成功，等待系统审核通过。");
			UserPlayEditActivity.compelte = false;
		}

	}
	private void setStar(int count){
		switch(count){
		case 0:
			user_remark_star1.setBackgroundResource(R.drawable.peiwan_pingjia2);
			user_remark_star2.setBackgroundResource(R.drawable.peiwan_pingjia2);
			user_remark_star3.setBackgroundResource(R.drawable.peiwan_pingjia2);
			user_remark_star4.setBackgroundResource(R.drawable.peiwan_pingjia2);
			user_remark_star5.setBackgroundResource(R.drawable.peiwan_pingjia2);
			break;
		case 1:
			user_remark_star1.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star2.setBackgroundResource(R.drawable.peiwan_pingjia2);
			user_remark_star3.setBackgroundResource(R.drawable.peiwan_pingjia2);
			user_remark_star4.setBackgroundResource(R.drawable.peiwan_pingjia2);
			user_remark_star5.setBackgroundResource(R.drawable.peiwan_pingjia2);
			break;
		case 2:
			user_remark_star1.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star2.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star3.setBackgroundResource(R.drawable.peiwan_pingjia2);
			user_remark_star4.setBackgroundResource(R.drawable.peiwan_pingjia2);
			user_remark_star5.setBackgroundResource(R.drawable.peiwan_pingjia2);
			break;
		case 3:
			user_remark_star1.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star2.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star3.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star4.setBackgroundResource(R.drawable.peiwan_pingjia2);
			user_remark_star5.setBackgroundResource(R.drawable.peiwan_pingjia2);
			break;
		case  4:
			user_remark_star1.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star2.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star3.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star4.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star5.setBackgroundResource(R.drawable.peiwan_pingjia2);
			break;
		case 5:
			user_remark_star1.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star2.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star3.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star4.setBackgroundResource(R.drawable.peiwan_pingjia);
			user_remark_star5.setBackgroundResource(R.drawable.peiwan_pingjia);
			break;
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if(v==backButton){
			finish();
		}
		if (v == play_manage) {// 点击陪玩管理菜单
			onClickPlayManageMenu();
		} else if (v == play_apply) {
			play_apply.setClickable(false);
			Intent intent = new Intent(PlayDetailInfoActivity.this, CreateEnrollInfoActivity.class);
			intent.putExtra("pid", pid);
			startActivityForResult(intent, CREATE_ENROLL);
//			Intent intent = new Intent(PlayDetailInfoActivity.this, PlayAppealActivity.class);
//			intent.putExtra("oid", orderid);
//			startActivity(intent);
		} else if (v == play_pay) {
			play_pay.setClickable(false);
			isNeedPay();
		} else if (v == rightBtn) {
			onClickRightMenu(v);
		} else if (v == play_cancle) {
			play_cancle.setClickable(false);
			cancleEnroll(this, orderid);
		} else if (v == play_image) {
			lookImage(playInfo.getResourceid());
		} else if (v == play_ss) {
			Intent intent = new Intent(PlayDetailInfoActivity.this, PlayAppealActivity.class);
			intent.putExtra("oid", orderid);
			intent.putExtra("type", 1);
			startActivity(intent);
		} else if (v == play_skip_manage) {
			Intent intent = new Intent(PlayDetailInfoActivity.this, PlayManageActivity.class);
			intent.putExtra("pid", pid);
			startActivity(intent);
		} else if (v == play_pay2) {
			play_pay2.setClickable(false);
			isNeedPay();
		}else if(v==play_more_remark){
			Intent intent = new Intent(PlayDetailInfoActivity.this, PlayAllCommentInfoActivity.class);
			intent.putExtra("pid",pid );
			intent.putExtra("uid", playInfo.getUserInfo().getId());
			startActivity(intent);
		}else if(v==play_remark){
			Intent intent = new Intent(PlayDetailInfoActivity.this,PlayEvaluationActivity.class);
			intent.putExtra("orderid",orderid);
			intent.putExtra("playid", pid);
			Log.i("PlayDetailActivity", "oderid="+orderid+":::"+"playid="+pid);
			startActivity(intent);
		} else if (v == playEnroll) {
			if(playInfo.getJoinnum()!=0){
			Intent intent = new Intent(PlayDetailInfoActivity.this, PlayManageActivity.class);
			intent.putExtra("pid", pid);
			startActivity(intent);}
		}else if(v==play_appeal){
			Intent intent = new Intent(PlayDetailInfoActivity.this, PlayAppealActivity.class);
			intent.putExtra("oid", orderid);
			intent.putExtra("type", 2);
			startActivity(intent);
		}
	}
	/**
	 * 
	 */
	public void onClickPlayManageMenu() {
		dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_playdetail_managemenu);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		edittime =new SimpleDateFormat("yyyy-MM-dd").format(new Date(playInfo.getEdittime()));
		todayTime=new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
		window.setWindowAnimations(R.anim.set_add_play_image);
		final TextView applyManage = (TextView) dialog.findViewById(R.id.applyManage);
		final TextView editManage = (TextView) dialog.findViewById(R.id.editManage);
		final TextView openOrCloseManage = (TextView) dialog.findViewById(R.id.openOrCloseManage);
		applyManage.setClickable(true);
		editManage.setClickable(true);
		openOrCloseManage.setClickable(true);
		if((playInfo.getUserPlayStatus() == 1)){
			openOrCloseManage.setText("关闭陪玩");
		}else{
			if (status == MsgsConstants.PLAY_STATUS_DOWN) {// 已关闭
				openOrCloseManage.setText("开启陪玩");
			} else {// 开启中
				openOrCloseManage.setText("关闭陪玩");
			}
		}
		
		dialog.show();
		applyManage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog.isShowing())
					dialog.dismiss();
				editManage.setClickable(false);
				openOrCloseManage.setClickable(false);
				Intent intent = new Intent(PlayDetailInfoActivity.this, PlayManageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PID, pid);
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PSTATUS, playInfo.getStatus());
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_USTATUS, playInfo.getUserPlayStatus());
				bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME, playInfo.getGamename());
				intent.putExtras(bundle);
				PlayDetailInfoActivity.this.startActivity(intent);
			}
		});
		editManage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog.isShowing())
					dialog.dismiss();
				applyManage.setClickable(false);
				openOrCloseManage.setClickable(false);
				if (playInfo.getUserPlayStatus() == 1 || status == MsgsConstants.PLAY_STATUS_CLOSED) {
					if (playInfo.getUserPlayStatus() == 1) {
						ToastUtil.showToast(PlayDetailInfoActivity.this, "由于你违规操作，你创建的陪玩功能已被封停，请联系管理员");
					} else {
						ToastUtil.showToast(PlayDetailInfoActivity.this, "该陪玩存在违规，已被关闭，请联系管理员");
					}
					applyManage.setClickable(true);
					openOrCloseManage.setClickable(true);
				} else {
					if (status == MsgsConstants.PLAY_STATUS_PUBLISH || status == MsgsConstants.PLAY_STATUS_APPROVAL_NO) {
						if (dialog.isShowing())
							dialog.dismiss();
						if(status==MsgsConstants.PLAY_STATUS_PUBLISH){
						if(edittime.equals(todayTime)){
							ToastUtil.showToast(PlayDetailInfoActivity.this, "你今天已经编辑过该陪玩，请明天再来吧!");
						}else{
						isNeedEditDialog(applyManage, openOrCloseManage);
							}
						}else{
							isNeedEditDialog(applyManage, openOrCloseManage);
						}
					} else {
						if(status==MsgsConstants.PLAY_STATUS_DOWN){
							ToastUtil.showToast(PlayDetailInfoActivity.this, "该陪玩已被关闭，无法编辑，你可以开启陪玩后，再进行编辑!");
						}else
						if(status==MsgsConstants.PLAY_STATUS_INIT){
						editTip("该陪玩正在审核中，暂时无法编辑！");
						}
					}
					applyManage.setClickable(true);
					openOrCloseManage.setClickable(true);
				}
			}
		});

		openOrCloseManage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog.isShowing())
					dialog.dismiss();
				play_manage.setClickable(false);
				if (playInfo.getUserPlayStatus() == 1) {
					ToastUtil.showToast(PlayDetailInfoActivity.this, "由于你违规操作，你创建的陪玩功能已被封停，请联系管理员");
					play_manage.setClickable(true);
				} else {
						if (status == MsgsConstants.PLAY_STATUS_DOWN) {// 已关闭
							changePlayStatus(1, openOrCloseManage);
						} else if (status == MsgsConstants.PLAY_STATUS_PUBLISH) {
							closePlayTip(openOrCloseManage);
						} else if (status == MsgsConstants.PLAY_STATUS_APPROVAL_NO) {
							ToastUtil.showToast(PlayDetailInfoActivity.this, "该陪玩审核未通过，暂时无法编辑，请重新编辑");
						} else if (status == MsgsConstants.PLAY_STATUS_CLOSED) {
							ToastUtil.showToast(PlayDetailInfoActivity.this, "该陪玩存在违规，已被关闭，请联系管理员");
						} else if (status == MsgsConstants.PLAY_STATUS_INIT) {
							editTip("该陪玩正在审核中，暂时无法关闭！");
						} else {
							ToastUtil.showToast(PlayDetailInfoActivity.this, "当前状态不能关闭");
						}
						play_manage.setClickable(true);
				}
				applyManage.setClickable(true);
				editManage.setClickable(true);
			}
		});
		dialog.findViewById(R.id.cancle).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog.isShowing())
					dialog.dismiss();
			}
		});
	}

	/**
	 * 编辑提示框
	 */
	public void isNeedEditDialog(final TextView text1, final TextView text2) {
		final Dialog dialog = new Dialog(PlayDetailInfoActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog消失
		TextView content = (TextView) dialog.findViewById(R.id.content_dialog);
		content.setText("重新编辑后，系统会在24小时内审核该陪玩信息，审核阶段不再显示陪玩列表中，待审核通过可正常显示，确定要编辑吗？");
		LinearLayout btnBttom = (LinearLayout) dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom = (LinearLayout) dialog.findViewById(R.id.text_bottom);
		Button cancle = (Button) dialog.findViewById(R.id.role_cannelBtn);
		Button commit = (Button) dialog.findViewById(R.id.role_commitBtn);
		cancle.setTextColor(getResources().getColor(R.color.dialog_cancel_btn_text_color));
		commit.setTextColor(getResources().getColor(R.color.dialog_commit_btn_text_color));
		btnBttom.setVisibility(View.VISIBLE);
		textBttom.setVisibility(View.GONE);
		dialog.show();
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (dialog.isShowing()) {
					dialog.dismiss();
					text1.setClickable(true);
					text2.setClickable(true);
				}
			}
		});
		commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PlayDetailInfoActivity.this, UserPlayEditActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PID, pid);
				bundle.putString("gname", playInfo.getGamename());
				intent.putExtras(bundle);
				startActivity(intent);
				dialog.dismiss();
			}
		});
	}

	/**
	 * 
	 * @param statusType
	 */
	private void changePlayStatus(final int type, final TextView text) {
		downloaddialog.show();
		ProxyFactory.getInstance().getPlayProxy().changePlayStatus(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				if(dialog.isShowing()){
					dialog.dismiss();
				}
				if (result == Msgs.ErrorCode.EC_OK_VALUE) {
					if (type == 2) {// 关闭成功
						text.setText("开启陪玩");
						ToastUtil.showToast(PlayDetailInfoActivity.this, "关闭陪玩成功");
						status = MsgsConstants.PLAY_STATUS_DOWN;
					} else if (type == 1) {
						text.setText("关闭陪玩");
						ToastUtil.showToast(PlayDetailInfoActivity.this, "开启陪玩成功");
						status = MsgsConstants.PLAY_STATUS_PUBLISH;
					} else {
						ToastUtil.showToast(PlayDetailInfoActivity.this, "操作成功");
					}
				} else {
					statusTip(type);
				}
				if(downloaddialog.isShowing()){
					downloaddialog.dismiss();
				}
				play_manage.setClickable(true);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				statusTip(type);
				if(downloaddialog.isShowing()){
					downloaddialog.dismiss();
				}
				play_manage.setClickable(true);
			}
		}, this, pid, type);
	}

	/**
	 * 是否需要付款提示框
	 */
	private void isNeedPay() {
		final Dialog dialog = new Dialog(PlayDetailInfoActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog消失
		TextView content = (TextView) dialog.findViewById(R.id.content_dialog);
		content.setText("确认对方已经陪你玩过游戏了吗？确认后,对方会收到你的U币报酬。");
		LinearLayout btnBttom = (LinearLayout) dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom = (LinearLayout) dialog.findViewById(R.id.text_bottom);
		Button cancle = (Button) dialog.findViewById(R.id.role_cannelBtn);
		Button commit = (Button) dialog.findViewById(R.id.role_commitBtn);
		cancle.setTextColor(getResources().getColor(R.color.dialog_cancel_btn_text_color));
		commit.setTextColor(getResources().getColor(R.color.dialog_commit_btn_text_color));
		btnBttom.setVisibility(View.VISIBLE);
		textBttom.setVisibility(View.GONE);
		dialog.show();
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
					play_pay.setClickable(true);
					play_pay2.setClickable(true);
					dialog.dismiss();

				
			}
		});
		commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				payEnrollOrder(PlayDetailInfoActivity.this, orderid);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case CREATE_ENROLL:
				orderid = data.getLongExtra("oriderid", 0);
				comp = data.getBooleanExtra("compelte", false);
				if (orderid != 0) {
					play_apply.setVisibility(View.GONE);
				}
//				if(comp){
//					editTip("你已经编辑成功，等待系统审核通过。");
//				}
				break;
			}
		}
	}

	/**
	 * 确认付款
	 * 
	 * @param context
	 * @param order
	 */
	public void payEnrollOrder(Context context, long order) {
		ProxyFactory.getInstance().getPlayProxy().payOrder(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				// TODO Auto-generated method stub
				if (result == 0) {
					play_cancleout.setVisibility(View.GONE);
					play_remark.setVisibility(View.VISIBLE);
					ToastUtil.showToast(PlayDetailInfoActivity.this, "支付完成");

				}
				play_cancle.setClickable(true);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				play_cancle.setClickable(true);
				play_pay.setClickable(true);
				play_pay2.setClickable(true);
				ToastUtil.showToast(PlayDetailInfoActivity.this, "支付失败");

			}
		}, context, order);
	}

	/**
	 * 取消报名
	 * 
	 * @param context
	 * @param oid
	 */
	public void cancleEnroll(Context context, long oid) {
		ProxyFactory.getInstance().getPlayProxy().cancelOrder(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				// TODO Auto-generated method stub
				if (result == 0) {
					ToastUtil.showToast(PlayDetailInfoActivity.this, "取消报名成功");
					play_cancleout.setVisibility(View.GONE);
					if(mode==MODE_0){
						play_cancleout.setVisibility(View.VISIBLE);
					}else{
					play_apply.setVisibility(View.VISIBLE);
					}
					play_cancle.setClickable(true);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				ToastUtil.showToast(PlayDetailInfoActivity.this, "取消报名失败");
				play_cancle.setClickable(true);
			}
		}, context, oid);
	}

	public void lookImage(String resid) {
		Intent intent = new Intent(PlayDetailInfoActivity.this, ImageBrowerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, new String[] { ResUtil.getOriginalRelUrl(resid) });
		ExtUserVo uvo = SystemContext.getInstance().getExtUserVo();
		bundle.putBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES_ISSHOWREPORTMENU, false);
		bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, 0);
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_USER);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		PlayDetailInfoActivity.this.startActivity(intent);
	}

	public void statusTip(int type) {
		if (type == 2) {// 关闭成功
			ToastUtil.showToast(PlayDetailInfoActivity.this, "关闭陪玩失败");
			status = MsgsConstants.PLAY_STATUS_DOWN;
		} else if (type == 1) {
			ToastUtil.showToast(PlayDetailInfoActivity.this, "开启陪玩失败");
			status = MsgsConstants.PLAY_STATUS_PUBLISH;
		} else {
			ToastUtil.showToast(PlayDetailInfoActivity.this, "操作失败");
		}
	}

	private void onClickRightMenu(View v) {
		final MsgsPopTextItem item0 = new MsgsPopTextItem(this, "分享");
		final MsgsPopTextItem item1 = new MsgsPopTextItem(this, "举报");
		List<View> items = new ArrayList<View>();
		items.add(item1);
		MsgsPopWindow pw = new MsgsPopWindow(this, items, v, 0, 0);
		pw.setOnClickPopItemListener(new OnClickPopItemListener() {

			@Override
			public void onClickPopItem(View v) {
				// TODO Auto-generated method stub
				 if (v == item1) {
					Intent intent = new Intent(PlayDetailInfoActivity.this, ReportActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID, playInfo.getUid());
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_USER);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 关闭陪玩提示
	 */
	private void closePlayTip(final TextView openOrCloseManage) {
		final Dialog dialog = new Dialog(PlayDetailInfoActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog消失
		TextView content = (TextView) dialog.findViewById(R.id.content_dialog);
		content.setText("关闭陪玩后，该陪玩信息将不在显示在陪玩列表中，同时将无法接收到任何报名信息，确定关闭该陪玩信息吗？");
		LinearLayout btnBttom = (LinearLayout) dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom = (LinearLayout) dialog.findViewById(R.id.text_bottom);
		Button cancle = (Button) dialog.findViewById(R.id.role_cannelBtn);
		Button commit = (Button) dialog.findViewById(R.id.role_commitBtn);
		cancle.setTextColor(getResources().getColor(R.color.dialog_cancel_btn_text_color));
		commit.setTextColor(getResources().getColor(R.color.dialog_commit_btn_text_color));
		btnBttom.setVisibility(View.VISIBLE);
		textBttom.setVisibility(View.GONE);
		dialog.show();
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (dialog.isShowing()) {

					dialog.dismiss();
					play_manage.setClickable(true);
				}
			}
		});
		commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				changePlayStatus(2, openOrCloseManage);
			}
		});
	}

	public void editTip(String text) {
		final Dialog dialog = new Dialog(PlayDetailInfoActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog消失
		TextView content = (TextView) dialog.findViewById(R.id.content_dialog);
		content.setText(text);
		LinearLayout btnBttom = (LinearLayout) dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom = (LinearLayout) dialog.findViewById(R.id.text_bottom);
		btnBttom.setVisibility(View.GONE);
		textBttom.setVisibility(View.VISIBLE);
		dialog.show();
		textBttom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
	}
		
		
		private void getCommentInfo(){
			ProxyFactory.getInstance().getPlayProxy().getPlayComments(new ProxyCallBack<Msgs.PlayEvalList>() {
				
				@Override
				public void onSuccess(PlayEvalList result) {
					if(downloaddialog.isShowing())
						downloaddialog.dismiss();
					if(result!=null&&playInfo.getEvalnum()!=0){
						renderCommentUI(result);
					}
					parentView.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onFailure(Integer result, String resultMsg) {
					downloaddialog.dismiss();
					ToastUtil.showToast(PlayDetailInfoActivity.this, "获取评价数据失败");
					parentView.setVisibility(View.VISIBLE);
				}
			}, PlayDetailInfoActivity.this, pid, 0, 3);
		}
		
		private void renderCommentUI(PlayEvalList eval){
			user_remark.setText("全部评价");
			user_remark_star1.setVisibility(View.VISIBLE);
			user_remark_star2.setVisibility(View.VISIBLE);
			user_remark_star3.setVisibility(View.VISIBLE);
			user_remark_star4.setVisibility(View.VISIBLE);
			user_remark_star5.setVisibility(View.VISIBLE);
			user_remark_count.setVisibility(View.VISIBLE);
			user_remark_line.setVisibility(View.VISIBLE);
			user_remark_type.setVisibility(View.VISIBLE);
			setStar(playInfo.getStar());
			user_remark_total.setText(playInfo.getEvalnum()+"人评价");
			user_remark_percent.setText(playInfo.getPraise()+"%");
			labelList = eval.getEvalLabelList();
			for(int i = 0;i<labelList.size();i++){
				PlayEvalLabelInfo label = labelList.get(i);
				if(label.getLabelid()==1){
					user_remark_good.setText("好("+label.getNum()+")");
				}else if(label.getLabelid()==2){
					user_remark_often.setText("一般("+label.getNum()+")");
				}else if(label.getLabelid()==3){
					user_remark_bad.setText("差("+label.getNum()+")");
				}
			}
			evallist = eval.getPlayEvalList();
			LogUtil.d("comment", evallist.size()+evallist.toString());
			user_remark_content.removeAllViews();
			if(evallist.size()<3)
			{
				for(int i=0;i<evallist.size();i++){
					PlayEvalInfo info = evallist.get(i);
					setReplyContent(info,i);
				}
			}else{
				for(int i=0;i<3;i++){
					PlayEvalInfo info = evallist.get(i);
					setReplyContent(info,i);
				}
				
			}
		}
	
		private void setReplyContent(final PlayEvalInfo pe, int i){
			String name=null;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			View remarkView = View.inflate(PlayDetailInfoActivity.this, R.layout.play_detail_info_remark, null);
			user_remark_content.addView(remarkView, params);
			ImageView icon =(ImageView)remarkView.findViewById(R.id.play_remark_icon);
			TextView userName=(TextView)remarkView.findViewById(R.id.play_remark_user_name);
			ImageView remark_star1=(ImageView)remarkView.findViewById(R.id.play_user_remark_star1);
			ImageView remark_star2=(ImageView)remarkView.findViewById(R.id.play_user_remark_star2);
			ImageView remark_star3=(ImageView)remarkView.findViewById(R.id.play_user_remark_star3);
			ImageView remark_star4=(ImageView)remarkView.findViewById(R.id.play_user_remark_star4);
			ImageView remark_star5=(ImageView)remarkView.findViewById(R.id.play_user_remark_star5);
			TextView remark_time=(TextView)remarkView.findViewById(R.id.play_remark_time);
			TextView remark_meter=(TextView)remarkView.findViewById(R.id.play_remark_meter);
			TextView remark_reply=(TextView)remarkView.findViewById(R.id.play_remark_reply);
			MyTextView remark_content =(MyTextView)remarkView.findViewById(R.id.remark_content);
			LinearLayout replyView=(LinearLayout)remarkView.findViewById(R.id.user_remark_content);
			ImageView verline =(ImageView)remarkView.findViewById(R.id.play_remark_vertical_line);
			ImageView  line = (ImageView)remarkView.findViewById(R.id.comment_line);
			if(i==2||i==evallist.size()){
				line.setVisibility(View.GONE);
			}else{
				line.setVisibility(View.VISIBLE);
			}
			remark_reply.setVisibility(View.GONE);
			UserInfoDetail user =pe.getUserInfo();
			if(user!=null){
				new ImageLoader().loadRes(ResUtil.getOriginalRelUrl(user.getAvatar()), 0, icon, R.drawable.common_default_icon);
				if(user.getNickname().length()>1){
					name=user.getNickname().substring(0, 1)+"**";
				}else{
					name = user.getNickname();
				}
				userName.setText(name);
				switch(pe.getStar()){
				case 0:
					remark_star1.setImageResource(R.drawable.peiwan_pingjia2);
					remark_star2.setImageResource(R.drawable.peiwan_pingjia2);
					remark_star3.setImageResource(R.drawable.peiwan_pingjia2);
					remark_star4.setImageResource(R.drawable.peiwan_pingjia2);
					remark_star5.setImageResource(R.drawable.peiwan_pingjia2);
					break;
				case 1:
					remark_star1.setImageResource(R.drawable.peiwan_pingjia);
					remark_star2.setImageResource(R.drawable.peiwan_pingjia2);
					remark_star3.setImageResource(R.drawable.peiwan_pingjia2);
					remark_star4.setImageResource(R.drawable.peiwan_pingjia2);
					remark_star5.setImageResource(R.drawable.peiwan_pingjia2);
					
					break;
				case 2:
					remark_star1.setImageResource(R.drawable.peiwan_pingjia);
					remark_star2.setImageResource(R.drawable.peiwan_pingjia);
					remark_star3.setImageResource(R.drawable.peiwan_pingjia2);
					remark_star4.setImageResource(R.drawable.peiwan_pingjia2);
					remark_star5.setImageResource(R.drawable.peiwan_pingjia2);
					break;
				case 3:
					remark_star1.setImageResource(R.drawable.peiwan_pingjia);
					remark_star2.setImageResource(R.drawable.peiwan_pingjia);
					remark_star3.setImageResource(R.drawable.peiwan_pingjia);
					remark_star4.setImageResource(R.drawable.peiwan_pingjia2);
					remark_star5.setImageResource(R.drawable.peiwan_pingjia2);
					break;
				case 4:
					remark_star1.setImageResource(R.drawable.peiwan_pingjia);
					remark_star2.setImageResource(R.drawable.peiwan_pingjia);
					remark_star3.setImageResource(R.drawable.peiwan_pingjia);
					remark_star4.setImageResource(R.drawable.peiwan_pingjia);
					remark_star5.setImageResource(R.drawable.peiwan_pingjia2);
					break;
				case 5:
					remark_star1.setImageResource(R.drawable.peiwan_pingjia);
					remark_star2.setImageResource(R.drawable.peiwan_pingjia);
					remark_star3.setImageResource(R.drawable.peiwan_pingjia);
					remark_star4.setImageResource(R.drawable.peiwan_pingjia);
					remark_star5.setImageResource(R.drawable.peiwan_pingjia);
					break;
				}
				remark_time.setText(SafeUtils.getDate2MyStr2(pe.getCreatetime()));
				if(user.getDistance()<=0){
					verline.setVisibility(View.GONE);
				}else{
					verline.setVisibility(View.VISIBLE);
				}
				remark_meter.setText(DistanceUtil.covertDistance(user.getDistance()));
				remark_content.setText(SmileyUtil.ReplaceSmiley(PlayDetailInfoActivity.this, 
						pe.getContent(),
						PlayDetailInfoActivity.this.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width_level2), PlayDetailInfoActivity.this.getResources()
								.getDimensionPixelSize(R.dimen.global_string_smiley_heigth_level2)));
				if(pe.getEvalReplyList().size()>0){
					addRelyContent(replyView, pe.getEvalReplyList());
				}
				
//				icon.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						if(pe.getUserInfo().getId()!= loginUserVo.getUserid()){
//							// 打开对方资料
//							Intent intent = new Intent(PlayDetailInfoActivity.this, UserDetailInfoActivity.class);
//							Bundle bundle = new Bundle();
//							bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID,pe.getUserInfo().getId());
//							intent.putExtras(bundle);
//							PlayDetailInfoActivity.this.startActivity(intent);
//						}else{
//							ToastUtil.showToast(PlayDetailInfoActivity.this, "查看自己的信息去我的页面");
//						}
//					}
//				});
			}
		}
		private void addRelyContent(LinearLayout view,List<PlayEvalReply> replylist){
			if(replylist.size()==0){
				view.setVisibility(View.GONE);
				return;
			}
				view.setVisibility(View.VISIBLE);
				view.removeAllViews();
			for(int i =0;i<replylist.size();i++){
				PlayEvalReply re = replylist.get(i);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				View reply = View.inflate(PlayDetailInfoActivity.this, R.layout.play_reply_content_item, null);
				TextView  replyuser = (TextView)reply.findViewById(R.id.play_reply_user);
				MyTextView replycontent=(MyTextView)reply.findViewById(R.id.play_replay_content);
				replyuser.setText(re.getNickname());
//				replyuser.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View arg0) {
//						if (playInfo.getUserInfo().getId() != loginUserVo.getUserid()) {
//							// 打开对方资料
//							Intent intent = new Intent(PlayDetailInfoActivity.this, UserDetailInfoActivity.class);
//							Bundle bundle = new Bundle();
//							bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, playInfo.getUserInfo().getId());
//							intent.putExtras(bundle);
//							PlayDetailInfoActivity.this.startActivity(intent);
//						} else {
//							ToastUtil.showToast(PlayDetailInfoActivity.this, "查看自己的信息去我的页面");
//						}
//					}
//				});
				replycontent.setText(SmileyUtil.ReplaceSmiley(PlayDetailInfoActivity.this, "回复 " + re.getTonickname() + ": "
						+ re.getContent(),
						PlayDetailInfoActivity.this.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width_level2), PlayDetailInfoActivity.this.getResources()
								.getDimensionPixelSize(R.dimen.global_string_smiley_heigth_level2)));
				
				view.addView(reply, params);
		}
	}
}
