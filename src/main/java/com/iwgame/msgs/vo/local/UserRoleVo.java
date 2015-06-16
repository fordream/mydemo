package com.iwgame.msgs.vo.local;

import java.io.Serializable;
import java.util.List;

import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr;

public class UserRoleVo implements Serializable{
	private static final long serialVersionUID = 672756235278646456L;
	
	private long uid;
	
	private long roldId;
	
	private String avatar;
	
	private int status;
	
	private long gid;
	
	private List<RoleAttr> list;
	
	private List<String> values;
	
	
	public long getGid() {
		return gid;
	}

	public void setGid(long gid) {
		this.gid = gid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	
	
	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getRoldId() {
		return roldId;
	}

	public void setRoldId(long roldId) {
		this.roldId = roldId;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public List<RoleAttr> getList() {
		return list;
	}

	public void setList(List<RoleAttr> list) {
		this.list = list;
	}

	}