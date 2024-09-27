package org.mintdaniel42.starediscordbot.di;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.bucket.PoolAdapter;
import org.mintdaniel42.starediscordbot.bucket.PoolUpdater;

import java.util.List;

@Factory
public final class PoolUpdaterFactory {
	@Bean
	public PoolUpdater build(BotConfig config, List<PoolAdapter> poolAdapters) {
		return new PoolUpdater(config, poolAdapters.toArray(PoolAdapter[]::new));
	}
}
