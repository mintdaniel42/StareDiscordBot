package org.mintdaniel42.starediscordbot.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

@Value
@DatabaseTable(tableName = "metadata")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class MetaDataModel {
	@DatabaseField(id = true) int id = 0;
	@NonNull @DatabaseField Version version;

	@Getter
	@RequiredArgsConstructor
	public enum Version {
		UNKNOWN("?"),
		V1("Apollo"),
		V2("Ares"),
		V2_1("Artemis");

		@NonNull private final String title;
	}
}
