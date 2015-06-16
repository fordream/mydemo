/**      
 * InitPlayListAdapter.java Create on 2015-5-15     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.proto.Msgs.PlayApplyOrderInfo;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: PlayManageListAdapter
 * @Description: 陪玩列表适配器
 * @author 王卫
 * @date 2015-5-15 下午3:55:30
 * @Version 1.0
 * 
 */
public class PlayManageListAdapter extends BaseAdapter {

	public static final int INDEX_INIT = 0;
	public static final int INDEX_INCOMPLETE = 1;
	public static final int INDEX_END = 2;

	private Context context;
	private LayoutInflater inflater;
	private List<Object> data;
	private int index;
	// 陪玩状态和玩家封停状态
	private int pStatus;
	private int uStatus;
	private String gameName = "";

	/**
	 * 
	 */
	public PlayManageListAdapter(Context context, List<Object> data, int index, int pStatus, int uStatus, String gameName) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
		this.index = index;
		this.pStatus = pStatus;
		this.uStatus = uStatus;
		this.gameName = gameName;
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
	 * android.view.ViewGroup)O
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.play_manage_list_item, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.nickNameTxt = (TextView) convertView.findViewById(R.id.nickNameTxt);
			holder.applyTimeTxt = (TextView) convertView.findViewById(R.id.applyTimeTxt);
			holder.timeTxT = (TextView) convertView.findViewById(R.id.timeTxt);
			holder.statusTxt = (TextView) convertView.findViewById(R.id.statusTxt);
			holder.statusBtn = (Button) convertView.findViewById(R.id.statusBtn);
			holder.statusBtn2 = (Button) convertView.findViewById(R.id.statusBtn2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final PlayApplyOrderInfo info = (PlayApplyOrderInfo) getItem(position);
		holder.statusTxt.setVisibility(View.GONE);
		holder.statusBtn.setVisibility(View.GONE);
		holder.statusBtn2.setVisibility(View.GONE);
		// 复用清空之前数据
		holder.nickNameTxt.setText("");
		holder.applyTimeTxt.setText("");
		holder.timeTxT.setText("");
		holder.statusTxt.setText("");
		if (index == INDEX_INIT) {
			holder.statusBtn.setVisibility(View.VISIBLE);
			holder.statusBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (pStatus == MsgsConstants.PLAY_STATUS_CLOSED) {// 已关闭
						ToastUtil.showToast(context, "你创建的" + gameName + "陪玩存在违规，已被关闭，去联系管理员");
						return;
					} else if (uStatus == MsgsConstants.USER_PLAY_STATUS_STOPED) {// 已封停
						ToastUtil.showToast(context, "由于你违规操作，你创建的陪玩功能已被封停，去联系去联系管理员");
						return;
					}
					// 确认陪玩
					ProxyFactory.getInstance().getPlayProxy().acceptOrder(new ProxyCallBack<Integer>() {

						@Override
						public void onSuccess(Integer result) {
							ToastUtil.showToast(context, "确认成功");
							holder.statusBtn.setVisibility(View.GONE);
							holder.statusBtn2.setVisibility(View.VISIBLE);
							holder.statusBtn2.setText("已确认");
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							ToastUtil.showToast(context, "确认失败");
						}
					}, context, info.getOrderid());
				}
			});
		} else if (index == INDEX_INCOMPLETE) {
			holder.statusTxt.setVisibility(View.VISIBLE);
			if (info.getStatus() == MsgsConstants.PLAY_ORDER_STATUS_PAY) {
				holder.statusTxt.setText("等待对方\n确认付款");
			} else if (info.getStatus() == MsgsConstants.PLAY_ORDER_STATUS_APPEAL) {
				holder.statusTxt.setText("对方\n申诉中");
			}
		} else if (index == INDEX_END) {
			holder.statusBtn2.setVisibility(View.VISIBLE);
			if (info.getStatus() == MsgsConstants.PLAY_ORDER_STATUS_END) {
				holder.statusBtn2.setText("游戏结束");
			} else if (info.getStatus() == MsgsConstants.PLAY_ORDER_STATUS_EVAL) {
				holder.statusBtn2.setText("待评价");
			} else if (info.getStatus() == MsgsConstants.PLAY_ORDER_STATUS_TIMEOUT) {
				holder.statusBtn2.setText("已过期");
			}
		}
		// 设置图片
		new ImageLoader().loadRes(ResUtil.getOriginalRelUrl(info.getAvatar()), 0, holder.icon, R.drawable.common_default_icon);
		holder.nickNameTxt.setText(info.getNickname());
		holder.applyTimeTxt.setText((new SimpleDateFormat("MM月dd日 HH:mm").format(new Date(info.getStarttime()))));
		holder.timeTxT.setText("时长：" + info.getDuration() + "小时");
		return convertView;
	}

	class ViewHolder {

		ImageView icon;
		TextView nickNameTxt;
		TextView applyTimeTxt;
		TextView timeTxT;
		TextView statusTxt;
		Button statusBtn;
		Button statusBtn2;

	}

}
