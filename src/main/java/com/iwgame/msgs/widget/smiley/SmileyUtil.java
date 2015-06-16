/**      
* SmileyUtil.java Create on 2013-12-6     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.widget.smiley;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.youban.msgs.R;

/** 
 * @ClassName: SmileyUtil 
 * @Description: TODO(表情的工具类，用于替换表情) 
 * @author chuanglong
 * @date 2013-12-6 下午4:36:05 
 * @Version 1.0
 * 
 */
public class SmileyUtil {

    /**
     * 替换内容中的所有表情
     * @param context
     * @param content
     * @param resW
     * @param resH
     * @return
     */
    public static SpannableString ReplaceSmiley(Context context ,String content,int resW,int resH)
	{
	   if(content == null){
	       return new SpannableString("");
	   }
	    SpannableString ss = new SpannableString(content);         
	    int len = 0;          
	    int starts = 0;          
	    int end = 0;          
	    while(len < content.length()){   
	    	//[sm:0]
	        if(content.indexOf(SmileyVo.FORMAT_PREFIX, starts) != -1){          
	            starts = content.indexOf(SmileyVo.FORMAT_PREFIX, starts);          
	            end = content.indexOf(SmileyVo.FORMAT_SUFFIX, starts);
	            // 更改判断，解决自己在表情中输入“]”时异常的问题
	            if(starts + SmileyVo.FORMAT_PREFIX.length() > end){
	        	break;
	            }
	            String phrase = content.substring(starts+SmileyVo.FORMAT_PREFIX.length(),end);   
	            String imageName = SmileyVo.NAME_PREFIX+ phrase;                                      
	            try {          
	                Field f = (Field)R.drawable.class.getDeclaredField(imageName);          
	                int i= f.getInt(R.drawable.class);          
	                Drawable drawable = context.getResources().getDrawable(i);            
	                if (drawable != null) {          
	                    drawable.setBounds(0, 0, resW>0 ? resW:drawable.getIntrinsicWidth(),resH>0 ? resH : drawable.getIntrinsicHeight());           
	                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);            
	                    ss.setSpan(span, starts,end + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);            
	                }          
	            } catch (SecurityException e) {          
	                e.printStackTrace();          
	            } catch (NoSuchFieldException e) {          
	                e.printStackTrace();          
	            } catch (IllegalArgumentException e) {          
	                e.printStackTrace();          
	            } catch (IllegalAccessException e) {          
	                          
	            }          
	            starts = end;          
	            len = end;          
	            end++;          
	        }else{          
	            starts++;          
	            end++;          
	            len = end;          
	        }          
	    }          
		
		return ss ;
	}
}
