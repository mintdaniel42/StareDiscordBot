package org.mintdaniel42.starediscordbot.commands.group;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.buttons.list.GroupListButtons;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class GroupShowCommand implements CommandAdapter {
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final UsernameRepository usernameRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("tag") instanceof final OptionMapping tagMapping) {
			final var groupOptional = groupRepository.selectByTag(tagMapping.getAsString());
			if (groupOptional.isPresent()) {
				final var group = groupOptional.get();
				return interactionHook.editOriginalEmbeds(GroupEmbed.of(group, userRepository, hnsUserRepository, usernameRepository))
						.setComponents(GroupListButtons.create(group, 0, (long) Math.ceil((double) userRepository.selectByGroupTag(group.getTag()).size() / BuildConfig.entriesPerPage)));
			} else return interactionHook.editOriginal(R.Strings.ui("this_group_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
