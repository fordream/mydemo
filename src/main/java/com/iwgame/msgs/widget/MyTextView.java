/**      
 * MyTextView.java Create on 2013-12-12     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.iwgame.msgs.common.WebBrowserActivity;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: MyTextView
 * @Description: TODO(增加自定义拦截Url的单击)
 * @author chuanglong
 * @date 2013-12-12 下午4:37:34
 * @Version 1.0
 * 
 */
public class MyTextView extends TextView {

    /**
     * @param context
     */
    public MyTextView(Context context) {
	super(context);
	// TODO Auto-generated constructor stub
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	// TODO Auto-generated constructor stub
    }

    /**
     * @param context
     * @param attrs
     */
    public MyTextView(Context context, AttributeSet attrs) {
	super(context, attrs);
	// TODO Auto-generated constructor stub
    }

    /**
     * 设置Url link单击的拦截,自己处理
     */
    public void SetLinkClickIntercept(URLSpanClickable clickable) {
	this.setMovementMethod(LinkMovementMethod.getInstance());
	CharSequence text = this.getText();
	if (text instanceof Spannable) {
	    int end = text.length();
	    Spannable sp = (Spannable) this.getText();
	    URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
	    SpannableStringBuilder style = new SpannableStringBuilder(text);
	    //style.clearSpans();// should clear old spans
	    for (URLSpan url : urls) {
		//LogUtil.d("", "---------2" + clickable + ";" + clickable.getIsClickUrlable());
		style.removeSpan(url);
		MyURLSpan myURLSpan = new MyURLSpan(url.getURL(), clickable);
		style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    }
	    this.setText(style);
	}
    }

    /**
     * 
     * @ClassName: MyURLSpan
     * @Description: TODO(实现自己的URLSpan点击事件)
     * @author chuanglong
     * @date 2013-12-19 下午12:04:03
     * @Version 1.0
     * 
     */
    private class MyURLSpan extends ClickableSpan implements ParcelableSpan {
	URLSpanClickable mURLSpanClickable;
	String mUrl;

	/**
	 * @param url
	 */
	public MyURLSpan(String url, URLSpanClickable clickable) {
	    mUrl = url;
	    // TODO Auto-generated constructor stub
	    mURLSpanClickable = clickable;
	}

	public MyURLSpan(Parcel src, URLSpanClickable clickable) {
	    mUrl = src.readString();
	    mURLSpanClickable = clickable;
	}

	@Override
	public void onClick(View widget) {
            if(mURLSpanClickable!= null)
            {
        	LogUtil.d("", "---------onClick:mURLSpanClickable = " + mURLSpanClickable + ";" + mURLSpanClickable.getIsClickUrlable());
            }
            else
            {
        	LogUtil.d("", "---------onClick:mURLSpanClickable = " + mURLSpanClickable);
            }
	    if (mURLSpanClickable != null && mURLSpanClickable.getIsClickUrlable()) {

		Uri uri = Uri.parse(getURL());
		Context context = widget.getContext();
//		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//		intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
//		context.startActivity(intent);
		 Intent intent = new Intent(context,WebBrowserActivity.class);
		 intent.setData(uri);
		 intent.setAction( Intent.ACTION_VIEW);
		 context.startActivity(intent) ;
		
	    } else {
		mURLSpanClickable.setIsClickUrlable(true);
	    }

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
	    // TODO Auto-generated method stub
	    return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    // TODO Auto-generated method stub
	    dest.writeString(mUrl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.text.ParcelableSpan#getSpanTypeId()
	 */
	@Override
	public int getSpanTypeId() {
	    // TODO Auto-generated method stub
	    return 11;
	    // return TextUtils.URL_SPAN;
	}

	public String getURL() {
	    return mUrl;
	}
    }
}
