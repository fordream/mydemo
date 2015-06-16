/**      
* FastClickLimitUtil.java Create on 2014-6-18     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.utils;

/** 
 * @ClassName: FastClickLimitUtil 
 * @Description: TODO(快速点击限制) 
 * @author chuanglong
 * @date 2014-6-18 下午5:38:02 
 * @Version 1.0
 * 
 */
public class FastClickLimitUtil {
    private static int gapMs = 500 ;
    private static long lastClickTime;  
    public static boolean isFastClick() {  
        long time = System.currentTimeMillis();  
        long timeD = time - lastClickTime;  
        if ( 0 < timeD && timeD < gapMs) {     
            return true;     
        }     
        lastClickTime = time;     
        return false;     
    } 

}
