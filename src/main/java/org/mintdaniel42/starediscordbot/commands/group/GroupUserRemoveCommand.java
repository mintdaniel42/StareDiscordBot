package org.mintdaniel42.starediscordbot.commands.group;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.R;
import org.mintdaniel42.starediscordbot.utils.Status;

import java.util.UUID;

@RequiredArgsConstructor
@Singleton
public final class GroupUserRemoveCommand implements CommandAdapter {
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final UsernameRepository usernameRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof final OptionMapping usernameMapping) {
			if (MCHelper.getUuid(usernameRepository, usernameMapping.getAsString()) instanceof UUID uuid) {
				final var userOptional = userRepository.selectByUUID(uuid);
				final var usernameOptional = usernameRepository.selectByUUID(uuid);
				if (userOptional.isPresent() && usernameOptional.isPresent()) {
					final var user = userOptional.get();
					final var username = usernameOptional.get();
					if (user.getGroupTag() == null) {
						return interactionHook.editOriginal(R.Strings.ui("the_user_s_is_not_in_any_group", username.getUsername()));
					} else {
						final var groupOptional = groupRepository.selectByTag(user.getGroupTag());
						if (groupOptional.isPresent() && userRepository.update(user.toBuilder()
										.groupTag(null)
										.build())
								.equals(Status.SUCCESS)) {
							return interactionHook.editOriginal(R.Strings.ui("the_user_s_was_removed_from_the_group_s",
									MCHelper.getUsername(usernameRepository, uuid),
									groupOptional.get().getName()));
						} else return interactionHook.editOriginal(R.Strings.ui("this_group_does_not_exist"));
					}
				} else return interactionHook.editOriginal(R.Strings.ui("this_user_entry_does_not_exist"));
			} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
