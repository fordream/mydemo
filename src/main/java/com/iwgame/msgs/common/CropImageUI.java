/**      
 * CropImageUI.java Create on 2014-7-14     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youban.msgs.R;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;

/**
 * @ClassName: CropImageUI
 * @Description: TODO(裁剪图片UI,整体思想是：截取屏幕的截图，然后截取矩形框里面的图片)
 * @author chuanglong
 * @date 2014-7-14 上午11:37:24
 * @Version 1.0
 * 
 */
public class CropImageUI extends BaseActivity implements OnTouchListener, OnClickListener {
    private static final String TAG = "CropImageUI";

    // PhotoView crop_src_image;

    ImageView crop_src_image;
    View crop_box;

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    // private static final String TAG = "11";
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    // These are various options can be specified in the intent.
    private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG; // only
									      // used
									      // with
									      // mSaveUri
    private Uri mSaveUri = null;
    private boolean mSetWallpaper = false;
    private int mAspectX, mAspectY;
    private boolean mDoFaceDetection = true;
    private boolean mCircleCrop = false;
    private final Handler mHandler = new Handler();

    // These options specifiy the output image size and whether we should
    // scale the output to fit it (or just crop it).
    private int mOutputX, mOutputY;
    private boolean mScale;
    private boolean mScaleUp = true;

    boolean mWaitingToPick; // Whether we are wait the user to pick a face.
    boolean mSaving; // Whether the "save" button is already clicked.
    private Bitmap mBitmap;
    private ContentResolver mContentResolver;

    boolean mReturnData;// 是否返回数据
    boolean mCrop;
    Uri mData;// 原始数据的路径
    String mType;// type
    private Bitmap tmp = null;
    private Bitmap cropResult = null;
    private Bitmap result = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	init();

	mContentResolver = getContentResolver();

	Intent intent = getIntent();
	Bundle extras = intent.getExtras();

	if (extras != null) {
	    mData = intent.getData();
	    mType = intent.getType();
	    mReturnData = extras.getBoolean("return-data", true);
	    mCrop = extras.getString("crop").equals("true") ? true : false;
	    if (extras.getString("circleCrop") != null) {
		mCircleCrop = true;
		mAspectX = 1;
		mAspectY = 1;
	    }
	    mAspectX = extras.getInt("aspectX");
	    mAspectY = extras.getInt("aspectY");
	    mOutputX = extras.getInt("outputX");
	    mOutputY = extras.getInt("outputY");
	    mScale = extras.getBoolean("scale", true);
	    mScaleUp = extras.getBoolean("scaleUpIfNeeded", true);
	    mDoFaceDetection = extras.containsKey("noFaceDetection") ? !extras.getBoolean("noFaceDetection") : true;
	    mSaveUri = (Uri) extras.getParcelable(MediaStore.EXTRA_OUTPUT);
	    // mBitmap = (Bitmap) extras.getParcelable("data");
	}

	if (mReturnData == false && mSaveUri == null) {
	    ToastUtil.showToast(this, "保存图片的参数不对，你将无法保存裁剪图片");
	}

	Display mDisplay = getWindowManager().getDefaultDisplay();
	int w = mDisplay.getWidth();
	int h = mDisplay.getHeight();
	if (mData != null) {
	    try {
		if(mData.toString().startsWith("file://"))
		   tmp = ImageUtil.createImageThumbnail( mData.toString().substring("file://".length()+1, mData.toString().length()), w * h, w * h,true);
		else
		    tmp = ImageUtil.createImageThumbnail(mContentResolver, mData, w * h, w * h,true); 
		
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		ToastUtil.showToast(this, "加载图片失败，请返回后，重新加载");
		e.printStackTrace();
	    }
	    crop_src_image.setImageBitmap(tmp);
	} else {
	    ToastUtil.showToast(this, "原始图片资源不对");
	}
	if (tmp != null) {
	    final int bitmap_w = tmp.getWidth();
	    final int bitmap_h = tmp.getHeight();
	    // 计算调整crop_box 和 crop_src_image的中心重合
	    ViewTreeObserver viewTreeObserver = crop_src_image.getViewTreeObserver();
	    viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		boolean isFirst = true;

		@Override
		public void onGlobalLayout() {
		    if (isFirst) {
			isFirst = false;
			matrix.postTranslate((crop_src_image.getWidth() - bitmap_w) / 2, (crop_src_image.getHeight() - bitmap_h) / 2);
			crop_src_image.setImageMatrix(matrix);
		    }
		}
	    });
	}

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
	topRightTextView.setText("确定");
	topRightTextView.setTextColor(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_title_textcolor());
	// topRightTextView.setTextSize(R.dimen.text_large);
	LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
	rightView.addView(topRightTextView);
	rightView.setOnClickListener(this);
	// 设置头中间
	setTitleTxt("");
	// 设置中间内容
	LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
	View view = (RelativeLayout) View.inflate(this, R.layout.image_crop, null);
	contentView.addView(view);
	crop_src_image = (ImageView) view.findViewById(R.id.crop_src_image);
	crop_src_image.setOnTouchListener(this);
	crop_box = view.findViewById(R.id.crop_box);

    }

    public boolean onTouch(View v, MotionEvent event) {
	ImageView view = (ImageView) v;
	// Handle touch events here...
	switch (event.getAction() & MotionEvent.ACTION_MASK) {
	case MotionEvent.ACTION_DOWN:
	    savedMatrix.set(matrix);
	    // 設置初始點位置
	    start.set(event.getX(), event.getY());
	    LogUtil.d(TAG, "mode=DRAG");
	    mode = DRAG;
	    break;
	case MotionEvent.ACTION_POINTER_DOWN:
	    oldDist = spacing(event);
	    LogUtil.d(TAG, "oldDist=" + oldDist);
	    if (oldDist > 10f) {
		savedMatrix.set(matrix);
		midPoint(mid, event);
		mode = ZOOM;
		LogUtil.d(TAG, "mode=ZOOM");
	    }
	    break;
	case MotionEvent.ACTION_UP:
	case MotionEvent.ACTION_POINTER_UP:
	    mode = NONE;
	    LogUtil.d(TAG, "mode=NONE");
	    break;
	case MotionEvent.ACTION_MOVE:

	    if (mode == DRAG) {
		// ...
		matrix.set(savedMatrix);
		matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
	    } else if (mode == ZOOM) {
		float newDist = spacing(event);
		LogUtil.d(TAG, "newDist=" + newDist);
		if (newDist > 10f) {
		    matrix.set(savedMatrix);
		    float scale = newDist / oldDist;
		    matrix.postScale(scale, scale, mid.x, mid.y);
		}
	    }
	    break;
	}

	view.setImageMatrix(matrix);
	return true; // indicate event was handled
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
	float x = event.getX(0) - event.getX(1);
	float y = event.getY(0) - event.getY(1);
	return FloatMath.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
	float x = event.getX(0) + event.getX(1);
	float y = event.getY(0) + event.getY(1);
	point.set(x / 2, y / 2);
    }

    /* 确认裁剪 */
    public void onClick(View v) {
	if (v.getId() == R.id.rightView) {
	    if (mReturnData == false && mSaveUri == null) {
		ToastUtil.showToast(this, "保存图片的参数不对，你将无法保存裁剪图片");
		return;
	    }
	    // 返回裁剪结果
	    Matrix tmp1 = crop_src_image.getImageMatrix();
	    float matrixValue[] = new float[9];
	    tmp1.getValues(matrixValue);
	    Rect rect = crop_src_image.getDrawable().getBounds();
	    // 需要创建的图片的宽度和高度
	    float width = crop_box.getWidth();
	    float height = crop_box.getHeight();
	    // 判断左边
	    if (crop_box.getLeft() < matrixValue[2]) {
		width = width - (matrixValue[2] - crop_box.getLeft());
	    }
	    // 判断右边
	    if (crop_box.getRight() > matrixValue[2] + rect.width() * matrixValue[0]) {
		width = width + (matrixValue[2] + rect.width() * matrixValue[0] - crop_box.getRight());
	    }
	    // 判断top
	    if (crop_box.getTop() < matrixValue[5]) {
		height = height - (matrixValue[5] - crop_box.getTop());
	    }
	    // 判断bottom
	    if (crop_box.getBottom() > matrixValue[5] + rect.height() * matrixValue[0]) {
		height = height + (matrixValue[5] + rect.height() * matrixValue[0] - crop_box.getBottom());
	    }

	    if (width < 0 || height < 0) {
		ToastUtil.showToast(this, "图片未置于裁剪框中，请重新选择");
		return;
	    }

	    cropResult = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(cropResult);
	    float dx = (crop_box.getLeft() < matrixValue[2] ? crop_box.getLeft() - matrixValue[2] : 0) - crop_box.getLeft();
	    float dy = (crop_box.getTop() < matrixValue[5] ? crop_box.getTop() - matrixValue[5] : 0) - crop_box.getTop();
	    canvas.translate(dx, dy);
	    crop_src_image.draw(canvas);
	    canvas.save(Canvas.ALL_SAVE_FLAG);
	    canvas.restore();
	    // 放大图片
	    result = ImageUtil.zoomBitmap(cropResult, crop_box.getWidth(), crop_box.getHeight());
		if(cropResult != null && !cropResult.isRecycled()){ 
			// 回收并且置为null
			cropResult.recycle(); 
			cropResult = null; 
		} 

	    // 保存，返回
	    if (mReturnData) {
		// 在返回结果中返回数据
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putParcelable("data", result);
		intent.putExtras(bundle);
		setResult(Activity.RESULT_OK, intent);
	    } else {
		// 保存在地址中
		OutputStream outputStream = null;
		try {

		    outputStream = mContentResolver.openOutputStream(mSaveUri);
		    if (outputStream != null) {
			result.compress(mOutputFormat, 75, outputStream);
		    }

		    setResult(Activity.RESULT_OK, null);
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    ToastUtil.showToast(this, "裁剪的图片保存成文件失败");
		    Log.e(TAG, "Cannot open file: " + mSaveUri, e);
		    setResult(Activity.RESULT_CANCELED, null);
		} finally {
		    try {
			outputStream.close();
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}

		setResult(Activity.RESULT_OK, null);
	    }

	    this.finish();
	    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    // result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	    // byte[] bitmapByte = baos.toByteArray();
	    //
	    // Intent intent = new Intent();
	    // intent.setClass(getApplicationContext(),
	    // CropImageResultUI.class);
	    // intent.putExtra("bitmap", bitmapByte);
	    //
	    // startActivity(intent);
	}
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
		if(tmp != null && !tmp.isRecycled()){ 
			// 回收并且置为null
			tmp.recycle(); 
			tmp = null; 
		} 
		//System.gc();
    }
    
}
