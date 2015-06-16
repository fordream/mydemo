/**      
* IMainFragment.java Create on 2013-10-17     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs;

import java.io.File;

import android.widget.ImageView;
import android.widget.LinearLayout;

/** 
 * @ClassName: IMainFragment 
 * @Description: 主界面接口类
 * @author 王卫
 * @date 2013-10-17 上午11:23:38 
 * @Version 1.0
 * 
 */
public interface IMainFragment {

	/**
	 * 更新新闻未读数信息
	 * 
	 * @param count
	 */
	public void updataNewsUnReadCount(int count);
	
	/**
	 * 设置图片内容视图
	 */
	public void setPhotoContentView(LinearLayout v);
	
	/**
	 * 获取SDCard临时文件
	 * @return
	 */
	public File getSDcardTempFile();
	
	/**
	 * 设置头像VIEW
	 * @param avatarView
	 */
	public void setAvatarView(ImageView avatarView);
	
}
