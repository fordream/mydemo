/**      
 * MessageVo.java Create on 2013-11-12     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.vo.local;

import java.io.Serializable;

/**
 * @ClassName: MessageVo
 * @Description: TODO(消息对象)
 * @author chuanglong
 * @date 2013-11-12 下午7:53:37
 * @Version 1.0
 * 
 */
public class MessageVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -557234927015863470L;
	/**
	 * 消息产生方为client
	 */
	public static final int SOURCE_CLIENT = 1;
	/**
	 * 消息产生方为server
	 */
	public static final int SOURCE_SERVER = 2;
	/**
	 * 消息状态 ： 已读
	 */
	public static final int READSTATUS_READ = 1;
	/**
	 * 消息状态：未读
	 */
	public static final int READSTATUS_UNREAD = 2;
	/**
	 * 消息发送状态： 发送中
	 */
	public static final int STATUS_SENDING = 1;
	/**
	 * 消息发送状态： 发送成功
	 */
	public static final int STATUS_SENDSUCC = 2;
	/**
	 * 消息发送状态： 发送失败
	 */
	public static final int STATUS_SENDERR = 3;

	/**
	 * 消息状态：删除
	 */
	public static final int STATUS_DEL = 4;

	/**
	 * 消息状态: from 是自己的时候不可见 (不需要，用del和正常替换
	 */
	// public static final int STATUS_FROMINVISIBLE = 5 ;

	/**
	 * 内部id
	 */
	private int id;
	/**
	 * 消息产生方类型 1:client,2:server
	 */
	private int source;
	/**
	 * 通道类型
	 */
	private String channelType;
	/**
	 * 消息id
	 */
	private long msgId;
	/**
	 * 发送者id
	 */
	private long fromId;
	/**
	 * 发送者domain
	 */
	private String fromDomain;
	/**
	 * 接受者id
	 */
	private long toId;
	/**
	 * 接受者domain
	 */
	private String toDomain;
	/**
	 * 主体id
	 */
	private long subjectId;
	/**
	 * 主体domain
	 */
	private String subjectDomain;
	/**
	 * 消息内容类别
	 */
	private String category;
	/**
	 * 消息内容摘要
	 */
	private String summary;
	/**
	 * 消息内容类型
	 */
	private int contentType;
	/**
	 * 消息内容
	 */
	private String content;
	/**
	 * 消息创建时间
	 */
	private long createTime;
	/**
	 * 发送消息时的位置
	 */
	private String position;
	/**
	 * 消息已读状态：1已读，2未读
	 */
	private int readStatus;

	/**
	 * 1：发送中（发送时有效）: 2：发送成功（发送时有效） 3：发送失败（发送时有效）
	 */
	private int status;

	/**
	 * 消息扩展数据 存储定义的操作码及相关信息
	 **/
	private String ext;

	/**
	 * 内容，当contentType类型指定是byte的数据时有效
	 */
	private byte[] contentBytes;

	private long forwardId = 0;
	private String forwardType = "";
	/** 当前消息应用类型 **/
	private String currnetApptype;
	/** 排序的权重 **/
	private int sortWeights;
	/** 消息来源 **/
	private String apptype;
	/** 消息扩展数据操作类型 **/
	private int estimatetype = -1;
	/** 消息扩展数据操作 **/
	private int estimateop = -1;
	/** 消息不通知 **/
	private int notNotify = 0;

	/**
	 * @return the forwardId
	 */
	public long getForwardId() {
		return forwardId;
	}

	/**
	 * @param forwardId
	 *            the forwardId to set
	 */
	public void setForwardId(long forwardId) {
		this.forwardId = forwardId;
	}

	/**
	 * @return the forwardType
	 */
	public String getForwardType() {
		return forwardType;
	}

	/**
	 * @param forwardType
	 *            the forwardType to set
	 */
	public void setForwardType(String forwardType) {
		this.forwardType = forwardType;
	}

	/**
	 * 消息在分类下的索引（目前为通道里面的索引）
	 */
	private long msgIndex;

	/**
	 * @return the msgIndex
	 */
	public long getMsgIndex() {
		return msgIndex;
	}

	/**
	 * @param msgIndex
	 *            the msgIndex to set
	 */
	public void setMsgIndex(long msgIndex) {
		this.msgIndex = msgIndex;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}

	public long getFromId() {
		return fromId;
	}

	public void setFromId(long fromId) {
		this.fromId = fromId;
	}

	public String getFromDomain() {
		return fromDomain;
	}

	public void setFromDomain(String fromDomain) {
		this.fromDomain = fromDomain;
	}

	public long getToId() {
		return toId;
	}

	public void setToId(long toId) {
		this.toId = toId;
	}

	public String getToDomain() {
		return toDomain;
	}

	public void setToDomain(String toDomain) {
		this.toDomain = toDomain;
	}

	public long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(long subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectDomain() {
		return subjectDomain;
	}

	public void setSubjectDomain(String subjectDomain) {
		this.subjectDomain = subjectDomain;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(int readStatus) {
		this.readStatus = readStatus;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageVo [id=" + id + ", source=" + source + ", channelType=" + channelType + ", msgId=" + msgId + ", fromId=" + fromId
				+ ", fromDomain=" + fromDomain + ", toId=" + toId + ", toDomain=" + toDomain + ", subjectId=" + subjectId + ", subjectDomain="
				+ subjectDomain + ", category=" + category + ", summary=" + summary + ", contentType=" + contentType + ", content=" + content
				+ ", createTime=" + createTime + ", position=" + position + ", readStatus=" + readStatus + ", status=" + status + ", contentBytes="
				+ ", forwardId=" + forwardId + ", forwardType=" + forwardType + ", isPlaying=" + isPlaying + ", isWaitStopPlaying="
				+ isWaitStopPlaying + ", ext=" + ext + ", estimateop=" + estimateop + ", estimatetype=" + estimatetype + "]";
	}

	/**
	 * @return the contentBytes
	 */
	public byte[] getContentBytes() {
		return contentBytes;
	}

	/**
	 * @param contentBytes
	 *            the contentBytes to set
	 */
	public void setContentBytes(byte[] contentBytes) {
		this.contentBytes = contentBytes;
	}

	/**
	 * 如果该内容为声音，是否正在播放，false 不在播放，true在播放
	 */
	private boolean isPlaying = false;

	/**
	 * @return the isPlaying
	 */
	public boolean isPlaying() {
		return isPlaying;
	}

	/**
	 * @param isPlaying
	 *            the isPlaying to set
	 */
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	/**
	 * 等待停止动画，只有当他为动画的是才有效 默认false，
	 */
	private boolean isWaitStopPlaying = false;

	/**
	 * @return the isStopPlaying
	 */
	public boolean isWaitStopPlaying() {
		return isWaitStopPlaying;
	}

	/**
	 * @param isStopPlaying
	 *            the isStopPlaying to set
	 */
	public void setWaitStopPlaying(boolean isStopPlaying) {
		this.isWaitStopPlaying = isStopPlaying;
	}

	public static int VOICESTATUS_OK = 1;// 正常状态
	public static int VOICESTATUS_DOWNLOADING = 2;// 下载中
	public static int VOICESTATUS_DOWNLOADERR = 3;// 下载失败
	/** 声音文件的状态 **/

	private int voiceStatus;

	/**
	 * @return the voiceStatus
	 */
	public int getVoiceStatus() {
		return voiceStatus;
	}

	/**
	 * @param voiceStatus
	 *            the voiceStatus to set
	 */
	public void setVoiceStatus(int voiceStatus) {
		this.voiceStatus = voiceStatus;
	}

	/**
	 * 未读数（只对首页统计部分有效）
	 */
	private int unReadCount = 0;

	/**
	 * @return the unReadCount
	 */
	public int getUnReadCount() {
		return unReadCount;
	}

	/**
	 * @param unReadCount
	 *            the unReadCount to set
	 */
	public void setUnReadCount(int unReadCount) {
		this.unReadCount = unReadCount;
	}

	public MessageVo() {

	}

	public MessageVo(MessageVo vo) {
		this.setId(vo.getId());
		this.setSource(vo.getSource());
		this.setChannelType(vo.getChannelType());
		this.setMsgId(vo.getMsgId());
		this.setFromId(vo.getFromId());
		this.setFromDomain(vo.getFromDomain());
		this.setToId(vo.getToId());
		this.setToDomain(vo.getToDomain());
		this.setSubjectId(vo.getSubjectId());
		this.setSubjectDomain(vo.getSubjectDomain());
		this.setCategory(vo.getCategory());
		this.setSummary(vo.getSummary());
		this.setContentType(vo.getContentType());
		this.setContent(vo.getContent());
		this.setCreateTime(vo.getCreateTime());
		this.setPosition(vo.getPosition());
		this.setReadStatus(vo.getReadStatus());
		this.setStatus(vo.getStatus());
		this.setForwardId(vo.getForwardId());
		this.setForwardType(vo.getForwardType());
		this.setMsgIndex(vo.getMsgIndex());
		this.setUnReadCount(vo.getUnReadCount());
	}

	public String getCurrnetApptype() {
		return currnetApptype;
	}

	public void setCurrnetApptype(String apptype) {
		this.currnetApptype = apptype;
	}

	public int getSortWeights() {
		return sortWeights;
	}

	public void setSortWeights(int sortWeights) {
		this.sortWeights = sortWeights;
	}

	public String getApptype() {
		return apptype;
	}

	public void setApptype(String apptype) {
		this.apptype = apptype;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public int getEstimatetype() {
		return estimatetype;
	}

	public void setEstimatetype(int estimatetype) {
		this.estimatetype = estimatetype;
	}

	public int getEstimateop() {
		return estimateop;
	}

	public void setEstimateop(int estimateop) {
		this.estimateop = estimateop;
	}

	public int getNotNotify() {
		return notNotify;
	}

	public void setNotNotify(int notNotify) {
		this.notNotify = notNotify;
	}

}
