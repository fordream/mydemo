package com.iwgame.msgs.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.module.user.ui.ReportActivity;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.widget.photoview.PhotoView;
import com.iwgame.utils.FileUtils;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.utils.imageselector.BitmapCache;
import com.iwgame.utils.imageselector.BitmapCache.ImageCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

public class ImageBrowerActivity extends BaseSuperActivity {

	private static final String TAG = "ImageBrowerActivity";
	DisplayImageOptions options;
	String[] images;
	// 打开显示图片的索引
	int imageindex = 0;
	boolean isShowReportMenu = true;
	Bitmap cachebitmap; // 用于保存
	boolean isLoadNet = true;
	int mode = 0;
	ViewPager pager;
	// 举报类型id
	private long tid;
	// 举报类型
	private int ttype;
	// 内容信息
	private String chatinfo;
	private Button backBtn;
	private LinearLayout saveItem;
	private LinearLayout reportItem;
	private Button rightBtn;
	private TextView page_num_info;
	private boolean flag = true;// 专门用了一个布尔变量来控制最右边的箭头显示的方向，从而控制保存和举报这两个按钮到底是显示还是隐藏
	private Drawable downdrawable;

	private BitmapCache cache = BitmapCache.getInstance();
	private PhotoView selectPhotoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_brower);
		getparams();// 通过intent获取从上一个界面传过来的参数
		init();// 初始化操作
		setListener();// 给界面上的按钮 设置监听器
	}

	/**
	 * 给界面上的按钮 添加监听器
	 */
	private void setListener() {
		// 点击的是返回按钮
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageBrowerActivity.this.finish();
			}
		});
		// 点击的是保存按钮
		saveItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				savaImage();
			}
		});
		// 点击的是举报按钮
		reportItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				reportImage();
			}
		});
		// 点击最右上的箭头按钮
		rightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flag) {
					rightBtn.setBackgroundDrawable(downdrawable);
					saveItem.setVisibility(View.VISIBLE);
					if (isShowReportMenu)
						reportItem.setVisibility(View.VISIBLE);
					else
						reportItem.setVisibility(View.INVISIBLE);
				} else {
					rightBtn.setBackgroundResource(R.drawable.preview_btn_up_nor);
					saveItem.setVisibility(View.INVISIBLE);
					reportItem.setVisibility(View.INVISIBLE);
				}
				flag = !flag;
			}
		});
		// 当viewpager切换的 时候会执行下面的这个方法
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageSelected(int arg0) {
				page_num_info.setText((arg0 + 1) + " / " + images.length);
				selectPhotoView = ((ImagePagerAdapter) pager.getAdapter()).getViews().get(arg0);
			}

		});
	}

	/**
	 * 做一些界面的初始化操作
	 */
	private void init() {
		// 左边返回
		backBtn = (Button) findViewById(R.id.leftBtn);
		saveItem = (LinearLayout) findViewById(R.id.save_item);
		reportItem = (LinearLayout) findViewById(R.id.report_item);
		rightBtn = (Button) findViewById(R.id.rightBtn);
		pager = (ViewPager) findViewById(R.id.pager);
		page_num_info = (TextView) findViewById(R.id.page_num_info);
		downdrawable = getResources().getDrawable(R.drawable.preview_btn_down_nor);
		rightBtn.setBackgroundResource(R.drawable.preview_btn_up_nor);
		saveItem.setVisibility(View.INVISIBLE);
		reportItem.setVisibility(View.INVISIBLE);
		/** 获取可見区域高度 **/
		WindowManager manager = getWindowManager();
		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_launcher).showImageOnFail(R.drawable.ic_launcher)
				.resetViewBeforeLoading(true).cacheOnDisc(true).cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(300)).build();

		if (images != null && images.length > 0) {
			if (imageindex >= images.length || imageindex < 0) {
				imageindex = 0;
			}
			pager.setAdapter(new ImagePagerAdapter(images));

			pager.setCurrentItem(imageindex);
			page_num_info.setText((imageindex + 1) + " / " + images.length);
		}
	}

	/**
	 * 获取从上一个页面传过来的参数
	 */
	private void getparams() {
		// 获得传入的参数
		Intent tmpintent = this.getIntent();
		if (tmpintent != null) {
			Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
			if (tmpbundle != null) {
				images = tmpbundle.getStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES);
				imageindex = tmpbundle.getInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, 0);
				isShowReportMenu = tmpbundle.getBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES_ISSHOWREPORTMENU, true);
				isLoadNet = tmpbundle.getBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_ISLOADNET, true);// 默认从网络加载数据
				mode = tmpbundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, 0);
				tid = tmpbundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID, 0);
				ttype = tmpbundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, 0);
				chatinfo = tmpbundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CHATINFO);
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
	}

	private void savaImage() {
		String imageUri = images[pager.getCurrentItem()];
		try {
			if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
				File SDFile = android.os.Environment.getExternalStorageDirectory();
				// 打开文件
				String url = SDFile.getAbsolutePath() + File.separator + "youban" + File.separator + "msgs_" + imageUri.hashCode() + ".jpg";
				try {
					Bitmap bitmap = null;
					if (selectPhotoView != null){
						selectPhotoView.setDrawingCacheEnabled(true);
						bitmap = selectPhotoView.getDrawingCache();
					}
					if (bitmap != null) {
						FileUtils.writeSDFile(url, ImageUtil.Bitmap2Bytes(bitmap, null, 100));
						// sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
						// Uri.parse(url)));
						sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + url)));
						ToastUtil.showToast(ImageBrowerActivity.this, "保存图片成功,图片路径：" + url);
						UMUtil.sendEvent(this, UMConfig.MSGS_EVENT_USER_SAVE_PHTO, null, null, imageUri, null, true);
					} else {
						ToastUtil.showToast(ImageBrowerActivity.this, "保存文件失败");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					ToastUtil.showToast(ImageBrowerActivity.this, "保存文件到\"" + url + "\"下失败");
					UMUtil.sendEvent(this, UMConfig.MSGS_EVENT_USER_SAVE_PHTO, null, null, imageUri, null, false);
				}
			} else {
				ToastUtil.showToast(ImageBrowerActivity.this, "sd卡存储不存在，无法保存");
				UMUtil.sendEvent(this, UMConfig.MSGS_EVENT_USER_SAVE_PHTO, null, null, imageUri, null, false);
			}
		} catch (Exception e) {
			saveImage2MediaStore(imageUri);
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param imageUri
	 */
	private void saveImage2MediaStore(String imageUri) {
		try {
			if (cachebitmap != null) {
				String url = MediaStore.Images.Media.insertImage(getContentResolver(), cachebitmap, "msgs_" + imageUri.hashCode(), "");
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(url)));
				ToastUtil.showToast(ImageBrowerActivity.this, "保存图片到相册成功");
				UMUtil.sendEvent(this, UMConfig.MSGS_EVENT_USER_SAVE_PHTO, null, null, imageUri, null, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过url获得路径
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	private String getFilePathByContentResolver(Context context, Uri uri) {

		if (null == uri) {

			return null;

		}

		Cursor c = context.getContentResolver().query(uri, null, null, null, null);

		String filePath = null;

		if (null == c) {

			throw new IllegalArgumentException(

			"Query on " + uri + " returns null result.");

		}

		try {

			if ((c.getCount() != 1) || !c.moveToFirst()) {

			} else {

				filePath = c.getString(

				c.getColumnIndexOrThrow(MediaColumns.DATA));

			}

		} finally {

			c.close();

		}

		return filePath;

	}

	private void reportImage() {
		if (images.length > 0) {
			Intent intent = new Intent(this, ReportActivity.class);
			Bundle bundle = new Bundle();
			bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID, tid);
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, ttype);
			bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CHATINFO, chatinfo);
			String imageUri = images[pager.getCurrentItem()];

			bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CONTENT, imageUri);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;
		private Map<Integer, PhotoView> views = new HashMap<Integer, PhotoView>();

		ImagePagerAdapter(String[] images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {

			View imageLayout = inflater.inflate(R.layout.image_brower_page_item, view, false);
			assert imageLayout != null;

			// MultiTouchImageView multiTouchImageView = (MultiTouchImageView)
			// imageLayout.findViewById(R.id.image_brower);

			final PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.image_brower);
			if(views.size() == 0)
				selectPhotoView = photoView;
			views.put(position, photoView);
			photoView.setScaleType(ScaleType.FIT_CENTER);
			// photoView.setScaleType(ScaleType.CENTER_INSIDE);
			photoView.setMinimumScale(0.5f);
			photoView.setMaximumScale(3.0f);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			final TextView textview = (TextView) imageLayout.findViewById(R.id.tv_info);
			textview.setVisibility(View.GONE);
			if (mode != 0) {
				cache.dispBitmap(photoView, images[position], images[position], new ImageCallBack() {

					@Override
					public void doImageLoad(ImageView iv, Bitmap bitmap, Object... params) {
						if (iv != null && bitmap != null) {
							iv.setImageBitmap(bitmap);
						} else {
							Log.e(TAG, "你写了些什么！？bitmap怎么是null");
							String message = "Downloads are denied";
							photoView.setImageBitmap(null);
							textview.setVisibility(View.VISIBLE);
							spinner.setVisibility(View.GONE);
						}

					}
				});
			} else {
				com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(images[position], photoView, options,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri, View view) {
								spinner.setVisibility(View.VISIBLE);
							}

							@Override
							public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
								String message = null;
								switch (failReason.getType()) {
								case IO_ERROR:
									message = "Input/Output error";
									break;
								case DECODING_ERROR:
									message = "Image can't be decoded";
									break;
								case NETWORK_DENIED:
									message = "Downloads are denied";
									break;
								case OUT_OF_MEMORY:
									message = "Out Of Memory error";
									break;
								case UNKNOWN:
									message = "Unknown error";
									break;
								}
								// ToastUtil.showToast(ImageBrowerActivity.this,
								// message,
								// 1000);
								photoView.setImageBitmap(null);
								textview.setVisibility(View.VISIBLE);
								spinner.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
								cachebitmap = loadedImage;
								spinner.setVisibility(View.GONE);
							}
						}, null, isLoadNet);
			}
			// multiTouchImageView.setmActivity(ImageBrowerActivity.this);//
			// 注入Activity.

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		/**
		 * 
		 * @return
		 */
		public Map<Integer, PhotoView> getViews() {
			return views;
		}
	}

}
