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
	@DatabaseField String group;
	@DatabaseField long discord;
	@DatabaseField String note;

	public static @NonNull UserModel from(@NonNull final RequestModel requestModel) {
		return UserModel.builder()
				.uuid(requestModel.getUuid())
				.group(requestModel.getGroup())
				.discord(requestModel.getDiscord())
				.note(requestModel.getNote())
				.build();
	}
}
