/**      
 * RemoteServiceFactory.java Create on 2013-8-28     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module;

import com.iwgame.msgs.module.baidu.service.BaiduLocationService;
import com.iwgame.msgs.module.baidu.service.BaiduLocationServiceImpl;
import com.iwgame.msgs.module.remote.AccountRemoteService;
import com.iwgame.msgs.module.remote.AccountRemoteServiceImpl;
import com.iwgame.msgs.module.remote.CommonRemoteService;
import com.iwgame.msgs.module.remote.CommonRemoteServiceImpl;
import com.iwgame.msgs.module.remote.ContentRemoteService;
import com.iwgame.msgs.module.remote.ContentRemoteServiceImpl;
import com.iwgame.msgs.module.remote.MessageRemoteService;
import com.iwgame.msgs.module.remote.MessageRemoteServiceImpl;
import com.iwgame.msgs.module.remote.PayRemoteService;
import com.iwgame.msgs.module.remote.PayRemoteServiceImpl;
import com.iwgame.msgs.module.remote.PlayRemoteService;
import com.iwgame.msgs.module.remote.PlayRemoteServiceImpl;
import com.iwgame.msgs.module.remote.PostbarRemoteService;
import com.iwgame.msgs.module.remote.PostbarRemoteServiceImpl;
import com.iwgame.msgs.module.remote.SearchRemoteService;
import com.iwgame.msgs.module.remote.SearchRemoteServiceImpl;
import com.iwgame.msgs.module.remote.UserRemoteService;
import com.iwgame.msgs.module.remote.UserRemoteServiceImpl;
import com.iwgame.msgs.module.sync.AsyncSyncEntityServiceImpl;
import com.iwgame.msgs.module.sync.AsyncSyncListServiceImpl;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.words.WordsManager;
import com.iwgame.msgs.module.words.WordsManagerImpl;

/**
 * @ClassName: RemoteServiceFactory
 * @Description: 获得所有SERVICE实例工厂
 * @author 王卫
 * @date 2013-8-28 上午09:13:23
 * @Version 1.0
 * 
 */
public class ServiceFactory {

	private static byte[] lock = new byte[0];

	private static ServiceFactory instance = null;

	private ServiceFactory() {

	}

	public static ServiceFactory getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new ServiceFactory();
				return instance;
			}
		} else {
			return instance;
		}
	}

	public AccountRemoteService getAccountRemoteService() {
		return AccountRemoteServiceImpl.getInstance();
	}

	public ContentRemoteService getContentRemoteService() {
		return ContentRemoteServiceImpl.getInstance();
	}

	public SearchRemoteService getSearchRemoteService() {
		return SearchRemoteServiceImpl.getInstance();
	}

	public UserRemoteService getUserRemoteService() {
		return UserRemoteServiceImpl.getInstance();
	}

	public BaiduLocationService getBaiduLocationService() {
		return BaiduLocationServiceImpl.getInstance();
	}

	public MessageRemoteService getMessageRemoteService() {
		return MessageRemoteServiceImpl.getInstance();
	}

	public SyncListService getSyncListService() {
		return AsyncSyncListServiceImpl.getInstance();
	}

	public SyncEntityService getSyncEntityService() {
		return AsyncSyncEntityServiceImpl.getInstance();
	}

	public PostbarRemoteService getPostbarService() {
		return PostbarRemoteServiceImpl.getInstance();
	}

	public WordsManager getWordsManager() {
		return WordsManagerImpl.getInstance();
	}

	public CommonRemoteService getCommonRemoteService() {
		return CommonRemoteServiceImpl.getInstance();
	}
	
	public PlayRemoteService getPlayRemoteService(){
		return PlayRemoteServiceImpl.getInstance();
	}

	public PayRemoteService getPayRemoteService(){
		return PayRemoteServiceImpl.getInstance();
	}

}
