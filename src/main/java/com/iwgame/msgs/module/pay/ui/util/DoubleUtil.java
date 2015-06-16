/**      
 * DoubleUtil.java Create on 2015-6-4     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.pay.ui.util;

import java.text.DecimalFormat;

/**
 * @ClassName: DoubleUtil
 * @Description: 双精度处理
 * @author 王卫
 * @date 2015-6-4 上午9:48:38
 * @Version 1.0
 * 
 */
public class DoubleUtil {

	public static String getDoubleFormat(double d) {
		if (d == 0) {
			return "0";
		} else {
			DecimalFormat df = new DecimalFormat("#0.00");
			return df.format(d);
		}
	}

}