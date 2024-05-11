package org.mintdaniel42.starediscordbot.commands;

import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.InteractPage;
import fr.leonarddoo.dba.annotation.Command;
import fr.leonarddoo.dba.annotation.Option;
import fr.leonarddoo.dba.element.DBACommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.Bot;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.HNSUserModel;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.utils.Options;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@Command(name = "listhnsusers", description = "Alle Hide 'n' Seek Einträge auflisten")
@Option(type = OptionType.INTEGER, name = "page", description = "Seite der Einträge", autocomplete = true)
@RequiredArgsConstructor
public final class ListHNSUsersCommand implements DBACommand {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // check maintenance
        if (Options.isInMaintenance()) {
            event.reply(Bot.strings.getString("the_bot_is_currently_in_maintenance_mode")).queue();
            return;
        }

        OptionMapping pageOptionMapping = event.getOption("page");
        int page = (pageOptionMapping) != null ? pageOptionMapping.getAsInt() - 1 : 0;
        if (page < 0 || page >= databaseAdapter.getHnsPages()){
            event.reply(Bot.strings.getString("this_page_does_not_exist")).queue();
            return;
        }

        List<HNSUserModel> entriesList = databaseAdapter.getHnsUserList(page);
        if (entriesList != null && !entriesList.isEmpty()) {
            event.deferReply().queue(interactionHook -> {
                interactionHook.editOriginalEmbeds(ListEmbed.createHnsList(databaseAdapter, entriesList, page))
                        .queue(success -> Pages.lazyPaginate(success, this::getPage, true, 60, TimeUnit.SECONDS));
            });
        } else {
            event.reply(Bot.strings.getString("no_entries_available")).queue();
        }
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        OptionMapping pageMapping = event.getOption("page");
        String page = pageMapping != null ? pageMapping.getAsString() : "";
        event.replyChoiceLongs(LongStream.range(0, Math.min(databaseAdapter.getHnsPages(), 25))
                .map(operand -> operand + 1)
                .boxed()
                .filter(operand -> String.valueOf(operand).startsWith(page))
                .toList()).queue();
    }

    private @Nullable InteractPage getPage(int page) {
        System.out.println(page);
        List<HNSUserModel> hnsUserModels = databaseAdapter.getHnsUserList(page);
        if (hnsUserModels == null) return null;
        MessageEmbed list = ListEmbed.createHnsList(databaseAdapter, hnsUserModels, page);
        return InteractPage.of(list);
    }
}