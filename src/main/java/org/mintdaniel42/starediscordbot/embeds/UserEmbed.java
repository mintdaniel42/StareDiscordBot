package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.HNSUserModel;
import org.mintdaniel42.starediscordbot.db.PGUserModel;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@UtilityClass
public class UserEmbed {
    @Contract(pure = true, value = "_, _ -> new")
    public @NonNull MessageEmbed of(@NonNull DatabaseAdapter databaseAdapter, @NonNull HNSUserModel hnsUserModel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(R.string("hide_n_seek_player_database"));
        embedBuilder.setDescription(MCHelper.getUsername(databaseAdapter, hnsUserModel.getUuid()));
        embedBuilder.setColor(Options.getColorNormal());
        embedBuilder.setThumbnail(MCHelper.getThumbnail(hnsUserModel.getUuid()));
        embedBuilder.addField(R.string("rating"), hnsUserModel.getRating(), true);
        embedBuilder.addField(R.string("points"), formatNumber(hnsUserModel.getPoints()), true);
        embedBuilder.addField(R.string("joined"), hnsUserModel.getJoined(), true);
        embedBuilder.addField(R.string("secondary"), hnsUserModel.isSecondary() ? "✅" : "❌", true);
        embedBuilder.addField(R.string("banned"), hnsUserModel.isBanned() ? "✅" : "❌", true);
        embedBuilder.addField(R.string("cheating"), hnsUserModel.isCheating() ? "✅" : "❌", true);

        return embedBuilder.build();
    }

    @Contract(pure = true, value = "_, _ -> new")
    public @NonNull MessageEmbed of(@NonNull DatabaseAdapter databaseAdapter, @NonNull PGUserModel pgUserModel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(R.string("partygames_player_database"));
        embedBuilder.setDescription(MCHelper.getUsername(databaseAdapter, pgUserModel.getUuid()));
        embedBuilder.setColor(Options.getColorNormal());
        embedBuilder.setThumbnail(MCHelper.getThumbnail(pgUserModel.getUuid()));
        embedBuilder.addField(R.string("rating"), pgUserModel.getRating(), true);
        embedBuilder.addField(R.string("points"), formatNumber(pgUserModel.getPoints()), true);
        embedBuilder.addField(R.string("joined"), pgUserModel.getJoined(), true);
        embedBuilder.addField(R.string("luck"), String.valueOf(pgUserModel.getLuck()), true);
        embedBuilder.addField(R.string("quota"), String.format("%s%%", pgUserModel.getQuota()), true);
        embedBuilder.addField(R.string("winrate"), String.format("%s%%", pgUserModel.getWinrate()), true);

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
