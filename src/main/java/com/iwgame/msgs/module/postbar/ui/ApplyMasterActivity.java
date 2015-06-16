/**      
 * ApplyMasterActivity.java Create on 2013-12-26     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.ui;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.StrictModeWrapper;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.UserActionDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.utils.EditTextUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.PhotoUtil;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: ApplyMasterActivity
 * @Description: TODO(申请吧主)
 * @author chuanglong
 * @date 2013-12-26 下午5:52:57
 * @Version 1.0
 * 
 */
public class ApplyMasterActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "ApplyMasterActivity";

	private ImageView postbar_applymaster_info_del;
	private EditText postbar_applymaster_name;
	private EditText postbar_applymaster_cardid;
	private Button apply_master_name_closeBtn;
	private Button apply_master_cardid_closeBtn;
	private ImageButton postbar_applymaster_cardimg;
	private EditText postbar_applymaster_content;

	private long gid;
	// 发送资源内容
	byte[] resource = null;
	
	private Bitmap btm = null;

	/**
	 * 是否按了确认发布按钮
	 */
	private boolean isOnClickSend = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    StrictModeWrapper.init(this);
	    super.onCreate(savedInstanceState);

		// 获取上一个页面的传值
		Intent tmpintent = this.getIntent();
		if (tmpintent != null) {
			Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
			if (tmpbundle != null) {
				gid = tmpbundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID);
			}
		}

		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 设置显示top左边
		setLeftVisible(true);
		// 设置包显示top右边
		setRightVisible(true);

		TextView topRightTextView = new TextView(this);
		topRightTextView.setText(R.string.postbar_applymaster_ok);
		topRightTextView.setTextColor(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_title_textcolor());
		// topRightTextView.setTextSize(R.dimen.text_large);
		LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
		rightView.addView(topRightTextView);
		rightView.setOnClickListener(this);

		setTitleTxt(getResources().getString(R.string.postbar_applymaster_activity_title));

		// 设置中间内容
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View view = (LinearLayout) View.inflate(this, R.layout.postbar_applymaster, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		contentView.addView(view,params);

		postbar_applymaster_info_del = (ImageView) view.findViewById(R.id.postbar_applymaster_info_del);
		postbar_applymaster_name = (EditText) view.findViewById(R.id.postbar_applymaster_name);
		postbar_applymaster_cardid = (EditText) view.findViewById(R.id.postbar_applymaster_cardid);
		apply_master_name_closeBtn = (Button) view.findViewById(R.id.apply_master_name_closeBtn);
		apply_master_cardid_closeBtn = (Button) view.findViewById(R.id.apply_master_cardid_closeBtn);
		postbar_applymaster_cardimg = (ImageButton) view.findViewById(R.id.postbar_applymaster_cardimg);
		postbar_applymaster_content = (EditText) view.findViewById(R.id.postbar_applymaster_content);

		
		apply_master_name_closeBtn.setOnClickListener(this);
		apply_master_cardid_closeBtn.setOnClickListener(this);
		EditTextUtil.ChangeCleanTextButtonVisible(postbar_applymaster_name, apply_master_name_closeBtn);
		EditTextUtil.ChangeCleanTextButtonVisible(postbar_applymaster_cardid, apply_master_cardid_closeBtn);
		
		final View postbar_applymaster_info = view.findViewById(R.id.postbar_applymaster_info);
		postbar_applymaster_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				postbar_applymaster_info.setVisibility(View.GONE);
			}
		});

		postbar_applymaster_cardimg.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.rightView) {
			applyPostbarMaster();
		} else if (v.equals(postbar_applymaster_cardimg)) {
			PhotoUtil.showSelectDialog(this);
		} else if(v.getId() == R.id.apply_master_name_closeBtn){
			postbar_applymaster_name.setText("");
		} else if(v.getId() == R.id.apply_master_cardid_closeBtn){
			postbar_applymaster_cardid.setText("");
		}
	}

	/**
	 * 申请吧主
	 */
	private void applyPostbarMaster() {

		String name = postbar_applymaster_name.getText().toString().trim();
		String idcardNo = postbar_applymaster_cardid.getText().toString().trim();
		String applyContent = postbar_applymaster_content.getText().toString().trim();
		if (name.equals("")) {
			ToastUtil.showToast(this, getString(R.string.postbar_applymaster_name_isnull));
			return;
		}
		if (name.length() < 2) {
			ToastUtil.showToast(this, getString(R.string.postbar_applymaster_name_lengtherr));
			return;
		}
		if (!personIdValidation(idcardNo)) {
			ToastUtil.showToast(this, getString(R.string.postbar_applymaster_idcardno_err));
			return;
		}
		if (applyContent.equals("")) {
			ToastUtil.showToast(this, getString(R.string.postbar_applymaster_content_isnull));
			return;
		}

		if (applyContent.length() < 10) {
			ToastUtil.showToast(this, getString(R.string.postbar_applymaster_content_minlength));
			return;
		}

		if (applyContent.length() > 100) {
			ToastUtil.showToast(this, getString(R.string.postbar_applymaster_content_maxlength));
			return;
		}
		
		if (resource == null) {
			ToastUtil.showToast(this, getString(R.string.postbar_applymaster_idcardimg_isnull));
			return;
		}

		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				// TODO Auto-generated method stub
				isOnClickSend = false;
				dialog.dismiss();
				ToastUtil.showToast(ApplyMasterActivity.this, getString(R.string.postbar_applymaster_applyok));
				DaoFactory.getDaoFactory().getUserActionDao(ApplyMasterActivity.this)
						.insertUserAction(gid, UserActionDao.ENTITY_TYPE_POSTBAR, UserActionDao.OP_TYPE_POSTBAR_MNAGER_APPLEY);
				ApplyMasterActivity.this.finish();
			}

			@Override
			public void onFailure(Integer result,String resultMsg) {
				// TODO Auto-generated method stub
				isOnClickSend = false;
				dialog.dismiss();
				ErrorCodeUtil.handleErrorCode(ApplyMasterActivity.this, result,resultMsg);
			}
		};

		isOnClickSend = true;
		ProxyFactory.getInstance().getPostbarProxy().applyPostbarMaster(callback, this, gid, name, idcardNo, applyContent, resource);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		byte[] photoByte = null;
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PhotoUtil.CAMERA_WITH_DATA:
				// 先缩放
				try {
					Bitmap bitmap = ImageUtil.createImageThumbnail(PhotoUtil.sdcardTempFilePath, -1, SystemConfig.BITMAP_MAX_RESOLUTION, true);
					photoByte = ImageUtil.Bitmap2Bytes(bitmap, CompressFormat.JPEG, SystemConfig.BITMAP_COMPRESS_QUALITY);
					if(bitmap != null && !bitmap.isRecycled()){ 
						// 回收并且置为null
						bitmap.recycle(); 
						bitmap = null; 
						//System.gc();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}
				break;
			case PhotoUtil.PHOTO_PICKED_WITH_DATA:
				ContentResolver resolver = this.getContentResolver();
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				try {
					// 使用ContentProvider通过URI获取原始图片
					// Bitmap photo =
					// MediaStore.Images.Media.getBitmap(resolver, originalUri);
					Bitmap photo = ImageUtil.createImageThumbnail(resolver, originalUri, -1, SystemConfig.BITMAP_MAX_RESOLUTION, true);
					if (photo != null) {
						photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, SystemConfig.BITMAP_COMPRESS_QUALITY);
						if(photo != null && !photo.isRecycled()){ 
							// 回收并且置为null
							photo.recycle(); 
							photo = null; 
							//System.gc();
						} 
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}

			case PhotoUtil.CROP_IMAGE_WITH_DATA:

				break;
			}
			if (photoByte != null) {
				Bitmap tempBtm = null;
				tempBtm = btm;
//				if(btm != null && !btm.isRecycled()){
//					btm.recycle();
//					btm = null;
//					//System.gc();
//				}
				
				try {
					btm = ImageUtil.Bytes2Bimap(photoByte);
				} catch (Exception e1) {
					e1.printStackTrace();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}
				if(btm != null){
					postbar_applymaster_cardimg.setImageBitmap(btm);
					resource = photoByte;
				}else {
				    ToastUtil.showToast(this, getResources().getString(R.string.common_add_photo_error));
				}
				if(tempBtm != null && !tempBtm.isRecycled()){
					tempBtm.recycle();
					tempBtm = null;
					//System.gc();
				}
			}else {
			    ToastUtil.showToast(this, getResources().getString(R.string.common_add_photo_error));
			}
		} else {
			LogUtil.w(TAG, "选择的图片异常");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cancelDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#back()
	 */
	@Override
	protected void back() {
		// TODO Auto-generated method stub
		cancelDialog();

		// super.back();
	}

	void cancelDialog() {
		this.finish();
	}

	/**
	 * 验证身份证号是否符合规则
	 * 
	 * @param text
	 *            身份证号
	 * @return
	 */
	public boolean personIdValidation(String text) {
		String regx = "[0-9]{17}x";
		String reg1 = "[0-9]{15}";
		String regex = "[0-9]{18}";
		return text.matches(regx) || text.matches(reg1) || text.matches(regex);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(btm != null && !btm.isRecycled()){
			btm.recycle();
			btm = null;
			//System.gc();
		}
	}
}
