package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.data.TutorialModel;
import org.mintdaniel42.starediscordbot.embeds.TutorialEmbed;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

public final class TutorialCommand extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getFullCommandName().startsWith("tutorial")) {
			if (!Options.isInMaintenance()) {
				if (event.getOption("page") instanceof OptionMapping pageMapping) {
					if (R.Tutorials.get(pageMapping.getAsString()) instanceof TutorialModel tutorialModel) {
						event.replyEmbeds(TutorialEmbed.of(tutorialModel)).queue();
					} else event.reply(R.Strings.ui("this_page_does_not_exist")).queue();
				} else event.reply(R.Strings.ui("your_command_was_incomplete")).queue();
			} else event.reply(R.Strings.ui("the_bot_is_currently_in_maintenance_mode")).queue();
		}
	}
}
