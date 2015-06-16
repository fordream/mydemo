/**      
* PlayTimeLongActivity.java Create on 2015-5-18     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.play.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.module.play.ui.EnrollServerListActivity.ServerAdapter;
import com.iwgame.msgs.module.play.ui.EnrollServerListActivity.ServerAdapter.ViewHolder;
import com.iwgame.msgs.proto.Msgs.GameServerEntry;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.youban.msgs.R;

/** 
 * @ClassName: PlayTimeLongActivity 
 * @Description: TODO(...) 
 * @author xingjianlong
 * @date 2015-5-18 下午6:13:09 
 * @Version 1.0
 * 
 */
public class PlayTimeLongActivity extends BaseActivity {
		
	private LayoutInflater inflater;
	private View view;
	private ListView listView;
	private String [] times=new String[]{"1小时","2小时","3小时","4小时","5小时","6小时","7小时","8小时","9小时","10小时","11小时","12小时"
			,"13小时","14小时","15小时","16小时","17小时","18小时","19小时","20小时","21小时","22小时","23小时"};
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inital();
	}
	private void inital() {
		setTitleTxt("陪玩时长");
		inflater = LayoutInflater.from(this);
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view = View.inflate(this, R.layout.user_role_nature, null);
		contentView.addView(view,params);
		listView = (ListView)view.findViewById(R.id.user_nature_info);
		listView.setAdapter(new ServerAdapter());
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
					Intent  intent = new Intent();
					intent.putExtra("position", position+1);
					intent.putExtra("hour", times[position]);
				    setResult(Activity.RESULT_OK, intent);
				    finish();
			}
		});
	}
class ServerAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return times.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return times[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder ;
			if(convertView==null){
				convertView = inflater.inflate(R.layout.user_nature_item, null);
				holder = new ViewHolder();
				holder.text = (TextView)convertView.findViewById(R.id.nature_text);
				convertView.setTag(holder);
			}else{
				holder =(ViewHolder) convertView.getTag();
			}
			holder.text.setText(times[position]);
			return convertView;
		}
		
		class ViewHolder{
			TextView text;
		}
	}
}
