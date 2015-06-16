package com.iwgame.msgs.module.setting.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ImageCacheLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareDate;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.setting.vo.Goods;
import com.iwgame.msgs.module.setting.vo.GoodsDetail;
import com.iwgame.msgs.module.setting.vo.ObservableScrollView;
import com.iwgame.msgs.module.setting.vo.ScrollViewListener;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.ShareUtil;
import com.iwgame.msgs.utils.SimpleDateFormateUtil;
import com.iwgame.msgs.widget.ShareImageView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.ShareTaskUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * 商品详情界面
 * @author jczhang
 *
 */
public class GoodsDetailActivity extends BaseActivity implements ScrollViewListener{
	private ImageView goodsIcon;
	private TextView goodsName;
	private TextView haveReceivedNum;
	private TextView remainNum;
	private TextView fetchDate;
	private TextView fetchTime;
	private TextView needPoin;
	private TextView changeBtn;
	private Goods goods = null;
	private DisplayMetrics dm;
	private LinearLayout goodsDetailContent;
    private ImageView rightImageView;
    private TextView transTimeDesc;
	//商品信息
	private long goodsId;
    private CustomProgressDialog customProgressDialog;
    //用来保存输入框控件
    private List<EditText> editTextList = new ArrayList<EditText>();
    private List<TextView> contentList = new ArrayList<TextView>();
    private String result;//兑换模板
    List<String> stringList = new ArrayList<String>();
    private LayoutInflater inflater;
    private boolean flag = false;//用来标记是否显示兑换的 按钮 
    private RelativeLayout changeBtnParent;
    private ObservableScrollView scrollView;
	private Dialog dialog1;
	private Dialog dialog2;
	private Dialog dialog3;
	private Dialog dialog4;
    private TextView cosumePointDesc;
	/**
	 * 当界面一启动的时候，
	 * 就执行下面的这个方法 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}


	/**
	 * 初始化界面
	 * 初始化界面上的控件
	 */
	private void init() {
		inflater = LayoutInflater.from(this);
		dm = new DisplayMetrics();getWindowManager().getDefaultDisplay().getMetrics(dm);
		flag = getIntent().getBooleanExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_FLAG, false);
		customProgressDialog = CustomProgressDialog.createDialog(this, false);
		goodsId = getIntent().getLongExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_ID, 0);
		titleTxt.setText("详情");
		setLeftVisible(true);
		setRightVisible(true);
		rightImageView = new ShareImageView(this);
		addRightView(rightImageView);
		View view = View.inflate(this, R.layout.goods_detail_activity, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(view,params);
		goodsIcon = (ImageView)view.findViewById(R.id.goods_icon);
		goodsName = (TextView)view.findViewById(R.id.goods_name);
		haveReceivedNum = (TextView)view.findViewById(R.id.have_receive_num);
		remainNum = (TextView)view.findViewById(R.id.remain_num);
		fetchDate = (TextView)view.findViewById(R.id.trans_date);
		fetchTime = (TextView)view.findViewById(R.id.trans_time);
		needPoin = (TextView)view.findViewById(R.id.need_point);
		changeBtn = (TextView)view.findViewById(R.id.trans_btn);
        cosumePointDesc = (TextView)view.findViewById(R.id.cosume_point_desc);
		transTimeDesc = (TextView)view.findViewById(R.id.trans_time_desc);
		changeBtnParent = (RelativeLayout)view.findViewById(R.id.trans_btn_parent);
		scrollView = (ObservableScrollView)view.findViewById(R.id.total_content);
		if(flag){
			scrollView.setScrollViewListener(this);
			changeBtnParent.setVisibility(View.VISIBLE);
			changeBtn.setVisibility(View.VISIBLE);
			rightView.setVisibility(View.VISIBLE);
		}else{
			changeBtnParent.setVisibility(View.GONE);
			rightView.setVisibility(View.INVISIBLE);
		}
		goodsDetailContent = (LinearLayout)view.findViewById(R.id.good_detail_content);
		setListener();
		getGoodsDetailMsgs();
	}

	/**
	 * 获取商品详细信息
	 * 展示到界面
	 */
	private void getGoodsDetailMsgs() {
		customProgressDialog.show();
		ProxyFactory.getInstance().getUserProxy().getGoodsDetail(new ProxyCallBack<Goods>() {

			@Override
			public void onSuccess(Goods result) {
				customProgressDialog.dismiss();
				if(result != null){
					goods = result;
					addDataToLayout(result);
				}
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("goods", result);
				intent.putExtras(bundle);
				setResult(1, intent);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				customProgressDialog.dismiss();
				ErrorCodeUtil.handleErrorCode(GoodsDetailActivity.this, result, resultMsg);
			}
		}, this, goodsId);
	}

	/**
	 * 添加右边菜单
	 * @param v
	 */
	protected void addRightView(View v){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.width = DisplayUtil.dip2px(this, 57);
		params.height = DisplayUtil.dip2px(this, 46);
		params.setMargins(0, 0, DisplayUtil.dip2px(this, 9), 0);
		rightView.setLayoutParams(params);
		rightView.addView(v, params);
	}
	
	
	/**
	 * 将获取到的详情
	 * 添加到线性布局当中 
	 */
	protected void addDataToLayout(Goods result) {
		goodsDetailContent.removeAllViews();
		AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		mAlarmManager.setTimeZone("GMT+08:00");
		ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(result.getIcon()), goodsIcon, R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
		goodsName.setText(""+result.getName());
		haveReceivedNum.setText(""+result.getObtainNum());
		remainNum.setText(""+result.getRemainNum());
		if(result.getNeedPoint() > 0){
			cosumePointDesc.setVisibility(View.VISIBLE);
			cosumePointDesc.setText("耗费积分：");
			if(setRoleItemVisible()){
			cosumePointDesc.setTextColor(getResources().getColor(R.color.global_color3));
			}
			needPoin.setVisibility(View.VISIBLE);
			needPoin.setText(""+result.getNeedPoint());
		}else{
			cosumePointDesc.setVisibility(View.VISIBLE);
			cosumePointDesc.setText("免费");
			cosumePointDesc.setTextColor(getResources().getColor(R.color.global_color16));
			needPoin.setVisibility(View.INVISIBLE);
		}
		if(result.getGoodsStatus() == 2){
			transTimeDesc.setText("开始兑换：");
			fetchDate.setText(""+SimpleDateFormateUtil.switchToDate(result.getTransTime()));
			fetchTime.setText(""+SimpleDateFormateUtil.toTimeNoscecond(result.getTransTime()));
		}else{
			transTimeDesc.setText("兑换截止：");
			fetchDate.setText(""+SimpleDateFormateUtil.switchToDate(result.getOffTime()));
			fetchTime.setText(""+SimpleDateFormateUtil.toTimeNoscecond(result.getOffTime()));
		}
		List<GoodsDetail> list = result.getDetail();
		updateBtn();
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				GoodsDetail detail = list.get(i);
				if(detail.getType() == 0){
					//0 为文本
					LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					View view = View.inflate(GoodsDetailActivity.this, R.layout.goods_detail_text_item, null);
					TextView tv = (TextView)view.findViewById(R.id.goods_detail_text);
					tv.setText(""+detail.getDetailItem());
					goodsDetailContent.addView(view,params);
				}else if(detail.getType() == 1){
					//1为图片
					LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					View view = View.inflate(GoodsDetailActivity.this, R.layout.goods_detail_imageview_item, null);
					ImageView iv = (ImageView)view.findViewById(R.id.goods_image);
					ImageCacheLoader.getInstance().loadRes(ResUtil.getOriginalRelUrl(detail.getDetailItem()), iv, R.drawable.postbar_thumbimg_default, R.drawable.postbar_thumbimg_default, R.drawable.postbar_thumbimg_default, null, true);
					params.setMargins(80,20,80,0);
					goodsDetailContent.addView(view,params);
				}
			}
		}
	}
	/**
	 * 设置角色模块是否显示
	 */
	public boolean setRoleItemVisible(){
		AppConfig config = AdaptiveAppContext.getInstance().getAppConfig();
		return config.isShowRoleList();
	}
	/**
	 * 更新Button的
	 * 背景
	 */
	private void updateBtn() {
		if(goods.getGoodsStatus() == 0){
			changeBtn.setEnabled(true);
			changeBtn.setText("兑换");
			changeBtn.setTextColor(getResources().getColor(R.color.market_btn_font_color));
			changeBtn.setBackgroundResource(R.drawable.goods_detail_started_btn_selector);
		}else if(goods.getGoodsStatus() == 1){
			changeBtn.setEnabled(false);
			changeBtn.setText("已兑换");
			changeBtn.setTextColor(getResources().getColor(R.color.market_btn_font_color));
			changeBtn.setBackgroundResource(R.drawable.goods_detail_notstarted_btn_selector);
		}else if(goods.getGoodsStatus() == 2){
			changeBtn.setEnabled(false);
			changeBtn.setText("未开始");
			changeBtn.setTextColor(getResources().getColor(R.color.market_btn_font_color));
			changeBtn.setBackgroundResource(R.drawable.goods_detail_notstarted_btn_selector);
		}else if(goods.getGoodsStatus() == 3){
			changeBtn.setEnabled(false);
			changeBtn.setText("兑换完");
			changeBtn.setTextColor(getResources().getColor(R.color.market_btn_font_color));
			changeBtn.setBackgroundResource(R.drawable.goods_detail_have_started_btn_selector);
		}else if(goods.getGoodsStatus() == 4){
			changeBtn.setEnabled(false);
			changeBtn.setText("已结束");
			changeBtn.setTextColor(getResources().getColor(R.color.point_task_words_color));
			changeBtn.setBackgroundResource(R.drawable.goods_detail_have_started_btn_selector);
		}
	}

	/**
	 * 给兑换按钮
	 * 添加事件监听
	 */
	private void setListener() {
		changeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
				//点击兑换的时候需要弹框
				changeGoods();
			}
		});
		/**
		 * 点击右边的按钮的时候
		 * 执行分享的方法 
		 */
		rightView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				shareGoodsDetail();
			}
		});
	}

	/**
	 * 分享商品的详情
	 */
	protected void shareGoodsDetail() {
		if(goods == null) return;
		ShareDate shareDate = new ShareDate();
		shareDate.setShareType(ShareUtil.SHARE_TYPE_SHAER);//类型为分享
		shareDate.setTargetType(ShareUtil.TYPE_GOOD);//类型为商品
		shareDate.setInTargetType(SystemConfig.CONTENT_TYPE_GOODS_DETIAL);//内部分享类型
		shareDate.setTargetId(goods.getId());//商品id(目标ID)
		shareDate.setTargetName(goods.getName());//商品名称（目标名称）
		// 获得图片地址
		shareDate.setImageUrl(ResUtil.getSmallRelUrl(goods.getIcon()));
		shareDate.setImagePath(goods.getIcon());
		ShareCallbackListener listener = new ShareCallbackListener() {
				@Override
				public void doSuccess(String plamType) {
					//分享商品详情
					ShareTaskUtil.makeShareTask(GoodsDetailActivity.this, "GoodsDetailActivity", goods.getId(), MsgsConstants.OT_POINT_GOODS, MsgsConstants.OP_RECORD_SHARE, plamType, null, this.shortUrl);
				}

				@Override
				public void doFail() {
					// TODO Auto-generated method stub
				}
			};
		ShareManager.getInstance().share(this, inflater, goods, shareDate, listener );
	}

	/**
	 * 点击的兑换的时候
	 * 兑换商品之前
	 * 先做一些判断
	 */
	protected void changeGoods() {
		//首先判断商品是否可兑换
		if(goods != null && goods.getGoodsStatus() == 0){
			//表示可兑换，判断有可能出现的情况 
			if(SystemContext.getInstance().getExtUserVo().getGrade() < goods.getNeedLevel()){
				showFirstDialog("个人等级需要达到LV"+goods.getNeedLevel()+"以上才能参与兑换哦！");
			}else if(SystemContext.getInstance().getPoint() < goods.getNeedPoint()){
				showFirstDialog("你当前的积分不足哦！");
			}else if(goods.getNeedPoint() <= 0){
				//不需要消耗积分的兑换
				noNeedPointTrans();
			}else if(goods.getNeedPoint() > 0){
				//需要消耗积分的兑换
				showTwoDialog("需要耗费你"+goods.getNeedPoint()+"积分参与兑换哦！");
			}
		}else{
			//如果商品信息获取失败，或者不可以领取  执行这个分支
		}
	}


	/**
	 * 第二种弹出框
	 */
	private void showTwoDialog(String msg) {

		dialog2 = new Dialog(this,R.style.SampleTheme_Light);
		dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = View.inflate(this, R.layout.dialog_upgrade_group, null);
		dialog2.setContentView(view);
		TextView tv = (TextView)view.findViewById(R.id.upgrade_group_success);
		tv.setText(""+msg);
		Button cannelBtn = (Button)view.findViewById(R.id.cannelBtn);
		Button commitBtn = (Button)view.findViewById(R.id.commitBtn);
		cannelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog2.dismiss();
			}
		});
		commitBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(goods != null && goods.getDeliveryNum() > 0){
					dialog2.dismiss();
					transGoods(goods.getTransTemplateID());
				}else{
				 dialog2.dismiss();
			     realTransGoods("");
				}
			}
		});
		dialog2.show();
	
	}

	/**
	 * 不需要消息积分的兑换
	 * 走这个分支
	 */
	private void noNeedPointTrans() {
		//判断是否需要获取模板，
		if(goods != null && goods.getDeliveryNum() <= 0){
			//不需要获取模板走这个分支
			realTransGoods("");
		}else if(goods != null && goods.getDeliveryNum() > 0){
			//需要获取兑换模板走这个方法 
			transGoods(goods.getTransTemplateID());
		}
	}

	/**
	 * 第一种弹出框
	 * 下面的按钮只有我知道了这种
	 * @param msg
	 */
	private void showFirstDialog(String msg){
		dialog1 = new Dialog(this,R.style.SampleTheme_Light);
		dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = View.inflate(this, R.layout.dialog_integral, null);
		dialog1.setContentView(view);
		TextView tv = (TextView)view.findViewById(R.id.cue_words);
		tv.setText(""+msg);
		Button cannceBtn = (Button)view.findViewById(R.id.i_know_it);
		cannceBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog1.dismiss();
			}
		});
		dialog1.show();
	}
	
	
	/**
	 * 兑换商品
	 * 首先去获取兑换模板
	 * @param id
	 */
	private void transGoods(final long id){
		customProgressDialog.show();
		new AsyncTask<String, Integer, String>(){

			@Override
			protected String doInBackground(String... arg0) {
				String url = SystemContext.getInstance().getTemplateDirIp()+"/"+id+".json";
				String templateContent = getTemplate(url);
				return templateContent;
			}
			
			protected void onPostExecute(String res) {
					customProgressDialog.dismiss();
					if(res != null){
						result = res;
						showThreeDialog();
					}
			};
		}.execute();
	}
	
	
	/**
	 * 第三种对话框，
	 * 获取到模板后
	 * 弹出来的对话框
	 */
	protected void showThreeDialog() {
		try {
			dialog3 = new Dialog(GoodsDetailActivity.this,
					R.style.SampleTheme_Light);
			dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
			View view = View.inflate(GoodsDetailActivity.this,
					R.layout.dialog2, null);
			final LinearLayout ll = (LinearLayout) view
					.findViewById(R.id.content);
			ll.setOrientation(LinearLayout.VERTICAL);
			Button cancelBtn = (Button) view.findViewById(R.id.cannelBtn);
			cancelBtn.setVisibility(View.GONE);
			View v = (View) view.findViewById(R.id.fengexian);
			v.setVisibility(View.GONE);
			Button commitBtn = (Button) view.findViewById(R.id.commitBtn);
			commitBtn.setText("提交");
			TextView title = (TextView) view.findViewById(R.id.title);
			title.setText("兑换信息");
			ll.removeAllViews();
			dialog3.setContentView(view);
			JSONObject object = new JSONObject(result);
			String templateDetail = object.getString("templateDetail");
			JSONArray array = new JSONArray(templateDetail);
			final LayoutParams params = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			editTextList.clear();
			for (int i = 0; i < array.length(); i++) {
				final JSONObject obj = array.getJSONObject(i);
				View view1 = View.inflate(GoodsDetailActivity.this,
						R.layout.template_item_view_edit, null);
				final EditText content = (EditText) view1
						.findViewById(R.id.show_content);
				View viewLine = (View)view1.findViewById(R.id.show_content_line);
				editTextList.add(content);
				if(i==array.length()-1){
					viewLine.setVisibility(View.GONE);
				}
				content.setHint(obj.getString("detailItem"));
				if(stringList.size() > 0){
					content.setText(stringList.get(i));
					content.setSelection(stringList.get(i).length());
				}
				content.addTextChangedListener(new TextWatcher() {
					
					@Override
					public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
						if(content.getText().toString().trim().length() <= 0){
							try {
								content.setHint(obj.getString("detailItem"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}else{
							content.setHint("");
						}
						
					}
					
					@Override
					public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
							int arg3) {
						if(content.getText().toString().trim().length() <= 0){
							try {
								content.setHint(obj.getString("detailItem"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else{
							content.setHint("");
						}
					}
					
					@Override
					public void afterTextChanged(Editable arg0) {
						if(content.getText().toString().trim().length() <= 0){
							try {
								content.setHint(obj.getString("detailItem"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else{
							content.setHint("");
						}
						
					}
				});
				ll.addView(view1, params);
			}
			final TextView tv = new TextView(GoodsDetailActivity.this);
			tv.setTextColor(getResources()
					.getColor(R.color.red));
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			tv.setText("兑换信息需要填写完成");
			commitBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					ll.removeView(tv);
					boolean flag = true;
					stringList.clear();
					for (int i = 0; i < editTextList.size(); i++) {
						stringList.add(editTextList.get(i).getText().toString().trim());
						if (editTextList.get(i).getText().toString().trim()
								.length() <= 0) {
							flag = false;
							LayoutParams params = new LayoutParams(
									LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
							params.setMargins((int)getResources().getDimension(R.dimen.global_padding3), 0, 0, 0);
							ll.addView(tv, params);
							break;
						}
					}
					if(flag){
						dialog3.dismiss();
						showFourDialog();
					}
				}
			});
			dialog3.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface arg0) {
					stringList.clear();
				}
			});
			dialog3.show();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 弹出第四种对话框
	 * 当提交信息后
	 */
	protected void showFourDialog() {
		try {
			dialog4 = new Dialog(GoodsDetailActivity.this,
					R.style.SampleTheme_Light);
			dialog4.requestWindowFeature(Window.FEATURE_NO_TITLE);
			View view = View.inflate(GoodsDetailActivity.this,
					R.layout.dialog2, null);
			final LinearLayout ll = (LinearLayout) view
					.findViewById(R.id.content);
			ll.setOrientation(LinearLayout.VERTICAL);
			Button cancelBtn = (Button) view.findViewById(R.id.cannelBtn);
			Button commitBtn = (Button) view.findViewById(R.id.commitBtn);
			cancelBtn.setText("修改");
			TextView title = (TextView) view.findViewById(R.id.title);
			title.setText("确定你的相关信息");
			ll.removeAllViews();
			dialog4.setContentView(view);
			JSONObject object = new JSONObject(result);
			String templateDetail = object.getString("templateDetail");
			JSONArray array = new JSONArray(templateDetail);
			final LayoutParams params = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				View view1 = View.inflate(GoodsDetailActivity.this,
						R.layout.template_item_view, null);
				TextView titleTxt = (TextView) view1
						.findViewById(R.id.show_title);
				TextView content = (TextView) view1
						.findViewById(R.id.show_content);
				titleTxt.setText(obj.getString("detailItem")+":");
				content.setText(stringList.get(i));
				contentList.add(content);
				ll.addView(view1, params);
			}
			commitBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					StringBuilder builder = new StringBuilder();
					stringList.clear();
					for(int i = 0; i < contentList.size(); i ++){
						stringList.add(contentList.get(i).getText().toString());
					}
					for (int i = 0; i < stringList.size(); i++) {
						builder.append(stringList.get(i).replaceAll("%", " "));
						builder.append("%");
					}
					dialog4.dismiss();
					String transInfoMsg = builder.toString().substring(0,builder.toString().length()-1);
					realTransGoods(transInfoMsg);
					stringList.clear();
				}
			});
			cancelBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dialog4.dismiss();
					stringList.clear();
					for(int i = 0; i < contentList.size(); i ++){
						stringList.add(contentList.get(i).getText().toString());
					}
					showThreeDialog();
				}
			});
			//当对话框取消的时候，清除stringlist里面的内容
			dialog4.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface arg0) {
					stringList.clear();
					contentList.clear();
				}
			});
			dialog4.show();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 真正执行兑换的方法 
	 * @param transInfo
	 */
	private void realTransGoods(String transInfo){
		customProgressDialog.show();
		ProxyFactory.getInstance().getUserProxy().transGoods(new ProxyCallBack<String>() {

			@Override
			public void onSuccess(String result) {
				customProgressDialog.dismiss();
				if(result != null){
					showFirstDialog("兑换成功,"+result);
					changeBtn.setEnabled(false);
					SystemContext.getInstance().setPoint(SystemContext.getInstance().getPoint() - goods.getNeedPoint());
				}
				getGoodsDetailMsgs();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				customProgressDialog.dismiss();
				showDifDialog(result,resultMsg);
				getGoodsDetailMsgs();
			}
		}, GoodsDetailActivity.this, goods.getId(), transInfo);
	}
	
	/**
	 * 要根据不同的
	 * 错误码 弹出不同的提示框
	 * @param result2
	 */
	protected void showDifDialog(Integer result2,String resultMsg) {
		switch (result2) {
		case -1000:
			ErrorCodeUtil.handleErrorCode(this, result2, resultMsg);
			break;
		case 500120:
			showFirstDialog("兑换失败，你的积分余额不足！");
			break;
		case 500300:
			ToastUtil.showToast(this, "未达到等级");
			break;
		case 500301:
			ToastUtil.showToast(this, "超过兑换次数");
			break;
		case 500302:
			showFirstDialog("兑换失败，该商品已兑换完！");
			break;
		case 500303:
			showFirstDialog("兑换失败，该商品兑换已结束！");
			break;
		case 500304:
			ToastUtil.showToast(this, "兑换未开始");
			break;
		default:
			break;
		}
	}


	/**
	 * 获取模板
	 * @param url
	 * @return
	 */
	private String getTemplate(String ur){
		 String result = null;
	        HttpURLConnection connection = null;
	        InputStreamReader in = null;
	        try {
	            URL url = new URL(ur);
	            connection = (HttpURLConnection) url.openConnection();
	            in = new InputStreamReader(connection.getInputStream());
	            BufferedReader bufferedReader = new BufferedReader(in);
	            StringBuffer strBuffer = new StringBuffer();
	            String line = null;
	            while ((line = bufferedReader.readLine()) != null) {
	                strBuffer.append(line);
	            }
	            result = strBuffer.toString();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (connection != null) {
	                connection.disconnect();
	            }
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	 
	        }
	        return result;
	    }


	/**
	 * 当scrollview滚动的时候
	 * 就会执行下面的这个方法 
	 */
	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y,
			int oldx, int oldy) {
		if(y > oldy && y-oldy > 5 ){
			changeBtnParent.setVisibility(View.GONE);
		}else if(y < oldy && oldy - y > 5){
			changeBtnParent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 在onstop方法里面
	 * 取消所弹出来的对话框
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if(dialog1 != null)dialog1.dismiss();
		if(dialog2 != null)dialog2.dismiss();
		if(dialog3 != null)dialog3.dismiss();
		if(dialog4 != null)dialog4.dismiss();
	}
}
