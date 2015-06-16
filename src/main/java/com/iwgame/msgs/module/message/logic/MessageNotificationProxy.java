/**      
* MessageNotificationProxy.java Create on 2014-3-25     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.message.logic;

import android.content.Context;

import com.iwgame.msgs.vo.local.MessageVo;

/** 
 * @ClassName: MessageNotificationProxy 
 * @Description: TODO(消息通知代理接口) 
 * @author chuanglong
 * @date 2014-3-25 下午2:10:55 
 * @Version 1.0
 * 
 */
public interface MessageNotificationProxy {
    /**
     * 发送消息到通知栏，点击打开人和人对聊窗口
     * @param context
     * @param vo
     */
    public void sendMessageToNotificationOpenChat( Context context,  MessageVo vo);
    /**
     * 发送消息到通知栏，点击打开游伴小助手消息窗口
     * @param context
     * @param vo
     */
    public void sendMessageToNotificationOpenSystemChat( Context context,  MessageVo vo) ;
    
    /**
     * 发送消息到通知栏，点击打开精彩推荐消息窗口
     * @param context
     * @param vo
     */
    public void sendMessageToNotificationOpenSplendidChat( Context context,  MessageVo vo) ;
    
    /**
     * 发送消息到通知栏，点击打开系统公告消息窗口
     * @param context
     * @param vo
     */
    public void sendMessageToNotificationOpenSysOfficialChat( Context context,  MessageVo vo) ;
    
    
    /**
     * 发送消息到通知栏，点击打开公会聊天室窗口
     * @param context
     * @param vo
     */
    public void sendMessageToNotificationOpenGroupChat( Context context,  MessageVo vo);
    /**
     * 发送消息到通知栏，点击打开关于我的
     * @param context
     * @param vo
     */
    public void sendMessageToNotificationOpenGyMy( Context context,  MessageVo vo);
    /**
     * 发送消息到通知栏，点击打开某个公会的会长群发消息界面
     * @param context
     * @param vo
     */
    public void sendMessageToNotificationOpenGroupMassMsg( Context context,  MessageVo vo);
    /**
     * 发送消息到通知栏，点击打开首页
     * @param context
     * @param vo
     */
    public void sendMessageToNotificationOpenMain( Context context,  MessageVo vo);
    

}
