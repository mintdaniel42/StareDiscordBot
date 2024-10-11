package org.mintdaniel42.starediscordbot.bucket;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.di.DI;
import org.mintdaniel42.starediscordbot.utils.Permission;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Singleton
@RequiredArgsConstructor
public final class SpotBucketPool implements PoolAdapter {
	@NonNull private final Map<Long, Bucket> buckets = new HashMap<>();
	@NonNull private final BotConfig config;

	@Override
	public @NonNull Bucket getBucket(@NonNull final Member member) {
		final var id = member.getIdLong();
		final var permission = Permission.fromUser(config, member);
		if (permission.equals(Permission.p4)) {
			DI.get(DefaultBucketPool.class)
						.getBucket(member);
		}
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
