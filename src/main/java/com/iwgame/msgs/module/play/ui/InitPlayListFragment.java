/**      
 * InitPlayListFragment.java Create on 2015-5-15     
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
 * @ClassName: InitPlayListFragment
 * @Description: 陪玩管理初始化管理
 * @author 王卫
 * @date 2015-5-15 下午2:56:32
 * @Version 1.0
 * 
 */
public class InitPlayListFragment extends PlayManageBaseListFragment {

	protected static final String TAG = "InitPlayListFragment";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.play.ui.PlayManageBaseListFragment#onCreateView
	 * (android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setData(MsgsConstants.PLAY_ORDER_STATUS_INIT + "", ((PlayManageActivity) getActivity()).getPid(), PlayManageListAdapter.INDEX_INIT,
				((PlayManageActivity) getActivity()).getPStatus(), ((PlayManageActivity) getActivity()).getUStatus(),
				((PlayManageActivity) getActivity()).getGameName());
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
