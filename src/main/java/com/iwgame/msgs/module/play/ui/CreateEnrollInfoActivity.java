/**      
* CreateEnrollInfoActivity.java Create on 2015-5-15     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.play.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.pay.ui.PayMainActivity;
import com.iwgame.msgs.module.pay.ui.util.DoubleUtil;
import com.iwgame.msgs.module.user.ui.UserRoleDetailActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.GameKeysDetail;
import com.iwgame.msgs.proto.Msgs.GameServerEntry;
import com.iwgame.msgs.proto.Msgs.IdResult;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.proto.Msgs.UserRoleData;
import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr;
import com.iwgame.msgs.proto.Msgs.YoubiSchemeResult;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.msgs.widget.picker.NumberPicker;
import com.iwgame.msgs.widget.picker.NumberPicker.OnValueChangeListener;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/** 
 * @ClassName: CreateEnrollInfoActivity 
 * @Description: TODO(...) 
 * @author xingjianlong
 * @date 2015-5-15 上午10:46:56 
 * @Version 1.0
 * 
 */
public class CreateEnrollInfoActivity extends BaseActivity implements OnClickListener{
		
	private long  playid;
	private TextView enroll_gameName;
	private TextView enroll_serverName;
	private TextView enroll_roleName;
	private TextView enroll_orderTime;
	private TextView enroll_playTime;
	private LinearLayout enroll_orderContent;
	private LinearLayout enroll_serverContent;
	private LinearLayout enroll_roleContent;
	private LinearLayout enroll_playContent;
	private TextView enroll_onceWaster;
	private TextView enroll_totalWaster;
	private Button enroll_compelte;
	private List <GameServerEntry> serverlist = null;
	private PlayInfo info;
	private final int ENROLL_SERVER =500;
	private final int ENROLL_ROLE=501;
	private final int ENROLL_HOUR=502;
	private long gid;
	private long sid;
	private long roleid;
	private UserRoleData vo;
	private static int flagsDate = DateUtils.FORMAT_SHOW_DATE;  
	private static int flagsTime = DateUtils.FORMAT_SHOW_TIME ;  
	private static int flagsWeek = DateUtils.FORMAT_SHOW_WEEKDAY;  
	private Button btn;
	private long currentTime;
	private long changeTime;
	private long maxTime;
	private String datestr;
	private String weekstr;
	private String hourstr;
	private String[] values = new String[]{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};;
	private String[] mins;
	private String hours ;
	private String min;
	private int timeNumber;
	private String timeContent;
	private  int cost;
	private  int total;
	private ImageView leftIcon;
	private ImageView rightIcon;
	private View parenView;
	private ImageView enroll_selectser;
	private LinearLayout leftPicker;
	private LinearLayout rightPicker;
	private NumberPicker np;
	private int w_screen;
	private int h_screen;
	private Dialog dialog;
	private int hourToday;
	private int minToday;
	private NumberPicker mp;
	private static final int EC_MSGS_ORDER_TIMES_OUT= 500625;
	private static final int EC_MSGS_ORDER_NOT_FINISHED =500626;
	private static final int EC_MSGS_ORDER_PAY_FAIL =500624;
	private static final int EC_MSGS_ORDER_NOT_GAME = 500620;
	private static final int EC_MSGS_ORDER_NOT_SERVER = 500621;//超出服务器范围
	private static final int EC_MSGS_ORDER_NOT_STARTTIME = 500622;//预约时间超限
	private static final int EC_MSGS_ORDER_NOT_DURATION = 500623;//时长超限
	private static final int EC_MSGS_YOUBI_NOREMAINING = 500701;//游币不足
	private static final int EC_MSGS_CONSUME_CONFIRM = 500702;//消费已完成
	private static final int EC_MSGS_ORDER_NOT_FINISHEd = 500626;//存在未完成订单
	private String todaytime ;
	private String todaydate;
	private TextView enroll_totlaub;
	private String sids;
	public static boolean compelte =false;
	private List<Object> serverid = new ArrayList<Object>();
	private List<GameKeysDetail> keylist = new ArrayList<GameKeysDetail>();
	private String servername;
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();
		inital();
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		enroll_compelte.setClickable(true);
		if(UserRoleDetailActivity.vo!=null){
			vo = UserRoleDetailActivity.vo;
			UserRoleDetailActivity.vo = null;
			setRoleName(vo);
		}
		getUb();
	}
	/**
	 * @param vo
	 */
	private void setRoleName(UserRoleData vo) {
		roleid = vo.getRoleid();
		List<RoleAttr> attrlist = vo.getAttrList();
		for (RoleAttr attr : attrlist) {
			if (attr.getAttrtype() == MsgsConstants.GAME_ROLE_KEY_USER) {
				enroll_roleName.setText(attr.getContent());
				enroll_roleName.setTextColor(getResources().getColor(R.color.black));
			} 
		}
	}

	/**
	 * 获取传过来的playid
	 */
	private void getData(){
		playid = getIntent().getLongExtra("pid", 0);
	}
	/**
	 * 初始化数据
	 */
	private void inital(){
		setTitleTxt("填写报名资料");
		LinearLayout convertView =(LinearLayout)findViewById(R.id.contentView);
		 parenView = View.inflate(this, R.layout.create_user_enroll, null);
		LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		convertView.addView(parenView, params);
		enroll_serverContent=(LinearLayout)parenView.findViewById(R.id.enroll_server_content);
		enroll_roleContent=(LinearLayout)parenView.findViewById(R.id.enroll_role_content);
		enroll_orderContent=(LinearLayout)parenView.findViewById(R.id.enroll_order_content);
		enroll_playContent =(LinearLayout)parenView.findViewById(R.id.enroll_play_content);
		enroll_gameName =(TextView)parenView.findViewById(R.id.enroll_game_name);
		enroll_serverName=(TextView)parenView.findViewById(R.id.enroll_server_name);
		enroll_roleName=(TextView)parenView.findViewById(R.id.enroll_role_name);
		enroll_orderTime=(TextView)parenView.findViewById(R.id.enroll_order_time);
		enroll_playTime=(TextView)parenView.findViewById(R.id.enroll_play_time);
		enroll_onceWaster=(TextView)parenView.findViewById(R.id.enroll_play_once_waste);
		enroll_totalWaster=(TextView)parenView.findViewById(R.id.enroll_play_total_waster);
		enroll_compelte =(Button)parenView.findViewById(R.id.enroll_compelte);
		enroll_selectser =(ImageView)parenView.findViewById(R.id.enroll_choose_server);
		enroll_totlaub =(TextView)parenView.findViewById(R.id.user_total_u);
		enroll_serverContent.setOnClickListener(this);
		enroll_roleContent.setOnClickListener(this);
		enroll_orderContent.setOnClickListener(this);
		enroll_playContent.setOnClickListener(this);
		enroll_compelte.setOnClickListener(this);
		getPlayInfo(this, playid, "1,2,3,4");
		parenView.setVisibility(View.GONE);
	}
	/**
	 * 获取陪玩信息
	 * @param context
	 * @param playid
	 * @param resulttype
	 */
	private void getPlayInfo(Context context ,long playid,String resulttype){
		final CustomProgressDialog downloaddialog = CustomProgressDialog.createDialog(this, false);
		downloaddialog.show();
		ProxyFactory.getInstance().getPlayProxy().searchPlayDetailInfo(new ProxyCallBack<Msgs.PlayInfo>() {
			
			@Override
			public void onSuccess(PlayInfo result) {
				if(result!=null){
					info = result;
					downloaddialog.dismiss();
					parenView.setVisibility(View.VISIBLE);
				setViewData(result);
				}else{
					return;
				}
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				downloaddialog.dismiss();
				parenView.setVisibility(View.VISIBLE);
			}
		}, context, playid, resulttype);
	}
	/**
	 * 设置界面的数据
	 * @param info
	 */
	public void setViewData(PlayInfo info){
		servername = info.getServername();
		enroll_gameName.setText(info.getGamename());
		enroll_serverName.setText(servername);
		cost=info.getCost();
		gid = info.getGid();
		sid=info.getSid();
		sids =info.getSids();
		if(sids==null||sids.equals("")) {
			enroll_selectser.setVisibility(View.GONE);
			enroll_serverContent.setClickable(false);
		}else{
			enroll_selectser.setVisibility(View.VISIBLE);
			enroll_serverContent.setClickable(true);
		}
		LogUtil.d("enroll", sid+"");
		enroll_onceWaster.setText(cost+"U币/小时*"+timeNumber+"小时"+"=");
		enroll_totalWaster.setText(total+"U币");
	}
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		if(view.getId()==R.id.enroll_server_content){
			Intent intent = new Intent(CreateEnrollInfoActivity.this, EnrollServerListActivity.class);
			intent.putExtra("playinfo", info);
			startActivityForResult(intent, ENROLL_SERVER);
		}else if(view.getId()==R.id.enroll_role_content){
			Intent intent = new Intent(CreateEnrollInfoActivity.this, UserRoleDetailActivity.class);
			intent.putExtra("enroll", true);
			intent.putExtra("gid",gid);
			intent.putExtra("sid", sid);
			intent.putExtra("sname", servername);
			startActivity(intent);
		}else if(view.getId()==R.id.enroll_order_content){
			setChooseDateDialog();
		}else if(view.getId()==R.id.enroll_play_content){
			Intent intent = new Intent(CreateEnrollInfoActivity.this, PlayTimeLongActivity.class);
			startActivityForResult(intent, ENROLL_HOUR);
		}else if(view.getId()==R.id.enroll_compelte){
			if(checkValues()){
				enroll_compelte.setClickable(false);
			certifyPlay();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK){
			switch(requestCode){
			case ENROLL_SERVER:
					sid =data.getLongExtra(SystemConfig.ENROLL_CHOOSED_SERVER_ID, 0);
					 servername =data.getStringExtra(SystemConfig.ENROLL_CHOOSED_SERVER_NAME);
					if(sid==0) return;
					if(servername.equals("")) return;
					enroll_serverName.setText(servername);
				break;
			case ENROLL_ROLE:
				vo =(UserRoleData)getIntent().getSerializableExtra("userrolevo");
				if(vo==null) return;
				List<RoleAttr> list = vo.getAttrList();
				roleid =vo.getRoleid();
				for(RoleAttr attr:list){
					if(attr.getAttrtype()== MsgsConstants.GAME_ROLE_KEY_USER){
						enroll_roleName.setText(attr.getContent());
					}
				}
				break;
			case ENROLL_HOUR:
				timeNumber=data.getExtras().getInt("position", 0);
				timeContent =data.getExtras().getString("hour");
				enroll_playTime.setText(timeContent);
				enroll_playTime.setTextColor(getResources().getColor(R.color.black));
				total =cost*timeNumber;
				enroll_onceWaster.setText(cost+"U币/小时*"+timeNumber+"小时"+"=");
				enroll_totalWaster.setText(total+"U币");
				break;
			}
		}
	}
	/**
	 * 选择时间对话框
	 */
	private void setChooseDateDialog(){
		dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.enroll_choose_play_time);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.anim.set_add_play_image);
		DisplayMetrics dm =getResources().getDisplayMetrics();  
        w_screen = dm.widthPixels;  
        h_screen = dm.heightPixels;  
		currentTime = SystemContext.getInstance().getCurrentTimeInMillis();
		todaydate = new SimpleDateFormat("MM月dd日").format(new java.util.Date(currentTime));
		setDate(currentTime, null, null);
		String hour = hourstr.substring(0,hourstr.indexOf(":"));
		String minute =hourstr.substring(hourstr.indexOf(":")+1);
		 hourToday =Integer.parseInt(hour);
		 minToday =Integer.parseInt(minute);
		TextView title = (TextView) dialog.findViewById(R.id.enroll_date);
		RelativeLayout content =(RelativeLayout)dialog.findViewById(R.id.content);
		TextView hour_content =(TextView)dialog.findViewById(R.id.hour_content);
		TextView min_content =(TextView)dialog.findViewById(R.id.min_content);
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params1.setMargins(w_screen/4+10, 0, 0, 0);
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params2.setMargins(w_screen/4+18, 0, 0, 0);
		hour_content.setLayoutParams(params1);
		min_content.setLayoutParams(params2);
		content.setLayoutParams(new LayoutParams(w_screen, LayoutParams.WRAP_CONTENT));
		 leftPicker = (LinearLayout)dialog.findViewById(R.id.enroll_leftPicker);
		 rightPicker = (LinearLayout)dialog.findViewById(R.id.enroll_rightPicker);
		mp = new NumberPicker(dialog.getContext());
		rightPicker.removeAllViews();
		rightPicker.addView(mp, new LinearLayout.LayoutParams(w_screen/2, LayoutParams.WRAP_CONTENT));
		mins = new String[]{"00","30"};
		mp.setMaxValue(mins.length-1);
		mp.setMinValue(0);
		mp.setFocusable(false);
		mp.setFocusableInTouchMode(true);
		mp.setWrapSelectorWheel(false);
		mp.setDisplayedValues(mins);
		setTodayTime();
		dialog.show();
		LinearLayout date =(LinearLayout)dialog.findViewById(R.id.date_view);
		changeDate(date);
		mp.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				// TODO Auto-generated method stub
				min=mins[newVal];
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				enroll_orderTime.setText(datestr+"  "+hours+":"+min);
				enroll_orderTime.setTextColor(getResources().getColor(R.color.black));
				dateToLong ();
			}
		});
	}
	
	/**
	 * @param hour
	 * @param minute
	 * @param np
	 * @param mp
	 */
	private void setModifyNumber(int hour, int mit, NumberPicker np,
			String [] time) {
		int value = 24 - hour;
		if(mit>=0&&mit<=15){
			np.setValue(0);
			mp.setValue(1);
			hours=hour+"";
			min=mins[1];
		}else if(value>1&&mit>15&&mit<=45){
			np.setValue(1);
			mp.setValue(0);
			hours=time[1];
			min=mins[0];
		}else if(value>1&&(mit>45&&mit<=59)){
			np.setValue(1);
			mp.setValue(1);
			hours=time[1];
			min=mins[1];
		}
		todaytime=hours+":"+min;
	}
	private void changeDate(View view){
		if(currentTime!=0){
			changeTime =currentTime;
			maxTime =currentTime+7*24*60*60*1000;
		}
		final ImageView enroll_left =(ImageView)view.findViewById(R.id.enroll_left);
		final ImageView enroll_right=(ImageView)view.findViewById(R.id.enroll_right);
		final LinearLayout leftContent =(LinearLayout)view.findViewById(R.id.enroll_left_content);
		final RelativeLayout rightContent=(RelativeLayout)view.findViewById(R.id.enroll_right_content);
		final TextView date =(TextView)view.findViewById(R.id.enroll_date);
		final TextView week =(TextView)view.findViewById(R.id.enroll_week);
		setDate(currentTime,date,week);
		if(changeTime==currentTime){
			leftContent.setClickable(false);
			enroll_left.setBackgroundResource(R.drawable.peiwan_time_left_dis);
			enroll_right.setBackgroundResource(R.drawable.enroll_time_right_selector);
		}else{
			leftContent.setClickable(true);
		}
		leftContent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				rightContent.setClickable(true);
				if(changeTime==currentTime){
					leftContent.setClickable(false);
					enroll_left.setBackgroundResource(R.drawable.peiwan_time_left_dis);
					enroll_right.setBackgroundResource(R.drawable.enroll_time_right_selector);
				}else{
					changeTime=changeTime-24*60*60*1000;
					enroll_left.setBackgroundResource(R.drawable.enroll_time_left_selector);
					enroll_right.setBackgroundResource(R.drawable.enroll_time_right_selector);
					leftContent.setClickable(true);
					setDate(changeTime,date,week);
					if(changeTime==currentTime){
						setTodayTime();
					}else{
					setNumPicker();
					}
				}
				
			}
		});
		rightContent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				leftContent.setClickable(true);
				if(changeTime==maxTime){
					enroll_right.setBackgroundResource(R.drawable.peiwan_time_right_dis);
					enroll_left.setBackgroundResource(R.drawable.enroll_time_left_selector);
					rightContent.setClickable(false);
				}else{
					changeTime=changeTime+24*60*60*1000;
					enroll_left.setBackgroundResource(R.drawable.enroll_time_left_selector);
					enroll_right.setBackgroundResource(R.drawable.enroll_time_right_selector);
					rightContent.setClickable(true);
					setDate(changeTime,date,week);
					setNumPicker();
				}
			}
		});
	}
	
	public void setDate(long time,TextView date,TextView week){
		 datestr=new SimpleDateFormat("MM月dd日").format(new java.util.Date(time));
		 hourstr=new SimpleDateFormat("HH:mm").format(new java.util.Date(time));
		 weekstr =(String)DateUtils.formatDateTime(CreateEnrollInfoActivity.this, time, flagsWeek);
		String hour = hourstr.substring(0,hourstr.indexOf(":"));
		String minute =hourstr.substring(hourstr.indexOf(":")+1);
		 hourToday =Integer.parseInt(hour);
		 minToday =Integer.parseInt(minute);
		 if(24-hourToday==1&&minToday>15){
			 currentTime=currentTime+(60-minToday)*60*1000;
			 datestr=new SimpleDateFormat("MM月dd日").format(new java.util.Date(time));
			 hourstr=new SimpleDateFormat("HH:mm").format(new java.util.Date(time));
			 weekstr =(String)DateUtils.formatDateTime(CreateEnrollInfoActivity.this, time, flagsWeek);
		 }
		 if(date!=null&&week!=null){
		 date.setText(datestr);
		 week.setText(weekstr);
		 }
	}
	/**
	 * 报名陪玩
	 * @param context
	 * @param pid
	 * @param rid
	 * @param gid
	 * @param sid
	 * @param starttime
	 * @param duration
	 */
	private void enrollPlay(Context context,long pid,long rid,long gid,long sid,long time,Integer duration){
		ProxyFactory.getInstance().getPlayProxy().createPlayEnroll(new ProxyCallBack<Msgs.IdResult>() {
			
			@Override
			public void onSuccess(IdResult result) {
				if(result!=null){
				enroll_compelte.setClickable(true);
				Intent intent  =new Intent();
				intent.putExtra("compelte", true);
				setResult(Activity.RESULT_OK, intent);
				CreateEnrollInfoActivity.this.finish();
				}else{
					ToastUtil.showToast(CreateEnrollInfoActivity.this, "报名失败");
				}
				downloaddialog.dismiss();
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				downloaddialog.dismiss();
				compelte = false;
				if(result ==EC_MSGS_ORDER_NOT_FINISHED){
					enrollErrorDialog("报名存在未完成订单");
				}else if(result==EC_MSGS_ORDER_TIMES_OUT){
					enrollErrorDialog("报名超过限制次数");
				}else if(result==EC_MSGS_ORDER_PAY_FAIL){
					payFail();
				}else if(result==EC_MSGS_ORDER_NOT_GAME){
					enrollErrorDialog("当前所选游戏角色和对方支持陪玩的游戏角色不一致！");
				}else if(result==EC_MSGS_ORDER_NOT_SERVER){
					enrollErrorDialog("当前所选游戏角色和对方支持陪玩的游戏角色不一致！");
				}else if(result==EC_MSGS_ORDER_NOT_STARTTIME){
					enrollErrorDialog("预约时间超限！");
				}else if(result==EC_MSGS_ORDER_NOT_DURATION){
					enrollErrorDialog("最长可预约23小时哦！");
				}else if(result==EC_MSGS_YOUBI_NOREMAINING ){
					payFail();
				}else if(result==EC_MSGS_CONSUME_CONFIRM){
					enrollErrorDialog("消费已完成哦！");
				}else if(result==EC_MSGS_ORDER_NOT_FINISHEd ){
					enrollErrorDialog("存在未完成订单");
				}
			}
		}, context, pid, rid, gid, sid, time, duration);
	}
	private CustomProgressDialog downloaddialog;
	/**
	 *确认报名提示框
	 */
	private void certifyPlay(){
		final Dialog dialog = new Dialog(CreateEnrollInfoActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("本次报名需要支付"+total+"U币，此U币会在陪玩确认结束后支付给对方。确认继续报名吗？");
		LinearLayout btnBttom = (LinearLayout)dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom =(LinearLayout)dialog.findViewById(R.id.text_bottom);
		Button cancle =(Button)dialog.findViewById(R.id.role_cannelBtn);
		Button commit =(Button)dialog.findViewById(R.id.role_commitBtn);
		cancle.setTextColor(getResources().getColor(R.color.dialog_cancel_btn_text_color));
		commit.setTextColor(getResources().getColor(R.color.dialog_commit_btn_text_color));
		commit.setText("确认报名");
		btnBttom.setVisibility(View.VISIBLE);
		textBttom.setVisibility(View.GONE);
		dialog.show();
		dialog.findViewById(R.id.role_cannelBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				enroll_compelte.setClickable(true);
			}
		});
		dialog.findViewById(R.id.role_commitBtn).setOnClickListener(new OnClickListener() {
			
			

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				downloaddialog = CustomProgressDialog.createDialog(CreateEnrollInfoActivity.this, false);
				downloaddialog.show();
				compelte = true;
				enrollPlay(CreateEnrollInfoActivity.this, playid, roleid, gid, sid,dateToLong() , timeNumber);
			}
		});
	}
	/**
	 * 支付失败提示框
	 */
	private void payFail(){
		final Dialog dialog = new Dialog(CreateEnrollInfoActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("支付失败，U币不足？");
		LinearLayout btnBttom = (LinearLayout)dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom =(LinearLayout)dialog.findViewById(R.id.text_bottom);
		btnBttom.setVisibility(View.VISIBLE);
		textBttom.setVisibility(View.GONE);
		Button cancle =(Button)dialog.findViewById(R.id.role_cannelBtn);
		Button commit =(Button)dialog.findViewById(R.id.role_commitBtn);
		cancle.setTextColor(getResources().getColor(R.color.dialog_cancel_btn_text_color));
		commit.setTextColor(getResources().getColor(R.color.dialog_commit_btn_text_color));
		commit.setText("去充值");
		dialog.show();
		dialog.findViewById(R.id.role_cannelBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				enroll_compelte.setClickable(true);
			}
		});
		dialog.findViewById(R.id.role_commitBtn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				enroll_compelte.setClickable(true);
				Intent intent = new Intent(CreateEnrollInfoActivity.this, PayMainActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private long setstartTime(){
	   long hourm = Integer.parseInt(hours)*60*60*1000;
	   long minm=Integer.parseInt(min)*60*1000;
	   return changeTime+hourm+minm;
	}
	
	public boolean checkValues(){
		if(roleid==0){
			ToastUtil.showToast(CreateEnrollInfoActivity.this, "请选择对应的游戏角色哦!");
			return false;
		}else if(changeTime==0){
			ToastUtil.showToast(CreateEnrollInfoActivity.this, "还没选择预约时间哦!");
			return false;
		}else if(timeNumber==0){
			ToastUtil.showToast(CreateEnrollInfoActivity.this, "还没选择陪玩时长哦!");
			return false;
		}else if(todayToLong()>dateToLong()){
			ToastUtil.showToast(CreateEnrollInfoActivity.this, "你选择的时间不是陪玩规定的时间哦!");
			return false;
		}
		return true;
	}
	/**
	 * 错误吗提示框
	 * @param text
	 */
	public void enrollErrorDialog(String text){
		final Dialog dialog = new Dialog(CreateEnrollInfoActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText(text);
		LinearLayout btnBttom = (LinearLayout)dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom =(LinearLayout)dialog.findViewById(R.id.text_bottom);
		Button commit = (Button)dialog.findViewById(R.id.role_commitBtn);
		Button cancle =(Button)dialog.findViewById(R.id.role_cannelBtn);
		btnBttom.setVisibility(View.GONE);
		textBttom.setVisibility(View.VISIBLE);
		dialog.show();
		textBttom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				enroll_compelte.setClickable(true);
			}
		});
	}
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#back()
	 */
	@Override
	protected void back() {
		// TODO Auto-generated method stub
		if(checkPlayData()){
			 backTip();
			}else{
				super.back();
			}
	}
	
	/**
	 * @return
	 */
	private boolean checkPlayData() {
		if(roleid!=0){
			return true;
		}else if(datestr!=null){
			return true;
		}else if(timeNumber!=0){
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		 if(keyCode == KeyEvent.KEYCODE_BACK){    
				if(checkPlayData()){
					 backTip();
					}else{
						super.back();
					}
		 }
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 返回角色提示框
	 */
	public void backTip(){
		final Dialog dialog = new Dialog(CreateEnrollInfoActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("尚有未填写的信息，确定要返回吗？");
		LinearLayout btnBttom = (LinearLayout)dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom =(LinearLayout)dialog.findViewById(R.id.text_bottom);
		Button commit = (Button)dialog.findViewById(R.id.role_commitBtn);
		Button cancle =(Button)dialog.findViewById(R.id.role_cannelBtn);
		btnBttom.setVisibility(View.VISIBLE);
		textBttom.setVisibility(View.GONE);
		dialog.show();
		commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CreateEnrollInfoActivity.this.finish();
			}
		});
		cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				enroll_compelte.setClickable(true);
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	private String [] randomTime(int hour,int min){
		int size = 24-hour;
		String[] time = new String[size];
		if(size==1){
			if(min>15){
				return values;
			}
		}
		for(int i=0;i<size;i++){
			time[i]=hour+i+"";
		}
		return time;
	}
	
	public void setNumPicker(){
		NumberPicker nup = new NumberPicker(dialog.getContext());
		leftPicker.removeAllViews();
		leftPicker.addView(nup, new LinearLayout.LayoutParams(w_screen/2, LayoutParams.WRAP_CONTENT));
		nup.setMaxValue(values.length-1);
		nup.setMinValue(0);
		nup.setFocusable(false);
		nup.setFocusableInTouchMode(true);
		nup.setWrapSelectorWheel(false);
		nup.setDisplayedValues(values);
		mp.setValue(0);
		hours=values[0];
		min=mins[0];
		nup.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				// TODO Auto-generated method stub
				hours=values[newVal];
			}
		});
	}
	public void setTodayTime(){
		leftPicker.removeAllViews();
		final String [] times = randomTime(hourToday, minToday);
		NumberPicker nup = new NumberPicker(dialog.getContext());
		leftPicker.addView(nup, new LinearLayout.LayoutParams(w_screen/2, LayoutParams.WRAP_CONTENT));
		nup.setMaxValue(times.length-1);
		nup.setMinValue(0);
		nup.setFocusable(false);
		nup.setFocusableInTouchMode(true);
		nup.setWrapSelectorWheel(false);
		nup.setDisplayedValues(times);
		setModifyNumber(hourToday, minToday, nup, times);
		nup.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				// TODO Auto-generated method stub
				hours=times[newVal];
			}
		});
	}
	/**
	 * 获取最终时间
	 * @return
	 */
	public long dateToLong () {
		 String  year=new SimpleDateFormat("yyyy年").format(new java.util.Date(changeTime));
		 String in=year+datestr+" "+hours+":"+min;
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        long time =0;
		try {
			time = format.parse(in).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return time;
	}
	
	public long todayToLong () {
		 String  year=new SimpleDateFormat("yyyy年").format(new java.util.Date(currentTime));
		 String in=year+todaydate+" "+todaytime;
       SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
       long time =0;
		try {
			time = format.parse(in).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       return time;
	}
	private void getUb(){
		ProxyFactory.getInstance().getPayProxy().getUb(new ProxyCallBack<Msgs.YoubiSchemeResult>() {

			@Override
			public void onSuccess(YoubiSchemeResult result) {
				if (result != null) {
					enroll_totlaub.setText(DoubleUtil.getDoubleFormat(result.getMyYoubi()) + "U币");
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				
			}
		}, CreateEnrollInfoActivity.this, 1);
	}
	
}
