package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.buttons.ApproveChangeButton;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.GroupModel;
import org.mintdaniel42.starediscordbot.db.RequestModel;
import org.mintdaniel42.starediscordbot.db.UserModel;
import org.mintdaniel42.starediscordbot.embeds.ErrorEmbed;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public final class GroupCommand extends ListenerAdapter {
	@NonNull final DatabaseAdapter databaseAdapter;

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getFullCommandName().startsWith("group")) {
			try {
				if (!Options.isInMaintenance()) {
					switch (event.getFullCommandName()) {
						case "group show" -> groupShow(event);
						case "group create" -> groupCreate(event);
						case "group edit" -> groupEdit(event);
						case "group user add" -> groupUserAdd(event);
						case "group user show" -> groupUserShow(event);
						case "group user remove" -> groupUserRemove(event);
					}
				} else event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
			} catch (Exception e) {
				log.error(R.logging("the_command_s_caused_an_error"), e);
				event.replyEmbeds(ErrorEmbed.of(event.getInteraction(), e)).queue();
			}
		}
	}

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		String[] buttonParts = event.getComponentId().split(":");
		if (!buttonParts[0].equals("group") || buttonParts.length != 2) return;

		if (!Options.isInMaintenance()) {
			if (databaseAdapter.getGroup(buttonParts[1]) instanceof GroupModel groupModel) {
				event.deferReply().queue(interactionHook -> interactionHook.editOriginalEmbeds(GroupEmbed.of(databaseAdapter, groupModel, 0))
						.setComponents(ListButtons.create(ListButtons.Type.group, 0, databaseAdapter.getGroupMemberPages(groupModel.getTag())))
						.queue());
			}
			else event.reply(R.string("this_group_does_not_exist")).queue();
		} else event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
	}

	private void groupShow(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("tag") instanceof OptionMapping tagMapping) {
			if (databaseAdapter.getGroup(tagMapping.getAsString()) instanceof GroupModel groupModel) {
				event.deferReply().queue(interactionHook -> interactionHook.editOriginalEmbeds(GroupEmbed.of(databaseAdapter, groupModel, 0))
						.setComponents(ListButtons.create(ListButtons.Type.group, 0, databaseAdapter.getGroupMemberPages(groupModel.getTag())))
						.queue());
			} else event.reply(R.string("this_group_does_not_exist")).queue();
		} else event.reply(R.string("your_command_was_incomplete")).queue();
	}

	private void groupCreate(@NonNull final SlashCommandInteractionEvent event) {
		if (DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
			if (event.getOption("tag") instanceof OptionMapping tagMapping &&
					event.getOption("name") instanceof OptionMapping nameMapping &&
					event.getOption("leader") instanceof OptionMapping leaderMapping &&
					event.getOption("relation") instanceof OptionMapping relationMapping) {
				if (!databaseAdapter.hasGroup(tagMapping.getAsString())) {
					if (MCHelper.getUuid(databaseAdapter, leaderMapping.getAsString()) instanceof UUID uuid) {
						GroupModel.GroupModelBuilder builder = GroupModel.builder();
						builder.tag(tagMapping.getAsString());
						builder.name(nameMapping.getAsString());
						builder.leader(uuid);
						builder.relation(GroupModel.Relation.valueOf(relationMapping.getAsString()));

						GroupModel groupModel = builder.build();
						if (databaseAdapter.addGroup(groupModel)) {
							event.reply(R.string("the_group_was_successfully_created"))
									.setEmbeds(GroupEmbed.of(databaseAdapter, groupModel, 0))
									.queue();
						} else event.reply(R.string("the_group_could_not_be_created")).queue();
					} else event.reply(R.string("this_username_does_not_exist")).queue();
				} else event.reply(R.string("this_group_already_exists")).queue();
			} else event.reply(R.string("your_command_was_incomplete")).queue();
		} else event.reply(R.string("you_do_not_have_the_permission_to_use_this_command")).queue();
	}

	private void groupEdit(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("tag") instanceof final OptionMapping tagMapping && event.getOptions().size() >= 2) {
			if (databaseAdapter.getGroup(tagMapping.getAsString()) instanceof GroupModel groupModel) {
				groupModel = buildGroupModel(event, groupModel);
				if (groupModel == null) return;

				if (!DCHelper.hasRole(event.getMember(), Options.getEditRoleId()) && !DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
					long timestamp = System.currentTimeMillis();
					if (event.getGuild() instanceof final Guild guild) {
						if (guild.getTextChannelById(Options.getRequestChannelId()) instanceof final TextChannel requestChannel) {
							if (event.getMember() instanceof final Member member) {
								if (databaseAdapter.addRequest(RequestModel.from(timestamp, groupModel))) {
									requestChannel.sendMessage(R.string("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
													member.getAsMention(),
													timestamp))
											.setComponents(ApproveChangeButton.create(timestamp))
											.addEmbeds(GroupEmbed.of(databaseAdapter, groupModel, 0)).queue();
									event.reply(R.string("the_entry_change_was_successfully_requested")).queue();
								} else event.reply(R.string("the_entry_could_not_be_updated")).queue();
							}
						}
					}
				} else if (!databaseAdapter.edit(groupModel)) {
					event.reply(R.string("the_entry_could_not_be_updated")).queue();
				} else event.reply(R.string("the_entry_was_successfully_updated"))
						.setEmbeds(GroupEmbed.of(databaseAdapter, groupModel, 0))
						.queue();
			} else event.reply(R.string("this_group_does_not_exist")).queue();
		} else event.reply(R.string("your_command_was_incomplete")).queue();
	}

	private @Nullable GroupModel buildGroupModel(@NonNull final SlashCommandInteractionEvent event, @NonNull final GroupModel groupModel) {
		var groupBuilder = groupModel.toBuilder();

		for (OptionMapping optionMapping : event.getOptions()) {
			switch (optionMapping.getName()) {
				case "name" -> groupBuilder.name(optionMapping.getAsString());
				case "leader" -> {
					if (MCHelper.getUuid(databaseAdapter, optionMapping.getAsString()) instanceof final UUID uuid) {
						groupBuilder.leader(uuid);
					} else {
						event.reply(R.string("this_username_does_not_exist")).queue();
						return null;
					}
				}
				case "relation" -> groupBuilder.relation(GroupModel.Relation.valueOf(optionMapping.getAsString()));
			}
		}

		return groupBuilder.build();
	}

	private void groupUserAdd(@NonNull final SlashCommandInteractionEvent event) {
		if (DCHelper.hasRole(event.getMember(), Options.getCreateRoleId()) || DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
			if (event.getOption("tag") instanceof OptionMapping tagMapping &&
					event.getOption("username") instanceof OptionMapping usernameMapping) {
				if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof UUID uuid) {
					if (databaseAdapter.getUser(uuid) instanceof UserModel userModel) {
						if (databaseAdapter.getGroup(tagMapping.getAsString()) instanceof GroupModel groupModel &&
								databaseAdapter.edit(userModel.toBuilder()
										.group(groupModel)
										.build())) {
							event.reply(R.string("the_user_s_was_added_to_the_group_s",
									MCHelper.getUsername(databaseAdapter, uuid),
									groupModel.getName())).queue();
						} else event.reply(R.string("this_group_does_not_exist")).queue();
					} else event.reply(R.string("this_user_entry_does_not_exist")).queue();
				} else event.reply(R.string("this_username_does_not_exist")).queue();
			} else event.reply(R.string("your_command_was_incomplete")).queue();
		} else event.reply(R.string("you_do_not_have_the_permission_to_use_this_command")).queue();
	}

	private void groupUserShow(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof OptionMapping usernameMapping) {
			if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof UUID uuid) {
				if (databaseAdapter.getUser(uuid) instanceof UserModel userModel) {
					if (userModel.getGroup() instanceof GroupModel groupModel) {
						event.replyEmbeds(GroupEmbed.of(databaseAdapter, groupModel, 0)).queue();
					} else event.reply(R.string("the_user_s_is_not_in_any_group",
							userModel.getUsername())).queue();
				} else event.reply(R.string("this_user_entry_does_not_exist")).queue();
			} else event.reply(R.string("this_username_does_not_exist")).queue();
		} else event.reply(R.string("your_command_was_incomplete")).queue();
	}

	private void groupUserRemove(@NonNull final SlashCommandInteractionEvent event) {
		if (DCHelper.hasRole(event.getMember(), Options.getCreateRoleId()) || DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
			if (event.getOption("username") instanceof OptionMapping usernameMapping &&
					event.getOption("tag") instanceof OptionMapping tagMapping) {
				if (databaseAdapter.hasGroup(tagMapping.getAsString())) {
					if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof UUID uuid) {
						if (databaseAdapter.getUser(uuid) instanceof UserModel userModel) {
							if (userModel.getGroup() instanceof GroupModel groupModel &&
									databaseAdapter.edit(userModel.toBuilder()
										.group(null)
										.build())) {
								event.reply(R.string("the_user_s_was_removed_from_the_group_s",
										MCHelper.getUsername(databaseAdapter, uuid),
										groupModel.getName())).queue();
							} else event.reply(R.string("the_user_s_is_not_in_any_group",
									userModel.getUsername())).queue();
						} else event.reply(R.string("this_user_entry_does_not_exist")).queue();
					} else event.reply(R.string("this_username_does_not_exist")).queue();
				} else event.reply(R.string("this_group_does_not_exist")).queue();
			} else event.reply(R.string("your_command_was_incomplete")).queue();
		} else event.reply(R.string("you_do_not_have_the_permission_to_use_this_command")).queue();
	}
}
