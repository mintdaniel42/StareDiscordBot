package org.mintdaniel42.starediscordbot.commands.group;

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

import java.util.UUID;

@RequiredArgsConstructor
public final class ShowGroupCommand extends ListenerAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		if (!event.getFullCommandName().equals("group show")) return;

		// check maintenance
		if (Options.isInMaintenance()) {
			event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
			return;
		}

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

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		String[] buttonParts = event.getComponentId().split(":");
		if (!buttonParts[0].equals("group") || buttonParts.length != 2) return;

		// check maintenance
		if (Options.isInMaintenance()) {
			event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
			return;
		}

		GroupModel groupModel = databaseAdapter.getGroupOf(UUID.fromString(buttonParts[1]));
		if (groupModel == null) {
			event.reply(R.string("this_group_does_not_exist")).queue();
		} else {
			event.replyEmbeds(GroupEmbed.of(databaseAdapter, groupModel)).queue();
		}
	}
}