package org.mintdaniel42.starediscordbot.buttons.misc;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.buttons.ButtonAdapter;
import org.mintdaniel42.starediscordbot.data.TutorialModel;
import org.mintdaniel42.starediscordbot.embeds.TutorialEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public final class TutorialSuggestionButtons implements ButtonAdapter {
	public static @NonNull ActionRow create(@NonNull final TutorialModel tutorialModel) {
		if (tutorialModel.getSimilar().length > 0) {
			final var buttons = Arrays.stream(tutorialModel.getSimilar())
					.map(R.Tutorials::get)
					.filter(Objects::nonNull)
					.limit(5)
					.map(similar -> Button.secondary("tutorial:%s:suggestion".formatted(similar.getId()), similar.getTitle()))
					.toArray(ItemComponent[]::new);
			if (buttons.length > 0) return ActionRow.of(buttons);
		}
		return ActionRow.of(Button.secondary(
				UUID.randomUUID().toString(),
				R.Strings.ui("no_suggestions_available")
		).withDisabled(true));
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
		if (R.Tutorials.get(event.getComponentId().split(":")[1]) instanceof final TutorialModel tutorialModel) {
			return interactionHook.editOriginalEmbeds(TutorialEmbed.of(tutorialModel))
					.setComponents(TutorialSuggestionButtons.create(tutorialModel));
		} else return interactionHook.editOriginal(R.Strings.ui("this_page_does_not_exist"));
	}
}
