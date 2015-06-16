/**      
 * UserRemoteService.java Create on 2013-9-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: UserRemoteService
 * @Description: 用户服务接口
 * @author 王卫
 * @date 2013-9-24 下午05:04:14
 * @Version 1.0
 * 
 */
public interface UserRemoteService {

	/**
	 * 注册账号
	 * 
	 * @param callback
	 * @param context
	 * @param captcha
	 * @param accountName
	 * @param password
	 * @param nickname
	 * @param sex
	 * @param age
	 * @param avatar
	 */
	public void registAccount(ServiceCallBack<XActionResult> callback, Context context, String captcha, String accountName, String password,
			String nickname, int sex, Integer age, byte[] avatar, String origin);
	
	
	/**
	 * 
	* @Title: registAccount    
	* @Description: 第三方账号绑定注册   
	* @param @param callback
	* @param @param context
	* @param @param openID
	* @param @param target
	* @param @param sex
	* @param @param avatar
	* @param @param nickname    设定文件    
	* @return void    返回类型    
	* @throws
	 */
	public void registAccount(ServiceCallBack<XActionResult> callback, Context context, String openID, int target, int sex, String avatar,
			String nickname, String origin);
	
	/**
	 * 游客快速注册
	 * 
	 * @param callback
	 * @param context
	 */
	public void registGuestAccount(ServiceCallBack<XActionResult> callback, Context context, String origin);

	/**
	 * 绑定手机
	 * @param callback
	 * @param context
	 * @param captcha
	 * @param accountName
	 * @param password
	 */
	public void bundPhone(ServiceCallBack<XActionResult> callback, Context context, String captcha, String accountName, String password, String nickname, int sex, byte[] avatar);
	
	/**
	 * 验证账号
	 * 
	 * @param callback
	 * @param context
	 */
	public void verifyAccount(ServiceCallBack<XActionResult> callback, Context context);

	/**
	 * 更新用户信息
	 * 
	 * @param callback
	 * @param context
	 * @param sex
	 * @param mood
	 * @param age
	 * @param job
	 * @param avatarData
	 * @param avatar
	 * @param city
	 * @param nickname
	 * @param desc
	 * @param gameTime
	 * @param likeGameTime
	 */
	public void updateUser(ServiceCallBack<XActionResult> callback, Context context, Integer sex, String mood, Integer age, String job,
			byte[] avatarData, String avatar, String city, String nickname, String desc, String gameTime, String likeGameType, String weiboUid, String weiboName);

	/**
	 * 更新用户之间的关系
	 * 
	 * @param callback
	 * @param context
	 * @param ids
	 * @param reltype
	 */
	public void updateRelUser(ServiceCallBack<XActionResult> callback, Context context, String ids, int reltype);

	/**
	 * 同步用户关系数据
	 * 
	 * @param callback
	 * @param context
	 * @param utime
	 */
	public void syncUserRel(ServiceCallBack<XActionResult> callback, Context context, long utime, int type);
	/**
	 * 同步用户角色数据
	 * @param callback
	 * @param context
	 * @param utime
	 * @param type
	 */
	public void syncUserRole(ServiceCallBack<XActionResult> callback, Context context, long utime, int type);
	/**
	 * 获取用户信息
	 * 
	 * @param callback
	 * @param context
	 * @param id
	 * @param utime
	 * @param type
	 */
	public void getUserInfo(ServiceCallBack<XActionResult> callback, Context context,ContentDetailParams ids, int type, Long uid);
	/**
	 * 获取用户角色信息
	 * @param callback
	 * @param context
	 * @param ids
	 * @param type
	 * @param uid
	 */
	public void getUserRoleInfo(ServiceCallBack<XActionResult> callback,Context context,ContentDetailParams ids,int type,Long uid,Long gid,Long sid);
	
	/**
	 * 添加游戏角色
	 * @param callback
	 * @param sid
	 * @param name
	 * @param grade
	 * @param rank
	 */
	public void addUserRole(ServiceCallBack<XActionResult> callback,Context context,long sid,String name,Integer grade,String rank);
	/**
	 * 添加图片
	 * 
	 * @param callback
	 * @param context
	 * @param image
	 */
	public void addAlbum(ServiceCallBack<XActionResult> callback, Context context, byte[] image);

	/**
	 * 获取用户相册信息
	 * 
	 * @param callback
	 * @param context
	 * @param id
	 * @param utime
	 */
	public void getUserAlbum(ServiceCallBack<XActionResult> callback, Context context, long id, long utime);

	/**
	 * 删除用户相册
	 * 
	 * @param callback
	 * @param context
	 * @param resourceId
	 */
	public void delUserAblum(ServiceCallBack<XActionResult> callback, Context context, String resourceId);

	/**
	 * 用户行为动作
	 * 
	 * @param callback
	 * @param context
	 * @param tid
	 * @param ttype
	 * @param op
	 * @param content
	 * @param seq
	 *            操作流水号
	 * @param resource
	 * @param resourceType
	 *            资源类型
	 * @param pos
	 */
	public void userAction(ServiceCallBack<XActionResult> callback, Context context, long tid, int ttype, int op, String content, long seq,
			byte[] resource, int resourceType, String pos,String remark);

	
	
	
	
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
	public void userAction(Context context,String position, long tid, int ttype, int op, String content, String remark,ServiceCallBack<XActionResult>callBack);
	
	
	
	
	/**
	 * 查询用户是否已向某人推荐某贴吧
	 * 
	 * @param callback
	 * @param context
	 * @param gid
	 * @param ids
	 */
	public void searchRecommended(ServiceCallBack<XActionResult> callback, Context context, long gid, String ids);

	/**
	 * 设置位置
	 * 
	 * @param callback
	 * @param context
	 * @param position
	 */
	public void setPosition(ServiceCallBack<XActionResult> callback, Context context, String position);

	/**
	 * 创建或修改公会
	 * 
	 * @param callback
	 * @param context
	 * @param name
	 * @param avatar
	 * @param gid
	 * @param desc
	 * @param notice
	 * @param isvalidate
	 * @param grid
	 */
	public void creatOrUpdataGroup(ServiceCallBack<XActionResult> callback, Context context, String name, byte[] avatar, Long gid, String desc,
			String notice, Boolean isvalidate, Long grid);

	/**
	 * 消息提醒
	 * 
	 * @param callback
	 * @param context
	 * @param tid
	 *            （公会ID等）
	 * @param type
	 *            （1全局消息2个人聊天消息3关于我的4公会消息）
	 * @param status
	 *            （0关1开）
	 */
	public void setMessageTip(ServiceCallBack<XActionResult> callback, Context context, Long tid, int type, int status);

	/**
	 * 获取消息提醒配置
	 * 
	 * @param callback
	 * @param context
	 */
	public void getMessageTip(ServiceCallBack<XActionResult> callback, Context context);

	/**
	 * 验证通讯录及微博用户是否注册游伴
	 * 
	 * @param callback
	 * @param context
	 * @param condition
	 * @param type
	 */
	public void verifiyContactUser(ServiceCallBack<XActionResult> callback, Context context, String condition, int type);
	
	/**
	 * 获取用户的动态
	 * @param callback
	 * @param context
	 * @param uids
	 */
	public void getTrend(ServiceCallBack<XActionResult> callback, Context context, String uids);
	/**
	 * 获取限制业务的次数 
	 * @param callback
	 * @param context
	 * @param limitedop [1创建公会,2加入公会,4发帖,可以按位与]
	 */
	public void getLimitedOPCount(ServiceCallBack<XActionResult> callback, Context context,  int limitedop);
	
	/**
	 * 上传通讯录
	 * @param callback
	 * @param context
	 * @param contacts
	 */
	public void upContact(ServiceCallBack<XActionResult> callback, Context context, List<Map<String, String>> contacts);
	
	/**
	 * 上传微博
	 * @param callback
	 * @param context
	 * @param weiboInfo
	 */
	public void upWeibo(ServiceCallBack<XActionResult> callback, Context context, String weiboInfo);
	
	/**
	 * 获取每日、新手积分任务
	 * 
	 * @param callback
	 * @param context
	 * @param type
	 *            任务类型
	 */
	public void getPointTask(ServiceCallBack<XActionResult> callback, Context context, int type, int code);
	
	/**
	 * 用户获取到积分历史详情
	 * @param callback
	 * @param context
	 * @param offset
	 * @param limit
	 */
	public void getUserHistoryPointDetail(ServiceCallBack<XActionResult> callback, Context context, long offset, int limit);
	
	/**
	 * 获取用户
	 * 额外的数据，关注数，公会数，粉丝数等
	 * @param callBack
	 * @param context
	 * @param ids
	 * @param type
	 */
	public void getExtUserData(ServiceCallBack<XActionResult> callBack,Context context,String ids,int type);
	
	/**
	 * 发送PUSH统计信息
	 * @param callBack
	 * @param context
	 * @param pushid
	 * @param type
	 */
	public void SendPushCount(ServiceCallBack<XActionResult> callBack,Context context,long pushid,int action);
	
	/**
	 * 获取积分商城顶部的tab
	 * @param callBack
	 * @param context
	 */
	public void getPointTab(ServiceCallBack<XActionResult>callBack,Context context);
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
	public void getGoodsList(ServiceCallBack<XActionResult>callBack,Context context, long categoryId, String goodsStatus, int goodType,
			int offset, int limit);
	/**
	 * 获取兑换记录
	 * 
	 * @param callBack
	 * @param context
	 * @param offset
	 * @param limit
	 */
	public void getChangeRecords(ServiceCallBack<XActionResult>callBack,Context context,int offset,int limit);
	/**
	 * 获取商品的详情
	 * @param callBack
	 * @param context
	 * @param goodsId
	 */
	public void getGoodsDetail(ServiceCallBack<XActionResult>callBack,Context context,long goodsId);
	
	/**
	 * 
	 * @param callBack
	 * @param context
	 * @param transId
	 */
	public void getOrderDetail(ServiceCallBack<XActionResult>callBack,Context context,long transId,int deliveryType);
	
	/**
	 * 兑换商品的接口
	 * @param callBack
	 * @param context
	 * @param goodsId
	 * @param transInfo
	 */
	public void transGoods(ServiceCallBack<XActionResult>callBack,Context context,long goodsId,String transInfo);

	/**
	 * 设置号码类型，0为Android传递
	 */
	public void setPhoneType(ServiceCallBack<XActionResult>callBack,Context context);
	
	/**
	 * 获取创建公会的最大数目 
	 * @param callBack
	 * @param context
	 * @param limiteType
	 */
	public void getCreatGroupMax(ServiceCallBack<XActionResult>callBack,Context context,int limiteType);
	
	/**
	 * 获取活动专区的title
	 * @param callBack
	 * @param context
	 */
	public void getActivityDivisionTitle(ServiceCallBack<XActionResult>callBack,Context context);
	
	/**
	 * 获取赞列表的用户数据
	 * @param callBack
	 * @param context
	 * @param offset
	 * @param limit
	 * @param gameid
	 */
	public void getSupportUserData(ServiceCallBack<XActionResult>callBack, Context context, long offset, int limit, long gameid);
	/**
	 * 用户获取到经验值历史详情
	 * @param callback
	 * @param context
	 * @param offset
	 * @param limit
	 */
	public void getUserHistoryExperienceDetail(ServiceCallBack<XActionResult> callback,Context context,long offset,int limit);
	/**
	 * 获取游戏的属性值
	 * @param callback
	 * @param context
	 * @param gid
	 */
	public void getGameRoleValues(ServiceCallBack<XActionResult> callback,Context context,long gid);
	/**
	 * 获取游戏列表
	 * @param callback
	 * @param context
	 */
	public void getGameList(ServiceCallBack<XActionResult> callback, Context context);
	/**
	 * 获取服务器信息
	 * @param callback
	 * @param context
	 * @param syncType
	 * @param utime
	 */
	public void getGameServiceList(ServiceCallBack<XActionResult> callback, Context context,int syncType, Long utime);
	/**
	 * 游戏反馈
	 * @param callback
	 * @param context
	 * @param gid
	 */
	
	public void getGameApplyInfo(ServiceCallBack<XActionResult> callback,Context context,long gid);
	/**
	 * 删除用户角色
	 * @param callback
	 * @param context
	 * @param roleId
	 */
	public void deleteUserRoleData(ServiceCallBack<XActionResult> callback,Context context,Long roleId);
	/**
	 * 添加用户角色
	 * @param callback
	 * @param context
	 * @param sid
	 * @param role
	 */
	public void addUserRoleData(ServiceCallBack<XActionResult> callback,Context context,Long gid,Long sid, String rolename ,Msgs.UserRoleData role);
	/**
	 * 获取角色数据2
	 * @param callback
	 * @param context
	 * @param gid
	 * @param sid
	 * @param roleid
	 * @param offset
	 * @param limit
	 */
	public void getFilterRoleData(ServiceCallBack<XActionResult>callback,Context context,long gid,long sid,long roleid,long offset,int limit);
}

