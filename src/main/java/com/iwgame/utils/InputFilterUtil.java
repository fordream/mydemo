/**      
 * InputFilterUtil.java Create on 2013-6-17     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.utils;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @ClassName: InputFilterUtil
 * @Description: TODO(...)
 * @author william
 * @date 2013-6-17 下午2:53:16
 * @Version 1.0
 * 
 */
public class InputFilterUtil {

	public static void lengthFilter(final Context context, final EditText editText, final int max_length, final String err_msg) {
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter.LengthFilter(max_length) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.text.InputFilter.LengthFilter#filter(java.lang.CharSequence
			 * , int, int, android.text.Spanned, int, int)
			 */
			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				// return super.filter(source, start, end, dest, dstart, dend);
				int destLen = StringUtil.getCharacterNum(dest.toString()); // 获取字符个数(一个中文算2个字符)
				int sourceLen = StringUtil.getCharacterNum(source.toString());
				if (destLen + sourceLen > max_length) {
					ToastUtil.showToast(context, err_msg, Toast.LENGTH_SHORT);
//					if (source != null && !"".equals(source.toString())) {
//						int l = max_length - destLen;
//						int s = source.toString().length() - 1;
//						return source.toString().substring(0, l > s ? s : l);
//					} else {
						return "";
//					}
				}
				return source;
			}

		};
		editText.setFilters(filters);
	}

}
