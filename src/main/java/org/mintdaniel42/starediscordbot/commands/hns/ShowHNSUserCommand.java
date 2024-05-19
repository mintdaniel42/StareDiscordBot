package org.mintdaniel42.starediscordbot.commands.hns;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.HNSUserModel;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public final class ShowHNSUserCommand extends ListenerAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        if (!event.getFullCommandName().equals("hns show")) return;

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
        HNSUserModel hnsUserModel;

        if (uuid != null && (hnsUserModel = databaseAdapter.getHnsUser(uuid)) != null) {
            event.deferReply().queue(interactionHook -> interactionHook
                    .editOriginalEmbeds(UserEmbed.of(databaseAdapter, hnsUserModel)).setComponents(
                            ActionRow.of(
                                    Button.primary(String.format("detailedhns:%s", uuid), R.string("more_info")),
                                    Button.primary(String.format("group:%s", ""), R.string("show_group"))
                            )
                    ).queue());
        } else {
            event.reply(R.string("this_username_or_entry_does_not_exist")).queue();
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull final CommandAutoCompleteInteractionEvent event) {
        if (!event.getFullCommandName().equals("hns show")) return;

        OptionMapping usernameMapping = event.getOption("username");
        if (usernameMapping != null) event.replyChoiceStrings(DCHelper.autoCompleteUsername(databaseAdapter, usernameMapping.getAsString())).queue();
    }
}
