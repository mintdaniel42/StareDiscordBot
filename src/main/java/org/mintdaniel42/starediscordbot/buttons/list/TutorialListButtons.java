package org.mintdaniel42.starediscordbot.buttons.list;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.buttons.ButtonAdapter;
import org.mintdaniel42.starediscordbot.buttons.misc.TutorialSuggestionButtons;
import org.mintdaniel42.starediscordbot.data.TutorialModel;
import org.mintdaniel42.starediscordbot.embeds.TutorialEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;

public final class TutorialListButtons implements ButtonAdapter {
	@Contract(pure = true, value = "_ -> new")
	public static @NonNull ActionRow create(@NonNull final TutorialModel tutorialModel) {
		final var tutorialList = Arrays.asList(R.Tutorials.list());
		final var index = tutorialList.stream()
				.sorted()
				.toList()
				.indexOf(tutorialModel);
		return ActionRow.of(
				Button.primary(
								"%s:%s:list".formatted("tutorial", tutorialList.get(index > 0 ? index - 1 : 0).getId()),
								R.Strings.ui("back_s", tutorialList.get(index > 0 ? index - 1 : 0).getTitle())
						).withEmoji(R.Emojis.arrowLeft)
						.withDisabled(index < 1),
				Button.primary(
								"%s:%s:list".formatted("tutorial", tutorialList.get(index < tutorialList.size() - 1 ? index + 1 : index).getId()),
								R.Strings.ui("continue_s", tutorialList.get(index < tutorialList.size() - 1 ? index + 1 : index).getTitle())
						).withEmoji(R.Emojis.arrowRight)
						.withDisabled(index >= tutorialList.size() - 1)
		);
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
		if (R.Tutorials.get(event.getComponentId().split(":")[1]) instanceof final TutorialModel tutorialModel) {
			return interactionHook.editOriginalEmbeds(TutorialEmbed.of(tutorialModel))
					.setComponents(TutorialListButtons.create(tutorialModel),
							TutorialSuggestionButtons.create(tutorialModel));
		} else return interactionHook.editOriginal(R.Strings.ui("this_page_does_not_exist"));
	}
}