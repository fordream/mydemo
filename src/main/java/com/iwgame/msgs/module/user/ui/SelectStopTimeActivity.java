package com.iwgame.msgs.module.user.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.StringUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;


/**
 * 选择禁言时长的界面
 * @author jczhang
 *
 */
public class SelectStopTimeActivity extends BaseActivity{

	private InputMethodManager manager;
	private LinearLayout threeMinite;
	private LinearLayout tenMinite;
	private LinearLayout oneHour;
	private LinearLayout twelveHour;
	private LinearLayout oneDay;
	private ImageView threeMinitePic;
	private ImageView tenMinitePic;
	private ImageView oneHourPic;
	private ImageView twelveHourPic;
	private ImageView oneDayPic;
	private TextView wordNum;
	private TextView inputWhy;
	private Button commitBtn;
	private int temp = -1;
	private int[] time = new int[]{3,10,60,720,1440};
	private ArrayList<ImageView>arrayList;
	private long groupId;
	private String uid;
	private FrameLayout fl;
	private Dialog dialog;
	private Button comfirmBtn;
	private Button cancelBtn;
	private EditText realWhy;
	private Button cleanBtn;
	private TextView title;
	private TextView editWordsName;


	/**
	 * 当程序一起来的时候
	 * 做一起初始化操作
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		groupId = getIntent().getLongExtra(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, 0);
		uid = getIntent().getStringExtra(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID);
		init();
		setView();
		setLinstener();
	}


	/**
	 * 初始化dialog
	 */
	private void setView() {
		dialog = new Dialog(this,R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = View.inflate(this, R.layout.dialog_card, null);
		dialog.setContentView(view);
		comfirmBtn = (Button)view.findViewById(R.id.commitBtn);
		cancelBtn = (Button)view.findViewById(R.id.cannelBtn);
		cleanBtn = (Button)view.findViewById(R.id.act_login_cleanAccountBtn);
		realWhy = (EditText)view.findViewById(R.id.edit_group_card);
		title = (TextView)view.findViewById(R.id.title);
		title.setVisibility(View.VISIBLE);
		title.setText("请输入禁言理由");
		editWordsName = (TextView)view.findViewById(R.id.edit_word_num);
		editWordsName.setText("0/20");
		InputFilterUtil.lengthFilter(this, realWhy, 40,  "不能超过20个汉字或40个字符哦！");
		realWhy.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}
			@Override
			public void afterTextChanged(Editable arg0) {
				String source = realWhy.getText().toString();
				int sourceLen = StringUtil.getCharacterNum(source.toString());
				if(sourceLen > 0){
					double length = Math.ceil(sourceLen/2.0);
					editWordsName.setText((int)length+"/20");
				}else{
					editWordsName.setText("0/20");
				}
			}
		});
	}


	/**
	 * 给界面中的按钮
	 * 设置监听器
	 */
	private void setLinstener() {
		comfirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(realWhy.getText().toString().trim().length() <= 0){
					inputWhy.setText("请输入禁言理由");
				}else{
					inputWhy.setText(realWhy.getText().toString().trim());
				}
				dialog.dismiss();
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		cleanBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(realWhy != null) realWhy.setText("");
			}
		});
		
		fl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popDialog();
			}
		});
		threeMinite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(temp >= 0)
					arrayList.get(temp).setSelected(false);
				temp = 0;
				arrayList.get(temp).setSelected(true);
			}
		});
		tenMinite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(temp >= 0)
					arrayList.get(temp).setSelected(false);
				temp = 1;
				arrayList.get(temp).setSelected(true);
			}
		});
		oneHour.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(temp >= 0)
					arrayList.get(temp).setSelected(false);
				temp = 2;
				arrayList.get(temp).setSelected(true);
			}
		});
		twelveHour.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(temp >= 0)
					arrayList.get(temp).setSelected(false);
				temp = 3;
				arrayList.get(temp).setSelected(true);
			}
		});
		oneDay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(temp >= 0)
					arrayList.get(temp).setSelected(false);
				temp = 4;
				arrayList.get(temp).setSelected(true);
			}
		});
		commitBtn.setOnClickListener(new OnClickListener() {//点击确定按钮，向服务端发送请求，实现禁言的功能
			@Override
			public void onClick(View arg0) {
				stopSayWords();
			}
		});
		inputWhy.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}
			@Override
			public void afterTextChanged(Editable arg0) {
				String text = inputWhy.getText().toString().trim();
				int sourceLen = StringUtil.getCharacterNum(text.toString());
				if(sourceLen > 0){
					double number = Math.ceil(sourceLen/2.0);
					wordNum.setText((int)number+"/20");
				}else{
					wordNum.setText("0/20");
				}
			}
		});
	}


	/**
	 * 当点击整个layout的时候，
	 * 弹出一个对话框
	 * 输入禁言理由
	 */
	protected void popDialog() {
		if(!"请输入禁言理由".equals(inputWhy.getText().toString().trim()))
		realWhy.setText(inputWhy.getText().toString());
		dialog.show();
	}


	/**
	 * 在这个方法里面
	 * 不让输入框弹出来
	 */
	@Override
	protected void onResume() {
		super.onResume();
		manager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);//获取输法manager
		manager.hideSoftInputFromWindow(inputWhy.getWindowToken(), 0);
	}


	/**
	 * 对当前的这个用户
	 * 禁言
	 */
	protected void stopSayWords() {
		if(temp == -1){
			ToastUtil.showToast(this, "禁言需要选择时间哦！");
			return;
		}
		String position = SystemContext.getInstance().getLocation();
		String s = ("请输入禁言理由".equals(inputWhy.getText().toString().trim()))?"":(inputWhy.getText().toString().trim());
		ProxyFactory.getInstance().getUserProxy().userAction(SelectStopTimeActivity.this,position, groupId, time[temp], 166, uid, s, new ProxyCallBack<Integer>() {
			@Override
			public void onSuccess(Integer result) {
				ToastUtil.showToast(SelectStopTimeActivity.this,SelectStopTimeActivity.this.getString(R.string.stop_say_success));
				SelectStopTimeActivity.this.finish();
			}
			@Override
			public void onFailure(Integer result, String resultMsg) {
				ErrorCodeUtil.handleErrorCode(SelectStopTimeActivity.this, result, resultMsg);
			}
		});
	}


	/**
	 * 做一些初始化
	 * 操作
	 * 初始化控件等方法
	 */
	private void init() {
		arrayList = new ArrayList<ImageView>();
		titleTxt.setText("禁言时长");
		View view = View.inflate(this, R.layout.select_stop_time_activity, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(view, params);
		inputWhy = (TextView)view.findViewById(R.id.input_why);
		threeMinite = (LinearLayout)view.findViewById(R.id.three_minite);
		tenMinite = (LinearLayout)view.findViewById(R.id.ten_minite);
		oneHour = (LinearLayout)view.findViewById(R.id.one_hour);
		twelveHour = (LinearLayout)view.findViewById(R.id.twelve_hour);
		oneDay = (LinearLayout)view.findViewById(R.id.one_day);
		threeMinitePic = (ImageView)view.findViewById(R.id.three_minite_tick);
		tenMinitePic = (ImageView)view.findViewById(R.id.ten_minite_tick);
		oneHourPic = (ImageView)view.findViewById(R.id.one_hour_tick);
		twelveHourPic = (ImageView)view.findViewById(R.id.twelve_hour_tick);
		oneDayPic = (ImageView)view.findViewById(R.id.one_day_tick);
		fl = (FrameLayout)view.findViewById(R.id.fl);
		arrayList.add(threeMinitePic);
		arrayList.add(tenMinitePic);
		arrayList.add(oneHourPic);
		arrayList.add(twelveHourPic);
		arrayList.add(oneDayPic);
		wordNum = (TextView)view.findViewById(R.id.input_word_num);
		wordNum.setText("0/20");
		commitBtn = (Button)view.findViewById(R.id.stop_say_commit_btn);
	}
}
