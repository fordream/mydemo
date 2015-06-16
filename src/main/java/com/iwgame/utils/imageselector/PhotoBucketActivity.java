/**      
 * FollowSearchGameActivity.java Create on 2013-9-5     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.utils.imageselector;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseListActivity;
import com.iwgame.utils.ToastUtil;
import com.iwgame.utils.imageselector.BitmapCache.ImageCallBack;
import com.youban.msgs.R;


public class PhotoBucketActivity extends BaseListActivity implements OnItemClickListener{

	protected static final String TAG = "PhotoBucketActivity";
	
	private List<ImageBucket> bucketList;

	private BitmapCache bitmapCache;
	private ListView listView; 

	/**
	 * 
	 */
	@Override
	protected void initialize() {
		// 获取上一个页面的传值
		bucketList = AlbumHelper.getHelper().getImageBucketList(false);
		if(bucketList == null){
			ToastUtil.showToast(this, "相册获取失败!");
			finish();
		}else{
			// 显示左边
			setLeftVisible(true);
			// 显示右边
			setRightVisible(false);
			titleTxt.setText("相册目录");
			// 设置内容UI
			LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
			View view = (LinearLayout) View.inflate(this, R.layout.img_bucket_list_pop, null);
			listView = (ListView) view.findViewById(R.id.listView1);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			contentView.addView(view, params);
			bitmapCache = BitmapCache.getInstance();
			ImageBucketAdapter adapter=new ImageBucketAdapter();
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
		}
	}

	public ImageCallBack callBack=new ImageCallBack() {

		@Override
		public void doImageLoad(ImageView iv, Bitmap bitmap, Object... params) {
			if(iv!=null&&bitmap!=null){
				String url=params[0].toString();
				if(url!=null&&iv.getTag()!=null&&url.equals(iv.getTag().toString())){
					iv.setImageBitmap(bitmap);
				}else{
					Log.e(TAG, "图片不相同...");
				}
			}else{
				Log.e(TAG, "你写了些什么！？bitmap怎么是null");
			}

		}
	};
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseListActivity2#getListData(long, int)
	 */
	@Override
	protected void getListData(long offset, int limit) {
		super.getListData(offset, limit);
		
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent data=new Intent();  
        data.putExtra("position", position);  
        data.putExtra("bucketName", bucketList.get(position).bucketName);  
        //请求代码可以自己设置，这里设置成20000  
        setResult(20000, data);  
        //关闭掉这个Activity  
        finish();  
	}
	
	
	class ImageBucketAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(bucketList!=null)
				return bucketList.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageBucket bucket=bucketList.get(position);
			if(convertView==null){
				convertView=LayoutInflater.from(PhotoBucketActivity.this).inflate(R.layout.img_bucket_item, null);
			}

			ImageView imageView=(ImageView)convertView.findViewById(R.id.imageView1);
			ImageView imageView2=(ImageView)convertView.findViewById(R.id.imageView2);
//			if(bucket.isSelected)
//				imageView2.setImageResource(R.drawable.img_bucket_duigou);
//			else
//				imageView2.setImageBitmap(null);
			TextView tv1=(TextView)convertView.findViewById(R.id.textView1);
			TextView tv2=(TextView)convertView.findViewById(R.id.textView2);
			tv1.setText(bucket.bucketName);
			tv2.setText(bucket.count+"张");
			if("我的图片".equals(bucket.bucketName))
				bitmapCache.dispBitmap(imageView, bucket.imageList.get(bucket.imageList.size()-1).imagePath, bucket.imageList.get(bucket.imageList.size()-1).thumbnailPath, callBack);	
			else
				bitmapCache.dispBitmap(imageView, bucket.imageList.get(0).imagePath, bucket.imageList.get(0).thumbnailPath, callBack);
			return convertView;
		}

	}
}
