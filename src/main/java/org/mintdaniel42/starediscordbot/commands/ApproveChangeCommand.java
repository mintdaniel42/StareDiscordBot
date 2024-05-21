package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class ApproveChangeCommand extends ListenerAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equals("approve")) {
            if (!Options.isInMaintenance()) {
                if (DCHelper.hasRole(event.getMember(), Options.getEditRoleId()) || DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
                    if (event.getOption("id") instanceof OptionMapping idMapping) {
                        if (databaseAdapter.mergeRequest(idMapping.getAsLong())) {
                            event.reply(R.string("request_was_successfully_merged")).queue();
                        } else event.reply(R.string("request_could_not_be_merged")).queue();
                    } else event.reply(R.string("your_command_was_incomplete")).queue();
                } else event.reply(R.string("you_do_not_have_the_permission_to_use_this_command")).queue();
            } else event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
        String[] buttonParts = event.getComponentId().split(":");
        if (buttonParts[0].equals("approve") && buttonParts.length == 2) {
            if (!Options.isInMaintenance()) {
                if (DCHelper.hasRole(event.getMember(), Options.getEditRoleId()) || DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
                    if (databaseAdapter.mergeRequest(Long.parseLong(buttonParts[1]))) {
                        event.reply(R.string("request_was_successfully_merged")).queue();
                    } else event.reply(R.string("request_could_not_be_merged")).queue();
                } else event.reply(R.string("you_do_not_have_the_permission_to_use_this_button")).queue();
            } else event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
        }
    }
}
