/**      
* HtmlLoader.java Create on 2013-10-11     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.common;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.utils.LogUtil;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.text.Html.ImageGetter;
import android.widget.TextView;

/** 
 * @ClassName: HtmlLoader 
 * @Description: TODO(异步加载html到textview中，另外可以解决用户在listview中随意滑动的时候多线程并发的问题) 
 * @author 吴禹青
 * @date 2013-10-11 下午4:23:44 
 * @Version 1.0
 * 
 */
public class HtmlLoader {
	private static final String TAG = "HtmlLoader";

	public void loadHtml( String html , TextView textView)
	{
		
		if(cancelPotentialHtmlLoad(html,textView))
		{
			LogUtil.d(TAG, String.format("----新建线程加载html：TextView[%s]=[%s]",textView.toString(),html));
			HtmlLoaderTask task = new HtmlLoaderTask(textView);
			HtmlSpanned  htmlSpanned = new HtmlSpanned(task);
			textView.setTag(htmlSpanned);
			task.execute(html);
		}
		else
		{
			LogUtil.d(TAG, String.format("----有老线程在加载html：TextView[%s]=[%s]",textView.toString(),html));
		}
		
	}
	
	/**
	 * 判断textview是否有线程在为它加载，如果有，判断加载的html是否和现在加载的一些，不一样取消之前的加载（之前加载线程作废）
	 * @param html
	 * @param textView
	 * @return
	 */
	private static boolean cancelPotentialHtmlLoad(String html ,TextView textView)
	{
		HtmlLoaderTask htmlLoaderTask = getHtmlLoaderTask(textView);
		if(htmlLoaderTask != null)
		{
			String tmpHtml = htmlLoaderTask.html;
			if(tmpHtml == null ||(!tmpHtml.equals(html)))
			{
				htmlLoaderTask.cancel(true);
			}
			else
			{
				//相同的html在加载
				return false;
			}
		}
		return true ;
	}
	
	/**
	 * 获得当前在加载的html任务
	 * @param textView
	 * @return
	 */
	private static HtmlLoaderTask getHtmlLoaderTask(TextView textView)
	{
		if(textView != null)
		{
			Object obj = textView.getTag();
			if(obj instanceof HtmlSpanned){
				HtmlSpanned htmlSpanned = (HtmlSpanned)obj;
				return htmlSpanned.getHtmlLoaderTask();
			}
		}
		return null;
	}
	
	/**
	 * 记录当前在加载html的任务，并且设置到textview的tag中
	* @ClassName: HtmlSpanned 
	* @Description: TODO(...) 
	* @author Administrator
	* @date 2013-10-11 下午7:46:55 
	* @Version 1.0
	*
	 */
	static class HtmlSpanned {

		private final WeakReference<HtmlLoaderTask> htmlLoaderTaskReference;
		public HtmlSpanned(HtmlLoaderTask htmlLoaderTask)
		{
			htmlLoaderTaskReference = new WeakReference<HtmlLoaderTask>(htmlLoaderTask);
		}
		public HtmlLoaderTask getHtmlLoaderTask(){
			return htmlLoaderTaskReference.get();
		}
	} 
	
	/**
	 * 异步加载html到textview中，异步task
	* @ClassName: HtmlLoaderTask 
	* @Description: TODO(...) 
	* @author Administrator
	* @date 2013-10-11 下午7:42:17 
	* @Version 1.0
	*
	 */
	class HtmlLoaderTask extends AsyncTask<String,Void,Spanned>{

		private String html ;
		private final WeakReference<TextView> textViewReference;
		public HtmlLoaderTask(TextView textView)
		{
			textViewReference = new WeakReference<TextView>(textView);
		}
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Spanned doInBackground(String... params) {
			// TODO Auto-generated method stub
			html = params[0];
			ImageGetter imageGetter = new Html.ImageGetter() {
				
				@Override
				public Drawable getDrawable(String source) {
					// TODO Auto-generated method stub
					
					//LogUtil.d("Utils", "html,source :" +source);
					Drawable drawable = null ;
					InputStream is = null ;
					try {
						is = (InputStream)new URL(source).getContent();
						drawable = Drawable.createFromStream(is,"src");
						if(drawable != null)
						{
							drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally{
						if(is != null){
							try {
								is.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					
					return drawable;
				}
			};
			return Html.fromHtml(html, imageGetter, new MyTagHandler(textViewReference.get().getContext()));		
		}
		
		@Override
		protected void onPostExecute(Spanned result) {
			// TODO Auto-generated method stub
			if(isCancelled()){
				result = null ;
			}
			if(textViewReference != null){
				TextView textView = textViewReference.get();
				HtmlLoaderTask htmlLoaderTask = getHtmlLoaderTask(textView);				
				if(this == htmlLoaderTask)
				{
					textView.setText(result);
				}
			}
				
		}
	}
	
	 
}
