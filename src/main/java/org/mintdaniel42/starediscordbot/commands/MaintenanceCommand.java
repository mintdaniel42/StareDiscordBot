package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.Bot;
import org.mintdaniel42.starediscordbot.utils.Options;

public final class MaintenanceCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
        if (!event.getFullCommandName().equals(Bot.CommandNames.maintenance.name())) return;
        Member member = event.getMember();
        if (member != null && !member.hasPermission(Permission.ADMINISTRATOR)) event.reply(Bot.strings.getString("you_do_not_have_the_permission_to_use_this_command")).queue();
        else {
            OptionMapping activeMapping = event.getOption("active");
            event.reply(String.format(Bot.strings.getString("maintenance_is_now_set_to_s"), activeMapping != null ? activeMapping.getAsBoolean() : Options.isInMaintenance())).queue();
            Options.setInMaintenance(activeMapping != null ? activeMapping.getAsBoolean() : Options.isInMaintenance());
        }
    }
}
