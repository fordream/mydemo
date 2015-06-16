/**      
 * CommonUserAdapter.java Create on 2014-4-24     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.adapter;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ShareTaskUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: CommonUserAdapter
 * @Description: 通用用户适配器(暂时用于通讯录联系人邀请)
 * @author 王卫
 * @date 2014-4-24 下午4:18:50
 * @Version 1.0
 * 
 */
public class CommonUserAdapter extends BaseAdapter {

	private final String TAG = "CommonUserAdapter";
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Object> mData = null;

	private int mMode;
	public static final int CONTACT_FRIEND = 0;
	public static final int WEIBO_FRIEND = 1;
    private UserVo userVo;
	private Dialog dialog;//定义成全局的  并添加了get 和 set方法 
	
	public Dialog getDialog() {
		return dialog;
	}
	
	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

	public CommonUserAdapter(Context context, List<Object> data, int mode) {
		mContext = context;
		mData = data;
		mMode = mode;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		userVo = SystemContext.getInstance().getExtUserVo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if (mData != null)
			return mData.size();
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		if (mData != null)
			return mData.get(position);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		InviteViewHolder ivh = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.user_invite_list_item, null);
			ivh = new InviteViewHolder();
			ivh.nickname = (TextView) convertView.findViewById(R.id.nickname);
			ivh.submitTxt = (TextView) convertView.findViewById(R.id.submitTxt);
			convertView.setTag(ivh);
		} else {
			ivh = (InviteViewHolder) convertView.getTag();
		}
		renderInviteView(position, ivh);
		return convertView;
	}

	/**
	 * 渲染
	 * 
	 * @param position
	 */
	private void renderInviteView(int position, InviteViewHolder holder) {
		Object obj = getItem(position);
		if (obj != null && obj instanceof UserObject) {
			final UserObject userObject = (UserObject) obj;
			if (mMode == CONTACT_FRIEND) {
				holder.nickname.setText(userObject.getContactName());
				holder.submitTxt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						inviteContact(mContext, userObject.getMobile());
					}
				});
			} else if (mMode == WEIBO_FRIEND) {
				holder.nickname.setText(userObject.getWeiboName());
				holder.submitTxt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						inviteWeibo(mContext, userObject.getWeibo());
					}
				});
			}
		}
	}
	
	/**
	 * 显示发送短信
	 */
	private void showSendMsmView(final String mobile,final String sUrl) {
		dialog = new Dialog(mContext, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		final EditText txt = new EditText(mContext);
		txt.setTextColor(mContext.getResources().getColor(R.color.darkgray));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		//txt.setText(nickname.getText());
		txt.setBackgroundResource(R.drawable.common_edit_text_bg);
		txt.setText(mContext.getString(R.string.user_contact_invite_msg_content, userVo.getSerial() + "", sUrl));
		content.setPadding(DisplayUtil.dip2px(mContext, 10), 0, DisplayUtil.dip2px(mContext, 10), 0);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		//InputFilterUtil.lengthFilter(mContext, txt, 16, mContext.getString(R.string.nickname_verify_fail, 16));
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("是否发送短信邀请该好友：");
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				String message = txt.getText().toString().trim();
				/** 手机号码 与输入内容 必需不为空 **/  
			    if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(message)) { 
			    	sendMessage(mobile, message);
			    }  
				
				
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private void sendMessage(final String mobile, final String message){
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";  
		Intent sentIntent = new Intent(SENT_SMS_ACTION);  
		PendingIntent sentPI = PendingIntent.getBroadcast(mContext, 0, sentIntent, 0);  
		// register the Broadcast Receivers  
		mContext.registerReceiver(new BroadcastReceiver() {  
			@Override  
			public void onReceive(Context _context, Intent _intent) {  
				switch (getResultCode()) {  
				case Activity.RESULT_OK:  
					Toast.makeText(mContext, "短信发送成功", Toast.LENGTH_SHORT).show();  
					//邀请通讯录好友
					ShareTaskUtil.makeShareTask(mContext, TAG, userVo.getUserid(), MsgsConstants.OT_ADREESSBOOK, MsgsConstants.OP_INVITE_FRIEND, "Contactfriend", mobile, null);
					break;  
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:  
					break;  
				case SmsManager.RESULT_ERROR_RADIO_OFF:  
					break;  
				case SmsManager.RESULT_ERROR_NULL_PDU:  
					break;  
				}  
			}  
		}, new IntentFilter(SENT_SMS_ACTION)); 


		//直接调用短信接口发短信  
		SmsManager smsManager = SmsManager.getDefault();  

		if(message.length() > 70){
			List<String> divideContents = smsManager.divideMessage(message);    
			for (String text : divideContents) {    
				smsManager.sendTextMessage(mobile, null, text, sentPI, null);    
			} 
		}else{
			smsManager.sendTextMessage(mobile, null, message, sentPI, null);  
		}

		
	}
	
	/**
	 * 邀请（通讯录）
	 * 
	 * @param context
	 * @param mobile
	 * @param content
	 */
	private void inviteContact(final Context context, final String mobile) {
		final long uid = userVo.getUserid();
		ProxyFactory.getInstance().getUserProxy().shareForShortUrl(new ProxyCallBack<String>() {

			@Override
			public void onSuccess(String result) {
				final String shareurl = result;//ShareUtil.getShareUrl2(uid, ShareUtil.MARKET_WEIXIN, ShareUtil.TYPE_USER);
				if(shareurl != null && !shareurl.isEmpty()){
					showSendMsmView(mobile, shareurl);
				}else{
					ToastUtil.showToast(context, "服务端异常");
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ToastUtil.showToast(context, "服务端异常");
			}
		}, context, uid, 1, 0, 0);
	}

	/**
	 * 
	 * @param context
	 * @param mobile
	 */
	private void sendMessage(final Context context, final String mobile, String url) {
		if (url != null && !url.isEmpty()) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("smsto:" + mobile));
			intent.putExtra("sms_body",
					mContext.getString(R.string.user_contact_invite_msg_content,userVo.getSerial() + "", url));
			context.startActivity(intent);
			ProxyFactory
					.getInstance()
					.getUserProxy()
					.userAction(new ProxyCallBack<Integer>() {

						@Override
						public void onSuccess(Integer result) {
							switch (result) {
							case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
								LogUtil.e(TAG, "邀请行为动作提交成功");
								break;
							default:
								LogUtil.e(TAG, "邀请行为动作提交失败");
								break;
							}
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							LogUtil.e(TAG, "邀请行为动作提交失败");
						}
					}, context, userVo.getUserid(), MsgsConstants.OT_ADREESSBOOK, MsgsConstants.OP_INVITE_FRIEND,
							mobile, null,null);
		} else {
			ToastUtil.showToast(context, "邀请失败");
		}
	}

	/**
	 * 邀请微博好友
	 * 
	 * @param context
	 * @param mobile
	 * @param content
	 */
	private void inviteWeibo(final Context context, final String toUid) {
		ToastUtil.showToast(context, "该功能暂未开放!");
	}

	/**
	 * 邀请好友HOLDER
	 * 
	 * @ClassName: InviteViewHolder
	 * @Description: TODO(...)
	 * @author 王卫
	 * @date 2014-4-24 下午4:28:32
	 * @Version 1.0
	 * 
	 */
	class InviteViewHolder {
		TextView nickname;
		TextView submitTxt;
	}

}
