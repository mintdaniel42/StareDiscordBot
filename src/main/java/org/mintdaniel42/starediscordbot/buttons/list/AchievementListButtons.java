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
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.buttons.ButtonAdapter;
import org.mintdaniel42.starediscordbot.data.entity.AchievementEntity;
import org.mintdaniel42.starediscordbot.data.repository.AchievementRepository;
import org.mintdaniel42.starediscordbot.embeds.AchievementEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class AchievementListButtons implements ButtonAdapter {
	@NonNull private final AchievementRepository achievementRepository;

	@Contract(pure = true, value = "_, _, _, _ -> new")
	public static @NonNull ActionRow create(@Nullable final AchievementEntity.Type type, final int points, final int page, final long maxPages) {
		return ActionRow.of(
				Button.primary(
								"achievement:%s:%s:%s".formatted(type, points, page - 1),
								R.Strings.ui("previous_page")
						).withEmoji(R.Emojis.arrowLeft)
						.withDisabled(page <= 0),
				Button.primary(
								"achievement:%s:%s:%s".formatted(type, points, page + 1),
								R.Strings.ui("next_page")
						).withEmoji(R.Emojis.arrowRight)
						.withDisabled(page >= maxPages - 1)
		);
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
		final var buttonParts = event.getComponentId().split(":");
		final AchievementEntity.Type type;
		final int points = Integer.parseInt(buttonParts[2]);
		final int page = Integer.parseInt(buttonParts[3]);
		if (!buttonParts[1].equals("null") && !buttonParts[1].isBlank()) {
			type = AchievementEntity.Type.valueOf(buttonParts[1]);
		} else type = null;
		final var achievements = achievementRepository.selectByTypeAndPoints(type, points);
		if (page < achievements.size()) {
			return interactionHook.editOriginalEmbeds(AchievementEmbed.of(achievements.get(page), page + 1, achievements.size()))
					.setComponents(AchievementListButtons.create(type, points, page, achievements.size()));
		} else return interactionHook.editOriginal(R.Strings.ui("this_page_does_not_exist"));
	}
}
