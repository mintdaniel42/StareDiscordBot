package org.mintdaniel42.starediscordbot.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Builder
@DatabaseTable(tableName = "maps")
@NoArgsConstructor(force = true)
@Value
public class MapModel {
	@NonNull @DatabaseField(id = true) UUID uuid;
	@NonNull @DatabaseField String name;
	@NonNull @DatabaseField UUID[] builders;
	@NonNull @DatabaseField Date release;
	@NonNull @DatabaseField String[] blocks;
	@NonNull @DatabaseField Object fastestMatch; // TODO: records required, placeholder only
	@NonNull @DatabaseField Difficulty difficulty;
	// TODO get picture method

	public enum Difficulty {
		easy,
		medium,
		hard,
		ultra
	}
}
