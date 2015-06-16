/**      
* EditTextUtil.java Create on 2013-8-16     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/** 
 * @ClassName: EditTextUtil 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2013-8-16 上午09:57:45 
 * @Version 1.0
 * 
 */
public class EditTextUtil {

	public static void ChangeCleanTextButtonVisible(final EditText editText, final Button cleanBtn){
		editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(editText.getText().length() == 0){
					cleanBtn.setVisibility(View.INVISIBLE);
				}else{
					cleanBtn.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
}
