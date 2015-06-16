package com.iwgame.msgs.vo.local;

import java.io.Serializable;

public class GameVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3318375662837033355L;

	private long id;
	private long gameid;
	private String gamename;
	private String gamelogo;
	private long gamepackageid;
	private String type;
	private String publisher;
	private int like;
	private int dislike;
	private int mlike;
	private int mdislike;
	private int status;
	private long utime;
	private String desc;
	private long gputime;
	private int gtype;
	private long followtime;
    private int visitCount;
    
    
    
    
    
	public int getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getGameid() {
		return gameid;
	}

	public void setGameid(long gameid) {
		this.gameid = gameid;
	}

	public String getGamename() {
		return gamename;
	}

	public void setGamename(String gamename) {
		this.gamename = gamename;
	}

	public String getGamelogo() {
		return gamelogo;
	}

	public void setGamelogo(String gamelogo) {
		this.gamelogo = gamelogo;
	}

	public long getGamepackageid() {
		return gamepackageid;
	}

	public void setGamepackageid(long gamepackageid) {
		this.gamepackageid = gamepackageid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public GameVo() {

	}

	public GameVo(long gameid, String gamename, String gamelogo, long gamepackageid, String type) {
		this.gameid = gameid;
		this.gamename = gamename;
		this.gamelogo = gamelogo;
		this.gamepackageid = gamepackageid;
		this.type = type;
	}

	/**
	 * @return the publisher
	 */
	public String getPublisher() {
		return publisher;
	}

	/**
	 * @param publisher
	 *            the publisher to set
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	/**
	 * @return the like
	 */
	public int getLike() {
		return like;
	}

	/**
	 * @param like
	 *            the like to set
	 */
	public void setLike(int like) {
		this.like = like;
	}

	/**
	 * @return the dislike
	 */
	public int getDislike() {
		return dislike;
	}

	/**
	 * @param dislike
	 *            the dislike to set
	 */
	public void setDislike(int dislike) {
		this.dislike = dislike;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the mlike
	 */
	public int getMlike() {
		return mlike;
	}

	/**
	 * @param mlike
	 *            the mlike to set
	 */
	public void setMlike(int mlike) {
		this.mlike = mlike;
	}

	/**
	 * @return the mdislike
	 */
	public int getMdislike() {
		return mdislike;
	}

	/**
	 * @param mdislike
	 *            the mdislike to set
	 */
	public void setMdislike(int mdislike) {
		this.mdislike = mdislike;
	}

	/**
	 * @return the utime
	 */
	public long getUtime() {
		return utime;
	}

	/**
	 * @param utime
	 *            the utime to set
	 */
	public void setUtime(long utime) {
		this.utime = utime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getGputime() {
		return gputime;
	}

	public void setGputime(long gputime) {
		this.gputime = gputime;
	}

	public int getGtype() {
		return gtype;
	}

	public void setGtype(int gtype) {
		this.gtype = gtype;
	}

	public long getFollowtime() {
		return followtime;
	}

	public void setFollowtime(long followtime) {
		this.followtime = followtime;
	}

	@Override
	public String toString() {
		return "GameVo [id=" + id + ", gameid=" + gameid + ", gamename=" + gamename + ", gamelogo=" + gamelogo + ", gamepackageid=" + gamepackageid
				+ ", type=" + type + "]";
	}

}
