package org.mintdaniel42.starediscordbot.commands.hns;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.buttons.misc.GroupButton;
import org.mintdaniel42.starediscordbot.buttons.misc.HNSShowButton;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.embeds.user.hns.HNSBasicUserEmbed;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
@Singleton
public final class HNSShowCommand implements CommandAdapter {
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final UsernameRepository usernameRepository;
	@NonNull private final GroupRepository groupRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof final OptionMapping usernameMapping) {
			if (MCHelper.getUuid(usernameRepository, usernameMapping.getAsString()) instanceof final UUID uuid) {
				final var hnsUserOptional = hnsUserRepository.selectByUUID(uuid);
				final var userOptional = userRepository.selectByUUID(uuid);
				final var usernameOptional = usernameRepository.selectByUUID(uuid);
				final var userOptional = userRepository.selectById(uuid);
				if (hnsUserOptional.isPresent() && userOptional.isPresent() && usernameOptional.isPresent()) {
					final var groupOptional = groupRepository.selectById(userOptional.get().getGroupTag());
					return interactionHook.editOriginalEmbeds(HNSBasicUserEmbed.of(hnsUserOptional.get(), userOptional.get(), usernameOptional.get(), false))
							.setComponents(ActionRow.of(HNSShowButton.create(HNSShowButton.Type.more, uuid),
									groupOptional.map(GroupButton::create)
											.orElseGet(GroupButton::disabled)));
				} else return interactionHook.editOriginal(R.Strings.ui("this_user_entry_does_not_exist"));
			} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
