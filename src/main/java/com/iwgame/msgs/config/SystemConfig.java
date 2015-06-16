/**      
 * SysConfig.java Create on 2013-7-30     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.config;

import java.io.File;

import android.os.Environment;

import com.iwgame.msgs.context.SystemContext;
import com.iwgame.utils.AppUtils;

/**
 * @ClassName: SysConfig
 * @Description: 全局配置文件
 * @author 王卫
 * @date 2013-7-30 下午06:13:37
 * @Version 1.0
 * 
 */
public class SystemConfig {

// 全局配置地址(域名形式)
//	public static String GLOBAL_CONFIG_URL = "http://p.51uban.com/msgs/g/a";//正式环境
//	public static String GLOBAL_CONFIG_URL = "http://p.rc.51uban.com/msgs/g/a";//准生产环境
//	public static String GLOBAL_CONFIG_URL = "http://p.rel.51uban.com/msgs/g/a";//压测环境
//	public static String GLOBAL_CONFIG_URL = "http://p.test.51uban.com/msgs/g/a";//
//	public static String GLOBAL_CONFIG_URL = "http://192.168.10.180/msgs/g/a";//办公网环境
//	public static String GLOBAL_CONFIG_URL = "http://183.136.130.155:81/msgs/g/a";//开发环境
//	public static String GLOBAL_CONFIG_URL = "http://183.136.130.173:81/msgs/g/a";//测试环境

	//活动专区的地址
	public static String DIVISION_URL = "";
	// 敏感词MDS地址
	public static String GLOBAL_WORDS_URL = ""; 
	// 敏感词地址
	public static String GLOBAL_WORDS_MD5 = "";
	// 名称敏感词MDS地址
	public static String GLOBAL_NAMEWORDS_URL = ""; 
	// 名称敏感词地址
	public static String GLOBAL_NAMEWORDS_MD5 = "";
	// 敏感词文件名
	public static String MSGS_WORDS = "msgs-wrods";
	// 名称敏感词文件名
	public static String MSGS_NAMEWORDS = "msgs-namewrods";
	// 服务器状态（1正常0停止）
	public static int SERVCE_STATUS = 1;
	
	// 服务条款等文件路径
	public static String AGREEMENT_PATH = "";
	
	// 帮助文件路径
	public static String HELP_PATH = "";

	// 服务端当前最新版本code
	public static int C_VERSION_CODE = AppUtils.getLocalAppVersionCode(SystemContext.getInstance().getContext());
	// 服务端当前必须最新版本code
	public static int UC_VERSION_CODE = AppUtils.getLocalAppVersionCode(SystemContext.getInstance().getContext());
	// 版本描述
	public static String VERSION_DESC;
	// 版本下载地址
	public static String VERSION_DOWNLOAD_URL;
	//app相关阀值配置文件地址
	public static String APP_CONFIG_URL;
	//游客权限配置
	public static String GUEST_PERMISSION_URL;
	// 自定义广播，用于接受到的聊天信息后广播
	public static String ACTION_RECEIVEMESSAGE_BROADCAST = "com.iwgame.msgs.receivemessage";
	// 自定义广播，用于接受到新商品或新任务等通知信息后广播
	public static String ACTION_RECEIVEMESSAGE_INTFO_BROADCAST = "com.iwgame.msgs.receivemessage.info";
	// 消息对象
	public static String BUNDLE_NAME_MESSAGEVO = "messagevo";
	// 是否有后续相同数据
	public static String BUNDLE_NAME_ISHAVEFOLLOWUPDATA = "ishavefollowupdata";
	// 是否聊天窗口接受了
	public static String BUNDLE_NAME_ISCHATACTIVITY_RECEIVE = "ischatactivity_receive";
	// 是否当前显示的是首页
	public static String BUNDLE_NAME_ISMAINACTIVITY_RECEIVE = "ismainactivity";

	// 传入窗口的扩展参数，最外层用于包含bundle的
	public static String BUNDLEEXTRA_NAME = "myBundleExtra";

	// 任务栏消息打开后真正的activity，用于传给中转activity
	public static String BUNDLEEXTRA_NAME_CLASS = "myClass";

	// 打开用户聊天窗口的参数
	public static String BUNDLE_NAME_TOUSERID = "toUserId";

	// 打开系统公告的参数
	public static String BUNDLE_NAME_TOSYSID = "toSysId";

	// 打开公会聊天窗口的参数
	public static String BUNDLE_NAME_TOGROUPID = "togroupid";

	// 打开用户动态或贴吧动态
	public static String BUNDLE_NAME_NEWS_SUBJECTID = "subjectid";
	public static String BUNDLE_NAME_NEWS_SUBJECTDOMIAN = "subjectdomain";

	// 打开动态详情页
	public static String BUNDLE_NAME_NEWS_CONTENTID = "contentid"; 
		
	//传递公会名片的key值 
	public static String GROUP_CARD_CONTENT_KEY = "group_card_content";
	
	// 聊天窗口Fragment的参数
	public static String BUNDLE_NAME_TOID = "toid";
	public static String BUNDLE_NAME_TODOMAIN = "todomain";
	public static String BUNDLE_NAME_ISSENDMSG = "issendmsg";// 是否允许发消息，true允许，false
	public static String BUNDLE_NAME_TITLE = "title";// 页面title
																// 不允许
	public static String BUNDLE_NAME_CATEGORY = "category"; // 内容类别，目前只有公会公告消息用到
	public static String BUNDLE_NAME_CHANNELTYPE = "channeltype"; // 暂时还未用到

	public static String BUNDLE_NAME_ISGROUPMASSMSGLIST = "isgroupmassmsglist";// 是否是公会会长群发消息list页面
	public static String BUNDLE_NAME_ISGROUPCHATMSGLIST = "isgroupchatmsglist";// 是否是公会聊天室消息list页面

	public static String BUNDLE_NAME_PAGETYPE = "pageType";

	//判断跳转到用户详情资料界面，上一个界面是不是从通讯录关注里面点击进去的
	public static String IS_FROM_FOLLOWS_LISTVIEW = "isfromlistview";
	//用户的备注名
	public static String USER_FOLLOW_REMARK_NAME = "remarkname";
	// 关于我的,选中的tab
	public static String BUNDLE_NAME_MY_TAB_SELECT = "my_select_tab";

	// 我的动态
	public static int MY_TAB_VALUE_MYNEWS = 0;
	// 评论我的
	public static int MY_TAB_VALUE_COMMENTMY = 1;
	
	// 打开动态详情窗口的参数
	public static String BUNDLE_NAME_NEWSID = "newsid";

	// 图片浏览器窗口的参数
	public static String BUNDLE_NAME_IMAGEBROWER_IMAGES = "images";// 里面存放string的数组
	public static String BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX = "imageIndex";// 索引
	public static String BUNDLE_NAME_IMAGEBROWER_IMAGES_ISSHOWREPORTMENU = "isShowReportMenu";// 是否显示举报按钮

	public static String BUNDLE_NAME_IMAGEBROWER_ISLOADNET = "isloadnet";// 是否从网络上加载图片，默认是
	// 是否从通知中打开的中转页打开主页面
	public static String BUNDLE_NAME_ISFROMNOTIFICATIONOPEN = "isFromNotificationOpen";

	// 转发分享时内容类型 1，消息，2帖子
	public static String BUNDLE_NAME_CONTENT_TYPE = "contenttype";
	// 内容
	public static String BUNDLE_NAME_CONTENT = "content";
	// 操作类型 1 转发，2 分享
	public static String BUNDLE_NAME_CONTENT_ACTION_TYPE = "contentactiontype";
	// 消息
	public static int CONTENT_TYPE_MESSAGEVO = 1;
	// 帖子
	public static int CONTENT_TYPE_TOPIC = 2;
	// 用户资料
	public static int CONTENT_TYPE_USER_DETIAL = 3;
	// 公会资料
	public static int CONTENT_TYPE_GROUP_DETIAL = 4;
	// 贴吧资料
	public static int CONTENT_TYPE_GAME_DETIAL = 5;
	// 商品资料
	public static int CONTENT_TYPE_GOODS_DETIAL = 6;
	// 攻略贴吧网页详情分享资料
	public static int CONTENT_TYPE_GAMENEW_DETIAL = 7;
	// 转发
	public static int CONTENT_ACTION_TYPE_FORWARDING = 1;
	// 分享
	public static int CONTENT_ACTION_TYPE_SHARE = 2;
	

	/************************************************************************
	 * 全局配置变量KEY名称
	 ************************************************************************/

	// 全局配置名称
	public static String SHAREDPREFERENCES_NAME_GLOBAL = "msgs";
	// 全局是否第一次使用
	public static String SHAREDPREFERENCES_NAME_GLOBAL_USED = "used";
	// 全局用户TOKEN值
	public static String SHAREDPREFERENCES_NAME_GLOBAL_TOKEN = "token";
	// 全局DEVICEID值
	public static String SHAREDPREFERENCES_NAME_GLOBAL_DEVICEID = "deviceId";
	// 全局当前用户类型
	public static String SHAREDPREFERENCES_NAME_IS_GUEST = "isGuest";
	// 全局当前用户类型
	public static String SHAREDPREFERENCES_NAME_ACCOUNT_TYPE = "accountType";
	// 全局用户账号
	public static String SHAREDPREFERENCES_NAME_GLOBAL_ACCOUNT = "account";
	// 全局游客账号
	public static String SHAREDPREFERENCES_NAME_GUEST_ACCOUNT = "guestaccount";
	// 全局PUSHID统计
	public static String SHAREDPREFERENCES_NAME_PUSHID_STRING = "pushidstring";
	// 全局用户SERIAL值
	public static String SHAREDPREFERENCES_NAME_GLOBAL_SERIAL = "serial";
	// 全局用户AVATAR值
	public static String SHAREDPREFERENCES_NAME_GLOBAL_AVATAR = "avatar";
	// 全局用户TOKEN值
	public static String SHAREDPREFERENCES_NAME_GLOBAL_GRADE = "grade";
	//全局用户的积分值
	public static String SHAREDPREFERENCES_NAME_GLOBAL_POINT = "point";
	//全局用户的经验值
	public static String SHAREDPREFERENCES_NAME_GLOBAL_EXP = "exp";
	//全局用户的相册数量
	public static String SHAREDPREFERENCES_NAME_GLOBAL_ALBUM_COUNT = "albumcount";
	// 全局用户GRADE值
	public static String SHAREDPREFERENCES_NAME_GLOBAL_SEX = "sex";
	// 全局用户心情
	public static String SHAREDPREFERENCES_NAME_GLOBAL_MOOD = "mood";
	// 全局用户年龄
	public static String SHAREDPREFERENCES_NAME_GLOBAL_AGE = "age";
	// 全局用户城市
	public static String SHAREDPREFERENCES_NAME_GLOBAL_CITY = "city";
	// 全局用户职业
	public static String SHAREDPREFERENCES_NAME_GLOBAL_JOB = "job";
	// 全局用户名
	public static String SHAREDPREFERENCES_NAME_GLOBAL_USERNAME = "username";
	// 全局用描述
	public static String SHAREDPREFERENCES_NAME_GLOBAL_DESC = "desc";
	// 全局用玩贴吧时长
	public static String SHAREDPREFERENCES_NAME_GLOBAL_GAMETIME = "gametime";
	// 全局用玩贴吧类型
	public static String SHAREDPREFERENCES_NAME_GLOBAL_LIKEGAMETYPE = "likegametype";
	// 全局配置名称uid
	public static String SHAREDPREFERENCES_NAME_GLOBAL_UID = "uid";
	// 全局配置名称pid
	public static String SHAREDPREFERENCES_NAME_GLOBAL_PID = "pid";
	// 全局配置名称oid
	public static String SHAREDPREFERENCES_NAME_GLOBAL_OID = "oid";
	//全局配置禁言
	public static String SHAREDPREFERENCES_STOP_SAY_WORDS = "stopsaywords";
	// 全局配置名称贴吧GID
	public static String SHAREDPREFERENCES_NAME_GLOBAL_GID = "gid";
	// 全局配置名商品playid
	public static String SHAREDPREFERENCES_NAME_GLOBAL_PLAYID = "playid";
	// 全局配置status
	public static String SHAREDPREFERENCES_NAME_GLOBAL_STATUS = "status";
	// 全局配置陪玩pstatus
	public static String SHAREDPREFERENCES_NAME_GLOBAL_PSTATUS = "pstatus";
	// 全局配置用户封停ustatus
	public static String SHAREDPREFERENCES_NAME_GLOBAL_USTATUS = "ustatus";
	// 公会ID
	public static String SHAREDPREFERENCES_NAME_GLOBAL_GRID = "grid";
	// 公会名称 name
	public static String SHAREDPREFERENCES_NAME_GLOBAL_GNAME = "groupname";
	// 目标TID
	public static String SHAREDPREFERENCES_NAME_GLOBAL_TID = "tid";
	// 目标TTYPE
	public static String SHAREDPREFERENCES_NAME_GLOBAL_TTYPE = "ttype";
	// 目标内容
	public static String SHAREDPREFERENCES_NAME_GLOBAL_CONTENT = "content";
	// 聊天相关mcode信息
	public static String SHAREDPREFERENCES_NAME_GLOBAL_CHATINFO = "chatinfo";
	// 电话号码
	public static String SHAREDPREFERENCES_NAME_GLOBAL_MOBILENUM = "mobileNum";
	// 验证码
	public static String SHAREDPREFERENCES_NAME_GLOBAL_CAPTCHA = "captcha";
	// 密码
	public static String SHAREDPREFERENCES_NAME_GLOBAL_PASSWORD = "passwrod";
	// 游客信息
	public static String SHAREDPREFERENCES_NAME_GUEST_SUFFIX = "guestaccount";
	// 游客密码
	public static String SHAREDPREFERENCES_NAME_GUEST_PASSWORD = "guestpasswrod";
	// 游客状态
	public static String SHAREDPREFERENCES_NAME_GUEST_STATUS = "gueststatus";
	
	// 第三方登录类型
	public static String SHAREDPREFERENCES_NAME_GLOBAL_API_TYPE = "apitype";
	// 登录类型
	public static String SHAREDPREFERENCES_NAME_GLOBAL_OPEN_ID = "openid";
	
	
	// AppConf配置名称
	public static String SHAREDPREFERENCES_NAME_APP_CONFIG = "appconfig";
	
	// 全局用户账号
	public static String SHAREDPREFERENCES_LUNCHAR_BG_LOAD_TIME = "lunchar_bg_load_time";	
	
	// 账号登录状态
	public static String SHAREDPREFERENCES_NAME_LOGIN_STATUS = "loginstatus";
	// 游客是否更改过昵称
	public static String SHAREDPREFERENCES_NAME_IS_UPDATE_USERNAME = "isupdateusername";
	// 类型
	public static String SHAREDPREFERENCES_NAME_GLOBAL_TYPE = "type";
	// 类型
	public static String SHAREDPREFERENCES_NAME_GLOBAL_KEYWORD = "keyWord";
	// 模式
	public static String SHAREDPREFERENCES_NAME_GLOBAL_MODE = "mode";
	// 贴吧是否显示来自什么游戏模式
	public static String SHAREDPREFERENCES_NAME_GLOBAL_POSTBAR_DETAIL_MODE = "mode_postbar";
	//索引
	public static String SHAREDPREFERENCES_NAME_GLOBAL_INDEX = "index";
	// URL
	public static String SHAREDPREFERENCES_NAME_GLOBAL_URL = "url";
	// 标题
	public static String SHAREDPREFERENCES_NAME_GLOBAL_TITLE = "title";
	// 扩展数据
	public static String SHAREDPREFERENCES_NAME_GLOBAL_EXT = "ext";
	// 是否有上一页
	public static String SHAREDPREFERENCES_NAME_GLOBAL_HAVE_PRE = "ishavePre";
	// 是否踢下线通知
	public static String SHAREDPREFERENCES_NAME_GLOBAL_UNAUTHENTICATED = "unauthenticated";
	// 会话失效
	public static String SHAREDPREFERENCES_NAME_GLOBAL_EXPIRED = "expired";
	// 封号
	public static String SHAREDPREFERENCES_NAME_GLOBAL_SEALACCOUNT = "sealAccount";
	// 贴吧名称
	public static String SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME = "gamename";
	// 是否关注
	public static String SHAREDPREFERENCES_NAME_GLOBAL_ISFOLLOW = "isfollow";
	// 关系
	public static String SHAREDPREFERENCES_NAME_GLOBAL_REL = "rel";

	public static String SHAREDPREFERENCES_NAME_GLOBAL_SEQ = "seq";

	// 用户配置名称后缀
	public static String SHAREDPREFERENCES_NAME_USER_SUFFIX = "_msgs";
	
	public static String SHAREDPREFERENCES_NAME_APPCONFIG_STRING = "appconfigstring";
	// 用户头像文件名称
	public static String LOGIN_USER_ICON_NAME = "login_user_icon.png";
	
	// 第三方登录类型
	public static int LOGIN_THIRD_AUTH_TYPE = 3;

	// 第三方登录，QQ登录
	public static String LOGIN_QQ_WEIBO = "qqweibo";
	// 第三方登录，新浪微博登录
	public static String LOGIN_SINA_WEIBO = "sinaweibo";
	// 游客登录
	public static int LOGIN_TARGET_GUEST = -1;
	// QQ登录，并且没有绑定手机
	public static int LOGIN_TARGET_QQ_WEIBO = 21;
	// sina微博登录，并且没有绑定手机
	public static int LOGIN_TARGET_SINA_WEIBO = 11;
	// 正常用户
	public static int LOGIN_TARGET_NORMAL = 0;

	
	// 启动页面文件名称
	public static String APP_LUNCHER_BG = "app_luncher_bg.png";
	
	public static int APP_LUNCHAR_PAGE_ID = 1;
	
	//商品配置数据
	public static String SHAREDPREFERENCES_NAME_GOODS_ID = "goodsid";
	public static String SHAREDPREFERENCES_NAME_GOODS_INDEX = "goodsindex";
	public static String SHAREDPREFERENCES_NAME_GOODS_FLAG = "isshowchangebtn";
	
	
	// 资源服务（图片）类型
	public static String RESCOURCE_TYPE_SMALL = "small";
	public static String RESCOURCE_TYPE_MIDIUM = "medium";
	// 是否由聊天窗口打开
	public static String SHAREDPREFERENCES_NAME_GLOBAL_ISFROMCHATACTIVITYOPEN = "isfromchatactivityopen";

	// 是否由动态页面打开
	public static String SHAREDPREFERENCES_NAME_GLOBAL_ISFROMNEWSACTIVITYOPEN = "isfromnewslistfragmentactivityopen";

	/************************************************************************
	 * 同步key定义
	 ************************************************************************/
	// 同步用户的key （mode = 0 ，关注）
	public static String KEY_SYNCUSER_MODE_0 = "KEY_SYNCUSER_MODE_0";
	// 同步用户的key （mode = 1 ，粉丝）
	public static String KEY_SYNCUSER_MODE_1 = "KEY_SYNCUSER_MODE_1";
	// 同步用户的key （mode = 1 ，黑名单）
	public static String KEY_SYNCUSER_MODE_2 = "KEY_SYNCUSER_MODE_2";

	// 聊天消息同步key
	// public static String KEY_SYNC_CHAR_REQUEST = "KEY_SYNC_CHAR_REQUEST" ;

	// 同步贴吧的key
	public static String KEY_SYNCGAME = "KEY_SYNCGAME";
	// 同步贴吧包的key
	public static String KEY_SYNCGAMEPACK = "KEY_SYNCGAMEPACK";
	// //同步关系贴吧的key
	public static String KEY_SYNCRELATIONGAME = "KEY_SYNCGAME";

	// 同步用户与用户关系key
	public static String SYNC_KEY_USER_REL = "sync_key_user_rel";
	// 同步用户与贴吧关系key
	public static String SYNC_KEY_GAME_REL = "sync_key_game_rel";
	// 同步贴吧key
	public static String SYNC_KEY_GAME = "sync_key_game";
	// 同步公会key
	public static String SYNC_KEY_GROUP = "sync_key_group";
	// 同步用户公会key
	public static String SYNC_KEY_GROUP_USER = "sync_key_group_user";
	// 同步贴吧包key
	public static String SYNC_KEY_GAME_PACKATE = "sync_key_game_package";
	// 同步用户等级key
	public static String SYNC_KEY_USER_GRADE_POLICY = "sync_key_group_grade_policy";
	// 同步公会等级key
	public static String SYNC_KEY_GROUP_GRADE_POLICY = "sync_key_group_grade_policy";
	// 同步任务详情key
	public static String SYNC_KEY_POINT_TASK_DETAIL = "sync_key_point_task_detail";
	// 系统设置(全局消息提醒)
	public static String SYS_SETTING_MSG_GLOBAL_OFFON = "sync_setting_msg_global_offon";
	// 系统设置(聊天消息)
	public static String SYS_SETTING_MSG_CHAT_OFFON = "sync_setting_msg_chat_offon";
	// 系统设置(贴吧动态)
	public static String SYS_SETTING_NEWS_GAME_OFFON = "sync_setting_news_game_offon";
	//系统设置关闭精彩推荐
	public static String SYS_SETTING_WONDERFUll_OFFON = "sync_setting_wonderfull_offon";
	//系统设置里面关闭缘分好友推荐
	public static String SYS_SETTING_FATE_OFFON = "sync_setting_fate_recommend_offon";
	// 系统设置(评论我的)
	public static String SYS_SETTING_COMMENT_MY_OFFON = "sync_setting_comment_my_offon";
	// 系统设置(好友动态)
	public static String SYS_SETTING_NEWS_FRIEND_OFFON = "sync_setting_news_friend_offon";
	// 声音
	public static String SYS_SETTING_SOUND_OFFON = "sync_setting_sound_offon";
	// 服务条款地址
	public static String PROTOCOL_SERVICE = "";
	// 公会服务条款地址
	public static String GROUP_PROTOCOL_SERVICE = "";
	//陪玩协议地址
	public static String PLAY_PROTOCOL_ADRRESS="";
	//支付协议地址
	public static String PAY_PROTOCOL_ADRRESS="";
	// 帮助
	public static String PROTOCOL_HELP = "";
	// HTTPIP
	public static String HTTP_IP = "http_ip";
	// HTTPLOGINIP
	public static String HTTP_LOGIN_IP = "http_login_ip";
	// TCPIP
	public static String TCP_IP = "tcp_ip";
	// HTTPPORT
	public static String HTTP_PORT = "http_port";
	// HTTPLOGINPORT
	public static String HTTP_LOGIN_PORT = "http_login_port";
	// TCPPORT
	public static String TCP_PORT = "tcp_port";
	// 附近的距离
	public static String NEAR_DISTANCE = "near_distance";
	// RESURL
	public static String RES_URL = "resurl";
	//模板url
	public static String TEMPLATE_URL = "templateurl"; 
	// 上传图片最大个数
	public static String AM = "am";
	// 群发消息最大个数
	public static String MM = "mm";
	// 公会加入最大个数
	public static String GJM = "gjm";
	// 公会创建最大个数
	public static String GCM = "gcm";
	// 公会申请最大个数
	public static String GAM = "gam";
	// 吧主当天申请最大个数
	public static String PAM = "pam";
	// 操作间隔时间（如发帖）
	public static String PIT = "pit";
	// 公会聊天消息提醒的规则 5条提醒
	public static String GCRN = "gcrn";
	// 公会聊天消息提醒的规则 后面每次隔10条提醒一次
	public static String GCRG = "gcrg";
	// 公会聊天消息提醒的规则 每条消息时间间隔30s提醒一次
	public static String GCRT = "gcrt";
	// 新帖的定义（毫秒）
	public static String PTID = "ptid";
	// 发送文本最大发送字符限制
	public static String MSGM = "msgm";
	// 赞踩提示次数
	public static String GAME_PRAISE_MAX_TIP_COUNT = "gptcm";
	// 赞踩已经提示次数
	public static String GAME_PRAISE_TIP_COUNT = "gptc";
	//关注贴吧的最大数
	public static String FGM = "fgm";
	//每日最大发帖数
	public static String PTM = "ptm";

	
	/***********公会名片****************/
	public static String SHAREDPREFERENCES_NAME_GROUP_CARD = "groupcard";
	public static String IF_NOT_SHOW_GROUP_CARD = "ifnotshowgroupcard";
	
	
	
	/************* 密码设置 ***************/
	public static int PASSWORD_MIN_LENGTH = 6;
	public static int PASSWORD_MAX_LENGTH = 16;

	/**
	 * 通知到状态栏的通知的id
	 */
	public static int NOTIFICATION_ID_BASE = 110;

	/**
	 * tcp XACTIONCLIENT 为 null
	 */
	public static int ERROR_XACTIONCLIENT_TCP_CODE = -10000;

	/**
	 * bitmap 压缩值， 100不压缩，
	 */
	public static int BITMAP_COMPRESS_QUALITY = 30;

	/**
	 * bitmap 最大分辨率，
	 */
	//public static int BITMAP_MAX_RESOLUTION = 1080 * 1920;
	public static int BITMAP_MAX_RESOLUTION = 720 * 1280;

	/**
	 * 分页加载时，向大的方向要数据 ，id做成关键字，值>id
	 */
	public static int PAGE_DIRECTION_UP = 1;
	/**
	 * 分页加载时，向小的方向要数据 ，id做成关键字，值<id
	 */
	public static int PAGE_DIRECTION_DOWN = 2;
	/**
	 * 默认每次加载的条数
	 */
	public static int PAGE_SIZE = 20;
	
	/**
	 * 每次更多加载的条数
	 */
	public static int PAGE_MORE_SIZE = 40;

	// 头像图片宽度
	public static final int IMAGE_W = 130;

	// 头像图片宽度
	public static final int IMAGE_H = 130;

	// 获得帖子列表的目标对象类型 ： 贴吧的帖子列表（BUNDLE_NAME_TAGGETID为贴吧id）
	public static final int GETTOPICLIST_TARGETTYPE_GAME = 0;
	// 获得帖子列表的目标对象类型 ： 用户的帖子列表（BUNDLE_NAME_TAGGETID为用户id）
	public static final int GETTOPICLIST_TARGETTYPE_USER = 1;
	// 获得帖子列表的目标对象类型 ： 搜索贴吧的帖子列表（BUNDLE_NAME_TAGGETID为贴吧id）
	public static final int GETTOPICLIST_TARGETTYPE_GAMESEARCHTOPIC = 2;
	// 获得帖子列表的目标对象类型 ：收藏的帖子
	public static final int GETTOPICLIST_TARGETTYPE_FAVORITE = 3;
	// 获得帖子列表的目标对象类型 ：新闻的帖子
	public static final int GETTOPICLIST_TARGETTYPE_NEWS = 4;
	// 获得帖子列表的目标对象类型 ：攻略的帖子
	public static final int GETTOPICLIST_TARGETTYPE_RAIDERS = 5;

	public static final int GETTOPICLIST_TARGETTYPE_GAME_TAG = 4;
	
	// 传入主题列表页的参数
	public static String BUNDLE_NAME_GETTOPICLIST_TARGETID = "getTopicListTargetID";
	public static String BUNDLE_NAME_GETTOPICLIST_TARGETTYPE = "getTopicListTargetType";

	public static String BUNDLE_NAME_GETTOPICLIST_TAGID = "getTopicListtagid";
	public static String BUNDLE_NAME_GETTOPICLIST_TAGNAME = "tagname";

	// 传入帖子详情页的参数，帖子id
	public static String BUNDLE_NAME_TOPICDETAIL_TOPICID = "topicdetail_topicid";
	public static String BUNDLE_NAME_TOPICDETAIL_ISSET = "topic_isset";

	// 传入帖子详情页的参数，回复我的回复的（用于从回复我的进入）
	public static String BUNDLE_NAME_TOPICDETAIL_REPLYMYREPLY = "replymyreply";
	public static String OTHER_AREA = "其他";

	/**
	 * 帖子对应的标签
	 */
	public static String BUNDLE_NAME_TOPIC_TAGS = "topic_tags";
	
	public static String BUNDLE_NAME_TOPIC_TAGID = "topic_tagid";

	/******************* 页面跳转 *****************/
	public static final int ACTIVITY_REQUESTCODE_DISTROY = 100;
	public static final int ACTIVITY_RESULTCODE_DISTROY = 101;
	public static final int ACTIVITY_RESULTCODE_DISTROY_USERLIST = 102;
	public static final int ACTIVITY_RESULTCODE_DISTROY_CHATLIST = 103;
	//公会名片的相关参数
	public static final int SYSTEMCONFIG_GROUP_CARD_NUM = 1000;
	public static final String AUDIO_FILENAME_FORMAT = Environment.getExternalStorageDirectory() + File.separator + "msgs" + File.separator + "voice"
			+ File.separator + "%s.amr";
	public static final String AUDIO_LOCAL_SEND_FILENAME_FORMAT = Environment.getExternalStorageDirectory() + File.separator + "msgs"
			+ File.separator + "voice" + File.separator + "local_%s.amr";
	public static final String IMAGE_LOCAL_SEND_FILENAME_FORMAT = Environment.getExternalStorageDirectory() + File.separator + "msgs"
		+ File.separator + "image" + File.separator + "local_%s.jpg";
	
	/**
	 * 录制声音的最大长度
	 */
	public static final int AUDIO_RECORDER_MAXDURATION = 60 * 1000;
	/**
	 * 录制声音倒记时提示的时长
	 */
	public static final int AUDIO_RECORDER_TIPDURATION = 5 * 1000;

	public static final String BUNDLE_NAME_FORWARDING_SUBJECT_LIST_TYPE = "forwardingsubjectlisttype";
	public static final int FORWARDING_SUBJECT_LIST_TYPE_LATESTCONTACTS = 0;
	public static final int FORWARDING_SUBJECT_LIST_TYPE_USERS = 1;
	public static final int FORWARDING_SUBJECT_LIST_TYPE_GROUP = 2;
	
	public static final String JVALUE_MESSAGE_NEWS_CONTENT_TYPE_POST = "POST";
	public static final String JVALUE_MESSAGE_NEWS_CONTENT_TYPE_URL = "URL";
	public static final String JVALUE_MESSAGE_NEWS_CONTENT_TYPE_USER = "USER";
	public static final String JVALUE_MESSAGE_NEWS_CONTENT_TYPE_GAME = "GAME";
	public static final String JVALUE_MESSAGE_NEWS_CONTENT_TYPE_GROUP = "GROUP";
	public static final String JVALUE_MESSAGE_NEWS_CONTENT_TYPE_GOODS = "GOODS";
	public static final String JVALUE_MESSAGE_NEWS_CONTENT_TYPE_WEBPAGE = "WEBPAGE";

	/**
	 * 全部帖子
	 */
	public static final int POSTBAR_TOPIC_TAG_ALL = 0;
	/**
	 * 精华帖子
	 */

	public static final int POSTBAR_TOPIC_TAG_ESSENCE = -1;

	/**
	 * 帖子分享成消息的内容中的key ：帖子id
	 */
	public static final String JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_TOPICID = "tId";
	/**
	 * 帖子分享成消息的内容中的key ：贴吧id
	 */
	public static final String JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_GAMEID = "gId";
	/**
	 * 帖子分享成消息的内容中的key ：用户id
	 */
	public static final String JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_UID = "uId";
	/**
	 * 帖子分享成消息的内容中的key ：公会id
	 */
	public static final String JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_GRID = "grId";
	/**
	 * 帖子分享成消息的内容中的key ：商品id
	 */
	public static final String JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_GOODSID = "goodsId";
	/**
	 * 网页分享成消息的内容中的key ：网页id
	 */
	public static final String JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_WEBPAGEID = "gndId";
	/**
	 * 网页分享成消息的内容中的key ：网页link
	 */
	public static final String JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_WEBPAGELINK = "gndLink";
	
	public static final String FORWARD_TYPE_MSG = "msg";
	public static final String FORWARD_TYPE_POSTBAR = "postbar";
	public static final String FORWARD_TYPE_USER = "user";
	public static final String FORWARD_TYPE_GAME = "game";
	public static final String FORWARD_TYPE_GROUP = "group";
	public static final String FORWARD_TYPE_GOODS = "goods";
	public static final String FORWARD_TYPE_GAMENEWDETAIL = "gamenewdetail";

	/**
	 * 帖子列表页打开帖子发布页的请求码
	 */
	public static final int TOPICLIST_OPENPUBLISHTOPIC_REQUESTCODE = 10001;
	
	public static final String CHANNELSHOWRULE_FILENAME = "channelshowrule";
	
	/************同步公会成员，同步下来后保存到的文件**************/
	public static final String SYNC_GROUP_MEMBER_FILENAME ="syncgroupmemberfile";
	/*********************通讯录的关注列表 和粉丝列表  的距离和上次登录时间保存到文件中的 文件名******************************/
	public static final String DISTANCE_AND_LASTLOGIN_FILENAME ="distanceaddgroupfilename";
	
	/** *******广播action*******/
	public static final String UPDATE_GOODS_LIST_ACTION = "update_goods_list_action";
	
	/************* 行为动作 ***************/
	//发送个人聊天信息
	public static String USERACTION_SEND_PERSONAL_CHAT = "1";
	//回帖
	public static String USERACTION_REPLY_TOPIC = "2";
	//修改密码
	public static String USERACTION_MODIFY_PASSWORD = "3";
	//发送公会聊天室信息
	public static String USERACTION_SEND_GROUP_CHAT = "4";
	//申请吧主
	public static String USERACTION_APPLY_TOPIC_HOST = "5";
	//推荐贴吧
	public static String USERACTION_RECOMMEND_TOPIC = "6";
	//创建公会
	public static String USERACTION_CREATE_GROUP = "7";
	//个人升级
	public static String USERACTION_PERSONAL_UPGRADE = "8";
	//贡献积分
	public static String USERACTION_CONTRIBUTE_INTEGRAL = "9";
	//发帖
	public static String USERACTION_PUBLISH_TOPIC = "10";
	//商城兑换物品
	public static String USERACTION_EXCHANGE_GOODS = "11";
	//关注别人
	public static String USERACTION_FOCUS_PEOPLE = "12";
	//被人关注
	public static String USERACTION_FOCUS_BY_PEOPLE = "13";
	//看帖
	public static String USERACTION_SEE_TOPIC = "14";
	//分享
	public static String USERACTION_SHARE = "15";
	//做新手任务
	public static String USERACTION_DO_NEW_TASK = "16";
	//领取任务积分
	public static String USERACTION_GET_TASK_INTEGRAL = "17";
	//关注贴吧
	public static String USERACTION_FOCUS_TOPIC = "18";
	//退出登录
	public static String USERACTION_EXIT_LOGIN = "19";
	//加入公会
	public static String USERACTION_JOIN_GROUP = "20";
	//点击我的等级
	public static String USERACTION_MY_GRADE = "21";
	
	
	
	
	/***********************ShareSdk分享********************************/
	public static int SHARE_RESULT_SUCCESS = 1;//分享成功
	public static int SHARE_RESULT_ERROR = 2;//分享失败
	public static int SHARE_RESULT_CANCLE = 3;//分享取消
	
	public static String APPTYPE = "apptype";
	
	
	
	/**********采集通讯录信息，保存的时间，一周只采集一次*************/
	public static String COLLECT_CONTACT_INFO_KEY = "collect_contact_info_key";
	public static String LOGIN_TIME_KEY = "login_time_key";
	public static Long SEVEN_DAY_TIME = 604800000L;//七天时间的一个毫秒数
	
	
	/********************赞用户列表界面***************************/
	public static String TOTAL_SUPPORT_NUMBER = "total_support_number";//总关注数量
	public static String SUPPORT_USER_LIST_ACTIVITY_GAMEID = "supprt_user_list_activity_gameid";//赞用户列表的帖子id
	/***********************多选服务器**************************/
	public static String CHOOSED_SERVER_IDS="serverids";
	public static String CHOOSED_SERVER_NAMES="servernames";
	
	public static String ENROLL_CHOOSED_SERVER_ID ="enroll_sid";
	public static String ENROLL_CHOOSED_SERVER_NAME="enroll_servername";
}
