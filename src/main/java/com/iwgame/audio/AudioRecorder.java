/**      
 * AudioRecorder.java Create on 2014-2-8     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.audio;

import java.io.FileInputStream;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;

import com.iwgame.utils.AudioUtils;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: AudioRecorder
 * @Description: TODO(声音录制)
 * @author chuanglong
 * @date 2014-2-8 下午3:05:40
 * @Version 1.0
 * 
 */
public class AudioRecorder implements Runnable {
    private static final String TAG = "AudioRecorder";
    /**
     * 声音 临时保存文件名
     */
    public final static String sdcardTempFileName = "msgs_tmp_audio.amr";
    /**
     * 声音临时图片保存文件路径
     */
    public final static String sdcardTempFilePath = Environment.getExternalStorageDirectory() + "/" + sdcardTempFileName;

    /**
     * 声音录制
     */
    private MediaRecorder mRecorder = null;

    private byte[] audioData = null;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
	// TODO Auto-generated method stub

    }

    /**
     * 开始录制
     */
    public boolean startRecording() {
	boolean ret = true ;
	mRecorder = new MediaRecorder();
	// 设置音源为Micphone
	mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	// 设置封装格式
	mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
	mRecorder.setOutputFile(sdcardTempFilePath);
	// 设置编码格式
	mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

	try {
	    mRecorder.prepare();
	   
	} catch (IOException e) {
	    LogUtil.e(TAG, "prepare() failed");
	    ret = false ;
	}
	

	try {
	    mRecorder.start();
	} catch (Exception e) {
	    LogUtil.e(TAG, "start() failed");
	    ret = false ;
	}
	
	return ret ;
	
	
	 

	
//	mRecorder.getMaxAmplitude();
//	double f=10*Math.log(mRecorder.getMaxAmplitude());
    }

    /**
     * 停止录制
     */
    public boolean  stopRecording() {
	boolean ret =true ;
	if(mRecorder!=null)
	{
	    try {
		mRecorder.stop();
	    	mRecorder.release();
	    	mRecorder = null;
		} catch (Exception e) {
		    LogUtil.e(TAG, "start() failed");
		    ret = false ;
		}
    
	}
	return ret ;
	  
    }

    public byte[] getAudioData() {
	try {
	    FileInputStream fin = new FileInputStream(sdcardTempFilePath);
	    int length = fin.available();
	    byte[] buffer = new byte[length];
	    fin.read(buffer);
	    fin.close();
	    audioData = buffer;
	} catch (Exception ex) {

	}
	return audioData;
    }
    
    public long getAudioDuration()
    {
	long ret = 0 ;
	try {
	    ret = AudioUtils.getAmrDuration(sdcardTempFilePath);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return ret ;
    }
    
    
//    private final Timer timer = new Timer(); 
//    private TimerTask task; 
    
    private int BASE = 600; 
    /**
     * 获得分贝数 
     * 更新话筒状态 分贝是也就是相对响度 分贝的计算公式K=20lg(Vo/Vi) Vo当前振幅值 Vi基准值为600：我是怎么制定基准值的呢？ 当20 * Math.log10(mMediaRecorder.getMaxAmplitude() / Vi)==0的时候vi就是我所需要的基准值 
     * 当我不对着麦克风说任何话的时候，测试获得的mMediaRecorder.getMaxAmplitude()值即为基准值。 
     * Log.i("mic_", "麦克风的基准值：" + mMediaRecorder.getMaxAmplitude());前提时不对麦克风说任何话 
     */  
    public int getMicDecibel()
    {
	int db =0 ;
	int ratio = 0 ;
	if(mRecorder != null)
	{
	    ratio = mRecorder.getMaxAmplitude() / BASE;  
	    if (ratio > 1)  
                db = (int) (20 * Math.log10(ratio));  
	   
	}
	LogUtil.d(TAG, String.format("分贝值：[ %d = (int) (20 * Math.log10(%d))]", db,ratio));  
	return db ;
    }
}
