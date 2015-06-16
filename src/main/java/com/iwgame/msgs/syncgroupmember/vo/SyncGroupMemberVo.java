package com.iwgame.msgs.syncgroupmember.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iwgame.msgs.module.remote.SearchRemoteService;
import com.iwgame.msgs.vo.local.GroupUserRelVo;

/**
 * 同步公会成员 成功后
 * 保存到这个对象里面
 * @author jczhang
 *
 */
public class SyncGroupMemberVo implements Serializable{

	
	private static final long serialVersionUID = -9167557200962435326L;
	Map<String, List<GroupUserRelVo>> groupuserList = new HashMap<String, List<GroupUserRelVo>>();
	
	
	
	public Map<String, List<GroupUserRelVo>> getGroupuserList() {
		return groupuserList;
	}
	public void setGroupuserList(Map<String, List<GroupUserRelVo>> groupuserList) {
		this.groupuserList = groupuserList;
	}
}
