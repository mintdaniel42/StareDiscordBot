package org.mintdaniel42.starediscordbot.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Builder
@DatabaseTable(tableName = "achievements")
@NoArgsConstructor(force = true)
@Value
public class AchievementModel {
	@NonNull @DatabaseField(generatedId = true) UUID uuid;
	@NonNull @DatabaseField String name;
	@NonNull @DatabaseField String description;
	@NonNull @DatabaseField Type type;
	@DatabaseField int points;

	public enum Type {
		afk,
		spelling
	}
}
