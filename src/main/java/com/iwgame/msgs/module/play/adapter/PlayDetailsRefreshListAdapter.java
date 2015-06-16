/**      
 * DiscoverPlayListAdapter.java Create on 2015-5-8     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.adapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.game.logic.GameProxyImpl;
import com.iwgame.msgs.proto.Msgs.GameServerEntry;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

/**
 * @ClassName: DiscoverPlayListAdapter
 * @Description: 发现陪玩列表适配器
 * @author 王卫
 * @date 2015-5-8 下午3:30:31
 * @Version 1.0
 * 
 */
public class PlayDetailsRefreshListAdapter extends BaseAdapter {


	private Context context;
	private LayoutInflater inflater;
	private List<PlayInfo> data;
	
	private final int TYPE_DETAIL = 0;
	private final int TYPE_NODATA = 1;
	private int noDataHeight;//用于在发现页面当没有获取到数据的时候，显示默认图片的高度
	
	
	public int getNoDataHeight() {
		return noDataHeight;
	}

	public void setNoDataHeight(int noDataHeight) {
		this.noDataHeight = noDataHeight;
	}
	
	/**
	 * 
	 */
	public PlayDetailsRefreshListAdapter(Context context, List<PlayInfo> data) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
	}

	
	@Override
	public int getItemViewType(int position) {
		if(getItem(position) instanceof PlayInfo){
			return TYPE_DETAIL;
		}else{
			return TYPE_NODATA;
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if (data != null)
			return data.size();
		else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		if (data != null)
			return data.get(position);
		else
			return null;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		ViewHolder holder1 = new ViewHolder();
		NodateViewHolder nodataHolder = new NodateViewHolder();
		if (convertView == null) {
			switch (type) {
			case TYPE_DETAIL:
				holder1 = new ViewHolder();
				convertView = inflater.inflate(R.layout.play_details_listview_item, null);
				holder1.bigIcon = (ImageView) convertView.findViewById(R.id.play_details_item_imageview);
				holder1.gameIcon = (ImageView) convertView.findViewById(R.id.play_details_item_img_gameIcon);
				holder1.beizhu = (TextView) convertView.findViewById(R.id.play_details_item_tv_beizhu);
				holder1.serviceName = (TextView) convertView.findViewById(R.id.play_details_item_tv_service_name);
				holder1.playServersName = (TextView) convertView.findViewById(R.id.play_details_item_tv_playservices_name);
				holder1.price = (TextView) convertView.findViewById(R.id.play_details_item_tv_price);
				convertView.setTag(holder1);
				break;
			case TYPE_NODATA:
				LinearLayout v = (LinearLayout)View.inflate(SystemContext.getInstance().getContext(), R.layout.no_data_view_discover, null);
				convertView = v;
				nodataHolder.bg_layout = v;
				nodataHolder.bgIcon = (ImageView)convertView.findViewById(R.id.bgIcon);
				convertView.setTag(nodataHolder);
				break;
			}
		} else {
			switch (type) {
			case TYPE_DETAIL:
				holder1 = (ViewHolder) convertView.getTag();
				break;
			case TYPE_NODATA:
				nodataHolder = (NodateViewHolder)convertView.getTag();
				break;
			}
		}

		switch (type) {
		case TYPE_DETAIL:
			// 设置界面数据
			Object itemData = getItem(position);
			if (itemData instanceof PlayInfo)
				setItemDetail(holder1, (PlayInfo) itemData);
			break;
		case TYPE_NODATA:
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, noDataHeight);
			nodataHolder.bg_layout.setLayoutParams(params);
			nodataHolder.bgIcon.setBackgroundResource(R.drawable.common_no_seach_result);
			break;
		}
		return convertView;
	}

	/**
	 * 设置界面数据
	 * 
	 * @param holder
	 * @param playInfo
	 */
	private void setItemDetail(ViewHolder holder1, PlayInfo playInfo) {
		ImageViewUtil.showImage(holder1.bigIcon, playInfo.getResourceid(), R.drawable.postbar_thumbimg_default);
		setGameIconId(playInfo.getGid(), holder1);
		holder1.beizhu.setText(playInfo.getRemark());
		holder1.serviceName.setText(playInfo.getServername());
		if (playInfo.getSids() != null && playInfo.getGameServerList() != null) {
			if(playInfo.getSids().equals("0")){
				holder1.playServersName.setText("(支持全服)");
			}else{
				if(playInfo.getGameServerList().size()>0){
					holder1.playServersName.setText("("+ getServerList(playInfo.getGameServerList()) + ")");
				}
			}
		}
		holder1.price.setText(playInfo.getCost()+"U币");
	}
	


	

	private void setGameIconId(long gid,final ViewHolder holder) {
		GameProxyImpl.getInstance().getGameInfo(new ProxyCallBack<GameVo>() {
			
			@Override
			public void onSuccess(GameVo result) {
				ImageViewUtil.showImage(holder.gameIcon, result.getGamelogo(), R.drawable.postbar_thumbimg_default);
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				ImageViewUtil.showImage(holder.gameIcon,null, R.drawable.postbar_thumbimg_default);
			}
		}, context, gid);
	}

	private String getServerList(List<GameServerEntry> gameServerList) {
		StringBuffer serverNames = new StringBuffer("");
		for(GameServerEntry gameServer : gameServerList){
			serverNames.append(gameServer.getName());
			serverNames.append(",");
		}
		//去掉最后一个逗号
		if(serverNames.length()>0){
			serverNames.delete(serverNames.length()-1, serverNames.length());
		}
		return serverNames.toString().trim();
	}


	class ViewHolder {
		public ImageView bigIcon;
		public ImageView gameIcon;
		public TextView beizhu;
		public TextView serviceName;
		public TextView playServersName;
		public TextView price;
	}
	
	/** 
	* @ClassName: NodateViewHolder 
	* @Description: TODO：没有数据的时候的viewholder
	* @author 彭赞 
	* @date 2015-6-4 下午5:22:39 
	* @Version 1.0 
	*/
	class NodateViewHolder {
		LinearLayout bg_layout;
		ImageView bgIcon;
	}
	
	/** 
     * 半角转换为全角 
     * @param input 
     * @return 
     */  
    private String ToDBC(String input) {  
        char[] c = input.toCharArray();  
        for (int i = 0; i < c.length; i++) {  
            if (c[i] == 12288) {  
                c[i] = (char) 32;  
                continue;  
            }  
            if (c[i] > 65280 && c[i] < 65375)  
                c[i] = (char) (c[i] - 65248);  
        }  
        return new String(c);  
    }  
    
    /** 
     * 去除特殊字符或将所有中文标号替换为英文标号 
     * @param str 
     * @return 
     */  
    private String stringFilter(String str) {  
        str = str.replaceAll("【", "[").replaceAll("】", "]")  
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号  
        String regEx = "[『』]"; // 清除掉特殊字符  
        Pattern p = Pattern.compile(regEx);  
        Matcher m = p.matcher(str);  
        return m.replaceAll("").trim();  
    }  
    
    
}
