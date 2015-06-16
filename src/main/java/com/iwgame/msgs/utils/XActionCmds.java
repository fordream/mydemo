/**      
 * XActionCmds.java Create on 2013-10-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

/**
 * @ClassName: XActionCmds
 * @Description: TODO(补充账号中心的cmd)
 * @author Administrator
 * @date 2013-10-24 下午4:02:42
 * @Version 1.0
 * 
 */
public interface XActionCmds {

	int XACTION_NEEDAUTH_COMMAND = 0x8000000;

	/**
	 * 登录
	 */
	public static int CMD_ACCOUNT_LOGIN = -1;
	/**
	 * 登出
	 */
	public static int CMD_ACCOUNT_LOGOUT = -2;

	/**
	 * 注册用户
	 */
	public static int CMD_ACCOUNT_REGISTACCOUN = 1;

	/**
	 * 重设密码
	 */
	public static int CMD_ACCOUNT_RESETPASSWORD = 2;

	/**
	 * 修改密码
	 */
	public static int CMD_ACCOUNT_MODIFYPASSWORD = XACTION_NEEDAUTH_COMMAND | 1;

	/**
	 * 获得用户摘要信息
	 */

	public static int CMD_ACCOUNT_GET_USERSUMMARY = XACTION_NEEDAUTH_COMMAND | 3;

	/**
	 * 获得用户详细信息
	 */

	public static int CMD_ACCOUNT_GET_USERDETAIL = XACTION_NEEDAUTH_COMMAND | 4;

	/**
	 * 修改用户信息
	 */

	public static int CMD_ACCOUNT_GET_MODIFYUSERINFO = XACTION_NEEDAUTH_COMMAND | 16;

	/**
	 * 获得短信验证码
	 */

	public static int CMD_ACCOUNT_GETCAPTCHA = 10;

	/**
	 * 验证短信验证码
	 */

	public static int CMD_ACCOUNT_VALIDATECAPTCHA = 11;

}
