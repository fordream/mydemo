/**      
* GameExtDataVo.java Create on 2014-4-23     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.game.object;

import java.util.List;

import com.iwgame.msgs.vo.local.TopicTagVo;

/** 
 * @ClassName: GameExtDataVo 
 * @Description: TODO(贴吧的扩展数据) 
 * @author chuanglong
 * @date 2014-4-23 上午10:03:48 
 * @Version 1.0
 * 
 */
public class GameData {
    /**
     * 贴吧id
     */
    private long gid ;
    /**
     * 游伴数
     */
    private int followCount ;
    /**
     * 公会数
     */
    private int groupCount ;
    /**
     * 赞的次数
     */
    private int praise ; 
    /**
     * 踩的次数
     */
    private int criticize ;
    /**
     * 是否赞过
     */
    private int ispraise ;
    /**
     * 是否踩过
     */
    private int iscriticize ;
    /**
     * 贴吧名称
     */
	private String gamename;
	/**
	 * 贴吧logo
	 */
	private String gamelogo;
	/**
	 * 与本地用户的关系 1：关注，2：取消关注
	 */
	private int relation;
	/**
	 * 是否吧主[0:不是1:是]
	 */
	private int isbarmanager;
	
	private List<TopicTagVo> tags;
	
	private boolean isGetExtraData;
	
	private boolean isGetGameInfo;
	/** 新闻数 **/
	private int newsCount;
	/** 攻略数 **/
	private int raidersCount;
	
	public long getGid() {
		return gid;
	}
	public void setGid(long gid) {
		this.gid = gid;
	}
	public int getFollowCount() {
		return followCount;
	}
	public void setFollowCount(int followCount) {
		this.followCount = followCount;
	}
	public int getGroupCount() {
		return groupCount;
	}
	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
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
	public int getIspraise() {
		return ispraise;
	}
	public void setIspraise(int ispraise) {
		this.ispraise = ispraise;
	}
	public int getIscriticize() {
		return iscriticize;
	}
	public void setIscriticize(int iscriticize) {
		this.iscriticize = iscriticize;
	}
	public String getGamename() {
		return gamename;
	}
	public void setGamename(String gamename) {
		this.gamename = gamename;
	}
	public String getGamelogo() {
		return gamelogo;
	}
	public void setGamelogo(String gamelogo) {
		this.gamelogo = gamelogo;
	}
	public int getRelation() {
		return relation;
	}
	public void setRelation(int relation) {
		this.relation = relation;
	}
	public int getIsbarmanager() {
		return isbarmanager;
	}
	public void setIsbarmanager(int isbarmanager) {
		this.isbarmanager = isbarmanager;
	}
	public List<TopicTagVo> getTags() {
		return tags;
	}
	public void setTags(List<TopicTagVo> tags) {
		this.tags = tags;
	}
	public boolean isGetExtraData() {
		return isGetExtraData;
	}
	public void setGetExtraData(boolean isGetExtraData) {
		this.isGetExtraData = isGetExtraData;
	}
	public boolean isGetGameInfo() {
		return isGetGameInfo;
	}
	public void setGetGameInfo(boolean isGetGameInfo) {
		this.isGetGameInfo = isGetGameInfo;
	}
	public int getNewsCount() {
		return newsCount;
	}
	public void setNewsCount(int newsCount) {
		this.newsCount = newsCount;
	}
	public int getRaidersCount() {
		return raidersCount;
	}
	public void setRaidersCount(int raidersCount) {
		this.raidersCount = raidersCount;
	}



    

}
