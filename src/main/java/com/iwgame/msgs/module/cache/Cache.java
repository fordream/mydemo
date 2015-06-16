/**      
 * Cache.java Create on 2014-1-23     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.cache;

/**
 * @ClassName: Cache
 * @Description: 缓存接口
 * @author 王卫
 * @date 2014-1-23 上午10:02:12
 * @Version 1.0
 * 
 */
public interface Cache {

	public static final int DATA_TYPE_DISCOVER_USER = 1;
	public static final int DATA_TYPE_DISCOVER_GAME = 2;
	public static final int DATA_TYPE_DISCOVER_PLAY = 12;
	public static final int DATA_TYPE_DISCOVER_GROUP = 3;
	public static final int DATA_TYPE_CONTRANCT_USER_FRIEND = 4;
	public static final int DATA_TYPE_CONTRANCT_USER_FRIEND_KEY = 5;
	public static final int DATA_TYPE_CONTRANCT_FANS = 6;
	public static final int DATA_TYPE_CONTRANCT_GROUP = 7;
	public static final int DATA_TYPE_GAME_TOP = 8;
	public static final int DATA_TYPE_GAME_RECOMMEND = 9;
	public static final int DATA_TYPE_GAME_NEAR = 10;
	public static final int DATA_TYPE_TASK_POINTTASK=11;
	/**
	 * 保存数据
	 * 
	 * @param dataType
	 * @param data
	 */
	public void saveData(int dataType, Object data);

	/**
	 * 获取数据
	 * 
	 * @param dataType
	 * @param callBack
	 * @return
	 */
	public void getData(int dataType, CacheCallBack callBack);

}
