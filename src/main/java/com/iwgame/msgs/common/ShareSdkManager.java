package com.iwgame.msgs.common;

import com.iwgame.utils.LogUtil;

import android.content.Context;
import android.graphics.Bitmap;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;


/**
 * ShareSdk分享 API接口调用类
 * @ClassName: ShareSdkManager 
 * @Description: ShareSdk分享
 * @author Administrator
 * @date 2014-11-11 下午2:14:22 
 * @Version 1.0
 *
 */
public class ShareSdkManager {
	private static final String TAG = "ShareSdkManager";

	private static byte[] lock = new byte[0];

	private static ShareSdkManager instance = null;
	
	private ShareSdkManager() {

	}

	public static ShareSdkManager getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new ShareSdkManager();
				return instance;
			}
		} else {
			return instance;
		}
	}
	
	/**
	 * 分享到微博（包括新浪微博和腾讯微博）
	 * @param weiboType 微博类型
	 * @param text 文字内容
	 * @param lat  地理坐标
	 * @param lon  地理坐标
	 * @param listener 回调接口
	 */
	public void shareToWeibo(String weiboType, String text,float lat, float lon, PlatformActionListener listener){
		//参数校验
		if(null == weiboType || weiboType.isEmpty() || null == text || text.isEmpty() || null == listener){
			return;
		}
		Platform weibo = ShareSDK.getPlatform(weiboType);
		weibo.setPlatformActionListener(listener); // 设置分享事件回调
		ShareParams sp = new ShareParams();
		sp.setText(text);
		if(lat != 0) sp.setLatitude(lat);//设置地理坐标
		if(lon != 0) sp.setLongitude(lon);
		weibo.share(sp);
	}
	
	/**
	 * 分享到微信好友或朋友圈
	 * @param weixinType 好友或朋友圈
	 * @param shareType 分享类型（文本/图片）
	 * @param title 标题
	 * @param text 内容
	 * @param imagePath 图片路径
	 * @param imageUrl  图片URL
	 * @param imageData 图片资源（Bitmap）
	 * @param listener 回调接口
	 */
	public void shareToWeixin(String weixinType, int shareType, String title, String text, String imagePath, String imageUrl, Bitmap imageData, String url, PlatformActionListener listener){
		//参数判断
		if(null == weixinType || weixinType.isEmpty()
				|| null == title || title.isEmpty()
				|| null == listener){
			return;
		}
		if(shareType == Platform.SHARE_TEXT || shareType == Platform.SHARE_WEBPAGE){
			if(null == text || text.isEmpty()) return;
		}
		if(shareType == Platform.SHARE_IMAGE || shareType == Platform.SHARE_WEBPAGE){
			if((null == imagePath || imagePath.isEmpty()) 
					&& (null == imageUrl || imageUrl.isEmpty()) 
					&& (null == imageData)){
				return;
			}
			if(shareType == Platform.SHARE_WEBPAGE){
				if(null == url || url.isEmpty()) return;
			}
		}
		
		Platform weixin = ShareSDK.getPlatform(weixinType);
		weixin.setPlatformActionListener(listener); // 设置分享事件回调
		ShareParams sp = new ShareParams();
		sp.setShareType(shareType);
		sp.setTitle(title);
		if(text != null && !text.isEmpty()){//设置文本内容
			if(shareType == Platform.SHARE_TEXT && url != null){//如果分享类型为文本且Url不为null，且拼接文本和Url组成行的文本
				sp.setText(text + url);
			}else{
				sp.setText(text);	
			}
		}
		//添加图片资源
		if(shareType == Platform.SHARE_IMAGE || shareType == Platform.SHARE_WEBPAGE ){
			if(imagePath != null && !imagePath.isEmpty()) sp.setImagePath(imagePath);
			if(imageUrl != null && !imageUrl.isEmpty()) sp.setImageUrl(imageUrl);
			if(imageData != null) sp.setImageData(imageData);
			if(shareType == Platform.SHARE_WEBPAGE) sp.setUrl(url);
		}
		weixin.share(sp);
	}
	
	/**
	 * 分享到QQ空间
	 * @param title 标题
	 * @param titleUrl 标题的超链接
	 * @param text 文本
	 * @param site 发布分享的网站名称
	 * @param siteUrl 发布分享的网站url
	 * @param imagePath 图片路径（本地路径）
	 * @param imageUrl 图片url（网络图片）
	 * @param listener 回调接口
	 */
	public void shareToQzone(String title, String titleUrl, String text, String site, String siteUrl, String imagePath, String imageUrl, PlatformActionListener listener){
		//参数判断
		if(null == title || title.isEmpty()
				|| null == titleUrl || titleUrl.isEmpty()
				|| null == text || text.isEmpty()
				|| null == site || site.isEmpty()
				|| null == siteUrl || siteUrl.isEmpty()
				|| null == listener){
			return;
		}
		Platform qzone = ShareSDK.getPlatform(QZone.NAME);
		qzone.setPlatformActionListener(listener); // 设置分享事件回调
		ShareParams sp = new ShareParams();
		sp.setTitle(title);
		sp.setTitleUrl(titleUrl);
		sp.setText(text);
		sp.setSite(site);
		sp.setSiteUrl(siteUrl);
		if(imagePath != null && !imagePath.isEmpty()) sp.setImagePath(imagePath);
		if(imageUrl != null && !imageUrl.isEmpty()) sp.setImageUrl(imageUrl);
		qzone.share(sp);
	}
	
	/**
	 * 分享到QQ好友
	 * @param title 标题
	 * @param titleUrl 标题的超链接
	 * @param text 文本内容
	 * @param imagePath 图片路径（本地路径）
	 * @param imageUrl 图片url（网络图片）
	 * @param listener 回调接口
	 */
	public void shareToQQ(String title, String titleUrl, String text, String imagePath, String imageUrl, PlatformActionListener listener){
		//参数判断
		if(null == title || title.isEmpty() || null == listener
				|| null == titleUrl || titleUrl.isEmpty()
				|| null == text || text.isEmpty()){
			return;
		}
		//此类型图片资源未必输字段，如果没有则不进行分享
		if((null == imagePath || imagePath.isEmpty()) 
				&& (null == imageUrl || imageUrl.isEmpty())){
			return;
		}
		
		Platform qq = ShareSDK.getPlatform(QQ.NAME);
		qq.setPlatformActionListener(listener); // 设置分享事件回调
		ShareParams sp = new ShareParams();
		sp.setTitle(title);
		sp.setTitleUrl(titleUrl);
		sp.setText(text);
		if(imagePath != null && !imagePath.isEmpty()) sp.setImagePath(imagePath);
		if(imageUrl != null && !imageUrl.isEmpty()) sp.setImageUrl(imageUrl);
		qq.share(sp);
	}
	
	/**
	 * 
	* @Title: getPlatform    
	* @Description: 获得单个第三方 句柄
	* @param @param context
	* @param @param authorizeType
	* @param @return    设定文件    
	* @return Platform    返回类型    
	* @throws
	 */
	public Platform getPlatform(Context context,String authorizeType){
		Platform plat= ShareSDK.getPlatform(context, authorizeType);
		return plat;
	}
	
	/**
	 * 授权或取消授权
	 * @param context 
	 * @param action 0授权;1取消授权
	 * @param authorizeType 要进行操作的平台（微博，微信等）
	 * @param listener 回调接口
	 */
	public void authorizeAction(Context context, int action,String authorizeType, PlatformActionListener listener,ShareCallbackListener callbackListener){
		Platform plat= ShareSDK.getPlatform(context, authorizeType);
		plat.setPlatformActionListener(listener);
		if(action == 0){//授权
			if(!plat.isValid()) plat.authorize();
		} else if(action == 1){//取消授权
			if(plat.isValid()){
				plat.removeAccount();
				ShareSDK.removeCookieOnAuthorize(true);
				callbackListener.doSuccess(authorizeType);
			}
		}
	}
	
	/**
	 * 返回是否认证
	 * @param context
	 * @param authorizeType 需判断是否认证的平台
	 * @return
	 */
	public boolean getAuthorizeStatus(Context context,String authorizeType){
		Platform plat= ShareSDK.getPlatform(context, authorizeType);
		return plat.isValid()?true:false;
	}
	
	/**
	 * 
	 * @param context
	 * @param authorizeType
	 * @return
	 */
	public String getTokoen(Context context,String authorizeType){
		Platform plat= ShareSDK.getPlatform(context, authorizeType);
		return plat.getDb().getToken();
	}
	
}
