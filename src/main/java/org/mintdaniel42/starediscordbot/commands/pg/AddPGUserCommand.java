package org.mintdaniel42.starediscordbot.commands.pg;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.PGUserModel;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public final class AddPGUserCommand extends ListenerAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
        if (!event.getFullCommandName().equals("pg add")) return;

        // check maintenance
        if (Options.isInMaintenance()) {
            event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
            return;
        }

        // check permission level
        if (DCHelper.lacksRole(event.getMember(), Options.getCreateRoleId())) {
            event.reply(R.string("you_do_not_have_the_permission_to_use_this_command")).queue();
            return;
        }

        // check if username is given
        OptionMapping username = event.getOption("username");
        if (username == null) {
            event.reply(R.string("this_username_does_not_exist")).queue();
            return;
        }

        // check if username exists
        UUID uuid = MCHelper.getUuid(databaseAdapter, username.getAsString());
        if (uuid == null) event.reply(R.string("this_username_does_not_exist")).queue();
        else if (databaseAdapter.hasPgUser(uuid)) event.reply(R.string("this_user_entry_already_exists")).queue();
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
            if (!databaseAdapter.addPgUser(pgUserModel)) event.reply(R.string("the_entry_could_not_be_created")).queue();
            else {
                event.reply(R.string("the_entry_was_successfully_created")).setEmbeds(UserEmbed.of(databaseAdapter, pgUserModel)).queue();
            }
        }
    }
}
