package com.iwgame.msgs.module.setting.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.iwgame.msgs.common.ImageCacheLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.setting.ui.GoodsDetailActivity;
import com.iwgame.msgs.module.setting.vo.ChangeRecordsEntity;
import com.iwgame.msgs.module.setting.vo.OrderDetail;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.SimpleDateFormateUtil;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.youban.msgs.R;
import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


/**
 * 兑换记录的适配器
 * @author jczhang
 *
 */
public class ChangeRecordsAdapter extends BaseAdapter {

	private Context context;
	private List<ChangeRecordsEntity> list;
	private CustomProgressDialog dialog;
	private Dialog detailDialog;
	
	
	public ChangeRecordsAdapter(Context context,List<ChangeRecordsEntity> list,Dialog detailDialog){
		this.context = context;
		if(list != null){
			this.list = list;
		}else{
			list = new ArrayList<ChangeRecordsEntity>();
		}
		dialog = CustomProgressDialog.createDialog(context, true);
		if(detailDialog != null){
			this.detailDialog = detailDialog;
			this.detailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}else{
			this.detailDialog = new Dialog(context,R.style.SampleTheme_Light);
			this.detailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ChangeRecordsEntity getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return list.get(arg0).getId();
	}

	@Override
	public View getView(final int poisition, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.change_records_item_view, null);
			holder.icon = (ImageView)convertView.findViewById(R.id.change_record_icon);
			holder.goodsName = (TextView)convertView.findViewById(R.id.goods_name);
			holder.changeDate = (TextView)convertView.findViewById(R.id.fetch_date);
			holder.cosumePoint = (TextView)convertView.findViewById(R.id.cosume_point);
			holder.orderDetail = (TextView)convertView.findViewById(R.id.order_detail);
			holder.goodsState = (TextView)convertView.findViewById(R.id.goods_trans_state);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final ViewHolder viewHolder = holder;
		AlarmManager mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		mAlarmManager.setTimeZone("GMT+08:00");
		final ChangeRecordsEntity entity = getItem(poisition);
		ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(entity.getIcon()), holder.icon, R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
		holder.goodsName.setText(entity.getGoodsName());
		//领取时间是怎么得到 
		holder.changeDate.setText("领取时间:"+SimpleDateFormateUtil.switchToDate(entity.getTransTime())+" "+SimpleDateFormateUtil.toTimeNoscecond(entity.getTransTime()));
		holder.cosumePoint.setText(entity.getNeedPrice()+"");
		holder.orderDetail.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		int deliveryStatus = 0;//表示已经发货
		if(entity.getDeliveryType() == 1 && entity.getDeliveryTime() <= 0) {
			deliveryStatus = 1; // 表示未发货
		}
		entity.setStatus(deliveryStatus);
		if(entity.getStatus() == 0){
			holder.goodsState.setText("已发货");
		}else{
			holder.goodsState.setText("待发货");
		}
		//弹出订单详情
		holder.orderDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				getOrderDetail(entity, viewHolder);
			}
		});
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context,GoodsDetailActivity.class);
				intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_ID, getItem(poisition).getGoodsId());
				intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_FLAG, false);
				context.startActivity(intent);
			}
		});
		return convertView;
	}
	
	/**
	 * 获取订单详情
	 * @param entity
	 */
	protected void getOrderDetail(final ChangeRecordsEntity entity, final ViewHolder holder) {
	    dialog.show();
		ProxyFactory.getInstance().getUserProxy().getOrderDetail(new ProxyCallBack<OrderDetail>() {

			@Override
			public void onSuccess(OrderDetail result) {
				showDialog(entity,result,holder);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
			}
		}, context, entity.getId(), entity.getDeliveryType());
	}

	/**
	 * 根据不同的商品状态
	 * 弹出不同的对话框
	 * @param entity
	 * @param result
	 */
	protected void showDialog(ChangeRecordsEntity entity, OrderDetail result, ViewHolder holder) {
		if(result.getStatus() == 0){
			entity.setStatus(0);
			holder.goodsState.setText("已发货");
			showAutoDialog(entity,result);
		}else if(result.getStatus() == 1){
			entity.setStatus(1);
			holder.goodsState.setText("待发货");
			showUnfillOrder(entity,result);
		}else if(result.getStatus() == 2){
			entity.setStatus(0);
			holder.goodsState.setText("已发货");
			showDeliveryDialog(entity,result);
		}
	}

	/**
	 * 弹出已发货的模板
	 * @param entity
	 * @param result
	 */
	private void showDeliveryDialog(final ChangeRecordsEntity entity,
		    final OrderDetail res) {

		new AsyncTask<String, Integer, String>(){

			@Override
			protected String doInBackground(String... arg0) {
				String url = SystemContext.getInstance().getTemplateDirIp()+"/"+entity.getDeliveryDetail()+".json";
				String templateContent = getTemplate(url);
				return templateContent;
			}
			
			protected void onPostExecute(String result) {
				try {
					dialog.dismiss();
					View view = View.inflate(context, R.layout.dialog_pop, null);
					Button cancelBtn = (Button)view.findViewById(R.id.cannelBtn);
					TextView title = (TextView)view.findViewById(R.id.title);
					title.setText("发货信息");
					cancelBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							detailDialog.dismiss();
						}
					});
					LinearLayout ll = (LinearLayout)view.findViewById(R.id.content);
					ll.removeAllViews();
					detailDialog.setContentView(view);
					JSONObject object = new JSONObject(result);
					String templateDetail = object.getString("templateDetail");
					JSONArray array = new JSONArray(templateDetail);
					LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					String[]s = res.getOrderInfo().split("%");
					int length = array.length();
					for(int i = 0; i < length; i++){
						JSONObject obj = array.getJSONObject(i);
						View view1 = View.inflate(context, R.layout.template_item_view, null);
						TextView title1 = (TextView)view1.findViewById(R.id.show_title);
						TextView content = (TextView)view1.findViewById(R.id.show_content);
						try {
							title1.setText(obj.getString("detailItem")+":");
							content.setText(""+s[i]);
						} catch (Exception e) {
							e.printStackTrace();
							content.setText("");
						}
						ll.addView(view1, params);
					}
					detailDialog.show();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.execute();

	}





	/**
	 * 获取模板
	 * @param url
	 * @return
	 */
	private String getTemplate(String ur){
		 String result = null;
	        HttpURLConnection connection = null;
	        InputStreamReader in = null;
	        try {
	            URL url = new URL(ur);
	            connection = (HttpURLConnection) url.openConnection();
	            in = new InputStreamReader(connection.getInputStream());
	            BufferedReader bufferedReader = new BufferedReader(in);
	            StringBuffer strBuffer = new StringBuffer();
	            String line = null;
	            while ((line = bufferedReader.readLine()) != null) {
	                strBuffer.append(line);
	            }
	            result = strBuffer.toString();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (connection != null) {
	                connection.disconnect();
	            }
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	 
	        }
	        return result;
	    }

	
	/**
	 * 弹出还未发货的对话框
	 * @param entity
	 * @param result
	 */
	private void showUnfillOrder(final ChangeRecordsEntity entity, final OrderDetail res) {
			new AsyncTask<String, Integer, String>(){

				@Override
				protected String doInBackground(String... arg0) {
					String url = SystemContext.getInstance().getTemplateDirIp()+"/"+entity.getTransDetail()+".json";
					String templateContent = getTemplate(url);
					return templateContent;
				}
				
				protected void onPostExecute(String result) {
					try {
						dialog.dismiss();
						View view = View.inflate(context, R.layout.dialog_pop, null);
						LinearLayout ll = (LinearLayout)view.findViewById(R.id.content);
						TextView title = (TextView)view.findViewById(R.id.title);
						title.setText("兑换信息");
						ll.setOrientation(LinearLayout.VERTICAL);
						Button cancelBtn = (Button)view.findViewById(R.id.cannelBtn);
						cancelBtn.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								detailDialog.dismiss();
							}
						});
						ll.removeAllViews();
						detailDialog.setContentView(view);
						JSONObject object = new JSONObject(result);
						String templateDetail = object.getString("templateDetail");
						JSONArray array = new JSONArray(templateDetail);
						LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						String[]s = res.getOrderInfo().split("%");
						int length = array.length();
						for(int i = 0; i < length; i++){
							JSONObject obj = array.getJSONObject(i);
							View view1 = View.inflate(context, R.layout.template_item_view, null);
							TextView title1 = (TextView)view1.findViewById(R.id.show_title);
							TextView content = (TextView)view1.findViewById(R.id.show_content);
							try {
								title1.setText(obj.getString("detailItem")+":");
								content.setText(""+s[i]);
							} catch (Exception e) {
								e.printStackTrace();
								content.setText("");
							}
							ll.addView(view1, params);
						}
						detailDialog.show();
					} catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						
					}
				};
			}.execute();
	}





	/**
	 * 弹出自动发货的对话框
	 */
	private void showAutoDialog(ChangeRecordsEntity entity, OrderDetail result) {
		dialog.dismiss();
		View view = View.inflate(context, R.layout.dialog_integral, null);
		detailDialog.setContentView(view);
		TextView tv = (TextView)view.findViewById(R.id.cue_words);
		tv.setTextColor(context.getResources().getColor(R.color.global_color7));
		Button cancelBtn = (Button)view.findViewById(R.id.i_know_it);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				detailDialog.dismiss();
			}
		});
		tv.setText("领取成功，"+result.getOrderInfo());
		detailDialog.show();
	}



	/*
	 * 定义一个
	 * 句柄类
	 */
	static class ViewHolder{
		ImageView icon;
		TextView goodsName;
		TextView changeDate;
		TextView cosumePoint;
		TextView orderDetail;
		TextView goodsState;
	}
	
}
