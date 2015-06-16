/**      
 * EditDetailActivity.java Create on 2013-11-20     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.ui;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.proto.Msgs.ErrorCode;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.PhotoUtil;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: EditDetailActivity
 * @Description: 编辑公会资料
 * @author 王卫
 * @date 2013-11-20 下午9:21:39
 * @Version 1.0
 * 
 */
public class EditDetailActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "EditDetailActivity";
	// 公会ID
	private long grid;
	// 详细信息
	private GroupVo groupVo;
	// 名称
	private LinearLayout groupNameItem;
	// 描述
	private LinearLayout descItem;
	// 贴吧公告
	private LinearLayout noticeItem;
	// 名称
	private TextView groupName;
	// 描述
	private TextView desc;
	// 贴吧标签
	private TextView notice;
	// 头像
	private ImageView avatar;

	private Uri imageUri;
	
	private Bitmap photo = null;
	
	private CustomProgressDialog dialog;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			grid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);
		}
		init();
		initialize();
	}

	/**
	 * 进入这个页面的时候， 先做一些初始化工作
	 */
	private void init() {
		// 显示左边
		setLeftVisible(true);
		// 影藏右边
		setRightVisible(false);
		titleTxt.setText("编辑公会资料");
		dialog = CustomProgressDialog.createDialog(this, true);
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View detailView = (LinearLayout) View.inflate(this, R.layout.group_detail_edit, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(detailView, params);
		// 设置UI元素
		avatar = (ImageView) contentView.findViewById(R.id.icon);
		groupName = (TextView) contentView.findViewById(R.id.groupName);
		desc = (TextView) contentView.findViewById(R.id.desc);
		notice = (TextView) contentView.findViewById(R.id.notice);

		groupNameItem = (LinearLayout) contentView.findViewById(R.id.groupNameItem);
		descItem = (LinearLayout) contentView.findViewById(R.id.descItem);
		noticeItem = (LinearLayout) contentView.findViewById(R.id.noticeItem);

		avatar.setOnClickListener(this);
		findViewById(R.id.icon).setOnClickListener(this);
		groupNameItem.setOnClickListener(this);
		descItem.setOnClickListener(this);
		noticeItem.setOnClickListener(this);
	}

	/**
	 * 初始化
	 */
	private void initialize() {
		if (dialog != null)
			dialog.show();
		GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
		GroupVo groupvo = groupDao.findGroupByGrid(grid);
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(grid);
		cdp.setUtime(groupvo == null ? 0 : groupvo.getUtime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(new ProxyCallBack<List<GroupVo>>() {

			@Override
			public void onSuccess(List<GroupVo> groupvoresult) {
				if(groupvoresult == null || groupvoresult.size() <= 0) return;
				groupVo = groupvoresult.get(0);
				if (dialog != null)
					dialog.dismiss();
				setUI(groupVo);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (dialog != null)
					dialog.dismiss();
				LogUtil.e(TAG, "获取公会资料失败");
			}
		}, this, p.build(), 5, null);
	}

	/**
	 * 根据我和公会的关系设置UI元素
	 * 
	 * @param rel
	 */
	private void setUI(GroupVo grVo) {
		if (grVo != null) {
			groupName.setText(grVo.getName());
			if (grVo.getUndesc() != null)
				desc.setText(grVo.getUndesc());
			if (grVo.getNotice() != null)
				notice.setText(grVo.getNotice());
			// 显示头像
			ImageViewUtil.showImage(avatar, grVo.getAvatar(), R.drawable.common_default_icon);
		} else {
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == groupNameItem.getId()) {
			v.setEnabled(false);
			modifyGroupName(v);
		} else if (v.getId() == descItem.getId()) {
			v.setEnabled(false);
			modifyGroupDesc(v);
		} else if (v.getId() == noticeItem.getId()) {
			v.setEnabled(false);
			modifyGroupNotic(v);
		} else if (v.getId() == R.id.icon) {
			modifyGroupAvatar();
		}
	}

	/**
	 * 修改公会名称
	 */
	private void modifyGroupName(View v) {
		if(dialog != null) dialog.show();
		showModifyTextView(v, this, groupName, 1, getString(R.string.group_name_verify_fail, 10, 20), 20, "编辑公会名称", new Executant() {

			@Override
			public void extcute(final String content) {
				ProxyFactory.getInstance().getGroupProxy().creatOrUpdataGroup(new ProxyCallBack<List<Object>>() {

					@Override
					public void onSuccess(List<Object> result) {
						if(dialog != null) dialog.dismiss();
						switch ((Integer) result.get(0)) {
						case ErrorCode.EC_OK_VALUE:
							groupName.setText(content);
							ToastUtil.showToast(EditDetailActivity.this, getString(R.string.group_modify_success));
							// 编辑公会成功后，更新数据库里面的数据
							GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
							groupVo.setName(content);
							groupDao.updateOrInsertById(groupVo);
							break;
						default:
							break;
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						if(dialog != null) dialog.dismiss();
					}
				}, EditDetailActivity.this, content, null, null, null, null, null, grid);
			}
		}, 1);
	}

	/**
	 * 修改公会描述
	 */
	private void modifyGroupDesc(View v) {
		if(dialog != null) dialog.show();
		showModifyTextView(v, this, desc, 5, getString(R.string.group_desc_verify_fail, 50, 100), 100, "编辑公会简介", new Executant() {

			@Override
			public void extcute(final String content) {
				ProxyFactory.getInstance().getGroupProxy().creatOrUpdataGroup(new ProxyCallBack<List<Object>>() {

					@Override
					public void onSuccess(List<Object> result) {
						if(dialog != null) dialog.dismiss();
						switch ((Integer) result.get(0)) {
						case ErrorCode.EC_OK_VALUE:
							desc.setText(content);
							ToastUtil.showToast(EditDetailActivity.this, getString(R.string.group_modify_success));
							GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
							groupVo.setUndesc(content);
							groupDao.updateOrInsertById(groupVo);
							break;
						default:
							break;
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						if(dialog != null) dialog.dismiss();
					}
				}, EditDetailActivity.this, null, null, null, content, null, null, grid);
			}
		}, 0);
	}

	/**
	 * 修改公会公告
	 */
	private void modifyGroupNotic(View v) {
		if(dialog != null) dialog.show();
		showModifyTextView(v, this, notice, 5, getString(R.string.group_notice_verify_fail, 20, 40), 40, "编辑公会公告", new Executant() {

			@Override
			public void extcute(final String content) {
				ProxyFactory.getInstance().getGroupProxy().creatOrUpdataGroup(new ProxyCallBack<List<Object>>() {

					@Override
					public void onSuccess(List<Object> result) {
						if(dialog != null) dialog.dismiss();
						switch ((Integer) result.get(0)) {
						case ErrorCode.EC_OK_VALUE:
							notice.setText(content);
							ToastUtil.showToast(EditDetailActivity.this, getString(R.string.group_modify_success));
							GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
							groupVo.setNotice(content);
							groupDao.updateOrInsertById(groupVo);
							break;
						default:
							break;
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						if(dialog != null) dialog.dismiss();
					}
				}, EditDetailActivity.this, null, null, null, null, content, null, grid);
			}
		}, 0);
	}

	/**
	 * 修改公会头像
	 */
	private void modifyGroupAvatar() {
		// 头像对话框
		PhotoUtil.showSelectDialog(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.d(TAG, "resultCode =" + resultCode + ";requestCode=" + requestCode);
//		if(photo != null && !photo.isRecycled()){
//			photo.recycle();
//			photo = null;
//			//System.gc();
//		}
		Bitmap tempBtm = null;
		byte[] photoByte = null;
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
					}catch(OutOfMemoryError e){
						e.printStackTrace();
					}catch (Exception e) {
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
						}catch(OutOfMemoryError e){
							e.printStackTrace();
						}catch (Exception e) {
							e.printStackTrace();
						}
						photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, 30);
					}
				}
				break;
			}
			LogUtil.d(TAG, "photo =" + photo);
			if (photo != null) {
				// 添加照片显示
				showImage(avatar, photo, photoByte);
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
				LogUtil.w(TAG, "获得需要发送的图片异常");
				return;
			}
		} else {
			LogUtil.w(TAG, "选择发送的图片异常");
		}
	}

	/**
	 * 显示图片
	 */
	private void showImage(final ImageView iv, final Bitmap bmp, byte[] photoByte) {
		// 添加照片显示
		if (bmp != null) {
			ProxyFactory.getInstance().getGroupProxy().creatOrUpdataGroup(new ProxyCallBack<List<Object>>() {

				@Override
				public void onSuccess(List<Object> result) {
					switch ((Integer) result.get(0)) {
					case ErrorCode.EC_OK_VALUE:
						if (iv != null)
							iv.setImageBitmap(bmp);
						ToastUtil.showToast(EditDetailActivity.this, getString(R.string.group_modify_success));
						break;
					default:
						break;
					}
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
				}
			}, EditDetailActivity.this, null, photoByte, null, null, null, null, grid);
		} else {
			return;
		}
	}

	/**
	 * 显示修改文本信息
	 * 
	 * @param context
	 * @param tv
	 * @param height
	 * @param errorTip
	 * @param len
	 * @param titleTxt
	 * @param executant
	 */
	private void showModifyTextView(final View v, final Context context, final TextView tv, int height, String errorTip, int len, String titleTxt,
			final Executant executant, final int matchType) {
		final Dialog dialog = new Dialog(context, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		final EditText txt = new EditText(context);
		txt.setTextColor(getResources().getColor(R.color.edit_group_info_color));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText(tv.getText());
		txt.setSelection(tv.getText().toString().trim().length());
		txt.setMaxLines(height);
		txt.setBackgroundResource(R.drawable.common_edit_text_bg);
		content.setPadding(20, 0, 20, 0);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		InputFilterUtil.lengthFilter(context, txt, len, errorTip);
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText(titleTxt);
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String scontent = txt.getText().toString();
				if (!scontent.isEmpty()) {
					boolean ismatch = true;
					if (matchType == 0)
						ismatch = ServiceFactory.getInstance().getWordsManager().match(scontent);
					else if (matchType == 1)
						ismatch = ServiceFactory.getInstance().getWordsManager().matchName(scontent);
					if (ismatch) {
						ToastUtil.showToast(EditDetailActivity.this, EditDetailActivity.this.getResources().getString(R.string.global_words_error));
					} else {
						executant.extcute(txt.getText().toString());
						dialog.dismiss();
					}
				} else {
					ToastUtil.showToast(context, getString(R.string.txt_verify_fail));
				}
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				dismissDialog();
			}
		});

		// 对话框取消的时候，把按钮的enable属性设置成为为true
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				v.setEnabled(true);
			}
		});
		dialog.show();
	}

	
	/**
	 * 取消全局的dialog
	 */
	private void dismissDialog(){
		if(dialog != null) dialog.dismiss();
	}
	
	
	interface Executant {

		public void extcute(String content);

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(photo != null && !photo.isRecycled()){
			photo.recycle();
			photo = null;
			//System.gc();
		}
	}
}
