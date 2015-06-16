package com.iwgame.msgs.adaptiveconfig;

public class FollowMenuVo {
    //标记是否显示菜单的
	private boolean isShow;
	//菜单的名字
	private String menuName;
	//游戏的id  如果id为0 表示关注了全部的用户， 如果id为-1，表示的是游伴用户，如果id > 0表示的是关注了真实游戏的id
	private Long gid;
	public boolean isShow() {
		return isShow;
	}
	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public Long getGid() {
		return gid;
	}
	public void setGid(Long gid) {
		this.gid = gid;
	}
	
}
