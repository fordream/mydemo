/**      
* ShowChannelsRuleVo.java Create on 2014-5-22     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.setting.vo;

/** 
 * @ClassName: ShowChannelsRuleVo 
 * @Description: TODO(前端频道对象) 
 * @author chuanglong
 * @date 2014-5-22 下午5:59:06 
 * @Version 1.0
 * 
 */
public class ChannelVo extends ShowChannelsRuleVo {

	/**
	 * 类型
	 */
	String type ;
	/**
	 * 频道类型	对应着消息中的频率类型
	 */
        String channelType;
        /**
         * 消息内容类别	对应着消息中的消息内容类别
         */
        String  category ;
        /**
         * 是否允许删除	
           false：.不允许
           true：. 允许 （默认）
         */
        boolean isAllowDel;
        /**
         * 是否使用频道的名称和icon
         * 
         * false:不使用，使用subjectid去获得主体来显示
         *       true：使用channelname和 channelicon来显示
         */
        boolean isUseChannel;	
        /**
         * 主体id
         */
        long subjectid ;
        /**
         * 主体domaim	对应消息系统中的domain
         */
        String subjectdomain;
        /**
         * 频道显示的名称,该值为系统配置或根据subjectid得到
         */
        String name;
        /**
         * 频道显示的icon	，该值为系统配置或根据subjectid得到	
         */
        String icon;
        /**
         * 打开页面类型
         */
        String pagetype;
	/**
	 * @return the type
	 */
	public String getType() {
	    return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
	    this.type = type;
	}
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
	 * @return the isAllowDel
	 */
	public boolean isAllowDel() {
	    return isAllowDel;
	}
	/**
	 * @param isAllowDel the isAllowDel to set
	 */
	public void setAllowDel(boolean isAllowDel) {
	    this.isAllowDel = isAllowDel;
	}
	/**
	 * @return the isUseChannel
	 */
	public boolean isUseChannel() {
	    return isUseChannel;
	}
	/**
	 * @param isUseChannel the isUseChannel to set
	 */
	public void setUseChannel(boolean isUseChannel) {
	    this.isUseChannel = isUseChannel;
	}
	/**
	 * @return the subjectid
	 */
	public long getSubjectid() {
	    return subjectid;
	}
	/**
	 * @param subjectid the subjectid to set
	 */
	public void setSubjectid(long subjectid) {
	    this.subjectid = subjectid;
	}
	/**
	 * @return the subjectdomain
	 */
	public String getSubjectdomain() {
	    return subjectdomain;
	}
	/**
	 * @param subjectdomain the subjectdomain to set
	 */
	public void setSubjectdomain(String subjectdomain) {
	    this.subjectdomain = subjectdomain;
	}
	/**
	 * @return the name
	 */
	public String getName() {
	    return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
	    this.name = name;
	}
	/**
	 * @return the icon
	 */
	public String getIcon() {
	    return icon;
	}
	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
	    this.icon = icon;
	}
	/**
	 * @return the pagetype
	 */
	public String getPagetype() {
	    return pagetype;
	}
	/**
	 * @param pagetype the pagetype to set
	 */
	public void setPagetype(String pagetype) {
	    this.pagetype = pagetype;
	}  
    }
    
   

