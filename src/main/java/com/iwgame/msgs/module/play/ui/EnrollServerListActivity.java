/**      
* EnrollServerListActivity.java Create on 2015-5-15     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.play.ui;

import java.util.ArrayList;
import java.util.List;

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
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.proto.Msgs.GameKey;
import com.iwgame.msgs.proto.Msgs.GameKeysDetail;
import com.iwgame.msgs.proto.Msgs.GameServerEntry;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.utils.MsgsConstants;
import com.youban.msgs.R;

/** 
 * @ClassName: EnrollServerListActivity 
 * @Description: TODO(...) 
 * @author xingjianlong
 * @date 2015-5-15 下午2:50:31 
 * @Version 1.0
 * 
 */
public class EnrollServerListActivity extends BaseActivity {
	
	private PlayInfo info;
	private List <GameKeysDetail> serverlist;
	private LayoutInflater inflater;
	private View view;
	private ListView listView;
	private List<GameKeysDetail> keylist = new ArrayList<GameKeysDetail>();
	private String sids;
	private long sid;
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inital();
	}

	/**
	 * 
	 */
	private void inital() {
		info =(PlayInfo)getIntent().getSerializableExtra("playinfo");
		sid = info.getSid();
		sids =info.getSids();
		serverlist = new ArrayList<GameKeysDetail>();
		setTitleTxt("请选择服务器");
		inflater = LayoutInflater.from(this);
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view = View.inflate(this, R.layout.user_role_nature, null);
		contentView.addView(view,params);
		//setMoreServerList();
		getServer();
		listView = (ListView)view.findViewById(R.id.user_nature_info);
		listView.setAdapter(new ServerAdapter());
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				GameKeysDetail detail = serverlist.get(position);
					Intent  intent = new Intent();
				    intent.putExtra(SystemConfig.ENROLL_CHOOSED_SERVER_ID, detail.getId());
				    intent.putExtra(SystemConfig.ENROLL_CHOOSED_SERVER_NAME,detail.getContent());
				    setResult(Activity.RESULT_OK, intent);
				    finish();
			}
		});
	}
	private void getServer(){
		boolean MR = true;
		getServerList();
		if("0".equals(info.getSids())){
			serverlist.addAll(keylist);
			return;
		}
		if(info.getGameServerList()==null||info.getGameServerList().size()<=0) return;
		for(int i =0 ;i<info.getGameServerList().size();i++){
			GameServerEntry entry = info.getGameServer(i);
				for(GameKeysDetail detail:keylist){
					if(entry.getId()==detail.getId()){
						serverlist.add(detail);
					}else if(sid==detail.getId()&&MR){
						MR = false;
						serverlist.add(detail);
					}
				}
		}
	}
	
	private void getServerList(){
		List<GameKey>  game  = new ArrayList<GameKey>();
		game.addAll( info.getGameRole().getGameKeyListList());
		for(GameKey key :game){
			if(key.getAttrType()==MsgsConstants.GAME_ROLE_KEY_SERVER){
				keylist .addAll(key.getGameKeysDetailListList());
			}
		}
	}
	private void setMoreServerList(){
		boolean MR = true;
		getServerList();
		if(sids==null&&sids.equals("")) return;
		String[] array = sids.split("\\,");
		for(int i =0;i<array.length;i++){
			for(GameKeysDetail detail:keylist){
				if(Long.parseLong(array[i])==detail.getId()){
					serverlist.add(detail);
					break;
				}else if(sid==detail.getId()&&MR){
					MR = false;
					serverlist.add(detail);
				}
			}
		}
	}
class ServerAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return serverlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return serverlist.get(arg0);
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
			holder.text.setText(serverlist.get(position).getContent());
			return convertView;
		}
		
		class ViewHolder{
			TextView text;
		}
	}
}
