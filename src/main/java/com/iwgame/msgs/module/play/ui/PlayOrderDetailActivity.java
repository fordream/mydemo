/**      
 * PlayOrderDetailActivity.java Create on 2015-5-18     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.chat.ui.UserChatFragmentActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PlayOrderInfo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: PlayOrderDetailActivity
 * @Description: 订单详情页面
 * @author 王卫
 * @date 2015-5-18 下午3:11:54
 * @Version 1.0
 * 
 */
public class PlayOrderDetailActivity extends BaseActivity {

	// 陪玩id
	private long oid;
	private UserVo loginUserVo;
	private LinearLayout topView;
	private TextView ucoinDesc;
	private TextView ucoinValue;
	private Button chatBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		if(bundle != null)
			oid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_OID);
		loginUserVo = SystemContext.getInstance().getExtUserVo();
		init();
	}

	/**
	 * 
	 */
	private void init() {
		setTitleTxt("");
		View view = View.inflate(PlayOrderDetailActivity.this, R.layout.play_order_detail_info, null);
		addContentViewChild(view);
		topView = (LinearLayout) findViewById(R.id.topView);
		ucoinDesc = (TextView) findViewById(R.id.ucoinDesc);
		ucoinValue = (TextView) findViewById(R.id.ucoinValue);
		chatBtn = (Button) findViewById(R.id.chatBtn);
		getOrderDetail();
	}

	/**
	 * 
	 */
	private void getOrderDetail() {
		final CustomProgressDialog loadDialog = CustomProgressDialog.createDialog(this);
		loadDialog.show();
		ProxyFactory.getInstance().getPlayProxy().getOrderDetail(new ProxyCallBack<Msgs.PlayOrderInfo>() {

			@Override
			public void onSuccess(final PlayOrderInfo result) {
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				if (loginUserVo.getUserid() != result.getUid()) {
					setTitleTxt(result.getNickname());
					chatBtn.setVisibility(View.VISIBLE);
				} else {
					setTitleTxt("查看报名资料");
					chatBtn.setVisibility(View.GONE);
				}
				chatBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 对聊
						Intent intent = new Intent(PlayOrderDetailActivity.this, UserChatFragmentActivity.class);
						Bundle bundle = new Bundle();
						bundle.putLong(SystemConfig.BUNDLE_NAME_TOUSERID, result.getUid());
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						startActivity(intent);
					}
				});
				ucoinDesc.setText(result.getCost() + "U币/小时*1小时=");
				ucoinValue.setText(result.getCost() * result.getDuration() + "U币");
				// 其他详情设置
				LayoutInflater inflater = (LayoutInflater) PlayOrderDetailActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.play_order_detail_item, topView, false);
				((TextView) view.findViewById(R.id.keyTxt)).setText(Html.fromHtml("游戏"));
				((TextView) view.findViewById(R.id.valueTxt)).setText(result.getGamename());
				topView.addView(view);
				view = inflater.inflate(R.layout.play_order_detail_item, topView, false);
				((TextView) view.findViewById(R.id.keyTxt)).setText(Html.fromHtml("服务器"));
				((TextView) view.findViewById(R.id.valueTxt)).setText(result.getServername());
				topView.addView(view);
				view = inflater.inflate(R.layout.play_order_detail_item, topView, false);
				((TextView) view.findViewById(R.id.keyTxt)).setText(Html.fromHtml("角色名"));
				((TextView) view.findViewById(R.id.valueTxt)).setText(result.getRolename());
				topView.addView(view);
				view = inflater.inflate(R.layout.play_order_detail_item, topView, false);
				((TextView) view.findViewById(R.id.keyTxt)).setText(Html.fromHtml("预约时间"));
				((TextView) view.findViewById(R.id.valueTxt)).setText(new SimpleDateFormat("MM月dd日 HH:mm").format(new Date(result.getStarttime())));
				topView.addView(view);
				view = inflater.inflate(R.layout.play_order_detail_item, topView, false);
				((TextView) view.findViewById(R.id.keyTxt)).setText(Html.fromHtml("陪玩时长"));
				((TextView) view.findViewById(R.id.valueTxt)).setText(result.getDuration() + "小时");
				topView.addView(view);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				ToastUtil.showToast(PlayOrderDetailActivity.this, "加载订单详情失败");
			}
		}, this, oid);
	}

}
