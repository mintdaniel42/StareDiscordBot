package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@DatabaseTable(tableName = "hns_entries")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserModel {
	@DatabaseField(id = true) UUID uuid;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "hnsId") HNSUserModel hnsUserModel;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "pgId") PGUserModel pgUserModel;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "groupId") GroupModel groupModel;
	@DatabaseField long discord;

	public static @NonNull UserModel from(@NonNull final RequestModel requestModel) {
		return UserModel.builder()
				.uuid(requestModel.getUuid())
				.hnsUserModel(requestModel.getHnsUserModel())
				.pgUserModel(requestModel.getPgUserModel())
				.groupModel(requestModel.getGroupModel())
				.discord(requestModel.getDiscord())
				.build();
	}
}
