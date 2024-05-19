package org.mintdaniel42.starediscordbot.commands;


import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.mintdaniel42.starediscordbot.Bot;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.HNSUserModel;
import org.mintdaniel42.starediscordbot.db.RequestModel;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public final class EditHNSUserCommand extends ListenerAdapter {
    @NotNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        if (!event.getFullCommandName().equals(Bot.CommandNames.edithnsuser.name())) return;

        // check maintenance
        if (Options.isInMaintenance()) {
            event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
            return;
        }

        // check if username is given
        OptionMapping username = event.getOption("username");
        if (username == null) {
            event.reply(R.string("this_username_does_not_exist")).queue();
            return;
        }

        // check if more than username option is given
        if (event.getOptions().size() <= 1) {
            event.reply(R.string("the_entry_was_not_updated")).queue();
            return;
        }

        // check if username exists
        UUID uuid = MCHelper.getUuid(databaseAdapter, username.getAsString());
        if (uuid == null) event.reply(R.string("this_username_does_not_exist")).queue();
        else if (!databaseAdapter.hasHnsUser(uuid)) event.reply(R.string("this_user_entry_does_not_exist")).queue();
        else {
            HNSUserModel.HNSUserModelBuilder builder = Objects.requireNonNull(databaseAdapter.getHnsUser(uuid)).toBuilder();

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

            // update the model
            HNSUserModel hnsUserModel = builder.build();

            // check permission level & edit via request
            if (DCHelper.lacksRole(event.getMember(), Options.getEditRoleId()) && DCHelper.lacksRole(event.getMember(), Options.getCreateRoleId())) {
                long timestamp = Instant.now().toEpochMilli();
                if (!databaseAdapter.addRequest(RequestModel.from(timestamp, hnsUserModel))) {
                    event.reply(R.string("the_entry_could_not_be_updated")).queue();
                } else {
                    event.reply(R.string("the_entry_change_was_successfully_requested")).queue();
                    event.getGuild()
                            .getTextChannelById(Options.getRequestChannelId())
                            .sendMessage(String.format(
                                    R.string("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s"),
                                    event.getMember().getAsMention(),
                                    timestamp))
                            .addActionRow(Button.primary(String.format("approve:%s", timestamp), R.string("approve_this_change")))
                            .addEmbeds(UserEmbed.of(databaseAdapter, hnsUserModel)).queue();
                }
                return;
            }

            // edit directly
            if (databaseAdapter.editHnsUser(hnsUserModel) == 0) event.reply(R.string("the_entry_could_not_be_updated")).queue();
            else {
                event.reply(R.string("the_entry_was_successfully_updated")).setEmbeds(UserEmbed.of(databaseAdapter, hnsUserModel)).queue();
            }
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull final CommandAutoCompleteInteractionEvent event) {
        if (!event.getFullCommandName().equals(Bot.CommandNames.edithnsuser.name())) return;

        OptionMapping pointsMapping = event.getOption("points");
        OptionMapping usernameMapping = event.getOption("username");
        String focusedOption = event.getFocusedOption().getName();

        if (focusedOption.equals("points") && pointsMapping != null && !pointsMapping.getAsString().isBlank()) event.replyChoices(DCHelper.autocompleteDouble(pointsMapping.getAsString())).queue();
        else if (focusedOption.equals("username") && usernameMapping != null) event.replyChoiceStrings(DCHelper.autoCompleteUsername(databaseAdapter, usernameMapping.getAsString())).queue();
    }
}
