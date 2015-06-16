/**      
 * GameProxyImpl.java Create on 2013-9-3     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.localdb.dao.GamePackageDao;
import com.iwgame.msgs.localdb.dao.RelationGameDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.cache.Cache;
import com.iwgame.msgs.module.cache.CacheCallBack;
import com.iwgame.msgs.module.game.object.GameExtDataObj;
import com.iwgame.msgs.module.game.object.GameExtDataVo;
import com.iwgame.msgs.module.game.object.GameTopItemObj;
import com.iwgame.msgs.module.remote.ContentRemoteService;
import com.iwgame.msgs.module.remote.PostbarRemoteService;
import com.iwgame.msgs.module.remote.SearchRemoteService;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentExtDataResult;
import com.iwgame.msgs.proto.Msgs.ContentExtDataResult.ContentExtData;
import com.iwgame.msgs.proto.Msgs.GameFollowResult;
import com.iwgame.msgs.proto.Msgs.GameFollowResult.GameFollowData;
import com.iwgame.msgs.proto.Msgs.GameFriendCountResult;
import com.iwgame.msgs.proto.Msgs.GameFriendCountResult.GameFriendCountEntry;
import com.iwgame.msgs.proto.Msgs.GameQueryResult;
import com.iwgame.msgs.proto.Msgs.GameQueryResult.GameQueryEntry;
import com.iwgame.msgs.proto.Msgs.GameRecommendResult;
import com.iwgame.msgs.proto.Msgs.GameRecommendResult.GameRecommendEntry;
import com.iwgame.msgs.proto.Msgs.GameSearchDetail;
import com.iwgame.msgs.proto.Msgs.PostbarMaxIndexResult;
import com.iwgame.msgs.proto.Msgs.PostbarMaxIndexResult.PostbarMaxIndex;
import com.iwgame.msgs.utils.BuildVoUtil;
import com.iwgame.msgs.vo.local.GamePackageVo;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.RelationGameVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.xaction.proto.XAction;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: GameProxyImpl
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-9-3 下午12:05:09
 * @Version 1.0
 * 
 */
public class GameProxyImpl implements GameProxy {

	private ContentRemoteService contentService = ServiceFactory.getInstance().getContentRemoteService();

	private SearchRemoteService searchService = ServiceFactory.getInstance().getSearchRemoteService();

	private PostbarRemoteService postbarService = ServiceFactory.getInstance().getPostbarService();

	private static byte[] lock = new byte[0];

	private static GameProxyImpl instance = null;

	private GameProxyImpl() {

	}

	public static GameProxyImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new GameProxyImpl();
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#getFollowGamesForLocal(com
	 * .iwgame.msgs.common.ProxyCallBack, android.content.Context)
	 */
	public void getFollowGamesForLocal(ProxyCallBack<List<GameVo>> callback, Context context) {
		// 先从本地数据库返回数据
		final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
		callback.onSuccess(gameDao.getGameListByRelation(1, 0, 1, Integer.MAX_VALUE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.game.logic.GameProxy#getFollowGames(com.iwgame
	 * .msgs.common.ProxyCallBack)
	 */
	@Override
	public void getFollowGames(final ProxyCallBack<List<GameVo>> callback, final Context context, final boolean isNotGetLocal) {
		if (!isNotGetLocal || !NetworkUtil.isNetworkAvailable(SystemContext.getInstance().getContext())) {
			// 先从本地数据库返回数据
			final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
			callback.onSuccess(gameDao.getGameListByRelation(1, 0, 1, Integer.MAX_VALUE));
		}
		// 再从远端同步我的贴吧
		final MyAsyncTask<List<GameVo>> asyncTask = new MyAsyncTask<List<GameVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getSyncListService()
						.syncList(SyncListService.TYPE_MY_GAME, new com.iwgame.msgs.module.sync.SyncCallBack() {

							@Override
							public void onSuccess(Object result) {
								final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
								asyncTask.getProxyCallBack().onSuccess(gameDao.getGameListByRelation(1, 0, 1, Integer.MAX_VALUE));
							}

							@Override
							public void onFailure(Integer result) {
								final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
								asyncTask.getProxyCallBack().onSuccess(gameDao.getGameListByRelation(1, 0, 1, Integer.MAX_VALUE));
							}
						});
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#getFollowGames(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, boolean,
	 * java.lang.Long)
	 */
	@Override
	public void getFollowGamesByUtime(final ProxyCallBack<List<GameVo>> callback, Context context, final boolean isNotGetLocal, final Long utime) {
		if (!isNotGetLocal) {
			// 先从本地数据库返回数据
			final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
			callback.onSuccess(gameDao.getGameListByRelation(1, 0, 1, Integer.MAX_VALUE));
		}
		// 再从远端同步我的贴吧
		final MyAsyncTask<List<GameVo>> asyncTask = new MyAsyncTask<List<GameVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getSyncListService()
						.syncListByUtime(SyncListService.TYPE_MY_GAME, utime, new com.iwgame.msgs.module.sync.SyncCallBack() {

							@Override
							public void onSuccess(Object result) {
								final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
								asyncTask.getProxyCallBack().onSuccess(gameDao.getGameListByRelation(1, 0, 1, Integer.MAX_VALUE));
							}

							@Override
							public void onFailure(Integer result) {
								final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
								asyncTask.getProxyCallBack().onSuccess(gameDao.getGameListByRelation(1, 0, 1, Integer.MAX_VALUE));
							}
						});
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#getGameInfo(com.iwgame.msgs
	 * .common.ProxyCallBack, long)
	 */
	@Override
	public void getGameInfo(final ProxyCallBack<GameVo> callback, final Context context, final long gid) {
		final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
		if (!NetworkUtil.isNetworkAvailable(context)) {
			callback.onSuccess(gameDao.getGameByGameId(gid));
			return;
		}
		final MyAsyncTask<GameVo> asyncTask = new MyAsyncTask<GameVo>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getSyncEntityService().syncEntity(gid, SyncEntityService.TYPE_GAME, new SyncCallBack() {

					@Override
					public void onSuccess(Object result) {
						asyncTask.getProxyCallBack().onSuccess(gameDao.getGameByGameId(gid));
					}

					@Override
					public void onFailure(Integer result) {
						asyncTask.getProxyCallBack().onSuccess(gameDao.getGameByGameId(gid));
					}
				});
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	@Override
	public void getGamesInfo(final ProxyCallBack<List<GameVo>> callback, final Context context, final List<Long> gids, boolean isNotFromLocal) {
		String ids = "";
		for (int i = 0; i < gids.size(); i++) {
			long gid = gids.get(i);
			if (i != gids.size() - 1) {
				ids = ids + gid + ",";
			} else {
				ids = ids + gid;
			}
		}

		final String tempIds = ids;

		final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
		if (!NetworkUtil.isNetworkAvailable(context) || !isNotFromLocal) {
			callback.onSuccess(gameDao.searchGamesByIds(tempIds));
			return;
		}
		final MyAsyncTask<List<GameVo>> asyncTask = new MyAsyncTask<List<GameVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getSyncEntityService().syncEntity(gids, SyncEntityService.TYPE_GAME, null, new SyncCallBack() {

					@Override
					public void onSuccess(Object result) {
						asyncTask.getProxyCallBack().onSuccess(gameDao.searchGamesByIds(tempIds));
					}

					@Override
					public void onFailure(Integer result) {
						asyncTask.getProxyCallBack().onSuccess(gameDao.searchGamesByIds(tempIds));
					}
				});
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#getRelGameInfo(com.iwgame
	 * .msgs.common.ProxyCallBack, long)
	 */
	@Override
	public void getRelGameInfo(final ProxyCallBack<RelationGameVo> callback, final Context context, final long gid) {
		final MyAsyncTask<RelationGameVo> asyncTask = new MyAsyncTask<RelationGameVo>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final RelationGameDao relGameDao = DaoFactory.getDaoFactory().getRelationGameDao(SystemContext.getInstance().getContext());
				ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GAME, new SyncCallBack() {

					@Override
					public void onSuccess(Object result) {
						asyncTask.getProxyCallBack().onSuccess(relGameDao.getRelationGameByGameId(gid));
					}

					@Override
					public void onFailure(Integer result) {
						final RelationGameDao relGameDao = DaoFactory.getDaoFactory().getRelationGameDao(SystemContext.getInstance().getContext());
						asyncTask.getProxyCallBack().onSuccess(relGameDao.getRelationGameByGameId(gid));
					}
				});
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#getRelGameInfo(com.iwgame
	 * .msgs.common.ProxyCallBack, long)
	 */
	@Override
	public void getRelGameInfoForLocal(final ProxyCallBack<RelationGameVo> callback, final Context context, final long gid) {
		final RelationGameDao relGameDao = DaoFactory.getDaoFactory().getRelationGameDao(SystemContext.getInstance().getContext());
		callback.onSuccess(relGameDao.getRelationGameByGameId(gid));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.game.logic.GameProxy#getGameLikeInfo(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void getGameLikeInfo(final ProxyCallBack<GameExtDataObj> callback, final Context context, final long gid) {
		final MyAsyncTask<GameExtDataObj> asyncTask = new MyAsyncTask<GameExtDataObj>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				contentService.getExtContent(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.contentExtDataResult)) {
							ContentExtDataResult contentExtResult = result.getExtension(Msgs.contentExtDataResult);
							if (contentExtResult != null && contentExtResult.getContentExtDataListList() != null
									&& contentExtResult.getContentExtDataListList().size() > 0) {
								ContentExtData data = contentExtResult.getContentExtDataListList().get(0);
								GameExtDataObj vo = new GameExtDataObj();
								vo.setCritivize(data.getCriticize());
								vo.setPraise(data.getPraise());
								vo.setIspraise(data.getIspraise());
								vo.setIscritivize(data.getIscriticize());
								asyncTask.getProxyCallBack().onSuccess(vo);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, String.valueOf(gid), 1);
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#searchGame(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void searchGame(final ProxyCallBack<List<ExtGameVo>> callback, Context context, final String keyword) {
		final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
		callback.onSuccess(gameDao.searchGameByKeyword(keyword));
	}

	@Override
	public void searchGameFollowCount(final ProxyCallBack<HashMap<Long, Long>> callback, final Context context, final String ids) {
		final MyAsyncTask<HashMap<Long, Long>> asyncTask = new MyAsyncTask<HashMap<Long, Long>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				postbarService.getGameFollowCount(new ServiceCallBack<XAction.XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.gameFollowResult)) {
							GameFollowResult followResult = result.getExtension(Msgs.gameFollowResult);
							List<GameFollowData> list = followResult.getGameFollowDataListList();
							HashMap<Long, Long> countMaps = new HashMap<Long, Long>();
							for (int i = 0; i < list.size(); i++) {
								GameFollowData followData = list.get(i);
								long gameId = followData.getGid();
								long count = followData.getFollowCount();
								countMaps.put(gameId, count);
							}
							if (countMaps.size() != 0) {
								asyncTask.getProxyCallBack().onSuccess(countMaps);
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, ids);
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#recommendGame(com.iwgame.
	 * msgs.common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void getConditionGame(final ProxyCallBack<PagerVo<ExtGameVo>> callback, final Context context, final Boolean isrecommend,
			final Integer rel, final Long uid, final String resulttype, final Long gid, final long offset, final int limit, final Integer uidtype,
			final Integer near, final Integer gtype, final Boolean isfind, String platform, Integer source) {
		final String position = SystemContext.getInstance().getLocation();
		searchGames(callback, context, isrecommend, rel, uid, resulttype, gid, offset, limit, uidtype, near, position, gtype, isfind, platform,
				source);
	}

	private void seachGameFromCache(final ProxyCallBack<PagerVo<ExtGameVo>> callback, final Context context, final Boolean isrecommend,
			final Integer rel, final Long uid, final String resulttype, final Long gid, final long offset, final int limit, final Integer uidtype,
			final Integer near, final Integer gtype, final Boolean isfind, final String position, final int cacheType, final String platform,
			final Integer source) {
		final MyAsyncTask<PagerVo<ExtGameVo>> asyncTask = new MyAsyncTask<PagerVo<ExtGameVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ProxyFactory.getInstance().getCache().getData(cacheType, new CacheCallBack() {

					@Override
					public void onBack(Object result) {
						if (result != null) {
							asyncTask.getProxyCallBack().onSuccess((PagerVo<ExtGameVo>) result);
						}
						searchGames(callback, context, isrecommend, rel, uid, resulttype, gid, offset, limit, uidtype, near, position, gtype, isfind,
								platform, source);
					}
				});
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 
	 * @param callback
	 * @param context
	 * @param isrecommend
	 * @param rel
	 * @param uid
	 * @param resulttype
	 * @param gid
	 * @param pageNo
	 * @param pageSize
	 * @param uidtype
	 * @param isnear
	 * @param position
	 */
	private void searchGames(final ProxyCallBack<PagerVo<ExtGameVo>> callback, final Context context, final Boolean isrecommend, final Integer rel,
			final Long uid, final String resulttype, final Long gid, final long offset, final int limit, final Integer uidtype, final Integer near,
			final String position, final Integer gtype, final Boolean isfind, final String platform, final Integer source) {
		final MyAsyncTask<PagerVo<ExtGameVo>> asyncTask = new MyAsyncTask<PagerVo<ExtGameVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final GameDao gdao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
				searchService.searchGames(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.gameQueryResult)) {
							GameQueryResult gresult = result.getExtension(Msgs.gameQueryResult);
							List<GameQueryEntry> list = gresult.getEntryList();
							PagerVo<ExtGameVo> pagerVo = new PagerVo<ExtGameVo>();
							List<ExtGameVo> items = new ArrayList<ExtGameVo>();
							for (int i = 0; i < list.size(); i++) {
								GameQueryEntry detail = list.get(i);
								ExtGameVo vo = new ExtGameVo();
								GameVo gvo = gdao.getGameByGameId(detail.getGid());
								if (gvo != null) {
									if (gvo.getGamelogo() != null)
										vo.setGamelogo(gvo.getGamelogo());
									if (gvo.getGamename() != null)
										vo.setGamename(gvo.getGamename());
								}
								vo.setVisitCount(detail.getVisit());
								vo.setGameid(detail.getGid());
								if (detail.hasDistance())
									vo.setDistance(detail.getDistance());
								vo.setFollowCount(detail.getFcount());
								vo.setNearFollowCount(detail.getNearcount());
								vo.setPostCount(detail.getPost());
								vo.setUserCount(detail.getFcount());
								if (detail.getRel() == 1) {
									vo.setFollow(true);
								} else {
									vo.setFollow(false);
								}
								items.add(vo);
							}
							pagerVo.setOffset(gresult.getOffset());
							pagerVo.setItems(items);
							asyncTask.getProxyCallBack().onSuccess(pagerVo);

							if (isrecommend != null && isrecommend) {
								if (offset == 0 || offset >= Integer.MAX_VALUE)
									saveGameToCache(pagerVo, Cache.DATA_TYPE_GAME_RECOMMEND, true);
								else
									saveGameToCache(pagerVo, Cache.DATA_TYPE_GAME_RECOMMEND, false);
							}
							if (near != null && near > 0) {
								if (offset == 0 || offset >= Integer.MAX_VALUE)
									saveGameToCache(pagerVo, Cache.DATA_TYPE_GAME_NEAR, true);
								else
									saveGameToCache(pagerVo, Cache.DATA_TYPE_GAME_NEAR, false);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, null, isrecommend, rel, uid, resulttype, position, gid, offset, limit, uidtype, near, gtype, isfind, platform, source);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void saveGameToCache(final PagerVo<ExtGameVo> newVo, final int cacheType, final boolean isclean) {
		new MyAsyncTask().execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				if (isclean) {
					ProxyFactory.getInstance().getCache().saveData(cacheType, newVo);
				} else {
					ProxyFactory.getInstance().getCache().getData(cacheType, new CacheCallBack() {

						@Override
						public void onBack(Object res) {
							if (res != null) {
								PagerVo<ExtGameVo> cacheVo = (PagerVo<ExtGameVo>) res;
								cacheVo.getItems().addAll(newVo.getItems());
								cacheVo.setOffset(newVo.getOffset());
								ProxyFactory.getInstance().getCache().saveData(cacheType, cacheVo);
							} else {
								ProxyFactory.getInstance().getCache().saveData(cacheType, newVo);
							}
						}
					});
				}
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#searchGamePackage(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void searchGamePackage(final ProxyCallBack<List<GamePackageVo>> callback, final Context context, final long gid) {
		final MyAsyncTask<List<GamePackageVo>> asyncTask = new MyAsyncTask<List<GamePackageVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_GAME_PACKAGE, gid, new SyncCallBack() {

					@Override
					public void onSuccess(Object result) {
						getGamePackage(callback, context, gid);
					}

					@Override
					public void onFailure(Integer result) {
						getGamePackage(callback, context, gid);
					}
				});
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	private void getGamePackage(final ProxyCallBack<List<GamePackageVo>> callback, Context context, final long gid) {
		final MyAsyncTask<List<GamePackageVo>> asyncTask = new MyAsyncTask<List<GamePackageVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final GamePackageDao gamePackageDao = DaoFactory.getDaoFactory().getGamePackageDao(SystemContext.getInstance().getContext());
				asyncTask.getProxyCallBack().onSuccess(gamePackageDao.getGamePackageListByGameId(gid));
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#searchGameTop(com.iwgame.
	 * msgs.common.ProxyCallBack, android.content.Context, int)
	 */
	@Override
	public void searchGameTop(final ProxyCallBack<List<GameTopItemObj>> callback, final Context context, final int type) {
		final MyAsyncTask<List<GameTopItemObj>> asyncTask = new MyAsyncTask<List<GameTopItemObj>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ProxyFactory.getInstance().getCache().getData(Cache.DATA_TYPE_GAME_TOP, new CacheCallBack() {

					@Override
					public void onBack(Object result) {
						if (result != null) {
							asyncTask.getProxyCallBack().onSuccess((List<GameTopItemObj>) result);
							getGameTop(callback, context, type);
						} else {
							getGameTop(callback, context, type);
						}
					}
				});
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	private void getGameTop(final ProxyCallBack<List<GameTopItemObj>> callback, final Context context, final int type) {
		final MyAsyncTask<List<GameTopItemObj>> asyncTask = new MyAsyncTask<List<GameTopItemObj>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final RelationGameDao relGameDao = DaoFactory.getDaoFactory().getRelationGameDao(SystemContext.getInstance().getContext());
				// searchService.searchTop(new ServiceCallBack<XActionResult>()
				// {
				//
				// @Override
				// public void onSuccess(XActionResult result) {
				// List<GameTopItemObj> list = new ArrayList<GameTopItemObj>();
				// if (result.hasExtension(Msgs.topQueryResult)) {
				// TopQueryResult tresult =
				// result.getExtension(Msgs.topQueryResult);
				// if (tresult != null && tresult.getEntrysList() != null) {
				// List<TopQueryEntry> tlist = tresult.getEntrysList();
				// for (int i = 0; i < tlist.size(); i++) {
				// TopQueryEntry te = tlist.get(i);
				// GameTopItemObj vo = new GameTopItemObj();
				// vo.setGid(te.getGid());
				// vo.setCriticize(te.getCriticize());
				// vo.setPraise(te.getPraise());
				// vo.setHotTopic(te.getHotTopic());
				// vo.setTopicCount(te.getTopicCount());
				// vo.setDailyTopicCount(te.getDailyTopicCount());
				// vo.setFollowCount(te.getFollowCount());
				// vo.setVisitCount(te.getPostbarVisits());
				// RelationGameVo rgVo =
				// relGameDao.getRelationGameByGameId(te.getGid());
				// if (rgVo != null) {
				// vo.setFollow(rgVo.getRelation() == 1 ? true : false);
				// } else {
				// vo.setFollow(false);
				// }
				// list.add(vo);
				// }
				// }
				// }
				// asyncTask.getProxyCallBack().onSuccess(list);
				// ProxyFactory.getInstance().getCache().saveData(Cache.DATA_TYPE_GAME_TOP,
				// list);
				// }
				//
				// @Override
				// public void onFailure(Integer result, String resultMsg) {
				// asyncTask.getProxyCallBack().onFailure(result, null);
				// }
				// }, context, type);
				asyncTask.getProxyCallBack().onSuccess(null);
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#searchAllGame(com.iwgame.
	 * msgs.common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void searchAllGame(ProxyCallBack<List<GameVo>> callback, Context context) {
		final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
		callback.onSuccess(gameDao.searchAllGames());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.game.logic.GameProxy#searchRecommendGame(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, int, int)
	 */
	@Override
	public void searchRecommendGames(final ProxyCallBack<PagerVo<ExtGameVo>> callback, final Context context, final long offset, final int limit) {
		final MyAsyncTask<PagerVo<ExtGameVo>> asyncTask = new MyAsyncTask<PagerVo<ExtGameVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				searchService.searchRecommendGames(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.gameRecommendResult)) {
							GameRecommendResult gr = result.getExtension(Msgs.gameRecommendResult);
							List<GameRecommendEntry> entry = gr.getEntryList();
							if (entry != null && entry.size() > 0) {
								PagerVo<ExtGameVo> pagetVo = new PagerVo<ExtGameVo>();
								List<ExtGameVo> items = new ArrayList<ExtGameVo>();
								pagetVo.setItems(items);
								pagetVo.setOffset(entry.get(entry.size() - 1).getId());
								for (int i = 0; i < entry.size(); i++) {
									GameRecommendEntry e = entry.get(i);
									ExtGameVo vo = new ExtGameVo();
									vo.setSortId(e.getId());
									vo.setGameid(e.getGid());
									vo.setGamename(e.getGamename());
									vo.setGamelogo(e.getIcon());
									vo.setPublisher(e.getPublisher());
									vo.setType(e.getCategory());
									items.add(vo);
								}
								asyncTask.getProxyCallBack().onSuccess(pagetVo);
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#getGamePostbarCount(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long, int)
	 */
	@Override
	public void getGamePostbarMaxIndex(final ProxyCallBack<List<ExtGameVo>> callback, final Context context, final String gids) {
		final MyAsyncTask<List<ExtGameVo>> asyncTask = new MyAsyncTask<List<ExtGameVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				postbarService.getGamePostbarMaxIndex(new ServiceCallBack<XAction.XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.postbarMaxIndexResult)) {

							PostbarMaxIndexResult ptr = result.getExtension(Msgs.postbarMaxIndexResult);
							List<PostbarMaxIndex> list = ptr.getIndexsList();
							ArrayList<ExtGameVo> gvoList = new ArrayList<ExtGameVo>();
							for (int i = 0; i < list.size(); i++) {
								ExtGameVo gvo = new ExtGameVo();
								gvo.setPostbarMaxIndex(list.get(i).getMaxIndex());
								gvo.setGameid(list.get(i).getGid());
								gvoList.add(gvo);
							}
							if (gvoList.size() != 0) {
								asyncTask.getProxyCallBack().onSuccess(gvoList);
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, gids);
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#getGameExtData(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void getGameExtData(final ProxyCallBack<GameExtDataVo> callback, final Context context, final long gid, final int type) {
		final MyAsyncTask<GameExtDataVo> asyncTask = new MyAsyncTask<GameExtDataVo>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				contentService.getExtContent(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.contentExtDataResult)) {
							ContentExtDataResult contentExtResult = result.getExtension(Msgs.contentExtDataResult);
							if (contentExtResult != null && contentExtResult.getContentExtDataListList() != null
									&& contentExtResult.getContentExtDataListList().size() > 0) {
								ContentExtData data = contentExtResult.getContentExtDataListList().get(0);
								GameExtDataVo vo = BuildVoUtil.ContentExtData2GameExtDataVo(data);
								asyncTask.getProxyCallBack().onSuccess(vo);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, String.valueOf(gid), type);
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#getGameFriendFollowers(com
	 * .iwgame.msgs.common.ProxyCallBack, android.content.Context,
	 * java.lang.String)
	 */
	@Override
	public void getGameFriendFollowers(final ProxyCallBack<Map<Long, ExtGameVo>> callback, final Context context, final String gids) {
		final MyAsyncTask<Map<Long, ExtGameVo>> asyncTask = new MyAsyncTask<Map<Long, ExtGameVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				searchService.searchGameFriendFollowers(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.gameFriendCountResult)) {
							GameFriendCountResult gfcResult = result.getExtension(Msgs.gameFriendCountResult);
							if (gfcResult != null) {
								List<GameFriendCountEntry> list = gfcResult.getEntryList();
								Map<Long, ExtGameVo> gvlist = new HashMap<Long, ExtGameVo>();
								int size = list.size();
								for (int i = 0; i < size; i++) {
									GameFriendCountEntry entry = list.get(i);
									ExtGameVo vo = new ExtGameVo();
									vo.setGameid(entry.getGid());
									vo.setFollowCount(entry.getCount());
									gvlist.put(entry.getGid(), vo);
								}
								asyncTask.getProxyCallBack().onSuccess(gvlist);
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, gids);
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#getGameService(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, int)
	 */
	@Override
	public void getGameService(ProxyCallBack<Msgs.UserGameServerResult> callback, final Context context) {
		final MyAsyncTask<Msgs.UserGameServerResult> asyncTask = new MyAsyncTask<Msgs.UserGameServerResult>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				searchService.searchGameService(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE
								&& result.hasExtension(Msgs.userGameServerResult)) {
							asyncTask.getProxyCallBack().onSuccess(result.getExtension(Msgs.userGameServerResult));
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context);
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#searchGameBykeyword(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void searchGameBykeyword(ProxyCallBack<List<ExtGameVo>> callback, final Context context, final String keyword, final long offset, final int limit){
		final MyAsyncTask<List<ExtGameVo>> asyncTask = new MyAsyncTask<List<ExtGameVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getSearchRemoteService().searchGameBykeyword(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						List<ExtGameVo> gamelist = new ArrayList<ExtGameVo>();
						if (result != null && result.getRc() == com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE
								&& result.hasExtension(Msgs.gameSearchList)) {
							List<GameSearchDetail> games = result.getExtension(Msgs.gameSearchList).getGameSearchDetailList();
							if (games.size() > 0) {
								for (int i = 0; i < games.size(); i++) {
									GameSearchDetail detail = games.get(i);
									ExtGameVo gameVo = new ExtGameVo();
									gameVo.setGameid(detail.getGid());
									gameVo.setGamename(detail.getGamename());
									gameVo.setGamelogo(detail.getIcon());
									gameVo.setType(detail.getCategory());
									gameVo.setPublisher(detail.getPublisher());
									gameVo.setFollowCount(detail.getFollowNum());
									gameVo.setFollow(detail.getIsFollow() == 1 ? true : false);
									gamelist.add(gameVo);
								}
							}
						}
						asyncTask.getProxyCallBack().onSuccess(gamelist);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, keyword, offset, limit);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

}
