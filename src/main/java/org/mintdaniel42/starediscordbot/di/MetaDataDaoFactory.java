package org.mintdaniel42.starediscordbot.di;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import org.mintdaniel42.starediscordbot.data.DatabaseConfig;
import org.mintdaniel42.starediscordbot.data.dao.MetaDataDao;
import org.mintdaniel42.starediscordbot.data.dao.MetaDataDaoImpl;

@Factory
public final class MetaDataDaoFactory {
	@Bean
	MetaDataDao build(DatabaseConfig config) {
		return new MetaDataDaoImpl(config);
	}
}
