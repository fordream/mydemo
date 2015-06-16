package com.iwgame.utils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Image {

	private String fileName;

	private String filePath;

	private String formName;

	private String contentType = "image/jpeg";

	public Image(String fileName, String formName) {
		this(fileName, formName, null);
	}

	public Image(String filePath, String formName, String contentType) {
		int beginIndex = filePath.lastIndexOf(System
				.getProperty("file.separator"));// The value of file.separator
												// is '\\'
		if (beginIndex < 0) {
			beginIndex = filePath.lastIndexOf("/");
		}
		this.filePath = filePath;
		this.fileName = filePath.substring(beginIndex + 1, filePath.length());
		this.formName = formName;
		if (contentType != null)
			this.contentType = contentType;
	}

	public static byte[] read(String filepath) throws IOException {
		InputStream is = new FileInputStream(filepath);
		DataInputStream input = new DataInputStream(is);
		int len = input.available();
		byte[] content = new byte[len];
		int r = input.read(content, 0, content.length);
		if (r != len) {
			content = null;
			throw new IOException("Image读取错误");
		}
		if(is != null)
			is.close();
		input.close();

		return content;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
