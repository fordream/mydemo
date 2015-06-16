/**      
 * PayItemRelativeLayout.java Create on 2015-5-22     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.pay.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwgame.msgs.proto.Msgs.YoubiFaceValue;
import com.iwgame.msgs.proto.Msgs.YoubiSchemeResult;
import com.youban.msgs.R;

/**
 * @ClassName: PayItemRelativeLayout
 * @Description: 支付条目相对布局
 * @author 王卫
 * @date 2015-5-22 下午4:33:38
 * @Version 1.0
 * 
 */
public class PayItemRelativeLayout extends RelativeLayout {

	public static final int VIEW_MODE_COMMON = 0;
	public static final int VIEW_MODE_OTHER = 1;

	public TextView rmb_txt;
	public TextView ub_txt;
	public TextView give_ub_txt;
	public ImageView firstpay_icon;
	public ImageView act_icon;
	public ImageView give_icon;
	public TextView rmb_other_txt;

	private int showMode;
	private ClickListener clickListener;

	public interface ClickListener {
		public void onClickAction(PayItemRelativeLayout view);
	}

	/**
	 * @param context
	 */
	public PayItemRelativeLayout(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public PayItemRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PayItemRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 
	 */
	public void initView(Context context, YoubiSchemeResult result, YoubiFaceValue fvalue, int mode) {
		rmb_txt = (TextView) findViewById(R.id.rmb_txt);
		ub_txt = (TextView) findViewById(R.id.ub_txt);
		give_ub_txt = (TextView) findViewById(R.id.give_ub_txt);
		firstpay_icon = (ImageView) findViewById(R.id.firstpay_icon);
		act_icon = (ImageView) findViewById(R.id.act_icon);
		give_icon = (ImageView) findViewById(R.id.give_icon);
		rmb_other_txt = (TextView) findViewById(R.id.rmb_other_txt);
		if (result != null && fvalue != null) {
			// 面值
			rmb_txt.setText(fvalue.getFaceValue() + "");
			// 是否有比例变化（活动或首充）
			if (fvalue.getGiveAdded() > 0 || (fvalue.getRate() > 0 && fvalue.getRate() != result.getDefaultRate())) {
				if (fvalue.getSchemeType() == 1) {
					firstpay_icon.setVisibility(View.VISIBLE);
					act_icon.setVisibility(View.GONE);
				} else {
					firstpay_icon.setVisibility(View.GONE);
					act_icon.setVisibility(View.VISIBLE);
				}
			} else {
				firstpay_icon.setVisibility(View.GONE);
				act_icon.setVisibility(View.GONE);
			}
			// U币值
			if (fvalue.getRate() > 0 && result.getDefaultRate() != fvalue.getRate()) {
				ub_txt.setText("(" + fvalue.getFaceValue() * fvalue.getRate() + "U币)");
			} else {
				ub_txt.setText("(" + fvalue.getFaceValue() * result.getDefaultRate() + "U币)");
			}
			// 额外赠送
			if (fvalue.getGiveAdded() > 0) {
				give_ub_txt.setVisibility(View.VISIBLE);
				give_icon.setVisibility(View.VISIBLE);
				give_ub_txt.setText(fvalue.getGiveAdded() + "U币");
			} else {
				give_ub_txt.setVisibility(View.GONE);
				give_icon.setVisibility(View.GONE);
				give_ub_txt.setText("");
			}
		}
		setShowMode(mode);
	}

	/**
	 * 
	 * @param showMode
	 */
	private void setShowMode(int showMode) {
		this.showMode = showMode;
		if (showMode == VIEW_MODE_OTHER)
			rmb_other_txt.setVisibility(View.VISIBLE);
	}

	/**
	 * 
	 */
	public int getShowMode() {
		return showMode;
	}

}
