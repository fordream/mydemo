/**      
 * PlayManageInitListFragment.java Create on 2015-5-15     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iwgame.msgs.module.play.adapter.PlayManageListAdapter;
import com.iwgame.msgs.utils.MsgsConstants;

/**
 * @ClassName: PlayManageInitListFragment
 * @Description: 陪玩管理已结束管理
 * @author 王卫
 * @date 2015-5-15 下午2:48:24
 * @Version 1.0
 * 
 */
public class ClosedPlayListFragment extends PlayManageBaseListFragment {

	protected static final String TAG = "ClosedPlayListFragment";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.play.ui.PlayManageBaseListFragment#onCreateView
	 * (android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setData(MsgsConstants.PLAY_ORDER_STATUS_END + "," + MsgsConstants.PLAY_ORDER_STATUS_EVAL + "," + MsgsConstants.PLAY_ORDER_STATUS_TIMEOUT,
				((PlayManageActivity) getActivity()).getPid(), PlayManageListAdapter.INDEX_END, ((PlayManageActivity) getActivity()).getPStatus(),
				((PlayManageActivity) getActivity()).getUStatus(), ((PlayManageActivity) getActivity()).getGameName());
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
