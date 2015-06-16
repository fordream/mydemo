/**      
 * SendMsgView.java Create on 2013-9-22     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.widget;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.iwgame.audio.AudioRecorder;
import com.iwgame.msgs.common.SendMsgCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.common.ShareSdkManager;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.proto.Msgs.MessageContentType;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.utils.Utils;
import com.iwgame.msgs.widget.smiley.SmileyPanel;
import com.iwgame.msgs.widget.smiley.SmileyPanel.ActionSmileyListener;
import com.iwgame.msgs.widget.smiley.SmileyUtil;
import com.iwgame.msgs.widget.smiley.SmileyVo;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: SendMsgView
 * @Description: 发送消息的消息框view
 * @author 吴禹青
 * @date 2013-9-22 下午6:49:44
 * @Version 1.0
 * 
 */
public class SendMsgView extends LinearLayout implements OnClickListener, ActionSmileyListener {
	private static final String TAG = "SendMsgView";

	public static int BTNTYPE_PIC = 1;
	public static int BTNTYPE_SML = 2;

	private LayoutInflater inflater;
	private Context context;
	private Button sendmsg_btsend;
	private LinearLayout sendmsg_content;
	private LinearLayout syncContentView;
	private LinearLayout sendmsg_bottom_panel;
	private RelativeLayout sendmsg_addattachments_panel;
	public LinearLayout sendmsg_ImageBrower;
	public TextView sendmsg_imageBrower_tipTxt;
	private SmileyPanel sendmsg_smiley_panel;
	private LinearLayout sendmsg_msg_microphoneContent;
	private ImageButton sendmsg_msg_microphone;
	private LinearLayout sendmsg_msg_pictureContent;
	private ImageButton sendmsg_msg_picture;
	private LinearLayout sendmsg_msg_photoContent;
	private ImageButton sendmsg_msg_photo;
	public ImageView sendmsg_addattachments;
	private TextView send_pic_count;
	private EditText sendmsg_text;
	public ImageView sendmsg_btem;

	private SendMsgCallBack sendMsgCallBack;

	/** 操作表情表情按钮的回调 */
	private ActionSmileyButtonListener actionSmileyButtonListener;

	/**
	 * 声音录制状态的回调
	 */
	private AudioRecorderStatusListener audioRecorderStatusListener;

	/**
	 * 控制已添加图片的显示
	 */
	private ActionShowImageVisibilityListener showImageVisibilityListener = null;

	private LinearLayout sendmsg_text_ll;
	private LinearLayout sendmsg_voice_ll;
	private Button sendmsg_keyborad;
	private Button sendmsg_btem2;
	private Button sendmsg_btvoice;

	private WindowManager mWindowManager;

	private RelativeLayout chat_voice_land;
	private ImageView chat_voice_land_ImageView;
	private TextView chat_voice_land_text;
	private AnimationDrawable animationDrawable;

	/**
	 * 屏幕中心坐标
	 */
	public ImageView syncContract;
	public ImageView syncmchat;
	public ImageView syncQQ;
	public ImageView syncSina;
	public ImageView syncWeibo;

	/**
	 * 屏幕中心坐标
	 */
	private int displayCenterX = 0;
	private int displayCenterY = 0;

	/**
	 * 是否是录制中
	 */
	private boolean isAudioRecorder = false;
	private AudioRecorder mAudioRecorder;
	private Handler handler = new Handler();

	// 倒记时显示的数值
	private int tipDuration = SystemConfig.AUDIO_RECORDER_TIPDURATION;

	/**
	 * sendmsgtext 的 OnKeyListener
	 */
	private OnKeyListener sendmsgTextOnKeyListener;
	/**
	 * sendmsgtext 的 OnFocusChangeListener
	 */
	private OnFocusChangeListener sendmsgTextOnFocusChangeListener;

	private Activity activity;
	/** 是否为内部编辑文本框 **/
	private Boolean isInnerEditText = true;

	/**
	 * @param context
	 * @param attrs
	 */
	public SendMsgView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
		displayCenterX = displayMetrics.widthPixels / 2;
		displayCenterY = displayMetrics.heightPixels / 2;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		chat_voice_land = (RelativeLayout) inflater.inflate(R.layout.chat_voice_land, null);
		chat_voice_land_ImageView = (ImageView) chat_voice_land.findViewById(R.id.chat_voice_land_ImageView);
		chat_voice_land_text = (TextView) chat_voice_land.findViewById(R.id.chat_voice_land_text);
		View mainview = (LinearLayout) inflater.inflate(R.layout.send_msg_view, null);
		addView(mainview);
		sendmsg_btsend = (Button) mainview.findViewById(R.id.sendmsg_btsend);
		sendmsg_content = (LinearLayout) mainview.findViewById(R.id.sendmsg_content);
		syncContentView = (LinearLayout) mainview.findViewById(R.id.syncContentView);

		syncContract = (ImageView) mainview.findViewById(R.id.syncContract);
		syncContract.setTag(Boolean.valueOf(false));
		syncmchat = (ImageView) mainview.findViewById(R.id.syncmchat);
		syncmchat.setTag(Boolean.valueOf(false));
		syncQQ = (ImageView) mainview.findViewById(R.id.syncQQ);
		;
		syncQQ.setTag(Boolean.valueOf(false));
		syncSina = (ImageView) mainview.findViewById(R.id.syncSina);
		;
		syncSina.setTag(Boolean.valueOf(false));
		syncWeibo = (ImageView) mainview.findViewById(R.id.syncWeibo);
		;
		syncWeibo.setTag(Boolean.valueOf(false));

		sendmsg_bottom_panel = (LinearLayout) mainview.findViewById(R.id.sendmsg_bottom_panel);
		sendmsg_addattachments_panel = (RelativeLayout) sendmsg_bottom_panel.findViewById(R.id.sendmsg_addattachments_panel);
		sendmsg_ImageBrower = (LinearLayout) sendmsg_bottom_panel.findViewById(R.id.sendmsg_ImageBrower);
		sendmsg_imageBrower_tipTxt = (TextView) mainview.findViewById(R.id.sendmsg_imageBrower_tipTxt);
		sendmsg_smiley_panel = (SmileyPanel) mainview.findViewById(R.id.sendmsg_smiley_panel);

		sendmsg_msg_microphone = (ImageButton) mainview.findViewById(R.id.sendmsg_msg_microphone);
		sendmsg_msg_microphoneContent = (LinearLayout) mainview.findViewById(R.id.sendmsg_msg_microphoneContent);
		sendmsg_msg_picture = (ImageButton) mainview.findViewById(R.id.sendmsg_msg_picture);
		sendmsg_msg_pictureContent = (LinearLayout) mainview.findViewById(R.id.sendmsg_msg_pictureContent);
		sendmsg_msg_photo = (ImageButton) mainview.findViewById(R.id.sendmsg_msg_photo);
		sendmsg_msg_photoContent = (LinearLayout) mainview.findViewById(R.id.sendmsg_msg_photoContent);
		sendmsg_addattachments = (ImageView) mainview.findViewById(R.id.sendmsg_addattachments);
		send_pic_count = (TextView) mainview.findViewById(R.id.send_pic_count);
		sendmsg_text = (EditText) mainview.findViewById(R.id.sendmsg_text);
		sendmsg_btem = (ImageView) mainview.findViewById(R.id.sendmsg_btem);

		sendmsg_smiley_panel.setActionSmileyListener(this);
		sendmsg_addattachments.setOnClickListener(this);

		sendmsg_btem.setOnClickListener(this);
		sendmsg_btsend.setOnClickListener(this);
		sendmsg_msg_microphone.setOnClickListener(this);
		sendmsg_msg_picture.setOnClickListener(this);
		sendmsg_msg_photo.setOnClickListener(this);
		sendmsgTextOnFocusChangeListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {// 获得焦点
					// 在这里可以对获得焦点进行处理
					sendmsg_bottom_panel.setVisibility(View.GONE);
					sendmsg_addattachments_panel.setVisibility(View.GONE);
					sendmsg_smiley_panel.setVisibility(View.GONE);
					setAddattachmentsBg();
					sendMsgCallBack.setListViewLastIndexSelection(700);
				} else {// 失去焦点
					// 在这里可以对输入的文本内容进行有效的验证
				}
			}
		};
		sendmsg_text.setOnFocusChangeListener(sendmsgTextOnFocusChangeListener);

		sendmsgTextOnKeyListener = new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				LogUtil.d(TAG, String.format("-----keyCode[%d][%s]", keyCode, event));
				if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
					// 按下删除键
					Editable edit = sendmsg_text.getEditableText();
					int selectionIndex = sendmsg_text.getSelectionStart();

					if (!edit.equals("") && selectionIndex > 0 && SmileyVo.FORMAT_SUFFIX.equals(String.valueOf(edit.charAt(selectionIndex - 1)))) {
						int tmpIndex = edit.toString().substring(0, selectionIndex - 1).lastIndexOf(SmileyVo.FORMAT_PREFIX);
						if (tmpIndex >= 0) {
							// 代表是需要删除的是表情
							onDelSmiley();
							return true;
						}
					}

				}
				return false;
			}
		};
		sendmsg_text.setOnKeyListener(sendmsgTextOnKeyListener);
		sendmsg_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendMsgCallBack.setListViewLastIndexSelection(700);
			}
		});

		sendmsg_text_ll = (LinearLayout) mainview.findViewById(R.id.sendmsg_text_ll);
		sendmsg_voice_ll = (LinearLayout) mainview.findViewById(R.id.sendmsg_voice_ll);
		sendmsg_keyborad = (Button) mainview.findViewById(R.id.sendmsg_keyborad);
		sendmsg_btem2 = (Button) mainview.findViewById(R.id.sendmsg_btem2);
		sendmsg_btvoice = (Button) mainview.findViewById(R.id.sendmsg_btvoice);
		sendmsg_keyborad.setOnClickListener(this);
		sendmsg_btem2.setOnClickListener(this);

		mAudioRecorder = new AudioRecorder();

		sendmsg_btvoice.setOnTouchListener(new OnTouchListener() {
			int VOICE_LAND_ANIM = 1;
			int VOICE_LAND_CANCEL = 2;
			int voice_land_type = VOICE_LAND_ANIM;
			Runnable autoActionUpRunnable = new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// 开始切换到倒记时
					// actionUp(true);
					handler.post(autoActionUpTipRunnable);
				}
			};

			Runnable autoActionUpTipRunnable = new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (tipDuration / 1000 > 0) {
						// ToastUtil.showToast(SendMsgView.this.context,
						// getResources().getString(R.string.chat_userchat_voice_tiptime_info,
						// tipDuration / 1000), 1000);
						chat_voice_land_text.setText(getResources().getString(R.string.chat_userchat_voice_tiptime_info, tipDuration / 1000));
						handler.postDelayed(autoActionUpTipRunnable, 1000);
						tipDuration = tipDuration - 1000;
					} else {
						// 提交
						actionUp(true);
					}
				}
			};

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// LogUtil.d("---onTouch","ACTION_DOWN:event["+
					// event.getRawX() +":"+ event.getRawY() +
					// "]"+",isAudioRecorder="+ isAudioRecorder);
					if (audioRecorderStatusListener != null)
						audioRecorderStatusListener.setViewPagerScrollable(false);
					showVoiceLandView();
					voice_land_type = VOICE_LAND_ANIM;

					// chat_voice_land_ImageView.setBackgroundResource(R.anim.voice_land);
					// animationDrawable = (AnimationDrawable)
					// chat_voice_land_ImageView.getBackground();
					// animationDrawable.start();

					updateVoiceLandImage();

					sendmsg_btvoice.setText(getResources().getString(R.string.chat_userchat_voice_pre));
					sendmsg_btvoice.setBackgroundResource(R.drawable.chat_msg_voice_bg_pre);
					isAudioRecorder = true;
					handler.postDelayed(autoActionUpRunnable, SystemConfig.AUDIO_RECORDER_MAXDURATION - SystemConfig.AUDIO_RECORDER_TIPDURATION);
					sendMsgCallBack.setAudioRecorderStatus(SendMsgCallBack.AUDIORECORDER_ING);

					if (!mAudioRecorder.startRecording()) {
						// 启动录制异常，可能存储空间不够
						actionUp(false);
						ToastUtil.showToast(SendMsgView.this.context, getResources().getString(R.string.chat_userchat_voice_fail));
					}

					return true;
				case MotionEvent.ACTION_MOVE:
					// LogUtil.d("---onTouch","ACTION_MOVE:event["+
					// event.getRawX() +":"+ event.getRawY() +
					// "]"+",isAudioRecorder="+ isAudioRecorder);
					if (isAudioRecorder) {
						int chat_voice_land_Width = chat_voice_land_ImageView.getMeasuredWidth();
						int chat_voice_land_Height = chat_voice_land.getMeasuredHeight();
						// Log.d("---onTouch","ACTION_MOVE:event["+event.getRawX()+","+event.getRawY()+"]rect["+(displayCenterX
						// - chat_voice_land_Width/2)+","+(displayCenterY -lse
						// chat_voice_land_Height/2)+","+(displayCenterX +
						// chat_voice_land_Width/2)+","+(displayCenterY +
						// chat_voice_land_Height/2)+"]");
						// if (event.getRawX() > displayCenterX -
						// chat_voice_land_Width / 2 && displayCenterX <
						// displayCenterX + chat_voice_land_Width / 2
						// && event.getRawY() > displayCenterY -
						// chat_voice_land_Height / 2 && event.getRawY() <
						// displayCenterY + chat_voice_land_Height / 2)
						if (event.getRawY() < displayCenterY + chat_voice_land_Height) {
							// 需要显示取消
							if (voice_land_type != VOICE_LAND_CANCEL) {
								// Log.d("---onTouch","---换背景为取消");
								// 先停止动画，然后设置背景
								// animationDrawable.stop();
								// 停止波形
								handler.removeCallbacks(updateVoiceLandImageRunnable);

								chat_voice_land_ImageView.setBackgroundResource(R.drawable.voice_land_cancel);
								chat_voice_land_text.setText(getResources().getString(R.string.sendmsgview_voice_cannel_info2));
								voice_land_type = VOICE_LAND_CANCEL;
							}
						} else {
							// 不需要显示取消
							if (voice_land_type != VOICE_LAND_ANIM) {
								// Log.d("---onTouch","---换背景为动画");
								// chat_voice_land_ImageView.setBackgroundResource(R.anim.voice_land);
								// animationDrawable = (AnimationDrawable)
								// chat_voice_land_ImageView.getBackground();
								// animationDrawable.start();
								updateVoiceLandImage();

								voice_land_type = VOICE_LAND_ANIM;
							}
						}
						// LogUtil.d("---onTouch","ACTION_MOVE:event["+event.getEventTime()+"-"+event.getDownTime()+"="+(event.getEventTime()
						// - event.getDownTime())+"]");
						// if( event.getEventTime() - event.getDownTime()
						// >SystemConfig.AUDIO_RECORDER_MAXDURATION)
						// {
						// handler.removeCallbacks(autoActionUpRunnable);
						// actionUp(true);
						// }

						if (chat_voice_land_ImageView.getBackground() != SendMsgView.this.context.getResources().getDrawable(
								R.drawable.voice_land_cancel))
							return false;
					} else {
						return true;
					}
				case MotionEvent.ACTION_UP:
					// LogUtil.d("---onTouch","ACTION_UP:event["+
					// event.getRawX() +":"+ event.getRawY() +
					// "]"+",isAudioRecorder="+ isAudioRecorder);
					if (isAudioRecorder) {
						// Log.d("---onTouch","ACTION_UP:event["+
						// event.getRawX()
						// +":"+ event.getRawY() + "]");
						int chat_voice_land_Width = chat_voice_land_ImageView.getMeasuredWidth();
						int chat_voice_land_Height = chat_voice_land.getMeasuredHeight();
						// boolean isSend = !(event.getRawX() > displayCenterX -
						// chat_voice_land_Width / 2 && displayCenterX <
						// displayCenterX + chat_voice_land_Width / 2
						// && event.getRawY() > displayCenterY -
						// chat_voice_land_Height / 2 && event.getRawY() <
						// displayCenterY + chat_voice_land_Height / 2);
						boolean isSend = !(event.getRawY() < displayCenterY + chat_voice_land_Height);
						handler.removeCallbacks(autoActionUpRunnable);
						handler.removeCallbacks(autoActionUpTipRunnable);
						actionUp(isSend);

						return true;
					}
				case MotionEvent.ACTION_CANCEL:
					// LogUtil.d("---onTouch","ACTION_CANCEL:event["+
					// event.getRawX() +":"+ event.getRawY() +
					// "]"+",isAudioRecorder="+ isAudioRecorder);
					if (isAudioRecorder) {
						boolean isSend = false;
						handler.removeCallbacks(autoActionUpRunnable);
						handler.removeCallbacks(autoActionUpTipRunnable);
						actionUp(isSend);
						return true;
					}

				default:
					// LogUtil.d("---onTouch","default:event["+ event.getRawX()
					// +":"+ event.getRawY() + "]"+",isAudioRecorder="+
					// isAudioRecorder);
					break;

				}
				return false;
			}
		});

	}

	private int delayMillis = 100;

	private void updateVoiceLandImage() {
		int db = mAudioRecorder != null ? mAudioRecorder.getMicDecibel() : 0;
		switch (db / 4) {
		case 0:
			chat_voice_land_ImageView.setBackgroundResource(R.drawable.voice_land_1);
			break;
		case 1:
			chat_voice_land_ImageView.setBackgroundResource(R.drawable.voice_land_2);
			break;
		case 2:
			chat_voice_land_ImageView.setBackgroundResource(R.drawable.voice_land_3);
			break;
		case 3:
			chat_voice_land_ImageView.setBackgroundResource(R.drawable.voice_land_4);
			break;
		case 4:
			chat_voice_land_ImageView.setBackgroundResource(R.drawable.voice_land_5);
			break;
		case 5:
			chat_voice_land_ImageView.setBackgroundResource(R.drawable.voice_land_6);
			break;
		case 6:
			chat_voice_land_ImageView.setBackgroundResource(R.drawable.voice_land_7);
			break;
		default:
			chat_voice_land_ImageView.setBackgroundResource(R.drawable.voice_land_8);
			break;
		}
		if (tipDuration < SystemConfig.AUDIO_RECORDER_TIPDURATION)
			chat_voice_land_text.setText(getResources().getString(R.string.chat_userchat_voice_tiptime_info, tipDuration / 1000));
		else
			chat_voice_land_text.setText(getResources().getString(R.string.sendmsgview_voice_cannel_info));

		handler.postDelayed(updateVoiceLandImageRunnable, delayMillis);
	}

	private Runnable updateVoiceLandImageRunnable = new Runnable() {
		public void run() {
			updateVoiceLandImage();
		}
	};

	/**
	 * 抬起的操作
	 */
	public void actionUp(boolean isSend) {
		handler.removeCallbacks(updateVoiceLandImageRunnable);
		tipDuration = SystemConfig.AUDIO_RECORDER_TIPDURATION;
		hideVoiceLandView();
		sendmsg_btvoice.setText(getResources().getString(R.string.chat_userchat_voice_nor));
		sendmsg_btvoice.setBackgroundResource(R.drawable.chat_msg_voice_bg_nor);
		if (audioRecorderStatusListener != null)
			audioRecorderStatusListener.setViewPagerScrollable(true);

		isAudioRecorder = false;
		// chat_voice_land_ImageView.setBackgroundResource(R.anim.voice_land);
		if (!mAudioRecorder.stopRecording()) {
			ToastUtil.showToast(SendMsgView.this.context, getResources().getString(R.string.chat_userchat_voice_fail));
		} else {

			if (!isSend) {
				// 不发送
				// Log.d("---onTouch","---取消声音的发送");
			} else {
				// 发送
				// Log.d("---onTouch","---发送声音");
				if (mAudioRecorder.getAudioDuration() < 1000) {
					showVoiceTooShortView();
				} else {
					// 调用发送程序
					sendMsgCallBack.send(MessageContentType.VOICE_VALUE, null, mAudioRecorder.getAudioData(), SendMsgCallBack.ACTION_MICROPHONE);
				}
			}
		}
	}

	/**
	 * @return the isAudioRecorder
	 */
	public boolean isAudioRecorder() {
		return isAudioRecorder;
	}

	private void showVoiceTooShortView() {
		WindowManager.LayoutParams lp;
		int pixelFormat;

		pixelFormat = PixelFormat.TRANSLUCENT;

		lp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0,
				WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
						| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				/* | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM */, pixelFormat);
		lp.gravity = Gravity.CENTER;
		lp.setTitle("voicelandtooshortView");
		final RelativeLayout tmp_chat_voice_land = (RelativeLayout) inflater.inflate(R.layout.chat_voice_land, null);
		ImageView tmp_chat_voice_land_ImageView = (ImageView) tmp_chat_voice_land.findViewById(R.id.chat_voice_land_ImageView);
		TextView tmp_chat_voice_land_text = (TextView) tmp_chat_voice_land.findViewById(R.id.chat_voice_land_text);
		tmp_chat_voice_land_ImageView.setBackgroundResource(R.drawable.voice_land_tooshort);
		tmp_chat_voice_land_text.setText(getResources().getString(R.string.sendmsgview_voice_short_info));
		mWindowManager.addView(tmp_chat_voice_land, lp);
		// ((Activity)context).runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		// }});

		final Handler handler = new Handler(Looper.getMainLooper());
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 在此处添加执行的代码
				mWindowManager.removeView(tmp_chat_voice_land);
			}
		};
		handler.postDelayed(runnable, 500);// 打开定时器，执行操作

	}

	private void showVoiceLandView() {
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
		lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		lp.x = 0;
		lp.y = 0;
		lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
		lp.flags = lp.flags | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		lp.flags = lp.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		// lp.flags = lp.flags |
		// WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM ;
		// lp.flags = lp.flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;
		// lp.flags = lp.flags | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ;
		// lp.flags = lp.flags | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
		// ;
		// lp.flags = lp.flags |
		// WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH ;
		// lp.flags = lp.flags |
		// WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES ;
		// lp.flags = lp.flags | WindowManager.LayoutParams.FLAG_FULLSCREEN ;
		// lp.flags = lp.flags | WindowManager.LayoutParams.FLAG_DIM_BEHIND ;
		lp.format = PixelFormat.TRANSLUCENT;
		lp.gravity = Gravity.CENTER;
		lp.setTitle("voicelandView");

		mWindowManager.addView(chat_voice_land, lp);
	}

	private void hideVoiceLandView() {
		try {
			mWindowManager.removeView(chat_voice_land);
		} catch (Exception ex) {
			LogUtil.w(TAG, "removeView error :移除正在录声音的view异常" + ex.getMessage());
		}
	}

	/**
	 * @param context
	 */
	public SendMsgView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (FastClickLimitUtil.isFastClick())
			return;
		if (v.getId() == R.id.sendmsg_btem) {
			// 点击表情
			setAddattachmentsBg();
			if (sendmsg_smiley_panel.getVisibility() == View.GONE) {
				/** 隐藏软键盘 **/
				Utils.hideSoftInput2(context, activity, sendmsg_text);
				new Handler().postDelayed(new Runnable() {

					public void run() {
						sendmsg_bottom_panel.setVisibility(View.VISIBLE);
						sendmsg_addattachments_panel.setVisibility(View.GONE);
						sendmsg_smiley_panel.setVisibility(View.VISIBLE);
						if (showImageVisibilityListener != null) {
							showImageVisibilityListener.showImageVisibility(BTNTYPE_SML, true);
						}
						if(isInnerEditText)
							sendmsg_text.clearFocus();
						sendmsg_smiley_panel.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

							@Override
							public boolean onPreDraw() {
								// TODO Auto-generated method stub
								setSmileyViewPagerHeight(sendmsg_smiley_panel.getMeasuredHeight());
								return true;
							}
						});
					}

				}, 700);
				sendMsgCallBack.setListViewLastIndexSelection(700);
			} else if (sendmsg_smiley_panel.getVisibility() == View.VISIBLE) {
				sendmsg_bottom_panel.setVisibility(View.GONE);
				sendmsg_addattachments_panel.setVisibility(View.GONE);
				sendmsg_smiley_panel.setVisibility(View.GONE);
				if (showImageVisibilityListener != null) {
					showImageVisibilityListener.showImageVisibility(BTNTYPE_SML, false);
				}
				if(isInnerEditText)
					sendmsg_text.clearFocus();
				setSmileyViewPagerHeight(0);
			}
		} else if (v.getId() == R.id.sendmsg_btsend) {
				String msg = "";
				msg = sendmsg_text.getText().toString();
				int msgm = SystemContext.getInstance().getMSGM();
				if (msg.length() > msgm) {
					ToastUtil.showToast(context, getResources().getString(R.string.chat_sendmsg_over_tip, msgm));
				} else {
					sendMsgCallBack.send(MessageContentType.TEXT_VALUE, msg, null, SendMsgCallBack.ACTION_TEXT);
				}
		} else if (v.getId() == R.id.sendmsg_addattachments) {
			// 点击添加图片语音
			setSmileyViewPagerHeight(0);
			if (sendmsg_addattachments_panel.getVisibility() == View.GONE) {
				Utils.hideSoftInput2(context, activity, sendmsg_text);
				new Handler().postDelayed(new Runnable() {

					public void run() {
						sendmsg_bottom_panel.setVisibility(View.VISIBLE);
						sendmsg_addattachments_panel.setVisibility(View.VISIBLE);
						if (showImageVisibilityListener != null) {
							showImageVisibilityListener.showImageVisibility(BTNTYPE_PIC, true);
						}
						sendmsg_smiley_panel.setVisibility(View.GONE);
						if(isInnerEditText)
							sendmsg_text.clearFocus();
						sendmsg_addattachments.setBackgroundResource(R.drawable.chat_msg_addattachments_selector2);
					}

				}, 500);
				sendMsgCallBack.setListViewLastIndexSelection(700);
			} else if (sendmsg_addattachments_panel.getVisibility() == View.VISIBLE) {
				sendmsg_bottom_panel.setVisibility(View.GONE);
				sendmsg_addattachments_panel.setVisibility(View.GONE);
				if (showImageVisibilityListener != null) {
					showImageVisibilityListener.showImageVisibility(BTNTYPE_PIC, false);
				}
				sendmsg_smiley_panel.setVisibility(View.GONE);
				if(isInnerEditText)
					sendmsg_text.clearFocus();
				setAddattachmentsBg();
			}
		} else if (v.getId() == R.id.sendmsg_msg_picture) {
				// 图片
				sendMsgCallBack.send(MessageContentType.IMAGE_VALUE, null, null, SendMsgCallBack.ACTION_PICTURE);
		} else if (v.getId() == R.id.sendmsg_msg_photo) {
				// 拍照
				sendMsgCallBack.send(MessageContentType.IMAGE_VALUE, null, null, SendMsgCallBack.ACTION_PHOTO);
		} else if (v.getId() == R.id.sendmsg_msg_microphone) {
				// 声音
				sendmsg_bottom_panel.setVisibility(View.GONE);
				sendmsg_addattachments_panel.setVisibility(View.GONE);
				sendmsg_smiley_panel.setVisibility(View.GONE);
				if(isInnerEditText)
					sendmsg_text.clearFocus();
				sendmsg_text_ll.setVisibility(View.GONE);
				sendmsg_voice_ll.setVisibility(View.VISIBLE);
		} else if (v.getId() == R.id.sendmsg_keyborad) {
			// 声音界面点键盘
			sendmsg_text_ll.setVisibility(View.VISIBLE);
			sendmsg_voice_ll.setVisibility(View.GONE);
			setEditTextFocus();
		} else if (v.getId() == R.id.sendmsg_btem2) {
			// 声音界面点表情
			sendmsg_text_ll.setVisibility(View.VISIBLE);
			sendmsg_voice_ll.setVisibility(View.GONE);
			Utils.hideSoftInput2(context, activity, sendmsg_text);
			new Handler().postDelayed(new Runnable() {

				public void run() {
					sendmsg_bottom_panel.setVisibility(View.VISIBLE);
					sendmsg_addattachments_panel.setVisibility(View.GONE);
					sendmsg_smiley_panel.setVisibility(View.VISIBLE);
					if(isInnerEditText)
						sendmsg_text.clearFocus();
					sendmsg_smiley_panel.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

						@Override
						public boolean onPreDraw() {
							// TODO Auto-generated method stub
							setSmileyViewPagerHeight(sendmsg_smiley_panel.getMeasuredHeight());
							return true;
						}
					});
				}

			}, 500);
		}

	}

	/**
	 * 设置发送消息的回调
	 * 
	 * @param callback
	 */
	public void setSendMsgCallBack(SendMsgCallBack callback) {
		sendMsgCallBack = callback;
		// if(sendMsgCallBack != null)
		// {
		// sendMsgCallBack.setAudioRecorderStatus(SendMsgCallBack.AUDIORECORDER_STOP);
		// }
	}

	public void setActionSmileyButtonListener(ActionSmileyButtonListener listener) {
		actionSmileyButtonListener = listener;
	}

	public void setAudioRecorderStatusListener(AudioRecorderStatusListener listener) {
		audioRecorderStatusListener = listener;
	}

	public void setActionShowImageVisibilityListener(ActionShowImageVisibilityListener listener) {
		showImageVisibilityListener = listener;
	}

	public interface AudioRecorderStatusListener {
		public void setViewPagerScrollable(boolean able);
	}

	public interface ActionSmileyButtonListener {
		public void setSmileyViewPagerHeight(int height);
	}

	public interface ActionShowImageVisibilityListener {
		public void showImageVisibility(int btnType, boolean isShow);
	}

	private void setSmileyViewPagerHeight(int height) {
		if (actionSmileyButtonListener != null) {
			actionSmileyButtonListener.setSmileyViewPagerHeight(height);
		}
	}

	/**
	 * 设置 发送图片的按钮的显示状态
	 * 
	 * @param visibility
	 */
	public void setAddattachmentsButtonVisibility(int visibility) {
		sendmsg_addattachments.setVisibility(visibility);
	}

	/**
	 * 设置表情是否显示
	 * 
	 * @param visibility
	 */
	public void setSmileyButtonVisibility(int visibility) {
		sendmsg_btem.setVisibility(visibility);
	}

	/**
	 * 设置发送声音的icon是否显示
	 * 
	 * @param visibility
	 */
	public void setSendAudioButtonVisibility(int visibility) {
		sendmsg_msg_microphoneContent.setVisibility(visibility);
		sendmsg_msg_microphone.setVisibility(visibility);
	}

	/**
	 * 设置发送button是否显示
	 * 
	 * @param visibility
	 */
	public void setSendButtonVisibility(int visibility) {
		sendmsg_btsend.setVisibility(visibility);
	}

	/**
	 * 设置分享同步ui显示
	 * 
	 * @param visibility
	 */
	public void setSyncContentViewVisibility(int visibility) {
		syncContentView.setVisibility(visibility);
		if (visibility == View.VISIBLE) {
			sendmsg_content.setVisibility(View.GONE);
			sendmsg_btsend.setVisibility(View.GONE);

			// 根据绑定情况设置分享按钮状态
			if (ShareSdkManager.getInstance().getAuthorizeStatus(context, SinaWeibo.NAME)) {// 已绑定，操作为解除
				syncSina.setTag(Boolean.valueOf(true));
				syncSina.setBackgroundResource(R.drawable.post_share_sina_pre);
			} else {// 未绑定，操作为绑定
				syncSina.setTag(Boolean.valueOf(false));
				syncSina.setBackgroundResource(R.drawable.post_share_sina_nor);
			}
			if (ShareSdkManager.getInstance().getAuthorizeStatus(context, TencentWeibo.NAME)) {// 已绑定，操作为解除
				syncWeibo.setTag(Boolean.valueOf(true));
				syncWeibo.setBackgroundResource(R.drawable.post_share_weibo_pre);
			} else {// 未绑定，操作为绑定
				syncWeibo.setTag(Boolean.valueOf(false));
				syncWeibo.setBackgroundResource(R.drawable.post_share_weibo_nor);
			}

			// 设置分享按钮点击事件
			syncContract.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if ((Boolean) syncContract.getTag()) {
						syncContract.setTag(Boolean.valueOf(false));
						syncContract.setBackgroundResource(R.drawable.post_share_album_nor);
					} else {
						syncContract.setTag(Boolean.valueOf(true));
						syncContract.setBackgroundResource(R.drawable.post_share_album_pre);
					}
				}
			});
			syncmchat.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if ((Boolean) syncmchat.getTag()) {
						syncmchat.setTag(Boolean.valueOf(false));
						syncmchat.setBackgroundResource(R.drawable.post_share_weixin_nor);
					} else {
						syncmchat.setTag(Boolean.valueOf(true));
						syncmchat.setBackgroundResource(R.drawable.post_share_weixin_pre);
					}
				}
			});
			syncQQ.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if ((Boolean) syncQQ.getTag()) {
						syncQQ.setTag(Boolean.valueOf(false));
						syncQQ.setBackgroundResource(R.drawable.post_share_qzone_nor);
					} else {
						syncQQ.setTag(Boolean.valueOf(true));
						syncQQ.setBackgroundResource(R.drawable.post_share_qzone_pre);
					}
				}
			});
			syncSina.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if ((Boolean) syncSina.getTag()) {
						syncSina.setTag(Boolean.valueOf(false));
						syncSina.setBackgroundResource(R.drawable.post_share_sina_nor);
					} else {
						// 绑定
						checkSetSinaWeiboIcon();
					}
				}
			});
			syncWeibo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if ((Boolean) syncWeibo.getTag()) {
						syncWeibo.setTag(Boolean.valueOf(false));
						syncWeibo.setBackgroundResource(R.drawable.post_share_weibo_nor);
					} else {
						// 绑定
						checkSetTencentWeiboIcon();
					}
				}
			});
		}
	}

	/**
	 * 
	 */
	private void checkSetSinaWeiboIcon() {
		if (ShareSdkManager.getInstance().getAuthorizeStatus(context, SinaWeibo.NAME)) {// 已绑定
			syncSina.setTag(Boolean.valueOf(true));
			syncSina.setBackgroundResource(R.drawable.post_share_sina_pre);
		} else {// 未绑定，操作为绑定
			// 绑定
			ShareManager.getInstance().authorize(context, 0, SinaWeibo.NAME, new ShareCallbackListener() {

				@Override
				public void doSuccess(String plamType) {
					syncSina.setTag(Boolean.valueOf(true));
					syncSina.setBackgroundResource(R.drawable.post_share_sina_pre);
				}

				@Override
				public void doFail() {
					syncSina.setTag(Boolean.valueOf(false));
					syncSina.setBackgroundResource(R.drawable.post_share_sina_nor);
				}
			});
		}
	}

	/**
	 * 
	 */
	private void checkSetTencentWeiboIcon() {
		if (ShareSdkManager.getInstance().getAuthorizeStatus(context, TencentWeibo.NAME)) {// 已绑定
			syncWeibo.setTag(Boolean.valueOf(true));
			syncWeibo.setBackgroundResource(R.drawable.post_share_weibo_pre);
		} else {// 未绑定，操作为绑定
			ShareManager.getInstance().authorize(context, 0, TencentWeibo.NAME, new ShareCallbackListener() {

				@Override
				public void doSuccess(String plamType) {
					syncWeibo.setTag(Boolean.valueOf(true));
					syncWeibo.setBackgroundResource(R.drawable.post_share_weibo_pre);
				}

				@Override
				public void doFail() {
					syncWeibo.setTag(Boolean.valueOf(false));
					syncWeibo.setBackgroundResource(R.drawable.post_share_weibo_nor);
				}
			});
		}
	}

	/**
	 * 返回发送的button
	 * 
	 * @return
	 */
	public Button getSendButton() {
		return sendmsg_btsend;
	}

	/**
	 * 获得输入文本的输入框
	 * 
	 * @return
	 */
	public EditText getSendMsgEditText() {
		return sendmsg_text;
	}

	/**
	 * 设置编辑框的无内容的提示
	 * 
	 * @param hint
	 */
	public void setEditTextHint(String hint) {
		sendmsg_text.setHint(hint);
	}

	/**
	 * 设置隐藏下面的输入表情的panel
	 */
	public void setHideSendMsgSmileypanel() {
		sendmsg_bottom_panel.setVisibility(View.GONE);
		sendmsg_addattachments_panel.setVisibility(View.GONE);
		sendmsg_smiley_panel.setVisibility(View.GONE);
		if(isInnerEditText)
			sendmsg_text.clearFocus();
		setAddattachmentsBg();
		setSmileyViewPagerHeight(0);
	}

	public void setEditTextFocus() {
		sendmsg_text.requestFocus();
		Utils.showSoftInput(context, sendmsg_text);
	}

	public void setEditTextFocus2() {
		sendmsg_text.setFocusable(true);
		sendmsg_text.setFocusableInTouchMode(true);
		sendmsg_text.requestFocus();
		Utils.showSoftInput2(context, sendmsg_text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.widget.smiley.SmileyPanel.ActionSmileyListener#onAddSmiley
	 * (java.lang.String)
	 */
	@Override
	public void onAddSmiley(SmileyVo vo) {
		// TODO Auto-generated method stub
		String imageName = vo.getName();
		try {
			Field f = (Field) R.drawable.class.getDeclaredField(imageName);
			int i = f.getInt(R.drawable.class);
			Drawable drawable = context.getResources().getDrawable(i);
			if (drawable != null) {
				// Paint paint = new Paint();
				// paint.setTextSize(sendmsg_text.getTextSize());
				// paint.setTextScaleX(sendmsg_text.getTextScaleX());
				// drawable.setBounds(0, 0, (int) TextUtil.getTextHeight(paint),
				// (int) TextUtil.getTextHeight(paint));
				drawable.setBounds(0, 0, context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width), context.getResources()
						.getDimensionPixelSize(R.dimen.global_string_smiley_heigth));
				// sendmsg_text.getText().append(Utils.ReplaceString(vo.getFormat(),
				// drawable));
				int index = sendmsg_text.getSelectionStart();// 获取光标所在位置
				Editable edit = sendmsg_text.getEditableText();// 获取EditText的文字
				if (index < 0 || index >= edit.length()) {
					edit.append(Utils.ReplaceString(vo.getFormat(), drawable));
				} else {
					edit.insert(index, Utils.ReplaceString(vo.getFormat(), drawable));// 光标所在位置插入文字
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.widget.smiley.SmileyPanel.ActionSmileyListener#onDelSmiley
	 * ()
	 */
	@Override
	public void onDelSmiley() {
		// TODO Auto-generated method stub
		// sendmsg_text.getText().removeSpan(what)
		Editable edit = sendmsg_text.getEditableText();
		int selectionIndex = sendmsg_text.getSelectionStart();

		if (!edit.equals("") && selectionIndex > 0 && SmileyVo.FORMAT_SUFFIX.equals(String.valueOf(edit.charAt(selectionIndex - 1)))) {
			int tmpIndex = edit.toString().substring(0, selectionIndex - 1).lastIndexOf(SmileyVo.FORMAT_PREFIX);
			if (tmpIndex >= 0) {
				edit.delete(tmpIndex, selectionIndex);
			}
		}
		selectionIndex = sendmsg_text.getSelectionStart();
		sendmsg_text.setText(SmileyUtil.ReplaceSmiley(context, sendmsg_text.getText().toString(),
				context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width),
				context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_heigth)));
		sendmsg_text.setSelection(selectionIndex);

	}

	/**
	 * 隐藏表情的view
	 * 
	 * @return true，隐藏掉： false ，本身就没有显示，不需要隐藏
	 */
	public boolean hideSmileyView() {
		setAddattachmentsBg();
		if (sendmsg_bottom_panel.getVisibility() != View.GONE) {
			sendmsg_bottom_panel.setVisibility(View.GONE);
			sendmsg_addattachments_panel.setVisibility(View.GONE);
			sendmsg_smiley_panel.setVisibility(View.GONE);
			if(isInnerEditText)
				sendmsg_text.clearFocus();
			setSmileyViewPagerHeight(0);
			return true;
		} else
			return false;
	}

	/**
	 * 隐藏下边的panel或输入法
	 */
	public void hidePanelAndSoftInput() {
		Utils.hideSoftInput2(context, activity, sendmsg_text);
		setAddattachmentsBg();
		if (sendmsg_bottom_panel.getVisibility() != View.GONE) {
			sendmsg_bottom_panel.setVisibility(View.GONE);
			sendmsg_addattachments_panel.setVisibility(View.GONE);
			sendmsg_smiley_panel.setVisibility(View.GONE);
		}

	}

	/**
	 * 显示已经添加图片的数量
	 * 
	 * @param count
	 */
	public void showSendPicCount(int count) {
		send_pic_count.setVisibility(View.VISIBLE);
		send_pic_count.setText(count + "");
	}

	/**
	 * 隐藏已经添加图片的数量
	 * 
	 * @param count
	 */
	public void hideSendPicCount() {
		send_pic_count.setVisibility(View.INVISIBLE);
		send_pic_count.setText("");
	}

	/**
	 * 设置进来外面的内容编辑框，而不用内部的编辑框,内部编辑框将隐藏
	 * 
	 * @param editText
	 */
	public void setEditTextView(EditText editText) {
		isInnerEditText = false;
		sendmsg_text.setVisibility(View.GONE);
		sendmsg_text = editText;
		sendmsg_text.setOnFocusChangeListener(sendmsgTextOnFocusChangeListener);
		sendmsg_text.setOnKeyListener(sendmsgTextOnKeyListener);
		sendmsg_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendMsgCallBack.setListViewLastIndexSelection(700);
			}
		});
	}

	public void setActivity(Activity act) {
		this.activity = act;
	}
	
	/**
	 * 
	 */
	private void setAddattachmentsBg(){
		if(sendmsg_addattachments.isEnabled())
			sendmsg_addattachments.setBackgroundResource(R.drawable.chat_msg_addattachments_selector);
		else
			sendmsg_addattachments.setBackgroundResource(R.drawable.chat_msg_addattachments_pre2);
	}

}
