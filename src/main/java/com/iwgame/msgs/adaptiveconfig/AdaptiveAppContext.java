/**      
 * CropImageUI.java Create on 2014-7-14     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */
package com.iwgame.msgs.adaptiveconfig;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceActivity;

import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.utils.BitmapUtil;

/**
 * 
 * @ClassName: AdaptiveAppContext 
 * @Description: 版本适配配置
 * @author yhxu
 * @date 2014-12-5 上午11:26:07 
 * @Version 1.0
 *
 */
public class AdaptiveAppContext {
	private static final String SYNC_KEY = "SYNC_KEY";

	private static AdaptiveAppContext instance;
	
	/**
	 * 构造方法
	 */
	private AdaptiveAppContext() {
	}
	
	/**
	 * app配置文件json字符串
	 */
	private String appConfigString;
	
	/**
	 * app配置文件，区分游伴和攻略版本
	 */
	private AppConfig appConfig;
	
	private SharedPreferences mAppConfigSharedPreferences;
	
	public static boolean isHasGetAppConfigFromNet = false;
	
	/**
	 *  标题背景图片
	 */
	private Drawable conmon_title_bg;

	public static AdaptiveAppContext getInstance() {
		synchronized (SYNC_KEY) {
			if (instance == null)
				instance = new AdaptiveAppContext();
			return instance;
		}
	}
	
	// 读系统全局配置文件
	public SharedPreferences getAppConfigSharedPreferences() {
		if (this.mAppConfigSharedPreferences == null) {
			this.mAppConfigSharedPreferences = SystemContext.getInstance().getContext().getSharedPreferences(SystemConfig.SHAREDPREFERENCES_NAME_APP_CONFIG, PreferenceActivity.MODE_PRIVATE);
		}
		return mAppConfigSharedPreferences;
	}
	
	public String getAppConfigString(){
		if(appConfigString == null){
			appConfigString = getAppConfigSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_APPCONFIG_STRING, null);
		}
		return appConfigString;
	}
	
	public void setAppConfigString(String appConfigString){
		this.appConfigString = appConfigString;
		getAppConfigSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_APPCONFIG_STRING, appConfigString).commit();
	}
	
	public void setAppConfig(AppConfig appConfig){
		this.appConfig =appConfig;
	}
	
	/**
	 * 读取app适配配置文件
	 * @return
	 */
	public AppConfig getAppConfig(String appConfigString) {
		if(appConfigString != null && !appConfigString.isEmpty()){
			AppConfig config = new AppConfig();
			try {
				JSONObject object = new JSONObject(appConfigString);
				parseAppConfig(object, config);
				return config;
			} catch (JSONException e) {
				e.printStackTrace();
				return  null;
			} catch (Exception e) {
				e.printStackTrace();
				return  null;
			}
		}else{
			return null;
		}
	}
	
	
	public AppConfig getAppConfig() {
		if(appConfig == null){
			try {
				InputStream abpath = getClass().getResourceAsStream("/assets/" +"rule");
				String respString = new String(readStream(abpath));
				JSONObject object = new JSONObject(respString);
				appConfig = new AppConfig();
				parseAppConfig(object, appConfig);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return appConfig;
	}
	
	private void parseAppConfig(JSONObject object, AppConfig appConfig) throws JSONException,Exception{
		//类型和流程
		appConfig.setAppType(object.getString("apptype"));
		appConfig.setApp_name(object.getString("appname"));
		appConfig.setDivisionurl(object.getString("divisionurl"));
		appConfig.setGameId(object.getLong("gid"));
		appConfig.setStartup(object.getInt("startup"));
		appConfig.setShowRoleList(object.getBoolean("showRoleList"));
		
		JSONObject recbarmsgObj =  object.getJSONObject("recbarmsg");
		appConfig.setRecbarmsg(recbarmsgObj.getBoolean("open"));
		appConfig.setRecbarmsgPackage(recbarmsgObj.getString("pkg"));
		appConfig.setRecbarmsgUrl(recbarmsgObj.getString("url"));
		
		//消息
		JSONObject msg = object.getJSONObject("msg");
		JSONObject remindObj =  msg.getJSONObject("remind");
		appConfig.setMsgremindCheck(remindObj.getBoolean("check"));
		appConfig.setMsgremindpkg(remindObj.getString("pkg"));
		
		//发现
		JSONObject discoverObject =  object.getJSONObject("discover");
		appConfig.setDiscover_user(discoverObject.getBoolean("user"));
		appConfig.setDiscover_group(discoverObject.getBoolean("group"));
		appConfig.setDiscover_game(discoverObject.getBoolean("game"));
		
		//贴吧
		JSONObject gameObject = object.getJSONObject("game");
		appConfig.setGame_model(gameObject.getInt("model"));
		appConfig.setGame_doMain(gameObject.getString("domain"));
		appConfig.setGame_guide(gameObject.getBoolean("guide"));
		
		//用户
		JSONObject userObject = object.getJSONObject("user");
		appConfig.setMode(userObject.getInt("mode"));
		appConfig.setUser_defaultSelect(userObject.getInt("defaultSelect"));
		String menuContent = userObject.getString("menu");
		JSONArray array = new JSONArray(menuContent);
		FollowMenuVo vo;
		JSONObject jobj;
		List<FollowMenuVo> list = new ArrayList<FollowMenuVo>();
		for(int i = 0; i < array.length(); i ++){
			jobj = array.getJSONObject(i);
			vo = new FollowMenuVo();
			vo.setGid(jobj.getLong("gid"));
			vo.setMenuName(jobj.getString("name"));
			vo.setShow(jobj.getBoolean("show"));
			list.add(vo);
		}
		appConfig.setMenuVoList(list);
		
		//添加好友
		JSONObject addfriendObject = object.getJSONObject("addfriend");
		appConfig.setAddfriend_fate(addfriendObject.getBoolean("fate"));
		
		//我
		JSONObject settingObject = object.getJSONObject("setting");
		appConfig.setSetting_logout(settingObject.getBoolean("logout"));
		appConfig.setSettingString(settingObject.getString("noBundingMes"));
		//绑定手机
		JSONObject bundingObject = object.getJSONObject("bunding");
		appConfig.setBundingLoginBTIsShow(bundingObject.getBoolean("bundingLoginBTIsShow"));
		
		//登录
		JSONObject loginObject = object.getJSONObject("login");
		appConfig.setLogin_backButtonIsShow(loginObject.getBoolean("backButtonIsShow"));
		
		//UI展示
		JSONObject displayObject = object.getJSONObject("display");
		appConfig.setDisplay_type(displayObject.getInt("type"));
		appConfig.setDisplay_height(displayObject.getInt("height"));
		JSONObject display_topObject = displayObject.getJSONObject("atop");
		JSONObject display_top_titleObject = display_topObject.getJSONObject("title");
		appConfig.setDisplay_title_textcolor(display_top_titleObject.getInt("textcolor"));
		appConfig.setDisplay_title_textsize(display_top_titleObject.getInt("textsize"));
		JSONObject display_top_descObject = display_topObject.getJSONObject("desc");
		appConfig.setDisplay_top_desc_text(display_top_descObject.getString("text"));
		appConfig.setDisplay_top_desc_textcolor(display_top_descObject.getInt("textcolor"));
		appConfig.setDisplay_top_desc_textsize(display_top_descObject.getInt("textsize"));
		JSONObject display_loadingObject = displayObject.getJSONObject("loading");
		appConfig.setDisplay_loading_text(display_loadingObject.getString("text"));
		appConfig.setDisplay_loading_textcolor(display_loadingObject.getInt("textcolor"));
		JSONObject display_webviewObject = displayObject.getJSONObject("webview");
		appConfig.setDisplay_webview_bgcolor(display_webviewObject.getInt("backgroundcolor"));
		
		//分享
		JSONObject shareObject = object.getJSONObject("share");
		JSONObject share_titleObject = shareObject.getJSONObject("title");
		appConfig.setShare_title_addqqfriend(share_titleObject.getString("addqqfriend"));
		appConfig.setShare_title_qzone(share_titleObject.getString("qzone"));
		appConfig.setShare_tilte_weixin(share_titleObject.getString("weixin"));
		JSONObject share_siteObject = shareObject.getJSONObject("site");
		appConfig.setShare_site_text(share_siteObject.getString("text"));
		appConfig.setShare_site_url(share_siteObject.getString("url"));
		//陪玩
		JSONObject playObject =object.getJSONObject("play");
		appConfig.setMoreServer(playObject.getJSONArray("multiservergids"));
		// 解析版本配置
		JSONObject versionObject = object.getJSONObject("v");
		SystemConfig.C_VERSION_CODE = versionObject.getInt("c");
		SystemConfig.UC_VERSION_CODE = versionObject.getInt("uc");
		SystemConfig.VERSION_DESC = versionObject.getString("d");
		SystemConfig.VERSION_DOWNLOAD_URL = versionObject.getString("u");
	}
	
	/**
	 * 读取配置文件
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	private byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	public Drawable getConmon_title_bg() {
		if(conmon_title_bg == null){
			conmon_title_bg = BitmapUtil.getDrawableFromAsstes(SystemContext.getInstance().getContext(), "common_title_bg.png");
		}
		return conmon_title_bg;
	}

	public void setConmon_title_bg(Drawable conmon_title_bg) {
		this.conmon_title_bg = conmon_title_bg;
	}
	
}
