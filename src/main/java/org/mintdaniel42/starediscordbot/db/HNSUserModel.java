package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@DatabaseTable(tableName = "hns_entries")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HNSUserModel {
    @NonNull @DatabaseField(id = true) UUID uuid;
    @DatabaseField @Builder.Default String rating = "❌";
    @DatabaseField @Builder.Default String joined = "❌";
    @DatabaseField @Builder.Default long points = 0;
    @DatabaseField @Builder.Default boolean secondary = false;
    @DatabaseField @Builder.Default boolean banned = false;
    @DatabaseField @Builder.Default boolean cheating = false;
    @DatabaseField @Builder.Default String top10 = "❌";
    @DatabaseField @Builder.Default int streak = 0;
    @DatabaseField @Builder.Default String highestRank = "❌";

    public static @NonNull HNSUserModel from(@NonNull final RequestModel requestModel) {
        return HNSUserModel.builder()
                .uuid(requestModel.getUuid())
                .rating(requestModel.getRating())
                .joined(requestModel.getJoined())
                .points(requestModel.getPoints())
                .secondary(requestModel.isSecondary())
                .banned(requestModel.isBanned())
                .cheating(requestModel.isCheating())
                .top10(requestModel.getTop10())
                .streak(requestModel.getStreak())
                .highestRank(requestModel.getHighestRank())
                .build();
    }
}
