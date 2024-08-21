package org.mintdaniel42.starediscordbot.di;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import org.mintdaniel42.starediscordbot.bucket.DefaultBucketPool;
import org.mintdaniel42.starediscordbot.bucket.PoolUpdater;
import org.mintdaniel42.starediscordbot.bucket.SpotBucketPool;

@Factory
public final class PoolUpdaterFactory {
	@Bean
	public PoolUpdater build(final DefaultBucketPool defaultBucketPool, final SpotBucketPool spotBucketPool) {
		return new PoolUpdater(
				defaultBucketPool,
				spotBucketPool
		);
	}
}
