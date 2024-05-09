package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.Bot;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.HNSUserModel;
import org.mintdaniel42.starediscordbot.db.PGUserModel;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;

@UtilityClass
public class UserEmbed {
    @Contract(pure = true, value = "_, _ -> new")
    public @NonNull MessageEmbed of(@NonNull DatabaseAdapter databaseAdapter, @NonNull HNSUserModel hnsUserModel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(Bot.strings.getString("hide_n_seek_player_database"));
        embedBuilder.setDescription(MCHelper.getUsername(databaseAdapter, hnsUserModel.getUuid()));
        embedBuilder.setColor(Options.getColorNormal());
        embedBuilder.setThumbnail(MCHelper.getThumbnail(hnsUserModel.getUuid()));
        embedBuilder.addField(Bot.strings.getString("rating"), hnsUserModel.getRating(), true);
        embedBuilder.addField(Bot.strings.getString("points"), formatNumber(hnsUserModel.getPoints()), true);
        embedBuilder.addField(Bot.strings.getString("joined"), hnsUserModel.getJoined(), true);
        embedBuilder.addField(Bot.strings.getString("secondary"), hnsUserModel.isSecondary() ? "✅" : "❌", true);
        embedBuilder.addField(Bot.strings.getString("banned"), hnsUserModel.isBanned() ? "✅" : "❌", true);
        embedBuilder.addField(Bot.strings.getString("cheating"), hnsUserModel.isCheating() ? "✅" : "❌", true);

        return embedBuilder.build();
    }

    @Contract(pure = true, value = "_, _ -> new")
    public @NonNull MessageEmbed of(@NonNull DatabaseAdapter databaseAdapter, @NonNull PGUserModel pgUserModel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(Bot.strings.getString("partygames_player_database"));
        embedBuilder.setDescription(MCHelper.getUsername(databaseAdapter, pgUserModel.getUuid()));
        embedBuilder.setColor(Options.getColorNormal());
        embedBuilder.setThumbnail(MCHelper.getThumbnail(pgUserModel.getUuid()));
        embedBuilder.addField(Bot.strings.getString("rating"), pgUserModel.getRating(), true);
        embedBuilder.addField(Bot.strings.getString("points"), formatNumber(pgUserModel.getPoints()), true);
        embedBuilder.addField(Bot.strings.getString("joined"), pgUserModel.getJoined(), true);
        embedBuilder.addField(Bot.strings.getString("luck"), String.format("%s%%", pgUserModel.getLuck()), true);
        embedBuilder.addField(Bot.strings.getString("quota"), String.format("%s%%", pgUserModel.getQuota()), true);
        embedBuilder.addField(Bot.strings.getString("winrate"), String.format("%s%%", pgUserModel.getWinrate()), true);

        return embedBuilder.build();
    }

    @Contract(pure = true, value = "_ -> new")
    private @NonNull String formatNumber(double value) {
        if (value >= 5_000_000_000L) return Math.round(value / 1_000_000_000L) + "B";
        else if (value >= 5_000_000L) return Math.round(value / 1_000_000) + "M";
        else if (value >= 5_000) return Math.round(value / 1_000) + "K";
        else return String.valueOf(value);
    }
}
