/**      
* PlayAppealActivity.java Create on 2015-5-19     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.play.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView.OnEditorActionListener;

import com.google.protobuf.ByteString;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.postbar.ui.PublishTopicActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ImageBytesDetail;
import com.iwgame.msgs.proto.Msgs.PlayOrderAppeal;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.PhotoUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.UserRoleVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/** 
 * @ClassName: PlayAppealActivity 
 * @Description: TODO(..陪玩申诉.) 
 * @author xingjianlong
 * @date 2015-5-19 下午7:42:30 
 * @Version 1.0
 * 
 */
public class PlayAppealActivity extends BaseActivity implements OnClickListener{
	
	private long orderid;
	private TextView rightText;
	private View view;
	private EditText appeal_theme;
	private EditText appeal_contact;
	private EditText appeal_induce;
	private ImageView appeal_image;
	private Dialog dialog;
	private Uri imageUri;
	private String photoPath;
	private byte[] avatar;
	private List<String> pathlist = new ArrayList<String>();
	private List<ImageBytesDetail> images = new ArrayList<ImageBytesDetail>();
	private List<View> viewlist = new ArrayList<View>();
	private List<Bitmap> maplist = new ArrayList<Bitmap>();
	private LinearLayout appeal_content;
	private int count;
	public  String sdcardTempFilePath;
	private boolean change = true;
	public String position;
	private TextView appeal_pic_tip;
	private Dialog cgDialog;
	private int type ;
	private String [] url ;
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();
		initial();
	}

	/**
	 * 
	 */
	private void initial() {
		// TODO Auto-generated method stub
				if(type==1){
					setTitleTxt("申诉");
				}else if(type==2){
					setTitleTxt("申诉中");
				}
				rightText =(TextView)findViewById(R.id.rightText);
				rightText.setText("提交");
				rightText.setVisibility(View.VISIBLE);
				LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				view = View.inflate(this, R.layout.play_info_appeal, null);
				contentView.addView(view,params);
				appeal_theme =(EditText)view.findViewById(R.id.play_appeal_theme);
				appeal_contact=(EditText)view.findViewById(R.id.play_appeal_contact);
				appeal_induce=(EditText)view.findViewById(R.id.play_appeal_induce);
				appeal_image =(ImageView)view.findViewById(R.id.appeal_add);
				appeal_content =(LinearLayout)view.findViewById(R.id.appeal_content);
				appeal_pic_tip =(TextView)view.findViewById(R.id.play_appeal_pic_cer);
				appeal_image.setOnClickListener(this);
				rightText.setOnClickListener(this);
				setEditType(appeal_theme);
				setEditType(appeal_induce);
				if(type==2){
					rightText.setVisibility(View.GONE);
					appeal_pic_tip.setVisibility(View.GONE);
					appeal_image.setVisibility(View.GONE);
					view.setVisibility(View.GONE);
					getAppealInfo();
				}
				InputFilterUtil.lengthFilter(this, appeal_induce, 400, "输入的原因说明不能超过200个汉字或400个字符哦!");
	}

	/**
	 * 
	 */
	private void getData() {
		orderid =getIntent().getLongExtra("oid", 0);
		type =getIntent().getIntExtra("type", 0);
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if(v==appeal_image){
			if(viewlist.size()<6){
				position = null;
				sdcardTempFilePath = Environment.getExternalStorageDirectory() + File.separator +  "msgs_tmp_pic"+System.currentTimeMillis()+".jpg";
			dialog = PhotoUtil.showSelectDialog(this,sdcardTempFilePath);
			}else{
				ToastUtil.showToast(PlayAppealActivity.this, "多只能上传6张图片哦！");
			}
		}else if(v==rightText){
			if(checkAppealContent()){
				appealOrder(PlayAppealActivity.this, getAppeal());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bitmap tempBtm = null;
		byte[] photoByte = null;
		Bitmap photo = null;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case PhotoUtil.CAMERA_WITH_DATA:// 相机
				Display mDisplay = this.getWindowManager().getDefaultDisplay();
				int w = mDisplay.getWidth();
				int h = mDisplay.getHeight();
				int imagewidth = w > h ? h : w;
				imageUri = Uri.parse("file://" + sdcardTempFilePath);
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
				imageUri = Uri.parse("file://" + sdcardTempFilePath);
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
				break;
			}
			if (photo != null) {
				dialog.dismiss();
				if(cgDialog!=null&&cgDialog.isShowing())
				cgDialog.dismiss();
				avatar = photoByte;
				addImageView(photoPath,photoByte);
				if (tempBtm != null && !tempBtm.isRecycled()) {
					tempBtm.recycle();
					tempBtm = null;
					 System.gc();
				}
			} else {
				if (tempBtm != null && !tempBtm.isRecycled()) {
					tempBtm.recycle();
					tempBtm = null;
					 System.gc();
				}
				ToastUtil.showToast(
						this,
						getResources().getString(
								R.string.common_add_photo_error));
				return;
			}
			}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 添加图片
	 * @param photoPath
	 * @param photos
	 */
	private void addImageView(String photoPath,byte[] photos){
		if(position==null){
			pathlist.add(photoPath);
			ImageBytesDetail.Builder ibd = ImageBytesDetail.newBuilder();
			ibd.setResourceIdBytes(ByteString.copyFrom(photos));
			images.add(ibd.build());
			ImageTag tag = new ImageTag();
			tag.image=ibd.build();
			tag.url =photoPath;
			
			LogUtil.d("appeal", photoPath);
		final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(PlayAppealActivity.this, 68), DisplayUtil.dip2px(
				PlayAppealActivity.this, 68));
		params.leftMargin = DisplayUtil.dip2px(PlayAppealActivity.this, 10);
		Bitmap map =ImageUtil.decodeUri2Bitmap(
				this.getContentResolver(), imageUri);
		View fv = View.inflate(PlayAppealActivity.this, R.layout.common_icon_big_gray, null);
		ImageView iv = (ImageView) fv.findViewById(R.id.icon);
		iv.setImageBitmap(map);
		tag.view = fv;
		fv.setTag(tag);
		appeal_content.addView(fv, params);
		viewlist.add(fv);
		
		fv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int position =0;
				for(int i=0;i<viewlist.size();i++){
					if(viewlist.get(i).equals(v)){
						position = i;
					}
				}
				Intent intent = new Intent(PlayAppealActivity.this, ImageBrowerActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, 0);
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, 1);
				bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, pathlist.toArray(new String[pathlist.size()]));
				bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, position);
				bundle.putBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES_ISSHOWREPORTMENU, false);
				intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
				PlayAppealActivity.this.startActivity(intent);
			}
		});
		fv.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				manageImage(v);
				return true;
			}
		});
		}else {
		int d = Integer.parseInt(position);
		ImageView image=	(ImageView)viewlist.get(d).findViewById(R.id.icon);
		Bitmap map =ImageUtil.decodeUri2Bitmap(
				this.getContentResolver(), imageUri);
		recycleGC(image);
		image.setImageBitmap(map);
		images.remove(d);
		pathlist.remove(d);
		ByteString bs = ByteString.copyFrom(photos);
		ImageBytesDetail.Builder ibd = ImageBytesDetail.newBuilder();
		ibd.setResourceIdBytes(ByteString.copyFrom(photos));
		ImageTag tag = (ImageTag) viewlist.get(d).getTag();
		tag.url =photoPath;
		tag.image=ibd.build();
		tag.view =viewlist.get(d);
		viewlist.get(d).setTag(tag);
		images.add(d, ibd.build());
		pathlist.add(d, photoPath);
		position=null;
		}
	}
	/**
	 * 把图片的地址集合转化为数组存放
	 * @param list
	 * @return
	 */
	public String[] getImagePathArray(List<String> list){
		String [] path =new String[list.size()];
		for(int i = 0;i<list.size();i++){
			path[i]=list.get(i);
		}
		return path;
	}
	/**
	 * 长按图片功能
	 */
	
	public void manageImage(final View v){
		final Dialog mdialog = new Dialog(PlayAppealActivity.this, R.style.SampleTheme_Light);
		mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mdialog.setContentView(R.layout.dialog);
		mdialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView title = (TextView) mdialog.findViewById(R.id.title);
		title.setText("图片管理");

		LinearLayout content = (LinearLayout) mdialog.findViewById(R.id.content);
		mdialog.findViewById(R.id.bottom).setVisibility(View.GONE);

		content.removeAllViews();
		View cview = View.inflate(PlayAppealActivity.this, R.layout.common_dialog_image_manage, null);
		content.addView(cview, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		((TextView) content.findViewById(R.id.txt1)).setText("替换");
		((TextView) content.findViewById(R.id.txt2)).setText("删除");
		mdialog.show();
		content.findViewById(R.id.cameraItem).setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View view) {
				for(int i=0;i<viewlist.size();i++){
					if(viewlist.get(i).equals(v)){
						position = i+"";
					}
				}
				sdcardTempFilePath = Environment.getExternalStorageDirectory() + File.separator +  "msgs_tmp_pic"+System.currentTimeMillis()+".jpg";
				cgDialog = PhotoUtil.showSelectDialog(PlayAppealActivity.this,sdcardTempFilePath);
				cgDialog.show();
				mdialog.dismiss();
			}
		});
		content.findViewById(R.id.albumItem).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ImageTag tag = (ImageTag) v.getTag();
				ImageView image =(ImageView)v.findViewById(R.id.icon);
				recycleGC(image);
				appeal_content.removeView(v);
				images.remove(tag.image);
				pathlist.remove(tag.url);
				viewlist.remove(tag.view);
				mdialog.dismiss();
			}
		});
	}
	class ImageTag{
		String url;
		ImageBytesDetail image;
		View view;
	}
	/**
	 * 判断数据
	 */
	private boolean checkAppealContent(){
		if(appeal_theme.getText().toString().equals("")){
			ToastUtil.showToast(PlayAppealActivity.this,"你还未输入申诉主题哦！");
			return false;
		}else if(appeal_contact.getText().toString().equals("")){
			ToastUtil.showToast(PlayAppealActivity.this, "你还未输入你的真实手机号哦！");
			return false;
		}else if(appeal_contact.getText().toString().length()<11){
			ToastUtil.showToast(PlayAppealActivity.this, "手机号码只能11位数字哦！");
			return false;
		}else if(appeal_induce.getText().toString().equals("")){
			ToastUtil.showToast(PlayAppealActivity.this, "你还未输入详细原因说明哦！");
			return false;
		}else if(images.size()<=0){
			ToastUtil.showToast(PlayAppealActivity.this, "你还未上传图片哦！");
			return false;
		}
		return true;
	}
	
	private PlayOrderAppeal getAppeal(){
		PlayOrderAppeal.Builder appeal = PlayOrderAppeal.newBuilder();
		appeal.setOrderid(orderid);
		appeal.addAllImgs(images);
		appeal.setMobile(appeal_contact.getText().toString());
		appeal.setRemark(appeal_induce.getText().toString());
		appeal.setReason(appeal_theme.getText().toString());
		return appeal.build();
	}
	private void appealOrder(Context context ,PlayOrderAppeal appeal){
		final CustomProgressDialog downloaddialog = CustomProgressDialog.createDialog(this, false);
		downloaddialog.show();
		ProxyFactory.getInstance().getPlayProxy().appealOrder(new ProxyCallBack<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				if(downloaddialog.isShowing()){
					downloaddialog.dismiss();
				}
				if(result==0){
					 appealCompTip();
				}
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				downloaddialog.dismiss();
				ToastUtil.showToast(PlayAppealActivity.this, "申诉失败");
			}
		}, context, appeal);
	}
	/**
	 * 申诉完成之后提示
	 */
	public void appealCompTip(){
		final Dialog dialog = new Dialog(PlayAppealActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("申诉完成,客服将在48小时内给你回复,请耐心等待哦!");
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
				PlayAppealActivity.this.finish();
			}
		});
	}
	/**
	 * 申诉完成之后改变申诉颜色
	 */
	public void compelteAppeal(){
		rightText.setVisibility(View.GONE);
		appeal_contact.setTextColor(PlayAppealActivity.this.getResources().getColor(R.color.play_appeal_font_color));
		appeal_contact.setKeyListener(null);
		appeal_theme.setTextColor(PlayAppealActivity.this.getResources().getColor(R.color.play_appeal_font_color));
		appeal_theme.setKeyListener(null);
		appeal_induce.setTextColor(PlayAppealActivity.this.getResources().getColor(R.color.play_appeal_font_color));
		appeal_induce.setKeyListener(null);
		appeal_pic_tip.setVisibility(View.GONE);
		appeal_image.setVisibility(View.GONE);
	}
	/**
	 * 设置edittext不能换行
	 * @param text
	 */
	private void setEditType(EditText text){
			text.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent event) {
				// TODO Auto-generated method stub
				return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
			}
		});
	}
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseSuperActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(type==1){
		recycle();
		}
	}
	
	private void recycle(){
		for(View view :viewlist){
			ImageView imageView = (ImageView)view.findViewById(R.id.icon);
			recycleGC(imageView);
		}
	}
	private void recycleGC(ImageView imageView){
		if(imageView !=  null &&  imageView.getDrawable() != null){     
		      Bitmap oldBitmap =  ((BitmapDrawable) imageView.getDrawable()).getBitmap();    
		       imageView.setImageDrawable(null);    
		      if(oldBitmap !=  null){    
		            oldBitmap.recycle();     
		            oldBitmap =  null;   
		      }    
		 }   
		 System.gc();
	}
	/***
	 * 检查返回时的数据
	 * @return
	 */
	private boolean checkContent(){
		if(appeal_theme.getText().toString().length()!=0){
			return true;
		}else if(appeal_contact.getText().toString().length()!=0){
			return true;
		}else if(appeal_induce.getText().toString().length()!=0){
			return true;
		}else if(viewlist.size()!=0){
			return true;
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#back()
	 */
	@Override
	protected void back() {
		if(type==1){
		if(checkContent()){
			 backTip();
			}else{
				super.back();
			}
		}else{
			super.back();
		}
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		 if(keyCode == KeyEvent.KEYCODE_BACK){   
			 if(type==1){
				if(checkContent()){
					 backTip();
					}else{
						super.back();
					}
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
		final Dialog dialog = new Dialog(PlayAppealActivity.this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView)dialog.findViewById(R.id.content_dialog);
		content.setText("尚有未填写的信息，确定要返回吗？");
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
				PlayAppealActivity.this.finish();
			}
		});
		cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	/**
	 * 获取申诉的数据
	 */
	private void getAppealInfo(){
		final  CustomProgressDialog downloaddialog = CustomProgressDialog.createDialog(this, false);
		downloaddialog.show();
		ProxyFactory.getInstance().getPlayProxy().seacrhAppealInfo(new ProxyCallBack<Msgs.PlayOrderAppeal>() {
			
			@Override
			public void onSuccess(PlayOrderAppeal result) {
				if(downloaddialog.isShowing()){
					downloaddialog.dismiss();
					view.setVisibility(View.VISIBLE);
				}
				if(result!=null){
					renderUI(result);
				}
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				if(downloaddialog.isShowing()){
					downloaddialog.dismiss();
					view.setVisibility(View.VISIBLE);
				}
				ToastUtil.showToast(PlayAppealActivity.this,"获取数据失败");
			}
		}, PlayAppealActivity.this, orderid);
	}
	/**
	 * 显示申诉的数据
	 * @param appeal
	 */
	private void renderUI(PlayOrderAppeal appeal){
		appeal_theme.setText(appeal.getReason());
		appeal_contact.setText(appeal.getMobile());
		appeal_induce.setText(appeal.getRemark());
		setImageUrl(appeal.getResourceids());
		addImage(url);
		compelteAppeal();
	}
	private void setImageUrl(String path){
		String [] arry = path.split("\\,");
		url = new String[arry.length];
		for(int i =0;i<arry.length;i++){
			url[i]=ResUtil.getOriginalRelUrl(arry[i]);
		}
	}
	
	private void addImage(final String[] url){
		for(int i=0;i<url.length;i++){
		final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(PlayAppealActivity.this, 68), DisplayUtil.dip2px(
				PlayAppealActivity.this, 68));
		params.leftMargin = DisplayUtil.dip2px(PlayAppealActivity.this, 10);
		View fv = View.inflate(PlayAppealActivity.this, R.layout.common_icon_big_gray, null);
		ImageView iv = (ImageView) fv.findViewById(R.id.icon);
		new ImageLoader().loadRes(url[i], 0, iv, R.drawable.common_default_icon);
		appeal_content.addView(fv, params);
		fv.setTag(i);
		viewlist.add(fv);
		setImageClick(fv);
		}
	}
	private void setImageClick(View fv){
	fv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int position =0;
					position = (Integer) v.getTag();
					Intent intent = new Intent(PlayAppealActivity.this, ImageBrowerActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, url);
						bundle.putBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES_ISSHOWREPORTMENU, false);
					bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, position);
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_USER);
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					PlayAppealActivity.this.startActivity(intent);
			}
		});
	}
}
