package org.mintdaniel42.starediscordbot.buttons.list;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.buttons.ButtonAdapter;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class GroupListButtons implements ButtonAdapter {
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final UsernameRepository usernameRepository;

	@Contract(pure = true, value = "_, _, _ -> new")
	public static @NonNull ActionRow create(@NonNull final GroupEntity group, final int page, final long maxPages) {
		return ActionRow.of(
				Button.primary(
								"group:%s:%s".formatted(group.getTag(), page - 1),
								R.Strings.ui("previous_page")
						).withEmoji(R.Emojis.arrowLeft)
						.withDisabled(page <= 0),
				Button.primary(
								"group:%s:%s".formatted(group.getTag(), page + 1),
								R.Strings.ui("next_page")
						).withEmoji(R.Emojis.arrowRight)
						.withDisabled(page >= maxPages - 1)
		);
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
		final var buttonParts = event.getComponentId().split(":");
		final var page = Integer.parseInt(buttonParts[2]);
		final var groupOptional = groupRepository.selectByTag(buttonParts[1]);
		if (groupOptional.isPresent()) {
			final var group = groupOptional.get();
			return interactionHook.editOriginalEmbeds(GroupEmbed.of(groupOptional.get(), userRepository, hnsUserRepository, usernameRepository, page, false))
					.setComponents(create(group, page, (long) Math.ceil((double) userRepository.selectByGroupTag(group.getTag()).size() / BuildConfig.entriesPerPage)));
		} else return interactionHook.editOriginal(R.Strings.ui("this_page_does_not_exist"));
	}
}
