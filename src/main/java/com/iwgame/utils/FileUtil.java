/**      
 * FileUtil.java Create on 2013-6-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.content.Context;

/**
 * @ClassName: FileUtil
 * @Description: TODO(...)
 * @author william
 * @date 2013-6-27 下午6:53:57
 * @Version 1.0
 * 
 */
public class FileUtil {

	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 * 
	 * @param sPath
	 *            要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */
	public boolean deleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除单个文件
	 * 
	 * @param File
	 *            被删除文件
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(File file) {
		boolean flag = false;
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 读raw中的文件 id： 资源编号 R.raw.filename;charset :编码，要和文件的编码一样，不然会乱码
	 */
	public static String readRawFile(Context context, int id, String charset) throws IOException {
		String res = "";
		// 得到资源中的Raw数据流
		InputStream in = context.getResources().openRawResource(id);
		// 得到数据的大小
		int length = in.available();
		byte[] buffer = new byte[length];
		// 读取数据
		in.read(buffer);
		// 依test.txt的编码类型选择合适的编码，如果不调整会乱码
		res = EncodingUtils.getString(buffer, charset);
		// 关闭
		in.close();
		return res;

	}

	/*
	 * 读assert中的文件 charset :编码，要和文件的编码一样，不然会乱码，一般“UTF-8”
	 */
	public static String readAssetFile(Context context, String fileName, String charset) throws IOException {
		// String fileName = "test.txt"; //文件名字
		String res = "";
		// 得到资源中的asset数据流
		InputStream in = context.getResources().getAssets().open(fileName);
		int length = in.available();
		byte[] buffer = new byte[length];
		in.read(buffer);
		in.close();
		res = EncodingUtils.getString(buffer, charset);
		return res;
	}

	/*
	 * 写文件到“/data/data/<应用程序名>目录 ”下，追加内容到file中
	 */
	public static void writeFile(Context context, String fileName, String writestr) throws IOException {
		FileOutputStream fout = context.openFileOutput(fileName, Context.MODE_APPEND);
		byte[] bytes = writestr.getBytes();
		fout.write(bytes);
		fout.close();
	}

	/*
	 * 读“/data/data/<应用程序名>目录 ”下文件 charset :编码，要和文件的编码一样，不然会乱码，一般“UTF-8”
	 */
	public static String readFile(Context context, String fileName, String charset) throws IOException {
		String res = "";
		FileInputStream fin = context.openFileInput(fileName);
		int length = fin.available();
		byte[] buffer = new byte[length];
		fin.read(buffer);
		res = EncodingUtils.getString(buffer, charset);
		fin.close();

		return res;

	}
	
	/**
	 * 通过file读取文件
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFile(File file) throws IOException {
		if (file != null) {
			FileInputStream fin = new FileInputStream(file);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			fin.close();
			return buffer;
		} else {
			return null;
		}
	}

	/*
	 * 往SD中的文件追加内容
	 */
	public static void writeSDFile(String fileName, String write_str) throws IOException {
		FileOutputStream fout = new FileOutputStream(fileName, true);
		byte[] bytes = write_str.getBytes();
		fout.write(bytes);
		fout.close();
	}

	/*
	 * 读sd卡中的文件内容，
	 */
	public static String readSDFile(String fileName, String charset) throws IOException {
		String res = "";
		FileInputStream fin = new FileInputStream(fileName);
		int length = fin.available();
		byte[] buffer = new byte[length];
		fin.read(buffer);
		// res = EncodingUtils.getString(buffer, "UTF-8");
		res = EncodingUtils.getString(buffer, charset);
		fin.close();
		return res;
	}

	/*
	 * 读sd卡中的文件内容（用File的方式）
	 */
	public static String readSDFile2(String fileName, String charset) throws IOException {
		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		int length = fis.available();
		byte[] buffer = new byte[length];
		fis.read(buffer);
		// String res = EncodingUtils.getString(buffer, "UTF-8");
		String res = EncodingUtils.getString(buffer, charset);
		fis.close();
		return res;
	}

	/*
	 * 往SD中的文件追加内容 （用File的方式）
	 */
	public static void writeSDFile2(String fileName, String write_str) throws IOException {
		File file = new File(fileName);
		FileOutputStream fos = new FileOutputStream(file, true);
		byte[] bytes = write_str.getBytes();
		fos.write(bytes);
		fos.close();
	}

	/*
	 * 获得SD卡上文件的长度
	 */
	public static long getSDFileLength(String fileName) throws IOException {
		File file = new File(fileName);
		// FileInputStream fis = new FileInputStream(file);
		// int fileLen = fis.available(); //这就是文件大小
		// fis.close();
		return file.length();
	}

	/*
	 * 获得“/data/data/<应用程序名>目录 ”下文件的长度
	 */
	public static long getFileLength(Context context, String fileName) throws IOException {
		File file = context.getFilesDir();
		String path = file.getAbsolutePath();
		File file2 = new File(path + File.separator + fileName);
		int fileLen = 0;
		if (file2.exists()) {
			FileInputStream fis = context.openFileInput(fileName);
			fileLen = fis.available(); // 这就是文件大小
			fis.close();
		}

		return fileLen;
	}

	/*
	 * 重命名“/data/data/<应用程序名>目录 ”下文件的名称
	 */
	public static void reFileName(Context context, String oldName, String newName) {
		File file = context.getFilesDir();
		String path = file.getAbsolutePath();
		File oldfile = new File(path + "/" + oldName);
		File newfile = new File(path + "/" + newName);
		if (oldfile.exists()) {
			oldfile.renameTo(newfile);
		}

	}

	/*
	 * 重命名sd卡上文件
	 */
	public static void reSDFileName(String oldname, String newName) {
		File oldfile = new File(oldname);
		File newfile = new File(newName);
		oldfile.renameTo(newfile);
	}

	/*
	 * 读“/data/data/<应用程序名>目录 ”下文件files文件夹下的所有文件
	 */
	public static File[] getFileLists(Context context) {
		File file = context.getFilesDir();
		File[] array = file.listFiles();
		/*
		 * String[] files = new String[array.length]; for(int i = 0 ; i <
		 * array.length ; i ++){ files[i] =array[i].getName(); }
		 */
		return array;

	}

	public static void delFile(Context context, String fileName) {
		context.deleteFile(fileName);
	}

	/*
	 * String Name = File.getName(); //获得文件或文件夹的名称： String parentPath =
	 * File.getParent(); //获得文件或文件夹的父目录 String path =
	 * File.getAbsoultePath();//绝对路经 String path = File.getPath();//相对路经
	 * File.createNewFile();//建立文件 File.mkDir(); //建立文件夹 File.isDirectory();
	 * //判断是文件或文件夹 File[] files = File.listFiles(); //列出文件夹下的所有文件和文件夹名
	 * File.renameTo(dest); //修改文件夹和文件名 File.delete(); //删除文件夹或文件
	 */

	/**
	 * /data/data/<应用程序名>目录 ”下文件files文件夹下
	 * 逐行追加内容到file中
	 */
	public static void writeFileByLine(Context context, String fileName, String writestr) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			FileOutputStream os = context.openFileOutput(fileName, Context.MODE_APPEND);
			fw = new FileWriter(context.getFilesDir() + "/" + fileName, true);
			bw = new BufferedWriter(fw);
			bw.write(writestr + "\n");
			// bw.newLine();
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	

}
