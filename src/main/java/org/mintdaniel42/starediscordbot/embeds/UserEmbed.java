package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.db.UserModel;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class UserEmbed {
    @Contract(pure = true, value = "_, _ -> new")
    public @NonNull MessageEmbed of(@NonNull final UserModel userModel, @NonNull final Type type) {
        final var builder = new EmbedBuilder()
                .setDescription(userModel.getUsername())
                .setColor(Options.getColorNormal())
                .setThumbnail(MCHelper.getThumbnail(userModel.getUuid()));

        return switch (type) {
            case BASE -> buildBaseEmbed(userModel, builder);
            //#if dev
	        case HNS -> buildHnsEmbed(userModel, builder);
            case HNS_MORE -> buildHnsMoreEmbed(userModel, builder);
            case HNS_ALL -> buildHnsAllEmbed(userModel, builder);
            //#else
            //$$ case HNS, HNS_MORE, HNS_ALL -> buildHnsEmbed(userModel, builder);
            //#endif
            case PG -> buildPgEmbed(userModel, builder);
        };
    }

    @Contract(pure = true, value = "_, _ -> new")
    private @NonNull MessageEmbed buildBaseEmbed(@NonNull final UserModel userModel, @NonNull final EmbedBuilder builder) {
        return builder.setTitle(R.string("user_base_info"))
                .addField(R.string("group_name"), userModel.getGroup() != null ? userModel.getGroup().getName() : "❌", true)
                .addField(R.string("discord_tag"), userModel.getDiscord() == 0 ? "❌" : "<@%s>".formatted(userModel.getDiscord()), true)
                .addField(R.string("note"), userModel.getNote(), false)
                .build();
    }

    @Contract(pure = true, value = "_, _ -> new")
    private @NonNull MessageEmbed buildHnsEmbed(@NonNull final UserModel userModel, @NonNull final EmbedBuilder builder) {
        return builder.setTitle(R.string("hide_n_seek_player_database"))
                .addField(R.string("rating"), userModel.getHnsUser().getRating(), true)
                .addField(R.string("points"), formatNumber(userModel.getHnsUser().getPoints()), true)
                .addField(R.string("joined"), userModel.getHnsUser().getJoined(), true)
                .addField(R.string("note"), userModel.getNote(), userModel.getNote().length() <= 10)
                .addField(R.string("banned"), userModel.getHnsUser().isBanned() ? "✅" : "❌", true)
                .addField(R.string("cheating"), userModel.getHnsUser().isCheating() ? "✅" : "❌", true)
                .build();
    }

    //#if dev
    @Contract(pure = true, value = "_, _ -> new")
    private @NonNull MessageEmbed buildHnsMoreEmbed(@NonNull final UserModel userModel, @NonNull final EmbedBuilder builder) {
        return builder.setTitle(R.string("hide_n_seek_player_database_more_information"))
                .addField(R.string("top10"), userModel.getHnsUser().getTop10(), true)
                .addField(R.string("streak"), formatNumber(userModel.getHnsUser().getStreak()), true)
                .addField(R.string("highest_rank"), userModel.getHnsUser().getHighestRank(), true)
                .addField(R.string("secondary"), userModel.getHnsUser().isSecondary() ? "✅" : "❌", true)
                .addField(R.string("discord_tag"), userModel.getDiscord() == 0 ? "❌" : "<@%s>".formatted(userModel.getDiscord()), true)
                .addField(R.string("group_name"), userModel.getGroup() != null ? userModel.getGroup().getName() : "❌", true)
                .build();
    }

    @Contract(pure = true, value = "_, _ -> new")
    private @NonNull MessageEmbed buildHnsAllEmbed(@NonNull final UserModel userModel, @NonNull final EmbedBuilder builder) {
        return builder.setTitle(R.string("hide_n_seek_player_database"))
                .addField(R.string("rating"), userModel.getHnsUser().getRating(), true)
                .addField(R.string("points"), formatNumber(userModel.getHnsUser().getPoints()), true)
                .addField(R.string("joined"), userModel.getHnsUser().getJoined(), true)
                .addField(R.string("banned"), userModel.getHnsUser().isBanned() ? "✅" : "❌", true)
                .addField(R.string("cheating"), userModel.getHnsUser().isCheating() ? "✅" : "❌", true)
                .addField(R.string("top10"), userModel.getHnsUser().getTop10(), true)
                .addField(R.string("streak"), formatNumber(userModel.getHnsUser().getStreak()), true)
                .addField(R.string("highest_rank"), userModel.getHnsUser().getHighestRank(), true)
                .addField(R.string("secondary"), userModel.getHnsUser().isSecondary() ? "✅" : "❌", true)
                .build();
    }
    //#endif

    @Contract(pure = true, value = "_, _ -> new")
    private @NonNull MessageEmbed buildPgEmbed(@NonNull final UserModel userModel, @NonNull final EmbedBuilder builder) {
        return builder.setTitle(R.string("partygames_player_database"))
                .setThumbnail(MCHelper.getThumbnail(userModel.getPgUser().getUuid()))
                .addField(R.string("rating"), userModel.getPgUser().getRating(), true)
                .addField(R.string("points"), formatNumber(userModel.getPgUser().getPoints()), true)
                .addField(R.string("joined"), userModel.getPgUser().getJoined(), true)
                .addField(R.string("luck"), String.valueOf(userModel.getPgUser().getLuck()), true)
                .addField(R.string("quota"), String.format("%s%%", userModel.getPgUser().getQuota()), true)
                .addField(R.string("winrate"), String.format("%s%%", userModel.getPgUser().getWinrate()), true)
                .build();
    }

    @Contract(pure = true, value = "_ -> new")
    private @NonNull String formatNumber(final double value) {
        if (value >= 5_000_000_000L) return Math.round(value / 1_000_000_000L) + "B";
        else if (value >= 5_000_000L) return Math.round(value / 1_000_000) + "M";
        else if (value >= 5_000) return Math.round(value / 1_000) + "K";
        else return String.valueOf(value);
    }

    public enum Type {
        BASE,
        HNS,
        HNS_MORE,
        HNS_ALL,
        PG
    }
}
