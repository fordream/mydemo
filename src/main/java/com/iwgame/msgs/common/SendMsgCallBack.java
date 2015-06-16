/**      
* SendMsgCallBack.java Create on 2013-9-22     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.common;

/** 
 * @ClassName: SendMsgCallBack 
 * @Description: TODO(发送消息控件 的回调) 
 * @author 吴禹青
 * @date 2013-9-22 下午8:36:28 
 * @Version 1.0
 * 
 */
public interface SendMsgCallBack {
	public int ACTION_TEXT = 0 ;//默认
	public int ACTION_MICROPHONE = 1 ;
	public int ACTION_PICTURE = 2 ;
	public int ACTION_PHOTO = 3 ;
	
	public int AUDIORECORDER_STOP = 1;//默认
	public int AUDIORECORDER_ING = 2 ;//  录音中
	/**
	 * 发送消息（content和contentBytes 只有一个起效，根据action
	 * @param msgtype 消息类型
	 * @param content 消息内容
	 * @param contentBytes  二进制的消息内容
	 * @param action 操作（用于拍照，相册，声音）,默认0 为文本
	 */
	public void send(int msgtype,String content,byte[] contentBytes,int action );
	
	public void setAudioRecorderStatus(int status);
	
	/**
	 * 设置列表选择到最后一个
	 */
	public void setListViewLastIndexSelection(int delaytime);
	
	/**
	 * 创建需要绑定手机对话框
	 */
	public void createDialog();

}
