package org.mintdaniel42.starediscordbot.commands.hns;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.buttons.misc.TutorialButtons;
import org.mintdaniel42.starediscordbot.buttons.misc.TutorialSuggestionButtons;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.TutorialModel;
import org.mintdaniel42.starediscordbot.embeds.TutorialEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;

@RequiredArgsConstructor
public final class HNSTutorialCommand implements CommandAdapter {

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("page") instanceof OptionMapping pageMapping) {
			if (R.Tutorials.get(pageMapping.getAsString()) instanceof TutorialModel tutorialModel) {
				return interactionHook.editOriginalEmbeds(TutorialEmbed.of(tutorialModel))
						.setComponents(TutorialSuggestionButtons.create(tutorialModel));
			} else return interactionHook.editOriginal(R.Strings.ui("this_page_does_not_exist"));
		} else {
			final var tutorialModelOptional = Arrays.stream(R.Tutorials.list())
					.sorted()
					.findFirst();
			if (tutorialModelOptional.isPresent()) {
				final var tutorialModel = tutorialModelOptional.get();
				return interactionHook.editOriginalEmbeds(TutorialEmbed.of(tutorialModel))
						.setComponents(ListButtons.createTutorial(tutorialModel),
								TutorialButtons.create(tutorialModel));
			} else return interactionHook.editOriginal(R.Strings.ui("no_entries_available"));
		}
	}
}