/**      
 * ErrorCode.java Create on 2013-8-19     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

/**
 * @ClassName: ErrorCode
 * @Description: 错误码
 * @author 王卫
 * @date 2013-8-19 上午11:15:18
 * @Version 1.0
 * 
 */
public class ErrorCode {

	/**
	 * 网络错误
	 */
	// 无可用网络
	public static final Integer EC_NET_WORK_NOT_AVAILABLE = -100;
	// 服务端返回结果为空
	public static final int EC_RESULT_ISNULL = -101;
	// 请求服务器错误
	public static final int EC_REQUEST_ERROR = -102;
	// 通讯层内部错误
	public static final int EC_XACTION_ERROR = -10000;
	// 通讯层网络错误
	public static final int EC_XACTION_NET_ERROR = -10001;
	// 通讯层超时错误
	public static final int EC_XACTION_TIMEOUT_ERROR = -10002;

	/**
	 * 通讯层级错误（连接不上服务端[网络不好、服务端维护中]）
	 */
	// TCP CLIENT为空
	public static final int EC_TCP_CLIENT_ISNULL = -200;
	// HTTP CLIENT为空
	public static final int EC_HTTP_CLIENT_ISNULL = -201;

	/**
	 * 服务端未知通用错误
	 */
	public static final int EC_COMMON_ERROR = -300;

	/**
	 * 业务错误
	 */
	// 获得用户信息，服务器返回成功，但是用户信息为空
	public static final Integer EC_CLIENT_USER_NULL = -400;
	public static final Integer EC_CLIENT_REMOTE_MESSAGE = -500;
	// 获得帖子详情时为空
	public static final Integer EC_CLIENT_POSTBAR_GETTOPICDETAILISNULL = -600;
	// 消息中快捷操作action未定义
	public static final Integer EC_CLIENY_MESSAGE_ACTION_UNDEFINED = -700;
	// 消息中快捷操作的参数不对
	public static final Integer EC_CLIENY_MESSAGE_ACTION_PARAMERROE = -701;

	// 登录时异常
	public static final Integer EC_CLIENT_LOGIN_EX = -800;
	
	//发送声音时，保存声音文件异常，一般是存储空间不够
	public static final Integer EC_CLIENT_SAVESENDAUDIOFILE_FAIL = -900 ;

	// 用户同意加入公会，但是已经加入的公会达到最大限制
	public static final Integer EC_CLIENT_USERAGREEJOINGROUP_OVER_COUNT = 50001001;
	
	//会长审批成员（成员加入工地已经达到最大数）
	public static final Integer EC_CLIENT_GROUPMANAGER_APPROVE_MEMBER_OVER_COUNT = 50001002;
	
	//每日发帖超过限制
	public static final Integer EC_CLIENT_PUBLISHTOPIC_OVER_COUNT = 50001003;
	//关注贴吧超过限制
	public static final Integer EC_CLIENT_FOLLOWGAME_OVER_COUNT = 50001004;

	// 对聊，被对方拉黑后，发送消息的错误
	public static final Integer EC_CLIENT_CHAT_NORIGHT = 50010001;

	// 群聊，被会长或管理员踢出公会
	public static final Integer EC_CLIENT_MCHAT_NORIGHT = 50010002;

	//邀请公会成员的时候，如果对方已经在公会里面了，则直接提示对方已经在公会中
	public static final Integer EC_CLIENT_INVITE_ERROR_CODE = 500007;
	
	// 用户同意加入公会，但是用户已经在公会中了
	public static final Integer EC_CLIENT_USERAGREEJOINGROUP_ALREADY_IN = 50000701;
	// 会长或管理员审批成员加入公会时，该成员已经在公会中了
	public static final Integer EC_CLIENT_GROUPMANAGER_APPROVE_MEMBER = 50000702;
	
	
	
	
	/**
	 * sina微博的错误码
	 */
	public static final Integer EC_CLIENT_SINA_WEIBO_ERR =-20001;
	
	/**
	 * 登录session状态错误异常
	 */
	public static final int EC_CLIENT_LOGIN_SESSINT_STATUS_ERR = -30001;
	
	
	//第三方账户注册失败
	public static final int EC_CLIENT_THIRD_REGISTER_FAIL = -1000 ;


}
