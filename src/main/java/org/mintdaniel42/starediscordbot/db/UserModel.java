package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@DatabaseTable(tableName = "users")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserModel {
	String username;
	@DatabaseField(id = true) UUID uuid;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true) GroupModel group;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true) HNSUserModel hnsUser;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true) PGUserModel pgUser;
	@DatabaseField long discord;
	@DatabaseField @Builder.Default String note = "❌";

	public static @NonNull UserModel from(@NonNull final RequestModel requestModel) {
		return UserModel.builder()
				.uuid(requestModel.getUuid())
				.group(requestModel.getGroup())
				.discord(requestModel.getDiscord())
				.note(requestModel.getNote())
				.build();
	}
}
