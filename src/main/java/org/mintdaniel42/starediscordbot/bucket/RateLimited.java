package org.mintdaniel42.starediscordbot.bucket;

import lombok.NonNull;

/**
 * This interface sets up anything related to rate limiting
 */
public interface RateLimited {
	/**
	 * @return the wanted PoolAdapter (usually {@link DefaultBucketPool})
	 */
	default @NonNull PoolAdapter getPool() {
		return DefaultBucketPool.getInstance();
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
	 * @return usually true
	 */
	default boolean isPublicResponseRestricted() {
		return false;
	}
}
