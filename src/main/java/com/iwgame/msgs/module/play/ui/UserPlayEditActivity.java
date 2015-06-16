package com.iwgame.msgs.module.play.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.play.object.PlayVo;
import com.iwgame.msgs.module.user.object.UserRoleAttr;
import com.iwgame.msgs.module.user.ui.UserRoleDetailActivity;
import com.iwgame.msgs.module.user.ui.UserRoleNatureActiviy;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.GameKey;
import com.iwgame.msgs.proto.Msgs.GameKeysDetail;
import com.iwgame.msgs.proto.Msgs.GameRole;
import com.iwgame.msgs.proto.Msgs.GameServerEntry;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.proto.Msgs.PlayStar;
import com.iwgame.msgs.proto.Msgs.UserRoleData;
import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr;
import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr.ValData;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.PhotoUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.UserRoleVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

public class UserPlayEditActivity extends BaseActivity implements
		OnClickListener {
	private static int VALUES_ERROR =-100;
	private static int NO_CAN_EDIT=500601;
	private static int PLAY_ALREADY_EXISTS=500602;
	private static int DAY_CAN_ONCE=500603;
	private static int GAME_PLAY_NO_EXIST=500604;
	private static int EC_MSGS_NOT_SAME_PLAY_GAME = 500605;//陪玩角色不属于所选游戏
	private static int EC_MSGS_USERPLAY_CLOSED = 500606;//陪玩业务已封停
	private static int EC_MSGS_PLAY_STATUS_CLOSED = 500607;//陪玩商品被oa关闭
	private LinearLayout chooseGame;
	private ImageView gameIcon;
	private TextView gameRole;
	private EditText playPay;
	private ImageView addGame;
	private EditText remark;
	private EditText qqEdit;
	private EditText phoneEdit;
	private ImageView playImage;
	private ImageView deleteImage;
	private CheckBox protocolImage;
	private TextView gameService;
	private Button compeltePlay;
	private TextView protocolText;
	private RelativeLayout addService;
	private TextView moreService;
	private long playid;
	public Map<Long, UserRoleAttr> attrMap = new HashMap<Long, UserRoleAttr>();
	private Map<Long, View> map = new HashMap<Long, View>();
	private List<ValData> list = new ArrayList<ValData>();
	private List<ValData> exitVal = new ArrayList<ValData>();
	private Map<Long, TextView> choiceMap = new HashMap<Long, TextView>();
	private long gid;
	private byte[] image;
	private UserRoleData userRole;
	private UserRoleVo vo;
	public GameRole game;
	public UserRoleData userData;
	List<GameServerEntry> serverlist;
	private PlayInfo playinfo;
	private boolean Image = true;
	private Uri imageUri;
	private Bitmap photo;
	private Dialog dialog;
	private byte[] avatar;
	private long keyid;
	private String keyName;
	private String name;
	private long sid ;
	private String resourceid;
	private LinearLayout content;
	private View playView;
	private List<GameKey>keylist = new ArrayList<GameKey>();
	private boolean CHANGE  = false;
	private Map<Long ,Long> exist = new HashMap<Long, Long>();
	private CustomProgressDialog downloaddialog;
	private String gname;
	private LinearLayout moreserver;
	public static boolean compelte = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getData();
		inital();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(UserRoleDetailActivity.vo!=null){
		userRole =UserRoleDetailActivity.vo;
		UserRoleDetailActivity.vo = null;
		updateRoleView(userRole);}
	}
	
	/**
	 * 获取传入的参数
	 */
	private void getData() {
		playid = getIntent().getLongExtra("pid", 0);
		gname=getIntent().getStringExtra("gname");
		userRole = (UserRoleData) getIntent().getSerializableExtra("gamerole");
	}
	/**
	 * 初始化界面
	 */
	private void inital() {
		setTitleTxt("编辑陪玩信息");
		LinearLayout convertView = (LinearLayout) findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		 playView = View.inflate(UserPlayEditActivity.this,
				R.layout.user_create_play, null);
		convertView.addView(playView, params);
		content = (LinearLayout) playView
				.findViewById(R.id.create_play_content);
		chooseGame = (LinearLayout) playView.findViewById(R.id.choose_play_game);
		gameIcon = (ImageView) playView.findViewById(R.id.play_game_icon);
		gameRole = (TextView) playView.findViewById(R.id.play_game_role);
		addGame = (ImageView) playView.findViewById(R.id.play_choose_role);
		playPay = (EditText) playView.findViewById(R.id.my_play_pay);
		remark = (EditText) playView.findViewById(R.id.play_remark);
		qqEdit = (EditText) playView.findViewById(R.id.play_qq);
		phoneEdit = (EditText) playView.findViewById(R.id.play_phone);
		playImage = (ImageView) playView.findViewById(R.id.play_game_image);
		deleteImage = (ImageView) playView.findViewById(R.id.delete_play_image);
		protocolImage = (CheckBox) playView.findViewById(R.id.play_protocol_check);
		protocolText = (TextView) playView.findViewById(R.id.play_protocol);
		compeltePlay = (Button) playView.findViewById(R.id.compelte_play);
		gameService = (TextView) playView.findViewById(R.id.play_game_service);
		moreService = (TextView) playView.findViewById(R.id.other_play_service);
		addService = (RelativeLayout) playView.findViewById(R.id.play_add_service);
		contentView =(LinearLayout)playView.findViewById(R.id.play_content_view);
		moreserver=(LinearLayout)playView.findViewById(R.id.more_servers);
		chooseGame.setTag(false);
		chooseGame.setOnClickListener(this);
		addGame.setOnClickListener(this);
		playImage.setOnClickListener(this);
		deleteImage.setOnClickListener(this);
		protocolImage.setOnClickListener(this);
		addService.setOnClickListener(this);
		protocolText.setOnClickListener(this);
		compeltePlay.setOnClickListener(this);
		protocolText.setOnClickListener(this);
		protocolText.setText(Html.fromHtml("<u>陪玩协议</u>"));
		playView.setVisibility(View.GONE);
		getPlayDetailInfo(UserPlayEditActivity.this, playid, "1,2,3,4");//从服务端获取陪玩详情数据
		remark.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent event) {
				// TODO Auto-generated method stub
				return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
			}
		});
		InputFilterUtil.lengthFilter(this, remark, 100, "输入的备注不能超过50个汉字或100个字符哦!");
	}
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.play_add_service) {
			getGameStar(UserPlayEditActivity.this, gid);
			// intent.putExtra("gid", gid);
			// intent.putExtra("sid", sid);
		} else 
			if (view.getId() == R.id.choose_play_game) {
			Intent intent = new Intent(UserPlayEditActivity.this,
					UserRoleDetailActivity.class);
			intent.putExtra("gid", gid);
			intent.putExtra("edit", true);
			intent.putExtra("gname", gname);
			startActivity(intent);
		} else if (view.getId() == R.id.delete_play_image) {
			playImage.setImageResource(R.drawable.setting_photo_add_btn_nor);
			deleteImage.setVisibility(View.GONE);
			resourceid =null;
			Image = false;
		} else if (view.getId() == R.id.play_game_image) {
			if (Image&&image!=null) {
				jumpImageBrowerAct(photoPath);
			}else if(Image&&image==null){
				lookImage(resourceid);
			}
			else if(!Image) {
				getImage();
			}
		} else if (view.getId() == R.id.compelte_play) {
			if (checkCompelteInfo()&&protocolImage.isChecked()) {
				compeltePlay.setClickable(false);
				downloaddialog = CustomProgressDialog.createDialog(this, false);
				downloaddialog.show();
				createPlayInfo(UserPlayEditActivity.this, playid, image,getPlayInfo(getPlayData()));
			}
		}else if(view.getId()==R.id.play_protocol){
			Intent intent = new Intent(UserPlayEditActivity.this, PlayProtocolActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("playprotocol", "陪玩协议");
			bundle.putString("playurl", SystemConfig.PLAY_PROTOCOL_ADRRESS);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
	private String path;
	/**
	 * 添加照片
	 */
	public void getImage() {
		dialog = new Dialog(UserPlayEditActivity.this,
				R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_play_add_image);
		TextView cameraAdd = (TextView) dialog
				.findViewById(R.id.create_paly_camera_add);
		TextView photoAdd = (TextView) dialog
				.findViewById(R.id.create_play_photo_add);
		TextView cancle = (TextView) dialog
				.findViewById(R.id.cancle_paly_image);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.anim.set_add_play_image);
		dialog.show();
		path =Environment.getExternalStorageDirectory() + File.separator + "msgs_tmp_pic"+System.currentTimeMillis()+".jpg";
		cameraAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				PhotoUtil.doTakePhoto(UserPlayEditActivity.this,path);
			}
		});
		photoAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PhotoUtil.doPickPhotoFromGallery(UserPlayEditActivity.this);
			}
		});
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}
	/**
	 * 从服务端获取陪玩详情接口
	 * @param context
	 * @param playid
	 * @param resulttype
	 */
	public void getPlayDetailInfo(Context context, Long playid,
			String resulttype) {
		final CustomProgressDialog downloaddialog = CustomProgressDialog.createDialog(this, false);
		downloaddialog.show();
		ProxyFactory.getInstance().getPlayProxy()
				.searchPlayDetailInfo(new ProxyCallBack<PlayInfo>() {

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(PlayInfo result) {
						// TODO Auto-generated method stub
						if (result != null) {
							playinfo = result;
							game = result.getGameRole();
							userData = result.getRoleData();
							gid = result.getGid();
							keylist =result.getGameRole().getGameKeyListList();
							setViewContent(result);
							downloaddialog.dismiss();
							playView.setVisibility(View.VISIBLE);
						}
					}
				}, context, playid, resulttype);
	}

	/**
	 * 设置陪玩属性的值
	 * 
	 * @param vo
	 */
	public void setViewContent(PlayInfo vo) {
		if(vo.getRoleData().getAvatar()!=null){
			chooseGame.setTag(true);
			gameIcon.setVisibility(View.VISIBLE);
		ImageViewUtil.showImage(gameIcon, vo.getRoleData().getAvatar(),
				R.drawable.postbar_thumbimg_default);
		}else{
			return;
		}
		sids=vo.getSids();
		UserRoleData.Builder data = UserRoleData.newBuilder();
		data.setAvatar(vo.getRoleData().getAvatar());
		data.setGid(vo.getRoleData().getGid());
		data.addAllAttr(vo.getRoleData().getAttrList());
		data.setRoleid(vo.getRoleData().getRoleid());
		data.setStatus(vo.getRoleData().getStatus());
		data.setSid(vo.getRoleData().getSid());
		userRole = data.build();
		sid= vo.getSid();
		resourceid=vo.getResourceid();
		gameRole.setText(vo.getRoleData().getName());
		gameRole.setTextColor(getResources().getColor(R.color.black));
		//setPlayServer();
		setServerName();
		name = vo.getRoleData().getName();
		playPay.setText(vo.getCost() + "");
		remark.setText(vo.getRemark());
		qqEdit.setText(vo.getQq());
		phoneEdit.setText(vo.getMobile());
		resourceid =vo.getResourceid();
		ImageViewUtil.showImage(playImage, vo.getResourceid(), R.drawable.common_user_icon_default);
		deleteImage.setVisibility(View.VISIBLE);
		gameService.setText(vo.getServername());
		gameService.setTextColor(getResources().getColor(R.color.black));
		List<RoleAttr> list = userData.getAttrList();
		setMutipliesServer(gid);
		addNatureLayout(list, game);//添加相应的属性view
	}
	/**
	 * 获取是否显示多服务器添加按钮
	 * @param gid
	 */
	private void  setMutipliesServer(long gid){
		AppConfig config = AdaptiveAppContext.getInstance().getAppConfig();
		JSONArray sersids = config.getMoreServer();
		if(sersids!=null&&!sersids.equals("")){
			for(int i=0;i<sersids.length();i++){
				long id;
				try {
					id = sersids.getLong(i);
					if(id==gid){
						addService.setVisibility(View.VISIBLE);
						return;
					}else{
						addService.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private void setServerName(){
		List<GameServerEntry> serverlist = new ArrayList<GameServerEntry>();
		if("0".equals(playinfo.getSids())){
			moreserver.setVisibility(View.VISIBLE);
			sname ="支持全服";
			moreService.setText(sname);
			setServerWid(false);
			return;
		}
		serverlist.addAll(playinfo.getGameServerList());
		if(serverlist.size()<=0){
			moreserver.setVisibility(View.GONE);
		}else{
			moreserver.setVisibility(View.VISIBLE);
			for(int i = 0 ; i<serverlist.size();i++){
				GameServerEntry entry = serverlist.get(i);
				
					  if(i ==serverlist.size()-1){
						  sname+=entry.getName();
					  }else{
						  sname+=entry.getName()+",";
					  }
			}
			moreService.setText(sname);
			setServerWid(true);
		}
	}
	private void setPlayServer(){
		if(sids==null||sids.equals("")) return;
		String[] arry = sids.split("\\,");
		if(arry==null&&arry.length==0) return;
		if(arry.length>3){
		moreserver.setVisibility(View.VISIBLE);
		moreService.setText("支持全服");
		sids="0";
		}else{
	outer:	for(int i=0;i<arry.length;i++){
			if(arry[i].equals("")) return;
			long d =Long.parseLong(arry[i]);
			for(GameKey key:game.getGameKeyListList()){
				if(key.getAttrType()==MsgsConstants.GAME_ROLE_KEY_SERVER){
						for(int j=0;j<key.getGameKeysDetailListList().size();j++){
							GameKeysDetail detail = key.getGameKeysDetailList(j);
								if(detail.getId()==d){
									if(i==arry.length-1){
										sname+= detail.getContent();
									}else{
										sname+= detail.getContent()+",";
									}
									continue outer;
								}
						}
					}
				}
		}
		moreserver.setVisibility(View.VISIBLE);
		moreService.setText(sname);
		}
	}
	/**
	 * 添加一些角色属性布局
	 * 
	 * @param vo
	 */
	private void addNatureLayout(List<RoleAttr> list, GameRole game) {

		List<GameKey> keylist = game.getGameKeyListList();
	outer:	for (int i = 0; i < keylist.size(); i++) {
			final GameKey key = keylist.get(i);
			for (int j = 0; j < list.size(); j++) {
				RoleAttr attr = list.get(j);
				if (key.getId() == attr.getKeyid()) {
					addDetailView(attr, key);
					continue outer;
				} 
			}
			addNewView(key);
		}
	}
	//如果用户的属性数据和服务端相等就在此方法中添加view
	public void addDetailView(RoleAttr attr, final GameKey key) {
		if (key.getAttrType() != MsgsConstants.GAME_ROLE_KEY_SERVER
				&& key.getAttrType() != MsgsConstants.GAME_ROLE_KEY_USER) {
			if (key.getKeyType() == 1) {//输入框view
				View view = View.inflate(UserPlayEditActivity.this,
						R.layout.create_play_item, null);
				TextView name = (TextView) view
						.findViewById(R.id.play_nature_name);
				TextView itemName = (TextView) view
						.findViewById(R.id.play_nature_values);
				itemName.setVisibility(View.GONE);
				EditText edit = (EditText) view
						.findViewById(R.id.play_nature_input);
				edit.setVisibility(View.VISIBLE);
				name.setText(key.getKeyName());
				edit.setText(attr.getContent());
				setEditText(key, edit);
				contentView.addView(view);
				map.put(key.getId(), view);
				addUserRoleAttr(key.getId(),key.getKeyName(),attr.getContent());
			} else if (key.getKeyType() == 0) {//下拉列表view
				View view = View.inflate(UserPlayEditActivity.this,
						R.layout.create_play_item, null);
				TextView name = (TextView) view
						.findViewById(R.id.play_nature_name);
				TextView itemName = (TextView) view
						.findViewById(R.id.play_nature_values);
				ImageView skipView = (ImageView) view
						.findViewById(R.id.skip_icon);
				skipView.setVisibility(View.VISIBLE);
				itemName.setVisibility(View.VISIBLE);
				EditText edit = (EditText) view
						.findViewById(R.id.play_nature_input);
				edit.setVisibility(View.GONE);
				name.setText(key.getKeyName());
				itemName.setText(attr.getContent());
				itemName.setTextColor(getResources().getColor(R.color.black));
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						
						skipListView(key);
					}
				});
				map.put(key.getId(), view);
				contentView.addView(view);
				exist.put(key.getId(),attr.getKeyid());
				addUserRoleAttr(key.getId(),key.getKeyName(),attr.getValid()+"");
			} else if (key.getKeyType() == 2) {//复选框的view
				View view = View.inflate(UserPlayEditActivity.this,
						R.layout.play_reset_choice_item, null);
				TextView natureName = (TextView) view
						.findViewById(R.id.play_reset);
				LinearLayout resetContent = (LinearLayout) view
						.findViewById(R.id.play_reset_content);
				keyid = key.getId();
				keyName = key.getKeyName();
				natureName.setText(key.getKeyName());
				List<GameKeysDetail> gamelist = key.getGameKeysDetailListList();
				addMoreChoiceView(attr, key, gamelist.size(), resetContent,
						key.getKeyName());
				contentView.addView(view);
			}
		
		}else {
			if(key.getAttrType() == MsgsConstants.GAME_ROLE_KEY_USER){
			addUserRoleAttr(key.getId(),key.getKeyName(),attr.getContent());
		}else if(key.getAttrType() != MsgsConstants.GAME_ROLE_KEY_SERVER){
			addUserRoleAttr(key.getId(),key.getKeyName(),attr.getKeyid()+"");
			}
		}
	}
	//如果服务端的属性比角色多，在通过此方法补加view
	public void addNewView(final GameKey key){
		if (key.getKeyType() == 1) {
			View view = View.inflate(UserPlayEditActivity.this,
					R.layout.create_play_item, null);
			TextView name = (TextView) view
					.findViewById(R.id.play_nature_name);
			TextView itemName = (TextView) view
					.findViewById(R.id.play_nature_values);
			itemName.setVisibility(View.GONE);
			EditText edit = (EditText) view
					.findViewById(R.id.play_nature_input);
			edit.setVisibility(View.VISIBLE);
			name.setText(key.getKeyName());
			setEditText(key, edit);
			contentView.addView(view);
			map.put(key.getId(), view);
		} else if (key.getKeyType() == 0) {
			View view = View.inflate(UserPlayEditActivity.this,
					R.layout.create_play_item, null);
			TextView name = (TextView) view
					.findViewById(R.id.play_nature_name);
			TextView itemName = (TextView) view
					.findViewById(R.id.play_nature_values);
			ImageView skipView = (ImageView) view
					.findViewById(R.id.skip_icon);
			skipView.setVisibility(View.VISIBLE);
			itemName.setVisibility(View.VISIBLE);
			EditText edit = (EditText) view
					.findViewById(R.id.play_nature_input);
			edit.setVisibility(View.GONE);
			name.setText(key.getKeyName());
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					
					skipListView(key);
				}
			});
			map.put(key.getId(), view);
			contentView.addView(view);
		} else if (key.getKeyType() == 2) {
			View view = View.inflate(UserPlayEditActivity.this,
					R.layout.play_reset_choice_item, null);
			TextView natureName = (TextView) view
					.findViewById(R.id.play_reset);
			LinearLayout resetContent = (LinearLayout) view
					.findViewById(R.id.play_reset_content);
			keyid = key.getId();
			keyName = key.getKeyName();
			natureName.setText(key.getKeyName());
			List<GameKeysDetail> gamelist = key.getGameKeysDetailListList();
			addMoreChoiceView(null, key, gamelist.size(), resetContent,
					key.getKeyName());
			contentView.addView(view);
		}
		
	}
	/**
	 * 添加更多复选框
	 * 
	 * @param attr
	 * @param key
	 * @param size
	 * @param content
	 */
	public void addMoreChoiceView(RoleAttr attr, GameKey key, int size,
			LinearLayout content, String name) {
		int count = 0;
		int numColumns = 4;
		if (size % 4 == 0) {
			count = size / 4;
		} else {
			count = size / 4 + 1;
		}
		for (int i = 0; i < count; i++) {
			View view = View.inflate(UserPlayEditActivity.this,
					R.layout.play_more_choose_content, null);
			View text1 = view.findViewById(R.id.choose1);
			View text2 = view.findViewById(R.id.choose2);
			View text3 = view.findViewById(R.id.choose3);
			View text4 = view.findViewById(R.id.choose4);
			for (int j = i * numColumns; j < (i + 1) * numColumns && j < size; j++) {
				final GameKeysDetail vo = key.getGameKeysDetailListList()
						.get(j);
				if (j % numColumns == 0) {
					text1.setVisibility(View.VISIBLE);
					addView(text1, vo, attr, name);
				} else if (j % numColumns == 1) {
					text2.setVisibility(View.VISIBLE);
					addView(text2, vo, attr, name);
				} else if (j % numColumns == 2) {
					text3.setVisibility(View.VISIBLE);
					addView(text3, vo, attr, name);
				} else if (j % numColumns == 3) {
					text4.setVisibility(View.VISIBLE);
					addView(text4, vo, attr, name);
				} else {
					text1.setVisibility(View.GONE);
					text2.setVisibility(View.GONE);
					text3.setVisibility(View.GONE);
					text4.setVisibility(View.GONE);
				}
			}
			content.addView(view);
		}
	}

	/**
	 * 设置复选颜色
	 * 
	 * @param view
	 * @param key
	 * @param attr
	 */
	public void addView(View view, final GameKeysDetail key, RoleAttr attr,
			final String name) {
		final TextView text = (TextView) view
				.findViewById(R.id.choose_check_name);
		text.setText(key.getContent());
		if(attr!=null){
		for (ValData data : attr.getValDataList()) {
			if (data.getId() == key.getId()) {
				text.setTextColor(getResources().getColor(
						R.color.play_text_font_pre_color));
				text.setBackgroundResource(R.drawable.play_text_dis_pre);
				list.add(data);
				exitVal.add(data);
				break;
			} else {
				text.setTextColor(getResources().getColor(
						R.color.play_text_font_nor_color));
				text.setBackgroundResource(R.drawable.play_text_dis_shap_nor);
			}
			}
		}
		text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (updateVal(key.getId())) {
					text.setTextColor(getResources().getColor(
							R.color.play_text_font_nor_color));
					text.setBackgroundResource(R.drawable.play_text_dis_shap_nor);
				} else {
					text.setTextColor(getResources().getColor(
							R.color.play_text_font_pre_color));
					text.setBackgroundResource(R.drawable.play_text_dis_pre);
					ValData.Builder data= ValData.newBuilder();
					data.setId(key.getId());
					data.setVal(key.getContent());
					list.add(data.build());
				}
			}
		});
	}

	/**
	 * 更改valdata集合
	 * 
	 * @param id
	 * @return
	 */
	public boolean updateVal(Long id) {
		for (ValData data : list) {
			if (data.getId() == id) {
				list.remove(data);
				return true;
			}
		}
		return false;
	}
	/**
	 * 把attrmap中RoleAttr转换成RoleAttr集合
	 * @return
	 */
	public List<RoleAttr> getRoleList(){
		if(list.size()>0){
			StringBuilder builder = new StringBuilder();
			for(int i=0;i<list.size();i++){
				if(i==list.size()-1){
					builder.append(list.get(i).getId());
				}else{
					builder.append(list.get(i).getId()+",");
				}
			}
		addUserRoleAttr(keyid, keyName, builder.toString());
		}
		List<RoleAttr>attrlist = new ArrayList<RoleAttr>();
		for(UserRoleAttr att : attrMap.values()){
			RoleAttr.Builder attr = RoleAttr.newBuilder();
			attr.setKeyid(att.getId());
			attr.setKey(att.getKey());
			if(att.getContent()!=null){
				attr.setContent(att.getContent());
				}
			attrlist.add(attr.build());
		}
		return attrlist;
	}
	/**
	 * 构建Userroledata 对象
	 * @return
	 */
	public UserRoleData getUserRoleData() {
		UserRoleData.Builder user = UserRoleData.newBuilder();
		user.setRoleid(userRole.getRoleid());
		user.setAvatar(userRole.getAvatar());
		user.setStatus(userRole.getStatus());
		user.setSid(userRole.getSid());
		user.setName(name);
		user.setGid(userRole.getGid());
		user.addAllAttr(getRoleList());
		return user.build();
	}
	//构建playVo对象
	public PlayVo getPlayData() {
		PlayVo vo = new PlayVo();
		vo.setCosts(Integer.parseInt(playPay.getText().toString()));
		vo.setGid(gid);
		vo.setRemark(remark.getText().toString());
		vo.setRoleData(getUserRoleData());
		vo.setMobile(phoneEdit.getText().toString());
		vo.setQq(qqEdit.getText().toString());
		vo.setRoleid(userRole.getRoleid());
		vo.setSid(sid);
		return vo;
	}
	//构建服务所需要传递的playinfo对象
	public PlayInfo getPlayInfo(PlayVo vo) {
		PlayInfo.Builder play = PlayInfo.newBuilder();
		play.setCost(vo.getCosts());
		play.setGid(vo.getGid());
		play.setRemark(vo.getRemark());
		play.setRoleData(vo.getRoleData());
		play.setMobile(vo.getMobile());
		play.setQq(vo.getQq());
		play.setRoleid(vo.getRoleid());
		play.setSid(vo.getSid());
		play.setSids(sids);
		return play.build();
	}
	/**
	 * 跳转取数据
	 * 
	 * @param vo
	 */
	public void skipListView(GameKey key) {
		
		Intent intent = new Intent(UserPlayEditActivity.this,
				UserRoleNatureActiviy.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("key", key);
		intent.putExtras(bundle);
		startActivityForResult(intent, 101);
	}

	/**
	 * 设置输入监听器
	 * 
	 * @param vo
	 * @param edit
	 */
	public void setEditText(final GameKey vo, final EditText edit) {
		edit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

				if (!edit.getText().toString().equals("")) {
					addUserRoleAttr(vo.getId(), vo.getKeyName(), edit.getText()
							.toString());
				}
			}
		});
	}

	/**
	 * 处理返回数据
	 * 添加到attrmap中
	 * @param id
	 * @param name2
	 * @param values
	 */
	public void addUserRoleAttr(Long id, String name2, String values) {
		UserRoleAttr attr = new UserRoleAttr();
		attr.setId(id);
		attr.setContent(values);
		attr.setKey(name2);
		attrMap.put(id, attr);
	}
	private String photoPath;
	private String sids;
	private String sname="";
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap tempBtm = null;
		byte[] photoByte = null;
		boolean tag = false;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case PhotoUtil.CAMERA_WITH_DATA:// 相机
				Display mDisplay = this.getWindowManager().getDefaultDisplay();
				int w = mDisplay.getWidth();
				int h = mDisplay.getHeight();
				int imagewidth = w > h ? h : w;
				imageUri = Uri.parse("file://" + path);
				photoPath=imageUri.getPath();
				PhotoUtil.doCropBigPhoto(this, imageUri, imageUri, 1, 1,
						imagewidth, imagewidth);
				return;
			case PhotoUtil.PHOTO_PICKED_WITH_DATA:// 相册
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				Display mDisplay2 = this.getWindowManager().getDefaultDisplay();
				int w2 = mDisplay2.getWidth();
				int h2 = mDisplay2.getHeight();
				int imagewidth2 = w2 > h2 ? h2 : w2;
				imageUri = Uri.parse("file://" +path);
				photoPath=imageUri.getPath();
				PhotoUtil.doCropBigPhoto(this, originalUri, imageUri, 1, 1,
						imagewidth2, imagewidth2);
				return;
			case PhotoUtil.CROP_IMAGE_WITH_DATA:
				if (data != null && data.getParcelableExtra("data") != null) {
					try {
						tempBtm = photo;
						photo = data.getParcelableExtra("data");
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (photo != null) {
						photoByte = ImageUtil.Bitmap2Bytes(photo,
								CompressFormat.JPEG, 30);
					}
				} else {
					if (imageUri != null) {
						try {
							tempBtm = photo;
							photo = ImageUtil.decodeUri2Bitmap(
							this.getContentResolver(), imageUri);
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (photo != null) {
							photoByte = ImageUtil.Bitmap2Bytes(photo,
									CompressFormat.JPEG, 30);
						}
					}
				}
				tag =true;
				break;
			case 101:
				Long id = data.getLongExtra("id", 0);
				String values = data.getExtras().getString("values");
				String name = data.getExtras().getString("name");
				Long key = data.getLongExtra("key", 0);
				if (id == 0)
					return;
				if (values == null)
					return;
				if (name == null)
					return;
				if(exist.get(id)!=null){
					if(exist.get(id)==key){
						CHANGE = false;
					}else {
						CHANGE =true;
					}
				}else{
					CHANGE =true;
				}
				if (map.get(id) != null) {
					TextView view = (TextView) map.get(id).findViewById(
							R.id.play_nature_values);
					view.setText(values);
					view.setTextColor(getResources().getColor(R.color.black));
				}
				// addUserRoleAttr(id, values);
				addUserRoleAttr(id, name, key+ "");
				break;
			case 112:
				Bundle bundle = data.getExtras();
				sids = bundle.getString(SystemConfig.CHOOSED_SERVER_IDS);
				sname=bundle.getString(SystemConfig.CHOOSED_SERVER_NAMES);
				if(!"".equals(sids)){
					String[] arry = sids.split("\\,");
					if(arry.length>2){
						moreserver.setVisibility(View.VISIBLE);
						moreService.setText("");
						moreService.setText("支持全服");
						sids="0";
					setServerWid(false);
					}else{
					if(sname!=null&&!sname.equals("")){
						moreserver.setVisibility(View.VISIBLE);
						moreService.setText("");
						moreService.setText(sname);
						setServerWid(true);
						}else{
							moreserver.setVisibility(View.GONE);
						}
					}
				}
				else{
					moreserver.setVisibility(View.GONE);
				}
				break;
			}
			if(tag){
			if (photo != null) {
				playImage.setImageBitmap(photo);
				Image = true;
				deleteImage.setVisibility(View.VISIBLE);
				dialog.dismiss();
				avatar = photoByte;
				image = null;
				image = photoByte;
				if (tempBtm != null && !tempBtm.isRecycled()) {
					tempBtm.recycle();
					tempBtm = null;
					// System.gc();
				}
			} else {
				if (tempBtm != null && !tempBtm.isRecycled()) {
					tempBtm.recycle();
					tempBtm = null;
					// System.gc();
				}
				ToastUtil.showToast(
						this,
						getResources().getString(
								R.string.common_add_photo_error));
				return;
			}
		} else {
		}
		}
	}

	public void updateRoleView(UserRoleData vo) {
		chooseGame.setTag(true);
		if(vo.getRoleid()==playinfo.getRoleid()){
			CHANGE=true;
		}else {
			CHANGE =false;
		}
		ImageViewUtil.showImage(gameIcon, vo.getAvatar(),
				R.drawable.postbar_thumbimg_default);
		List<RoleAttr> attrlist = vo.getAttrList();
		for (RoleAttr attr : attrlist) {
			if (attr.getAttrtype() == MsgsConstants.GAME_ROLE_KEY_USER) {
				gameRole.setText(attr.getContent());
				gameRole.setTextColor(getResources().getColor(R.color.black));
			} else if (attr.getAttrtype() == MsgsConstants.GAME_ROLE_KEY_SERVER) {
				gameService.setText(attr.getContent());
				gameService.setTextColor(getResources().getColor(R.color.black));
				sid=attr.getValid();
			}
		}
		setMutipliesServer(gid);
		moreserver.setVisibility(View.GONE);
		moreService.setText("");
		sids="";
		contentView.removeAllViews();
		attrMap.clear();
		map.clear();
		choiceMap.clear();
		list.clear();
		addNatureLayout(attrlist, game);
	}

	/**
	 * 构建userRoleData参数
	 * 
	 * @param map
	 * @return
	 */
	public UserRoleData builderAttrMap(Map<Long, UserRoleAttr> map) {
		UserRoleData.Builder urdBuilder = UserRoleData.newBuilder();
		if (map == null && map.size() <= 0)
			return null;
		for (UserRoleAttr att : map.values()) {
			RoleAttr.Builder attr = RoleAttr.newBuilder();
			attr.setKeyid(att.getId());
			attr.setKey(att.getKey());
			attr.setContent(att.getContent());
			attr.addAllValData(att.getList());
			urdBuilder.addAttr(attr);
		}
		return urdBuilder.build();
	}

	public void setValData(Map<Long, TextView> map) {
		if (keyid == 0 || keyName.equals(""))
			return;
		List<ValData> list = new ArrayList<ValData>();
	}

	// 完成陪玩編輯
	public boolean checkCompelteInfo() {
		int pay = Integer.parseInt(playPay.getText().toString());
		String qq =qqEdit.getText().toString();
		if(!checkNature()){
			return false;
		}else if (playPay.getText().toString().equals("")) {
			ToastUtil.showToast(UserPlayEditActivity.this, "你还未输入收费数目哦！");
			return false;
		}else if(pay<10){
				ToastUtil.showToast(UserPlayEditActivity.this, "输入的收费数目不能低于10哦！");
				return false;
			}
		else if(pay>20000){
			ToastUtil.showToast(UserPlayEditActivity.this, "输入的收费数目不能超过20000哦！");
			return false;
		}else if (pay % 10 != 0) {
			ToastUtil.showToast(UserPlayEditActivity.this, "输入的收费数目必须为10的倍数哦！");
			return false;
		}else if (remark.getText().toString().equals("")) {
			ToastUtil.showToast(UserPlayEditActivity.this, "你还未输入备注哦！");
			return false;
		}else if(ServiceFactory.getInstance().getWordsManager().match(remark.getText().toString())){
			ToastUtil.showToast(UserPlayEditActivity.this, "输入的备注中有非法字符或敏感词，请重新输入!");
			return false;
		}
		else if((remark.getText().toString()).length()>100){
			ToastUtil.showToast(UserPlayEditActivity.this, "你输入的备注内容不能超过50个字哦！");
			return false;
		}
		else if (qqEdit.getText().toString().equals("")) {
			ToastUtil.showToast(UserPlayEditActivity.this, "你还未填写QQ号哦!");
			return false;
		}else if(qq.length()>13||qq.length()<5){
			ToastUtil.showToast(UserPlayEditActivity.this, "你输入的QQ号格式不对哦!（QQ号格式为5~13位数字）");
			return false;
		}
		else if (phoneEdit.getText().toString().equals("")) {
			ToastUtil.showToast(UserPlayEditActivity.this, "你还未输入手机号码哦！");
			return false;
		}else if (phoneEdit.getText().toString().length() < 11
				|| phoneEdit.getText().toString().length() > 11) {
			ToastUtil.showToast(UserPlayEditActivity.this, "请输入正确的手机号码哦!");
			return false;
		} else if (!Image) {
			ToastUtil.showToast(UserPlayEditActivity.this, "你还未上传图片哦");
			return false;
		} else if(!protocolImage.isChecked()){
			ToastUtil.showToast(UserPlayEditActivity.this, "你还未同意游伴陪玩协议哦！");
			return  false;
			}
		return true;
	}
	/**
	 * 编辑陪玩接口
	 * @param context
	 * @param playid
	 * @param image
	 * @param info
	 */
	public void createPlayInfo(Context context, long playid, byte[] image,
			PlayInfo info) {
		ProxyFactory.getInstance().getPlayProxy()
				.createPlayInfo(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						if (result == 0) {
							compeltePlay.setClickable(true);
							downloaddialog.dismiss();
							compelte = true;
							UserPlayEditActivity.this.finish();
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						String text=null;
						 if(result==NO_CAN_EDIT){
						text=getResources().getString(R.string.no_can_edit);
						}else if(result==PLAY_ALREADY_EXISTS){
						text=getResources().getString(R.string.play_already_exist);
						}else if(result==DAY_CAN_ONCE){
						text=getResources().getString(R.string.day_can_once);
						}else if(result==GAME_PLAY_NO_EXIST){
						text=getResources().getString(R.string.play_info_no_exits);
						}else if(result==EC_MSGS_NOT_SAME_PLAY_GAME){
							text ="陪玩角色不属于所选游戏";
						}else if(result==EC_MSGS_USERPLAY_CLOSED){
							text="陪玩业务已封停";
						}else if(result==EC_MSGS_PLAY_STATUS_CLOSED){
							text="陪玩商品被oa关闭";
						}
						 downloaddialog.dismiss();
						playErrorDialog(text);
						compeltePlay.setClickable(true);
					}
				}, context, playid, image, info);
	}
	
	public void  getGameStar(Context context,long gid){
		ProxyFactory.getInstance().getPlayProxy().getGameStarLeve(new ProxyCallBack<Msgs.PlayStar>() {
			
			@Override
			public void onSuccess(PlayStar result) {
				// TODO Auto-generated method stub
				if(result!=null){
					if(result.getNum()>=3){
						Intent intent = new Intent(UserPlayEditActivity.this,
								PlayServiceListActivity.class);
						intent.putExtra("game", game);
						intent.putExtra("playinfo", playinfo);
						intent.putExtra("sid",sid);
						intent.putExtra("sidss", sids);
						startActivityForResult(intent, 112);
					}else{
						startDialog();
					}
				}
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				
			}
		}, context, gid);
	}
	public void startDialog(){
		final Dialog dialog = new Dialog(UserPlayEditActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("获取3个五星级好评才可以添加更多服务器哦!");
		LinearLayout btnBttom = (LinearLayout)dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom =(LinearLayout)dialog.findViewById(R.id.text_bottom);
		btnBttom.setVisibility(View.GONE);
		textBttom.setVisibility(View.VISIBLE);
		dialog.show();
		textBttom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}
	public void playErrorDialog(String text){
		final Dialog dialog = new Dialog(UserPlayEditActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText(text);
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
	/**
	 * 
	 * @param path
	 */
	private void jumpImageBrowerAct(String path) {
		
			List<String> pathList = new ArrayList<String>();
			pathList.add(path);
			Intent intent = new Intent(UserPlayEditActivity.this, ImageBrowerActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, 0);
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, 1);
			bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, pathList.toArray(new String[pathList.size()]));
			bundle.putBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES_ISSHOWREPORTMENU, false);
			intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			UserPlayEditActivity.this.startActivity(intent);
		}
	public void lookImage(String resid){
		Intent intent = new Intent(UserPlayEditActivity.this, ImageBrowerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, new String[] { ResUtil.getOriginalRelUrl(resid) });
		ExtUserVo uvo = SystemContext.getInstance().getExtUserVo();
		bundle.putBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES_ISSHOWREPORTMENU, false);
		bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, 0);
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_USER);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		UserPlayEditActivity.this.startActivity(intent);
	}
	
	
	public boolean checkNature(){
		for(int i =0;i<keylist.size();i++){
			if(keylist.get(i).getKeyType()==0){
				if(keylist.get(i).getAttrType()!=MsgsConstants.GAME_ROLE_KEY_SERVER){
					TextView text =(TextView) map.get(keylist.get(i).getId()).findViewById(R.id.play_nature_values);
					String data =text.getText().toString();
				if(data.contains(keylist.get(i).getKeyName())&&data.equals("")){
					ToastUtil.showToast(UserPlayEditActivity.this, "你还未选择你的"+keylist.get(i).getKeyName()+"哦！");
					return false;
				}
				}
			}else if(keylist.get(i).getKeyType()==1){
				if(keylist.get(i).getAttrType()!=MsgsConstants.GAME_ROLE_KEY_USER){
				EditText edit = (EditText)map.get(keylist.get(i).getId()).findViewById(R.id.play_nature_input);
				if(CheckUserRoleName(edit,keylist.get(i))){
				}else{
					return false;
				}
				}
			}else if(keylist.get(i).getKeyType()==2){
				if(list.size()<=0){
					ToastUtil.showToast(UserPlayEditActivity.this, "你还未选择你的"+keylist.get(i).getKeyName()+"哦！");
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * 检查输入的内容是否合适
	 * @param txt
	 */
	public boolean CheckUserRoleName(EditText txt,GameKey key){
		String moode = txt.getText().toString();
		if (!txt.getText().toString().isEmpty()) {
			if (ServiceFactory.getInstance().getWordsManager().match(moode)) {
				ToastUtil.showToast(UserPlayEditActivity.this,"您输入的"+key.getKeyName()+"内容"+UserPlayEditActivity.this.getResources().getString(R.string.global_words_error2));
				return false;
			} else {
				if(!txt.getText().toString().trim().isEmpty()){
					if(txt.getText().toString().getBytes().length>40){
						ToastUtil.showToast(UserPlayEditActivity.this,"您输入的"+key.getKeyName()+"内容"+UserPlayEditActivity.this.getResources().getString(R.string.role_name_size));
						return false;
					}
				}else{
					ToastUtil.showToast(UserPlayEditActivity.this,"您输入的"+key.getKeyName()+"内容"+UserPlayEditActivity.this.getResources().getString(R.string.role_not_space));
					return false;
				}
			}
		} else {
			ToastUtil.showToast(UserPlayEditActivity.this, UserPlayEditActivity.this.getResources().getString(R.string.txt_verify_fail)+key.getKeyName()+"哦！");
			return false;
		}
		return true;
	}
	/**
	 * 检查复选框内容有没有改变
	 * @return
	 */
	public boolean checkMoreChoice(){
		if(list.size()!=exitVal.size()){
			return true;
		}else if(list.size()==exitVal.size()){
			if(list.size()==0){
				return false;
			}
			int count =0;
			for(int i=0;i<list.size();i++){
				for(int j=0;j<exitVal.size();j++){
					if(list.get(i).getId()==exitVal.get(j).getId()){
						count ++;
					}
				}
			}
			if(count==list.size()){
				return false;
			}else{
				return true;
			}
		}
		return false;
	}
	/**
	 * 检查陪玩其他信息有没有发生改变
	 * 
	 * @return
	 */
	private boolean otherInfo(){
		String pay = playPay.getText().toString();
		String rek =remark.getText().toString();
		String qq =qqEdit.getText().toString();
		String phone =phoneEdit.getText().toString();
		if(pay.equals("")&&rek.equals("")&&qq.equals("")&&phone.equals("")&&!Image){
			return true;
		}else if(Integer.parseInt(pay)!=playinfo.getCost()){
			return true;
		}else if(!rek.equals(playinfo.getRemark())){
			return true;
		}else if(!qq.equals(playinfo.getQq())){
			return true;
		}else if(!phone.equals(playinfo.getMobile())){
			return true;
		}else if(resourceid==null){
			return true;
		}else if(!protocolImage.isChecked()){
			return true;
		}
		return false;
	}
	private boolean checkPlayData(){
		if(CHANGE){
			return true;
		}else if(checkMoreChoice()){
			return true;
		}else if(otherInfo()){
			return true;
		}else if(!sids.equals(playinfo.getSids())){
			return true;
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#back()
	 */
	@Override
	protected void back() {
		// TODO Auto-generated method stub
		if(checkPlayData()){
			 backTip();
			}else{
				super.back();
			}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		 if(keyCode == KeyEvent.KEYCODE_BACK){    
				if(checkPlayData()){
					 backTip();
					}else{
						super.back();
					}
		 }
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 返回角色提示框
	 */
	public void backTip(){
		final Dialog dialog = new Dialog(UserPlayEditActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("尚有未发送的内容，确定要返回吗？");
		LinearLayout btnBttom = (LinearLayout)dialog.findViewById(R.id.btn_bottom);
		LinearLayout textBttom =(LinearLayout)dialog.findViewById(R.id.text_bottom);
		Button commit = (Button)dialog.findViewById(R.id.role_commitBtn);
		Button cancle =(Button)dialog.findViewById(R.id.role_cannelBtn);
		btnBttom.setVisibility(View.VISIBLE);
		textBttom.setVisibility(View.GONE);
		dialog.show();
		commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				UserPlayEditActivity.this.finish();
			}
		});
		cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	private void setServerWid(boolean  tag){
		if(tag){
			moreService.setMaxWidth(240);
		}else{
			moreService.setMaxWidth(150);
		}
	}
}
