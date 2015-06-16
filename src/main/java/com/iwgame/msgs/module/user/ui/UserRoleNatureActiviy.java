package com.iwgame.msgs.module.user.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.proto.Msgs.GameKey;
import com.iwgame.msgs.proto.Msgs.GameKeysDetail;
import com.iwgame.msgs.vo.local.GameKeyVo;
import com.youban.msgs.R;

public class UserRoleNatureActiviy extends BaseActivity {
		
	private GameKeyVo vo;
	private GameKey key;
	private View view;
	private ListView listView;
	private LayoutInflater inflater;
	private List <GameKeysDetail> list = new ArrayList<GameKeysDetail>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initial();
	}

	private void initial() {
		// TODO Auto-generated method stub
		vo = (GameKeyVo) getIntent().getSerializableExtra("gamekey");
		key=(GameKey)getIntent().getSerializableExtra("key");
		if(vo==null&&key!=null){
			vo = new GameKeyVo();
			vo.setAttrType(key.getAttrType());
			vo.setGid(key.getGid());
			vo.setId(key.getId());
			vo.setList(key.getGameKeysDetailListList());
			vo.setName(key.getKeyName());
		}
		setTitleTxt(vo.getName());
		inflater = LayoutInflater.from(this);
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view = View.inflate(this, R.layout.user_role_nature, null);
		contentView.addView(view,params);
		listView = (ListView)view.findViewById(R.id.user_nature_info);
		list.addAll(vo.getList());
		listView.setAdapter(new NatureAdapter());
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
					Intent  intent = new Intent();
				    intent.putExtra("id", vo.getId());
				    intent.putExtra("name", vo.getName());
				    intent.putExtra("values",vo.getList().get(position).getContent());
				    intent.putExtra("key", vo.getList().get(position).getId());
				    setResult(Activity.RESULT_OK, intent);
				    finish();
			}
		});
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	class NatureAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
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
			holder.text.setText(list.get(position).getContent());
			return convertView;
		}
		
		class ViewHolder{
			TextView text;
		}
	}
}
