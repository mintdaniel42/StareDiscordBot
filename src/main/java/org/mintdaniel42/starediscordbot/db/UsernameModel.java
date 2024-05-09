package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@DatabaseTable(tableName = "usernames")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UsernameModel {
    @DatabaseField(id = true) UUID uuid;
    @DatabaseField String username;
    @DatabaseField long lastUpdated;
}
