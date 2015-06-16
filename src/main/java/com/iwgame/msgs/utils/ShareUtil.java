/**      
 * ShareUtil.java Create on 2014-5-5     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import com.iwgame.msgs.context.SystemContext;

/**
 * @ClassName: ShareUtil
 * @Description: 分享工具类
 * @author 王卫
 * @date 2014-5-5 下午3:45:27
 * @Version 1.0
 * 
 */
public class ShareUtil { 

	/** market **/
	
	public static final String MARKET_SINAWEIBO = "sinaweibo";
	public static final String MARKET_WEIXIN = "weixin";
	public static final String MARKET_PENGYOUQUAN = "pengyouquan";
	public static final String MARKET_TENCENTWEIBO = "tencentweibo";
	public static final String MARKET_QQ = "qq";
	public static final String MARKET_QZONE = "qzone";

	/** mark **/
	public static final String MARKET_TYPE_TIEBA = "tieba_post_more";
	public static final String MARKET_TYPE_USER = "user_info_more";

	public static final int TYPE_POST = 0;//帖子
	public static final int TYPE_USER = 1;//用户
	public static final int TYPE_GROUP = 2;//公会
	public static final int TYPE_GAME = 3;//贴吧
	public static final int TYPE_GOOD = 4;//商品
	public static final int TYPE_WEBPAGE = 5;//攻略网页

	public static final int TYPE_TARGET_DEFAULT = 0;
	public static final int TYPE_TARGET_WEIXIN = 0;//微信好友
	public static final int TYPE_TARGET_SINAWEIBO = 1;//新浪微博
	public static final int TYPE_TARGET_PENGYOUQUAN = 2;//朋友圈
	public static final int TYPE_TARGET_TENCENTWEIBO = 3;//腾讯微博
	public static final int TYPE_TARGET_QQ = 4;//QQ
	public static final int TYPE_TARGET_QZONE = 5;//空间
	
	public static final int SHARE_TYPE_SHAER = 0;//分享
	public static final int SHARE_TYPE_INVITE = 1;//邀请

	public static final String D_ANDROID = "a";

	/**
	 * 
	 * @param id
	 * @param platform
	 * @param market
	 * @param type
	 * @return
	 */
	public static String getShareUrl2(long id, String market, int type) {
		String tid = id + "";
		String baseUrl = null;
		String mark = null;
		if (type == TYPE_POST) {
			baseUrl = SystemContext.getInstance().getSharePostBaseUrl();
			mark = MARKET_TYPE_TIEBA;
		} else {
			baseUrl = SystemContext.getInstance().getSharePersonBaseUrl();
			mark = MARKET_TYPE_USER;
		}
		return baseUrl + tid + "?" + "d=" + D_ANDROID + "&mark=" + mark + "&market=" + market + "&t=" + System.currentTimeMillis();
	}

	/**
	 * 
	 * @param id
	 * @param platform
	 * @param market
	 * @param type
	 * @return
	 */
	@Deprecated
	public static String getShareUrl(long id, String market, int type) {
		if (type == TYPE_POST) {
			return SystemContext.getInstance().getSharePostBaseUrl() + id + "_a_tieba_post_more_weibo.html";
		} else if (type == TYPE_USER) {
			return SystemContext.getInstance().getSharePersonBaseUrl() + id + "_a_user_info_more_weibo.html";
		}
		return null;
	}

}
