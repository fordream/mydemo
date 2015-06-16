package com.iwgame.msgs.vo.local.ext;

import java.io.Serializable;

import com.iwgame.msgs.vo.local.ContentVo;

public class ExtContentVo extends ContentVo implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8133662160401405147L;
	
	/**
	 * 是否显示日期
	 */
	private boolean isShowDate;
	

	public boolean isShowDate() {
		return isShowDate;
	}

	public void setShowDate(boolean isShowDate) {
		this.isShowDate = isShowDate;
	}

	public ExtContentVo(ContentVo vo)
	{
		this.setId(vo.getId());
		this.setPublishingid(vo.getPublishingid());
		this.setPublishingtype(vo.getPublishingtype());
		this.setContentid(vo.getContentid());
		this.setContent(vo.getContent());
		this.setType(vo.getType());
		this.setParentid(vo.getParentid());
		this.setParenttype(vo.getParenttype());
		this.setParentpublishingid(vo.getParentpublishingid());
		this.setParentpublishingtype(vo.getParentpublishingtype());
		this.setAncestorId(vo.getAncestorId());
		this.setAncestorType(vo.getAncestorType());
		this.setAncestorpublishingid(vo.getAncestorpublishingid());
		this.setAncestorpublishingtype(vo.getAncestorpublishingtype());
		this.setStatus(vo.getStatus());
		this.setCreateTime(vo.getCreateTime());
		this.setPraiseCount(vo.getPraiseCount());
		this.setTreadCount(vo.getTreadCount());
		this.setCommentCount(vo.getCommentCount());
		this.setForwardCount(vo.getForwardCount());
		this.setAllowPraise(vo.getAllowPraise());
		this.setAllowTread(vo.getAllowTread());
		this.setAllowComment(vo.getAllowComment());
		this.setAllowForward(vo.getAllowForward());
		this.setIsPraise(vo.getIsPraise());
		this.setIsTread(vo.getIsTread());
		this.setIsComment(vo.getIsComment());
		this.setIsForward(vo.getIsForward());
		
	}
	
	@Override
	public String toString() {
		return "ExtContentVo [isShowDate=" + isShowDate + ", toString()="
				+ super.toString() + "]";
	}
}
