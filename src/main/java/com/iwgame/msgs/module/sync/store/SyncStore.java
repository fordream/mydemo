/**      
 * SyncStore.java Create on 2013-12-19     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.sync.store;

import java.util.List;

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
public interface SyncStore {

	/**
	 * 存储同步结果
	 * 
	 * @param result
	 * @param callBack
	 */
	public void process(List<Long> ids, int syncType, XActionResult result, SyncCallBack callBack);

	/**
	 * 获取同步KEY
	 * 
	 * @param syncType
	 * @param id
	 * @return
	 */
	public long getSyncKey(int syncType, Long id);

}