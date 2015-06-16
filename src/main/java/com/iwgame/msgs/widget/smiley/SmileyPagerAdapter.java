package com.iwgame.msgs.widget.smiley;

import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class SmileyPagerAdapter extends PagerAdapter {

	List<View> pageViews ;
	Context context;
	public SmileyPagerAdapter(Context context ,List<View> pageViews)
	{
	    this.context = context ;
		this.pageViews = pageViews ;
	}
	 @Override  
     public int getCount() {  
         return pageViews.size();  
     }  

     @Override  
     public boolean isViewFromObject(View arg0, Object arg1) {  
         return arg0 == arg1;  
     }  

     @Override  
     public int getItemPosition(Object object) {  
         // TODO Auto-generated method stub  
         return super.getItemPosition(object);  
     }  

     @Override  
     public void destroyItem(View arg0, int arg1, Object arg2) {  
         // TODO Auto-generated method stub  
         ((ViewPager)arg0).removeView(pageViews.get(arg1));  
     }  

     @Override  
     public Object instantiateItem(View arg0, int arg1) {  
         // TODO Auto-generated method stub  
         ((ViewPager)arg0).addView(pageViews.get(arg1));  
         return pageViews.get(arg1);  
     }  

     @Override  
     public void restoreState(Parcelable arg0, ClassLoader arg1) {  
         // TODO Auto-generated method stub  

     }  

     @Override  
     public Parcelable saveState() {  
         // TODO Auto-generated method stub  
         return null;  
     }  

     @Override  
     public void startUpdate(View arg0) {  
         // TODO Auto-generated method stub  

     }  

     @Override  
     public void finishUpdate(View arg0) {  
         // TODO Auto-generated method stub  

     }  

}
