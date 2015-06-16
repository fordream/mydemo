/**      
 * BuildVoUtil.java Create on 2013-8-28     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.game.object.GameExtDataVo;
import com.iwgame.msgs.module.setting.vo.MessageSubjectRuleVo;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentExtDataResult.ContentExtData;
import com.iwgame.msgs.proto.Msgs.ContentListResult.MessageDetail;
import com.iwgame.msgs.proto.Msgs.ContentListResult.MessageRelateEntry;
import com.iwgame.msgs.proto.Msgs.ForwardInfo;
import com.iwgame.msgs.proto.Msgs.GameInfoDetail;
import com.iwgame.msgs.proto.Msgs.GameKey;
import com.iwgame.msgs.proto.Msgs.GamePackageInfoDetail;
import com.iwgame.msgs.proto.Msgs.MessageContentType;
import com.iwgame.msgs.proto.Msgs.PostbarTagResult.PostbarTag;
import com.iwgame.msgs.proto.Msgs.SyncMessage;
import com.iwgame.msgs.proto.Msgs.UserInfoDetail;
import com.iwgame.msgs.proto.Msgs.UserRoleData;
import com.iwgame.msgs.proto.Msgs.UserRoleDetail;
import com.iwgame.msgs.vo.local.ContentVo;
import com.iwgame.msgs.vo.local.GamePackageVo;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.TopicTagVo;
import com.iwgame.msgs.vo.local.UserRoleEntity;
import com.iwgame.msgs.vo.local.UserRoleVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.utils.LogUtil;

//import com.iwgame.msgs.proto.Msgs.NewsDetail;

/**
 * @ClassName: BuildVoUtil
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-8-28 下午03:23:51
 * @Version 1.0
 * 
 */
public class BuildVoUtil {

	private static final String TAG = "BuildVoUtil";

	/**
	 * 
	 * @param detail
	 * @return
	 */
	public static ExtUserVo buildExtUserVo(UserInfoDetail detail) {
		if (detail != null) {
			ExtUserVo extUserVo = new ExtUserVo();
			extUserVo.setSerial(detail.getSerial());
			extUserVo.setUserid(detail.getId());
			extUserVo.setJob(detail.getJob());
			extUserVo.setAge(detail.getAge());
			extUserVo.setMood(detail.getMood());
			extUserVo.setSex(detail.getSex());
			extUserVo.setCity(detail.getCity());
			extUserVo.setUsername(detail.getNickname());
			extUserVo.setAvatar(detail.getAvatar());
			extUserVo.setGrade(detail.getGrade());
			extUserVo.setDescription(detail.getDesc());
			extUserVo.setUpdatetime(detail.getUpdatetime());
			extUserVo.setGameTime(detail.getGametime());
			extUserVo.setLikeGameType(detail.getGametype());
			extUserVo.setWeibo(detail.getMicroblog());
			extUserVo.setMobile(detail.getPhoneNo());
			extUserVo.setWeiboName(detail.getMicroblogname());
			extUserVo.setIsGuest(detail.getIsGuest());
			extUserVo.setCreateTime(detail.getCreatetime());
			extUserVo.setAlbumCount(detail.getAlbumcount());
			extUserVo.setPoint(detail.getPoint());
			return extUserVo;
		} else {
			return null;
		}
	}
	
	public static UserRoleEntity buildUserRoleEntity(UserRoleDetail detail){
		if(detail!=null){
			UserRoleEntity roleEntity = new UserRoleEntity();
			roleEntity.setId(detail.getUid());
			roleEntity.setList(detail.getRoleList());
			return roleEntity;
		}else {
			return null;
		}
	}
	
	public static void buildUserRoleVo(UserRoleEntity roleEntity,List<UserRoleVo> list){
		if(roleEntity!=null){
			for(int i=0;i<roleEntity.getList().size();i++){
				long uid = roleEntity.getId();
					UserRoleVo vo = new UserRoleVo();
					UserRoleData data =roleEntity.getList().get(i);
					vo.setUid(uid);
					vo.setRoldId(data.getRoleid());
					vo.setAvatar(data.getAvatar());
					vo.setList(data.getAttrList());
					vo.setStatus(data.getStatus());
					vo.setGid(data.getGid());
					list.add(vo);
				
			
			}
		}
		return ;
	}
	/**
	 * 
	 * @param detail
	 * @return
	 */
	public static ExtUserVo buildExtUserVo(UserVo detail) {
		if (detail != null) {
			ExtUserVo extUserVo = new ExtUserVo();
			extUserVo.setSerial(detail.getSerial());
			extUserVo.setUserid(detail.getUserid());
			extUserVo.setAvatar(detail.getAvatar());
			extUserVo.setMood(detail.getMood());
			extUserVo.setSex(detail.getSex());
			extUserVo.setAge(detail.getAge());
			extUserVo.setCity(detail.getCity());
			extUserVo.setJob(detail.getJob());
			extUserVo.setGrade(detail.getGrade());
			extUserVo.setUsername(detail.getUsername());
			extUserVo.setRelPositive(detail.getRelPositive());
			extUserVo.setRelInverse(detail.getRelInverse());
			extUserVo.setDescription(detail.getDescription());
			extUserVo.setGameTime(detail.getGameTime());
			extUserVo.setLikeGameType(detail.getLikeGameType());
			extUserVo.setUpdatetime(detail.getUpdatetime());
			extUserVo.setWeibo(detail.getWeibo());
			extUserVo.setMobile(detail.getMobile());
			extUserVo.setRemarksName(detail.getRemarksName());
			extUserVo.setPoint(detail.getPoint());
			return extUserVo;
		} else {
			return null;
		}
	}

	public static UserVo UserInfoDetail2UserVo(UserInfoDetail obj) {
		UserVo vo = new UserVo();
		vo.setUserid(obj.getId());
		vo.setUsername(obj.getNickname());
		vo.setSerial(obj.getSerial());
		vo.setAvatar(obj.getAvatar());
		vo.setGrade(obj.getGrade());
		vo.setSex(obj.getSex());
		vo.setCity(obj.getCity());
		vo.setMood(obj.getMood());
		vo.setDescription(obj.getDesc());
		vo.setUpdatetime(obj.getUpdatetime());
		vo.setAge(obj.getAge());
		vo.setJob(obj.getJob());
		vo.setGameTime(obj.getGametime());
		vo.setLikeGameType(obj.getGametype());
		vo.setWeibo(obj.getMicroblog());
		vo.setMobile(obj.getPhoneNo());
		vo.setWeiboName(obj.getMicroblogname());
		return vo;
	}

	public static GameVo GameInfoDetail2GameVo(GameInfoDetail obj) {
		GameVo vo = new GameVo();
		vo.setGameid(obj.getGid());
		vo.setGamename(obj.getGameName());
		vo.setGamepackageid(obj.getDefaultgp());
		vo.setGamelogo(vo.getGamelogo());
		vo.setType(obj.getCategory());
		return vo;
	}

	public static GamePackageVo GamePackageDetail2GamePackageVo(GamePackageInfoDetail obj) {
		GamePackageVo vo = new GamePackageVo();
		vo.setPackageid(obj.getGpid());
		vo.setGameid(obj.getGid());
		vo.setPackagename(obj.getGamePackage());
		vo.setDownloadurl(obj.getDownloadUrl());
		vo.setType(obj.getCategory());
		vo.setDev(obj.getPublisher());
		vo.setVersion(obj.getGameVersion());
		vo.setFilesize(obj.getFileSize());
		vo.setDesc(obj.getDesc());
		vo.setScreenshot(obj.getGamePic());
		return vo;
	}

	/**
	 * SyncMessage 转 MessageVo
	 * 
	 * @param obj
	 * @param localUserId
	 *            本地登录用户的uid
	 * @param channelType
	 * @return
	 */
	public static MessageVo SyncMessage2MessageVo(SyncMessage obj, String channelType, long localUserId, List<MessageSubjectRuleVo> ruleList) {
		if (obj != null) {
			MessageVo vo = new MessageVo();
			vo.setSource(MessageVo.SOURCE_SERVER);
			vo.setChannelType(channelType);
			vo.setMsgId(obj.getMsgid());
			vo.setFromId(obj.getFrom().getId());
			vo.setFromDomain(obj.getFrom().getDomain());
			vo.setToId(obj.getTo().getId());
			vo.setToDomain(obj.getTo().getDomain());
			vo.setCategory(obj.getCategory());
			vo.setMsgIndex(obj.getMsgIndex());
			vo.setCurrnetApptype(SystemContext.APPTYPE);
			vo.setApptype(obj.getContent().getApptype());
			vo.setExt(obj.getContent().getExt());
			vo.setNotNotify(obj.getContent().getNotNotify() ? 1 : 0);
			vo.setEstimateop(obj.getContent().getEstimateop());
			vo.setEstimatetype(obj.getContent().getEstimatetype());
			LogUtil.d(TAG, "--->>>ext="+obj.getContent().getExt());
			String tmpresult = MessageSubjectRuleVo.ITEM_RESULT_TO;
			if (ruleList != null) {
				for (int i = 0; i < ruleList.size(); i++) {
					if (ruleList.get(i).getChannelType().equals(channelType) && ruleList.get(i).getCategory().equals(obj.getCategory())) {
						tmpresult = ruleList.get(i).getResult();
						break;
					}
				}
			}
			if (tmpresult.equals(MessageSubjectRuleVo.ITEM_RESULT_FROMORTO)) {
				if (obj.getFrom().getId() == localUserId && obj.getFrom().getDomain().equals(MsgsConstants.DOMAIN_USER)) {
					vo.setSubjectId(obj.getTo().getId());
					vo.setSubjectDomain(obj.getTo().getDomain());
				} else {
					vo.setSubjectId(obj.getFrom().getId());
					vo.setSubjectDomain(obj.getFrom().getDomain());
				}
			} else if (tmpresult.equals(MessageSubjectRuleVo.ITEM_RESULT_FROM)) {
				vo.setSubjectId(obj.getFrom().getId());
				vo.setSubjectDomain(obj.getFrom().getDomain());
			} else if (tmpresult.equals(MessageSubjectRuleVo.ITEM_RESULT_TO)) {
				vo.setSubjectId(obj.getTo().getId());
				vo.setSubjectDomain(obj.getTo().getDomain());
			} else {
				vo.setSubjectId(obj.getTo().getId());
				vo.setSubjectDomain(obj.getTo().getDomain());
			}

			vo.setContentType(obj.getContent().getContentType().getNumber());
			switch (obj.getContent().getContentType().getNumber()) {
			case MessageContentType.TEXT_VALUE:
				vo.setContent(obj.getContent().getText());
				break;
			case MessageContentType.HTML_TEXT_VALUE:
				vo.setContent(obj.getContent().getHtmltext());
				break;
			case MessageContentType.IMAGE_VALUE:
				break;
			case MessageContentType.VOICE_VALUE://
				vo.setContentBytes(obj.getContent().getVoice().toByteArray());
				break;
			case MessageContentType.MV_VALUE:// 先忽略
			case MessageContentType.FILE_VALUE:// 先忽略
				break;
			case MessageContentType.IMAGE_ID_REF_VALUE:
			case MessageContentType.VOICE_ID_REF_VALUE:
			case MessageContentType.MV_ID_REF_VALUE:
			case MessageContentType.FILE_ID_REF_VALUE:
				vo.setContent(obj.getContent().getResourceId());
				break;
			case MessageContentType.CONTENT_ID_VALUE:
				vo.setContent(Long.toString(obj.getContent().getContentId()));
				break;
			case MessageContentType.NEWS_TEXT_VALUE:
				vo.setContent(obj.getContent().getText());
				break;
			case MessageContentType.CARD_TEXT_VALUE:
				vo.setContent(obj.getContent().getText());
				break;
			}

			// summary有就直接写，如果没有的话，那么就拿content中的内容填充
			String summary = obj.getSummary();
			if (summary != null && !summary.trim().equals("")) {
				vo.setSummary(summary);
			} else {
				switch (obj.getContent().getContentType().getNumber()) {
				case MessageContentType.TEXT_VALUE:
					vo.setSummary(obj.getContent().getText());
					break;
				case MessageContentType.CARD_TEXT_VALUE:
					vo.setSummary(obj.getContent().getText());
					break;
				case MessageContentType.HTML_TEXT_VALUE:
					vo.setSummary(obj.getContent().getHtmltext());
					break;
				case MessageContentType.IMAGE_VALUE:
				case MessageContentType.IMAGE_ID_REF_VALUE:
					vo.setSummary("[图片]");
					break;
				case MessageContentType.VOICE_VALUE:
				case MessageContentType.VOICE_ID_REF_VALUE:
					vo.setSummary("[语音]");
					break;
				case MessageContentType.MV_VALUE:
				case MessageContentType.MV_ID_REF_VALUE:
					vo.setSummary("[视频]");
					break;
				case MessageContentType.FILE_VALUE:
				case MessageContentType.FILE_ID_REF_VALUE:
					vo.setSummary("[文件]");
					break;

				case MessageContentType.CONTENT_ID_VALUE:
					vo.setSummary("[内容编号]");
					break;
				case MessageContentType.NEWS_TEXT_VALUE:
					if (vo.getForwardType() == MsgsConstants.FORWARD_TYPE_POSTBAR) {
						summary = "[分享了帖子]";
					} else if (vo.getForwardType() == MsgsConstants.FORWARD_TYPE_GROUP) {
						summary = "[分享了公会]";
					} else if (vo.getForwardType() == MsgsConstants.FORWARD_TYPE_GAME) {
						summary = "[分享了贴吧]";
					} else if (vo.getForwardType() == MsgsConstants.FORWARD_TYPE_USER) {
						summary = "[分享了资料]";
					} else if (vo.getForwardType() == MsgsConstants.FORWARD_TYPE_GOODS) {
						summary = "[分享了商品]";
					} else {
						summary = "";
					}
					vo.setSummary(summary);
					break;
				default:
					vo.setSummary("");
				}

			}
			vo.setCreateTime(obj.getCreatedTime());
			vo.setPosition(obj.getPosition());
			vo.setReadStatus(MessageVo.READSTATUS_UNREAD);

			if (obj.getStaus() == MsgsConstants.MSG_STATUS_NORMAL) {
				vo.setStatus(MessageVo.STATUS_SENDSUCC);
			} else if (obj.getStaus() == MsgsConstants.MSG_STATUS_DELETED) {
				vo.setStatus(MessageVo.STATUS_DEL);
			} else if (obj.getStaus() == MsgsConstants.MSG_STATUS_FROMINVISIBLE && obj.getFrom().getId() == localUserId
					&& obj.getFrom().getDomain().equals(MsgsConstants.DOMAIN_USER)) {
				vo.setStatus(MessageVo.STATUS_DEL);
			} else {
				vo.setStatus(MessageVo.STATUS_SENDSUCC);
			}

			if (obj.hasForwardInfo()) {
				ForwardInfo forwardInfo = obj.getForwardInfo();
				vo.setForwardId(forwardInfo.getForwardId());
				vo.setForwardType(forwardInfo.getForwardType());
			}
			if (obj.getContent().hasApptype()) {
				UserVo cuvo = SystemContext.getInstance().getExtUserVo();
				if (cuvo != null && vo.getFromId() == cuvo.getUserid() && SystemContext.APPTYPE.equals(obj.getContent().getApptype())) {
					vo.setStatus(MessageVo.STATUS_DEL);
				} else if (vo.getFromId() == 0 && !SystemContext.APPTYPE.equals(obj.getContent().getApptype())) {
					vo.setStatus(MessageVo.STATUS_DEL);
				}
			}
			return vo;
		} else {
			return null;
		}
	}

	/**
	 * MessageDetail 转ContentVo
	 * 
	 * @param obj
	 * @return
	 */
	public static ContentVo MessageDetail2ContentVo(MessageDetail obj) {
		ContentVo ret = null;
		if (obj != null) {
			ret = new ContentVo();
			ret.setPublishingid(obj.getPublisher());
			ret.setContentid(obj.getId());
			ret.setContent(obj.getContent());
			ret.setCreateTime(obj.getCreatetime());
			ret.setIsPraise(obj.getIsPraise());

			if (obj.getType() == 0 && obj.getTypeId() == 0)// 代表是评论
			{

				ret.setType(MsgsConstants.OT_COMMENT);
				List<MessageRelateEntry> tmp = obj.getRelateEntryList();
				if (tmp.size() == 1) {
					// 只有父动态
					// OT_NEWS(3)或评论OT_COMMENT(4)
					if (tmp.get(0).getRelateType() == MsgsConstants.OT_NEWS) {
						ret.setParentid(tmp.get(0).getRelateId());
						ret.setParenttype(tmp.get(0).getRelateType());
						ret.setParentpublishingid(tmp.get(0).getPublisher());
						ret.setParentpublishingtype(MsgsConstants.DOMAIN_USER);
						ret.setAncestorId(tmp.get(0).getRelateId());
						ret.setAncestorType(tmp.get(0).getRelateType());
						ret.setAncestorpublishingid(tmp.get(0).getPublisher());
						ret.setAncestorpublishingtype(MsgsConstants.DOMAIN_USER);
					}

				} else if (tmp.size() == 2) {
					// 有父评论和祖先动态
					for (int i = 0; i < tmp.size(); i++) {
						if (tmp.get(i).getRelateType() == MsgsConstants.OT_NEWS) {
							ret.setAncestorId(tmp.get(i).getRelateId());
							ret.setAncestorType(tmp.get(i).getRelateType());
							ret.setAncestorpublishingid(tmp.get(i).getPublisher());
							ret.setAncestorpublishingtype(MsgsConstants.DOMAIN_USER);
						} else if (tmp.get(i).getRelateType() == MsgsConstants.OT_COMMENT) {
							ret.setParentid(tmp.get(i).getRelateId());
							ret.setParenttype(tmp.get(i).getRelateType());
							ret.setParentpublishingid(tmp.get(i).getPublisher());
							ret.setParentpublishingtype(MsgsConstants.DOMAIN_USER);
						}

					}
				}

			} else// 动态
			{
				ret.setType(MsgsConstants.OT_NEWS);
				ret.setParentid(obj.getTypeId());
				ret.setParenttype(obj.getType());
				ret.setAncestorId(obj.getTypeId());
				ret.setAncestorType(obj.getType());

			}

		}
		return ret;
	}

	// /**
	// * 动态详情转换
	// *
	// * @param obj
	// * @return
	// */
	// public static ContentVo NewsDetail2ContentVo(NewsDetail obj) {
	// ContentVo ret = null;
	// if (obj != null) {
	// ret = new ContentVo();
	// ret.setContentid(obj.getId());
	// ret.setContent(obj.getContent());
	// ret.setCreateTime(obj.getCreatetime());
	// ret.setType(MsgsConstants.OT_NEWS);
	// ret.setParentid(obj.getTypeId());
	// ret.setParenttype(obj.getType());
	// ret.setAncestorId(obj.getTypeId());
	// ret.setAncestorType(obj.getType());
	// ret.setPublishingid(obj.getPublisher());
	// ret.setIsPraise(obj.getIsPraise());
	// }
	// return ret;
	// }

	// /**
	// *
	// * @param obj
	// * @return
	// */
	// public static ContentVo CommentDetail2ContentVo(CommentDetail obj) {
	// ContentVo ret = null;
	// if (obj != null) {
	// ret = new ContentVo();
	// ret.setContentid(obj.getId());
	// ret.setContent(obj.getContent());
	// ret.setCreateTime(obj.getCreatetime());
	// ret.setType(MsgsConstants.OT_COMMENT);
	// List<CommentRelateEntry> tmp = obj.getEntriesList();
	// if (tmp.size() == 1) {
	// // 只有父动态
	// // OT_NEWS(3)或评论OT_COMMENT(4)
	// if (tmp.get(0).getRelateType() == MsgsConstants.OT_NEWS) {
	// ret.setParentid(tmp.get(0).getRelateId());
	// ret.setParenttype(tmp.get(0).getRelateType());
	// ret.setParentpublishingid(tmp.get(0).getPublisher());
	// ret.setParentpublishingtype(MsgsConstants.DOMAIN_USER);
	// ret.setAncestorId(tmp.get(0).getRelateId());
	// ret.setAncestorType(tmp.get(0).getRelateType());
	// ret.setAncestorpublishingid(tmp.get(0).getPublisher());
	// ret.setAncestorpublishingtype(MsgsConstants.DOMAIN_USER);
	// }
	//
	// } else if (tmp.size() == 2) {
	// // 有父评论和祖先动态
	// for (int i = 0; i < tmp.size(); i++) {
	// if (tmp.get(i).getRelateType() == MsgsConstants.OT_NEWS) {
	// ret.setAncestorId(tmp.get(i).getRelateId());
	// ret.setAncestorType(tmp.get(i).getRelateType());
	// ret.setAncestorpublishingid(tmp.get(i).getPublisher());
	// ret.setAncestorpublishingtype(MsgsConstants.DOMAIN_USER);
	// } else if (tmp.get(i).getRelateType() == MsgsConstants.OT_COMMENT) {
	// ret.setParentid(tmp.get(i).getRelateId());
	// ret.setParenttype(tmp.get(i).getRelateType());
	// ret.setParentpublishingid(tmp.get(i).getPublisher());
	// ret.setParentpublishingtype(MsgsConstants.DOMAIN_USER);
	// }
	//
	// }
	// }
	//
	// ret.setPublishingid(obj.getPublisher());
	// ret.setIsPraise(obj.getIsPraise());
	// }
	// return ret;
	// }

	/**
	 * 
	 * @param tag
	 * @return
	 */
	public static TopicTagVo PostbarTag2TTopicTagVo(PostbarTag tag) {
		TopicTagVo ret = null;
		if (tag != null) {
			ret = new TopicTagVo();
			ret.setId((int) tag.getId());
			ret.setName(tag.getTagName());
			ret.setAccess(tag.getTagAccess());
			ret.setSort(tag.getSort());
			ret.setTagDefault(tag.getTagDefault());
			ret.setTopicNums(tag.getTopicNums());
		}
		return ret;
	}

	/**
	 * ContentExtData 转 GameExtDataVo
	 * 
	 * @param obj
	 * @return
	 */
	public static GameExtDataVo ContentExtData2GameExtDataVo(ContentExtData obj) {
		GameExtDataVo ret = null;
		if (obj != null) {
			ret = new GameExtDataVo();
			if (obj.hasPraise())
				ret.setPraise(obj.getPraise());
			if (obj.hasCriticize())
				ret.setCriticize(obj.getCriticize());
			if (obj.hasComment())
				ret.setComment(obj.getComment());
			if (obj.hasIspraise())
				ret.setIspraise(obj.getIspraise());
			if (obj.hasCriticize())
				ret.setIscriticize(obj.getIscriticize());
			if (obj.hasGameExt()) {
				if (obj.getGameExt().hasFollowCount())
					ret.setFollowCount(obj.getGameExt().getFollowCount());
				if (obj.getGameExt().hasGroupCount())
					ret.setGroupCount(obj.getGameExt().getGroupCount());
				if (obj.getGameExt().hasNewsPostCount())
					ret.setNewsPostCount(obj.getGameExt().getNewsPostCount());
				if (obj.getGameExt().hasStrategyPostCount())
					ret.setStrategyPostCount(obj.getGameExt().getStrategyPostCount());
				if (obj.getGameExt().hasIsBarMaster()) {
					ret.setBarMaster(obj.getGameExt().getIsBarMaster());
				} else {
					ret.setBarMaster(false);
				}
			}

		}
		return ret;
	}
}
