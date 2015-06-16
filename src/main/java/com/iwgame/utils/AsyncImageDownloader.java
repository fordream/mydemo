package com.iwgame.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.iwgame.msgs.utils.ImageFile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

@Deprecated
public class AsyncImageDownloader {
	/**
	 * 为了加快速度，在内存中开启缓存（主要应用于重复图片较多时，或者同一个图片要多次被访问，比如在ListView时来回滚动）。
	 * 使用SoftReference 是为了解决内存不足的错误（OutOfMemoryError。
	 */
	// private static Map<String, SoftReference<Bitmap>> imageCache = new
	// HashMap<String, SoftReference<Bitmap>>();
	private static LRUCache<String, Bitmap> cache = new LRUCache<String, Bitmap>(
			100); // 容量200是个大概估计值，取决于Bitmap有多大。

	private static HashSet<String> urlList = new HashSet<String>();

	private static Lock lock = new ReentrantLock();

	private static final String TAG = "AsyncImageDownloader";

	/** 固定五个线程来执行任务 */
	private static ExecutorService executorService = Executors
			.newFixedThreadPool(10);

	static {
		cache.setRecycler(new LRUCache.Recycler<Bitmap>() {
			@Override
			public void recycle(Bitmap object) {
				// object.recycle();
			}
		});
	}

	/**
	 * @param imageUrl
	 *            图像url地址
	 * @param callback
	 *            回调接口
	 * @return 返回内存中缓存的图像，第一次加载返回null
	 */
	public static Bitmap load(final Context context, final String imageUrl,
			final ImageCallback callback, final int width, final int height,
			final String imageSize) {

		// 如果缓存过就从缓存中取出数据
		// if (imageCache.containsKey(imageUrl)) {
		// SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
		// if (softReference.get() != null) {
		// return softReference.get();
		// } else {
		// // 被垃圾回收
		// }
		// }
		Bitmap cacheBitmap = cache.get(imageUrl);
		if (cacheBitmap != null) {
			return cacheBitmap;
		}

		lock.lock(); // 取得锁定
		try {
			// 对共享资源进行操作
			if (urlList.contains(imageUrl)) {
				return null;
			} else {
				urlList.add(imageUrl);
			}
		} finally {
			// 一定记着把锁取消掉，锁本身是不会自动解锁的
			lock.unlock();
		}

		// 缓存中没有图像，则从网络上取出数据，并将取出的数据缓存到内存中
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {

					final Bitmap bitmap = getBitmap(context, imageUrl, width,
							height, imageSize);

					lock.lock(); // 取得锁定
					try {
						// 对共享资源进行操作

						if (bitmap != null) {
							// imageCache.put(imageUrl, new
							// SoftReference<Bitmap>(
							// bitmap));
							cache.put(imageUrl, bitmap);

							if (callback != null) {
								callback.dispatch(bitmap);
							}
						}

						urlList.remove(imageUrl);
					} finally {
						// 一定记着把锁取消掉，锁本身是不会自动解锁的
						lock.unlock();
					}

				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		return null;
	}

	private static Bitmap getBitmap(Context context, String url, int width,
			int height, String imageSize) {
		// 从数据库中读取
		Bitmap bitmap = null;

		// bitmap = getBitmapFromDB(context, url, width, height);
		if (bitmap != null) {
			return bitmap;
		}

		// 从网络读取
		URL myFileUrl = null;

		LogUtil.error(url);

		try {
			if (imageSize != null) {
				int index = url.lastIndexOf(".");
				if (index > 0) {
					String suffix = url.substring(index + 1);
					String prefix = url.substring(0, index);
					url = prefix + imageSize + "." + suffix;
				}
			}

			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}

		BitmapFactory.Options ops = null;
		InputStream is = null;
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			// int length = (int) conn.getContentLength();
			// if (length != -1) {

			byte[] imgData = StreamTool.readInputStream(is);

			if (imgData.length > 0) {
				ops = new BitmapFactory.Options();
				ops.outWidth = width;
				ops.outHeight = height;
				bitmap = BitmapFactory.decodeByteArray(imgData, 0,
						imgData.length, ops);

				// saveBitmapIntoDB(context, url, imgData);
				// saveBitmapIntoDB(context, url, bitmap);
				// }
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError ex) {
			LogUtil.error("out of memory when getting thumb, try to GC");
			//System.gc();
			ops.inSampleSize *= 2;
		} finally{
			if(is != null){
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

	/** 从数据库读取 */
	private static Bitmap getBitmapFromDB(Context context, String url,
			int width, int height) {
		String data = ImageDatabaseHelper.getInstance(context).query(url);
		if (data != null) {
			File file = new File(data);
			if (file.exists()) {
				BitmapFactory.Options ops = new BitmapFactory.Options();
				ops.outWidth = width;
				ops.outHeight = height;
				return BitmapFactory.decodeFile(data, ops);
			} else {
				ImageDatabaseHelper.getInstance(context).delete(url);
			}
		}
		return null;
	}

	/** 存入数据库 */
	private static void saveBitmapIntoDB(Context context, String url,
			byte[] data) {
		String fileName = ImageFile.getFileName();
		File file = new File(fileName);
		if (!file.getParentFile().exists()) {
			if (!file.getParentFile().mkdirs()) {
				return;
			}
		}
		try {
			file.createNewFile();

			FileOutputStream out = new FileOutputStream(file);
			out.write(data);
			out.close();

			ImageDatabaseHelper.getInstance(context).save(url, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void saveBitmapIntoDB(Context context, String url,
			Bitmap bitmap) {
		String fileName = ImageFile.getFileName();
		File file = new File(fileName);
		if (!file.getParentFile().exists()) {
			if (!file.getParentFile().mkdirs()) {
				return;
			}
		}
		try {
			file.createNewFile();

			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);

			ImageDatabaseHelper.getInstance(context).save(url, fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/** 对外界开放的回调接口 */
	public static abstract class ImageCallback {
		private final Handler mHandler;

		public ImageCallback(Handler handler) {
			this.mHandler = handler;
		}

		public final void dispatch(Bitmap bitmap) {
			if (mHandler == null) {
				imageLoaded(bitmap);
			} else {
				mHandler.post(new NotificationRunnable(bitmap));
			}
		}

		private final class NotificationRunnable implements Runnable {
			private final Bitmap bitmap;

			public NotificationRunnable(Bitmap bitmap) {
				this.bitmap = bitmap;
			}

			@Override
			public void run() {
				imageLoaded(bitmap);
			}
		}

		// 注意 此方法是用来设置目标对象的图像资源
		public abstract void imageLoaded(Bitmap image);
	}

	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = null;
		try {
			// 获取资源图片
			is = context.getResources().openRawResource(resId);
			return BitmapFactory.decodeStream(is, null, opt);
		} finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
