/**      
 * PayProxy.java Create on 2015-5-20     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.pay.adapter;

import java.util.List;

import android.content.Context;

import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.remote.PayRemoteService;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PlayOrderAppeal;
import com.iwgame.msgs.proto.Msgs.RechargeOrderResult;
import com.iwgame.msgs.proto.Msgs.UserYoubiDetail;
import com.iwgame.msgs.proto.Msgs.YoubiDetailInfo;
import com.iwgame.msgs.proto.Msgs.YoubiSchemeResult;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: PayProxy
 * @Description: 支付数据代理接口
 * @author 王卫
 * @date 2015-5-20 下午4:03:06
 * @Version 1.0
 * 
 */
public class PayProxyImpl implements PayProxy {

	protected static final String TAG = "PayProxyImpl";

	private static byte[] lock = new byte[0];

	private static PayProxyImpl instance = null;

	private PayRemoteService payService = ServiceFactory.getInstance().getPayRemoteService();

	private PayProxyImpl() {

	}

	public static PayProxyImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new PayProxyImpl();
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
	 * com.iwgame.msgs.module.pay.adapter.PayProxy#getUb(com.iwgame.msgs.common
	 * .ProxyCallBack, android.content.Context)
	 */
	@Override
	public void getUb(ProxyCallBack<YoubiSchemeResult> callback, final Context context, final int type) {
		final MyAsyncTask<YoubiSchemeResult> asyncTask = new MyAsyncTask<YoubiSchemeResult>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				payService.getUb(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							if (result.hasExtension(Msgs.youbiSchemeResult)) {
								asyncTask.getProxyCallBack().onSuccess(result.getExtension(Msgs.youbiSchemeResult));
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, type);
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
	 * com.iwgame.msgs.module.pay.adapter.PayProxy#pay(com.iwgame.msgs.common
	 * .ProxyCallBack, android.content.Context, int, int, int)
	 */
	@Override
	public void pay(ProxyCallBack<RechargeOrderResult> callback, final Context context, final int faceValue, final int channel, final int phoneType) {
		final MyAsyncTask<RechargeOrderResult> asyncTask = new MyAsyncTask<RechargeOrderResult>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				payService.pay(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							if (result.hasExtension(Msgs.rechargeOrderResult)) {
								asyncTask.getProxyCallBack().onSuccess(result.getExtension(Msgs.rechargeOrderResult));
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, faceValue, channel, phoneType);
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
	 * com.iwgame.msgs.module.pay.adapter.PayProxy#searchOrderDeatil(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, int, long)
	 */
	@Override
	public void searchOrderDeatil(ProxyCallBack<YoubiDetailInfo> callback, final Context context, final int bussinessType, final long orderNo) {
		final MyAsyncTask<YoubiDetailInfo> asyncTask = new MyAsyncTask<YoubiDetailInfo>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				payService.searchOrderDeatil(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							if (result.hasExtension(Msgs.youbiDetailInfo)) {
								asyncTask.getProxyCallBack().onSuccess(result.getExtension(Msgs.youbiDetailInfo));
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, bussinessType, orderNo);
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
	 * com.iwgame.msgs.module.pay.adapter.PayProxy#searchOrderDeatils(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, int, long, int)
	 */
	@Override
	public void searchOrderDeatils(ProxyCallBack<PagerVo<UserYoubiDetail>> callback, final Context context, final int bussinessType,
			final long offset, final int limit) {
		final MyAsyncTask<PagerVo<UserYoubiDetail>> asyncTask = new MyAsyncTask<PagerVo<UserYoubiDetail>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				payService.searchOrderDeatils(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE && result.hasExtension(Msgs.youbiDetailList)) {
							PagerVo<UserYoubiDetail> pagerVo = new PagerVo<UserYoubiDetail>();
							// List<PlayInfo> list =
							// result.getExtension(Msgs.youbiDetailList).getPlayInfoList();
							// pagerVo.setItems(list);
							List<UserYoubiDetail> list = result.getExtension(Msgs.youbiDetailList).getUserYoubiDetailList();
							LogUtil.d("pay", bussinessType + "list大小" + list.size());
							pagerVo.setItems(list);
							asyncTask.getProxyCallBack().onSuccess(pagerVo);
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						LogUtil.d("pay", "接口6");
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, bussinessType, offset, limit);
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
	 * com.iwgame.msgs.module.pay.adapter.PayProxy#cannelOrder(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void cannelOrder(ProxyCallBack<Integer> callback, final Context context, final long order, final int type) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				payService.cannelOrder(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, order, type);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub

			}
		});
	}

}
