package com.iwgame.msgs.module.group.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupUserRelDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.StringUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * 编辑公会名片的界面
 * 
 * @author jczhang
 * 
 */
public class EditGroupCardActivity extends BaseActivity {

	private long grid;
	private TextView groupcard;
	private LinearLayout groupCardItem;
	private Dialog dialog;
	private TextView cardwords;
	private EditText editCards;
	private Button cancelBtn;
	private Button commitBtn;
	private Button cleanBtn;
	private TextView title;
	private String groupCardContent;
	private GroupUserRelDao dao;
	private String oriContent; // 表示起初的输入框里面的内容

	/**
	 * 在界面刚起来的时候 做一些初始化操作
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		grid = getIntent().getLongExtra(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, 0);
		groupCardContent = getIntent().getStringExtra(SystemConfig.GROUP_CARD_CONTENT_KEY);
		init();
	}

	/**
	 * 做一些初始化操作 初始化控件
	 */
	private void init() {
		dao = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getContext());
		titleTxt.setText("我的公会名片");
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View view = View.inflate(this, R.layout.group_card_activity, null);
		getContentView().addView(view, params);
		groupcard = (TextView) view.findViewById(R.id.group_card_detail);
		groupCardItem = (LinearLayout) view.findViewById(R.id.groupcardItem);
		groupcard.setText(groupCardContent + "");
		this.oriContent = groupCardContent;
		setListener();
	}

	/**
	 * 当点击整个item 的时候，弹出整个dialog
	 */
	private void setListener() {
		groupCardItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				groupCardItem.setEnabled(false);
				popDialog();
			}
		});
	}

	/**
	 * 弹出对话框 修改我的公会名片
	 */
	protected void popDialog() {
		dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		View view = View.inflate(this, R.layout.dialog_card, null);
		cardwords = (TextView) view.findViewById(R.id.edit_word_num);
		title = (TextView) view.findViewById(R.id.title);
		title.setVisibility(View.INVISIBLE);
		cardwords.setText("0/20");
		editCards = (EditText) view.findViewById(R.id.edit_group_card);
		String s = groupcard.getText().toString().trim();
		editCards.setText(s);
		editCards.setSelection(s.length());
		InputFilterUtil.lengthFilter(this, editCards, 40, getString(R.string.group_card_not_limit));
		cleanBtn = (Button) view.findViewById(R.id.act_login_cleanAccountBtn);
		cleanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				editCards.setText("");
			}
		});
		cancelBtn = (Button) view.findViewById(R.id.cannelBtn);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				groupCardItem.setEnabled(true);
				dialog.dismiss();
				editCards.setText("");
			}
		});
		commitBtn = (Button) view.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				groupCardItem.setEnabled(true);
				String content = editCards.getText().toString();
				editCards.setText("");
				// 修改我的公会名片
				if (!content.equals(oriContent))
					modifiyMyGroupCard(content);
			}
		});

		editCards.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String source = editCards.getText().toString();
				int sourceLen = StringUtil.getCharacterNum(source.toString());
				if (sourceLen > 0) {
					double length = Math.ceil(sourceLen / 2.0);
					cardwords.setText((int) length + "/20");
				} else {
					cardwords.setText("0/20");
				}
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}

	/**
	 * 执行下面的这个 方法 修改我的公会名片
	 */
	protected void modifiyMyGroupCard(final String content) {
		if (content == null || "".equals(content)) {
			ToastUtil.showToast(EditGroupCardActivity.this, getString(R.string.group_card_not_null));
		} else if (content != null && content.trim().length() == 0) {
			ToastUtil.showToast(EditGroupCardActivity.this, getString(R.string.group_card_not_space));
		} else if (ServiceFactory.getInstance().getWordsManager().matchName(content)) {
			ToastUtil.showToast(this, this.getResources().getString(R.string.global_words_error));
		} else {
			dialog.dismiss();
			final CustomProgressDialog customProgressDialog = CustomProgressDialog.createDialog(this);
			customProgressDialog.show();
			ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

				@Override
				public void onSuccess(Integer result) {
					modifyDatabase(content);
					// 弹出提示语，修改textview里面的内容
					oriContent = content;
					groupcard.setText(content);
					customProgressDialog.dismiss();
					Intent intent = new Intent();
					intent.putExtra(SystemConfig.GROUP_CARD_CONTENT_KEY, groupcard.getText().toString());
					setResult(20, intent);
					// 获取到我与公会之间的关系
					GroupUserRelVo groupUserRelVo = ProxyFactory.getInstance().getGroupProxy()
							.getRel(grid, SystemContext.getInstance().getExtUserVo().getUserid());
					groupUserRelVo.setRemark(groupcard.getText().toString().trim());
					dao.insertOrUpdate(groupUserRelVo);
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					ErrorCodeUtil.handleErrorCode(EditGroupCardActivity.this, result, resultMsg);
					customProgressDialog.dismiss();
				}
			}, EditGroupCardActivity.this, grid, 5, 165, content.trim(), null, null);
		}
	}

	/**
	 * 当修改公会名片成功后 去更新数据库里面的内容
	 */
	protected void modifyDatabase(String content) {
		GroupUserRelDao userRelDao = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getContext());
		GroupUserRelVo vo = userRelDao.findUsers(grid, SystemContext.getInstance().getExtUserVo().getUserid());
		if (vo == null) {
			ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_GROUP_USER, grid, new SyncCallBack() {

				@Override
				public void onSuccess(Object result) {
				}

				@Override
				public void onFailure(Integer result) {
				}
			});

		} else {
			vo.setRemark(content);
			userRelDao.insertOrUpdate(vo);
		}
	}
}
