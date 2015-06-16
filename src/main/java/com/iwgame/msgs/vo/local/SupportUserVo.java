package com.iwgame.msgs.vo.local;


/**
 * 赞列表 的实体对象
 * @author jczhang
 *
 */
public class SupportUserVo{
	private long uid;
	private String userName;
	private String avatar;
	private int sex;
	private int age;
	private int grade;
	private int commonCount;//共同关注的游戏数
	private String news;
	private String mood;
	private long time;//赞的时间
	
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getGrade() {
		return grade;
	}
	public void setGrade(int grade) {
		this.grade = grade;
	}
	public int getCommonCount() {
		return commonCount;
	}
	public void setCommonCount(int commonCount) {
		this.commonCount = commonCount;
	}
	public String getNews() {
		return news;
	}
	public void setNews(String news) {
		this.news = news;
	}
	public String getMood() {
		return mood;
	}
	public void setMood(String mood) {
		this.mood = mood;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}
