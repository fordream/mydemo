package com.iwgame.msgs.vo.local;

import java.io.Serializable;
import java.util.List;

import com.iwgame.msgs.proto.Msgs.GameKeysDetail;

public class GameKeyVo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id ;
	
	private long gid;
	
	private int type;
	
	private String name;
	
	private List<GameKeysDetail> list;
	
	private int attrType;

	public int getAttrType() {
		return attrType;
	}

	public void setAttrType(int attrType) {
		this.attrType = attrType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getGid() {
		return gid;
	}

	public void setGid(long gid) {
		this.gid = gid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<GameKeysDetail> getList() {
		return list;
	}

	public void setList(List<GameKeysDetail> list) {
		this.list = list;
	}
	
	
}
