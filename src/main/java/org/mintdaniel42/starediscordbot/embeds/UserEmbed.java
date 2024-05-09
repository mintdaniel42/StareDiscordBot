package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.mintdaniel42.starediscordbot.Bot;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.HNSUserModel;
import org.mintdaniel42.starediscordbot.db.PGUserModel;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;

@UtilityClass
public class UserEmbed {
    public @NonNull MessageEmbed of(@NonNull DatabaseAdapter databaseAdapter, @NonNull HNSUserModel hnsUserModel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(Bot.strings.getString("hide_n_seek_player_database"));
        embedBuilder.setDescription(MCHelper.getUsername(databaseAdapter, hnsUserModel.getUuid()));
        embedBuilder.setColor(Options.getColorNormal());
        embedBuilder.setThumbnail(MCHelper.getThumbnail(hnsUserModel.getUuid()));
        embedBuilder.addField(Bot.strings.getString("rating"), hnsUserModel.getRating(), true);
        embedBuilder.addField(Bot.strings.getString("points"), String.valueOf(hnsUserModel.getPoints()), true);
        embedBuilder.addField(Bot.strings.getString("joined"), hnsUserModel.getJoined(), true);
        embedBuilder.addField(Bot.strings.getString("secondary"), String.valueOf(hnsUserModel.isSecondary()), true);
        embedBuilder.addField(Bot.strings.getString("banned"), String.valueOf(hnsUserModel.isBanned()), true);
        embedBuilder.addField(Bot.strings.getString("cheating"), String.valueOf(hnsUserModel.isCheating()), true);

        return embedBuilder.build();
    }

    public @NonNull MessageEmbed of(@NonNull DatabaseAdapter databaseAdapter, @NonNull PGUserModel pgUserModel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(Bot.strings.getString("partygames_player_database"));
        embedBuilder.setDescription(MCHelper.getUsername(databaseAdapter, pgUserModel.getUuid()));
        embedBuilder.setColor(Options.getColorNormal());
        embedBuilder.setThumbnail(MCHelper.getThumbnail(pgUserModel.getUuid()));
        embedBuilder.addField(Bot.strings.getString("rating"), pgUserModel.getRating(), true);
        embedBuilder.addField(Bot.strings.getString("points"), String.valueOf(pgUserModel.getPoints()), true);
        embedBuilder.addField(Bot.strings.getString("joined"), pgUserModel.getJoined(), true);
        embedBuilder.addField(Bot.strings.getString("luck"), String.valueOf(pgUserModel.getLuck()), true);
        embedBuilder.addField(Bot.strings.getString("quota"), String.valueOf(pgUserModel.getQuota()), true);
        embedBuilder.addField(Bot.strings.getString("winrate"), String.valueOf(pgUserModel.getWinrate()), true);

        return embedBuilder.build();
    }
}
