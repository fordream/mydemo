/**      
 * ImageUtil2.java Create on 2013-12-3     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

/**
 * @ClassName: ImageUtil2
 * @Description: TODO(图片处理工具类)
 * @author chuanglong
 * @date 2013-12-3 下午3:56:15
 * @Version 1.0
 * 
 */
public class ImageUtil {

	// 圆角
	public static int ROUNDER_CORNER = 10;

	/*
	 * bitmap 2 byte[]
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) { // if(DEBUG)
		// Log.d("------------testtesttest2","W:H["
		// +bm.getWidth()+":"+bm.getHeight()+"]");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	/*
	 * bitmap 2 byte[] quality 为100时，代表不压缩
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm, int quality) { // if(DEBUG)
		// Log.d("------------testtesttest2","W:H["
		// +bm.getWidth()+":"+bm.getHeight()+"]");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/*
	 * byte[] 2 Bitmap
	 */
	public static Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	/*
	 * Drawable 2 Bitmap
	 */
	public static Bitmap Drawable2Bitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/*
	 * Bitmap 2 Drawable
	 */
	public static Drawable Bitmap2Drawable(Bitmap bm, int density) {
		BitmapDrawable bd = new BitmapDrawable(bm);
		try {
			// Method m=BitmapDrawable.class.getMethod("setTargetDensity", new
			// Class[]{DisplayMetrics.class});
			Method m = BitmapDrawable.class.getMethod("setTargetDensity", int.class);

			m.invoke(bd, density);
		} catch (Exception e) {
		}
		return bd;
	}

	/*
	 * Bitmap 2 Drawable
	 */
	public static Drawable Bitmap2Drawable(Bitmap bm, Context context) {
		BitmapDrawable bd = new BitmapDrawable(bm);
		try {
			Method m = BitmapDrawable.class.getMethod("setTargetDensity", new Class[] { DisplayMetrics.class });
			m.invoke(bd, context.getApplicationContext().getResources().getDisplayMetrics());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bd;
	}

	/*
	 * Drawable 2 byte[]
	 */
	public static byte[] Drawable2Bytes(Drawable drawable) {
		return Bitmap2Bytes(Drawable2Bitmap(drawable), Bitmap.CompressFormat.PNG, 100);

	}

	public static Bitmap CreateBitmap(Bitmap src, Bitmap bgbm) {
		int w = src.getWidth();
		int h = src.getHeight();
		int wbg = bgbm.getWidth();
		int hbg = bgbm.getHeight();

		Bitmap newb = Bitmap.createBitmap(wbg, hbg, Config.ARGB_8888);
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(bgbm, 0, 0, null);
		cv.drawBitmap(src, (wbg - w) / 2, (hbg - h) / 2, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newb;
	}

	// 合成新的图片
	public static Bitmap CreateBitmap(Bitmap src, Bitmap bgbm, int width, int height) {
		Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(bgbm, new Rect(0, 0, width, height), new Rect(0, 0, width, height), null);
		cv.drawBitmap(src, new Rect(0, 0, width, height), new Rect(0, 0, width, height), null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newb;
	}

	// 合成新的图片
	public static Bitmap CreateBitmap3(Bitmap src, Bitmap bgbm, int width, int height) {
		Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(bgbm, new Rect(0, 0, width, height), new Rect(0, 0, width, height), null);
		cv.drawBitmap(src, new Rect(10, 10, width - 20, height - 20), new Rect(10, 10, width - 20, height - 20), null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newb;
	}

	// 合成新的图片
	public static Bitmap CreateBitmap2(Bitmap src1, Bitmap src2, int left, int top) {
		Bitmap newb = Bitmap.createBitmap(src1);
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(src2, left, top, null);// 把src2画到canvas的left，top 起点的位置
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newb;
	}

	// 放大缩小图片
	public static Bitmap zoomBitmap(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(Drawable2Bitmap(drawable), 0, 0, width, height, matrix, true);
		return newbmp;
	}

	// 等比例放大缩小图片 2
	public static Bitmap zoomBitmap2(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleWidht);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return newbmp;
	}

	// 放大缩小图片
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return newbmp;
	}

	// 获得圆角图片的方法
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		if (bitmap != null) {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(rect);

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			return output;
		} else {
			return bitmap;
		}
	}

	public static Bitmap createGrayBitmap(Bitmap bitmap, int pixel) {
		Bitmap grayBitmap;
		final Paint paint = new Paint();

		grayBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

		final int color = 0x80646464;

		Canvas canvas = new Canvas(grayBitmap);
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		canvas.drawBitmap(bitmap, rect, rect, null);
		// canvas.drawARGB(180, 100, 100, 100);

		paint.setAntiAlias(true);
		// canvas.drawARGB(50, 100, 100, 100);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, pixel, pixel, paint);

		return grayBitmap;
	}

	/*
	 * 获得带倒影的图片方法
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 8;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		// canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		// canvas.drawRect(0, height,width,height + reflectionGap,
		// deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0xa0ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

		return bitmapWithReflection;
	}

	/*
	 * 获得带倒影的图片方法
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap srcBitmap, int width, int height, int top, int gap, boolean isHaveSrcBitmap) {

		int src_width = srcBitmap.getWidth();
		int src_height = srcBitmap.getHeight();

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, height, Config.ARGB_8888);

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflectionImage = null;
		if (height - src_height - top - gap > src_height) {
			reflectionImage = Bitmap.createBitmap(srcBitmap, 0, 0, src_width, src_height, matrix, false);
		} else {
			// reflectionImage = Bitmap.createBitmap(srcBitmap, 0, src_height -
			// (height - src_height- top - gap), src_width, height - src_height-
			// top - gap, matrix, false);
			reflectionImage = Bitmap.createBitmap(srcBitmap, 0, src_height / 2, src_width, src_height / 2, matrix, false);
		}

		Canvas canvas = new Canvas(bitmapWithReflection);
		if (isHaveSrcBitmap) {
			canvas.drawBitmap(srcBitmap, (width - src_width) / 2, top, null);
		}
		// Paint deafalutPaint = new Paint();
		// canvas.drawRect(0, height,width,height + reflectionGap,
		// deafalutPaint);

		canvas.drawBitmap(reflectionImage, (width - src_width) / 2, src_height + top + gap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, src_height, 0, height, 0xffffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, src_height, width, height, paint);

		reflectionImage.recycle();

		return bitmapWithReflection;
	}

	// 保存bitmap到sdcard上
	public static void saveBitmapImg(Bitmap bm, String imgname) throws IOException {
		File f = new File(Environment.getExternalStorageDirectory(), imgname);
		f.createNewFile();
		FileOutputStream fOut = null;
		fOut = new FileOutputStream(f);
		bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		fOut.flush();
		fOut.close();
	}

	public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height) {
		Bitmap bm = null;
		try {
			bm = Bitmap.createBitmap(source, x, y, width, height);
		} catch (OutOfMemoryError outofmemoryerror) {
			gc();
			bm = Bitmap.createBitmap(source, x, y, width, height);
		}
		return bm;
	}

	public static Bitmap createBitmap(int width, int height, Config config) {
		Bitmap bm = null;
		try {
			bm = Bitmap.createBitmap(width, height, config);
		} catch (OutOfMemoryError outofmemoryerror) {
			gc();
			bm = Bitmap.createBitmap(width, height, config);
		}
		return bm;
	}

	public static Runnable onLowMemory;

	private static void gc() {
		if (onLowMemory != null) {
			Thread i = Looper.getMainLooper().getThread();
			Thread j = Thread.currentThread();
			if (i == j)
				onLowMemory.run();
		}
		// System.gc();
		System.runFinalization();
	}

	/**
	 * 通过图片的路径，不加载图片，而获得图片的宽高
	 * 
	 * @param filePath
	 *            图片路径
	 * @return int数组，int[0]是宽度。int[1]是高度
	 */
	public static int[] getWidthAndHeight(String filePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
		// 这里返回的bmp是null
		// options.outWidth 和 options.outHeight就是图片的宽和高了
		int[] tmp = new int[2];
		tmp[0] = options.outWidth;
		tmp[1] = options.outHeight;
		if (bmp != null && !bmp.isRecycled()) {
			// 回收并且置为null
			bmp.recycle();
			bmp = null;
			// System.gc();
		}
		return tmp;
	}

	/**
	 * 获取图片指定大小的缩略图
	 * 
	 * @param filePath
	 *            图片路径
	 * @param minSideLength
	 *            最小的宽度或高度，不使用此限制可传-1，将只使用maxNumOfPixels来限制，
	 *            若两参数都为-1，将使用原始尺寸；若两参数都指定，将使用较小的尺寸限制
	 * @param maxNumOfPixels
	 *            最大的宽度*高度，不使用此限制可传-1，将只使用minSideLength来限制，
	 *            若两参数都为-1，将使用原始尺寸；若两参数都指定，将使用较小的尺寸限制
	 * @return 指定大小的缩略图
	 * @throws Exception
	 *             图片解码时异常时抛出
	 */
	public static Bitmap createImageThumbnail(String filePath, int minSideLength, int maxNumOfPixels) throws Exception {
		return createImageThumbnail(filePath, minSideLength, maxNumOfPixels, false);

	}

	/**
	 * 获取图片指定大小的缩略图，并且根据属性和参数判断是否需要相关的旋转
	 * 
	 * @param filePath
	 *            图片路径
	 * @param minSideLength
	 *            最小的宽度或高度，不使用此限制可传-1，将只使用maxNumOfPixels来限制，
	 *            若两参数都为-1，将使用原始尺寸；若两参数都指定，将使用较小的尺寸限制
	 * @param maxNumOfPixels
	 *            最大的宽度*高度，不使用此限制可传-1，将只使用minSideLength来限制，
	 *            若两参数都为-1，将使用原始尺寸；若两参数都指定，将使用较小的尺寸限制
	 * @param isRotate
	 *            是否需要根据属性进行相应的旋转， true 需要，false不需要
	 * @return 指定大小的缩略图
	 * @throws Exception
	 *             图片解码时异常时抛出
	 */
	public static Bitmap createImageThumbnail(String filePath, int minSideLength, int maxNumOfPixels, boolean isRotate) throws Exception {
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);
		opts.inSampleSize = computeSampleSize(opts, minSideLength, maxNumOfPixels);
		opts.inJustDecodeBounds = false;

		bitmap = BitmapFactory.decodeFile(filePath, opts);
		Bitmap returnBitmap = bitmap;
		int angle = readPictureDegree(filePath);
		if (angle != 0 && angle != -1 && isRotate) {
			// 需要旋转
			returnBitmap = rotaingBitmap(angle, bitmap);
			if (bitmap != null && !bitmap.isRecycled()) {
				// 回收并且置为null
				bitmap.recycle();
				bitmap = null;
				// System.gc();
			}
		}

		return returnBitmap;
	}

	/**
	 * 获取图片指定大小的缩略图
	 * 
	 * @param ContentResolver
	 * @param Uri
	 * @param minSideLength
	 *            最小的宽度或高度，不使用此限制可传-1，将只使用maxNumOfPixels来限制，
	 *            若两参数都为-1，将使用原始尺寸；若两参数都指定，将使用较小的尺寸限制
	 * @param maxNumOfPixels
	 *            最大的宽度*高度，不使用此限制可传-1，将只使用minSideLength来限制，
	 *            若两参数都为-1，将使用原始尺寸；若两参数都指定，将使用较小的尺寸限制
	 * @return 指定大小的缩略图
	 * @throws Exception
	 *             图片解码时异常时抛出
	 */
	public static Bitmap createImageThumbnail(ContentResolver resolver, Uri uri, int minSideLength, int maxNumOfPixels) throws Exception {
		return createImageThumbnail(resolver, uri, minSideLength, maxNumOfPixels, false);

	}

	/**
	 * 获取图片指定大小的缩略图并且根据属性做旋转，并且根据属性和参数判断是否需要相关的旋转
	 * 
	 * @param ContentResolver
	 * @param Uri
	 * @param minSideLength
	 *            最小的宽度或高度，不使用此限制可传-1，将只使用maxNumOfPixels来限制，
	 *            若两参数都为-1，将使用原始尺寸；若两参数都指定，将使用较小的尺寸限制
	 * @param maxNumOfPixels
	 *            最大的宽度*高度，不使用此限制可传-1，将只使用minSideLength来限制，
	 *            若两参数都为-1，将使用原始尺寸；若两参数都指定，将使用较小的尺寸限制
	 * @param isRotate
	 *            是否需要根据属性进行相应的旋转， true 需要，false不需要
	 * @return 指定大小的缩略图
	 * @throws Exception
	 *             图片解码时异常时抛出
	 */
	public static Bitmap createImageThumbnail(ContentResolver resolver, Uri uri, int minSideLength, int maxNumOfPixels, boolean isRotate)
			throws Exception {
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		InputStream is = resolver.openInputStream(uri);
		BitmapFactory.decodeStream(is, null, opts);

		opts.inSampleSize = computeSampleSize(opts, minSideLength, maxNumOfPixels);
		opts.inJustDecodeBounds = false;

		InputStream is2 = resolver.openInputStream(uri);
		bitmap = BitmapFactory.decodeStream(is2, null, opts);
		Bitmap returnBitmap = bitmap;
		int angle = getOrientation(resolver, uri);
		if (angle != 0 && angle != -1 && isRotate) {
			// 需要旋转
			returnBitmap = rotaingBitmap(angle, bitmap);
			if (bitmap != null && !bitmap.isRecycled()) {
				// 回收并且置为null
				bitmap.recycle();
				bitmap = null;
				// System.gc();
			}
		}
		if (is != null) {
			is.close();
		}
		if (is2 != null) {
			is2.close();
		}
		return returnBitmap;

	}

	/*
	 * 旋转图片
	 * 
	 * @param angle
	 * 
	 * @param bitmap
	 * 
	 * @return Bitmap
	 */
	public static Bitmap rotaingBitmap(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 获得相册中：旋转的角度
	 * 
	 * @param resolver
	 * @param uri
	 * @return
	 */
	public static int getOrientation(ContentResolver resolver, Uri uri) {
		Cursor cursor = resolver.query(uri, new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);
		if (cursor == null) {
			return -1;
		}
		if (cursor.getCount() != 1) {
			return -1;
		}
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	private static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 计算inSampleSize值，图片缩放后大小为1/inSampleSize
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 保存图片到sdcard上
	 * 
	 * @param bm
	 *            Bitmap对象
	 * @param imgname
	 *            文件名
	 * @param format
	 *            文件格式，如果format = null ，默认jpg的
	 * @param quality
	 *            质量 0-100 ,0 压缩得最狠，100不压缩， 一般传30-60质量还可以
	 * @throws IOException
	 */
	public static void saveBitmapToSdCard(Bitmap bm, String imgname, CompressFormat format, int quality) throws IOException {
		File f = new File(Environment.getExternalStorageDirectory() + imgname);
		// 根据文件地址创建文件
		if (f.exists()) {
			f.delete();
		}
		FileOutputStream fOut = new FileOutputStream(f);
		if (format == null)
			format = Bitmap.CompressFormat.JPEG;
		bm.compress(format, quality, fOut);
		fOut.flush();
		fOut.close();
	}

	/**
	 * 保存图片到手机上
	 * 
	 * @param bm
	 *            Bitmap对象
	 * @param imgname
	 *            文件名
	 * @param format
	 *            文件格式，如果format = null ，默认jpg的
	 * @param quality
	 *            质量 0-100 ,0 压缩得最狠，100不压缩， 一般传30-60质量还可以
	 */
	public static String saveBitmapToLocal(Bitmap bm, String imgname, CompressFormat format, int quality) {
		String filePath = null;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			filePath = Environment.getExternalStorageDirectory() + imgname;
		} else {
			filePath = Environment.getRootDirectory() + imgname;
		}
		if (filePath == null || filePath.isEmpty()) {
			return null;
		}
		File f = new File(filePath);
		// 根据文件地址创建文件
		if (f.exists()) {
			f.delete();
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			if (format == null)
				format = Bitmap.CompressFormat.JPEG;
			bm.compress(format, quality, fOut);
			return filePath;
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return null;
		} finally {
			try {
				if (fOut != null) {
					fOut.flush();
					fOut.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Bitmap 转 byte[]
	 * 
	 * @param bm
	 *            Bitmap对象
	 * @param format
	 *            文件格式，如果format = null ，默认jpg的
	 * @param quality
	 *            质量 0-100 ,0 压缩得最狠，100不压缩， 一般传30-60质量还可以
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm, CompressFormat format, int quality) {
		if (bm == null) {
			return null;
		}
		if (format == null)
			format = Bitmap.CompressFormat.JPEG;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(format, quality, baos);
		return baos.toByteArray();
	}

	/**
	 * 压缩图片大小
	 * 
	 * @param bmp
	 * @param maxSize
	 *            图片的最大大小，单位（KB）
	 * @return
	 */
	public static Bitmap imageZoom(Bitmap bmp, double maxSize) {
		// 图片允许最大空间 单位：KB
		// double maxSize =400.00;
		// 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
		Bitmap bitMap = bmp;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		// 将字节换成KB
		double mid = b.length / 1024;
		// 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
		while (mid > maxSize) {
			// 获取bitmap大小 是允许最大大小的多少倍
			double i = mid / maxSize;
			// 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
			// （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
			bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i), bitMap.getHeight() / Math.sqrt(i));
			mid = bmpToByteArray(bitMap, false).length / 1024;
		}
		return bitMap;
	}

	/**
	 * 压缩图片大小
	 * 
	 * @param bmp
	 * @param maxSize
	 *            图片的最大大小，单位（KB）
	 * @return
	 */
	public static byte[] imageZoom2(Bitmap bmp, double maxSize) {
		// 图片允许最大空间 单位：KB
		// double maxSize =400.00;
		// 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
		Bitmap bitMap = bmp;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		// 将字节换成KB
		double mid = b.length / 1024;
		// 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
		while (mid > maxSize) {
			// 获取bitmap大小 是允许最大大小的多少倍
			double i = mid / maxSize;
			// 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
			// （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
			bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i), bitMap.getHeight() / Math.sqrt(i));
			b = bmpToByteArray(bitMap, false);
			mid = b.length / 1024;
		}
		return b;
	}

	/***
	 * 图片的缩放方法
	 * 
	 * @param bgimage
	 *            ：源图片资源
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 *            ：缩放后高度
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
		return bitmap;
	}

	/**
	 * 把bitmap转字节数组
	 * 
	 * @param bmp
	 * @param needRecycle
	 * @return
	 */
	static byte[] bmpToByteArray(final Bitmap bmp, boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 通过uri转bitmap
	 * 
	 * @param resolver
	 * @param uri
	 * @return
	 */
	public static Bitmap decodeUri2Bitmap(ContentResolver resolver, Uri uri) {
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			is = resolver.openInputStream(uri);
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			bitmap = BitmapFactory.decodeStream(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}
}
