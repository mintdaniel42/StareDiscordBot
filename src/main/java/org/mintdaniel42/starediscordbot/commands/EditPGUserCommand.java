package org.mintdaniel42.starediscordbot.commands;

import fr.leonarddoo.dba.annotation.Command;
import fr.leonarddoo.dba.annotation.Option;
import fr.leonarddoo.dba.element.DBACommand;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.mintdaniel42.starediscordbot.Bot;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.PGUserModel;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Command(name = "editpguser", description = "Partygames Eintrag bearbeiten")
@Option(type = OptionType.STRING, name = "username", description = "Spielername", required = true, autocomplete = true)
@Option(type = OptionType.NUMBER, name = "points", description = "Punkte", autocomplete = true)
@Option(type = OptionType.STRING, name = "rating",  description = "Bewertung")
@Option(type = OptionType.STRING, name = "joined", description = "Im Modus seit")
@Option(type = OptionType.NUMBER, name = "luck", description = "Würfelglück")
@Option(type = OptionType.NUMBER, name = "quota", description = "Minispiel Gewinnquote")
@Option(type = OptionType.NUMBER, name = "winrate", description = "Gewinnrate")
public final class EditPGUserCommand implements DBACommand {
    private final DatabaseAdapter databaseAdapter;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // check maintenance
        if (Options.isInMaintenance()) {
            event.reply(Bot.strings.getString("the_bot_is_currently_in_maintenance_mode")).queue();
            return;
        }

        // check permission level
        if (!DCHelper.hasRole(event.getMember(), Options.getEditRoleId())) {
            event.reply(Bot.strings.getString("you_do_not_have_the_permission_to_use_this_command")).queue();
            return;
        }

        // check if username is given
        OptionMapping username = event.getOption("username");
        if (username == null) {
            event.reply(Bot.strings.getString("this_username_does_not_exist")).queue();
            return;
        }

        // check if more than username option is given
        if (event.getOptions().size() <= 1) {
            event.reply(Bot.strings.getString("the_entry_was_not_updated")).queue();
            return;
        }

        // check if username exists
        UUID uuid = MCHelper.getUuid(databaseAdapter, username.getAsString());
        if (uuid == null) event.reply(Bot.strings.getString("this_username_does_not_exist")).queue();
        else if (!databaseAdapter.hasPgUser(uuid)) event.reply(Bot.strings.getString("this_user_entry_does_not_exist")).queue();
        else {
            PGUserModel.PGUserModelBuilder builder = Objects.requireNonNull(databaseAdapter.getPgUser(uuid)).toBuilder();

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

            // update the model
            PGUserModel pgUserModel = builder.build();
            if (databaseAdapter.editPgUser(pgUserModel) == 0) event.reply(Bot.strings.getString("the_entry_could_not_be_updated")).queue();
            else {
                event.reply(Bot.strings.getString("the_entry_was_successfully_updated")).queue();
                event.getChannel().sendMessageEmbeds(UserEmbed.of(databaseAdapter, pgUserModel)).queue();
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
