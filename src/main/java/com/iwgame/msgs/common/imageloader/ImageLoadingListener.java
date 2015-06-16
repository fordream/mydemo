/**      
* ImageLoadingListener.java Create on 2014-8-27     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.common.imageloader;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;

/** 
 * @ClassName: ImageLoadingListener 
 * @Description: TODO(图片加载listener) 
 * @author chuanglong
 * @date 2014-8-27 上午11:35:54 
 * @Version 1.0
 * 
 */
public abstract interface ImageLoadingListener {
    public abstract void onLoadingStarted(String paramString, View paramView);

    public abstract void onLoadingFailed(String paramString, View paramView, FailReason paramFailReason);

    public abstract void onLoadingComplete(String paramString, View paramView, Bitmap paramBitmap);

    public abstract void onLoadingCancelled(String paramString, View paramView);
}
