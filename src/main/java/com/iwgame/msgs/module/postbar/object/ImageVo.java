/**      
 * ImageVo.java Create on 2015-3-25     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.object;

import java.io.Serializable;

/**
 * @ClassName: ImageVo
 * @Description: 图片数据
 * @author 王卫
 * @date 2015-3-25 上午11:45:03
 * @Version 1.0
 * 
 */
public class ImageVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 577633783678773771L;
	// 图片路径
	private String path;
	// 图片数据
	private byte[] data;

	public ImageVo() {

	}

	public ImageVo(String path, byte[] data) {
		this.path = path;
		this.data = data;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
