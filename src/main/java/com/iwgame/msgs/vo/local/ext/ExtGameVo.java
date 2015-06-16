/**      
 * ExtGameVo.java Create on 2013-9-3     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.vo.local.ext;

import com.iwgame.msgs.vo.local.GameVo;

/**
 * @ClassName: ExtGameVo
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-9-3 上午11:56:25
 * @Version 1.0
 * 
 */
public class ExtGameVo extends GameVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -830170694749703204L;
	// 是否关注
	private boolean follow;
	// 有多少个好友关注
	private int userCount;
	// 关注的好友昵称
	private String nickname;
	// 距离
	private int distance;
	// 分页排序ID
	private long sortId;
	

	// 关注人数
	private int followCount;
	// 帖子最大index
	private long postbarMaxIndex;
	//今日帖子数
	private int postCount ;


	// 附近关注
	private int nearFollowCount;
	// 是否选中
	private boolean isSelected = false;

	public long getSortId() {
		return sortId;
	}

	public void setSortId(long sortId) {
		this.sortId = sortId;
	}

	/**
	 * @return the follow
	 */
	public boolean getFollow() {
		return follow;
	}

	/**
	 * @param follow
	 *            the follow to set
	 */
	public void setFollow(boolean follow) {
		this.follow = follow;
	}

	/**
	 * @return the userCount
	 */
	public int getUserCount() {
		return userCount;
	}

	/**
	 * @param userCount
	 *            the userCount to set
	 */
	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname
	 *            the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getFollowCount() {
		return followCount;
	}

	public void setFollowCount(int followCount) {
		this.followCount = followCount;
	}



	public int getNearFollowCount() {
		return nearFollowCount;
	}

	public void setNearFollowCount(int nearFollowCount) {
		this.nearFollowCount = nearFollowCount;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	/**
	 * @return the postbarMaxIndex
	 */
	public long getPostbarMaxIndex() {
	    return postbarMaxIndex;
	}

	/**
	 * @param postbarMaxIndex the postbarMaxIndex to set
	 */
	public void setPostbarMaxIndex(long postbarMaxIndex) {
	    this.postbarMaxIndex = postbarMaxIndex;
	}
	
	
	/**
	 * @return the postCount
	 */
	public int getPostCount() {
	    return postCount;
	}

	/**
	 * @param postCount the postCount to set
	 */
	public void setPostCount(int postCount) {
	    this.postCount = postCount;
	}

}
