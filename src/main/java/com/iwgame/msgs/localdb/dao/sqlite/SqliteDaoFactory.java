package com.iwgame.msgs.localdb.dao.sqlite;

import android.content.Context;

import com.iwgame.msgs.localdb.dao.AreaDao;
import com.iwgame.msgs.localdb.dao.ContentDao;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.localdb.dao.GamePackageDao;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.GroupGradeDao;
import com.iwgame.msgs.localdb.dao.GroupUserRelDao;
import com.iwgame.msgs.localdb.dao.MessageDao;
import com.iwgame.msgs.localdb.dao.PointTaskDao;
import com.iwgame.msgs.localdb.dao.RelationGameDao;
import com.iwgame.msgs.localdb.dao.ResourceDao;
import com.iwgame.msgs.localdb.dao.TopicTagDao;
import com.iwgame.msgs.localdb.dao.UserActionDao;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.localdb.dao.UserGradeDao;

public class SqliteDaoFactory extends DaoFactory {

	@Override
	public GameDao getGameDao(Context context) {
		GameDao dao = new SqliteGameDaoImpl(context);
		return dao;
	}

	@Override
	public GamePackageDao getGamePackageDao(Context context) {
		GamePackageDao dao = new SqliteGamePackageDaoImpl(context);
		return dao;
	}

	@Override
	public RelationGameDao getRelationGameDao(Context context) {
		RelationGameDao dao = new SqliteRelationGameDaoImpl(context);
		return dao;
	}

	@Override
	public UserDao getUserDao(Context context) {
		UserDao dao = new SqliteUserDaoImpl(context);
		return dao;
	}

	@Override
	public ContentDao getContentDao(Context context) {
		ContentDao dao = new SqliteContentDaoImpl(context);
		return dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.DaoFactory#getAreaDao(android.content.Context
	 * )
	 */
	@Override
	public AreaDao getAreaDao(Context context) {
		AreaDao dao = new SqliteAreaDaoImpl(context);
		return dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.DaoFactory#getResourceDao(android.content
	 * .Context)
	 */
	@Override
	public ResourceDao getResourceDao(Context context) {
		return new SqliteResourceDaoImpl(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.DaoFactory#getGroupDao(android.content.Context
	 * )
	 */
	@Override
	public GroupDao getGroupDao(Context context) {
		return new SqliteGroupDaoImpl(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.DaoFactory#getGroupUserRelDao(android.content
	 * .Context)
	 */
	@Override
	public GroupUserRelDao getGroupUserRelDao(Context context) {
		return new SqliteGroupUserRelDaoImpl(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.DaoFactory#getMessageDao(android.content.
	 * Context)
	 */
	@Override
	public MessageDao getMessageDao(Context context) {
		return new SqliteMessageDaoImpl(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.DaoFactory#getUserActionDao(android.content
	 * .Context)
	 */
	@Override
	public UserActionDao getUserActionDao(Context context) {
		return new SqliteUserActionDaoImpl(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.DaoFactory#getGroupGradeDao(android.content
	 * .Context)
	 */
	@Override
	public GroupGradeDao getGroupGradeDao(Context context) {
		return new SqliteGroupGradeDaoImpl(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.DaoFactory#getPointTaskDao(android.content
	 * .Context)
	 */
	@Override
	public PointTaskDao getPointTaskDao(Context context) {
		return new SqlitePointTaskDaoImpl(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.DaoFactory#getUserGradeDao(android.content
	 * .Context)
	 */
	@Override
	public UserGradeDao getUserGradeDao(Context context) {
		return new SqliteUserGradeDaoImpl(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.DaoFactory#getTopicTag(android.content.Context
	 * )
	 */
	@Override
	public TopicTagDao getTopicTag(Context context) {
		return new SqliteTopicTagDaoImpl(context);
	}

}
