/**      
 * AccountTransformProxy.java Create on 2014-1-24     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.logic;

import android.content.Context;

import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.AsyncResponseHandler;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.module.account.object.GuestObject;

/**
 * @ClassName: AccountTransformProxy
 * @Description: TODO(...)
 * @author chuanglong
 * @date 2014-1-24 下午4:36:42
 * @Version 1.0
 * 
 */
public class AccountTransformProxyImpl implements AccountProxy {
    

	private static byte[] lock = new byte[0];

	private static AccountTransformProxyImpl instance = null;

	private AccountTransformProxyImpl() {

	}

	public static AccountTransformProxyImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new AccountTransformProxyImpl();
				return instance;
			}
		} else {
			return instance;
		}
	}

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iwgame.msgs.module.account.logic.AccountProxy#getCaptcha(com.iwgame
     * .msgs.common.ProxyCallBack, android.content.Context, java.lang.String,
     * int)
     */
    @Override
    public void getCaptcha(final ProxyCallBack<Integer> callback, final Context context, final String mobileNum, final int mode) {
	// TODO Auto-generated method stub
	final AsyncResponseHandler<Integer> handler = new AsyncResponseHandler<Integer>() {

	    @Override
	    public void onSuccess(Integer content) {
		callback.onSuccess(content);
	    }

	    @Override
	    public void onFailure(Integer content,String resultMsg) {
		callback.onFailure(content,resultMsg);
	    }
	};

	final ProxyCallBack<Integer> newCallback = new ProxyCallBack<Integer>() {

	    @Override
	    public void onSuccess(Integer result) {
		// TODO Auto-generated method stub
		handler.setSuccess(result);
	    }

	    @Override
	    public void onFailure(Integer result,String resultMsg) {
		// TODO Auto-generated method stub
		handler.setFailure(result,resultMsg);

	    }

	};
	new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

	    @Override
	    public Void execute() {
		// TODO Auto-generated method stub
		AccountProxyImpl.getInstance().getCaptcha(newCallback, context, mobileNum, mode);
		return null;
	    }

	    @Override
	    public void onHandle(Void result) {
		// TODO Auto-generated method stub
	    }
	});

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iwgame.msgs.module.account.logic.AccountProxy#validateCaptcha(com
     * .iwgame.msgs.common.ProxyCallBack, android.content.Context,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void validateCaptcha(final ProxyCallBack<Integer> callback, final Context context, final String mobileNum, final String captcha) {
	// TODO Auto-generated method stub
	final AsyncResponseHandler<Integer> handler = new AsyncResponseHandler<Integer>() {

	    @Override
	    public void onSuccess(Integer content) {
		callback.onSuccess(content);
	    }

	    @Override
	    public void onFailure(Integer content,String resultMsg) {
		callback.onFailure(content,resultMsg);
	    }
	};

	final ProxyCallBack<Integer> newCallback = new ProxyCallBack<Integer>() {

	    @Override
	    public void onSuccess(Integer result) {
		// TODO Auto-generated method stub
		handler.setSuccess(result);
	    }

	    @Override
	    public void onFailure(Integer result,String resultMsg) {
		// TODO Auto-generated method stub
		handler.setFailure(result,resultMsg);

	    }

	};
	new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

	    @Override
	    public Void execute() {
		// TODO Auto-generated method stub
		AccountProxyImpl.getInstance().validateCaptcha(newCallback, context, mobileNum, captcha);
		return null;
	    }

	    @Override
	    public void onHandle(Void result) {
		// TODO Auto-generated method stub
	    }
	});
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iwgame.msgs.module.account.logic.AccountProxy#registerAccount(com
     * .iwgame.msgs.common.ProxyCallBack, android.content.Context,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * int, int, byte[])
     */
    @Override
    public void registerAccount(final ProxyCallBack<Integer> callback, final Context context, final String captcha, final String accountName, final String password, final String nickname, final int sex,final  Integer age,final  byte[] avatar, final String origin) {
	// TODO Auto-generated method stub
	final AsyncResponseHandler<Integer> handler = new AsyncResponseHandler<Integer>() {

	    @Override
	    public void onSuccess(Integer content) {
		callback.onSuccess(content);
	    }

	    @Override
	    public void onFailure(Integer content,String resultMsg) {
		callback.onFailure(content,resultMsg);
	    }
	};

	final ProxyCallBack<Integer> newCallback = new ProxyCallBack<Integer>() {

	    @Override
	    public void onSuccess(Integer result) {
		// TODO Auto-generated method stub
		handler.setSuccess(result);
	    }

	    @Override
	    public void onFailure(Integer result,String resultMsg) {
		// TODO Auto-generated method stub
		handler.setFailure(result,resultMsg);

	    }

	};
	new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

	    @Override
	    public Void execute() {
		// TODO Auto-generated method stub
		AccountProxyImpl.getInstance().registerAccount(newCallback, context, captcha, accountName, password, nickname, sex, age, avatar, origin);
		return null;
	    }

	    @Override
	    public void onHandle(Void result) {
		// TODO Auto-generated method stub
	    }
	});

    }
    
    /**
     * 绑定手机
     */
    @Override
	public void bundPhone(final ProxyCallBack<Integer> callback, final Context context,
			final String captcha, final String accountName, final String password, final String nickname, final int sex, final byte[] avatar) {
    	// TODO Auto-generated method stub
    	final AsyncResponseHandler<Integer> handler = new AsyncResponseHandler<Integer>() {

    	    @Override
    	    public void onSuccess(Integer content) {
    		callback.onSuccess(content);
    	    }

    	    @Override
    	    public void onFailure(Integer content,String resultMsg) {
    		callback.onFailure(content,resultMsg);
    	    }
    	};

    	final ProxyCallBack<Integer> newCallback = new ProxyCallBack<Integer>() {

    	    @Override
    	    public void onSuccess(Integer result) {
    		// TODO Auto-generated method stub
    		handler.setSuccess(result);
    	    }

    	    @Override
    	    public void onFailure(Integer result,String resultMsg) {
    		// TODO Auto-generated method stub
    		handler.setFailure(result,resultMsg);

    	    }

    	};
    	new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

    	    @Override
    	    public Void execute() {
    		// TODO Auto-generated method stub
    		AccountProxyImpl.getInstance().bundPhone(newCallback, context, captcha, accountName, password, nickname, sex, avatar);
    		return null;
    	    }

    	    @Override
    	    public void onHandle(Void result) {
    		// TODO Auto-generated method stub
    	    }
    	});		
	}
    
    @Override
    public void registerGuestAccount(final ProxyCallBack<GuestObject> callback, final Context context, final String origin) {
	// TODO Auto-generated method stub
	final AsyncResponseHandler<GuestObject> handler = new AsyncResponseHandler<GuestObject>() {

	    @Override
	    public void onSuccess(GuestObject content) {
		callback.onSuccess(content);
	    }

	    @Override
	    public void onFailure(Integer content,String resultMsg) {
		callback.onFailure(content,resultMsg);
	    }
	};

	final ProxyCallBack<GuestObject> newCallback = new ProxyCallBack<GuestObject>() {

	    @Override
	    public void onSuccess(GuestObject result) {
		// TODO Auto-generated method stub
		handler.setSuccess(result);
	    }

	    @Override
	    public void onFailure(Integer result,String resultMsg) {
		// TODO Auto-generated method stub
		handler.setFailure(result,resultMsg);

	    }

	};
	new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

	    @Override
	    public Void execute() {
		// TODO Auto-generated method stub
		AccountProxyImpl.getInstance().registerGuestAccount(newCallback, context, origin);
		return null;
	    }

	    @Override
	    public void onHandle(Void result) {
		// TODO Auto-generated method stub
	    }
	});

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iwgame.msgs.module.account.logic.AccountProxy#resetPassword(com.iwgame
     * .msgs.common.ProxyCallBack, android.content.Context, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void resetPassword(final ProxyCallBack<Integer> callback, final Context context,final String account,final String password,final String captcha) {
	// TODO Auto-generated method stub
	final AsyncResponseHandler<Integer> handler = new AsyncResponseHandler<Integer>() {

	    @Override
	    public void onSuccess(Integer content) {
		callback.onSuccess(content);
	    }

	    @Override
	    public void onFailure(Integer content,String resultMsg) {
		callback.onFailure(content,resultMsg);
	    }
	};

	final ProxyCallBack<Integer> newCallback = new ProxyCallBack<Integer>() {

	    @Override
	    public void onSuccess(Integer result) {
		// TODO Auto-generated method stub
		handler.setSuccess(result);
	    }

	    @Override
	    public void onFailure(Integer result,String resultMsg) {
		// TODO Auto-generated method stub
		handler.setFailure(result,resultMsg);

	    }

	};
	new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

	    @Override
	    public Void execute() {
		// TODO Auto-generated method stub
		AccountProxyImpl.getInstance().resetPassword(newCallback, context, account, password, captcha);
		return null;
	    }

	    @Override
	    public void onHandle(Void result) {
		// TODO Auto-generated method stub
	    }
	});
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iwgame.msgs.module.account.logic.AccountProxy#login(com.iwgame.msgs
     * .common.ProxyCallBack, android.content.Context, java.lang.String,
     * java.lang.String)
     */
    @Override
    public void login(final ProxyCallBack<Integer> callback,final Context context,final String account,final String password) {
	// TODO Auto-generated method stub
	final AsyncResponseHandler<Integer> handler = new AsyncResponseHandler<Integer>() {

	    @Override
	    public void onSuccess(Integer content) {
		callback.onSuccess(content);
	    }

	    @Override
	    public void onFailure(Integer content,String resultMsg) {
		callback.onFailure(content,resultMsg);
	    }
	};

	final ProxyCallBack<Integer> newCallback = new ProxyCallBack<Integer>() {

	    @Override
	    public void onSuccess(Integer result) {
		// TODO Auto-generated method stub
		handler.setSuccess(result);
	    }

	    @Override
	    public void onFailure(Integer result,String resultMsg) {
		// TODO Auto-generated method stub
		handler.setFailure(result,resultMsg);

	    }

	};
	new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

	    @Override
	    public Void execute() {
		// TODO Auto-generated method stub
		AccountProxyImpl.getInstance().login(newCallback, context, account, password);
		return null;
	    }

	    @Override
	    public void onHandle(Void result) {
		// TODO Auto-generated method stub
	    }
	});
    }

	@Override
	public void getUserInfoHasLogin(final ProxyCallBack<Integer> callback,
			final Context context, final String account, final String password) {
		final AsyncResponseHandler<Integer> handler = new AsyncResponseHandler<Integer>() {

		    @Override
		    public void onSuccess(Integer content) {
			callback.onSuccess(content);
		    }

		    @Override
		    public void onFailure(Integer content,String resultMsg) {
			callback.onFailure(content,resultMsg);
		    }
		};

		final ProxyCallBack<Integer> newCallback = new ProxyCallBack<Integer>() {

		    @Override
		    public void onSuccess(Integer result) {
			handler.setSuccess(result);
		    }

		    @Override
		    public void onFailure(Integer result,String resultMsg) {
			handler.setFailure(result,resultMsg);

		    }

		};
		new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

		    @Override
		    public Void execute() {
			AccountProxyImpl.getInstance().getUserInfoHasLogin(newCallback, context, account, password);
			return null;
		    }

		    @Override
		    public void onHandle(Void result) {
		    }
		});
	}

    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iwgame.msgs.module.account.logic.AccountProxy#verifyAccount(com.iwgame
     * .msgs.common.ProxyCallBack, android.content.Context)
     */
    @Override
    public void verifyAccount(final ProxyCallBack<Integer> callback,final Context context) {
	// TODO Auto-generated method stub
	final AsyncResponseHandler<Integer> handler = new AsyncResponseHandler<Integer>() {

	    @Override
	    public void onSuccess(Integer content) {
		callback.onSuccess(content);
	    }

	    @Override
	    public void onFailure(Integer content,String resultMsg) {
		callback.onFailure(content,resultMsg);
	    }
	};

	final ProxyCallBack<Integer> newCallback = new ProxyCallBack<Integer>() {

	    @Override
	    public void onSuccess(Integer result) {
		// TODO Auto-generated method stub
		handler.setSuccess(result);
	    }

	    @Override
	    public void onFailure(Integer result,String resultMsg) {
		// TODO Auto-generated method stub
		handler.setFailure(result,resultMsg);

	    }

	};
	new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

	    @Override
	    public Void execute() {
		// TODO Auto-generated method stub
		AccountProxyImpl.getInstance().verifyAccount(newCallback, context);
		return null;
	    }

	    @Override
	    public void onHandle(Void result) {
		// TODO Auto-generated method stub
	    }
	});
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iwgame.msgs.module.account.logic.AccountProxy#logout(com.iwgame.msgs
     * .common.ProxyCallBack, android.content.Context, java.lang.String)
     */
    @Override
    public void logout(final ProxyCallBack<Integer> callback,final Context context,final String token) {
	// TODO Auto-generated method stub
	final AsyncResponseHandler<Integer> handler = new AsyncResponseHandler<Integer>() {

	    @Override
	    public void onSuccess(Integer content) {
		callback.onSuccess(content);
	    }

	    @Override
	    public void onFailure(Integer content,String resultMsg) {
		callback.onFailure(content,resultMsg);
	    }
	};

	final ProxyCallBack<Integer> newCallback = new ProxyCallBack<Integer>() {

	    @Override
	    public void onSuccess(Integer result) {
		// TODO Auto-generated method stub
		handler.setSuccess(result);
	    }

	    @Override
	    public void onFailure(Integer result,String resultMsg) {
		// TODO Auto-generated method stub
		handler.setFailure(result,resultMsg);

	    }

	};
	new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

	    @Override
	    public Void execute() {
		// TODO Auto-generated method stub
		AccountProxyImpl.getInstance().logout(newCallback, context, token);
		return null;
	    }

	    @Override
	    public void onHandle(Void result) {
		// TODO Auto-generated method stub
	    }
	});
    }
    
    /* (non-Javadoc)
     * @see com.iwgame.msgs.module.account.logic.AccountProxy#validateAccount(com.iwgame.msgs.common.ProxyCallBack, android.content.Context)
     */
    @Override
    public void validateAccount(final ProxyCallBack<Integer> callback, final Context context) {
    	// TODO Auto-generated method stub
    	final AsyncResponseHandler<Integer> handler = new AsyncResponseHandler<Integer>() {

    	    @Override
    	    public void onSuccess(Integer content) {
    		callback.onSuccess(content);
    	    }

    	    @Override
    	    public void onFailure(Integer content,String resultMsg) {
    		callback.onFailure(content,resultMsg);
    	    }
    	};

    	final ProxyCallBack<Integer> newCallback = new ProxyCallBack<Integer>() {

    	    @Override
    	    public void onSuccess(Integer result) {
    		// TODO Auto-generated method stub
    		handler.setSuccess(result);
    	    }

    	    @Override
    	    public void onFailure(Integer result,String resultMsg) {
    		// TODO Auto-generated method stub
    		handler.setFailure(result,resultMsg);

    	    }

    	};
    	new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

    	    @Override
    	    public Void execute() {
    		// TODO Auto-generated method stub
    		AccountProxyImpl.getInstance().validateAccount(callback, context);
    		return null;
    	    }

    	    @Override
    	    public void onHandle(Void result) {
    		// TODO Auto-generated method stub
    	    }
    	});
    }

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.account.logic.AccountProxy#login(com.iwgame.msgs.common.ProxyCallBack, android.content.Context, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public void login(final ProxyCallBack<Integer> callback, final Context context,
			final String account, final int authtype, final String apitype, final String openId) {
		// TODO Auto-generated method stub
		final AsyncResponseHandler<Integer> handler = new AsyncResponseHandler<Integer>() {

		    @Override
		    public void onSuccess(Integer content) {
			callback.onSuccess(content);
		    }

		    @Override
		    public void onFailure(Integer content,String resultMsg) {
			callback.onFailure(content,resultMsg);
		    }
		};

		final ProxyCallBack<Integer> newCallback = new ProxyCallBack<Integer>() {

		    @Override
		    public void onSuccess(Integer result) {
			// TODO Auto-generated method stub
			handler.setSuccess(result);
		    }

		    @Override
		    public void onFailure(Integer result,String resultMsg) {
			// TODO Auto-generated method stub
			handler.setFailure(result,resultMsg);

		    }

		};
		new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

		    @Override
		    public Void execute() {
			// TODO Auto-generated method stub
			AccountProxyImpl.getInstance().login(newCallback, context, account, authtype, apitype, openId);
			return null;
		    }

		    @Override
		    public void onHandle(Void result) {
			// TODO Auto-generated method stub
		    }
		});
		
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.account.logic.AccountProxy#registerAccount(com.iwgame.msgs.common.ProxyCallBack, android.content.Context, java.lang.String, int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public void registerAccount(final ProxyCallBack<Integer> callback,
			final Context context, final String openID, final int target, final int sex, final String avatar,
			final String nickname, final String origin) {
		// TODO Auto-generated method stub
		final AsyncResponseHandler<Integer> handler = new AsyncResponseHandler<Integer>() {

		    @Override
		    public void onSuccess(Integer content) {
			callback.onSuccess(content);
		    }

		    @Override
		    public void onFailure(Integer content,String resultMsg) {
			callback.onFailure(content,resultMsg);
		    }
		};

		final ProxyCallBack<Integer> newCallback = new ProxyCallBack<Integer>() {

		    @Override
		    public void onSuccess(Integer result) {
			// TODO Auto-generated method stub
			handler.setSuccess(result);
		    }

		    @Override
		    public void onFailure(Integer result,String resultMsg) {
			// TODO Auto-generated method stub
			handler.setFailure(result,resultMsg);

		    }

		};
		new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

		    @Override
		    public Void execute() {
			// TODO Auto-generated method stub
			AccountProxyImpl.getInstance().registerAccount(newCallback, context, openID, target, sex, avatar, nickname, origin);
			return null;
		    }

		    @Override
		    public void onHandle(Void result) {
			// TODO Auto-generated method stub
		    }
		});

	}

	
}
