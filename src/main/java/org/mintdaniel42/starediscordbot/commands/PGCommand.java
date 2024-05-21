package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.PGUserModel;
import org.mintdaniel42.starediscordbot.db.RequestModel;
import org.mintdaniel42.starediscordbot.db.UserModel;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public final class PGCommand extends ListenerAdapter {
	@NonNull final DatabaseAdapter databaseAdapter;

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		// check maintenance
		if (Options.isInMaintenance()) {
			event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
			return;
		}

		// set command
		String command = event.getFullCommandName();

		// check if username is given
		OptionMapping usernameMapping = event.getOption("username");
		if (usernameMapping == null && !command.equals("pg list")) {
			event.reply(R.string("your_command_was_incomplete")).queue();
			return;
		}

		// check if username exists
		UUID uuid = null;
		if (usernameMapping != null && !command.equals("pg list")) {
			uuid = MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString());
			if (uuid == null) {
				event.reply(R.string("this_username_does_not_exist")).queue();
				return;
			}
		}

		// check if the page exists (if provided)
		OptionMapping pageMapping = event.getOption("page");
		int page = pageMapping != null ? pageMapping.getAsInt() - 1 : 0;
		if (page < 0 || page >= databaseAdapter.getPgPages()) {
			event.reply(R.string("this_page_does_not_exist")).queue();
			return;
		}

		// select command
		switch (command) {
			case "pg show" -> pgShow(event, uuid);
			case String name when name.equals("pg edit") || name.equals("pg add") -> pgAddOrEdit(event, uuid, name);
			case "pg list" -> pgList(event, page);
			default -> {}
		}
	}

	private void pgShow(@NonNull final SlashCommandInteractionEvent event, @NonNull final UUID uuid) {
		UserModel userModel;

		if ((userModel = databaseAdapter.getUser(uuid)) != null) {
			event.deferReply().queue(interactionHook -> interactionHook
					.editOriginalEmbeds(UserEmbed.of(userModel, UserEmbed.Type.PG))
					.setComponents(ActionRow.of(
							Button.primary(String.format("group:%s", uuid), R.string("show_group")).withDisabled(!databaseAdapter.hasGroupFor(uuid))
					)).queue());
		} else {
			event.reply(R.string("this_username_or_entry_does_not_exist")).queue();
		}
	}

	private void pgAddOrEdit(@NonNull final SlashCommandInteractionEvent event, @NonNull final UUID uuid, @NonNull final String command) {
		if (!databaseAdapter.hasPgUser(uuid) && command.equals("pg edit")) event.reply(R.string("this_user_entry_does_not_exist")).queue();
		else if (databaseAdapter.hasPgUser(uuid) && command.equals("pg add")) event.reply(R.string("this_user_entry_already_exists")).queue();
		else {
			// get builder
			PGUserModel.PGUserModelBuilder pgBuilder;
			UserModel.UserModelBuilder userBuilder;
			if (command.equals("pg add")) {
				pgBuilder = PGUserModel.builder().uuid(uuid);
				UserModel userModel = databaseAdapter.getUser(uuid);
				if (userModel == null) userBuilder = UserModel.builder().uuid(uuid);
				else userBuilder = userModel.toBuilder();
			}
			else {
				PGUserModel pgUserModel = databaseAdapter.getPgUser(uuid);
				UserModel userModel = databaseAdapter.getUser(uuid);
				if (pgUserModel == null || userModel == null) {
					event.reply(R.string("this_user_entry_does_not_exist")).queue();
					return;
				}
				else {
					pgBuilder = pgUserModel.toBuilder();
					userBuilder = userModel.toBuilder();
				}
			}

			// set attributes
			for (OptionMapping optionMapping : event.getOptions()) {
				switch (optionMapping.getName()) {
					case "rating" -> pgBuilder.rating(optionMapping.getAsString());
					case "points" -> pgBuilder.points(Math.round(optionMapping.getAsDouble()));
					case "joined" -> pgBuilder.joined(optionMapping.getAsString());
					case "luck" -> pgBuilder.luck(optionMapping.getAsDouble());
					case "quota" -> pgBuilder.quota(optionMapping.getAsDouble());
					case "winrate" -> pgBuilder.winrate(optionMapping.getAsDouble());
				}
			}

			// update the model
			UserModel userModel = userBuilder.pgUser(pgBuilder.build()).build();

			if (command.equals("pg add")) {
				if (!databaseAdapter.addUser(userModel) && !databaseAdapter.addPgUser(pgBuilder.build())) event.reply(R.string("the_entry_could_not_be_created")).queue();
				else event.reply(R.string("the_entry_was_successfully_created")).setEmbeds(UserEmbed.of(userModel, UserEmbed.Type.PG)).queue();
			} else {
				if (DCHelper.lacksRole(event.getMember(), Options.getEditRoleId()) && DCHelper.lacksRole(event.getMember(), Options.getCreateRoleId())) {
					long timestamp = System.currentTimeMillis();
					if (!databaseAdapter.addRequest(RequestModel.from(timestamp, userModel.getPgUser()))) {
						event.reply(R.string("the_entry_could_not_be_updated")).queue();
					} else {
						event.reply(R.string("the_entry_change_was_successfully_requested")).queue();
						Guild guild = event.getGuild();
						if (guild != null) {
							TextChannel requestChannel = guild.getTextChannelById(Options.getRequestChannelId());
							if (requestChannel != null) {
								Member member = event.getMember();
								if (member != null) requestChannel.sendMessage(String.format(
												R.string("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s"),
												member.getAsMention(),
												timestamp))
										.addActionRow(Button.primary(String.format("approve:%s", timestamp), R.string("approve_this_change")))
										.addEmbeds(UserEmbed.of(userModel, UserEmbed.Type.PG)).queue();
							}
						}
					}
				} else {
					if (databaseAdapter.editPgUser(userModel.getPgUser()) == 0) event.reply(R.string("the_entry_could_not_be_updated")).queue();
					else {
						event.reply(R.string("the_entry_was_successfully_updated")).setEmbeds(UserEmbed.of(userModel, UserEmbed.Type.PG)).queue();
					}
				}
			}
		}
	}

	private void pgList(@NonNull final SlashCommandInteractionEvent event, final int page) {
		List<PGUserModel> entriesList = databaseAdapter.getPgUserList(page);
		if (entriesList != null && !entriesList.isEmpty()) {
			event.deferReply().queue(interactionHook -> interactionHook.editOriginalEmbeds(ListEmbed.createPgList(databaseAdapter, entriesList, page))
					.setComponents(ActionRow.of(
							Button.primary(String.format("previous:pg:%s", page), R.string("previous_page")).withDisabled(page < 1),
							Button.primary(String.format("next:pg:%s", page), R.string("next_page")).withDisabled(page + 1 >= databaseAdapter.getPgPages())
					))
					.queue());
		} else event.reply(R.string("no_entries_available")).queue();
	}
}
