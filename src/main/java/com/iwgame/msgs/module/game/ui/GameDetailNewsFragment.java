/**      
* GameNewsFragment.java Create on 2013-9-3     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.game.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iwgame.msgs.common.BaseFragment;
import com.youban.msgs.R;

/** 
 * @ClassName: GameNewsFragment 
 * @Description: 贴吧-动态
 * @author 王卫
 * @date 2013-9-3 下午04:58:11 
 * @Version 1.0
 * 
 */
public class GameDetailNewsFragment extends BaseFragment {
	private static final String TAG = "GameDetailNewsFragment";
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_game_detail_news, container, false);
		PTAG = TAG;
		return v;
	}
	
}
