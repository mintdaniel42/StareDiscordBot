package org.mintdaniel42.starediscordbot.data.entity;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.utils.Calculator;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import java.util.UUID;

@Builder(toBuilder = true)
@Entity(immutable = true, metamodel = @Metamodel(suffix = "Meta"))
@Table(name = "pg_entries")
@Value
public class PGUserEntity {
	@NonNull @Id UUID uuid;
	@Builder.Default String rating = "❌";
	@Builder.Default String joined = "❌";
	@Builder.Default long points = 0;
	@Builder.Default double luck = 0;
	@Builder.Default double quota = 0;
	@Builder.Default double winrate = 0;

	@Contract(pure = true, value = "_ -> new")
	public static @NonNull PGUserEntity from(@NonNull final RequestEntity request) {
		return PGUserEntity.builder()
				.uuid(request.getUuid())
				.rating(request.getRating())
				.joined(request.getJoined())
				.points(request.getPoints())
				.luck(request.getLuck())
				.quota(request.getQuota())
				.winrate(request.getWinrate())
				.build();
	}

	public double getLuck() {
		if (luck != 0) return luck;
		else return Calculator.calculateLuck(quota, winrate);
	}
}