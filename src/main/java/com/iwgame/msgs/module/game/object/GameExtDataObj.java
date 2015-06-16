/**      
* GameExtDataVo.java Create on 2013-10-8     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.game.object;


/** 
 * @ClassName: GameExtDataVo 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2013-10-8 下午06:32:42 
 * @Version 1.0
 * 
 */
public class GameExtDataObj {

	//赞的次数
	private int praise;
	//踩的次数
	private int critivize;
	//是否赞过
	private int ispraise;
	//是否踩过
	private int iscritivize;
	
	/**
	 * @return the praise
	 */
	public int getPraise() {
		return praise;
	}
	/**
	 * @param praise the praise to set
	 */
	public void setPraise(int praise) {
		this.praise = praise;
	}
	/**
	 * @return the critivize
	 */
	public int getCritivize() {
		return critivize;
	}
	/**
	 * @param critivize the critivize to set
	 */
	public void setCritivize(int critivize) {
		this.critivize = critivize;
	}
	/**
	 * @return the ispraise
	 */
	public int getIspraise() {
		return ispraise;
	}
	/**
	 * @param ispraise the ispraise to set
	 */
	public void setIspraise(int ispraise) {
		this.ispraise = ispraise;
	}
	/**
	 * @return the iscritivize
	 */
	public int getIscritivize() {
		return iscritivize;
	}
	/**
	 * @param iscritivize the iscritivize to set
	 */
	public void setIscritivize(int iscritivize) {
		this.iscritivize = iscritivize;
	}
	
}
