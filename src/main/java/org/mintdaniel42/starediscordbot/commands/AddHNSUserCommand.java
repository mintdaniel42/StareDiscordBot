package org.mintdaniel42.starediscordbot.commands;

import fr.leonarddoo.dba.annotation.Command;
import fr.leonarddoo.dba.annotation.Option;
import fr.leonarddoo.dba.element.DBACommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.mintdaniel42.starediscordbot.Bot;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.HNSUserModel;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;

import java.util.UUID;

@Command(name = "addhnsuser", description = "Einen neuen Eintrag in der Hide 'n' Seek Datenbank anlegen")
@Option(type = OptionType.STRING, name = "username", description = "Spielername", required = true, autocomplete = true)
@Option(type = OptionType.NUMBER, name = "points", description = "Punkte", required = true, autocomplete = true)
@Option(type = OptionType.STRING, name = "rating",  description = "Bewertung")
@Option(type = OptionType.STRING, name = "joined", description = "Im Modus seit")
@Option(type = OptionType.BOOLEAN, name = "secondary", description = "Zweitaccount")
@Option(type = OptionType.BOOLEAN, name = "banned", description = "Gebannt")
@Option(type = OptionType.BOOLEAN, name = "cheating", description = "Cheatet")
@RequiredArgsConstructor
public final class AddHNSUserCommand implements DBACommand {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // check maintenance
        if (Options.isInMaintenance()) {
            event.reply(Bot.strings.getString("the_bot_is_currently_in_maintenance_mode")).queue();
            return;
        }

        // check permission level
        if (!DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
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
        else if (databaseAdapter.hasHnsUser(uuid)) event.reply(Bot.strings.getString("this_user_entry_already_exists")).queue();
        else {
            HNSUserModel.HNSUserModelBuilder builder = HNSUserModel.builder();
            builder.uuid(uuid);

            for (OptionMapping optionMapping : event.getOptions()) {
                switch (optionMapping.getName()) {
                    case "rating" -> builder.rating(optionMapping.getAsString());
                    case "points" -> builder.points(Math.round(optionMapping.getAsDouble()));
                    case "joined" -> builder.joined(optionMapping.getAsString());
                    case "secondary" -> builder.secondary(optionMapping.getAsBoolean());
                    case "banned" -> builder.banned(optionMapping.getAsBoolean());
                    case "cheating" -> builder.cheating(optionMapping.getAsBoolean());
                }
            }

            // add the entry
            HNSUserModel hnsUserModel = builder.build();
            if (!databaseAdapter.addHnsUser(hnsUserModel)) event.reply(Bot.strings.getString("the_entry_could_not_be_created")).queue();
            else {
                event.reply(Bot.strings.getString("the_entry_was_successfully_created")).setEmbeds(UserEmbed.of(databaseAdapter, hnsUserModel)).queue();
            }
        }
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        OptionMapping pointsMapping = event.getOption("points");
        OptionMapping usernameMapping = event.getOption("username");
        String focusedOption = event.getFocusedOption().getName();

        if (focusedOption.equals("points") && pointsMapping != null && !pointsMapping.getAsString().isBlank()) event.replyChoices(DCHelper.autocompleteDouble(pointsMapping.getAsString())).queue();
        else if (focusedOption.equals("username") && usernameMapping != null) event.replyChoiceStrings(DCHelper.autoCompleteUsername(databaseAdapter, usernameMapping.getAsString())).queue();
    }
}