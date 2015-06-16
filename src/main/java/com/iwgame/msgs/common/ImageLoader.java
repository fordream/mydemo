/**      
 * ImageLoader.java Create on 2013-11-2     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import java.util.logging.Logger;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youban.msgs.R;

/**
 * @ClassName: ImageLoader
 * @Description: TODO(图片异步加载类封装)
 * @author Administrator
 * @date 2013-11-2 上午10:57:02
 * @Version 1.0
 * 
 */
public class ImageLoader {

	public final static int RADIUS_DEFAULT_PX0 = 0;
	public final static int RADIUS_DEFAULT_PX10 = 10;
	public final static int RADIUS_DEFAULT_PX6 = 6;

	/**
	 * 加载图片资源
	 * 
	 * @param imageUri
	 *            图片地址，各种情况的图片如下 String imageUri = "http://site.com/image.png";
	 *            // from Web String imageUri = "file:///mnt/sdcard/image.png";
	 *            // from SD card String imageUri =
	 *            "content://media/external/audio/albumart/13"; // from content
	 *            imageUri = "assets://image.png"; // from assets String
	 *            imageUri = "drawable://" + R.drawable.image; // from drawables
	 *            (only images, non-9patch)
	 * @param imageView
	 *            用于展示图片的view
	 */

	public void loadRes(String imageUri, int radiusPx, final ImageView imageView) {
		loadRes(imageUri, radiusPx, imageView, R.drawable.ic_launcher, true);
	}

	/**
	 * 
	 * 加载图片资源
	 * 
	 * @param imageUri
	 *            图片地址，各种情况的图片如下 String imageUri = "http://site.com/image.png";
	 *            // from Web String imageUri = "file:///mnt/sdcard/image.png";
	 *            // from SD card String imageUri =
	 *            "content://media/external/audio/albumart/13"; // from content
	 *            imageUri = "assets://image.png"; // from assets String
	 *            imageUri = "drawable://" + R.drawable.image; // from drawables
	 *            (only images, non-9patch)
	 * @param imageView
	 *            用于展示图片的view
	 * @param defaultResId
	 *            加载图片失败后的默认图片，R.drawable.image
	 */

	public void loadRes(String imageUri, int radiusPx, final ImageView imageView, int defaultResId) {
		loadRes(imageUri, radiusPx, imageView, defaultResId, true);
	}

	/**
	 * 加载图片资源
	 * 
	 * @param imageUri
	 * @param radiusPx
	 * @param imageView
	 * @param defaultResId
	 */
	public void loadRes(String imageUri, final int radiusPx, final ImageView imageView, final int defaultResId, boolean isLoadNet) {
		loadRes(imageUri, radiusPx, imageView, defaultResId, isLoadNet, false, true);
	}

	/**
	 * 
	 * 加载图片资源
	 * 
	 * @param imageUri
	 *            图片地址，各种情况的图片如下 String imageUri = "http://site.com/image.png";
	 *            // from Web String imageUri = "file:///mnt/sdcard/image.png";
	 *            // from SD card String imageUri =
	 *            "content://media/external/audio/albumart/13"; // from content
	 *            imageUri = "assets://image.png"; // from assets String
	 *            imageUri = "drawable://" + R.drawable.image; // from drawables
	 *            (only images, non-9patch)
	 * @param imageView
	 *            用于展示图片的view
	 * @param isLoadNet
	 *            是否从网络上加载图片，用于本地没有图片时，控制在移动网络下，需不需要到网络加载，从而省移动流量，默认为true
	 *            （目前未实现控制）
	 */

	public void loadRes(String imageUri, int radiusPx, final ImageView imageView, boolean isLoadNet) {
		loadRes(imageUri, radiusPx, imageView, R.drawable.ic_launcher, isLoadNet);
	}

	/**
	 * 加载图片资源
	 * 
	 * @param imageUri
	 *            图片地址，各种情况的图片如下
	 * 
	 *            String imageUri = "http://site.com/image.png"; // from Web
	 *            String imageUri = "file:///mnt/sdcard/image.png"; // from SD
	 *            card String imageUri =
	 *            "content://media/external/audio/albumart/13"; // from content
	 *            imageUri = "assets://image.png"; // from assets String
	 *            imageUri = "drawable://" + R.drawable.image; // from drawables
	 *            (only images, non-9patch)
	 * 
	 * @param radiusPx
	 *            圆角
	 * @param imageView
	 *            用于展示图片的view
	 * @param defaultResId
	 *            加载图片失败后的默认图片，R.drawable.image
	 * @param isLoadNet
	 *            是否从网络上加载图片，用于本地没有图片时，控制在移动网络下，需不需要到网络加载，从而省移动流量，默认为true
	 *            （目前未实现控制）
	 */
	public void loadRes(String imageUri, final int radiusPx, final ImageView imageView, final int defaultResId, boolean isLoadNet,
			final boolean isgray, final boolean cache) {
		if (imageUri == null) {
			imageView.setImageResource(defaultResId);
			imageView.setTag(R.id.imageview_tag_current_display_uri, "drawable://" + defaultResId);
			imageView.setTag(R.id.imageview_tag_current_wait_display_uri, "drawable://" + defaultResId);
			return;
		}

		// logger.debug("----loadRes:imageView=" + imageView);
		// logger.debug("----loadRes:imageUri:" + imageUri);
		// logger.debug("----loadRes:current:" +
		// imageView.getTag(R.id.imageview_tag_current_display_uri));
		// logger.debug("----loadRes:wait: is current :" +
		// imageView.getTag(R.id.imageview_tag_current_wait_display_uri));
		imageView.setTag(R.id.imageview_tag_current_wait_display_uri, imageUri);
		String current_display = (String) imageView.getTag(R.id.imageview_tag_current_display_uri);
		// String current_wait_display = (String)
		// imageView.getTag(R.id.imageview_tag_current_wait_display_uri);
		if (cache && current_display != null && imageUri != null && current_display.equals(imageUri)) {
			// logger.debug("----loadRes: imageUri = current ; so return");
			return;
		}

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				// .showImageForEmptyUri(defaultResId)
				// .showImageOnFail(defaultResId)
				// .showStubImage(defaultResId) // 在显示真正的图片前，会加载这个资源
				.resetViewBeforeLoading(true).cacheOnDisc(true).cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				// .considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		ImageLoadingListener listener = new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {

			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
				case IO_ERROR:
					message = "Input/Output error";
					break;
				case DECODING_ERROR:
					message = "Image can't be decoded";
					break;
				case NETWORK_DENIED:
					message = "Downloads are denied";
					break;
				case OUT_OF_MEMORY:
					message = "Out Of Memory error";
					break;
				case UNKNOWN:
					message = "Unknown error";
					break;
				}

				// String tmp_current_wait_display=
				// (String)imageView.getTag(R.id.imageview_tag_current_wait_display_uri);
				String tmp_current_display = (String) imageView.getTag(R.id.imageview_tag_current_display_uri);
				if (tmp_current_display == null || !tmp_current_display.equals("drawable://" + defaultResId)) {
					imageView.setImageResource(defaultResId);
					imageView.setTag(R.id.imageview_tag_current_display_uri, "drawable://" + defaultResId);
				}
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				// logger.debug("----onLoadingComplete:imageView=" + imageView);
				// logger.debug("----onLoadingComplete:imageUri:" + imageUri);
				// logger.debug("----onLoadingComplete:current:" +
				// imageView.getTag(R.id.imageview_tag_current_display_uri));
				// logger.debug("----onLoadingComplete:wait:" +
				// imageView.getTag(R.id.imageview_tag_current_wait_display_uri));
				String tmp_current_wait_display = (String) imageView.getTag(R.id.imageview_tag_current_wait_display_uri);
				String tmp_current_display = (String) imageView.getTag(R.id.imageview_tag_current_display_uri);
				if (!cache || (imageUri.equals(tmp_current_wait_display) && (tmp_current_display == null || !tmp_current_display.equals(imageUri)))) {
					if (isgray) {
						// new ProcessImageTask(loadedImage,
						// imageView).execute();
						imageView.setImageBitmap(toGrayscale(loadedImage));
					} else {
						imageView.setImageBitmap(loadedImage);
					}
					imageView.setTag(R.id.imageview_tag_current_display_uri, imageUri);
				} else {
					// logger.debug("----onLoadingComplete:imageUri != wait or wait = current = imageUri ,so return ");
				}
			}

		};
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().loadImage(imageUri, null, options, listener, null, isLoadNet);

	}

	/**
	 * 对照片黑白处理
	 * 
	 * @param bmpOriginal
	 * @return
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

}
