package com.iwgame.msgs.utils;

import android.os.Environment;

public class ImageFile {
	public static final String DIR = Environment.getExternalStorageDirectory()
			+ "/msgs/image/";

	public static String getFileName() {
		return DIR + System.currentTimeMillis();
	}

}
