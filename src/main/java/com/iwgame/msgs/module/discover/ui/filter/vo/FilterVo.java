/**      
 * RadioFilter.java Create on 2015-4-17     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.discover.ui.filter.vo;

/**
 * @ClassName: RadioFilter
 * @Description: TODO(...)
 * @author 王卫
 * @date 2015-4-17 下午4:44:36
 * @Version 1.0
 * 
 */
public class FilterVo {

	// ID
	public int id;
	// 名称
	public String name;
	// 选择状态
	public boolean selected;

	// 是否显示图片
	public boolean showIcon;
	// 是否显示图片
	public String icon;

	// 游戏ID
	public long gid;
	// 服务ID
	public long sid;
	//角色属性id
	public Long keyid;
	//角色属性值
	public String keyval;

	// 网络类型
	public Integer gtype;

	// 平台
	public String platform;

	// 性别值
	public Integer sex;

	// 时间
	public Integer time;

	// 验证
	public Boolean verify;

	// 表识某个服务器的唯一key（格式gid-sid）
	public String serviceKey;

	/**
	 * 
	 * @param id
	 * @param name
	 * @param selected
	 */
	public FilterVo(int id, String name, boolean selected) {
		this.id = id;
		this.name = name;
		this.selected = selected;
	}

	/**
	 * 
	 * @param id
	 * @param name
	 * @param selected
	 * @param showIcon
	 */
	public FilterVo(int id, String name, boolean selected, boolean showIcon) {
		this.id = id;
		this.name = name;
		this.selected = selected;
		this.showIcon = showIcon;
	}

	/**
	 * 
	 * @param id
	 * @param name
	 * @param selected
	 * @param icon
	 * @param showIcon
	 */
	public FilterVo(int id, String name, boolean selected, String icon, boolean showIcon) {
		this.id = id;
		this.name = name;
		this.selected = selected;
		this.icon = icon;
		this.showIcon = showIcon;
	}

	public FilterVo setGtypeValue(Integer gtype) {
		this.gtype = gtype;
		return this;
	}

	public FilterVo setPlatformValue(String platform) {
		this.platform = platform;
		return this;
	}

	public FilterVo setSexValue(Integer sex) {
		this.sex = sex;
		return this;
	}

	public FilterVo setTimeValue(Integer time) {
		this.time = time;
		return this;
	}

	public FilterVo setVerifyValue(Boolean verify) {
		this.verify = verify;
		return this;
	}

	public FilterVo setGidValue(long gid) {
		this.gid = gid;
		return this;
	}

	public FilterVo setSidValue(long sid) {
		this.sid = sid;
		return this;
	}
	
	public FilterVo setKeyid(Long keyid) {
		this.keyid = keyid;
		return this;
	}

	public FilterVo setKeyval(String keyval) {
		this.keyval = keyval;
		return this;
	}

	public FilterVo setServiceKeyValue(Long gid, Long sid) {
		this.serviceKey = gid + "-" + sid;
		return this;
	}

}
