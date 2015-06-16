/**      
 * Utils.java Create on 2014-2-11     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @ClassName: AudioUtils
 * @Description: TODO(声音的工具类)
 * @author chuanglong
 * @date 2014-2-11 上午10:57:00
 * @Version 1.0
 */
public class AudioUtils {
    /**
     * 得到amr的时长（ms)
     * 
     * @param fileName
     * @return  毫秒数
     * @throws IOException
     */
    public static long getAmrDuration(String fileName) throws IOException {
	return getAmrDuration(new RandomAccessFile(fileName, "rw"));
    }

    /**
     * 得到amr的时长(ms)
     * 
     * @param fileName
     * @return 毫秒数
     * @throws IOException
     */
    public static long getAmrDuration(File file) throws IOException {
	return getAmrDuration(new RandomAccessFile(file, "rw"));
    }

    private static long getAmrDuration(RandomAccessFile randomAccessFile) throws IOException {
	long duration = -1;
	int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };
	try {
	    long length = randomAccessFile.length();// 文件的长度
	    int pos = 6;// 设置初始位置
	    int frameCount = 0;// 初始帧数
	    int packedPos = -1;
	    // ///////////////////////////////////////////////////
	    byte[] datas = new byte[1];// 初始数据值
	    while (pos <= length) {
		randomAccessFile.seek(pos);
		if (randomAccessFile.read(datas, 0, 1) != 1) {
		    duration = length > 0 ? ((length - 6) / 650) : 0;
		    break;
		}
		packedPos = (datas[0] >> 3) & 0x0F;
		pos += packedSize[packedPos] + 1;
		frameCount++;
	    }
	    // ///////////////////////////////////////////////////
	    duration += frameCount * 20;// 帧数*20
	} finally {
	    if (randomAccessFile != null) {
		randomAccessFile.close();
	    }
	}
	return duration;
    }

}
