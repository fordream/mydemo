package com.iwgame.msgs.module.play.ui;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.play.object.PlayVo;
import com.iwgame.msgs.module.postbar.ui.PublishTopicActivity;
import com.iwgame.msgs.module.user.object.UserRoleAttr;
import com.iwgame.msgs.module.user.ui.UserAddRoleInfoActivity;
import com.iwgame.msgs.module.user.ui.UserRoleDetailActivity;
import com.iwgame.msgs.module.user.ui.UserRoleNatureActiviy;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.GameKey;
import com.iwgame.msgs.proto.Msgs.GameKeysDetail;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.proto.Msgs.PlayStar;
import com.iwgame.msgs.proto.Msgs.UserRoleData;
import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr;
import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr.ValData;
import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr.ValData.Builder;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.PhotoUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GameKeyVo;
import com.iwgame.msgs.vo.local.UserRoleVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

public class CreateUserPlayActivity extends BaseActivity implements
		OnClickListener {

	private ImageView gameIcon;
	private TextView gameRole;
	private ImageView addGame;
	private EditText playPay;
	private EditText remark;
	private EditText qqEdit;
	private EditText phoneEdit;
	private ImageView playImage;
	private ImageView deleteImage;
	private CheckBox protocolImage;
	private TextView protocolText;
	private Button compeltePlay;
	private TextView gameService;
	private TextView moreService;
	private RelativeLayout addService;
	private UserRoleData userRole;
	private Uri imageUri;
	private byte[] avatar = null;
	private LinearLayout chooseGame;
	private List<RoleAttr> attrlist = null;
	private long gid;
	private List<GameKeyVo> gamekeylist = null;

	private List<ValData> vallist = new ArrayList<ValData>();

	private List<RoleAttr> rolelist = new ArrayList<RoleAttr>();

	private String name;
	private Bitmap photo = null;
	private Dialog dialog;
	private byte[] image;
	private long keyid;
	private String keyName;
	public Map<Long, UserRoleAttr> attrMap = new HashMap<Long, UserRoleAttr>();
	private Map<Long, View> map = new HashMap<Long, View>();
	private List<ValData> list = new ArrayList<ValData>();
	private List<ValData> exitVal =new ArrayList<ValData>();
	private Map<Long, TextView> choiceMap = new HashMap<Long, TextView>();
	private boolean Image =true;
	private long sid;
	private LinearLayout content;
	private LinearLayout serverContent;
	private static int VALUES_ERROR =-100;
	private static int NO_CAN_EDIT=500601;
	private static int PLAY_ALREADY_EXISTS=500602;
	private static int DAY_CAN_ONCE=500603;
	private static int GAME_PLAY_NO_EXIST=500604;
	private static int EC_MSGS_NOT_SAME_PLAY_GAME = 500605;//陪玩角色不属于所选游戏
	private static int EC_MSGS_USERPLAY_CLOSED = 500606;//陪玩业务已封停
	private static int EC_MSGS_PLAY_STATUS_CLOSED = 500607;//陪玩商品被oa关闭
	private boolean CHANGE = false;
	private CustomProgressDialog downloaddialog;
	private long userid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(UserRoleDetailActivity.vo!=null){
			 userRole=UserRoleDetailActivity.vo;
			 userid =UserRoleDetailActivity.userid;
			 UserRoleDetailActivity.vo =null;
			 LogUtil.d("userRole",userRole.getGid()+"sid"+userRole.getSid());
			 getData();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseSuperActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	private void init() {
		setTitleTxt("创建陪玩信息");
		LinearLayout convertView = (LinearLayout) findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View view = View.inflate(this, R.layout.user_create_play, null);
		convertView.addView(view, params);
		content = (LinearLayout) view
				.findViewById(R.id.create_play_content);
		contentView = (LinearLayout) view
				.findViewById(R.id.play_content_view);
		chooseGame = (LinearLayout) view.findViewById(R.id.choose_play_game);
		serverContent =(LinearLayout)view.findViewById(R.id.play_server_content);
		gameIcon = (ImageView) view.findViewById(R.id.play_game_icon);
		gameRole = (TextView) view.findViewById(R.id.play_game_role);
		addGame = (ImageView) view.findViewById(R.id.play_choose_role);
		playPay = (EditText) view.findViewById(R.id.my_play_pay);
		playPay.setInputType(InputType.TYPE_CLASS_NUMBER);
		remark = (EditText) view.findViewById(R.id.play_remark);
		qqEdit = (EditText) view.findViewById(R.id.play_qq);
		phoneEdit = (EditText) view.findViewById(R.id.play_phone);
		phoneEdit.setInputType(InputType.TYPE_CLASS_PHONE);// 电话
		playImage = (ImageView) view.findViewById(R.id.play_game_image);
		deleteImage = (ImageView) view.findViewById(R.id.delete_play_image);
		protocolImage = (CheckBox) view.findViewById(R.id.play_protocol_check);
		protocolText = (TextView) view.findViewById(R.id.play_protocol);
		compeltePlay = (Button) view.findViewById(R.id.compelte_play);
		gameService = (TextView) view.findViewById(R.id.play_game_service);
		moreService = (TextView) view.findViewById(R.id.other_play_service);
		addService = (RelativeLayout) view.findViewById(R.id.play_add_service);
		deleteImage.setVisibility(View.GONE);
		moreService.setVisibility(View.GONE);
		serverContent.setVisibility(View.GONE);
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
		playPay.setCustomSelectionActionModeCallback(new Callback() {
			
			@Override
			public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode arg0, Menu arg1) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		remark.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent event) {
				// TODO Auto-generated method stub
				return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
			}
		});
		InputFilterUtil.lengthFilter(this, remark, 100, "输入的备注不能超过50个汉字或100个字符哦!");
	}
		private  void setEditTextTip(final String s,final EditText text,final int num){
			 text.addTextChangedListener(new TextWatcher(){  
		            private int selectionStart = 0;  
		            private int selectionEnd = 0;  
		            private CharSequence temp = null;  
		              
		            public void afterTextChanged(Editable arg0) {  
		                // TODO Auto-generated method stub  
		                selectionStart = text.getSelectionStart();  
		                selectionEnd = text.getSelectionEnd(); 
		                temp = text.getText().toString();
		                //ToastUtil.showToast(CreateUserPlayActivity.this,temp.length()+"");
		                if(temp.length() >=num)  
		                {  
		                    Toast.makeText(CreateUserPlayActivity.this, "输入的"+s+"内容，最大不能超过"+num+"个字哦！", Toast.LENGTH_SHORT).show();  
		                    arg0.delete(selectionStart-1, selectionEnd);  
		                    int tempSelection = selectionStart;  
		                   text.setText(arg0);  
		                   text.setSelection(tempSelection);  
		                }                 
		            }  
		  
		            public void beforeTextChanged(CharSequence arg0, int arg1,  
		                    int arg2, int arg3) {  
		                // TODO Auto-generated method stub  
		                temp = arg0;  
		            }  
		  
		            public void onTextChanged(CharSequence arg0, int arg1, int arg2,  
		                    int arg3) {  
		                // TODO Auto-generated method stub  
		  
		            }  
		              
		        });  
		}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.choose_play_game) {
			Intent intent = new Intent(CreateUserPlayActivity.this,
					UserRoleDetailActivity.class);
			intent.putExtra("play", true);
			startActivityForResult(intent, 112);
		} else if (v.getId() == R.id.play_game_image) {
			if(image==null){
				getImage();
			}else {
				jumpImageBrowerAct(photoPath);
			}
		} else if (v.getId() == R.id.compelte_play) {
			if (CheckContent() && protocolImage.isChecked()) {
				
				createPlayInfo(CreateUserPlayActivity.this, 0, image,getPlayInfo(getPlayData()));
				compeltePlay.setClickable(false);
			}
		} else if(v.getId()==R.id.delete_play_image){
			playImage.setImageResource(R.drawable.setting_photo_add_btn_nor);
			Image = false;
			image=null;
			deleteImage.setVisibility(View.GONE);
		}else if(v.getId()==R.id.play_add_service){
//			getGameStar(CreateUserPlayActivity.this, gid);
			startDialog();
		}else if(v.getId()==R.id.play_protocol){
			Intent intent = new Intent(CreateUserPlayActivity.this, PlayProtocolActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("playprotocol", "陪玩协议");
			bundle.putString("playurl", SystemConfig.PLAY_PROTOCOL_ADRRESS);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	public void getData() {
		//userRole =(UserRoleVo) getIntent().getSerializableExtra("gamerole");
		if (userRole != null) {
			CHANGE =true;
			serverContent.setVisibility(View.VISIBLE);
			chooseGame.setTag(true);
			gid = userRole.getGid();
			attrMap.clear();
			map.clear();
			choiceMap.clear();
			list.clear();
			contentView.removeAllViews();
			ImageViewUtil.showImage(gameIcon, userRole.getAvatar(),
					R.drawable.postbar_thumbimg_default);
			gameIcon.setVisibility(View.VISIBLE);
			attrlist = new ArrayList<RoleAttr>();
			attrlist.clear();
			attrlist = userRole.getAttrList();
			
			for (RoleAttr attr : attrlist) {
				if (attr.getAttrtype() == MsgsConstants.GAME_ROLE_KEY_SERVER) {
					gameService.setText(attr.getContent());
					gameService.setTextColor(getResources().getColor(R.color.black));
					sid=attr.getValid();
					moreService.setVisibility(View.GONE);
					setRoleAttr(attr);
				} else if (attr.getAttrtype() == MsgsConstants.GAME_ROLE_KEY_USER) {
					gameRole.setText(attr.getContent());
					gameRole.setTextColor(getResources().getColor(R.color.black));
					name = attr.getContent();
					setRoleAttr(attr);
				}
			}
			setMutipliesServer(gid);
			getRoleInfoData(CreateUserPlayActivity.this, attrlist, gid);
		}else{
			CHANGE =false;
		}
	}
	private String path;
	public void getImage() {
		dialog = new Dialog(CreateUserPlayActivity.this,
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
			
				PhotoUtil.doTakePhoto(CreateUserPlayActivity.this,path);
			}
		});
		photoAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PhotoUtil.doPickPhotoFromGallery(CreateUserPlayActivity.this);
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
	private  String photoPath;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean tag= false;
		Bitmap tempBtm = null;
		byte[] photoByte = null;
		
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case PhotoUtil.CAMERA_WITH_DATA:// 相机
				Display mDisplay = this.getWindowManager().getDefaultDisplay();
				int w = mDisplay.getWidth();
				int h = mDisplay.getHeight();
				int imagewidth = w > h ? h : w;
				imageUri =null;
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
				imageUri = Uri.parse("file://" + path);
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
				tag= true;
				break;
			case 112:
				userRole = (UserRoleData) data.getExtras().getSerializable("gamerole");
				getData();
				break;
			case 101:
				Long id = data.getLongExtra("id", 0);
				String values =data.getExtras().getString("values");
				String name =data.getExtras().getString("name");
				Long key =data.getLongExtra("key", 0);
				if(id==0) return;
				if(values==null)return;
				if(name==null) return;
				if(map.get(id)!=null){
					TextView view = (TextView) map.get(id).findViewById(R.id.play_nature_values);
					view.setText(values);
					view.setTextColor(getResources().getColor(R.color.black));
				}
				//addUserRoleAttr(id, values);
				addUserRoleAttr(id,name,key+"");
				break;
			}
			if(tag){
			if (photo != null) {
				playImage.setImageBitmap(photo);
				dialog.dismiss();
				avatar = photoByte;
				image = null;
				image = photoByte;
				Image = true;
				deleteImage.setVisibility(View.VISIBLE);
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
			}
		} 
		super.onActivityResult(requestCode, resultCode, data);
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
	/**
	 * 获得游戏所对应的属性值
	 * 
	 * @param context
	 */
	public void getRoleInfoData(Context context, final List<RoleAttr> attrlist,
			Long gid) {
		gamekeylist = new ArrayList<GameKeyVo>();
		ProxyFactory.getInstance().getUserProxy()
				.getGameKeyData(new ProxyCallBack<List<GameKeyVo>>() {

					@Override
					public void onSuccess(List<GameKeyVo> result) {
						// TODO Auto-generated method stub
						for (int i = 0; i < result.size(); i++) {
							gamekeylist.add(result.get(i));
						}
						setPlayNature(attrlist, gamekeylist);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub

					}
				}, context, gid);

	}

	public void setPlayNature(List<RoleAttr> attrlist, List<GameKeyVo> list) {
		if (attrlist == null || attrlist.size() <= 0 || list == null
				|| list.size() <= 0)
			return;
		outer: for (int i = 0; i < list.size(); i++) {
			GameKeyVo vo = list.get(i);
			
			for (int j = 0; j < attrlist.size(); j++) {
				RoleAttr attr = attrlist.get(j);
				if (attr.getKeyid() == vo.getId()) {
					if (vo.getAttrType() != MsgsConstants.GAME_ROLE_KEY_SERVER
							&& vo.getAttrType() != MsgsConstants.GAME_ROLE_KEY_USER) {
						addDetailView(attr, vo);
					}
					continue outer;
				} 
			}
			addNewView(vo);
			}

	}

	public void addDetailView(RoleAttr attr, final GameKeyVo key) {
		if (key.getAttrType() != MsgsConstants.GAME_ROLE_KEY_SERVER
				&& key.getAttrType() != MsgsConstants.GAME_ROLE_KEY_USER) {
			if (key.getType() == 1) {
				View view = View.inflate(CreateUserPlayActivity.this,
						R.layout.create_play_item, null);
				TextView name = (TextView) view
						.findViewById(R.id.play_nature_name);
				TextView itemName = (TextView) view
						.findViewById(R.id.play_nature_values);
				itemName.setVisibility(View.GONE);
				EditText edit = (EditText) view
						.findViewById(R.id.play_nature_input);
				edit.setVisibility(View.VISIBLE);
				name.setText(key.getName());
				edit.setText(attr.getContent());
				edit.setTextColor(getResources().getColor(R.color.black));
				setEditText(key, edit);
				contentView.addView(view);
				map.put(key.getId(), view);
				addUserRoleAttr(key.getId(),key.getName(),attr.getContent());
			} else if (key.getType() == 0) {
				View view = View.inflate(CreateUserPlayActivity.this,
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
				name.setText(key.getName());
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
				addUserRoleAttr(key.getId(),key.getName(),attr.getValid()+"");
			} else if (key.getType() == 2) {
				View view = View.inflate(CreateUserPlayActivity.this,
						R.layout.play_reset_choice_item, null);
				TextView natureName = (TextView) view
						.findViewById(R.id.play_reset);
				LinearLayout resetContent = (LinearLayout) view
						.findViewById(R.id.play_reset_content);
				keyid = key.getId();
				keyName = key.getName();
				natureName.setText(key.getName());
				List<GameKeysDetail> gamelist = key.getList();
				addMoreChoiceView(attr, key, gamelist.size(), resetContent,
						key.getName());
				map.put(keyid, view);
				contentView.addView(view);
			}
			
		}
	}
	
	public void addNewView(final GameKeyVo key){
		if (key.getType() == 1) {
			View view = View.inflate(CreateUserPlayActivity.this,
					R.layout.create_play_item, null);
			TextView name = (TextView) view
					.findViewById(R.id.play_nature_name);
			TextView itemName = (TextView) view
					.findViewById(R.id.play_nature_values);
			itemName.setVisibility(View.GONE);
			EditText edit = (EditText) view
					.findViewById(R.id.play_nature_input);
			edit.setVisibility(View.VISIBLE);
			name.setText(key.getName());
			setEditText(key, edit);
			contentView.addView(view);
			map.put(key.getId(), view);
		} else if (key.getType() == 0) {
			View view = View.inflate(CreateUserPlayActivity.this,
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
			name.setText(key.getName());
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					skipListView(key);
				}
			});
			map.put(key.getId(), view);
			contentView.addView(view);
		} else if (key.getType() == 2) {
			View view = View.inflate(CreateUserPlayActivity.this,
					R.layout.play_reset_choice_item, null);
			TextView natureName = (TextView) view
					.findViewById(R.id.play_reset);
			LinearLayout resetContent = (LinearLayout) view
					.findViewById(R.id.play_reset_content);
			keyid = key.getId();
			keyName = key.getName();
			natureName.setText(key.getName());
			List<GameKeysDetail> gamelist = key.getList();
			addMoreChoiceView(null, key, gamelist.size(), resetContent,
					key.getName());
			map.put(keyid, view);
			contentView.addView(view);
		}
	}
	/**
	 * 设置输入监听器
	 * 
	 * @param vo
	 * @param edit
	 */
	public void setEditText(final GameKeyVo vo, final EditText edit) {
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
					addUserRoleAttr(vo.getId(), vo.getName(), edit.getText()
							.toString());
				}
			}
		});
	}
	/**
	 * 处理返回数据
	 * 
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
		List<RoleAttr> attrlist = new ArrayList<RoleAttr>();
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
	 * 跳转取数据
	 * 
	 * @param vo
	 */
	public void skipListView(GameKeyVo vo) {
		Intent intent = new Intent(CreateUserPlayActivity.this,
				UserRoleNatureActiviy.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("gamekey", vo);
		intent.putExtras(bundle);
		startActivityForResult(intent, 101);
	}
	
	/**
	 * 添加更多复选框
	 * 
	 * @param attr
	 * @param key
	 * @param size
	 * @param content
	 */
	public void addMoreChoiceView(RoleAttr attr, GameKeyVo key, int size,
			LinearLayout content, String name) {
		int count = 0;
		int numColumns = 4;
		if (size % 4 == 0) {
			count = size / 4;
		} else {
			count = size / 4 + 1;
		}
		for (int i = 0; i < count; i++) {
			View view = View.inflate(CreateUserPlayActivity.this,
					R.layout.play_more_choose_content, null);
			View text1 = view.findViewById(R.id.choose1);
			View text2 = view.findViewById(R.id.choose2);
			View text3 = view.findViewById(R.id.choose3);
			View text4 = view.findViewById(R.id.choose4);
			for (int j = i * numColumns; j < (i + 1) * numColumns && j < size; j++) {
				final GameKeysDetail vo = key.getList()
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
	

	public void setRoleAttr(RoleAttr attr) {
		RoleAttr.Builder role = RoleAttr.newBuilder();
		role.setKeyid(attr.getKeyid());
		role.setContent(attr.getValid()+"");
		role.setKey(attr.getKey());
		rolelist.add(role.build());
	}

	

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

	public boolean CheckContent() {
		int pay =0;
		if(!playPay.getText().toString().equals("")){
		pay = Integer.parseInt(playPay.getText().toString());
		}
		String qq =qqEdit.getText().toString();
		if(!(Boolean)chooseGame.getTag()){
			ToastUtil.showToast(CreateUserPlayActivity.this, "你还未选择游戏哦！");
			return false;
		}else
		if(!checkNature()){
			return false;
		}else
		if (playPay.getText().toString().equals("")) {
			ToastUtil.showToast(CreateUserPlayActivity.this, "你还未输入收费数目哦！");
			return false;
		}else
		 if(pay<10){
			ToastUtil.showToast(CreateUserPlayActivity.this, "输入的收费数目不能低于10哦！");
			return false;
		}else
		  if(pay>20000){
			ToastUtil.showToast(CreateUserPlayActivity.this, "输入的收费数目不能超过20000哦！");
			return false;
		}else
		  if (pay % 10 != 0) {
			ToastUtil.showToast(CreateUserPlayActivity.this, "输入的收费数目必须为10的倍数哦！");
			return false;
		}else
		  if (remark.getText().toString().equals("")) {
			ToastUtil.showToast(CreateUserPlayActivity.this, "你还未输入备注哦！");
			return false;
		}else
		  if(remark.getText().toString().trim().isEmpty()){
			  ToastUtil.showToast(CreateUserPlayActivity.this, "你输入的备注内容不能全为空格哦！");
			  return false;
		  }else
		   if(ServiceFactory.getInstance().getWordsManager().match(remark.getText().toString())){
			ToastUtil.showToast(CreateUserPlayActivity.this, "输入的备注中有非法字符或敏感词，请重新输入!");
			return false;
		}else
		 if((remark.getText().toString()).length()>50){
			ToastUtil.showToast(CreateUserPlayActivity.this, getResources().getString(R.string.remark_tip));
			return false;
		}else
		 if (qqEdit.getText().toString().equals("")) {
			ToastUtil.showToast(CreateUserPlayActivity.this, "你还未填写QQ号哦!");
			return false;
		}else
		  if(qq.length()>13||qq.length()<5){
			ToastUtil.showToast(CreateUserPlayActivity.this, "你输入的QQ号格式不对哦!（QQ号格式为5~13位数字）");
			return false;
		}else
		   if (phoneEdit.getText().toString().equals("")) {
			ToastUtil.showToast(CreateUserPlayActivity.this, "你还未输入手机号码哦!");
			return false;
		} else
		   if (phoneEdit.getText().toString().length() < 11
					|| phoneEdit.getText().toString().length() > 11) {
				ToastUtil.showToast(CreateUserPlayActivity.this, "请输入正确的手机号码哦!");
				return false;
			}else
		    if (image==null) {
			ToastUtil.showToast(CreateUserPlayActivity.this, "你还未上传图片哦!");
			return false;
		} 
		   
//		    if(!isMobileNO(phoneEdit.getText().toString())){
//				ToastUtil.showToast(CreateUserPlayActivity.this, "请输入正确的手机号码哦！");
//				return false;
//		   }
		     if(!protocolImage.isChecked()){
			ToastUtil.showToast(CreateUserPlayActivity.this, "你还未同意游伴陪玩协议哦！");
			return  false;
		}
		return true;
	}

	public PlayVo getPlayData() {
		PlayVo vo = new PlayVo();
		vo.setCosts(Integer.parseInt(playPay.getText().toString()));
		vo.setGid(userRole.getGid());
		vo.setRemark(remark.getText().toString());
		vo.setRoleData(getUserRoleData());
		vo.setMobile(phoneEdit.getText().toString());
		vo.setQq(qqEdit.getText().toString());
		vo.setRoleid(userRole.getRoleid());
		vo.setSid(sid);
		return vo;
	}

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
		return play.build();
	}

	public void createPlayInfo(Context context, long playid, byte[] image,
			PlayInfo info) {
		downloaddialog = CustomProgressDialog.createDialog(this, false);
		downloaddialog.show();
		ProxyFactory.getInstance().getPlayProxy()
				.createPlayInfo(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						if(downloaddialog.isShowing()){
							downloaddialog.dismiss();
						}
						if (result == 0) {
							createSuccess();
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						downloaddialog.dismiss();
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
							text="陪玩角色不属于所选游戏";
						}else if(result==EC_MSGS_USERPLAY_CLOSED){
							text="由于你的违规操作，陪玩功能已被封停，请去联系管理员！";
						}else if(result==EC_MSGS_PLAY_STATUS_CLOSED){
							text="陪玩商品被oa关闭";
						}
						playErrorDialog(text);
						compeltePlay.setClickable(true);
					}
				}, context, playid, image, info);
	}
	
	public void createSuccess(){
		final Dialog dialog = new Dialog(CreateUserPlayActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("创建成功，等待系统审核，请注意保持QQ或手机畅通，工作人员可能会与您联系确认验证！");
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
				compeltePlay.setClickable(true);
				Intent intent = new Intent(CreateUserPlayActivity.this, MainPlayListActivity.class);
				startActivity(intent);
				CreateUserPlayActivity.this.finish();
			}
		});
	}
	public void playErrorDialog(String text){
		final Dialog dialog = new Dialog(CreateUserPlayActivity.this, R.style.SampleTheme_Light);
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
				downloaddialog.dismiss();
			}
		});
	}
	public void  getGameStar(Context context,long gid){
		ProxyFactory.getInstance().getPlayProxy().getGameStarLeve(new ProxyCallBack<Msgs.PlayStar>() {
			
			@Override
			public void onSuccess(PlayStar result) {
				// TODO Auto-generated method stub
				if(result!=null){
					if(result.getNum()<3){
						startDialog();
					}else{
//						Intent intent = new Intent(CreateUserPlayActivity.this, PlayServiceListActivity.class);
//						CreateUserPlayActivity.this.startActivityForResult(intent, 113);
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
		final Dialog dialog = new Dialog(CreateUserPlayActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("获得三个5星好评可以在编辑陪玩时添加更多服务器哦！");
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
	/**
	 * 
	 * @param path
	 */
	private void jumpImageBrowerAct(String path) {
		
			List<String> pathList = new ArrayList<String>();
			pathList.add(path);
			Intent intent = new Intent(CreateUserPlayActivity.this, ImageBrowerActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, 0);
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, 1);
			bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, pathList.toArray(new String[pathList.size()]));
			bundle.putBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES_ISSHOWREPORTMENU, false);
			intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			CreateUserPlayActivity.this.startActivity(intent);
		}
	public boolean checkNature(){
		for(int i =0;i<gamekeylist.size();i++){
			if(gamekeylist.get(i).getType()==0){
				if(gamekeylist.get(i).getAttrType()!=MsgsConstants.GAME_ROLE_KEY_SERVER){
					TextView text =(TextView) map.get(gamekeylist.get(i).getId()).findViewById(R.id.play_nature_values);
					String data =text.getText().toString();
				if(data.contains(gamekeylist.get(i).getName())&&data.equals("")){
					ToastUtil.showToast(CreateUserPlayActivity.this, "你还未选择你的"+gamekeylist.get(i).getName()+"哦！");
					return false;
				}
				}
			}else if(gamekeylist.get(i).getType()==1){
				if(gamekeylist.get(i).getAttrType()!=MsgsConstants.GAME_ROLE_KEY_USER){
				EditText edit = (EditText)map.get(gamekeylist.get(i).getId()).findViewById(R.id.play_nature_input);
				if(CheckUserRoleName(edit,gamekeylist.get(i))){
				}else{
					return false;
				}
				}
			}else if(gamekeylist.get(i).getType()==2){
				if(list.size()<=0){
					ToastUtil.showToast(CreateUserPlayActivity.this, "你还未选择你的"+gamekeylist.get(i).getName()+"哦！");
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
	public boolean CheckUserRoleName(EditText txt,GameKeyVo vo){
		String moode = txt.getText().toString();
		if (!txt.getText().toString().isEmpty()) {
			if (ServiceFactory.getInstance().getWordsManager().match(moode)) {
				ToastUtil.showToast(CreateUserPlayActivity.this,"您输入的"+vo.getName()+"内容"+CreateUserPlayActivity.this.getResources().getString(R.string.global_words_error2));
				return false;
			} else {
				if(!txt.getText().toString().trim().isEmpty()){
					if(txt.getText().toString().getBytes().length>40){
						ToastUtil.showToast(CreateUserPlayActivity.this,"您输入的"+vo.getName()+"内容"+CreateUserPlayActivity.this.getResources().getString(R.string.role_name_size));
						return false;
					}
				}else{
					ToastUtil.showToast(CreateUserPlayActivity.this,"您输入的"+vo.getName()+"内容"+CreateUserPlayActivity.this.getResources().getString(R.string.role_not_space));
					return false;
				}
			}
		} else {
			ToastUtil.showToast(CreateUserPlayActivity.this, CreateUserPlayActivity.this.getResources().getString(R.string.txt_verify_fail)+vo.getName()+"哦！");
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
		if(!pay.equals("")&&!rek.equals("")&&!qq.equals("")&&!phone.equals("")&&image!=null){
			return true;
		}
		else if(!protocolImage.isChecked()){
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
		final Dialog dialog = new Dialog(CreateUserPlayActivity.this, R.style.SampleTheme_Light);
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
				// TODO Auto-generated method stub
				CreateUserPlayActivity.this.finish();
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
	/**
	 * 判断手机号格式
	 * @param mobiles
	 * @return
	 */
	public boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
		.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);

		return m.matches();
		}
}
