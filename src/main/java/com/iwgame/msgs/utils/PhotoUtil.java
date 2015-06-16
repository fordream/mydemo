/**      
 * PhotoUtil.java Create on 2013-12-3     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.iwgame.msgs.common.CropImageUI;
import com.youban.msgs.R;

/**
 * @ClassName: PhotoUtil
 * @Description: TODO(相册，拍照，裁剪帮助类)
 * @author chuanglong
 * @date 2013-12-3 上午11:43:44
 * @Version 1.0
 * 
 */
public class PhotoUtil {
	/**
	 * 临时图片保存文件名
	 */
	public final static String sdcardTempFileName = "msgs_tmp_pic.jpg";
	/**
	 * 临时图片保存文件路径
	 */
	public final static String sdcardTempFilePath = Environment.getExternalStorageDirectory() + File.separator + sdcardTempFileName;
	/**
	 * 发帖图片保存文件根路径
	 */
	public final static String sdcardFileRootPath = Environment.getExternalStorageDirectory() + File.separator;

	/** 照相机功能返回的标识 */
	public static final int CAMERA_WITH_DATA = 1000;

	/** 相册返回的标识 */
	public static final int PHOTO_PICKED_WITH_DATA = 1001;

	/** 裁剪返回的标识 */
	public static final int CROP_IMAGE_WITH_DATA = 1002;

	/**
	 * 裁剪时最小输出
	 */
	private static int CROP_OUTPUT = 80;

	/**
	 * 相机拍照获得图片
	 * 
	 * @param activity
	 */
	public static void doTakePhoto(Object obj) {
		Intent intent = getTakePickIntent(null);
		if (obj instanceof Activity) {
			((Activity) obj).startActivityForResult(intent, CAMERA_WITH_DATA);
		} else if (obj instanceof Fragment) {
			((Fragment) obj).startActivityForResult(intent, CAMERA_WITH_DATA);
		}
	}

	/**
	 * 相机拍照获得图片
	 * 
	 * @param activity
	 */
	public static void doTakePhoto(Object obj, String path) {
		Intent intent = getTakePickIntent(path);
		if (obj instanceof Activity) {
			((Activity) obj).startActivityForResult(intent, CAMERA_WITH_DATA);
		} else if (obj instanceof Fragment) {
			((Fragment) obj).startActivityForResult(intent, CAMERA_WITH_DATA);
		}
	}

	/**
	 * 拍照的intent
	 * 
	 * @return
	 */
	private static Intent getTakePickIntent(String path) {
		Intent intent = new Intent();
		// 指定开启系统相机的Action
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		File sdcardTempFile = null;
		if(path == null)
			sdcardTempFile = new File(sdcardTempFilePath);
		else
			sdcardTempFile = new File(path);
		// 根据文件地址创建文件
		if (sdcardTempFile.exists()) {
			sdcardTempFile.delete();
		}
		// 把文件地址转换成Uri格式
		Uri uri = Uri.fromFile(sdcardTempFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(sdcardTempFile));
		return intent;

	}

	/**
	 * 相册获得图片
	 */
	public static void doPickPhotoFromGallery(Object obj) {
		Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
		openAlbumIntent.setType("image/*");
		if (obj instanceof Activity) {
			((Activity) obj).startActivityForResult(openAlbumIntent, PHOTO_PICKED_WITH_DATA);
		} else if (obj instanceof Fragment) {
			((Fragment) obj).startActivityForResult(openAlbumIntent, PHOTO_PICKED_WITH_DATA);
		}
	}

	/**
	 * 裁剪照片
	 * 
	 * @param activity
	 * @param uri
	 * @param aspectX
	 * @param aspectY
	 * @param outputX
	 * @param outputY
	 */
	public static void doCropPhoto(Activity activity, Uri uri, int aspectX, int aspectY, int outputX, int outputY) {
		Intent intent = getCropSmallImageIntent(uri, aspectX, aspectY, outputX, outputY);
		activity.startActivityForResult(intent, CROP_IMAGE_WITH_DATA);
	}

	/**
	 * 裁剪照片
	 * 
	 * @param activity
	 * @param uri
	 * @param aspectX
	 * @param aspectY
	 * @param outputX
	 * @param outputY
	 */
	public static void doCropPhoto(Fragment fragment, Uri uri, int aspectX, int aspectY, int outputX, int outputY) {
		Intent intent = getCropSmallImageIntent(uri, aspectX, aspectY, outputX, outputY);
		fragment.startActivityForResult(intent, CROP_IMAGE_WITH_DATA);
	}
	
	public static void doCropBigPhoto(Fragment fragment, Uri cropUri,  Uri saveUri, int aspectX, int aspectY, int outputX, int outputY) {
//		Intent intent = getCropBigImageIntent(cropUri,saveUri, aspectX, aspectY, outputX, outputY);
		Intent intent = getCropImageIntent(fragment.getActivity() ,cropUri,saveUri, aspectX, aspectY, outputX, outputY);
		fragment.startActivityForResult(intent, CROP_IMAGE_WITH_DATA);
	}
	
	public static void doCropBigPhoto(Activity activity, Uri cropUri,  Uri saveUri, int aspectX, int aspectY, int outputX, int outputY) {
//		Intent intent = getCropBigImageIntent(cropUri,saveUri, aspectX, aspectY, outputX, outputY);
		Intent intent = getCropImageIntent(activity ,cropUri,saveUri, aspectX, aspectY, outputX, outputY);
		activity.startActivityForResult(intent, CROP_IMAGE_WITH_DATA);
	}

	/**
	 * 裁剪图片的intent(该方法只能够裁剪小图片）
	 * 
	 * @param photoUri
	 * @return
	 */
	private static Intent getCropSmallImageIntent(Uri photoUri, int aspectX, int aspectY, int outputX, int outputY) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("return-data", true);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", aspectX > 0 ? aspectX : 1);
		intent.putExtra("aspectY", aspectY > 0 ? aspectY : 1);
		intent.putExtra("outputX", outputX > CROP_OUTPUT ? outputX : CROP_OUTPUT);
		intent.putExtra("outputY", outputY > CROP_OUTPUT ? outputY : CROP_OUTPUT);
		intent.putExtra("scale", true);// 去黑边
		intent.putExtra("noFaceDetection", true);// 去黑边
		return intent;
	}
	
	/**
	 * 裁剪图片的intent(该方法裁剪大图片）
	 * 
	 * @param photoUri
	 * @return
	 */
	private static Intent getCropBigImageIntent(Uri cropUri,  Uri saveUri, int aspectX, int aspectY, int outputX, int outputY) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(cropUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", aspectX > 0 ? aspectX : 1);
		intent.putExtra("aspectY", aspectY > 0 ? aspectY : 1);
		intent.putExtra("outputX", outputX > CROP_OUTPUT ? outputX : CROP_OUTPUT);
		intent.putExtra("outputY", outputY > CROP_OUTPUT ? outputY : CROP_OUTPUT);
		intent.putExtra("scale", true);// 去黑边
		intent.putExtra("noFaceDetection", true);// 去黑边
		
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		return intent;
	}

	/**
	 * 自定义的裁剪图片的intent
	 * @param context
	 * @param photoUri
	 * @return
	 */
	private static Intent getCropImageIntent(Context context,Uri cropUri,  Uri saveUri, int aspectX, int aspectY, int outputX, int outputY)
	{
    	Intent intent = new Intent(context, CropImageUI.class);
    	intent.setDataAndType(cropUri, "image/*");
	intent.putExtra("crop", "true");
	intent.putExtra("aspectX", aspectX > 0 ? aspectX : 1);
	intent.putExtra("aspectY", aspectY > 0 ? aspectY : 1);
	intent.putExtra("outputX", outputX > CROP_OUTPUT ? outputX : CROP_OUTPUT);
	intent.putExtra("outputY", outputY > CROP_OUTPUT ? outputY : CROP_OUTPUT);
	intent.putExtra("scale", true);// 去黑边
	intent.putExtra("noFaceDetection", true);// 去黑边
	
	intent.putExtra("return-data", false);
	intent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
	intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	return intent;
	}
	/**
	 * 显示默认的选择框
	 * 
	 * @param obj
	 */
	public static Dialog showSelectDialog(final Object obj) {
		Context context = null;
		if (obj instanceof Activity) {
			context = (Activity) obj;
		} else if (obj instanceof Fragment) {
			context = ((Fragment) obj).getActivity();
		}
		final Dialog dialog = new Dialog(context, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("图片管理");

		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		dialog.findViewById(R.id.bottom).setVisibility(View.GONE);

		content.removeAllViews();
		View contentView = View.inflate(context, R.layout.common_dialog_image_manage, null);
		content.addView(contentView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		content.findViewById(R.id.cameraItem).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				doTakePhoto(obj);
			}
		});
		content.findViewById(R.id.albumItem).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				doPickPhotoFromGallery(obj);
			}
		});
		dialog.show();
		return dialog;
	}
	/**
	 * 显示默认的选择框
	 * 
	 * @param obj
	 */
	public static Dialog showSelectDialog(final Object obj,final String path) {
		Context context = null;
		if (obj instanceof Activity) {
			context = (Activity) obj;
		} else if (obj instanceof Fragment) {
			context = ((Fragment) obj).getActivity();
		}
		final Dialog dialog = new Dialog(context, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("图片管理");

		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		dialog.findViewById(R.id.bottom).setVisibility(View.GONE);

		content.removeAllViews();
		View contentView = View.inflate(context, R.layout.common_dialog_image_manage, null);
		content.addView(contentView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		content.findViewById(R.id.cameraItem).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				doTakePhoto(obj, path);
			}
		});
		content.findViewById(R.id.albumItem).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				doPickPhotoFromGallery(obj);
			}
		});
		dialog.show();
		return dialog;
	}
}
