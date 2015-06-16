/**      
 * ContactUtil.java Create on 2014-4-24     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;

/**
 * @ClassName: ContactUtil
 * @Description: TODO(...)
 * @author 王卫
 * @date 2014-4-24 下午1:35:58
 * @Version 1.0
 * 
 */
public class ContactUtil {

	/** 得到手机通讯录联系人信息 **/
	public static List<Map<String, String>> getPhoneContacts(Context context) {
		List<Map<String, String>> contacts = new ArrayList<Map<String, String>>();
		final String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
		ContentResolver resolver = context.getContentResolver();
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				Map<String, String> contact = new HashMap<String, String>();
				final int PHONES_NUMBER_INDEX = phoneCursor.getColumnIndexOrThrow(Phone.NUMBER);
				final int PHONES_DISPLAY_NAME_INDEX = phoneCursor.getColumnIndexOrThrow(Phone.DISPLAY_NAME);
				// final int PHONES_CONTACT_ID_INDEX =
				// phoneCursor.getColumnIndexOrThrow(Phone.CONTACT_ID);
				// final int PHONES_PHOTO_ID_INDEX =
				// phoneCursor.getColumnIndexOrThrow(Phone.PHOTO_ID);
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (phoneNumber.isEmpty())
					continue;
				contact.put(Phone.NUMBER, phoneNumber);
				// 得到联系人名称
				String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				contact.put(Phone.DISPLAY_NAME, contactName);
				// 得到联系人ID
				// Long contactid =
				// phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				// 得到联系人头像ID
				// Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
				//
				// //得到联系人头像Bitamp
				// Bitmap contactPhoto = null;
				//
				// //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				// if(photoid > 0 ) {
				// Uri uri
				// =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);
				// InputStream input =
				// ContactsContract.Contacts.openContactPhotoInputStream(resolver,
				// uri);
				// contactPhoto = BitmapFactory.decodeStream(input);
				// }else {
				// contactPhoto = BitmapFactory.decodeResource(getResources(),
				// R.drawable.contact_photo);
				// }
				contacts.add(contact);
			}
try{
    phoneCursor.close();
}catch(Exception ex){
    
}finally{
    phoneCursor= null ;
}
			
		}
		return contacts;
	}

}
