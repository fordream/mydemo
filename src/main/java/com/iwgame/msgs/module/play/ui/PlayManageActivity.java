/**      
 * PlayManageActivity.java Create on 2015-5-15     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.ui;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseSuperFragmentActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.chat.adapter.MyTabsAdapter;
import com.iwgame.msgs.module.chat.ui.ParentViewPager;
import com.iwgame.msgs.module.play.adapter.PlayManageListAdapter;
import com.iwgame.msgs.utils.MsgsConstants;
import com.youban.msgs.R;

/**
 * @ClassName: PlayManageActivity
 * @Description: 陪玩管理
 * @author 王卫
 * @date 2015-5-15 下午2:29:56
 * @Version 1.0
 * 
 */
public class PlayManageActivity extends BaseSuperFragmentActivity {

	// 定义数组来存放Fragment界面
	private Class mFragmentArray[] = null;
	// Tab选项卡的文字
	private String mTabTextArray[] = null;
	// 定义TabHost对象
	private TabHost mTabHost;
	private ParentViewPager mViewPager;
	private MyTabsAdapter mTabsAdapter;

	private LayoutInflater layoutInflater;
	private Map<Integer, TextView> tablist = new HashMap<Integer, TextView>();

	private long pid;
	// 要获取数据的订单状态
	private int status;
	// 陪玩状态和玩家封停状态
	private int pStatus;
	private int uStatus;
	private String gameName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			pid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PID);
			status = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_STATUS);
			pStatus = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PSTATUS);
			uStatus = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_USTATUS);
			gameName = bundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME);
		}
		// 实例化布局对象
		layoutInflater = LayoutInflater.from(this);
		init();
	}

	/**
		 * 
		 */
	private void init() {
		setContentView(R.layout.common_content);
		// 设置Top中间标题
		((TextView) findViewById(R.id.titleTxt)).setText(getResources().getString(R.string.play_manage_title));
		// 定义数组来存放Fragment界面
		mFragmentArray = new Class[] { InitPlayListFragment.class, ConfirmedPlayListFragment.class, ClosedPlayListFragment.class };
		// Tab选项卡的文字
		mTabTextArray = new String[] { getString(R.string.play_manage_list_tab_init), getString(R.string.play_manage_list_tab_confirmed),
				getString(R.string.play_manage_list_tab_close) };
		// 设置Top左边返回功能
		Button backBtn = (Button) findViewById(R.id.leftBtn);
		if (backBtn != null) {
			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					PlayManageActivity.this.finish();
				}
			});
		}
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		contentView.addView(View.inflate(this, R.layout.play_list_main, null));
		// 实例化TabHost对象，得到TabHost
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ParentViewPager) findViewById(R.id.pager);
		mTabsAdapter = new MyTabsAdapter(this, mTabHost, mViewPager, null);
		// 得到fragment的个数
		int count = mFragmentArray.length;
		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTabTextArray[i]).setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			Bundle bundle = new Bundle();
			mTabsAdapter.addTab(tabSpec, mFragmentArray[i], bundle);
		}
		mTabHost.setCurrentTabByTag(mTabTextArray[0]);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int arg0) {
				mTabsAdapter.onPageSelected(arg0);
			}

		});
		if (status <= 0)
			return;
		if (status == MsgsConstants.PLAY_ORDER_STATUS_INIT) {
			mViewPager.setCurrentItem(0);
		} else if (status == MsgsConstants.PLAY_ORDER_STATUS_PAY) {
			mViewPager.setCurrentItem(1);
		} else if (status == MsgsConstants.PLAY_ORDER_STATUS_END) {
			mViewPager.setCurrentItem(2);
		}
	}

	/**
	 * 根据索引值获取界面
	 * 
	 * @param index
	 * @return
	 */
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.common_sub_tab_item_view, null);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTabTextArray[index]);
		if (tablist != null)
			tablist.put(index, textView);
		return view;
	}

	/**
	 * 设置列表选项卡的文本
	 */
	public void setTextView(int count, int index) {
		if (tablist != null) {
			String title = "";
			if (index == PlayManageListAdapter.INDEX_INIT) {
				title = count > 0 ? ("待确认(" + count + ")") : "待确认";
			} else if (index == PlayManageListAdapter.INDEX_INCOMPLETE) {
				title = count > 0 ? ("已确认(" + count + ")") : "已确认";
			} else if (index == PlayManageListAdapter.INDEX_END) {
				title = count > 0 ? ("已结束(" + count + ")") : "已结束";
			}
			tablist.get(index).setText(title);
		}
	}

	/**
	 * 获取陪玩id
	 */
	public long getPid() {
		return pid;
	}

	public int getPStatus() {
		return pStatus;
	}

	public int getUStatus() {
		return uStatus;
	}

	public String getGameName() {
		return gameName == null ? "" : gameName;
	}
}
