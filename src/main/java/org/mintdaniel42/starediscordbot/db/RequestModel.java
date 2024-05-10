package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@DatabaseTable(tableName = "requests")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RequestModel {
    @DatabaseField(id = true) long timestamp;
    @DatabaseField UUID uuid;
    @DatabaseField String rating;
    @DatabaseField String joined;
    @DatabaseField long points;
    @DatabaseField double luck;
    @DatabaseField double quota;
    @DatabaseField double winrate;
    @DatabaseField boolean secondary;
    @DatabaseField boolean banned;
    @DatabaseField boolean cheating;
    @DatabaseField Database database;

    public enum Database {
        PG,
        HNS
    }
}
