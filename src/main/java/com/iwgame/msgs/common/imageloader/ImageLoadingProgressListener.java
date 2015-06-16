/**      
* ImageLoadingProgressListener.java Create on 2014-6-30     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.common.imageloader;

import android.view.View;

/** 
 * @ClassName: ImageLoadingProgressListener 
 * @Description: TODO(图片加载进度回调) 
 * @author chuanglong
 * @date 2014-6-30 上午10:32:11 
 * @Version 1.0
 * 
 */
public abstract interface ImageLoadingProgressListener {
    public abstract void onProgressUpdate(String paramString, View paramView, int paramInt1, int paramInt2);
}
