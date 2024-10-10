package org.mintdaniel42.starediscordbot.data.migration;

import io.avaje.inject.Prototype;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.Version;
import org.mintdaniel42.starediscordbot.data.dao.*;
import org.seasar.doma.jdbc.Config;

@Prototype
public class UnknownMigration implements Migration {
	@NonNull private final AchievementDao achievementDao;
	@NonNull private final GroupDao groupDao;
	@NonNull private final HNSUserDao hnsUserDao;
	@NonNull private final MetaDataDao metaDataDao;
	@NonNull private final PGUserDao pgUserDao;
	@NonNull private final RequestDao requestDao;
	@NonNull private final UserDao userDao;
	@NonNull private final ProfileDao profileDao;

	public UnknownMigration(@NonNull final Config config) {
		achievementDao = new AchievementDaoImpl(config);
		groupDao = new GroupDaoImpl(config);
		hnsUserDao = new HNSUserDaoImpl(config);
		metaDataDao = new MetaDataDaoImpl(config);
		pgUserDao = new PGUserDaoImpl(config);
		requestDao = new RequestDaoImpl(config);
		userDao = new UserDaoImpl(config);
		profileDao = new ProfileDaoImpl(config);
	}

	@Override
	public int apply(int version) {
		achievementDao.createTable();
		groupDao.createTable();
		hnsUserDao.createTable();
		metaDataDao.createTable();
		pgUserDao.createTable();
		requestDao.createTable();
		userDao.createTable();
		profileDao.createTable();
		return Version.V2_3.ordinal();
	}
}
