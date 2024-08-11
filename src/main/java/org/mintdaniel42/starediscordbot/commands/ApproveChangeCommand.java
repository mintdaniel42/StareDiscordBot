package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.R;

@Slf4j
@RequiredArgsConstructor
public final class ApproveChangeCommand implements CommandAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("id") instanceof OptionMapping idMapping) {
			if (databaseAdapter.mergeRequest(idMapping.getAsLong())) {
				return interactionHook.editOriginal(R.Strings.ui("request_was_successfully_merged"));
			} else return interactionHook.editOriginal(R.Strings.ui("request_could_not_be_merged"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
