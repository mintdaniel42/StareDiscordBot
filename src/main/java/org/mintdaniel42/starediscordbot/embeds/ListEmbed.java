package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntity;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;

@UtilityClass
public class ListEmbed {
    @Contract(value = "_, _, _, _ -> new")
    public @NonNull MessageEmbed createHnsList(@NonNull final ProfileRepository profileRepository, @NonNull List<HNSUserEntity> hnsUsers,
                                               final int page, final int pageCount) throws BotException {
        EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle(R.Strings.ui("hide_n_seek_player_database"));
        embedBuilder.setDescription(R.Strings.ui("page_s_of_s", page + 1, pageCount));
        embedBuilder.setColor(Options.getColorNormal());

        for (int entry = 0; entry < hnsUsers.size(); entry++) {
            String username = MCHelper.getUsername(profileRepository, hnsUsers.get(entry).getUuid());
            if (username == null) continue;
            embedBuilder.addField("#" + (BuildConfig.entriesPerPage * page + entry + 1), username, false);
        }
        return embedBuilder.build();
    }

    @Contract(value = "_, _, _, _ -> new")
    public @NonNull MessageEmbed createPgList(@NonNull final ProfileRepository profileRepository, @NonNull final List<PGUserEntity> pgUsers,
                                              final int page, final int pageCount) throws BotException {
        EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle(R.Strings.ui("partygames_player_database"));
        embedBuilder.setDescription(R.Strings.ui("page_s_of_s", page + 1, pageCount));
        embedBuilder.setColor(Options.getColorNormal());

        for (int entry = 0; entry < pgUsers.size(); entry++) {
            String username = MCHelper.getUsername(profileRepository, pgUsers.get(entry).getUuid());
            if (username == null) continue;
            embedBuilder.addField("#" + (BuildConfig.entriesPerPage * page + entry + 1), username, false);
        }
        return embedBuilder.build();
    }
}
