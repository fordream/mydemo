/**      
* PlayVo.java Create on 2015-5-8     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.play.object;

import java.util.List;

import com.iwgame.msgs.proto.Msgs.GameRole;
import com.iwgame.msgs.proto.Msgs.GameServerEntry;
import com.iwgame.msgs.proto.Msgs.UserInfoDetail;
import com.iwgame.msgs.proto.Msgs.UserRoleData;

/** 
 * @ClassName: PlayVo 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2015-5-8 下午3:30:57 
 * @Version 1.0
 * 
 */
public class PlayVo {
	
	
	private long playid;
	
	private long gid;
	
	private String sids;
	
	private int costs;
	
	private String remark;
	
	private String mobile;
	
	private String qq;
	
	private String resourcdid;
	
	private long roleid;
	
	private long sid;
	
	private UserRoleData roleData;
	
	private int status;
	
	private long uid;
	
	private String gamename;
	
	private String servername;
	
	private GameRole gamerole;
	
	private List<GameServerEntry> gameserver;
	
	private UserInfoDetail userInfo;
	
	private long createtime;
	
	
	
	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public long getUid() {
		return uid;
	}


	public void setUid(long uid) {
		this.uid = uid;
	}


	public String getGamename() {
		return gamename;
	}


	public void setGamename(String gamename) {
		this.gamename = gamename;
	}


	public String getServername() {
		return servername;
	}


	public void setServername(String servername) {
		this.servername = servername;
	}


	public GameRole getGamerole() {
		return gamerole;
	}


	public void setGamerole(GameRole gamerole) {
		this.gamerole = gamerole;
	}


	public List<GameServerEntry> getGameserver() {
		return gameserver;
	}


	public void setGameserver(List<GameServerEntry> gameserver) {
		this.gameserver = gameserver;
	}


	public UserInfoDetail getUserInfo() {
		return userInfo;
	}


	public void setUserInfo(UserInfoDetail userInfo) {
		this.userInfo = userInfo;
	}


	public long getCreatetime() {
		return createtime;
	}


	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}


	public long getPlayid() {
		return playid;
	}


	public void setPlayid(long playid) {
		this.playid = playid;
	}


	public long getGid() {
		return gid;
	}


	public void setGid(long gid) {
		this.gid = gid;
	}


	public String getSids() {
		return sids;
	}


	public void setSids(String sids) {
		this.sids = sids;
	}


	public int getCosts() {
		return costs;
	}


	public void setCosts(int costs) {
		this.costs = costs;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public String getQq() {
		return qq;
	}


	public void setQq(String qq) {
		this.qq = qq;
	}


	public String getResourcdid() {
		return resourcdid;
	}


	public void setResourcdid(String resourcdid) {
		this.resourcdid = resourcdid;
	}


	public long getRoleid() {
		return roleid;
	}


	public void setRoleid(long roleid) {
		this.roleid = roleid;
	}


	public long getSid() {
		return sid;
	}


	public void setSid(long sid) {
		this.sid = sid;
	}


	public UserRoleData getRoleData() {
		return roleData;
	}


	public void setRoleData(UserRoleData roleData) {
		this.roleData = roleData;
	}


	
	
	
}
