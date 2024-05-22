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
		if (event.getFullCommandName().startsWith("pg")) {
			if (!Options.isInMaintenance()) {
				switch (event.getFullCommandName()) {
					case "pg show" -> pgShow(event);
					case "pg add" -> pgAdd(event);
					case "pg edit" -> pgEdit(event);
					case "pg list" -> pgList(event);
				}
			} else event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
		}
	}

	private void pgShow(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof final OptionMapping usernameMapping) {
			if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof final UUID uuid) {
				if (databaseAdapter.hasPgUser(uuid) && databaseAdapter.getUser(uuid) instanceof final UserModel userModel) {
					event.deferReply().queue(interactionHook -> interactionHook
							.editOriginalEmbeds(UserEmbed.of(userModel, UserEmbed.Type.PG))
							.setComponents(ActionRow.of(
											Button.primary(
													String.format("group:%s", userModel.getGroup() != null ? userModel.getGroup().getTag() : null),
													R.string("show_group")).withDisabled(userModel.getGroup() == null)
									)
							).queue());
				} else event.reply(R.string("this_user_entry_does_not_exist")).queue();
			} else event.reply(R.string("this_username_does_not_exist")).queue();
		} else event.reply(R.string("your_command_was_incomplete")).queue();
	}

	private void pgAdd(@NonNull final SlashCommandInteractionEvent event) {
		if (DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
			if (event.getOption("username") instanceof final OptionMapping usernameMapping && event.getOptions().size() >= 2) {
				if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof final UUID uuid) {
					if (!databaseAdapter.hasPgUser(uuid)) {
						final var pgModel = buildPgModel(event, PGUserModel.builder().uuid(uuid));
						final var userModel = databaseAdapter.getUser(uuid);
						final var userBuilder = userModel == null ? UserModel.builder().uuid(uuid) : userModel.toBuilder();
						userBuilder.pgUser(pgModel).build();

						if (!databaseAdapter.addUser(userBuilder.build()) && !databaseAdapter.addPgUser(pgModel)) {
							event.reply(R.string("the_entry_could_not_be_created")).queue();
						} else event.reply(R.string("the_entry_was_successfully_created"))
								.setEmbeds(UserEmbed.of(userBuilder.build(), UserEmbed.Type.PG))
								.queue();
					} else event.reply(R.string("this_user_entry_already_exists")).queue();
				} else event.reply(R.string("this_username_does_not_exist")).queue();
			} else event.reply(R.string("your_command_was_incomplete")).queue();
		} else event.reply(R.string("you_do_not_have_the_permission_to_use_this_command")).queue();
	}

	private void pgEdit(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof final OptionMapping usernameMapping &&
				event.getOptions().size() >= 2) {
			if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof final UUID uuid) {
				if (databaseAdapter.getPgUser(uuid) instanceof PGUserModel pgUserModel &&
						databaseAdapter.getUser(uuid) instanceof UserModel userModel) {
					pgUserModel = buildPgModel(event, pgUserModel.toBuilder());
					userModel = userModel.toBuilder().pgUser(pgUserModel).build();

					if (!DCHelper.hasRole(event.getMember(), Options.getEditRoleId()) && !DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
						long timestamp = System.currentTimeMillis();
						if (event.getGuild() instanceof Guild guild) {
							if (guild.getTextChannelById(Options.getRequestChannelId()) instanceof TextChannel requestChannel) {
								if (event.getMember() instanceof Member member) {
									if (databaseAdapter.addRequest(RequestModel.from(timestamp, pgUserModel))) {
										requestChannel.sendMessage(R.string("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
														member.getAsMention(),
														timestamp))
												.addActionRow(Button.primary(String.format("approve:%s", timestamp), R.string("approve_this_change")))
												.addEmbeds(UserEmbed.of(userModel, UserEmbed.Type.PG)).queue();
										event.reply(R.string("the_entry_change_was_successfully_requested")).queue();
									} else event.reply(R.string("the_entry_could_not_be_updated")).queue();
								}
							}
						}
					} else if (!databaseAdapter.edit(pgUserModel)) {
						event.reply(R.string("the_entry_could_not_be_updated")).queue();
					} else event.reply(R.string("the_entry_was_successfully_updated"))
							.setEmbeds(UserEmbed.of(userModel, UserEmbed.Type.PG))
							.queue();
				} else event.reply(R.string("this_user_entry_does_not_exist")).queue();
			} else event.reply(R.string("this_username_does_not_exist")).queue();
		} else event.reply(R.string("your_command_was_incomplete")).queue();
	}

	private static PGUserModel buildPgModel(@NonNull final SlashCommandInteractionEvent event, @NonNull final PGUserModel.PGUserModelBuilder pgBuilder) {
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
		return pgBuilder.build();
	}

	private void pgList(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("page") instanceof final OptionMapping pageMapping) {
			if (databaseAdapter.getPgUserList(pageMapping.getAsInt()) instanceof final List<PGUserModel> entries && !entries.isEmpty()) {
				final var page = pageMapping.getAsInt();
				if (databaseAdapter.getPgPages() < page && page >= 0) {
					event.deferReply().queue(interactionHook -> interactionHook.editOriginalEmbeds(ListEmbed.createPgList(databaseAdapter, entries, page))
							.setComponents(ActionRow.of(
									Button.primary(String.format("previous:pg:%s", page), R.string("previous_page")).withDisabled(page < 1),
									Button.primary(String.format("next:pg:%s", page), R.string("next_page")).withDisabled(page + 1 >= databaseAdapter.getPgPages())
							))
							.queue());
				} else event.reply(R.string("this_page_does_not_exist")).queue();
			} else event.reply(R.string("no_entries_available")).queue();
		} else event.reply(R.string("your_command_was_incomplete")).queue();
	}
}
