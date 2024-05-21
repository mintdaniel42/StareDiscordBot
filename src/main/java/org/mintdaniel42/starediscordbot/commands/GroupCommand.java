package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.GroupModel;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class GroupCommand extends ListenerAdapter {
	@NonNull final DatabaseAdapter databaseAdapter;

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		// check maintenance
		if (Options.isInMaintenance()) {
			event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
			return;
		}

		switch (event.getFullCommandName()) {
			case "group show" -> groupShow(event);
		}
	}

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		String[] buttonParts = event.getComponentId().split(":");
		if (!buttonParts[0].equals("group") || buttonParts.length != 2) return;

		// check maintenance
		if (Options.isInMaintenance()) {
			event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
			return;
		}

		GroupModel groupModel = databaseAdapter.getGroup(buttonParts[1]);
		if (groupModel == null) {
			event.reply(R.string("this_group_does_not_exist")).queue();
		} else {
			event.replyEmbeds(GroupEmbed.of(databaseAdapter, groupModel)).queue();
		}
	}

	private void groupShow(SlashCommandInteractionEvent event) {
		// check if group exists and send if yes
		GroupModel groupModel;
		OptionMapping tagMapping = event.getOption("tag");
		if (tagMapping == null) {
			event.reply(R.string("your_command_was_incomplete")).queue();
		} else if ((groupModel = databaseAdapter.getGroup(tagMapping.getAsString())) == null) {
			event.reply(R.string("this_group_does_not_exist")).queue();
		} else {
			event.replyEmbeds(GroupEmbed.of(databaseAdapter, groupModel)).queue();
		}
	}
}
