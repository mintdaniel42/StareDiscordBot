package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.mintdaniel42.starediscordbot.Bot;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.HNSUserModel;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;
import java.util.stream.LongStream;

@RequiredArgsConstructor
public final class ListHNSUsersCommand extends ListenerAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        if (!event.getFullCommandName().equals(Bot.CommandNames.listhnsusers.name())) return;

        // check maintenance
        if (Options.isInMaintenance()) {
            event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
            return;
        }

        OptionMapping pageOptionMapping = event.getOption("page");
        int page = (pageOptionMapping) != null ? pageOptionMapping.getAsInt() - 1 : 0;
        if (page < 0 || page >= databaseAdapter.getHnsPages()){
            event.reply(R.string("this_page_does_not_exist")).queue();
            return;
        }

        List<HNSUserModel> entriesList = databaseAdapter.getHnsUserList(page);
        if (entriesList != null && !entriesList.isEmpty()) {
            event.deferReply().queue(interactionHook -> interactionHook.editOriginalEmbeds(ListEmbed.createHnsList(databaseAdapter, entriesList, page))
                    .setComponents(ActionRow.of(
                            Button.primary(String.format("previous:hns:%s", page), R.string("previous_page")).withDisabled(page < 1),
                            Button.primary(String.format("next:hns:%s", page), R.string("next_page")).withDisabled(page + 1 >= databaseAdapter.getHnsPages())
                    ))
                    .queue());
        } else {
            event.reply(R.string("no_entries_available")).queue();
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull final CommandAutoCompleteInteractionEvent event) {
        if (!event.getFullCommandName().equals(Bot.CommandNames.listhnsusers.name())) return;

        OptionMapping pageMapping = event.getOption("page");
        String page = pageMapping != null ? pageMapping.getAsString() : "";
        event.replyChoiceLongs(LongStream.range(0, Math.min(databaseAdapter.getHnsPages(), 25))
                .map(operand -> operand + 1)
                .boxed()
                .filter(operand -> String.valueOf(operand).startsWith(page))
                .toList()).queue();
    }
}