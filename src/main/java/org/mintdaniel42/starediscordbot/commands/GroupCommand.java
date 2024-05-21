package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.GroupModel;
import org.mintdaniel42.starediscordbot.db.UserModel;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public final class GroupCommand extends ListenerAdapter {
	@NonNull final DatabaseAdapter databaseAdapter;

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getFullCommandName().startsWith("group")) {
			if (!Options.isInMaintenance()) {
				switch (event.getFullCommandName()) {
					case "group show" -> groupShow(event);
					case "group create" -> groupCreate(event);
					case "group user add" -> groupUserAdd(event);
				}
			} else event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
		}
	}

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		String[] buttonParts = event.getComponentId().split(":");
		if (!buttonParts[0].equals("group") || buttonParts.length != 2) return;

		if (!Options.isInMaintenance()) {
			if (databaseAdapter.getGroup(buttonParts[1]) instanceof GroupModel groupModel) {
				event.replyEmbeds(GroupEmbed.of(databaseAdapter, groupModel)).queue();
			}
			else event.reply(R.string("this_group_does_not_exist")).queue();
		} else event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
	}

	private void groupShow(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("tag") instanceof OptionMapping tagMapping) {
			if (databaseAdapter.getGroup(tagMapping.getAsString()) instanceof GroupModel groupModel) {
				event.replyEmbeds(GroupEmbed.of(databaseAdapter, groupModel)).queue();
			} else event.reply(R.string("this_group_does_not_exist")).queue();
		} else event.reply(R.string("your_command_was_incomplete")).queue();
	}

	private void groupCreate(@NonNull final SlashCommandInteractionEvent event) {
		if (DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
			if (event.getOption("tag") instanceof OptionMapping tagMapping &&
					event.getOption("name") instanceof OptionMapping nameMapping &&
					event.getOption("leader") instanceof OptionMapping leaderMapping &&
					event.getOption("relation") instanceof OptionMapping relationMapping) {
				if (databaseAdapter.getGroup(tagMapping.getAsString()) == null) {
					if (MCHelper.getUuid(databaseAdapter, leaderMapping.getAsString()) instanceof UUID uuid) {
						GroupModel.GroupModelBuilder builder = GroupModel.builder();
						builder.tag(tagMapping.getAsString());
						builder.name(nameMapping.getAsString());
						builder.leader(uuid);
						builder.relation(GroupModel.Relation.valueOf(relationMapping.getAsString()));

						GroupModel groupModel = builder.build();
						if (databaseAdapter.addGroup(groupModel)) {
							event.reply(R.string("the_group_was_successfully_created"))
									.setEmbeds(GroupEmbed.of(databaseAdapter, groupModel))
									.queue();
						} else event.reply(R.string("the_group_could_not_be_created")).queue();
					} else event.reply(R.string("this_username_does_not_exist")).queue();
				} else event.reply(R.string("this_group_already_exists")).queue();
			} else event.reply(R.string("your_command_was_incomplete")).queue();
		} else event.reply(R.string("you_do_not_have_the_permission_to_use_this_command")).queue();
	}

	private void groupUserAdd(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("tag") instanceof OptionMapping tagMapping &&
				event.getOption("username") instanceof OptionMapping usernameMapping) {
			if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof UUID uuid) {
				if (databaseAdapter.getUser(uuid) instanceof UserModel userModel) {
					if (databaseAdapter.getGroup(tagMapping.getAsString()) instanceof GroupModel groupModel) {
						databaseAdapter.editUser(userModel.toBuilder()
								.group(groupModel)
								.build());
						event.reply(R.string("the_user_s_was_added_to_the_group_s",
								MCHelper.getUsername(databaseAdapter, uuid),
								groupModel.getName())).queue();
					} else event.reply(R.string("this_group_does_not_exist")).queue();
				} else event.reply(R.string("this_user_entry_does_not_exist")).queue();
			} else event.reply(R.string("this_username_does_not_exist")).queue();
		} else event.reply(R.string("your_command_was_incomplete")).queue();
	}
}
