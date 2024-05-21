package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.db.*;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class UserEmbed {
    @Contract(pure = true, value = "_, _ -> new")
    public @NonNull MessageEmbed of(@NonNull final UserModel userModel, @NonNull final Type type) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setDescription(userModel.getUsername());
        builder.setColor(Options.getColorNormal());
        builder.setThumbnail(MCHelper.getThumbnail(userModel.getUuid()));

        switch (type) {
            case HNS -> {
                builder.setTitle(R.string("hide_n_seek_player_database"));
                builder.addField(R.string("rating"), userModel.getHnsUser().getRating(), true);
                builder.addField(R.string("points"), formatNumber(userModel.getHnsUser().getPoints()), true);
                builder.addField(R.string("joined"), userModel.getHnsUser().getJoined(), true);
                builder.addField(R.string("note"), userModel.getNote(), userModel.getNote().length() <= 10);
                builder.addField(R.string("banned"), userModel.getHnsUser().isBanned() ? "✅" : "❌", true);
                builder.addField(R.string("cheating"), userModel.getHnsUser().isCheating() ? "✅" : "❌", true);
            }
            case HNS_MORE -> {
                builder.setTitle(R.string("hide_n_seek_player_database_more_information"));
                builder.addField(R.string("top10"), userModel.getHnsUser().getTop10(), true);
                builder.addField(R.string("streak"), formatNumber(userModel.getHnsUser().getStreak()), true);
                builder.addField(R.string("highest_rank"), userModel.getHnsUser().getHighestRank(), true);
                builder.addField(R.string("secondary"), userModel.getHnsUser().isSecondary() ? "✅" : "❌", true);
                builder.addField(R.string("discord_tag"), userModel.getDiscord() == 0 ? "❌" : "<@%s>".formatted(userModel.getDiscord()), true);
                builder.addField(R.string("group_name"), userModel.getGroup() != null ? userModel.getGroup().getName() : "❌", true);
            }
            case HNS_ALL -> {
                builder.setTitle(R.string("hide_n_seek_player_database"));
                builder.addField(R.string("rating"), userModel.getHnsUser().getRating(), false);
                builder.addField(R.string("points"), formatNumber(userModel.getHnsUser().getPoints()), false);
                builder.addField(R.string("joined"), userModel.getHnsUser().getJoined(), false);
                builder.addField(R.string("note"), userModel.getNote(), false);
                builder.addField(R.string("banned"), userModel.getHnsUser().isBanned() ? "✅" : "❌", false);
                builder.addField(R.string("cheating"), userModel.getHnsUser().isCheating() ? "✅" : "❌", false);
                builder.addField(R.string("top10"), userModel.getHnsUser().getTop10(), false);
                builder.addField(R.string("streak"), formatNumber(userModel.getHnsUser().getStreak()), false);
                builder.addField(R.string("highest_rank"), userModel.getHnsUser().getHighestRank(), false);
                builder.addField(R.string("secondary"), userModel.getHnsUser().isSecondary() ? "✅" : "❌", false);
                builder.addField(R.string("discord_tag"), userModel.getDiscord() == 0 ? "❌" : "<@%s>".formatted(userModel.getDiscord()), false);
                builder.addField(R.string("group_name"), userModel.getGroup() != null ? userModel.getGroup().getName() : "❌", false);
            }
            case PG -> {
                builder.setTitle(R.string("partygames_player_database"));
                builder.setThumbnail(MCHelper.getThumbnail(userModel.getPgUser().getUuid()));
                builder.addField(R.string("rating"), userModel.getPgUser().getRating(), true);
                builder.addField(R.string("points"), formatNumber(userModel.getPgUser().getPoints()), true);
                builder.addField(R.string("joined"), userModel.getPgUser().getJoined(), true);
                builder.addField(R.string("luck"), String.valueOf(userModel.getPgUser().getLuck()), true);
                builder.addField(R.string("quota"), String.format("%s%%", userModel.getPgUser().getQuota()), true);
                builder.addField(R.string("winrate"), String.format("%s%%", userModel.getPgUser().getWinrate()), true);
            }
        }

        return builder.build();
    }

    @Contract(pure = true, value = "_ -> new")
    private @NonNull String formatNumber(double value) {
        if (value >= 5_000_000_000L) return Math.round(value / 1_000_000_000L) + "B";
        else if (value >= 5_000_000L) return Math.round(value / 1_000_000) + "M";
        else if (value >= 5_000) return Math.round(value / 1_000) + "K";
        else return String.valueOf(value);
    }

    public enum Type {
        HNS,
        HNS_MORE,
        HNS_ALL,
        PG
    }
}
