/**      
* CurrentChatWindowsVo.java Create on 2013-11-4     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.chat.vo;



/** 
 * @ClassName: CurrentChatWindowsVo 
 * @Description: TODO(当前聊天窗口对象) 
 * @author 吴禹青
 * @date 2013-11-4 下午12:18:22 
 * @Version 1.0
 * 
 */
public class CurrentChatWindowsVo {
	
//	public static int TYPE_CHAT = 1 ;//人和人对聊
//	public static int TYPE_MCHAT = 2 ; //群聊
//	public static int TYPE_SYSTEM = 3 ; //系统通知

	private long toId ;
	private String toDomain;
	
	private String channelType;
	private String category;
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
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
}
