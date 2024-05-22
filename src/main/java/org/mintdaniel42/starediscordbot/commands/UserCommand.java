package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.RequestModel;
import org.mintdaniel42.starediscordbot.db.UserModel;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public class UserCommand extends ListenerAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getFullCommandName().startsWith("user")) {
			if (!Options.isInMaintenance()) {
				switch (event.getFullCommandName()) {
					case "user edit" -> userEdit(event);
					case "user delete" -> userDelete(event);
				}
			} else event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
		}
	}

	private void userEdit(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof OptionMapping usernameMapping && event.getOptions().size() >= 2) {
			if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof UUID uuid) {
				if (databaseAdapter.getUser(uuid) instanceof UserModel userModel) {
					UserModel.UserModelBuilder userBuilder = userModel.toBuilder();

					for (OptionMapping optionMapping : event.getOptions()) {
						switch (optionMapping.getName()) {
							case "discord" -> userBuilder.discord(optionMapping.getAsLong());
							case "note" -> userBuilder.note(optionMapping.getAsString());
						}
					}

					if (!DCHelper.hasRole(event.getMember(), Options.getEditRoleId()) && !DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
						long timestamp = System.currentTimeMillis();
						if (event.getGuild() instanceof Guild guild) {
							if (guild.getTextChannelById(Options.getRequestChannelId()) instanceof TextChannel requestChannel) {
								if (event.getMember() instanceof Member member) {
									if (databaseAdapter.addRequest(RequestModel.from(timestamp, userModel))) {
										requestChannel.sendMessage(R.string("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
														member.getAsMention(),
														timestamp))
												.addActionRow(Button.primary(String.format("approve:%s", timestamp), R.string("approve_this_change")))
												.addEmbeds(UserEmbed.of(userModel, UserEmbed.Type.HNS_ALL)).queue();
										event.reply(R.string("the_entry_change_was_successfully_requested")).queue();
									} else event.reply(R.string("the_entry_could_not_be_updated")).queue();
								}
							}
						}
					} else if (databaseAdapter.edit(userModel) == 0) {
						event.reply(R.string("the_entry_could_not_be_updated")).queue();
					} else event.reply(R.string("the_entry_was_successfully_updated"))
							.setEmbeds(UserEmbed.of(userModel, UserEmbed.Type.HNS))
							.queue();
				} else event.reply(R.string("this_user_entry_does_not_exist")).queue();
			} else event.reply(R.string("this_username_does_not_exist")).queue();
		} else event.reply(R.string("your_command_was_incomplete")).queue();
	}

	private void userDelete(@NonNull final SlashCommandInteractionEvent event) {
		if (DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
			if (event.getOption("username") instanceof OptionMapping usernameMapping) {
				if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof UUID uuid) {
					if (databaseAdapter.getUser(uuid) instanceof UserModel userModel) {
						if (databaseAdapter.deleteUser(uuid)) {
							event.reply(R.string("the_user_s_was_successfully_deleted",
									userModel.getUsername())).queue();
						} else event.reply(R.string("the_user_s_could_not_be_deleted",
								userModel.getUsername())).queue();
					} else event.reply(R.string("this_user_entry_does_not_exist")).queue();
				} else event.reply(R.string("this_username_does_not_exist")).queue();
			} else event.reply(R.string("your_command_was_incomplete")).queue();
		} else event.reply(R.string("you_do_not_have_the_permission_to_use_this_command")).queue();
	}
}
