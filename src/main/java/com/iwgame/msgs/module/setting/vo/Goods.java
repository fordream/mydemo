package com.iwgame.msgs.module.setting.vo;

import java.io.Serializable;
import java.util.List;



/**
 * 商品的实体类
 * @author jczhang
 *
 */
public class Goods implements Serializable{

	private static final long serialVersionUID = 6783297778085007451L;
	private long id;
	private String name;
	private int type;
	private String icon;
	private int remainNum;
	private int obtainNum;
	private int deliveryNum;//发货类型
	private int needPoint;
	private int weight;//权重
	private int needLevel;
	private long transTimes;//兑换次数
	private long showTime;
	private long transTime;//兑换时间
	private long offTime;//下架时间 
	private long deliveryTemplateID;//发货模板
	private long transTemplateID;//兑换模板
	private int goodsStatus;//商品状态
	private List<GoodsDetail> detail;
	



	
	public List<GoodsDetail> getDetail() {
		return detail;
	}
	public void setDetail(List<GoodsDetail> detail) {
		this.detail = detail;
	}
	public long getDeliveryTemplateID() {
		return deliveryTemplateID;
	}
	public void setDeliveryTemplateID(long deliveryTemplateID) {
		this.deliveryTemplateID = deliveryTemplateID;
	}
	public long getTransTemplateID() {
		return transTemplateID;
	}
	public void setTransTemplateID(long transTemplateID) {
		this.transTemplateID = transTemplateID;
	}
	public int getGoodsStatus() {
		return goodsStatus;
	}
	public void setGoodsStatus(int goodsStatus) {
		this.goodsStatus = goodsStatus;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public int getRemainNum() {
		return remainNum;
	}
	public void setRemainNum(int remainNum) {
		this.remainNum = remainNum;
	}
	public int getObtainNum() {
		return obtainNum;
	}
	public void setObtainNum(int obtainNum) {
		this.obtainNum = obtainNum;
	}
	public int getDeliveryNum() {
		return deliveryNum;
	}
	public void setDeliveryNum(int deliveryNum) {
		this.deliveryNum = deliveryNum;
	}
	public int getNeedPoint() {
		return needPoint;
	}
	public void setNeedPoint(int needPoint) {
		this.needPoint = needPoint;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getNeedLevel() {
		return needLevel;
	}
	public void setNeedLevel(int needLevel) {
		this.needLevel = needLevel;
	}
	public long getTransTimes() {
		return transTimes;
	}
	public void setTransTimes(long transTimes) {
		this.transTimes = transTimes;
	}
	public long getShowTime() {
		return showTime;
	}
	public void setShowTime(long showTime) {
		this.showTime = showTime;
	}
	public long getTransTime() {
		return transTime;
	}
	public void setTransTime(long transTime) {
		this.transTime = transTime;
	}
	public long getOffTime() {
		return offTime;
	}
	public void setOffTime(long offTime) {
		this.offTime = offTime;
	}
	
	
}
