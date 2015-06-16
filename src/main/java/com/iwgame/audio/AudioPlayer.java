/**      
 * AudioPlayer.java Create on 2014-2-10     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.audio;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * @ClassName: AudioPlayer
 * @Description: TODO(声音播放)
 * @author chuanglong
 * @date 2014-2-10 下午5:41:27
 * @Version 1.0
 * 
 */
public class AudioPlayer {

    protected static final String TAG = "AudioPlayer";

    private static byte[] lock = new byte[0];

    private static AudioPlayer instance = null;
    private MediaPlayer mPlayer = null;

    public static AudioPlayer getInstance() {
	if (instance == null) {
	    synchronized (lock) {
		if (instance == null)
		    instance = new AudioPlayer();
		return instance;
	    }
	} else {
	    return instance;
	}
    }

    private AudioPlayer() {
	mPlayer = new MediaPlayer();
    }

    public void startPlaying(Context context,String filePath) {

	AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	 audioManager.setMode(AudioManager.MODE_IN_CALL);// 把模式调成听筒放音模式
	 startPlaying(filePath);
    }
    
    private void startPlaying(String filePath) {

   	try {
   	    if(mPlayer == null)
   		mPlayer = new MediaPlayer();
   	    mPlayer.reset();// 把各项参数恢复到初始状态  
   	    // 设置要播放的文件
   	    mPlayer.setDataSource(filePath);
   	    mPlayer.prepare();
   	    // 播放
   	    mPlayer.start();
   	} catch (IOException e) {
   	    Log.e(TAG, "prepare() failed");
   	}
       }
    
    public void stopPlaying(Context context) {
	 AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	 audioManager.setMode(AudioManager.MODE_NORMAL);
	 stopPlaying();
    }

    // 停止播放
    private void stopPlaying() {
	if(mPlayer != null)
	{
    		mPlayer.release();
    		mPlayer = null;
	}
	
    }
    
    /**
     * 当前播放的点
     * @return
     */
    public int getCurrentPosition()
    {   int ret = 0 ;
        if(mPlayer != null)
	  ret = mPlayer.getCurrentPosition();
        return ret ;
    }

}
