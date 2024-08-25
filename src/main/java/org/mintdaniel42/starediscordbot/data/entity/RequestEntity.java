package org.mintdaniel42.starediscordbot.data.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import java.util.UUID;

@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
@Entity(immutable = true, metamodel = @Metamodel(suffix = "Meta"))
@Table(name = "users")
@Value
public class RequestEntity {
	@Id long timestamp;
	UUID uuid;
	String rating;
	String joined;
	@Builder.Default long points = 0;
	@Builder.Default double luck = 0;
	@Builder.Default double quota = 0;
	@Builder.Default double winrate = 0;
	@Builder.Default boolean secondary = false;
	@Builder.Default boolean banned = false;
	@Builder.Default boolean cheating = false;
	String tag;
	String name;
	UUID leader;
	GroupEntity.Relation relation;
	String groupTag;
	@Builder.Default long discord = 0;
	String note;
	String top10;
	@Builder.Default int streak = 0;
	String highestRank;
	@NonNull Type type;

	public static @NonNull RequestEntity from(final long timestamp, @NonNull final HNSUserEntity hnsUser) {
		return RequestEntity.builder()
				.timestamp(timestamp)
				.uuid(hnsUser.getUuid())
				.rating(hnsUser.getRating())
				.joined(hnsUser.getJoined())
				.points(hnsUser.getPoints())
				.secondary(hnsUser.isSecondary())
				.banned(hnsUser.isBanned())
				.cheating(hnsUser.isCheating())
				.top10(hnsUser.getTop10())
				.streak(hnsUser.getStreak())
				.highestRank(hnsUser.getHighestRank())
				.type(Type.hns)
				.build();
	}

	public static @NonNull RequestEntity from(final long timestamp, @NonNull final PGUserEntity pgUser) {
		return RequestEntity.builder()
				.timestamp(timestamp)
				.uuid(pgUser.getUuid())
				.rating(pgUser.getRating())
				.joined(pgUser.getJoined())
				.points(pgUser.getPoints())
				.luck(pgUser.getLuck())
				.quota(pgUser.getQuota())
				.winrate(pgUser.getWinrate())
				.type(Type.pg)
				.build();
	}

	public static @NonNull RequestEntity from(final long timestamp, @NonNull final GroupEntity group) {
		return RequestEntity.builder()
				.timestamp(timestamp)
				.tag(group.getTag())
				.name(group.getName())
				.leader(group.getLeader())
				.relation(group.getRelation())
				.type(Type.group)
				.build();
	}

	public static @NonNull RequestEntity from(final long timestamp, @NonNull final UserEntity user) {
		return RequestEntity.builder()
				.timestamp(timestamp)
				.uuid(user.getUuid())
				.discord(user.getDiscord())
				.note(user.getNote())
				.type(Type.user)
				.build();
	}

	public enum Type {
		hns,
		pg,
		user,
		group
	}
}
