/**      
 * UserRemoteServiceImpl.java Create on 2013-9-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.UploadContactsRequest;
import com.iwgame.msgs.proto.Msgs.UploadContactsRequest.ContactsEntry;
import com.iwgame.msgs.proto.Msgs.UploadWeiboFriendsRequest;
import com.iwgame.msgs.proto.Msgs.UploadWeiboFriendsRequest.UploadWeiboFriendEntry;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.utils.AppUtils;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction.XActionResult;
import com.iwgame.xaction.service.XActionConstants;

/**
 * @ClassName: UserRemoteServiceImpl
 * @Description: 用户服务接口实现
 * @author 王卫
 * @date 2013-9-24 下午06:53:40
 * @Version 1.0
 * 
 */
public class UserRemoteServiceImpl implements UserRemoteService {

	protected static final String TAG = "UserRemoteServiceImpl";

	private static byte[] lock = new byte[0];

	private static UserRemoteServiceImpl instance = null;

	private UserRemoteServiceImpl() {
	}

	public static UserRemoteServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new UserRemoteServiceImpl();
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
	 * com.iwgame.msgs.remote.UserRemoteService#registAccount(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, int, int, byte[])
	 */
	@Override
	public void registAccount(final ServiceCallBack<XActionResult> callback, Context context, String captcha, String accountName, String password,
			String nickname, int sex, Integer age, byte[] avatar, String origin) {
		if (callback == null)
			return;

		if (captcha != null && accountName != null && password != null && nickname != null && context != null) {

			Map<String, Object> params = new HashMap<String, Object>();

			params.put("captcha", captcha);

			params.put("accountName", accountName);

			params.put("password", password);

			params.put("nickname", nickname);

			params.put("sex", sex);

			params.put(SystemConfig.APPTYPE, SystemContext.APPTYPE);

			if (age != null)
				params.put("age", age);

			if (avatar != null)
				params.put("avatar", avatar);

			if (origin != null)
				params.put("origin", origin);

			// 新增版版本号
			params.put("version", AppUtils.getLocalAppVersionName(SystemContext.getInstance().getContext()));

			RemoteUtils.httpRequest(callback, context, params, MsgsConstants.CMD_USER_REGISTER);

		} else {
			callback.onFailure(ErrorCode.EC_CLIENT_REMOTE_MESSAGE, null);
		}
	}

	@Override
	public void bundPhone(ServiceCallBack<XActionResult> callback, Context context, String captcha, String accountName, String password,
			String nickname, int sex, byte[] avatar) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (captcha != null && accountName != null && password != null && context != null) {

			Map<String, Object> params = new HashMap<String, Object>();

			params.put("captcha", captcha);

			params.put("accountName", accountName);

			params.put("password", password);

			params.put("nickname", nickname);

			params.put("sex", sex);

			if (avatar != null)
				params.put("avatar", avatar);

			// 新增版版本号
			params.put("version", AppUtils.getLocalAppVersionName(SystemContext.getInstance().getContext()));

			RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_GUEST_BIND_CPN);

		} else {
			callback.onFailure(ErrorCode.EC_CLIENT_REMOTE_MESSAGE, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.UserRemoteService#registAccount(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, int, int, byte[])
	 */
	@Override
	public void registGuestAccount(final ServiceCallBack<XActionResult> callback, Context context, String origin) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();

		// 新增版版本号
		params.put("version", AppUtils.getLocalAppVersionName(SystemContext.getInstance().getContext()));
		if (origin != null)
			params.put("origin", origin);

		params.put(SystemConfig.APPTYPE, SystemContext.APPTYPE);

		RemoteUtils.httpRequest(callback, context, params, MsgsConstants.CMD_GUEST_REGISTER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.UserRemoteService#verifyAccount(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context)
	 */
	@Override
	public void verifyAccount(final ServiceCallBack<XActionResult> callback, Context context) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_VALIDATE_TOKEN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.UserRemoteService#updateUser(com.iwgame.msgs.common
	 * .ServiceCallBack, android.content.Context, java.lang.Integer,
	 * java.lang.String, java.lang.Integer, java.lang.String, byte[],
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void updateUser(final ServiceCallBack<XActionResult> callback, Context context, Integer sex, String mood, Integer age, String job,
			byte[] avatarData, String avatar, String city, String nickname, String desc, String gameTime, String likeGameType, String weiboUid,
			String weiboName) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();

		if (sex != null)
			params.put("sex", sex);

		if (mood != null && !mood.isEmpty())
			params.put("mood", mood);

		if (age != null)
			params.put("age", age);

		if (job != null && !job.isEmpty())
			params.put("job", job);

		if (avatarData != null)
			params.put("avatar_data", avatarData);

		if (avatar != null && !avatar.isEmpty())
			params.put("avatar", avatar);

		if (city != null && !city.isEmpty())
			params.put("city", city);

		if (nickname != null && !nickname.isEmpty())
			params.put("nickname", nickname);

		if (desc != null && !desc.isEmpty())
			params.put("desc", desc);

		if (gameTime != null && !gameTime.isEmpty())
			params.put("gameTime", gameTime);

		if (likeGameType != null && !likeGameType.isEmpty())
			params.put("gameType", likeGameType);

		if (weiboUid != null && !weiboUid.isEmpty())
			params.put("microblog", weiboUid);

		if (weiboName != null && !weiboName.isEmpty())
			params.put("microblogName", weiboName);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_UPDATE);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.UserRemoteService#updateRelUser(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context, java.lang.String, int)
	 */
	@Override
	public void updateRelUser(final ServiceCallBack<XActionResult> callback, Context context, String ids, int reltype) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("ids", ids);
		params.put("reltype", reltype);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_REL_UPDATE);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.UserRemoteService#syncUserRel(com.iwgame.msgs.
	 * common.ServiceCallBack, android.content.Context, long)
	 */
	@Override
	public void syncUserRel(final ServiceCallBack<XActionResult> callback, Context context, long utime, int type) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("utime", utime);
		params.put("type", type);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_REL_SYNC);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.UserRemoteService#getUserInfo(com.iwgame.msgs.
	 * common.ServiceCallBack, android.content.Context, long, long, int)
	 */
	@Override
	public void getUserInfo(final ServiceCallBack<XActionResult> callback, Context context, ContentDetailParams ids, int type, Long uid) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("type", type);
		if (uid != null)
			params.put("uid", uid);
		RemoteUtils.tcpRequest(callback, context, params, Msgs.ids, ids, MsgsConstants.CMD_CONTENT_DETAIL_NEW);

	}
	/**
	 * 获取用户的角色信息
	 */
	public void getUserRoleInfo(ServiceCallBack<XActionResult> callback,Context context,ContentDetailParams ids,int type,Long uid,Long gid,Long sid){
		if(callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
//		if (uid != null)
//			params.put("uid", uid);
		if(gid!=0){
			params.put("gid",gid);
		}else if(sid!=0){
			params.put("sid", sid);
		}
		RemoteUtils.tcpRequest(callback, context, params, Msgs.ids, ids, MsgsConstants.CMD_CONTENT_DETAIL_NEW);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#addAlbum(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context, byte[])
	 */
	@Override
	public void addAlbum(final ServiceCallBack<XActionResult> callback, Context context, byte[] image) {
		if (callback == null)
			return;
		if (image != null) {

			Map<String, Object> params = new HashMap<String, Object>();

			params.put("image", image);

			RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_ALBUM_ADD);
		} else {
			callback.onFailure(ErrorCode.EC_CLIENT_REMOTE_MESSAGE, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.UserRemoteService#getUserAlbum(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context, long, long)
	 */
	@Override
	public void getUserAlbum(final ServiceCallBack<XActionResult> callback, Context context, long id, long utime) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("id", id);

		params.put("utime", utime);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_ALBUM);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#delUserAblum(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void delUserAblum(ServiceCallBack<XActionResult> callback, Context context, String resourceId) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("resourceId", resourceId);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_ALBUM_DEL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.UserRemoteService#getUserAction(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context, long, int, int,
	 * java.lang.String, byte[], java.lang.String)
	 */
	@Override
	public void userAction(final ServiceCallBack<XActionResult> callback, Context context, long tid, int ttype, int op, String content, long seq,
			byte[] resource, int resourceType, String pos, String remark) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("tid", tid);

		params.put("ttype", ttype);

		params.put("op", op);

		if (content != null)
			params.put("content", content);
		if (seq != 0)
			params.put("seq", seq);

		if (resource != null) {
			params.put("resource", resource);
			params.put("resourceType", resourceType);
		}

		if (remark != null) {
			params.put("remark", remark);
		}
		if (pos != null)
			params.put("position", pos);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_ACTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.SearchRemoteService#searchRecommended(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, long,
	 * java.lang.String)
	 */
	@Override
	public void searchRecommended(final ServiceCallBack<XActionResult> callback, Context context, long gid, String ids) {
		if (callback == null)
			return;
		if (ids != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ids", ids);
			params.put("gid", gid);
			RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_IS_RECOMMEND);
		} else {
			callback.onFailure(ErrorCode.EC_CLIENT_REMOTE_MESSAGE, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#setPosition(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void setPosition(final ServiceCallBack<XActionResult> callback, Context context, String position) {
		if (callback == null)
			return;
		if (position != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("position", position);

			RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_SET_POSITION);

		} else {
			callback.onFailure(ErrorCode.EC_CLIENT_REMOTE_MESSAGE, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#creatGroup(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, java.lang.String,
	 * java.lang.String, java.lang.Long, java.lang.String, java.lang.Boolean,
	 * java.lang.Long)
	 */
	@Override
	public void creatOrUpdataGroup(ServiceCallBack<XActionResult> callback, Context context, String name, byte[] avatar, Long gid, String desc,
			String notice, Boolean isvalidate, Long grid) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		if (name != null)
			params.put("name", name);
		if (avatar != null)
			params.put("avatar", avatar);
		if (gid != null)
			params.put("gid", gid);
		if (desc != null)
			params.put("desc", desc);
		if (isvalidate != null)
			params.put("isvalidate", isvalidate);
		if (notice != null)
			params.put("notice", notice);
		if (grid != null)
			params.put("grid", grid);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_CREATE_GROUP);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#setMessageTip(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, java.lang.Long,
	 * int, int)
	 */
	@Override
	public void setMessageTip(ServiceCallBack<XActionResult> callback, Context context, Long tid, int type, int status) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		if (tid != null)
			params.put("id", tid);
		params.put("type", type);
		params.put("status", status);
		params.put(SystemConfig.APPTYPE, SystemContext.APPTYPE);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_MSG_ANNOUNCE_SET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#getMessageTip(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context)
	 */
	@Override
	public void getMessageTip(ServiceCallBack<XActionResult> callback, Context context) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(SystemConfig.APPTYPE, SystemContext.APPTYPE);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_CONTENT_SYN_MSG_SET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#verifiyContactUser(com
	 * .iwgame.msgs.common.ServiceCallBack, android.content.Context,
	 * java.lang.String, int)
	 */
	@Override
	public void verifiyContactUser(ServiceCallBack<XActionResult> callback, Context context, String condition, int type) {
		if (callback == null && condition != null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		if (condition != null)
			params.put("condition", condition);
		params.put("type", type);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_ISPHONE_EXIST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#getTrend(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void getTrend(ServiceCallBack<XActionResult> callback, Context context, String uids) {
		if (callback == null && uids != null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uids", uids);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_NEWS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#getLimitedOPCount(com
	 * .iwgame.msgs.common.ServiceCallBack, android.content.Context, int)
	 */
	@Override
	public void getLimitedOPCount(ServiceCallBack<XActionResult> callback, Context context, int limitedop) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("limitedop", limitedop);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_LIMITED_OP_COUNT);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#upContact(com.iwgame.
	 * msgs.common.ServiceCallBack, android.content.Context, java.util.List)
	 */
	@Override
	public void upContact(ServiceCallBack<XActionResult> callback, Context context, List<Map<String, String>> contacts) {
		if (callback == null || contacts == null || contacts.size() == 0)
			return;
		UploadContactsRequest.Builder obj = UploadContactsRequest.newBuilder();
		for (Map<String, String> map : contacts) {
			String name = map.get(Phone.DISPLAY_NAME);
			String num = map.get(Phone.NUMBER);
			if (name != null && num != null) {
				num = num.replaceAll("\\+86|\\D", "");
				ContactsEntry.Builder entry = ContactsEntry.newBuilder();
				entry.setName(name);
				entry.setPhoneNumber(num);
				ContactsEntry centry = entry.build();
				obj.addEntry(centry);
			}
		}
		UploadContactsRequest request = obj.build();
		RemoteUtils.tcpRequest(callback, context, null, Msgs.uploadContactsRequest, request, MsgsConstants.CMD_UPLOAD_CONTACTS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#upWeibo(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void upWeibo(ServiceCallBack<XActionResult> callback, Context context, String weiboInfo) {
		if (callback == null || weiboInfo == null)
			return;
		try {
			UploadWeiboFriendsRequest.Builder obj = UploadWeiboFriendsRequest.newBuilder();
			JSONObject ships = new JSONObject(weiboInfo);
			JSONArray users = ships.getJSONArray("users");
			for (int i = 0; i < users.length(); i++) {
				JSONObject user = users.getJSONObject(i);
				String weibo = String.valueOf(user.get("id"));
				String name = String.valueOf(user.get("name"));
				if (weibo != null && name != null) {
					UploadWeiboFriendEntry.Builder entryBuilder = UploadWeiboFriendEntry.newBuilder();
					entryBuilder.setName(name);
					entryBuilder.setUid(weibo);
					UploadWeiboFriendEntry entry = entryBuilder.build();
					obj.addEntry(entry);
				}
			}
			RemoteUtils.tcpRequest(callback, context, null, Msgs.uploadWeiboFriendsRequest, obj.build(), MsgsConstants.CMD_UPLOAD_WEIBO_FRIENDS);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#getPointTask(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, int)
	 */
	@Override
	public void getPointTask(ServiceCallBack<XActionResult> callback, Context context, int type, int code) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		params.put("code", code);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POINT_TASK_DETAIL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#userPointDetail(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, long, int)
	 */
	@Override
	public void getUserHistoryPointDetail(ServiceCallBack<XActionResult> callback, Context context, long offset, int limit) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_POINT_DETAILS);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#UserHistoryExperienceDetail(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, long, int)
	 */
	@Override
	public void getUserHistoryExperienceDetail(ServiceCallBack<XActionResult> callback,Context context,long offset,int limit){
		if(callback == null)
			return;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("offset",offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_EXP_DETAILS);
		//RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_POINT_DETAILS);
	}
	/**
	 * 获取用户的额外数据 比如关注数， 粉丝数，公会数，收藏数等等
	 */
	@Override
	public void getExtUserData(ServiceCallBack<XActionResult> callBack, Context context, String ids, int type) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", ids);
		params.put("type", type);
		RemoteUtils.tcpRequest(callBack, context, params, MsgsConstants.CMD_USER_CONTENT_EXT);
	}

	/**
	 * 用户行为动作之 公会禁言
	 */
	@Override
	public void userAction(Context context, String position, long tid, int ttype, int op, String content, String remark,
			ServiceCallBack<XActionResult> callBack) {
		if (callBack == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tid", tid);
		params.put("content", content);
		params.put("remark", remark);
		params.put("ttype", ttype);
		params.put("position", position);
		params.put("op", op);
		RemoteUtils.tcpRequest(callBack, context, params, MsgsConstants.CMD_USER_ACTION);
	}

	/**
	 * PUSH统计
	 */
	@Override
	public void SendPushCount(ServiceCallBack<XActionResult> callBack, Context context, long pushid, int action) {
		if (callBack == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pushid", pushid);
		params.put("action", action);
		RemoteUtils.tcpRequest(callBack, context, params, MsgsConstants.CMD_MSG_PUSH_STAT);
	}

	/**
	 * 获取积分商城顶部的tab
	 */
	@Override
	public void getPointTab(ServiceCallBack<XActionResult> callBack, Context context) {
		if (callBack == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		RemoteUtils.tcpRequest(callBack, context, params, MsgsConstants.CMD_MALL_GET_CATEGORY);
	}

	/**
	 * 获取商品的详细列表
	 */
	@Override
	public void getGoodsList(ServiceCallBack<XActionResult> callBack, Context context, long categoryId, String goodsStatus, int goodType, int offset,
			int limit) {
		if (callBack == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("categoryId", categoryId);
		params.put("goodsStatus", goodsStatus);
		params.put("type", goodType);
		params.put("offset", offset);
		params.put("limit", limit);
		params.put(SystemConfig.APPTYPE, SystemContext.APPTYPE);
		RemoteUtils.tcpRequest(callBack, context, params, MsgsConstants.CMD_MALL_GET_GOODS);
	}

	/**
	 * 获取兑换记录
	 */
	@Override
	public void getChangeRecords(ServiceCallBack<XActionResult> callBack, Context context, int offset, int limit) {
		if (callBack == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callBack, context, params, MsgsConstants.CMD_MALL_GET_TRANS_DETAILS);
	}

	/**
	 * 获取商品的详情
	 */
	@Override
	public void getGoodsDetail(ServiceCallBack<XActionResult> callBack, Context context, long goodsId) {
		if (callBack == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("goodsId", goodsId);
		RemoteUtils.tcpRequest(callBack, context, params, MsgsConstants.CMD_MALL_GET_SINGLE_GOODS);
	}

	/**
	 * 获取订单详情
	 */
	@Override
	public void getOrderDetail(ServiceCallBack<XActionResult> callBack, Context context, long transId, int deliveryType) {
		if (callBack == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("transId", transId);
		params.put("deliveryType", deliveryType);
		RemoteUtils.tcpRequest(callBack, context, params, MsgsConstants.CMD_MALL_GET_ORDER_DETAILS);
	}

	/**
	 * 兑换商品的接口
	 */
	@Override
	public void transGoods(ServiceCallBack<XActionResult> callBack, Context context, long goodsId, String transInfo) {
		if (callBack == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("goodsId", goodsId);
		params.put("transInfo", transInfo);
		RemoteUtils.tcpRequest(callBack, context, params, MsgsConstants.CMD_MALL_TRANS_GOODS);
	}

	/**
	 * 设置号码类型，0为Android传递
	 */
	@Override
	public void setPhoneType(ServiceCallBack<XActionResult> callBack, Context context) {
		if (callBack == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("phoneType", 0);
		RemoteUtils.tcpRequest(callBack, context, params, MsgsConstants.CMD_USER_PHONE_TYPE);
	}

	/**
	 * 获取创建公会的最大数目
	 */
	@Override
	public void getCreatGroupMax(ServiceCallBack<XActionResult> callBack, Context context, int limiteType) {
		if (callBack == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("limitedop", limiteType);
		RemoteUtils.tcpRequest(callBack, context, params, MsgsConstants.CMD_USER_LIMITED_OP_COUNT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#registAccount(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, java.lang.String,
	 * int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public void registAccount(final ServiceCallBack<XActionResult> callback, Context context, String openID, int target, int sex, String avatar,
			String nickname, String origin) {
		if (callback == null)
			return;

		if (openID != null && nickname != null && context != null) {

			Map<String, Object> params = new HashMap<String, Object>();

			params.put("openID", openID);

			params.put("target", target);

			params.put("nickname", nickname);

			params.put("sex", sex);

			params.put(SystemConfig.APPTYPE, SystemContext.APPTYPE);

			if (avatar != null)
				params.put("avatar", avatar);

			if (origin != null)
				params.put("origin", origin);

			// 新增版版本号
			params.put("version", AppUtils.getLocalAppVersionName(SystemContext.getInstance().getContext()));

			RemoteUtils.httpRequest(callback, context, params, MsgsConstants.CMD_THIRDPARTY_USER_REGISTER);

		} else {
			callback.onFailure(ErrorCode.EC_CLIENT_REMOTE_MESSAGE, null);
		}
	}

	/**
	 * 获取我页面的活动专区的title
	 */
	@Override
	public void getActivityDivisionTitle(ServiceCallBack<XActionResult> callBack, Context context) {
		if (callBack == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		RemoteUtils.tcpRequest(callBack, context, params, MsgsConstants.CMD_ACTIVITY_GET_TITLE);
	}

	/**
	 * 获取先列表的用户的数据
	 */
	@Override
	public void getSupportUserData(ServiceCallBack<XActionResult> callBack, Context context, long offset, int limit, long gameid) {
		if (callBack == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tid", gameid);
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callBack, context, params, MsgsConstants.CMD_PRAISE_TOPIC_LIST);
	}
	/**
	 * 添加游戏角色
	 */
	@Override
	public void addUserRole(ServiceCallBack<XActionResult> callback, Context context,long sid,
			String name, Integer grade, String rank) {
		// TODO Auto-generated method stub
		if(callback == null) return;
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("sid", sid);
		params.put("name", name);
		params.put("grade", grade);
		params.put("rank", rank);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_ROLE_ADD);
	}
	/**
	 * 同步用户角色
	 */
	@Override
	public void syncUserRole(ServiceCallBack<XActionResult> callback,
			Context context, long utime, int type) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("utime", utime);
		params.put("type", type);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_REL_SYNC);
	}
	/**
	 * 获取角色的属性值
	 */
	@Override
	public void getGameRoleValues(ServiceCallBack<XActionResult> callback,
			Context context, long gid) {
		if(callback ==null) return ;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("gid", gid);
		RemoteUtils.tcpRequest(callback, context, params, XActionConstants.XACTION_NEEDAUTH_COMMAND|1316);
	}
	/**
	 * 获取游戏列表
	 */
   @Override
   public void getGameList(ServiceCallBack<XActionResult> callback,
		Context context) {
	// TODO Auto-generated method stub
	if(callback == null) return ;
	LogUtil.d("gamelist","remote");
	Map<String, Object> params = new HashMap<String, Object>();
	
	 RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_SEARCH_GAMEROLE);
   }
   
   public void getGameServiceList(ServiceCallBack<XActionResult> callback, Context context,int syncType, Long utime){
	   if(callback == null) return;
	   Map<String,Object> params = new HashMap<String, Object>();
	   params.put("type",syncType);
	   params.put("utime", utime);
	   RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_REL_SYNC);
   }
   /**
    * 游戏反馈
    */
   @Override
public void getGameApplyInfo(ServiceCallBack<XActionResult> callback,
		Context context, long gid) {
	if(callback == null) return;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("gid", gid );
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_GAMEROLE_APPLYLOG);
   }
   /**
    * 删除用户角色
    */
   @Override
   public void deleteUserRoleData(ServiceCallBack<XActionResult> callback,
		Context context, Long roleId) {
	if(callback==null) return;
	Map<String,Object>params = new HashMap<String, Object>();
	params.put("roleid", roleId);
	RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_ROLE_DEL);
   }
   
   @Override
public void addUserRoleData(ServiceCallBack<XActionResult> callback,Context context,Long gid,Long sid,String rolename, Msgs.UserRoleData roledata){
   
	   if(callback == null)  return ;
	   
	   Map<String,Object> params = new HashMap<String, Object>();
	   params.put("sid", sid);
	   params.put("gid", gid);
	   params.put("name", rolename);
	   RemoteUtils.tcpRequest(callback, context, params,  Msgs.roleDataRequest, roledata, MsgsConstants.CMD_USER_ROLE_ADD);
}

/* (non-Javadoc)
 * @see com.iwgame.msgs.module.remote.UserRemoteService#getFilterRoleData(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long, long, long, long, int)
 */
@Override
public void getFilterRoleData(ServiceCallBack<XActionResult> callback,
		Context context, long gid, long sid, long roleid, long offset, int limit) {
	if(callback==null) return;
	Map<String,Object> params = new HashMap<String, Object>();
	if(gid!=0){
		params.put("gid", gid);
	}
	if(sid!=0){
		params.put("sid", sid);
	}
	if(roleid!=0){
		params.put("roleid",roleid);
	}
	params.put("offset", offset);
	params.put("limit", limit);
	RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_GAMEROLE);
	}
}
