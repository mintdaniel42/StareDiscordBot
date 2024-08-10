package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.embeds.InfoEmbed;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class InfoCommand extends ListenerAdapter {
	@NonNull final DatabaseAdapter databaseAdapter;

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getFullCommandName().equals("info")) {
			if (!Options.isInMaintenance()) {
				event.replyEmbeds(InfoEmbed.of(databaseAdapter))
						.addComponents(ListButtons.createInfo())
						.queue();
			} else event.reply(R.Strings.ui("the_bot_is_currently_in_maintenance_mode")).queue();
		}
	}
}
