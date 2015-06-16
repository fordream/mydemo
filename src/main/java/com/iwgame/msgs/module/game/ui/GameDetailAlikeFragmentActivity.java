/**      
* GameDetailAlikeFragmentActivity.java Create on 2014-4-18     
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
import com.iwgame.msgs.config.SystemConfig;

/** 
 * @ClassName: GameDetailAlikeFragmentActivity 
 * @Description: TODO(游伴) 
 * @author chuanglong
 * @date 2014-4-18 下午3:05:53 
 * @Version 1.0
 * 
 */
public class GameDetailAlikeFragmentActivity extends BaseFragmentActivity {
    private long gid = 0 ;
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle arg0) {
	// TODO Auto-generated method stub
	super.onCreate(arg0);
	// 获得传入的参数
	Intent tmpintent = this.getIntent();
	if (tmpintent != null) {
	    Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
	    if (tmpbundle != null) {
		 gid = tmpbundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID ,0) ;
	    }
	}
	init();
    }

    private void init() {

	// 设置显示top左边
	setLeftVisible(true);
	// 设置包显示top右边
	setRightVisible(false);

	// 设置TOP的中间布局容器
	LinearLayout topcenter = (LinearLayout) findViewById(R.id.center);
	titleTxt = (TextView) topcenter.findViewById(R.id.titleTxt);
	titleTxt.setText("游伴");
	// 设置中间内容
	GameDetailAlikeFragment fragment = new GameDetailAlikeFragment();
	Bundle bundle = new Bundle();
	bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID,gid);
	fragment.setArguments(bundle);
	FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
	fragmentTransaction.replace(R.id.contentView, fragment);
	fragmentTransaction.commit();

    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	MobclickAgent.onPause(this);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	MobclickAgent.onResume(this);
    }
}
