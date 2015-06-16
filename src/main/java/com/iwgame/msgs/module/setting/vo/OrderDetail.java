package com.iwgame.msgs.module.setting.vo;

/**
 * 订单详情实体类
 * @author jczhang
 *
 */
public class OrderDetail {
	private int status;
	private String orderInfo;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getOrderInfo() {
		return orderInfo;
	}
	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}
	
}
