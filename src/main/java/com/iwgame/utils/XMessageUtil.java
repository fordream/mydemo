package com.iwgame.utils;

/**      
 * XMessageUtil.java Create on 2013-8-5     
 *      
 * Copyright (c) 2012 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)
 *
 */



import java.io.UnsupportedEncodingException;



import android.util.Log;

import com.google.protobuf.ByteString;
import com.iwgame.xnode.proto.XMessage;
import com.iwgame.xnode.proto.XMessage.Message.MessageSignature;
import com.iwgame.xnode.proto.XMessage.Message.MessageSignature.SignAlgorithm;

/**
 * @ClassName: XMessageUtil
 * @Description: TODO(...)
 * @author guwei
 * @date 2013-8-5 下午4:19:25
 * @Version 1.0
 * 
 */
public class XMessageUtil {

	//private static final Logger logger = LoggerFactory.getLogger(XMessageUtil.class);
    private static final String TAG = "XMessageUtil";
	public static void makeContentSignature(XMessage.Message.Builder message, SignAlgorithm sa, String signKey) {
		byte[] makeSignBytes = makeSignBytes(message, sa, signKey);
		com.iwgame.xnode.proto.XMessage.Message.MessageSignature.Builder signBuilder = MessageSignature.newBuilder();
		signBuilder.setAlgorithm(sa);
		signBuilder.setSign(ByteString.copyFrom(makeSignBytes));
		message.setSignature(signBuilder);
	}

	public static byte[] makeSignBytes(XMessage.Message.Builder message, SignAlgorithm sa, String signKey) {
		byte[] signKeyBytes;
		try {
			signKeyBytes = signKey.getBytes("utf8");
		} catch (UnsupportedEncodingException e) {
			signKeyBytes = signKey.getBytes();
			Log.w(TAG,"warning:fetch signkey byte array in system default charset");
		}
		byte[] headBytes = message.getHead().toByteArray();
		byte[] contentBytes = message.getContent().toByteArray();

		byte[] encodeBytes = null ;
		switch (sa.getNumber()) {
		case SignAlgorithm.MD5_ENC_VALUE:
			Log.d(TAG,"Sign algorithm=md5");
			//暂时未实现
//			encodeBytes = DigestUtils.md5(addAllBytes(addAllBytes(headBytes, contentBytes), signKeyBytes));
			break;
		case SignAlgorithm.SHA1_ENC_VALUE:
			Log.d(TAG,"Sign algorithm=sha1");
			//暂时未实现
//			encodeBytes = DigestUtils.sha1(addAllBytes(addAllBytes(headBytes, contentBytes), signKeyBytes));
			break;
		default:
			Log.d(TAG,"Sign algorithm=sha1");
			//暂时未实现
//			encodeBytes = DigestUtils.sha1(addAllBytes(addAllBytes(headBytes, contentBytes), signKeyBytes));
			break;
		}
		signKeyBytes = null;
		headBytes = null;
		contentBytes = null;
		return encodeBytes;
	}

	private static byte[] clone(byte[] array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}

	private static byte[] addAllBytes(byte[] array1, byte[] array2) {
		if (array1 == null) {
			return clone(array2);
		} else if (array2 == null) {
			return clone(array1);
		}
		byte[] joinedArray = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}
}

