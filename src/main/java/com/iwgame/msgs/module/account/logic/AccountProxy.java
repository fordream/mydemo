/**      
 * UserServiceProxy.java Create on 2013-8-22     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.logic;

import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.module.account.object.GuestObject;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: AccountProxy
 * @Description: 用户账号模块代理服务接口（处理业务逻辑）
 * @author 王卫
 * @date 2013-8-22 上午10:04:30
 * @Version 1.0
 * 
 */
public interface AccountProxy {

	/**
	 * 提交手机号获取验证码
	 * 
	 * @param callback
	 * @param context
	 * @param mobileNum
	 * @param mode
	 */
	public void getCaptcha(ProxyCallBack<Integer> callback, Context context, String mobileNum, int mode);

	/**
	 * 验证验证码
	 * 
	 * @param callback
	 * @param context
	 * @param mobileNum
	 * @param captcha
	 */
	public void validateCaptcha(ProxyCallBack<Integer> callback, Context context, String mobileNum, String captcha);

	/**
	 * 注册账号 提交注册信息
	 * 
	 * @param callback
	 * @param context
	 * @param captcha
	 * @param accountName
	 * @param password
	 * @param nickname
	 * @param sex
	 * @param age
	 * @param avatar
	 */
	public void registerAccount(ProxyCallBack<Integer> callback, Context context, String captcha, String accountName, String password,
			String nickname, int sex, Integer age, byte[] avatar, String origin);

	/**
	 * 
	* @Title: registerAccount    
	* @Description: 第三方账号注册
	* @param @param callback
	* @param @param context
	* @param @param openID
	* @param @param target
	* @param @param sex
	* @param @param avatar
	* @param @param nickname    设定文件    
	* @return void    返回类型    
	* @throws
	 */
	public void registerAccount(ProxyCallBack<Integer> callback, Context context, String openID, int target, 
			int sex, String avatar, String nickname, String origin);
	/**
	 * 游客注册
	 * 
	 * @param callback
	 * @param context
	 * @param origin
	 */
	public void registerGuestAccount(ProxyCallBack<GuestObject> callback, Context context, String origin);
	
	/**
	 * 绑定手机
	 * @param callback
	 * @param context
	 * @param captcha
	 * @param accountName
	 * @param password
	 * @param sex
	 * @param avatar
	 */
	public void bundPhone(ProxyCallBack<Integer> callback, Context context, String captcha, String accountName, String password, String nickname, int sex, byte[] avatar);

	/**
	 * 密码重置
	 * 
	 * @param callback
	 * @param context
	 * @param account
	 * @param password
	 * @param captcha
	 */
	public void resetPassword(ProxyCallBack<Integer> callback, Context context, String account, String password, String captcha);

	/**
	 * 登录
	 * 
	 * @param callback
	 * @param context
	 * @param account
	 * @param password
	 */
	public void login(ProxyCallBack<Integer> callback, Context context, String account, String password);
	
	
	/**
	* @Title: login    
	* @Description: 登录  
	* @param @param callback
	* @param @param context
	* @param @param account
	* @param @param password
	* @param @param authtype
	* @param @param apitype    设定文件    
	* @return void    返回类型    
	* @throws
	 */
	public void login(ProxyCallBack<Integer> callback, Context context, String account, int authtype, String apitype, String openId);

	/**
	 * 已经登录成功后获取用户信息
	 * @param callback
	 * @param context
	 */
	public void getUserInfoHasLogin(ProxyCallBack<Integer> callback, Context context, String account, String password);

	
	/**
	 * 验证账号
	 * 
	 * @param callback
	 * @param context
	 */
	public void verifyAccount(ProxyCallBack<Integer> callback, Context context);

	/**
	 * 注销
	 * 
	 * @param callback
	 * @param context
	 * @param account
	 * @param password
	 */
	public void logout(ProxyCallBack<Integer> callback, Context context, String token);
	
	/**
	 * 
	 * @param callback
	 * @param context
	 */
	public void validateAccount(ProxyCallBack<Integer> callback, Context context);

}
