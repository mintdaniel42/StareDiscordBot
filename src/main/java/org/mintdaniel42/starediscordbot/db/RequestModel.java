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
    @DatabaseField UUID uuid;
    @DatabaseField String rating;
    @DatabaseField String joined;
    @DatabaseField @Builder.Default long points = 0;
    @DatabaseField @Builder.Default double luck = 0;
    @DatabaseField @Builder.Default double quota = 0;
    @DatabaseField @Builder.Default double winrate = 0;
    @DatabaseField @Builder.Default boolean secondary = false;
    @DatabaseField @Builder.Default boolean banned = false;
    @DatabaseField @Builder.Default boolean cheating = false;
    @DatabaseField String tag;
    @DatabaseField String name;
    @DatabaseField UUID leader;
    @DatabaseField GroupModel.Relation relation;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true) GroupModel group;
    @DatabaseField @Builder.Default long discord = 0;
    @DatabaseField String note;
    @DatabaseField String top10;
    @DatabaseField @Builder.Default int streak = 0;
    @DatabaseField String highestRank;
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
                .top10(hnsUserModel.getTop10())
                .streak(hnsUserModel.getStreak())
                .highestRank(hnsUserModel.getHighestRank())
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
                .uuid(userModel.getUuid())
                .discord(userModel.getDiscord())
                .note(userModel.getNote())
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
