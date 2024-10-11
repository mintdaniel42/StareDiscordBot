package org.mintdaniel42.starediscordbot.bucket;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.experimental.NonFinal;
import net.dv8tion.jda.api.entities.Member;
import org.mintdaniel42.starediscordbot.utils.Permission;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Singleton
public final class DefaultBucketPool implements PoolAdapter {
	private static final long CAPACITY = 500;
	@NonFinal private final Map<Long, Bucket> buckets;

	public DefaultBucketPool() {
		buckets = new HashMap<>();
	}

	@Override
	public void onPermissionChanged(@NonNull final Member member, @NonNull final Permission permission) {
		buckets.remove(member.getIdLong());
	}

	@Override
	public @NonNull Bucket getBucket(@NonNull final Member member) {
		final var id = member.getIdLong();
		if (buckets.containsKey(id)) return buckets.get(id);

		final var bucket = Bucket.builder()
				.addLimit(Bandwidth.builder()
						.capacity(CAPACITY)
						.refillGreedy(10, Duration.ofMinutes(1))
						.build())
				.build();
		buckets.put(id, bucket);
		return bucket;
	}
}
