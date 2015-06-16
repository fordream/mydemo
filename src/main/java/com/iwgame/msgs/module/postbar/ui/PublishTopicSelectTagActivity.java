/**      
 * PublishTopicEndActivity.java Create on 2015-2-9     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.ui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.ByteString;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.TopicTagDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.postbar.adapter.TopicTagAdapter;
import com.iwgame.msgs.proto.Msgs.ContentDetail;
import com.iwgame.msgs.proto.Msgs.ContentDetail.ContentType;
import com.iwgame.msgs.proto.Msgs.ContentList;
import com.iwgame.msgs.proto.Msgs.PostbarActionResult;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.TopicTagVo;
import com.iwgame.msgs.widget.MyGridView;
import com.iwgame.utils.StringUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: PublishTopicSelectTagActivity
 * @Description: 发布帖子选择标签
 * @author 王卫
 * @date 2015-2-9 下午7:20:46
 * @Version 1.0
 * 
 */
public class PublishTopicSelectTagActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private LinearLayout setTagContent;
	private TextView tagTxt;
	private Button tagBtn;

	private LinearLayout selectTagContent;
	private TextView selectTagTxt;

	private LinearLayout sysTagsContent;
	private LinearLayout sysTags;
	private LinearLayout historyTags;
	private TextView historyTagTip;
	private TextView historyTag1;
	private TextView historyTag2;
	private TextView historyTag3;
	private TextView historyTag4;
	private TextView historyTag5;

	private String title;
	private long gid;
	private String content;
	private List<TopicTagVo> tags;
	private TopicTagVo selectTag;

	private TopicTagAdapter topicTagAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
		if (b != null) {
			gid = b.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID);
			title = b.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TITLE);
			content = b.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CONTENT);
		}
		init();
	}

	private void init() {
		// 设置显示top左边
		setLeftVisible(true);
		// 设置包显示top右边
		setRightVisible(true);
		// 设置头中间
		setTitleTxt(getResources().getString(R.string.postbar_publish_topic_activity_title_tag));

		TextView topRightTextView = new TextView(this);
		topRightTextView.setText(R.string.postbar_publish_topic_publish);
		topRightTextView.setTextColor(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_title_textcolor());
		LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
		rightView.addView(topRightTextView);
		rightView.setOnClickListener(this);

		// 设置中间内容
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View view = (RelativeLayout) View.inflate(this, R.layout.postbar_publish_topic_selecttag, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		setTagContent = (LinearLayout) view.findViewById(R.id.setTagContent);
		tagTxt = (TextView) view.findViewById(R.id.tagTxt);
		tagTxt.setOnClickListener(this);
		tagBtn = (Button) view.findViewById(R.id.tagBtn);
		tagBtn.setOnClickListener(this);
		selectTagContent = (LinearLayout) view.findViewById(R.id.selectTagContent);
		selectTagTxt = (TextView) view.findViewById(R.id.selectTagTxt);
		selectTagTxt.setOnClickListener(this);
		sysTagsContent = (LinearLayout) view.findViewById(R.id.sysTagsContent);
		sysTags = (LinearLayout) view.findViewById(R.id.sysTags);
		historyTags = (LinearLayout) view.findViewById(R.id.historyTags);
		historyTagTip = (TextView) view.findViewById(R.id.historyTagTip);
		historyTag1 = (TextView) view.findViewById(R.id.historyTag1);
		historyTag2 = (TextView) view.findViewById(R.id.historyTag2);
		historyTag3 = (TextView) view.findViewById(R.id.historyTag3);
		historyTag4 = (TextView) view.findViewById(R.id.historyTag4);
		historyTag5 = (TextView) view.findViewById(R.id.historyTag5);
		historyTagTip.setOnClickListener(this);
		historyTag1.setOnClickListener(this);
		historyTag2.setOnClickListener(this);
		historyTag3.setOnClickListener(this);
		historyTag4.setOnClickListener(this);
		historyTag5.setOnClickListener(this);
		contentView.addView(view, params);

		// 获取标签内容
		getTags();
		showHistroyTags();
	}

	/**
	 * 获取标签内容
	 */
	private void getTags() {
		ProxyFactory.getInstance().getPostbarProxy().getTopicTags(new ProxyCallBack<List<TopicTagVo>>() {

			@Override
			public void onSuccess(List<TopicTagVo> result) {
				// 设置LIST数据
				MyGridView sysTagGridView = new MyGridView(PublishTopicSelectTagActivity.this);
				sysTags.addView(sysTagGridView);
				sysTagGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
				topicTagAdapter = new TopicTagAdapter(PublishTopicSelectTagActivity.this, new ArrayList<TopicTagVo>());
				sysTagGridView.setAdapter(topicTagAdapter);
				sysTagGridView.setOnItemClickListener(PublishTopicSelectTagActivity.this);
				sysTagGridView.setNumColumns(4);
				TopicTagAdapter adapter = (TopicTagAdapter) sysTagGridView.getAdapter();
				tags = new ArrayList<TopicTagVo>();
				int size = result.size();
				for (int i = 0; i < size; i++) {
					TopicTagVo tag = result.get(i);
					// 对访问权限和标签id为1新闻&2攻略的过滤
					if (tag.getId() != 1 && tag.getId() != 2 && tag.getId() != 7) {
						tags.add(tag);
					}
				}
				adapter.getData().addAll(tags);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO
			}
		}, gid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.rightView) {
			publishTopic();
		} else if (v.getId() == R.id.selectTagTxt) {
			setTagContent.setVisibility(View.VISIBLE);
			selectTagContent.setVisibility(View.GONE);
			selectTagTxt.setText("");
			topicTagAdapter.setSeclection(-1);
			topicTagAdapter.notifyDataSetChanged();
			historyTags.setVisibility(View.GONE);
		} else if (v.getId() == R.id.tagBtn) {
			String tname = tagTxt.getText().toString();
			if (tname != null && !"".equals(tname)) {
				if (StringUtil.getCharacterNum(tname) <= 18) {
					if (StringUtil.getCharacterNum(tname) >= 4) {
						// 设置选中标签
						selectTag = new TopicTagVo();
						selectTag.setId(7);
						selectTag.setName(tname);
						setTagContent.setVisibility(View.GONE);
						selectTagContent.setVisibility(View.VISIBLE);
						selectTagTxt.setText(selectTag.getName());
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
		} else if (v == historyTag1 || v == historyTag2 || v == historyTag3 || v == historyTag4 || v == historyTag5) {
			tagTxt.setText(historyTag1.getText().toString());
			historyTags.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示历史标签
	 */
	private void showHistroyTags() {
		TopicTagDao topicTagDao = DaoFactory.getDaoFactory().getTopicTag(this);
		List<TopicTagVo> tags = topicTagDao.getTopicTags(10);
		if (tags != null && tags.size() > 0) {
			historyTags.setVisibility(View.VISIBLE);
			int size = tags.size();
			for (int i = 0; i < size; i++) {
				TopicTagVo vo = tags.get(i);
				if (i == 0) {
					historyTag1.setVisibility(View.VISIBLE);
					historyTag1.setText(vo.getName());
				} else if (i == 2) {
					historyTag2.setVisibility(View.VISIBLE);
					historyTag3.setText(vo.getName());
				} else if (i == 3) {
					historyTag3.setVisibility(View.VISIBLE);
					historyTag3.setText(vo.getName());
				} else if (i == 4) {
					historyTag4.setVisibility(View.VISIBLE);
					historyTag4.setText(vo.getName());
				} else if (i == 5) {
					historyTag5.setVisibility(View.VISIBLE);
					historyTag5.setText(vo.getName());
				}
			}
		} else {
			historyTags.setVisibility(View.GONE);
			historyTag1.setVisibility(View.GONE);
			historyTag2.setVisibility(View.GONE);
			historyTag3.setVisibility(View.GONE);
			historyTag4.setVisibility(View.GONE);
			historyTag5.setVisibility(View.GONE);
		}
	}

	/**
	 * 发布帖子
	 */
	private void publishTopic() {
		if (selectTag != null && selectTagContent.getVisibility() == View.VISIBLE) {
			ContentList.Builder cb = ContentList.newBuilder();
			cb.setTitle(title);
			cb.setTagId(selectTag.getId());
			cb.setTagName(selectTag.getName());
			ContentDetail.Builder cd = ContentDetail.newBuilder();
			cd.setType(ContentType.TEXT);
			cd.setText(content);
			cd.setSeq(1);
			cb.addContentDetail(cd);
			ProxyFactory
					.getInstance()
					.getPostbarProxy()
					.publicTopic(new ProxyCallBack<PostbarActionResult>() {

						@Override
						public void onSuccess(PostbarActionResult result) {
							ToastUtil.showToast(PublishTopicSelectTagActivity.this, "发帖成功");
							TopicTagDao topicTagDao = DaoFactory.getDaoFactory().getTopicTag(PublishTopicSelectTagActivity.this);
							topicTagDao.insertOrUpdate(selectTag);
							PublishTopicActivity.goNext = true;
							PublishTopicSelectTagActivity.this.finish();
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							ErrorCodeUtil.handleErrorCode(PublishTopicSelectTagActivity.this, result, resultMsg);
						}
					}, this, 0l, gid, MsgsConstants.OP_POST_TOPIC, MsgsConstants.OT_TOPIC, 0, cb.build(),
							SystemContext.getInstance().getLocation(), 0);
		} else {
			ToastUtil.showToast(this, getString(R.string.ec_postbar_publishtopic_no_tag));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		historyTags.setVisibility(View.GONE);
		// 设置选中标签
		selectTag = ((TopicTagAdapter) parent.getAdapter()).getData().get(position);
		setTagContent.setVisibility(View.GONE);
		selectTagContent.setVisibility(View.VISIBLE);
		selectTagTxt.setText(selectTag.getName());
		tagTxt.setText("");
		topicTagAdapter.setSeclection(position);
		topicTagAdapter.notifyDataSetChanged();
	}

}
