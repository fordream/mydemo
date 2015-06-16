/**      
 * MessageUtil.java Create on 2014-5-27     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.iwgame.msgs.module.setting.vo.ChannelGroupVo;
import com.iwgame.msgs.module.setting.vo.ChannelVo;
import com.iwgame.msgs.vo.local.MessageExt;
import com.iwgame.msgs.vo.local.MessageVo;

/**
 * @ClassName: MessageUtil
 * @Description: 消息工具类
 * @author chuanglong
 * @date 2014-5-27 下午2:53:19
 * @Version 1.0
 * 
 */
public class MessageUtil {
	/**
	 * 通过消息获得消息的展示规则
	 * 
	 * @param rules
	 * @param vo
	 * @return
	 */
	public static Object getShowRuleByMessage(List<Object> rules, MessageVo vo) {
		if (rules != null) {
			for (int i = 0; i < rules.size(); i++) {
				Object obj = rules.get(i);
				ChannelVo channelVo = null;
				ChannelGroupVo channelGroupVo = null;
				if (obj instanceof ChannelVo) {
					channelVo = (ChannelVo) obj;
				} else if (obj instanceof ChannelGroupVo) {
					channelGroupVo = (ChannelGroupVo) obj;
					channelVo = channelGroupVo.getChannelVo();
				}
				if (vo.getChannelType().equals(channelVo.getChannelType()) && vo.getCategory().equals(channelVo.getCategory())) {
					// 得到对应的rule
					if (vo.getSubjectId() == -1) {
						return channelGroupVo;
					} else if (channelVo.isUseChannel()) {
						return channelVo;
					} else {
						return channelVo;
					}
				}

			}
		}
		return null;
	}

	public static MessageExt buildMessageExt(MessageVo message) {
		MessageExt messageExt = null;
		if (message != null) {
			String ext = message.getExt();
			if (ext != null && !"".equals(ext)) {
				messageExt = new MessageExt();
				try {
					JSONObject json = new JSONObject(ext);
					if (json.has("op")) {
						messageExt.setOp(json.getInt("op"));
					}
					if (json.has("p")) {
						MessageExt.Content content = messageExt.new Content();
						messageExt.setContent(content);
						JSONObject p = json.getJSONObject("p");
						if (p.has("msg")) {
							content.setMsg(p.getString("msg"));
						}
						if (p.has("uid")) {
							content.setUid(p.getLong("uid"));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return messageExt;
	}

}
