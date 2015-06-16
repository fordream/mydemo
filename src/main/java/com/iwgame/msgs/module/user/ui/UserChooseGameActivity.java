package com.iwgame.msgs.module.user.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.user.adapter.UserGameAdapter;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.msgs.widget.roundedimageview.RoundedImageView;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

public class UserChooseGameActivity extends BaseActivity {

	private View view;
	private GridView gameGrid;
	private List<ExtraGameVo> voList = new ArrayList<ExtraGameVo>();
	private List<ExtraGameVo> list = new ArrayList<ExtraGameVo>();
	private UserGameAdapter adapter;
	private boolean flag = false;
	private LinearLayout gameContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initial();
		getNetWork();
		getGameData(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void initial() {
		// TODO Auto-generated method stub
		setTitleTxt(getString(R.string.choose_game));
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view = View.inflate(this, R.layout.user_choose_game, null);
		contentView.addView(view, params);
		gameGrid = (GridView) view.findViewById(R.id.game_list);
		gameContent = (LinearLayout) view.findViewById(R.id.game_list_info);
		view.setVisibility(View.GONE);
	}

	/**
	 * 获取游戏数据
	 * 
	 * @param context
	 */
	public void getGameData(final Context context) {
		final CustomProgressDialog downloaddialog = CustomProgressDialog
				.createDialog(this, false);
		downloaddialog.show();
		ProxyFactory.getInstance().getUserProxy()
				.getGameList(new ProxyCallBack<List<ExtraGameVo>>() {

					@Override
					public void onSuccess(List<ExtraGameVo> result) {
						if (downloaddialog.isShowing())
							
							downloaddialog.dismiss();
						view.setVisibility(View.VISIBLE);
						if (result != null && result.size() > 0) {
							voList.clear();
							list.clear();
							for (int i = 0; i < result.size(); i++) {
								ExtraGameVo vo = result.get(i);
								voList.add(vo);
							}
							list.addAll(voList);

							addGameView(list);
							
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						if (downloaddialog.isShowing())
							downloaddialog.dismiss();
						handleErrorCode(result, resultMsg);
					}
				}, context);
	}

	/**
	 * 排列游戏列表顺序
	 * 
	 * @param voList
	 * @param list
	 */
	public void Transter(List<ExtraGameVo> voList, List<ExtraGameVo> list) {
		if (voList == null || voList.size() <= 0)
			return;
		for (int i = 0; i < voList.size(); i++) {
			ExtraGameVo vo = voList.get(i);
			if (vo.getPriority() == 7) {
				list.add(vo);
			}
		}
		if (voList == null || voList.size() <= 0)
			return;
		for (int i = 0; i < voList.size(); i++) {
			ExtraGameVo vo = voList.get(i);
			if (vo.getRecomment()==1) {
				list.add(vo);
			}
		}
		if (voList == null || voList.size() <= 0)
			return;
		for (int i = 0; i < voList.size(); i++) {
			ExtraGameVo vo = voList.get(i);
			if (vo.getPriority() <= 2) {
				list.add(vo);
			}
		}
	}

	/**
	 * 用户反馈到服务器
	 * 
	 * @param context
	 * @param gid
	 */
	public void sendUserApply(final Context context, Long gid) {
		ProxyFactory.getInstance().getUserProxy()
				.UserGameAPPly(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						// TODO Auto-generated method stub
						if (result == 0) {
							getApplyDialog();
						} else {
							ToastUtil.showToast(context, "无法获取游戏信息");
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub
						handleErrorCode(result, resultMsg);
					}
				}, context, gid);
	}

	/**
	 * 设置用户反馈对话框
	 * 
	 * @param gid
	 */

	public void setOpenGameDialog(final Long gid) {
		final Dialog dialog = new Dialog(UserChooseGameActivity.this,
				R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView) dialog.findViewById(R.id.content_dialog);
		content.setText("该游戏的角色名还未开通，若觉得需要开通请点击确定哦！");
		LinearLayout btnBttom = (LinearLayout) dialog
				.findViewById(R.id.btn_bottom);
		LinearLayout textBttom = (LinearLayout) dialog
				.findViewById(R.id.text_bottom);
		btnBttom.setVisibility(View.VISIBLE);
		textBttom.setVisibility(View.GONE);
		dialog.show();
		dialog.findViewById(R.id.role_cannelBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();

					}
				});
		dialog.findViewById(R.id.role_commitBtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						sendUserApply(UserChooseGameActivity.this, gid);
					}
				});
	}

	/**
	 * 用户反馈成功提示
	 */
	public void getApplyDialog() {
		final Dialog dialog = new Dialog(UserChooseGameActivity.this,
				R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.user_role_dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView content = (TextView) dialog.findViewById(R.id.content_dialog);
		content.setText("谢谢你的反馈！");
		LinearLayout btnBttom = (LinearLayout) dialog
				.findViewById(R.id.btn_bottom);
		LinearLayout textBttom = (LinearLayout) dialog
				.findViewById(R.id.text_bottom);
		btnBttom.setVisibility(View.GONE);
		textBttom.setVisibility(View.VISIBLE);
		dialog.show();
		textBttom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

	public void getNetWork() {
		if (NetworkUtil.isNetworkAvailable(getApplicationContext())) {
			flag = true;
		} else {
			ToastUtil.showToast(UserChooseGameActivity.this,
					getString(R.string.network_error));
		}
	}

	public void addGameView(List<ExtraGameVo> list) {
		if (list == null && list.size() <= 0)
			return;
		gameContent.removeAllViews();
		int count = (list.size() % 4 == 0 ? list.size() : list.size() + 1);
		int numColumns = 4;
		int size = list.size();
		for (int i = 0; i < count; i++) {
			View rowView = View.inflate(this, R.layout.game_role_row, null);
			View item1 = rowView.findViewById(R.id.game_followgame_row_item_1);
			View item2 = rowView.findViewById(R.id.game_followgame_row_item_2);
			View item3 = rowView.findViewById(R.id.game_followgame_row_item_3);
			View item4 = rowView.findViewById(R.id.game_followgame_row_item_4);
			for (int j = i * numColumns; j < (i + 1) * numColumns && j < size; j++) {
				final ExtraGameVo vo = list.get(j);
				if (j % numColumns == 0) {
					item1.setVisibility(View.VISIBLE);
					addViewHolder(item1, vo);
				} else if (j % numColumns == 1) {
					item2.setVisibility(View.VISIBLE);
					addViewHolder(item2, vo);
				} else if (j % numColumns == 2) {
					item3.setVisibility(View.VISIBLE);
					addViewHolder(item3, vo);
				} else if (j % numColumns == 3) {
					item4.setVisibility(View.VISIBLE);
					addViewHolder(item4, vo);
				} else {
					item1.setVisibility(View.GONE);
					item2.setVisibility(View.GONE);
					item3.setVisibility(View.GONE);
					item4.setVisibility(View.GONE);
				}
			}
			gameContent.addView(rowView);
		}

	}

	private void addViewHolder(View view, final ExtraGameVo vo) {
		RelativeLayout gameView = (RelativeLayout) view
				.findViewById(R.id.user_role_game);
		gameView.setVisibility(View.VISIBLE);
		RoundedImageView icon = (RoundedImageView) view
				.findViewById(R.id.game_icon_image);
		TextView name = (TextView) view.findViewById(R.id.game_name);
		ImageView tuijian = (ImageView) view.findViewById(R.id.tui_jian_icon);
		if (vo.getRecomment()==1) {
			tuijian.setVisibility(View.VISIBLE);
		} else {
			tuijian.setVisibility(View.GONE);
		}
		icon.setImageResource(R.drawable.common_default_icon);
		if (vo.getPriority() <= 2) {
			new ImageLoader().loadRes(ResUtil.getOriginalRelUrl(vo.getIcon()),
					0, icon, R.drawable.common_default_icon, true, true, false);
			name.setTextColor(this.getResources().getColor(
					R.color.game_list_name));
		} else {
			new ImageLoader()
					.loadRes(ResUtil.getOriginalRelUrl(vo.getIcon()), 0, icon,
							R.drawable.common_default_icon, true, false, false);
			name.setTextColor(this.getResources().getColor(R.color.black));
		}
		name.setText(vo.getgName());

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				if (vo.getPriority() <= 2) {
					setOpenGameDialog(vo.getGid());
				} else {
					Intent intent = new Intent(UserChooseGameActivity.this,
							UserAddRoleInfoActivity.class);
					intent.putExtra("gid", vo.getGid());
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 
	 * 处理错误码
	 * 
	 * @param i
	 * @param msg
	 */
	private void handleErrorCode(Integer i, String msg) {
		ErrorCodeUtil.handleErrorCode(UserChooseGameActivity.this, i, msg);
	}
}
