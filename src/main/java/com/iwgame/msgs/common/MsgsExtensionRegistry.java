/**      
 * MsgsExtensionRegistry.java Create on 2013-9-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import com.iwgame.msgs.proto.Msgs;
import com.iwgame.sdk.xaction.XActionUtils.XMessageExtensionRegistry;
import com.iwgame.xaccount.proto.XAccountInfo;

/**
 * @ClassName: MsgsExtensionRegistry
 * @Description: 注册pb扩展方法
 * @author 王卫
 * @date 2013-9-24 上午10:42:12
 * @Version 1.0
 * 
 */
public class MsgsExtensionRegistry {

    public static void registryExtension() {
        XMessageExtensionRegistry extensionRegistry = XMessageExtensionRegistry.getInstance();
        
        extensionRegistry.add(Msgs.syncRequest);
        extensionRegistry.add(Msgs.chatRequest);
        
        extensionRegistry.add(Msgs.subscribeChannelResponse);
        extensionRegistry.add(Msgs.syncResponse);
        extensionRegistry.add(Msgs.chatResponse);
        
        extensionRegistry.add(Msgs.syncNotification);
        
        extensionRegistry.add(Msgs.userInfoDetail);
        extensionRegistry.add(Msgs.userAlbumResult);
        extensionRegistry.add(Msgs.gameInfoDetail);
        extensionRegistry.add(Msgs.gamePackageInfoDeal);
        extensionRegistry.add(Msgs.groupQueryResult);
        extensionRegistry.add(Msgs.groupApplyQueryResult);
        extensionRegistry.add(Msgs.groupMembersSyncResult);
        extensionRegistry.add(Msgs.contentDetailsResult);
        extensionRegistry.add(Msgs.contentExtDataResult);
        extensionRegistry.add(Msgs.contentListResult);
        extensionRegistry.add(Msgs.userQueryInfo);
        extensionRegistry.add(Msgs.userQueryResult);
        extensionRegistry.add(Msgs.gameQueryResult);
        //extensionRegistry.add(Msgs.topQueryResult);
        extensionRegistry.add(Msgs.recommendResult);
        extensionRegistry.add(Msgs.id);
        extensionRegistry.add(XAccountInfo.accountSummary);
        extensionRegistry.add(Msgs.gameRecommendResult);
        extensionRegistry.add(Msgs.groupRecommendResult);
        extensionRegistry.add(Msgs.userRecommendResult);
        extensionRegistry.add(Msgs.relationResult);
        
        extensionRegistry.add(Msgs.postbarTopicListResult);
        extensionRegistry.add(Msgs.postbarTopicDetail);
        extensionRegistry.add(Msgs.postbarTopicReplyListResult);
        extensionRegistry.add(Msgs.postbarTopicReplyDetail);
        extensionRegistry.add(Msgs.msgNoticeSet);
        extensionRegistry.add(Msgs.postbarTopicAddedResult);
        extensionRegistry.add(Msgs.postbarTagResult);
        extensionRegistry.add(Msgs.uidMapperResult);
        extensionRegistry.add(Msgs.gameFriendCountResult);
        extensionRegistry.add(Msgs.limitedOPCountResult);
        extensionRegistry.add(Msgs.userNewsResult);
        
        extensionRegistry.add(Msgs.serviceMsgResult);
        extensionRegistry.add(Msgs.postbarMaxIndexResult);
        extensionRegistry.add(Msgs.pointEntityResult);
        extensionRegistry.add(Msgs.pointConfigDataResult);
        extensionRegistry.add(Msgs.pointTaskDataResult);
        extensionRegistry.add(Msgs.userPointDetailsResult);
        extensionRegistry.add(Msgs.guestRegisterResult);
        extensionRegistry.add(Msgs.goodsCategoryResult);
        extensionRegistry.add(Msgs.transDetailResult);
        extensionRegistry.add(Msgs.goodsResult);
        extensionRegistry.add(Msgs.goodsDetailResult);
        extensionRegistry.add(Msgs.orderDetailResult);
        extensionRegistry.add(Msgs.goods);
        extensionRegistry.add(Msgs.transSuccessResult);
        extensionRegistry.add(Msgs.userShareInfoResult);
        extensionRegistry.add(Msgs.applyMasterResult);
        extensionRegistry.add(Msgs.ids);
        extensionRegistry.add(Msgs.gameFollowResult);
        extensionRegistry.add(Msgs.pageDataResult);
        extensionRegistry.add(Msgs.activityInfo);
        extensionRegistry.add(Msgs.topicPraiseUsers);
        extensionRegistry.add(Msgs.postbarActionResult);
        extensionRegistry.add(Msgs.userGameServerResult);
        extensionRegistry.add(Msgs.gameRoleBindResult);
        extensionRegistry.add(Msgs.gameRole);
        extensionRegistry.add(Msgs.roleListResult);
        extensionRegistry.add(Msgs.playListResult);
        extensionRegistry.add(Msgs.playStarResult);
        extensionRegistry.add(Msgs.playInfoResult);
        extensionRegistry.add(Msgs.playOrderListResult);
        extensionRegistry.add(Msgs.playApplyOrderListResult);
        extensionRegistry.add(Msgs.playOrderInfo);
        extensionRegistry.add(Msgs.id);
        extensionRegistry.add(Msgs.playEvalListResult);
        extensionRegistry.add(Msgs.orderAppealResult);
        
        extensionRegistry.add(Msgs.youbiSchemeResult);
        extensionRegistry.add(Msgs.youbiDetailList);
        extensionRegistry.add(Msgs.youbiDetailInfo);
        extensionRegistry.add(Msgs.rechargeOrderResult);
        extensionRegistry.add(Msgs.gameSearchList);
    }

}
