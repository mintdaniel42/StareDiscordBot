package org.mintdaniel42.starediscordbot.commands;

import fr.leonarddoo.dba.annotation.Command;
import fr.leonarddoo.dba.annotation.Option;
import fr.leonarddoo.dba.element.DBACommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.mintdaniel42.starediscordbot.Bot;
import org.mintdaniel42.starediscordbot.utils.Options;

@Command(name = "maintenance", description = "Wartungsmodus steuern")
@Option(type = OptionType.BOOLEAN, name = "active", description = "Wartungsmodus aktiv", required = true)
public final class MaintenanceCommand implements DBACommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member != null && !member.hasPermission(Permission.ADMINISTRATOR)) event.reply(Bot.strings.getString("you_do_not_have_the_permission_to_use_this_command")).queue();
        else {
            OptionMapping activeMapping = event.getOption("active");
            event.reply(String.format(Bot.strings.getString("maintenance_is_now_set_to_s"), activeMapping != null ? activeMapping.getAsBoolean() : Options.isInMaintenance())).queue();
            Options.setInMaintenance(activeMapping != null ? activeMapping.getAsBoolean() : Options.isInMaintenance());
        }
    }
}
