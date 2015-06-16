/**      
 * MsgsBroadcastReceiver.java Create on 2013-7-30     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.utils.DataCollectHelp;
import com.iwgame.msgs.utils.GameUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GamePackageVo;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: MsgsBroadcastReceiver
 * @Description: 应用广播接受
 * @author 王卫
 * @date 2013-7-30 下午06:56:06
 * @Version 1.0
 * 
 */
public class MsgsBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "MsgsBroadcastReceiver";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String pname = intent.getDataString().split(":")[1];
		LogUtil.i(TAG, "------->MsgsBroadcastReceiver::onReceive:应用包名称：" + pname);
		dealAction(context, intent.getAction(), pname);
	}

	private void dealAction(Context context, String action, String pname) {
		if ((Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action))) {
			int actionType = 0;
			int opType = 0;
			if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
				actionType = DataCollectHelp.ACTION_PACKAGE_ADDED;
				opType = MsgsConstants.OP_APP_INSTALL;
				LogUtil.i(TAG, "------->你安装了应用" + pname);
			} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
				opType = MsgsConstants.OP_APP_UNINSTALL;
				actionType = DataCollectHelp.ACTION_PACKAGE_REMOVED;
				LogUtil.i(TAG, "------->你卸载了应用：" + pname);
			}
			if (opType != 0 && SystemContext.getInstance().getExtUserVo() != null) {
				GamePackageVo gamePackage = GameUtil.getGamePackage(pname);
				LogUtil.i(TAG, "------->你安装了或卸载了应用：" + pname + ", opType:" + opType);
				if (gamePackage != null) {
					// DataCollectHelp.saveActionData(context, actionType,
					// pname, System.currentTimeMillis());
					GameUtil.userAction(gamePackage.getGameid(), MsgsConstants.OT_GAME, opType);
					if (opType == DataCollectHelp.ACTION_PACKAGE_ADDED)
						followGame(gamePackage.getGameid(), MsgsConstants.OP_FOLLOW);
				}
			}
		}
	}

	/**
	 * 添加或取消关注
	 * 
	 * @param v
	 * @param gid
	 * @param op
	 */
	private void followGame(long gid, int op) {
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GAME, null);
					break;
				default:
					break;
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
			}
		}, null, gid, MsgsConstants.OT_GAME, op, null, null,null);
	}
}
