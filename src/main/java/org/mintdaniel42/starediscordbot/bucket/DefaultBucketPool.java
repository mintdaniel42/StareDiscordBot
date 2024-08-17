package org.mintdaniel42.starediscordbot.bucket;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.NonNull;
import lombok.experimental.NonFinal;
import net.dv8tion.jda.api.entities.Member;
import org.mintdaniel42.starediscordbot.utils.Permission;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DefaultBucketPool implements PoolAdapter {
	private static final long CAPACITY = 500;
	private static DefaultBucketPool instance;
	@NonFinal private final Map<Long, Bucket> buckets;

	private DefaultBucketPool() {
		buckets = new HashMap<>();
	}

	public static @NonNull DefaultBucketPool getInstance() {
		if (instance == null) instance = new DefaultBucketPool();
		return instance;
	}

	@Override
	public void onPermissionChanged(@NonNull Member member, @NonNull Permission permission) {
		buckets.remove(member.getIdLong());
	}

	@Override
	public @NonNull Bucket getBucket(@NonNull Member member) {
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
