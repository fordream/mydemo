/**      
 * BitmapUtil.java Create on 2013-5-23     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.EncodingUtils;

import com.iwgame.msgs.context.SystemContext;
import com.youban.msgs.R.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

/**
 * @ClassName: BitmapUtil
 * @Description: TODO(...)
 * @author william
 * @date 2013-5-23 下午4:38:02
 * @Version 1.0
 * 
 */
public class BitmapUtil {

	public static Bitmap getBitmapFromURL(String src) {
		InputStream input = null;
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.setConnectTimeout(3000);
			connection.connect();
			input = connection.getInputStream();
			BitmapFactory.Options opt = new BitmapFactory.Options();  
			opt.inPreferredConfig = Bitmap.Config.RGB_565;   
			opt.inPurgeable = true;  
			opt.inInputShareable = true;  
			Bitmap myBitmap = BitmapFactory.decodeStream(input, null ,opt);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		} catch (OutOfMemoryError e1) {
			e1.printStackTrace();
			return null;
		} finally{
			if(input != null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}
	}

	public static Drawable getDrawableFromAsstes(Context context, String imageName){
		InputStream is = null;
		Drawable drawbale = null;
		try {
			is = context.getAssets().open("app_" + SystemContext.APPTYPE + "/source/image/" + imageName);
			drawbale = FormatTools.InputStream2Drawable(is);
			return drawbale;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		} finally{
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	public static Bitmap getBitmapFromAsstes(Context context, String imageName){
		InputStream is = null;
		Bitmap bm = null;
		try {
			is = context.getAssets().open("app_" + SystemContext.APPTYPE + "/source/image/" + imageName);
			bm = FormatTools.InputStream2Bitmap(is);
			return bm;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		} finally{
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * 保存网络图片到data/data下
	 * @param context
	 * @param imageUrl
	 * @param fileName
	 */
	public static boolean saveUrlBitmapToData(final Context context, final String imageUrl, final String fileName){
		FileOutputStream fout = null;
		Bitmap bitmap = null;
		try {
			bitmap = BitmapUtil.getBitmapFromURL(imageUrl);
			if(bitmap != null){
				fout = context.openFileOutput(fileName, Context.MODE_PRIVATE);  
				Bitmap.CompressFormat localCompressFormat = Bitmap.CompressFormat.PNG;  
				return bitmap.compress(localCompressFormat, 100, fout); 
			}else{
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e2){
			e2.printStackTrace();
			return false;
		} finally{
			if(fout != null){
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bitmap != null && !bitmap.isRecycled()){
				bitmap.recycle();
				bitmap = null;
			}
			//System.gc();
		}
	}


	/**
	 * 从data/data下读取图片
	 * @param context
	 * @param fileName
	 * @param charset
	 * @return
	 */
	public static Bitmap getBitmapFromData(Context context, String fileName, String charset){
		FileInputStream fin = null;
		Bitmap bitmap = null;
		try {
			fin = context.openFileInput(fileName);
			bitmap = FormatTools.InputStream2Bitmap(fin);
			return bitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}  catch (Exception e2) {
			e2.printStackTrace();
			return null;
		} catch (OutOfMemoryError e2) {
			e2.printStackTrace();
			return null;
		} finally {
			if(fin != null){
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
