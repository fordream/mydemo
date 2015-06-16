/**      
 * AreaDao.java Create on 2013-9-16     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.localdb.dao;

import java.util.List;

import com.iwgame.msgs.vo.local.AreaVo;

/**
 * @ClassName: AreaDao
 * @Description: 地区DAO
 * @author 王卫
 * @date 2013-9-16 下午07:47:51
 * @Version 1.0
 * 
 */
public interface AreaDao {

	/**
	 * 根据类型查找地区（省）
	 * 
	 * @param types
	 * @return
	 */
	public List<AreaVo> findAreaByType(String[] types);
	
	/**
	 * 根据父级地区ID（省ID）查找城市
	 * 
	 * @param id
	 * @return
	 */
	public List<AreaVo> findAreaByParentid(int pid);

}
