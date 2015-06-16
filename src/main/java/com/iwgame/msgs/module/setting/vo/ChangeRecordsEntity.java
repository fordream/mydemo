package com.iwgame.msgs.module.setting.vo;


/**
 * 兑换记录的
 * 实体类 扩展数据
 * @author jczhang
 *
 */
public class ChangeRecordsEntity{
	
	private long id;
	private String goodsName;
	private int type;
	private String icon;
	private int deliveryType;
	private int needPrice;
	private long transTime;
	private long deliveryTime;
	private long deliveryDetail;
	private long transDetail;
	private long goodsId;
	private int status = -1;//商品的状态
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public int getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(int deliveryType) {
		this.deliveryType = deliveryType;
	}
	public int getNeedPrice() {
		return needPrice;
	}
	public void setNeedPrice(int needPrice) {
		this.needPrice = needPrice;
	}
	public long getTransTime() {
		return transTime;
	}
	public void setTransTime(long transTime) {
		this.transTime = transTime;
	}
	public long getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(long deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	public long getDeliveryDetail() {
		return deliveryDetail;
	}
	public void setDeliveryDetail(long deliveryDetail) {
		this.deliveryDetail = deliveryDetail;
	}
	public long getTransDetail() {
		return transDetail;
	}
	public void setTransDetail(long transDetail) {
		this.transDetail = transDetail;
	}
	
}
