package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

import java.util.UUID;

@Value
@Builder
@DatabaseTable(tableName = "usernames")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UsernameModel {
    @NonNull @DatabaseField(id = true) UUID uuid;
    @NonNull @DatabaseField String username;
    @DatabaseField long lastUpdated;
}
