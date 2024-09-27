package org.mintdaniel42.starediscordbot.commands.hns;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.buttons.list.TutorialListButtons;
import org.mintdaniel42.starediscordbot.buttons.misc.TutorialSuggestionButtons;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.compose.exception.ComposeException;
import org.mintdaniel42.starediscordbot.data.entity.TutorialEntity;
import org.mintdaniel42.starediscordbot.embeds.TutorialEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;

@RequiredArgsConstructor
@Singleton
public final class HNSTutorialCommand extends BaseComposeCommand {
	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws ComposeException {
		return nullableStringOption(context, "page", page -> {
			if (R.Tutorials.get(page) instanceof final TutorialEntity tutorialEntity) {
				return response().setEmbeds(TutorialEmbed.of(tutorialEntity)).build();
			} else return response().setContent(R.Strings.ui("this_page_does_not_exist")).build();
		}).orElseGet(() -> Arrays.stream(R.Tutorials.list())
				.sorted()
				.findFirst()
				.map(tutorialEntity -> response()
						.setEmbeds(TutorialEmbed.of(tutorialEntity))
						.setComponents(TutorialListButtons.create(tutorialEntity),
								TutorialSuggestionButtons.create(tutorialEntity))
						.build())
				.orElseGet(() -> response().setContent(R.Strings.ui("no_entries_available")).build()));
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns tutorial";
	}
}