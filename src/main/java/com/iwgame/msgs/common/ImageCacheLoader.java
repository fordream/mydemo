/**      
 * ImageCacheLoader.java Create on 2014-7-9     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import cn.trinea.android.common.entity.FailedReason;
import cn.trinea.android.common.service.impl.ImageCache;
import cn.trinea.android.common.service.impl.ImageMemoryCache.OnImageCallbackListener;
import cn.trinea.android.common.service.impl.RemoveTypeLastUsedTimeFirst;
import cn.trinea.android.common.util.ObjectUtils;

import com.iwgame.msgs.common.imageloader.ImageLoadingProgressListener;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: ImageCacheLoader
 * @Description: TODO(Image cache 的图片加载器)
 * @author chuanglong
 * @date 2014-7-9 上午11:23:31
 * @Version 1.0
 * 
 */
public class ImageCacheLoader {

	private static volatile ImageCacheLoader instance;

	public static ImageCacheLoader getInstance() {
		if (instance == null) {
			synchronized (ImageCacheLoader.class) {
				if (instance == null) {
					instance = new ImageCacheLoader();
				}
			}
		}
		return instance;
	}
	
	private static final Map<String, Integer> resIdMap = new HashMap<String, Integer>();

	/** icon cache **/
	private static final ImageCache IMAGE_CACHE = new ImageCache(256, 768);

	static {
		/** init icon cache **/
		OnImageCallbackListener imageCallBack = new OnImageCallbackListener() {

			/**
			 * callback function after get image successfully, run on ui thread
			 * 
			 * @param imageUrl
			 *            imageUrl
			 * @param loadedImage
			 *            bitmap
			 * @param view
			 *            view need the image
			 * @param isInCache
			 *            whether already in cache or got realtime
			 */
			@Override
			public void onGetSuccess(String imageUrl, Bitmap loadedImage, View view, boolean isInCache) {
				// if (view != null && loadedImage != null) {
				// ImageView imageView = (ImageView)view;
				// imageView.setImageBitmap(loadedImage);
				// // first time show with animation
				// if (!isInCache) {
				// imageView.startAnimation(getInAlphaAnimation(2000));
				// }
				//
				// // auto set height accroding to rate between height and
				// weight
				// LayoutParams imageParams =
				// (LayoutParams)imageView.getLayoutParams();
				// imageParams.height = imageParams.width *
				// loadedImage.getHeight() / loadedImage.getWidth();
				// imageView.setScaleType(ScaleType.FIT_XY);
				// }
				if (view != null && loadedImage != null) {
					ImageView imageView = (ImageView) view;
					// add tag judge, avoid listView cache and so on
					String imageUrlTag = (String) imageView.getTag();
					if (ObjectUtils.isEquals(imageUrlTag, imageUrl)) {
						imageView.setImageBitmap(loadedImage);
					}
				}

			}

			/**
			 * callback function before get image, run on ui thread
			 * 
			 * @param imageUrl
			 *            imageUrl
			 * @param view
			 *            view need the image
			 */
			@Override
			public void onPreGet(String imageUrl, View view) {
				// Log.e(TAG_CACHE, "pre get image");
			}

			/**
			 * callback function after get image failed, run on ui thread
			 * 
			 * @param imageUrl
			 *            imageUrl
			 * @param loadedImage
			 *            bitmap
			 * @param view
			 *            view need the image
			 * @param failedReason
			 *            failed reason for get image
			 */
			@Override
			public void onGetFailed(String imageUrl, Bitmap loadedImage, View view, FailedReason failedReason) {
				LogUtil.error(new StringBuilder(128).append("get image ").append(imageUrl).append(" error, failed type is: ")
						.append(failedReason.getFailedType()).append(", failed reason is: ").append(failedReason.getCause().getMessage()).toString());
				if(view instanceof ImageView){
					if(resIdMap.containsKey(imageUrl)){
						int resid = resIdMap.get(imageUrl);
						if(resid > 0){
							new ImageLoader().loadRes(imageUrl, 0, (ImageView)view, resid);
						}
						resIdMap.remove(imageUrl);
					}
				}
			}

			@Override
			public void onGetNotInCache(String imageUrl, View view) {
				// if (view != null && view instanceof ImageView) {
				// ((ImageView)view).setImageResource(R.drawable.common_default_icon);
				// }
			}
		};
		IMAGE_CACHE.setOnImageCallbackListener(imageCallBack);
		IMAGE_CACHE.setCacheFullRemoveType(new RemoveTypeLastUsedTimeFirst<Bitmap>());

		IMAGE_CACHE.setHttpReadTimeOut(10000);
		IMAGE_CACHE.setOpenWaitingQueue(true);
		IMAGE_CACHE.setValidTime(-1);
		/**
		 * close connection, default is connect keep-alive to reuse connection.
		 * if image is from different server, you can set this
		 */
		// IMAGE_CACHE.setRequestProperty("Connection", "false");
	}

	private static AlphaAnimation getInAlphaAnimation(long durationMillis) {
		AlphaAnimation inAlphaAnimation = new AlphaAnimation(0, 1);
		inAlphaAnimation.setDuration(durationMillis);
		return inAlphaAnimation;
	}

	private static final String TAG_CACHE = "image_cache";
	/** cache folder path which be used when saving images **/
	private static final String DEFAULT_CACHE_FOLDER = new StringBuilder().append(Environment.getExternalStorageDirectory().getAbsolutePath())
			.append(File.separator).append("Android").append(File.separator).append("data").append(File.separator).append("com.iwgame.msgs")
			.append(File.separator).append("cache").append(File.separator).append("ImageCache").toString();

	public void init(Context context) {
		IMAGE_CACHE.initData(context, TAG_CACHE);
		IMAGE_CACHE.setContext(context);
		IMAGE_CACHE.setCacheFolder(DEFAULT_CACHE_FOLDER);
	}

	public void saveDataToDb(Context context) {
		IMAGE_CACHE.saveDataToDb(context, TAG_CACHE);
	}

	public void loadRes(String imageUrl, ImageView imageView, final int resIdForEmptyUri, final int resIdForFail, final int resIdForOnLoading,
			final ImageLoadingProgressListener progressListener, boolean isLoadNet) {
		LogUtil.i("ImageCacheLoader", "----->>imageUrl=" + imageUrl);
		if (imageView == null)
			return;
		if (imageUrl == null || imageUrl.equals("")) {
			imageView.setTag("drawable://" + resIdForEmptyUri);
			imageView.setImageResource(resIdForEmptyUri);
		} else {
			imageView.setTag(imageUrl);
			resIdMap.put(imageUrl, resIdForFail);
			if (!IMAGE_CACHE.get(imageUrl, imageView)) {
				// cache 中不存在，设置加载中
				imageView.setImageResource(resIdForOnLoading);
			} else {
				// cache 中存在，直接加载，不需要做任何事
			}
		}
	}
}
