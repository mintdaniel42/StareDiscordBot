package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.HNSUserModel;
import org.mintdaniel42.starediscordbot.data.PGUserModel;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ListEmbed {
    private final byte entriesPerPage = BuildConfig.entriesPerPage;

    @Contract(value = "_, _, _ -> new")
    public @NonNull MessageEmbed createHnsList(@NonNull DatabaseAdapter databaseAdapter, @NonNull List<HNSUserModel> hnsUserModels, int page) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle(R.Strings.ui("hide_n_seek_player_database"));
		embedBuilder.setDescription(R.Strings.ui("page_s_of_s", page + 1, databaseAdapter.getHnsPages()));
        embedBuilder.setColor(Options.getColorNormal());

        for (int entry = 0; entry < hnsUserModels.size(); entry++) {
            String username = MCHelper.getUsername(databaseAdapter, hnsUserModels.get(entry).getUuid());
            if (username == null) continue;
            embedBuilder.addField("#" + (entriesPerPage * page + entry + 1), username, false);
        }
        return embedBuilder.build();
    }

    @Contract(value = "_, _, _ -> new")
    public @NonNull MessageEmbed createPgList(@NonNull DatabaseAdapter databaseAdapter, @NonNull List<PGUserModel> pgUserModels, int page) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle(R.Strings.ui("partygames_player_database"));
		embedBuilder.setDescription(R.Strings.ui("page_s_of_s", page + 1, databaseAdapter.getPgPages()));
        embedBuilder.setColor(Options.getColorNormal());

        for (int entry = 0; entry < pgUserModels.size(); entry++) {
            String username = MCHelper.getUsername(databaseAdapter, pgUserModels.get(entry).getUuid());
            if (username == null) continue;
            embedBuilder.addField("#" + (entriesPerPage * page + entry + 1), username, false);
        }
        return embedBuilder.build();
    }

    public @NonNull MessageEmbed createHnsList(@NonNull DatabaseAdapter databaseAdapter, int page) {
        List<HNSUserModel> hnsUserModels;
        hnsUserModels = databaseAdapter.getHnsUserList(page);

        return createHnsList(databaseAdapter, hnsUserModels != null ? hnsUserModels : new ArrayList<>(0), page);
    }

    public @NonNull MessageEmbed createPgList(@NonNull DatabaseAdapter databaseAdapter, int page) {
        List<PGUserModel> pgUserModels;
        pgUserModels = databaseAdapter.getPgUserList(page);

        return createPgList(databaseAdapter, pgUserModels != null ? pgUserModels : new ArrayList<>(0), page);
    }
}
