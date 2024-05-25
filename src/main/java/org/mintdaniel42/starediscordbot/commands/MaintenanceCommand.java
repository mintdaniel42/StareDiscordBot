package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

public final class MaintenanceCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equals("maintenance")) {
            if (event.getMember() instanceof Member member) {
                if (member.hasPermission(Permission.ADMINISTRATOR)) {
                    if (event.getOption("active") instanceof OptionMapping activeMapping) {
                        Options.setInMaintenance(activeMapping.getAsBoolean());
                        event.reply(R.string("maintenance_is_now_set_to_s",
                                activeMapping.getAsBoolean()))
                                .setEphemeral(true)
                                .queue();
                        if (activeMapping.getAsBoolean()) {
                            event.getJDA()
                                    .getPresence()
                                    .setPresence(OnlineStatus.DO_NOT_DISTURB,
                                            Activity.customStatus(R.string("under_maintenance")));
                        } else event.getJDA().getPresence().setPresence(null, null);
                    } else event.reply(R.string("your_command_was_incomplete")).queue();
                } else event.reply(R.string("you_do_not_have_the_permission_to_use_this_command")).queue();
            }
        }
    }
}
