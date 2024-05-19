package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@DatabaseTable(tableName = "pg_entries")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PGUserModel {
    @NotNull @DatabaseField(id = true) UUID uuid;
    @DatabaseField @Builder.Default String rating = "❌";
    @DatabaseField @Builder.Default String joined = "❌";
    @DatabaseField long points;
    @DatabaseField double luck;
    @DatabaseField double quota;
    @DatabaseField double winrate;

    public static @NonNull PGUserModel from(@NonNull final RequestModel requestModel) {
        return PGUserModel.builder()
                .uuid(requestModel.getUuid())
                .rating(requestModel.getRating())
                .joined(requestModel.getJoined())
                .points(requestModel.getPoints())
                .luck(requestModel.getLuck())
                .quota(requestModel.getQuota())
                .winrate(requestModel.getWinrate())
                .build();
    }
}