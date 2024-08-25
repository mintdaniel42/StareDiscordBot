package org.mintdaniel42.starediscordbot.bucket;

import io.avaje.inject.BeanScope;
import lombok.Cleanup;
import lombok.NonNull;

/**
 * This interface sets up anything related to rate limiting
 */
public interface RateLimited {
	/**
	 * @return the wanted PoolAdapter (usually {@link DefaultBucketPool})
	 */
	default @NonNull PoolAdapter getPool() {
		@Cleanup final var beanScope = BeanScope.builder().build();
		return beanScope.get(DefaultBucketPool.class);
	}

	/**
	 * Override this to change how many tokens an action requires
	 *
	 * @return the amount of tokens
	 */
	default long getActionTokenPrice() {
		return 1;
	}

	/**
	 * Override this to force responses to be ephemeral
	 *
	 * @return usually false
	 */
	default boolean isPublicResponseRestricted() {
		return false;
	}
}
