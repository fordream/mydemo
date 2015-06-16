/**      
 * PublishTopicActivity.java Create on 2013-12-26     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.google.protobuf.ByteString;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.LocationCallBack;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.SendMsgCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareDate;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.common.StrictModeWrapper;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.TopicTagDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.postbar.object.ImageVo;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetail;
import com.iwgame.msgs.proto.Msgs.ContentDetail.ContentType;
import com.iwgame.msgs.proto.Msgs.ContentList;
import com.iwgame.msgs.proto.Msgs.MessageContentType;
import com.iwgame.msgs.proto.Msgs.PostbarActionResult;
import com.iwgame.msgs.utils.DialogUtil;
import com.iwgame.msgs.utils.DialogUtil.OKCallBackListener;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.PhotoUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.ShareUtil;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.vo.local.RelationGameVo;
import com.iwgame.msgs.vo.local.ResourceVo;
import com.iwgame.msgs.vo.local.TopicTagVo;
import com.iwgame.msgs.widget.ResizeLayout;
import com.iwgame.msgs.widget.SendMsgView;
import com.iwgame.msgs.widget.SendMsgView.ActionShowImageVisibilityListener;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.AppUtils;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.ShareTaskUtil;
import com.iwgame.utils.StringUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.utils.imageselector.BitmapBucket;
import com.iwgame.utils.imageselector.BitmapCache;
import com.iwgame.utils.imageselector.BitmapCache.ImageCallBack;
import com.iwgame.utils.imageselector.ImageGridActivity;
import com.youban.msgs.R;

/**
 * @ClassName: PublishTopicActivity
 * @Description: 发布帖子
 * @author 王卫
 * @date 2015-3-10 下午9:24:56
 * @Version 1.0
 * 
 */
public class PublishTopicActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "PublishTopicActivity";
	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private static final int MSG_RESIZE = 1;

	private static final int HEIGHT_THREADHOLD = 30;

	private TextView topRightTextView;

	private EditText postbar_publish_topic_title;
	private EditText postbar_publish_topic_content;

	private long gid;

	private Handler handler = new Handler();

	/**
	 * 是否按了确认发布按钮
	 */
	private boolean isOnClickSendMsg = false;

	private LayoutInflater inflater;

	/**
	 * 发送消息的组件
	 */
	private SendMsgView sendMsgView;

	private LinearLayout rightView;

	/**
	 * 图片显示控件
	 */
	private ImageView postbar_topicreply_replyimage_grid_item_image;

	private LinearLayout tagsContent;

	private LinearLayout setTagContent;
	private TextView tagTxt;
	private Button tagBtn;

	private LinearLayout historyTags;
	private LinearLayout sysTags;
	private TextView historyTagTip;
	private TextView historyTag1;
	private TextView historyTag2;
	private TextView historyTag3;

	/**
	 * 选择的tag
	 */
	private TopicTagVo selectTag = null;

	// private int tagId;

	// private int tempTagId;

	// private boolean isTagSelected = false;

	private View otherTopicTagItem;

	/**
	 * 是否通过点击添加表情按钮控制过下方图片的显示
	 */
	private boolean isActionImageShow = false;

	private List<ImageVo> images;

	public static boolean goNext = false;

	private BitmapCache cache;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictModeWrapper.init(this);
		goNext = false;
		super.onCreate(savedInstanceState);
		images = new ArrayList<ImageVo>();
		this.inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Intent tmpintent = this.getIntent();
		if (tmpintent != null) {
			Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
			if (tmpbundle != null) {
				gid = tmpbundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID);
				// tagId =
				// tmpbundle.getInt(SystemConfig.BUNDLE_NAME_TOPIC_TAGID);
			}
		}
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	class InputHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESIZE: {
				if (msg.arg1 == BIGGER) { // 键盘隐藏
					if (images != null) {
						if (!isActionImageShow) {
							isActionImageShow = false;
						}
					}
				} else { // 键盘显示
					isActionImageShow = false;
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

	/**
	 * 初始化
	 */
	private void init() {
		// 设置显示top左边
		setLeftVisible(true);
		// 设置包显示top右边
		setRightVisible(true);

		ResizeLayout layout = (ResizeLayout) findViewById(R.id.root_layout);
		layout.setOnResizeListener(new ResizeLayout.OnResizeListener() {

			public void OnResize(int w, int h, int oldw, int oldh) {
				int change = BIGGER;
				if (h < oldh) {
					change = SMALLER;
				}

				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = change;
				mHandler.sendMessage(msg);
			}
		});

		topRightTextView = new TextView(this);
		topRightTextView.setText(R.string.postbar_publish_topic_publish_submit);
		topRightTextView.setTextColor(getResources().getColor(R.color.publish_topic_menu_submit_text_dis_color));
		topRightTextView.setBackgroundResource(R.drawable.postbar_publish_menu_dis_selector);// common_btn_ls_selector
		topRightTextView.setHeight(DisplayUtil.dip2px(this, 28));
		topRightTextView.setWidth(DisplayUtil.dip2px(this, 50));
		topRightTextView.setGravity(Gravity.CENTER);
		rightView = (LinearLayout) findViewById(R.id.rightView);
		rightView.addView(topRightTextView);
		rightView.setOnClickListener(this);
		rightView.setEnabled(false);

		// 设置头中间
		setTitleTxt(getResources().getString(R.string.postbar_publish_topic_activity_title));

		// 设置中间内容
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View view = View.inflate(this, R.layout.postbar_publish_topic, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(view, params);
		postbar_publish_topic_title = (EditText) view.findViewById(R.id.postbar_publish_topic_title);
		InputFilterUtil.lengthFilter(this, postbar_publish_topic_title, 40, "标题不能超过20个汉字或40个字符哦！");
		postbar_publish_topic_title.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendMsgView.hideSmileyView();
					sendMsgView.sendmsg_btem.setEnabled(false);
					sendMsgView.sendmsg_addattachments.setEnabled(false);
					sendMsgView.sendmsg_addattachments.setBackgroundResource(R.drawable.chat_msg_addattachments_pre2);
					sendMsgView.sendmsg_btem.setBackgroundResource(R.drawable.chat_msg_em_pre2);
				}
				return false;
			}
		});
		postbar_publish_topic_title.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int len = StringUtil.getCharacterNum(s + "");
				if (len >= 4 && selectTag != null) {
					topRightTextView.setTextColor(getResources().getColor(R.color.publish_topic_menu_submit_text_color));
					topRightTextView.setBackgroundResource(R.drawable.postbar_publish_menu_selector);
					rightView.setEnabled(true);
				} else {
					topRightTextView.setTextColor(getResources().getColor(R.color.publish_topic_menu_submit_text_dis_color));
					topRightTextView.setBackgroundResource(R.drawable.postbar_publish_menu_dis_selector);
					rightView.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});

		postbar_publish_topic_content = (EditText) view.findViewById(R.id.postbar_publish_topic_content);
		postbar_publish_topic_content.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendMsgView.hideSmileyView();
					sendMsgView.sendmsg_btem.setEnabled(true);
					sendMsgView.sendmsg_addattachments.setEnabled(true);
					sendMsgView.sendmsg_addattachments.setBackgroundResource(R.drawable.chat_msg_addattachments_selector);
					sendMsgView.sendmsg_btem.setBackgroundResource(R.drawable.chat_msg_em_selector);
				}
				return false;
			}
		});
		tagsContent = (LinearLayout) view.findViewById(R.id.tagsContent);
		setTagContent = (LinearLayout) view.findViewById(R.id.setTagContent);
		tagTxt = (TextView) view.findViewById(R.id.tagTxt);
		tagTxt.setOnClickListener(this);
		tagBtn = (Button) view.findViewById(R.id.tagBtn);
		tagBtn.setOnClickListener(this);
		historyTags = (LinearLayout) view.findViewById(R.id.historyTags);
		historyTagTip = (TextView) view.findViewById(R.id.historyTagTip);
		historyTag1 = (TextView) view.findViewById(R.id.historyTag1);
		historyTag2 = (TextView) view.findViewById(R.id.historyTag2);
		historyTag3 = (TextView) view.findViewById(R.id.historyTag3);
		historyTagTip.setOnClickListener(this);
		historyTag1.setOnClickListener(this);
		historyTag2.setOnClickListener(this);
		historyTag3.setOnClickListener(this);
		tagBtn.setEnabled(false);
		tagTxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 0) {
					tagBtn.setBackgroundResource(R.drawable.common_btn_ls_selector);
					tagBtn.setTextColor(getResources().getColor(R.color.common_btn_text_color));
					tagBtn.setEnabled(true);
				} else {
					tagBtn.setBackgroundResource(R.drawable.publish_topic_tag_edittxt_bg);
					tagBtn.setTextColor(getResources().getColor(R.color.publish_topic_hint_text_color));
					tagBtn.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		tagTxt.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendMsgView.hideSmileyView();
					sendMsgView.sendmsg_btem.setEnabled(false);
					sendMsgView.sendmsg_addattachments.setEnabled(false);
					sendMsgView.sendmsg_addattachments.setBackgroundResource(R.drawable.chat_msg_addattachments_pre2);
					sendMsgView.sendmsg_btem.setBackgroundResource(R.drawable.chat_msg_em_pre2);
				}
				return false;
			}
		});

		// 设置底部发布框
		LinearLayout bottomView = (LinearLayout) findViewById(R.id.bottomView);
		sendMsgView = (SendMsgView) View.inflate(this, R.layout.public_send_msg_view, null);
		sendMsgView.sendmsg_addattachments.setEnabled(false);
		sendMsgView.sendmsg_btem.setEnabled(false);
		sendMsgView.sendmsg_addattachments.setBackgroundResource(R.drawable.chat_msg_addattachments_pre2);
		sendMsgView.sendmsg_btem.setBackgroundResource(R.drawable.chat_msg_em_pre2);
		bottomView.addView(sendMsgView);
		// 设置发声音按钮不显示
		// sendMsgView.setAddattachmentsButtonVisibility(View.GONE);
		sendMsgView.setSendAudioButtonVisibility(View.GONE);
		// 设置发送button不显示
		sendMsgView.setSendButtonVisibility(View.GONE);
		// 设置分享同步显示
		sendMsgView.setSyncContentViewVisibility(View.VISIBLE);
		// 设置外部的编辑框到控件中
		sendMsgView.setEditTextView(postbar_publish_topic_content);

		SendMsgCallBack sendMsgCallBack = new SendMsgCallBack() {

			@Override
			public void send(int msgtype, final String content, byte[] contentBytes, int action) {
				if (msgtype == MessageContentType.TEXT_VALUE) {
				} else if (msgtype == MessageContentType.IMAGE_VALUE && action == SendMsgCallBack.ACTION_PHOTO) {
					// 相机
					if (images != null && images.size() >= BitmapBucket.max) {
						ToastUtil.showToast(PublishTopicActivity.this, "已上传" + BitmapBucket.max + "张相片，不能继续上传了哦！");
					} else {
						photoName = "msgs_" + System.currentTimeMillis() + "_uban.jpg";
						photoPath = PhotoUtil.sdcardFileRootPath + photoName;
						PhotoUtil.doTakePhoto(PublishTopicActivity.this, photoPath);
					}
				} else if (msgtype == MessageContentType.IMAGE_VALUE && action == SendMsgCallBack.ACTION_PICTURE) {
					Intent intent = new Intent(PublishTopicActivity.this, ImageGridActivity.class);
					intent.putExtra(ImageGridActivity.IMAGE_MAP, (Serializable) images);
					PublishTopicActivity.this.startActivityForResult(intent, ImageGridActivity.REQUESTCODE);
				} else if (msgtype == MessageContentType.VOICE_VALUE && action == SendMsgCallBack.ACTION_MICROPHONE) {
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

		sendMsgView.setActionShowImageVisibilityListener(new ActionShowImageVisibilityListener() {
			@Override
			public void showImageVisibility(int btnType, boolean isShow) {
			}
		});

		// 获取标签
		getTags();
		// 获取相册
		getUserAlbum();
		// 判断微信和qq是否安装
		setSyncView(sendMsgView.syncmchat, AppUtils.isInstallAppByPackageName("com.tencent.mm"), R.drawable.post_share_weixin_pre,
				R.drawable.post_share_weixin_nor, "还没有安装微信，安装后才能同步分享到微信朋友圈哦！", false);
		setSyncView(sendMsgView.syncQQ, AppUtils.isInstallAppByPackageName("com.tencent.mobileqq"), R.drawable.post_share_qzone_pre,
				R.drawable.post_share_qzone_nor, "还没有安装QQ，安装后才能同步分享到QQ哦！", false);
	}

	/**
	 * 
	 * @param view
	 * @param issync
	 * @param pre
	 * @param nor
	 * @param tip
	 */
	private void setSyncView(ImageView view, boolean issync, int pre, int nor, final String tip, boolean moo) {
		if (issync) {
			view.setTag(Boolean.valueOf(moo));
			if (moo)
				view.setBackgroundResource(pre);
			else
				view.setBackgroundResource(nor);
		} else {
			view.setTag(Boolean.valueOf(false));
			view.setBackgroundResource(nor);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ToastUtil.showToast(PublishTopicActivity.this, tip);
				}
			});
		}
	}

	private void getUserAlbum() {
		ProxyFactory.getInstance().getUserProxy().getUserAlbum(new ProxyCallBack<List<ResourceVo>>() {

			@Override
			public void onSuccess(List<ResourceVo> result) {
				setSyncView(sendMsgView.syncContract, result == null || (result != null && result.size() < 10), R.drawable.post_share_album_pre,
						R.drawable.post_share_album_nor, "相册已满，不能同步到相册了哦！", true);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				setSyncView(sendMsgView.syncContract, true, R.drawable.post_share_album_pre, R.drawable.post_share_album_nor, "相册已满，不能同步到相册了哦！", true);
			}

		}, this, SystemContext.getInstance().getExtUserVo().getUserid(), 0);
	}

	/**
	 * 获取标签内容
	 */
	private void getTags() {
		final TopicTagVo tagVo = new TopicTagVo();
		tagVo.setAccess(1);
		tagVo.setId(7);
		tagVo.setName("自定义");
		tagVo.setTagDefault(0);
		ProxyFactory.getInstance().getPostbarProxy().getTopicTags(new ProxyCallBack<List<TopicTagVo>>() {

			@Override
			public void onSuccess(List<TopicTagVo> tags) {
				// 增加标签
				if (tags != null) {
					int tagsSize = tags.size();
					for (int i = 0; i < tagsSize; i++) {
						TopicTagVo tagVo = tags.get(i);
						addTagView(tagVo);
					}
				}
				otherTopicTagItem = addTagView(tagVo);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				otherTopicTagItem = addTagView(tagVo);
			}
		}, gid);
	}

	private View addTagView(TopicTagVo tagVo) {
		if (tagVo.getAccess() == 0) {
			return null;
		}
		View item = PublishTopicActivity.this.inflater.inflate(R.layout.postbar_topic_tag_item, null);
		TextView postbar_topic_tag_name = (TextView) item.findViewById(R.id.postbar_topic_tag_name);
		postbar_topic_tag_name.setText(tagVo.getName());
		postbar_topic_tag_name.setTag(tagVo);
		if (tagVo.getTagDefault() == 1) {
			selectTag = tagVo;
			postbar_topic_tag_name.setBackgroundResource(R.drawable.publish_topic_tag_bg_pre_shap);
			postbar_topic_tag_name.setTextColor(getResources().getColor(R.color.publish_topic_tag_txt_pre_color));
		}
		addTagItemEvent(item);
		tagsContent.addView(item);
		return item;
	}

	/**
	 * 
	 * @param item
	 * @param tagId
	 */
	private void addTagItemEvent(final View item) {
		item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView old = (TextView) tagsContent.findViewWithTag(selectTag);
				if (old != null) {
					old.setBackgroundResource(R.drawable.publish_topic_tag_bg_shap);
					old.setTextColor(getResources().getColor(R.color.publish_topic_tag_txt_color));
				}
				TextView postbar_topic_tag_name = (TextView) v.findViewById(R.id.postbar_topic_tag_name);
				selectTag = (TopicTagVo) postbar_topic_tag_name.getTag();
				postbar_topic_tag_name.setBackgroundResource(R.drawable.publish_topic_tag_bg_pre_shap);
				postbar_topic_tag_name.setTextColor(getResources().getColor(R.color.publish_topic_tag_txt_pre_color));
				boolean hasTag = true;
				if (selectTag.getId() == 7) {
					if ("自定义".equals(selectTag.getName()))
						hasTag = false;
					setTagContent.setVisibility(View.VISIBLE);
					showHistroyTags();
				} else {
					setTagContent.setVisibility(View.GONE);
					historyTags.setVisibility(View.GONE);
				}
				if (hasTag && !postbar_publish_topic_title.getText().toString().trim().equals("")) {
					topRightTextView.setTextColor(getResources().getColor(R.color.publish_topic_menu_submit_text_color));
					topRightTextView.setBackgroundResource(R.drawable.postbar_publish_menu_selector);
					rightView.setEnabled(true);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.rightView) {
			if (FastClickLimitUtil.isFastClick())
				return;
			publishTopic();
		} else if (v.getId() == R.id.tagBtn) {
			String tname = tagTxt.getText().toString();
			if (tname != null && !"".equals(tname)) {
				if (Character.isWhitespace(tname.charAt(0)) || Character.isWhitespace(tname.charAt(tname.length() - 1))) {
					ToastUtil.showToast(this, getString(R.string.ec_postbar_publishtopic_tag_startorend_isnull));
					return;
				}
				if (StringUtil.getCharacterNum(tname) <= 18) {
					if (StringUtil.getCharacterNum(tname) >= 4) {
						if (ServiceFactory.getInstance().getWordsManager().matchName(tname)) {
							ToastUtil.showToast(PublishTopicActivity.this,
									PublishTopicActivity.this.getResources().getString(R.string.global_words_error));
							return;
						}
						// 设置选中标签
						TextView postbar_topic_tag_name = (TextView) otherTopicTagItem.findViewById(R.id.postbar_topic_tag_name);
						postbar_topic_tag_name.setText(tname);
						selectTag = (TopicTagVo) postbar_topic_tag_name.getTag();
						selectTag.setName(tname);
						setTagContent.setVisibility(View.GONE);
						historyTags.setVisibility(View.GONE);
						tagTxt.setText("");
					} else {
						ToastUtil.showToast(this, getString(R.string.ec_postbar_publishtopic_title_tag_min));
					}
				} else {
					ToastUtil.showToast(this, getString(R.string.ec_postbar_publishtopic_title_tag));
				}
			} else {
				ToastUtil.showToast(this, getString(R.string.ec_postbar_publishtopic_title_tag_min));
			}
		} else if (v.getId() == R.id.tagTxt) {
			showHistroyTags();
		} else if (v == historyTagTip) {
			historyTags.setVisibility(View.GONE);
		} else if (v == historyTag1) {
			tagTxt.setText(historyTag1.getText().toString());
			historyTags.setVisibility(View.GONE);
		} else if (v == historyTag2) {
			tagTxt.setText(historyTag2.getText().toString());
			historyTags.setVisibility(View.GONE);
		} else if (v == historyTag3) {
			tagTxt.setText(historyTag3.getText().toString());
			historyTags.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示历史标签
	 */
	private void showHistroyTags() {
		TopicTagDao topicTagDao = DaoFactory.getDaoFactory().getTopicTag(this);
		List<TopicTagVo> tags = topicTagDao.getTopicTags(BitmapBucket.max);
		if (tags != null && tags.size() > 0) {
			historyTags.setVisibility(View.VISIBLE);
			int size = tags.size();
			for (int i = 0; i < size; i++) {
				TopicTagVo vo = tags.get(i);
				if (i == 0) {
					historyTag1.setVisibility(View.VISIBLE);
					historyTag1.setText(vo.getName());
				} else if (i == 1) {
					historyTag2.setVisibility(View.VISIBLE);
					historyTag2.setText(vo.getName());
				} else if (i == 2) {
					historyTag3.setVisibility(View.VISIBLE);
					historyTag3.setText(vo.getName());
				}
			}
		} else {
			historyTags.setVisibility(View.GONE);
			historyTag1.setVisibility(View.GONE);
			historyTag2.setVisibility(View.GONE);
			historyTag3.setVisibility(View.GONE);
		}
	}

	protected void cancelDialog() {
		if ((postbar_publish_topic_title.getText().toString() == null || postbar_publish_topic_title.getText().toString().trim().equals(""))
				&& (postbar_publish_topic_content.getText().toString() == null || postbar_publish_topic_content.getText().toString().trim()
						.equals("")) && images == null) {
			System.gc();
			this.finish();
			return;
		}

		final TextView txt = new TextView(this);
		txt.setGravity(Gravity.CENTER);
		txt.setPadding(0, getResources().getDimensionPixelSize(R.dimen.global_page_paddingtop),
				getResources().getDimensionPixelSize(R.dimen.global_page_paddingright),
				getResources().getDimensionPixelSize(R.dimen.global_page_paddingbottom));

		txt.setTextColor(getResources().getColor(R.color.dialog_content_text_color));

		txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_medium));
		txt.setText("尚有未发送的内容，确定要返回吗？");

		OKCallBackListener listener = new OKCallBackListener() {

			@Override
			public void execute() {
				// TODO Auto-generated method stub
				System.gc();
				PublishTopicActivity.this.finish();
			}

			@Override
			public void cannel() {
				// TODO Auto-generated method stub
			}

		};
		DialogUtil.showDialog(this, "提示", txt, listener);

	}

	/**
	 * 发布帖子
	 */
	private void publishTopic() {
		rightView.setEnabled(false);
		String title = postbar_publish_topic_title.getText().toString();
		if (title == null || title.trim().equals("")) {
			ToastUtil.showToast(this, getString(R.string.ec_postbar_publishtopic_title_nonull));
			return;
		}
		int len = StringUtil.getCharacterNum(title);
		if (len < 4 || len > 40) {
			ToastUtil.showToast(this, getString(R.string.ec_postbar_publishtopic_title_max));
			return;
		}
		if (selectTag == null) {
			ToastUtil.showToast(this, "你尚未选择标签！");
			return;
		}
		// 判断是否关注过该贴吧
		ProxyCallBack<RelationGameVo> callback = new ProxyCallBack<RelationGameVo>() {
			@Override
			public void onSuccess(RelationGameVo result) {
				if (result != null && result.getRelation() == 1) {
					// 关注过该贴吧
					publishTopicAction();
				} else {
					long gameid = AdaptiveAppContext.getInstance().getAppConfig().getGameId();
					if (gameid != 0 && gameid == gid) {// 自动关注攻略贴吧
						ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

							@Override
							public void onSuccess(Integer result) {
								publishTopicAction();
							}

							@Override
							public void onFailure(Integer result, String resultMsg) {
								rightView.setEnabled(true);
							}
						}, PublishTopicActivity.this, gameid, MsgsConstants.OT_GAME, MsgsConstants.OP_FOLLOW, null, null, SystemContext.APPTYPE);
					} else {
						rightView.setEnabled(true);
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ErrorCodeUtil.handleErrorCode(PublishTopicActivity.this, result, resultMsg);
				rightView.setEnabled(true);
			}
		};
		ProxyFactory.getInstance().getGameProxy().getRelGameInfoForLocal(callback, PublishTopicActivity.this, gid);

	}

	/**
	 * 发布帖子
	 */
	private void publishTopicAction() {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(PublishTopicActivity.this);
		dialog.show();
		ServiceFactory.getInstance().getBaiduLocationService().requestLocation(new LocationCallBack() {

			@Override
			public void onBack(BDLocation bdLocation) {
				if (!isOnClickSendMsg) {
					isOnClickSendMsg = true;
					ContentList.Builder cb = ContentList.newBuilder();
					cb.setTitle(postbar_publish_topic_title.getText().toString().trim());
					cb.setTagId(selectTag.getId());
					cb.setTagName(selectTag.getName());
					ContentDetail.Builder cd = ContentDetail.newBuilder();
					cd.setType(ContentType.TEXT);
					cd.setText(postbar_publish_topic_content.getText().toString().trim());
					cd.setSeq(1);
					cb.addContentDetail(cd);
					if (images != null) {
						for (int j = 0; j < images.size(); j++) {
							byte[] imageData = images.get(j).getData();
							if (imageData != null && imageData.length > 0) {
								cd = ContentDetail.newBuilder();
								cd.setType(ContentType.IMAGE);
								cd.setResourceId(ByteString.copyFrom(imageData));
								cd.setSeq(j + 1);
								cb.addContentDetail(cd);
							}
						}
					}
					ProxyFactory.getInstance().getPostbarProxy().publicTopic(
							new ProxyCallBack<PostbarActionResult>() {

								@Override
								public void onSuccess(PostbarActionResult result) {
									rightView.setEnabled(true);
									dialog.dismiss();
									isOnClickSendMsg = false;
									if (result != null) {
										sendMsgView.sendmsg_ImageBrower.removeAllViews();
										sendMsgView.hideSendPicCount();
										sendMsgView.sendmsg_ImageBrower.setVisibility(View.GONE);
										sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.GONE);
										sendMsgView.sendmsg_imageBrower_tipTxt.setText("还没有上传图片哦");
										sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color));
										ToastUtil.showToast(PublishTopicActivity.this, "发送成功");
										shareTopic(result.getResultId(), postbar_publish_topic_title.getText().toString().trim(), "",
												postbar_publish_topic_content.getText().toString().trim());
										if (selectTag.getId() == 7) {
											selectTag.setUtime(System.currentTimeMillis());
											TopicTagDao topicTagDao = DaoFactory.getDaoFactory().getTopicTag(PublishTopicActivity.this);
											topicTagDao.insertOrUpdate(selectTag);
										}
										postbar_publish_topic_title.setText("");
										postbar_publish_topic_content.setText("");
										images.clear();
										images = null;

										// 数据使用Intent返回
										Intent intent = new Intent();
										// 把返回数据存入Intent
										intent.putExtra("result", "ok");
										// 设置返回数据
										PublishTopicActivity.this.setResult(RESULT_OK, intent);
										System.gc();
										PublishTopicActivity.this.finish();
										SystemContext.getInstance().setLastTopicPublishTime(System.currentTimeMillis());
										handler.postDelayed(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												SystemContext.getInstance().setLastTopicPublishTime(0);
											}
										}, SystemContext.getInstance().getPIT() * 1000);
									} else {
										ToastUtil.showToast(PublishTopicActivity.this, "发帖失败");
									}
								}

								@Override
								public void onFailure(Integer result, String resultMsg) {
									rightView.setEnabled(true);
									isOnClickSendMsg = false;
									dialog.dismiss();
									if (result == Msgs.ErrorCode.EC_MSGS_OVER_COUNT_VALUE)
										ErrorCodeUtil.handleErrorCode(PublishTopicActivity.this, ErrorCode.EC_CLIENT_PUBLISHTOPIC_OVER_COUNT, null,
												SystemContext.getInstance().getPTM());
									else
										ErrorCodeUtil.handleErrorCode(PublishTopicActivity.this, result, resultMsg);
								}
							}, PublishTopicActivity.this, 0l, gid, MsgsConstants.OP_POST_TOPIC, MsgsConstants.OT_TOPIC,
							(images != null && images.size() > 0) ? 1 : 0, cb.build(), SystemContext.getInstance().getLocation(),
							(Boolean) sendMsgView.syncContract.getTag() ? 1 : 0);
				} else {
					ToastUtil.showToast(PublishTopicActivity.this, getString(R.string.ec_postbar_publishtopic_no_tag));
				}
			}
		});
	}

	/**
	 * 
	 * @param topicId
	 * @param topicTitle
	 * @param gameName
	 */
	public void shareTopic(final long topicId, String topicTitle, String gameName, String content) {
		if (FastClickLimitUtil.isFastClick())
			return;

		ShareDate shareDate = new ShareDate();
		shareDate.setShareType(ShareUtil.SHARE_TYPE_SHAER);// 类型为分享
		shareDate.setTargetType(ShareUtil.TYPE_POST);// 类型为帖子
		shareDate.setInTargetType(SystemConfig.CONTENT_TYPE_TOPIC);// 内部分享类型
		shareDate.setTargetId(topicId);// 帖子id(目标ID)
		shareDate.setTargetName(topicTitle);// 帖子名称（目标名称）
		shareDate.setTempString(gameName);// 备用字段，传入贴吧名称（暂仅分享帖子时使用）
		shareDate.setText(content);
		shareDate.setImageUrl(ResUtil.getSmallRelUrl("i_youban"));
		shareDate.setImagePath("i_youban");
		shareQQ(shareDate, topicId);
		shareSinaWeibo(shareDate, topicId);
		shareTencentWeibo(shareDate, topicId);
	}

	/**
	 * 
	 * @param shareDate
	 */
	private void shareChatZone(ShareDate shareDate, final long topicId) {
		if ((Boolean) sendMsgView.syncmchat.getTag()) {
			ShareCallbackListener listener = new ShareCallbackListener() {

				@Override
				public void doSuccess(String plamType) {
					// 分享帖子信息
					ShareTaskUtil.makeShareTask(SystemContext.getInstance().getContext(), TAG, topicId, MsgsConstants.OT_TOPIC,
							MsgsConstants.OP_RECORD_SHARE, plamType, null, this.shortUrl);
				}

				@Override
				public void doFail() {

				}

			};
			ShareManager.getInstance().shareContent(SystemContext.getInstance().getContext(), shareDate, ShareUtil.MARKET_PENGYOUQUAN,
					shareDate.getTargetType(), ShareUtil.TYPE_TARGET_PENGYOUQUAN, listener);
			UMUtil.sendEvent(SystemContext.getInstance().getContext(), UMConfig.MSGS_EVENT_SHARE_WEIXIN_PY, null, null,
					String.valueOf(shareDate.getTargetId()), shareDate.getTargetName(), true);
		}
	}

	/**
	 * 
	 * @param shareDate
	 */
	private void shareQQ(final ShareDate shareDate, final long topicId) {
		if ((Boolean) sendMsgView.syncQQ.getTag()) {
			ShareCallbackListener listener = new ShareCallbackListener() {

				@Override
				public void doSuccess(String plamType) {
					// 分享帖子信息
					ShareTaskUtil.makeShareTask(SystemContext.getInstance().getContext(), TAG, topicId, MsgsConstants.OT_TOPIC,
							MsgsConstants.OP_RECORD_SHARE, plamType, null, this.shortUrl);
					shareChatZone(shareDate, topicId);
				}

				@Override
				public void doFail() {

				}

			};
			ShareManager.getInstance().shareContent(SystemContext.getInstance().getContext(), shareDate, ShareUtil.MARKET_QZONE,
					shareDate.getTargetType(), ShareUtil.TYPE_TARGET_QZONE, listener);
			UMUtil.sendEvent(SystemContext.getInstance().getContext(), UMConfig.MSGS_EVENT_SHARE_TENCENT_ZONE, null, null,
					String.valueOf(shareDate.getTargetId()), shareDate.getTargetName(), true);
		} else {
			shareChatZone(shareDate, topicId);
		}
	}

	/**
	 * 
	 * @param shareDate
	 */
	private void shareSinaWeibo(ShareDate shareDate, final long topicId) {
		if ((Boolean) sendMsgView.syncSina.getTag()) {
			ShareCallbackListener listener = new ShareCallbackListener() {

				@Override
				public void doSuccess(String plamType) {
					// 分享帖子信息
					ShareTaskUtil.makeShareTask(SystemContext.getInstance().getContext(), TAG, topicId, MsgsConstants.OT_TOPIC,
							MsgsConstants.OP_RECORD_SHARE, plamType, null, this.shortUrl);
				}

				@Override
				public void doFail() {
				}

			};
			ShareManager.getInstance().shareContent(SystemContext.getInstance().getContext(), shareDate, ShareUtil.MARKET_SINAWEIBO,
					shareDate.getTargetType(), ShareUtil.TYPE_TARGET_SINAWEIBO, listener);
			UMUtil.sendEvent(SystemContext.getInstance().getContext(), UMConfig.MSGS_EVENT_SHARE_WEIBO, null, null,
					String.valueOf(shareDate.getTargetId()), shareDate.getTargetName(), true);
		}
	}

	/**
	 * 
	 * @param shareDate
	 */
	private void shareTencentWeibo(ShareDate shareDate, final long topicId) {
		if ((Boolean) sendMsgView.syncWeibo.getTag()) {
			ShareCallbackListener listener = new ShareCallbackListener() {

				@Override
				public void doSuccess(String plamType) {
					// 分享帖子信息
					ShareTaskUtil.makeShareTask(SystemContext.getInstance().getContext(), TAG, topicId, MsgsConstants.OT_TOPIC,
							MsgsConstants.OP_RECORD_SHARE, plamType, null, this.shortUrl);
				}

				@Override
				public void doFail() {
				}

			};
			ShareManager.getInstance().shareContent(SystemContext.getInstance().getContext(), shareDate, ShareUtil.MARKET_TENCENTWEIBO,
					shareDate.getTargetType(), ShareUtil.TYPE_TARGET_TENCENTWEIBO, listener);
			UMUtil.sendEvent(SystemContext.getInstance().getContext(), UMConfig.MSGS_EVENT_SHARE_TENCENT_WEIBO, null, null,
					String.valueOf(shareDate.getTargetId()), shareDate.getTargetName(), true);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// 先关闭表情框，再判断是否要弹出退出的提示
		if (keyCode == KeyEvent.KEYCODE_BACK) {
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
		cancelDialog();

		// super.back();
	}

	private String photoPath;
	private String photoName;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			byte[] photoByte = null;
			switch (requestCode) {
			case PhotoUtil.CAMERA_WITH_DATA:
				// 先缩放
				try {
					Bitmap bitmap = ImageUtil.createImageThumbnail(photoPath, -1, SystemConfig.BITMAP_MAX_RESOLUTION, true);
					photoByte = ImageUtil.Bitmap2Bytes(bitmap, CompressFormat.JPEG, SystemConfig.BITMAP_COMPRESS_QUALITY);
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
					Bitmap photo = ImageUtil.createImageThumbnail(resolver, originalUri, -1, SystemConfig.BITMAP_MAX_RESOLUTION, true);
					if (photo != null) {
						photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, SystemConfig.BITMAP_COMPRESS_QUALITY);
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
				ToastUtil.showToast(this, getResources().getString(R.string.common_add_photo_error));
			}
		} else if (resultCode == ImageGridActivity.RESULTCODE) {
			cache = BitmapCache.getInstance();
			sendMsgView.sendmsg_ImageBrower.removeAllViews();
			images.clear();
			sendMsgView.hideSendPicCount();
			sendMsgView.sendmsg_ImageBrower.setVisibility(View.GONE);
			sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.GONE);
			sendMsgView.sendmsg_imageBrower_tipTxt.setText("还没有上传图片哦");
			sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color));
			List<String> resImageMap = (List<String>) data.getExtras().get(ImageGridActivity.IMAGE_MAP);
			int size = resImageMap.size();
			for (int i = 0; i < size; i++) {
				String path = resImageMap.get(i);
				addCacheImageView(path, path);
			}
		}
	}

	/**
	 * 
	 * @param btm
	 */
	private void addCacheImageView(final String imagePath, final String thumbnailPath) {
		// 增加底部显示发送的图片
		final LinearLayout postbar_topicreply_replyimage_grid_item = (LinearLayout) View.inflate(this,
				R.layout.postbar_topicreply_replyimage_grid_item, null);
		sendMsgView.sendmsg_ImageBrower.addView(postbar_topicreply_replyimage_grid_item);
		final ImageView postbar_topicreply_replyimage_grid_item_image = (ImageView) postbar_topicreply_replyimage_grid_item
				.findViewById(R.id.postbar_topicreply_replyimage_grid_item_image);
		final ImageView postbar_topicreply_replyimage_grid_item_del = (ImageView) postbar_topicreply_replyimage_grid_item
				.findViewById(R.id.postbar_topicreply_replyimage_grid_item_del);
		final ImageVo imageVo = new ImageVo();
		cache.dispBitmap(postbar_topicreply_replyimage_grid_item_image, imagePath, thumbnailPath, new ImageCallBack() {

			@Override
			public void doImageLoad(ImageView iv, Bitmap bitmap, Object... params) {
				if (iv != null && bitmap != null) {
					iv.setImageBitmap(bitmap);
					try {
						Bitmap bp = ImageUtil.createImageThumbnail(imagePath, -1, SystemConfig.BITMAP_MAX_RESOLUTION, true);
						final byte[] tphotoByte = ImageUtil.Bitmap2Bytes(bp, CompressFormat.JPEG, SystemConfig.BITMAP_COMPRESS_QUALITY);
						if (bp != null && !bp.isRecycled()) {
							// 回收并且置为null
							bp.recycle();
							bp = null;
						}
						imageVo.setPath(imagePath);
						imageVo.setData(tphotoByte);
						images.add(imageVo);
						sendMsgView.showSendPicCount(images.size());
						sendMsgView.sendmsg_ImageBrower.setVisibility(View.VISIBLE);
						sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.VISIBLE);
						if (images.size() == BitmapBucket.max) {
							sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + BitmapBucket.max + "张图片！");
							sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color2));
						} else {
							sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + images.size() + "张, 还能选择" + (BitmapBucket.max - images.size())
									+ "张哦!");
							sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color));
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
					postbar_topicreply_replyimage_grid_item_del.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							images.remove(imageVo);
							sendMsgView.sendmsg_ImageBrower.removeView(postbar_topicreply_replyimage_grid_item);
							sendMsgView.showSendPicCount(images.size());
							sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + images.size() + "张, 还能选择" + (BitmapBucket.max - images.size())
									+ "张哦!");
							sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color));
							if (images.size() == 0) {
								sendMsgView.hideSendPicCount();
								sendMsgView.sendmsg_ImageBrower.setVisibility(View.GONE);
								sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.GONE);
								sendMsgView.sendmsg_imageBrower_tipTxt.setText("还没有上传图片哦");
							}
						}
					});
					postbar_topicreply_replyimage_grid_item_image.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							jumpImageBrowerAct(imagePath);
						}
					});
				} else {
					sendMsgView.sendmsg_ImageBrower.removeView(postbar_topicreply_replyimage_grid_item);
					Log.e(TAG, "bitmap是null");
				}

			}
		});
		// 隐藏panel
		// sendMsgView.hidePanelAndSoftInput();
		// sendMsgView.showSendPicCount(1);
	}

	/**
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
			Intent intent = new Intent(PublishTopicActivity.this, ImageBrowerActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, index);
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, 1);
			bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, pathList.toArray(new String[pathList.size()]));
			intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			PublishTopicActivity.this.startActivity(intent);
		}
	}

	/**
	 * 
	 * @param btm
	 */
	private void addImageView(final byte[] photoByte, final String path) {
		if (images == null) {
			return;
		} else if (images.size() == 0) {
			sendMsgView.sendmsg_ImageBrower.removeAllViews();
		}
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
		final ImageVo imageVo = new ImageVo(path, photoByte);
		images.add(imageVo);
		Bitmap btm = ImageUtil.Bytes2Bimap(photoByte);
		sendMsgView.showSendPicCount(images.size());
		// 增加底部显示发送的图片
		final LinearLayout postbar_topicreply_replyimage_grid_item = (LinearLayout) View.inflate(this,
				R.layout.postbar_topicreply_replyimage_grid_item, null);
		sendMsgView.sendmsg_ImageBrower.setVisibility(View.VISIBLE);
		sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.VISIBLE);
		if (images.size() == BitmapBucket.max) {
			sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + BitmapBucket.max + "张图片！");
			sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color2));
		} else {
			sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + images.size() + "张, 还能选择" + (BitmapBucket.max - images.size()) + "张哦!");
			sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color));
		}
		sendMsgView.sendmsg_ImageBrower.addView(postbar_topicreply_replyimage_grid_item);
		final ImageView postbar_topicreply_replyimage_grid_item_image = (ImageView) postbar_topicreply_replyimage_grid_item
				.findViewById(R.id.postbar_topicreply_replyimage_grid_item_image);
		postbar_topicreply_replyimage_grid_item_image.setImageBitmap(btm);
		postbar_topicreply_replyimage_grid_item_image.setTag(new String(path));
		ImageView postbar_topicreply_replyimage_grid_item_del = (ImageView) postbar_topicreply_replyimage_grid_item
				.findViewById(R.id.postbar_topicreply_replyimage_grid_item_del);
		postbar_topicreply_replyimage_grid_item_del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				images.remove(imageVo);
				sendMsgView.sendmsg_ImageBrower.removeView(postbar_topicreply_replyimage_grid_item);
				sendMsgView.showSendPicCount(images.size());
				if (images.size() == BitmapBucket.max) {
					sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + BitmapBucket.max + "张图片！");
					sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color2));
				} else {
					sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + images.size() + "张, 还能选择" + (BitmapBucket.max - images.size()) + "张哦!");
					sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color));
				}
				if (images.size() == 0)
					sendMsgView.hideSendPicCount();
			}
		});
		postbar_topicreply_replyimage_grid_item_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jumpImageBrowerAct(String.valueOf(postbar_topicreply_replyimage_grid_item_image.getTag()));
			}
		});
		sendMsgView.sendmsg_ImageBrower.setVisibility(View.VISIBLE);
		sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.VISIBLE);
		// 隐藏panel
		// sendMsgView.hidePanelAndSoftInput();
		// sendMsgView.showSendPicCount(1);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
