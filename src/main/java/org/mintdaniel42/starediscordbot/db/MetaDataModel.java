package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@DatabaseTable(tableName = "metadata")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class MetaDataModel {
	@DatabaseField(id = true) int id = 0;
	@NonNull @DatabaseField Version version;

	public enum Version {
		V1
	}
}
