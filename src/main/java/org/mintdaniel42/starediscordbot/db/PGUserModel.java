package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;
import org.mintdaniel42.starediscordbot.utils.Calculator;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@DatabaseTable(tableName = "pg_entries")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PGUserModel {
    @NonNull @DatabaseField(id = true) UUID uuid;
    @DatabaseField @Builder.Default String rating = "❌";
    @DatabaseField @Builder.Default String joined = "❌";
    @DatabaseField @Builder.Default long points = 0;
    @DatabaseField @Builder.Default double luck = 0;
    @DatabaseField @Builder.Default double quota = 0;
    @DatabaseField @Builder.Default double winrate = 0;

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

    public double getLuck() {
        if (luck != 0) return luck;
        else return Calculator.calculateLuck(quota, winrate);
    }
}