/**      
 * GroupListView.java Create on 2014-1-22     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.iwgame.msgs.module.group.ui.AddGroupActivity;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.youban.msgs.R;

/**
 * @ClassName: GroupListView
 * @Description: TODO(...)
 * @author 王卫
 * @date 2014-1-22 下午5:29:42
 * @Version 1.0
 * 
 */
public class GroupListView extends CommonListView {

	private Context context;

	/**
	 * @param context
	 */
	public GroupListView(Context context) {
		super(context, View.GONE);
		this.context = context;
	}

	/**
	 * 设置添加公会
	 */
	public void setAddGroupUI() {
		showNullBgView();
		nullContent.removeAllViews();
		final LinearLayout view = (LinearLayout) View.inflate(getContext(), R.layout.fragment_group_content2, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		nullContent.addView(view, params);
		view.findViewById(R.id.addGroupBtn).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), AddGroupActivity.class);
				getContext().startActivity(intent);
			}
		});
	}
}
