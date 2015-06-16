package com.iwgame.msgs.common;

import java.io.Serializable;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.chat.ui.ForwardingShareContentFragmentActivity;
import com.iwgame.msgs.utils.DialogUtil;
import com.iwgame.msgs.utils.ShareUtil;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.vo.local.GameNewDetailVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;


/**
 * 分享管理类
 * @ClassName: ShareManager 
 * @Description: 分享管理类
 * @author yhxu
 * @date 2014-12-15 下午5:01:42 
 * @Version 1.0
 *
 */
public class ShareManager implements Callback{
	private static final String TAG = "ShareManager";

	private static byte[] lock = new byte[0];

	private static ShareManager instance = null;

	private ShareManager() {

	}

//	public interface ShareCallbackListener{
//		public void doSuccess(String plamType);
//
//		public void doFail();
//	}

	public static ShareManager getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new ShareManager();
				return instance;
			}
		} else {
			return instance;
		}
	}

	/**
	 * 分享
	 * @param context 
	 * @param inflater
	 * @param object  要分享的对象
	 * @param shareDate 分享数据对象
	 * @param listener 分享成功回调
	 */
	public void share(final Context context,LayoutInflater inflater,final Object object, final ShareDate shareDate, final ShareCallbackListener listener){
		if(context == null || inflater == null || object == null || shareDate == null || listener == null) return;
		UMUtil.sendEvent(context, UMConfig.MSGS_EVENT_SHARE, null, null, String.valueOf(shareDate.getTargetId()), shareDate.getTargetName(), null);
		View contentView = inflater.inflate(R.layout.postbar_share_dialog_content, null);
		final Dialog dialog = DialogUtil.showDialog(context, R.layout.dialog, context.getString(R.string.postbar_topic_detail_share_dialog_title),
				contentView, 5, 15, 5, 15, null);
		// 好友 公会
		TextView postbar_topic_detail_share_dialog_item_youb = (TextView) contentView.findViewById(R.id.postbar_topic_detail_share_dialog_item_youb);
		postbar_topic_detail_share_dialog_item_youb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(shareDate.getTargetType() == ShareUtil.TYPE_WEBPAGE){
					ProxyFactory.getInstance().getUserProxy().shareWebPageForShortUrl(new ProxyCallBack<String>() {

						@Override
						public void onSuccess(String result) {
							String shortUrl = result;
							if(shortUrl == null || shortUrl.isEmpty()){
								dialog.dismiss();
								return;
							}
							listener.shortUrl = result;
							GameNewDetailVo vo = (GameNewDetailVo) object;
							vo.setLink(shortUrl);
							dialog.dismiss();
							Intent intent = new Intent(context, ForwardingShareContentFragmentActivity.class);
							Bundle bundle = new Bundle();
							bundle.putInt(SystemConfig.BUNDLE_NAME_CONTENT_ACTION_TYPE, SystemConfig.CONTENT_ACTION_TYPE_SHARE);
							bundle.putInt(SystemConfig.BUNDLE_NAME_CONTENT_TYPE, shareDate.getInTargetType());
							bundle.putSerializable(SystemConfig.BUNDLE_NAME_CONTENT, vo);
							intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
							context.startActivity(intent);
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							dialog.dismiss();
							ToastUtil.showToast(context, "分享失败，请稍后再试……");
							listener.doFail();
						}
					}, context, shareDate.getTargetId(), shareDate.getTargetType(), ShareUtil.TYPE_TARGET_SINAWEIBO);
				}else{
					dialog.dismiss();
					Intent intent = new Intent(context, ForwardingShareContentFragmentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.BUNDLE_NAME_CONTENT_ACTION_TYPE, SystemConfig.CONTENT_ACTION_TYPE_SHARE);
					bundle.putInt(SystemConfig.BUNDLE_NAME_CONTENT_TYPE, shareDate.getInTargetType());
					bundle.putSerializable(SystemConfig.BUNDLE_NAME_CONTENT, (Serializable) object);
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					context.startActivity(intent);
				}
			}
		});
		// 新浪微博
		TextView postbar_topic_detail_share_dialog_item_weibo = (TextView) contentView
				.findViewById(R.id.postbar_topic_detail_share_dialog_item_weibo);
		postbar_topic_detail_share_dialog_item_weibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				shareContent(context, shareDate, ShareUtil.MARKET_SINAWEIBO, shareDate.getTargetType(), ShareUtil.TYPE_TARGET_SINAWEIBO, listener);
				UMUtil.sendEvent(context, UMConfig.MSGS_EVENT_SHARE_WEIBO, null, null, String.valueOf(shareDate.getTargetId()),
						shareDate.getTargetName(), true);
			}
		});
		// 微信朋友圈
		TextView postbar_topic_detail_share_dialog_item_pengyouquan = (TextView) contentView
				.findViewById(R.id.postbar_topic_detail_share_dialog_item_pengyouquan);
		postbar_topic_detail_share_dialog_item_pengyouquan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				shareContent(context, shareDate, ShareUtil.MARKET_PENGYOUQUAN, shareDate.getTargetType(), ShareUtil.TYPE_TARGET_PENGYOUQUAN, listener);
				UMUtil.sendEvent(context, UMConfig.MSGS_EVENT_SHARE_WEIXIN_PY, null, null, String.valueOf(shareDate.getTargetId()),
						shareDate.getTargetName(), true);

			}
		});
		// 微信
		TextView postbar_topic_detail_share_dialog_item_weixin = (TextView) contentView
				.findViewById(R.id.postbar_topic_detail_share_dialog_item_weixin);
		postbar_topic_detail_share_dialog_item_weixin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				shareContent(context, shareDate, ShareUtil.MARKET_WEIXIN, shareDate.getTargetType(), ShareUtil.TYPE_TARGET_WEIXIN, listener);
				UMUtil.sendEvent(context, UMConfig.MSGS_EVENT_SHARE_WEIXIN, null, null, String.valueOf(shareDate.getTargetId()),
						shareDate.getTargetName(), true);

			}
		});
		// QQ空间
		TextView postbar_topic_detail_share_dialog_item_qqzone = (TextView) contentView
				.findViewById(R.id.postbar_topic_detail_share_dialog_item_qqzone);
		postbar_topic_detail_share_dialog_item_qqzone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				shareContent(context, shareDate, ShareUtil.MARKET_QZONE, shareDate.getTargetType(), ShareUtil.TYPE_TARGET_QZONE, listener);
				UMUtil.sendEvent(context, UMConfig.MSGS_EVENT_SHARE_TENCENT_ZONE, null, null, String.valueOf(shareDate.getTargetId()),
						shareDate.getTargetName(), true);
			}
		});
		// 腾讯微博
		TextView postbar_topic_detail_share_dialog_item_tencentweibo = (TextView) contentView
				.findViewById(R.id.postbar_topic_detail_share_dialog_item_tencentweibo);
		postbar_topic_detail_share_dialog_item_tencentweibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				shareContent(context, shareDate, ShareUtil.MARKET_TENCENTWEIBO, shareDate.getTargetType(), ShareUtil.TYPE_TARGET_TENCENTWEIBO, listener);
				UMUtil.sendEvent(context, UMConfig.MSGS_EVENT_SHARE_TENCENT_WEIBO, null, null, String.valueOf(shareDate.getTargetId()),
						shareDate.getTargetName(), true);
			}
		});
	}

	/**
	 * 分享
	 * @param context
	 * @param shareDate
	 * @param shareMarket 分享平台
	 * @param target 
	 * @param listener 成功回调
	 */
	public void shareContent(final Context context, final ShareDate shareDate, final String shareMarket, final int type, final int target, final ShareCallbackListener listener) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(context);
		dialog.show();
		if(shareDate.getTargetType() == ShareUtil.TYPE_WEBPAGE){//分享攻略网页
			ProxyFactory.getInstance().getUserProxy().shareWebPageForShortUrl(new ProxyCallBack<String>() {

				@Override
				public void onSuccess(String result) {
					String shortUrl = result;
					if(shortUrl == null || shortUrl.isEmpty()){
						dialog.dismiss();
						return;
					}
					shareDate.setShareUrl(shortUrl);
					selectShareMarket(context, shareDate, shareMarket, shortUrl, dialog, listener);
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					dialog.dismiss();
					ToastUtil.showToast(context, "分享失败，请稍后再试……");
					listener.doFail();
				}
			}, context, shareDate.getTargetId(), type, target);
		}else{//帖子,贴吧，公会资料，个人资料，商品详情
			//生成短链
			ProxyFactory.getInstance().getUserProxy().shareForShortUrl(new ProxyCallBack<String>() {
				@Override
				public void onSuccess(String result) {
					String shortUrl = result;
					if(shortUrl == null || shortUrl.isEmpty()){
						dialog.dismiss();
						return;
					}
					shareDate.setShareUrl(shortUrl);
					getShareText(context, shareDate);
					selectShareMarket(context, shareDate, shareMarket, shortUrl, dialog, listener);
				}
				@Override
				public void onFailure(Integer result, String resultMsg) {
					dialog.dismiss();
					ToastUtil.showToast(context, "分享失败，请稍后再试……");
					listener.doFail();
				}
			}, context, shareDate.getTargetId(), type, target, shareDate.getTagId());
		}
	}

	/**
	 * 选择分享平台
	 * @param context
	 * @param shareDate
	 * @param shareMarket
	 * @param shortUrl
	 * @param dialog
	 * @param listener
	 */
	private void selectShareMarket(Context context, ShareDate shareDate,String shareMarket, String shortUrl, CustomProgressDialog dialog, ShareCallbackListener listener){
		if (shortUrl != null && !shortUrl.isEmpty()) {
			if(!shareMarket.equals(ShareUtil.MARKET_QZONE)){
				dialog.dismiss();
				if(shareDate.getShareType() == ShareUtil.SHARE_TYPE_SHAER){
					ToastUtil.showToast(context, "分享正在进行中……");
				}else{
					ToastUtil.showToast(context, "邀请正在进行中……");
				}
			}

			if (shareMarket.equals(ShareUtil.MARKET_SINAWEIBO)) {// 分享到微博
				shareToWeibo(context, shareDate, SinaWeibo.NAME, listener);
			} else if(shareMarket.equals(ShareUtil.MARKET_TENCENTWEIBO)){//腾讯微博
				shareToWeibo(context, shareDate, TencentWeibo.NAME, listener);
			} else if(shareMarket.equals(ShareUtil.MARKET_WEIXIN)){//微信好友
				shareToWinxin(context, shareDate, Wechat.NAME, listener);
			} else if(shareMarket.equals(ShareUtil.MARKET_PENGYOUQUAN)){//朋友圈
				shareToWinxin(context, shareDate, WechatMoments.NAME, listener);
			} else if(shareMarket.equals(ShareUtil.MARKET_QZONE)){//QQ空间
				shareToQzone(context, shareDate, listener, dialog);
			} else if(shareMarket.equals(ShareUtil.MARKET_QQ)){//QQ好友
				shareToQQ(context, shareDate, listener);
			} else {
				ToastUtil.showToast(context, "分享平台不存在!");
			}
		}
	}

	/**
	 * 获取分享内容
	 * @param shareDate 分享对象
	 */
	private void getShareText(Context context, ShareDate shareDate){
		String text = "";
		int targetType = shareDate.getTargetType();
		if(targetType == ShareUtil.TYPE_POST){// 帖子
			text = shareDate.getTargetName();
		}else if(targetType == ShareUtil.TYPE_USER){//个人资料
			if(shareDate.getDetailType() == 0){//自己
				text = context.getResources().getString(R.string.postbar_topic_share_user_content,"我", String.valueOf(shareDate.getTargerSerialId()));
			}else if(shareDate.getDetailType() == 1){//他人资料（男）
				text = context.getResources().getString(R.string.postbar_topic_share_user_content,"他", String.valueOf(shareDate.getTargerSerialId()));
			}else if(shareDate.getDetailType() == 2){//他人资料（女）
				text = context.getResources().getString(R.string.postbar_topic_share_user_content,"她", String.valueOf(shareDate.getTargerSerialId()));
			}else if(shareDate.getDetailType() == 3){//游客
				text = context.getResources().getString(R.string.postbar_topic_share_user_content,"TA", String.valueOf(shareDate.getTargerSerialId()));
			}
		}else if(targetType == ShareUtil.TYPE_GROUP){//公会资料
			text = context.getResources().getString(R.string.postbar_topic_share_group_content, String.valueOf(shareDate.getTargerSerialId()));
		}else if(targetType == ShareUtil.TYPE_GAME){//贴吧
			text = context.getResources().getString(R.string.postbar_topic_share_game_content, shareDate.getTargetName());
		}else if(targetType == ShareUtil.TYPE_GOOD){//商品
			text = context.getResources().getString(R.string.postbar_topic_share_goods_content, shareDate.getTargetName());
		}
		shareDate.setText(text);
	}

	/**
	 * 分享到新浪微博
	 * @param shareDate
	 * @param weiboType  腾讯微博/新浪微博
	 * @param listener
	 */
	private void shareToWeibo(final Context context, final ShareDate shareDate, final String weiboType, final ShareCallbackListener listener) {
		String tmpcontent = shareDate.getText() + shareDate.getShareUrl();
		ShareSdkManager.getInstance().shareToWeibo(weiboType, tmpcontent, (float)0, (float)0, new PlatformActionListener() {

			@Override
			public void onError(Platform platform, int action, Throwable t) {
				t.printStackTrace();
				sendShareResult(context, SystemConfig.SHARE_RESULT_ERROR, action, shareDate.getShareType(), platform, weiboType, t, listener);
			}

			@Override
			public void onComplete(Platform platform, int action, HashMap<String, Object> arg2) {
				sendShareResult(context,SystemConfig.SHARE_RESULT_SUCCESS,action,shareDate.getShareType(),platform,weiboType,null,listener);
			}

			@Override
			public void onCancel(Platform platform, int action) {
				sendShareResult(context,SystemConfig.SHARE_RESULT_CANCLE,action,shareDate.getShareType(),platform,weiboType,null,listener);
			}
		});
	}

	/**
	 * 分享到微信（微信好友和朋友圈）
	 * @param shareDate
	 * @param weixinType  微信好友和朋友圈
	 * @param listener
	 */
	private void shareToWinxin(final Context context, final ShareDate shareDate, final String weixinType, final ShareCallbackListener listener) {
		String content = shareDate.getText();
		String title = "";
		if(shareDate.getTargetType() == ShareUtil.TYPE_WEBPAGE){//攻略网页分享
			title = shareDate.getTitle();
		}else{//其他分享
			if(Wechat.NAME.equals(weixinType)){
				//title = context.getResources().getString(R.string.postbar_topic_share_toweixin_title);
				title = shareDate.getTitle();
				if(title == null || title.isEmpty()){
					title = AdaptiveAppContext.getInstance().getAppConfig().getShare_tilte_weixin();
				}
				if(shareDate.getShareType() == ShareUtil.SHARE_TYPE_INVITE){
					title = AdaptiveAppContext.getInstance().getAppConfig().getShare_tilte_weixin();
				}
			}else{
				title = content;
			}
		}

		String shareUrl = shareDate.getShareUrl();
		// 获得图片地址
		String imagePath = null;
		String imageUrl = shareDate.getImageUrl();
		Bitmap bitmap = shareDate.getImageData();
		int shareType = -1;
		if((imagePath == null || imagePath.isEmpty()) && (imageUrl == null || imageUrl.isEmpty()) && bitmap == null){
			shareType = Platform.SHARE_TEXT;//文本
		}else{
			shareType = Platform.SHARE_WEBPAGE;//网页
		}

		ShareSdkManager.getInstance().shareToWeixin(weixinType, shareType, title, content, imagePath, imageUrl, bitmap, shareUrl,new PlatformActionListener() {
			@Override
			public void onError(Platform platform, int action, Throwable t) {
				t.printStackTrace();
				sendShareResult(context,SystemConfig.SHARE_RESULT_ERROR,action,shareDate.getShareType(),platform,weixinType,t,listener);
			}

			@Override
			public void onComplete(Platform platform, int action, HashMap<String, Object> arg2) {
				sendShareResult(context,SystemConfig.SHARE_RESULT_SUCCESS,action,shareDate.getShareType(),platform,weixinType,null,listener);
			}

			@Override
			public void onCancel(Platform platform, int action) {
				sendShareResult(context,SystemConfig.SHARE_RESULT_CANCLE,action,shareDate.getShareType(),platform,weixinType,null,listener);
			}
		});
		
	}

	/**
	 * 分享到QQ空间
	 * 
	 * @param shareDate
	 * @param listener
	 */
	private void shareToQzone(final Context context, final ShareDate shareDate, final ShareCallbackListener listener, final CustomProgressDialog dialog) {
		String temptitle = null;
		if(shareDate.getTargetType() == ShareUtil.TYPE_WEBPAGE){//攻略网页分享
			temptitle = shareDate.getTitle();
		}else{//其他分享
			temptitle = AdaptiveAppContext.getInstance().getAppConfig().getShare_title_qzone();
			//temptitle = context.getResources().getString(R.string.postbar_topic_share_share_title);	
		}
		final String title = temptitle;
		final String content = shareDate.getText();
		final String shareUrl = shareDate.getShareUrl();
		final String site = AdaptiveAppContext.getInstance().getAppConfig().getShare_site_text();
		final String siteUrl = AdaptiveAppContext.getInstance().getAppConfig().getShare_site_url();
		// 获得图片地址
		final String imagePath = shareDate.getImagePath();

		/**
		 * 保存图片到本地，并返回图片的存储路径
		 * QQ空间分享时不支持部分网络图片，故先下载到本地，再通过本地的Url去实现
		 */
		ProxyFactory.getInstance().getUserProxy().getImageUrlToPath(new ProxyCallBack<String>() {

			@Override
			public void onSuccess(String result) {
				if(dialog != null){
					dialog.dismiss();
				}

				if(result != null && !result.isEmpty()){
					shareToQZone(context, shareDate, title, shareUrl, content, site, siteUrl, result, null, listener);
				}else{
					shareToQZone(context, shareDate, title, shareUrl, content, site, siteUrl, null, shareDate.getImageUrl(), listener);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if(dialog != null){
					dialog.dismiss();
				}
				shareToQZone(context, shareDate, title, shareUrl, content, site, siteUrl, null, shareDate.getImageUrl(), listener);
			}
		}, context, imagePath);
	}


	/**
	 * 分享到QQ空间
	 * @param context
	 * @param shareDate
	 * @param title
	 * @param shareUrl
	 * @param text
	 * @param site
	 * @param siteUrl
	 * @param imagePath
	 * @param imageUrl
	 * @param listener
	 */
	private void shareToQZone(final Context context, final ShareDate shareDate, String title, String shareUrl, String text, String site, String siteUrl, String imagePath, String imageUrl, final ShareCallbackListener listener){
		ToastUtil.showToast(context, "分享正在进行中……");
		ShareSdkManager.getInstance().shareToQzone(title, shareUrl, text, site, siteUrl, imagePath, imageUrl, new PlatformActionListener() {
			@Override
			public void onError(Platform platform, int action, Throwable t) {
				t.printStackTrace();
				sendShareResult(context,SystemConfig.SHARE_RESULT_ERROR,action,shareDate.getShareType(),platform,QZone.NAME,t,listener);
			}

			@Override
			public void onComplete(Platform platform, int action, HashMap<String, Object> arg2) {
				sendShareResult(context,SystemConfig.SHARE_RESULT_SUCCESS,action,shareDate.getShareType(),platform,QZone.NAME,null,listener);
			}

			@Override
			public void onCancel(Platform platform, int action) {
				sendShareResult(context,SystemConfig.SHARE_RESULT_CANCLE,action,shareDate.getShareType(),platform,QZone.NAME,null,listener);
			}
		});
	}

	/**
	 * 分享到QQ
	 * @param shareDate
	 * @param listener
	 */
	private void shareToQQ(final Context context, final ShareDate shareDate, final ShareCallbackListener listener) {
		String title = shareDate.getTitle();
		if(title == null || title.isEmpty()){
			title = AdaptiveAppContext.getInstance().getAppConfig().getShare_title_addqqfriend();
			//title = context.getResources().getString(R.string.postbar_topic_share_add_qqfriend_title);
		}
		String content = shareDate.getText();
		String titleUrl = shareDate.getShareUrl();
		// 获得图片地址
		String imagePath = shareDate.getImagePath();
		String imageUrl = shareDate.getImageUrl();

		ShareSdkManager.getInstance().shareToQQ(title, titleUrl, content, imagePath, imageUrl, new PlatformActionListener() {
			@Override
			public void onError(Platform platform, int action, Throwable t) {
				t.printStackTrace();
				sendShareResult(context,SystemConfig.SHARE_RESULT_ERROR,action,shareDate.getShareType(),platform,QQ.NAME,t,listener);
			}

			@Override
			public void onComplete(Platform platform, int action, HashMap<String, Object> arg2) {
				sendShareResult(context,SystemConfig.SHARE_RESULT_SUCCESS,action,shareDate.getShareType(),platform,QQ.NAME,null,listener);
			}

			@Override
			public void onCancel(Platform platform, int action) {
				sendShareResult(context,SystemConfig.SHARE_RESULT_CANCLE,action,shareDate.getShareType(),platform,QQ.NAME,null,listener);
			}
		});
	}

	/**
	 * 处理消息
	 */
	@Override
	public boolean handleMessage(Message msg) {
		HandlerData handlerData = null;
		if(msg.obj instanceof HandlerData){
			handlerData = (HandlerData)msg.obj;
		}
		if(handlerData == null) return false;
		Context context = handlerData.getContext();
		ShareCallbackListener listener = handlerData.getCallbackListener();
		String plamType = handlerData.getPlamType();
		Throwable t = handlerData.getT();

		String text = actionToString(msg.arg2);
		switch (msg.arg1) {
		case 1: {
			// 成功
			if(msg.what == 0){
				if(SinaWeibo.NAME.equals(plamType) || TencentWeibo.NAME.equals(plamType)){//微博类分享
					text = "分享成功，如改过微博密码需重新绑定才能显示分享内容哦！";
				}else{
					text = "分享成功";
				}
			}else if(msg.what == 1){
				text = "邀请成功";
			}else if(msg.what == 2){
				text = "绑定成功";
			}else{
				text = "解绑成功";
			}
			listener.doSuccess(plamType);
		}
		break;
		case 2: {
			// 失败
			if(t != null){
				if ("WechatClientNotExistException".equals(t.getClass().getSimpleName())) {
					text = context.getString(R.string.wechat_client_inavailable);
				}
				else if ("WechatTimelineNotSupportedException".equals(t.getClass().getSimpleName())) {
					text = context.getString(R.string.wechat_client_inavailable);
				}else {
					if(msg.what == 0){
						text = context.getString(R.string.share_failed);
					}else if(msg.what == 1){
						text = "邀请失败";
					}else if(msg.what == 2){
						text = "绑定失败";
					}else{
						text = "解绑失败";
					}
				}
			}
			listener.doFail();
		}
		break;
		case 3: {
			// 取消
			if(msg.what == 0){
				text = "取消分享";
			}else if(msg.what == 1){
				text = "取消邀请";
			}else if(msg.what == 2){
				text = "取消授权";
			}else{
				text = "取消解绑";
			}
			listener.doFail();
		}
		break;
		}

		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		return false;
	}

	/**
	 * 行为动作转换
	 * @param action
	 * @return
	 */
	public String actionToString(int action) {
		switch (action) {
		case Platform.ACTION_AUTHORIZING: return "ACTION_AUTHORIZING";
		case Platform.ACTION_GETTING_FRIEND_LIST: return "ACTION_GETTING_FRIEND_LIST";
		case Platform.ACTION_FOLLOWING_USER: return "ACTION_FOLLOWING_USER";
		case Platform.ACTION_SENDING_DIRECT_MESSAGE: return "ACTION_SENDING_DIRECT_MESSAGE";
		case Platform.ACTION_TIMELINE: return "ACTION_TIMELINE";
		case Platform.ACTION_USER_INFOR: return "ACTION_USER_INFOR";
		case Platform.ACTION_SHARE: return "ACTION_SHARE";
		default: {
			return "UNKNOWN";
		}
		}
	}


	/**
	 * 授权或取消授权
	 * @param context 
	 * @param action 0授权;1取消授权
	 * @param authorizeType 要进行操作的平台（微博，微信等）
	 * @param listener 回调接口
	 */
	public void authorize(final Context context,final int authAction,final String plamType, final ShareCallbackListener listener){
		ShareSdkManager.getInstance().authorizeAction(context, authAction, plamType, new PlatformActionListener() {

			@Override
			public void onError(Platform platform, int action, Throwable t) {
				t.printStackTrace();				
				int what = (authAction == 0)? 2 : 3;//2为授权微博；3为解绑
				sendShareResult(context,SystemConfig.SHARE_RESULT_ERROR,action,what,platform,plamType,t,listener);
			}

			@Override
			public void onComplete(Platform platform, int action, HashMap<String, Object> arg2) {
				int what = (authAction == 0)? 2 : 3;//2为授权微博；3为解绑
				sendShareResult(context,SystemConfig.SHARE_RESULT_SUCCESS,action,what,platform,plamType,null,listener);
			}

			@Override
			public void onCancel(Platform platform, int action) {
				int what = (authAction == 0)? 2 : 3;//2为授权微博；3为解绑
				sendShareResult(context,SystemConfig.SHARE_RESULT_CANCLE,action,what,platform,plamType,null,listener);
			}
		},listener);
	}

	/**
	 * 发送分享结果
	 * @param context 
	 * @param result 结果
	 * @param action 行为	
	 * @param what 操作类型 分享/邀请/授权微博/解除绑定授权
	 * @param platform
	 * @param plamType
	 * @param t
	 * @param listener
	 */
	private void sendShareResult(Context context, int result, int action, int what, Platform platform, String plamType, Throwable t, ShareCallbackListener listener){
		Message msg = new Message();
		msg.arg1 = result;
		msg.arg2 = action;
		msg.what = what;
		HandlerData handlerData = new HandlerData(context, listener, plamType);
		handlerData.setPlatform(platform);
		if(t != null){
			handlerData.setT(t);
		}
		msg.obj = handlerData;
		UIHandler.sendMessage(msg, ShareManager.this);
	}

	/**
	 * 发送到Handler的集合
	 * @ClassName: HandlerData 
	 * @Description: 发送到Handler的集合
	 * @author Administrator
	 * @date 2014-11-11 下午12:43:52 
	 * @Version 1.0
	 *
	 */
	class HandlerData{
		private Context context;//上下文
		private ShareCallbackListener callbackListener;//分享外部回调接口
		private String plamType;//分享平台
		private Platform platform;//平台对象
		private Throwable t;//错误消息对象

		public HandlerData(Context context, ShareCallbackListener callbackListener, String plamType) {
			this.context = context;
			this.callbackListener = callbackListener;
			this.plamType = plamType;
		}

		public Context getContext() {
			return context;
		}
		public void setContext(Context context) {
			this.context = context;
		}
		public ShareCallbackListener getCallbackListener() {
			return callbackListener;
		}
		public void setCallbackListener(ShareCallbackListener callbackListener) {
			this.callbackListener = callbackListener;
		}
		public String getPlamType() {
			return plamType;
		}
		public void setPlamType(String plamType) {
			this.plamType = plamType;
		}

		public Platform getPlatform() {
			return platform;
		}

		public void setPlatform(Platform platform) {
			this.platform = platform;
		}

		public Throwable getT() {
			return t;
		}

		public void setT(Throwable t) {
			this.t = t;
		}
	}
}
