package org.mintdaniel42.starediscordbot.commands.group;

import jakarta.inject.Singleton;
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
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Singleton
public final class GroupUserShowCommand implements CommandAdapter {
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final UsernameRepository usernameRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof final OptionMapping usernameMapping) {
			if (MCHelper.getUuid(usernameRepository, usernameMapping.getAsString()) instanceof final UUID uuid) {
				final var userOptional = userRepository.selectByUUID(uuid);
				final var usernameOptional = usernameRepository.selectByUUID(uuid);
				if (userOptional.isPresent() && usernameOptional.isPresent()) {
					final var user = userOptional.get();
					final var groupOptional = user.getGroupTag() != null ? groupRepository.selectById(user.getGroupTag()) : Optional.<GroupEntity>empty();
					if (user.getGroupTag() == null || groupOptional.isEmpty()) {
						return interactionHook.editOriginal(R.Strings.ui("the_user_s_is_not_in_any_group",
								usernameOptional.get()));
					} else {
						final var group = groupOptional.get();
						return interactionHook.editOriginalEmbeds(GroupEmbed.of(group, userRepository, hnsUserRepository, usernameRepository))
								.setComponents(GroupListButtons.create(group, 0, (long) Math.ceil((double) userRepository.selectByGroupTag(group.getTag()).size() / BuildConfig.entriesPerPage)));
					}
				} else return interactionHook.editOriginal(R.Strings.ui("this_user_entry_does_not_exist"));
			} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
