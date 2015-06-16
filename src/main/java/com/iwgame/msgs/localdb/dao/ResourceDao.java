/**      
* ResourceDao.java Create on 2013-9-30     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.localdb.dao;

import java.util.List;

import com.iwgame.msgs.vo.local.ResourceVo;

/** 
 * @ClassName: ResourceDao 
 * @Description: 资源（图片）DAO类
 * @author 王卫
 * @date 2013-9-30 上午11:43:31 
 * @Version 1.0
 * 
 */
public interface ResourceDao {

	/**
	 * 插入资源
	 * @param vo
	 * @return
	 */
	public long insert(ResourceVo vo);
	
	/**
	 * 更新资源
	 * @param vo
	 * @return
	 */
	public long update(ResourceVo vo);
	
	/**
	 * 插入资源
	 * @param vo
	 * @return
	 */
	public long insertOrUpdate(ResourceVo vo);
	
	/**
	 * 删除资源
	 * @param resid
	 * @return
	 */
	public long deleteByResId(String resid);
	
	/**
	 * 根据用户ID查找资源
	 * @param userid
	 * @return
	 */
	public List<ResourceVo> findResourceByUserid(long userid);
	
	/**
	 * 根据资源ID查找资源
	 * @param resid
	 * @return
	 */
	public ResourceVo findResourceByResid(String resid);
	
}
