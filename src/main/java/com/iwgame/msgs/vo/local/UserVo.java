package com.iwgame.msgs.vo.local;

import java.io.Serializable;

public class UserVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 672756235278667449L;
	// 内部
	private long id;
	// 用户id
	private long userid;
	// 用户名称
	private String username;
	// 头像
	private String avatar;
	// 用户与当前登录用户的关系（正向关系）
	private int relPositive = -1;
	// 当前登录用户与该用户的关系（正向关系）
	private int relInverse = -1;
	// 等级
	private int grade;
	// 性别
	private int sex = -1;
	// 城市
	private String city;

	private String mood;
	// 描述
	private String description;
	// 更新时间
	private long updatetime;
	// 编号
	private long serial;
	// 年龄
	private int age = -1;
	// 职业
	private String job;
	// 贴吧时长
	private String gameTime;
	// 喜欢贴吧类型
	private String likeGameType;
	// 手机号码
	private String mobile;
	// 手机号码名称
	private String mobileName;
	// 微博账号
	private String weibo;
	// 微博账号名称
	private String weiboName;
	//用户积分
	private int point;
	//用户经验值
	private int exp;
	//活跃时间
	private long atime;
	//备注名
	private String remarksName;
	//群片名
	private String gremarksName;
	//是否游客
	private int isGuest;

	public String getRemarksName() {
		return remarksName;
	}

	public void setRemarksName(String remarksName) {
		this.remarksName = remarksName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getRelPositive() {
		return relPositive;
	}

	public void setRelPositive(int relPositive) {
		this.relPositive = relPositive;
	}

	public int getRelInverse() {
		return relInverse;
	}

	public void setRelInverse(int relInverse) {
		this.relInverse = relInverse;
	}

	public long getSerial() {
		return serial;
	}

	public void setSerial(long serial) {
		this.serial = serial;
	}

	/**
	 * @return the avatar
	 */
	public String getAvatar() {
		return avatar;
	}

	/**
	 * @param avatar
	 *            the avatar to set
	 */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	/**
	 * @return the grade
	 */
	public int getGrade() {
		return grade;
	}

	/**
	 * @param grade
	 *            the grade to set
	 */
	public void setGrade(int grade) {
		this.grade = grade;
	}

	/**
	 * @return the sex
	 */
	public int getSex() {
		return sex;
	}

	/**
	 * @param sex
	 *            the sex to set
	 */
	public void setSex(int sex) {
		this.sex = sex;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the mood
	 */
	public String getMood() {
		return mood;
	}

	/**
	 * @param mood
	 *            the mood to set
	 */
	public void setMood(String mood) {
		this.mood = mood;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the updatetime
	 */
	public long getUpdatetime() {
		return updatetime;
	}

	/**
	 * @param updatetime
	 *            the updatetime to set
	 */
	public void setUpdatetime(long updatetime) {
		this.updatetime = updatetime;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return the job
	 */
	public String getJob() {
		return job;
	}

	/**
	 * @param job
	 *            the job to set
	 */
	public void setJob(String job) {
		this.job = job;
	}
	
	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public UserVo(long userid, String username, String avatar, int relPositive, int relInverse, int grade, int sex, String city, String mood,
			String description, long createtime, long serial, int age, String job) {
		this.userid = userid;
		this.username = username;
		this.avatar = avatar;
		this.relPositive = relPositive;
		this.relInverse = relInverse;
		this.grade = grade;
		this.sex = sex;
		this.city = city;
		this.mood = mood;
		this.description = description;
		this.updatetime = createtime;
		this.serial = serial;
		this.age = age;
		this.job = job;
	}

	public UserVo() {
		// TODO Auto-generated constructor stub
	}

	public String getGameTime() {
		return gameTime;
	}

	public void setGameTime(String gameTime) {
		this.gameTime = gameTime;
	}

	public String getLikeGameType() {
		return likeGameType;
	}

	public void setLikeGameType(String likeGameType) {
		this.likeGameType = likeGameType;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public String getMobileName() {
		return mobileName;
	}

	public void setMobileName(String mobileName) {
		this.mobileName = mobileName;
	}

	public String getWeiboName() {
		return weiboName;
	}

	public void setWeiboName(String weiboName) {
		this.weiboName = weiboName;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public long getAtime() {
		return atime;
	}

	public void setAtime(long atime) {
		this.atime = atime;
	}
	
	public int getIsGuest() {
		return isGuest;
	}

	public void setIsGuest(int isGuest) {
		this.isGuest = isGuest;
	}

	public String getGremarksName() {
		return gremarksName;
	}

	public void setGremarksName(String gremarksName) {
		this.gremarksName = gremarksName;
	}

	@Override
	public String toString() {
		return "UserVo [id=" + id + ", userid=" + userid + ", username=" + username + ", avatar=" + avatar + ", relPositive=" + relPositive
				+ ", relInverse=" + relInverse + ", grade=" + grade + ", sex=" + sex + ", city=" + city + ", mood=" + mood + ", description="
				+ description + ", updatetime=" + updatetime + ", serial=" + serial + ", age=" + age + ", job=" + job + "]";
	}

}
