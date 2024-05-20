package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

import java.util.UUID;

@Value
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
@DatabaseTable(tableName = "requests")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RequestModel {
    @DatabaseField(id = true) long timestamp;
    @NonNull @DatabaseField UUID uuid;
    @NonNull @DatabaseField String rating;
    @NonNull @DatabaseField String joined;
    @DatabaseField long points;
    @DatabaseField double luck;
    @DatabaseField double quota;
    @DatabaseField double winrate;
    @DatabaseField boolean secondary;
    @DatabaseField boolean banned;
    @DatabaseField boolean cheating;
    @DatabaseField String tag;
    @DatabaseField String name;
    @DatabaseField UUID leader;
    @DatabaseField GroupModel.Relation relation;
    @DatabaseField String group;
    @DatabaseField long discord;
    @NonNull @DatabaseField Database database;

    public static @NonNull RequestModel from(final long timestamp, @NonNull final HNSUserModel hnsUserModel) {
        return RequestModel.builder()
                .timestamp(timestamp)
                .uuid(hnsUserModel.getUuid())
                .rating(hnsUserModel.getRating())
                .joined(hnsUserModel.getJoined())
                .points(hnsUserModel.getPoints())
                .secondary(hnsUserModel.isSecondary())
                .banned(hnsUserModel.isBanned())
                .cheating(hnsUserModel.isCheating())
                .database(Database.HNS)
                .build();
    }

    public static @NonNull RequestModel from(final long timestamp, @NonNull final PGUserModel pgUserModel) {
        return RequestModel.builder()
                .timestamp(timestamp)
                .uuid(pgUserModel.getUuid())
                .rating(pgUserModel.getRating())
                .joined(pgUserModel.getJoined())
                .points(pgUserModel.getPoints())
                .luck(pgUserModel.getLuck())
                .quota(pgUserModel.getQuota())
                .winrate(pgUserModel.getWinrate())
                .database(Database.PG)
                .build();
    }

    public static @NonNull RequestModel from(final long timestamp, @NonNull final GroupModel groupModel) {
        return RequestModel.builder()
                .timestamp(timestamp)
                .tag(groupModel.getTag())
                .name(groupModel.getName())
                .leader(groupModel.getLeader())
                .relation(groupModel.getRelation())
                .database(Database.GROUP)
                .build();
    }

    public static @NonNull RequestModel from(final long timestamp, @NonNull final UserModel userModel) {
        return RequestModel.builder()
                .timestamp(timestamp)
                .group(userModel.getGroup())
                .discord(userModel.getDiscord())
                .database(Database.USER)
                .build();
    }

    public enum Database {
        PG,
        HNS,
        GROUP,
        USER
    }
}
