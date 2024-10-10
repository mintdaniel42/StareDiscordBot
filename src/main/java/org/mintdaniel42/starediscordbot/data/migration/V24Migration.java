package org.mintdaniel42.starediscordbot.data.migration;

import io.avaje.inject.Prototype;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.Version;
import org.mintdaniel42.starediscordbot.data.dao.*;
import org.seasar.doma.jdbc.Config;

@Prototype
public class V24Migration implements Migration {
	@NonNull private final MetaDataDao metaDataDao;
	@NonNull private final RequestDao requestDao;
	@NonNull private final SpotDao spotDao;
	@NonNull private final UserDao userDao;

	public V24Migration(@NonNull final Config config) {
		metaDataDao = new MetaDataDaoImpl(config);
		requestDao = new RequestDaoImpl(config);
		spotDao = new SpotDaoImpl(config);
		userDao = new UserDaoImpl(config);
	}

	@Override
	public int apply(final int version) {
		spotDao.createTable();
		userDao.renameColumnGroupTag();
		requestDao.renameColumnGroupTag();
		requestDao.renameColumnType();
		metaDataDao.dropTable();
		return Version.V2_4.ordinal();
	}
}
