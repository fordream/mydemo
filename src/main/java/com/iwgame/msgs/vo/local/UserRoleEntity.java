package com.iwgame.msgs.vo.local;

import java.util.List;

import com.iwgame.msgs.proto.Msgs;

public class UserRoleEntity {
	
	private long id ;
	
	private List<Msgs.UserRoleData> list;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Msgs.UserRoleData> getList() {
		return list;
	}

	public void setList(List<Msgs.UserRoleData> list) {
		this.list = list;
	}
	
	
}
