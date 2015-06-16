/**      
 * ImageLoader2.java Create on 2014-06-30     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common.imageloader;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * @ClassName: ImageLoader
 * @Description: TODO(图片异步加载类封装)
 * @author chuanglong
 * @date 2014-06-30 上午10:10:02
 * @Version 1.0
 * 
 */
public class ImageLoader {

    private static volatile ImageLoader instance;

    public static ImageLoader getInstance() {
	if (instance == null) {
	    synchronized (ImageLoader.class) {
		if (instance == null) {
		    instance = new ImageLoader();
		}
	    }
	}
	return instance;
    }
    /**
     * 获得第三方图片加载器的实例子
     * @return
     */
    public com.nostra13.universalimageloader.core.ImageLoader getUniversalImageLoader()
    {
	return com.nostra13.universalimageloader.core.ImageLoader.getInstance();
    }
    
    /**
     * 加载图片
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
     * @param imageView
     *             用于展示图片的view
     * @param resIdForEmptyUri
     *              加载图片地址为null时默认图片
     * @param resIdForFail
     *             加载图片地址失败时默认图片
     * @param resIdForOnLoading
     *             加载图片  加载中的图片
     * @param progressListener
     *             加载进度
     * @param isLoadNet
     *             是否从网络上加载图片，用于本地没有图片时，控制在移动网络下，需不需要到网络加载，从而省移动流量，默认为true
     */
    public void loadRes(String imageUri,final ImageView imageView,final int resIdForEmptyUri,final int resIdForFail,  final int resIdForOnLoading,final ImageLoadingProgressListener progressListener, boolean isLoadNet) {
	DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
	if(resIdForEmptyUri != 0)
           builder.showImageForEmptyUri(resIdForEmptyUri);
	if(resIdForFail != 0)
	    builder.showImageOnFail(resIdForFail);
	if(resIdForOnLoading != 0)
	    builder.showImageOnLoading(resIdForOnLoading);
	builder.resetViewBeforeLoading(true);
	builder.cacheOnDisc(true);
	builder.cacheInMemory(true);
	builder.imageScaleType(ImageScaleType.EXACTLY);
	builder.bitmapConfig(Bitmap.Config.RGB_565);
	//builder.considerExifParams(true)
	builder.displayer(new FadeInBitmapDisplayer(300));
	
	DisplayImageOptions options = builder.build();
	com.nostra13.universalimageloader.core.listener.ImageLoadingListener listener = new SimpleImageLoadingListener() {
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
//		imageView.setImageResource(resIdForFail);
	    }

	    @Override
	    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//		 imageView.setImageBitmap(loadedImage);
	    }

	};
	com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener progressListener2 = new com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener(){

	    @Override
	    public void onProgressUpdate(String paramString, View paramView, int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		if(progressListener != null){
		    progressListener.onProgressUpdate(paramString, paramView, paramInt1, paramInt2);
		}
	    }
	    
	};
	getUniversalImageLoader().displayImage(imageUri, imageView, options, listener, progressListener2, isLoadNet);

    }
    
    /**
     * 加载图片
     * @param uri
     * @param listener
     */
    public void  loadImage(String uri, final ImageLoadingListener listener){
	com.nostra13.universalimageloader.core.listener.ImageLoadingListener listener2 = new com.nostra13.universalimageloader.core.listener.ImageLoadingListener(){

	    @Override
	    public void onLoadingStarted(String paramString, View paramView) {
		// TODO Auto-generated method stub
		if(listener != null){
		    listener.onLoadingStarted(paramString, paramView);
		}
	    }

	    @Override
	    public void onLoadingFailed(String paramString, View paramView, FailReason paramFailReason) {
		// TODO Auto-generated method stub
		if(listener != null){
		    listener.onLoadingFailed(paramString, paramView, paramFailReason);
		}
	    }

	    @Override
	    public void onLoadingComplete(String paramString, View paramView, Bitmap paramBitmap) {
		// TODO Auto-generated method stub
		if(listener != null){
		    listener.onLoadingComplete(paramString, paramView, paramBitmap);
		}
	    }

	    @Override
	    public void onLoadingCancelled(String paramString, View paramView) {
		// TODO Auto-generated method stub
		if(listener != null){
		    listener.onLoadingCancelled(paramString, paramView);
		}
	    }

	  
	    
	};
	
	getUniversalImageLoader().loadImage(uri, listener2);
    }
    

}
