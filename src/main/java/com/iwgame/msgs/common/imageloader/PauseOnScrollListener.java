/**      
* PauseOnScrollListener.java Create on 2014-6-30     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.common.imageloader;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/** 
 * @ClassName: PauseOnScrollListener 
 * @Description: TODO(...) 
 * @author chuanglong
 * @date 2014-6-30 上午10:16:43 
 * @Version 1.0
 * 
 */
public class PauseOnScrollListener implements AbsListView.OnScrollListener {

    private com.nostra13.universalimageloader.core.listener.PauseOnScrollListener pauseOnScrollListener ;
    /**
     * @param imageLoader
     * @param pauseOnScroll
     * @param pauseOnFling
     */
    public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
	pauseOnScrollListener = new com.nostra13.universalimageloader.core.listener.PauseOnScrollListener (imageLoader.getUniversalImageLoader(), pauseOnScroll, pauseOnFling);
    }

    /**
     * @param imageLoader
     * @param pauseOnScroll
     * @param pauseOnFling
     * @param customListener
     */
    public PauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling, OnScrollListener customListener) {
	pauseOnScrollListener = new com.nostra13.universalimageloader.core.listener.PauseOnScrollListener (imageLoader.getUniversalImageLoader(), pauseOnScroll, pauseOnFling, customListener);
    }

    /* (non-Javadoc)
     * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView, int)
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
	// TODO Auto-generated method stub
	pauseOnScrollListener.onScrollStateChanged(view, scrollState);
	
    }

    /* (non-Javadoc)
     * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	// TODO Auto-generated method stub
	pauseOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }
    

}
