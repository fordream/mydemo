/**      
* PostbarProxy.java Create on 2013-12-24     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.postbar.logic;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentList;
import com.iwgame.msgs.proto.Msgs.PostbarActionResult;
import com.iwgame.msgs.proto.Msgs.PostbarTopicListResult;
import com.iwgame.msgs.proto.Msgs.PostbarTopicReplyListResult;
import com.iwgame.msgs.vo.local.TopicTagVo;

/** 
 * @ClassName: PostbarProxy 
 * @Description: TODO(贴吧proxy接口) 
 * @author chuanglong
 * @date 2013-12-24 下午3:23:16 
 * @Version 1.0
 * 
 */
public interface PostbarProxy {
    /**
     * 搜索帖子，获得帖子列表
     * @param callback
     * @param context
     * @param gid 贴吧id（可选）
     * @param title 帖子的标题（可选）
     * @param order 排序(最新回复/发布时间)（可选） 1 发布时间 2回复时间
     * 
     * @param uid 用户id(可选）
     * @param offset
     * @param limit
     */
    public void getTopicList(ProxyCallBack<List<Msgs.PostbarTopicDetail>> callback,Context context,long gid,String title,int order,int tagid,int filter,long uid,long offset,int limit, String tagName);
    
    /**
     * 搜索帖子，获得帖子列表
     * @param callback
     * @param context
     * @param gid 贴吧id（可选）
     * @param title 帖子的标题（可选）
     * @param order 排序(最新回复/发布时间)（可选） 1 发布时间 2回复时间
     * 
     * @param uid 用户id(可选）
     * @param offset
     * @param limit
     * @param tagName
     */
    public void getTopicListMap(ProxyCallBack<Map<String, Object>> callback,Context context,long gid,String title,int order,int tagid,int filter,long uid,long offset,int limit, String tagName);
    /**
     * 获得收藏帖子的列表
     * @param callback
     * @param context
     * @param offset
     * @param limit
     */
    public void getFavoriteTopicList(ProxyCallBack<List<Msgs.PostbarTopicDetail>> callback, Context context,long uid ,long offset,int limit);
    /**
     * 获得帖子详情
     * @param callback
     * @param context
     * @param topicId
     */
    public void getTopicDetail(ProxyCallBack<Msgs.PostbarTopicDetail> callback,Context context,long topicId);
    
    /**
     * 操作帖子
     * @param callback
     * @param context
     * @param topicId
     * @param op操作类型 (置顶,加精,删除，设公告，取消置顶，取消加精，取消设为公告,收藏，取消收藏)
     * @param actionReason 操作理由
     */
    public void actionTopic(ProxyCallBack<Integer> callback, Context context,long topicId,int op,String actionReason);
    /**
     * 发布帖子
     * @param callback
     * @param context
     * @param gid 贴吧
     * @param title 标题
     * @param content 内容
     * @param resource 资源
     * @param tagid tagid
     */
    public void publishTopic(ProxyCallBack<Integer> callback, Context context,long gid ,String title,String content,byte[] resource,int tagId);
    
    /**
     * 发布回复
     * @param callback
     * @param context
     * @param targetId 帖子和评论的id，根据type来决定
     * @param targetType 9为帖子，10，评论
     * @param Content
     */
    public void publishReply(ProxyCallBack<Integer> callback, Context context,long targetId, int targetType,String Content, byte[] resource);
    

    /**
     * 获得帖子的回复列表（帖子回复（可以有过滤条件），回复的父回复，回复我的
     * @param callback
     * @param context
     * @param tid 帖子
     * @param ttype (9为帖子，10为帖子回复，0 为用户）
     * @param filter 过滤条件（可选，目前有过滤楼主）
     * @param offset
     * @param limit
     */
    public void getTopicReplyList(ProxyCallBack<PostbarTopicReplyListResult> callback, Context context,long tid,int ttype,int filter, int offsettype,long offset,int limit);
    

    /**
     * 获得帖子回复详情
     * @param callback
     * @param context
     * @param replyid
     */
    public void getTopicReplyDetail(ProxyCallBack<Msgs.PostbarTopicReplyDetail> callback, Context context,long replyid);
    
    /**
     * 操作帖子回复
     * @param callback
     * @param context
     * @param replyid 帖子id
     * @param op（删除帖子回复）
     */
    public void actionTopicReply(ProxyCallBack<Integer> callback, Context context,long replyid,int op,String actionReason);
    
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
    public void applyPostbarMaster(ProxyCallBack<Integer> callback, Context context,long gid, String name,String idcardNo,String applyContent,byte[] idcardImage);
  
    /**
     * 获取已经申请贴吧贴吧的次数
     * @param callback
     * @param context
     * @param gid
     */
    public void getMasterApplyCount(ProxyCallBack<Integer> callback, Context context,long gid);
    
    /**
     * 获得某个贴吧的帖子标签
     */
    public void getTopicTags(ProxyCallBack<List<TopicTagVo>> callback, long gid);
    
    
    /**
     * 获取限制业务的次数 (贴吧）
     * @param callback
     * @param context
     * @param limitedop
     */
    public void getLimitedOPCount(ProxyCallBack<Map<Integer,Integer>> callback, Context context, int limitedop);
    
    /**
     * 主帖和评论的赞踩
     * @param callback
     * @param context
     * @param tid
     * @param topicId
     * @param topicUid
     * @param trUid
     * @param type
     * @param op
     */
    public void likeOrUnLikeTopic(ProxyCallBack<Integer> callback, Context context, Long tid, Long topicId, Long topicUid, Long trUid, int type, int op);
    
    /**
     * 发帖
     * @param callback
     * @param context
     * @param tid
     * @param gid
     * @param op
     * @param actiontype
     * @param topictype
     * @param contentList
     * @param position
     * @param isSaveAlbum
     */
    public void publicTopic(ProxyCallBack<PostbarActionResult> callback, Context context, Long tid, Long gid, int op, int actiontype, int topictype, ContentList contentList, String position, int isSaveAlbum);
    
    /**
     * 查找贴吧动态
     * @param callback
     * @param context
     * @param offset
     * @param limit
     */
    public void searchTopicNews(ProxyCallBack<PostbarTopicListResult> callback, Context context, long offset, int limit);
}
