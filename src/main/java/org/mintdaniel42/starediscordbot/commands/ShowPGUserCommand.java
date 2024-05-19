package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.mintdaniel42.starediscordbot.Bot;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.PGUserModel;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;

import java.util.UUID;

@RequiredArgsConstructor
public final class ShowPGUserCommand extends ListenerAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        if (!event.getFullCommandName().equals(Bot.CommandNames.showpguser.name())) return;

        // check maintenance
        if (Options.isInMaintenance()) {
            event.reply(Bot.strings.getString("the_bot_is_currently_in_maintenance_mode")).queue();
            return;
        }

        OptionMapping username = event.getOption("username");
        if (username == null) {
            event.reply(Bot.strings.getString("this_username_does_not_exist")).queue();
            return;
        }

        UUID uuid = MCHelper.getUuid(databaseAdapter, username.getAsString());
        PGUserModel pgUserModel;

        if (uuid != null && (pgUserModel = databaseAdapter.getPgUser(uuid)) != null) {
            event.deferReply().queue(interactionHook -> interactionHook.editOriginalEmbeds(UserEmbed.of(databaseAdapter, pgUserModel)).queue());
        } else {
            event.reply(Bot.strings.getString("this_username_or_entry_does_not_exist")).queue();
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull final CommandAutoCompleteInteractionEvent event) {
        if (!event.getFullCommandName().equals(Bot.CommandNames.showpguser.name())) return;

        OptionMapping usernameMapping = event.getOption("username");
        if (usernameMapping != null) event.replyChoiceStrings(DCHelper.autoCompleteUsername(databaseAdapter, usernameMapping.getAsString())).queue();
    }
}
