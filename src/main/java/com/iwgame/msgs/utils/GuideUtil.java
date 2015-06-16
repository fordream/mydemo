/**      
 * GuideUtil.java Create on 2014-5-29     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.guide.GuideActivity;
import com.iwgame.msgs.module.user.ui.UserFragment;

/**
 * @ClassName: GuideUtil
 * @Description: TODO(...)
 * @author 王卫
 * @date 2014-5-29 下午12:01:24
 * @Version 1.0
 * 
 */
public class GuideUtil {

	public static void startGuide(Context context, int mode) {
		if (context == null)
			return;
		switch (mode) {
		case GuideActivity.GUIDE_MODE_USER_FOLLOW:
			if (!SystemContext.getInstance().getGuidUserFollow() && SystemContext.getInstance().isnotShowGuide()) {
					startGuideActivity(context, mode);
					SystemContext.getInstance().setGuidUserFollow(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_GROUP_CHAT:
			if (!SystemContext.getInstance().getGuidChatGroup() && SystemContext.getInstance().isnotShowGuide()) {
				startGuideActivity(context, mode);
				SystemContext.getInstance().setGuidChatGroup(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_CONTRACT_FOLLOW:
			if (!SystemContext.getInstance().getGuidFollowContact() && SystemContext.getInstance().isnotShowGuide()) {
				startGuideActivity(context, mode);
				SystemContext.getInstance().setGuidFollowContact(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_CONTRACT_INVITE:
			if (!SystemContext.getInstance().getGuidInivteContact() && SystemContext.getInstance().isnotShowGuide()) {
				startGuideActivity(context, mode);
				SystemContext.getInstance().setGuidInivteContact(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_CREAT_GROUP:
			return;
		case GuideActivity.GUIDE_MODE_DISCOVER_GROUP:
			if (!SystemContext.getInstance().getGuideDiscover() && SystemContext.getInstance().isnotShowGuide()) {
				startGuideActivity(context, mode);
				SystemContext.getInstance().setGuideDiscover(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_FOLLOW_GAME:
			if (!SystemContext.getInstance().getGuidGame() && SystemContext.getInstance().isnotShowGuide()) {
				startGuideActivity(context, mode);
				SystemContext.getInstance().setGuidGame(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_MESSAGE:
			if (!SystemContext.getInstance().getGuidMessage() && SystemContext.getInstance().isnotShowGuide()) {
				startGuideActivity(context, mode);
				SystemContext.getInstance().setGuidMessage(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_POST_SHARE:
			if (!SystemContext.getInstance().getGuidPostShare() && SystemContext.getInstance().isnotShowGuide()) {
				startGuideActivity(context, mode);
				SystemContext.getInstance().setGuidPostShare(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_POST:
			if (!SystemContext.getInstance().getGuidPost() && SystemContext.getInstance().isnotShowGuide()) {
				startGuideActivity(context, mode);
				SystemContext.getInstance().setGuidPost(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_USER_GROUP:
			if (!SystemContext.getInstance().getGuidUserGroup() && SystemContext.getInstance().isnotShowGuide()) {
				startGuideActivity(context, mode);
				SystemContext.getInstance().setGuidUserGroup(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_USER_DETAIL:
			if (!SystemContext.getInstance().getGuidUserDetail() && SystemContext.getInstance().isnotShowGuide()) {
				startGuideActivity(context, mode);
				SystemContext.getInstance().setGuidUserDetail(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_WEIBO_FOLLOW:
			if (!SystemContext.getInstance().getGuidWeiboFollow() && SystemContext.getInstance().isnotShowGuide()) {
				startGuideActivity(context, mode);
				SystemContext.getInstance().setGuidWeiboFollow(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_GROUP_POINT:
			if (!SystemContext.getInstance().getGuidGroupPoint() && SystemContext.getInstance().isnotShowGuide()) {
				startGuideActivity(context, mode);
				SystemContext.getInstance().setGuidGroupPoint(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_POINT_TASK:
			if (!SystemContext.getInstance().getGuidPointTask() && SystemContext.getInstance().isnotShowGuide()) {
				startGuideActivity(context, mode);
				SystemContext.getInstance().setGuidPointTask(true);
			}
			break;
		case GuideActivity.GUIDE_MODE_SETTINGFRAGMENT_YES:
				startGuideActivity(context, mode);
			break;
		case GuideActivity.GUIDE_MODE_SETTINGFRAGMENT_NO:
			startGuideActivity(context, mode);
			break;
		default:
			break;
		}
	}

	private static void startGuideActivity(Context context, int mode) {
		if (context == null)
			return;
		Intent intent = new Intent(context, GuideActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, mode);
		intent.putExtras(bundle);
		if(mode == GuideActivity.GUIDE_MODE_SETTINGFRAGMENT_NO || mode == GuideActivity.GUIDE_MODE_SETTINGFRAGMENT_YES)
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

}
