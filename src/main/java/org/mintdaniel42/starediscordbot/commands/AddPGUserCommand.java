package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.Bot;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.PGUserModel;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;

import java.util.UUID;

@RequiredArgsConstructor
public final class AddPGUserCommand extends ListenerAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getFullCommandName().equals(Bot.CommandNames.addpguser.name())) return;

        // check maintenance
        if (Options.isInMaintenance()) {
            event.reply(Bot.strings.getString("the_bot_is_currently_in_maintenance_mode")).queue();
            return;
        }

        // check permission level
        if (DCHelper.lacksRole(event.getMember(), Options.getCreateRoleId())) {
            event.reply(Bot.strings.getString("you_do_not_have_the_permission_to_use_this_command")).queue();
            return;
        }

        // check if username is given
        OptionMapping username = event.getOption("username");
        if (username == null) {
            event.reply(Bot.strings.getString("this_username_does_not_exist")).queue();
            return;
        }

        // check if username exists
        UUID uuid = MCHelper.getUuid(databaseAdapter, username.getAsString());
        if (uuid == null) event.reply(Bot.strings.getString("this_username_does_not_exist")).queue();
        else if (databaseAdapter.hasPgUser(uuid)) event.reply(Bot.strings.getString("this_user_entry_already_exists")).queue();
        else {
            PGUserModel.PGUserModelBuilder builder = PGUserModel.builder();
            builder.uuid(uuid);

            for (OptionMapping optionMapping : event.getOptions()) {
                switch (optionMapping.getName()) {
                    case "rating" -> builder.rating(optionMapping.getAsString());
                    case "points" -> builder.points(Math.round(optionMapping.getAsDouble()));
                    case "joined" -> builder.joined(optionMapping.getAsString());
                    case "luck" -> builder.luck(optionMapping.getAsDouble());
                    case "quota" -> builder.quota(optionMapping.getAsDouble());
                    case "winrate" -> builder.winrate(optionMapping.getAsDouble());
                }
            }

            // add the entry
            PGUserModel pgUserModel = builder.build();
            if (!databaseAdapter.addPgUser(pgUserModel)) event.reply(Bot.strings.getString("the_entry_could_not_be_created")).queue();
            else {
                event.reply(Bot.strings.getString("the_entry_was_successfully_created")).setEmbeds(UserEmbed.of(databaseAdapter, pgUserModel)).queue();
            }
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (!event.getFullCommandName().equals(Bot.CommandNames.addpguser.name())) return;

        OptionMapping pointsMapping = event.getOption("points");
        OptionMapping usernameMapping = event.getOption("username");
        String focusedOption = event.getFocusedOption().getName();

        if (focusedOption.equals("points") && pointsMapping != null && !pointsMapping.getAsString().isBlank()) event.replyChoices(DCHelper.autocompleteDouble(pointsMapping.getAsString())).queue();
        else if (focusedOption.equals("username") && usernameMapping != null) event.replyChoiceStrings(DCHelper.autoCompleteUsername(databaseAdapter, usernameMapping.getAsString())).queue();
    }
}
