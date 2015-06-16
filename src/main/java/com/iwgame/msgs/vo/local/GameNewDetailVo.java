/**      
* GameNewDetailVo.java Create on 2014-12-12     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.vo.local;

import java.io.Serializable;

/** 
 * @ClassName: GameNewDetailVo 
 * @Description: 攻略贴吧网页详情分享传输对象 
 * @author 王卫
 * @date 2014-12-12 下午1:41:37 
 * @Version 1.0
 * 
 */
public class GameNewDetailVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5491371986946694972L;
	
	private long id;
	
	private String icon;
	
	private String title;
	
	private String content;
	
	private String link;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
