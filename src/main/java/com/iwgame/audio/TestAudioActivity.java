package com.iwgame.audio;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.utils.AudioUtils;
/**      
 * TestAudio.java Create on 2014-2-10     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */
import com.umeng.analytics.MobclickAgent;

/** 
 * @ClassName: TestAudioActivity 
 * @Description: TODO(...) 
 * @author chuanglong
 * @date 2014-2-10 下午5:54:41 
 * @Version 1.0
 * 
 */
public class TestAudioActivity extends BaseSuperActivity{
	private static final String TAG = "TestAudioActivity";

    RecordButton mRecordButton = null ;
    PlayButton mPlayButton = null ;
    
    TextView mLength = null ;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
        //构造界面
        LinearLayout ll = new LinearLayout(this);
        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        mPlayButton = new PlayButton(this);
        ll.addView(mPlayButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        mLength = new TextView(this);
        ll.addView(mLength,
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0));
        
        try {
		long length = AudioUtils.getAmrDuration(AudioRecorder.sdcardTempFilePath);
		mLength.setText("时长：" + length +"ms");
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
        setContentView(ll);
    }
    
    AudioRecorder mAudioRecorder = new AudioRecorder();
    //定义录音按钮
    class RecordButton extends Button {
        OnTouchListener toucher = new OnTouchListener() {
	    
	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction() )
		{
		case MotionEvent.ACTION_DOWN:
    		        setText("松开结束录音");
    		    mAudioRecorder.startRecording();
    		
    		        return true ;
		case MotionEvent.ACTION_UP:
		        setText("按下开始录音");
		        mAudioRecorder.stopRecording();
		        try {
				long length = AudioUtils.getAmrDuration(AudioRecorder.sdcardTempFilePath);
				mLength.setText("时长：" + length +"ms");
			    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
		        //调用发送程序
		     // mAudioRecorder.getAudioData() ;
		        return true ;  
		    default:
			break ;
		
		}
		
		    
		return false;
	    }
	};

        public RecordButton(Context ctx) {
            super(ctx);
            setText("按下开始录音");
            //setOnClickListener(clicker);
            setOnTouchListener(toucher);
        }
    }

    //定义播放按钮
    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
               AudioPlayer.getInstance().startPlaying(TestAudioActivity.this,AudioRecorder.sdcardTempFilePath);
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	MobclickAgent.onPageStart(TAG);
    	MobclickAgent.onResume(this);
    	
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	MobclickAgent.onPageEnd(TAG);
    	MobclickAgent.onPause(this);
    }

}
