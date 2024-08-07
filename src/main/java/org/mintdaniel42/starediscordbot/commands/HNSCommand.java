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
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.buttons.ApproveChangeButton;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.db.*;
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
public final class HNSCommand extends ListenerAdapter {
	@NonNull final DatabaseAdapter databaseAdapter;

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getFullCommandName().startsWith("hns")) {
			if (!Options.isInMaintenance()) {
				try {
					switch (event.getFullCommandName()) {
						case "hns show" -> hnsShow(event, false);
						case "hns showmore" -> hnsShow(event, true);
						case "hns add" -> hnsAdd(event);
						case "hns edit" -> hnsEdit(event);
						case "hns list" -> hnsList(event);
					}
				} catch (Exception e) {
					log.error(R.Strings.log("the_command_s_caused_an_error", event.getFullCommandName()), e);
					event.replyEmbeds(ErrorEmbed.of(event.getInteraction(), e)).queue();
				}
			} else event.reply(R.Strings.ui("the_bot_is_currently_in_maintenance_mode")).queue();
		}
	}

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		String[] buttonParts = event.getComponentId().split(":");
		if (!(buttonParts[0].equals("hns") || buttonParts[0].equals("detailedhns")) || buttonParts.length != 2) return;

		boolean more = buttonParts[0].equals("detailedhns");
		UUID uuid = UUID.fromString(buttonParts[1]);

		UserModel userModel;

		if ((userModel = databaseAdapter.getUser(uuid)) != null && userModel.getHnsUser() != null) {
			GroupModel groupModel = userModel.getGroup();
			event.deferReply().queue(interactionHook -> interactionHook
					.editOriginalEmbeds(UserEmbed.of(userModel, more ? UserEmbed.Type.HNS_MORE : UserEmbed.Type.HNS))
					.setComponents(ActionRow.of(
									Button.primary(
											String.format(more ? "hns:%s" : "detailedhns:%s", uuid),
											R.Strings.ui(!more ? "more_info" : "basic_info")
									)
									,
									Button.primary(
											String.format("group:%s", groupModel != null ? groupModel.getTag() : null),
											R.Strings.ui("show_group")).withDisabled(!databaseAdapter.hasGroupFor(uuid))
							)
					)
					.queue());
		} else event.reply(R.Strings.ui("this_username_or_entry_does_not_exist")).queue();
	}

	private void hnsShow(@NonNull final SlashCommandInteractionEvent event, final boolean more) {
		if (event.getOption("username") instanceof final OptionMapping usernameMapping) {
			if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof final UUID uuid) {
				if (databaseAdapter.hasHnsUser(uuid) && databaseAdapter.getUser(uuid) instanceof final UserModel userModel) {
					event.deferReply().queue(interactionHook -> interactionHook
							.editOriginalEmbeds(UserEmbed.of(userModel, more ? UserEmbed.Type.HNS_MORE : UserEmbed.Type.HNS))
							.setComponents(ActionRow.of(
											Button.primary(
													String.format(more ? "hns:%s" : "detailedhns:%s", uuid),
													R.Strings.ui(!more ? "more_info" : "basic_info")
											),
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
	private static @NonNull HNSUserModel buildHnsModel(@NonNull final List<OptionMapping> options, @NonNull final HNSUserModel.HNSUserModelBuilder builder) {
		for (final var optionMapping : options) {
			switch (optionMapping.getName()) {
				case "rating" -> builder.rating(optionMapping.getAsString());
				case "points" -> builder.points(Math.round(optionMapping.getAsDouble()));
				case "joined" -> builder.joined(optionMapping.getAsString());
				case "secondary" -> builder.secondary(optionMapping.getAsBoolean());
				case "banned" -> builder.banned(optionMapping.getAsBoolean());
				case "cheating" -> builder.cheating(optionMapping.getAsBoolean());
				case "top10" -> builder.top10(optionMapping.getAsString());
				case "streak" -> builder.streak(optionMapping.getAsInt());
				case "highest_rank" -> builder.highestRank(optionMapping.getAsString());
			}
		}
		return builder.build();
	}

	private void hnsAdd(@NonNull final SlashCommandInteractionEvent event) {
		if (DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
			if (event.getOption("username") instanceof final OptionMapping usernameMapping && event.getOptions().size() >= 2) {
				if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof final UUID uuid) {
					if (!databaseAdapter.hasHnsUser(uuid)) {
						final var hnsModel = buildHnsModel(event.getOptions(), HNSUserModel.builder().uuid(uuid));
						final var userModel = databaseAdapter.getUser(uuid);
						final var userBuilder = userModel == null ? UserModel.builder().uuid(uuid) : userModel.toBuilder();
						userBuilder.hnsUser(hnsModel)
								.username(MCHelper.getUsername(uuid));

						if (!databaseAdapter.addUser(userBuilder.build()) && !databaseAdapter.addHnsUser(hnsModel)) {
							event.reply(R.Strings.ui("the_entry_could_not_be_created")).queue();
						} else event.reply(R.Strings.ui("the_entry_was_successfully_created"))
								.setEmbeds(UserEmbed.of(userBuilder.build(), UserEmbed.Type.HNS_ALL))
								.queue();
					} else event.reply(R.Strings.ui("this_user_entry_already_exists")).queue();
				} else event.reply(R.Strings.ui("this_username_does_not_exist")).queue();
			} else event.reply(R.Strings.ui("your_command_was_incomplete")).queue();
		} else event.reply(R.Strings.ui("you_do_not_have_the_permission_to_use_this_command")).queue();
	}

	private void hnsEdit(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof final OptionMapping usernameMapping &&
				event.getOptions().size() >= 2) {
			if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof final UUID uuid) {
				if (databaseAdapter.getHnsUser(uuid) instanceof HNSUserModel hnsUserModel &&
						databaseAdapter.getUser(uuid) instanceof UserModel userModel) {
					hnsUserModel = buildHnsModel(event.getOptions(), hnsUserModel.toBuilder());
					userModel = userModel.toBuilder().hnsUser(hnsUserModel).build();

					if (!DCHelper.hasRole(event.getMember(), Options.getEditRoleId()) && !DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
						long timestamp = System.currentTimeMillis();
						if (event.getGuild() instanceof Guild guild) {
							if (guild.getTextChannelById(Options.getRequestChannelId()) instanceof TextChannel requestChannel) {
								if (event.getMember() instanceof Member member) {
									if (databaseAdapter.addRequest(RequestModel.from(timestamp, hnsUserModel))) {
										requestChannel.sendMessage(R.Strings.ui("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
														member.getAsMention(),
														timestamp))
												.setComponents(ApproveChangeButton.create(timestamp))
												.addEmbeds(UserEmbed.of(userModel, UserEmbed.Type.HNS_ALL, true)).queue();
										event.reply(R.Strings.ui("the_entry_change_was_successfully_requested")).queue();
									} else event.reply(R.Strings.ui("the_entry_could_not_be_updated")).queue();
								}
							}
						}
					} else if (!databaseAdapter.edit(hnsUserModel)) {
						event.reply(R.Strings.ui("the_entry_could_not_be_updated")).queue();
					} else event.reply(R.Strings.ui("the_entry_was_successfully_updated"))
							.setEmbeds(UserEmbed.of(userModel, UserEmbed.Type.HNS_ALL))
							.queue();
				} else event.reply(R.Strings.ui("this_user_entry_does_not_exist")).queue();
			} else event.reply(R.Strings.ui("this_username_does_not_exist")).queue();
		} else event.reply(R.Strings.ui("your_command_was_incomplete")).queue();
	}

	private void hnsList(@NonNull final SlashCommandInteractionEvent event) {
		final int page;
		if (event.getOption("page") instanceof final OptionMapping pageMapping) {
			page = pageMapping.getAsInt();
		} else page = 0;
		if (databaseAdapter.getHnsUserList(page) instanceof final List<HNSUserModel> entries && !entries.isEmpty()) {
			if (databaseAdapter.getHnsPages() > page && page >= 0) {
				event.deferReply().queue(interactionHook -> interactionHook.editOriginalEmbeds(ListEmbed.createHnsList(databaseAdapter, entries, page))
						.setComponents(ListButtons.create(ListButtons.Type.hns, page, databaseAdapter.getHnsPages()))
						.queue());
			} else event.reply(R.Strings.ui("this_page_does_not_exist")).queue();
		} else event.reply(R.Strings.ui("no_entries_available")).queue();
	}
}
