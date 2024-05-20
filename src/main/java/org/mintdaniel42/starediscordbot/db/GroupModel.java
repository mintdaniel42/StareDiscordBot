package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@DatabaseTable(tableName = "groups")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class GroupModel {
	@DatabaseField(id = true) String tag;
	@DatabaseField String name;
	@DatabaseField UUID leader;
	@DatabaseField Relation relation;

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
