/**      
 * ISyncService.java Create on 2013-12-19     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.sync;

/**
 * @ClassName: ISyncService
 * @Description: 同步服务 功能：与业务无关，同步实体对象，同步关系列表数据，处理业务数据内容通过注入数据存储类来处理，支持回调和事件通知方式
 *               同步服务记录同步实体和内容的同步状态，同步完成移除
 * @author 王卫
 * @date 2013-12-19 上午9:24:08
 * @Version 1.0
 * 
 */
public interface SyncListService {

	public static final String TYPE = "type";
	public static final String UTIME = "utime";
	public static final String ID = "id";
	public static final int TYPE_GAME = 1;
	public static final int TYPE_GAME_PACKAGE = 2;
	public static final int TYPE_MY_GAME = 3;
	public static final int TYPE_MY_REL_USER = 4;
	public static final int TYPE_MY_GROUP = 5;
	public static final int TYPE_GROUP_USER = 6;
	public static final int TYPE_GAME_SERVICE = 7;
	// 同步列表内容完成ACTION
	public static final String SYNC_LIST_COMPLETE = "com.iwgame.msgs.module.sync.list.completed";

	/**
	 * 同步关系内容(贴吧、贴吧包、用户、公会等关系列表内容)
	 * 
	 * 同步KEY
	 * 
	 * @param syncType
	 *            同步类型
	 * @param id
	 *            同步实体ID（暂用于同步公会成员和同步贴吧包）
	 */
	public void syncList(int syncType, Long id, SyncCallBack callBack);

	public void syncList(int syncType, SyncCallBack callBack);

	public void syncListByUtime(int syncType, Long utime, SyncCallBack callBack);
	public void syncList(int syncType, Long id, Long utime, SyncCallBack callBack);
}
