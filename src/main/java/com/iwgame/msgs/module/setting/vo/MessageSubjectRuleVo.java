/**      
* SubjectRuleVo.java Create on 2014-5-22     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.setting.vo;


/** 
 * @ClassName: SubjectRuleVo 
 * @Description: TODO(消息主体的规则) 
 * @author chuanglong
 * @date 2014-5-22 下午6:00:27 
 * @Version 1.0
 * 
 */
public class MessageSubjectRuleVo {
    public static String ITEM_RESULT_FROMORTO ="fromorto";
    public static String ITEM_RESULT_FROM ="from";
    public static String ITEM_RESULT_TO ="to";

	String channelType;//消息系统中channeltype
	String category; //消息系统中categrory
	String result;
	/**
	 * @return the channelType
	 */
	public String getChannelType() {
	    return channelType;
	}
	/**
	 * @param channelType the channelType to set
	 */
	public void setChannelType(String channelType) {
	    this.channelType = channelType;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
	    return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
	    this.category = category;
	}
	/**
	 * @return the result
	 */
	public String getResult() {
	    return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
	    this.result = result;
	} 
}
