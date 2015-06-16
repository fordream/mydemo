/**      
* StrictModeWrapper.java Create on 2014-3-3     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.common;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.StrictMode;
import android.util.Log;

/** 
 * @ClassName: StrictModeWrapper 
 * @Description: TODO(...) 
 * @author chuanglong
 * @date 2014-3-3 下午4:43:00 
 * @Version 1.0
 * 
 */
public class StrictModeWrapper {
    /**
     * 启动StrictMode策略
     * @param context
     */
    public static void init(Context context) {  
        // check if android:debuggable is set to true  
//        int appFlags = context.getApplicationInfo().flags;  
//        if ((appFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {  
//            
//            
//            
//            try {  
//                //Android 2.3及以上调用严苛模式  
//                Class sMode = Class.forName("android.os.StrictMode");  
//                Method enableDefaults = sMode.getMethod("enableDefaults");  
//                enableDefaults.invoke(null);  
//                
//                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
//                .detectDiskReads()  
//                .detectDiskWrites()  
//                .detectNetwork()  
//                //.detectAll() 
//	        .penaltyLog() 
//	       // .penaltyDialog() ////打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
//                .build());  
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()  
//                //.detectLeakedSqlLiteObjects()  
//                .detectAll()
//                .penaltyLog()  
//               // .penaltyDeath()  
//                .build());  
//            } catch (Exception e) {  
//                // StrictMode not supported on this device, punt  
//                Log.v("StrictMode", "... not supported. Skipping...");  
//            }    
//        }  
    }
    
    /**
     * 停止使用这些规则去检查（用于想跳过规则的代码之前）
     * @return
     */
    public static StrictMode.ThreadPolicy stopHasStrictMode()
    {
//	StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
//	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
//	.permitDiskWrites()
//	.build());
//	return old ;
	return null ;
	
    }
    /**
     * 恢复使用的规则去检查（用于想跳过规则的代码之前）
     * @param tp
     */
    public static void resumeHasStrictMode(StrictMode.ThreadPolicy tp)
    {
//	StrictMode.setThreadPolicy(tp);
    }
    
}
