package org.mintdaniel42.starediscordbot.data.entity;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.utils.Calculator;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import java.util.List;
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

	@Contract(pure = true, value = "_, _ -> new")
	public static @NonNull PGUserEntity merge(@NonNull final List<OptionMapping> options, final EntityBuilder builder) {
		for (final var optionMapping : options) {
			switch (optionMapping.getName()) {
				case "rating" -> builder.rating(optionMapping.getAsString());
				case "points" -> builder.points(Math.round(optionMapping.getAsDouble()));
				case "joined" -> builder.joined(optionMapping.getAsString());
				case "luck" -> builder.luck(optionMapping.getAsDouble());
				case "quota" -> builder.quota(optionMapping.getAsDouble());
				case "winrate" -> builder.winrate(optionMapping.getAsDouble());
			}
		}
		return builder.build();
	}

	public double getLuck() {
		if (luck != 0) return luck;
		else return Calculator.calculateLuck(quota, winrate);
	}
}