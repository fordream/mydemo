/**      
 * ProxyFactory.java Create on 2013-8-28     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module;

import com.iwgame.msgs.module.account.logic.AccountProxy;
import com.iwgame.msgs.module.account.logic.AccountProxyImpl;
import com.iwgame.msgs.module.cache.Cache;
import com.iwgame.msgs.module.cache.CacheImpl;
import com.iwgame.msgs.module.game.logic.GameProxy;
import com.iwgame.msgs.module.game.logic.GameProxyImpl;
import com.iwgame.msgs.module.group.logic.GroupProxy;
import com.iwgame.msgs.module.group.logic.GroupProxyImpl;
import com.iwgame.msgs.module.message.logic.MessageNotificationProxy;
import com.iwgame.msgs.module.message.logic.MessageNotificationProxyImpl;
import com.iwgame.msgs.module.message.logic.MessageProxy;
import com.iwgame.msgs.module.message.logic.MessageProxyImpl;
import com.iwgame.msgs.module.pay.adapter.PayProxy;
import com.iwgame.msgs.module.pay.adapter.PayProxyImpl;
import com.iwgame.msgs.module.play.logic.PlayProxy;
import com.iwgame.msgs.module.play.logic.PlayProxyImpl;
import com.iwgame.msgs.module.postbar.logic.PostbarProxy;
import com.iwgame.msgs.module.postbar.logic.PostbarProxyImpl;
import com.iwgame.msgs.module.setting.logic.SettingProxy;
import com.iwgame.msgs.module.setting.logic.SettingProxyImpl;
import com.iwgame.msgs.module.user.logic.UserProxy;
import com.iwgame.msgs.module.user.logic.UserProxyImpl;

/**
 * @ClassName: ProxyFactory
 * @Description: 获得所有PROXY实例工厂
 * @author 王卫
 * @date 2013-8-28 上午09:10:44
 * @Version 1.0
 * 
 */
public class ProxyFactory {

	private static byte[] lock = new byte[0];

	private static ProxyFactory instance = null;

	private ProxyFactory() {

	}

	public static ProxyFactory getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new ProxyFactory();
				return instance;
			}
		} else {
			return instance;
		}
	}

	public AccountProxy getAccountProxy() {
		return AccountProxyImpl.getInstance();
	}

	public UserProxy getUserProxy() {
		return UserProxyImpl.getInstance();
	}

	public SettingProxy getSettingProxy() {
		return SettingProxyImpl.getInstance();
	}

	public GameProxy getGameProxy() {
		return GameProxyImpl.getInstance();
	}

	public MessageProxy getMessageProxy() {
		return MessageProxyImpl.getInstance();
	}

	public GroupProxy getGroupProxy() {
		return GroupProxyImpl.getInstance();
	}

	public PostbarProxy getPostbarProxy() {
		return PostbarProxyImpl.getInstance();
	}

	public Cache getCache() {
		return CacheImpl.getInstance();
	}

	public MessageNotificationProxy getMessageNotificationProxy() {
		return MessageNotificationProxyImpl.getInstance();
	}

	public PlayProxy getPlayProxy() {
		return PlayProxyImpl.getInstance();
	}

	public PayProxy getPayProxy() {
		return PayProxyImpl.getInstance();
	}
}
