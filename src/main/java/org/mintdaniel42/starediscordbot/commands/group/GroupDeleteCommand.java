package org.mintdaniel42.starediscordbot.commands.group;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.utils.R;
import org.mintdaniel42.starediscordbot.utils.Status;

@RequiredArgsConstructor
public final class GroupDeleteCommand implements CommandAdapter {
	@NonNull private final GroupRepository groupRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("tag") instanceof final OptionMapping tagMapping) {
			if (groupRepository.deleteByTag(tagMapping.getAsString()).equals(Status.SUCCESS)) {
				return interactionHook.editOriginal(R.Strings.ui("the_group_was_successfully_deleted"));
			} else return interactionHook.editOriginal(R.Strings.ui("the_group_could_not_be_deleted"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
