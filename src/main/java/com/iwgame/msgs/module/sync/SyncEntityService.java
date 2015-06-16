/**      
 * ISyncService.java Create on 2013-12-19     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.sync;

import java.util.List;

/**
 * @ClassName: ISyncService
 * @Description: 同步服务 功能：与业务无关，同步实体对象，同步关系列表数据，处理业务数据内容通过注入数据存储类来处理，支持回调和事件通知方式
 *               同步服务记录同步实体和内容的同步状态，同步完成移除
 * @author 王卫
 * @date 2013-12-19 上午9:24:08
 * @Version 1.0
 * 
 */
public interface SyncEntityService {

	public static final String TYPE = "type";
	public static final String UTIME = "utime";
	public static final String ID = "id";
	public static final String IDS = "ids";
	public static final String UID = "uid";

	public static final int TYPE_USER = 0;
	public static final int TYPE_GAME = 1;
	public static final int TYPE_GAME_PACKAGE = 2;
	public static final int TYPE_GROUP = 5;

	// 同步实体完成ACTION
	public static final String SYNC_ENTITY_COMPLETE = "com.iwgame.msgs.module.sync.entity.completed";

	/**
	 * 同步实体对象
	 * 
	 * @param id
	 *            实体ID（用户等）
	 * @param updateTime
	 *            最后更新时间
	 * @param callBack
	 *            回调
	 */
	public void syncEntity(long id, int syncType, SyncCallBack callBack);
	
	/**
	 * 同步多个实体对象
	 * @param ids 尸体id集合
	 * @param syncType 同步对象类型
	 * @param uid 用户id（获取用户公会名片使用）
	 * @param callBack
	 */
	public void syncEntity(List<Long> ids , int syncType, Long uid, SyncCallBack callBack);

}
