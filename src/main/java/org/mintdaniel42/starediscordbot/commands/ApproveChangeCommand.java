package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.mintdaniel42.starediscordbot.Bot;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.RequestModel;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;

import java.time.Instant;
import java.util.Objects;

@RequiredArgsConstructor
public final class ApproveChangeCommand extends ListenerAdapter {
    @NotNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
        if (!event.getFullCommandName().equals(Bot.CommandNames.approvechange.name())) return;

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
    public void onCommandAutoCompleteInteraction(@NonNull final CommandAutoCompleteInteractionEvent event) {
        if (!event.getFullCommandName().equals(Bot.CommandNames.approvechange.name())) return;

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
