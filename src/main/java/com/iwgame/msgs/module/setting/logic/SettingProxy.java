/**      
 * ContractServiceProxy.java Create on 2013-8-22     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.setting.logic;

import java.util.List;

import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.vo.local.AreaVo;
import com.iwgame.msgs.vo.local.PageDataVo;
import com.iwgame.msgs.vo.local.ResourceVo;

/**
 * @ClassName: SettingProxy
 * @Description: 设置服务数据代理接口
 * @author 王卫
 * @date 2013-8-22 上午10:57:39
 * @Version 1.0
 * 
 */
public interface SettingProxy {

	/**
	 * 设置账号信息
	 * 
	 * @param callback
	 * @param context
	 * @param sex
	 * @param mood
	 * @param age
	 * @param job
	 * @param avatarData
	 * @param avatar
	 * @param city
	 * @param nickname
	 * @param desc
	 * @param gameTime
	 * @param likeGameType
	 */
	public void modifyAccountInfo(ProxyCallBack<Integer> callback, Context context, Integer sex, String mood, Integer age, String job,
			byte[] avatarData, String avatar, String city, String nickname, String desc, String gameTime, String likeGameType, String weiboUid, String weiboName);

	/**
	 * 设置账号头像
	 * 
	 * @param callback
	 * @param context
	 * @param avatarData
	 */
	public void modifyAccountAvatar(ProxyCallBack<ResourceVo> callback, Context context, byte[] avatarData);

	/**
	 * 查找地区BY类型
	 * 
	 * @param callBack
	 * @param types
	 */
	public void findAreaByType(ProxyCallBack<List<AreaVo>> callBack, String[] types);

	/**
	 * 根据父级地区ID（省ID）查找城市
	 * 
	 * @param callBack
	 * @param type
	 */
	public void findAreaByParentid(ProxyCallBack<List<AreaVo>> callBack, int pid);

	/**
	 * 修改密码
	 * 
	 * @param callback
	 * @param opwd
	 * @param npwd
	 */
	public void modifypassword(ProxyCallBack<Integer> callback, Context context, String opwd, String npwd);

	/**
	 * 获取全局配置
	 * 
	 * @param callback
	 * @param context
	 */
	public void getGlobalConfig(ProxyCallBack<Integer> callback, String url);
	
	/**
	 * 加载App配置文件
	 * @param callback
	 */
	public void getAppConfig(ProxyCallBack<Integer> callback);
	
	/**
	 * 加载App启动图片
	 * @param callback
	 */
	public void loadAppLuncherBg(ProxyCallBack<PageDataVo> callback, Context context, int pageId);
	
	/**
	 * 清空聊天cache和图片
	 */
	public void cleanCache();

}
