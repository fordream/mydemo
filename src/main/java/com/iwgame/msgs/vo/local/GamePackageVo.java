package com.iwgame.msgs.vo.local;

import java.io.Serializable;

public class GamePackageVo  implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5552100746829574369L;
	
	private long id ;
	private long packageid ;
	private long gameid ;
	private String packagename ;
	private String downloadurl ;
	private String type ;
	private String dev ;
	private String version ;
	private long filesize ;
	private String desc ;
	private String screenshot ;
	private String gamename;
	private int status;
	private long utime;
	private String gameicon;
	private String publisher;
	private int platform;
	private long createtime;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getPackageid() {
		return packageid;
	}
	public void setPackageid(long packageid) {
		this.packageid = packageid;
	}
	public long getGameid() {
		return gameid;
	}
	public void setGameid(long gameid) {
		this.gameid = gameid;
	}
	public String getPackagename() {
		return packagename;
	}
	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}
	public String getDownloadurl() {
		return downloadurl;
	}
	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDev() {
		return dev;
	}
	public void setDev(String dev) {
		this.dev = dev;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public long getFilesize() {
		return filesize;
	}
	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getScreenshot() {
		return screenshot;
	}
	public void setScreenshot(String screenshot) {
		this.screenshot = screenshot;
	}
	
	public GamePackageVo()
	{
		
	}
	
	public GamePackageVo(long packageid,long gameid ,String packagename,String downloadurl
			,String type ,String dev ,String version ,long filesize ,String desc ,String screenshot)
	{
		
		this.packageid = packageid ;
		this.gameid = gameid ;
		this.packagename = packagename ;
		this.downloadurl = downloadurl ;
		this.type = type ;
		this.dev = dev ;
		this.version = version ;
		this.filesize =  filesize;
		this.desc = desc ;
		this.screenshot = screenshot ;
	}
	
	/**
	 * @return the gamename
	 */
	public String getGamename() {
		return gamename;
	}
	/**
	 * @param gamename the gamename to set
	 */
	public void setGamename(String gamename) {
		this.gamename = gamename;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the utime
	 */
	public long getUtime() {
		return utime;
	}
	/**
	 * @param utime the utime to set
	 */
	public void setUtime(long utime) {
		this.utime = utime;
	}
	/**
	 * @return the gameicon
	 */
	public String getGameicon() {
		return gameicon;
	}
	/**
	 * @param gameicon the gameicon to set
	 */
	public void setGameicon(String gameicon) {
		this.gameicon = gameicon;
	}
	/**
	 * @return the publisher
	 */
	public String getPublisher() {
		return publisher;
	}
	/**
	 * @param publisher the publisher to set
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	/**
	 * @return the platform
	 */
	public int getPlatform() {
		return platform;
	}
	/**
	 * @param platform the platform to set
	 */
	public void setPlatform(int platform) {
		this.platform = platform;
	}
	/**
	 * @return the createtime
	 */
	public long getCreatetime() {
		return createtime;
	}
	/**
	 * @param createtime the createtime to set
	 */
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	@Override
	public String toString() {
		return "GamePackageVo [id=" + id + ", packageid=" + packageid
				+ ", gameid=" + gameid + ", packagename=" + packagename
				+ ", downloadurl=" + downloadurl + ", type=" + type + ", dev="
				+ dev + ", version=" + version + ", filesize=" + filesize
				+ ", desc=" + desc + ", screenshot=" + screenshot + "]";
	}
	
	

}
