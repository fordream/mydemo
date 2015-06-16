package com.iwgame.msgs.localdb.dao;

import com.iwgame.msgs.vo.local.ContentVo;

public interface ContentDao {
    /**
     * 插入一条内容详情
     * 
     * @param vo
     * @return
     */
    public ContentVo insert(ContentVo vo);

    /**
     * 通过id更新一条消息
     * 
     * @param vo
     * @return
     */
    public int updateById(ContentVo vo);

    /**
     * 通过内容id和内容类型获得详细信息
     * 
     * @param type
     * @param channel
     * @return
     */
    public ContentVo getContent(int type, long contentid);
    //
    // /**
    // * 通过id获得详细信息
    // * @param id
    // * @return
    // */
    // public ContentVo getContentById(long id);
    //
    // /**
    // * 通过pid和ptype 获得相关信息
    // * @param pid
    // * @param ptype
    // * @return
    // */
    // public List<ContentVo> getContentList(long pid,int ptype);
    //

    // /**
    // * 获得某个主体的动态
    // * @param subjectid 为-1 代表不区分用户或贴吧
    // * @param subjectDomain
    // * @param direction
    // * @param keyvalue
    // * @param size
    // * @return
    // */
    // public List<ExtContentVo> getContentList(long subjectid,String
    // subjectDomain,int direction,int keyvalue,int size);

}
