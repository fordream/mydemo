package com.iwgame.msgs.vo.local;

import java.io.Serializable;

public class ChannelVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 86863716012774379L;

	/**
	 * 内部id
	 */
	long id ;
	/**
	 * 通道类型
	 */
	String channelType;
	/**
	 * 通道id
	 */
	int channelId;
	/**
	 * 通道编码
	 */
	String channelCode;
	

	/**
	 * 通道中已经同步到本地的数据的最新内容索引值
	 */
	int syncSeqNo;
	/**
	 * 订阅该通道id时的索引值
	 */
	int contentStartSeqNo;
	/**
	 * 当前我已经查看到的内容索引值
	 */
	int currentReadSeqNo;
	/**
	 * 当前通道中最新的内容索引值
	 */
	int currentContentSeqNo;
	
	/**
	 * 当前通道中最新的内容索引值对应的时间戳
	 */
	long currentContentTimeStamp;
	
	/**
	 * 通道状态 ，1正常（true），0停用（false）
	 */
	int status ;
	
	
	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getChannelType() {
		return channelType;
	}


	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}


	public int getChannelId() {
		return channelId;
	}


	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}


	public int getSyncSeqNo() {
		return syncSeqNo;
	}


	public void setSyncSeqNo(int syncSeqNo) {
		this.syncSeqNo = syncSeqNo;
	}


	public int getContentStartSeqNo() {
		return contentStartSeqNo;
	}


	public void setContentStartSeqNo(int contentStartSeqNo) {
		this.contentStartSeqNo = contentStartSeqNo;
	}


	public int getCurrentReadSeqNo() {
		return currentReadSeqNo;
	}


	public void setCurrentReadSeqNo(int currentReadSeqNo) {
		this.currentReadSeqNo = currentReadSeqNo;
	}


	public int getCurrentContentSeqNo() {
		return currentContentSeqNo;
	}


	public void setCurrentContentSeqNo(int currentContentSeqNo) {
		this.currentContentSeqNo = currentContentSeqNo;
	}


	public long getCurrentContentTimeStamp() {
		return currentContentTimeStamp;
	}


	public void setCurrentContentTimeStamp(long currentContentTimeStamp) {
		this.currentContentTimeStamp = currentContentTimeStamp;
	}


	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getChannelCode() {
		return channelCode;
	}


	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}



	@Override
	public String toString() {
		return "ChannelVo [id=" + id + ", channelType=" + channelType + ", channelId=" + channelId + ", channelCode=" + channelCode
				+ ", syncSeqNo=" + syncSeqNo + ", contentStartSeqNo=" + contentStartSeqNo + ", currentReadSeqNo=" + currentReadSeqNo
				+ ", currentContentSeqNo=" + currentContentSeqNo + ", currentContentTimeStamp=" + currentContentTimeStamp + ", status="
				+ status + "]";
	}
	
	
}
