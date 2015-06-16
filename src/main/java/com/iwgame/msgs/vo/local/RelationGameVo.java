package com.iwgame.msgs.vo.local;

import java.io.Serializable;

public class RelationGameVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4885559839961652204L;

	private long id;
	private long gameid;
	// 与本地用户的关系 1：关注，2：取消关注
	private int relation;
	private long lastupdatetime;
	// 是否吧主[0:不是1:是]
	private int isbarmanager;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getGameid() {
		return gameid;
	}

	public void setGameid(long gameid) {
		this.gameid = gameid;
	}

	public int getRelation() {
		return relation;
	}

	public void setRelation(int relation) {
		this.relation = relation;
	}

	public long getLastupdatetime() {
		return lastupdatetime;
	}

	public void setLastupdatetime(long lastupdatetime) {
		this.lastupdatetime = lastupdatetime;
	}

	public RelationGameVo() {

	}

	public RelationGameVo(long gameid, int relation, long lastupdatetime) {
		this.gameid = gameid;
		this.relation = relation;
		this.lastupdatetime = lastupdatetime;
	}

	public int getIsbarmanager() {
		return isbarmanager;
	}

	public void setIsbarmanager(int isbarmanager) {
		this.isbarmanager = isbarmanager;
	}

	@Override
	public String toString() {
		return "RelationGameVo [id=" + id + ", gameid=" + gameid + ", relation=" + relation + ", lastupdatetime=" + lastupdatetime + "]";
	}

}
