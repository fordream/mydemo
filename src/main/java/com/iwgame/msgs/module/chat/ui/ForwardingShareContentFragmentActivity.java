/**      
 * ForwardingMessageActivity.java Create on 2014-3-31     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.chat.ui;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseFragmentActivity;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.chat.adapter.MyTabsAdapter;
import com.iwgame.msgs.module.setting.vo.Goods;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.MessageContentType;
import com.iwgame.msgs.proto.Msgs.PostElementType;
import com.iwgame.msgs.utils.DialogUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GameNewDetailVo;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.FileUtils;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: ForwardingMessageActivity
 * @Description: TODO(转发消息选择用户或公会界面)
 * @author chuanglong
 * @date 2014-3-31 上午11:19:13
 * @Version 1.0
 * 
 */
public class ForwardingShareContentFragmentActivity extends BaseFragmentActivity {
	protected static final String TAG = "ForwardingMessageFragmentActivity";
	private LayoutInflater layoutInflater;

	// 定义数组来存放Fragment界面
	private Class mFragmentArray[] = null;
	// Tab选项卡的文字
	private String mTabTextArray[] = null;

	// 定义TabHost对象
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private MyTabsAdapter mTabsAdapter;

	/**
	 * 操作类型，是转发还是分享
	 */
	private int contentActionType = 0;
	/**
	 * 内容类型，是消息还是帖子
	 */
	private int contentType = 0;
	/**
	 * 内容是消息
	 */
	private MessageVo contentMessageVo;
	/**
	 * 内容是用户
	 */
	private UserVo userVo;
	/**
	 * 内容是游戏
	 */
	private GameVo gameVo;
	/**
	 * 内容是公会
	 */
	private GroupVo groupVo;
	/**
	 * 内容是商品
	 */
	private Goods goodsVo;
	/**
	 * 内容是帖子
	 */
	private Msgs.PostbarTopicDetail contentTopic;

	/**
	 * 攻略贴吧网页详情分享传输对象
	 */
	private GameNewDetailVo gameNewDeatilVo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取上一个页面的传值
		Intent tmpintent = this.getIntent();
		if (tmpintent != null) {
			Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
			if (tmpbundle != null) {
				contentActionType = tmpbundle.getInt(SystemConfig.BUNDLE_NAME_CONTENT_ACTION_TYPE);
				contentType = tmpbundle.getInt(SystemConfig.BUNDLE_NAME_CONTENT_TYPE);
				// 反序列化，在本地重建数据
				Serializable data = tmpbundle.getSerializable(SystemConfig.BUNDLE_NAME_CONTENT);
				if (data != null) {
					if (contentType == SystemConfig.CONTENT_TYPE_TOPIC) {
						if (data instanceof Msgs.PostbarTopicDetail)
							contentTopic = (Msgs.PostbarTopicDetail) data;
					} else if (contentType == SystemConfig.CONTENT_TYPE_MESSAGEVO) {
						if (data instanceof MessageVo)
							contentMessageVo = (MessageVo) data;
					} else if (contentType == SystemConfig.CONTENT_TYPE_USER_DETIAL) {
						if (data instanceof UserVo)
							userVo = (UserVo) data;
					} else if (contentType == SystemConfig.CONTENT_TYPE_GROUP_DETIAL) {
						if (data instanceof GroupVo)
							groupVo = (GroupVo) data;
					} else if (contentType == SystemConfig.CONTENT_TYPE_GAME_DETIAL) {
						if (data instanceof GameVo)
							gameVo = (GameVo) data;
					} else if (contentType == SystemConfig.CONTENT_TYPE_GOODS_DETIAL) {
						if (data instanceof Goods)
							goodsVo = (Goods) data;
					} else if (contentType == SystemConfig.CONTENT_TYPE_GAMENEW_DETIAL) {
						if (data instanceof GameNewDetailVo)
							gameNewDeatilVo = (GameNewDetailVo) data;
					}
				}

			}
		}
		setContentView(R.layout.common_content);
		// 实例化布局对象
		layoutInflater = LayoutInflater.from(this);
		// 初始化
		init(savedInstanceState);

	}

	private void init(Bundle savedInstanceState) {
		// 定义数组存放tab相关资源
		// 定义数组来存放Fragment界面
		mFragmentArray = new Class[] { ForwardingShareSubjectListFragment.class, ForwardingShareSubjectListFragment.class,
				ForwardingShareSubjectListFragment.class };
		// Tab选项卡的文字
		mTabTextArray = new String[] { getString(R.string.chat_forwarding_title_latestcontacts), getString(R.string.chat_forwarding_title_follow),
				getString(R.string.chat_forwarding_title_group) };

		// 设置Top左边返回功能
		Button backBtn = (Button) findViewById(R.id.leftBtn);
		if (backBtn != null) {
			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ForwardingShareContentFragmentActivity.this.finish();
				}
			});
		}

		// 设置Top中间标题
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText(R.string.chat_forwarding_title);
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View view = (LinearLayout) View.inflate(this, R.layout.common_sub_tab_main, null);
		contentView.addView(view);

		// 实例化TabHost对象，得到TabHost
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mViewPager = (ViewPager) findViewById(R.id.pager);

		mTabsAdapter = new MyTabsAdapter(this, mTabHost, mViewPager, null);
		// 得到fragment的个数
		int count = mFragmentArray.length;
		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTabTextArray[i]).setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			Bundle bundle = null;
			bundle = new Bundle();
			if (i == 0)// 最近联系人
			{
				// 最近联系人的参数
				bundle.putInt(SystemConfig.BUNDLE_NAME_FORWARDING_SUBJECT_LIST_TYPE, SystemConfig.FORWARDING_SUBJECT_LIST_TYPE_LATESTCONTACTS);
			} else if (i == 1) {
				bundle.putInt(SystemConfig.BUNDLE_NAME_FORWARDING_SUBJECT_LIST_TYPE, SystemConfig.FORWARDING_SUBJECT_LIST_TYPE_USERS);
			} else if (i == 2) {
				bundle.putInt(SystemConfig.BUNDLE_NAME_FORWARDING_SUBJECT_LIST_TYPE, SystemConfig.FORWARDING_SUBJECT_LIST_TYPE_GROUP);
			}
			mTabsAdapter.addTab(tabSpec, mFragmentArray[i], bundle);
		}

		if (savedInstanceState != null && savedInstanceState.getString("tab") != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		} else {// 设置最近联系人为默认
			mTabHost.setCurrentTabByTag(mTabTextArray[0]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 根据索引值获取界面
	 * 
	 * @param index
	 * @return
	 */
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.common_sub_tab_item_view, null);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTabTextArray[index]);
		return view;
	}

	/**
	 * 转发分享内容
	 * 
	 * @param targetId
	 *            目标id
	 * @param targetDomain
	 *            目标domain
	 * @param targetObject
	 *            目标对象的实体（用户或公会）
	 */
	public void forwardingShareContent(final long targetId, final String targetDomain, final Object targetObject) {
		// 先弹出确认窗口
		View view = this.layoutInflater.inflate(R.layout.forwarding_confirm_content, null);
		ImageView tmp_iconView = (ImageView) view.findViewById(R.id.icon);
		TextView tmp_textView = (TextView) view.findViewById(R.id.title);
		if (targetObject != null) {
			String iconUrl = "";
			String title = "";
			if (targetObject instanceof UserVo) {
				iconUrl = ((UserVo) targetObject).getAvatar();
				title = ((UserVo) targetObject).getUsername();
			} else if (targetObject instanceof GroupVo) {
				iconUrl = ((GroupVo) targetObject).getAvatar();
				title = ((GroupVo) targetObject).getName();
			}
			new ImageLoader().loadRes(ResUtil.getSmallRelUrl(iconUrl), 0, tmp_iconView, R.drawable.common_user_icon_default);
			tmp_textView.setText(title);
		}
		// listener
		DialogUtil.OKCallBackListener listener = new DialogUtil.OKCallBackListener() {
			@Override
			public void execute() {
				if (contentType == SystemConfig.CONTENT_TYPE_MESSAGEVO) {
					if (contentMessageVo != null)
						realForwardingContentMessage(targetId, targetDomain, contentMessageVo);
					else
						ToastUtil.showToast(ForwardingShareContentFragmentActivity.this, "内容不存在");
				} else if (contentType == SystemConfig.CONTENT_TYPE_TOPIC) {
					if (contentTopic != null)
						realShareContentTopic(targetId, targetDomain, contentTopic);
					else
						ToastUtil.showToast(ForwardingShareContentFragmentActivity.this, "内容不存在");
				} else if (contentType == SystemConfig.CONTENT_TYPE_GAME_DETIAL) {
					if (gameVo != null)
						realShareContentGame(targetId, targetDomain, gameVo);
					else
						ToastUtil.showToast(ForwardingShareContentFragmentActivity.this, "内容不存在");
				} else if (contentType == SystemConfig.CONTENT_TYPE_GOODS_DETIAL) {
					if (goodsVo != null)
						realShareContentGoods(targetId, targetDomain, goodsVo);
					else
						ToastUtil.showToast(ForwardingShareContentFragmentActivity.this, "内容不存在");
				} else if (contentType == SystemConfig.CONTENT_TYPE_GROUP_DETIAL) {
					if (groupVo != null)
						realShareContentGroup(targetId, targetDomain, groupVo);
					else
						ToastUtil.showToast(ForwardingShareContentFragmentActivity.this, "内容不存在");
				} else if (contentType == SystemConfig.CONTENT_TYPE_USER_DETIAL) {
					if (userVo != null)
						realShareContentUser(targetId, targetDomain, userVo);
					else
						ToastUtil.showToast(ForwardingShareContentFragmentActivity.this, "内容不存在");
				} else if (contentType == SystemConfig.CONTENT_TYPE_GAMENEW_DETIAL) {
					if (gameNewDeatilVo != null)
						realShareContentGameNewDetail(targetId, targetDomain, gameNewDeatilVo);
					else
						ToastUtil.showToast(ForwardingShareContentFragmentActivity.this, "内容不存在");
				}

			}

			@Override
			public void cannel() {
				// TODO Auto-generated method stub
			}
		};
		if (contentActionType == SystemConfig.CONTENT_ACTION_TYPE_FORWARDING)
			DialogUtil.showDialog(this, R.layout.dialog, getString(R.string.chat_forwarding_action_forwarding_title), view, listener);
		else if (contentActionType == SystemConfig.CONTENT_ACTION_TYPE_SHARE)
			DialogUtil.showDialog(this, R.layout.dialog, getString(R.string.chat_forwarding_action_share_title), view, listener);

	}

	/**
	 * 转发消息
	 * 
	 * @param toId
	 * @param toDomain
	 * @param forwardingMessageVo
	 */
	private void realForwardingContentMessage(final long toId, final String toDomain, MessageVo forwardingMessageVo) {
		int msgtype = forwardingMessageVo.getContentType();
		String content = null;
		byte[] contentBytes = null;
		long forwardId = 0;
		String forwardType = null;
		switch (msgtype) {
		case MessageContentType.TEXT_VALUE:
			content = ServiceFactory.getInstance().getWordsManager().replace(forwardingMessageVo.getContent());
			break;

		case MessageContentType.IMAGE_VALUE:
			if (forwardingMessageVo.getFromId() == SystemContext.getInstance().getExtUserVo().getUserid())
				contentBytes = FileUtils.getSDFileData(String.format(SystemConfig.IMAGE_LOCAL_SEND_FILENAME_FORMAT, forwardingMessageVo.getId()));
			break;
		case MessageContentType.VOICE_VALUE:
			// 判断声音是否是自己发出
			if (forwardingMessageVo.getFromId() == SystemContext.getInstance().getExtUserVo().getUserid())
				contentBytes = FileUtils.getSDFileData(String.format(SystemConfig.AUDIO_LOCAL_SEND_FILENAME_FORMAT, forwardingMessageVo.getId()));
			else
				contentBytes = FileUtils.getSDFileData(String.format(SystemConfig.AUDIO_FILENAME_FORMAT, forwardingMessageVo.getMsgId()));
			break;
		case MessageContentType.MV_VALUE:
			break;
		case MessageContentType.FILE_VALUE:
			break;
		case MessageContentType.IMAGE_ID_REF_VALUE:
			// 获得图片的数据
			content = forwardingMessageVo.getContent();
			break;
		case MessageContentType.NEWS_TEXT_VALUE:
			// 新闻
			content = forwardingMessageVo.getContent();
			break;
		}

		if (forwardingMessageVo.getForwardId() != 0) {
			// 祖节点
			forwardId = forwardingMessageVo.getForwardId();
			forwardType = forwardingMessageVo.getForwardType();
		} else {
			// 消息本身
			forwardId = forwardingMessageVo.getMsgId();
			forwardType = MsgsConstants.FORWARD_TYPE_MSG;
		}
		realSendMessage(toId, toDomain, msgtype, content, contentBytes, forwardId, forwardType, forwardingMessageVo.getSummary());
	}

	/**
	 * 分享帖子
	 * 
	 * @param toId
	 * @param toDomain
	 * @param topic
	 */
	private void realShareContentTopic(final long toId, final String toDomain, Msgs.PostbarTopicDetail topic) {
		int msgtype = MessageContentType.NEWS_TEXT_VALUE;
		String content = null;
		byte[] contentBytes = null;
		long forwardId = 0;
		String forwardType = null;

		content = topic2jsonString(topic);
		forwardId = topic.getId();
		forwardType = MsgsConstants.FORWARD_TYPE_POSTBAR;
		realSendMessage(toId, toDomain, msgtype, content, contentBytes, forwardId, forwardType, "[分享了帖子]");
	}

	private String topic2jsonString(Msgs.PostbarTopicDetail topic) {
		JSONObject ret = new JSONObject();
		if (topic != null) {
			try {
				JSONObject news = new JSONObject();
				JSONArray articles = new JSONArray();
				JSONObject article = new JSONObject();
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_TITLE, topic.getTitle());
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_DESC, topic.getContent());
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT_TYPE, MsgsConstants.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_POST);
				JSONObject content = new JSONObject();
				content.put(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_TOPICID, topic.getId());
				content.put(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_GAMEID, topic.getGameid());
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT, content);
				String tmpImageSrc = "";
				if (topic.hasPostContent() && topic.getPostContent().getElementsCount() > 0) {
					for (int i = 0; i < topic.getPostContent().getElementsCount(); i++) {
						if (topic.getPostContent().getElements(i).getType() == PostElementType.PE_IMAGE_ID_REF) {
							tmpImageSrc = topic.getPostContent().getElements(i).getResourceId();
							break;
						}
					}

				}
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_PIC, tmpImageSrc);

				articles.put(article);
				news.put(MsgsConstants.JKEY_MESSAGE_NEWS_ARTICLES, articles);
				ret.put(MsgsConstants.JKEY_MESSAGE_NEWS, news);
			} catch (JSONException ex) {

			}
		}
		return ret.toString();
	}

	/**
	 * 分享公会
	 * 
	 * @param toId
	 * @param toDomain
	 * @param topic
	 */
	private void realShareContentGroup(final long toId, final String toDomain, GroupVo vo) {
		int msgtype = MessageContentType.NEWS_TEXT_VALUE;
		String content = null;
		byte[] contentBytes = null;
		long forwardId = 0;
		String forwardType = null;

		content = group2jsonString(vo);
		forwardId = vo.getGrid();
		forwardType = SystemConfig.FORWARD_TYPE_GROUP;
		realSendMessage(toId, toDomain, msgtype, content, contentBytes, forwardId, forwardType, "[分享了公会]");
	}

	/**
	 * 
	 * @param vo
	 * @return
	 */
	private String group2jsonString(GroupVo vo) {
		JSONObject ret = new JSONObject();
		if (vo != null) {
			try {
				JSONObject news = new JSONObject();
				JSONArray articles = new JSONArray();
				JSONObject article = new JSONObject();
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_TITLE, "分享[" + vo.getName() + "]公会！");
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_DESC, vo.getUndesc());
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT_TYPE, SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_GROUP);
				JSONObject content = new JSONObject();
				content.put(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_GRID, vo.getGrid());
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT, content);
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_PIC, vo.getAvatar());

				articles.put(article);
				news.put(MsgsConstants.JKEY_MESSAGE_NEWS_ARTICLES, articles);
				ret.put(MsgsConstants.JKEY_MESSAGE_NEWS, news);
			} catch (JSONException ex) {

			}
		}
		return ret.toString();
	}

	/**
	 * 分享贴吧
	 * 
	 * @param toId
	 * @param toDomain
	 * @param topic
	 */
	private void realShareContentGame(final long toId, final String toDomain, GameVo vo) {
		int msgtype = MessageContentType.NEWS_TEXT_VALUE;
		String content = null;
		byte[] contentBytes = null;
		long forwardId = 0;
		String forwardType = null;

		content = game2jsonString(vo);
		forwardId = vo.getGameid();
		forwardType = SystemConfig.FORWARD_TYPE_GAME;
		realSendMessage(toId, toDomain, msgtype, content, contentBytes, forwardId, forwardType, "[分享了贴吧]");
	}

	/**
	 * 
	 * @param vo
	 * @return
	 */
	private String game2jsonString(GameVo vo) {
		JSONObject ret = new JSONObject();
		if (vo != null) {
			try {
				JSONObject news = new JSONObject();
				JSONArray articles = new JSONArray();
				JSONObject article = new JSONObject();
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_TITLE, "分享[" + vo.getGamename() + "]贴吧！");
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_DESC, vo.getDesc());
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT_TYPE, SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_GAME);
				JSONObject content = new JSONObject();
				content.put(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_GAMEID, vo.getGameid());
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT, content);
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_PIC, vo.getGamelogo());

				articles.put(article);
				news.put(MsgsConstants.JKEY_MESSAGE_NEWS_ARTICLES, articles);
				ret.put(MsgsConstants.JKEY_MESSAGE_NEWS, news);
			} catch (JSONException ex) {

			}
		}
		return ret.toString();
	}

	/**
	 * 分享个人资料
	 * 
	 * @param toId
	 * @param toDomain
	 * @param topic
	 */
	private void realShareContentUser(final long toId, final String toDomain, UserVo vo) {
		int msgtype = MessageContentType.NEWS_TEXT_VALUE;
		String content = null;
		byte[] contentBytes = null;
		long forwardId = 0;
		String forwardType = null;

		content = user2jsonString(vo);
		forwardId = vo.getUserid();
		forwardType = SystemConfig.FORWARD_TYPE_USER;
		realSendMessage(toId, toDomain, msgtype, content, contentBytes, forwardId, forwardType, "[分享了资料]");
	}

	/**
	 * 
	 * @param vo
	 * @return
	 */
	private String user2jsonString(UserVo vo) {
		JSONObject ret = new JSONObject();
		if (vo != null) {
			try {
				JSONObject news = new JSONObject();
				JSONArray articles = new JSONArray();
				JSONObject article = new JSONObject();
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_TITLE, "分享[" + vo.getUsername() + "]个人资料");
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_DESC, vo.getMood());
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT_TYPE, SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_USER);
				JSONObject content = new JSONObject();
				content.put(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_UID, vo.getUserid());
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT, content);
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_PIC, vo.getAvatar());

				articles.put(article);
				news.put(MsgsConstants.JKEY_MESSAGE_NEWS_ARTICLES, articles);
				ret.put(MsgsConstants.JKEY_MESSAGE_NEWS, news);
			} catch (JSONException ex) {

			}
		}
		return ret.toString();
	}

	/**
	 * 分享商品
	 * 
	 * @param toId
	 * @param toDomain
	 * @param topic
	 */
	private void realShareContentGoods(final long toId, final String toDomain, Goods vo) {
		int msgtype = MessageContentType.NEWS_TEXT_VALUE;
		String content = null;
		byte[] contentBytes = null;
		long forwardId = 0;
		String forwardType = null;

		content = goods2jsonString(vo);
		forwardId = vo.getId();
		forwardType = SystemConfig.FORWARD_TYPE_GOODS;
		realSendMessage(toId, toDomain, msgtype, content, contentBytes, forwardId, forwardType, "[分享了商品]");
	}

	/**
	 * 分享CMS内容详情
	 * 
	 * @param toId
	 * @param toDomain
	 * @param topic
	 */
	private void realShareContentGameNewDetail(final long toId, final String toDomain, GameNewDetailVo vo) {
		int msgtype = MessageContentType.NEWS_TEXT_VALUE;
		String content = null;
		byte[] contentBytes = null;
		long forwardId = 0;
		String forwardType = null;

		content = gameNewDetail2jsonString(vo);
		forwardId = vo.getId();
		forwardType = SystemConfig.FORWARD_TYPE_GAMENEWDETAIL;
		realSendMessage(toId, toDomain, msgtype, content, contentBytes, forwardId, forwardType, "[分享了内容]");
	}

	/**
	 * 
	 * @param vo
	 * @return
	 */
	private String goods2jsonString(Goods vo) {
		JSONObject ret = new JSONObject();
		if (vo != null) {
			try {
				JSONObject news = new JSONObject();
				JSONArray articles = new JSONArray();
				JSONObject article = new JSONObject();
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_TITLE, "分享[" + vo.getName() + "]兑换！");
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_DESC, vo.getName());
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT_TYPE, SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_GOODS);
				JSONObject content = new JSONObject();
				content.put(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_GOODSID, vo.getId());
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT, content);
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_PIC, vo.getIcon());

				articles.put(article);
				news.put(MsgsConstants.JKEY_MESSAGE_NEWS_ARTICLES, articles);
				ret.put(MsgsConstants.JKEY_MESSAGE_NEWS, news);
			} catch (JSONException ex) {

			}
		}
		return ret.toString();
	}

	/**
	 * 
	 * @param vo
	 * @return
	 */
	private String gameNewDetail2jsonString(GameNewDetailVo vo) {
		JSONObject ret = new JSONObject();
		if (vo != null) {
			try {
				JSONObject news = new JSONObject();
				JSONArray articles = new JSONArray();
				JSONObject article = new JSONObject();
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_TITLE, "分享[" + vo.getTitle() + "]内容！");
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_DESC, vo.getContent());
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT_TYPE, SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_WEBPAGE);
				JSONObject content = new JSONObject();
				content.put(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_WEBPAGEID, vo.getId());
				content.put(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_WEBPAGELINK, vo.getLink());
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT, content);
				article.put(MsgsConstants.JKEY_MESSAGE_NEWS_PIC, vo.getIcon());

				articles.put(article);
				news.put(MsgsConstants.JKEY_MESSAGE_NEWS_ARTICLES, articles);
				ret.put(MsgsConstants.JKEY_MESSAGE_NEWS, news);
			} catch (JSONException ex) {

			}
		}
		return ret.toString();
	}

	/**
	 * 
	 * @param toId
	 * @param toDomain
	 * @param msgtype
	 * @param content
	 * @param contentBytes
	 * @param forwardId
	 * @param forwardType
	 * @param summary
	 */
	private void realSendMessage(final long toId, final String toDomain, int msgtype, String content, byte[] contentBytes, long forwardId,
			final String forwardType, String summary) {
		// 发送消息
		MessageVo vo = new MessageVo();

		vo.setSource(MessageVo.SOURCE_CLIENT);
		if (toDomain.equals(MsgsConstants.DOMAIN_USER)) {
			vo.setChannelType(MsgsConstants.MC_CHAT);

		} else if (toDomain.equals(MsgsConstants.DOMAIN_GROUP)) {
			vo.setChannelType(MsgsConstants.MC_MCHAT);
		}

		vo.setMsgId(0);
		vo.setFromId(SystemContext.getInstance().getExtUserVo().getUserid());
		vo.setFromDomain(MsgsConstants.DOMAIN_USER);
		vo.setToId(toId);
		vo.setToDomain(toDomain);
		vo.setSubjectId(toId);
		vo.setSubjectDomain(toDomain);

		vo.setCategory(MsgsConstants.MCC_CHAT);

		vo.setContentType(msgtype);
		switch (msgtype) {
		case MessageContentType.TEXT_VALUE:
			content = ServiceFactory.getInstance().getWordsManager().replace(content);
			vo.setContent(content);
			vo.setSummary(content);
			break;

		case MessageContentType.IMAGE_VALUE:
			vo.setContentBytes(contentBytes);
			vo.setSummary("[图片]");
			break;
		case MessageContentType.VOICE_VALUE:
			vo.setContentBytes(contentBytes);
			vo.setSummary("[声音]");
			break;
		case MessageContentType.MV_VALUE:
			vo.setContentBytes(contentBytes);
			vo.setSummary("[视频]");
			break;
		case MessageContentType.FILE_VALUE:
			vo.setContentBytes(contentBytes);
			vo.setSummary("[文件]");
			break;
		case MessageContentType.IMAGE_ID_REF_VALUE:
			vo.setContent(content);
			vo.setSummary("[图片]");
			break;
		case MessageContentType.NEWS_TEXT_VALUE:
			vo.setContent(content);
			vo.setSummary(summary);
			break;
		}

		vo.setCreateTime(System.currentTimeMillis());
		vo.setPosition(SystemContext.getInstance().getLocation());
		vo.setReadStatus(MessageVo.READSTATUS_READ);
		vo.setStatus(MessageVo.STATUS_SENDING);

		vo.setForwardId(forwardId);
		vo.setForwardType(forwardType);
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();

		// 先入本地库中
		final MessageVo local_vo = ProxyFactory.getInstance().getMessageProxy().sendMessageSaveToLocal(this, vo);

		if (local_vo == null) {
			ToastUtil.showToast(this, "消息本地入库异常，请稍后重复");
		} else {
			ProxyCallBack<MessageVo> callback = new ProxyCallBack<MessageVo>() {

				@Override
				public void onSuccess(MessageVo result) {
					dialog.dismiss();
					if (contentActionType == SystemConfig.CONTENT_ACTION_TYPE_FORWARDING) {
						LogUtil.i(TAG, "消息转发成功:" + (result != null ? result.toString() : ""));
						ToastUtil.showToast(ForwardingShareContentFragmentActivity.this, "消息转发成功");

					} else if (contentActionType == SystemConfig.CONTENT_ACTION_TYPE_SHARE) {
						LogUtil.i(TAG, "分享成功:" + (result != null ? (result.toString()) : ""));
						if (forwardType == MsgsConstants.FORWARD_TYPE_POSTBAR)
							ToastUtil.showToast(ForwardingShareContentFragmentActivity.this, "帖子分享成功");
						if (forwardType == SystemConfig.FORWARD_TYPE_GAME)
							ToastUtil.showToast(ForwardingShareContentFragmentActivity.this, "贴吧分享成功");
						if (forwardType == SystemConfig.FORWARD_TYPE_GOODS)
							ToastUtil.showToast(ForwardingShareContentFragmentActivity.this, "商品分享成功");
						if (forwardType == SystemConfig.FORWARD_TYPE_GROUP)
							ToastUtil.showToast(ForwardingShareContentFragmentActivity.this, "公会资料分享成功");
						if (forwardType == SystemConfig.FORWARD_TYPE_USER)
							ToastUtil.showToast(ForwardingShareContentFragmentActivity.this, "用户资料分享成功");
					}
					// 发送消息广播
					Bundle bundle = new Bundle();
					// 序列化
					bundle.putSerializable(SystemConfig.BUNDLE_NAME_MESSAGEVO, result);
					bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISHAVEFOLLOWUPDATA, false);
					bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISCHATACTIVITY_RECEIVE, false);
					bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISMAINACTIVITY_RECEIVE, false);

					Intent intent = new Intent();
					intent.setAction(SystemConfig.ACTION_RECEIVEMESSAGE_BROADCAST);
					intent.putExtras(bundle);
					ForwardingShareContentFragmentActivity.this.sendOrderedBroadcast(intent, null);

					ForwardingShareContentFragmentActivity.this.finish();

				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					dialog.dismiss();
					// "消息发送失败:请稍后再试");
					LogUtil.e(TAG, "消息转发失败:" + result);
					// 删除转发失败的消息
					ProxyFactory.getInstance().getMessageProxy().delMessageById(local_vo.getId());

					// 增加转换错误码
					if (result.intValue() == Msgs.ErrorCode.EC_MSGS_MSG_POST_NORIGHT_VALUE) {
						if (toDomain.equals(MsgsConstants.DOMAIN_GROUP)) {
							ErrorCodeUtil.handleErrorCode(ForwardingShareContentFragmentActivity.this, ErrorCode.EC_CLIENT_MCHAT_NORIGHT, null);
						} else if (toDomain.equals(MsgsConstants.DOMAIN_USER)) {
							ErrorCodeUtil.handleErrorCode(ForwardingShareContentFragmentActivity.this, ErrorCode.EC_CLIENT_CHAT_NORIGHT, null);
						} else {
							ErrorCodeUtil.handleErrorCode(ForwardingShareContentFragmentActivity.this, result, resultMsg);
						}

					} else {
						ErrorCodeUtil.handleErrorCode(ForwardingShareContentFragmentActivity.this, result, resultMsg);
					}

				}

			};

			ProxyFactory.getInstance().getMessageProxy().sendMessage(callback, this, local_vo, false);
		}

	}

}
