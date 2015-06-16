/**      
* ParentViewPager.java Create on 2013-12-11     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.chat.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

/** 
 * @ClassName: ParentViewPager 
 * @Description: TODO(自定义viewpager， 用于解决ViewPager双层嵌套问题，主要作为父viewpager）
 * 此方法虽然简单可行，但是会出现，子ViewPager如果为ScrollView的时候，子ViewPager虽然已经滑动到看不到的地方，但是设定的高度内还是不能让父ViewPager左右滑动，onTouch的动作透过了父Viewpager传递到了子控件 
 * @author chuanglong
 * @date 2013-12-11 上午11:27:40 
 * @Version 1.0
 * 
 */
public class ParentViewPager extends ViewPager {
    private String TAG = "ParentViewPager" ;
    private int childVPHeight=0;
    private int  displayHeight = 0 ;
    /**
     * 是否可以滑动，默认true ，可以滑动
     */
    private boolean isScrollable =true;
    
    private MyTouchListener myTouchListener;
    public ParentViewPager(Context context) {
        super(context); 
        // TODO Auto-generated constructor stub
        init(context);
    }

    public ParentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public void init(Context context){
        // 获取屏幕宽高
        WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        displayHeight = windowManager.getDefaultDisplay().getHeight();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
	//触摸时设置是否可以滚动（通过回调自定义的方法，自定义方法中判断是否可以滚动，如果设置进来）
	if(isScrollable != false)
	{
	 if(myTouchListener != null)
	      myTouchListener.onTouch();
	}
        //触摸在子ViewPager所在的页面和子ViewPager控件高度之内时
                //返回false，此时将会将触摸的动作传给子ViewPager
	Log.d(TAG,"----------" + childVPHeight+":"+arg0.getRawY()+":displayHeight - childVPHeight="+(displayHeight - childVPHeight));
        if(isScrollable == false || (getCurrentItem()==0 && arg0.getRawY()<displayHeight && arg0.getRawY() > displayHeight - childVPHeight) ){
            return false;
        }
        else
        {
        return super.onInterceptTouchEvent(arg0);
        }
    }  
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
	
        if (isScrollable == false) {
            return false;
        } else {
            return super.onTouchEvent(ev);
        }

    }
    
    public void setChildViewPagerHeight(int height)
    {
	childVPHeight = height ;
    }
   
    public void setScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
    }
    
    public void setMyTouchListener(MyTouchListener listener)
    {
	myTouchListener = listener;
    }
    
    public interface MyTouchListener{
         public void onTouch();
    }
    
}
