/**      
 * UMConstant.java Create on 2014-2-19     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.config;

/**
 * @ClassName: UMConstant
 * @Description: 友盟统计相关配置
 * @author 王卫
 * @date 2014-2-19 下午3:22:23
 * @Version 1.0
 * 
 */
public class UMConfig {

    /**
     * 事件名称
     */
    // 贴吧关注事件
    public static final String MSGS_EVENT_GAME_FOLLOW = "msgs_event_game_follow";
    // 用户关注事件
    public static final String MSGS_EVENT_USER_FOLLOW = "msgs_event_user_follow";
    // 缘分好友关注
    public static final String MSGS_EVENT_FTAE_USER_FOLLOW = "msgs_event_fate_user_follow";
    // 贴吧赞事件
    public static final String MSGS_EVENT_GAME_PRAISE = "msgs_event_game_praise";
    // 贴吧踩事件
    public static final String MSGS_EVENT_GAME_CRITICIZE = "msgs_event_game_criticize";
    // 贴吧包下载
    public static final String MSGS_EVENT_GAMEPACKAGE_DOWNLOAD = "msgs_event_gamepackage_download";
    // 贴吧打开
    public static final String MSGS_EVENT_GAME_OPEN = "msgs_event_game_open";
    // 贴吧帖子浏览数
    public static final String MSGS_EVENT_GAME_POSTBAR_VIEW = "msgs_event_game_postbar_view";
    // 用户注册统计数
    public static final String MSGS_EVENT_USER_REGISTER = "msgs_event_user_register";
    // 加入公会
    public static final String MSGS_EVENT_GROUP_ADD = "msgs_event_group_add";

    // 贴吧-筛选-全部
    public static final String MSGS_EVENT_DISCOVER_BAR_CHOOSE_ALL = "msgs_event_discover_bar_choose_all";
    // 贴吧-筛选-pc端游
    public static final String MSGS_EVENT_DISCOVER_BAR_CHOOSE_PC_GAME = "msgs_event_discover_bar_choose_pc_game";
    // 贴吧-筛选-网页贴吧
    public static final String MSGS_EVENT_DISCOVER_BAR_CHOOSE_PAGE_GAME = "msgs_event_discover_bar_choose_page_game";
    // 贴吧-筛选-手机贴吧
    public static final String MSGS_EVENT_DISCOVER_BAR_CHOOSE_MOBILE_GAME = "msgs_event_discover_bar_choose_mobile_game";
    // 贴吧-筛选-电视贴吧
    public static final String MSGS_EVENT_DISCOVER_BAR_CHOOSE_TV_GAME = "msgs_event_discover_bar_choose_tv_game";
    // 贴吧-筛选-掌机贴吧
    public static final String MSGS_EVENT_DISCOVER_BAR_CHOOSE_MI_GAME = "msgs_event_discover_bar_choose_mi_game";
    // 贴吧-筛选-不限
    public static final String MSGS_EVENT_DISCOVER_BAR_CHOOSE_NET_ALL = "msgs_event_discover_bar_choose_net_all";
    // 贴吧-筛选-单机贴吧
    public static final String MSGS_EVENT_DISCOVER_BAR_CHOOSE_NET_NO = "msgs_event_discover_bar_choose_net_no";
    // 贴吧-筛选-联网贴吧
    public static final String MSGS_EVENT_DISCOVER_BAR_CHOOSE_NET = "msgs_event_discover_bar_choose_net";
    // 绑定新浪微博账号
    public static final String MSGS_EVENT_BINDING_WEIBO = "msgs_event_binding_weibo";

    // 查看用户相册图片
    public static final String MSGS_EVENT_USER_ALBUM_VIEW = "msgs_event_user_album_view";
    // 推荐贴吧
    public static final String MSGS_EVENT_GAME_RECOMMEND = "msgs_event_game_recommend";

    // 长按头像
    public static final String MSGS_EVENT_USER_CLICK_AVATAR = "msgs_event_user_click_avatar";
    // 长按相册图片
    public static final String MSGS_EVENT_USER_CLICK_ALBUM = "msgs_event_user_click_album";

    // 转让公会
    public static final String MSGS_EVENT_GROUP_TRANSFER = "msgs_event_group_transfer";
    // 邀请成员
    public static final String MSGS_EVENT_GROUP_INVITE = "msgs_event_group_invite";

    // 删除成员
    public static final String MSGS_EVENT_GROUP_DEL = "msgs_event_group_del";

    // 通讯录-关注-按关注贴吧分组
    public static final String MSGS_EVENT_USER_BARGROUP_CLICK = "msgs_event_user_bargroup_click";
    // 添加微信好友的按钮点击
    public static final String MSGS_EVENT_USER_WEIXIN_CLICK = "msgs_event_user_weixin_click";
    // 成功添加微信好友
    public static final String MSGS_EVENT_USER_WEIXIN_CLICK_SEND = "msgs_event_user_weixin_click_send";
    // 成功分享个人信息到微信朋友圈
    public static final String MSGS_EVENT_USER_WEIXINPY_CLICK_SEND = "msgs_event_user_weixinpy_click_send";

    // 批准成员
    public static final String MSGS_EVENT_GROUP_APPROVE = "msgs_event_group_approve";
    // 拉黑
    public static final String MSGS_EVENT_USER_BLACK = "msgs_event_user_black";

    // 分享(点击贴吧分享按钮)
    public static final String MSGS_EVENT_SHARE = "msgs_event_share";
    // 成功分享新浪微博
    public static final String MSGS_EVENT_SHARE_WEIBO = "msgs_event_share_weibo";
    // 成功分享微信朋友圈
    public static final String MSGS_EVENT_SHARE_WEIXIN_PY = "msgs_event_share_weixin_py";
    // 成功分享微信好友
    public static final String MSGS_EVENT_SHARE_WEIXIN = "msgs_event_share_weixin";
    // 成功分享腾讯微博
    public static final String MSGS_EVENT_SHARE_TENCENT_WEIBO = "msgs_event_share_tencent_weibo";
    // 成功分享腾讯空间
    public static final String MSGS_EVENT_SHARE_TENCENT_ZONE = "msgs_event_share_tencent_zone";

//    // 添加表情
//    public static final String MSGS_EVENT_SHARE_BIAOQING = "msgs_event_share_biaoqing";

    // 用户贴吧 回复帖子
    public static final String MSGS_EVENT_BAR_COMMENT_TOPIC = "msgs_event_bar_comment_topic";
    //用户贴吧回复回复
    public static final String MSGS_EVENT_BAR_COMMENT_REPLY = "msgs_event_bar_comment_reply";
    // 保存图片
    public static final String MSGS_EVENT_USER_SAVE_PHTO = "msgs_event_user_save_phto";
    // 会长群发消息
    public static final String MSGS_EVENT_GROUP_MCHAT = "msgs_event_group_mchat";
    // 收藏帖子
    public static final String MSGS_EVENT_BAR_COLLECT = "msgs_event_bar_collect";
    // 发送图片
    public static final String MSGS_EVENT_CHAT_SEND_PHOTO = "msgs_event_chat_send_photo";
    // 群聊发送图片
    public static final String MSGS_EVENT_MCHAT_SEND_PHOTO = "msgs_event_mchat_send_photo";
    // 发送语音
    public static final String MSGS_EVENT_CHAT_SEND_VOICE = "msgs_event_chat_send_voice";
    // 群聊发送语音
    public static final String MSGS_EVENT_MCHAT_SEND_VOICE = "msgs_event_mchat_send_voice";
    // 发送文字
    public static final String MSGS_EVENT_CHAT_SEND_WORD = "msgs_event_chat_send_word";
    // 群聊发送文字
    public static final String MSGS_EVENT_MCHAT_SEND_WORD = "msgs_event_mchat_send_word";
    // 首页轮播图切换事件
    public static final String MSGS_EVENT_USERMAIN = "msgs_event_usermain";

    // 缘分好友推荐的查看个人资料
    public static final String MSGS_EVENT_FATE_USER_CLICK = "msgs_event_splendid_user_click";
    // 精彩推荐的用户列表点击链接查看次数 
    public static final String MSGS_EVENT_SPLENDID_USER_CLICK = "msgs_event_splendid_user_click";
    // 精彩推荐的公会列表点击链接查看次数 
    public static final String MSGS_EVENT_SPLENDID_GROUP_CLICK = "msgs_event_splendid_group_click";
    // 精彩推荐的贴吧列表点击链接查看次数 
    public static final String MSGS_EVENT_SPLENDID_GAME_CLICK = "msgs_event_splendid_game_click";
    // 精彩推荐的帖子列表点击链接查看次数 
    public static final String MSGS_EVENT_SPLENDID_TOPIC_CLICK = "msgs_event_splendid_topic_click";
    
    /**
     * 事件属性key
     */
    public static final String MSGS_OPT_FROM_OBJ_ID = "msgs_opt_from_obj_id";
    public static final String MSGS_OPT_FROM_OBJ_NAME = "msgs_opt_from_obj_name";
    public static final String MSGS_OPT_FROM_OBJ2_ID = "msgs_opt_from_obj2_id";
    public static final String MSGS_OPT_FROM_OBJ2_NAME = "msgs_opt_from_obj2_name";
    public static final String MSGS_OPT_TO_OBJ_ID = "msgs_opt_to_obj_id";
    public static final String MSGS_OPT_TO_OBJ_NAME = "msgs_opt_to_obj_name";
    public static final String MSGS_USERMAIN_PAGEINDEX = "msgs_usermain_pageindex";//首页轮播图索引

    public static final String MSGS_SUCCESS = "msgs_success";
    public static final String MSGS_FAIL = "msgs_fail";
}
