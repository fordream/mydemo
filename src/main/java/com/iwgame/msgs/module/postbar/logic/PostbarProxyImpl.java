/**      
 * PostbarProxyImpl.java Create on 2013-12-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ApplyMasterResult;
import com.iwgame.msgs.proto.Msgs.ContentList;
import com.iwgame.msgs.proto.Msgs.LimitedOPCountResult.LimitedOPCount;
import com.iwgame.msgs.proto.Msgs.PostbarActionResult;
import com.iwgame.msgs.proto.Msgs.PostbarTagResult;
import com.iwgame.msgs.proto.Msgs.PostbarTopicDetail;
import com.iwgame.msgs.proto.Msgs.PostbarTopicListResult;
import com.iwgame.msgs.proto.Msgs.PostbarTopicReplyDetail;
import com.iwgame.msgs.proto.Msgs.PostbarTopicReplyListResult;
import com.iwgame.msgs.utils.BuildVoUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.TopicTagVo;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: PostbarProxyImpl
 * @Description: 贴吧proxy实现
 * @author chuanglong
 * @date 2013-12-24 下午6:30:47
 * @Version 1.0
 * 
 */
public class PostbarProxyImpl implements PostbarProxy {

	protected static final String TAG = "PostbarProxyImpl";

	private static byte[] lock = new byte[0];

	private static PostbarProxyImpl instance = null;

	private PostbarProxyImpl() {

	}

	public static PostbarProxyImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new PostbarProxyImpl();
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#getTopicList(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long,
	 * java.lang.String, int, long, long, int)
	 */
	@Override
	public void getTopicList(final ProxyCallBack<List<PostbarTopicDetail>> callback, final Context context, final long gid, final String title,
			final int order, final int tagid, final int filter, final long uid, final long offset, final int limit, final String tagName) {
		final MyAsyncTask<List<PostbarTopicDetail>> asyncTask = new MyAsyncTask<List<PostbarTopicDetail>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceCallBack<XActionResult> serviceCallBack = new ServiceCallBack<XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							List<PostbarTopicDetail> list = new ArrayList<Msgs.PostbarTopicDetail>();
							if (result.hasExtension(Msgs.postbarTopicListResult) && result.getExtension(Msgs.postbarTopicListResult) != null) {
								list.addAll(result.getExtension(Msgs.postbarTopicListResult).getEntryList());
							}
							asyncTask.getProxyCallBack().onSuccess(list);
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};
				ServiceFactory.getInstance().getPostbarService()
						.getTopicList(serviceCallBack, context, gid, title, order, tagid, filter, uid, offset, limit, tagName);
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#getTopicList(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long,
	 * java.lang.String, int, long, long, int)
	 */
	@Override
	public void getTopicListMap(final ProxyCallBack<Map<String, Object>> callback, final Context context, final long gid, final String title,
			final int order, final int tagid, final int filter, final long uid, final long offset, final int limit, final String tagName) {
		final MyAsyncTask<Map<String, Object>> asyncTask = new MyAsyncTask<Map<String, Object>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceCallBack<XActionResult> serviceCallBack = new ServiceCallBack<XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							Map<String, Object> map = new HashMap<String, Object>();
							List<PostbarTopicDetail> list = new ArrayList<Msgs.PostbarTopicDetail>();
							if (result.hasExtension(Msgs.postbarTopicListResult) && result.getExtension(Msgs.postbarTopicListResult) != null) {
								list.addAll(result.getExtension(Msgs.postbarTopicListResult).getEntryList());
							}
							map.put("tagid", tagid);
							map.put("filter", filter);
							map.put("list", list);
							asyncTask.getProxyCallBack().onSuccess(map);
						} else {
							int tag = 0;
							if (tagid != SystemConfig.POSTBAR_TOPIC_TAG_ALL) {// 如果标签不为0，则说明当前标签不为全部和精华
								tag = tagid;
							} else {// 全部或精华
								if (filter == MsgsConstants.POSTBAR_TOPIC_FILTER_ESSENCE) {// 加精（精华）
									tag = -1;
								} else {// 全部
									tag = 0;
								}
							}
							asyncTask.getProxyCallBack().onFailure(result.getRc(), tag + "");
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						int tag = 0;
						if (tagid != SystemConfig.POSTBAR_TOPIC_TAG_ALL) {// 如果标签不为0，则说明当前标签不为全部和精华
							tag = tagid;
						} else {// 全部或精华
							if (filter == MsgsConstants.POSTBAR_TOPIC_FILTER_ESSENCE) {// 加精（精华）
								tag = -1;
							} else {// 全部
								tag = 0;
							}
						}
						asyncTask.getProxyCallBack().onFailure(result, tag + "");
					}
				};
				ServiceFactory.getInstance().getPostbarService()
						.getTopicList(serviceCallBack, context, gid, title, order, tagid, filter, uid, offset, limit, tagName);
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#getFavoriteTopicList
	 * (com.iwgame.msgs.common.ProxyCallBack, android.content.Context, long,
	 * int)
	 */
	@Override
	public void getFavoriteTopicList(final ProxyCallBack<List<PostbarTopicDetail>> callback, final Context context, final long uid,
			final long offset, final int limit) {
		final MyAsyncTask<List<PostbarTopicDetail>> asyncTask = new MyAsyncTask<List<PostbarTopicDetail>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceCallBack<XActionResult> serviceCallBack = new ServiceCallBack<XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						// TODO Auto-generated method stub
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							List<PostbarTopicDetail> list = new ArrayList<Msgs.PostbarTopicDetail>();
							if (result.hasExtension(Msgs.postbarTopicListResult) && result.getExtension(Msgs.postbarTopicListResult) != null) {
								list.addAll(result.getExtension(Msgs.postbarTopicListResult).getEntryList());
							}
							asyncTask.getProxyCallBack().onSuccess(list);
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};
				ServiceFactory.getInstance().getPostbarService().getFavoriteTopicList(serviceCallBack, context, uid, offset, limit);
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#getTopicDetail(com.
	 * iwgame.msgs.common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void getTopicDetail(final ProxyCallBack<PostbarTopicDetail> callback, final Context context, final long topicId) {
		final MyAsyncTask<PostbarTopicDetail> asyncTask = new MyAsyncTask<PostbarTopicDetail>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				// 加载测试数据
				ServiceCallBack<XActionResult> serviceCallBack = new ServiceCallBack<XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						// TODO Auto-generated method stub
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							PostbarTopicDetail detail = null;
							List<PostbarTopicDetail> list = new ArrayList<Msgs.PostbarTopicDetail>();
							if (result.hasExtension(Msgs.postbarTopicDetail) && result.getExtension(Msgs.postbarTopicDetail) != null) {
								detail = result.getExtension(Msgs.postbarTopicDetail);
							}
							asyncTask.getProxyCallBack().onSuccess(detail);
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};
				ServiceFactory.getInstance().getPostbarService().getTopicDetail(serviceCallBack, context, topicId);
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#actionTopic(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long, int)
	 */
	@Override
	public void actionTopic(final ProxyCallBack<Integer> callback, final Context context, final long topicId, final int op, final String actionReason) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				String pos = SystemContext.getInstance().getLocation();
				ServiceCallBack<XActionResult> serviceCallback = new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};
				ServiceFactory.getInstance().getPostbarService()
						.userAction(serviceCallback, context, topicId, MsgsConstants.OT_TOPIC, op, actionReason, 0, null, 0, pos);
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#publishTopic(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long,
	 * java.lang.String, java.lang.String, byte[])
	 */
	@Override
	public void publishTopic(final ProxyCallBack<Integer> callback, final Context context, final long gid, final String title, final String content,
			final byte[] resource, final int tagid) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				String pos = SystemContext.getInstance().getLocation();
				ServiceCallBack<XActionResult> serviceCallback = new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};

				try {
					JSONObject json = new JSONObject();
					json.put("title", title);
					json.put("content", content);
					json.put("tagId", tagid);
					ServiceFactory
							.getInstance()
							.getPostbarService()
							.userAction(serviceCallback, context, gid, MsgsConstants.OT_GAME, MsgsConstants.OP_POST_TOPIC, json.toString(), 0,
									resource, MsgsConstants.RESOURCE_TYPE_IMAGE, pos);
				} catch (JSONException e) {
					e.printStackTrace();
					asyncTask.getProxyCallBack().onFailure(-100010, "发送帖子时，构造json对象异常，请重试");
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#publishReply(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long, int,
	 * java.lang.String)
	 */
	@Override
	public void publishReply(final ProxyCallBack<Integer> callback, final Context context, final long targetId, final int targetType,
			final String content, final byte[] resource) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				String pos = SystemContext.getInstance().getLocation();
				ServiceCallBack<XActionResult> serviceCallback = new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};
				ServiceFactory
						.getInstance()
						.getPostbarService()
						.userAction(serviceCallback, context, targetId, targetType, MsgsConstants.OP_REPLY_TOPIC, content, 0, resource,
								MsgsConstants.RESOURCE_TYPE_IMAGE, pos);
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#getTopicReplyList(com
	 * .iwgame.msgs.common.ProxyCallBack, android.content.Context, long, int,
	 * long, int)
	 */
	@Override
	public void getTopicReplyList(final ProxyCallBack<PostbarTopicReplyListResult> callback, final Context context, final long tid, final int ttype,
			final int filter, final int offsettype, final long offset, final int limit) {
		final MyAsyncTask<PostbarTopicReplyListResult> asyncTask = new MyAsyncTask<PostbarTopicReplyListResult>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceCallBack<XActionResult> serviceCallBack = new ServiceCallBack<XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							if (result.hasExtension(Msgs.postbarTopicReplyListResult)
									&& result.getExtension(Msgs.postbarTopicReplyListResult) != null) {
								asyncTask.getProxyCallBack().onSuccess(result.getExtension(Msgs.postbarTopicReplyListResult));
							}
							asyncTask.getProxyCallBack().onSuccess(null);

						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};
				ServiceFactory.getInstance().getPostbarService()
						.getTopicReplyList(serviceCallBack, context, tid, ttype, filter, offsettype, offset, limit);
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#getTopicReply(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void getTopicReplyDetail(final ProxyCallBack<PostbarTopicReplyDetail> callback, final Context context, final long replyid) {
		final MyAsyncTask<PostbarTopicReplyDetail> asyncTask = new MyAsyncTask<PostbarTopicReplyDetail>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceCallBack<XActionResult> serviceCallBack = new ServiceCallBack<XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							PostbarTopicReplyDetail detail = null;
							if (result.hasExtension(Msgs.postbarTopicReplyDetail) && result.getExtension(Msgs.postbarTopicReplyDetail) != null) {
								detail = result.getExtension(Msgs.postbarTopicReplyDetail);
							}
							asyncTask.getProxyCallBack().onSuccess(detail);
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};
				ServiceFactory.getInstance().getPostbarService().getTopicReplyDetail(serviceCallBack, context, replyid);
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#actionTopicReply(com
	 * .iwgame.msgs.common.ProxyCallBack, android.content.Context, long, int)
	 */
	@Override
	public void actionTopicReply(final ProxyCallBack<Integer> callback, final Context context, final long replyid, final int op,
			final String actionReason) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				String pos = SystemContext.getInstance().getLocation();
				ServiceCallBack<XActionResult> serviceCallback = new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};
				ServiceFactory.getInstance().getPostbarService()
						.userAction(serviceCallback, context, replyid, MsgsConstants.OT_TOPICREPLY, op, actionReason, 0, null, 0, pos);
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#applyPostbarMaster(
	 * com.iwgame.msgs.common.ProxyCallBack, android.content.Context, long,
	 * java.lang.String, java.lang.String, java.lang.String, byte[])
	 */
	@Override
	public void applyPostbarMaster(final ProxyCallBack<Integer> callback, final Context context, final long gid, final String name,
			final String idcardNo, final String applyContent, final byte[] idcardImage) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceCallBack<XActionResult> serviceCallBack = new ServiceCallBack<XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						// TODO Auto-generated method stub
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							asyncTask.getProxyCallBack().onSuccess(Msgs.ErrorCode.EC_OK_VALUE);
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};
				ServiceFactory.getInstance().getPostbarService()
						.applyPostbarMaster(serviceCallBack, context, gid, name, idcardNo, applyContent, idcardImage);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	@Override
	public void getMasterApplyCount(final ProxyCallBack<Integer> callback, final Context context, final long gid) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceCallBack<XActionResult> serviceCallBack = new ServiceCallBack<XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							if (result.hasExtension(Msgs.applyMasterResult) && result.getExtension(Msgs.applyMasterResult) != null) {
								ApplyMasterResult applyMasterResult = result.getExtension(Msgs.applyMasterResult);
								asyncTask.getProxyCallBack().onSuccess(applyMasterResult.getIsApplied());
							} else {
								asyncTask.getProxyCallBack().onSuccess(-1);
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};
				ServiceFactory.getInstance().getPostbarService().getMasterApplyCount(serviceCallBack, context, gid);
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#getTopicTags(com.iwgame
	 * .msgs.common.ProxyCallBack, long)
	 */
	@Override
	public void getTopicTags(final ProxyCallBack<List<TopicTagVo>> callback, final long gid) {
		final MyAsyncTask<List<TopicTagVo>> asyncTask = new MyAsyncTask<List<TopicTagVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceCallBack<XActionResult> serviceCallBack = new ServiceCallBack<XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						// TODO Auto-generated method stub
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							List<TopicTagVo> ret = new ArrayList<TopicTagVo>();
							PostbarTagResult detail = null;
							if (result.hasExtension(Msgs.postbarTagResult) && result.getExtension(Msgs.postbarTagResult) != null) {
								detail = result.getExtension(Msgs.postbarTagResult);
								for (int i = 0; i < detail.getEntryCount(); i++) {
									ret.add(BuildVoUtil.PostbarTag2TTopicTagVo(detail.getEntry(i)));
								}
							}
							asyncTask.getProxyCallBack().onSuccess(ret);
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};
				ServiceFactory.getInstance().getPostbarService().getTopicTags(serviceCallBack, gid);
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#getLimitedOPCount(com
	 * .iwgame.msgs.common.ProxyCallBack, android.content.Context, int)
	 */
	@Override
	public void getLimitedOPCount(final ProxyCallBack<Map<Integer, Integer>> callback, final Context context, final int limitedop) {
		final MyAsyncTask<Map<Integer, Integer>> asyncTask = new MyAsyncTask<Map<Integer, Integer>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceCallBack<XActionResult> serviceCallBack = new ServiceCallBack<XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							Map<Integer, Integer> map = new HashMap<Integer, Integer>();
							if (result.hasExtension(Msgs.limitedOPCountResult) && result.getExtension(Msgs.limitedOPCountResult) != null) {
								List<LimitedOPCount> list = result.getExtension(Msgs.limitedOPCountResult).getOpCountList();
								for (int i = 0; i < list.size(); i++) {
									LimitedOPCount item = list.get(i);
									map.put(item.getLop(), item.getCount());
								}
							}
							asyncTask.getProxyCallBack().onSuccess(map);
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};
				ServiceFactory.getInstance().getPostbarService().getLimitedOPCount(serviceCallBack, context, limitedop);
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#likeOrUnLikeTopic(com
	 * .iwgame.msgs.common.ProxyCallBack, java.lang.Long, java.lang.Long,
	 * java.lang.Long, java.lang.Long, int, int)
	 */
	@Override
	public void likeOrUnLikeTopic(final ProxyCallBack<Integer> callback, final Context context, final Long tid, final Long topicId,
			final Long topicUid, final Long trUid, final int type, final int op) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getPostbarService().likeOrUnLikeTopic(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, tid, topicId, topicUid, trUid, type, op);
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
	 * com.iwgame.msgs.module.postbar.logic.PostbarProxy#publicTopic(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, java.lang.Long,
	 * java.lang.Long, int, int, int, com.iwgame.msgs.proto.Msgs.ContentList,
	 * java.lang.String)
	 */
	@Override
	public void publicTopic(final ProxyCallBack<PostbarActionResult> callback, final Context context, final Long tid, final Long gid, final int op,
			final int actiontype, final int topictype, final ContentList contentList, final String position, final int isSaveAlbum) {
		final MyAsyncTask<PostbarActionResult> asyncTask = new MyAsyncTask<PostbarActionResult>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getPostbarService().publicTopic(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.getRc() == Msgs.ErrorCode.EC_OK_VALUE && result.hasExtension(Msgs.postbarActionResult)) {
							asyncTask.getProxyCallBack().onSuccess(result.getExtension(Msgs.postbarActionResult));
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, tid, gid, op, actiontype, topictype, contentList, position, isSaveAlbum);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.postbar.logic.PostbarProxy#searchTopicNews(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long, int)
	 */
	@Override
	public void searchTopicNews(ProxyCallBack<PostbarTopicListResult> callback, final Context context, final long offset, final int limit) {
		final MyAsyncTask<PostbarTopicListResult> asyncTask = new MyAsyncTask<PostbarTopicListResult>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceCallBack<XActionResult> serviceCallBack = new ServiceCallBack<XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							if (result.hasExtension(Msgs.postbarTopicListResult)
									&& result.getExtension(Msgs.postbarTopicListResult) != null) {
								asyncTask.getProxyCallBack().onSuccess(result.getExtension(Msgs.postbarTopicListResult));
							}else{
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
				};
				ServiceFactory.getInstance().getSearchRemoteService().searchTopicNews(serviceCallBack, context, offset, limit);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

}
