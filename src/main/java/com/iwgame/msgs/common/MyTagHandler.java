/**      
* MyTagHandler.java Create on 2013-11-25     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.common;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.group.adapter.GroupAdapter2;
import com.iwgame.msgs.module.group.ui.GroupListActivity;
import com.iwgame.msgs.module.user.adapter.UserAdapter2;
import com.iwgame.msgs.module.user.ui.UserListActicity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.Html.TagHandler;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/** 
 * @ClassName: MyTagHandler 
 * @Description: TODO(用于处理自定义html标签) 
 * @author chuanglong
 * @date 2013-11-25 上午9:40:40 
 * @Version 1.0
 * 
 */
public class MyTagHandler implements TagHandler {

	  private int sIndex = 0;
	  private int eIndex = 0;
	  private final Context mContext;
  
	  public  MyTagHandler(Context context) {
	        mContext = context;
	    }
	/* (non-Javadoc)
	 * @see android.text.Html.TagHandler#handleTag(boolean, java.lang.String, android.text.Editable, org.xml.sax.XMLReader)
	 */
	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		// TODO Auto-generated method stub
		  if (tag.toLowerCase().equals("iwgamerecommendgroup")
				  || tag.toLowerCase().equals("iwgamerecommenduser")) {
	            if (opening) {
	            	handleStartTag(tag,output,xmlReader);
	            } else {
	            	handleEndTag(tag,output,xmlReader);
	            }
	        }
	}
	
	 private void handleStartTag(String tag, Editable output,
			    XMLReader xmlReader )
	    {
	    	 sIndex = output.length();
//	    	 try {
//				String id = xmlReader.getContentHandler().toString();
//				String id2 = xmlReader.getProperty("iwgame").toString();
//			} catch (SAXNotRecognizedException e) {
//				// TODO Auto-generated catch block
//				Log.e("onClick", "111111111111111");
//				e.printStackTrace();
//			} catch (SAXNotSupportedException e) {
//				// TODO Auto-generated catch block
//				Log.e("onClick", "111111111111111");
//				e.printStackTrace();
//			}
	    }
	    
	    private void handleEndTag(String tag, Editable output,
	    	    XMLReader xmlReader )
	    {
	    	 eIndex = output.length();
	         output.setSpan(new MSpan(tag), sIndex, eIndex,
	                 Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	    }

	    private class MSpan extends ClickableSpan implements OnClickListener {
	    	String mTag;
	    	public MSpan(String tag)
	    	{
	    		mTag = tag;
	    	}
	        @Override
	        public void onClick(View widget) {
	            // TODO Auto-generated method stub
	            // 具体代码，可以是跳转页面，可以是弹出对话框等等
	      	  if (mTag.toLowerCase().equals("iwgamerecommendgroup"))
  			  {
	      		
					// 跳转到推荐公会列表界面
					Intent intent = new Intent(mContext, GroupListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, GroupAdapter2.MODE_RECOMMEND_GROUP);
					intent.putExtras(bundle);
					mContext.startActivity(intent);
  			  }
	      	  else if (mTag.toLowerCase().equals("iwgamerecommenduser"))
	      	  {
	      		 // 跳转到推荐好友列表界面
				  Intent intent = new Intent(mContext, UserListActicity.class);
				 Bundle bundle = new Bundle();
				 bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TYPE,UserAdapter2.TYPE_RECOMMEND);
				 intent.putExtras(bundle);
				 mContext.startActivity(intent);

	      	  }
	        }
	        
	        
	    }


}
