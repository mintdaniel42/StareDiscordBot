package org.mintdaniel42.starediscordbot.buttons.misc;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.buttons.ButtonAdapter;
import org.mintdaniel42.starediscordbot.buttons.list.GroupListButtons;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
@Singleton
public final class GroupButton implements ButtonAdapter {
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final ProfileRepository profileRepository;

	public static @NonNull Button create(@NonNull final GroupEntity group) {
		return Button.primary(
						String.format("group:%s", group.getTag()),
						R.Strings.ui("show_group"))
				.withEmoji(R.Emojis.group);
	}

	public static @NonNull Button disabled() {
		return Button.primary(
						UUID.randomUUID().toString(),
						R.Strings.ui("show_group"))
				.withEmoji(R.Emojis.group)
				.withDisabled(true);
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) throws BotException {
		final var groupOptional = groupRepository.selectById(event.getComponentId().split(":")[1]);
		if (groupOptional.isPresent()) {
			final var group = groupOptional.get();
			return interactionHook.editOriginalEmbeds(GroupEmbed.of(group, userRepository, hnsUserRepository, profileRepository, 0, false))
					.setComponents(GroupListButtons.create(group, 0, (long) Math.ceil((double) userRepository.selectByGroupTag(group.getTag()).size() / BuildConfig.entriesPerPage)));
		} else return interactionHook.editOriginal(R.Strings.ui("this_group_does_not_exist"));
	}
}
