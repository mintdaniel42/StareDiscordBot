package org.mintdaniel42.starediscordbot.commands.group;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.GroupModel;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public final class CreateGroupCommand extends ListenerAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		if (!event.getFullCommandName().equals("group create")) return;

		// check maintenance
		if (Options.isInMaintenance()) {
			event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
			return;
		}

		// check permission level
		if (DCHelper.lacksRole(event.getMember(), Options.getCreateRoleId())) {
			event.reply(R.string("you_do_not_have_the_permission_to_use_this_command")).queue();
			return;
		}

		// check if username is given
		OptionMapping username = event.getOption("leader");
		if (username == null) {
			event.reply(R.string("this_username_does_not_exist")).queue();
			return;
		}

		// check if group exists
		OptionMapping tagMapping = event.getOption("tag");
		OptionMapping nameMapping = event.getOption("name");
		OptionMapping leaderMapping = event.getOption("leader");
		OptionMapping relationMapping = event.getOption("relation");
		if (tagMapping == null || nameMapping == null || leaderMapping == null || relationMapping == null) {
			event.reply(R.string("your_command_was_incomplete")).queue();
		} else if (databaseAdapter.hasGroup(tagMapping.getAsString())) {
			event.reply(R.string("this_group_already_exists")).queue();
		} else {
			GroupModel.GroupModelBuilder builder = GroupModel.builder();
			builder.tag(tagMapping.getAsString());
			builder.name(nameMapping.getAsString());
			UUID uuid = MCHelper.getUuid(databaseAdapter, leaderMapping.getAsString());
			if (uuid == null) {
				event.reply(R.string("this_username_does_not_exist")).queue();
				return;
			}
			builder.leader(uuid);
			builder.relation(GroupModel.Relation.valueOf(relationMapping.getAsString()));

			// add the group
			GroupModel groupModel = builder.build();
			if (!databaseAdapter.addGroup(groupModel)) event.reply(R.string("the_group_could_not_be_created")).queue();
			else {
				event.reply(R.string("the_group_was_successfully_created")).setEmbeds(GroupEmbed.of(databaseAdapter, groupModel)).queue();
			}
		}
	}

	@Override
	public void onCommandAutoCompleteInteraction(@NonNull final CommandAutoCompleteInteractionEvent event) {
		if (!event.getFullCommandName().equals("group create")) return;

		OptionMapping usernameMapping = event.getOption("leader");
		if (usernameMapping != null) event.replyChoiceStrings(DCHelper.autoCompleteUsername(databaseAdapter, usernameMapping.getAsString())).queue();
	}
}
