package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@DatabaseTable(tableName = "hns_entries")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class GroupModel {
	@DatabaseField(id = true) String tag;
	@DatabaseField String name;
	@DatabaseField UUID leader;
	@DatabaseField Relation relation;

	public enum Relation {
		ally,
		neutral,
		enemy
	}
}
