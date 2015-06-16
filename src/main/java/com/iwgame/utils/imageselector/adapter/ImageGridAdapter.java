package com.iwgame.utils.imageselector.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.iwgame.msgs.module.postbar.object.ImageVo;
import com.iwgame.utils.imageselector.BitmapBucket;
import com.iwgame.utils.imageselector.BitmapCache;
import com.iwgame.utils.imageselector.BitmapCache.ImageCallBack;
import com.iwgame.utils.imageselector.ImageItem;
import com.youban.msgs.R;


public class ImageGridAdapter extends BaseAdapter{
	private TextCallBack textCasllBack=null;
	private final String TAG=getClass().getSimpleName();
	private List<ImageItem> dataList;
	private Activity act;
	public List<String> map=new ArrayList<String>();
	private BitmapCache cache;
	private Handler mHandler;
	private int selectedTotal=0;
	private int mScreenWidth;
	private int imageItemViewWidth;
	
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
	public void setTextCallBack(TextCallBack textCallBack){
		this.textCasllBack=textCallBack;
		this.textCasllBack.onListen(selectedTotal+BitmapBucket.bitlist.size());
	}
	public ImageGridAdapter(Activity act,List<ImageItem> dataList,Handler mHandler, int mScreenWidth){
		this.act=act;
		this.dataList=dataList;
		this.mHandler=mHandler;
		this.mScreenWidth = mScreenWidth;
		this.cache=BitmapCache.getInstance();
	}
	
	public ImageGridAdapter(Activity act,List<ImageItem> dataList,Handler mHandler, int mScreenWidth, List<ImageVo> imageList){
		this.act=act;
		this.dataList=dataList;
		this.mHandler=mHandler;
		this.mScreenWidth = mScreenWidth;
		this.cache=BitmapCache.getInstance();
		if (imageList != null) {
			selectedTotal+=imageList.size();
			for (int i = 0; i < imageList.size(); i++) {
				map.add(imageList.get(i).getPath());
			}
		}
	}
	
	@Override
	public int getCount() {
		if(dataList!=null){
			return dataList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if(convertView==null){
			holder=new Holder();
			convertView=View.inflate(act, R.layout.img_grid_cell, null);
			holder.iv=(ImageView)convertView.findViewById(R.id.img_grid_item);
			holder.selected=(ImageButton) convertView.findViewById(R.id.check_img);
			convertView.setTag(holder);
		}else{
			holder=(Holder)convertView.getTag();
		}
		final ImageItem item=dataList.get(position);
		holder.iv.setTag(item.imagePath);
		cache.dispBitmap(holder.iv, item.imagePath, null, callBack);
		if(map.contains(item.imagePath)){
			item.isSelected = true;
			holder.selected.setVisibility(View.VISIBLE);
			holder.iv.setColorFilter(Color.parseColor("#88000000"));
		}else{
			item.isSelected = false;
			holder.selected.setVisibility(View.INVISIBLE);
			holder.iv.setColorFilter(null);
		}
		holder.iv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String path=dataList.get(position).imagePath;
				if(selectedTotal+BitmapBucket.bitlist.size() < BitmapBucket.max){
					item.isSelected=!item.isSelected;
					if(item.isSelected){
						holder.selected.setVisibility(View.VISIBLE);
						holder.iv.setColorFilter(Color.parseColor("#88000000"));
						selectedTotal+=1;
						if(textCasllBack!=null){
							textCasllBack.onListen(selectedTotal+BitmapBucket.bitlist.size());
						}
						map.add(path);
					}else if(!item.isSelected){
						holder.selected.setVisibility(View.INVISIBLE);
						holder.iv.setColorFilter(null);
						selectedTotal-=1;
						if(textCasllBack!=null){
							textCasllBack.onListen(selectedTotal+BitmapBucket.bitlist.size());
						}
						map.remove(path);
					}
				}else if(selectedTotal+BitmapBucket.bitlist.size() >= BitmapBucket.max){
					if(item.isSelected){
						item.isSelected=!item.isSelected;
						holder.selected.setVisibility(View.INVISIBLE);
						holder.iv.setColorFilter(null);
						selectedTotal--;
						if(textCasllBack!=null){
							textCasllBack.onListen(selectedTotal+BitmapBucket.bitlist.size());
						}
						map.remove(path);
					}else{
						mHandler.sendEmptyMessage(0);
					}
				}
			}
		});
		return convertView;
	}
	class Holder{
		private ImageView iv;
		private ImageButton selected;
	}
	public static interface TextCallBack{
		public void onListen(int count);
	}
}
