/**      
 * UserFilterView.java Create on 2015-4-16     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.discover.ui.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.discover.adapter.BaseMultipleFilterAdapter;
import com.iwgame.msgs.module.discover.adapter.BaseRadioFilterAdapter;
import com.iwgame.msgs.module.discover.ui.filter.vo.FilterVo;
import com.iwgame.msgs.proto.Msgs;
import com.youban.msgs.R;

/**
 * @ClassName: UserFilterView
 * @Description: 用户过滤视图
 * @author 王卫
 * @date 2015-4-16 下午3:59:22
 * @Version 1.0
 * 
 */
public class UserFilterView extends FilterView {

	private TextView genderTxt;
	private TextView timeTxt;
	private TextView serviceNunTxt;
	private TextView serviceTxt;

	private PullToRefreshListView genderRefreshList;
	private PullToRefreshListView timeRefreshList;
	private PullToRefreshListView serviceRefreshList;
	private View genderView;
	private View timeView;
	private View seviceView;
	private TextView serviceNullBg;
	private LinearLayout serviceBottom;

	private Integer selectedGender;
	private Integer selectedTime;

	private BaseRadioFilterAdapter<FilterVo> genderAdapter;
	private BaseRadioFilterAdapter<FilterVo> timeAdapter;
	private BaseMultipleFilterAdapter<FilterVo> serviceAdapter;

	private String selectServiceIds;

	/**
	 * @param context
	 */
	public UserFilterView(Context context, RefreshDataListener listener, int showServiceTab, int mode) {
		super(context, listener, R.layout.discover_filter_user_view, mode);
		findViewById(R.id.serviceTab).setVisibility(showServiceTab);
		genderTxt = (TextView) findViewById(R.id.genderTxt);
		timeTxt = (TextView) findViewById(R.id.timeTxt);
		serviceNunTxt = (TextView) findViewById(R.id.serviceNunTxt);
		serviceTxt = (TextView) findViewById(R.id.serviceTxt);
		// 初始化列表
		creatGenderListView();
		creatTimeListView();
		creatServiceListView(false);
		// 设置选项卡点击事件
		views = new ArrayList<TextView>();
		views.add(genderTxt);
		views.add(timeTxt);
		views.add(serviceTxt);
		genderTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addPopView(0, genderView, genderTxt);
			}
		});
		timeTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addPopView(1, timeView, timeTxt);
			}
		});
		serviceTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				creatServiceListView(true);
			}
		});
	}

	/**
	 * 性别
	 */
	private void creatGenderListView() {
		int index = 0;
		if (mode == MODE_DISCOVER)
			index = SystemContext.getInstance().getDiscoverUserSex();
		if (index == 0) {
			genderTxt.setText("性别(全部)");
		} else if (index == 1) {
			genderTxt.setText("性别(男)");
		} else if (index == 2) {
			genderTxt.setText("性别(女)");
		} else {
			genderTxt.setText("性别(全部)");
		}

		genderView = creatPullToRefreshListView();
		genderRefreshList = (PullToRefreshListView) genderView.findViewById(R.id.refreshList);

		final List<FilterVo> genders = new ArrayList<FilterVo>();
		FilterVo gf = new FilterVo(0, "全部", index == 0 ? true : false).setSexValue(null);
		genders.add(gf);
		gf = new FilterVo(1, "男", index == 1 ? true : false).setSexValue(0);
		genders.add(gf);
		gf = new FilterVo(2, "女", index == 2 ? true : false).setSexValue(1);
		genders.add(gf);
		genderAdapter = new BaseRadioFilterAdapter<FilterVo>(getContext(), genders);
		genderRefreshList.setAdapter(genderAdapter);
		genderRefreshList.setMode(Mode.DISABLED);
		genderRefreshList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position--;
				int size = genders.size();
				for (int i = 0; i < size; i++) {
					FilterVo filter = genders.get(i);
					if (i == position) {
						filter.selected = true;
					} else {
						filter.selected = false;
					}
				}
				if (position == 0) {
					selectedGender = null;
					genderTxt.setText("性别(全部)");
				} else if (position == 1) {
					selectedGender = 0;
					genderTxt.setText("性别(男)");
				} else if (position == 2) {
					selectedGender = 1;
					genderTxt.setText("性别(女)");
				}
				genderAdapter.notifyDataSetChanged();
				addPopView(0, genderRefreshList, genderTxt);
				if (mode == MODE_DISCOVER) {
					SystemContext.getInstance().setDiscoverUserSex(position);
					onRefreshData();
				} else {
					listener.onRefreshUser(selectedGender, selectedTime, getSelectServiceIds());
				}
			}
		});
	}

	/**
	 * 时间
	 */
	private void creatTimeListView() {
		int index = 0;
		if (mode == MODE_DISCOVER)
			index = SystemContext.getInstance().getDiscoverTime();
		if (index == 0) {
			if (mode == MODE_DISCOVER)
				timeTxt.setText("活跃时间(1小时)");
			else
				timeTxt.setText("活跃时间(不限)");
		} else if (index == 1) {
			timeTxt.setText("活跃时间(1天)");
		} else if (index == 2) {
			timeTxt.setText("活跃时间(3天)");
		} else if (index == 3) {
			timeTxt.setText("活跃时间(7天)");
		} else {
			timeTxt.setText("活跃时间(不限)");
		}
		timeView = creatPullToRefreshListView();
		timeRefreshList = (PullToRefreshListView) timeView.findViewById(R.id.refreshList);

		final List<FilterVo> times = new ArrayList<FilterVo>();
		FilterVo gf;
		if (mode == MODE_DISCOVER) {
			gf = new FilterVo(0, "1小时", index == 0 ? true : false).setTimeValue(60);
			times.add(gf);
		} else {
			gf = new FilterVo(0, "不限", index == 0 ? true : false);
			times.add(gf);
			gf = new FilterVo(0, "1小时", index == 1 ? true : false).setTimeValue(60);
			times.add(gf);
		}
		gf = new FilterVo(1, "1天", index == 1 ? true : false).setTimeValue(1440);
		times.add(gf);
		gf = new FilterVo(2, "3天", index == 2 ? true : false).setTimeValue(4320);
		times.add(gf);
		gf = new FilterVo(3, "7天", index == 3 ? true : false).setTimeValue(10080);
		times.add(gf);
		timeAdapter = new BaseRadioFilterAdapter<FilterVo>(getContext(), times);
		timeRefreshList.setAdapter(timeAdapter);
		timeRefreshList.setMode(Mode.DISABLED);
		timeRefreshList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position--;
				int size = times.size();
				for (int i = 0; i < size; i++) {
					FilterVo filter = times.get(i);
					if (i == position) {
						filter.selected = true;
					} else {
						filter.selected = false;
					}
				}
				if (mode == MODE_DISCOVER) {
					if (position == 0) {
						selectedTime = 60;
						timeTxt.setText("活跃时间(1小时)");
					} else if (position == 1) {
						selectedTime = 1440;
						timeTxt.setText("活跃时间(1天)");
					} else if (position == 2) {
						selectedTime = 4320;
						timeTxt.setText("活跃时间(3天)");
					} else if (position == 3) {
						selectedTime = 10080;
						timeTxt.setText("活跃时间(7天)");
					}
				} else {
					if (position == 0) {
						selectedTime = null;
						timeTxt.setText("活跃时间(不限)");
					} else if (position == 1) {
						selectedTime = 60;
						timeTxt.setText("活跃时间(1小时)");
					} else if (position == 2) {
						selectedTime = 1440;
						timeTxt.setText("活跃时间(1天)");
					} else if (position == 3) {
						selectedTime = 4320;
						timeTxt.setText("活跃时间(3天)");
					} else if (position == 4) {
						selectedTime = 10080;
						timeTxt.setText("活跃时间(7天)");
					}
				}
				timeAdapter.notifyDataSetChanged();
				addPopView(1, timeRefreshList, timeTxt);
				if (mode == MODE_DISCOVER) {
					SystemContext.getInstance().setDiscoverTime(position);
					onRefreshData();
				} else {
					listener.onRefreshUser(selectedGender, selectedTime, getSelectServiceIds());
				}
			}
		});
	}

	/**
	 * 
	 * @return
	 */
	protected PullToRefreshListView creatServicePullToRefreshListView() {
		seviceView = View.inflate(getContext(), R.layout.discover_filter_content_list, null);
		serviceNullBg = (TextView) seviceView.findViewById(R.id.nullTxt);
		serviceNullBg.setText("尚未找到服务器信息");
		serviceBottom = (LinearLayout) seviceView.findViewById(R.id.bottom);
		serviceBottom.setVisibility(View.VISIBLE);
		Button cannelBtn = (Button) seviceView.findViewById(R.id.cannelBtn);
		Button commitBtn = (Button) seviceView.findViewById(R.id.commitBtn);
		cannelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 重置
				if (serviceAdapter.data != null) {
					int size = serviceAdapter.data.size();
					for (int i = 0; i < size; i++) {
						FilterVo fvo = serviceAdapter.data.get(i);
						fvo.selected = false;
					}
					serviceAdapter.notifyDataSetChanged();
					getSelectServiceIds();
				}
			}
		});
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addPopView(2, seviceView, serviceTxt);
				setServiveNun();
				if (mode == MODE_DISCOVER) {
					getSelectServiceIds();
					onRefreshData();
				} else {
					listener.onRefreshUser(selectedGender, selectedTime, getSelectServiceIds());
				}
			}
		});
		return (PullToRefreshListView) seviceView.findViewById(R.id.refreshList);
	}

	/**
	 * 服务器
	 */
	private void creatServiceListView(boolean isadd) {
		serviceAdapter = new BaseMultipleFilterAdapter<FilterVo>(getContext(), new ArrayList<FilterVo>());
		serviceRefreshList = creatServicePullToRefreshListView();
		serviceRefreshList.setAdapter(serviceAdapter);
		serviceRefreshList.setMode(Mode.DISABLED);
		setServiceData(SystemContext.getInstance().getGameServices(), isadd);
	}

	/**
	 * 
	 * @param result
	 * @param isadd
	 */
	private void setServiceData(Msgs.UserGameServerResult result, boolean isadd) {
		serviceAdapter.data.clear();
		if (result != null) {
			List<Msgs.GameServerEntry> server = result.getEntryList();
			if (server != null && server.size() > 0) {
				// 添加全部
				boolean isall = true;
				FilterVo afvo = new FilterVo(0, "全部", isall, null, false);
				serviceAdapter.data.add(afvo);
				Set<String> sids = getServiceIds(mode == MODE_DISCOVER ? SystemContext.getInstance().getDiscoverUserService() : selectServiceIds);
				List<FilterVo> filters = new ArrayList<FilterVo>();
				int size = server.size();
				for (int i = 0; i < size; i++) {
					Msgs.GameServerEntry gvo = server.get(i);
					boolean select = sids.contains(gvo.getGid() + "-" + gvo.getId());
					FilterVo fvo = new FilterVo(i, gvo.getName(), select, null, false).setServiceKeyValue(gvo.getGid(), gvo.getId()).setSidValue(
							gvo.getId());
					filters.add(fvo);
					if (!select)
						isall = false;
				}
				afvo.selected = isall;
				serviceAdapter.data.addAll(filters);
				setServiveNun();
			}
		}
		serviceAdapter.notifyDataSetChanged();
		if (isadd)
			addPopView(2, seviceView, serviceTxt);
		if (serviceAdapter.data.size() > 0) {
			serviceBottom.setVisibility(View.VISIBLE);
			serviceRefreshList.setVisibility(View.VISIBLE);
			serviceNullBg.setVisibility(View.GONE);
		} else {
			serviceBottom.setVisibility(View.GONE);
			serviceRefreshList.setVisibility(View.GONE);
			serviceNullBg.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 
	 */
	private void setServiveNun() {
		// 0一个都没选 －1全选
		int num = 0;
		String firstSname = "";
		if (serviceAdapter.data != null && serviceAdapter.data.size() > 0) {
			int size = serviceAdapter.data.size();
			for (int i = 0; i < size; i++) {
				FilterVo fvo = serviceAdapter.data.get(i);
				if (fvo.selected) {
					num++;
					firstSname = fvo.name;
				}
			}
			if (num == size) {
				num = -1;
			}
		}
		if (num == 0) {
			serviceNunTxt.setVisibility(View.GONE);
			serviceTxt.setText("只看同服");
		} else if (num == -1) {
			serviceNunTxt.setVisibility(View.GONE);
			serviceTxt.setText("只看同服(全部)");
		} else {
			if (num > 1) {
				serviceTxt.setText("只看同服");
				serviceNunTxt.setVisibility(View.VISIBLE);
			} else {
				serviceTxt.setText(firstSname);
				serviceNunTxt.setVisibility(View.GONE);
			}
			serviceNunTxt.setText(num + "");
			if (num < 100) {
				serviceNunTxt.setBackgroundResource(R.drawable.discover_filter_num_bg_shap);
			} else {
				serviceNunTxt.setBackgroundResource(R.drawable.discover_filter_num_bg_shap2);
			}
		}
	}

	/**
	 * 设置服务器IDS
	 * 
	 * @param lists
	 * @return
	 */
	private String getSelectServiceIds() {
		if (serviceAdapter.data != null) {
			StringBuffer strBuf = new StringBuffer();
			int size = serviceAdapter.data.size();
			for (int i = 0; i < size; i++) {
				FilterVo fvo = serviceAdapter.data.get(i);
				if (fvo.selected && !TextUtils.isEmpty(fvo.serviceKey)) {
					strBuf.append(fvo.serviceKey);
					strBuf.append(",");
				}
			}
			if (strBuf.length() > 0) {
				String sg = strBuf.substring(0, strBuf.length() - 1);
				selectServiceIds = sg;
				if (mode == MODE_DISCOVER)
					SystemContext.getInstance().setDiscoverUserService(sg);
				return sg;
			} else {
				selectServiceIds = null;
				if (mode == MODE_DISCOVER)
					SystemContext.getInstance().setDiscoverUserService(null);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param ids
	 * @return
	 */
	private Set<String> getServiceIds(String ids) {
		Set<String> gs = new HashSet<String>();
		if (ids != null) {
			String[] idsarray = ids.split(",");
			for (int i = 0; i < idsarray.length; i++) {
				gs.add(idsarray[i]);
			}
		}
		return gs;
	}

	/**
	 * 
	 * @return
	 */
	public static Integer getSelectGenderByShare() {
		int sexIndex = SystemContext.getInstance().getDiscoverUserSex();
		switch (sexIndex) {
		case 0:
			return null;
		case 1:
			return 0;
		case 2:
			return 1;
		default:
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public static Integer getSelectTimeByShare() {
		int timeIndex = SystemContext.getInstance().getDiscoverTime();
		switch (timeIndex) {
		case 0:
			return 60;
		case 1:
			return 1440;
		case 2:
			return 4320;
		case 3:
			return 10080;
		default:
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.discover.ui.filter.FilterView#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		selectedGender = null;
		genderTxt.setText("性别(全部)");
		selectedTime = 10080;
		timeTxt.setText("活跃时间(7天)");
		serviceTxt.setText("只看同服");
		serviceNunTxt.setVisibility(View.GONE);
		// 初始化列表
		creatGenderListView();
		creatTimeListView();
		creatServiceListView(false);
	}

}
