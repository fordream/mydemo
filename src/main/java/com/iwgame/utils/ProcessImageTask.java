package com.iwgame.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.iwgame.msgs.utils.BlackWhiteFilter;
import com.iwgame.msgs.utils.Image;

public class ProcessImageTask extends AsyncTask<Void, Void, Bitmap> {
	private BlackWhiteFilter filter = new BlackWhiteFilter();
	private Bitmap bitmap;
	private ImageView ima;
	public ProcessImageTask(Bitmap bitmap ,ImageView image) {
		this.bitmap = bitmap;
		ima = image;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	public Bitmap doInBackground(Void... params) {
		Image img = null;
		try
    	{
			img = new Image(bitmap);
			if (filter != null) {
				img = filter.process(img);
				img.copyPixelsFromBuffer();
			}
			return img.getImage();
    	}
		catch(Exception e){
			if (img != null && img.destImage.isRecycled()) {
				img.destImage.recycle();
				img.destImage = null;
				System.gc(); // 提醒系统及时回收
			}
		}
		finally{
			if (img != null && img.image.isRecycled()) {
				img.image.recycle();
				img.image = null;
				System.gc(); // 提醒系统及时回收
			}
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		if(result != null){
			super.onPostExecute(result);
			ima.setImageBitmap(result);	
		}
	}
}
