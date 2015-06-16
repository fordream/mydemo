/**      
* MyImageView.java Create on 2015-3-25     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/** 
 * @ClassName: MyImageView 
 * @Description: 图片扩展组件 
 * @author 王卫
 * @date 2015-3-25 下午3:20:52 
 * @Version 1.0
 * 
 */
public class SquareCenterImageView extends ImageView {

	/**
	 * @param context
	 */
	public SquareCenterImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public SquareCenterImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SquareCenterImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see android.widget.ImageView#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));  
		   
        // Children are just made to fill our space.  
        int childWidthSize = getMeasuredWidth();  
        int childHeightSize = getMeasuredHeight();  
        //高度和宽度一样  
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);  
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
	}

}
