package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

public class HelpCommand extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getFullCommandName().equals("help")) {
			if (event.getGuild() instanceof Guild guild) {
				guild.retrieveCommands().queue(
						commands -> {
							EmbedBuilder embedBuilder = new EmbedBuilder();
							embedBuilder.setColor(Options.getColorNormal());
							for (Command command : commands) {
								embedBuilder.addField("/" + command.getFullCommandName(), command.getDescription(), false);
								for (Command.Subcommand subcommand : command.getSubcommands()) {
									embedBuilder.addField("--- /" + subcommand.getFullCommandName(), subcommand.getDescription(), false);
								}
							}
							event.replyEmbeds(embedBuilder.build()).queue();
						}
				);
			} else event.reply(R.string("an_impossible_error_occurred")).queue();
		}
	}
}
