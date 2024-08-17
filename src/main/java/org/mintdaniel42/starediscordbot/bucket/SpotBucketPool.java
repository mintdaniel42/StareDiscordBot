package org.mintdaniel42.starediscordbot.bucket;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.NonNull;
import lombok.experimental.NonFinal;
import net.dv8tion.jda.api.entities.Member;
import org.mintdaniel42.starediscordbot.utils.Permission;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public final class SpotBucketPool implements PoolAdapter {
	private static SpotBucketPool instance;
	@NonFinal private final Map<Long, Bucket> buckets;

	private SpotBucketPool() {
		buckets = new HashMap<>();
	}

	public static @NonNull SpotBucketPool getInstance() {
		if (instance == null) instance = new SpotBucketPool();
		return instance;
	}

	@Override
	public @NonNull Bucket getBucket(@NonNull final Member member) {
		final var id = member.getIdLong();
		final var permission = Permission.fromUser(member);
		if (permission.equals(Permission.p4)) return DefaultBucketPool.getInstance().getBucket(member);
		if (buckets.containsKey(id)) return buckets.get(id);

		final var max = switch (permission) {
			case p2 -> 3;
			case p3 -> 5;
			default -> 1;
		};

		final var bucket = Bucket.builder()
				.addLimit(Bandwidth.builder()
						.capacity(max)
						.refillIntervallyAligned(max, Duration.ofDays(1), getFirstRefill())
						.build())
				.build();
		buckets.put(id, bucket);
		return bucket;
	}

	@Override
	public void onPermissionChanged(@NonNull Member member, @NonNull Permission permission) {
		buckets.remove(member.getIdLong());
	}

	@NonNull
	Instant getFirstRefill() {
		return ZonedDateTime.now()
				.truncatedTo(ChronoUnit.DAYS)
				.plusDays(1)
				.toInstant();
	}
}
