/**      
 * ByteUtil.java Create on 2014-2-10     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

/**
 * @ClassName: BitUtil
 * @Description: TODO(...)
 * @author 王卫
 * @date 2014-2-10 上午11:31:02
 * @Version 1.0
 * 
 */
public class BitUtil {

	/**
	 * 字节大小转换
	 * 
	 * @param size
	 * @return
	 */
	public static String covertBit(long size) {
		double m = Double.valueOf(size) / (1024000);
		return String.format("%.2f", m) + "M";
	}

}
