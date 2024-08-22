package org.mintdaniel42.starediscordbot.di;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import org.mintdaniel42.starediscordbot.bucket.PoolAdapter;
import org.mintdaniel42.starediscordbot.bucket.PoolUpdater;

import java.util.List;

@Factory
public final class PoolUpdaterFactory {
	@Bean
	public PoolUpdater build(List<PoolAdapter> poolAdapters) {
		return new PoolUpdater(poolAdapters.toArray(PoolAdapter[]::new));
	}
}
