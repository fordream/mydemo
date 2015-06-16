/**      
* SocketReceiveMessageListener.java Create on 2013-9-24     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.common;

import com.google.protobuf.InvalidProtocolBufferException;
import com.iwgame.xnode.proto.XMessage.Message;

/** 
 * @ClassName: SocketReceiveMessageListener 
 * @Description: TODO(socket接受到数据后处理接口) 
 * @author Administrator
 * @date 2013-9-24 上午10:54:01 
 * @Version 1.0
 * 
 */
public interface SocketReceiveMessageListener {

	/**
	 * 通道刚连接上，需要重新做业务握手连接
	 */
	public static int TYPE_RECONNENT = 1 ;
	/**
	 * 数据正常收发
	 */
	public static int TYPE_NORMAL = 2;
	/**
	 * socket接受到数据后处理
	 * @param type 回调类型  [1,通道重连接过，需要重新做业务握手连接；2数据正常收发]
	 * @param message Message格式的业务数据
	 * @throws InvalidProtocolBufferException
	 */
	public void onHandle(int type ,Message message) throws InvalidProtocolBufferException;
}
