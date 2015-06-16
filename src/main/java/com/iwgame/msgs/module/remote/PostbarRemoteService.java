/**      
* PostbarRemoteService.java Create on 2013-12-27     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.remote;

import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.proto.Msgs.ContentList;
import com.iwgame.xaction.proto.XAction.XActionResult;

/** 
 * @ClassName: PostbarRemoteService 
 * @Description: TODO(帖吧的service接口) 
 * @author chuanglong
 * @date 2013-12-27 上午9:02:04 
 * @Version 1.0
 * 
 */
public interface PostbarRemoteService {
    
    /**
     * 搜索帖子，获得帖子列表
     * @param callback
     * @param context
     * @param gid 贴吧id（可选）
     * @param title 帖子的标题（可选）
     * @param order 排序(最新回复/发布时间)（可选） 1 发布时间 2回复时间
     * @param uid 用户id(可选）
     * @param offset
     * @param limit
     */
    public void getTopicList(ServiceCallBack<XActionResult> callback,Context context,long gid,String title,int order,int tagid,int filter, long uid,long offset,int limit, String tagName);
    
    /**
     * 获得收藏帖子的列表
     * @param callback
     * @param context
     * @param uid 用户id
     * @param offset
     * @param limit
     */
    public void getFavoriteTopicList(ServiceCallBack<XActionResult> callback , Context context,long uid ,long offset,int limit);
    /**
     * 获得帖子详情
     * @param callback
     * @param context
     * @param topicId 帖子id
     */
    public void getTopicDetail(ServiceCallBack<XActionResult> callback,Context context,long topicId);
    
    /**
     * 来获得回复列表(帖子的回复（可以用过滤条件）；帖子回复的父回复；回复我的）
     * @param callback
     * @param context
     * @param tid  target id 
     * @param ttype (帖子， 帖子回复， 用户)
     * @param filter 过滤条件（可选，目前有过滤楼主）
     * @param offset
     * @param limit
     * @param offsettype offset的类型 （1为按照偏移量来获得，2 按照
     */
    public void getTopicReplyList(ServiceCallBack<XActionResult> callback, Context context,long tid,int ttype ,int filter,int offsettype ,long offset,int limit);

    
    /**
     * 获得帖子回复详情
     * @param callback
     * @param context
     * @param replyid 回复id
     */
    public void getTopicReplyDetail(ServiceCallBack<XActionResult> callback, Context context,long replyid);
    
    
    
    /**
     * 申请吧主
     * @param callback
     * @param context
     * @param gid 贴吧id
     * @param name 用户姓名
     * @param idcardNo 身份证号
     * @param applyContent 申请理由
     * @param idcardImage 身份证图片
     */
    public void applyPostbarMaster(ServiceCallBack<XActionResult> callback, Context context,long gid, String name,String idcardNo,String applyContent,byte[] idcardImage);
  
    /**
     * 获取已经申请贴吧贴吧的次数
     * @param callback
     * @param context
     * @param gid
     */
    public void getMasterApplyCount(ServiceCallBack<XActionResult> callback, Context context,long gid);

    /**
     * 获得吧主列表
     * @param callback
     * @param context
     * @param gid
     */
    public void getPostbarMaster(ServiceCallBack<XActionResult> callback, Context context,long gid);

    /**
     * 获得贴吧发帖数信息
     * @param callback
     * @param context
     * @param gid
     */
//    public void getGamePostbarCount(ServiceCallBack<XActionResult> callback, Context context,long gid);
    

    
    /**
     * 获得某个贴吧的帖子的标签
     * @param callback
     * @param gid
     */
    public void getTopicTags(ServiceCallBack<XActionResult> callback,long gid);
    
    /**
     * 获取限制业务的次数 (贴吧）
     * @param callback
     * @param context
     * @param limitedop
     */
    public void getLimitedOPCount(ServiceCallBack<XActionResult> callback, Context context,  int limitedop);
    
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
			byte[] resource, int resourceType, String pos);
	
	/**
	 * 获得贴吧发帖的最大Index
	 * @param callback
	 * @param context
	 * @param gid
	 */
	public void getGamePostbarMaxIndex(ServiceCallBack<XActionResult> callback, Context context,String gids);
    
	/**
	 * 查询贴吧的关注数
	 * @param callback
	 * @param context
	 * @param gids
	 */
	public void getGameFollowCount(ServiceCallBack<XActionResult> callback, Context context,String gids);
	
	/**
	 * 主帖和评论的赞踩
	 * @param callback
	 * @param tid
	 * @param topicId
	 * @param topicUid
	 * @param trUid
	 * @param type
	 * @param op
	 */
	public void likeOrUnLikeTopic(ServiceCallBack<XActionResult> callback, Context context, Long tid, Long topicId, Long topicUid, Long trUid, int type, int op);
	
	/**
	 * 
	 * @param callback
	 * @param context
	 * @param tid
	 * @param gid
	 * @param op
	 * @param actiontype
	 * @param topictype
	 * @param contentList
	 * @param position
	 */
	public void publicTopic(ServiceCallBack<XActionResult> callback, Context context, Long tid, Long gid, int op, int actiontype, int topictype, ContentList contentList, String position, int isSaveAlbum);
}
