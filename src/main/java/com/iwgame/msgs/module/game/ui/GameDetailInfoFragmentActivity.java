/**      
 * GameDetailInfoFragmentActivity.java Create on 2014-4-18     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;
import com.iwgame.msgs.common.BaseFragmentActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.StrictModeWrapper;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.vo.local.GameVo;

/**
 * @ClassName: GameDetailInfoFragmentActivity
 * @Description: TODO(贴吧详情页)
 * @author chuanglong
 * @date 2014-4-18 下午3:03:08
 * @Version 1.0
 * 
 */
public class GameDetailInfoFragmentActivity extends BaseFragmentActivity {

    /**
     * 贴吧id
     */
    long gid;

    protected void onCreate(Bundle savedInstanceState) {
	StrictModeWrapper.init(this);
	super.onCreate(savedInstanceState);

	Intent tmpintent = this.getIntent();
	if (tmpintent != null) {
	    Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
	    if (tmpbundle != null) {
		gid = tmpbundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID);

	    }
	}
	init();
    }

    private void init() {
	// 设置显示top左边
	setLeftVisible(true);

	// 设置显示top右边
	setRightVisible(false);

	// 设置TOP的中间布局容器
	LinearLayout topcenter = (LinearLayout) findViewById(R.id.center);
	titleTxt = (TextView) topcenter.findViewById(R.id.titleTxt);

	GameDetailInfoFragment gameDetailInfoFragment = new GameDetailInfoFragment();

	Bundle bundle = new Bundle();
	bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gid);
	gameDetailInfoFragment.setArguments(bundle);
	FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
	fragmentTransaction.replace(R.id.contentView, gameDetailInfoFragment);
	// fragmentTransaction.addToBackStack(null);
	fragmentTransaction.commit();
	
	titleTxt.setText("游戏详情");
	
//	ProxyFactory.getInstance().getGameProxy().getGameInfo(new ProxyCallBack<GameVo>() {
//
//		@Override
//		public void onSuccess(GameVo result) {
//			if (result != null) {
//			    //titleTxt.setText(result.getGamename());
//			}
//		}
//
//		@Override
//		public void onFailure(Integer result,String resultMsg) {
//			// 获取贴吧赞和踩
//			
//		}
//	}, this, gid);

    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	MobclickAgent.onPause(this);
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	MobclickAgent.onResume(this);
    }
}
