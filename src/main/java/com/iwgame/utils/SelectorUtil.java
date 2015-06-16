package com.iwgame.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.iwgame.msgs.context.SystemContext;

/**
 * 
 * @ClassName: SelectorUtil 
 * @Description: Selector工具类 
 * @author Administrator
 * @date 2014-12-4 下午8:50:36 
 * @Version 1.0
 *
 */
public class SelectorUtil {

	
	/**
	 * 生成app Tab切换Selector
	 * @param context
	 * @param imagePath 图片在assets下的路径，null时为默认路径
	 * @param normalImageName 默认图片名称
	 * @param selectedImageName 选择状态下图片名称
	 * @return
	 */
	public static StateListDrawable getSelectDrawable(Context context, String imagePath, String normalImageName, String selectedImageName){
		StateListDrawable bg = new StateListDrawable();
		String appType = SystemContext.APPTYPE;
		String relaImagePath = null;
		if(imagePath == null || imagePath.isEmpty()){
			relaImagePath = "/source/image/";
		}else{
			relaImagePath = imagePath;
		}
		InputStream normalIs = null;
		InputStream selectedIs = null;
		try {
			normalIs = context.getAssets().open("app_" + appType + relaImagePath + normalImageName);
			Drawable normal = FormatTools.InputStream2Drawable(normalIs);

			selectedIs = context.getAssets().open("app_" + appType + relaImagePath + selectedImageName);
			Drawable selected = FormatTools.InputStream2Drawable(selectedIs);

			bg.addState(new int[] { android.R.attr.state_selected }, selected);
			bg.addState(new int[] { android.R.attr.state_enabled }, normal);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(normalIs != null){
				try {
					normalIs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(selectedIs != null){
				try {
					selectedIs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bg;
	}
	
	
	/**
	 * 按钮点击背景
	 * @param context
	 * @param normalImageName 默认图片名称
	 * @param selectedImageName 按下状态下图片名称
	 * @return
	 */
	public static StateListDrawable getClickDrawable(Context context, String normalImageName, String selectedImageName){
		StateListDrawable bg = new StateListDrawable();
		String appType = SystemContext.APPTYPE;
		InputStream normalIs = null;
		InputStream selectedIs = null;
		Drawable normal = null;
		Drawable selected = null;
		try {
			normalIs = context.getAssets().open("app_" + appType + "/source/image/" + normalImageName);
			normal = FormatTools.InputStream2Drawable(normalIs);
			selectedIs = context.getAssets().open("app_" + appType + "/source/image/" + selectedImageName);
			selected = FormatTools.InputStream2Drawable(selectedIs);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(normalIs != null){
				try {
					normalIs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(selectedIs != null){
				try {
					selectedIs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(selected != null){
			bg.addState(new int[] { android.R.attr.state_selected }, selected);
			bg.addState(new int[] { android.R.attr.state_pressed }, selected);
		}
		if(normal != null){
			bg.addState(new int[] { android.R.attr.state_enabled }, normal);
		}
		return bg;
	}

}
