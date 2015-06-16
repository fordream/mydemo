package com.iwgame.msgs.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class Utils {

	/**
	 * 用图片替代文字中所有的表情
	 * 
	 * @param context
	 * @param content
	 * @param resW
	 * @param resH
	 * @return
	 */

	/**
	 * 用图片替代文字
	 * 
	 * @param str
	 * @param drawable
	 * @return
	 */
	public static SpannableString ReplaceString(String str, Drawable drawable) {
		final SpannableString ss = new SpannableString(str);
		ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
		// ss.setSpan(span, 0, str.length(),
		// Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		ss.setSpan(span, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return ss;
	}

	/**
	 * 用图片替代文字
	 * 
	 * @param context
	 * @param str
	 *            需要替代的文字
	 * @param resId图片资源编号
	 * @return
	 */
	public static SpannableString ReplaceString(Context context, String str, int resId) {
		return ReplaceString(context, str, resId, 0, 0);
	}

	/**
	 * 用图片替代文字
	 * 
	 * @param context
	 * @param str
	 *            需要替代的文字
	 * @param resId
	 *            图片资源编号
	 * @param resW
	 *            替换后文字中图片的宽度
	 * @param resH
	 *            替换后文字中图片的高度
	 * @return
	 */
	public static SpannableString ReplaceString(Context context, String str, int resId, int resW, int resH) {
		Drawable drawable = context.getResources().getDrawable(resId);
		// drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
		// drawable.getIntrinsicHeight());
		drawable.setBounds(0, 0, resW > 0 ? resW : drawable.getIntrinsicWidth(), resH > 0 ? resH : drawable.getIntrinsicHeight());
		return ReplaceString(str, drawable);
	}

	/**
	 * 隐藏输入法
	 * 
	 * @param context
	 * @param view
	 */

	public static void hideSoftInput(Context context, View view) {
		if(context == null || ((Activity)context).getWindow()==null || ((Activity)context).getWindow().getAttributes() == null || view == null){
			return;
		}
		if (((Activity)context).getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
			InputMethodManager inputmethodmanager = (InputMethodManager) context.getSystemService("input_method");
			android.os.IBinder ibinder = view.getWindowToken();
			inputmethodmanager.hideSoftInputFromWindow(ibinder, 0);
		}
	}
	
	/**
	 * 隐藏输入法2
	 * 
	 * @param context
	 * @param view
	 */

	public static void hideSoftInput2(Context context, Activity activity, View view) {
		if(activity != null){
			View v = activity.getWindow().peekDecorView();
			if (v != null) {
				InputMethodManager inputmanger = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputmanger.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		}else{
			hideSoftInput(context, view);
		}
	}

	/**
	 * 显示输入法
	 * 
	 * @param context
	 * @param view
	 */
	public static void showSoftInput(Context context, View view) {
		InputMethodManager inputmethodmanager = (InputMethodManager) context.getSystemService("input_method");

		inputmethodmanager.showSoftInput(view, 2);
	}

	/**
	 * 显示输入法
	 * 
	 * @param context
	 * @param view
	 */
	public static void showSoftInput2(Context context, View view) {
		InputMethodManager inputmethodmanager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

		inputmethodmanager.showSoftInput(view, 0);
	}
	
	/**
	 * 判断输入法是否活动（打开
	 * 
	 * @param context
	 * @return true 打开状态， false 关闭状态
	 */
	public static boolean isActiveSoftInput(Context context) {
		InputMethodManager inputmethodmanager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		return inputmethodmanager.isActive();
	}

	/**
	 * 同城关注人数量转换
	 * 
	 * @param amount
	 * @return
	 */
	public static String convertAmount(int amount) {
		if (amount >= 10000) {
			double d = Double.valueOf(amount) / 10000;
			return String.format("%.2f", d) + "万";
		} else {
			return amount + "";
		}
	}
	
}