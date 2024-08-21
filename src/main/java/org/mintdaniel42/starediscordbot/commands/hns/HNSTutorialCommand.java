package org.mintdaniel42.starediscordbot.commands.hns;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.buttons.list.TutorialListButtons;
import org.mintdaniel42.starediscordbot.buttons.misc.TutorialSuggestionButtons;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.entity.TutorialEntity;
import org.mintdaniel42.starediscordbot.embeds.TutorialEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;

@RequiredArgsConstructor
@Singleton
public final class HNSTutorialCommand implements CommandAdapter {

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("page") instanceof OptionMapping pageMapping) {
			if (R.Tutorials.get(pageMapping.getAsString()) instanceof TutorialEntity tutorialEntity) {
				return interactionHook.editOriginalEmbeds(TutorialEmbed.of(tutorialEntity))
						.setComponents(TutorialSuggestionButtons.create(tutorialEntity));
			} else return interactionHook.editOriginal(R.Strings.ui("this_page_does_not_exist"));
		} else {
			final var tutorialModelOptional = Arrays.stream(R.Tutorials.list())
					.sorted()
					.findFirst();
			if (tutorialModelOptional.isPresent()) {
				final var tutorialModel = tutorialModelOptional.get();
				return interactionHook.editOriginalEmbeds(TutorialEmbed.of(tutorialModel))
						.setComponents(TutorialListButtons.create(tutorialModel),
								TutorialSuggestionButtons.create(tutorialModel));
			} else return interactionHook.editOriginal(R.Strings.ui("no_entries_available"));
		}
	}
}