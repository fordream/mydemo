package com.iwgame.msgs.common;

import android.graphics.Bitmap;

public class ShareDate {
	private int shareType;//分享类型（分享、邀请好友）
	private long targetId;//目标ID
	private long targerSerialId;//目标SerialID
	private int targetType;//分享目标类型
	private int inTargetType;//内部分享目标类型，详见SystomConfig文件中定义
	private String targetName;//目标名称
	private String title;//标题
	private String titleUrl;//标题超链接
	private String text;//文本
	private String site;//应用地址
	private String siteUrl;	//应用地址url
	private String imagePath;// 图片路径
	private String imageUrl;// 图片URL
	private Bitmap imageData;// 图片对象
	private String shareUrl;// 分享链接
	
	private String tempString;//备用字段，传入贴吧名称（暂仅分享帖子时使用）
	private int detailType;//个人资料类型 0:自己 1：他人男 2、他人女 3、游客（分享个人资料时使用）
	private int tagId;//贴吧分享时传入的贴吧的标签ID，其中全部为0，精华为-1；默认全部
	
	public int getShareType() {
		return shareType;
	}
	public void setShareType(int shareType) {
		this.shareType = shareType;
	}
	public int getInTargetType() {
		return inTargetType;
	}
	public void setInTargetType(int inTargetType) {
		this.inTargetType = inTargetType;
	}
	public long getTargetId() {
		return targetId;
	}
	public long getTargerSerialId() {
		return targerSerialId;
	}
	public void setTargerSerialId(long targerSerialId) {
		this.targerSerialId = targerSerialId;
	}
	public void setTargetId(long targetId) {
		this.targetId = targetId;
	}
	public int getTargetType() {
		return targetType;
	}
	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}
	public String getTargetName() {
		return targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitleUrl() {
		return titleUrl;
	}
	public void setTitleUrl(String titleUrl) {
		this.titleUrl = titleUrl;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getSiteUrl() {
		return siteUrl;
	}
	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public Bitmap getImageData() {
		return imageData;
	}
	public void setImageData(Bitmap imageData) {
		this.imageData = imageData;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}
	public String getTempString() {
		return tempString;
	}
	public void setTempString(String tempString) {
		this.tempString = tempString;
	}
	public int getDetailType() {
		return detailType;
	}
	public void setDetailType(int detailType) {
		this.detailType = detailType;
	}
	public int getTagId() {
		return tagId;
	}
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	
}
