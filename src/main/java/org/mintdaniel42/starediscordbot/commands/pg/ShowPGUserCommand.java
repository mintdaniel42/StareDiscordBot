package org.mintdaniel42.starediscordbot.commands.pg;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.build.Features;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.PGUserModel;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public final class ShowPGUserCommand extends ListenerAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
        if (!event.getFullCommandName().equals("pg show")) return;

        // check maintenance
        if (Options.isInMaintenance()) {
            event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
            return;
        }

        OptionMapping username = event.getOption("username");
        if (username == null) {
            event.reply(R.string("this_username_does_not_exist")).queue();
            return;
        }

        UUID uuid = MCHelper.getUuid(databaseAdapter, username.getAsString());
        PGUserModel pgUserModel;

        if (uuid != null && (pgUserModel = databaseAdapter.getPgUser(uuid)) != null) {
            event.deferReply()
                    .queue(interactionHook -> {
                        WebhookMessageEditAction<Message> webhookMessageEditAction = interactionHook.editOriginalEmbeds(UserEmbed.of(databaseAdapter, pgUserModel));
                        if (Features.dev) webhookMessageEditAction.setComponents(ActionRow.of(Button.primary(String.format("group:%s", uuid), R.string("show_group")).withDisabled(!databaseAdapter.hasGroupFor(uuid)))).queue();
                        webhookMessageEditAction.queue();
                    });
        } else {
            event.reply(R.string("this_username_or_entry_does_not_exist")).queue();
        }
    }
}
