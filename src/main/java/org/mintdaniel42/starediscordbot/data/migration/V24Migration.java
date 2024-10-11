package org.mintdaniel42.starediscordbot.data.migration;

import io.avaje.inject.Prototype;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.Version;
import org.mintdaniel42.starediscordbot.data.dao.*;
import org.seasar.doma.jdbc.Config;

@Prototype
public final class V24Migration implements Migration {
	@NonNull private final MetaDataDao metaDataDao;
	@NonNull private final RequestDao requestDao;
	@NonNull private final SpotDao spotDao;
	@NonNull private final UserDao userDao;
	@NonNull private final ProfileDao profileDao;

	public V24Migration(@NonNull final Config config) {
		metaDataDao = new MetaDataDaoImpl(config);
		requestDao = new RequestDaoImpl(config);
		spotDao = new SpotDaoImpl(config);
		userDao = new UserDaoImpl(config);
		profileDao = new ProfileDaoImpl(config);
	}

	@Override
	public int apply(final int version) {
		spotDao.createTable();
		userDao.renameColumnGroupTag();
		requestDao.renameColumnGroupTag();
		requestDao.renameColumnType();
		metaDataDao.dropTable();
		profileDao.renameTable();
		return Version.V2_4.ordinal();
	}
}
