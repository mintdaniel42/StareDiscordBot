package org.mintdaniel42.starediscordbot.di;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

@Factory
public final class EntityqlFactory {
	@Bean
	public Entityql build(final Config config) {
		return new Entityql(config);
	}
}
