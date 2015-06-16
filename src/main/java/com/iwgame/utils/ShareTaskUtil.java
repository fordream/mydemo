/**      
 * BitmapUtil.java Create on 2013-5-23     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */
package com.iwgame.utils;

import android.content.Context;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.utils.MsgsConstants;

/**
 * 
 * @ClassName: ShareTaskUtil 
 * @Description: TODO(...) 
 * @author Administrator
 * @date 2014-11-14 上午10:20:26 
 * @Version 1.0
 *
 */
public class ShareTaskUtil {
	private static String taskDescribe = "";//任务描述，用户日志打印
	
	/**
	 * 提交分享或邀请任务
	 * @param context 上下文
	 * @param TAG  页面标志
	 * @param tid  分享对象的ID
	 * @param ttype 分享对象的类型
	 * @param op  动作类型
 	 * @param content 分享平台类型
	 * @param taskDescribe 任务描述
	 * @param inviteName 被分享对象的名称（通讯录指被分享人的手机号码，新浪微博为微博好友名称）
	 */
	public static void makeShareTask(Context context, final String TAG, long tid, int ttype, int op, String plamType, String inviteName, String shortUrl){
		String content = null;
		taskDescribe = "";//任务描述，用户日志打印
		
		if(WechatMoments.NAME.equals(plamType)){//朋友圈
			content = 1 + "";
			taskDescribe = "分享到朋友圈";
		}else if(SinaWeibo.NAME.equals(plamType)){// 新浪微博
			content = 2 + "";
			taskDescribe = "分享到新浪微博";
		}else if(Wechat.NAME.equals(plamType)){// 微信好友
			if(op == MsgsConstants.OP_INVITE_FRIEND){//邀请
				taskDescribe = "邀请微信好友";
			}else{
				taskDescribe = "分享到微信好友";
				content = 3 + "";
			}
		}else if(TencentWeibo.NAME.equals(plamType)){// 腾讯微博
			content = 4 + "";
			taskDescribe = "分享到腾讯微博";
		}else if(QZone.NAME.equals(plamType)){// QQ空间
			content = 5 + "";
			taskDescribe = "分享到QQ空间";
		}else if(QQ.NAME.equals(plamType)){// QQ好友
			taskDescribe = "邀请QQ好友";
		}else if("Contactfriend".equals(plamType)){// 通讯录
			taskDescribe = "邀请通讯录好友";
			if(inviteName != null && !inviteName.isEmpty()){
				content = inviteName;//手机号码
			}else{//手机号码未传入
				LogUtil.e(TAG, "邀请通讯录好友为添加手机号码");
				return;
			}
		}
		if(op == MsgsConstants.OP_RECORD_SHARE && (content == null || content.isEmpty())){//分享时context值必须有
			LogUtil.e(TAG, "分享平台未知");
			return;
		}
		
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					LogUtil.e(TAG, taskDescribe + "提交成功");
					break;
				default:
					LogUtil.e(TAG, taskDescribe + "提交失败");
					break;
				}
			}
			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, taskDescribe + "提交失败");
			}
		}, context, tid, ttype, op, content, null, shortUrl);
	}
}
