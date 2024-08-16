package org.mintdaniel42.starediscordbot.data.entity;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
@Entity(immutable = true, metamodel = @Metamodel(suffix = "Meta"))
@Table(name = "groups")
@Value
public class GroupEntity {
	@NonNull @Id String tag;
	@NonNull String name;
	@NonNull UUID leader;
	@NonNull Relation relation;

	public static @NonNull GroupEntity from(@NonNull final RequestEntity request) {
		return GroupEntity.builder()
				.tag(request.getTag())
				.name(request.getName())
				.leader(request.getLeader())
				.relation(request.getRelation())
				.build();
	}

	@Contract(pure = true, value = "_, _, _ -> new")
	public static @NonNull GroupEntity merge(@NonNull final List<OptionMapping> options, final GroupEntity.EntityBuilder builder, @Nullable UUID leaderUuid) {
		for (OptionMapping optionMapping : options) {
			switch (optionMapping.getName()) {
				case "name" -> builder.name(optionMapping.getAsString());
				case "leader" -> {
					if (leaderUuid != null) builder.leader(leaderUuid);
				}
				case "relation" -> builder.relation(GroupEntity.Relation.valueOf(optionMapping.getAsString()));
			}
		}
		return builder.build();
	}

	public enum Relation {
		ally,
		neutral,
		enemy
	}
}
