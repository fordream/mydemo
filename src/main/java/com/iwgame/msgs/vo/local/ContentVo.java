package com.iwgame.msgs.vo.local;

import java.io.Serializable;

public class ContentVo  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6891773254585707758L;
    /**
     * id
     */
	private long id ;
	/**
	 * 内容发布者id
	 */
	private long publishingid;
	/**
	 * 内容发布者类型，MsgsConstants.DOMAIN_USERD等
	 */
	private String publishingtype;
	/**
	 * 内容id
	 */
	private long contentid;
	/**
	 * 内容
	 */
	private String content;
	/**
	 * 内容类型 ，
	 * MsgsConstants.OT_NEWS(3); //动态   
	 * MsgsConstants.OT_COMMENT(4); //评论
	 */
	private int type;
	/**
	 * 父内容id
	 */
	private long parentid ;
	/**
	 * 父内容类型 
	 * 当type = MsgsConstants.OT_NEWS(3); //动态    为0
	 *  当type = MsgsConstants.OT_COMMENT(4); //评论; 为动态OT_NEWS(3)或评论OT_COMMENT(4)
	 */
	private int parenttype ;
	/**
	 * 父亲内容的发布者
	 */
	private long parentpublishingid ;
	private String parentpublishingtype;
	/**
	 * 祖先(这条内容的最顶端）
	 */
	private long ancestorId;
	public long getAncestorId() {
		return ancestorId;
	}
	public void setAncestorId(long ancestorId) {
		this.ancestorId = ancestorId;
	}
	public int getAncestorType() {
		return ancestorType;
	}
	public void setAncestorType(int ancestorType) {
		this.ancestorType = ancestorType;
	}
	/**
	 * 祖先(这条内容的最顶端）
	 * 当type =MsgsConstants.OT_NEWS(3); //动态   为贴吧（OT_GAME(1)）或用户OT_USER(0)
     * 当 type =MsgsConstants.OT_COMMENT(4);//评论   为动态OT_NEWS(3)
	 */
	private int ancestorType;
	
	/**
	 * 祖先(这条内容的最顶端）的发布者
	 */
	private long ancestorpublishingid ;
	private String ancestorpublishingtype;

	public long getParentpublishingid() {
		return parentpublishingid;
	}
	public void setParentpublishingid(long parentpublishingid) {
		this.parentpublishingid = parentpublishingid;
	}
	public String getParentpublishingtype() {
		return parentpublishingtype;
	}
	public void setParentpublishingtype(String parentpublishingtype) {
		this.parentpublishingtype = parentpublishingtype;
	}
	public long getAncestorpublishingid() {
		return ancestorpublishingid;
	}
	public void setAncestorpublishingid(long ancestorpublishingid) {
		this.ancestorpublishingid = ancestorpublishingid;
	}
	public String getAncestorpublishingtype() {
		return ancestorpublishingtype;
	}
	public void setAncestorpublishingtype(String ancestorpublishingtype) {
		this.ancestorpublishingtype = ancestorpublishingtype;
	}
	/**
	 * 内容的状态 （暂时保留）
	 */
	private int status ;
	/**
	 * 内容创建时间
	 */
	private long createTime;
	/**
	 * 赞的次数
	 */
	private int praiseCount;
	/**
	 * 踩的次数
	 */
	private int treadCount;
	/**
	 * 评论的次数
	 */
	private int commentCount ;
	/**
	 * 转发的次数
	 */
	private int forwardCount ;
	/**
	 * 是否允许赞
	 */
	private int allowPraise;
	/**
	 * 是否允许踩
	 */
	private int allowTread;
	/**
	 * 是否允许评论
	 */
	private int allowComment;
	/**
	 * 是否允许转发
	 */
	private int allowForward;
	/**
	 * 当前用户是否已经赞过
	 */
	private int isPraise;
	/**
	 * 当前用户是否已经踩过
	 */
	private int isTread;
	/**
	 * 当前用户是否已经评论过
	 */
	private int isComment;
	/**
	 * 当前用户是否已经转发过
	 */
	private int isForward;

	/**
	 * 最后一次更新时间
	 */
	private long utime;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getPublishingid() {
		return publishingid;
	}
	public void setPublishingid(long publishingid) {
		this.publishingid = publishingid;
	}
	public String getPublishingtype() {
		return publishingtype;
	}
	public void setPublishingtype(String publishingtype) {
		this.publishingtype = publishingtype;
	}
	public long getContentid() {
		return contentid;
	}
	public void setContentid(long contentid) {
		this.contentid = contentid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getParentid() {
		return parentid;
	}
	public void setParentid(long parentid) {
		this.parentid = parentid;
	}
	public int getParenttype() {
		return parenttype;
	}
	public void setParenttype(int parenttype) {
		this.parenttype = parenttype;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public int getPraiseCount() {
		return praiseCount;
	}
	public void setPraiseCount(int praiseCount) {
		this.praiseCount = praiseCount;
	}
	public int getTreadCount() {
		return treadCount;
	}
	public void setTreadCount(int treadCount) {
		this.treadCount = treadCount;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public int getForwardCount() {
		return forwardCount;
	}
	public void setForwardCount(int forwardCount) {
		this.forwardCount = forwardCount;
	}
	public int getAllowPraise() {
		return allowPraise;
	}
	public void setAllowPraise(int allowPraise) {
		this.allowPraise = allowPraise;
	}
	public int getAllowTread() {
		return allowTread;
	}
	public void setAllowTread(int allowTread) {
		this.allowTread = allowTread;
	}
	public int getAllowComment() {
		return allowComment;
	}
	public void setAllowComment(int allowComment) {
		this.allowComment = allowComment;
	}
	public int getAllowForward() {
		return allowForward;
	}
	public void setAllowForward(int allowForward) {
		this.allowForward = allowForward;
	}
	public int getIsPraise() {
		return isPraise;
	}
	public void setIsPraise(int isPraise) {
		this.isPraise = isPraise;
	}
	public int getIsTread() {
		return isTread;
	}
	public void setIsTread(int isTread) {
		this.isTread = isTread;
	}
	public int getIsComment() {
		return isComment;
	}
	public void setIsComment(int isComment) {
		this.isComment = isComment;
	}
	public int getIsForward() {
		return isForward;
	}
	public void setIsForward(int isForward) {
		this.isForward = isForward;
	}
	public long getUtime() {
		return utime;
	}
	public void setUtime(long utime) {
		this.utime = utime;
	}
	@Override
	public String toString() {
		return "ContentVo [id=" + id + ", publishingid=" + publishingid
				+ ", publishingtype=" + publishingtype + ", contentid="
				+ contentid + ", content=" + content + ", type=" + type
				+ ", parentid=" + parentid + ", parenttype=" + parenttype
				+ ", ancestorId=" + ancestorId + ", ancestorType="
				+ ancestorType + ", status=" + status + ", createTime="
				+ createTime + ", praiseCount=" + praiseCount + ", treadCount="
				+ treadCount + ", commentCount=" + commentCount
				+ ", forwardCount=" + forwardCount + ", allowPraise="
				+ allowPraise + ", allowTread=" + allowTread
				+ ", allowComment=" + allowComment + ", allowForward="
				+ allowForward + ", isPraise=" + isPraise + ", isTread="
				+ isTread + ", isComment=" + isComment + ", isForward="
				+ isForward + ", utime=" + utime + "]";
	}
	
	
	
}
