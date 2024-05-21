package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.mintdaniel42.starediscordbot.db.*;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public final class HNSCommand extends ListenerAdapter {
	@NonNull final DatabaseAdapter databaseAdapter;

	@Override
	public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
		if (!event.getFullCommandName().startsWith("hns")) return;

		// check maintenance
		if (Options.isInMaintenance()) {
			event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
			return;
		}

		// set command
		String command = event.getFullCommandName();

		// check if username is given
		OptionMapping usernameMapping = event.getOption("username");
		if (usernameMapping == null && !command.equals("hns list")) {
			event.reply(R.string("your_command_was_incomplete")).queue();
			return;
		}

		// check if username exists
		UUID uuid = null;
		if (usernameMapping != null && !command.equals("hns list")) {
			uuid = MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString());
			if (uuid == null) {
				event.reply(R.string("this_username_does_not_exist")).queue();
				return;
			}
		}

		// check if the page exists (if provided)
		OptionMapping pageMapping = event.getOption("page");
		int page = pageMapping != null ? pageMapping.getAsInt() - 1 : 0;
		if (page < 0 || page >= databaseAdapter.getHnsPages()) {
			event.reply(R.string("this_page_does_not_exist")).queue();
			return;
		}

		// select command
		switch (command) {
			case "hns show" -> hnsShow(event, uuid);
			case String name when name.equals("hns edit") || name.equals("hns add") -> hnsAddOrEdit(event, uuid, name);
			case "hns list" -> hnsList(event, page);
			default -> {}
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
									R.string(!more ? "more_info" : "basic_info")
							),
							Button.primary(
									String.format("group:%s", groupModel != null ? groupModel.getTag() : null),
									R.string("show_group")).withDisabled(!databaseAdapter.hasGroupFor(uuid) || groupModel == null)
							)
					).queue());
		} else {
			event.reply(R.string("this_username_or_entry_does_not_exist")).queue();
		}
	}

	private void hnsShow(@NonNull final SlashCommandInteractionEvent event, @NonNull final UUID uuid) {
		UserModel userModel;

		if ((userModel = databaseAdapter.getUser(uuid)) != null && userModel.getHnsUser() != null) {
			OptionMapping moreMapping = event.getOption("more");
			boolean more = moreMapping != null && moreMapping.getAsBoolean();
			GroupModel groupModel = userModel.getGroup();
			event.deferReply().queue(interactionHook -> interactionHook
					.editOriginalEmbeds(UserEmbed.of(userModel, more ? UserEmbed.Type.HNS_MORE : UserEmbed.Type.HNS))
					.setComponents(ActionRow.of(
							Button.primary(
									String.format(more ? "hns:%s" : "detailedhns:%s", uuid),
									R.string(!more ? "more_info" : "basic_info")
							),
							Button.primary(
									String.format("group:%s", groupModel != null ? groupModel.getTag() : null),
									R.string("show_group")).withDisabled(!databaseAdapter.hasGroupFor(uuid)
							)
					)).queue());
		} else {
			event.reply(R.string("this_username_or_entry_does_not_exist")).queue();
		}
	}

	private void hnsAddOrEdit(@NonNull final SlashCommandInteractionEvent event, @NonNull final UUID uuid, @NonNull final String command) {
		if (!databaseAdapter.hasHnsUser(uuid) && command.equals("hns edit")) event.reply(R.string("this_user_entry_does_not_exist")).queue();
		else if (databaseAdapter.hasHnsUser(uuid) && command.equals("hns add")) event.reply(R.string("this_user_entry_already_exists")).queue();
		else {
			// get builder
			HNSUserModel.HNSUserModelBuilder hnsBuilder;
			UserModel.UserModelBuilder userBuilder;
			if (command.equals("hns add")) {
				hnsBuilder = HNSUserModel.builder().uuid(uuid);
				UserModel userModel = databaseAdapter.getUser(uuid);
				if (userModel == null) userBuilder = UserModel.builder().uuid(uuid);
				else userBuilder = userModel.toBuilder();
			}
			else {
				HNSUserModel hnsUserModel = databaseAdapter.getHnsUser(uuid);
				UserModel userModel = databaseAdapter.getUser(uuid);
				if (hnsUserModel == null || userModel == null) {
					event.reply(R.string("this_user_entry_does_not_exist")).queue();
					return;
				}
				else {
					hnsBuilder = hnsUserModel.toBuilder();
					userBuilder = userModel.toBuilder();
				}
			}

			// set attributes
			for (OptionMapping optionMapping : event.getOptions()) {
				switch (optionMapping.getName()) {
					case "rating" -> hnsBuilder.rating(optionMapping.getAsString());
					case "points" -> hnsBuilder.points(Math.round(optionMapping.getAsDouble()));
					case "joined" -> hnsBuilder.joined(optionMapping.getAsString());
					case "secondary" -> hnsBuilder.secondary(optionMapping.getAsBoolean());
					case "banned" -> hnsBuilder.banned(optionMapping.getAsBoolean());
					case "cheating" -> hnsBuilder.cheating(optionMapping.getAsBoolean());
					case "top10" -> hnsBuilder.top10(optionMapping.getAsString());
					case "streak" -> hnsBuilder.streak(optionMapping.getAsInt());
					case "highest_rank" -> hnsBuilder.highestRank(optionMapping.getAsString());
					case "discord" -> userBuilder.discord(optionMapping.getAsLong());
					case "note" -> userBuilder.note(optionMapping.getAsString());
				}
			}

			// update the model
			UserModel userModel = userBuilder.hnsUser(hnsBuilder.build()).build();

			if (command.equals("hns add")) {
				if (!databaseAdapter.addUser(userModel) && !databaseAdapter.addHnsUser(hnsBuilder.build())) event.reply(R.string("the_entry_could_not_be_created")).queue();
				else event.reply(R.string("the_entry_was_successfully_created")).setEmbeds(UserEmbed.of(userModel, UserEmbed.Type.HNS_ALL)).queue();
			} else {
				if (DCHelper.lacksRole(event.getMember(), Options.getEditRoleId()) && DCHelper.lacksRole(event.getMember(), Options.getCreateRoleId())) {
					long timestamp = System.currentTimeMillis();
					if (!databaseAdapter.addRequest(RequestModel.from(timestamp, userModel.getHnsUser()))) {
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
										.addEmbeds(UserEmbed.of(userModel, UserEmbed.Type.HNS_ALL)).queue();
							}
						}
					}
				} else {
					if (databaseAdapter.editHnsUser(userModel.getHnsUser()) == 0 || databaseAdapter.editUser(userModel) == 0) event.reply(R.string("the_entry_could_not_be_updated")).queue();
					else {
						event.reply(R.string("the_entry_was_successfully_updated")).setEmbeds(UserEmbed.of(userModel, UserEmbed.Type.HNS)).queue();
					}
				}
			}
		}
	}

	private void hnsList(@NonNull final SlashCommandInteractionEvent event, final int page) {
		List<HNSUserModel> entriesList = databaseAdapter.getHnsUserList(page);
		if (entriesList != null && !entriesList.isEmpty()) {
			event.deferReply().queue(interactionHook -> interactionHook.editOriginalEmbeds(ListEmbed.createHnsList(databaseAdapter, entriesList, page))
					.setComponents(ActionRow.of(
							Button.primary(String.format("previous:hns:%s", page), R.string("previous_page")).withDisabled(page < 1),
							Button.primary(String.format("next:hns:%s", page), R.string("next_page")).withDisabled(page + 1 >= databaseAdapter.getHnsPages())
					))
					.queue());
		} else event.reply(R.string("no_entries_available")).queue();
	}
}
