package org.mintdaniel42.starediscordbot.buttons.list;

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
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@Singleton
public final class HNSListButtons implements ButtonAdapter {
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UsernameRepository usernameRepository;

	@Contract(pure = true, value = "_, _ -> new")
	public static @NonNull ActionRow create(final int page, final long maxPages) {
		return ActionRow.of(
				Button.primary("list:hns:%s".formatted(page - 1), R.Strings.ui("previous_page"))
						.withEmoji(R.Emojis.arrowLeft)
						.withDisabled(page <= 0),
				Button.primary("list:hns:%s".formatted(page + 1), R.Strings.ui("next_page"))
						.withEmoji(R.Emojis.arrowRight)
						.withDisabled(page >= maxPages - 1)
		);
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
		final var buttonParts = event.getComponentId().split(":");
		final var page = Integer.parseInt(buttonParts[buttonParts.length - 1]);
		return interactionHook.editOriginalEmbeds(ListEmbed.createHnsList(usernameRepository, hnsUserRepository.selectByPage(page), page, hnsUserRepository.countPages()))
				.setComponents(create(page, hnsUserRepository.countPages()));
	}
}
