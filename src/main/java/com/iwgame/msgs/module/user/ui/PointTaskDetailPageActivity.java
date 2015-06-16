package com.iwgame.msgs.module.user.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.UserGradeDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.cache.Cache;
import com.iwgame.msgs.module.cache.CacheCallBack;
import com.iwgame.msgs.module.cache.CacheImpl;
import com.iwgame.msgs.module.setting.ui.EditDetailActivity;
import com.iwgame.msgs.module.user.object.UserPointTaskObj;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PostContent;
import com.iwgame.msgs.proto.Msgs.PostElementType;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.PointTaskVo;
import com.iwgame.msgs.vo.local.UserGradeVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.youban.msgs.R;


/**
 * @ClassName: PointTaskDetailPageActivity 
 * @Description: 任务详情页面
 * @author jczhang
 * @date 2014-9-17 下午2:43:56 
 * @Version 1.0
 *
 */
public class PointTaskDetailPageActivity extends BaseActivity implements OnClickListener {

	private TextView pointTaskTitle;
	private TextView pointTaskStatus;
	private TextView pointTaskCharacter;
	private TextView pointTaskPoint;
	private TextView pointTaskExp;
	private double gradePoint;
	private UserGradeVo userGradeVo;
	private UserGradeDao userGradeDao; 
	private UserVo userVo;
	private TextView pointTaskExplain1;
	private TextView pointTaskExplain2;
	private ImageView pointTaskExplainImg1;
	private ImageView pointTaskExplainImg2;
	
	private int tid;
	private String title;
	private String desc;
	private String detail;
	private int point;
	private int exp;
	private int type;
	private int toid;
	private PostContent postContent;
	private List<Msgs.PostElement> elementList;
	private LayoutInflater inflater;
	private LinearLayout taskExplain;
	private LinearLayout taskContent;
	private ImageView taskImage;
	private TextView taskName;
	private RelativeLayout taskSkip;
	private int groupPosition;
	
	private ArrayList<List<Map<String, UserPointTaskObj>>> childs;
	
	private ArrayList<List<Map<String, UserPointTaskObj>>> childsys;
	private ArrayList<Map<String, String>> groups;
	private int childPosition;
	private CustomProgressDialog downloaddialog;
	private View view;
	private int size;
	private boolean flag = false;
	/**
	 * 最先执行的代码
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initial();//对界面上的一些控件，做一些初始化
		 
		 setView();//设置加载dialog
		 //设置按钮的跳转
		inflater = this.getLayoutInflater();
	}
	/**
	 * 初始化控件
	 */
	private void setView() {
		downloaddialog = CustomProgressDialog.createDialog(this, false);
		view.setVisibility(View.GONE);
		downloaddialog.show();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getUserGrade();
		getData();//获取传过来的数据
		view.setVisibility(View.GONE);
		downloaddialog.show();
		getNetWork();//判断网络状况
		if(flag){
		view.setVisibility(View.GONE);
		addContent();//获取服务端的数据
		 taskSkip.setOnClickListener(this);
		setTaskContent(taskExplain,elementList);
		}else{
			view.setVisibility(View.VISIBLE);
			downloaddialog.dismiss();
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		flag = false;
	}
	/**
	 * 初始化操作
	 */
	private void initial() {
		
		
		// 显示左边
		setLeftVisible(true);
		// 添加右边功能按钮
		setRightVisible(false);
		titleTxt.setText("任务详情");
		groups = new ArrayList<Map<String, String>>();
		childs = new ArrayList<List<Map<String, UserPointTaskObj>>>();
		childsys = new ArrayList<List<Map<String, UserPointTaskObj>>>();
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		 view = View.inflate(this, R.layout.point_task_detail, null);
		contentView.addView(view, params);
		pointTaskTitle = (TextView)contentView.findViewById(R.id.point_task_title);
		pointTaskStatus = (TextView)contentView.findViewById(R.id.point_task_status);
		pointTaskCharacter = (TextView)view.findViewById(R.id.point_task_character);
		pointTaskPoint = (TextView)view.findViewById(R.id.point_task_point);
		pointTaskExp = (TextView)view.findViewById(R.id.point_task_exp);
		taskExplain = (LinearLayout)view.findViewById(R.id.task_explain_info);
		taskContent=(LinearLayout)view.findViewById(R.id.task_info_content);
		taskSkip=(RelativeLayout)view.findViewById(R.id.skip_task_content);
		taskImage=(ImageView)view.findViewById(R.id.skip_icon);
		taskName=(TextView)view.findViewById(R.id.skip_task_tv);
		view.setVisibility(View.GONE);
	}
	/**
	 * 设置积分任务文本显示状态
	 */
	public void setPointTask(int status,String taskName, int tid){
		if(status==3){
			if(type==1){
				pointTaskStatus.setText("已挣到该任务的" + point + "积分!");
			}else{
			pointTaskStatus.setText("今日已挣到该任务的" + point + "积分,明日再来!");
			}
			pointTaskStatus.setTextColor(this.getResources().getColor(R.color.point_task_status3));
		}else{
			pointTaskStatus.setTextColor(this.getResources().getColor(R.color.point_task_status1));
			if(type==2){
				pointTaskStatus.setText("该任务还未完成!");
			}else{
				pointTaskStatus.setText("还没有挣到该积分任务哦!");
			}
		}
		if(point == 0){
			pointTaskStatus.setText("该任务木有要领取的积分哦!");
			pointTaskPoint.setText( "无积分");
		}else{
			if(type==1) {
			pointTaskPoint.setText("+" + point + "积分");
			}else if(tid==2007){
				pointTaskPoint.setText("+" + point + "积分");
			}else{
				pointTaskPoint.setText("+" +(int) (point*gradePoint) + "积分");
			}
		}
		pointTaskTitle.setText(title);
		pointTaskCharacter.setText(type==1?"新手任务":"每日任务");
		pointTaskExp.setText("+" + exp + "经验值/每次");
	}
	/**
	 * 从缓存中取得存入的PostContent对象
	 * @param id
	 */
	public void getPostContent(final int id){
		
		CacheImpl.getInstance().getData(Cache.DATA_TYPE_TASK_POINTTASK, new CacheCallBack() {
			
			@Override
			public void onBack(Object result) {
				// TODO Auto-generated method stub
				HashMap<Integer,PointTaskVo> map = (HashMap<Integer, PointTaskVo>) result;
				if(map.get(id)!=null){
					postContent = map.get(id).getPostContent();
				}
			}
		});
	}
	/**
	 * 添加任务说明的显示内容
	 * @param pview
	 * @param data
	 */
	
	public void setTaskContent(LinearLayout pview, List<Msgs.PostElement> data){
		pview.removeAllViews();
		if(data.size()>0){
			taskContent.setVisibility(View.VISIBLE);
		for(int i=0;i<data.size();i++){
			final Msgs.PostElement element = data.get(i);
			if(element.getType() == PostElementType.PE_TEXT){
				TextView textView = new TextView(PointTaskDetailPageActivity.this);
				textView.setLineSpacing(3,(float) 1.2);
				textView.setTextColor(PointTaskDetailPageActivity.this.getResources().getColor(R.color.point_task_explain));
				textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						this.getResources().getDimensionPixelSize(R.dimen.global_font_size8));
				textView.setText(element.getText());
				textView.setTextColor(getResources().getColor(R.color.task_point_exp_values_color));
				pview.addView(textView);
			}
			else if(element.getType() == PostElementType.PE_IMAGE_ID_REF){
				LinearLayout view = (LinearLayout) this.inflater.inflate(R.layout.setting_point_task_more, pview, false);
				ImageView image = (ImageView) view
						.findViewById(R.id.setting_point_task_more_info);
//				ImageView image = new ImageView(PointTaskDetailPageActivity.this);
//				image.setTop(1);
				image.setBottom(2);
				new ImageLoader().loadRes(ResUtil.getOriginalRelUrl(element.getResourceId()), 0, image,
						R.drawable.postbar_thumbimg_default);
				pview.addView(view);
			//	pview.addView(image);
			}
		}
		}else{
			taskContent.setVisibility(View.INVISIBLE);
		}
	}
	
	public void getData(){
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			 tid = bundle.getInt("tid");
			 title = bundle.getString("tname");
			 desc = bundle.getString("tdesc");
			 detail = bundle.getString("detail");
			 point = bundle.getInt("point");
			 exp = bundle.getInt("exp");
			 type = bundle.getInt("type");
			 toid = bundle.getInt("toid");
			 groupPosition = bundle.getInt("group");
			 childPosition = bundle.getInt("child");
			 size=bundle.getInt("size");
			 getPostContent(tid);
			elementList = postContent.getElementsList();
			
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
		if(view.getId()==R.id.skip_task_content){
			
			if(toid==11){
				Intent intent = new Intent(PointTaskDetailPageActivity.this,BundPhoneActivity.class);
				startActivity(intent);
			}else if(toid==12){
				Intent intent = new Intent(PointTaskDetailPageActivity.this,EditDetailActivity.class);
				startActivity(intent);
			}else if(toid == 13){
				Intent intent = new Intent(PointTaskDetailPageActivity.this,UserAddActivity.class);
				startActivity(intent);
			}else if(toid == 4){
				Intent intent = new Intent(PointTaskDetailPageActivity.this, MainFragmentActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, 1);
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_INDEX, 2);
				intent.putExtras(bundle);
				startActivity(intent);
			}else if(toid == 2){
				Intent intent = new Intent(PointTaskDetailPageActivity.this, MainFragmentActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, 1);
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_INDEX, 0);
				intent.putExtras(bundle);
				startActivity(intent);
			}else if(toid == 14){
				Intent intent = new Intent(PointTaskDetailPageActivity.this, UserChooseGameActivity.class);
				startActivity(intent);
			}
		}
	}
	/**
	 * 设定跳转图标及文本显示
	 */
	public void setSkipView(int status){
		taskSkip.setClickable(true);
		if(toid==11){
			taskSkip.setVisibility(View.VISIBLE);
			taskImage.setImageResource(R.drawable.setting_bund_phone__selector);
			taskName.setText("绑定手机号>");
			if(status == 3){
				taskName.setText("已绑定手机号");
				taskSkip.setClickable(false);
			}
		}else if(toid == 12){
			taskSkip.setVisibility(View.VISIBLE);
			taskImage.setImageResource(R.drawable.setting_zi_liao__selector);
			taskName.setText("完善基本资料>");
			if(status==3){
				taskName.setText("已完善基本资料");
				taskSkip.setClickable(false);
			}
		}else if(toid == 13){
			taskSkip.setVisibility(View.VISIBLE);
			taskImage.setImageResource(R.drawable.setting_yao_qing__selector);
			taskName.setText("去邀请好友>");
		}else if(toid==4){
			taskSkip.setVisibility(View.VISIBLE);
			taskImage.setImageResource(R.drawable.add_game_role__selector);
			taskName.setText("加入公会");
			if(status==3){
				taskName.setText("已加入公会");
				taskSkip.setClickable(false);
			}
		}else if(toid==2){
			taskSkip.setVisibility(View.VISIBLE);
			taskImage.setImageResource(R.drawable.add_game_role__selector);
			taskName.setText("添加游伴好友");
			if(status==3){
				taskName.setText("已添加游伴好友");
				taskSkip.setClickable(false);
			}
		}else if(toid ==14){
			taskSkip.setVisibility(View.VISIBLE);
			taskImage.setImageResource(R.drawable.add_game_role__selector);
			taskName.setText("添加游戏角色");
			if(status==3){
				taskName.setText("已添加游戏角色");
				taskSkip.setClickable(false);
			}
		}
	}
	
	/**
	 * 首先去服务器
	 * 获取新手任务
	 */
	protected void addContent() {
		
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
		}, PointTaskDetailPageActivity.this, 1,1);
	}
	/**
	 * 接下来的这个方法
	 * 是去获取每日任务
	 */
	private void getDayTask(final List<UserPointTaskObj> result1){
		/**
		 * 这个方法是去获取每日任务
		 */
		downloaddialog.show();
		ProxyFactory.getInstance().getUserProxy().getPointTask(new ProxyCallBack<List<UserPointTaskObj>>() {

			@Override
			public void onSuccess(List<UserPointTaskObj> result) {

				setData(result1, result);
				downloaddialog.dismiss();
				view.setVisibility(View.VISIBLE);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				//处理错误码
				handleErrorCode(result, resultMsg);
				setData(result1, null);
				downloaddialog.dismiss();
				view.setVisibility(View.VISIBLE);
				//取消dialog
			}
		}, PointTaskDetailPageActivity.this, 2,1);
	}
	
	
	public void setData(List<UserPointTaskObj> result1,List<UserPointTaskObj> result2){
		if(childs.size()==2&&childs.get(0).size()!=childs.get(1).size()){
				showData(result1, result2);
				if(childs.size()!=2){
					UserPointTaskObj tobj= childsys.get(groupPosition).get(childPosition).get("taskobj");
					int  status=3;
					String taskName =tobj.getPointTask().getTaskname();
					int tid =tobj.getPointTask().getTaskid();
					if(childs.size()<=1){
						groupPosition = 1;
					}
					setSkipView(status);
					setPointTask(status,taskName, tid);
				}else{
					childsys.clear();
					showData(result1, result2);
					childsys.addAll(childs);
					UserPointTaskObj tobj= childsys.get(groupPosition).get(childPosition).get("taskobj");
					int  status=tobj.getStatus();
					String taskName =tobj.getPointTask().getTaskname();
					int tid =tobj.getPointTask().getTaskid();
					if(childs.size()<=1){
						groupPosition = 1;
					}
					setSkipView(status);
					setPointTask(status,taskName, tid);
				}
		}else {
			childsys.clear();
			showData(result1, result2);
			childsys.addAll(childs);
			UserPointTaskObj tobj= childsys.get(groupPosition).get(childPosition).get("taskobj");
			int  status=tobj.getStatus();
			String taskName =tobj.getPointTask().getTaskname();
			int tid =tobj.getPointTask().getTaskid();
			if(childs.size()<=1){
				groupPosition = 1;
			}
			setSkipView(status);
			setPointTask(status,taskName, tid);
		}
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
				Map<String, UserPointTaskObj> childdata = new HashMap<String, UserPointTaskObj>();
				UserPointTaskObj obj = result1.get(i);
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
	}
	
	public void getNetWork(){
		if(NetworkUtil.isNetworkAvailable(getApplicationContext())){
			flag  =true;
		}
	}
	
	public void getUserGrade(){
		userVo = SystemContext.getInstance().getExtUserVo();
		userGradeDao = DaoFactory.getDaoFactory().getUserGradeDao(SystemContext.getInstance().getContext());
		userGradeVo = userGradeDao.queryByGrade(userVo.getGrade());
		gradePoint = Double.parseDouble(userGradeVo.getMultiple());
	}
	/**
	 * 当请求数据失败了以后
	 * 返回来是执行onfailure方法时
	 * 在这个方法里面处理这个错误码
	 * @param i
	 * @param msg
	 */
	private void handleErrorCode(Integer i,String msg){
		ErrorCodeUtil.handleErrorCode(PointTaskDetailPageActivity.this, i, msg);
	}
}
