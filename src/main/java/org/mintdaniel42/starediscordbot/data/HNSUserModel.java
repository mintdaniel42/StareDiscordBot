package org.mintdaniel42.starediscordbot.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.Contract;

import java.util.List;
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

    @Contract(pure = true, value = "_ -> new")
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

    @Contract(pure = true, value = "_, _ -> new")
    public static @NonNull HNSUserModel merge(@NonNull final List<OptionMapping> options, @NonNull final HNSUserModelBuilder builder) {
        for (final var optionMapping : options) {
            switch (optionMapping.getName()) {
                case "rating" -> builder.rating(optionMapping.getAsString());
                case "points" -> builder.points(Math.round(optionMapping.getAsDouble()));
                case "joined" -> builder.joined(optionMapping.getAsString());
                case "secondary" -> builder.secondary(optionMapping.getAsBoolean());
                case "banned" -> builder.banned(optionMapping.getAsBoolean());
                case "cheating" -> builder.cheating(optionMapping.getAsBoolean());
                case "top10" -> builder.top10(optionMapping.getAsString());
                case "streak" -> builder.streak(optionMapping.getAsInt());
                case "highest_rank" -> builder.highestRank(optionMapping.getAsString());
            }
        }
        return builder.build();
    }
}
