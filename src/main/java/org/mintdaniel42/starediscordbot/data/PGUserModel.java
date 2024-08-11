package org.mintdaniel42.starediscordbot.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.utils.Calculator;

import java.util.List;
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

    @Contract(pure = true, value = "_ -> new")
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

    @Contract(pure = true, value = "_, _ -> new")
    public static @NonNull PGUserModel merge(@NonNull final List<OptionMapping> options, @NonNull final PGUserModel.PGUserModelBuilder builder) {
        for (final var optionMapping : options) {
            switch (optionMapping.getName()) {
                case "rating" -> builder.rating(optionMapping.getAsString());
                case "points" -> builder.points(Math.round(optionMapping.getAsDouble()));
                case "joined" -> builder.joined(optionMapping.getAsString());
                case "luck" -> builder.luck(optionMapping.getAsDouble());
                case "quota" -> builder.quota(optionMapping.getAsDouble());
                case "winrate" -> builder.winrate(optionMapping.getAsDouble());
            }
        }
        return builder.build();
    }

    public double getLuck() {
        if (luck != 0) return luck;
        else return Calculator.calculateLuck(quota, winrate);
    }
}