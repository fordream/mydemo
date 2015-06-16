package com.iwgame.msgs.module.play.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.LinearLayout.LayoutParams;

import com.google.protobuf.ByteString;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.SendMsgCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.postbar.object.ImageVo;
import com.iwgame.msgs.module.postbar.ui.PublishTopicActivity;

import com.iwgame.msgs.proto.Msgs.ImageBytesDetail;
import com.iwgame.msgs.proto.Msgs.MessageContentType;
import com.iwgame.msgs.proto.Msgs.PlayEvalRequest;
import com.iwgame.msgs.proto.Msgs.PlayOrderAppeal;
import com.iwgame.msgs.utils.DialogUtil;
import com.iwgame.msgs.utils.PhotoUtil;
import com.iwgame.msgs.utils.Utils;
import com.iwgame.msgs.utils.DialogUtil.OKCallBackListener;
import com.iwgame.msgs.widget.ResizeLayout;
import com.iwgame.msgs.widget.SendMsgView;
import com.iwgame.msgs.widget.SendMsgView.ActionShowImageVisibilityListener;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.utils.imageselector.BitmapBucket;
import com.iwgame.utils.imageselector.BitmapCache;
import com.iwgame.utils.imageselector.ImageGridActivity;
import com.iwgame.utils.imageselector.BitmapCache.ImageCallBack;
import com.youban.msgs.R;

/**
 * @ClassName: PlayAppealActivity
 * @Description: TODO(..陪玩评价.)
 * @author Gordon
 * 
 * @Version 1.0
 * 
 */
public class PlayEvaluationActivity extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "PlayEvaluationActivity";
	private long orderId;// 报名单号
	private long playId;// 陪玩商品id

	private View view;
	private Button eval_star1;
	private Button eval_star2;
	private Button eval_star3;
	private Button eval_star4;
	private Button eval_star5;
	private RadioGroup evaluation_radiogroup;
	private EditText evaluation_comment;
	private TextView evaluation_commit;
	private LinearLayout bottomView;
	private SendMsgView sendMsgView;
	private int radio_evaluation = 0;// 标示好中差（1：好，2：中3：差 0：默认不选）
	private int rating_evaluation = 0;// 标示星级
	private List<Button> starList;
	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private static final int NORMALL = 3;

	private static final int MSG_RESIZE = 1;
	private List<ImageVo> images;
	private String photoPath;
	private String photoName;
	private BitmapCache cache;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		images = new ArrayList<ImageVo>();
//		BitmapBucket.max=3;
		getData();
		initial();
	}
   @Override
   protected void onResume(){
//	   BitmapBucket.max=3;
	   super.onResume();
   }
	/**
	 * 
	 */

	private void initial() {
		// TODO Auto-generated method stub
		setTitleTxt("评价");
		evaluation_commit = (TextView) findViewById(R.id.rightText);
		evaluation_commit.setText("提交");
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view = View.inflate(this, R.layout.play_info_evaluation, null);
		contentView.addView(view, params);
		eval_star1 = (Button) view.findViewById(R.id.play_evaluation_star1);
		eval_star2 = (Button) view.findViewById(R.id.play_evaluation_star2);
		eval_star3 = (Button) view.findViewById(R.id.play_evaluation_star3);
		eval_star4 = (Button) view.findViewById(R.id.play_evaluation_star4);
		eval_star5 = (Button) view.findViewById(R.id.play_evaluation_star5);
		bottomView = (LinearLayout) findViewById(R.id.bottomView);
		starList = new ArrayList<Button>();
		evaluation_radiogroup = (RadioGroup) view
				.findViewById(R.id.play_evaluation_main_radio);
		evaluation_comment = (EditText) view
				.findViewById(R.id.play_evaluation_induce);
		eval_star1.setOnClickListener(this);
		eval_star2.setOnClickListener(this);
		eval_star3.setOnClickListener(this);
		eval_star4.setOnClickListener(this);
		eval_star5.setOnClickListener(this);
		starList.add(eval_star1);
		starList.add(eval_star2);
		starList.add(eval_star3);
		starList.add(eval_star4);
		starList.add(eval_star5);
		evaluation_commit.setOnClickListener(this);
		evaluation_commit.setVisibility(View.VISIBLE);
		evaluation_radiogroup
				.setOnCheckedChangeListener(onCheckedChangeListener);
		evaluation_comment.addTextChangedListener(textWatcher);
		// 不允许输入换行
		evaluation_comment
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView arg0, int arg1,
							KeyEvent event) {
						// TODO Auto-generated method stub
						return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
					}
				});
		evaluation_comment.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Log.i("evaluation_comment.onTouch", "is");
					bottomView.setVisibility(View.VISIBLE);
//					 evaluation_commit.setVisibility(View.GONE);
					sendMsgView.hideSmileyView();
					sendMsgView.sendmsg_btem.setEnabled(true);
					sendMsgView.sendmsg_addattachments.setEnabled(false);
					sendMsgView.sendmsg_addattachments
							.setBackgroundResource(R.drawable.chat_msg_addattachments_pre2);
					sendMsgView.sendmsg_btem
							.setBackgroundResource(R.drawable.chat_msg_em_selector);
				}
				return false;
			}
		});
		addView();

		final ResizeLayout layout = (ResizeLayout) findViewById(R.id.root_layout);
		layout.setOnResizeListener(new ResizeLayout.OnResizeListener() {

			public void OnResize(int w, int h, int oldw, int oldh) {
				int change = BIGGER;
				if (h < oldh) {
					change = SMALLER;
				}
				if (h == oldh) {
					change = NORMALL;
				}
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = change;
				mHandler.sendMessage(msg);
			}
		});
		layout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				layout.setFocusable(true);
				layout.setFocusableInTouchMode(true);
				layout.requestFocus();
				bottomView.setVisibility(View.GONE);
				Utils.hideSoftInput2(PlayEvaluationActivity.this,
						PlayEvaluationActivity.this, evaluation_comment);
				return false;
			}

		});
	}

	class InputHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESIZE: {
				if (msg.arg1 == BIGGER) { // 键盘隐藏
					Log.i("InputHandler", "键盘隐藏");
				} else if (msg.arg1 == SMALLER) { // 键盘显示
					Log.i("InputHandler", "键盘显示");
				}
			}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	private InputHandler mHandler = new InputHandler();

	private void addView() {
		// TODO Auto-generated method stub
		// 设置底部发布框
		LinearLayout bottomView = (LinearLayout) findViewById(R.id.bottomView);
		sendMsgView = (SendMsgView) View.inflate(this,
				R.layout.public_send_msg_view, null);
		sendMsgView.sendmsg_addattachments.setEnabled(false);
		sendMsgView.sendmsg_btem.setEnabled(false);
		sendMsgView.sendmsg_addattachments
				.setBackgroundResource(R.drawable.chat_msg_addattachments_pre2);
		sendMsgView.sendmsg_btem
				.setBackgroundResource(R.drawable.chat_msg_em_pre2);
		sendMsgView.setActivity(this);
		bottomView.addView(sendMsgView);
		sendMsgView.setSendAudioButtonVisibility(View.GONE);
		// 设置发送button不显示
		sendMsgView.setSendButtonVisibility(View.GONE);
		// 设置外部的编辑框到控件中
		sendMsgView.setEditTextView(evaluation_comment);

		SendMsgCallBack sendMsgCallBack = new SendMsgCallBack() {

			@Override
			public void send(int msgtype, final String content,
					byte[] contentBytes, int action) {
				if (msgtype == MessageContentType.TEXT_VALUE) {
				} else if (msgtype == MessageContentType.IMAGE_VALUE
						&& action == SendMsgCallBack.ACTION_PHOTO) {
					// 相机
					if (images != null && images.size() >= BitmapBucket.max) {
						ToastUtil.showToast(PlayEvaluationActivity.this, "已上传"
								+ BitmapBucket.max + "张相片，不能继续上传了哦！");
					} else {
						photoName = "msgs_" + System.currentTimeMillis()
								+ "_uban.jpg";
						photoPath = PhotoUtil.sdcardFileRootPath + photoName;
						PhotoUtil.doTakePhoto(PlayEvaluationActivity.this,
								photoPath);
					}
				} else if (msgtype == MessageContentType.IMAGE_VALUE
						&& action == SendMsgCallBack.ACTION_PICTURE) {
					Intent intent = new Intent(PlayEvaluationActivity.this,
							ImageGridActivity.class);
					intent.putExtra(ImageGridActivity.IMAGE_MAP,
							(Serializable) images);
					PlayEvaluationActivity.this.startActivityForResult(intent,
							ImageGridActivity.REQUESTCODE);
				} else if (msgtype == MessageContentType.VOICE_VALUE
						&& action == SendMsgCallBack.ACTION_MICROPHONE) {
					// 声音
				}
			}

			@Override
			public void setAudioRecorderStatus(int status) {
				// TODO Auto-generated method stub

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.iwgame.msgs.common.SendMsgCallBack#setListViewLastIndexSelection
			 * ()
			 */
			@Override
			public void setListViewLastIndexSelection(int delaytime) {
				// TODO Auto-generated method stub
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.iwgame.msgs.common.SendMsgCallBack#createBundPhoneDialog()
			 */
			@Override
			public void createDialog() {
				// TODO Auto-generated method stub

			}

		};
		sendMsgView.setSendMsgCallBack(sendMsgCallBack);
		sendMsgView
				.setActionShowImageVisibilityListener(new ActionShowImageVisibilityListener() {
					@Override
					public void showImageVisibility(int btnType, boolean isShow) {
					}
				});
		bottomView.setVisibility(View.GONE);
	}

	/**
	 * 相册和相机回调
	 * 
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			byte[] photoByte = null;
			switch (requestCode) {
			case PhotoUtil.CAMERA_WITH_DATA:
				// 先缩放
				try {
					Bitmap bitmap = ImageUtil.createImageThumbnail(photoPath,
							-1, SystemConfig.BITMAP_MAX_RESOLUTION, true);
					photoByte = ImageUtil.Bitmap2Bytes(bitmap,
							CompressFormat.JPEG,
							SystemConfig.BITMAP_COMPRESS_QUALITY);
					if (bitmap != null && !bitmap.isRecycled()) {
						// 回收并且置为null
						bitmap.recycle();
						bitmap = null;
						// System.gc();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}
				break;
			case PhotoUtil.PHOTO_PICKED_WITH_DATA:
				ContentResolver resolver = this.getContentResolver();
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				try {
					Bitmap photo = ImageUtil.createImageThumbnail(resolver,
							originalUri, -1,
							SystemConfig.BITMAP_MAX_RESOLUTION, true);
					if (photo != null) {
						photoByte = ImageUtil.Bitmap2Bytes(photo,
								CompressFormat.JPEG,
								SystemConfig.BITMAP_COMPRESS_QUALITY);
						if (photo != null && !photo.isRecycled()) {
							// 回收并且置为null
							photo.recycle();
							photo = null;
							// System.gc();
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}

			case PhotoUtil.CROP_IMAGE_WITH_DATA:

				break;
			}
			if (photoByte != null) {
				addImageView(photoByte, photoPath);
			} else {
				ToastUtil.showToast(
						this,
						getResources().getString(
								R.string.common_add_photo_error));
			}
		} else if (resultCode == ImageGridActivity.RESULTCODE) {
			cache = BitmapCache.getInstance();
			sendMsgView.sendmsg_ImageBrower.removeAllViews();
			images.clear();
			sendMsgView.hideSendPicCount();
			sendMsgView.sendmsg_ImageBrower.setVisibility(View.GONE);
			sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.GONE);
			sendMsgView.sendmsg_imageBrower_tipTxt.setText("还没有上传图片哦");
			sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources()
					.getColor(R.color.message_send_add_image_txt_color));
			List<String> resImageMap = (List<String>) data.getExtras().get(
					ImageGridActivity.IMAGE_MAP);
			int size = resImageMap.size();
			for (int i = 0; i < size; i++) {
				String path = resImageMap.get(i);
				addCacheImageView(path, path);
			}
		}
	}

	/**
	 * 添加图片
	 * 
	 * @param btm
	 */
	private void addImageView(final byte[] photoByte, final String path) {
		if (images == null) {
			return;
		} else if (images.size() == 0) {
			sendMsgView.sendmsg_ImageBrower.removeAllViews();
		}
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file://" + path)));
		final ImageVo imageVo = new ImageVo(path, photoByte);
		images.add(imageVo);
		Bitmap btm = ImageUtil.Bytes2Bimap(photoByte);
		sendMsgView.showSendPicCount(images.size());
		// 增加底部显示发送的图片
		final LinearLayout postbar_topicreply_replyimage_grid_item = (LinearLayout) View
				.inflate(this,
						R.layout.postbar_topicreply_replyimage_grid_item, null);
		sendMsgView.sendmsg_ImageBrower.setVisibility(View.VISIBLE);
		sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.VISIBLE);
		if (images.size() == BitmapBucket.max) {
			sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传"
					+ BitmapBucket.max + "张图片！");
			sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources()
					.getColor(R.color.message_send_add_image_txt_color2));
		} else {
			sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传"
					+ images.size() + "张, 还能选择"
					+ (BitmapBucket.max - images.size()) + "张哦!");
			sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources()
					.getColor(R.color.message_send_add_image_txt_color));
		}
		sendMsgView.sendmsg_ImageBrower
				.addView(postbar_topicreply_replyimage_grid_item);
		final ImageView postbar_topicreply_replyimage_grid_item_image = (ImageView) postbar_topicreply_replyimage_grid_item
				.findViewById(R.id.postbar_topicreply_replyimage_grid_item_image);
		postbar_topicreply_replyimage_grid_item_image.setImageBitmap(btm);
		postbar_topicreply_replyimage_grid_item_image.setTag(new String(path));
		ImageView postbar_topicreply_replyimage_grid_item_del = (ImageView) postbar_topicreply_replyimage_grid_item
				.findViewById(R.id.postbar_topicreply_replyimage_grid_item_del);
		postbar_topicreply_replyimage_grid_item_del
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						images.remove(imageVo);
						sendMsgView.sendmsg_ImageBrower
								.removeView(postbar_topicreply_replyimage_grid_item);
						sendMsgView.showSendPicCount(images.size());
						if (images.size() == BitmapBucket.max) {
							sendMsgView.sendmsg_imageBrower_tipTxt
									.setText("已经上传" + BitmapBucket.max + "张图片！");
							sendMsgView.sendmsg_imageBrower_tipTxt
									.setTextColor(getResources()
											.getColor(
													R.color.message_send_add_image_txt_color2));
						} else {
							sendMsgView.sendmsg_imageBrower_tipTxt
									.setText("已经上传"
											+ images.size()
											+ "张, 还能选择"
											+ (BitmapBucket.max - images.size())
											+ "张哦!");
							sendMsgView.sendmsg_imageBrower_tipTxt
									.setTextColor(getResources()
											.getColor(
													R.color.message_send_add_image_txt_color));
						}
						if (images.size() == 0)
							sendMsgView.hideSendPicCount();
					}
				});
		postbar_topicreply_replyimage_grid_item_image
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						jumpImageBrowerAct(String
								.valueOf(postbar_topicreply_replyimage_grid_item_image
										.getTag()));
					}
				});
		sendMsgView.sendmsg_ImageBrower.setVisibility(View.VISIBLE);
		sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.VISIBLE);
		// 隐藏panel
		// sendMsgView.hidePanelAndSoftInput();
		// sendMsgView.showSendPicCount(1);
	}

	/**
	 * 
	 * @param btm
	 */
	private void addCacheImageView(final String imagePath,
			final String thumbnailPath) {
		// 增加底部显示发送的图片
		final LinearLayout postbar_topicreply_replyimage_grid_item = (LinearLayout) View
				.inflate(this,
						R.layout.postbar_topicreply_replyimage_grid_item, null);
		sendMsgView.sendmsg_ImageBrower
				.addView(postbar_topicreply_replyimage_grid_item);
		final ImageView postbar_topicreply_replyimage_grid_item_image = (ImageView) postbar_topicreply_replyimage_grid_item
				.findViewById(R.id.postbar_topicreply_replyimage_grid_item_image);
		final ImageView postbar_topicreply_replyimage_grid_item_del = (ImageView) postbar_topicreply_replyimage_grid_item
				.findViewById(R.id.postbar_topicreply_replyimage_grid_item_del);
		final ImageVo imageVo = new ImageVo();
		cache.dispBitmap(postbar_topicreply_replyimage_grid_item_image,
				imagePath, thumbnailPath, new ImageCallBack() {

					@Override
					public void doImageLoad(ImageView iv, Bitmap bitmap,
							Object... params) {
						if (iv != null && bitmap != null) {
							iv.setImageBitmap(bitmap);
							try {
								Bitmap bp = ImageUtil.createImageThumbnail(
										imagePath, -1,
										SystemConfig.BITMAP_MAX_RESOLUTION,
										true);
								final byte[] tphotoByte = ImageUtil
										.Bitmap2Bytes(
												bp,
												CompressFormat.JPEG,
												SystemConfig.BITMAP_COMPRESS_QUALITY);
								if (bp != null && !bp.isRecycled()) {
									// 回收并且置为null
									bp.recycle();
									bp = null;
								}
								imageVo.setPath(imagePath);
								imageVo.setData(tphotoByte);
								images.add(imageVo);
								sendMsgView.showSendPicCount(images.size());
								sendMsgView.sendmsg_ImageBrower
										.setVisibility(View.VISIBLE);
								sendMsgView.sendmsg_imageBrower_tipTxt
										.setVisibility(View.VISIBLE);
								if (images.size() == BitmapBucket.max) {
									sendMsgView.sendmsg_imageBrower_tipTxt
											.setText("已经上传" + BitmapBucket.max
													+ "张图片！");
									sendMsgView.sendmsg_imageBrower_tipTxt
											.setTextColor(getResources()
													.getColor(
															R.color.message_send_add_image_txt_color2));
								} else {
									sendMsgView.sendmsg_imageBrower_tipTxt
											.setText("已经上传"
													+ images.size()
													+ "张, 还能选择"
													+ (BitmapBucket.max - images
															.size()) + "张哦!");
									sendMsgView.sendmsg_imageBrower_tipTxt
											.setTextColor(getResources()
													.getColor(
															R.color.message_send_add_image_txt_color));
								}
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							} catch (OutOfMemoryError e) {
								e.printStackTrace();
							}
							postbar_topicreply_replyimage_grid_item_del
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											images.remove(imageVo);
											sendMsgView.sendmsg_ImageBrower
													.removeView(postbar_topicreply_replyimage_grid_item);
											sendMsgView.showSendPicCount(images
													.size());
											sendMsgView.sendmsg_imageBrower_tipTxt
													.setText("已经上传"
															+ images.size()
															+ "张, 还能选择"
															+ (BitmapBucket.max - images
																	.size())
															+ "张哦!");
											sendMsgView.sendmsg_imageBrower_tipTxt
													.setTextColor(getResources()
															.getColor(
																	R.color.message_send_add_image_txt_color));
											if (images.size() == 0) {
												sendMsgView.hideSendPicCount();
												sendMsgView.sendmsg_ImageBrower
														.setVisibility(View.GONE);
												sendMsgView.sendmsg_imageBrower_tipTxt
														.setVisibility(View.GONE);
												sendMsgView.sendmsg_imageBrower_tipTxt
														.setText("还没有上传图片哦");
											}
										}
									});
							postbar_topicreply_replyimage_grid_item_image
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											jumpImageBrowerAct(imagePath);
										}
									});
						} else {
							sendMsgView.sendmsg_ImageBrower
									.removeView(postbar_topicreply_replyimage_grid_item);
							Log.e(TAG, "bitmap是null");
						}

					}
				});
		// 隐藏panel
		// sendMsgView.hidePanelAndSoftInput();
		// sendMsgView.showSendPicCount(1);
	}

	/**
	 * 跳转到图片浏览页面
	 * 
	 * @param path
	 */
	private void jumpImageBrowerAct(String path) {
		if (images != null && images.size() > 0) {
			List<String> pathList = new ArrayList<String>();
			int index = 0;
			for (int i = 0; i < images.size(); i++) {
				String tpath = images.get(i).getPath();
				pathList.add(tpath);
				if (path.equals(tpath)) {
					index = i;
				}
			}
			Intent intent = new Intent(PlayEvaluationActivity.this,
					ImageBrowerActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX,
					index);
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, 1);
			bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES,
					pathList.toArray(new String[pathList.size()]));
			intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			PlayEvaluationActivity.this.startActivity(intent);
		}
	}

	/**
	 * 监听好中差评
	 * 
	 * */
	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			Log.i("playEvaluationActivity", checkedId + "");
			group.check(checkedId);
			switch (checkedId) {
			case R.id.play_evaluation_radio_button0:
				radio_evaluation = 1;
				break;
			case R.id.play_evaluation_radio_button1:
				radio_evaluation = 2;
				break;
			case R.id.play_evaluation_radio_button2:
				radio_evaluation = 3;
				break;

			}
		}

	};
	/**
	 * 监听评论的字数
	 * 
	 * */
	TextWatcher textWatcher = new TextWatcher() {
		private CharSequence temp;
		private int selectionStart;
		private int selectionEnd;

		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2,
				int arg3) {
			temp = s;
			selectionStart = arg1;
		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		}

		@Override
		public void afterTextChanged(Editable s) {

			selectionEnd = evaluation_comment.getSelectionEnd();
			// 敏感词检测
			if (ServiceFactory.getInstance().getWordsManager()
					.match(temp.toString())) {
				ToastUtil.showToast(PlayEvaluationActivity.this,
						"你输入的内容含有敏感词哦!");
				s.delete(selectionStart, selectionEnd);
				int tempSelection = selectionStart;
				evaluation_comment.setText(s);
				evaluation_comment.setSelection(tempSelection);

			}
			InputFilterUtil.lengthFilter(PlayEvaluationActivity.this,
					evaluation_comment, 100, "输入字数不能超过50字哦！");
		}

	};

	/**
	 * 
	 */
	private void getData() {
		orderId = getIntent().getLongExtra("orderid", 0);
		playId = getIntent().getLongExtra("playid", 0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// 先关闭表情框，再判断是否要弹出退出的提示
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (bottomView.getVisibility() == View.VISIBLE) {
				sendMsgView.hideSmileyView();
				bottomView.setVisibility(View.GONE);
				Utils.hideSoftInput2(PlayEvaluationActivity.this,
						PlayEvaluationActivity.this, evaluation_comment);
				return true;
			}
			cancelDialog();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#back()
	 */
	@Override
	protected void back() {
		// TODO Auto-generated method stub
		if (sendMsgView.hideSmileyView()
				|| bottomView.getVisibility() == View.VISIBLE) {
			bottomView.setVisibility(View.GONE);
			Utils.hideSoftInput2(PlayEvaluationActivity.this,
					PlayEvaluationActivity.this, evaluation_comment);
			return;
		}
		cancelDialog();

		// super.back();
	}

	protected void cancelDialog() {
		if (rating_evaluation == 0
				&& evaluation_comment.getText().toString().trim().equals("")
				&& radio_evaluation == 0) {
			System.gc();
			this.finish();
			return;
		}

		final TextView txt = new TextView(this);
		txt.setGravity(Gravity.CENTER);
		txt.setPadding(
				0,
				getResources().getDimensionPixelSize(
						R.dimen.global_page_paddingtop),
				getResources().getDimensionPixelSize(
						R.dimen.global_page_paddingright),
				getResources().getDimensionPixelSize(
						R.dimen.global_page_paddingbottom));

		txt.setTextColor(getResources().getColor(
				R.color.dialog_content_text_color));

		txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
				.getDimension(R.dimen.text_medium));
		txt.setText("您还未完成评价，确定放弃吗？");

		OKCallBackListener listener = new OKCallBackListener() {

			@Override
			public void execute() {
				// TODO Auto-generated method stub
				System.gc();
				PlayEvaluationActivity.this.finish();
			}

			@Override
			public void cannel() {
				// TODO Auto-generated method stub
			}

		};
		DialogUtil.showDialog(this, "提示", txt, listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */

	@Override
	public void onClick(View v) {
		if (v == evaluation_commit) {
			if (this.checkEvaluationContent()) {
				PlayEvalRequest playeval=getEval();
//				if(playeval.getImgsCount()>3){
//					ToastUtil.showToast(PlayEvaluationActivity.this, "最多只能上传3张图片哦！");
//					return;
//				}
				evaluation_commit.setEnabled(false);
				 EvaluationOrder(PlayEvaluationActivity.this, playeval);
			}

			// else if(v==evaluation_comment){
			//
			// bottomView.setVisibility(View.VISIBLE);
			//
			// }
		} else {
			if (v == eval_star1) {
				rating_evaluation = 1;
			} else if (v == eval_star2) {
				rating_evaluation = 2;
			} else if (v == eval_star3) {
				rating_evaluation = 3;
			} else if (v == eval_star4) {
				rating_evaluation = 4;
			} else if (v == eval_star5) {
				rating_evaluation = 5;
			}
			setStarBg(rating_evaluation);
		}

	}

	private void setStarBg(int sign) {
		for (int i = 0; i < starList.size(); i++) {
			if (i < sign) {
				starList.get(i).setBackgroundResource(
						R.drawable.peiwan_pingjia_detail);
			} else {
				starList.get(i).setBackgroundResource(
						R.drawable.peiwan_pingjia_detail2);
			}

		}

	}

	/**
	 * 判断数据
	 */
	private boolean checkEvaluationContent() {
		if (rating_evaluation == 0) {
			ToastUtil.showToast(PlayEvaluationActivity.this, "你还未给卖家评分哦！");
			return false;
		}
		if (radio_evaluation == 0) {
			ToastUtil.showToast(PlayEvaluationActivity.this, "你还未给卖家评价哦！");
			return false;
		}
		return true;
	}

	private PlayEvalRequest getEval() {
		PlayEvalRequest.Builder playeval = PlayEvalRequest.newBuilder();
		playeval.setOrderid(orderId);
		playeval.setStar(rating_evaluation);
		playeval.setDesc(radio_evaluation);
		playeval.setContent(evaluation_comment.getText().toString());
//	
//		List<ImageBytesDetail> imageDetails = new ArrayList<ImageBytesDetail>();
//		if (images != null && images.size() > 0) {
//			for (ImageVo image : images) {
//				ImageBytesDetail.Builder ibd = ImageBytesDetail.newBuilder();
//				ibd.setResourceIdBytes(ByteString.copyFrom(image.getData()));
//				imageDetails.add(ibd.build());
//				Log.i("playEval",image.getPath());
//			}
//			if (imageDetails.size() > 0) {
//				playeval.addAllImgs(imageDetails);
//			}
//			
//		}
//		Log.i("playEval", playeval.getOrderid() + ":" + playeval.getStar()
//				+ ":" + playeval.getDesc() + ":" + playeval.getContent() + ":"
//				+ playeval.getImgsCount() + ":"
//		);
		return playeval.build();

	}

	private void EvaluationOrder(Context context, PlayEvalRequest playeval) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(
				this, false, false);
		dialog.show();
		ProxyFactory.getInstance().getPlayProxy()
				.playEvalOrder((new ProxyCallBack<Integer>() {
					
					@Override
					public void onSuccess(Integer result) {
						if (result == 0) {
							dialog.dismiss();
							sendMsgView.sendmsg_ImageBrower.removeAllViews();
							sendMsgView.hideSendPicCount();
							sendMsgView.sendmsg_ImageBrower.setVisibility(View.GONE);
							sendMsgView.sendmsg_imageBrower_tipTxt
									.setVisibility(View.GONE);
							sendMsgView.sendmsg_imageBrower_tipTxt
									.setText("还没有上传图片哦");
							sendMsgView.sendmsg_imageBrower_tipTxt
									.setTextColor(getResources()
											.getColor(
													R.color.message_send_add_image_txt_color));
							ToastUtil.showToast(PlayEvaluationActivity.this,
									"评价成功");
							evaluation_commit.setEnabled(true);
							PlayEvaluationActivity.this.finish();
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						dialog.dismiss();
						switch (result) {
						case -100:
							resultMsg = "(缺少必要参数！)";
							break;
						case -1000:
							resultMsg = "（程序异常！）";
							break;
						case -1001:
							resultMsg = "（参数错误！）";
							break;
						case 500601:
							resultMsg = "（不可编辑！）";
							break;
						case 500604:
							resultMsg = "（游玩陪玩信息不存在！）";
						}
						evaluation_commit.setEnabled(true);
						ToastUtil.showToast(PlayEvaluationActivity.this, "评价失败"
								+ resultMsg);
					}

				}), context, playeval);
	}
}
