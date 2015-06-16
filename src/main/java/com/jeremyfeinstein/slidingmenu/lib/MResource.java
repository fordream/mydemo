/**      
* MResource.java Create on 2014-4-21     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.jeremyfeinstein.slidingmenu.lib;

import android.content.Context;

/** 
 * @ClassName: MResource 
 * @Description: TODO(用于把资源放到外部进行转换) 
 * @author chuanglong
 * @date 2014-4-21 下午12:38:32 
 * @Version 1.0
 * 
 */
public class MResource {
    
    public static int getIdByName(Context context, String className, String name) {  
        String packageName = context.getPackageName();  
        Class r = null;  
        int id = 0;  
        try {  
            r = Class.forName(packageName + ".R");  
  
            Class[] classes = r.getClasses();  
            Class desireClass = null;  
  
            for (int i = 0; i < classes.length; ++i) {  
                if (classes[i].getName().split("\\$")[1].equals(className)) {  
                    desireClass = classes[i];  
                    break;  
                }  
            }  
  
            if (desireClass != null)  
                id = desireClass.getField(name).getInt(desireClass);  
        } catch (ClassNotFoundException e) {  
            e.printStackTrace();  
        } catch (IllegalArgumentException e) {  
            e.printStackTrace();  
        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IllegalAccessException e) {  
            e.printStackTrace();  
        } catch (NoSuchFieldException e) {  
            e.printStackTrace();  
        }  
  
        return id;  
    }  

}
