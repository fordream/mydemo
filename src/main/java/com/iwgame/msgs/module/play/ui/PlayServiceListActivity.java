package com.iwgame.msgs.module.play.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.play.ui.PlayServiceListActivity.ServerAdapter;
import com.iwgame.msgs.proto.Msgs.GameKey;
import com.iwgame.msgs.proto.Msgs.GameKeysDetail;
import com.iwgame.msgs.proto.Msgs.GameRole;
import com.iwgame.msgs.proto.Msgs.GameServerEntry;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

public class PlayServiceListActivity extends BaseActivity implements OnClickListener,OnCheckedChangeListener,OnFocusChangeListener{
		
	private TextView rightText;
	private EditText searchServer;
	private CheckBox chooseall;
	private TextView approve;
	private ListView listserver;
	private GameRole game;
	private List<GameKeysDetail> keylist = new ArrayList<GameKeysDetail>();
	private List<Item> filterList = new ArrayList<Item>();
	private PlayInfo playinfo;
	private List<GameServerEntry> serverlist = new ArrayList<GameServerEntry>();
	private List<Long>choosed = new ArrayList<Long>();
	private boolean TOTAL = false;
	private ServerAdapter adapter;
	private List<Item> itemList = new ArrayList<Item>();
	private List<Long > moreChoose = new ArrayList<Long>();
	private boolean able;
	private long sid;
	private String sids;
	private String content;
	private boolean MORE =false;
	private Button cleanBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getData();
		init();
	}

	private void getData() {
	game = (GameRole) getIntent().getExtras().get("game");
	playinfo=(PlayInfo)getIntent().getExtras().get("playinfo");
	sids =getIntent().getExtras().getString("sidss");
	sid = getIntent().getLongExtra("sid",0);
	if(game!=null){
	List<GameKey>list=	game.getGameKeyListList();
	for(GameKey key:list){
		if(key.getAttrType()==MsgsConstants.GAME_ROLE_KEY_SERVER){
			keylist.addAll(key.getGameKeysDetailListList());
				}
			}
		}
	if(playinfo!=null){
		serverlist.addAll(playinfo.getGameServerList());
		}
	setMoreChoose();
	setItemList(keylist);
	
	}
	private void setMoreChoose(){
		moreChoose.add(sid);
		if(sids==null||sids.equals(""))return;
		String[] arry = sids.split("\\,");
		if(arry==null||arry.length==0) return;
		for(int i=0;i<arry.length;i++){
			if(arry[i].equals("")) return;
			if(arry[i].equals("0")){
				able= false;
				MORE = true;
				return;
			}
			if(arry[i]!=null){
			long d =Long.parseLong(arry[i]);
			moreChoose.add(d);
			}
		}
	}
	
	private void setItemList(List<GameKeysDetail> list){
		if(list==null&&list.size()==0) return;
		filterList.clear();
	outer:	for(int i =0 ;i<list.size();i++){
		Item item	;
					if(!MORE){
					for(int j =0;j<moreChoose.size();j++){
						if(moreChoose.get(j)==list.get(i).getId()){
							 item = new Item(list.get(i), true);
							filterList.add(item);
							continue outer;
							}
						}
					item = new Item(list.get(i), false);
						}else{
					item = new Item(list.get(i), true);
					}
			filterList.add(item);
		}
	}
	
	private void init() {
		setTitleTxt("服务器");
		LinearLayout parentView = (LinearLayout)findViewById(R.id.contentView);
		View view = View.inflate(this, R.layout.user_play_choose_service, null);
		rightText =(TextView)findViewById(R.id.rightText);
		rightText.setText("完成");
		rightText.setVisibility(View.VISIBLE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		parentView.addView(view, params);
		searchServer =(EditText)view.findViewById(R.id.search_service);
		chooseall=(CheckBox)view.findViewById(R.id.play_choose_all);
		approve =(TextView)view.findViewById(R.id.approve_service);
		listserver=(ListView)view.findViewById(R.id.service_list);
		cleanBtn =(Button)view.findViewById(R.id.play_cleanBtn);
		searchServer.setOnFocusChangeListener(this);
		cleanBtn.setOnClickListener(this);
		itemList.addAll(filterList);
		adapter = new ServerAdapter();
		listserver.setAdapter(adapter);
		rightText.setOnClickListener(this);
		chooseall.setOnCheckedChangeListener(this);
		if(MORE){
			able = false;
			chooseall.setChecked(true);
		}else{
			able = true;
		}
		listserver.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				
				 	setChooseId();
					 Item item = itemList.get(position);
					
				 	if(!item.status){
				 		if(choosed.size()>2){
				 			enrollErrorDialog();
				 			return;
				 		}
				 	}
	                item.status = !item.status;// 取反  
	                initAdapter();  
//	                compareItem(item);
	               
			}
			
		});
		setEditListener();
	}
	/**
	 * 改变fiterList中的选中状态
	 */
	private void compareItem(){
		for(int i = 0;i<filterList.size();i++){
			for(int j = 0;j<itemList.size();j++){
				if(filterList.get(i).gb.getId()==itemList.get(j).gb.getId()){
					filterList.get(i).status=itemList.get(j).status;
				}
			}
		}
	}
	/**
	 * 输入框监听
	 */
	private void setEditListener(){
		searchServer.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				content = searchServer.getText().toString();
				if(content.length()>0){
					cleanBtn.setVisibility(View.VISIBLE);
				}else{
					cleanBtn.setVisibility(View.GONE);
				}
				filterServer(true);
				initAdapter();
			}
		});
	}
	/**
	 * 过滤服务器
	 * @param flag
	 */
	private void filterServer(boolean flag){
		if(filterList==null&&filterList.size()<=0) return;
		if(flag){
		compareItem();
		}
		itemList.clear();
		for(int i =0;i<filterList.size();i++){
			Item it = filterList.get(i);
			if(content ==null){
				itemList.add(it);
			}
			else if(it.gb.getContent().contains(content)){
				itemList.add(it);
			}
		}
	}
	/**
	 * 获取选中sid
	 */
	private void setChooseId(){
		choosed.clear();
		outer:for(int i = 0;i<filterList.size();i++){
					for(int j=0;j<itemList.size();j++){
						if(filterList.get(i).gb.getId()==itemList.get(j).gb.getId()){	
								if(itemList.get(j).status){
									choosed.add(filterList.get(i).gb.getId());
									continue outer;
										}
									}
								}
			if(filterList.get(i).status){
				choosed.add(filterList.get(i).gb.getId());
			}
		}
	}
	private void initAdapter(){
		if(adapter==null){
			adapter = new ServerAdapter();
		}
		else {  
            adapter.notifyDataSetChanged();  
        }  
  
        int size = itemList.size();  
        choosed.clear();  
        for (int i = 0; i < size; i++) {  
            if (itemList.get(i).status)  
                     choosed.add(itemList.get(i).gb.getId());
            else                    
               /*为false时的事件*/    
               choosed.remove(itemList.get(i).gb.getId());
        }  
	}
	class ServerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return itemList.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if(convertView==null){
				convertView=LayoutInflater.from(PlayServiceListActivity.this).inflate(R.layout.play_choose_server_item, null);
				holder = new ViewHolder();
				holder.text=(TextView)convertView.findViewById(R.id.server_name);
				holder.box=(CheckBox)convertView.findViewById(R.id.server_check);
				holder.mr=(TextView)convertView.findViewById(R.id.server_mr);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			Item item = itemList.get(position);
			holder.text.setText(item.gb.getContent());
			holder.box.setChecked(item.status);
			if(item.gb.getId()==sid){
				isEnabled(position, false);
				holder.box.setVisibility(View.GONE);
				holder.mr.setVisibility(View.VISIBLE);
			}else{
				holder.mr.setVisibility(View.GONE);
				holder.box.setVisibility(View.VISIBLE);
			}
			return convertView;
		}
		public boolean areAllItemsEnabled(){
		return able;
		
		}
		 @Override  
		    public boolean isEnabled(int position) {  
		        return able;  
		    }  
		 public boolean isEnabled(int position ,boolean able){
			 return able;
		 }
	}
	class ViewHolder{
		TextView text;
		CheckBox box;
		TextView mr;
	}
	/* (non-Javadoc)
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton comView, boolean isChecked) {
		if(comView == chooseall){
			int size = filterList.size();
			if(isChecked){
				able = false;
				for (int i = 0; i < size; i++) {  
	                filterList.get(i).status = true; 
	               filterServer(false);
	            }  
			}else{
				able = true;
			outer:	for (int i = 0; i < size; i++) {  
							if(!MORE){
						for(int j=0;j<moreChoose.size();j++){
						if(filterList.get(i).gb.getId()==moreChoose.get(j)){
							  filterList.get(i).status = true;
				                filterServer(false);
				                continue outer;
									}
								}
							}
	                filterList.get(i).status = false;
	                filterServer(false);
	            }  
			}
			initAdapter();
		}
	}
	class Item {
		GameKeysDetail gb;
		boolean status ;
		public Item(GameKeysDetail gb,boolean status){
			this.gb = gb;
			this.status =status;
		}
	}
	
	private String  getServerName(){
		StringBuilder builder = new StringBuilder();
		if(choosed.size()==0) return "";
		for(int i =0 ;i<choosed.size();i++){
			if(choosed.get(i)==sid){
				choosed.remove(i);
			}
		}
		for(int i=0;i<keylist.size();i++){
			for(int j=0;j<choosed.size();j++){
				if(keylist.get(i).getId()==choosed.get(j)){
					if(j!=choosed.size()-1){
						builder.append(keylist.get(i).getContent()+",");
					}else{
						builder.append(keylist.get(i).getContent());
					}
				}
			}
		}
		return builder.toString();
	}
	private String getServerId(){
		//StringBuilder builder = new StringBuilder();
		String ret="";
		if(choosed.size()==0) return ret;
		for(int i =0 ;i<choosed.size();i++){
			if(choosed.get(i)==sid){
			}else{
				if(i!=choosed.size()-1){
					ret+= choosed.get(i)+",";
					//builder.append(choosed.get(i)+",");
				}else{
					ret+=choosed.get(i);
					//builder.append(choosed.get(i)+"");
				}
			}
		}
		LogUtil.d("morechoose", ret);
		return ret;
	}
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if(v==rightText){
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString(SystemConfig.CHOOSED_SERVER_IDS, getServerId());
			bundle.putString(SystemConfig.CHOOSED_SERVER_NAMES, getServerName());
			intent.putExtras(bundle);
			setResult(Activity.RESULT_OK, intent);
			 finish();
		}else if(v==cleanBtn){
			searchServer.setText("");
		}
	}
	/**
	 * 错误吗提示框
	 * @param text
	 */
	public void enrollErrorDialog(){
		final Dialog dialog = new Dialog(PlayServiceListActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("最多可选择3个服务器哦!");
		LinearLayout btnBttom = (LinearLayout)dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom =(LinearLayout)dialog.findViewById(R.id.text_bottom);
		Button commit = (Button)dialog.findViewById(R.id.role_commitBtn);
		Button cancle =(Button)dialog.findViewById(R.id.role_cannelBtn);
		btnBttom.setVisibility(View.GONE);
		textBttom.setVisibility(View.VISIBLE);
		dialog.show();
		textBttom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(v==searchServer){
			if(hasFocus){
			if(searchServer.getText().length()>0){
				cleanBtn.setVisibility(View.VISIBLE);
			}
			}else{
				cleanBtn.setVisibility(View.INVISIBLE);
			}
		}
	}
}
