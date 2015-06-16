/**      
* ExcUserVo.java Create on 2013-8-23     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.vo.local.ext;

import com.iwgame.msgs.vo.local.UserVo;

/** 
 * @ClassName: ExcUserVo 
 * @Description: 用户扩展对象
 * @author 王卫
 * @date 2013-8-23 上午11:57:40 
 * @Version 1.0
 * 
 */
public class ExtUserVo extends UserVo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4462829979083649458L;
	//共同关注的贴吧数
	private int alikeFollowNum;
	
	private int postCount;
	private int gameCount;
	private int groupCount;
	private int favoritesCount;
	private int followCount;
	private int fansCount;
    private long createTime;
    private int albumCount;//相册图片数量
    
	public int getAlbumCount() {
		return albumCount;
	}

	public void setAlbumCount(int albumCount) {
		this.albumCount = albumCount;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getPostCount() {
		return postCount;
	}

	public void setPostCount(int postCount) {
		this.postCount = postCount;
	}

	public int getGameCount() {
		return gameCount;
	}

	public void setGameCount(int gameCount) {
		this.gameCount = gameCount;
	}

	public int getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}

	public int getFavoritesCount() {
		return favoritesCount;
	}

	public void setFavoritesCount(int favoritesCount) {
		this.favoritesCount = favoritesCount;
	}

	public int getFollowCount() {
		return followCount;
	}

	public void setFollowCount(int followCount) {
		this.followCount = followCount;
	}

	public int getFansCount() {
		return fansCount;
	}

	public void setFansCount(int fansCount) {
		this.fansCount = fansCount;
	}

	/**
	 * @return the alikeFollowNum
	 */
	public int getAlikeFollowNum() {
		return alikeFollowNum;
	}

	/**
	 * @param alikeFollowNum the alikeFollowNum to set
	 */
	public void setAlikeFollowNum(int alikeFollowNum) {
		this.alikeFollowNum = alikeFollowNum;
	}

}
