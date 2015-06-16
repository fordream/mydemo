/**      
 * PlayProxyimpl.java Create on 2015-5-8     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.logic;

import java.util.List;

import android.content.Context;

import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.remote.PlayRemoteService;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.IdResult;
import com.iwgame.msgs.proto.Msgs.PlayApplyOrderInfo;
import com.iwgame.msgs.proto.Msgs.PlayApplyOrderList;
import com.iwgame.msgs.proto.Msgs.PlayEvalList;
import com.iwgame.msgs.proto.Msgs.PlayEvalRequest;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.proto.Msgs.PlayInfoList;
import com.iwgame.msgs.proto.Msgs.PlayOrderAppeal;
import com.iwgame.msgs.proto.Msgs.PlayOrderInfo;
import com.iwgame.msgs.proto.Msgs.PlayStar;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: PlayProxyimpl
 * @Description: 陪玩数据代理类接口实现类
 * @author 王卫
 * @date 2015-5-8 下午3:23:28
 * @Version 1.0
 * 
 */
public class PlayProxyImpl implements PlayProxy {

	protected static final String TAG = "PlayProxyimpl";

	private static byte[] lock = new byte[0];

	private static PlayProxyImpl instance = null;

	private PlayRemoteService playService = ServiceFactory.getInstance().getPlayRemoteService();

	private PlayProxyImpl() {

	}

	public static PlayProxyImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new PlayProxyImpl();
				return instance;
			}
		} else {
			return instance;
		}
	}

	/**
	 * 增加用户角色
	 */
	public void createPlayInfo(ProxyCallBack<Integer> callback, final Context context, final Long playid, final byte[] image, final Msgs.PlayInfo info) {
		// TODO Auto-generated method stub
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				LogUtil.d("play", "play1");
				playService.addPlayData(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						LogUtil.d("play", "play2");
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {

							asyncTask.getProxyCallBack().onSuccess(result.getRc());
							// 获取游戏服务器列表
							ProxyFactory.getInstance().getGameProxy().getGameService(new ProxyCallBack<Msgs.UserGameServerResult>() {

								@Override
								public void onSuccess(Msgs.UserGameServerResult result) {
									SystemContext.getInstance().setGameServices(result);
								}

								@Override
								public void onFailure(Integer result, String resultMsg) {
									// TODO Auto-generated method stub

								}
							}, context);
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, playid, image, info);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void searchPlayDetailInfo(ProxyCallBack<PlayInfo> callback, final Context context, final Long playid, final String resulttype) {
		final MyAsyncTask<PlayInfo> asyncTask = new MyAsyncTask<PlayInfo>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				playService.searchPlayInfo(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							if (result.hasExtension(Msgs.playInfoResult)) {
								asyncTask.getProxyCallBack().onSuccess(result.getExtension(Msgs.playInfoResult));
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
				}, context, playid, resulttype);
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
	 * com.iwgame.msgs.module.play.logic.PlayProxy#searchCreatPlays(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, long, int)
	 */
	@Override
	public void searchCreatPlays(final ProxyCallBack<PagerVo<PlayInfo>> callback, final Context context, final Long uid, final Integer status, final long offset, final int limit) {
		final MyAsyncTask<PagerVo<PlayInfo>> asyncTask = new MyAsyncTask<PagerVo<PlayInfo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getPlayRemoteService().searchCreatPlays(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XAction.XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE && result.hasExtension(Msgs.playListResult)) {
							PagerVo<PlayInfo> pagerVo = new PagerVo<PlayInfo>();
							PlayInfoList playInfoList = result.getExtension(Msgs.playListResult);
							List<PlayInfo> list = playInfoList.getPlayInfoList();
							SystemContext.getInstance().setUserPlayStatus(playInfoList.getUserPlayStatus());
							pagerVo.setItems(list);
							asyncTask.getProxyCallBack().onSuccess(pagerVo);
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, uid, status, offset, limit);
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
	 * com.iwgame.msgs.module.play.logic.PlayProxy#searchDiscoverPlays(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, java.lang.String,
	 * long, int)
	 */
	@Override
	public void searchDiscoverPlays(ProxyCallBack<PagerVo<PlayInfo>> callback, final Context context, final long gid, final long sid, final int sorttype, final Long keyid, 
			final String keyval, final Integer sex, final String resulttype, final String position, final long offset, final int limit) {
		final MyAsyncTask<PagerVo<PlayInfo>> asyncTask = new MyAsyncTask<PagerVo<PlayInfo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getPlayRemoteService().searchDiscoverPlays(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XAction.XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE && result.hasExtension(Msgs.playListResult)) {
							PagerVo<PlayInfo> pagerVo = new PagerVo<PlayInfo>();
							List<PlayInfo> list = result.getExtension(Msgs.playListResult).getPlayInfoList();
							pagerVo.setItems(list);
							// pagerVo.setOffset(userQueryResult.getOffset());
							asyncTask.getProxyCallBack().onSuccess(pagerVo);
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, gid, sid, sorttype, keyid, keyval, sex, resulttype, position, offset, limit);
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
	 * com.iwgame.msgs.module.play.logic.PlayProxy#getGameStarLeve(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	public void getGameStarLeve(ProxyCallBack<PlayStar> callback, final Context context, final Long gid) {
		final MyAsyncTask<PlayStar> asyncTask = new MyAsyncTask<PlayStar>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				playService.getGameStarLeve(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							if (result.hasExtension(Msgs.playStarResult)) {
								asyncTask.getProxyCallBack().onSuccess(result.getExtension(Msgs.playStarResult));
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
				}, context, gid);
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
	 * com.iwgame.msgs.module.play.logic.PlayProxy#changePlayStatus(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, java.lang.Long, int)
	 */
	@Override
	public void changePlayStatus(ProxyCallBack<Integer> callback, final Context context, final Long pid, final int type) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				playService.changePlayStatus(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, pid, type);
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
	 * com.iwgame.msgs.module.play.logic.PlayProxy#createPlayEnroll(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long, long, long,
	 * long, long, java.lang.Integer)
	 */
	@Override
	public void createPlayEnroll(ProxyCallBack<IdResult> callback, final Context context, final long pid, final long rid, final long gid,
			final long sid, final long time, final Integer duration) {
		final MyAsyncTask<IdResult> asyncTask = new MyAsyncTask<IdResult>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {
			@Override
			public Void execute() {
				playService.playEnrollOrder(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						LogUtil.d("enroll", "jieguo");
						if (result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							if (result.hasExtension(Msgs.id)) {
								IdResult list = result.getExtension(Msgs.id);
								asyncTask.getProxyCallBack().onSuccess(list);
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
				}, context, pid, rid, gid, sid, time, duration);
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
	 * com.iwgame.msgs.module.play.logic.PlayProxy#searchApplyOrder(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, int, long, int)
	 */
	@Override
	public void searchApplyOrder(ProxyCallBack<PagerVo<PlayApplyOrderInfo>> callback, final Context context, final long pid, final String status,
			final long offset, final int limit) {
		final MyAsyncTask<PagerVo<PlayApplyOrderInfo>> asyncTask = new MyAsyncTask<PagerVo<PlayApplyOrderInfo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getPlayRemoteService().searchApplyOrder(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XAction.XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE && result.hasExtension(Msgs.playApplyOrderListResult)) {
							PagerVo<PlayApplyOrderInfo> pagerVo = new PagerVo<PlayApplyOrderInfo>();
							PlayApplyOrderList playResult = result.getExtension(Msgs.playApplyOrderListResult);
							List<PlayApplyOrderInfo> list = playResult.getOrderInfoList();
							pagerVo.setTotalCount(playResult.getTotal());
							pagerVo.setItems(list);
							asyncTask.getProxyCallBack().onSuccess(pagerVo);
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, pid, status, offset, limit);
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
	 * com.iwgame.msgs.module.play.logic.PlayProxy#searchApplyPlays(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long, int)
	 */
	@Override
	public void searchApplyPlays(ProxyCallBack<PagerVo<PlayOrderInfo>> callback, final Context context, final long offset, final int limit) {
		final MyAsyncTask<PagerVo<PlayOrderInfo>> asyncTask = new MyAsyncTask<PagerVo<PlayOrderInfo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getPlayRemoteService().searchApplyPlays(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XAction.XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE && result.hasExtension(Msgs.playOrderListResult)) {
							PagerVo<PlayOrderInfo> pagerVo = new PagerVo<PlayOrderInfo>();
							List<PlayOrderInfo> list = result.getExtension(Msgs.playOrderListResult).getPlayOrderInfoList();
							pagerVo.setItems(list);
							asyncTask.getProxyCallBack().onSuccess(pagerVo);
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, offset, limit);
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
	 * com.iwgame.msgs.module.play.logic.PlayProxy#getOrderDetail(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void getOrderDetail(ProxyCallBack<PlayOrderInfo> callback, final Context context, final long oid) {
		final MyAsyncTask<PlayOrderInfo> asyncTask = new MyAsyncTask<PlayOrderInfo>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getPlayRemoteService().getOrderDetail(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XAction.XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE && result.hasExtension(Msgs.playOrderInfo)) {
							asyncTask.getProxyCallBack().onSuccess(result.getExtension(Msgs.playOrderInfo));
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, oid);
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
	 * com.iwgame.msgs.module.play.logic.PlayProxy#acceptOrder(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void acceptOrder(ProxyCallBack<Integer> callback, final Context context, final long oid) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				playService.acceptOrder(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, oid);
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
	 * com.iwgame.msgs.module.play.logic.PlayProxy#appealOrder(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context,
	 * com.iwgame.msgs.proto.Msgs.PlayOrderAppeal)
	 */
	@Override
	public void appealOrder(ProxyCallBack<Integer> callback, final Context context, final PlayOrderAppeal playOrderAppeal) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				playService.appealOrder(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, playOrderAppeal);
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
	 * com.iwgame.msgs.module.play.logic.PlayProxy#payOrder(com.iwgame.msgs.
	 * common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void payOrder(ProxyCallBack<Integer> callback, final Context context, final long oid) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				playService.payOrder(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, oid);
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
	 * com.iwgame.msgs.module.play.logic.PlayProxy#cancelOrder(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void cancelOrder(ProxyCallBack<Integer> callback, final Context context, final long oid) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				playService.cancelOrder(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, oid);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub

			}
		});
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.play.logic.PlayProxy#getPlayComments(com.iwgame.msgs.common.ProxyCallBack, android.content.Context, long, long, int)
	 */
	@Override
	public void getPlayComments(ProxyCallBack<PlayEvalList> callback,
			final Context context, final long pid, final long offset, final int limit) {
		final MyAsyncTask<PlayEvalList> asyncTask = new MyAsyncTask<PlayEvalList>(callback);
		asyncTask.execute(new AsyncCallBack<Void>(){

			@Override
			public Void execute() {
				playService.getPlayComments(new ServiceCallBack<XAction.XActionResult>() {
					
					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE && result.hasExtension(Msgs.playEvalListResult)) {
							PlayEvalList list = result.getExtension(Msgs.playEvalListResult);
							asyncTask.getProxyCallBack().onSuccess(list);
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}
					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, pid, offset, limit);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				
			}});
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.play.logic.PlayProxy#sendPlayReply(com.iwgame.msgs.common.ProxyCallBack, android.content.Context, long, java.lang.String)
	 */
	@Override
	public void sendPlayReply(ProxyCallBack<Integer> callback, final Context context,
			final long id, final String content) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>(){

			@Override
			public Void execute() {
				playService.sendPlayRely(new ServiceCallBack<XAction.XActionResult>() {
					
					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}
					
					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, id, content);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				
			}});
	}
	@Override
	public void playEvalOrder(ProxyCallBack<Integer> callback, final Context context,final PlayEvalRequest playeval) {
		// TODO Auto-generated method stub
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				playService.playEvalOrder(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, playeval);
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
	 * com.iwgame.msgs.module.play.logic.PlayProxy#seacrhAppealInfo(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void seacrhAppealInfo(ProxyCallBack<PlayOrderAppeal> callback, final Context context, final long oid) {
		final MyAsyncTask<PlayOrderAppeal> asynTask = new MyAsyncTask<PlayOrderAppeal>(callback);
		asynTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				playService.searchAppealInfo(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE && result.hasExtension(Msgs.orderAppealResult)) {
							asynTask.getProxyCallBack().onSuccess(result.getExtension(Msgs.orderAppealResult));
						} else {
							asynTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asynTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, oid);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
		});
	}

}
