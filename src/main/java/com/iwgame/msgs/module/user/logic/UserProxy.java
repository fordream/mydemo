/**      
 * ContractServiceProxy.java Create on 2013-8-22     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.logic;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.module.game.object.RecommendEntryObj;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.module.setting.vo.ChangeRecordsEntity;
import com.iwgame.msgs.module.setting.vo.Goods;
import com.iwgame.msgs.module.setting.vo.GoodsTab;
import com.iwgame.msgs.module.setting.vo.OrderDetail;
import com.iwgame.msgs.module.user.object.ContactObj;
import com.iwgame.msgs.module.user.object.DistanceItemObj;
import com.iwgame.msgs.module.user.object.UserNewsVo;
import com.iwgame.msgs.module.user.object.UserPointDetailObj;
import com.iwgame.msgs.module.user.object.UserPointTaskObj;
import com.iwgame.msgs.module.user.ui.ExtraGameVo;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.proto.Msgs.UploadContactsRequest;
import com.iwgame.msgs.proto.Msgs.UserRoleDetail;
import com.iwgame.msgs.vo.local.GameKeyVo;
import com.iwgame.msgs.vo.local.GameRoleServiceVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.PointTaskVo;
import com.iwgame.msgs.vo.local.ResourceVo;
import com.iwgame.msgs.vo.local.SupportUserVo;
import com.iwgame.msgs.vo.local.UserGradeVo;
import com.iwgame.msgs.vo.local.UserRoleVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.sdk.xaction.XActionCallback;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: UserProxy
 * @Description: 通讯录服务数据代理接口
 * @author 王卫
 * @date 2013-8-22 上午10:57:39
 * @Version 1.0
 * 
 */
public interface UserProxy {

	/**
	 * 获取用户列表
	 * 
	 * @param callback
	 * @param context
	 * @param type
	 *            (1:我和其他用户的关系（关注） 2：其他用户和我的关系（粉丝）)
	 * @param relation
	 *            (0:无关系 1：关注2黑名单)
	 * @param sort
	 *            排序(1:首字母 2：距离3：最后登录时间)
	 * @param pageNo
	 * @param size
	 * @param keyword
	 * @return
	 */
	public void getContactUsers(ProxyCallBack<List<UserVo>> callback, Context context, int type, int relation, int sort, int pageNo, int size,
			String keyword);

	/**
	 * 获取用户列表
	 * 
	 * @param callback
	 * @param context
	 * @param type
	 *            (1:我和其他用户的关系（关注） 2：其他用户和我的关系（粉丝）)
	 * @param relation
	 *            (0:无关系 1：关注2黑名单)
	 * @param sort
	 *            排序(1:首字母 2：距离3：最后登录时间)
	 * @param pageNo
	 * @param size
	 * @param keyword
	 * @return
	 */
	public void getFollowUsers(ProxyCallBack<List<UserItemObj>> callback, Context context, int type, int relation, int sort, int pageNo, int size,
			String keyword, long grid);
	
	
	
	/**
	 * 搜索用户
	 * 
	 * @param callback
	 * @param context
	 * @param gid
	 * @param isfate
	 * @param isrecommend
	 * @param rel
	 * @param resulttype
	 * @param uid
	 * @param offset
	 * @param limit
	 * @param nearDistance
	 * @param lastlogin
	 * @param sex
	 */
	public void searchUsers(ProxyCallBack<PagerVo<com.iwgame.msgs.module.account.object.UserObject>> callback, Context context, Long gid,
			Boolean isfate, Boolean isrecommend, Integer rel, String resulttype, String uid, long offset, int limit, Integer nearDistance,
			Integer lastlogin, Integer sex, Boolean isfind, Integer source);
	
	public void searchUsersBysid(ProxyCallBack<PagerVo<com.iwgame.msgs.module.account.object.UserObject>> callback, Context context, Long gid,
			Boolean isfate, Boolean isrecommend, Integer rel, String resulttype, String uid, long offset, int limit, Integer nearDistance,
			Integer lastlogin, Integer sex, Boolean isfind, String sid, Integer source);

	/**
	 * 搜索用户
	 * 
	 * @param callback
	 * @param context
	 * @param gid
	 * @param isfate
	 * @param isrecommend
	 * @param rel
	 * @param resulttype
	 * @param uid
	 * @param offset
	 * @param limit
	 * @param nearDistance
	 * @param lastlogin
	 * @param sex
	 */
	public void searchUsers(ProxyCallBack<PagerVo<com.iwgame.msgs.module.account.object.UserObject>> callback, Context context, Long gid,
			Boolean isfate, Boolean isrecommend, Integer rel, String resulttype, String uid, long offset, int limit, Integer nearDistance,
			Integer lastlogin, Integer sex, Boolean isfind,Integer source,String keywords);
	/**
	 * 搜索用户
	 * 
	 * @param callback
	 * @param context
	 * @param condition
	 * @param resulttype
	 * @param offset
	 * @param limit
	 * @param uid
	 */
	public void searchUser(ProxyCallBack<PagerVo<com.iwgame.msgs.module.account.object.UserObject>> callback, Context context, String condition,
			String resulttype, long offset, int limit, Long uid);

	/**
	 * 添加关注
	 * 
	 * @param callback
	 * @param context
	 * @param callback
	 * @param context
	 * @param uid
	 * @param isSync
	 */
	public void addFollowUser(ProxyCallBack<Integer> callback, Context context, long uid, boolean isSync);

	/**
	 * 取消关注
	 * 
	 * @param callback
	 * @param context
	 * @param id
	 * @return
	 */
	public void cannelFollowUser(ProxyCallBack<Integer> callback, Context context, long uid);

	/**
	 * 加入黑名单
	 * 
	 * @param callback
	 * @param context
	 * @param id
	 * @return
	 */
	public void addToBlackList(ProxyCallBack<Integer> callback, Context context, long uid);

	/**
	 * 获得用户的详细信息
	 * 
	 * @param callback
	 * @param context
	 * @param uid
	 * @return
	 */
	public void getUserDetailInfo(ProxyCallBack<List<UserVo>> callback, Context context, ContentDetailParams params, int type, Long uid);
	/**
	 * 获取用户的角色信息
	 * @param callback
	 * @param context
	 * @param type
	 * @param uid
	 * @return
	 */
	public void getUserRoleInfo(ProxyCallBack<List<UserRoleVo>> callback,Context context,ContentDetailParams params,int type,Long uid,Long gid,Long sid);
	
	/**
	 * 
	 * @param callback
	 * @param context
	 * @param uid
	 * @param utime
	 * @param fromnet
	 */
	public void getUserDetailInfo(ProxyCallBack<List<UserVo>> callback, Context context, ContentDetailParams params, int type, Long uid, boolean fromnet);
	
	/**
	 * 获取用户信息不从本地获取数据
	 * @param callback
	 * @param context
	 * @param uid
	 * @param utime
	 */
	public void getUserDetailInfoNoFromLocal(ProxyCallBack<List<UserVo>> callback, Context context, ContentDetailParams params, int type, Long uid);

	/**
	 * 用户行为动作
	 * 
	 * @param callback
	 * @param tid
	 * @param op
	 */
	public void userAction(ProxyCallBack<Integer> callback, Context context, long tid, int ttype, int op, String content, byte[] resource, long seq,String remark);
	
	
	
	/**
	 * 用户行为动作 之公会禁言
	 * @param callback
	 * @param context
	 * @param tid
	 * @param ttype
	 * @param op
	 * @param content
	 * @param remark
	 */
	public void userAction(Context context,String position, long tid, int ttype, int op, String content, String remark,ProxyCallBack<Integer> callback);

	/**
	 * 用户行为动作
	 * 
	 * @param callback
	 * @param tid
	 * @param op
	 */
	public void userAction(ProxyCallBack<Integer> callback, Context context, long tid, int ttype, int op, String content, byte[] resource,String remark);

	/**
	 * 添加个人图片
	 * 
	 * @param callback
	 * @param context
	 * @param image
	 */
	public void addAlbum(ProxyCallBack<ResourceVo> callback, Context context, byte[] image);

	/**
	 * 获取用户相册
	 * 
	 * @param callback
	 * @param context
	 * @param uid
	 * @param utime
	 */
	public void getUserAlbum(ProxyCallBack<List<ResourceVo>> callback, Context context, long uid, long utime);

	/**
	 * 删除用户相册图片
	 * 
	 * @param callback
	 * @param context
	 * @param resourceId
	 */
	public void delUserAlbum(ProxyCallBack<Integer> callback, Context context, String resourceId);

	/**
	 * 获取用户是否已向某人推荐某贴吧
	 * 
	 * @param callback
	 * @param context
	 * @param gid
	 * @param ids
	 */
	public void getRecommendedGameInfo(ProxyCallBack<Map<Long, Boolean>> callback, Context context, long gid, String ids);

	/**
	 * 设置位置
	 * 
	 * @param callback
	 * @param context
	 * @param postion
	 */
	public void setPosition(ProxyCallBack<Integer> callback, Context context, String position);

	/**
	 * 查询推荐的玩家
	 * 
	 * @param callback
	 * @param context
	 * @param grids
	 * @param offset
	 * @param limit
	 */
	public void searchRecommendUsers(ProxyCallBack<PagerVo<UserObject>> callback, Context context, String grids, long offset, int limit);

	/**
	 * 搜素吧主
	 * 
	 * @param callback
	 * @param context
	 * @param gid
	 * @param offset
	 * @param limit
	 */
	public void searchPostBarManager(ProxyCallBack<PagerVo<UserObject>> callback, Context context, long gid, long offset, int limit);

	/**
	 * 设置消息提醒
	 * 
	 * @param callback
	 * @param context
	 * @param tid
	 * @param type
	 * @param status
	 */
	public void setMessageTip(ProxyCallBack<Integer> callback, Context context, Long tid, int type, int status);

	/**
	 * 获取消息提醒配置
	 * 
	 * @param callback
	 * @param context
	 */
	public void getMessageTip(ProxyCallBack<XActionResult> callback, Context context);

	/**
	 * 获取通讯录好友
	 * 
	 * @param callback
	 * @param context
	 */
	public void getContact(ProxyCallBack<ContactObj> callback, Context context);

	/**
	 * 获取微博好友
	 * 
	 * @param callback
	 * @param context
	 */
	public void getWeiboFriends(ProxyCallBack<ContactObj> callback, Context context);

	/**
	 * 获取推荐好友
	 * 
	 * @param callback
	 * @param context
	 */
	public void getRecommondUser(ProxyCallBack<List<UserObject>> callback, Context context);

	/**
	 * 分享贴吧
	 * 
	 * @param callback
	 * @param context
	 * @param id
	 * @param type
	 */
	public void share(ProxyCallBack<Integer> callback, Context context, long id, int type, int target);

	/**
	 * 分享资料
	 * 
	 * @param callback
	 * @param context
	 * @param id
	 * @param type
	 */
	public void shareForShortUrl(ProxyCallBack<String> callback, Context context, long id, int type, int target ,int tagId);

	/**
	 * 分享攻略网页
	 * @param callback
	 * @param context
	 * @param id
	 * @param type
	 * @param target
	 */
	public void shareWebPageForShortUrl(ProxyCallBack<String> callback, Context context, long id, int type, int target);
	
	/**
	 * 
	 * @param callback
	 * @param context
	 * @param uids
	 */
	public void getTrend(ProxyCallBack<List<UserNewsVo>> callback, Context context, String uids);

	/**
	 * 获取限制业务的次数
	 * 
	 * @param callback
	 * @param context
	 * @param limitedop
	 *            [1创建公会,2加入公会,4发帖,可以按位与]
	 */
	public void getLimitedOPCount(ProxyCallBack<Map<Integer, Integer>> callback, Context context, int limitedop);

	/**
	 * 上传通讯录
	 * 
	 * @param callback
	 * @param context
	 */
	public void upContacts(ProxyCallBack<Integer> callback, Context context);

	/**
	 * 上传微博
	 * 
	 * @param callback
	 * @param context
	 */
	public void upWeiboFriends(ProxyCallBack<Integer> callback, Context context);

	/**
	 * 获取每日、新手积分任务
	 * 
	 * @param callback
	 * @param context
	 * @param type
	 *            任务类型
	 */
	public void getPointTask(ProxyCallBack<List<UserPointTaskObj>> callback, Context context, int type,int code);

	/**
	 * 获取用户积分
	 * 
	 * @param callback
	 * @param context
	 * @param uids
	 *            用户id集合
	 */
	public void getUserPoint(ProxyCallBack<List<ExtUserVo>> callback, Context context, String uids);
	/**
	 * 获取用户经验值
	 * @param callback
	 * @param context
	 * @param uids
	 */
	public void getUserExperience(ProxyCallBack<List<ExtUserVo>> callback, Context context, String uids);

	/**
	 * 用户获取到积分历史详情
	 * 
	 * @param callback
	 * @param context
	 * @param offset
	 * @param limit
	 */
	public void getUserHistroyPointDetail(ProxyCallBack<List<UserPointDetailObj>> callback, Context context, long offset, int limit);
	/**
	 * 用户获取到经验值历史详情
	 * @param callback
	 * @param context
	 * @param offset
	 * @param limit
	 */
	public void getUserHistroyExperienceValueDetail(ProxyCallBack<List<UserPointDetailObj>> callback, Context context, long offset, int limit);

	/**
	 * 获取用户等级策略接口
	 * 
	 * @param callback
	 * @param context
	 */
	public void getUserGradePolicy(ProxyCallBack<List<UserGradeVo>> callback, Context context);

	/**
	 * 获取所有积分任务详情（同步在本地，数据库查询）
	 * 
	 * @param callback
	 * @param context
	 */
	public void getPointTaskDetail(ProxyCallBack<List<PointTaskVo>> callback, Context context);
	
	/**
	 * 获取积分任务详情（同步在本地，数据库查询）
	 * 
	 * @param callback
	 * @param context
	 */
	public void getPointTaskDetailById(ProxyCallBack<PointTaskVo> callback, Context context, int tid);

	/**
	 * 获取用户的扩展数据， 关注数，公会数，粉丝数，贴吧数，帖子数，收藏数
	 * @param callBack
	 * @param context
	 * @param ids
	 * @param type
	 */
	public void getExtUserData(ProxyCallBack<List<ExtUserVo>>callBack,Context context,String ids,int type);
	
	/**
	 * 上传PUSH信息
	 * @param callBack
	 * @param context
	 * @param pushid
	 * @param type
	 */
	public void SendPushCount(ProxyCallBack<Integer>callback,Context context,long pushid,int action);
	
	/**
	 * 获取积分商城顶部的标签
	 * @param callBack
	 * @param context
	 */
	public void getPointTab(ProxyCallBack<List<GoodsTab>>callBack,Context context);
	/**
	 * 获取商品详细列表
	 * @param callBack
	 * @param context
	 * @param categoryId
	 * @param goodsStatus
	 * @param goodType
	 * @param offset
	 * @param limit
	 */
	public void getGoodsList(ProxyCallBack<List<Goods>>callBack,Context context,long categoryId,String goodsStatus,int goodType,int offset,int limit);
	
	/**
	 * 获取兑换记录
	 * @param callBack
	 * @param context
	 * @param offset
	 * @param limit
	 */
	public void getChangeRecords(ProxyCallBack<List<ChangeRecordsEntity>>callBack,Context context,int offset, int limit);
	
	/**
	 * 获取商品详情的
	 * 接口
	 * @param callBack
	 * @param context
	 * @param goodsId
	 */
	public void getGoodsDetail(ProxyCallBack<Goods> callBack,Context context,long goodsId);
	/**
	 * 获取订单详情
	 * @param callBack
	 * @param context
	 * @param transId
	 */
	public void getOrderDetail(ProxyCallBack<OrderDetail> callBack,Context context,long transId,int deliveryType);
	
	/**
	 * 兑换商品接口
	 * @param callBack
	 * @param context
	 * @param goodsId
	 * @param transInfo
	 */
	public void transGoods(ProxyCallBack<String>callBack,Context context,long goodsId,String transInfo);
	
	/**
	 * 采集应用信息
	 * @param callback
	 * @param context
	 * @param app
	 */
	public void collectAppInfo(ProxyCallBack<Integer>callback, Context context, String app);
	
	
	/**
	 * 采集通讯录信息发送到服务端
	 * @param callback
	 * @param context
	 * @param b
	 * @param type
	 */
	public void collectContactInfo(ProxyCallBack<Integer>callback,Context context, UploadContactsRequest b);
	
	
	/**
	 * 下载网络图片并获取本地图片路径
	 * @param callback
	 * @param context
	 * @param imageUrl
	 */
	public void getImageUrlToPath(ProxyCallBack<String>callback, Context context, String imageUrl);
	
	/**
	 * 下载网络图片到data/data下
	 * @param callback
	 * @param context
	 * @param imageUrl
	 * @param fileName
	 */
	public void getImageUrlToData(ProxyCallBack<Integer>callback, Context context, String imageUrl, String fileName);
	
	/**
	 * 获取在设置页面 活动专区的title 
	 * @param callBack
	 * @param context
	 */
	public void getActivityDivisionTitle(ProxyCallBack<String>callBack,Context context);
	
	/**
	 * 获取赞列表的用户数据
	 * @param callBack
	 * @param context
	 * @param offset
	 * @param limit
	 * @param gameid
	 */
	public void getSupportUserData(ProxyCallBack<List<SupportUserVo>>callBack, Context context, long offset, int limit, long gameid);
	
	/**
	 * 记录用户行为日志
	 * @param callback
	 * @param context
	 * @param op 行为op
	 * @param tid 对象id
	 * @param ttype 对象类型
	 * @param content 备注信息
	 */
	public void collectActionlLog(ProxyCallBack<Integer>callback,Context context, int op, Long tid, Integer ttype, String content);
	/**
	 * 获取用户游戏属性值
	 * @param callback
	 * @param context
	 * @param gid
	 */
	public void getGameKeyData(ProxyCallBack<List<GameKeyVo>> callback,Context context,Long gid);
	/**
	 * 获取游戏服务器信息
	 * @param callback
	 * @param context
	 * @param type
	 * @param uptime
	 */
	public void getGameServiceData(ProxyCallBack<List<GameRoleServiceVo>> callback,Context context,int type,Long uptime);
	/**
	 * 获取游戏列表
	 * @param callback
	 * @param context
	 */
	public void getGameList(ProxyCallBack<List<ExtraGameVo>> callback, Context context);
	/**
	 * 刪除用的角色
	 * @param callback
	 * @param context
	 * @param roleid
	 */
	public void deleteGameRole(ProxyCallBack<Integer> callback, Context context,Long roleid);
	/**
	 * 角色反馈
	 * @param callback
	 * @param context
	 * @param gid
	 */
	public void UserGameAPPly(ProxyCallBack<Integer> callback,Context context,Long gid);
	/**
	 * 添加用户角色
	 * @param callback
	 * @param context
	 * @param sid
	 * @param role
	 */
	public void addUserRoleData(ProxyCallBack<Integer> callback,Context context,Long gid,Long sid,String rolename,Msgs.UserRoleData role);
	/**
	 * 获取用户角色数据2
	 * @param callback
	 * @param gid
	 * @param sid
	 * @param roleid
	 * @param offset
	 * @param limit
	 */
	public void getFilterUserRoleData(ProxyCallBack<UserRoleDetail> callback,Context context,long gid,long sid,long roleid,long offset,int limit);
}
