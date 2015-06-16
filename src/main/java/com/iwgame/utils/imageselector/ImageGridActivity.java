package com.iwgame.utils.imageselector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.msgs.module.postbar.object.ImageVo;
import com.iwgame.utils.imageselector.adapter.ImageGridAdapter;
import com.iwgame.utils.imageselector.adapter.ImageGridAdapter.TextCallBack;
import com.youban.msgs.R;

public class ImageGridActivity extends BaseSuperActivity implements OnItemClickListener,OnClickListener{
	public final String TAG=getClass().getSimpleName();
	public final static String IMAGE_MAP="image_map"; 
	public final static String BUCKET_LIST="bucket_list";
	public static Bitmap def;
	private List<ImageItem> dataList=new ArrayList<ImageItem>();
	private List<ImageBucket> imageBucketList = new ArrayList<ImageBucket>();
	private GridView gridView;
	private ImageGridAdapter adapter;
	private AlbumHelper helper;
	private Button selectCommit;
	private TextView bucketBtn;
	private TextView space;
	private TextView image_selected_count;
	private int mScreenWidth;
	private int mScreenHeight;
	private RelativeLayout anchor;
	Animation shake = null; 
	
	public static int RESULTCODE = 200;
	public static int REQUESTCODE = 201;
	
	private List<ImageVo> imageList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.img_grid_activity);
		Object imap = getIntent().getExtras().get(ImageGridActivity.IMAGE_MAP);
		if(imap != null)
			imageList = (List<ImageVo>) imap;
		initView(imageList);
		def=BitmapFactory.decodeResource(getResources(), R.drawable.default_photo);
		dataList.addAll(helper.getAllImageList(true));
		adapter = new ImageGridAdapter(this, dataList, mHandler, mScreenWidth, imageList);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		adapter.setTextCallBack(new TextCallBack() {
			
			@Override
			public void onListen(int count) {
				if(count <= 0){
					count = 0;
				}
				image_selected_count.setText(count + "/"+BitmapBucket.max);
				
			}
		});
	}
	private void initView(List<ImageVo> resImageMap){
		helper=AlbumHelper.getHelper();
		helper.init(this);
		selectCommit=(Button)findViewById(R.id.btn_sel);
		selectCommit.setEnabled(true);
		selectCommit.setTextColor(getResources().getColor(R.color.image_select_btn_text));
		bucketBtn=(TextView)findViewById(R.id.sel_bucket);//相册
		space=(TextView)findViewById(R.id.space);//返回
		image_selected_count = (TextView) findViewById(R.id.image_selected_count);
		if(resImageMap != null)
			image_selected_count.setText( resImageMap.size() + "/"+BitmapBucket.max);
		else
			image_selected_count.setText( 0 + "/"+BitmapBucket.max);
		gridView=(GridView)findViewById(R.id.image_grid);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		anchor=(RelativeLayout)findViewById(R.id.layout_footer);
		imageBucketList = helper.getImageBucketList(true);
		shake = AnimationUtils.loadAnimation(ImageGridActivity.this, R.anim.shake);
		selectCommit.setOnClickListener(this);
		space.setOnClickListener(this);
		bucketBtn.setOnClickListener(this);
	}
	Handler mHandler=new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				image_selected_count.startAnimation(shake);
				//Toast.makeText(ImageGridActivity.this, "最多选择"+BitmapBucket.max+"张图片", 400).show();
				break;
			case 1:
				adapter.notifyDataSetChanged();
				break;
			default:
				
				break;
			}
		}
	};
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		adapter.notifyDataSetChanged();
		
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_sel) {
			Intent intent=new Intent();
			intent.putExtra(IMAGE_MAP,(Serializable) adapter.map);
			setResult(RESULTCODE, intent);
			finish();
		} else if (id == R.id.sel_bucket) {
			Intent intent=new Intent(this, PhotoBucketActivity.class);
			startActivityForResult(intent, 10000);
		} else if (id == R.id.space) {
			finish();
		} else {
		}
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//BitmapBucket.pathList.clear();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(10000 == requestCode && 20000 == resultCode)  
        {  
            int position = data.getExtras().getInt("position");  
            String bucketName = data.getExtras().getString("bucketName");  
            if(imageBucketList.size() > position + 1){
            	if(imageBucketList.get(position).bucketName.equals(bucketName)){
            		dataList.clear();
             		dataList.addAll(imageBucketList.get(position).imageList);
             		mHandler.sendEmptyMessage(1);
             		Intent res=new Intent();  
                    //res.put("dataList", dataList);
             		ImageGridActivity.this.setResult(600, res);
            	}
            }
        }  
	}
	
}
