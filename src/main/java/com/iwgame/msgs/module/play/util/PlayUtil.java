/**      
 * PlayUtil.java Create on 2015-5-15     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.util;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.proto.Msgs.GameServerEntry;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.proto.Msgs.UserRoleData;
import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr;
import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr.ValData;
import com.iwgame.msgs.utils.MsgsConstants;
import com.youban.msgs.R;

/**
 * @ClassName: PlayUtil
 * @Description: 陪玩工具菜单
 * @author 王卫
 * @date 2015-5-15 上午11:37:31
 * @Version 1.0
 * 
 */
public class PlayUtil {

	/**
	 * 添加所有陪玩列表或资料页中的陪玩属性view
	 * 
	 * @param playInfoContent
	 */
	public static void addAllPlayAttrView(Context context, LayoutInflater inflater, LinearLayout playInfoContent, PlayInfo playInfo) {
		addBasePlayAttrView(context, inflater, playInfoContent, playInfo, false);
		View view = inflater.inflate(R.layout.play_list_item_detail_item, playInfoContent, false);
		((TextView) view.findViewById(R.id.dname)).setText("价格");
		TextView descTxt = ((TextView) view.findViewById(R.id.desc));
		descTxt.setText(playInfo.getCost() + "U币/小时");
		descTxt.setTextColor(context.getResources().getColor(R.color.play_list_item_detail_desc_color2));
		playInfoContent.addView(view);
		view = inflater.inflate(R.layout.play_list_item_detail_item, playInfoContent, false);
		((TextView) view.findViewById(R.id.dname)).setText("备注");
		((TextView) view.findViewById(R.id.desc)).setText(playInfo.getRemark());
		playInfoContent.addView(view);
	}

	/**
	 * 添加基本陪玩列表或资料页中的陪玩属性view
	 * 
	 * @param playInfoContent
	 */
	public static void addBasePlayAttrView(Context context, LayoutInflater inflater, LinearLayout playInfoContent, PlayInfo playInfo,
			boolean showMultiple) {
		// 其他详情设置
		View view = inflater.inflate(R.layout.play_list_item_detail_item, playInfoContent, false);
		((TextView) view.findViewById(R.id.dname)).setText("游戏");
		((TextView) view.findViewById(R.id.desc)).setText(playInfo.getGamename());
		playInfoContent.addView(view);
		view = inflater.inflate(R.layout.play_list_item_detail_item, playInfoContent, false);
		((TextView) view.findViewById(R.id.dname)).setText("服务器");
		((TextView) view.findViewById(R.id.desc)).setText(playInfo.getServername());

		TextView extLeftTxt = (TextView) view.findViewById(R.id.extLeftTxt);
		TextView extTxt = (TextView) view.findViewById(R.id.extTxt);
		TextView extRightTxt = (TextView) view.findViewById(R.id.extRightTxt);
		List<GameServerEntry> gsentrys = playInfo.getGameServerList();
		extLeftTxt.setTextColor(context.getResources().getColor(R.color.play_list_item_detail_desc_color));
		extTxt.setTextColor(context.getResources().getColor(R.color.play_list_item_detail_desc_color));
		extRightTxt.setTextColor(context.getResources().getColor(R.color.play_list_item_detail_desc_color));
		if (gsentrys.size() > 0) {
			extLeftTxt.setVisibility(View.VISIBLE);
			extTxt.setVisibility(View.VISIBLE);
			extRightTxt.setVisibility(View.VISIBLE);
			StringBuffer sb = new StringBuffer();
			if ("0".equals(playInfo.getSids())) {// 支持全服
				sb.append("支持全服");
			} else {
				if (gsentrys != null) {// 多服务器
					for (int i = 0, size = gsentrys.size(); i < size; i++) {
						if (i > 0)
							sb.append(",");
						GameServerEntry entry = gsentrys.get(i);
						sb.append(entry.getName());
					}
				}
			}
			extTxt.setText(sb.toString());
			if (showMultiple) {
				extLeftTxt.setTextColor(context.getResources().getColor(R.color.play_list_item_service_detail_desc_color));
				extTxt.setTextColor(context.getResources().getColor(R.color.play_list_item_service_detail_desc_color));
				extRightTxt.setTextColor(context.getResources().getColor(R.color.play_list_item_service_detail_desc_color));
			}
		} else {
			extLeftTxt.setVisibility(View.GONE);
			extTxt.setVisibility(View.GONE);
			extRightTxt.setVisibility(View.GONE);
		}
		playInfoContent.addView(view);
		TextView roleDesc = null;
		if (showMultiple) {
			view = inflater.inflate(R.layout.play_list_item_detail_item, playInfoContent, false);
			((TextView) view.findViewById(R.id.dname)).setText("角色");
			roleDesc = ((TextView) view.findViewById(R.id.desc));
			playInfoContent.addView(view);
		}
		UserRoleData roleData = playInfo.getRoleData();
		int size = roleData.getAttrCount();
		if (roleData != null && size > 0) {
			List<RoleAttr> roleList = roleData.getAttrList();
			for (int i = 0; i < size; i++) {
				RoleAttr attr = roleList.get(i);
				if (attr.getAttrtype() != MsgsConstants.GAME_ROLE_KEY_SERVER) {
					if (showMultiple) {
						if (attr.getAttrtype() == MsgsConstants.GAME_ROLE_KEY_USER) {
							if (roleDesc != null)
								roleDesc.setText(attr.getContent());
						} else {
							addAttrItem(inflater, playInfoContent, attr);
						}
					} else {
						if (attr.getAttrtype() == 5) {
							addAttrItem(inflater, playInfoContent, attr);
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param inflater
	 * @param playInfoContent
	 * @param attr
	 */
	public static void addAttrItem(LayoutInflater inflater, LinearLayout playInfoContent, RoleAttr attr) {
		View view = inflater.inflate(R.layout.play_list_item_detail_item, playInfoContent, false);
		((TextView) view.findViewById(R.id.dname)).setText(attr.getKey());
		if (!attr.getContent().isEmpty()) {
			((TextView) view.findViewById(R.id.desc)).setText(attr.getContent());
		} else {
			if (attr.getValDataList() != null) {
				int vdsize = attr.getValDataCount();
				String desc = "";
				List<ValData> valList = attr.getValDataList();
				for (int j = 0; j < vdsize; j++) {
					ValData val = valList.get(j);
					if (j == vdsize - 1) {
						desc += val.getVal() + "";
					} else {
						desc += val.getVal() + ",";
					}
				}
				((TextView) view.findViewById(R.id.desc)).setText(desc);
			}
		}
		playInfoContent.addView(view);
	}

}
