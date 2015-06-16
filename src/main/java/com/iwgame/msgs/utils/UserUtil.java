/**      
 * UserUtil.java Create on 2013-9-30     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.UserGradeDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.guide.GuideActivity;
import com.iwgame.msgs.module.setting.ui.SettingFragment;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.vo.local.ResourceVo;
import com.iwgame.msgs.vo.local.UserGradeVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: UserUtil
 * @Description: 用户工具类
 * @author 王卫
 * @date 2013-9-30 下午03:49:40
 * @Version 1.0
 * 
 */
public class UserUtil {

	protected static final String TAG = "UserUtil";
	// 用户资料类型
	public static int TYPE_USER = 0;
	// 设置我的资料类型
	public static int TYPE_SETTING = 1;

	public static Bitmap bmp = null;
	
	public static List<View> imageList;
	/**
	 * 显示用户图片
	 * 
	 * @param obj调用对象
	 * @param context
	 * @param detailView
	 * @param uid
	 * @param avatarView
	 * @param type
	 */
	public static void showUserAlbum(final Object obj, final Context context, final Object object, final Long uid,
			final ImageView avatarView, final int type,final int key) {
		imageList = new ArrayList<View>();
		// 请求相册数据
		ProxyFactory.getInstance().getUserProxy().getUserAlbum(new ProxyCallBack<List<ResourceVo>>() {

			@Override
			public void onSuccess(final List<ResourceVo> result) {
				if(key == 1){
					((UserDetailInfoActivity)object).photoContent.removeAllViews();
					if (result != null && result.size() > 0) {
						ExtUserVo uvo = SystemContext.getInstance().getExtUserVo();
						if (uvo != null && uid.equals(uvo.getUserid())) {
							((UserDetailInfoActivity)object).tip.setVisibility(View.VISIBLE);
						}
						((UserDetailInfoActivity)object).setPhotoCount(result.size());
						((UserDetailInfoActivity)object).horizontalScrollView.setVisibility(View.VISIBLE);
						final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(context, 68), DisplayUtil.dip2px(
								context, 68));
						params.leftMargin = DisplayUtil.dip2px(context, 10);
						if (result != null) {
							List<String> images = new ArrayList<String>();
							for (int i = 0; i < result.size(); i++) {
								final int c = i;
								final String resid = result.get(c).getResourceId();
								images.add(ResUtil.getOriginalRelUrl(resid));
								View fv = View.inflate(context, R.layout.common_icon_big_gray, null);
								ImageView iv = (ImageView) fv.findViewById(R.id.icon);
								((UserDetailInfoActivity)object).photoContent.addView(fv, params);
								fv.setTag(ResUtil.getOriginalRelUrl(resid));
								imageList.add(fv);
								new ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.get(i).getResourceId()), 0, iv, R.drawable.common_default_icon);

								setImageEvent(obj, context, fv, resid, avatarView, ((UserDetailInfoActivity)object), uid, c, images, type);
							}
						}
					} else {
						if(((UserDetailInfoActivity)object).getFengexian() != null) ((UserDetailInfoActivity)object).getFengexian().setVisibility(View.GONE);
						ExtUserVo uvo = SystemContext.getInstance().getExtUserVo();
						if (uvo != null && !uid.equals(uvo.getUserid())) {
							((UserDetailInfoActivity)object).horizontalScrollView.setVisibility(View.GONE);
							((UserDetailInfoActivity)object).getFengexian().setVisibility(View.GONE);
						} else {
							((UserDetailInfoActivity)object).horizontalScrollView.setVisibility(View.VISIBLE);
							((UserDetailInfoActivity)object).tip.setVisibility(View.GONE);
						}
					}
				}else if(key == 2){
					((SettingFragment)object).photoContent.removeAllViews();
					if (result != null && result.size() > 0) {
						((SettingFragment)object).setPhotoCount(result.size());
						((SettingFragment)object).horizontalScrollView.setVisibility(View.VISIBLE);
						final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(context, 68), DisplayUtil.dip2px(
								context, 68));
						params.leftMargin = DisplayUtil.dip2px(context, 10);
						if (result != null) {
							List<String> images = new ArrayList<String>();
							for (int i = 0; i < result.size(); i++) {
								final int c = i;
								final String resid = result.get(c).getResourceId();
								images.add(ResUtil.getOriginalRelUrl(resid));
								View fv = View.inflate(context, R.layout.common_icon_big_gray, null);
								ImageView iv = (ImageView) fv.findViewById(R.id.icon);
								((SettingFragment)object).photoContent.addView(fv, params);
								fv.setTag(ResUtil.getOriginalRelUrl(resid));
								imageList.add(fv);
								new ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.get(i).getResourceId()), 0, iv, R.drawable.common_default_icon);
								setImageEvent(obj, context, fv, resid, avatarView, ((SettingFragment)object), uid, c, images, type);
							}
						}
						if (SystemContext.getInstance().getGuidUserDetail() || SystemContext.getInstance().isnotShowGuide()) {
							((SettingFragment)object).tip.setVisibility(View.VISIBLE);
						}
					} else {
						((SettingFragment)object).tip.setVisibility(View.GONE);
						ExtUserVo uvo = SystemContext.getInstance().getExtUserVo();
						((SettingFragment)object).setPhotoCount(0);
						if (uvo != null && !uid.equals(uvo.getUserid())) {
							((SettingFragment)object).horizontalScrollView.setVisibility(View.GONE);
						} else {
							((SettingFragment)object).horizontalScrollView.setVisibility(View.VISIBLE);
							return;
						}
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "请求相册数据失败=" + result);
				ExtUserVo uvo = SystemContext.getInstance().getExtUserVo();
				if(key == 1){
					if (uvo != null && !uid.equals(uvo.getUserid())) {
						((UserDetailInfoActivity)object).horizontalScrollView.setVisibility(View.GONE);
						((UserDetailInfoActivity)object).getFengexian().setVisibility(View.GONE);
					} else {
						((UserDetailInfoActivity)object).horizontalScrollView.setVisibility(View.VISIBLE);
					}
				}else if(key == 2){
					((SettingFragment)object).tip.setVisibility(View.GONE);
					if (uvo != null && !uid.equals(uvo.getUserid())) {
						((SettingFragment)object).horizontalScrollView.setVisibility(View.GONE);
					} else {
						((SettingFragment)object).setPhotoCount(0);
						((SettingFragment)object).horizontalScrollView.setVisibility(View.VISIBLE);
						return;
					}
				}
			}
		}, context, uid, 0);
	}

	/**
	 * 先获取到bitmap对象
	 * @param resId
	 * @return
	 */
	public static Bitmap getBitmap(int resId){
		Bitmap bitmap = null;
		try {
			InputStream is = SystemContext.getInstance().getContext().getResources().openRawResource(resId);
			bitmap = BitmapFactory.decodeStream(is);
			if(is != null)
				is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}


	/**
	 * 显示头像
	 * 
	 * @param avatarView
	 */
	public static void showAvatar(final Context context, final ImageView avatarView, final String resid, final boolean isSetting, final Long uid,
			final Integer type) {
		if (isSetting)
			SystemContext.getInstance().setAvatar(resid);
		ImageViewUtil.showImage(avatarView, resid, R.drawable.common_user_icon_default);
		avatarView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (resid != null && !"".equals(resid)) {
					// 点击预览照片
					Intent intent = new Intent(context, ImageBrowerActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, new String[] { ResUtil.getOriginalRelUrl(resid) });
					ExtUserVo uvo = SystemContext.getInstance().getExtUserVo();
					if (uvo != null && uid.equals(uvo.getUserid()))
						bundle.putBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES_ISSHOWREPORTMENU, false);
					bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, 0);
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID, uid);
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_USER);
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					context.startActivity(intent);
					if (type != null && type == TYPE_SETTING) {
						SettingFragment.isRender = false;
					}
				}
			}
		});
	}

	/**
	 * 设置图片事件功能
	 * 
	 * @param context
	 * @param v
	 */
	private static void setImageEvent(final Object obj, final Context context, final View iv, final String resid, final ImageView avatarView,
			final Object contentView, final Long uid,  final int resIndex, final List<String> images, final int type) {
		// 点击图片预览
		iv.setOnClickListener(new View.OnClickListener() {
           int position;
           String[] imagesArray ;
			@Override
			public void onClick(View v) {
				int size = imageList.size();
				if(imageList.size()>10){
					imagesArray = new String[10];
					size = 10;
				}else{
				imagesArray = new String[imageList.size()];
				}
				position = resIndex;
				for (int i = 0; i < size; i++) {
				if(imageList.get(i).equals(v)){
					position= i;
					}
				String imgUrl = (String) imageList.get(i).getTag();
				imagesArray[i] = imgUrl;
				}
				
				Intent intent = new Intent(context, ImageBrowerActivity.class);
				Bundle bundle = new Bundle();
				bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, imagesArray);
				ExtUserVo uvo = SystemContext.getInstance().getExtUserVo();
				if (uvo != null && uid.equals(uvo.getUserid()))
					bundle.putBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES_ISSHOWREPORTMENU, false);
				bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, position);
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID, uid);
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_USER);
				intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);

				context.startActivity(intent);
				if (type == TYPE_SETTING) {
					SettingFragment.isRender = false;
				}
				UMUtil.sendEvent(context, UMConfig.MSGS_EVENT_USER_ALBUM_VIEW, null, null, null, null, null);
			}
		});
		// 长按图片操作
		ExtUserVo uvo = SystemContext.getInstance().getExtUserVo();
		if (uvo != null && uid.equals(uvo.getUserid())) {
			iv.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					final Dialog dialog = new Dialog(context, R.style.SampleTheme_Light);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.dialog);
					dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
					TextView title = (TextView) dialog.findViewById(R.id.title);
					title.setText("图片管理");

					LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
					dialog.findViewById(R.id.bottom).setVisibility(View.GONE);

					content.removeAllViews();
					View cview = View.inflate(context, R.layout.common_dialog_image_manage, null);
					content.addView(cview, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
					((TextView) content.findViewById(R.id.txt1)).setText("设置为头像");
					((TextView) content.findViewById(R.id.txt2)).setText("删除");
					content.findViewById(R.id.cameraItem).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							doPickPhotoFromResource(obj, resid);
						}
					});
					content.findViewById(R.id.albumItem).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							delPhoto(context, resid, iv, contentView,images);
						}
					});
					UMUtil.sendEvent(context, UMConfig.MSGS_EVENT_USER_CLICK_ALBUM, null, null, null, null, null);
					dialog.show();
					return true;
				}
			});
		}
	}

	/**
	 * 
	 * @return
	 */
	public static void doPickPhotoFromResource(Object obj, String url) {
		if (obj instanceof SettingFragment) {
			((SettingFragment) obj).cropRes(url);
		}

	}

	/**
	 * 删除相册
	 * 
	 * @param context
	 * @param resid
	 */
	private static void delPhoto(final Context context, final String resid, final View v, final Object contentView,final List<String> images) {
		ProxyFactory.getInstance().getUserProxy().delUserAlbum(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					ToastUtil.showToast(context, context.getString(R.string.setting_del_album_success));
					if(contentView instanceof SettingFragment){
						((SettingFragment)contentView).photoContent.removeView(v);
						imageList.remove(v);
						images.remove(ResUtil.getOriginalRelUrl(resid));
						((SettingFragment)contentView).setPhotoCount(((SettingFragment)contentView).getPhotoCount() - 1);
						if(((SettingFragment)contentView).getPhotoCount() <= 0){
							((SettingFragment)contentView).tip.setVisibility(View.GONE);
						}
					}
					break;
				default:
					ToastUtil.showToast(context, context.getString(R.string.setting_del_album_fail));
					LogUtil.e("TAG", "删除照片失败");
					break;
				}
			}
			@Override
			public void onFailure(Integer result, String resultMsg) {
				ToastUtil.showToast(context, context.getString(R.string.setting_del_album_fail));
				LogUtil.e("TAG", "删除照片失败");
			}
		}, context, resid);
	}

	public static String convertAmount(int amount) {
		if (amount >= 10000) {
			double d = Double.valueOf(amount) / 10000;
			return String.format("%.1f", d) + "万";
		} else {
			return amount + "";
		}
	}

	
	/**
	 * 获取用户等级
	 * 
	 * @param grade
	 * @return
	 */
	public static UserGradeVo getUserGradeConfigByGrade(List<UserGradeVo> userGradeConfig, int grade) {
		if (userGradeConfig == null) {
			UserGradeDao userGradeDao = DaoFactory.getDaoFactory().getUserGradeDao(SystemContext.getInstance().getContext());
			userGradeConfig = userGradeDao.queryAll();
		}
		if (userGradeConfig != null) {
			for (Iterator iterator = userGradeConfig.iterator(); iterator.hasNext();) {
				UserGradeVo userGradeVo = (UserGradeVo) iterator.next();
				if (userGradeVo.getGrade() == grade) {
					return userGradeVo;
				}
			}
		}
		return null;
	}

}
