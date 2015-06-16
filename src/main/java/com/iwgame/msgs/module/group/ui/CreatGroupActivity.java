/**      
 * CreatGropActivity.java Create on 2013-10-23     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.msgs.common.LocationCallBack;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.account.ui.register.ProtocolAcitivity;
import com.iwgame.msgs.module.game.ui.GameDetailGroupActivity;
import com.iwgame.msgs.module.group.adapter.GameLableAdapter;
import com.iwgame.msgs.module.group.adapter.GroupUserAdapter;
import com.iwgame.msgs.module.remote.UserRemoteService;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.proto.Msgs.ErrorCode;
import com.iwgame.msgs.utils.PhotoUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.xaction.proto.XAction.XActionResult;
import com.youban.msgs.R;

/**
 * @ClassName: CreatGropActivity
 * @Description: 创建公会界面
 * @author 王卫
 * @date 2013-10-23 上午11:51:52
 * @Version 1.0
 * 
 */
public class CreatGroupActivity extends BaseSuperActivity implements OnClickListener {

	private static final String TAG = "CreatGropActivity";
	// 设置头像按钮
	private ImageView avatarView;
	// 完成提交菜单
	private ImageView commitBtn;
	// 头像数据
	private byte[] avatar;
	
	private Bitmap photo = null;
	// 公会名称
	private EditText nameTxt;
	// 公会简介
	private EditText groupDesTxt;
	// 所属贴吧
	private LinearLayout gameChooseItem;
	private TextView gameLable;
	// 所选贴吧的ID
	private Long gid;
	// 所选贴吧的名称
	private String gname;
	private boolean hasGame = false;
	// 是否需要验证
	private Button verifyView;
	// 是否要验证
	private boolean isVerify = false;
	// 协议勾选复选框
	private CheckBox checkBox;
	private Uri imageUri;
	private Dialog sucDialog;
	private TextView cueWords;
	private Button cancelBtn;
	private Button confirmBtn;
	private Dialog exitDialog;
	private TextView exitword;
	private LinearLayout exitContent;
	private Button cancelExitBtn;
	private Button commitExitBtn;
	private long groupId;
	private TextView titleTxt;
	private Button backBtn;
	private ImageView rightImage;
	private List<GameVo>gameVoList;
	private TextView tiaoKuan;
	private UserRemoteService userService = ServiceFactory.getInstance().getUserRemoteService();

	/**
	 * 初始化页面
	 * 以及初始化当前页面
	 * 需要用到的一些dialog
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
		setView();
	}

	/**
	 * 初始化一个dialog
	 */
	private void setView() {
		exitDialog = new Dialog(CreatGroupActivity.this, R.style.SampleTheme_Light);
		exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		exitDialog.setContentView(R.layout.dialog);
		exitword = (TextView)exitDialog.findViewById(R.id.title);
		exitContent = (LinearLayout)exitDialog.findViewById(R.id.content);
		cancelExitBtn = (Button)exitDialog.findViewById(R.id.cannelBtn);
		commitExitBtn = (Button)exitDialog.findViewById(R.id.commitBtn);
		exitword.setText("提示");
		TextView txt = new TextView(this);
		txt.setTextColor(getResources().getColor(R.color.dialog_font_color));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText("还未完成编辑，确定返回？");
		exitContent.setPadding(DisplayUtil.dip2px(this, 10), 10, DisplayUtil.dip2px(this, 10), 10);
		exitContent.removeAllViews();
		exitContent.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		cancelExitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				exitDialog.dismiss();				
			}
		});
		commitExitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CreatGroupActivity.this.finish();
			}
		});
		sucDialog = new Dialog(CreatGroupActivity.this,R.style.SampleTheme_Light);
		sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		sucDialog.setContentView(R.layout.dialog_upgrade_group);
		sucDialog.setCanceledOnTouchOutside(false);
		cueWords = (TextView)sucDialog.findViewById(R.id.upgrade_group_success);
		cueWords.setText("创建公会成功，马上去邀请成员！");
		cancelBtn = (Button)sucDialog.findViewById(R.id.cannelBtn);
		confirmBtn = (Button)sucDialog.findViewById(R.id.commitBtn);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				sucDialog.dismiss();
				if (hasGame) {
					Intent intent = new Intent(CreatGroupActivity.this, GameDetailGroupActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gid);
					bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME, gname);
					intent.putExtras(bundle);
					startActivity(intent);
					CreatGroupActivity.this.finish();
				}else{
					CreatGroupActivity.this.finish();
				}
			}
		});

		confirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CreatGroupActivity.this, GroupManageUserListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOOD, GroupUserAdapter.MODE_INVITE);
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, groupId);
				intent.putExtras(bundle);
				CreatGroupActivity.this.startActivity(intent);
				sucDialog.dismiss();
				CreatGroupActivity.this.finish();
			}
		});
	}


	/**
	 * 对当前页面的
	 * 一些初始化操作
	 */
	private void initialize() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			gid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID);
			gname = bundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME);
			hasGame = true;
		}
		setContentView(R.layout.group_creat);
		backBtn = (Button)findViewById(R.id.leftBtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(exitDialog != null)
					exitDialog.show();
			}
		});
		commitBtn = new ImageView(this);
		commitBtn.setBackgroundResource(R.drawable.account_register_commit_btn);
		LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rightView.addView(commitBtn, params);
		commitBtn.setOnClickListener(this);
		// 设置TITLE
		titleTxt = (TextView)findViewById(R.id.titleTxt);
		titleTxt.setText(getString(R.string.group_creat_title));
		// 设置头像按钮
		avatarView = (ImageView) findViewById(R.id.icon);
		avatarView.setOnClickListener(this);
		// 设置公会名称文本框
		nameTxt = (EditText) findViewById(R.id.nameTxt);
		InputFilterUtil.lengthFilter(this, nameTxt, 20, getString(R.string.group_create_name_verify_fail, 10,20));
		// 设置公会简介文本框
		groupDesTxt = (EditText) findViewById(R.id.groupDesTxt);
		InputFilterUtil.lengthFilter(this, groupDesTxt, 100, getString(R.string.group_create_desc_verify_fail, 50,100));
		//选择贴吧游戏右边的图片
		rightImage = (ImageView)findViewById(R.id.gameJT);
		// 设置所属贴吧
		gameLable = (TextView) findViewById(R.id.gameLable);
		gameChooseItem = (LinearLayout) findViewById(R.id.gameChooseItem);
		if (hasGame) {
			gameLable.setText(gname);
			findViewById(R.id.gameJT).setVisibility(View.INVISIBLE);
		} else {
			gameChooseItem.setOnClickListener(this);
		}
		// 设置所属贴吧
		verifyView = (Button) findViewById(R.id.verifyView);
		verifyView.setOnClickListener(this);
		// 设置复选框
		checkBox = (CheckBox) findViewById(R.id.act_reg_checkBox);
		tiaoKuan = (TextView)findViewById(R.id.sproto);
		tiaoKuan.setOnClickListener(this);
		tiaoKuan.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		//这里面去查询贴吧的数据
		getGameNum();//去获取贴吧的数据，如果只关注了一个贴吧 ，直接显示到界面上，如果关注了多个贴吧 ，则弹出框让用户选择
	}

	/**
	 * 获取当前登录的用户
	 * 的贴吧的数据
	 * 如果是单攻略，则直接把当前用户的关注的游戏写死到界面
	 * 如果是游伴，如果只关注了一款游戏，则把游戏直接写死到界面 ，
	 * 如果 关注了多款游戏，则弹出一个对话框 
	 */
	private void getGameNum() {
		AppConfig config = AdaptiveAppContext.getInstance().getAppConfig();
		int mode = config.getMode();
		final long gameId = config.getGameId();
		switch (mode) {
		case 1://表示的是游伴
			ProxyFactory.getInstance().getGameProxy().getFollowGames(new ProxyCallBack<List<GameVo>>() {

				@Override
				public void onSuccess(List<GameVo> result) {
					if(result != null && result.size() == 1){
						rightImage.setVisibility(View.GONE);
						gameChooseItem.setEnabled(false);
						gameChooseItem.setBackgroundResource(R.color.set_common_item_nor_shap);
						gameLable.setText(result.get(0).getGamename());
						gid = result.get(0).getGameid();
					}else if(result != null && result.size() > 1){
						rightImage.setVisibility(View.VISIBLE);
						gameVoList = result;
						gameChooseItem.setEnabled(true);
						gameChooseItem.setBackgroundResource(R.drawable.common_item_top_selector);
					}
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					gameChooseItem.setEnabled(true);
					rightImage.setVisibility(View.VISIBLE);
					gameChooseItem.setBackgroundResource(R.drawable.common_item_top_selector);
				}

			}, this, true);

			break;

		case 2://表示的是单攻略
			ServiceFactory.getInstance().getSyncEntityService().syncEntity(gameId, SyncEntityService.TYPE_GAME, new SyncCallBack() {

				@Override
				public void onSuccess(Object result) {
					GameDao dao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
					GameVo vo = dao.getGameByGameId(gameId);
					rightImage.setVisibility(View.GONE);
					gameChooseItem.setEnabled(false);
					gameChooseItem.setBackgroundResource(R.color.set_common_item_nor_shap);
					gameLable.setText(vo.getGamename());
					gid = vo.getGameid();
				}

				@Override
				public void onFailure(Integer result) {

				}
			});
			break;
		}
	}


	/**
	 * 弹出绑定手机的对话框
	 * 如果创建公会的时候，没有绑定手机号
	 * 则点击创建公会时，跳转到绑定手机号页面
	 * @param actionName
	 */
	private void createBundPhoneDialog(){
		Intent intent = new Intent(CreatGroupActivity.this, BundPhoneActivity.class);
		CreatGroupActivity.this.startActivity(intent);
	}


	@Override
	protected void onResume() {
		super.onResume();
		commitBtn.setEnabled(true);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v == avatarView) {
			// 头像对话框
			PhotoUtil.showSelectDialog(this);
		} else if (v.getId() == R.id.gameChooseItem) {
			// 贴吧选择按钮
			setGameLable();
		} else if (v.getId() == commitBtn.getId()) {
			// 创建公会
			commitBtn.setEnabled(false);
			String name = nameTxt.getText().toString();
			String desc = groupDesTxt.getText().toString();
			if (ServiceFactory.getInstance().getWordsManager().matchName(name)) {
				ToastUtil.showToast(this, getResources().getString(R.string.group_words_name_error));
				commitBtn.setEnabled(true);
				return;
			}
			if (ServiceFactory.getInstance().getWordsManager().match(desc)) {
				ToastUtil.showToast(this, getResources().getString(R.string.group_words_desc_error));
				commitBtn.setEnabled(true);
				return;
			}
				creatGroup();

		} else if (v.getId() == R.id.verifyView) {
			isVerify = !isVerify;
			if (isVerify) {
				verifyView.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
			} else {
				verifyView.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
			}
		} else if (v.getId() == R.id.sproto) {
			jumpProtocolAct("服务条款", SystemConfig.GROUP_PROTOCOL_SERVICE);
		}
	}

	private void jumpProtocolAct(String title, String url) {
		Intent intent = new Intent(this, ProtocolAcitivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TITLE, title);
		bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_URL, url);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.d(TAG, "resultCode =" + resultCode + ";requestCode=" + requestCode);
		Bitmap tempBtm = null;
		byte[] photoByte = null;
		avatar = null;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case PhotoUtil.CAMERA_WITH_DATA:
				Display mDisplay = this.getWindowManager().getDefaultDisplay();
				int w = mDisplay.getWidth();
				int h = mDisplay.getHeight();
				int imagewidth = w > h ? h : w;
				imageUri = Uri.parse("file://" + PhotoUtil.sdcardTempFilePath);
				PhotoUtil.doCropBigPhoto(this, imageUri, imageUri, 1, 1, imagewidth, imagewidth);
				return;
			case PhotoUtil.PHOTO_PICKED_WITH_DATA:
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				Display mDisplay2 = this.getWindowManager().getDefaultDisplay();
				int w2 = mDisplay2.getWidth();
				int h2 = mDisplay2.getHeight();
				int imagewidth2 = w2 > h2 ? h2 : w2;
				imageUri = Uri.parse("file://" + PhotoUtil.sdcardTempFilePath);
				PhotoUtil.doCropBigPhoto(this, originalUri, imageUri, 1, 1, imagewidth2, imagewidth2);
				return;
			case PhotoUtil.CROP_IMAGE_WITH_DATA:
				if (data != null && data.getParcelableExtra("data") != null) {
					try {
						tempBtm = photo;
						photo = data.getParcelableExtra("data");
					} catch (Exception e) {
						e.printStackTrace();
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
					if (photo != null) {
						photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, 30);
					}
				} else {
					if (imageUri != null) {
						try {
							tempBtm = photo;
							photo = ImageUtil.decodeUri2Bitmap(this.getContentResolver(), imageUri);
						} catch (Exception e) {
							e.printStackTrace();
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
						}
						if(photo != null){
							photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, 30);
						}
					}
				}
				break;
			}
			LogUtil.d(TAG, "photo =" + photo);
			if (photo != null) {
				avatarView.setImageBitmap(photo);
				LogUtil.i(TAG, "---->CreatGropActivity::onActivityResult:bmp=" + photo);
				avatar = photoByte;
				if(tempBtm != null && !tempBtm.isRecycled()){
					tempBtm.recycle();
					tempBtm = null;
					//System.gc();
				}
			} else {
				if(tempBtm != null && !tempBtm.isRecycled()){
					tempBtm.recycle();
					tempBtm = null;
					//System.gc();
				}
				ToastUtil.showToast(this, getResources().getString(R.string.common_add_photo_error));
				LogUtil.e(TAG, "获得需要发送的图片异常");
				return;
			}
		} else {
			LogUtil.e(TAG, "选择发送的图片异常");
		}
	}

	/**
	 * 设置所属贴吧
	 */
	private void setGameLable() {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("所属贴吧");
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		dialog.findViewById(R.id.bottom).setVisibility(View.GONE);
		// 添加列表
		LinearLayout listContentView = (LinearLayout) View.inflate(this, R.layout.group_creat_choose_game_list, null);
		final ListView listView = (ListView) listContentView.findViewById(R.id.listView);
		getListData(listView, dialog);
		content.removeAllViews();
		content.addView(listContentView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		dialog.show();
	}

	/**
	 * 添加列表点击功能
	 * 
	 * @param list
	 */
	private void setListItemClikEvent(ListView list, final Dialog dialog) {
		// 添加列表点击功能
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
				if (map.get("gid") != null) {
					gid = (Long) map.get("gid");
					gameLable.setText((String) map.get("gamename"));
					if (dialog != null)
						dialog.dismiss();
				}
			}
		});
	}

	/**
	 * 查找贴吧列表数据
	 */
	private void getListData(final ListView list, final Dialog listDialog) {
		if(gameVoList != null && gameVoList.size() > 1){

			// 设置LIST数据
			final GameLableAdapter adapter = new GameLableAdapter(CreatGroupActivity.this, praseList(gameVoList),
					R.layout.group_game_lable_list_item, new String[] { "gamename" }, new int[] { R.id.gamename });
			list.setAdapter(adapter);
			// 添加列表点击功能
			setListItemClikEvent(list, listDialog);
			return;
		}
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getGameProxy().getFollowGames(new ProxyCallBack<List<GameVo>>() {

			@Override
			public void onSuccess(List<GameVo> result) {
				// 设置LIST数据
				final GameLableAdapter adapter = new GameLableAdapter(CreatGroupActivity.this, praseList(result),
						R.layout.group_game_lable_list_item, new String[] { "gamename" }, new int[] { R.id.gamename });
				list.setAdapter(adapter);
				// 添加列表点击功能
				setListItemClikEvent(list, listDialog);
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				dialog.dismiss();
			}

		}, this, true);
	}

	/**
	 * 解析用户数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseList(List<GameVo> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				GameVo vo = list.get(i);
				map.put("logo", vo.getGamelogo());
				map.put("gamename", vo.getGamename());
				map.put("gid", vo.getGameid());
				tmplist.add(map);
			}
		}
		return tmplist;
	}

	/**
	 * 创建公会
	 */
	private void creatGroup() {
		// 判断头像不为空
		if (avatar != null) {
			// 判断昵称不为空
			if (nameTxt.getText() != null && !nameTxt.getText().toString().isEmpty()) {
				// 判断公会简介是否为空
				if (groupDesTxt.getText() != null && !groupDesTxt.getText().toString().isEmpty()) {
					if (checkBox.isChecked()) {
						if (gid != null) {
							commitBtn.setEnabled(false);
							// 提交
							final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
							dialog.show();
							ProxyFactory.getInstance().getGroupProxy().creatOrUpdataGroup(new ProxyCallBack<List<Object>>() {

								@Override
								public void onSuccess(List<Object> result) {
									sendPosition();
									commitBtn.setEnabled(true);
									dialog.dismiss();
									switch ((Integer)result.get(0)) {
									case ErrorCode.EC_OK_VALUE:
										groupId = (Long)result.get(1);
										Log.i("groupid", result.get(1)+"");
										SystemContext.getInstance().setPoint(SystemContext.getInstance().getPoint() - 50);//更新当前用户的积分保存到偏好设置中
										sucDialog.show();
										ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GROUP, new SyncCallBack() {

											@Override
											public void onSuccess(Object result) {
												
											}

											@Override
											public void onFailure(Integer result) {
												
											}});
										break;

									default:
										break;
									}
								}

								@Override
								public void onFailure(Integer result, String resultMsg) {
									dialog.dismiss();
									commitBtn.setEnabled(true);
									switch (result) {

									case ErrorCode.EC_MSGS_OPERATOR_SELF_VALUE:
										break;
									case ErrorCode.EC_MSGS_INFO_NOT_FULL_VALUE:
										ToastUtil.showToast(CreatGroupActivity.this,
												CreatGroupActivity.this.getResources().getString(R.string.group_creat_info_not_full));
										break;
									case ErrorCode.EC_MSGS_POINT_NOT_ENOUGH_VALUE:
										ToastUtil.showToast(CreatGroupActivity.this,
												CreatGroupActivity.this.getResources().getString(R.string.group_creat_info_point_notenough));
										break;
									default:
										ToastUtil.showToast(CreatGroupActivity.this,
												CreatGroupActivity.this.getResources().getString(R.string.group_creat_fail));
										break;
									}

								}
							}, this, nameTxt.getText().toString(), avatar, gid, groupDesTxt.getText().toString(), null, isVerify, null);
						} else {
							ToastUtil.showToast(this, getString(R.string.group_gid_isnull));
							commitBtn.setEnabled(true);
						}
					} else {
						ToastUtil.showToast(this, getString(R.string.group_service_verify_fail));
						commitBtn.setEnabled(true);
					}
				} else {
					ToastUtil.showToast(this, getString(R.string.group_des_isnull));
					commitBtn.setEnabled(true);
				}
			} else {
				ToastUtil.showToast(this, getString(R.string.group_name_isnull));
				commitBtn.setEnabled(true);
			}
		} else {
			ToastUtil.showToast(this, getString(R.string.group_avatar_isnull));
			commitBtn.setEnabled(true);
		}
	}

	/**
	 * 创建公会成功的时候
	 * 向服务端发送位置信息
	 */
	private void sendPosition(){
		ServiceFactory.getInstance().getBaiduLocationService().requestLocation(new LocationCallBack() {

			@Override
			public void onBack(BDLocation bdLocation) {
				SystemContext.getInstance().setLocation(bdLocation);
				userService.setPosition(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						LogUtil.d(TAG, "---->向服务端设置位置成功");
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						LogUtil.d(TAG, "---->向服务端设置位置失败");
					}
				}, null, SystemContext.getInstance().getLocation());
			}
		});

	}


	/**
	 * 当点击返回键的时候
	 * 弹出提示框
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
			exitDialog.show();
		return super.onKeyDown(keyCode, event);
	}


	/**
	 * 在onstop方法里面
	 * 取消所弹的对话框 
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if(sucDialog != null) sucDialog.dismiss();
		if(exitDialog != null) exitDialog.dismiss();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(photo != null && !photo.isRecycled()){
			photo.recycle();
			photo = null;
		}
	}
}
