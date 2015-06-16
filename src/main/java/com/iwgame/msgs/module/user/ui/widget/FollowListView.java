/**      
 * FollowListView.java Create on 2014-1-22     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.ui.widget;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView.OnEditorActionListener;

import com.youban.msgs.R;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.FollowMenuVo;
import com.iwgame.msgs.module.group.ui.AddGroupActivity;
import com.iwgame.msgs.utils.EditTextUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: FollowListView
 * @Description: 关注列表
 * @author 王卫
 * @date 2014-1-22 下午2:28:24
 * @Version 1.0
 * 
 */
public class FollowListView extends CommonListView {

	protected static final String TAG = "FollowListView";

	public EditText searchTxt;

	public LinearLayout typeSelect;

	public TextView typeTxt;

	public String keyword = null;
	
	public GameVo selectGameVo;
	
	public long gid;//如果gid为0，表示关注了全部用户，如果为-1，表示关注了游伴用户，如果id为大于0，表示关注了对应游戏的用户

    private boolean flag = false;//这个参数表示的是是不是执行了userfragemnt里面的addview方法 
	
	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	
	/**
	 * @param context
	 */
	public FollowListView(Context context) {
		super(context, R.layout.fragment_user_content_follow, View.GONE);
		init();
	}

	private void init() {
		// 设置文本框输入监听功能
		searchTxt = (EditText) findViewById(R.id.searchTxt);
		searchTxt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					return true;
				}
				return false;
			}
		});
		setSearchChangeListener(searchTxt);
		// 设置清除按钮
		Button cleanBtn = (Button) findViewById(R.id.cleanBtn);
		// 设置文本输入框文本变化监听
		EditTextUtil.ChangeCleanTextButtonVisible(searchTxt, cleanBtn);
		cleanBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchTxt.setText("");
			}
		});

		typeSelect = (LinearLayout) findViewById(R.id.typeSelect);
		typeTxt = (TextView) findViewById(R.id.typeTxt);
		List<FollowMenuVo> list = AdaptiveAppContext.getInstance().getAppConfig().getMenuVoList();
		int size = list.size();
		FollowMenuVo vo;
        for(int i = 0; i < size; i ++){		
        	vo = list.get(i);
        	if(vo.getGid() == 0){
        		gid = vo.getGid();
        		typeTxt.setText(vo.getMenuName());
        	}
        }
	}

	/**
	 * 设置文本框输入监听功能
	 * 
	 * @param txt
	 */
	private void setSearchChangeListener(final EditText txt) {
		txt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				LogUtil.d(TAG, "--------获取通讯录好友数据::setSearchChangeListener：文本框内容s为=" + s.toString());
				LogUtil.d(TAG, "--------获取通讯录好友数据::setSearchChangeListener：文本框txt内容为=" + txt.getText().toString());
				if (txt.getText().length() == 0) {
					keyword = null;
				} else {
					keyword = txt.getText().toString();
				}
				if(flag){
					refreshList();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void cleanKeyWord() {
		keyword = null;
		searchTxt.setText("");
	}
	
	/**
	 * 设置添加公会
	 */
	public void setNoDataUI() {
		showNullBgView();
		nullContent.removeAllViews();
		final LinearLayout view = (LinearLayout) View.inflate(getContext(),R.layout.user_null_data_bg, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		nullContent.addView(view, params);
	}
}
