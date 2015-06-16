package com.youban.msgs.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.pay.ui.PayMainActivity;
import com.iwgame.utils.LogUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.youban.msgs.R;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

	private static final String TAG = "WXPayEntryActivity";

	private IWXAPI api;
	private TextView resulttxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addContentViewChild(View.inflate(this, R.layout.pay_result, null));
		setTitleTxt("微信支付结果");
		resulttxt = (TextView) findViewById(R.id.resulttxt);

		api = WXAPIFactory.createWXAPI(this, SystemContext.WXPAY_APPID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		LogUtil.d(TAG, "--->>onPayFinish, errCode=" + resp.errCode + ", errStr=" + resp.errStr + ", transaction=" + resp.transaction + ", openid="
				+ resp.openId);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			PayMainActivity.wxpayErrorCode = resp.errCode;
			// 处理返回结果
			if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
				resulttxt.setText("支付成功！");
			} else if (resp.errCode == BaseResp.ErrCode.ERR_COMM) {
				resulttxt.setText("支付异常！");
			} else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
				resulttxt.setText("中途取消支付！");
			} else if (resp.errCode == BaseResp.ErrCode.ERR_SENT_FAILED) {
				resulttxt.setText("支付提交失败！");
			} else if (resp.errCode == BaseResp.ErrCode.ERR_AUTH_DENIED) {
				resulttxt.setText("支付认证失败！");
			} else if (resp.errCode == BaseResp.ErrCode.ERR_UNSUPPORT) {
				resulttxt.setText("支付异常！");
			} else {
				resulttxt.setText("支付异常！");
			}
		}
	}
}