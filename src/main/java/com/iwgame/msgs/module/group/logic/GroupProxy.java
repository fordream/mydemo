/**      
 * GroupProxy.java Create on 2013-10-23     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.logic;

import java.util.List;

import android.R.integer;
import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.vo.local.GroupGradeVo;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;

/**
 * @ClassName: GroupProxy
 * @Description: 公会数据代理层
 * @author 王卫
 * @date 2013-10-23 下午04:33:57
 * @Version 1.0
 * 
 */
public interface GroupProxy {

	/**
	 * 创建和修改公会
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
	public void creatOrUpdataGroup(ProxyCallBack<List<Object>> callback, Context context, String name, byte[] avatar, Long gid, String desc,
			String notice, Boolean isvalidate, Long grid);
	
	/**
	 * 增加了一个方法   
	 * 就是点击加入验证的时候 
	 * 要去访问服务端
	 * 之前的接口方法是调用了上面 的这个接口
	 * 这个接口在实现的时候 请求太多 了
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
	public void modifyGroupSettingInvaliDate(ProxyCallBack<Integer> callback, Context context, String name, byte[] avatar, Long gid, String desc,
			String notice, Boolean isvalidate, Long grid);

	/**
	 * 获取我的公会
	 * 
	 * @param callback
	 * @param context
	 */
	public void getMyGroups(ProxyCallBack<List<GroupVo>> callback, Context context);

	/**
	 * 搜索贴吧
	 * 
	 * @param callback
	 * @param context
	 * @param idname
	 * @param uid
	 * @param gid
	 * @param limit
	 * @param offset
	 * @param resulttype
	 */
	public void searchGroups(ProxyCallBack<PagerVo<GroupVo>> callback, Context context, String idname, Long uid, String gid, int limit, long offset,
			Integer near, Integer minCount, Integer maxCount, Boolean needValidate, Boolean isfind, Integer source, String sid);

	/**
	 * 搜索工会个数
	 * 
	 * @param callback
	 * @param context
	 * @param gid
	 * @param offset
	 * @param limit
	 */
	public void searchGroups(ProxyCallBack<Integer> callback, Context context, String gid, long offset, int limit, Integer source);

	/**
	 * 获取申请加入公会的用户，用户管理员批准
	 * 
	 * @param callback
	 * @param context
	 * @param grid
	 */
	public void getApplyUsers(ProxyCallBack<List<UserItemObj>> callback, Context context, long grid);

	/**
	 * 获取某个公会的成员列表
	 * 
	 * @param callback
	 * @param context
	 * @param grid
	 */
	public void getGroupUsers(ProxyCallBack<List<UserItemObj>> callback, Context context, long grid);

	/**
	 * 获取某个公会的成员列表，按照最后发言的倒序排列
	 * 
	 * @param callback
	 * @param context
	 * @param grid
	 */
	public void getGroupUsersByMessage(ProxyCallBack<List<UserItemObj>> callback, Context context, long grid);

	/**
	 * 获取邀请用户列表(数据为通讯录的好友且不是公会的成员的用户，所有需要先同步)
	 * 
	 * @param callback
	 * @param context
	 */
	public void getInviteUsers(ProxyCallBack<List<UserItemObj>> callback, Context context, long grid);

	/**
	 * 清除申请用户列表
	 * 
	 * @param callback
	 * @param context
	 * @param grid
	 * @param maxUid
	 */
	public void cleanApproveUsers(ProxyCallBack<Integer> callback, Context context, long grid);

	/**
	 * 查找公会详情
	 * 
	 * @param callback
	 * @param context
	 * @param grid
	 * @param type
	 */
	public void getGroupDetailInfo(ProxyCallBack<List<GroupVo>> callback, Context context, ContentDetailParams params, int type,Long uid);

	/**
	 * 获得某个用户和某个公会的关系
	 * 
	 * @param grid
	 * @param uid
	 * @return
	 */
	public GroupUserRelVo getRel(long grid, long uid);

	/**
	 * 查询推荐的公会
	 * 
	 * @param callback
	 * @param context
	 * @param gids
	 * @param offset
	 * @param limit
	 */
	public void searchRecommendGroups(ProxyCallBack<PagerVo<GroupVo>> callback, Context context, String gids, long offset, int limit);
	
	/**
	 * 
	 * @param callback
	 * @param context
	 * @param grid
	 * @param utime
	 */
	public void getGroupUsers(ProxyCallBack<List<UserItemObj>> callback, Context context, Long grid, long utime);
	
	/**
	 * 获取公会成员贡献排行
	 * @param callback
	 * @param context
	 * @param grid
	 */
	public void getGroupContributePointTop(ProxyCallBack<List<UserItemObj>> callback, Context context, Long grid, int limit);

	/**
	 * 获取公会等级策略接口
	 * 
	 * @param callback
	 * @param context
	 */
	public void getGroupGradePolicy(ProxyCallBack<List<GroupGradeVo>> callback, Context context);
	
	/**
	 * 获取公会积分
	 * 
	 * @param callback
	 * @param context
	 * @param gids
	 *            用户id集合
	 */
	public void getGroupPoint(ProxyCallBack<List<GroupVo>> callback, Context context, String gids);
	
	/**
	 * 查询用户当天贡献公会积分
	 * @param callback
	 * @param context
	 * @param grids
	 */
	public void getUserContributeGroupPoint(ProxyCallBack<List<UserItemObj>> callback, Context context, String grids);
	
	
	/**
	 * 同步公会的成员列表
	 * @param callBack
	 * @param context
	 * @param grids
	 */
	public void syncGroupMemberList(ProxyCallBack<List<GroupUserRelVo>>callBack,Context context, long grids,long offset,int limit,int orderType);
	
	
	/**
	 * 获取我创建公会的最大数据
	 * @param callBack
	 * @param context
	 * @param limitType
	 */
	public void getCreatGroupMax(ProxyCallBack<Integer> callBack,Context context, int limitType);
	
}
