/**      
 * ImageViewUtil.java Create on 2013-12-5     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import android.widget.ImageView;

import com.iwgame.msgs.common.ImageCacheLoader;

/**
 * @ClassName: ImageViewUtil
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-12-5 下午7:24:32
 * @Version 1.0
 * 
 */
public class ImageViewUtil {
	/**
	 * 加载图片
	 * 
	 * @param view
	 * @param resid
	 * @param defaultResid
	 */
	public static void showImage(ImageView view, String resid, int defaultResid) {
	    ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(resid), view, defaultResid, defaultResid, defaultResid, null, true);
	}
	/**
	 * 加载图片
	 * 
	 * @param view
	 * @param resid
	 * @param defaultResid
	 */
	public static void showMediumImage(ImageView view, String resid, int defaultResid) {
		ImageCacheLoader.getInstance().loadRes(ResUtil.getMediumRelUrl(resid), view, defaultResid, defaultResid, defaultResid, null, true);
	}

	/**
	 * 加载图片
	 * 
	 * @param view
	 * @param resid
	 * @param defaultResid
	 */
	public static void showBigImage(ImageView view, String resid, int defaultResid) {
		ImageCacheLoader.getInstance().loadRes(ResUtil.getOriginalRelUrl(resid), view, defaultResid, defaultResid, defaultResid, null, true);
	}
}
