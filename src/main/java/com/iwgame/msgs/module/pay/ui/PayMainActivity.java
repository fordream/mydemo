/**      
 * PayCashActivity.java Create on 2015-5-20     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.pay.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.pay.ui.util.AliPayManager;
import com.iwgame.msgs.module.pay.ui.util.DoubleUtil;
import com.iwgame.msgs.module.pay.ui.util.OnPayCallBack;
import com.iwgame.msgs.module.pay.ui.util.WXPayManager;
import com.iwgame.msgs.module.pay.ui.view.PayItemRelativeLayout;
import com.iwgame.msgs.module.play.ui.PlayProtocolActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ErrorCode;
import com.iwgame.msgs.proto.Msgs.RechargeOrderResult;
import com.iwgame.msgs.proto.Msgs.YoubiFaceValue;
import com.iwgame.msgs.proto.Msgs.YoubiSchemeResult;
import com.iwgame.msgs.utils.Utils;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.msgs.widget.popwindow.MsgsPopTextItem;
import com.iwgame.msgs.widget.popwindow.MsgsPopWindow;
import com.iwgame.msgs.widget.popwindow.MsgsPopWindow.OnClickPopItemListener;
import com.iwgame.utils.AppUtils;
import com.iwgame.utils.ToastUtil;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.youban.msgs.R;

/**
 * @ClassName: PayCashActivity
 * @Description: 我的U币
 * @author 王卫
 * @date 2015-5-20 下午3:40:26
 * @Version 1.0
 * 
 */
public class PayMainActivity extends BaseActivity implements OnClickListener {

	private TextView coinNumTxt;
	private LinearLayout faceValueContent1;
	private LinearLayout faceValueContent2;
	private LinearLayout faceValueContent3;
	private LinearLayout faceValueContent4;
	private LinearLayout faceValueContent5;
	private LinearLayout faceValueContent6;
	private PayItemRelativeLayout payItem1;
	private PayItemRelativeLayout payItem2;
	private PayItemRelativeLayout payItem3;
	private PayItemRelativeLayout payItem4;
	private PayItemRelativeLayout payItem5;
	private PayItemRelativeLayout payItem6;
	private TextView alipay;
	private TextView chatpay;
	private CheckBox payBox;
	private TextView sproto;
	private List<PayItemRelativeLayout> payItems;
	private PayItemRelativeLayout selectItem;

	private LinearLayout payPanelView;
	private EditText enterTxt;
	private Button payBtn;
	private TextView actTipTxt;

	private Button rightbtn;

	private YoubiSchemeResult youbiSchemeResult;

	private AliPayManager alipayManager;

	// 支付宝类型
	public static final int PAY_TYPE_NO = 0;
	public static final int PAY_TYPE_ALI = 1;
	public static final int PAY_TYPE_WX = 2;
	// 支付宝支付商户服务端订单
	private RechargeOrderResult aliOrderResult;
	private RechargeOrderResult wxOrderResult;

	// 支付类型
	public int payType = PAY_TYPE_NO;
	// 微信回调成功或失败错误码
	public static int wxpayErrorCode = BaseResp.ErrCode.ERR_USER_CANCEL;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		alipayManager = new AliPayManager(this, new OnPayCallBack() {

			@Override
			public void onSuccess() {
				ToastUtil.showToast(PayMainActivity.this, "成功充值" + aliOrderResult.getRechargeYoubi() + "U币！");
				aliOrderResult = null;
				clickPayItem(null);
			}

			@Override
			public void onFailure(final int type) {
				if (aliOrderResult == null)
					return;
				// 调用服务端取消订单
				ProxyFactory.getInstance().getPayProxy().cannelOrder(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						// TODO
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub

					}
				}, PayMainActivity.this, aliOrderResult.getOrderNo(), type);
				aliOrderResult = null;
			}
		});
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (wxOrderResult != null) {// 微信支付结果
			if (wxpayErrorCode != BaseResp.ErrCode.ERR_OK) {// 微信加游币失败
				int type = 3;
				if (wxpayErrorCode == BaseResp.ErrCode.ERR_USER_CANCEL) {// 失败调用服务端取消订单
					type = 2;
				}
				// 调用服务端取消订单
				ProxyFactory.getInstance().getPayProxy().cannelOrder(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub

					}
				}, PayMainActivity.this, wxOrderResult.getOrderNo(), type);
			} else {// 微信成功加游币
				ToastUtil.showToast(PayMainActivity.this, "成功充值" + wxOrderResult.getRechargeYoubi() + "U币！");
				clickPayItem(null);
			}
		}
		wxOrderResult = null;
		wxpayErrorCode = BaseResp.ErrCode.ERR_USER_CANCEL;
		getUb();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#back()
	 */
	@Override
	protected void back() {
		Utils.hideSoftInput2(PayMainActivity.this, PayMainActivity.this, enterTxt);
		super.back();
	}

	/**
	 * 
	 */
	private void init() {
		payItems = new ArrayList<PayItemRelativeLayout>();
		// 设置Top中间标题
		setTitleTxt(getResources().getString(R.string.pay_main_title));
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		contentView.addView(View.inflate(this, R.layout.pay_main, null));
		coinNumTxt = (TextView) findViewById(R.id.coinNumTxt);
		faceValueContent1 = (LinearLayout) findViewById(R.id.faceValueContent1);
		faceValueContent2 = (LinearLayout) findViewById(R.id.faceValueContent2);
		faceValueContent3 = (LinearLayout) findViewById(R.id.faceValueContent3);
		faceValueContent4 = (LinearLayout) findViewById(R.id.faceValueContent4);
		faceValueContent5 = (LinearLayout) findViewById(R.id.faceValueContent5);
		faceValueContent6 = (LinearLayout) findViewById(R.id.faceValueContent6);
		alipay = (TextView) findViewById(R.id.alipay);
		chatpay = (TextView) findViewById(R.id.chatpay);
		payBox = (CheckBox) findViewById(R.id.payBox);
		sproto = (TextView) findViewById(R.id.sproto);
		sproto.setText(Html.fromHtml("<u>充值协议</u>"));
		alipay.setOnClickListener(this);
		chatpay.setOnClickListener(this);
		sproto.setOnClickListener(this);
		payItem1 = (PayItemRelativeLayout) View.inflate(this, R.layout.pay_main_item, null);
		payItems.add(payItem1);
		payItem1.setOnClickListener(this);
		payItem2 = (PayItemRelativeLayout) View.inflate(this, R.layout.pay_main_item, null);
		payItems.add(payItem2);
		payItem2.setOnClickListener(this);
		payItem3 = (PayItemRelativeLayout) View.inflate(this, R.layout.pay_main_item, null);
		payItems.add(payItem3);
		payItem3.setOnClickListener(this);
		payItem4 = (PayItemRelativeLayout) View.inflate(this, R.layout.pay_main_item, null);
		payItems.add(payItem4);
		payItem4.setOnClickListener(this);
		payItem5 = (PayItemRelativeLayout) View.inflate(this, R.layout.pay_main_item, null);
		payItems.add(payItem5);
		payItem5.setOnClickListener(this);
		payItem6 = (PayItemRelativeLayout) View.inflate(this, R.layout.pay_main_item, null);
		payItems.add(payItem6);
		payItem6.setOnClickListener(this);
		faceValueContent1.addView(payItem1);
		faceValueContent2.addView(payItem2);
		faceValueContent3.addView(payItem3);
		faceValueContent4.addView(payItem4);
		faceValueContent5.addView(payItem5);
		faceValueContent6.addView(payItem6);
		payItem6.initView(PayMainActivity.this, null, null, PayItemRelativeLayout.VIEW_MODE_OTHER);
		// 设置底部发布框
		LinearLayout bottomView = (LinearLayout) findViewById(R.id.bottomView);
		payPanelView = (LinearLayout) View.inflate(this, R.layout.pay_panel_view, null);
		enterTxt = (EditText) payPanelView.findViewById(R.id.enterTxt);
		payBtn = (Button) payPanelView.findViewById(R.id.payBtn);
		payBtn.setOnClickListener(this);
		actTipTxt = (TextView) payPanelView.findViewById(R.id.actTipTxt);
		payPanelView.setVisibility(View.GONE);
		bottomView.addView(payPanelView);
		// 设置右边菜单
		rightbtn = (Button) findViewById(R.id.rightbtn);
		rightbtn.setBackgroundResource(R.drawable.common_menu_more_nor);
		rightbtn.setVisibility(View.VISIBLE);
		rightbtn.setOnClickListener(this);
		findViewById(R.id.coinContentView).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (payPanelView.getVisibility() == View.VISIBLE && payPanelView != null && enterTxt != null) {
					payPanelView.setVisibility(View.VISIBLE);
					Utils.hideSoftInput2(PayMainActivity.this, PayMainActivity.this, enterTxt);
				}
				return false;
			}
		});
	}

	/**
	 * 获取U币
	 */
	private void getUb() {
		actTipTxt.setVisibility(View.GONE);
		actTipTxt.setText("");
		final CustomProgressDialog loadDialog = CustomProgressDialog.createDialog(this);
		loadDialog.show();
		ProxyFactory.getInstance().getPayProxy().getUb(new ProxyCallBack<Msgs.YoubiSchemeResult>() {

			@Override
			public void onSuccess(YoubiSchemeResult result) {
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				if (result != null) {
					youbiSchemeResult = result;
					coinNumTxt.setText(DoubleUtil.getDoubleFormat(result.getMyYoubi()));
					String tips = result.getActivityTips();
					if (!TextUtils.isEmpty(tips)) {
						actTipTxt.setVisibility(View.VISIBLE);
						actTipTxt.setText(tips);
					}
					List<YoubiFaceValue> faceList = result.getYoubiFaceValueListList();
					if (faceList != null && faceList.size() > 0) {
						for (int i = 0; i < faceList.size(); i++) {
							switch (i) {
							case 0:
								payItem1.initView(PayMainActivity.this, result, faceList.get(i), PayItemRelativeLayout.VIEW_MODE_COMMON);
								break;
							case 1:
								payItem2.initView(PayMainActivity.this, result, faceList.get(i), PayItemRelativeLayout.VIEW_MODE_COMMON);
								break;
							case 2:
								payItem3.initView(PayMainActivity.this, result, faceList.get(i), PayItemRelativeLayout.VIEW_MODE_COMMON);
								break;
							case 3:
								payItem4.initView(PayMainActivity.this, result, faceList.get(i), PayItemRelativeLayout.VIEW_MODE_COMMON);
								break;
							case 4:
								payItem5.initView(PayMainActivity.this, result, faceList.get(i), PayItemRelativeLayout.VIEW_MODE_COMMON);
								break;
							default:
								break;
							}
						}
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				ToastUtil.showToast(PayMainActivity.this, "加载数据失败");
			}
		}, this, 2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v == sproto) {// 跳转协议按钮点击
			Intent intent = new Intent(this, PlayProtocolActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("playprotocol", "充值协议");
			bundle.putString("playurl", SystemConfig.PAY_PROTOCOL_ADRRESS);
			intent.putExtras(bundle);
			startActivity(intent);
		} else if (v == alipay) {// 支付宝支付按钮点击
			if (payBox.isChecked()) {
				getPayOrder(PAY_TYPE_ALI);
			} else {
				ToastUtil.showToast(PayMainActivity.this, "请阅读并同意充值协议");
			}
		} else if (v == chatpay) {// 微信支付按钮点击
			if (payBox.isChecked()) {
				if (AppUtils.isInstallAppByPackageName("com.tencent.mm")) {
					getPayOrder(PAY_TYPE_WX);
				} else {
					ToastUtil.showToast(PayMainActivity.this, "目前您的微信版本过低或未安装微信，需要安装微信才能使用");
				}
			} else {
				ToastUtil.showToast(PayMainActivity.this, "请阅读并同意充值协议");
			}
		} else if (v == payItem1 || v == payItem2 || v == payItem3 || v == payItem4 || v == payItem5 || v == payItem6) {
			clickPayItem((PayItemRelativeLayout) v);
		} else if (v == payBtn) {// 输钱面板确定按钮点击
			// 面值
			String etxt = enterTxt.getText().toString();
			if (etxt.isEmpty()) {
				ToastUtil.showToast(PayMainActivity.this, "请输入充值金额");
			} else {
				long rmb = Long.valueOf(etxt);
				if (rmb <= 0) {
					ToastUtil.showToast(PayMainActivity.this, "请输入大于0的充值金额");
				} else if (youbiSchemeResult.getRechargeMin() != 0 && youbiSchemeResult.getRechargeMax() != 0 && (rmb < youbiSchemeResult.getRechargeMin() || rmb > youbiSchemeResult.getRechargeMax())) {
					ToastUtil.showToast(PayMainActivity.this, "充值金额必须在"+youbiSchemeResult.getRechargeMin()+"~"+youbiSchemeResult.getRechargeMax()+"之间哦！");
				} else {
					// 需要相关校验
					payItem6.rmb_other_txt.setVisibility(View.GONE);
					payItem6.rmb_txt.setText(rmb + "");
					long ubnum = 0l;
					if (youbiSchemeResult != null) {
						ubnum = rmb * youbiSchemeResult.getDefaultRate();
					} else {
						ubnum = rmb * 10;
					}
					payItem6.ub_txt.setText("(" + ubnum + "U币)");
					alipay.setBackgroundResource(R.drawable.pay_alipay_btn_selector);
					chatpay.setBackgroundResource(R.drawable.pay_wechatpay_btn_selector);
					alipay.setEnabled(true);
					chatpay.setEnabled(true);
					alipay.setClickable(true);
					chatpay.setClickable(true);
					Utils.hideSoftInput2(PayMainActivity.this, PayMainActivity.this, enterTxt);
					payPanelView.setVisibility(View.GONE);
				}
			}
		} else if (v == rightbtn) {
			Utils.hideSoftInput2(PayMainActivity.this, PayMainActivity.this, enterTxt);
			onClickRightMenu(v);
		}
	}

	/**
	 * 获取支付订单 payType 1支付宝2微信
	 */
	private void getPayOrder(final int ptype) {
		int num = checkNum(selectItem);
		if (num <= 0) {
			ToastUtil.showToast(PayMainActivity.this, "您支付的金额错误");
			return;
		}
		payBox.setEnabled(false);
		payBox.setClickable(false);
		alipay.setEnabled(false);
		chatpay.setEnabled(false);
		alipay.setClickable(false);
		chatpay.setClickable(false);
		final CustomProgressDialog loadDialog = CustomProgressDialog.createDialog(this);
		loadDialog.show();
		ProxyFactory.getInstance().getPayProxy().pay(new ProxyCallBack<Msgs.RechargeOrderResult>() {

			@Override
			public void onSuccess(RechargeOrderResult result) {
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				if (result != null) {
					if (ptype == PAY_TYPE_ALI) {
						aliOrderResult = result;
						alipayManager.alipay(result.getLinkStr());
					} else if (ptype == PAY_TYPE_WX) {
						wxOrderResult = result;
						if (!TextUtils.isEmpty(result.getLinkStr()) && !TextUtils.isEmpty(result.getPrepayId())) {
							new WXPayManager(PayMainActivity.this).sendPayReq(result.getPartnerId(), result.getPrepayId(), result.getNonceStr(),
									result.getPrepayIdTimeStamp() + "", result.getLinkStr());
						} else {
							ToastUtil.showToast(PayMainActivity.this, "获取支付订单失败:" + result.getReason());
						}
					} else {
						ToastUtil.showToast(PayMainActivity.this, "支付类型错误");
					}
					payType = ptype;
				} else {
					ToastUtil.showToast(PayMainActivity.this, "获取支付订单失败");
				}
				payBox.setEnabled(true);
				payBox.setClickable(true);
				alipay.setEnabled(true);
				chatpay.setEnabled(true);
				alipay.setClickable(true);
				chatpay.setClickable(true);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				if (result == ErrorCode.EC_MSGS_RECHARGE_FACEVALUE_OVERFLOW_VALUE) {
					ToastUtil.showToast(PayMainActivity.this, "该面值充值超限了,请选其他面试哦！");
				} else {
					ToastUtil.showToast(PayMainActivity.this, "获取支付订单失败");
				}
				payBox.setEnabled(true);
				payBox.setClickable(true);
				alipay.setEnabled(true);
				chatpay.setEnabled(true);
				alipay.setClickable(true);
				chatpay.setClickable(true);
			}
		}, this, num, ptype, 1);
	}

	/**
	 * 
	 * @param item
	 * @return
	 */
	private int checkNum(PayItemRelativeLayout item) {
		int num = 0;
		if (item != null && !item.rmb_txt.getText().toString().isEmpty()) {
			num = Integer.valueOf(item.rmb_txt.getText().toString());
		}
		return num;
	}

	/**
	 * 
	 * @param pitem
	 */
	private void clickPayItem(PayItemRelativeLayout pitem) {
		selectItem = pitem;
		for (int i = 0; i < payItems.size(); i++) {
			PayItemRelativeLayout item = payItems.get(i);
			if (pitem == item) {
				item.setBackgroundResource(R.drawable.pay_payitem_bg_pre_shap);
			} else {
				item.setBackgroundResource(R.drawable.pay_payitem_bg_shap);
			}
		}
		if (pitem != payItem6) {
			alipay.setBackgroundResource(R.drawable.pay_alipay_btn_selector);
			chatpay.setBackgroundResource(R.drawable.pay_wechatpay_btn_selector);
			alipay.setEnabled(true);
			chatpay.setEnabled(true);
			alipay.setClickable(true);
			chatpay.setClickable(true);
			Utils.hideSoftInput2(PayMainActivity.this, PayMainActivity.this, enterTxt);
			payPanelView.setVisibility(View.GONE);
			payItem6.rmb_other_txt.setVisibility(View.VISIBLE);
			payItem6.ub_txt.setText("(" + 0 + "U币)");
			payItem6.rmb_txt.setText("0");
		} else {
			if (payItem6.rmb_other_txt.getVisibility() == View.GONE && !TextUtils.isEmpty(payItem6.rmb_txt.getText().toString())) {
				alipay.setBackgroundResource(R.drawable.pay_alipay_btn_selector);
				chatpay.setBackgroundResource(R.drawable.pay_wechatpay_btn_selector);
				alipay.setEnabled(true);
				chatpay.setEnabled(true);
				alipay.setClickable(true);
				chatpay.setClickable(true);
			} else {
				alipay.setBackgroundResource(R.drawable.pay_pay_btn_bg_dis_shap);
				chatpay.setBackgroundResource(R.drawable.pay_pay_btn_bg_dis_shap);
				alipay.setEnabled(false);
				chatpay.setEnabled(false);
				alipay.setClickable(false);
				chatpay.setClickable(false);
			}
			enterTxt.setText("");
			payPanelView.setVisibility(View.VISIBLE);
			enterTxt.requestFocus();
			Utils.showSoftInput(this, enterTxt);
		}
	}

	private void onClickRightMenu(View v) {
		final MsgsPopTextItem item0 = new MsgsPopTextItem(this, "U币明细");
		final MsgsPopTextItem item1 = new MsgsPopTextItem(this, "我要提现");
		List<View> items = new ArrayList<View>();
		items.add(item0);
		items.add(item1);
		MsgsPopWindow pw = new MsgsPopWindow(this, items, v, 0, 0);
		pw.setOnClickPopItemListener(new OnClickPopItemListener() {

			@Override
			public void onClickPopItem(View v) {
				if (v == item0) {
					Intent intent = new Intent(PayMainActivity.this, PayDetailInfoActivity.class);
					startActivity(intent);
				} else if (v == item1) {
					ToastUtil.showToast(PayMainActivity.this, "该功能暂未开放，敬请期待！");
				}
			}
		});
	}

}
