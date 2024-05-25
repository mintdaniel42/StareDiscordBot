package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

import java.util.Collection;
import java.util.Collections;
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

	public enum Relation {
		ally,
		neutral,
		enemy
	}
}
