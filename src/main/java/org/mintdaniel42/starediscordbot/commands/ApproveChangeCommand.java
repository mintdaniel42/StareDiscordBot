package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.embeds.ErrorEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@Slf4j
@RequiredArgsConstructor
public final class ApproveChangeCommand extends ListenerAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equals("approve")) {
            if (!Options.isInMaintenance()) {
                try {
                    if (DCHelper.hasRole(event.getMember(), Options.getEditRoleId()) || DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
                        if (event.getOption("id") instanceof OptionMapping idMapping) {
                            if (databaseAdapter.mergeRequest(idMapping.getAsLong())) {
								event.reply(R.Strings.ui("request_was_successfully_merged")).queue();
							} else event.reply(R.Strings.ui("request_could_not_be_merged")).queue();
						} else event.reply(R.Strings.ui("your_command_was_incomplete")).queue();
					} else event.reply(R.Strings.ui("you_do_not_have_the_permission_to_use_this_command")).queue();
                } catch (Exception e) {
                    log.error(R.logging("the_command_s_caused_an_error", event.getFullCommandName()), e);
                    event.replyEmbeds(ErrorEmbed.of(event.getInteraction(), e)).queue();
                }
			} else event.reply(R.Strings.ui("the_bot_is_currently_in_maintenance_mode")).queue();
        }
    }
}
