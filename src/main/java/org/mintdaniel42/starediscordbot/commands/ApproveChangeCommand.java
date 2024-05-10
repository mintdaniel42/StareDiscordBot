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
import org.mintdaniel42.starediscordbot.db.RequestModel;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;

import java.time.Instant;
import java.util.Objects;

@Command(name = "approve", description = "Änderungen freigeben")
@Option(type = OptionType.INTEGER, name = "id", description = "ID der Änderung", required = true, autocomplete = true)
@RequiredArgsConstructor
public final class ApproveChangeCommand implements DBACommand {
    private final DatabaseAdapter databaseAdapter;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // check maintenance
        if (Options.isInMaintenance()) {
            event.reply(Bot.strings.getString("the_bot_is_currently_in_maintenance_mode")).queue();
            return;
        }

        // check permission level
        if (DCHelper.lacksRole(event.getMember(), Options.getEditRoleId()) && DCHelper.lacksRole(event.getMember(), Options.getCreateRoleId())) {
            event.reply(Bot.strings.getString("you_do_not_have_the_permission_to_use_this_command")).queue();
            return;
        }

        // try to merge the change
        OptionMapping idMapping = event.getOption("id");
        if (!(idMapping == null) && databaseAdapter.mergeRequest(idMapping.getAsLong())) {
            event.reply(Bot.strings.getString("request_was_successfully_merged")).queue();
        } else {
            event.reply(Bot.strings.getString("request_could_not_be_merged")).queue();
        }
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        long now = Instant.now().toEpochMilli();
        OptionMapping idMapping = event.getOption("id");
        String id = idMapping != null ? idMapping.getAsString() : "";
        event.replyChoiceLongs(Objects.requireNonNull(databaseAdapter.getPendingRequests())
                .stream()
                .map(RequestModel::getTimestamp)
                .filter(timestamp -> timestamp > now - Options.getMaxRequestAge())
                .filter(timestamp -> String.valueOf(timestamp).startsWith(id))
                .limit(25)
                .toList())
                .queue();
    }
}
