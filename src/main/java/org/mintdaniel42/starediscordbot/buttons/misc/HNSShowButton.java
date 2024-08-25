package org.mintdaniel42.starediscordbot.buttons.misc;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.buttons.ButtonAdapter;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.embeds.user.hns.HNSBasicUserEmbed;
import org.mintdaniel42.starediscordbot.embeds.user.hns.HNSMoreUserEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
@Singleton
public final class HNSShowButton implements ButtonAdapter {
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final UsernameRepository usernameRepository;
	@NonNull private final GroupRepository groupRepository;

	@Contract(pure = true, value = "_, _ -> new")
	public static @NonNull Button create(@NonNull final Type type, @NonNull final UUID uuid) {
		return Button.primary(
				"hns:%s:%s".formatted(type.name(), uuid),
						R.Strings.ui(type == Type.more ? "more_info" : "basic_info"))
				.withEmoji(R.Emojis.information);
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
		final var buttonParts = event.getComponentId().split(":");
		final var current = Type.valueOf(buttonParts[1]);
		final var uuid = UUID.fromString(buttonParts[2]);
		final var hnsUserOptional = hnsUserRepository.selectByUUID(uuid);
		final var usernameOptional = usernameRepository.selectByUUID(uuid);
		final var userOptional = userRepository.selectByUUID(uuid);
		if (hnsUserOptional.isPresent() && userOptional.isPresent() && usernameOptional.isPresent()) {
			final var groupOptional = groupRepository.selectById(userOptional.get().getGroupTag());
			final var embed = current == Type.basic ?
					HNSBasicUserEmbed.of(hnsUserOptional.get(), userOptional.get(), usernameOptional.get(), false) :
					HNSMoreUserEmbed.of(hnsUserOptional.get(), userOptional.get(), groupOptional.orElse(null), usernameOptional.get(), false);
			return interactionHook.editOriginalEmbeds(embed)
					.setComponents(ActionRow.of(
							HNSShowButton.create(current == Type.basic ? Type.more : Type.basic, uuid),
							groupOptional.map(GroupButton::create)
									.orElseGet(GroupButton::disabled))
					);
		} else return interactionHook.editOriginal(R.Strings.ui("this_username_or_entry_does_not_exist"));
	}

	public enum Type {
		basic,
		more
	}
}
