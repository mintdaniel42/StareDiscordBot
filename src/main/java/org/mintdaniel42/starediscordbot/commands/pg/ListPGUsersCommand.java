package org.mintdaniel42.starediscordbot.commands.pg;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.PGUserModel;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;

@RequiredArgsConstructor
public final class ListPGUsersCommand extends ListenerAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
        if (!event.getFullCommandName().equals("pg list")) return;

        // check maintenance
        if (Options.isInMaintenance()) {
            event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
            return;
        }

        OptionMapping pageOptionMapping = event.getOption("page");
        int page = (pageOptionMapping) != null ? pageOptionMapping.getAsInt() - 1 : 0;
        if (page < 0 || page >= databaseAdapter.getPgPages()) {
            event.reply(R.string("this_page_does_not_exist") + page + "," + databaseAdapter.getPgPages()).queue();
            return;
        }

        List<PGUserModel> entriesList = databaseAdapter.getPgUserList(page);
        if (entriesList != null && !entriesList.isEmpty()) {
            event.deferReply().queue(interactionHook -> interactionHook.editOriginalEmbeds(ListEmbed.createPgList(databaseAdapter, entriesList, page))
                    .setComponents(ActionRow.of(
                            Button.primary(String.format("previous:pg:%s", page), R.string("previous_page")).withDisabled(page < 1),
                            Button.primary(String.format("next:pg:%s", page), R.string("next_page")).withDisabled(page + 1 >= databaseAdapter.getPgPages())
                    )).queue());
        } else {
            event.reply(R.string("no_entries_available")).queue();
        }
    }
}
