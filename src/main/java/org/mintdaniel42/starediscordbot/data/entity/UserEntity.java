package org.mintdaniel42.starediscordbot.data.entity;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
@Entity(immutable = true, metamodel = @Metamodel(suffix = "Meta"))
@Table(name = "users")
@Value
public class UserEntity {
	@Id UUID uuid;
	String groupTag;
	@Builder.Default long discord = 0;
	@Builder.Default String note = "❌";

	public static @NonNull UserEntity from(@NonNull final RequestEntity request) {
		return UserEntity.builder()
				.uuid(request.getUuid())
				.discord(request.getDiscord())
				.note(request.getNote())
				.build();
	}

	public static @NonNull UserEntity merge(@NonNull final List<OptionMapping> options, UserEntity.EntityBuilder builder) {
		for (OptionMapping optionMapping : options) {
			switch (optionMapping.getName()) {
				case "discord" -> builder.discord(optionMapping.getAsLong());
				case "note" -> builder.note(optionMapping.getAsString());
			}
		}
		return builder.build();
	}
}
