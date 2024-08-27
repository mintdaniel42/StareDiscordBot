package org.mintdaniel42.starediscordbot.data;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.dao.*;
import org.mintdaniel42.starediscordbot.data.entity.MetaDataEntity;
import org.seasar.doma.jdbc.Config;

@Singleton
public final class Migrator {
	@NonNull private final AchievementDao achievementDao;
	@NonNull private final GroupDao groupDao;
	@NonNull private final HNSUserDao hnsUserDao;
	@NonNull private final MetaDataDao metaDataDao;
	@NonNull private final PGUserDao pgUserDao;
	@NonNull private final RequestDao requestDao;
	@NonNull private final SpotDao spotDao;
	@NonNull private final UserDao userDao;
	@NonNull private final UsernameDao usernameDao;

	public Migrator(@NonNull final Config config) {
		achievementDao = new AchievementDaoImpl(config);
		groupDao = new GroupDaoImpl(config);
		hnsUserDao = new HNSUserDaoImpl(config);
		metaDataDao = new MetaDataDaoImpl(config);
		pgUserDao = new PGUserDaoImpl(config);
		requestDao = new RequestDaoImpl(config);
		spotDao = new SpotDaoImpl(config);
		userDao = new UserDaoImpl(config);
		usernameDao = new UsernameDaoImpl(config);
	}

	public void onUpgrade(@NonNull MetaDataEntity.Version current, @NonNull final MetaDataEntity.Version to) {
		while (current != to) {
			current = switch (current) {
				case UNKNOWN -> migrateUnknownToV2_3();
				case V2_3 -> migrateV2_3ToV3();
				default -> to;
			};
		}
	}

	private @NonNull MetaDataEntity.Version migrateUnknownToV2_3() {
		achievementDao.createTable();
		groupDao.createTable();
		hnsUserDao.createTable();
		metaDataDao.createTable();
		pgUserDao.createTable();
		requestDao.createTable();
		userDao.createTable();
		usernameDao.createTable();
		return MetaDataEntity.Version.V2_3;
	}

	private @NonNull MetaDataEntity.Version migrateV2_3ToV3() {
		spotDao.createTable();
		userDao.renameColumnGroupTag();
		requestDao.renameColumnGroupTag();
		requestDao.renameColumnType();
		return MetaDataEntity.Version.V3;
	}
}
