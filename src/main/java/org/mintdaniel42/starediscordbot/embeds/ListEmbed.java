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

import java.util.List;

@UtilityClass
public class ListEmbed {
    private final byte entriesPerPage = Options.getEntriesPerPage();

    @Contract(value = "_, _, _ -> new")
    public MessageEmbed createHnsList(@NonNull DatabaseAdapter databaseAdapter, @NonNull List<HNSUserModel> hnsUserModels, int page) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(Bot.strings.getString("hide_n_seek_player_database"));
        embedBuilder.setDescription(String.format(Bot.strings.getString("page_s_of_s"), page + 1, databaseAdapter.getHnsPages()));
        embedBuilder.setColor(Options.getColorNormal());

        for (int entry = 0; entry < hnsUserModels.size(); entry++) {
            String username = MCHelper.getUsername(databaseAdapter, hnsUserModels.get(entry).getUuid());
            if (username == null) continue;
            embedBuilder.addField("#" + (entriesPerPage * page + entry + 1), username, false);
        }
        return embedBuilder.build();
    }

    @Contract(value = "_, _, _ -> new")
    public MessageEmbed createPgList(@NonNull DatabaseAdapter databaseAdapter, @NonNull List<PGUserModel> pgUserModels, int page) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(Bot.strings.getString("partygames_player_database"));
        embedBuilder.setDescription(String.format(Bot.strings.getString("page_s_of_s"), page + 1, databaseAdapter.getPgPages()));
        embedBuilder.setColor(Options.getColorNormal());

        for (int entry = 0; entry < pgUserModels.size(); entry++) {
            String username = MCHelper.getUsername(databaseAdapter, pgUserModels.get(entry).getUuid());
            if (username == null) continue;
            embedBuilder.addField("#" + (entriesPerPage * page + entry + 1), username, false);
        }
        return embedBuilder.build();
    }
}
