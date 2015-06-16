/**      
 * SyncStore.java Create on 2013-12-19     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.sync.store;

import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: SyncStore
 * @Description: 同步存储接口
 * @author 王卫
 * @date 2013-12-19 上午10:48:49
 * @Version 1.0
 * 
 */
public interface SyncListStore {

	/**
	 * 存储同步结果
	 * 
	 * @param id
	 * @param syncType
	 * @param result
	 */
	public void process(Long id, int syncType, XActionResult result, SyncCallBack callBack);

	/**
	 * 获取同步KEY
	 * 
	 * @param syncType
	 * @param id
	 * @return
	 */
	public long getSyncKey(int syncType, Long id);

}