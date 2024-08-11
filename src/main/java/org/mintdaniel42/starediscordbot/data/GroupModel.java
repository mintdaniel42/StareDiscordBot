package org.mintdaniel42.starediscordbot.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@DatabaseTable(tableName = "groups")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class GroupModel {
	@NonNull @DatabaseField(id = true) String tag;
	@NonNull @DatabaseField String name;
	@NonNull @DatabaseField UUID leader;
	@NonNull @DatabaseField Relation relation;
	@NonNull @Builder.Default Collection<UserModel> members = Collections.emptyList();

	public static @NonNull GroupModel from(@NonNull final RequestModel requestModel) {
		return GroupModel.builder()
				.tag(requestModel.getTag())
				.name(requestModel.getName())
				.leader(requestModel.getLeader())
				.relation(requestModel.getRelation())
				.build();
	}

	@Contract(pure = true, value = "_, _, _ -> new")
	public static @NonNull GroupModel merge(@NonNull final List<OptionMapping> options, @NonNull final GroupModel.GroupModelBuilder builder, @Nullable UUID leaderUuid) {
		for (OptionMapping optionMapping : options) {
			switch (optionMapping.getName()) {
				case "name" -> builder.name(optionMapping.getAsString());
				case "leader" -> {
					if (leaderUuid != null) builder.leader(leaderUuid);
				}
				case "relation" -> builder.relation(GroupModel.Relation.valueOf(optionMapping.getAsString()));
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
