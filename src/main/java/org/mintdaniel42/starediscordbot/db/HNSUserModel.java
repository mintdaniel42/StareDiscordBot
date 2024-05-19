package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@DatabaseTable(tableName = "hns_entries")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HNSUserModel {
    @NotNull @DatabaseField(id = true) UUID uuid;
    @DatabaseField @Builder.Default String rating = "❌";
    @DatabaseField @Builder.Default String joined = "❌";
    @DatabaseField long points;
    @DatabaseField boolean secondary;
    @DatabaseField boolean banned;
    @DatabaseField boolean cheating;

    public static @NonNull HNSUserModel from(@NonNull final RequestModel requestModel) {
        return HNSUserModel.builder()
                .uuid(requestModel.getUuid())
                .rating(requestModel.getRating())
                .joined(requestModel.getJoined())
                .points(requestModel.getPoints())
                .secondary(requestModel.isSecondary())
                .banned(requestModel.isBanned())
                .cheating(requestModel.isCheating())
                .build();
    }
}
