/**      
* GameExtDataVo.java Create on 2014-4-23     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.game.object;

/** 
 * @ClassName: GameExtDataVo 
 * @Description: TODO(贴吧的扩展数据) 
 * @author chuanglong
 * @date 2014-4-23 上午10:03:48 
 * @Version 1.0
 * 
 */
public class GameExtDataVo {
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
   * 新闻帖子数
   */
    private int newsPostCount ;
  /**
   * 攻略帖子数
   */
    private int strategyPostCount ;
    /**
     * 赞的次数
     */
    private int praise ; 

    /**
     * 踩的次数
     */
    private int criticize ;
    /**
     * 评论的个数(暂时未用）
     */
    private int comment ;
    /**
     * 是否赞过
     */
    private int ispraise ;
    /**
     * 是否踩过
     */
    private int iscriticize ;
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
    /**
     * @return the groupCount
     */
    public int getGroupCount() {
        return groupCount;
    }
    /**
     * @param groupCount the groupCount to set
     */
    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }
    /**
     * @return the newsPostCount
     */
    public int getNewsPostCount() {
        return newsPostCount;
    }
    /**
     * @param newsPostCount the newsPostCount to set
     */
    public void setNewsPostCount(int newsPostCount) {
        this.newsPostCount = newsPostCount;
    }
    /**
     * @return the strategyPostCount
     */
    public int getStrategyPostCount() {
        return strategyPostCount;
    }
    /**
     * @param strategyPostCount the strategyPostCount to set
     */
    public void setStrategyPostCount(int strategyPostCount) {
        this.strategyPostCount = strategyPostCount;
    }
    /**
     * @return the praise
     */
    public int getPraise() {
        return praise;
    }
    /**
     * @param praise the praise to set
     */
    public void setPraise(int praise) {
        this.praise = praise;
    }
    /**
     * @return the criticize
     */
    public int getCriticize() {
        return criticize;
    }
    /**
     * @param criticize the criticize to set
     */
    public void setCriticize(int criticize) {
        this.criticize = criticize;
    }
    /**
     * @return the comment
     */
    public int getComment() {
        return comment;
    }
    /**
     * @param comment the comment to set
     */
    public void setComment(int comment) {
        this.comment = comment;
    }
    /**
     * @return the ispraise
     */
    public int getIspraise() {
        return ispraise;
    }
    /**
     * @param ispraise the ispraise to set
     */
    public void setIspraise(int ispraise) {
        this.ispraise = ispraise;
    }
    /**
     * @return the iscriticize
     */
    public int getIscriticize() {
        return iscriticize;
    }
    /**
     * @param iscriticize the iscriticize to set
     */
    public void setIscriticize(int iscriticize) {
        this.iscriticize = iscriticize;
    }
    
    /**
     * 是否是吧主
     */
    private boolean isBarMaster;
    /**
     * @return the isBarMaster
     */
    public boolean isBarMaster() {
        return isBarMaster;
    }
    /**
     * @param isBarMaster the isBarMaster to set
     */
    public void setBarMaster(boolean isBarMaster) {
        this.isBarMaster = isBarMaster;
    }
    
    
    



    

}
