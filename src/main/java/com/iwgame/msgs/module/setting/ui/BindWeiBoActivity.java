package com.iwgame.msgs.module.setting.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.common.ShareSdkManager;
import com.iwgame.msgs.context.SystemContext;
import com.youban.msgs.R;



/**
 * 绑定微博帐号的界面
 * @author jczhang
 *
 */
public class BindWeiBoActivity extends BaseActivity implements OnClickListener{

	private LinearLayout sinaItem;
	private LinearLayout tencentItem;
	private ImageView sinaIcon;
	private ImageView tencentIcon;
	private TextView sinaBindState;
	private TextView tencentBindState;
	int action = -1;

	/**
	 * 当界面一启动的时候
	 * 就执行下面的这个方法 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	/**
	 * 初始化界面的方法 
	 */
	private void init() {
		titleTxt.setText("社交帐号绑定");
		View v = (LinearLayout)View.inflate(this, R.layout.bind_weibo_activity, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(v,params);
		sinaItem = (LinearLayout)findViewById(R.id.sina_weibo_item);
		sinaItem.setOnClickListener(this);
		tencentItem = (LinearLayout)findViewById(R.id.tencent_weibo_item);
		tencentItem.setOnClickListener(this);
		sinaIcon = (ImageView)findViewById(R.id.sina_weibo_icon);
		tencentIcon = (ImageView)findViewById(R.id.tencent_weibo_icon);
		sinaBindState = (TextView)findViewById(R.id.sina_bind_state);
		tencentBindState = (TextView)findViewById(R.id.tencent_bind_state);
		checkBindState(false);
	}

	/**
	 * 判断是否绑定成功
	 */
	private void checkBindState(boolean flag){
		if (ShareSdkManager.getInstance().getAuthorizeStatus(this, SinaWeibo.NAME)) {// 已绑定，操作为解除
			action = 1;
		} else {// 未绑定，操作为绑定
			action = 0;
		}
		modifyBindStateColor(sinaBindState, sinaIcon, action,flag);
		if (ShareSdkManager.getInstance().getAuthorizeStatus(this, TencentWeibo.NAME)) {// 已绑定，操作为解除
			action = 1;
		} else {// 未绑定，操作为绑定
			action = 0;
		}
		modifyBindStateColor(tencentBindState,tencentIcon, action,flag);
	}



	/**
	 * 当点击界面上面的按钮的时候
	 * 会执行下面的onclick方法 
	 */
	@Override
	public void onClick(View v) {
		if(v.getId() == sinaItem.getId()){//点击的是新浪微博
			if (ShareSdkManager.getInstance().getAuthorizeStatus(this, SinaWeibo.NAME)) {// 已绑定，操作为解除
				action = 1;
			} else {// 未绑定，操作为绑定
				action = 0;
			}
			ShareManager.getInstance().authorize(this, action, SinaWeibo.NAME, new ShareCallbackListener() {

				@Override
				public void doSuccess(String plamType) {
					if(action == 1)
						checkBindState(true);
					else
						checkBindState(false);
				}

				@Override
				public void doFail() {
					checkBindState(false);
				}
			});

		}else if(v.getId() == tencentItem.getId()){//点击的是腾讯微博
			if (ShareSdkManager.getInstance().getAuthorizeStatus(this, TencentWeibo.NAME)) {// 已绑定，操作为解除
				action = 1;
			} else {// 未绑定，操作为绑定
				action = 0;
			}
			ShareManager.getInstance().authorize(this, action, TencentWeibo.NAME, new ShareCallbackListener() {

				@Override
				public void doSuccess(String plamType) {
					if(action == 1)
						checkBindState(true);
					else
						checkBindState(false);
				}

				@Override
				public void doFail() {
					checkBindState(false);
				}
			});

		}
	}

	/**
	 * 修改是否已经被 绑定
	 * @param tv
	 * @param action
	 */
	private void modifyBindStateColor(TextView tv, ImageView iv, int action,boolean flag){
		if(action == 1){//如果已经绑定，执行下面的这个分支
			tv.setTextColor(getResources().getColor(R.color.bund_weibo_state_bund_text_color));
			tv.setText("已绑定");
			if(iv.getId() == sinaIcon.getId())
				iv.setBackgroundResource(R.drawable.setting_main_sina_nor);
			else if(iv.getId() == tencentIcon.getId())
				iv.setBackgroundResource(R.drawable.setting_main_tencent_nor);

		}else if(action == 0){//如果没有绑定，则执行下面的这个分支
			if(flag)
				Toast.makeText(SystemContext.getInstance().getContext(), "解绑成功", Toast.LENGTH_SHORT).show();
			tv.setTextColor(getResources().getColor(R.color.bund_weibo_state_nobund_text_color));
			tv.setText("未绑定");
			if(iv.getId() == sinaIcon.getId())
				iv.setBackgroundResource(R.drawable.setting_main_sina_un);
			else if(iv.getId() == tencentIcon.getId())
				iv.setBackgroundResource(R.drawable.setting_main_tencent_un);
		}
	}
}
