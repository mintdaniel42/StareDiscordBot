package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntity;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;

@UtilityClass
public class ListEmbed {
    private final byte entriesPerPage = BuildConfig.entriesPerPage;

    @Contract(value = "_, _, _, _ -> new")
    public @NonNull MessageEmbed createHnsList(@NonNull final UsernameRepository usernameRepository, @NonNull List<HNSUserEntity> hnsUsers,
                                               final int page, final int pageCount) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle(R.Strings.ui("hide_n_seek_player_database"));
        embedBuilder.setDescription(R.Strings.ui("page_s_of_s", page + 1, pageCount));
        embedBuilder.setColor(Options.getColorNormal());

        for (int entry = 0; entry < hnsUsers.size(); entry++) {
            String username = MCHelper.getUsername(usernameRepository, hnsUsers.get(entry).getUuid());
            if (username == null) continue;
            embedBuilder.addField("#" + (entriesPerPage * page + entry + 1), username, false);
        }
        return embedBuilder.build();
    }

    @Contract(value = "_, _, _, _ -> new")
    public @NonNull MessageEmbed createPgList(@NonNull final UsernameRepository usernameRepository, @NonNull final List<PGUserEntity> pgUsers,
                                              final int page, final int pageCount) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle(R.Strings.ui("partygames_player_database"));
        embedBuilder.setDescription(R.Strings.ui("page_s_of_s", page + 1, pageCount));
        embedBuilder.setColor(Options.getColorNormal());

        for (int entry = 0; entry < pgUsers.size(); entry++) {
            String username = MCHelper.getUsername(usernameRepository, pgUsers.get(entry).getUuid());
            if (username == null) continue;
            embedBuilder.addField("#" + (entriesPerPage * page + entry + 1), username, false);
        }
        return embedBuilder.build();
    }
}
