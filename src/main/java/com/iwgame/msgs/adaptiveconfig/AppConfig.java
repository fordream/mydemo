/**      
 * CropImageUI.java Create on 2014-7-14     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */
package com.iwgame.msgs.adaptiveconfig;

import java.util.List;

import org.json.JSONArray;

/**
 * 
 * @ClassName: AppConfig 
 * @Description: App配置文件，区别游伴和攻略 
 * @author Administrator
 * @date 2014-12-4 上午10:54:26 
 * @Version 1.0
 *
 */
public class AppConfig {
	//App类型，游伴或者攻略
	private String appType;
	//抽奖活动地址
	private String divisionurl;
	//流程（1游伴流程2攻略流程）
	private int startup = 1;
	//判断推荐贴吧信息点击是否打开或下载游伴 false不需要true需要
	private boolean recbarmsg = false;
	//判断推荐贴吧信息点击是否打开或下载游伴 app的package
	private String recbarmsgPackage = null;
	//判断推荐贴吧信息点击是否打开或下载游伴 app的下载url
	private String recbarmsgUrl = null;
	//消息提醒是否检查
	private boolean msgremindCheck = false;
	//消息提醒检查的包
	private String msgremindpkg = null;
	//发现是否显示公会列表 false不显示true显示
	private boolean discover_group = true;
	//发现是否显示玩家列表 false不显示true显示
	private boolean discover_user = true;
	//发现是否显示贴吧列表 false不显示true显示
	private boolean discover_game = true;
	//贴吧显示模式 1显示原生贴吧模式（游伴）2html模式（攻略）
	private int game_model = 1;
	//html打开网页地址
	private String game_doMain;
	//是否需要添加引导页
	private boolean game_guide;
	//通讯录默认选择放置的选项0公会1关注2粉丝
	private int user_defaultSelect = 0;
	//是否显示缘分好友 false不显示true显示
	private boolean addfriend_fate = true;
	//退出登录按钮是否显示 false不显示true显示
	private boolean setting_logout = true;
	//mode 为2 的时候为单攻略，为1 的时候为游伴
	private int followMode;
	//关注下面的筛选菜单的集合
	private List<FollowMenuVo> menuVoList;
	//单攻略的id
	private long gameId;
	//底栏贴吧显示的模式 1正常2有弧顶（是否超过其他模块的Tab）
	private int display_type;
	//弧顶（超出的部分）的高度
	private int display_height;
	//标题的字体颜色
	private int display_title_textcolor = 0xffffff;
	//这个字符串表示的是游客进入到app的时候在设置界面 的绑定手机提示文字
	private String settingString;
	//标题的字体大小
	private int display_title_textsize = 20;
	//攻略首页描述文字
	private String display_top_desc_text;
	//攻略首页描述文字颜色
	private int display_top_desc_textcolor;
	//攻略首页描述文字大小
	private int display_top_desc_textsize;
	//启动页的版权信息字体内容
	private String display_loading_text;
	//启动页的版权信息字体颜色
	private int display_loading_textcolor;
	
	private int display_webview_bgcolor;
	//绑定手机页面是否登录按钮
	private boolean bundingLoginBTIsShow;
	//登录页面是否显示返回按钮
	private boolean login_backButtonIsShow;

	//邀请QQ好友的分享标题
	private String share_title_addqqfriend;
	//分享到QQ空间的标题
	private String share_title_qzone;
	//分享到微信的标题
	private String share_tilte_weixin;
	//分享到QQ空间配置：发布分享的网站名称
	private String share_site_text;
	//分享到QQ空间配置：发布分享的网站地址
	private String share_site_url;
	//app的名字
	private String app_name;
	//设置我的角色是否显示
	private boolean showRoleList;
	
	private JSONArray moreServer;
	
	/**
	 * 获取多服务器游戏ids
	 * @return
	 */
	public JSONArray getMoreServer() {
		return moreServer;
	}
	/**
	 * 设置支持多服务器ids
	 * @param moreServer
	 */
	public void setMoreServer(JSONArray moreServer) {
		this.moreServer = moreServer;
	}
	
	public boolean isShowRoleList() {
		return showRoleList;
	}
	public void setShowRoleList(boolean showRoleList) {
		this.showRoleList = showRoleList;
	}
	/**
	 * 获取当前app的名字
	 * @return
	 */
	public String getApp_name() {
		return app_name;
	}
	/**
	 * 设置app的名字
	 * @param app_name
	 */
	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}
	/**
	 * 获取app类型
	 * @return
	 */
	public String getAppType() {
		return appType;
	}
	/**
	 * 设置app类型
	 * @param appType
	 */
	public void setAppType(String appType) {
		this.appType = appType;
	}
	
	public String getDivisionurl() {
		return divisionurl;
	}
	public void setDivisionurl(String divisionurl) {
		this.divisionurl = divisionurl;
	}
	public int getFollowMode() {
		return followMode;
	}
	public void setFollowMode(int followMode) {
		this.followMode = followMode;
	}

	public String getSettingString() {
		return settingString;
	}
	public void setSettingString(String settingString) {
		this.settingString = settingString;
	}
	
	/**
	 * 获取启动流程
	 * @return
	 */
	public int getStartup() {
		return startup;
	}
	/**
	 * 设置启动流程
	 * @param startup
	 */
	public void setStartup(int startup) {
		this.startup = startup;
	}
	/**
	 * 获取推荐贴吧是否打开或下载游伴
	 * @return
	 */
	public boolean isRecbarmsg() {
		return recbarmsg;
	}
	/**
	 * 设置推荐贴吧是否打开或下载游伴
	 * @param recbarmsg
	 */
	public void setRecbarmsg(boolean recbarmsg) {
		this.recbarmsg = recbarmsg;
	}
	/**
	 * 获取发现界面公会列表是否显示
	 * @return
	 */
	public boolean isDiscover_group() {
		return discover_group;
	}
	/**
	 * 设置发现界面公会列表是否显示
	 * @param discover_group
	 */
	public void setDiscover_group(boolean discover_group) {
		this.discover_group = discover_group;
	}
	/**
	 * 获取发现界面玩家列表是否显示
	 * @return
	 */
	public boolean isDiscover_user() {
		return discover_user;
	}
	/**
	 * 设置发现界面玩家列表是否显示
	 * @param discover_group
	 */
	public void setDiscover_user(boolean discover_user) {
		this.discover_user = discover_user;
	}
	/**
	 * 获取发现界面贴吧列表是否显示
	 * @return
	 */
	public boolean isDiscover_game() {
		return discover_game;
	}
	/**
	 * 设置发现界面贴吧列表是否显示
	 * @param discover_group
	 */
	public void setDiscover_game(boolean discover_game) {
		this.discover_game = discover_game;
	}
	/**
	 * 获取贴吧显示模式
	 * @return
	 */
	public int getGame_model() {
		return game_model;
	}
	/**
	 * 设置贴吧显示模式
	 * @param game_model
	 */
	public void setGame_model(int game_model) {
		this.game_model = game_model;
	}
	/**
	 * 获取攻略首页访问地址
	 * @return
	 */
	public String getGame_doMain() {
		return game_doMain;
	}
	/**
	 * 设置攻略首页访问地址
	 * @param game_doMain
	 */
	public void setGame_doMain(String game_doMain) {
		this.game_doMain = game_doMain;
	}
	public boolean isGame_guide() {
		return game_guide;
	}
	public void setGame_guide(boolean game_guide) {
		this.game_guide = game_guide;
	}
	/**
	 * 获取通讯录默认选择放置的选项
	 * @return
	 */
	public int getUser_defaultSelect() {
		return user_defaultSelect;
	}
	/**
	 * 设置通讯录默认选择放置的选项
	 * @param user_defaultSelect
	 */
	public void setUser_defaultSelect(int user_defaultSelect) {
		this.user_defaultSelect = user_defaultSelect;
	}
	/**
	 * 获取是否显示缘分好友
	 * @return
	 */
	public boolean isAddfriend_fate() {
		return addfriend_fate;
	}
	/**
	 * 设置是否显示缘分好友
	 * @param addfriend_fate
	 */
	public void setAddfriend_fate(boolean addfriend_fate) {
		this.addfriend_fate = addfriend_fate;
	}
	/**
	 * 获取是否显示退出按钮
	 * @return
	 */
	public boolean isSetting_logout() {
		return setting_logout;
	}
	/**
	 * 设置是否显示退出按钮
	 * @param setting_logout
	 */
	public void setSetting_logout(boolean setting_logout) {
		this.setting_logout = setting_logout;
	}
	public String getRecbarmsgPackage() {
		return recbarmsgPackage;
	}
	public void setRecbarmsgPackage(String recbarmsgPackage) {
		this.recbarmsgPackage = recbarmsgPackage;
	}
	public String getRecbarmsgUrl() {
		return recbarmsgUrl;
	}
	public void setRecbarmsgUrl(String recbarmsgUrl) {
		this.recbarmsgUrl = recbarmsgUrl;
	}
	public boolean isMsgremindCheck() {
		return msgremindCheck;
	}
	public void setMsgremindCheck(boolean msgremindCheck) {
		this.msgremindCheck = msgremindCheck;
	}
	public String getMsgremindpkg() {
		return msgremindpkg;
	}
	public void setMsgremindpkg(String msgremindpkg) {
		this.msgremindpkg = msgremindpkg;
	}
	public int getDisplay_type() {
		return display_type;
	}
	public void setDisplay_type(int display_type) {
		this.display_type = display_type;
	}
	public int getDisplay_height() {
		return display_height;
	}
	public void setDisplay_height(int display_height) {
		this.display_height = display_height;
	}
	public int getDisplay_title_textcolor() {
		return display_title_textcolor;
	}
	public void setDisplay_title_textcolor(int display_title_textcolor) {
		this.display_title_textcolor = display_title_textcolor;
	}
	public float getDisplay_title_textsize() {
		return display_title_textsize;
	}
	public void setDisplay_title_textsize(int display_title_textsize) {
		this.display_title_textsize = display_title_textsize;
	}
	public String getDisplay_top_desc_text() {
		return display_top_desc_text;
	}
	public void setDisplay_top_desc_text(String display_top_desc_text) {
		this.display_top_desc_text = display_top_desc_text;
	}
	public int getDisplay_top_desc_textcolor() {
		return display_top_desc_textcolor;
	}
	public void setDisplay_top_desc_textcolor(int display_top_desc_textcolor) {
		this.display_top_desc_textcolor = display_top_desc_textcolor;
	}
	public int getDisplay_top_desc_textsize() {
		return display_top_desc_textsize;
	}
	public String getDisplay_loading_text() {
		return display_loading_text;
	}
	public void setDisplay_loading_text(String display_loading_text) {
		this.display_loading_text = display_loading_text;
	}
	public int getDisplay_loading_textcolor() {
		return display_loading_textcolor;
	}
	public void setDisplay_loading_textcolor(int display_loading_textcolor) {
		this.display_loading_textcolor = display_loading_textcolor;
	}
	public int getDisplay_webview_bgcolor() {
		return display_webview_bgcolor;
	}
	public void setDisplay_webview_bgcolor(int display_webview_bgcolor) {
		this.display_webview_bgcolor = display_webview_bgcolor;
	}
	public void setDisplay_top_desc_textsize(int display_top_desc_textsize) {
		this.display_top_desc_textsize = display_top_desc_textsize;
	}public long getGameId() {
		return gameId;
	}
	public void setGameId(long gameId) {
		this.gameId = gameId;
	}
	public List<FollowMenuVo> getMenuVoList() {
		return menuVoList;
	}
	public void setMenuVoList(List<FollowMenuVo> menuVoList) {
		this.menuVoList = menuVoList;
	}
	public int getMode() {
		return followMode;
	}
	public void setMode(int mode) {
		this.followMode = mode;
	}
	public boolean isBundingLoginBTIsShow() {
		return bundingLoginBTIsShow;
	}
	public void setBundingLoginBTIsShow(boolean bundingLoginBTIsShow) {
		this.bundingLoginBTIsShow = bundingLoginBTIsShow;
	}
	public boolean isLogin_backButtonIsShow() {
		return login_backButtonIsShow;
	}
	public void setLogin_backButtonIsShow(boolean login_backButtonIsShow) {
		this.login_backButtonIsShow = login_backButtonIsShow;
	}
	public String getShare_title_addqqfriend() {
		return share_title_addqqfriend;
	}
	public void setShare_title_addqqfriend(String share_title_addqqfriend) {
		this.share_title_addqqfriend = share_title_addqqfriend;
	}
	public String getShare_title_qzone() {
		return share_title_qzone;
	}
	public void setShare_title_qzone(String share_title_qzone) {
		this.share_title_qzone = share_title_qzone;
	}
	public String getShare_tilte_weixin() {
		return share_tilte_weixin;
	}
	public void setShare_tilte_weixin(String share_tilte_weixin) {
		this.share_tilte_weixin = share_tilte_weixin;
	}
	public String getShare_site_text() {
		return share_site_text;
	}
	public void setShare_site_text(String share_site_text) {
		this.share_site_text = share_site_text;
	}
	public String getShare_site_url() {
		return share_site_url;
	}
	public void setShare_site_url(String share_site_url) {
		this.share_site_url = share_site_url;
	}
}
