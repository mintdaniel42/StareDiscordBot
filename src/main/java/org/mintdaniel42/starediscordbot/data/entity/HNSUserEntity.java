package org.mintdaniel42.starediscordbot.data.entity;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import java.util.UUID;

@Builder(toBuilder = true)
@Entity(immutable = true, metamodel = @Metamodel(suffix = "Meta"))
@Table(name = "hns_entries")
@Value
public class HNSUserEntity {
	@NonNull @Id UUID uuid;
	@Builder.Default String rating = "❌";
	@Builder.Default String joined = "❌";
	@Builder.Default long points = 0;
	@Builder.Default boolean secondary = false;
	@Builder.Default boolean banned = false;
	@Builder.Default boolean cheating = false;
	@Builder.Default String top10 = "❌";
	@Builder.Default int streak = 0;
	@Builder.Default String highestRank = "❌";

	@Contract(pure = true, value = "_ -> new")
	public static @NonNull HNSUserEntity from(@NonNull final RequestEntity request) {
		return HNSUserEntity.builder()
				.uuid(request.getUuid())
				.rating(request.getRating())
				.joined(request.getJoined())
				.points(request.getPoints())
				.secondary(request.isSecondary())
				.banned(request.isBanned())
				.cheating(request.isCheating())
				.top10(request.getTop10())
				.streak(request.getStreak())
				.highestRank(request.getHighestRank())
				.build();
	}
}
