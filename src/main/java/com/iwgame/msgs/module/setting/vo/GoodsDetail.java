package com.iwgame.msgs.module.setting.vo;

import java.io.Serializable;

/**
 * 商品详情实体类
 * @author jczhang
 *
 */
public class GoodsDetail implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5855263018550275939L;
	private long id;
	private String detailItem;
	private int type;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDetailItem() {
		return detailItem;
	}
	public void setDetailItem(String detailItem) {
		this.detailItem = detailItem;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
