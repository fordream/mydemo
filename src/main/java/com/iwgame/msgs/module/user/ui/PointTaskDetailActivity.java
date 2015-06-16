package com.iwgame.msgs.module.user.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.PointTaskDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.cache.Cache;
import com.iwgame.msgs.module.cache.CacheCallBack;
import com.iwgame.msgs.module.cache.CacheImpl;
import com.iwgame.msgs.module.guide.GuideActivity;
import com.iwgame.msgs.module.setting.ui.PointMarketActivity;
import com.iwgame.msgs.module.user.adapter.ExpandableListviewAdapter;
import com.iwgame.msgs.module.user.object.UserPointTaskObj;
import com.iwgame.msgs.module.user.ui.widget.QQListView;
import com.iwgame.msgs.proto.Msgs.PostContent;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.GuideUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.PointTaskVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;


/**
 * @ClassName: PointTaskDetailActivity 
 * @Description: 轻松赚取积分的页面
 * @author jczhang
 * @date 2014-9-17 下午2:43:56 
 * @Version 1.0
 *
 */
public class PointTaskDetailActivity extends BaseActivity implements OnClickListener{

//	private TextView cur_your_integral;
	private CustomProgressDialog downloaddialog;
	private Dialog dialog;
	private int NEW_HAND_TASK_TYPE = 1;
	private int EVERY_DAY_TASK_TYPE = 2;
	private QQListView exList;
	private ExpandableListviewAdapter adapter;
	private ArrayList<List<Map<String, UserPointTaskObj>>> childs;
	private ArrayList<Map<String, String>> groups;
	SharedPreferences sp;
	private Dialog detailDialog;
	private TextView cue_words;
	private TextView other_cue_words;
	private PointTaskDao pointTaskDao;
    private List<PointTaskVo> taskList;
	private boolean flag = true;
	private LinearLayout pointAndExpGuid;
	private ImageView pointInfo;
	private ImageView marketInfo;
	private ImageView expInfo;
	private ImageView upGrade;
	private ImageView pointTask;
	private ImageView expTask;

	/**
	 * 最先执行的代码
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//GuideUtil.startGuide(this, GuideActivity.GUIDE_MODE_POINT_TASK);
		initial();//对界面上的一些控件，做一些初始化
		setView();//初始化dialog
		setData();//显示新手任务和每日任务
		setListener();//给界面上的按钮设置监听器
	}


	/**
	 * 给界面中的按钮
	 * 设置事件监听
	 */
	private void setListener() {
		/**
		 * 当点击expandablelistview的子item的时候，
		 * 则进行相应的处理
		 * 一，领取积分 
		 * 二、跳转到相应的页面
		 */
		exList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View view, int groupPosition,
					int childPosition, long id) {
				if (childs != null) {
					UserPointTaskObj obj = childs.get(groupPosition)
							.get(childPosition).get("taskobj");
//					popDialog(obj);
					
					showDetail(obj,groupPosition,childPosition,childs.size());
				}
				return true;
			}
		});
	}
	
	
	
	protected void showDetail(final UserPointTaskObj obj,int groupPosition,int childPosition,int size){
		Intent intent = new Intent(PointTaskDetailActivity.this, PointTaskDetailPageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("tid", obj.getPointTask().getTaskid());
		bundle.putString("tname", obj.getPointTask().getTaskname());
		bundle.putString("tdesc", obj.getPointTask().getTaskdesc());
		bundle.putInt("point", obj.getPointTask().getPoint());
		bundle.putInt("type", obj.getPointTask().getType());
		bundle.putInt("exp", obj.getPointTask().getExp());
		bundle.putInt("toid", obj.getPointTask().getToid());
		bundle.putString("detail", obj.getPointTask().getDetail());
		bundle.putInt("exptimes", obj.getTimes());
		bundle.putInt("group", groupPosition);
		bundle.putInt("child", childPosition);
		bundle.putInt("size", size);
		intent.putExtras(bundle);
		startActivity(intent);
	}


	/**
	 * 弹出提示框
	 * 当点击详情按钮的
	 
	 * @param obj
	 */
	protected void popDialog(UserPointTaskObj obj) {
			cue_words.setText(""+obj.getPointTask().getDetail());
			other_cue_words.setVisibility(View.GONE);
			detailDialog.show();
	}


	/**
	 * 初始化控件
	 */
	private void setView() {
		dialog = new Dialog(PointTaskDetailActivity.this,R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		downloaddialog = CustomProgressDialog.createDialog(this, false);
		downloaddialog.show();
	}


	/**
	 * 初始化数据
	 */
	private void setData() {
		groups = new ArrayList<Map<String, String>>();
		childs = new ArrayList<List<Map<String, UserPointTaskObj>>>();
		for(int i = 0;i < 2; i ++ ){
			if(i == 0){
				Map<String, String> group = new HashMap<String, String>();   
				group.put("groupname", "新手任务");  
				groups.add(group); 
				groups.clear();
			}else if(i == 1){
				Map<String, String> group = new HashMap<String, String>();   
				group.put("groupname", "每日任务");  
				groups.add(group); 
			}
			List<Map<String, UserPointTaskObj>> child = new ArrayList<Map<String, UserPointTaskObj>>();  
			childs.add(child);
		}
		int grade = SystemContext.getInstance().getExtUserVo().getGrade();
		adapter = new ExpandableListviewAdapter(this, exList, groups, childs, null,downloaddialog,dialog,grade);
		exList.setAdapter(adapter);
	}


	/**
	 * 在onresum这个方法里面
	 * 把当前的积分显示出来
	 * 获取任务，如果当前的数据库里面能够获取到任务
	 * 则从数据库里面获取，
	 * 如果获取不到任务，则去同步任务到数据库中
	 */
	@Override
	protected void onResume() {
		super.onResume();
		synctask();
	}

	/**
	 * 获取用户的
	 * 积分信息
	 * 将积分数值显示到界面上
	 */
//	private void getPoint() {
//		int point = SystemContext.getInstance().getPoint();
//		if(point > 0){
//			cur_your_integral.setText(point+"");
//		}else{
//			ProxyFactory.getInstance().getUserProxy().getUserPoint(new ProxyCallBack<List<ExtUserVo>>() {
//				@Override
//				public void onSuccess(List<ExtUserVo> result) {
//					if(result != null && result.size() == 1){
//						int point = result.get(0).getPoint();
//						cur_your_integral.setText(point+"");
//						SystemContext.getInstance().setPoint(point);
//					}else{
//						cur_your_integral.setText(0+"");
//						SystemContext.getInstance().setPoint(0);
//					}
//				}
//				@Override
//				public void onFailure(Integer result, String resultMsg) {
//				}
//			}, this, SystemContext.getInstance().getExtUserVo().getUserid()+"");
//		}
//	}

	
	
	/**
	 * 初始化操作
	 */
	private void initial() {
		//当有新的任务上架的时候删除消息
		ProxyFactory.getInstance().getMessageProxy().delByChannelTypeAndCategory(MsgsConstants.MC_PUB, MsgsConstants.MCC_INFO, MsgsConstants.RESOURCE_APP_TYPE_TASK+"");
		pointTaskDao = DaoFactory.getDaoFactory().getPointTaskDao(SystemContext.getInstance().getContext());
		sp = getSharedPreferences("sync", MODE_PRIVATE);
		setTitleTxt(getString(R.string.easy_get_integral));
		View view = View.inflate(PointTaskDetailActivity.this, R.layout.point_task_list, null);
		getContentView().addView(view);
//		cur_your_integral = (TextView)view.findViewById(R.id.cur_your_integral);
		exList = (QQListView)getContentView().findViewById(R.id.home_expandableListView);
		exList.setHeaderView(getLayoutInflater().inflate(R.layout.group_header,exList,false));
		View footView = getLayoutInflater().inflate(R.layout.point_task_guide,exList, false);
		pointTask =(ImageView)footView.findViewById(R.id.point_task_image);
		pointTask.setOnClickListener(this);
		expTask =(ImageView)footView.findViewById(R.id.exp_task_image);
		expTask.setOnClickListener(this);
		pointInfo=(ImageView)footView.findViewById(R.id.skip_point_info);
		pointInfo.setOnClickListener(this);
		marketInfo=(ImageView)footView.findViewById(R.id.shop_market);
		marketInfo.setOnClickListener(this);
		expInfo= (ImageView)footView.findViewById(R.id.exp_value_info);
		expInfo.setOnClickListener(this);
		upGrade=(ImageView)footView.findViewById(R.id.up_grade_info);
		upGrade.setOnClickListener(this);
		exList.addFooterView(footView);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
		detailDialog = new Dialog(this, R.style.SampleTheme_Light);
		detailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View v = View.inflate(this, R.layout.dialog_integral, null);
		TextView i_know_it = (TextView)v.findViewById(R.id.i_know_it);
		pointAndExpGuid = (LinearLayout)footView.findViewById(R.id.guide_get_point_exp);
		i_know_it.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				detailDialog.dismiss();
			}
		});
		cue_words = (TextView)v.findViewById(R.id.cue_words);
		other_cue_words = (TextView)v.findViewById(R.id.other_cue_words);
		other_cue_words.setVisibility(View.VISIBLE);
		detailDialog.setContentView(v);
	}


	/**
	 * 同步任务
	 * 把任务同步下来保存到数据库里面
	 */
	private void synctask() {
		
		//首先去同步数据
		ProxyFactory.getInstance().getUserProxy().getPointTaskDetail(new ProxyCallBack<List<PointTaskVo>>() {
			@Override
			public void onSuccess(List<PointTaskVo> result) {
				Editor et = sp.edit();
				et.putBoolean("issync", true);
				et.commit();
				if(result == null || result.size() <= 0){
					downloaddialog.dismiss();
				}else{
					addContent();//同步了数据后在去获取新手任务和每日任务
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				handleErrorCode(result, resultMsg);
			}
		}, PointTaskDetailActivity.this);
	}


	/**
	 * 首先去服务器
	 * 获取新手任务
	 */
	protected void addContent() {
		//显示去获取数据的loading效果
		if(flag){
			flag = false;
			//downloaddialog.show();
		}
		/**
		 * 下面的这个方法是去获取新手任务
		 */
		ProxyFactory.getInstance().getUserProxy().getPointTask(new ProxyCallBack<List<UserPointTaskObj>>() {

			@Override
			public void onSuccess(List<UserPointTaskObj> result) {
				//三、获取每日任务
				getDayTask(result);
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				handleErrorCode(result, resultMsg);
				getDayTask(null);
			}
		}, PointTaskDetailActivity.this, NEW_HAND_TASK_TYPE,1);
	}


	/**
	 * 接下来的这个方法
	 * 是去获取每日任务
	 */
	private void getDayTask(final List<UserPointTaskObj> result1){
		/**
		 * 这个方法是去获取每日任务
		 */
		ProxyFactory.getInstance().getUserProxy().getPointTask(new ProxyCallBack<List<UserPointTaskObj>>() {

			@Override
			public void onSuccess(List<UserPointTaskObj> result) {

				showData(result1, result);
				//取消dialog
				downloaddialog.dismiss();
				pointAndExpGuid.setVisibility(View.VISIBLE);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				//处理错误码
				handleErrorCode(result, resultMsg);
				showData(result1, null);
				//取消dialog
				downloaddialog.dismiss();
			}
		}, PointTaskDetailActivity.this, EVERY_DAY_TASK_TYPE,1);
	}

	/**
	 * 将获取到的
	 * 新手任务
	 * 每日任务
	 * 显示到手机界面
	 */
	private void showData(List<UserPointTaskObj> result1,List<UserPointTaskObj> result2){
		groups.clear();
		childs.clear();
		boolean flag = true;//判断新手任务是否已经全部完成
		if(result1 != null && result1.size() > 0){
			//将数据添加到集合里面
			Map<String, String> group = new HashMap<String, String>();   
			group.put("groupname", "新手任务");  
			groups.add(group); 
			List<Map<String, UserPointTaskObj>> child = new ArrayList<Map<String, UserPointTaskObj>>(); 
			int length = result1.size();
			for(int i = 0; i < length;i++){
				UserPointTaskObj obj = result1.get(i);
				if(!setRoleItemVisible()&&obj.getPointTask().getToid()==14){
					break;
				}
				Map<String, UserPointTaskObj> childdata = new HashMap<String, UserPointTaskObj>();
				childdata.put("taskobj", obj);
				child.add(childdata);
				if(obj.getStatus() != 3){
					flag = false;
				}
			}
			childs.add(child);
			if(flag){
				groups.clear();
				childs.clear();
			}
		}else{
			groups.clear();
			childs.clear();
		}
		//下面的这段代码是将每日任务添加到集合里面去，用于后面的代码显示出来
		Map<String, String> group = new HashMap<String, String>();   
		group.put("groupname", "每日任务");  
		groups.add(group); 
		List<Map<String, UserPointTaskObj>> child = new ArrayList<Map<String, UserPointTaskObj>>(); 
		//设置适配器
		if(result2 != null && result2.size() > 0){
			int size = result2.size();
			for(int i = 0;i < size;i++){
				Map<String, UserPointTaskObj> childdata = new HashMap<String, UserPointTaskObj>();
				UserPointTaskObj obj = result2.get(i);
				childdata.put("taskobj", obj);
				child.add(childdata);
			}
		}
		childs.add(child);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
		//下面的这个代码的作用是 当获取到了数据后，第一次进入的时候  都展开列表，并标明其状态
		for(int i = 0;i < groups.size(); i++){
			exList.expandGroup(i);
			adapter.setGroupClickStatus(i, 1);
		}
		int index = exList.getFirstVisiblePosition();
		View v =exList.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		exList.setSelectionFromTop(index, top);
	}
	
	/**
	 * 设置角色模块是否显示
	 */
	public boolean setRoleItemVisible(){
		AppConfig config = AdaptiveAppContext.getInstance().getAppConfig();
		return config.isShowRoleList();
	}
	/**
	 * 当请求数据失败了以后
	 * 返回来是执行onfailure方法时
	 * 在这个方法里面处理这个错误码
	 * @param i
	 * @param msg
	 */
	private void handleErrorCode(Integer i,String msg){
		ErrorCodeUtil.handleErrorCode(PointTaskDetailActivity.this, i, msg);
	}

	/**
	 * 在当前界面被销毁的时候
	 * 要释放广播资源
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		sp.edit().remove("issync").commit();
	}

	
	/**
	 * 在这个方法里面
	 * 取消所弹出来的对话框 
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if(dialog != null) dialog.dismiss();
		if(detailDialog != null)detailDialog.dismiss();
	}


	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.point_task_image:
		case R.id.skip_point_info:
		case R.id.shop_market:
			Intent intent = new Intent(PointTaskDetailActivity.this, PointMarketActivity.class);
			startActivity(intent);
			break;
		case R.id.exp_task_image:
		case R.id.exp_value_info:
		case R.id.up_grade_info:
			Intent gradeIntent = new Intent(PointTaskDetailActivity.this, UserGradePolicyActivity.class);
			startActivity(gradeIntent );
			break;
		}
	}
}
