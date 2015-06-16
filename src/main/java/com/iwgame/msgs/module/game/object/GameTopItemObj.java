/**      
* GameTopItemVo.java Create on 2013-10-8     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.game.object;

import java.io.Serializable;

import com.iwgame.msgs.proto.Msgs.PostbarTopicDetail;
import com.iwgame.msgs.vo.local.GameVo;

/** 
 * @ClassName: GameTopItemVo 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2013-10-8 下午08:33:06 
 * @Version 1.0
 * 
 */
public class GameTopItemObj implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7694809334885513766L;

	private long gid;
	
	private int total;
	
	private String nickname;
	
	private GameVo gameVo;
	
	private boolean follow;
	
	private int praise;
	
	private int criticize;
	
	private String gameLogo;

	/**
	 * @return the gid
	 */
	public long getGid() {
		return gid;
	}

	/**
	 * @param gid the gid to set
	 */
	public void setGid(long gid) {
		this.gid = gid;
	}

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the gameVo
	 */
	public GameVo getGameVo() {
		return gameVo;
	}
	/**
	 * @param gameVo the gameVo to set
	 */
	public void setGameVo(GameVo gameVo) {
		this.gameVo = gameVo;
	}

	public boolean isFollow() {
		return follow;
	}

	public void setFollow(boolean follow) {
		this.follow = follow;
	}

	public int getPraise() {
		return praise;
	}

	public void setPraise(int praise) {
		this.praise = praise;
	}

	public int getCriticize() {
		return criticize;
	}

	public void setCriticize(int criticize) {
		this.criticize = criticize;
	}
	
	PostbarTopicDetail  hotTopic ;

	/**
	 * @return the topic
	 */
	public PostbarTopicDetail getHotTopic() {
	    return hotTopic;
	}

	/**
	 * @param topic the topic to set
	 */
	public void setHotTopic(PostbarTopicDetail hotTopic) {
	    this.hotTopic = hotTopic;
	}
	
	/**
	 * 总帖数
	 */
	private int topicCount;
	/**
	 * 今日帖数
	 */
	private int dailyTopicCount;
	/**
	 * 关注人数
	 */
	private int followCount ;

	/**
	 * 贴吧访问数
	 */
	private long visitCount;
	/**
	 * @return the topicCount
	 */
	public int getTopicCount() {
	    return topicCount;
	}

	/**
	 * @param topicCount the topicCount to set
	 */
	public void setTopicCount(int topicCount) {
	    this.topicCount = topicCount;
	}

	/**
	 * @return the dailyTopicCount
	 */
	public int getDailyTopicCount() {
	    return dailyTopicCount;
	}

	/**
	 * @param dailyTopicCount the dailyTopicCount to set
	 */
	public void setDailyTopicCount(int dailyTopicCount) {
	    this.dailyTopicCount = dailyTopicCount;
	}

	/**
	 * @return the followCount
	 */
	public int getFollowCount() {
	    return followCount;
	}

	/**
	 * @param followCount the followCount to set
	 */
	public void setFollowCount(int followCount) {
	    this.followCount = followCount;
	}

	
	public long getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(long visitCount) {
		this.visitCount = visitCount;
	}

	public String getGameLogo() {
		return gameLogo;
	}

	public void setGameLogo(String gameLogo) {
		this.gameLogo = gameLogo;
	}
	
}
