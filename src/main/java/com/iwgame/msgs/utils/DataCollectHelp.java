/**      
 * DataCollectUtil.java Create on 2013-9-12     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import android.content.Context;

import com.iwgame.utils.FileUtil;

/**
 * @ClassName: DataCollectUtil
 * @Description: 数据采集
 * @author 王卫
 * @date 2013-9-12 上午11:17:02
 * @Version 1.0
 * 
 */
public class DataCollectHelp {
	// 文件名称
	public static final String FILE_NAME = "actionData.txt";
	// 安装
	public static final int ACTION_PACKAGE_ADDED = 1;
	// 删除
	public static final int ACTION_PACKAGE_REMOVED = 2;
	// 退出
	public static final int ACTION_PACKAGE_EXIT = 3;
	// 启动
	public static final int ACTION_PACKAGE_START = 4;

	/**
	 * 保存动作数据
	 * 
	 * @param packageName
	 * @param time
	 * @param action
	 */
	public static void saveActionData(Context context, int action, String packageName, long time) {
		if (action != 0 && packageName != null && !packageName.isEmpty()) {
			FileUtil.writeFileByLine(context, FILE_NAME, packageName + "|" + action + "|" + String.valueOf(time));
		}
	}

}
