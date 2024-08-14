package org.mintdaniel42.starediscordbot.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Builder
@DatabaseTable(tableName = "achievements")
@Value
public class AchievementModel {
	@NonNull @DatabaseField(id = true, generatedId = true) UUID uuid;
	@NonNull @DatabaseField String name;
	@NonNull @DatabaseField String description;
	@NonNull @DatabaseField Type type;
	@DatabaseField int points;

	public enum Type {
		afk,
		spelling
	}
}
