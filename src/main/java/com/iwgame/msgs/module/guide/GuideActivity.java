/**      
 * GuideActivity.java Create on 2014-2-8     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.guide;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: GuideActivity
 * @Description: 引导
 * @author 王卫
 * @date 2014-2-8 上午11:32:04
 * @Version 1.0
 * 
 */
public class GuideActivity extends BaseSuperActivity {

	// 引导图片
	private ImageView mGuideView;
	private static final String TAG = "GuideActivity";
	public static final int GUIDE_MODE_USER_FOLLOW = 1;
	public static final int GUIDE_MODE_GROUP_CHAT = 2;
	public static final int GUIDE_MODE_CONTRACT_FOLLOW = 3;
	public static final int GUIDE_MODE_CONTRACT_INVITE = 4;
	public static final int GUIDE_MODE_CREAT_GROUP = 5;
	public static final int GUIDE_MODE_DISCOVER_GROUP = 6;
	public static final int GUIDE_MODE_FOLLOW_GAME = 7;
	public static final int GUIDE_MODE_MESSAGE = 8;
	public static final int GUIDE_MODE_POST_SHARE = 9;
	public static final int GUIDE_MODE_POST = 10;
	public static final int GUIDE_MODE_USER_GROUP = 11;
	public static final int GUIDE_MODE_USER_DETAIL = 12;
	public static final int GUIDE_MODE_WEIBO_FOLLOW = 14;
	public static final int GUIDE_MODE_WEIBO_INVITE = 15;
	public static final int GUIDE_MODE_GROUP_POINT = 16;
	public static final int GUIDE_MODE_POINT_TASK = 17;
    public static final int GUIDE_MODE_SETTINGFRAGMENT_YES = 18;//表示的是在 我  这个页面是有相册图片的
    public static final int GUIDE_MODE_SETTINGFRAGMENT_NO = 19;//表示的是在 我这个页面是没有相册图片的
	
	
	public static GuideActivity instance;
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.guide);
		mGuideView = (ImageView) findViewById(R.id.guideView);
		mGuideView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Bundle bundle = getIntent().getExtras();
		int mode = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE);
		if (mode != 0) {
			addView(mode);
		} else {
			finish();
		}
	}

	/**
	 * 获取到实例
	 * @return
	 */
	public static GuideActivity getInstance(){
		return instance;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	
	@Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	MobclickAgent.onPageStart(TAG);
    	MobclickAgent.onResume(this);
    	
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	MobclickAgent.onPageEnd(TAG);
    	MobclickAgent.onPause(this);
    }

	/**
	 * 添加图片
	 * 
	 * @param mode
	 */
	private void addView(int mode) {
		switch (mode) {
		case GUIDE_MODE_USER_FOLLOW:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_user_follow);
			
			break;
		case GUIDE_MODE_GROUP_CHAT:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_chat);
			break;
		case GUIDE_MODE_CONTRACT_FOLLOW:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_contract_follow);
			break;
		case GUIDE_MODE_CONTRACT_INVITE:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_contract_invite);
			break;
		case GUIDE_MODE_CREAT_GROUP:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_creat_group);
			break;
		case GUIDE_MODE_DISCOVER_GROUP:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_discover_group);
			break;
		case GUIDE_MODE_FOLLOW_GAME:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_follow_game);
			break;
		case GUIDE_MODE_MESSAGE:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_message);
			break;
		case GUIDE_MODE_POST_SHARE:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_post_share);
			break;
		case GUIDE_MODE_POST:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_post);
			break;
		case GUIDE_MODE_USER_GROUP:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_user_add);
			break;
		case GUIDE_MODE_USER_DETAIL:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_user_detail);
			break;
		case GUIDE_MODE_WEIBO_FOLLOW:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_weibo_follow);
			break;
		case GUIDE_MODE_WEIBO_INVITE:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_weibo_invite);
			break;
		case GUIDE_MODE_GROUP_POINT:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_group_point);
			break;
		case GUIDE_MODE_POINT_TASK:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_point_task);
			break;
		case GUIDE_MODE_SETTINGFRAGMENT_YES:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_user_detail);
			break;
		case GUIDE_MODE_SETTINGFRAGMENT_NO:
			mGuideView.setBackgroundResource(R.drawable.guide_mode_user_detail2);
			break;
		default:
			break;
		}
	}

}
