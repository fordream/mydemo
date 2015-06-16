package com.iwgame.msgs.widget;

import com.youban.msgs.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DotView extends LinearLayout{

	private Context mContext;
	private int  mCurrentIndex = -1;
	public DotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	public DotView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context ;
	}
	
	public void setDotCount(int count)
	{
		if (count > 0)
		{
			
			removeAllViews();
			for(int i= 0 ; i< count ;i++)
			{
				ImageView localImageView = new ImageView(mContext);
				//LayoutParams params = new LayoutParams(20, 20);
				//localImageView.setLayoutParams(params);
				localImageView.setImageResource(R.drawable.dot_unselected);
				addView(localImageView);
			}
			
			requestLayout();
			 
		}
	}
	
	public void setSelectedDot(int index)
	{
		int n = this.getChildCount();
		if(index < n  && index >=0)
		{
			if(mCurrentIndex != -1 &&  mCurrentIndex < n)
			{
				((ImageView)this.getChildAt(mCurrentIndex)).setImageResource(R.drawable.dot_unselected);
			}
			
			((ImageView)this.getChildAt(index)).setImageResource(R.drawable.dot_selected);
			mCurrentIndex = index;
		}
	}

}
