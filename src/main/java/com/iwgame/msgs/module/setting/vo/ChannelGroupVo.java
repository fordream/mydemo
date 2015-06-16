/**      
* ShowChannelsRuleVo.java Create on 2014-5-22     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.setting.vo;

/** 
 * @ClassName: ShowChannelsRuleVo 
 * @Description: TODO(频道组对象) 
 * @author chuanglong
 * @date 2014-5-22 下午5:59:06 
 * @Version 1.0
 * 
 */
public class ChannelGroupVo  extends ShowChannelsRuleVo {
  

	/**
	 * 类型
	 */
	private String type ;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 图标
	 */
	private String icon ;
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
	 *   * 是否使用频道的名称和icon
         * 
         * false:不使用，使用subjectid去获得主体来显示
         *       true：使用channelname和 channelicon来显示
	 */
	private boolean isUseChannel ;
	/**
	 * 是否允许删除
	 */
	private boolean isAllowDel ;
	/**
	 * 最小条数为几条时展开
	 */
	private int minExp;
	/**
	 * 打开的页面
	 */
	private String pagetype;
	/**
	 * 组下的频道
	 */
	private ChannelVo channelVo;
	/**
	 * @return the channelVo
	 */
	public ChannelVo getChannelVo() {
	    return channelVo;
	}
	/**
	 * @param channelVo the channelVo to set
	 */
	public void setChannelVo(ChannelVo channelVo) {
	    this.channelVo = channelVo;
	}
	
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
	 * @return the minExp
	 */
	public int getMinExp() {
	    return minExp;
	}
	/**
	 * @param minExp the minExp to set
	 */
	public void setMinExp(int minExp) {
	    this.minExp = minExp;
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

