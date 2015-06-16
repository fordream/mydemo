package com.iwgame.msgs.module.user.object;

import java.util.List;

import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr.ValData;

public class UserRoleAttr {
	private Long id;
	
	private String key;
	
	private String content;
	
	private int attrtype;
	
	private List <ValData> list;
	
	public List<ValData> getList() {
		return list;
	}

	public void setList(List<ValData> list) {
		this.list = list;
	}

	public int getAttrtype() {
		return attrtype;
	}

	public void setAttrtype(int attrtype) {
		this.attrtype = attrtype;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
