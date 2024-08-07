package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.buttons.ApproveChangeButton;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.PGUserModel;
import org.mintdaniel42.starediscordbot.db.RequestModel;
import org.mintdaniel42.starediscordbot.db.UserModel;
import org.mintdaniel42.starediscordbot.embeds.ErrorEmbed;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public final class PGCommand extends ListenerAdapter {
	@NonNull final DatabaseAdapter databaseAdapter;

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getFullCommandName().startsWith("pg")) {
			if (!Options.isInMaintenance()) {
				try {
					switch (event.getFullCommandName()) {
						case "pg show" -> pgShow(event);
						case "pg add" -> pgAdd(event);
						case "pg edit" -> pgEdit(event);
						case "pg list" -> pgList(event);
					}
				} catch (Exception e) {
					log.error(R.Strings.log("the_command_s_caused_an_error", event.getFullCommandName()), e);
					event.replyEmbeds(ErrorEmbed.of(event.getInteraction(), e)).queue();
				}
			} else event.reply(R.Strings.ui("the_bot_is_currently_in_maintenance_mode")).queue();
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
													R.Strings.ui("show_group")).withDisabled(userModel.getGroup() == null)
									)
							)
							.queue());
				} else event.reply(R.Strings.ui("this_user_entry_does_not_exist")).queue();
			} else event.reply(R.Strings.ui("this_username_does_not_exist")).queue();
		} else event.reply(R.Strings.ui("your_command_was_incomplete")).queue();
	}

	@Contract(pure = true, value = "_, _ -> new")
	private static @NonNull PGUserModel buildPgModel(@NonNull final List<OptionMapping> options, @NonNull final PGUserModel.PGUserModelBuilder builder) {
		for (final var optionMapping : options) {
			switch (optionMapping.getName()) {
				case "rating" -> builder.rating(optionMapping.getAsString());
				case "points" -> builder.points(Math.round(optionMapping.getAsDouble()));
				case "joined" -> builder.joined(optionMapping.getAsString());
				case "luck" -> builder.luck(optionMapping.getAsDouble());
				case "quota" -> builder.quota(optionMapping.getAsDouble());
				case "winrate" -> builder.winrate(optionMapping.getAsDouble());
			}
		}
		return builder.build();
	}

	private void pgAdd(@NonNull final SlashCommandInteractionEvent event) {
		if (DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
			if (event.getOption("username") instanceof final OptionMapping usernameMapping && event.getOptions().size() >= 2) {
				if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof final UUID uuid) {
					if (!databaseAdapter.hasPgUser(uuid)) {
						final var pgModel = buildPgModel(event.getOptions(), PGUserModel.builder().uuid(uuid));
						final var userModel = databaseAdapter.getUser(uuid);
						final var userBuilder = userModel == null ? UserModel.builder().uuid(uuid) : userModel.toBuilder();
						userBuilder.pgUser(pgModel)
								.username(MCHelper.getUsername(uuid));

						if (!databaseAdapter.addUser(userBuilder.build()) && !databaseAdapter.addPgUser(pgModel)) {
							event.reply(R.Strings.ui("the_entry_could_not_be_created")).queue();
						} else event.reply(R.Strings.ui("the_entry_was_successfully_created"))
								.setEmbeds(UserEmbed.of(userBuilder.build(), UserEmbed.Type.PG))
								.queue();
					} else event.reply(R.Strings.ui("this_user_entry_already_exists")).queue();
				} else event.reply(R.Strings.ui("this_username_does_not_exist")).queue();
			} else event.reply(R.Strings.ui("your_command_was_incomplete")).queue();
		} else event.reply(R.Strings.ui("you_do_not_have_the_permission_to_use_this_command")).queue();
	}

	private void pgEdit(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof final OptionMapping usernameMapping &&
				event.getOptions().size() >= 2) {
			if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof final UUID uuid) {
				if (databaseAdapter.getPgUser(uuid) instanceof PGUserModel pgUserModel &&
						databaseAdapter.getUser(uuid) instanceof UserModel userModel) {
					pgUserModel = buildPgModel(event.getOptions(), pgUserModel.toBuilder());
					userModel = userModel.toBuilder().pgUser(pgUserModel).build();

					if (!DCHelper.hasRole(event.getMember(), Options.getEditRoleId()) && !DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
						long timestamp = System.currentTimeMillis();
						if (event.getGuild() instanceof Guild guild) {
							if (guild.getTextChannelById(Options.getRequestChannelId()) instanceof TextChannel requestChannel) {
								if (event.getMember() instanceof Member member) {
									if (databaseAdapter.addRequest(RequestModel.from(timestamp, pgUserModel))) {
										requestChannel.sendMessage(R.Strings.ui("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
														member.getAsMention(),
														timestamp))
												.setComponents(ApproveChangeButton.create(timestamp))
												.addEmbeds(UserEmbed.of(userModel, UserEmbed.Type.PG, true)).queue();
										event.reply(R.Strings.ui("the_entry_change_was_successfully_requested")).queue();
									} else event.reply(R.Strings.ui("the_entry_could_not_be_updated")).queue();
								}
							}
						}
					} else if (!databaseAdapter.edit(pgUserModel)) {
						event.reply(R.Strings.ui("the_entry_could_not_be_updated")).queue();
					} else event.reply(R.Strings.ui("the_entry_was_successfully_updated"))
							.setEmbeds(UserEmbed.of(userModel, UserEmbed.Type.PG))
							.queue();
				} else event.reply(R.Strings.ui("this_user_entry_does_not_exist")).queue();
			} else event.reply(R.Strings.ui("this_username_does_not_exist")).queue();
		} else event.reply(R.Strings.ui("your_command_was_incomplete")).queue();
	}

	private void pgList(@NonNull final SlashCommandInteractionEvent event) {
		final int page;
		if (event.getOption("page") instanceof final OptionMapping pageMapping) {
			page = pageMapping.getAsInt();
		} else page = 0;
		if (databaseAdapter.getPgUserList(page) instanceof final List<PGUserModel> entries && !entries.isEmpty()) {
			if (databaseAdapter.getPgPages() > page && page >= 0) {
				event.deferReply().queue(interactionHook -> interactionHook.editOriginalEmbeds(ListEmbed.createPgList(databaseAdapter, entries, page))
						.setComponents(ListButtons.create(ListButtons.Type.pg, page, databaseAdapter.getPgPages()))
						.queue());
			} else event.reply(R.Strings.ui("this_page_does_not_exist")).queue();
		} else event.reply(R.Strings.ui("no_entries_available")).queue();
	}
}
