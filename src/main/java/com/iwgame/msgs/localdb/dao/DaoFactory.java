package com.iwgame.msgs.localdb.dao;

import android.content.Context;

import com.iwgame.msgs.localdb.dao.sqlite.SqliteDaoFactory;

public abstract class DaoFactory {

	public abstract GameDao getGameDao(Context context);

	public abstract GamePackageDao getGamePackageDao(Context context);

	public abstract RelationGameDao getRelationGameDao(Context context);

	public abstract UserDao getUserDao(Context context);

	public abstract ContentDao getContentDao(Context context);


	/**
	 * 获取地区DAO
	 * 
	 * @param context
	 * @return
	 */
	public abstract AreaDao getAreaDao(Context context);
	
	/**
	 * 获取资源DAO
	 * @param context
	 * @return
	 */
	public abstract ResourceDao getResourceDao(Context context);
	/**
	 * 获取公会
	 * @param context
	 * @return
	 */
	public abstract GroupDao getGroupDao(Context context);

	/**
	 * 获取公会用户关系
	 * @param context
	 * @return
	 */
	public abstract GroupUserRelDao getGroupUserRelDao(Context context);
	
	public abstract MessageDao getMessageDao(Context context);
	
	public abstract UserActionDao getUserActionDao(Context context);
	
	public abstract UserGradeDao getUserGradeDao(Context context);
	
	public abstract GroupGradeDao getGroupGradeDao(Context context);
	
	public abstract PointTaskDao getPointTaskDao(Context context);
	
	public abstract TopicTagDao getTopicTag(Context context);
	
	public static DaoFactory getDaoFactory() {

		return new SqliteDaoFactory();
	}
}
