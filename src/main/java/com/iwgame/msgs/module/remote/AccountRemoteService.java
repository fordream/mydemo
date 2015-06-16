/**      
 * AccountRemoteService.java Create on 2013-9-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import android.content.Context;

import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: AccountRemoteService
 * @Description: 账号服务接口
 * @author 王卫
 * @date 2013-9-24 下午05:03:38
 * @Version 1.0
 * 
 */
public interface AccountRemoteService {

	/**
	 * 注册提交手机号
	 * 
	 * @param callback
	 * @param context
	 * @param mobileNum
	 * @param mode
	 *            [0注册1找回密码]
	 */
	public void getCaptcha(ServiceCallBack<XActionResult> callback, Context context, String mobileNum, int mode);

	/**
	 * 验证码校验
	 * 
	 * @param callback
	 * @param context
	 * @param mobileNum
	 * @param captcha
	 */
	public void validateCaptcha(ServiceCallBack<XActionResult> callback, Context context, String mobileNum, String captcha);

	/**
	 * 重置密码
	 * 
	 * @param callback
	 * @param context
	 * @param account
	 * @param password
	 * @param captcha
	 */
	public void resetPassword(ServiceCallBack<XActionResult> callback, Context context, String account, String password, String captcha);

	/**
	 * 登录
	 * 
	 * @param callback
	 * @param context
	 * @param account
	 * @param password
	 * @param deviceId
	 */
	public void login(ServiceCallBack<XActionResult> callback, Context context, String account, String password, String deviceId);
	
	
	/**
	 * 
	* @Title: login    
	* @Description: 登录   
	* @param @param callback
	* @param @param context
	* @param @param account
	* @param @param password
	* @param @param deviceId
	* @param @param authtype
	* @param @param apitype
	* @param @param openId    设定文件    
	* @return void    返回类型    
	* @throws
	 */
	public void login(ServiceCallBack<XActionResult> callback, Context context, String account, String deviceId, int authtype, String apitype, String openId);

	/**
	 * 注销
	 * 
	 * @param callback
	 * @param context
	 * @param token
	 */
	public void logout(ServiceCallBack<XActionResult> callback, Context context, String token);

	/**
	 * 修改密码
	 * 
	 * @param callback
	 * @param context
	 * @param opwd
	 * @param npwd
	 */
	public void modifyPassword(ServiceCallBack<XActionResult> callback, Context context, String opwd, String npwd);
	
	/**
	 * 验证账号
	 * @param callback
	 * @param context
	 */
	public void validateAccount(ServiceCallBack<XActionResult> callback, Context context);

}
