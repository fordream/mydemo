/**      
* MonitorSizeChangeLinearLayout.java Create on 2014-2-19     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/** 
 * @ClassName: MonitorSizeChangeLinearLayout 
 * @Description: TODO(...) 
 * @author chuanglong
 * @date 2014-2-19 下午3:37:47 
 * @Version 1.0
 * 
 */
public class MonitorSizeChangeLinearLayout extends LinearLayout {
    
    public interface SizeChangedListener{
	public void onSizeChanged();
    }
    private SizeChangedListener sizeChangedListener;

    /**
     * @param context
     */
    public MonitorSizeChangeLinearLayout(Context context) {
	super(context);
	// TODO Auto-generated constructor stub
    }
    
    /**
     * @param context
     * @param attrs
     */
    public MonitorSizeChangeLinearLayout(Context context, AttributeSet attrs) {
	super(context, attrs);
	// TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see android.view.View#onSizeChanged(int, int, int, int)
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	// TODO Auto-generated method stub
	super.onSizeChanged(w, h, oldw, oldh);
	if(sizeChangedListener != null){
	    sizeChangedListener.onSizeChanged();
	}
    }
    
    public void setSizeChangedListener(SizeChangedListener listener)
    {
	sizeChangedListener = listener ;
    }

   

   
    

}
