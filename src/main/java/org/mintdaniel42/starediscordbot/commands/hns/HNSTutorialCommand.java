package org.mintdaniel42.starediscordbot.commands.hns;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.buttons.list.TutorialListButtons;
import org.mintdaniel42.starediscordbot.buttons.misc.TutorialSuggestionButtons;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.entity.TutorialEntity;
import org.mintdaniel42.starediscordbot.embeds.TutorialEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;

@RequiredArgsConstructor
@RequiresBean(HNSCommand.class)
@RequiresProperty(value = "feature.command.hns.tutorial.enabled", equalTo = "true")
@Singleton
public final class HNSTutorialCommand extends BaseComposeCommand {
	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
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

	@Inject
	public void register(@NonNull @Named("hns") SlashCommandData command) {
		command.addSubcommands(new SubcommandData("tutorial", R.Strings.ui("show_the_tutorial"))
				.addOption(OptionType.STRING, "page", R.Strings.ui("page"), false, true));
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns tutorial";
	}
}